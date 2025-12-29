package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import service.AuditLogService;
import utils.SessionConfig;

import java.io.IOException;

@WebServlet("/profile/edit")
public class EditProfileServlet extends HttpServlet {

    private AuditLogService auditLog;

    @Override
    public void init() throws ServletException {
        auditLog = AuditLogService.getInstance();
    }

    // Add your existing doGet/doPost methods here
    // When password is changed successfully, add:
    // auditLog.logPasswordChange(String.valueOf(user.getId()), user.getUsername(), getClientIp(request));

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
        if ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) {
            ip = "127.0.0.1";
        }
        return ip;
    }
}