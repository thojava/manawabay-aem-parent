package nz.co.manawabay.core.utils;

import com.day.cq.wcm.api.Page;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.sling.api.resource.Resource;

import java.util.Optional;

import static nz.co.manawabay.core.models.Teaser.NN_PAGE_BRANDIMAGE_IMAGE;
import static com.adobe.cq.wcm.core.components.models.Page.NN_PAGE_FEATURED_IMAGE;

/**
 * The Class CommonUtils.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PageUtils {

  /**
   * Get brand image src from page.
   *
   * @param page page.
   * @return brand image src.
   */
  public static Optional<String> getBrandImage(Page page) {
    Resource featuredImageResource = page.getContentResource(NN_PAGE_BRANDIMAGE_IMAGE);
    return ResourceUtils.getFeaturedImage(featuredImageResource);
  }

  /**
   * Get featured image src from page.
   *
   * @param page page.
   * @return featured image src.
   */
  public static Optional<String> getFeaturedImage(Page page) {
    Resource featuredImageResource = page.getContentResource(NN_PAGE_FEATURED_IMAGE);
    return ResourceUtils.getFeaturedImage(featuredImageResource);
  }

}
