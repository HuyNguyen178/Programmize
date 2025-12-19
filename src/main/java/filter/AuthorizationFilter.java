package filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Filter này thực hiện 2 nhiệm vụ:
 * 1. Authentication: Kiểm tra user đã đăng nhập chưa.
 * 2. Authorization (RBAC): Kiểm tra user có quyền truy cập URL này không.
 */
@WebFilter("/*")
public class AuthorizationFilter implements Filter {

    // Danh sách các URL cho phép truy cập tự do (không cần login)
    private static final List<String> PUBLIC_URLS = Arrays.asList(
            "/login",
            "/register",
            "/home",
            "/forgot-password",
            "/verify-email",
            "/reset-password",
            "/check-otp",
            "/new-password",
            "/public-courses",
            "/public-course-details",
            "/lesson-detail"
    );

    // Danh sách các URL dành riêng cho Admin
    private static final List<String> ADMIN_URLS = Arrays.asList(
            "/dashboard",
            "/account-list",
            "/account-detail",
            "/add-account",
            "/setting-list",
            "/setting-detail",
            "/add-setting",
            "/course-list",
            "/course-details",
            "/add-course"
    );

    // Danh sách các URL CHỈ DÀNH CHO Instructor (Admin cũng có thể vào)
    private static final List<String> INSTRUCTOR_URLS = Arrays.asList(
            "/course-content",
            "/student-list",
            "/student-detail",
            "/add-student",
            "/add-chapter",
            "/add-lesson",
            "/edit-lesson"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String path = req.getServletPath();

        // 1. CHO PHÉP TÀI NGUYÊN TĨNH
        if (path.startsWith("/assets/") || path.endsWith(".css") || path.endsWith(".js") || path.endsWith(".png") || path.endsWith(".jpg")) {
            chain.doFilter(request, response);
            return;
        }

        // 2. CHO PHÉP CÁC TRANG PUBLIC
        if (PUBLIC_URLS.contains(path) || path.equals("/")) {
            chain.doFilter(request, response);
            return;
        }

        // 3. AUTHENTICATION (KIỂM TRA ĐĂNG NHẬP)
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("loginUser") : null;

        if (user == null) {
            res.sendRedirect(req.getContextPath() + "/login?message=Please login first");
            return;
        }

        // 4. AUTHORIZATION (PHÂN QUYỀN - RBAC)
        String role = user.getRoleName();

        // Quy tắc A: Bảo vệ trang Admin
        if (ADMIN_URLS.contains(path)) {
            if (!"Admin".equals(role)) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: You do not have Admin privileges.");
                return;
            }
        }

        // Quy tắc B: Bảo vệ trang dành riêng cho Instructor (Tạo bài, Sửa bài)
        if (INSTRUCTOR_URLS.contains(path)) {
            if (!"Admin".equals(role) && !"Instructor".equals(role)) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Instructor privileges required.");
                return;
            }
        }

        // Quy tắc C: Các trang chung cho người dùng đã đăng nhập (My Courses, My Classes...)
        // Mặc định: Nếu đã login và không bị chặn bởi 2 quy tắc trên thì cho qua.
        // Điều này có nghĩa là Student, Instructor, Admin đều vào được /my-courses.

        chain.doFilter(request, response);
    }
}