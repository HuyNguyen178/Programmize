package filter;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import service.RateLimiterService;
import java.io.IOException;

public class RateLimitFilter implements Filter {

    private RateLimiterService rateLimiter;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        rateLimiter = RateLimiterService.getInstance();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String clientIp = getClientIp(httpRequest);

        if (rateLimiter.isRateLimited(clientIp)) {
            httpResponse.setStatus(429);
            httpResponse.setHeader("Retry-After", "60");
            httpResponse.setContentType("application/json;charset=UTF-8");
            httpResponse.getWriter().write(
                "{\"error\": \"Too many requests. Please try again later.\"}"
            );
            return;
        }

        String requestUri = httpRequest.getRequestURI();
        if (isLoginEndpoint(requestUri) && rateLimiter.isLocked(clientIp)) {
            long remainingMinutes = rateLimiter.getRemainingLockoutMinutes(clientIp);

            httpResponse.setStatus(403);
            httpRequest.setAttribute("lockoutMessage",
                "Account locked due to too many failed attempts. " +
                "Please try again in " + remainingMinutes + " minute(s).");
            httpRequest.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        chain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    private boolean isLoginEndpoint(String uri) {
        return uri.contains("/login") || uri.contains("/LoginServlet");
    }

    @Override
    public void destroy() {}
}