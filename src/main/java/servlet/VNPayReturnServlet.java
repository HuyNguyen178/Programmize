package servlet;

import dao.EnrollmentDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ClassEnrollment;
import model.CourseEnrollment;
import java.io.IOException;

@WebServlet("/vnpay-payment-return")
public class VNPayReturnServlet extends HttpServlet {
    private EnrollmentDAO enrollmentDAO = new EnrollmentDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String vnp_ResponseCode = request.getParameter("vnp_ResponseCode");
        String type = (String) request.getSession().getAttribute("enrollmentType");
        Object pendingObj = request.getSession().getAttribute("pendingEnrollment");

        if (pendingObj != null && "00".equals(vnp_ResponseCode)) {
            boolean isAdded = false;

            if ("class".equals(type)) {
                ClassEnrollment classEnroll = (ClassEnrollment) pendingObj;
                classEnroll.setStatus(true);
                isAdded = enrollmentDAO.addEnrollment(classEnroll);
            } else if ("course".equals(type)) {
                CourseEnrollment courseEnroll = (CourseEnrollment) pendingObj;
                courseEnroll.setStatus(true);
                isAdded = enrollmentDAO.addEnrollment(courseEnroll);
            }
            if (isAdded) {
                request.getSession().removeAttribute("pendingEnrollment");
                request.getSession().removeAttribute("enrollmentType");

                String txnRef = request.getParameter("vnp_TransactionNo");
                request.setAttribute("transactionId", txnRef);

                request.getRequestDispatcher("/WEB-INF/views/enrollment-success.jsp").forward(request, response);
            } else {
                response.sendRedirect("enrollment?error=db_error&type=" + type);
            }
        } else {
            response.sendRedirect("enrollment?error=payment_failed&type=" + type);
        }
    }
}