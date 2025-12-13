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
            "/public-courses", // Danh sách khóa học public
            "/public-course-details"
    );

    // Danh sách các URL dành riêng cho Admin
    private static final List<String> ADMIN_URLS = Arrays.asList(
            "/dashboard",
            "/account-list",
            "/setting-list",
            "/add-setting",
            "/setting-detail",
            "/add-student" // Giả sử chỉ admin được thêm học viên thủ công
    );

    // Danh sách các URL dành cho Instructor (Admin cũng có thể vào nếu muốn)
    private static final List<String> INSTRUCTOR_URLS = Arrays.asList(
            "/my-courses",
            "/add-course",
            "/edit-course",
            "/my-classes",
            "/student-list" // Danh sách học viên trong lớp
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String path = req.getServletPath();

        // 1. CHO PHÉP TÀI NGUYÊN TĨNH (CSS, JS, IMAGES)
        // Kiểm tra nếu request là assets hoặc file tĩnh thì cho qua luôn
        if (path.startsWith("/assets/") || path.endsWith(".css") || path.endsWith(".js") || path.endsWith(".png") || path.endsWith(".jpg")) {
            chain.doFilter(request, response);
            return;
        }

        // 2. CHO PHÉP CÁC TRANG PUBLIC
        // Nếu path nằm trong whitelist hoặc là trang chủ ("/")
        if (PUBLIC_URLS.contains(path) || path.equals("/")) {
            chain.doFilter(request, response);
            return;
        }

        // 3. AUTHENTICATION (KIỂM TRA ĐĂNG NHẬP)
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("loginUser") : null;

        if (user == null) {
            // Nếu chưa đăng nhập mà cố vào trang nội bộ -> Đá về trang Login
            res.sendRedirect(req.getContextPath() + "/login?message=Please login first");
            return;
        }

        // 4. AUTHORIZATION (PHÂN QUYỀN - RBAC)
        String role = user.getRoleName(); // Lấy Role từ đối tượng User (Admin, Instructor, Student)

        // Quy tắc A: Bảo vệ trang Admin
        if (ADMIN_URLS.contains(path)) {
            if (!"Admin".equals(role)) {
                // Nếu không phải Admin -> Báo lỗi 403 (Forbidden) hoặc chuyển về trang 403
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: You do not have Admin privileges.");
                return;
            }
        }

        // Quy tắc B: Bảo vệ trang Instructor
        if (INSTRUCTOR_URLS.contains(path)) {
            // Chỉ cho phép Admin hoặc Instructor
            if (!"Admin".equals(role) && !"Instructor".equals(role)) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Instructor privileges required.");
                return;
            }
        }

        // Quy tắc C: Trang dành cho Student (hoặc user đã login nói chung)
        // Ví dụ: /profile, /learning, /course-content
        // Mặc định: Nếu đã login thì cho qua (trừ khi bạn muốn chặn Student vào trang cụ thể)

        // Nếu vượt qua tất cả các chốt chặn -> Cho phép request đi tiếp
        chain.doFilter(request, response);
    }
}