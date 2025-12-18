package servlet;

import dao.CourseEnrollmentDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.CourseEnrollment;
import java.io.IOException;

@WebServlet("/vnpay-payment-return")
public class VNPayReturnServlet extends HttpServlet {
    private CourseEnrollmentDAO enrollmentDAO = new CourseEnrollmentDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String vnp_ResponseCode = request.getParameter("vnp_ResponseCode");
        CourseEnrollment enrollment = (CourseEnrollment) request.getSession().getAttribute("pendingEnrollment");

        if (enrollment != null && "00".equals(vnp_ResponseCode)) {
            enrollment.setStatus(true);
            if (enrollmentDAO.addEnrollment(enrollment)) {
                request.getSession().removeAttribute("pendingEnrollment");

                String txnRef = request.getParameter("vnp_TransactionNo");

                request.setAttribute("transactionId", txnRef);
                request.getRequestDispatcher("/WEB-INF/views/enrollment-success.jsp?id=" + txnRef).forward(request, response);
            } else {
                response.sendRedirect("course-enrollment?error=db_error");
            }
        } else {
            response.sendRedirect("course-enrollment?error=payment_failed");
        }
    }
}