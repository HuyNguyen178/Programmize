package servlet;

import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import utils.EmailUtil;

import java.io.IOException;

@WebServlet("/forgot-password")
public class ForgotPasswordServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String step = request.getParameter("step");
        HttpSession session = request.getSession();
        if ("resend".equals(step)) {
            String email = (String) session.getAttribute("resetEmail");

            if (email == null) {
                response.sendRedirect("forgot-password");
                return;
            }

            String newCode = String.valueOf((int) (Math.random() * 900000) + 100000);
            session.setAttribute("resetCode", newCode);

            try {
                EmailUtil.sendEmail(email, "Your new verification code is: " + newCode);
                request.getRequestDispatcher("/WEB-INF/views/verifyReset.jsp").forward(request, response);
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("error", "Failed to resend verification code!");
                request.getRequestDispatcher("/WEB-INF/views/verifyReset.jsp").forward(request, response);
            }
            return;
        }
        request.getRequestDispatcher("/WEB-INF/views/forgotPassword.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String step = request.getParameter("step");
        HttpSession session = request.getSession();
        UserDAO dao = new UserDAO();

        try {
            if ("send".equals(step)) {
                String email = request.getParameter("email");

                if (!dao.checkUserOrEmailExists(email)) {
                    request.setAttribute("error", "Email does not exist!");
                    request.getRequestDispatcher("/WEB-INF/views/forgotPassword.jsp").forward(request, response);
                    return;
                }

                String code = String.valueOf((int) (Math.random() * 900000) + 100000);

                session.setAttribute("resetEmail", email);
                session.setAttribute("resetCode", code);

                try {
                    EmailUtil.sendEmail(email, "Your password verification code is: " + code);
                    request.getRequestDispatcher("/WEB-INF/views/verifyReset.jsp").forward(request, response);
                } catch (Exception e) {
                    e.printStackTrace();
                    request.setAttribute("error", "Failed to send email. Please try again.");
                    request.getRequestDispatcher("/WEB-INF/views/forgotPassword.jsp").forward(request, response);
                }

                return;
            }

            else if ("verify".equals(step)) {
                String codeInput = request.getParameter("code");
                String codeSession = (String) session.getAttribute("resetCode");

                if (codeSession != null && codeSession.equals(codeInput)) {
                    request.getRequestDispatcher("/WEB-INF/views/newPassword.jsp").forward(request, response);
                } else {
                    request.setAttribute("error", "Incorrect verification code!");
                    request.getRequestDispatcher("/WEB-INF/views/verifyReset.jsp").forward(request, response);
                }

                return;
            }

            else if ("change".equals(step)) {
                String newPassword = request.getParameter("newPassword");
                String confirmPassword = request.getParameter("confirmPassword");

                // Validate password
                if (newPassword == null || newPassword.length() < 6) {
                    request.setAttribute("error", "Password must be at least 6 characters!");
                    request.getRequestDispatcher("/WEB-INF/views/newPassword.jsp").forward(request, response);
                    return;
                }

                if (!newPassword.equals(confirmPassword)) {
                    request.setAttribute("error", "Passwords do not match!");
                    request.getRequestDispatcher("/WEB-INF/views/newPassword.jsp").forward(request, response);
                    return;
                }

                String email = (String) session.getAttribute("resetEmail");
                if (email == null) {
                    response.sendRedirect("forgot-password");
                    return;
                }

                // Update password (DAO tự hash)
                boolean success = dao.updatePasswordByEmail(email, newPassword);

                if (!success) {
                    request.setAttribute("error", "Failed to update password!");
                    request.getRequestDispatcher("/WEB-INF/views/newPassword.jsp").forward(request, response);
                    return;
                }

                // Clear session
                session.removeAttribute("resetEmail");
                session.removeAttribute("resetCode");

                request.getRequestDispatcher("/WEB-INF/views/resetSuccess.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "An unexpected error occurred!");
            request.getRequestDispatcher("/WEB-INF/views/forgotPassword.jsp").forward(request, response);
        }
    }
}
