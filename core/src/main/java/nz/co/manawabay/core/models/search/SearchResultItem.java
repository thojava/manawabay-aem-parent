package nz.co.manawabay.core.models.search;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import nz.co.manawabay.core.utils.LinkUtils;
import nz.co.manawabay.core.utils.PageUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;


/**
 * Search Servlet Response bean for page.
 */
@Model(adaptables = Resource.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SearchResultItem {
    private static final String PUBLISH_DATE_FORMAT = "MMM yyyy";
    private static final String PUBLISH_DATE_FIELD = "publishDate";

    @Getter
    @ValueMapValue(name = JcrConstants.JCR_CONTENT + "/" + JcrConstants.JCR_TITLE)
    private String title;

    @Getter
    @Setter
    private String description;

    @Getter
    private String uri;

    @Getter
    private String featuredImage;

    @Getter
    private String brandImage;

    @Getter
    private String publishedDate;

    @Getter
    @Setter
    @JsonIgnore
    private boolean isArticlePage;

    @Self
    private Resource resource;

    @SlingObject
    private ResourceResolver resourceResolver;

    @PostConstruct
    void init() {
        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        assert pageManager != null;
        Page resourcePage = pageManager.getContainingPage(resource);
        this.uri = resourceResolver.map(LinkUtils.getPageUrl(resourcePage));
        this.publishedDate = new SimpleDateFormat(PUBLISH_DATE_FORMAT).format(getPagePublishedDate(resourcePage).getTime());
        this.featuredImage = PageUtils.getFeaturedImage(resourcePage).orElse(StringUtils.EMPTY);
        this.brandImage = PageUtils.getBrandImage(resourcePage).orElse(StringUtils.EMPTY);
    }

    private Calendar getPagePublishedDate(Page page) {
        Resource contentResource = page.getContentResource();
        Calendar date = Objects.requireNonNull(contentResource).getValueMap().get(PUBLISH_DATE_FIELD, Calendar.class);
        return date != null ? date : page.getLastModified();
    }
}
