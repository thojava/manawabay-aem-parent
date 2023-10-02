
package nz.co.manawabay.core.models;


import com.adobe.cq.wcm.core.components.commons.link.LinkManager;
import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;
import com.day.cq.search.Predicate;
import com.day.cq.search.SimpleSearch;
import com.day.cq.search.result.SearchResult;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.components.Component;
import com.day.cq.wcm.api.designer.Style;
import com.fasterxml.jackson.annotation.JsonProperty;
import nz.co.manawabay.core.internal.models.v1.PageListItemImpl;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
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
import javax.jcr.RepositoryException;
import java.io.Serializable;
import java.text.Collator;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static nz.co.manawabay.core.JsonUtils.getJsonString;
import static nz.co.manawabay.core.models.ActivationModule.CONFIG_ITEMS_PATH;
import static nz.co.manawabay.core.models.Teaser.NN_PAGE_BRANDIMAGE_IMAGE;
import static nz.co.manawabay.core.models.Teaser.NN_PAGE_ICON_IMAGE;

@Model(adaptables = SlingHttpServletRequest.class,
        adapters = com.adobe.cq.wcm.core.components.models.List.class,
        resourceType = List.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class List implements com.adobe.cq.wcm.core.components.models.List {
    public static final String RESOURCE_TYPE = "manawabay/components/list";

    private static final Logger LOGGER = LoggerFactory.getLogger(List.class);

    public static final String REQUEST_QUERY_SEARCH = "q";
    public static final String PN_LIST_FROM_SEARCH = "search";
    public static final String PN_ORDER_BY_TITLE = "title";
    public static final String PN_ORDER_BY_MODIFIED = "modified";
    public static final String PN_SORT_ORDER_ASC = "asc";
    public static final String PN_SORT_ORDER_DESC = "desc";

    public static final String PN_MAX_ITEMS = "maxItems";
    public static final int MAX_ITEMS_DEFAULT = 0;

    public static final String PN_SEARCH_QUERY = "query";
    private static final String PN_SEARCH_LIMIT = "limit";
    private static final int SEARCH_LIMIT_DEFAULT = 100;

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

    /**
     * The current request
     */
    @Self
    private SlingHttpServletRequest slingRequest;
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
    @Named(com.adobe.cq.wcm.core.components.models.List.PN_SOURCE)
    @Default(values = com.adobe.cq.wcm.core.components.models.List.PN_PAGES)
    protected String listFrom;
    @ValueMapValue
    @Named(com.adobe.cq.wcm.core.components.models.List.PN_ORDER_BY)
    @Default(values = StringUtils.EMPTY)
    protected String orderBy;
    @ValueMapValue
    @Named(com.adobe.cq.wcm.core.components.models.List.PN_SORT_ORDER)
    @Default(values = PN_SORT_ORDER_ASC)
    protected String sortOrder;

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
        if (list != null) {
            if (listFrom.equals(PN_LIST_FROM_SEARCH)) {
                return getPages(listFrom);
            }
            return list.getItems();
        }
        return Collections.EMPTY_LIST;
    }
    protected java.util.List<Page> getPages(String type) {
        Stream<Page> itemStream;
        if (type.equals(PN_LIST_FROM_SEARCH)) {
            itemStream = getSearchListItems();
        } else {
            return new ArrayList<>(list.getItems());
        }

        if (orderBy != null && sortOrder != null && currentPage != null) {
            itemStream = itemStream.sorted(new ListSort(orderBy, sortOrder, this.currentPage.getLanguage()));
        }

        if (properties != null) {
            // get the max number of items to display (0 means no limit
            int maxItems = properties.get(PN_MAX_ITEMS, MAX_ITEMS_DEFAULT);
            // limit the results
            if (maxItems != 0) {
                itemStream = itemStream.limit(maxItems);
            }
        }

        return itemStream.collect(Collectors.toList());
    }
    protected Stream<Page> getSearchListItems() {
        Optional<Page> searchRoot = getRootPage(PN_SEARCH_IN);
        // get default query
        String query = properties.get(PN_SEARCH_QUERY, String.class);
        // get query from request
        if (slingRequest != null) {
            String searchString = slingRequest.getParameter(REQUEST_QUERY_SEARCH);
            if (StringUtils.isNotBlank(searchString)) {
                query = searchString;
            }
        }

        if (!StringUtils.isBlank(query) && searchRoot.isPresent()) {
            SimpleSearch search = resource.adaptTo(SimpleSearch.class);
            if (search != null) {
                search.setQuery(query);
                search.setSearchIn(searchRoot.get().getPath());
                search.addPredicate(new Predicate("type", "type").set("type", NameConstants.NT_PAGE));
                int limit = properties.get(PN_SEARCH_LIMIT, SEARCH_LIMIT_DEFAULT);
                search.setHitsPerPage(limit);
                return safeGetSearchResult(search)
                        .map(SearchResult::getResources)
                        .map(it -> (Iterable<Resource>) () -> it)
                        .map(it -> StreamSupport.stream(it.spliterator(), false))
                        .orElseGet(Stream::empty)
                        .filter(Objects::nonNull)
                        .map(currentPage.getPageManager()::getContainingPage)
                        .filter(Objects::nonNull);
            }
        }
        return Stream.empty();
    }
    protected Optional<Page> getRootPage(@NotNull final String fieldName) {
        Optional<String> parentPath = Optional.ofNullable(this.properties.get(fieldName, String.class))
                .filter(StringUtils::isNotBlank);

        // no path is specified, use current page
        if (!parentPath.isPresent()) {
            return Optional.of(this.currentPage);
        }

        // a path is specified, get that page or return null
        return parentPath
                .map(resource.getResourceResolver()::getResource)
                .map(currentPage.getPageManager()::getContainingPage);
    }
    @NotNull
    protected Optional<SearchResult> safeGetSearchResult(@NotNull final SimpleSearch search) {
        try {
            return Optional.of(search.getResult());
        } catch (RepositoryException e) {
            LOGGER.error("Unable to retrieve search results for query.", e);
        }
        return Optional.empty();
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
                                Tag tag = tagManager.resolve(tagid);
                                if (tag != null) {
                                    listItems.add(newListItemTag(tag, getId(), component, this));
                                }
                            }
                        } else {
                            // get first tag in list of tags, list all childen of that tag and return as list
                            if (listTags.length > 0) {
                                String tagid = listTags[0];
                                Tag tag = tagManager.resolve(tagid);
                                if (tag != null) {
                                    for (Iterator<Tag> it = tag.listChildren(); it.hasNext(); ) {
                                        Tag child = it.next();
                                        listItems.add(newListItemTag(child, getId(), component, this));
                                    }
                                }
                            }
                        }
                    } else {
                        LOGGER.error("Could not get TagManager.");
                    }
                } else {
                    if (listFrom.equals(PN_LIST_FROM_SEARCH)) {
                        this.listItems = getItems().stream()
                                .filter(Objects::nonNull)
                                .map(page -> newListItem(linkManager, page, getId(), component, this))
                                .collect(Collectors.toList());
                    } else {
                        this.listItems = list.getItems().stream()
                                .filter(Objects::nonNull)
                                .map(page -> newListItem(linkManager, page, getId(), component, this))
                                .collect(Collectors.toList());
                    }
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

    /**
     * Comparator for sorting pages.
     */
    public static class ListSort implements Comparator<Page>, Serializable {

        /**
         * Serial version UID.
         */
        private static final long serialVersionUID = -707429230313589969L;

        /**
         * The sort order
         */
        @Nullable
        private final String sortOrder;

        /**
         * Comparator for comparing pages.
         */
        @NotNull
        private final transient Comparator<Page> pageComparator;

        /**
         * Construct a page sorting comparator.
         *
         * @param orderBy The field to order by.
         * @param sortOrder The sort order.
         * @param locale Current locale.
         */
        ListSort(@Nullable final String orderBy, @Nullable final String sortOrder, @NotNull Locale locale) {
            this.sortOrder = sortOrder;

            if (PN_ORDER_BY_MODIFIED.equals(orderBy)) {
                // getLastModified may return null, define null to be after nonnull values
                this.pageComparator = (a, b) -> ObjectUtils.compare(a.getLastModified(), b.getLastModified(), true);
            } else if (PN_ORDER_BY_TITLE.equals(orderBy)) {
                Collator collator = Collator.getInstance(locale);
                collator.setStrength(Collator.PRIMARY);
                // getTitle may return null, define null to be greater than nonnull values
                Comparator<String> titleComparator = Comparator.nullsLast(collator);
                this.pageComparator = (a, b) -> titleComparator.compare(PageListItemImpl.getTitle(a), PageListItemImpl.getTitle(b));
            } else {
                this.pageComparator = (a, b) -> 0;
            }
        }

        @Override
        public int compare(@NotNull final Page item1, @NotNull final Page item2) {
            int i = this.pageComparator.compare(item1, item2);
            if (sortOrder.equals(PN_SORT_ORDER_DESC)) {
                i = i * -1;
            }
            return i;
        }
    }
}
