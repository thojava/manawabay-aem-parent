package nz.co.manawabay.core.models;

import com.adobe.cq.wcm.core.components.models.ListItem;
import com.day.cq.search.SimpleSearch;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.Page;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import nz.co.manawabay.core.internal.models.v1.PageListItemImpl;
import nz.co.manawabay.core.utils.ModelUtils;
import nz.co.manawabay.core.testcontext.AppAemContext;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.jcr.Session;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(AemContextExtension.class)
public class ListTest {
    private static final String TEST_BASE = "/list";
    private static final String CURRENT_PAGE = "/content/list";
    private static final String TEST_PAGE_CONTENT_ROOT = CURRENT_PAGE + "/jcr:content/root";
    private static final String LIST_1 = TEST_PAGE_CONTENT_ROOT + "/staticListType";
    private static final String LIST_2 = TEST_PAGE_CONTENT_ROOT + "/staticListType";
    private static final String LIST_3 = TEST_PAGE_CONTENT_ROOT + "/childrenListType";
    private static final String LIST_4 = TEST_PAGE_CONTENT_ROOT + "/childrenListTypeWithDepth";
    private static final String LIST_5 = TEST_PAGE_CONTENT_ROOT + "/tagsListType";
    private static final String LIST_6 = TEST_PAGE_CONTENT_ROOT + "/searchListType";
    private static final String LIST_7 = TEST_PAGE_CONTENT_ROOT + "/staticOrderByTitleListType";
    private static final String LIST_8 = TEST_PAGE_CONTENT_ROOT + "/staticOrderByTitleDescListType";
    private static final String LIST_9 = TEST_PAGE_CONTENT_ROOT + "/staticOrderByModificationDateListType";
    private static final String LIST_10 = TEST_PAGE_CONTENT_ROOT + "/staticOrderByModificationDateDescListType";
    private static final String LIST_11 = TEST_PAGE_CONTENT_ROOT + "/staticMaxItemsListType";
    private static final String LIST_12 = TEST_PAGE_CONTENT_ROOT + "/staticOrderByModificationDateListTypeWithNoModificationDate";
    private static final String LIST_13 = TEST_PAGE_CONTENT_ROOT + "/staticOrderByModificationDateListTypeWithNoModificationDateForOneItem";
    private static final String LIST_14 = TEST_PAGE_CONTENT_ROOT + "/staticOrderByTitleListTypeWithNoTitle";
    private static final String LIST_15 = TEST_PAGE_CONTENT_ROOT + "/staticOrderByTitleListTypeWithNoTitleForOneItem";
    private static final String LIST_16 = TEST_PAGE_CONTENT_ROOT + "/staticOrderByTitleListTypeWithAccent";
    private static final String LIST_21 = TEST_PAGE_CONTENT_ROOT + "/staticOrderByTitleListTypeWithBlankTitle";

    private final AemContext context = AppAemContext.newAemContext();

    private String testBase;

    @BeforeEach
    void setUp() {
        testBase = TEST_BASE;
        internalSetup();
    }

    protected void internalSetup() {
        context.load().json(testBase + AppAemContext.TEST_CONTENT_JSON, AppAemContext.CONTENT_ROOT);
        context.load().json(testBase + AppAemContext.TEST_APPS_JSON, AppAemContext.TEST_APPS_ROOT);
        context.load().json(testBase + AppAemContext.TEST_TAGS_JSON, AppAemContext.CONTENT_ROOT + "/cq:tags/list");
    }

    @Test
    public void testProperties() {
        List list = getListUnderTest(LIST_1);
        assertTrue(list.showDescription());
        assertTrue(list.showModificationDate());
        assertTrue(list.getHideImage());
        assertTrue(list.getHideTitle());
        assertTrue(list.linkItems());
        assertFalse(list.getHideDescription());
        assertFalse(list.showIcon());
        assertFalse(list.showPublishDate());
        assertEquals("yyyyMMdd", list.getPublishDateFormatString());
        assertEquals("staticListType", list.getId());
        assertNull(list.getAppliedCssClasses());
        assertEquals("core/wcm/components/list", list.getExportedType());
        assertEquals("pages", list.getType());
        assertEquals("children", list.getListFromTags());
        assertArrayEquals(new String[]{"tag1", "tag2"}, list.getListTags());
        assertTrue(list.getDisplayAsPages());
        assertFalse(list.getDisplayAsTags());
        assertNotNull(list.getData());
        assertEquals("", list.getActivationData());
    }

    @Test
    public void testStaticListType() {
        List list = getListUnderTest(LIST_2);
        checkListConsistencyByPaths(list, new String[]{
                "/content/list/pages/page_1",
                "/content/list/pages/page_2",
        });
    }

    @Test
    public void testListItems() {
        List list = getListUnderTest(LIST_2);
        Collection<ListItem> listItems = list.getListItems();
        assertEquals(2, listItems.size());
        for (ListItem listItem : listItems) {
            nz.co.manawabay.core.models.ListItem teaserResource = (nz.co.manawabay.core.models.ListItem) listItem.getTeaserResource();
            assertNull(teaserResource);
        }
    }


    @Test
    protected void testChildrenListType() {
        List list = getListUnderTest(LIST_3);
        checkListConsistencyByPaths(list, new String[]{
                "/content/list/pages/page_1/page_1_1",
                "/content/list/pages/page_1/page_1_2",
                "/content/list/pages/page_1/page_1_3",
        });
    }

    @Test
    protected void testChildrenListTypeWithDepth() {
        List list = getListUnderTest(LIST_4);
        checkListConsistencyByPaths(list, new String[]{
                "/content/list/pages/page_1/page_1_1",
                "/content/list/pages/page_1/page_1_2",
                "/content/list/pages/page_1/page_1_2/page_1_2_1",
                "/content/list/pages/page_1/page_1_3",

        });
    }

    @Test
    protected void testTagsListType() {
        List list = getListUnderTest(LIST_5);
        Collection<ListItem> listItems = list.getListItems();
        assertEquals(0, listItems.size());
        checkListConsistencyByPaths(list, new String[]{"/content/list/pages/page_1/page_1_3"});
    }

    @Test
    protected void testSearchListType() throws Exception {
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

        List list = getListUnderTest(LIST_6);
        checkListConsistencyByPaths(list, new String[]{"/content/list/pages/page_1"});
    }

    @Test
    protected void testOrderBy() {
        List list = getListUnderTest(LIST_7);
        checkListConsistencyByTitle(list, new String[]{"Page 1", "Page 2"});
    }

    @Test
    protected void testOrderDescBy() {
        List list = getListUnderTest(LIST_8);
        checkListConsistencyByTitle(list, new String[]{"Page 2", "Page 1"});
    }

    @Test
    protected void testOrderByModificationDate() {
        List list = getListUnderTest(LIST_9);
        checkListConsistencyByTitle(list, new String[]{"Page 2", "Page 1"});
    }

    @Test
    protected void testOrderByModificationDateDesc() {
        List list = getListUnderTest(LIST_10);
        checkListConsistencyByTitle(list, new String[]{"Page 1", "Page 2"});
    }

    @Test
    protected void testMaxItems() {
        List list = getListUnderTest(LIST_11);
        checkListConsistencyByTitle(list, new String[]{"Page 1"});
    }

    @Test
    protected void testOrderByModificationDateWithNoModificationDate() {
        List list = getListUnderTest(LIST_12);
        checkListConsistencyByTitle(list, new String[]{"Page 1.1", "Page 1.2"});
    }

    @Test
    protected void testOrderByModificationDateWithNoModificationDateForOneItem() {
        List list = getListUnderTest(LIST_13);
        checkListConsistencyByTitle(list, new String[]{"Page 2", "Page 1", "Page 1.2"});
    }

    @Test
    protected void testOrderByTitleWithNoTitle() {
        List list = getListUnderTest(LIST_14);
        checkListConsistencyByPaths(list, new String[]{"/content/list/pages/page_3", "/content/list/pages/page_4"});
    }

    @Test
    protected void testOrderByTitleWithNoTitleForOneItem() {
        List list = getListUnderTest(LIST_15);
        checkListConsistencyByPaths(list, new String[]{"/content/list/pages/page_4", "/content/list/pages/page_1", "/content/list/pages/page_2"});
    }

    @Test
    protected void testOrderByTitleWithBlankTitle() {
        List list = getListUnderTest(LIST_21);
        checkListConsistencyByPaths(list, new String[]{
                "/content/list/pages/page_4",
                "/content/list/pages/page_6",
                "/content/list/pages/page_1",
                "/content/list/pages/page_2"
        });
    }

    @Test
    public void testOrderByTitleWithAccent() {
        List list = getListUnderTest(LIST_16);
        checkListConsistencyByPaths(list, new String[]{"/content/list/pages/page_1", "/content/list/pages/page_5", "/content/list/pages/page_2"});
    }

    private List getListUnderTest(String resourcePath) {
        ModelUtils.enableDataLayer(context, true);
        Resource resource = context.resourceResolver().getResource(resourcePath);
        if (resource == null) {
            throw new IllegalStateException("Did you forget to define test resource " + resourcePath + "?");
        }
        context.currentResource(resource);
        return context.request().adaptTo(List.class);
    }

    private void checkListConsistencyByTitle(List list, String[] expectedPageTitles) {
        assertArrayEquals(expectedPageTitles, list.getItems().stream().map(Page::getTitle).toArray());
    }

    private void checkListConsistencyByPaths(List list, String[] expectedPagePaths) {
        java.util.List<Page> pages = new ArrayList<>(list.getItems());
        for (Page page : pages) {
            assertNotNull(PageListItemImpl.getTitle(page));
        }
        Object[] array = pages.stream().map(Page::getPath).toArray();
        assertArrayEquals(expectedPagePaths, array);
    }
}
