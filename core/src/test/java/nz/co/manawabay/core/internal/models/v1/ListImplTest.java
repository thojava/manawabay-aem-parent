package nz.co.manawabay.core.internal.models.v1;

import com.day.cq.search.SimpleSearch;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.Page;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import nz.co.manawabay.core.testcontext.AppAemContext;
import nz.co.manawabay.core.utils.ModelUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.jcr.Session;
import java.util.Collections;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(AemContextExtension.class)
class ListImplTest {

    private static final String TEST_BASE = "/list";
    private static final String CURRENT_PAGE = "/content/list";
    protected static final String TEST_PAGE_CONTENT_ROOT = CURRENT_PAGE + "/jcr:content/root";
    protected static final String LIST_1 = TEST_PAGE_CONTENT_ROOT + "/staticListType";
    protected static final String LIST_2 = TEST_PAGE_CONTENT_ROOT + "/staticListType";
    protected static final String LIST_3 = TEST_PAGE_CONTENT_ROOT + "/childrenListType";
    protected static final String LIST_4 = TEST_PAGE_CONTENT_ROOT + "/childrenListTypeWithDepth";
    protected static final String LIST_5 = TEST_PAGE_CONTENT_ROOT + "/tagsListType";
    protected static final String LIST_6 = TEST_PAGE_CONTENT_ROOT + "/searchListType";
    protected static final String LIST_7 = TEST_PAGE_CONTENT_ROOT + "/staticOrderByTitleListType";
    protected static final String LIST_8 = TEST_PAGE_CONTENT_ROOT + "/staticOrderByTitleDescListType";
    protected static final String LIST_9 = TEST_PAGE_CONTENT_ROOT + "/staticOrderByModificationDateListType";
    protected static final String LIST_10 = TEST_PAGE_CONTENT_ROOT + "/staticOrderByModificationDateDescListType";
    protected static final String LIST_11 = TEST_PAGE_CONTENT_ROOT + "/staticMaxItemsListType";
    protected static final String LIST_12 = TEST_PAGE_CONTENT_ROOT + "/staticOrderByModificationDateListTypeWithNoModificationDate";
    protected static final String LIST_13 = TEST_PAGE_CONTENT_ROOT + "/staticOrderByModificationDateListTypeWithNoModificationDateForOneItem";
    protected static final String LIST_14 = TEST_PAGE_CONTENT_ROOT + "/staticOrderByTitleListTypeWithNoTitle";
    protected static final String LIST_15 = TEST_PAGE_CONTENT_ROOT + "/staticOrderByTitleListTypeWithNoTitleForOneItem";
    protected static final String LIST_16 = TEST_PAGE_CONTENT_ROOT + "/staticOrderByTitleListTypeWithAccent";
    protected static final String LIST_21 = TEST_PAGE_CONTENT_ROOT + "/staticOrderByTitleListTypeWithBlankTitle";

    protected final AemContext context = AppAemContext.newAemContext();

    protected String testBase;

    @BeforeEach
    public void setUp() {
        testBase = TEST_BASE;
        internalSetup();
    }

    void internalSetup() {
        context.load().json(testBase + AppAemContext.TEST_CONTENT_JSON, AppAemContext.CONTENT_ROOT);
        context.load().json(testBase + AppAemContext.TEST_TAGS_JSON, AppAemContext.CONTENT_ROOT + "/cq:tags/list");
    }

    @Test
    void testProperties() {
        ListImpl list = getListUnderTest(LIST_1);
        assertTrue(list.showDescription());
        assertTrue(list.showModificationDate());
        assertTrue(list.linkItems());
    }

    @Test
    void testStaticListType() {
        ListImpl list = getListUnderTest(LIST_2);
        checkListConsistencyByPaths(list, new String[]{
                "/content/list/pages/page_1",
                "/content/list/pages/page_2",
        });
    }

    @Test
    void testChildrenListType() {
        ListImpl list = getListUnderTest(LIST_3);
        checkListConsistencyByPaths(list, new String[]{
                "/content/list/pages/page_1/page_1_1",
                "/content/list/pages/page_1/page_1_2",
                "/content/list/pages/page_1/page_1_3",
        });
    }

    @Test
    void testChildrenListTypeWithDepth() {
        ListImpl list = getListUnderTest(LIST_4);
        checkListConsistencyByPaths(list, new String[]{
                "/content/list/pages/page_1/page_1_1",
                "/content/list/pages/page_1/page_1_2",
                "/content/list/pages/page_1/page_1_2/page_1_2_1",
                "/content/list/pages/page_1/page_1_3",

        });
    }

    @Test
    void testTagsListType() {
        ListImpl list = getListUnderTest(LIST_5);
        checkListConsistencyByPaths(list, new String[]{"/content/list/pages/page_1/page_1_3"});
    }

    @Test
    void testSearchListType() throws Exception {
        Session mockSession = mock(Session.class);
        SimpleSearch mockSimpleSearch = mock(SimpleSearch.class);
        context.registerAdapter(ResourceResolver.class, Session.class, mockSession);
        context.registerAdapter(Resource.class, SimpleSearch.class, mockSimpleSearch);
        SearchResult searchResult = mock(SearchResult.class);

        when(mockSimpleSearch.getResult()).thenReturn(searchResult);
        when(searchResult.getResources()).thenReturn(
                Collections.singletonList(Objects.requireNonNull(
                                context.resourceResolver().getResource("/content/list/pages/page_1/jcr:content")))
                        .iterator());

        ListImpl list = getListUnderTest(LIST_6);
        checkListConsistencyByPaths(list, new String[]{"/content/list/pages/page_1"});
    }

    @Test
    void testOrderBy() {
        ListImpl list = getListUnderTest(LIST_7);
        checkListConsistencyByTitle(list, new String[]{"Page 1", "Page 2"});
    }

    @Test
    void testOrderDescBy() {
        ListImpl list = getListUnderTest(LIST_8);
        checkListConsistencyByTitle(list, new String[]{"Page 2", "Page 1"});
    }

    @Test
    void testOrderByModificationDate() {
        ListImpl list = getListUnderTest(LIST_9);
        checkListConsistencyByTitle(list, new String[]{"Page 2", "Page 1"});
    }

    @Test
    void testOrderByModificationDateDesc() {
        ListImpl list = getListUnderTest(LIST_10);
        checkListConsistencyByTitle(list, new String[]{"Page 1", "Page 2"});
    }

    @Test
    void testMaxItems() {
        ListImpl list = getListUnderTest(LIST_11);
        checkListConsistencyByTitle(list, new String[]{"Page 1"});
    }

    @Test
    void testOrderByModificationDateWithNoModificationDate() {
        ListImpl list = getListUnderTest(LIST_12);
        checkListConsistencyByTitle(list, new String[]{"Page 1.1", "Page 1.2"});
    }

    @Test
    void testOrderByModificationDateWithNoModificationDateForOneItem() {
        ListImpl list = getListUnderTest(LIST_13);
        checkListConsistencyByTitle(list, new String[]{"Page 2", "Page 1", "Page 1.2"});
    }

    @Test
    void testOrderByTitleWithNoTitle() {
        ListImpl list = getListUnderTest(LIST_14);
        checkListConsistencyByPaths(list, new String[]{"/content/list/pages/page_3", "/content/list/pages/page_4"});
    }

    @Test
    void testOrderByTitleWithNoTitleForOneItem() {
        ListImpl list = getListUnderTest(LIST_15);
        checkListConsistencyByPaths(list, new String[]{"/content/list/pages/page_4", "/content/list/pages/page_1", "/content/list/pages/page_2" });
    }

    @Test
    void testOrderByTitleWithBlankTitle() {
        ListImpl list = getListUnderTest(LIST_21);
        checkListConsistencyByPaths(list, new String[]{
                "/content/list/pages/page_4",
                "/content/list/pages/page_6",
                "/content/list/pages/page_1",
                "/content/list/pages/page_2"
        });
    }

    @Test
    void testOrderByTitleWithAccent() {
        ListImpl list = getListUnderTest(LIST_16);
        checkListConsistencyByPaths(list, new String[]{"/content/list/pages/page_1", "/content/list/pages/page_5", "/content/list/pages/page_2"});
    }

    protected ListImpl getListUnderTest(String resourcePath) {
        ModelUtils.enableDataLayer(context, true);
        Resource resource = context.resourceResolver().getResource(resourcePath);
        if (resource == null) {
            throw new IllegalStateException("Did you forget to define test resource " + resourcePath + "?");
        }
        context.currentResource(resource);
        return context.request().adaptTo(ListImpl.class);
    }

    void checkListConsistencyByTitle(ListImpl list, String[] expectedPageTitles) {
        assertArrayEquals(expectedPageTitles, list.getItems().stream().map(Page::getTitle).toArray());
    }

    void checkListConsistencyByPaths(ListImpl list, String[] expectedPagePaths) {
        assertArrayEquals(expectedPagePaths, list.getItems().stream().map(Page::getPath).toArray());
    }
}
