package servlet;

import dao.UserDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import model.User;

import java.io.IOException;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String userOrEmail = request.getParameter("userOrEmail");
        String password = request.getParameter("password");

        UserDAO dao = new UserDAO();

        if (!dao.checkUserOrEmailExists(userOrEmail)) {
            request.setAttribute("userOrEmailError", "Username or email does not exist!");
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
            return;
        }

        User user = dao.checkLogin(userOrEmail, password);

        if (user != null) {
            HttpSession session = request.getSession();
            session.setAttribute("fullname", user.getFullname());
            session.setAttribute("username", user.getUsername());
            session.setAttribute("email", user.getEmail());
            request.getRequestDispatcher("/WEB-INF/views/home.jsp").forward(request, response);

        } else {
            request.setAttribute("passError", "Wrong password!");
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
        }
    }
}
