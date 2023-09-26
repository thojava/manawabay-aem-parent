
package nz.co.manawabay.core.models;


import com.adobe.cq.wcm.core.components.commons.link.LinkManager;
import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.components.Component;
import com.day.cq.wcm.api.designer.Style;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.annotations.via.ResourceSuperType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import java.util.*;
import java.util.stream.Collectors;

import static nz.co.manawabay.core.JsonUtils.getJsonString;
import static nz.co.manawabay.core.models.ActivationModule.CONFIG_ITEMS_PATH;
import static nz.co.manawabay.core.models.Teaser.NN_PAGE_BRANDIMAGE_IMAGE;
import static nz.co.manawabay.core.models.Teaser.NN_PAGE_ICON_IMAGE;

@Model(adaptables = SlingHttpServletRequest.class,
        adapters = com.adobe.cq.wcm.core.components.models.List.class,
        resourceType = "manawabay/components/list",
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class List implements com.adobe.cq.wcm.core.components.models.List {

    private static final Logger LOGGER = LoggerFactory.getLogger(List.class);

    /**
     * Type of content to use when list is built. Possible values are:
     *
     * <ul>
     *     <li><code>pages</code> - the list will be built from page</li>
     *     <li><code>tags</code> - the list will be built from tags</li>
     * </ul>
     *
     * @since com.adobe.cq.wcm.core.components.models 11.0.0
     */
    public static final String PN_TYPE = "listType";
    public static final String PN_TYPE_PAGES = "pages";
    public static final String PN_TYPE_TAGS = "tags";
    public static final String PN_TYPE_DEFAULT = PN_TYPE_PAGES;

    /**
     * Name of the resource property indicating how the list will be built. Possible values are:
     *
     * <ul>
     *     <li><code>children</code> - the list will be built from the child pages of the page identified by {@link #PN_PARENT_PAGE}</li>
     *     <li><code>static</code> - the list will be built from a custom set of pages and external links, stored under the {@link #NN_STATIC} node</li>
     *     which are tagged with the tags stored by the {@link #PN_TAGS} property</li>
     * </ul>
     *
     * @since com.adobe.cq.wcm.core.components.models 11.0.0
     */
    public static final String PN_SOURCE_TAGS = "listFromTags";
    public static final String PN_SOURCE_TAGS_CHILDREN = "children";
    public static final String PN_SOURCE_TAGS_FIXED = "static";
    public static final String PN_SOURCE_TAGS_DEFAULT = PN_SOURCE_TAGS_CHILDREN;

    public static final String PN_TAG = "tags";

    /**
     * Default format for the published date.
     */
    public static final String PUBLISHED_DATE_FORMAT_DEFAULT = "yyyy-MM-dd";
    /**
     * Name of the resource property storing the format for the published date.
     */
    public static final String PN_PUBLISHED_DATE_FORMAT = "publishDateFormat";
    public static final String PN_PUBLISHED_DATE_FORMAT_STRING = "publishDateFormatString";
    public static final String PN_SHOW_PUBLISH_DATE = "showPublishDate";
    public static final String PN_SHOW_ICON = "showIcon";
    public static final String PN_SHOW_BRAND_IMAGE = "showBrandImage";
    public static final String PN_HIDE_TITLE = "hideTitle";
    public static final String PN_HIDE_DESCRIPTION = "hideDescription";
    public static final String PN_HIDE_IMAGE = "hideImage";
    @ScriptVariable
    protected ValueMap pageProperties;
    /**
     * Component properties.
     */
    @ScriptVariable
    protected ValueMap properties;

    /**
     * The current style.
     */
    @ScriptVariable
    protected Style currentStyle;

    @ScriptVariable
    protected Page currentPage;

    @ScriptVariable
    protected Resource resource;

    @ScriptVariable
    protected Component component;

    @Self
    @Via(type = ResourceSuperType.class)
    private com.adobe.cq.wcm.core.components.models.List list;

    @Self
    protected LinkManager linkManager;

    @ValueMapValue
    @Named(PN_TYPE)
    @Default(values = PN_TYPE_DEFAULT)
    protected String type;

    @ValueMapValue
    @Named(PN_SOURCE_TAGS)
    @Default(values = PN_SOURCE_TAGS_DEFAULT)
    protected String listFromTags;

    @ValueMapValue
    @Named(PN_TAGS)
    protected String[] listTags;

    @ValueMapValue
    protected boolean showIcon;
    @ValueMapValue
    protected boolean showPublishDate;
    @ValueMapValue
    protected boolean showBrandImage;

    @ValueMapValue
    protected boolean hideTitle;
    @ValueMapValue
    protected boolean hideDescription;

    @ValueMapValue
    protected boolean hideImage;

    private Resource iconResource;
    private Resource brandImageResource;

    private String publishDateFormatString;

    @ChildResource
    @Named(CONFIG_ITEMS_PATH)
    @Nullable
    private java.util.List<ActivationModule> activationconfig;

    protected Collection<com.adobe.cq.wcm.core.components.models.ListItem> listItems;

    @Override
    public Collection<Page> getItems() {
        return list.getItems();
    }

    @Override
    @NotNull
    public Collection<com.adobe.cq.wcm.core.components.models.ListItem> getListItems() {
        if (listItems == null) {

            if (list == null) {
                LOGGER.warn("Could not locate the AEM WCM Core Components List SlingModel via this component's ResourceSuperType. Returning an empty list.");
                listItems = Collections.EMPTY_LIST;
            } else {
                 if (type.equals(PN_TYPE_TAGS)) {
                    listItems = new ArrayList<>();
                    TagManager tagManager = resource.getResourceResolver().adaptTo(TagManager.class);
                    if (tagManager != null) {
                        if (listFromTags.equals(PN_SOURCE_TAGS_FIXED)) {
                            for (String tagid : listTags) {
                                try {
                                    Tag tag = tagManager.resolve(tagid);
                                    if (tag != null) {
                                        listItems.add(newListItemTag(tag, getId(), component, this));
                                    }
                                } catch (Exception e) {
                                    LOGGER.error("Error resolving tag: " + tagid, e);
                                }
                            }
                        } else {
                            // get first tag in list of tags, list all childen of that tag and return as list
                            if (listTags.length > 0) {
                                String tagid = listTags[0];
                                try {
                                    Tag tag = tagManager.resolve(tagid);
                                    if (tag != null) {
                                        for (Iterator<Tag> it = tag.listChildren(); it.hasNext(); ) {
                                            Tag child = it.next();
                                            listItems.add(newListItemTag(child, getId(), component, this));
                                        }
                                    }
                                } catch (Exception e) {
                                    LOGGER.error("Error resolving tag: " + tagid, e);
                                }
                            }
                        }
                    } else {
                        LOGGER.error("Could not get TagManager.");
                    }
                } else {
                    this.listItems = list.getItems().stream()
                            .filter(Objects::nonNull)
                            .map(page -> newListItem(linkManager, page, getId(), component, this))
                            .collect(Collectors.toList());
                }
            }


        }
        return listItems;
    }

    protected com.adobe.cq.wcm.core.components.models.ListItem newListItemTag(@NotNull Tag tag, String parentId, Component component, List list) {
        return new ListItem(tag, parentId, component, this);
    }
    protected com.adobe.cq.wcm.core.components.models.ListItem newListItem(@NotNull LinkManager linkManager, @NotNull Page page, String parentId, Component component, List list) {
        return new ListItem(linkManager, page, parentId, component, this);
    }

    @PostConstruct
    protected void init(){

        LOGGER.info("List init");

        publishDateFormatString = properties.get(PN_PUBLISHED_DATE_FORMAT, currentStyle.get(PN_PUBLISHED_DATE_FORMAT, PUBLISHED_DATE_FORMAT_DEFAULT));
        showPublishDate = properties.get(PN_SHOW_PUBLISH_DATE, currentStyle.get(PN_SHOW_PUBLISH_DATE, false));
        showIcon = properties.get(PN_SHOW_ICON, currentStyle.get(PN_SHOW_ICON, false));
        showBrandImage = properties.get(PN_SHOW_BRAND_IMAGE, currentStyle.get(PN_SHOW_BRAND_IMAGE, false));
        hideTitle = properties.get(PN_HIDE_TITLE, currentStyle.get(PN_HIDE_TITLE, false));
        hideDescription = properties.get(PN_HIDE_DESCRIPTION, currentStyle.get(PN_HIDE_DESCRIPTION, false));
        hideImage = properties.get(PN_HIDE_IMAGE, currentStyle.get(PN_HIDE_IMAGE, false));
    }

    @Override
    public boolean displayItemAsTeaser() {
        return list.displayItemAsTeaser();
    }

    @Override
    public boolean showDescription() {
        return list.showDescription();
    }

    public boolean getHideTitle() {
        return hideTitle;
    }
    public boolean getHideDescription() { return hideDescription; }

    public boolean getHideImage() { return hideImage; }
    @Override
    public boolean linkItems() { return list.linkItems(); }

    @Override
    public boolean showModificationDate() {
        return list.showModificationDate();
    }

    @Override
    public String getDateFormatString() {
        return list.getDateFormatString();
    }

    @JsonProperty("showIcon")
    public boolean showIcon() {
        return showIcon;
    }
    @JsonProperty("showPublishDate")
    public boolean showPublishDate() {
        return showPublishDate;
    }
    @JsonProperty("showBrandImage")
    public boolean showBrandImage() {
        return showBrandImage;
    }


    public Resource getIconImage(@NotNull Page page) {
        if (this.iconResource == null) {
            this.iconResource = page.getContentResource(NN_PAGE_ICON_IMAGE);
        }
        return iconResource;
    }

    public Resource getBrandImage(@NotNull Page page) {
        if (this.brandImageResource == null) {
            this.brandImageResource = page.getContentResource(NN_PAGE_BRANDIMAGE_IMAGE);
        }
        return brandImageResource;
    }

    @NotNull
    public String getActivationData() {
        if (activationconfig == null) {
            return "";
        }
        return getJsonString(activationconfig);
    }

    public String getPublishDateFormatString() {
        return publishDateFormatString;
    }

    public ComponentData getData() {
        return list.getData();
    }

    public String getId() {
        return list.getId();
    }

    @JsonProperty("appliedCssClassNames")
    public String getAppliedCssClasses() {
        return list.getAppliedCssClasses();
    }
    public String getExportedType() {
        return list.getExportedType();
    }

    public String getType() {
        return type;
    }

    public String getListFromTags() {
        return listFromTags;
    }

    public String[] getListTags() {
        return listTags;
    }

    public boolean getDisplayAsPages() {
        return type.equals(PN_TYPE_PAGES);
    }

    public boolean getDisplayAsTags() {
        return type.equals(PN_TYPE_TAGS);
    }
}
