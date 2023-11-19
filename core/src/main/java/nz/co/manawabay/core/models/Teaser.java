package nz.co.manawabay.core.models;

import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.models.Image;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.day.cq.commons.DownloadResource;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.components.Component;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import nz.co.manawabay.core.internal.resource.CoreResourceWrapper;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.commons.mime.MimeTypeService;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.*;
import org.apache.sling.models.annotations.via.ResourceSuperType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.*;


@Model(adaptables = SlingHttpServletRequest.class,
        adapters = com.adobe.cq.wcm.core.components.models.Teaser.class,
        resourceType = "manawabay/components/teaser",
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Slf4j
public class Teaser implements com.adobe.cq.wcm.core.components.models.Teaser {
    public static final String PN_IMAGE_DELEGATE = "imageDelegate";
    public static final String PN_PAGE_PUBLISHDATE = "publishDate";

    public static final String NN_PAGE_ICON_IMAGE = "icon";
    public static final String NN_PAGE_BRANDIMAGE_IMAGE = "brandimage";

    protected ValueMap pageProperties;

    @ScriptVariable
    protected Page currentPage;

    @ScriptVariable
    protected Resource resource;

    @ScriptVariable
    protected Component component;

    @SlingObject
    private ResourceResolver resourceResolver;

    @Self
    @Via(type = ResourceSuperType.class)
    private com.adobe.cq.wcm.core.components.models.Teaser teaser;

    @OSGiService
    private MimeTypeService mimeTypeService;

    private Resource iconResource;
    private Resource brandImageResource;
    @ValueMapValue
    private Calendar publishDate;

    @ValueMapValue
    protected boolean showIcon;
    @ValueMapValue
    protected boolean showPublishDate;
    @ValueMapValue
    protected boolean hideTitle;
    @ValueMapValue
    protected boolean showDescription;
    @ValueMapValue
    protected boolean hideImage;

    @ValueMapValue
    protected boolean showBrandImage;
    @ValueMapValue
    private String publishDateFormatString;

    public Calendar getPublishDate() {
        if (publishDate == null) {
            publishDate = pageProperties.get(PN_PAGE_PUBLISHDATE, Calendar.class);
        }
        return publishDate;
    }

    public boolean getShowPublishDate() {
        return showPublishDate;
    }

    public boolean getShowIcon() {
        return showIcon;
    }

    public boolean getHideTitle() {
        return hideTitle;
    }

    public boolean getHideImage() {
        return hideImage;
    }

    public boolean getShowDescription() {
        return showDescription;
    }

    public boolean getShowBrandImage() {
        return showBrandImage;
    }

    public String getPublishDateFormatString() {
        return publishDateFormatString;
    }

    @Getter
    private String videoUrl;

    @JsonIgnore
    public Resource getIconImage(@NotNull Page page) {
        if (this.iconResource == null) {
            this.iconResource = page.getContentResource(this.NN_PAGE_ICON_IMAGE);
        }
        return iconResource;
    }

    @JsonIgnore
    public Resource getBrandImage(@NotNull Page page) {
        if (this.brandImageResource == null) {
            this.brandImageResource = page.getContentResource(this.NN_PAGE_BRANDIMAGE_IMAGE);
        }
        return brandImageResource;
    }


    @JsonIgnore
    public Resource getIconResource() {
        if (iconResource != null && component != null) {
            String delegateResourceType = component.getProperties().get(PN_IMAGE_DELEGATE, String.class);
            if (StringUtils.isEmpty(delegateResourceType)) {
                log.error("In order for list rendering delegation to work correctly you need to set up the teaserDelegate property on" +
                        " the {} component; its value has to point to the resource type of a teaser component.", component.getPath());
            } else {
                ValueMap valueMap = iconResource.getValueMap();
                Map<String, Object> overriddenProperties = new HashMap<>();
                List<String> hiddenProperties = new ArrayList<>();
                String inheritedFileReference = valueMap.get(DownloadResource.PN_REFERENCE, String.class);
                overriddenProperties.put(DownloadResource.PN_REFERENCE, inheritedFileReference);
                overriddenProperties.put(Image.PN_EXTERNAL_IMAGE_RESOURCE_PATH, iconResource.getPath());
                // use the page featured image and inherit properties from the page item
                overriddenProperties.put(JcrConstants.JCR_TITLE, this.getTitle());

                hiddenProperties.add(JcrConstants.JCR_TITLE);
                hiddenProperties.add(JcrConstants.JCR_DESCRIPTION);
                hiddenProperties.add(Link.PN_LINK_URL);

                iconResource = new CoreResourceWrapper(iconResource, delegateResourceType, hiddenProperties, overriddenProperties);
            }
        }
        return iconResource;
    }

    @JsonIgnore
    public Resource getBrandImageResource() {
        if (brandImageResource != null && component != null) {
            String delegateResourceType = component.getProperties().get(PN_IMAGE_DELEGATE, String.class);
            if (StringUtils.isEmpty(delegateResourceType)) {
                log.error("In order for list rendering delegation to work correctly you need to set up the teaserDelegate property on" +
                        " the {} component; its value has to point to the resource type of a teaser component.", component.getPath());
            } else {
                ValueMap valueMap = brandImageResource.getValueMap();
                Map<String, Object> overriddenProperties = new HashMap<>();
                List<String> hiddenProperties = new ArrayList<>();
                String inheritedFileReference = valueMap.get(DownloadResource.PN_REFERENCE, String.class);
                overriddenProperties.put(DownloadResource.PN_REFERENCE, inheritedFileReference);
                overriddenProperties.put(Image.PN_EXTERNAL_IMAGE_RESOURCE_PATH, brandImageResource.getPath());
                // use the page featured image and inherit properties from the page item
                overriddenProperties.put(JcrConstants.JCR_TITLE, this.getTitle());

                hiddenProperties.add(JcrConstants.JCR_TITLE);
                hiddenProperties.add(JcrConstants.JCR_DESCRIPTION);
                hiddenProperties.add(Link.PN_LINK_URL);

                brandImageResource = new CoreResourceWrapper(iconResource, delegateResourceType, hiddenProperties, overriddenProperties);
            }
        }
        return brandImageResource;
    }


    public static @Nullable Resource getPageIcon(@NotNull Page page) {
        return page.getContentResource(NN_PAGE_ICON_IMAGE);
    }

    public static @Nullable Resource getPageBrandImage(@NotNull Page page) {
        return page.getContentResource(NN_PAGE_BRANDIMAGE_IMAGE);
    }

    @PostConstruct
    protected void init() {

        log.info("Teaser init");

        this.showPublishDate = this.resource.getValueMap().get(nz.co.manawabay.core.models.List.PN_SHOW_PUBLISH_DATE, Boolean.FALSE);
        this.publishDateFormatString = this.resource.getValueMap().get(nz.co.manawabay.core.models.List.PN_PUBLISHED_DATE_FORMAT_STRING, nz.co.manawabay.core.models.List.PUBLISHED_DATE_FORMAT_DEFAULT);
        this.publishDate = this.resource.getValueMap().get(PN_PAGE_PUBLISHDATE, Calendar.class);

        if (this.publishDate == null) {
            this.publishDate = this.currentPage.getContentResource().getValueMap().get(PN_PAGE_PUBLISHDATE, Calendar.class);
        }

        this.showIcon = this.resource.getValueMap().get(nz.co.manawabay.core.models.List.PN_SHOW_ICON, Boolean.FALSE);
        this.showBrandImage = this.resource.getValueMap().get(nz.co.manawabay.core.models.List.PN_SHOW_BRAND_IMAGE, Boolean.FALSE);
        this.hideTitle = this.resource.getValueMap().get(nz.co.manawabay.core.models.List.PN_HIDE_TITLE, Boolean.FALSE);
        this.showDescription = this.resource.getValueMap().get(nz.co.manawabay.core.models.List.PN_SHOW_DESCRIPTION, Boolean.FALSE);
        this.iconResource = this.resource.getValueMap().get(NN_PAGE_ICON_IMAGE, Resource.class);
        this.hideImage = this.resource.getValueMap().get(nz.co.manawabay.core.models.List.PN_HIDE_IMAGE, Boolean.FALSE);

        if (this.iconResource == null) {
            this.iconResource = this.currentPage.getContentResource(NN_PAGE_ICON_IMAGE);
        }

        this.brandImageResource = this.resource.getValueMap().get(NN_PAGE_BRANDIMAGE_IMAGE, Resource.class);

        if (this.brandImageResource == null) {
            this.brandImageResource = this.currentPage.getContentResource(NN_PAGE_BRANDIMAGE_IMAGE);
        }

        String videoFileReference = this.resource.getValueMap().get("videoFileReference", String.class);
        if (StringUtils.isNotBlank(videoFileReference)) {
            initVideoResource(videoFileReference);
        }
    }

    private void initVideoResource(String videoFileReference) {
        Resource downloadResource = resourceResolver.getResource(videoFileReference);

        if (downloadResource != null) {
            Asset downloadAsset = downloadResource.adaptTo(Asset.class);
            String filename = downloadAsset.getName();

            String format = downloadAsset.getMetadataValue(DamConstants.DC_FORMAT);
            String extension = mimeTypeService.getExtension(format);

            if (StringUtils.isEmpty(extension)) {
                extension = FilenameUtils.getExtension(filename);
            }

            videoUrl = getDownloadUrl(downloadResource, extension);
        }
    }

    private String getDownloadUrl(Resource resource, String extension) {
        return resource.getPath() + ".coredownload." + extension;
    }

    @Override
    public boolean isActionsEnabled() {
        return teaser.isActionsEnabled();
    }

    @Override
    public List<ListItem> getActions() {
        return teaser.getActions();
    }

    @Override
    public @Nullable Link getLink() {
        if (teaser != null) {
            return teaser.getLink();
        }
        return null;
    }

    @Override
    public Resource getImageResource() {
        return teaser.getImageResource();
    }

    @Override
    public boolean isImageLinkHidden() {
        return teaser.isImageLinkHidden();
    }

    @Override
    public String getPretitle() {
        return teaser.getPretitle();
    }

    @Override
    public String getTitle() {
        return !this.hideTitle ? teaser.getTitle() : null;
    }

    @Override
    public boolean isTitleLinkHidden() {
        return teaser.isTitleLinkHidden();
    }

    @Override
    public String getDescription() {
        return this.showDescription ? teaser.getDescription() : null;
    }

    @Override
    public String getTitleType() {
        return teaser.getTitleType();
    }
}
