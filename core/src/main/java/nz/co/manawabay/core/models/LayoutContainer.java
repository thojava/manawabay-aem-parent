package nz.co.manawabay.core.models;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.wcm.core.components.models.ContainerItem;
import com.adobe.cq.wcm.core.components.models.ListItem;
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
import java.util.Map;

import static nz.co.manawabay.core.utils.JsonUtils.getJsonString;
import static nz.co.manawabay.core.models.ActivationModule.CONFIG_ITEMS_PATH;

@Model(adaptables = SlingHttpServletRequest.class,
        adapters = com.adobe.cq.wcm.core.components.models.LayoutContainer.class,
        resourceType = LayoutContainer.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class LayoutContainer implements com.adobe.cq.wcm.core.components.models.LayoutContainer {

    public static final String RESOURCE_TYPE = "manawabay/components/container";
    //LOGGER
    private static final Logger LOGGER = LoggerFactory.getLogger(LayoutContainer.class);


    @Self
    @Via(type = ResourceSuperType.class)
    private com.adobe.cq.wcm.core.components.models.LayoutContainer container;

    @ChildResource
    @Named(CONFIG_ITEMS_PATH)
    @Nullable
    private List<ActivationModule> activationconfig;

    @PostConstruct
    protected void init(){

        LOGGER.info("Container init");

    }


    @NotNull
    public String getActivationData() {
        if (activationconfig == null) {
            return "";
        }
        return getJsonString(activationconfig);
    }

    public LayoutContainer() {
    }

    public LayoutContainer(com.adobe.cq.wcm.core.components.models.LayoutContainer container) {
        this.container = container;
    }

    @Override
    public List<ListItem> getItems() {
        return container.getItems();
    }

    @Override
    public List<? extends ContainerItem> getChildren() {
        return container.getChildren();
    }

    @Override
    public String getBackgroundStyle() {
        return container.getBackgroundStyle();
    }

    @Override
    public Map<String, ? extends ComponentExporter> getExportedItems() {
        return container.getExportedItems();
    }
    @Override
    public String[] getExportedItemsOrder() { return container.getExportedItemsOrder(); }
    @Override
    public LayoutType getLayout() { return container.getLayout(); }
    @Override
    public String getAccessibilityLabel() { return container.getAccessibilityLabel(); }
    @Override
    public String getRoleAttribute() { return container.getRoleAttribute(); }

}
