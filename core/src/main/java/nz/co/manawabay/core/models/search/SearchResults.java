package nz.co.manawabay.core.models.search;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
@Slf4j
@Getter
@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SearchResults {
    public static final String RESOURCE_TYPE = "manawabay/components/search-results";

    /**
     * The root page from which to search in.
     */
    @ValueMapValue
    private String storesSearchRoot;

    /**
     * The files directory from which to search in.
     */
    @ValueMapValue
    private String articlesSearchRoot;

    /**
     * Number of results per page.
     */
    @ValueMapValue
    private int limit;

    /**
     * Load More Button Label
     */
    @ValueMapValue
    private String loadMoreLabel;

}
