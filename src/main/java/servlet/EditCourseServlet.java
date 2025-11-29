package servlet;

import dao.CourseDAO;
import model.Course;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet("/editCourse")
public class EditCourseServlet extends HttpServlet {
    private CourseDAO courseDAO;

    @Override
    public void init() throws ServletException {
        courseDAO = new CourseDAO();
    }

    // GET: Hiển thị form edit với dữ liệu course hiện tại
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Lấy course ID từ parameter
            String idParam = request.getParameter("id");

            if (idParam == null || idParam.trim().isEmpty()) {
                // Nếu không có ID, redirect về course list
                response.sendRedirect(request.getContextPath() + "/courseList");
                return;
            }

            int courseId = Integer.parseInt(idParam);

            // Lấy thông tin course từ database
            Course course = courseDAO.getCourseById(courseId);

            if (course == null) {
                // Nếu không tìm thấy course, redirect về course list với error message
                request.getSession().setAttribute("errorMessage", "Course not found!");
                response.sendRedirect(request.getContextPath() + "/courseList");
                return;
            }

            // Lấy danh sách categories và instructors cho dropdown
            List<String[]> allCategories = courseDAO.getAllCategoriesFromSettings();
            List<String[]> allInstructors = courseDAO.getAllUsersAsInstructors();

            // Lấy categories hiện tại của course
            List<String[]> courseCategories = courseDAO.getCategoriesForCourse(courseId);

            // Set attributes cho JSP
            request.setAttribute("course", course);
            request.setAttribute("allCategories", allCategories);      // List of [setting_id, setting_name]
            request.setAttribute("allInstructors", allInstructors);    // List of [user_id, fullname]
            request.setAttribute("courseCategories", courseCategories); // Current categories of this course

            // Forward đến trang edit
            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/editCourse.jsp");
            dispatcher.forward(request, response);

        } catch (NumberFormatException e) {
            // Nếu ID không hợp lệ
            request.getSession().setAttribute("errorMessage", "Invalid course ID!");
            response.sendRedirect(request.getContextPath() + "/courseList");
        }
    }

    // POST: Xử lý submit form edit (cập nhật database)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Lấy tất cả parameters từ form
            int courseId = Integer.parseInt(request.getParameter("courseId"));
            String courseName = request.getParameter("courseName");
            String thumbnailUrl = request.getParameter("thumbnailUrl");
            String description = request.getParameter("description");
            String status = request.getParameter("status");

            // Lấy instructor ID (thay vì instructor name)
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
            BigDecimal listedPrice = new BigDecimal(request.getParameter("listedPrice"));
            BigDecimal salePrice = new BigDecimal(request.getParameter("salePrice"));

            // Tạo Course object với dữ liệu mới
            Course course = new Course();
            course.setCourseId(courseId);
            course.setCourseName(courseName);
            course.setThumbnailUrl(thumbnailUrl);
            course.setDescription(description);
            course.setListedPrice(listedPrice);
            course.setSalePrice(salePrice);
            course.setStatus(status);
            course.setDuration(duration);
            course.setInstructorId(instructorId);

            // Gọi DAO để update database (bao gồm cả categories)
            boolean success = courseDAO.updateCourseWithCategories(course, categoryIds);

            if (success) {
                // Nếu update thành công, set success message
                request.getSession().setAttribute("successMessage",
                        "Course '" + courseName + "' updated successfully!");
            } else {
                // Nếu update thất bại, set error message
                request.getSession().setAttribute("errorMessage",
                        "Failed to update course. Please try again.");
            }

            // Redirect về course list
            response.sendRedirect(request.getContextPath() + "/courseList");

        } catch (NumberFormatException e) {
            // Nếu có lỗi parse number
            request.getSession().setAttribute("errorMessage",
                    "Invalid input format. Please check your data.");
            response.sendRedirect(request.getContextPath() + "/courseList");
        } catch (Exception e) {
            // Nếu có lỗi khác
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage",
                    "An error occurred: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/courseList");
        }
    }
}