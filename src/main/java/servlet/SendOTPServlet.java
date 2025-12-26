package servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import utils.EmailUtil;
import utils.SessionConfig;

import java.io.IOException;

@WebServlet("/send-otp")
public class SendOTPServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        String type = request.getParameter("type");
        User u = (User) session.getAttribute(SessionConfig.ATTR_LOGIN_USER);

        String emailDest;
        if ("old".equals(type)) {
            emailDest = u.getEmail();
        } else {
            emailDest = request.getParameter("email");
        }


        String otp = String.format("%06d", new java.util.Random().nextInt(999999));
        session.setAttribute(type + "_email_otp", otp);

        try {
            EmailUtil.sendEmail(emailDest, "Your verification code is: " + otp);
            response.setStatus(200);
        } catch (Exception e) {
            response.setStatus(500);
        }
    }
}
