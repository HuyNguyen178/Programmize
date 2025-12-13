package filter;

import dao.UserDAO;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import java.io.IOException;

@WebFilter("/*")
public class A_AutoLoginFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        if (session != null && session.getAttribute("loginUser") != null) {
            chain.doFilter(request, response);
            return;
        }

        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (c.getName().equals("rememberToken")) {

                    String token = c.getValue();

                    UserDAO dao = new UserDAO();
                    User user = dao.loginWithToken(token);

                    if (user != null) {
                        HttpSession newSession = req.getSession();
                        newSession.setAttribute("loginUser", user);

                        switch (user.getRoleName()) {
                            case "Admin":
                                res.sendRedirect("dashboard");
                                return;
                            case "Instructor":
                                res.sendRedirect("student-list");
                                return;
                            default:
                                res.sendRedirect("home");
                                return;
                        }
                    }
                }
            }
        }
        chain.doFilter(request, response);
    }
}
