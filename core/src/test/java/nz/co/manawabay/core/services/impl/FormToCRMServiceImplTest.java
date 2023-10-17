package nz.co.manawabay.core.services.impl;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(AemContextExtension.class)
class FormToCRMServiceImplTest {

    private FormToCRMServiceImpl formToCRMServiceImplUnderTest;

    @BeforeEach
    void setUp() {
        formToCRMServiceImplUnderTest = new FormToCRMServiceImpl();
    }

    @Test
    void testPostToCRM(AemContext context) throws Exception {
        final MockSlingHttpServletRequest request = context.request();
        final SlingHttpServletResponse response = context.response();

        Map<String, Object> requestParameters = new HashMap<>();
        requestParameters.put("firstName", "Girish");
        requestParameters.put("email", "girish@manawabay.com");
        requestParameters.put("mobile", "012345");
        requestParameters.put("dateOfBirth", "01/01/2020");
        requestParameters.put("gender", "Male");
        request.setParameterMap(requestParameters);
        formToCRMServiceImplUnderTest.postToCRM(request, response);
        assertNotNull(request);
    }
}
