package nz.co.manawabay.core.servlets;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import nz.co.manawabay.core.models.search.SearchResultItem;
import nz.co.manawabay.core.models.search.SearchResults;
import nz.co.manawabay.core.services.SearchService;
import nz.co.manawabay.core.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.apache.sling.api.servlets.HttpConstants.METHOD_GET;

@Slf4j
@Component(service = Servlet.class)
@SlingServletResourceTypes(
        resourceTypes = SearchResults.RESOURCE_TYPE,
        methods = METHOD_GET,
        selectors = "data",
        extensions = "json"
)
public class SearchResultsServlet extends SlingSafeMethodsServlet {
    /**
     * Name of the query parameter containing the user query.
     */
    private static final String PARAM_QUERY = "query";
    /**
     * Name of the filter parameter containing the user filter.
     */
    private static final String PARAM_FILTER = "filter";
    /**
     * Name of the query parameter indicating the search result offset.
     */
    private static final String PARAM_RESULTS_OFFSET = "offset";
    /**
     * Name of the query parameter indicating the search result limit
     */
    private static final String PARAM_RESULTS_LIMIT = "limit";

    @Reference
    private transient SearchService searchService;

    @Override
    protected void doGet(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response) {
        SearchResults searchResultsComponent = request.adaptTo(SearchResults.class);
        String fulltext = request.getParameter(PARAM_QUERY);
        String filterParam = request.getParameter(PARAM_FILTER);
        long resultsOffset = Optional.ofNullable(request.getParameter(PARAM_RESULTS_OFFSET)).filter(StringUtils::isNumeric).map(Long::parseLong).orElse(0L);
        long resultsLimit = Optional.ofNullable(request.getParameter(PARAM_RESULTS_LIMIT)).filter(StringUtils::isNumeric).map(Long::parseLong).orElse(15L);
        assert searchResultsComponent != null;
        List<SearchResultItem> results = searchService.getResults(request,
                searchResultsComponent.getStoresSearchRoot(),
                searchResultsComponent.getArticlesSearchRoot(),
                fulltext, filterParam, resultsOffset, resultsLimit);
        Map<String, String> filteredResultsCountMap = searchService.getFilteredResultsCount(request,
                searchResultsComponent.getStoresSearchRoot(),
                searchResultsComponent.getArticlesSearchRoot(),
                fulltext);
        SearchResultsResponse responseObj = new SearchResultsResponse(
                results.stream().filter(item -> !item.isArticlePage()).collect(Collectors.toList()),
                results.stream().filter(SearchResultItem::isArticlePage).collect(Collectors.toList()),
                filteredResultsCountMap);
        JsonUtils.writeObjAsJsonResponse(response, HttpStatus.SC_OK, responseObj);
    }

    @AllArgsConstructor
    @Getter
    static class SearchResultsResponse {
        private List<SearchResultItem> storeResults;
        private List<SearchResultItem> articleResults;
        private Map<String, String> filters;
    }
}
