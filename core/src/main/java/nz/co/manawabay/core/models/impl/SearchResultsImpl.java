package nz.co.manawabay.core.models.impl;

import com.adobe.cq.wcm.core.components.models.Search;
import com.day.cq.wcm.api.LanguageManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.api.designer.Style;
import com.day.cq.wcm.msm.api.LiveRelationship;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.PostConstruct;
import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Model(
        adaptables = {SlingHttpServletRequest.class},
        adapters = nz.co.manawabay.core.models.SearchResults.class,
        resourceType = "manawabay/components/searchresults",
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class SearchResultsImpl implements nz.co.manawabay.core.models.SearchResults {

    @SlingObject
    private SlingHttpServletRequest request;

    @ScriptVariable
    private Page currentPage;

    @ScriptVariable
    private Style currentStyle;

    @OSGiService
    private LanguageManager languageManager;

    @OSGiService
    private LiveRelationshipManager relationshipManager;

    private String queryString;

    @ValueMapValue
    @Default(values = "Search results for")
    private String header;

    @ValueMapValue
    @Default(longValues = 10)
    private long pageNumber;

    private String searchRootPagePath;

    @PostConstruct
    void init() {
        queryString = request.getParameter("query");
    }

    @Override
    public String getHeader() {
        return header;
    }

    @Override
    public String getQueryString() {
        return queryString;
    }

    @Override
    public String getSearchRootPagePath() {
        if (this.searchRootPagePath == null) {
            this.searchRootPagePath = Optional.ofNullable(this.request.getResource().getValueMap().get(Search.PN_SEARCH_ROOT, String.class))
                    .flatMap(searchRoot -> this.getLocalPage(searchRoot, currentPage, this.request.getResourceResolver(), languageManager, relationshipManager))
                    .map(Page::getPath)
                    .orElseGet(currentPage::getPath);
        }
        return this.searchRootPagePath;
    }

    private Optional<Page> getLocalPage(@Nullable final String referencePagePath,
                                        @NotNull final Page currentPage,
                                        @NotNull final ResourceResolver resourceResolver,
                                        @NotNull final LanguageManager languageManager,
                                        @NotNull final LiveRelationshipManager relationshipManager) {
        return Optional.ofNullable(currentPage.getPageManager().getPage(referencePagePath))
                .flatMap(referencePage -> getLocalPage(referencePage, currentPage, resourceResolver, languageManager, relationshipManager));
    }

    private Optional<Page> getLocalPage(@NotNull final Page referencePage,
                                        @NotNull final Page currentPage,
                                        @NotNull final ResourceResolver resourceResolver,
                                        @NotNull final LanguageManager languageManager,
                                        @NotNull final LiveRelationshipManager relationshipManager) {
        Page referencePageLanguageRoot = Optional.ofNullable(referencePage.getPath())
                .map(resourceResolver::getResource)
                .map(languageManager::getLanguageRoot)
                .orElse(null);

        Page currentPageLanguageRoot = languageManager.getLanguageRoot(currentPage.getContentResource());
        if (referencePageLanguageRoot != null && currentPageLanguageRoot != null && !referencePageLanguageRoot.equals
                (currentPageLanguageRoot)) {
            // check if there's a language copy of the navigation root
            return Optional.ofNullable(
                    referencePage.getPageManager().getPage(
                            ResourceUtil.normalize(
                                    String.join("/",
                                            currentPageLanguageRoot.getPath(),
                                            getRelativePath(referencePageLanguageRoot, referencePage)))));
        } else {
            try {
                String currentPagePath = currentPage.getPath() + "/";
                return Optional.of(
                        Optional.ofNullable((Iterator<LiveRelationship>) relationshipManager.getLiveRelationships(referencePage.adaptTo(Resource.class), null, null))
                                .map(liveRelationshipIterator -> StreamSupport.stream(((Iterable<LiveRelationship>) () -> liveRelationshipIterator).spliterator(), false))
                                .orElseGet(Stream::empty)
                                .map(LiveRelationship::getTargetPath)
                                .filter(target -> currentPagePath.startsWith(target + "/"))
                                .map(referencePage.getPageManager()::getPage)
                                .findFirst()
                                .orElse(referencePage));
            } catch (WCMException e) {
                // ignore it
            }
        }
        return Optional.empty();
    }

    private String getRelativePath(@NotNull final Page root, @NotNull final Page child) {
        if (child.equals(root)) {
            return ".";
        } else if ((child.getPath() + "/").startsWith(root.getPath())) {
            return child.getPath().substring(root.getPath().length() + 1);
        }
        return null;
    }

    @Override
    public long getPageNumber() {
        return pageNumber;
    }
}
