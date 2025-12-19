package servlet;

import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import model.User;
import utils.SessionConfig;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String timeout = req.getParameter("timeout");
        if ("true".equals(timeout)) {
            req.setAttribute("timeoutMessage", "Your session has expired. Please login again.");
        }

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
            // Tạo session mới với secure settings
            HttpSession session = request.getSession(true);
            session.setAttribute(SessionConfig.ATTR_LOGIN_USER, user);
            session.setAttribute(SessionConfig.ATTR_CREATION_TIME, System.currentTimeMillis());
            session.setAttribute(SessionConfig.ATTR_LAST_ROTATION, System.currentTimeMillis());
            session.setMaxInactiveInterval(SessionConfig.SESSION_TIMEOUT_MINUTES * 60);

            // Set secure session cookie
            Cookie sessionCookie = new Cookie(SessionConfig.SESSION_COOKIE_NAME, session.getId());
            sessionCookie.setHttpOnly(SessionConfig.COOKIE_HTTP_ONLY);
            sessionCookie.setSecure(SessionConfig.COOKIE_SECURE);
            sessionCookie.setPath(request.getContextPath());
            sessionCookie.setMaxAge(SessionConfig.SESSION_TIMEOUT_MINUTES * 60);
            response.addCookie(sessionCookie);

            // Remember me functionality
            if (remember != null) {
                String token = user.getId() + "-" + System.currentTimeMillis();

                Cookie c = new Cookie("rememberToken", token);
                c.setMaxAge(10 * 365 * 24 * 60 * 60);
                c.setPath("/");
                c.setHttpOnly(true);
                c.setSecure(SessionConfig.COOKIE_SECURE);
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