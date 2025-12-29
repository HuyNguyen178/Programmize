package filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter(urlPatterns = {"/upload/*", "/api/upload/*", "/add-course", "/edit-course", "/add-lesson", "/edit-lesson", "/edit-profile", "/add-account"})
public class FileUploadValidationFilter implements Filter {

    private static final long MAX_REQUEST_SIZE = 55 * 1024 * 1024; // 55MB

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialize if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Only check POST requests with multipart content
        if ("POST".equalsIgnoreCase(httpRequest.getMethod())) {
            String contentType = httpRequest.getContentType();

            if (contentType != null && contentType.toLowerCase().startsWith("multipart/form-data")) {

                // Check request size
                long contentLength = httpRequest.getContentLengthLong();
                if (contentLength > MAX_REQUEST_SIZE) {
                    httpResponse.setStatus(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE);
                    httpResponse.getWriter().write("File too large. Maximum 50MB allowed.");
                    return;
                }
            }
        }

        // Add security headers
        httpResponse.setHeader("X-Content-Type-Options", "nosniff");

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Cleanup if needed
    }
}