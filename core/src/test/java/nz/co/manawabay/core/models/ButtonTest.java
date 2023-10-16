package nz.co.manawabay.core.models;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import nz.co.manawabay.core.testcontext.AppAemContext;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith({AemContextExtension.class})
public class ButtonTest {

    private static final String TEST_BASE = "/button";
    protected static final String TEST_ROOT_PAGE_GRID = "/button/jcr:content/root/responsivegrid";
    protected static final String BUTTON_1 = AppAemContext.CONTENT_ROOT + TEST_ROOT_PAGE_GRID + "/button-1";

    private final AemContext context = AppAemContext.newAemContext();

    protected String testBase;
    protected String resourceType;

    @BeforeEach
    void setUp() {
        testBase = TEST_BASE;
        resourceType = "manawabay/components/button";
        internalSetup();
    }

    private void internalSetup() {
        context.load().json(testBase + AppAemContext.TEST_CONTENT_JSON, AppAemContext.CONTENT_ROOT);
        context.load().json(testBase + AppAemContext.TEST_APPS_JSON, AppAemContext.TEST_APPS_ROOT);
    }

    @Test
    void testGetText() {
        Button button = getButtonUnderTest();
        assertEquals("Adobe", button.getText());
        assertNull(button.getAccessibilityLabel());

    }

    @Test
    void testActivationData() {
        Button button = getButtonUnderTest();
        assertEquals("", button.getActivationData());
    }

    @Test
    public void testGetLink() {
        Button button = getButtonUnderTest();
        assertEquals("https://www.adobe.com", button.getLink());
    }

    @Test
    public void testGetButtonLink() {
        Button button = getButtonUnderTest();
        assertNull(button.getButtonLink());
    }

    @Test
    public void testGetIcon() {
        Button button = getButtonUnderTest();
        assertEquals("adobe", button.getIcon());
    }

    private Button getButtonUnderTest(Object... properties) {
        Resource resource = context.currentResource(ButtonTest.BUTTON_1);
        if (resource != null && properties != null) {
            context.contentPolicyMapping(resource.getResourceType(), properties);
        }
        return context.request().adaptTo(Button.class);
    }
}
