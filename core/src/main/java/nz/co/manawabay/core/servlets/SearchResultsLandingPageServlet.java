package nz.co.manawabay.core.servlets;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import nz.co.manawabay.core.services.SearchResultsLandingPage;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.scripting.core.ScriptHelper;
import org.jetbrains.annotations.NotNull;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Component(
        service = Servlet.class,
        property = {
                "sling.servlet.selectors=" + SearchResultsLandingPageServlet.DEFAULT_SELECTOR,
                "sling.servlet.resourceTypes=cq/Page",
                "sling.servlet.extensions=json",
                "sling.servlet.methods=GET"
        }
)
public class SearchResultsLandingPageServlet extends SlingSafeMethodsServlet {
    protected static final String DEFAULT_SELECTOR = "searchresultslandingpage";

    private BundleContext bundleContext;

    @Reference
    private SearchResultsLandingPage searchResultsLandingPage;

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
                searchResultsLandingPage.doSearch(currentPage, request, response);
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }
}
