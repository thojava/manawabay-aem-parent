package nz.co.manawabay.core.services.impl;

import com.adobe.cq.wcm.core.components.commons.link.LinkManager;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.day.cq.search.PredicateConverter;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.eval.FulltextPredicateEvaluator;
import com.day.cq.search.eval.PathPredicateEvaluator;
import com.day.cq.search.eval.TypePredicateEvaluator;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.*;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import nz.co.manawabay.core.internal.models.v1.PageListItemImpl;
import nz.co.manawabay.core.models.SearchResults;
import nz.co.manawabay.core.services.SearchResultsLandingPage;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.factory.ModelFactory;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.jcr.Session;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.StreamSupport;

import static com.adobe.cq.wcm.core.components.models.ExperienceFragment.PN_FRAGMENT_VARIATION_PATH;

@Component(service = SearchResultsLandingPage.class)
public class SearchResultsLandingPageImpl implements SearchResultsLandingPage {
    private static final String PARAM_FULLTEXT = "fulltext";
    private static final String PARAM_RESULTS_OFFSET = "resultsOffset";
    private static final String NN_STRUCTURE = "structure";

    @Reference
    private QueryBuilder queryBuilder;

    @Reference
    private LanguageManager languageManager;

    @Reference
    private LiveRelationshipManager relationshipManager;

    @Reference
    private ModelFactory modelFactory;


    @Override
    public void doSearch(@NotNull Page currentPage, @NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response) throws IOException {
        SearchResults searchComponent = getSearchComponent(request, currentPage);
        List<ListItem> results = getResults(request, searchComponent, currentPage.getPageManager());
        response.setContentType("application/json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        new ObjectMapper().writeValue(response.getWriter(), results);
    }

    @NotNull
    private SearchResults getSearchComponent(@NotNull final SlingHttpServletRequest request, @NotNull final Page currentPage) {
        String suffix = request.getRequestPathInfo().getSuffix();
        String relativeContentResourcePath = Optional.ofNullable(suffix)
                .filter(path -> StringUtils.startsWith(path, "/"))
                .map(path -> StringUtils.substring(path, 1))
                .orElse(suffix);

        return Optional.ofNullable(relativeContentResourcePath)
                .filter(StringUtils::isNotEmpty)
                .map(rcrp -> getSearchComponentResourceFromPage(request.getResource(), rcrp)
                        .orElse(getSearchComponentResourceFromTemplate(currentPage, rcrp)
                                .orElse(null)))
                .map(resource -> modelFactory.getModelFromWrappedRequest(request, resource, SearchResults.class))
                .orElseThrow(() -> new RuntimeException("The search is invalid"));
    }

    private Optional<Resource> getSearchComponentResourceFromPage(@NotNull final Resource pageResource, final String relativeContentResourcePath) {
        return Optional.ofNullable(Optional.ofNullable(pageResource.getChild(relativeContentResourcePath))
                .orElse(getSearchComponentResourceFromFragments(pageResource.getChild(NameConstants.NN_CONTENT), relativeContentResourcePath)
                        .orElse(null)));
    }

    private Optional<Resource> getSearchComponentResourceFromTemplate(@NotNull final Page currentPage, final String relativeContentResourcePath) {
        return Optional.ofNullable(currentPage.getTemplate())
                .map(Template::getPath)
                .map(currentPage.getContentResource().getResourceResolver()::getResource)
                .map(templateResource -> Optional.ofNullable(templateResource.getChild(NN_STRUCTURE + "/" + relativeContentResourcePath))
                        .orElse(getSearchComponentResourceFromFragments(templateResource, relativeContentResourcePath)
                                .orElse(null)));
    }

    private Optional<Resource> getSearchComponentResourceFromFragments(Resource resource, String relativeContentResourcePath) {
        return Optional.ofNullable(resource)
                .map(res -> getSearchComponentResourceFromFragment(res, relativeContentResourcePath)
                        .orElse(StreamSupport.stream(res.getChildren().spliterator(), false)
                                .map(child -> getSearchComponentResourceFromFragments(child, relativeContentResourcePath).orElse(null))
                                .filter(Objects::nonNull)
                                .findFirst()
                                .orElse(null)));
    }

    private Optional<Resource> getSearchComponentResourceFromFragment(Resource candidate, String relativeContentResourcePath) {
        return Optional.ofNullable(candidate)
                .map(Resource::getValueMap)
                .map(properties -> properties.get(PN_FRAGMENT_VARIATION_PATH, String.class))
                .map(path -> candidate.getResourceResolver().getResource(path + "/" + relativeContentResourcePath));
    }

    @NotNull
    private List<ListItem> getResults(@NotNull final SlingHttpServletRequest request,
                                      @NotNull final SearchResults searchComponent,
                                      @NotNull final PageManager pageManager) {

        List<ListItem> results = new ArrayList<>();
        String fulltext = request.getParameter(PARAM_FULLTEXT);

        long resultsOffset = Optional.ofNullable(request.getParameter(PARAM_RESULTS_OFFSET)).map(Long::parseLong).orElse(0L);
        Map<String, String> predicatesMap = new HashMap<>();
        predicatesMap.put(FulltextPredicateEvaluator.FULLTEXT, fulltext);
        predicatesMap.put(PathPredicateEvaluator.PATH, searchComponent.getSearchRootPagePath());
        predicatesMap.put(TypePredicateEvaluator.TYPE, NameConstants.NT_PAGE);
        PredicateGroup predicates = PredicateConverter.createPredicates(predicatesMap);
        ResourceResolver resourceResolver = request.getResource().getResourceResolver();
        Query query = queryBuilder.createQuery(predicates, resourceResolver.adaptTo(Session.class));
        if (searchComponent.getPageNumber() != 0) {
            query.setHitsPerPage(searchComponent.getPageNumber());
        }
        if (resultsOffset != 0) {
            query.setStart(resultsOffset);
        }
        SearchResult searchResult = query.getResult();

        LinkManager linkManager = request.adaptTo(LinkManager.class);
        // Query builder has a leaking resource resolver, so the following work around is required.
        ResourceResolver leakingResourceResolver = null;
        try {
            Iterator<Resource> resourceIterator = searchResult.getResources();
            while (resourceIterator.hasNext()) {
                Resource resource = resourceIterator.next();

                // Get a reference to QB's leaking resource resolver
                if (leakingResourceResolver == null) {
                    leakingResourceResolver = resource.getResourceResolver();
                }

                Optional.of(resource)
                        .map(res -> resourceResolver.getResource(res.getPath()))
                        .map(pageManager::getContainingPage)
                        .map(page -> new PageListItemImpl(linkManager, page, searchComponent.getId(), null))
                        .ifPresent(results::add);
            }
        } finally {
            if (leakingResourceResolver != null) {
                leakingResourceResolver.close();
            }
        }
        return results;
    }
}
