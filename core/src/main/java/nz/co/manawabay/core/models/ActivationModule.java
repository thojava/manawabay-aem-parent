package nz.co.manawabay.core.models;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.List;

@Model(adaptables = {SlingHttpServletRequest.class, Resource.class} , defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ActivationModule {
    private static final Logger LOGGER = LoggerFactory.getLogger(ActivationModule.class);
    public static final String CONFIG_ITEMS_PATH = "activationconfig";

    @ValueMapValue
    public String[] tags;
    @ValueMapValue
    public String type;
    @ValueMapValue
    public String topic;
    @ValueMapValue
    public String module;

    @ChildResource
    public List<KeyValuePair> options;

    @PostConstruct
    protected void init() {
        LOGGER.info("ActivationModuleItem init");
    }

}
