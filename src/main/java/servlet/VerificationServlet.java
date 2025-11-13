package servlet;

import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.User;
import utils.EmailUtil;

import java.io.IOException;

@WebServlet("/verify")
public class VerificationServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String step = request.getParameter("step");
        HttpSession session = request.getSession();

        // ====== XỬ LÝ RESEND CODE ======
        if ("resend".equals(step)) {
            String email = (String) session.getAttribute("verifyEmail");

            if (email == null) {
                response.sendRedirect("register");
                return;
            }

            // Tạo mã ngẫu nhiên
            String newCode = String.valueOf((int) (Math.random() * 900000) + 100000);
            session.setAttribute("verifyCode", newCode);

            try {
                EmailUtil.sendEmail(email, "Your new verification code is: " + newCode);
                request.setAttribute("message", "A new verification code has been sent to your email.");
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("error", "Failed to resend verification code!");
            }

            request.getRequestDispatcher("/WEB-INF/views/verifyEmail.jsp").forward(request, response);
            return;
        }

        // ====== LẦN ĐẦU GỬI MÃ SAU KHI ĐĂNG KÝ ======
        String email = request.getParameter("email");
        if (email == null || email.isEmpty()) {
            response.sendRedirect("register");
            return;
        }

        String code = String.valueOf((int) (Math.random() * 900000) + 100000);
        session.setAttribute("verifyEmail", email);
        session.setAttribute("verifyCode", code);

        try {
            EmailUtil.sendEmail(email, "Your verification code is: " + code);
            request.getRequestDispatcher("/WEB-INF/views/verifyEmail.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Could not send verification email. Please try again.");
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String inputCode = request.getParameter("code");
        HttpSession session = request.getSession();

        String sessionCode = (String) session.getAttribute("verifyCode");
        String email = (String) session.getAttribute("verifyEmail");

        if (inputCode != null && inputCode.equals(sessionCode)) {

            // ===== LẤY THÔNG TIN TỪ SESSION =====
            String fullname = (String) session.getAttribute("registerFullname");
            String username = (String) session.getAttribute("registerUsername");
            String password = (String) session.getAttribute("registerPassword");

            // ===== TẠO TÀI KHOẢN SAU KHI VERIFY THÀNH CÔNG =====
            if (fullname != null && username != null && password != null) {
                User user = new User();
                user.setFullname(fullname);
                user.setUsername(username);
                user.setEmail(email);
                user.setPassword(password);
                user.setStatus(true); // Email đã được xác thực

                UserDAO dao = new UserDAO();
                if (dao.addUser(user)) {
                    // Xóa thông tin trong session
                    session.removeAttribute("registerFullname");
                    session.removeAttribute("registerUsername");
                    session.removeAttribute("registerEmail");
                    session.removeAttribute("registerPassword");
                    session.removeAttribute("verifyCode");
                    session.removeAttribute("verifyEmail");

                    request.getRequestDispatcher("/WEB-INF/views/verifySuccess.jsp").forward(request, response);
                } else {
                    request.setAttribute("error", "Failed to create account. Please try again.");
                    request.getRequestDispatcher("/WEB-INF/views/verifyEmail.jsp").forward(request, response);
                }
            } else {
                // Nếu không có thông tin đăng ký trong session
                // (Trường hợp user đã có account, chỉ verify email)
                UserDAO dao = new UserDAO();
                dao.updateStatusByEmail(email);

                session.removeAttribute("verifyCode");
                session.removeAttribute("verifyEmail");

                request.getRequestDispatcher("/WEB-INF/views/verifySuccess.jsp").forward(request, response);
            }

        } else {
            request.setAttribute("error", "Incorrect verification code!");
            request.getRequestDispatcher("/WEB-INF/views/verifyEmail.jsp").forward(request, response);
        }
    }
}
