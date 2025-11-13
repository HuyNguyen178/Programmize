package servlet;

import dao.UserDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet(name = "RegisterServlet", urlPatterns = {"/register"})
public class RegisterServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String fullname = request.getParameter("fullname");
        String username = request.getParameter("username");
        String email = request.getParameter("email").trim();
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        UserDAO dao = new UserDAO();
        boolean error = false;

        // Check fullname
        if (fullname.trim().isEmpty()) {
            request.setAttribute("fullnameError", "Full name cannot be empty!");
            error = true;
        } else if (fullname.length() > 50) {
            request.setAttribute("fullnameError", "Full name must be less than 50 characters!");
            error = true;
        }

        // Check username
        if (username.trim().isEmpty()) {
            request.setAttribute("usernameError", "Username cannot be empty!");
            error = true;
        } else if (username.length() > 20) {
            request.setAttribute("usernameError", "Username must be less than 20 characters!");
            error = true;
        } else if (dao.checkUserOrEmailExists(username)) {
            request.setAttribute("usernameError", "Username already exists!");
            error = true;
        }

        // Check email
        if (dao.checkUserOrEmailExists(email)) {
            request.setAttribute("emailError", "Email already exists!");
            error = true;
        }

        // Check password
        if (password.length() < 8) {
            request.setAttribute("passError", "Password must be at least 8 characters!");
            error = true;
        }

        // Check Confirm Password
        if (!password.equals(confirmPassword)) {
            request.setAttribute("confirmPassError", "Confirmation password does not match!");
            error = true;
        }

        if (error) {
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
            return;
        }

        // ===== LƯU THÔNG TIN VÀO SESSION (CHƯA TẠO ACCOUNT) =====
        HttpSession session = request.getSession();
        session.setAttribute("registerFullname", fullname);
        session.setAttribute("registerUsername", username);
        session.setAttribute("registerEmail", email);
        session.setAttribute("registerPassword", password);

        // Chuyển đến verify (sẽ gửi email code)
        response.sendRedirect("verify?email=" + email);
    }
}