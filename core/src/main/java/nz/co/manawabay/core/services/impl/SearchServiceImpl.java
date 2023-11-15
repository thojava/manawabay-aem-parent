package nz.co.manawabay.core.services.impl;

import com.day.cq.search.PredicateConverter;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.eval.FulltextPredicateEvaluator;
import com.day.cq.search.eval.PathPredicateEvaluator;
import com.day.cq.search.eval.TypePredicateEvaluator;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.constants.NameConstants;
import lombok.extern.slf4j.Slf4j;
import nz.co.manawabay.core.models.search.SearchResultItem;
import nz.co.manawabay.core.services.SearchService;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.util.Text;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.toList;
import static nz.co.manawabay.core.constants.CommonConstants.REGEX_HYPHENATED;
import static nz.co.manawabay.core.constants.CommonConstants.TEXT_ALL;

@Slf4j
@Component(immediate = true, service = SearchService.class)
public class SearchServiceImpl implements SearchService {
    /**
     * Query builder service.
     */
    @Reference
    private QueryBuilder queryBuilder;

    @Override
    public List<SearchResultItem> getResults(final SlingHttpServletRequest request, String storePageRoot,
                                             String articlePageRoot, String fulltextParam,
                                             String filterParam, long offset, long limit) {
        Map<String, String> predicatesMap = this.getPredicatesMap(storePageRoot, articlePageRoot, fulltextParam);
        List<Hit> filteredResults = this.executeQuery(request, offset, limit, predicatesMap);

        return filteredResults.stream()
                .map(hit -> processSearchResult(hit, articlePageRoot))
                .filter(Objects::nonNull)
                .collect(toList());
    }

    @Override
    public Map<String, String> getFilteredResultsCount(SlingHttpServletRequest request, String storePageRoot,
                                                       String articlePageRoot, String fulltextParam) {
        Map<String, String> filteredResultsCount = new HashMap<>();
        filteredResultsCount.put(TEXT_ALL,
                this.getResultsCount(request, storePageRoot, articlePageRoot, fulltextParam));
        return filteredResultsCount;
    }

    private SearchResultItem processSearchResult(Hit hit, String articlePageRoot) {
        Resource filteredResource;
        try {
            filteredResource = hit.getResource();
            SearchResultItem searchResult = filteredResource.adaptTo(SearchResultItem.class);
            assert searchResult != null;
            searchResult.setDescription(hit.getExcerpt());
            searchResult.setArticlePage(filteredResource.getPath().contains(articlePageRoot));
            return searchResult;
        } catch (RepositoryException e) {
            log.error("Unable to get resource or excerpt from hit - exception : ", e);
            return null;
        }
    }

    private String getResultsCount(SlingHttpServletRequest request, String storePageRoot, String articlePageRoot,
                                   String fulltext) {
        Map<String, String> predicatesMap = this.getPredicatesMap(storePageRoot, articlePageRoot, fulltext);
        long filteredResults = executeQueryCount(request, predicatesMap);
        return String.valueOf(filteredResults);
    }

    private Map<String, String> getPredicatesMap(String storePageRoot, String articlePageRoot, String fulltext) {
        Map<String, String> predicatesMap = new HashMap<>();
        predicatesMap.put("group.p.or", "true");
        predicatesMap.put("orderby", "@jcr:score");
        predicatesMap.put("orderby.sort", "desc");
        if(StringUtils.isNotEmpty(fulltext)) {
            fulltext = fulltext.replaceAll(REGEX_HYPHENATED, " ").trim();
            predicatesMap.put(FulltextPredicateEvaluator.FULLTEXT, Text.escapeIllegalXpathSearchChars(fulltext) + "*");
        }

        // Page Predicate
        this.addPagePredicates(predicatesMap, "group.1_group.", storePageRoot);

        this.addPagePredicates(predicatesMap, "group.2_group.", articlePageRoot);

        return predicatesMap;
    }

    private void addPagePredicates(Map<String, String> predicatesMap, String groupPrefix,
                                   String searchRoot) {
        predicatesMap.put(groupPrefix + PathPredicateEvaluator.PATH, searchRoot);
        predicatesMap.put(groupPrefix + TypePredicateEvaluator.TYPE, NameConstants.NT_PAGE);
        predicatesMap.put(groupPrefix + "1_group.p.not", "true");
        predicatesMap.put(groupPrefix + "1_group.1_property", "jcr:content/hideInSiteSearch");
        predicatesMap.put(groupPrefix + "1_group.1_property.value", "true");
        predicatesMap.put(groupPrefix + "1_group.1_property.operation", "equals");
    }

    private List<Hit> executeQuery(SlingHttpServletRequest request, long resultsOffset,
                                   long resultsLimit, Map<String, String> predicatesMap) {
        Query query = this.getQuery(request, predicatesMap);
        query.setHitsPerPage(resultsLimit);
        if (resultsOffset != 0) {
            query.setStart(resultsOffset);
        }
        SearchResult searchResult = query.getResult();
        return searchResult.getHits();
    }

    private long executeQueryCount(SlingHttpServletRequest request, Map<String, String> predicatesMap) {
        Query query = this.getQuery(request, predicatesMap);
        SearchResult searchResult = query.getResult();
        return searchResult.getTotalMatches();
    }

    private Query getQuery(SlingHttpServletRequest request, Map<String, String> predicatesMap) {
        PredicateGroup predicates = PredicateConverter.createPredicates(predicatesMap);
        ResourceResolver resourceResolver = request.getResourceResolver();
        return queryBuilder.createQuery(predicates, resourceResolver.adaptTo(Session.class));
    }
}
