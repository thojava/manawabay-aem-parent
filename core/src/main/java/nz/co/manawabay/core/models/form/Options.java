package nz.co.manawabay.core.models.form;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.annotations.via.ResourceSuperType;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Model(adaptables = SlingHttpServletRequest.class,
        adapters = com.adobe.cq.wcm.core.components.models.form.Options.class,
        resourceType = "manawabay/components/form/options",
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class Options implements com.adobe.cq.wcm.core.components.models.form.Options {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(Options.class);

    @Self
    @Via(type = ResourceSuperType.class)
    private com.adobe.cq.wcm.core.components.models.form.Options options;

    private List<com.adobe.cq.wcm.core.components.models.form.OptionItem> optionItems;

    @ValueMapValue(name = "source")
    @Nullable
    private String source;

    @ValueMapValue
    @Nullable
    private String datasourceRT;

    @Override
    public List<com.adobe.cq.wcm.core.components.models.form.OptionItem> getItems() {
        if (StringUtils.equalsIgnoreCase(source, "datasource")) {
            if (optionItems == null) {
                populateOptionItemsFromDatasource();
            }
            return optionItems;
        }

        return options.getItems();
    }

    private void populateOptionItemsFromDatasource() {
        optionItems = new ArrayList<>();

        if (StringUtils.isBlank(datasourceRT)) {
            return;
        }

        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            client.execute(new HttpGet(datasourceRT), response -> {
                final int statusCode = response.getStatusLine().getStatusCode();

                if (statusCode == HttpStatus.SC_OK) {
                    final InputStream content = response.getEntity().getContent();
                    final List<OptionItemImpl> items = OBJECT_MAPPER.readValue(content, new TypeReference<List<OptionItemImpl>>() {
                    });
                    optionItems.addAll(items);
                }

                return response;
            });
        } catch (IOException e) {
            LOGGER.error(String.format("Cannot get items from resource: %s", source), e);
        }
    }

    @Override
    public Type getType() {
        return options.getType();
    }

    @Override
    public String getId() {
        return options.getId();
    }

    @Override
    public String getTitle() {
        return options.getTitle();
    }

    @Override
    public String getHelpMessage() {
        return options.getHelpMessage();
    }
}
