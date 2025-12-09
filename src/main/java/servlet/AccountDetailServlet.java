package servlet;

import dao.SettingDAO;
import dao.UserDAO;
import model.Setting;
import model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList; // Cần import nếu dùng List trống

@WebServlet("/account-detail")
public class AccountDetailServlet extends HttpServlet {

    private UserDAO userDAO;
    private SettingDAO settingDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        settingDAO = new SettingDAO();
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String userIdParam = request.getParameter("id");

        List<String> roles = settingDAO.getRoleNames();
        request.setAttribute("roles", roles);

        if (userIdParam != null && !userIdParam.isEmpty()) {
            try {
                int userId = Integer.parseInt(userIdParam);
                User user = userDAO.getUserById(userId);

                if (user != null) {
                    request.setAttribute("user", user);
                    request.getRequestDispatcher("WEB-INF/views/account-detail.jsp").forward(request, response);
                    return;
                }
            } catch (NumberFormatException e) {
            }
        }

        response.sendRedirect("account-list");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int userId = Integer.parseInt(request.getParameter("userId"));
        String fullname = request.getParameter("fullname");
        String email = request.getParameter("email");
        String newRoleName = request.getParameter("roleName");
        String password = request.getParameter("password");
        boolean status = "1".equals(request.getParameter("status"));
        String avatarUrl = request.getParameter("avatarUrl");

        User user = new User();
        user.setId(userId);
        user.setFullname(fullname);
        user.setEmail(email);
        user.setStatus(status);
        user.setAvatarUrl(avatarUrl);
        user.setRoleName(newRoleName);

        if (password != null && !password.isEmpty()) {
            user.setPassword(password);
        }

        if (userDAO.updateUser(user)) {
            request.setAttribute("updateSuccess", true);

            User updatedUser = userDAO.getUserById(userId);
            List<String> roles = settingDAO.getRoleNames();

            request.setAttribute("user", updatedUser);
            request.setAttribute("roles", roles);

            request.getRequestDispatcher("WEB-INF/views/account-detail.jsp").forward(request, response);

        } else {
            request.setAttribute("errorMsg", "Update failed! Please check your input and try again.");

            request.setAttribute("fullnameValue", fullname);
            request.setAttribute("emailValue", email);
            request.setAttribute("roleValue", newRoleName);
            request.setAttribute("statusValue", status ? "1" : "0");
            request.setAttribute("avatarUrlValue", avatarUrl);

            User originalUser = userDAO.getUserById(userId);
            List<String> roles = settingDAO.getRoleNames();

            if(originalUser != null) {
                request.setAttribute("user", originalUser);
                request.setAttribute("roles", roles);
                request.getRequestDispatcher("WEB-INF/views/account-detail.jsp").forward(request, response);
            } else {
                response.sendRedirect("account-list?error=notfound");
            }
        }
    }
}