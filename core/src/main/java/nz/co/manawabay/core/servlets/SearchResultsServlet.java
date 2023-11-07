package nz.co.manawabay.core.servlets;

import com.adobe.cq.wcm.core.components.models.ListItem;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import nz.co.manawabay.core.services.SearchService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.scripting.core.ScriptHelper;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.jetbrains.annotations.NotNull;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Component(service = Servlet.class)
@SlingServletResourceTypes(
        methods = HttpConstants.METHOD_GET,
        resourceTypes = "cq/Page",
        selectors = SearchResultsServlet.DEFAULT_SELECTOR,
        extensions = "json"
)
public class SearchResultsServlet extends SlingSafeMethodsServlet {
    protected static final String DEFAULT_SELECTOR = "searchresultslandingpage";

    private BundleContext bundleContext;

    @Reference
    private SearchService searchService;

    @Activate
    protected void activate(final BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    @Override
    protected void doGet(@NotNull final SlingHttpServletRequest request, @NotNull final SlingHttpServletResponse response)
            throws IOException {
        Page currentPage = Optional.ofNullable(request.getResourceResolver().adaptTo(PageManager.class))
                .map(pm -> pm.getContainingPage(request.getResource()))
                .orElse(null);

        if (currentPage == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } else {
            SlingBindings bindings = new SlingBindings();
            bindings.setSling(new ScriptHelper(bundleContext, null, request, response));
            request.setAttribute(SlingBindings.class.getName(), bindings);

            try {
                List<ListItem> results = searchService.doSearch(currentPage, request);
                response.setContentType("application/json");
                response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                new ObjectMapper().writeValue(response.getWriter(), results);
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }
}
