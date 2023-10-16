package nz.co.manawabay.core.models;

import com.day.cq.wcm.msm.api.LiveRelationshipManager;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import nz.co.manawabay.core.testcontext.AppAemContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
@ExtendWith(AemContextExtension.class)
class SearchTest {

    private static final String TEST_BASE = "/search";
    private static final String SEARCH_PAGE = "/content/en/search/page";
    private static final String SEARCH_PAGE_DE = "/content/de/search/page";

    private final AemContext context = AppAemContext.newAemContext();

    protected String testBase;

    @BeforeEach
    void setup() {
        testBase = TEST_BASE;
        context.load().json(testBase + AppAemContext.TEST_CONTENT_JSON, AppAemContext.CONTENT_ROOT);
        context.load().json(testBase + AppAemContext.TEST_APPS_JSON, AppAemContext.TEST_APPS_ROOT);
        context.registerService(LiveRelationshipManager.class, mock(LiveRelationshipManager.class));
    }

    @Test
    void testSearchProperties() {
        Search search = getSearchUnderTest(SEARCH_PAGE + "/jcr:content/search");
        assertEquals(10, search.getResultsSize());
        assertEquals(3, search.getSearchTermMinimumLength());
        assertEquals("/jcr:content/search", search.getRelativePath());
        assertEquals("/content/en/search", search.getSearchRootPagePath());
        assertSame(search.getSearchRootPagePath(), search.getSearchRootPagePath());
        assertEquals("", search.getActivationData());
    }

    @Test
    void testSearchProperties_noPath() {
        Search search = getSearchUnderTest(SEARCH_PAGE_DE + "/jcr:content/search");
        assertEquals(10, search.getResultsSize());
        assertEquals(3, search.getSearchTermMinimumLength());
        assertEquals("/jcr:content/search", search.getRelativePath());
        assertEquals(SEARCH_PAGE_DE, search.getSearchRootPagePath());
        assertSame(search.getSearchRootPagePath(), search.getSearchRootPagePath());
    }

    private Search getSearchUnderTest(String contentPath) {
        context.currentResource(contentPath);
        return context.request().adaptTo(Search.class);
    }
}
