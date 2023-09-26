package nz.co.manawabay.core.models;

import com.adobe.cq.wcm.core.components.commons.link.Link;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.via.ResourceSuperType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import java.util.List;

import static nz.co.manawabay.core.JsonUtils.getJsonString;
import static nz.co.manawabay.core.models.ActivationModule.CONFIG_ITEMS_PATH;

@Model(adaptables = SlingHttpServletRequest.class,
        adapters = com.adobe.cq.wcm.core.components.models.Button.class,
        resourceType = "manawabay/components/button",
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class Button implements com.adobe.cq.wcm.core.components.models.Button {

    //LOGGER
    private static final Logger LOGGER = LoggerFactory.getLogger(Button.class);


    @Self
    @Via(type = ResourceSuperType.class)
    private com.adobe.cq.wcm.core.components.models.Button button;

    @ChildResource
    @Named(CONFIG_ITEMS_PATH)
    @Nullable
    private List<ActivationModule> activationconfig;

    @PostConstruct
    protected void init(){

        LOGGER.info("Button init");

    }



    @NotNull
    public String getActivationData() {
        if (activationconfig == null) {
            return "";
        }
        return getJsonString(activationconfig);
    }

    public Button() {
    }

    public Button(com.adobe.cq.wcm.core.components.models.Button button) {
        this.button = button;
    }

    @Override
    public String getText() {
        return button.getText();
    }

    @Override
    public Link getButtonLink() {
        return button.getButtonLink();
    }

    @Override
    public String getLink() {
        return button.getLink();
    }

    @Override
    public String getIcon() {
        return button.getIcon();
    }

    @Override
    public String getAccessibilityLabel() {
        return button.getAccessibilityLabel();
    }

}
