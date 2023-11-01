package nz.co.manawabay.core.services;

import com.day.cq.wcm.api.Page;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public interface SearchService {
    void doSearch(@NotNull Page currentPage, @NotNull final SlingHttpServletRequest request, @NotNull final SlingHttpServletResponse response) throws IOException;
}
