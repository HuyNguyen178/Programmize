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
                session.setAttribute("message", "Incorrect password!");
                session.setAttribute("success", false);
                response.sendRedirect("profile");
                return;
            }

            if (userDAO.checkUserOrEmailExists(newUsername)) {
                session.setAttribute("message", "Username has already existed!");
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
    }
}
