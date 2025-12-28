package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import service.AuditLogService;
import utils.SessionConfig;

import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    private AuditLogService auditLog;

    @Override
    public void init() throws ServletException {
        auditLog = AuditLogService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session != null) {
            // Log logout before invalidating session
            User user = (User) session.getAttribute(SessionConfig.ATTR_LOGIN_USER);
            if (user != null) {
                auditLog.logLogout(String.valueOf(user.getId()), user.getUsername(), getClientIp(request));
            }

            // Clear all session attributes
            session.invalidate();
        }

        // Clear session cookie
        Cookie sessionCookie = new Cookie(SessionConfig.SESSION_COOKIE_NAME, "");
        sessionCookie.setMaxAge(0);
        sessionCookie.setPath(request.getContextPath());
        sessionCookie.setHttpOnly(true);
        sessionCookie.setSecure(SessionConfig.COOKIE_SECURE);
        response.addCookie(sessionCookie);

        // Clear remember me cookie
        Cookie rememberCookie = new Cookie("rememberToken", "");
        rememberCookie.setMaxAge(0);
        rememberCookie.setPath(request.getContextPath());
        rememberCookie.setHttpOnly(true);
        rememberCookie.setSecure(SessionConfig.COOKIE_SECURE);
        response.addCookie(rememberCookie);

        response.sendRedirect(request.getContextPath() + "/login");
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
}