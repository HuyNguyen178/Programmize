package servlet;

import dao.UserDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import model.User;

import java.io.IOException;

@WebServlet("/login")
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
        String remember = request.getParameter("rememberMe");

        UserDAO dao = new UserDAO();

        if (!dao.checkUserOrEmailExists(userOrEmail)) {
            request.setAttribute("userOrEmailError", "Username or email does not exist!");
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
            return;
        }

        User user = dao.checkLogin(userOrEmail, password);

        if (user != null) {
            HttpSession session = request.getSession();
            session.setAttribute("loginUser", user);

            if (remember != null) {
                String token = user.getId() + "-" + System.currentTimeMillis();

                Cookie c = new Cookie("rememberToken", token);
                c.setMaxAge(10 * 365 * 24 * 60 * 60);
                c.setPath("/");
                response.addCookie(c);

                dao.saveRememberToken(user.getId(), token);
            }

            String redirectUrl = request.getParameter("redirect");

            if (redirectUrl != null && !redirectUrl.isEmpty()) {
                response.sendRedirect(redirectUrl);
                return;
            }

            switch (user.getRoleName()) {
                case "Admin":
                    response.sendRedirect("dashboard");
                    break;
                case "Instructor":
                    response.sendRedirect("student-list");
                    break;
                default:
                    response.sendRedirect("home");
                    break;
            }

        } else {
            request.setAttribute("passError", "Wrong password!");
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
        }
    }
}
