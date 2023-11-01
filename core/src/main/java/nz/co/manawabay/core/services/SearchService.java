package nz.co.manawabay.core.services;

import com.adobe.cq.wcm.core.components.models.ListItem;
import com.day.cq.wcm.api.Page;
import org.apache.sling.api.SlingHttpServletRequest;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface SearchService {
    List<ListItem> doSearch(@NotNull Page currentPage, @NotNull final SlingHttpServletRequest request);
}
