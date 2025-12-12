package servlet;

import dao.CourseDAO;
import model.Course;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet("/add-course")
public class AddCourseServlet extends HttpServlet {
    private CourseDAO publicCourseDAO;

    @Override
    public void init() throws ServletException {
        publicCourseDAO = new CourseDAO();
    }

    // GET: Hiển thị form add course
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Lấy danh sách categories và instructors cho dropdown
            List<String[]> allCategories = publicCourseDAO.getAllCategoriesFromSettings();
            List<String[]> allInstructors = publicCourseDAO.getAllUsersAsInstructors();

            // Set attributes cho JSP
            request.setAttribute("allCategories", allCategories);      // List of [setting_id, setting_name]
            request.setAttribute("allInstructors", allInstructors);    // List of [user_id, fullname]

            // Forward đến trang add
            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/add-course.jsp");
            dispatcher.forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/course-list");
        }
    }

    // POST: Xử lý submit form add (thêm vào database)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Lấy tất cả parameters từ form
            String courseName = request.getParameter("courseName");
            String thumbnailUrl = request.getParameter("thumbnailUrl");
            String description = request.getParameter("description");
            String status = request.getParameter("status");

            // Lấy instructor ID
            int instructorId = Integer.parseInt(request.getParameter("instructorId"));

            // Lấy category IDs (có thể chọn nhiều)
            String[] categoryIdStrings = request.getParameterValues("categoryIds");
            int[] categoryIds = null;
            if (categoryIdStrings != null && categoryIdStrings.length > 0) {
                categoryIds = new int[categoryIdStrings.length];
                for (int i = 0; i < categoryIdStrings.length; i++) {
                    categoryIds[i] = Integer.parseInt(categoryIdStrings[i]);
                }
            }

            // Lấy duration
            int duration = 0;
            String durationStr = request.getParameter("duration");
            if (durationStr != null && !durationStr.trim().isEmpty()) {
                duration = Integer.parseInt(durationStr);
            }

            // Parse prices
            BigDecimal listedPrice = new BigDecimal("0");
            BigDecimal salePrice = new BigDecimal("0");

            String listedPriceStr = request.getParameter("listedPrice");
            String salePriceStr = request.getParameter("salePrice");

            if (listedPriceStr != null && !listedPriceStr.trim().isEmpty()) {
                listedPrice = new BigDecimal(listedPriceStr);
            }
            if (salePriceStr != null && !salePriceStr.trim().isEmpty()) {
                salePrice = new BigDecimal(salePriceStr);
            }

            // Tạo Course object với dữ liệu mới
            Course course = new Course();
            course.setCourseName(courseName);
            course.setThumbnailUrl(thumbnailUrl);
            course.setDescription(description);
            course.setListedPrice(listedPrice);
            course.setSalePrice(salePrice);
            course.setStatus(Boolean.parseBoolean(status));
            course.setDuration(duration);
            course.setInstructorId(instructorId);

            // Gọi DAO để thêm vào database (bao gồm cả categories)
            int newCourseId = publicCourseDAO.addCourseWithCategories(course, categoryIds);

            if (newCourseId > 0) {
                // Nếu thêm thành công, set success message
                request.getSession().setAttribute("successMessage",
                        "Course '" + courseName + "' added successfully!");
            } else {
                // Nếu thêm thất bại, set error message
                request.getSession().setAttribute("errorMessage",
                        "Failed to add course. Please try again.");
            }

            // Redirect về course list
            response.sendRedirect(request.getContextPath() + "/course-list");

        } catch (NumberFormatException e) {
            // Nếu có lỗi parse number
            request.getSession().setAttribute("errorMessage",
                    "Invalid input format. Please check your data.");
            response.sendRedirect(request.getContextPath() + "/add-course");
        } catch (Exception e) {
            // Nếu có lỗi khác
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage",
                    "An error occurred: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/add-course");
        }
    }
}