package nz.co.manawabay.core.models;

import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.commons.link.LinkManager;
import com.adobe.cq.wcm.core.components.models.Image;
import com.adobe.cq.wcm.core.components.models.List;
import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;
import com.adobe.cq.wcm.core.components.models.datalayer.builder.DataLayerBuilder;
import com.adobe.cq.wcm.core.components.util.ComponentUtils;
import com.day.cq.commons.DownloadResource;
import com.day.cq.commons.ImageResource;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.components.Component;
import com.fasterxml.jackson.annotation.JsonIgnore;
import nz.co.manawabay.core.internal.resource.CoreResourceWrapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.adobe.cq.wcm.core.components.models.Image.PN_DISPLAY_POPUP_TITLE;
import static com.adobe.cq.wcm.core.components.models.List.PN_TEASER_DELEGATE;
import static nz.co.manawabay.core.models.List.PN_TYPE_TAGS;
import static nz.co.manawabay.core.models.Teaser.*;

@Model(adaptables = {SlingHttpServletRequest.class, Resource.class} , defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ListItem implements com.adobe.cq.wcm.core.components.models.ListItem {

    public static final String ATTR_TARGET = "target";
    private static final Logger LOGGER = LoggerFactory.getLogger(ListItem.class);
    private com.adobe.cq.wcm.core.components.models.ListItem listItem;
    private Resource iconResource;
    private Resource brandImageResource;
    private Resource teaserResource;

    private Teaser teaserExtended;

    protected Page page;
    protected Link<Page> link;
    protected String parentId;
    protected String path;
    protected Resource resource;
    protected Component component;
    protected nz.co.manawabay.core.models.List list;
    private Resource listResource;
    protected String dataLayerType;
    protected boolean showDescription;
    protected boolean hideTitle;
    protected boolean hideDescription;
    protected boolean hideImage;
    protected Calendar publishDate;
    protected Tag[] pageTags;
    protected String publishDateFormatString;
    protected String title;
    protected String value;
    protected boolean showPublishDate;
    protected boolean linkItems;
    protected ValueMap properties;

    protected Tag tag;
    /**
     * List of properties that should be inherited when delegating to the featured image of the page.
     */
    private final Map<String, Object> overriddenProperties = new HashMap<>();
    private final java.util.List<String> hiddenProperties = new ArrayList<>();
    private String linkText;
    private static final String ITEM_ID_PREFIX = "item";

    public ListItem(@NotNull final LinkManager linkManager,
                    @NotNull final Page page,
                    final String parentId,
                    final Component component,
                    final nz.co.manawabay.core.models.List list) {
        this(linkManager.get(page).build(), page, parentId, component, list);
    }

    public ListItem(@NotNull final Tag tag,
                    final String parentId,
                    final Component component,
                    final nz.co.manawabay.core.models.List list) {
        this.component = component;
        this.list = list;
        this.listResource = list.resource;
        this.tag = tag;
        this.title = tag.getTitle();
        this.value = tag.getTagID();
        this.parentId = parentId;
        this.path = tag.getPath();
        this.resource = tag.adaptTo(Resource.class);
        if (component != null) {
            this.dataLayerType = component.getResourceType() + "/tag";
        }
    }

    public ListItem(@NotNull final Link link,
                    @NotNull final Page page,
                    final String parentId,
                    final Component component,
                    final nz.co.manawabay.core.models.List list) {
        this.component = component;
        this.list = list;
        this.listResource = list.resource;
        this.resource = page.getContentResource();
        if (resource != null) {
            this.path = resource.getPath();
        }
        if (component != null) {
            this.dataLayerType = component.getResourceType() + "/" + ITEM_ID_PREFIX;
        }
        this.parentId = parentId;
        this.link = link;
        if (this.link.isValid() && (link.getReference() instanceof Page)) {
            this.page = (Page) link.getReference();
        } else {
            this.page = page;
        }
        if(resource != null) {
            properties = resource.getValueMap();
            linkText = properties.get(List.PN_LINK_TEXT, String.class);
        }

        showDescription = list.showDescription();
        showPublishDate = list.showPublishDate();
        publishDateFormatString = list.getPublishDateFormatString();
        hideTitle = list.getHideTitle();
        hideDescription = list.getHideDescription();
        hideImage = list.getHideImage();
        linkItems = list.linkItems() || list.displayItemAsTeaser();

        title = StringUtils.isNotBlank(linkText) ? linkText : ListItem.getTitle(page);

        if (this.page != null) {
            this.pageTags = page.getTags();
        }


    }


    @Override
    public String getURL() {
        return link.getURL();
    }


    @Override
    @JsonIgnore
    @Nullable
    public Link<Page> getLink() {
        return link;
    }

    @Override
    public String getTitle() {
        return ! this.hideTitle ? title : null;
    }

    /**
     * Gets the title of a page list item from a given page.
     * The list item title is derived from the page by selecting the first non-blank value from the
     * following:
     * <ul>
     *     <li>{@link Page#getNavigationTitle()}</li>
     *     <li>{@link Page#getPageTitle()}</li>
     *     <li>{@link Page#getTitle()}</li>
     *     <li>{@link Page#getName()}</li>
     * </ul>
     *
     * @param page The page for which to get the title.
     * @return The list item title.
     */
    public static String getTitle(@NotNull final Page page) {
        return Stream.<Supplier<String>>of(page::getNavigationTitle, page::getPageTitle, page::getTitle)
                .map(Supplier::get)
                .filter(StringUtils::isNotBlank)
                .findFirst()
                .orElseGet(page::getName);
    }

    @Override
    public String getPath() {
        return page.getPath();
    }

    @Override
    public String getName() {
        return page.getName();
    }

    public String getValue() {
        return value;
    }

    public Calendar getPublishDate() {
        if (publishDate == null) {
            publishDate = page.getProperties().get(PN_PAGE_PUBLISHDATE, Calendar.class);
        }
        return publishDate;
    }
    @Override
    public String getDescription() {
        return ! this.hideDescription ? page.getDescription() : null;
    }

    @Override
    public Calendar getLastModified() {
        return page.getLastModified();
    }

    public Resource getIconResource() {
        if (iconResource == null && component != null) {
            String delegateResourceType = component.getProperties().get(PN_IMAGE_DELEGATE, String.class);
            if (StringUtils.isEmpty(delegateResourceType)) {
                LOGGER.error("In order for image rendering delegation to work correctly you need to set up the imageDelegate property on" +
                        " the {} component; its value has to point to the resource type of an image component.", component.getPath());
            } else {
                iconResource = getPageIcon(page);
                Map<String, Object> overriddenProperties = new HashMap<>();
                overriddenProperties.put(PN_DISPLAY_POPUP_TITLE, false);
                overriddenProperties.put(PN_IMAGE_LINK_HIDDEN, true);
                java.util.List<String> hiddenProperties = new ArrayList<>();
                hiddenProperties.add(JcrConstants.JCR_TITLE);
                hiddenProperties.add(JcrConstants.JCR_DESCRIPTION);
                iconResource = new CoreResourceWrapper(iconResource, delegateResourceType, hiddenProperties, overriddenProperties);
            }
        }
        return iconResource;
    }

    public Resource getBrandImageResource() {
        if (brandImageResource == null && component != null) {
            String delegateResourceType = component.getProperties().get(PN_IMAGE_DELEGATE, String.class);
            if (StringUtils.isEmpty(delegateResourceType)) {
                LOGGER.error("In order for image rendering delegation to work correctly you need to set up the imageDelegate property on" +
                        " the {} component; its value has to point to the resource type of an image component.", component.getPath());
            } else {
                brandImageResource = getPageBrandImage(page);
                Map<String, Object> overriddenProperties = new HashMap<>();
                overriddenProperties.put(PN_DISPLAY_POPUP_TITLE, false);
                overriddenProperties.put(PN_IMAGE_LINK_HIDDEN, true);
                java.util.List<String> hiddenProperties = new ArrayList<>();
                hiddenProperties.add(JcrConstants.JCR_TITLE);
                hiddenProperties.add(JcrConstants.JCR_DESCRIPTION);
                brandImageResource = new CoreResourceWrapper(brandImageResource, delegateResourceType, hiddenProperties, overriddenProperties);
            }
        }
        return brandImageResource;
    }

    @Override
    @JsonIgnore
    public Resource getTeaserResource() {
        if (teaserResource == null && component != null) {
            String delegateResourceType = component.getProperties().get(PN_TEASER_DELEGATE, String.class);
            if (StringUtils.isEmpty(delegateResourceType)) {
                LOGGER.error("In order for list rendering delegation to work correctly you need to set up the teaserDelegate property on" +
                        " the {} component; its value has to point to the resource type of a teaser component.", component.getPath());
            } else {
                Resource resourceToBeWrapped = ComponentUtils.getFeaturedImage(page);
                if (resourceToBeWrapped != null) {
                    ValueMap valueMap = resourceToBeWrapped.getValueMap();
                    String inheritedFileReference = valueMap.get(DownloadResource.PN_REFERENCE, String.class);
                    overriddenProperties.put(DownloadResource.PN_REFERENCE, inheritedFileReference);
                    overriddenProperties.put(Image.PN_EXTERNAL_IMAGE_RESOURCE_PATH, resourceToBeWrapped.getPath());
                    // use the page featured image and inherit properties from the page item
                    overriddenProperties.put(JcrConstants.JCR_TITLE, this.getTitle());
                    if (showDescription) {
                        overriddenProperties.put(JcrConstants.JCR_DESCRIPTION, this.getDescription());
                    }
                } else {
                    // use the page content node and inherit properties from the page item
                    resourceToBeWrapped = page.getContentResource();
                    if (resourceToBeWrapped == null) {
                        return null;
                    }
                    if (!showDescription) {
                        hiddenProperties.add(JcrConstants.JCR_DESCRIPTION);
                    }
//                    if (!showTitle) {
//                        hiddenProperties.add(JcrConstants.JCR_TITLE);
//                    }
                }
                if (linkItems) {
                    overriddenProperties.put(ImageResource.PN_LINK_URL, this.getPath());
                    Link<Page> itemLink = this.getLink();
                    if (itemLink != null) {
                        String target = itemLink.getHtmlAttributes().get(ATTR_TARGET);
                        if (StringUtils.isNotBlank(target)) {
                            overriddenProperties.put(Link.PN_LINK_TARGET, target);
                        }
                    }
                }

                //add all the props into teaser
                overriddenProperties.put(nz.co.manawabay.core.models.List.PN_PUBLISHED_DATE_FORMAT_STRING, publishDateFormatString);
                overriddenProperties.put(nz.co.manawabay.core.models.List.PN_SHOW_PUBLISH_DATE, showPublishDate);
                overriddenProperties.put(nz.co.manawabay.core.models.List.PN_SHOW_DESCRIPTION, showDescription);
                overriddenProperties.put(nz.co.manawabay.core.models.List.PN_HIDE_TITLE, hideTitle);
                overriddenProperties.put(nz.co.manawabay.core.models.List.PN_HIDE_IMAGE, hideImage);
                overriddenProperties.put(PN_PAGE_PUBLISHDATE, getPublishDate());

                overriddenProperties.put(NN_PAGE_ICON_IMAGE, getIconResource());
                overriddenProperties.put(NN_PAGE_BRANDIMAGE_IMAGE, getBrandImageResource());

                teaserResource = new CoreResourceWrapper(listResource, delegateResourceType, hiddenProperties, overriddenProperties);
            }
        }
        return teaserResource;
    }


    public String getId() {
        return ComponentUtils.generateId(StringUtils.join(parentId, ComponentUtils.ID_SEPARATOR, ITEM_ID_PREFIX), path);
    }

    @NotNull
    public ComponentData getData() {
        if (list.type.equals(PN_TYPE_TAGS)) {
            return DataLayerBuilder.forComponent()
                    .withId(this::getId)
                    .withType(() -> Optional.ofNullable(this.dataLayerType).orElseGet(() -> this.resource.getResourceType()))
                    .withTitle(this::getTitle)
                    .build();
        } else {
            return DataLayerBuilder.forComponent()
                    .withId(this::getId)
                    .withLastModifiedDate(() ->
                            // Note: this can be simplified in JDK 11
                            Optional.ofNullable(resource.getValueMap().get(JcrConstants.JCR_LASTMODIFIED, Calendar.class))
                                    .map(Calendar::getTime)
                                    .orElseGet(() ->
                                            Optional.ofNullable(resource.getValueMap().get(JcrConstants.JCR_CREATED, Calendar.class))
                                                    .map(Calendar::getTime)
                                                    .orElse(null)))
                    .withType(() -> Optional.ofNullable(this.dataLayerType).orElseGet(() -> this.resource.getResourceType()))
                    .withTitle(this::getTitle)
                    .withLinkUrl(() -> Optional.ofNullable(this.getLink()).map(Link::getMappedURL).orElse(null))
                    .build();
        }
    }

    public Tag[] getPageTags() {
        return pageTags;
    }
    public String[] getPageTagsIds() {
        //return tag id from pageTags as a string array
        return Arrays.stream(pageTags).map(Tag::getTagID).toArray(String[]::new);
    }
}

