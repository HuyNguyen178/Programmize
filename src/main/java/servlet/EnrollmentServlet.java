package servlet;

import dao.ClassDAO;
import dao.CourseDAO;
import dao.EnrollmentDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ClassEnrollment;
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

@WebServlet("/enrollment")
public class EnrollmentServlet extends HttpServlet {
    private EnrollmentDAO enrollmentDAO;

    public void init() {
        this.enrollmentDAO = new EnrollmentDAO();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String type = request.getParameter("type");
        try {
            String idParam = request.getParameter("id");

            if (idParam != null && !idParam.isEmpty() && type != null && !type.isEmpty()) {
                int id = Integer.parseInt(idParam);
                Object item = null;
                if(type.equals("course")){
                    CourseDAO courseDAO = new CourseDAO();
                    item = courseDAO.getCourseById(id);
                } else if (type.equals("class")) {
                    ClassDAO classDAO = new ClassDAO();
                    item = classDAO.getClassById(id);
                }

                if (item != null) {
                    request.setAttribute("item", item);
                    request.setAttribute("type", type);
                    request.getRequestDispatcher("/WEB-INF/views/enrollment.jsp").forward(request, response);
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
            String type = request.getParameter("type");
            int id = Integer.parseInt(request.getParameter("id"));
            BigDecimal pricePaid = new BigDecimal(request.getParameter("pricePaid"));
            String paymentMethod = request.getParameter("paymentMethod");

            if ("FREE".equals(paymentMethod)) {
                if ("course".equals(type)) {
                    CourseDAO courseDAO = new CourseDAO();

                    // check if enrolled
                    if (courseDAO.isUserEnrolled(user.getId(), id)) {
                        request.getSession().setAttribute("message", "You are already enrolled in this course!");
                        response.sendRedirect(request.getContextPath() + "/my-courses");
                        return;
                    }

                    // free enroll
                    boolean success = courseDAO.enrollUserInCourse(user.getId(), id, 0.0, "FREE");

                    if (success) {
                        request.getSession().setAttribute("successMessage", "Successfully enrolled in the course!");
                        response.sendRedirect(request.getContextPath() + "/my-courses");
                    } else {
                        request.getSession().setAttribute("errorMessage", "Failed to enroll. Please try again.");
                        response.sendRedirect(request.getContextPath() + "/public-course-details?id=" + id);
                    }
                } else if ("class".equals(type)) {
                    ClassDAO classDAO = new ClassDAO();
                }
                return;
            }

            if ("VNPAY".equals(paymentMethod)) {
                String vnp_TmnCode = "IOGQJ94Z";
                String vnp_HashSecret = "GBJNFFG0MPLVPW5X2H892AO0WHLQUMGZ";
                String vnp_Returnurl = "http://localhost:8080/vnpay-payment-return";

                Map<String, String> vnp_Params = new HashMap<>();
                vnp_Params.put("vnp_Version", "2.1.0");
                vnp_Params.put("vnp_Command", "pay");
                vnp_Params.put("vnp_TmnCode", vnp_TmnCode);

                long amount = pricePaid.multiply(new BigDecimal(100)).longValue();
                vnp_Params.put("vnp_Amount", String.valueOf(amount));
                vnp_Params.put("vnp_CurrCode", "VND");
                vnp_Params.put("vnp_TxnRef", String.valueOf(System.currentTimeMillis()));
                vnp_Params.put("vnp_OrderInfo", "Thanh toan " + type + ":" + id);
                vnp_Params.put("vnp_OrderType", "other");
                vnp_Params.put("vnp_Locale", "vn");
                vnp_Params.put("vnp_ReturnUrl", vnp_Returnurl);
                vnp_Params.put("vnp_IpAddr", request.getRemoteAddr());

                SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
                vnp_Params.put("vnp_CreateDate", formatter.format(Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7")).getTime()));

                if ("class".equals(type)) {
                    request.getSession().setAttribute("pendingEnrollment", new ClassEnrollment(0, user.getId(), id, pricePaid, "VNPAY", null, false));
                } else if("course".equals(type)) {
                    request.getSession().setAttribute("pendingEnrollment", new CourseEnrollment(0, user.getId(), id, pricePaid, "VNPAY", null, false));
                }
                request.getSession().setAttribute("enrollmentType", type);

                response.sendRedirect(VNPayUtil.getPaymentURL(vnp_Params, vnp_HashSecret));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
