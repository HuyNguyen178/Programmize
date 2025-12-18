package servlet;

import dao.CourseDAO;
import dao.CourseEnrollmentDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.CourseEnrollment;
import model.User;
import utils.VNPayUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

@WebServlet("/course-enrollment")
public class CourseEnrollmentServlet extends HttpServlet {
    private CourseEnrollmentDAO enrollmentDAO;

    public void init() {
        this.enrollmentDAO = new CourseEnrollmentDAO();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String idParam = request.getParameter("courseId");

            if (idParam != null && !idParam.isEmpty()) {
                int courseId = Integer.parseInt(idParam);

                CourseDAO courseDAO = new CourseDAO();
                model.Course course = courseDAO.getCourseById(courseId);

                if (course != null) {
                    request.setAttribute("course", course);
                    request.getRequestDispatcher("/WEB-INF/views/course-enrollment.jsp").forward(request, response);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User user = (User) request.getSession().getAttribute("loginUser");
        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            int courseId = Integer.parseInt(request.getParameter("courseId"));
            BigDecimal pricePaid = new BigDecimal(request.getParameter("pricePaid"));
            String paymentMethod = request.getParameter("paymentMethod");

            if ("VNPAY".equals(paymentMethod)) {
                String vnp_TmnCode = "IOGQJ94Z";
                String vnp_HashSecret = "GBJNFFG0MPLVPW5X2H892AO0WHLQUMGZ";
                String vnp_Url = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
                String vnp_Returnurl = "http://localhost:8080/vnpay-payment-return";

                Map<String, String> vnp_Params = new HashMap<>();
                vnp_Params.put("vnp_Version", "2.1.0");
                vnp_Params.put("vnp_Command", "pay");
                vnp_Params.put("vnp_TmnCode", vnp_TmnCode);

                // Số tiền: VNPay yêu cầu VNĐ và nhân 100. Ví dụ: 100$ * 25000 * 100
                long amount = pricePaid.multiply(new BigDecimal(2500000)).longValue();
                vnp_Params.put("vnp_Amount", String.valueOf(amount));
                vnp_Params.put("vnp_CurrCode", "VND");
                vnp_Params.put("vnp_TxnRef", String.valueOf(System.currentTimeMillis()));
                vnp_Params.put("vnp_OrderInfo", "Thanh toan khoa hoc:" + courseId);
                vnp_Params.put("vnp_OrderType", "other");
                vnp_Params.put("vnp_Locale", "vn");
                vnp_Params.put("vnp_ReturnUrl", vnp_Returnurl);
                vnp_Params.put("vnp_IpAddr", request.getRemoteAddr());

                Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
                SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
                vnp_Params.put("vnp_CreateDate", formatter.format(cld.getTime()));

                // Tạo URL và redirect
                String paymentUrl = VNPayUtil.getPaymentURL(vnp_Params, vnp_HashSecret);


                request.getSession().setAttribute("pendingEnrollment",
                        new CourseEnrollment(0, user.getId(), courseId, pricePaid, "VNPAY", null, false));

                response.sendRedirect(paymentUrl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
