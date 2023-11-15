package nz.co.manawabay.core.servlets;

import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import junitx.util.PrivateAccessor;
import nz.co.manawabay.core.services.SearchService;
import nz.co.manawabay.core.services.impl.SearchServiceImpl;
import nz.co.manawabay.core.testcontext.AppAemContext;
import nz.co.manawabay.core.utils.JsonUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.jcr.RepositoryException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class SearchResultsServletTest {

    private final AemContext aemContext = AppAemContext.newAemContext();
    private static final String CONTENT_ROOT_PATH = "/content/manawabay/home";
    private static final String TITLE_ARTICLE = "Home Loan Application Process";
    private static final String TITLE_PRODUCT = "Personal loans";
    private static final String URL_ARTICLE = "/content/manawabay/home/loan-application-process.html";
    private static final String URL_PRODUCT = "/content/manawabay/home/personal-loans.html";
    private static final String EXCERPT_ARTICLE = "Applying for a home loan is easier than you might think. We'll guide you through the step-by-step process of applying for a home loan with manawabay Money.";
    private static final String EXCERPT_PRODUCT = "Lorem ipsum dolor sit amet.";
    private static final String FEATURE_IMAGE_ARTICLE = "/content/manawabay/home/loan-application-process/jcr:content/cq:featuredimage/file";
    private static final String BRAND_IMAGE_ARTICLE = "/content/manawabay/home/loan-application-process/jcr:content/brandimage/file";
    private static final String BRAND_IMAGE_PRODUCT = "/content/manawabay/home/personal-loans/jcr:content/brandimage/file";
    private static final String FEATURED_IMAGE_PRODUCT = "/content/manawabay/home/personal-loans/jcr:content/cq:featuredimage/file";

    @InjectMocks
    private SearchResultsServlet searchResultsServlet;

    @Mock
    private QueryBuilder queryBuilder;
    @Mock
    private Query query;
    @Mock
    private SearchResult searchResult;
    @Mock
    private Hit hitPageArticle,
            hitPageProduct,
            hitInvalid;

    @BeforeEach
    void setUp() throws RepositoryException, NoSuchFieldException {
        aemContext.load().json("/search/search-servlet-page.json", CONTENT_ROOT_PATH);

        Resource articleResource = aemContext.currentResource(CONTENT_ROOT_PATH + "/loan-application-process");
        Resource productResource = aemContext.currentResource(CONTENT_ROOT_PATH + "/personal-loans");
        SearchService searchService = new SearchServiceImpl();
        aemContext.registerService(QueryBuilder.class, queryBuilder);
        when(queryBuilder.createQuery(any(), any())).thenReturn(query);
        when(query.getResult()).thenReturn(searchResult);
        when(hitPageArticle.getResource()).thenReturn(articleResource);
        when(hitPageProduct.getResource()).thenReturn(productResource);
        when(hitInvalid.getResource()).thenThrow(new RepositoryException("Invalid hit"));
        when(hitPageArticle.getExcerpt()).thenReturn(EXCERPT_ARTICLE);
        when(hitPageProduct.getExcerpt()).thenReturn(EXCERPT_PRODUCT);
        when(searchResult.getHits()).thenReturn(Arrays.asList(hitPageArticle,
                hitPageProduct,
                hitInvalid));
        PrivateAccessor.setField(searchService, "queryBuilder", queryBuilder);
        PrivateAccessor.setField(searchResultsServlet, "searchService", searchService);
    }

    @Test
    void doGet() throws JsonProcessingException {
        MockSlingHttpServletRequest request = aemContext.request();
        MockSlingHttpServletResponse response = aemContext.response();
        request.addRequestParameter("query", "loan");
        request.addRequestParameter("offset", "1");
        request.addRequestParameter("limit", "5");
        aemContext.currentResource(CONTENT_ROOT_PATH + "/loan-application-process/jcr:content/root/container/search_results");
        searchResultsServlet.doGet(request, response);
        assertNotNull(response.getOutputAsString());
        Map responseObj = JsonUtils.getObjectFromString(response.getOutputAsString(), Map.class);
        if (Objects.nonNull(responseObj)) {
            List<Map<String, Object>> storeResults = (List<Map<String, Object>>) responseObj.get("storeResults");
            assertEquals(1, storeResults.size());
            Map<String, Object> storeResult = storeResults.get(0);
            assertEquals(TITLE_ARTICLE, storeResult.get("title"));
            assertEquals(EXCERPT_ARTICLE, storeResult.get("description"));
            assertEquals(URL_ARTICLE, storeResult.get("uri"));
            assertEquals(FEATURE_IMAGE_ARTICLE, storeResult.get("featuredImage"));
            assertEquals(BRAND_IMAGE_ARTICLE, storeResult.get("brandImage"));

            List<Map<String, Object>> articleResults = (List<Map<String, Object>>) responseObj.get("articleResults");
            Map<String, Object> articleResult = articleResults.get(0);
            assertEquals(TITLE_PRODUCT, articleResult.get("title"));
            assertEquals(EXCERPT_PRODUCT, articleResult.get("description"));
            assertEquals(URL_PRODUCT, articleResult.get("uri"));
            assertEquals(FEATURED_IMAGE_PRODUCT, articleResult.get("featuredImage"));
            assertEquals(BRAND_IMAGE_PRODUCT, articleResult.get("brandImage"));
        }
    }
}
