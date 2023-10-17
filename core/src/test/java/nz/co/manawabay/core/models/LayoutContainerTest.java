package nz.co.manawabay.core.models;

import com.adobe.cq.wcm.core.components.models.Container;
import com.adobe.cq.wcm.core.components.models.ListItem;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import nz.co.manawabay.core.utils.ModelUtils;
import nz.co.manawabay.core.testcontext.AppAemContext;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(AemContextExtension.class)
public class LayoutContainerTest {
    private static final String TEST_BASE = "/container";
    private static final String TEST_ROOT_PAGE = AppAemContext.CONTENT_ROOT + TEST_BASE;
    private static final String TEST_ROOT_PAGE_GRID = "/jcr:content/root/responsivegrid";
    private static final String CONTAINER_1 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/container-1";
    private static final String CONTAINER_2 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/container-2";
    private static final String CONTAINER_3 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/container-3";
    private static final String CONTAINER_4 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/container-4";
    public final AemContext context = AppAemContext.newAemContext();

    @BeforeEach
    public void init() {
        context.load().json(TEST_BASE + AppAemContext.TEST_CONTENT_JSON, AppAemContext.CONTENT_ROOT);
        context.load().json(TEST_BASE + AppAemContext.TEST_APPS_JSON, AppAemContext.TEST_APPS_ROOT);
    }

    @Test
    public void testContainerWithPropertiesAndPolicy() {
        context.contentPolicyMapping(LayoutContainer.RESOURCE_TYPE, new HashMap<String, Object>() {{
            put(Container.PN_BACKGROUND_IMAGE_ENABLED, true);
            put(Container.PN_BACKGROUND_COLOR_ENABLED, true);
            put(LayoutContainer.PN_LAYOUT, "simple");
        }});
        LayoutContainer container = getContainerUnderTest(CONTAINER_1);
        Resource containerResource = context.resourceResolver().getResource(CONTAINER_1);

        assertEquals(
                "background-image:url(/content/dam/core-components-examples/library/sample-assets/mountain-range.jpg);background-size:cover;background-repeat:no-repeat;background-color:#000000;",
                container.getBackgroundStyle(),
                "Style mismatch");
        // layout set in component properties
        assertEquals(LayoutContainer.LayoutType.RESPONSIVE_GRID, container.getLayout(), "Layout type mismatch");
        Object[][] expectedItems = {
                {"item_1", "Teaser 1"},
                {"item_2", "Teaser 2"}
        };
        assert containerResource != null;
        verifyContainerItems(containerResource, expectedItems, container.getItems());
        assertEquals(2, container.getExportedItems().size());
        assertEquals(2, container.getExportedItemsOrder().length);
        assertNull(container.getAccessibilityLabel());
        assertNull(container.getRoleAttribute());
        assertEquals("", container.getActivationData());
        assertEquals(2, container.getChildren().size());
    }

    @Test
    public void testContainerWithPropertiesAndLayoutInPolicy() {
        context.contentPolicyMapping(LayoutContainer.RESOURCE_TYPE, new HashMap<String, Object>() {{
            put(LayoutContainer.PN_LAYOUT, "responsiveGrid");
        }});
        LayoutContainer container = getContainerUnderTest(CONTAINER_2);

        // layout set in content policy
        assertEquals(LayoutContainer.LayoutType.RESPONSIVE_GRID, container.getLayout(), "Layout type mismatch");
    }

    @Test
    public void testContainerNoProperties() {
        LayoutContainer container = getContainerUnderTest(CONTAINER_2);
        assertNull(container.getBackgroundStyle(), "Style");
        assertEquals(LayoutContainer.LayoutType.SIMPLE, container.getLayout(), "Layout type mismatch");
    }

    @Test
    public void testContainerWithPropertiesAndNoPolicy() {
        LayoutContainer container = getContainerUnderTest(CONTAINER_3);
        assertNull(container.getBackgroundStyle(), "Style");
    }

    @Test
    public void testSpaceEscapingInBackgroundImage() {
        context.contentPolicyMapping(LayoutContainer.RESOURCE_TYPE, new HashMap<String, Object>() {{
            put(Container.PN_BACKGROUND_IMAGE_ENABLED, true);
            put(Container.PN_BACKGROUND_COLOR_ENABLED, true);
            put(LayoutContainer.PN_LAYOUT, "simple");
        }});
        LayoutContainer container = getContainerUnderTest(CONTAINER_4);
        assertEquals("background-image:url(/content/dam/core-components-examples/library/sample-assets/mountain%20range.jpg);background-size:cover;background-repeat:no-repeat;background-color:#000000;", container.getBackgroundStyle(), "Style mismatch");
    }

    private void verifyContainerItems(Resource containerResource, Object[][] expectedItems, List<ListItem> items) {
        assertEquals(expectedItems.length, items.size(), "Item number mismatch");
        String containerPath = containerResource.getPath();
        int index = 0;
        for (ListItem item : items) {
            assertEquals(containerPath + "/" + expectedItems[index][0], item.getPath());
            assertEquals(expectedItems[index][1], item.getTitle(), "Item title mismatch");
            index++;
        }
    }

    private LayoutContainer getContainerUnderTest(String resourcePath) {
        ModelUtils.enableDataLayer(context, true);
        Resource resource = context.resourceResolver().getResource(resourcePath);
        if (resource == null) {
            throw new IllegalStateException("Does the test resource " + resourcePath + " exist?");
        }
        context.currentResource(resource);

        return context.request().adaptTo(LayoutContainer.class);
    }
}

