package servlet;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import dao.CourseDAO;
import dao.UserDAO;
import model.Course;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.annotation.MultipartConfig;
import utils.CloudinaryUtil;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

@WebServlet("/add-course")
@MultipartConfig(
    maxFileSize = 10485760,      // 10MB for images
    maxRequestSize = 20971520    // 20MB
)
public class AddCourseServlet extends HttpServlet {
    private CourseDAO courseDAO;
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        courseDAO = new CourseDAO();
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Load categories and instructors for the form
        request.setAttribute("allCategories", courseDAO.getAllCategoriesFromSettings());
        request.setAttribute("allInstructors", userDAO.getAllInstructors());
        request.getRequestDispatcher("WEB-INF/views/add-course.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            request.setCharacterEncoding("UTF-8");
            String courseName = request.getParameter("courseName");
            String description = request.getParameter("description");
            String statusStr = request.getParameter("status");
            String instructorIdStr = request.getParameter("instructorId");
            String durationStr = request.getParameter("duration");
            String listedPriceStr = request.getParameter("listedPrice");
            String salePriceStr = request.getParameter("salePrice");
            String[] categoryIdStrs = request.getParameterValues("categoryIds");

            // Parse status
            boolean status = "1".equals(statusStr) || "true".equalsIgnoreCase(statusStr);

            // Parse instructor ID
            int instructorId = 0;
            if (instructorIdStr != null && !instructorIdStr.trim().isEmpty()) {
                instructorId = Integer.parseInt(instructorIdStr);
            }

            // Parse duration
            int duration = 0;
            if (durationStr != null && !durationStr.trim().isEmpty()) {
                duration = Integer.parseInt(durationStr);
            }

            // Parse category IDs
            int[] categoryIds = null;
            if (categoryIdStrs != null && categoryIdStrs.length > 0) {
                categoryIds = new int[categoryIdStrs.length];
                for (int i = 0; i < categoryIdStrs.length; i++) {
                    categoryIds[i] = Integer.parseInt(categoryIdStrs[i]);
                }
            }

            // Parse prices
            BigDecimal listedPrice = new BigDecimal("0");
            BigDecimal salePrice = null;
            if (listedPriceStr != null && !listedPriceStr.trim().isEmpty()) {
                listedPrice = new BigDecimal(listedPriceStr);
            }
            if (salePriceStr != null && !salePriceStr.trim().isEmpty()) {
                salePrice = new BigDecimal(salePriceStr);
            }

            String thumbnailUrl = "/assets/img/user_avt/admin_avatar.png";
            Part thumbnailPart = request.getPart("thumbnailImg");

            if (thumbnailPart != null && thumbnailPart.getSize() > 0) {

                String contentType = thumbnailPart.getContentType();
                if (!contentType.startsWith("image/")) {
                    request.getSession().setAttribute("errorMessage", "Only image files are allowed!");
                    response.sendRedirect("add-course");
                    return;
                }

                byte[] fileBytes = thumbnailPart.getInputStream().readAllBytes();

                Cloudinary cloudinary = CloudinaryUtil.getCloudinary();

                Map uploadResult = cloudinary.uploader().upload(
                        fileBytes,
                        ObjectUtils.asMap(
                                "folder", "course_thumbnail",
                                "resource_type", "image"
                        )
                );

                thumbnailUrl = (String) uploadResult.get("secure_url");
            }

            // Create course object
            Course course = new Course();
            course.setCourseName(courseName);
            course.setDescription(description);
            course.setStatus(status);
            course.setInstructorId(instructorId);
            course.setThumbnailUrl(thumbnailUrl);
            course.setDuration(duration);
            course.setListedPrice(listedPrice);
            course.setSalePrice(salePrice);

            // Use the correct DAO method
            int courseId = courseDAO.addCourseWithCategories(course, categoryIds);

            if (courseId > 0) {
                response.sendRedirect(request.getContextPath() + "/course-list?success=added");
                request.getSession().setAttribute("successMessage", "Course added successfully!");
            } else {
                request.setAttribute("error", "Failed to add course");
                request.setAttribute("description", description);
                request.setAttribute("categories", courseDAO.getAllCategoriesFromSettings());
                request.setAttribute("instructors", courseDAO.getAllUsersAsInstructors());
                request.getRequestDispatcher("WEB-INF/views/add-course.jsp").forward(request, response);
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Invalid number format: " + e.getMessage());
            request.setAttribute("categories", courseDAO.getAllCategoriesFromSettings());
            request.setAttribute("instructors", courseDAO.getAllUsersAsInstructors());
            request.getRequestDispatcher("WEB-INF/views/add-course.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error adding course: " + e.getMessage());
            request.setAttribute("categories", courseDAO.getAllCategoriesFromSettings());
            request.setAttribute("instructors", courseDAO.getAllUsersAsInstructors());
            request.getRequestDispatcher("WEB-INF/views/add-course.jsp").forward(request, response);
        }
    }
}