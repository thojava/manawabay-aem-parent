package nz.co.manawabay.core.services;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;

import javax.servlet.ServletException;
import java.io.IOException;

public interface FormToCRMService {
    void postToCRM(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException;
}