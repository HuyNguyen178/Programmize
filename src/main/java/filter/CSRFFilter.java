package filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import utils.CSRFUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@WebFilter(urlPatterns = "/*", filterName = "CSRFFilter")
public class CSRFFilter implements Filter {

    private static final List<String> CSRF_EXEMPT_URLS = Arrays.asList(
            "/login",
            "/register",
            "/logout",
            "/verify-email",
            "/check-otp",
            "/forgot-password",
            "/reset-password",
            "/new-password",
            "/course-enrollment",
            "/vnpay-return",
            "/vnpay-payment-return"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String method = req.getMethod();
        String path = req.getServletPath();

        boolean isStateChanging = method.equals("POST") || method.equals("PUT") ||
                method.equals("DELETE") || method.equals("PATCH");

        if (!isStateChanging) {
            HttpSession session = req.getSession(false);
            if (session != null) {
                CSRFUtil.getToken(session);
            }
            chain.doFilter(request, response);
            return;
        }

        if (CSRF_EXEMPT_URLS.contains(path)) {
            chain.doFilter(request, response);
            return;
        }

        if (path.startsWith("/assets/") || path.endsWith(".css") ||
                path.endsWith(".js") || path.endsWith(".png") || path.endsWith(".jpg")) {
            chain.doFilter(request, response);
            return;
        }

        if (!CSRFUtil.validateToken(req)) {
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            res.setContentType("application/json");
            res.getWriter().write("{\"error\":\"Invalid CSRF token. Please refresh the page.\"}");
            return;
        }

        chain.doFilter(request, response);
    }
}