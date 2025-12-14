package servlet;

import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;

import java.io.IOException;

@WebServlet("/profile-update")
public class ProfileUpdateServlet extends HttpServlet {
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("userId"));
        String avtUrl = request.getParameter("avatarUrl");
        String fullName = request.getParameter("fullname");
        String email = request.getParameter("email");

        User user = userDAO.getUserById(id);

        if (fullName.isEmpty() || avtUrl.isEmpty() || email.isEmpty()) {
            request.setAttribute("message", "All fields are required!");
            request.setAttribute("user", user);
            request.getRequestDispatcher("/WEB-INF/views/user-profile.jsp").forward(request, response);
            return;
        }

        if (userDAO.checkUserOrEmailExists(email) && !user.getEmail().equals(email)) {
            request.setAttribute("message", "Email has been used!");
            request.setAttribute("user", user);
            request.getRequestDispatcher("/WEB-INF/views/user-profile.jsp").forward(request, response);
            return;
        }

        user.setFullname(fullName);
        user.setEmail(email);
        user.setAvatarUrl(avtUrl);

        if (userDAO.updateUser(user)) {
            HttpSession session = request.getSession();
            session.setAttribute("loginUser", user);

            request.setAttribute("message", "Update successfully!");
            request.setAttribute("success", true);
        }
        else {
            request.setAttribute("message", "Error in updating your profile!");
        }

        request.setAttribute("user", user);
        request.getRequestDispatcher("/WEB-INF/views/user-profile.jsp").forward(request, response);
    }
}
