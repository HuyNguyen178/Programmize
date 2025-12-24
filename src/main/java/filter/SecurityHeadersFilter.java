package filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/*")
public class SecurityHeadersFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // No initialization needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 1. X-Content-Type-Options - Ngăn MIME type sniffing
        httpResponse.setHeader("X-Content-Type-Options", "nosniff");

        // 2. X-Frame-Options - Ngăn clickjacking attacks
        httpResponse.setHeader("X-Frame-Options", "SAMEORIGIN"); //change DENY to SAMEORIGIN to be able to load iframe for pdf </assets/pdf/>

        // 3. X-XSS-Protection - Bật XSS filter của browser
        httpResponse.setHeader("X-XSS-Protection", "1; mode=block");

        // 4. Strict-Transport-Security - Bắt buộc HTTPS
        httpResponse.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains; preload");

        // 5. Content-Security-Policy - Kiểm soát nguồn tài nguyên
        httpResponse.setHeader("Content-Security-Policy",
            "default-src 'self'; " +
            "script-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net https://cdnjs.cloudflare.com; " +
            "style-src 'self' 'unsafe-inline' https://fonts.googleapis.com https://cdn.jsdelivr.net https://cdnjs.cloudflare.com; " +
            "font-src 'self' https://fonts.gstatic.com https://cdnjs.cloudflare.com; " +
            "img-src 'self' data: https: https://lh3.googleusercontent.com; " +
            "connect-src 'self'; " +
            "frame-src 'self' https://www.youtube.com https://youtube.com https://www.youtube-nocookie.com https://player.vimeo.com https://drive.google.com https://docs.google.com; " +
            "frame-ancestors 'self'; " + //change "none" to "self" to be able to load iframe for pdf </assets/pdf/>
            "form-action 'self'; " +
            "base-uri 'self'"
        );

        // 6. Referrer-Policy - Kiểm soát thông tin referrer
        httpResponse.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");

        // 7. Permissions-Policy - Hạn chế quyền truy cập API
        httpResponse.setHeader("Permissions-Policy",
            "geolocation=(), " +
            "microphone=(), " +
            "camera=(), " +
            "payment=(self)"
        );

        // 8. Cache-Control cho các trang nhạy cảm
        httpResponse.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, proxy-revalidate");
        httpResponse.setHeader("Pragma", "no-cache");
        httpResponse.setHeader("Expires", "0");

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // No cleanup needed
    }
}