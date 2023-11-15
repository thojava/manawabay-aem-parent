package nz.co.manawabay.core.utils;

import com.day.cq.wcm.api.Page;

public class LinkUtils {

  public static final String HTML_EXTENSION = ".html";

  /**
   * Disable creating objects.
   */
  private LinkUtils() {

  }

  /**
   * Returns the URL for the page.
   *
   * @param page the page
   * @return the URL of the page
   */
  public static String getPageUrl(Page page) {
    return page.getPath() + HTML_EXTENSION;
  }

}
