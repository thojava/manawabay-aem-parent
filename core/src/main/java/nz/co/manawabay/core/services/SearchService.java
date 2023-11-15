package nz.co.manawabay.core.services;

import nz.co.manawabay.core.models.search.SearchResultItem;
import org.apache.sling.api.SlingHttpServletRequest;

import java.util.List;
import java.util.Map;

public interface SearchService {

    /**
     * Get Search results based on search term and tag filter.
     */
    List<SearchResultItem> getResults(SlingHttpServletRequest request, String storePageRoot,
                                      String articlePageRoot, String fulltextParam,
                                      String filterParam, long offset, long limit);

    /**
     * Get count of search results based on filter tags.
     */
    Map<String, String> getFilteredResultsCount(SlingHttpServletRequest request, String storePageRoot,
                                                String articlePageRoot, String fulltextParam);
}
