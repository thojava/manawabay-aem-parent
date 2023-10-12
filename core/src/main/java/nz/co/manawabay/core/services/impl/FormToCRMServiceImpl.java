package nz.co.manawabay.core.services.impl;

import com.day.cq.wcm.foundation.forms.FormsConstants;
import com.google.gson.Gson;
import nz.co.manawabay.core.pojos.crm.Address;
import nz.co.manawabay.core.pojos.crm.Contact;
import nz.co.manawabay.core.pojos.crm.CustomerSubscriptions;
import nz.co.manawabay.core.services.FormToCRMService;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;

@Component(service = FormToCRMService.class)
@Designate(ocd = FormToCRMServiceImpl.Config.class)
public class FormToCRMServiceImpl implements FormToCRMService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FormToCRMServiceImpl.class);

    private String crmEndpoint;
    private String crmAPIKey;

    @Activate
    public void activate(Config config) {
        this.crmEndpoint = config.crm_endpoint();
        this.crmAPIKey = config.crm_apikey();
    }

    @Override
    public void postToCRM(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        postToCRM(request);

        redirectToThankYouPage(request, response);
    }

    private static void redirectToThankYouPage(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        String redirectPath = request.getParameter(FormsConstants.REQUEST_PROPERTY_REDIRECT);
        if (StringUtils.isNotBlank(redirectPath))
            response.sendRedirect(redirectPath);
    }

    private void postToCRM(SlingHttpServletRequest request) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(this.crmEndpoint + "?key=" + this.crmAPIKey);

            Contact contact = Contact.builder()
                    .firstName(request.getParameter("firstName"))
                    .lastName(null)
                    .email(request.getParameter("email"))
                    .mobile(request.getParameter("mobile"))
                    .dateOfBirth(request.getParameter("dateOfBirth"))
                    .gender(request.getParameter("gender"))
                    .initialSignUpSource("ManawaBayWifi")
                    .address(Address.builder()
                            .country(request.getParameter("country")).
                            city(request.getParameter("city")).
                            postcode(request.getParameter("postcode")).build())
                    .customerSubscriptions(CustomerSubscriptions.builder().build())
                    .customerInterests(Collections.emptyList())
                    .build();

            String jsonBody = new Gson().toJson(contact);
            httpPost.setEntity(new StringEntity(jsonBody));
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            LOGGER.info("Calling API with request body" + jsonBody);
            CloseableHttpResponse httpResponse = client.execute(httpPost);
            String response = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
            LOGGER.info("Called API successfully with response" + response);
        } catch (IOException exception) {
            LOGGER.error("Error when creating HTTP client", exception);
        }
    }

    @ObjectClassDefinition(name = "Form to CRM Service Configuration")
    @interface Config {
        @AttributeDefinition(
                name = "CRM Endpoint to post the form data"
        )
        String crm_endpoint();

        @AttributeDefinition(
                name = "The secreted key use to call CRM API"
        )
        String crm_apikey();
    }
}
