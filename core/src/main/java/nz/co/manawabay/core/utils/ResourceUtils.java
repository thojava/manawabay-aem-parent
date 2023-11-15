package nz.co.manawabay.core.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

import java.util.Objects;
import java.util.Optional;

import static com.day.cq.commons.DownloadResource.NN_FILE;
import static com.day.cq.commons.DownloadResource.PN_REFERENCE;

/**
 * Resource utils.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResourceUtils {

  /**
   * Get featured image src from resource.
   *
   * @param featuredImageResource resource.
   * @return featured image src.
   */
  public static Optional<String> getFeaturedImage(Resource featuredImageResource) {
    if (Objects.isNull(featuredImageResource)) {
      return Optional.empty();
    }
    String imageSrc = featuredImageResource.getValueMap().get(PN_REFERENCE, String.class);
    if (StringUtils.isNotBlank(imageSrc)) {
      return Optional.of(imageSrc);
    }
    Resource fileResource = featuredImageResource.getChild(NN_FILE);
    if (Objects.nonNull(fileResource)) {
      return Optional.of(fileResource.getPath());
    }
    return Optional.empty();
  }
}
