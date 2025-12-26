package servlet;

import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import utils.PasswordUtil;
import utils.SessionConfig;

import java.io.IOException;

@WebServlet("/profile")
public class UserProfileServlet extends HttpServlet {
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User loginUser = (User) session.getAttribute(SessionConfig.ATTR_LOGIN_USER);

        if (loginUser == null) {
            response.sendRedirect("login");
            return;
        }

        User user = userDAO.getUserById(loginUser.getId());

        if (user == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        request.setAttribute("user", user);
        request.getRequestDispatcher("/WEB-INF/views/user-profile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(SessionConfig.ATTR_LOGIN_USER);
        User u = userDAO.getUserById(user.getId());
        if (u == null) {
            response.sendRedirect("login");
            return;
        }

        // Thêm vào trong phương thức doPost của UserProfileServlet
        String newEmail = request.getParameter("newEmail");
        String verifyCode = request.getParameter("verifyCode");
        String oldVerifyCode = request.getParameter("oldVerifyCode");

        if (newEmail != null && verifyCode != null && oldVerifyCode != null) {
            String sessionOldOtp = (String) session.getAttribute("old_email_otp");
            String sessionNewOtp = (String) session.getAttribute("new_email_otp");

            // 1. Kiểm tra mã OTP của email cũ
            if (sessionOldOtp == null || !sessionOldOtp.equals(oldVerifyCode)) {
                session.setAttribute("message", "Mã xác thực email CŨ không chính xác!");
                session.setAttribute("success", false);
                response.sendRedirect("profile");
                return;
            }

            // 2. Kiểm tra mã OTP của email mới
            if (sessionNewOtp == null || !sessionNewOtp.equals(verifyCode)) {
                session.setAttribute("message", "Mã xác thực email MỚI không chính xác!");
                session.setAttribute("success", false);
                response.sendRedirect("profile");
                return;
            }

            // 3. Cập nhật Database
            u.setEmail(newEmail);
            if (userDAO.updateUser(u, null)) {
                session.setAttribute(SessionConfig.ATTR_LOGIN_USER, u);
                session.setAttribute("message", "Email updated successfully!");
                session.setAttribute("success", true);
            } else {
                session.setAttribute("message", "Update failed!");
                session.setAttribute("success", false);
            }

            session.removeAttribute("old_email_otp");
            session.removeAttribute("new_email_otp");

            response.sendRedirect("profile");
            return;
        }

        String fullName = request.getParameter("fullname");
        if (fullName != null && !fullName.equals(user.getFullname())) {
            user.setFullname(fullName);
            userDAO.updateUser(user, u.getPassword());
            session.setAttribute(SessionConfig.ATTR_LOGIN_USER, user);
            session.setAttribute("message", "Full name updated successfully!");
            session.setAttribute("success", true);
            response.sendRedirect("profile");
            return;
        }

        String newUsername = request.getParameter("newUsername");
        String currentPassword = request.getParameter("password");
        if (newUsername != null && currentPassword != null) {
            if (!PasswordUtil.check(currentPassword, u.getPassword())) {
                session.setAttribute("modalError", "Incorrect password!");
                session.setAttribute("openModal", "username");
                session.setAttribute("success", false);
                response.sendRedirect("profile");
                return;
            }

            if (userDAO.checkUserOrEmailExists(newUsername)) {
                session.setAttribute("modalError", "Username has already existed!");
                session.setAttribute("openModal", "username");
                session.setAttribute("success", false);
                response.sendRedirect("profile");
                return;
            }

            user.setUsername(newUsername);
            userDAO.updateUser(user, currentPassword);
            session.setAttribute(SessionConfig.ATTR_LOGIN_USER, user);
            session.setAttribute("message", "Username updated successfully!");
            session.setAttribute("success", true);
            response.sendRedirect("profile");
            return;
        }

        String oldPass = request.getParameter("oldPass");
        String newPass = request.getParameter("newPass");
        String confirmPass = request.getParameter("confirmPass");
        if (!PasswordUtil.check(oldPass, u.getPassword())) {
            session.setAttribute("modalError", "Incorrect password!");
            session.setAttribute("openModal", "password");
            session.setAttribute("success", false);
            response.sendRedirect("profile");
            return;
        }

        if (!newPass.equals(confirmPass)) {
            session.setAttribute("modalError", "Password do not match!");
            session.setAttribute("openModal", "password");
            session.setAttribute("success", false);
            response.sendRedirect("profile");
            return;
        }
        if (newPass.length() < 8) {
            session.setAttribute("modalError", "Password must be at least 8 characters!");
            session.setAttribute("openModal", "password");
            session.setAttribute("success", false);
            response.sendRedirect("profile");
            return;
        }

        userDAO.updateUser(user, newPass);
        session.setAttribute(SessionConfig.ATTR_LOGIN_USER, user);
        session.setAttribute("message", "Password updated successfully!");
        session.setAttribute("success", true);
        response.sendRedirect("profile");
    }
}
