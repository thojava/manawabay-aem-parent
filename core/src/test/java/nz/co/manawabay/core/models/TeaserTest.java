package nz.co.manawabay.core.models;

import com.adobe.cq.wcm.core.components.models.ListItem;
import com.day.cq.commons.jcr.JcrConstants;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import nz.co.manawabay.core.testcontext.AppAemContext;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(AemContextExtension.class)
public class TeaserTest {

    private static final String TEST_BASE = "/teaser";
    protected static final String CONTENT_ROOT = "/content";
    protected static final String CONF_ROOT = "/conf";
    protected static final String PNG_IMAGE_BINARY_NAME = "Adobe_Systems_logo_and_wordmark.png";
    protected static final String PNG_ASSET_PATH = "/content/dam/core/images/" + PNG_IMAGE_BINARY_NAME;
    protected static final String CONTEXT_PATH = "/core";
    protected static final String TEST_ROOT_PAGE = "/content/teasers";
    protected static final String TEST_APPS_ROOT = "/apps/manawabay/components";
    protected static final String TEST_ROOT_PAGE_GRID = "/jcr:content/root/responsivegrid";
    protected static final String TITLE = "Teaser";
    protected static final String PRETITLE = "Teaser's Pretitle";
    protected static final String DESCRIPTION = "Description";
    protected static final String TEASER_1 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/teaser-1";
    protected static final String TEASER_2 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/teaser-2";
    protected static final String TEASER_3 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/teaser-3";
    protected static final String TEASER_4 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/teaser-4";
    protected static final String TEASER_5 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/teaser-5";
    protected static final String TEASER_6 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/teaser-6";
    protected static final String TEASER_7 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/teaser-7";
    protected static final String TEASER_8 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/teaser-8";
    protected static final String TEASER_9 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/teaser-9";
    protected static final String TEASER_10 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/teaser-10";
    protected static final String TEASER_11 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/teaser-11";
    protected static final String TEASER_12 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/teaser-12";
    protected static final String TEASER_13 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/teaser-13";
    protected static final String TEASER_14 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/teaser-14";
    protected static final String TEASER_15 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/teaser-15";

    protected final AemContext context = AppAemContext.newAemContext();

    protected String testBase;

    @BeforeEach
    void setUp() {
        testBase = TEST_BASE;
        internalSetup();
    }

    protected void internalSetup() {
        context.load().json(testBase + AppAemContext.TEST_CONTENT_JSON, CONTENT_ROOT);
        context.load().json(testBase + AppAemContext.TEST_CONTENT_DAM_JSON, "/content/dam/core/images");
        context.load().json(testBase + AppAemContext.TEST_APPS_JSON, AppAemContext.TEST_APPS_ROOT);
        context.load().json(testBase + AppAemContext.TEST_CONF_JSON, CONF_ROOT);
        context.load().binaryFile("/image/" + PNG_IMAGE_BINARY_NAME, PNG_ASSET_PATH + "/jcr:content/renditions/original");
    }

    @Test
    protected void testFullyConfiguredTeaser() {
        Teaser teaser = getTeaserUnderTest(TEASER_1);
        if (teaser.getImageResource() != null) {
            // let's verify the ValueMap wrapping here
            testImageResourceValueMap(teaser.getImageResource().getValueMap());
            testImageResourceValueMap(Objects.requireNonNull(teaser.getImageResource().adaptTo(ValueMap.class)));
            assertEquals(TEASER_1, teaser.getImageResource().getPath());
        }
        assertEquals(PRETITLE, teaser.getPretitle());
        assertEquals(TITLE, teaser.getTitle());
        assertEquals(DESCRIPTION, teaser.getDescription());
    }

    @Test
    protected void testFullyConfiguredTeaserVanityPath() {
        Teaser teaser = getTeaserUnderTest(TEASER_5);
        if (teaser.getImageResource() != null) {
            // let's verify the ValueMap wrapping here
            testImageResourceValueMap(teaser.getImageResource().getValueMap());
            testImageResourceValueMap(Objects.requireNonNull(teaser.getImageResource().adaptTo(ValueMap.class)));
            assertEquals(TEASER_5, teaser.getImageResource().getPath());
        }
        assertEquals(TITLE, teaser.getTitle());
        assertEquals(DESCRIPTION, teaser.getDescription());
    }

    @Test
    protected void testPageInheritedProperties() {
        Teaser teaser = getTeaserUnderTest(TEASER_6);
        assertEquals("Teasers Test", teaser.getTitle());
        // < and > are expected escaped, because the page properties provide only a plain text field for the page description
        assertEquals("Teasers description from &lt;page properties&gt;", teaser.getDescription());
    }

    @Test
    protected void testInvalidFileReference() {
        Teaser teaser = getTeaserUnderTest(TEASER_2);
        assertNull(teaser.getImageResource());
    }

    @Test
    protected void testEmptyFileReference() {
        Teaser teaser = getTeaserUnderTest(TEASER_3);
        assertNull(teaser.getImageResource());
    }

    @Test
    @SuppressWarnings("deprecation")
    protected void testTeaserWithoutLink() {
        Teaser teaser = getTeaserUnderTest(TEASER_4);
        assertNull(teaser.getLinkURL());
    }

    @Test
    protected void testTeaserWithHiddenLinks() {
        getTeaserUnderTest(TEASER_5,
                Teaser.PN_TITLE_LINK_HIDDEN, true,
                Teaser.PN_IMAGE_LINK_HIDDEN, true);
    }

    @Test
    protected void testTeaserWithHiddenElements() {
        getTeaserUnderTest(TEASER_5,
                Teaser.PN_TITLE_HIDDEN, true,
                Teaser.PN_DESCRIPTION_HIDDEN, true);
    }

    @Test
    protected void testTeaserWithoutImage() {
        getTeaserUnderTest(TEASER_9);
    }

    @Test
    protected void testTeaserWithActions() {
        Teaser teaser = getTeaserUnderTest(TEASER_7);
        assertTrue(teaser.isActionsEnabled(), "Expected teaser with actions");
        assertEquals(2, teaser.getActions().size(), "Expected to find two actions");
        ListItem action = teaser.getActions().get(0);
        assertEquals("http://www.adobe.com", action.getPath(), "Action link does not match");
        assertEquals("Adobe", action.getTitle(), "Action text does not match");
    }

    @Test
    protected void testTeaserWithActionsDisabled() {
        getTeaserUnderTest(TEASER_7,
                Teaser.PN_ACTIONS_DISABLED, true);
    }

    @Test
    protected void testTeaserWithTitleAndDescriptionFromActions() {
        Teaser teaser = getTeaserUnderTest(TEASER_8);
        assertTrue(teaser.isActionsEnabled(), "Expected teaser with actions");
        assertEquals(2, teaser.getActions().size(), "Expected to find two Actions");
        assertEquals("Teasers Test", teaser.getTitle());
        // < and > are expected escaped, because the page properties provide only a plain text field for the page description
        assertEquals("Teasers description from &lt;page properties&gt;", teaser.getDescription());
    }

    @Test
    protected void testTeaserWithTitleType() {
        Teaser teaser = getTeaserUnderTest(TEASER_1,
                Teaser.PN_TITLE_TYPE, "h5");
        assertEquals("h5", teaser.getTitleType(), "Expected title type is not correct");
    }

    @Test
    protected void testTeaserWithDefaultTitleType() {
        Teaser teaser = getTeaserUnderTest(TEASER_1);
        assertNull(teaser.getTitleType(), "Expected the default title type is not correct");
    }

    @Test
    void testTeaserWithTitleTypeOverride() {
        Teaser teaser = getTeaserUnderTest(TEASER_13,
                Teaser.PN_TITLE_TYPE, "h5", Teaser.PN_SHOW_TITLE_TYPE, true);
        assertEquals("h4", teaser.getTitleType(), "Expected title type is not correct");
    }

    @Test
    void testTeaserWithTitleTypeOverrideHidden() {
        Teaser teaser = getTeaserUnderTest(TEASER_13,
                Teaser.PN_TITLE_TYPE, "h5", Teaser.PN_SHOW_TITLE_TYPE, false);
        assertEquals("h5", teaser.getTitleType(), "Expected title type is not correct");
    }

    @Test
    void testTeaserWithTitleNotFromLinkedPageAndNoActions() {
        Teaser teaser = getTeaserUnderTest(TEASER_10);
        assertEquals("Teaser", teaser.getTitle());
        assertTrue(teaser.getActions().isEmpty());
    }

    @Test
    void testTeaserWithTitleNotFromLinkedPageAndActions() {
        Teaser teaser = getTeaserUnderTest(TEASER_11);
        assertEquals("Teaser", teaser.getTitle());
        List<ListItem> actions = teaser.getActions();
        assertEquals("http://www.adobe.com", actions.get(0).getPath());
        assertEquals("Adobe", actions.get(0).getTitle());
        assertEquals("/content/teasers", actions.get(1).getPath());
        assertEquals("Teasers", actions.get(1).getTitle());
    }

    @Test
    void testTeaserWithTitleFromLinkedPageAndActions() {
        Teaser teaser = getTeaserUnderTest(TEASER_12);
        assertEquals("Adobe", teaser.getTitle());
        List<ListItem> actions = teaser.getActions();
        assertEquals("http://www.adobe.com", actions.get(0).getPath());
        assertEquals("Adobe", actions.get(0).getTitle());
    }

    @Test
    void testTeaserWithCTALinkContainingSpecialCharacters() {
        String ENCODED_LINK_URL = CONTEXT_PATH + "/content/teasers/%C3%A9.html";
        Teaser teaser = getTeaserUnderTest(TEASER_14);
        List<ListItem> actions = teaser.getActions();
        assertEquals(ENCODED_LINK_URL, actions.get(0).getURL());
    }

    @Test
    protected void testTeaserWithLinkToAsset() {
        Teaser teaser = getTeaserUnderTest(TEASER_15);
        List<ListItem> actions = teaser.getActions();
        assertEquals(CONTEXT_PATH + "/content/dam/core/images/Adobe_Systems_logo_and_wordmark.png", actions.get(0).getPath());
    }

    protected Teaser getTeaserUnderTest(String resourcePath, Object... properties) {
        Utils.enableDataLayer(context, true);
        MockSlingHttpServletRequest request = context.request();
        Resource resource = context.currentResource(resourcePath);
        if (resource != null && properties != null) {
            context.contentPolicyMapping(resource.getResourceType(), properties);
        }
        request.setContextPath(CONTEXT_PATH);
        return request.adaptTo(Teaser.class);
    }

    protected void testImageResourceValueMap(ValueMap valueMap) {
        assertFalse(valueMap.containsKey(JcrConstants.JCR_TITLE));
        assertFalse(valueMap.containsKey(JcrConstants.JCR_DESCRIPTION));
    }
}
