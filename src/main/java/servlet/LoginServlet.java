package servlet;

import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import model.User;
import service.RateLimiterService;
import utils.SessionConfig;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private RateLimiterService rateLimiter;

    @Override
    public void init() throws ServletException {
        rateLimiter = RateLimiterService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String clientIp = getClientIp(req);

        String timeout = req.getParameter("timeout");
        if ("true".equals(timeout)) {
            req.setAttribute("timeoutMessage", "Your session has expired. Please login again.");
        }

        // Check lockout status
        if (rateLimiter.isLocked(clientIp)) {
            long remainingMinutes = rateLimiter.getRemainingLockoutMinutes(clientIp);
            req.setAttribute("lockoutMessage",
                "Account locked due to too many failed attempts. " +
                "Please try again in " + remainingMinutes + " minute(s).");
        }

        // Show remaining attempts warning
        int remaining = rateLimiter.getRemainingAttempts(clientIp);
        if (remaining < 5 && remaining > 0) {
            req.setAttribute("remainingAttempts", remaining);
        }

        req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String clientIp = getClientIp(request);
        String userOrEmail = request.getParameter("userOrEmail");
        String password = request.getParameter("password");
        String remember = request.getParameter("rememberMe");

        // Check lockout before processing
        if (rateLimiter.isLocked(clientIp)) {
            long remainingMinutes = rateLimiter.getRemainingLockoutMinutes(clientIp);
            request.setAttribute("lockoutMessage",
                "Account locked due to too many failed attempts. " +
                "Please try again in " + remainingMinutes + " minute(s).");
            request.setAttribute("userOrEmail", userOrEmail);
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
            return;
        }

        UserDAO dao = new UserDAO();

        if (!dao.checkUserOrEmailExists(userOrEmail)) {
            rateLimiter.recordFailedLogin(clientIp, userOrEmail);
            request.setAttribute("userOrEmailError", "Username or email does not exist!");
            request.setAttribute("remainingAttempts", rateLimiter.getRemainingAttempts(clientIp));
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
            return;
        }

        User user = dao.checkLogin(userOrEmail, password);

        if (user != null) {
            // Check if user is active
            if (!user.isStatus()) {
                request.setAttribute("passError", "Your account is inactive. Please contact administrator.");
                request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
                return;
            }

            // Login successful - clear rate limiter
            rateLimiter.recordSuccessfulLogin(clientIp);

            // Create new session with secure settings
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

            // Safe null check for role name
            String roleName = user.getRoleName();
            if (roleName == null) {
                roleName = "";
            }

            switch (roleName) {
                case "Admin":
                    response.sendRedirect("dashboard");
                    break;
                case "Instructor":
                    response.sendRedirect("student-list");
                    break;
                case "Student":
                    response.sendRedirect("home");
                    break;
                default:
                    response.sendRedirect("home");
                    break;
            }

        } else {
            // Login failed - record attempt
            rateLimiter.recordFailedLogin(clientIp, userOrEmail);
            int remaining = rateLimiter.getRemainingAttempts(clientIp);

            if (remaining > 0) {
                request.setAttribute("passError", "Wrong password!");
                request.setAttribute("remainingAttempts", remaining);
            } else {
                long lockMinutes = rateLimiter.getRemainingLockoutMinutes(clientIp);
                request.setAttribute("lockoutMessage",
                    "Too many failed attempts. Account locked for " + lockMinutes + " minute(s).");
            }

            request.setAttribute("userOrEmail", userOrEmail);
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
        }
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