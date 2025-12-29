package servlet;

import dao.SettingDAO;
import dao.UserDAO;
import model.User;
import service.AuditLogService;
import utils.SessionConfig;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/account-add")
public class AddAccountServlet extends HttpServlet {

    private UserDAO userDAO;
    private SettingDAO settingDAO;
    private AuditLogService auditLog;

    @Override
    public void init() throws ServletException {
        super.init();
        settingDAO = new SettingDAO();
        userDAO = new UserDAO();
        auditLog = AuditLogService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<String> roles = settingDAO.getRoleNames();
        request.setAttribute("roles", roles);

        request.getRequestDispatcher("WEB-INF/views/account-detail.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String fullname = request.getParameter("fullname");
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String newRoleName = request.getParameter("roleName");
        boolean status = "1".equals(request.getParameter("status"));
        String avatarUrl = request.getParameter("avatarUrl");
        if(avatarUrl == null){
            avatarUrl = "assets/img/admin-avatar.png";
        }

        User newUser = new User();
        newUser.setFullname(fullname);
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(password);
        newUser.setStatus(status);
        newUser.setAvatarUrl(avatarUrl);
        newUser.setRoleName(newRoleName);

        if (userDAO.checkUserOrEmailExists(username) || userDAO.checkUserOrEmailExists(email)) {
            request.setAttribute("errorMsg", "Failed to add new account! Username or Email already exists.");

            request.setAttribute("fullnameValue", fullname);
            request.setAttribute("emailValue", email);
            request.setAttribute("roleValue", newRoleName);
            request.setAttribute("statusValue", status ? "1" : "0");
            request.setAttribute("avatarUrlValue", avatarUrl);

            List<String> roles = settingDAO.getRoleNames();
            request.setAttribute("roles", roles);
            request.getRequestDispatcher("WEB-INF/views/account-detail.jsp").forward(request, response);
            return;
        }

        if (userDAO.addUser(newUser)) {
            // Log user creation
            HttpSession session = request.getSession(false);
            User admin = (User) session.getAttribute(SessionConfig.ATTR_LOGIN_USER);
            if (admin != null) {
                auditLog.logUserCreated(
                        String.valueOf(admin.getId()),
                        admin.getUsername(),
                        getClientIp(request),
                        String.valueOf(newUser.getId()),
                        username
                );
            }

            request.setAttribute("addSuccess", true);

        } else {
            request.setAttribute("errorMsg", "Failed to add new account! A database error occurred.");
            request.setAttribute("addSuccess", false);

            request.setAttribute("fullnameValue", fullname);
            request.setAttribute("emailValue", email);
            request.setAttribute("roleValue", newRoleName);
            request.setAttribute("statusValue", status ? "1" : "0");
            request.setAttribute("avatarUrlValue", avatarUrl);
        }
        List<String> roles = settingDAO.getRoleNames();
        request.setAttribute("roles", roles);
        response.sendRedirect("account-list?status=success");
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