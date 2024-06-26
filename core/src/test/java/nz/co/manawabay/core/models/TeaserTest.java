package nz.co.manawabay.core.models;

import com.adobe.cq.wcm.core.components.models.ListItem;
import com.day.cq.commons.jcr.JcrConstants;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import nz.co.manawabay.core.testcontext.AppAemContext;
import nz.co.manawabay.core.utils.ModelUtils;
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
class TeaserTest {

    private static final String TEST_BASE = "/teaser";
    static final String CONF_ROOT = "/conf";
    static final String PNG_IMAGE_BINARY_NAME = "Adobe_Systems_logo_and_wordmark.png";
    static final String PNG_ASSET_PATH = "/content/dam/core/images/" + PNG_IMAGE_BINARY_NAME;
    static final String VIDEO_BINARY_NAME = "test_video.mp4";
    static final String CONTEXT_PATH = "/core";
    static final String TEST_ROOT_PAGE = "/content/teasers";
    static final String TEST_ROOT_PAGE_GRID = "/jcr:content/root/responsivegrid";
    static final String TITLE = "Teaser";
    static final String PRETITLE = "Teaser's Pretitle";
    static final String DESCRIPTION = "Description";
    static final String TEASER_1 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/teaser-1";
    static final String TEASER_2 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/teaser-2";
    static final String TEASER_3 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/teaser-3";
    static final String TEASER_4 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/teaser-4";
    static final String TEASER_5 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/teaser-5";
    static final String TEASER_6 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/teaser-6";
    static final String TEASER_7 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/teaser-7";
    static final String TEASER_8 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/teaser-8";
    static final String TEASER_9 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/teaser-9";
    static final String TEASER_10 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/teaser-10";
    static final String TEASER_11 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/teaser-11";
    static final String TEASER_12 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/teaser-12";
    static final String TEASER_13 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/teaser-13";
    static final String TEASER_14 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/teaser-14";
    static final String TEASER_15 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/teaser-15";
    static final String TEASER_16 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/teaser-16";

    final AemContext context = AppAemContext.newAemContext();

    String testBase;

    @BeforeEach
    void setUp() {
        testBase = TEST_BASE;
        internalSetup();
    }

    void internalSetup() {
        context.load().json(testBase + AppAemContext.TEST_CONTENT_JSON, AppAemContext.CONTENT_ROOT);
        context.load().json(testBase + AppAemContext.TEST_CONTENT_DAM_JSON, "/content/dam/core/images");
        context.load().json(testBase + AppAemContext.TEST_APPS_JSON, AppAemContext.TEST_APPS_ROOT);
        context.load().json(testBase + AppAemContext.TEST_CONF_JSON, CONF_ROOT);
        context.load().binaryFile("/image/" + PNG_IMAGE_BINARY_NAME, PNG_ASSET_PATH + "/jcr:content/renditions/original");
        context.load().json(testBase + AppAemContext.TEST_CONTENT_DAM_VIDEO_JSON, "/content/dam/core/videos");
        context.load().binaryFile("/video/" + VIDEO_BINARY_NAME, "/content/dam/core/videos/jcr:content/renditions/original");
    }

    @Test
    void testFullyConfiguredTeaser() {
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
        assertTrue(teaser.getShowPublishDate());
        assertTrue(teaser.getShowIcon());
        assertFalse(teaser.getHideImage());
        assertFalse(teaser.getHideTitle());
        assertTrue(teaser.getShowDescription());
        assertTrue(teaser.getShowBrandImage());
        assertFalse(teaser.isImageLinkHidden());
        assertEquals("YYYYmmdd", teaser.getPublishDateFormatString());
    }

    @Test
    void testFullyConfiguredTeaserVanityPath() {
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
    void testPageInheritedProperties() {
        Teaser teaser = getTeaserUnderTest(TEASER_6);
        assertEquals("Teasers Test", teaser.getTitle());
        // < and > are expected escaped, because the page properties provide only a plain text field for the page description
        assertEquals("Teasers description from &lt;page properties&gt;", teaser.getDescription());
    }

    @Test
    void testInvalidFileReference() {
        Teaser teaser = getTeaserUnderTest(TEASER_2);
        assertNull(teaser.getImageResource());
    }

    @Test
    void testEmptyFileReference() {
        Teaser teaser = getTeaserUnderTest(TEASER_3);
        assertNull(teaser.getImageResource());
    }

    @Test
    @SuppressWarnings("deprecation")
    void testTeaserWithoutLink() {
        Teaser teaser = getTeaserUnderTest(TEASER_4);
        assertNull(teaser.getLinkURL());
    }

    @Test
    void testTeaserWithHiddenLinks() {
        getTeaserUnderTest(TEASER_5,
                Teaser.PN_TITLE_LINK_HIDDEN, true,
                Teaser.PN_IMAGE_LINK_HIDDEN, true);
    }

    @Test
    void testTeaserWithHiddenElements() {
        getTeaserUnderTest(TEASER_5,
                Teaser.PN_TITLE_HIDDEN, true,
                Teaser.PN_DESCRIPTION_HIDDEN, true);
    }

    @Test
    void testTeaserWithoutImage() {
        getTeaserUnderTest(TEASER_9);
    }

    @Test
    void testTeaserWithActions() {
        Teaser teaser = getTeaserUnderTest(TEASER_7);
        assertTrue(teaser.isActionsEnabled(), "Expected teaser with actions");
        assertEquals(2, teaser.getActions().size(), "Expected to find two actions");
        ListItem action = teaser.getActions().get(0);
        assertEquals("https://www.adobe.com", action.getPath(), "Action link does not match");
        assertEquals("Adobe", action.getTitle(), "Action text does not match");
    }

    @Test
    void testTeaserWithActionsDisabled() {
        getTeaserUnderTest(TEASER_7, Teaser.PN_ACTIONS_DISABLED, true);
    }

    @Test
    void testTeaserWithTitleAndDescriptionFromActions() {
        Teaser teaser = getTeaserUnderTest(TEASER_8);
        assertTrue(teaser.isActionsEnabled(), "Expected teaser with actions");
        assertEquals(2, teaser.getActions().size(), "Expected to find two Actions");
        assertEquals("Teasers Test", teaser.getTitle());
        // < and > are expected escaped, because the page properties provide only a plain text field for the page description
        assertEquals("Teasers description from &lt;page properties&gt;", teaser.getDescription());
    }

    @Test
    void testTeaserWithTitleType() {
        Teaser teaser = getTeaserUnderTest(TEASER_1, Teaser.PN_TITLE_TYPE, "h5");
        assertEquals("h5", teaser.getTitleType(), "Expected title type is not correct");
    }

    @Test
    void testTeaserWithDefaultTitleType() {
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
        assertEquals("https://www.adobe.com", actions.get(0).getPath());
        assertEquals("Adobe", actions.get(0).getTitle());
        assertEquals("/content/teasers", actions.get(1).getPath());
        assertEquals("Teasers", actions.get(1).getTitle());
    }

    @Test
    void testTeaserWithTitleFromLinkedPageAndActions() {
        Teaser teaser = getTeaserUnderTest(TEASER_12);
        assertEquals("Adobe", teaser.getTitle());
        List<ListItem> actions = teaser.getActions();
        assertEquals("https://www.adobe.com", actions.get(0).getPath());
        assertEquals("Adobe", actions.get(0).getTitle());
    }

    @Test
    void testTeaserWithCTALinkContainingSpecialCharacters() {
        String ENCODED_LINK_URL = CONTEXT_PATH + "/content/teasers/%C3%A9.html";
        Teaser teaser = getTeaserUnderTest(TEASER_14);
        List<ListItem> actions = teaser.getActions();
        assertEquals(ENCODED_LINK_URL, Objects.requireNonNull(actions.get(0).getLink()).getURL());
    }

    @Test
    void testTeaserWithLinkToAsset() {
        Teaser teaser = getTeaserUnderTest(TEASER_15);
        List<ListItem> actions = teaser.getActions();
        assertEquals(CONTEXT_PATH + "/content/dam/core/images/Adobe_Systems_logo_and_wordmark.png", actions.get(0).getPath());
    }

    @Test
    void testTeaserWithVideoAsset() {
        Teaser teaser = getTeaserUnderTest(TEASER_16);
        assertEquals("/content/dam/core/videos/sample_video.mp4.coredownload.mp4", teaser.getVideoUrl());
    }

    Teaser getTeaserUnderTest(String resourcePath, Object... properties) {
        ModelUtils.enableDataLayer(context, true);
        MockSlingHttpServletRequest request = context.request();
        Resource resource = context.currentResource(resourcePath);
        if (resource != null && properties != null) {
            context.contentPolicyMapping(resource.getResourceType(), properties);
        }
        request.setContextPath(CONTEXT_PATH);
        return request.adaptTo(Teaser.class);
    }

    void testImageResourceValueMap(ValueMap valueMap) {
        assertFalse(valueMap.containsKey(JcrConstants.JCR_TITLE));
        assertFalse(valueMap.containsKey(JcrConstants.JCR_DESCRIPTION));
    }
}
