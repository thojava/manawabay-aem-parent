package nz.co.manawabay.core.models;

import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;

@Model(adaptables = {SlingHttpServletRequest.class, Resource.class} , defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class KeyValuePair  {

    private static final Logger LOGGER = LoggerFactory.getLogger(KeyValuePair.class);

    @ScriptVariable
    protected Resource resource;
    @ValueMapValue
    public String key;

    @ValueMapValue
    public String value;

    @PostConstruct
    protected void init() {
        LOGGER.info("KeyValuePair init");

    }

}
