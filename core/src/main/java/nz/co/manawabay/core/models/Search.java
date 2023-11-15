package nz.co.manawabay.core.models;

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

import static nz.co.manawabay.core.utils.JsonUtils.getJsonString;
import static nz.co.manawabay.core.models.ActivationModule.CONFIG_ITEMS_PATH;

@Model(adaptables = SlingHttpServletRequest.class,
        adapters = com.adobe.cq.wcm.core.components.models.Search.class,
        resourceType = "manawabay/components/search",
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class Search implements com.adobe.cq.wcm.core.components.models.Search {

    //LOGGER
    private static final Logger LOGGER = LoggerFactory.getLogger(Search.class);


    @Self
    @Via(type = ResourceSuperType.class)
    private com.adobe.cq.wcm.core.components.models.Search search;

    @ChildResource
    @Named(CONFIG_ITEMS_PATH)
    @Nullable
    private List<ActivationModule> activationconfig;

    @PostConstruct
    protected void init(){

        LOGGER.info("Search init");

    }


    @NotNull
    public String getActivationData() {
        if (activationconfig == null) {
            return "";
        }
        return getJsonString(activationconfig);
    }

    public Search() {
    }

    public Search(com.adobe.cq.wcm.core.components.models.Search search) {
        this.search = search;
    }

    @Override
    public int getResultsSize() {
        return search.getResultsSize();
    }

    @Override
    public int getSearchTermMinimumLength() {
        return search.getSearchTermMinimumLength();
    }

    @Override
    public String getRelativePath() {
        return search.getRelativePath();
    }

    @Override
    public String getSearchRootPagePath() {
        return search.getSearchRootPagePath();
    }

}
