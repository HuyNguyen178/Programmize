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
import model.User;
import service.FileValidationService;
import service.FileValidationService.ValidationResult;
import utils.CloudinaryUtil;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@WebServlet("/edit-course")
@MultipartConfig(
    maxFileSize = 10485760,      // 10MB for images
    maxRequestSize = 20971520    // 20MB total
)
public class EditCourseServlet extends HttpServlet {
    private CourseDAO courseDAO;
    private UserDAO userDAO;
    private FileValidationService fileValidator;

    @Override
    public void init() throws ServletException {
        courseDAO = new CourseDAO();
        userDAO = new UserDAO();
        fileValidator = FileValidationService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null) {
            response.sendRedirect(request.getContextPath() + "/course-list");
            return;
        }

        int courseId = Integer.parseInt(idParam);
        Course course = courseDAO.getCourseById(courseId);

        List<String[]> allCategories = courseDAO.getAllCategoriesFromSettings();
        List<String[]> courseCategories = courseDAO.getCategoriesForCourse(courseId);
        List<User> instructors = userDAO.getAllInstructors();

        request.setAttribute("course", course);
        request.setAttribute("allCategories", allCategories);
        request.setAttribute("courseCategories", courseCategories);
        request.setAttribute("allInstructors", instructors);
        request.getRequestDispatcher("WEB-INF/views/edit-course.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            request.setCharacterEncoding("UTF-8");
            int courseId = Integer.parseInt(request.getParameter("courseId"));
            Course course = courseDAO.getCourseById(courseId);

            if (course == null) {
                response.sendRedirect(request.getContextPath() + "/course-list?error=notfound");
                return;
            }

            String courseName = request.getParameter("courseName");
            String description = request.getParameter("description");
            boolean status = "1".equals(request.getParameter("status")) || "true".equals(request.getParameter("status"));

            int instructorId = 0;
            String instructorIdStr = request.getParameter("instructorId");
            if (instructorIdStr != null && !instructorIdStr.trim().isEmpty()) {
                instructorId = Integer.parseInt(instructorIdStr);
            }

            // Parse category IDs
            String[] categoryIdStrs = request.getParameterValues("categoryIds");
            int[] categoryIds = null;
            if (categoryIdStrs != null && categoryIdStrs.length > 0) {
                categoryIds = new int[categoryIdStrs.length];
                for (int i = 0; i < categoryIdStrs.length; i++) {
                    categoryIds[i] = Integer.parseInt(categoryIdStrs[i]);
                }
            }

            int duration = 0;
            String durationStr = request.getParameter("duration");
            if (durationStr != null && !durationStr.trim().isEmpty()) {
                duration = Integer.parseInt(durationStr);
            }

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

            // Handle thumbnail upload with validation
            String thumbnailUrl = course.getThumbnailUrl();
            Part thumbnailPart = request.getPart("thumbnailImg");

            if (thumbnailPart != null && thumbnailPart.getSize() > 0) {

                String contentType = thumbnailPart.getContentType();
                if (!contentType.startsWith("image/")) {
                    request.getSession().setAttribute("errorMessage", "Only image files are allowed!");
                    response.sendRedirect("edit-course");
                    return;
                }

                byte[] fileBytes = thumbnailPart.getInputStream().readAllBytes();

                Cloudinary cloudinary = CloudinaryUtil.getCloudinary();

                Map uploadResult = cloudinary.uploader().upload(
                        fileBytes,
                        ObjectUtils.asMap(
                                "folder", "course_thumbnail",
                                "public_id", "course_" + courseId,
                                "overwrite", true,
                                "resource_type", "image"
                        )
                );

                thumbnailUrl = (String) uploadResult.get("secure_url");
            }

            // Update course object
            course.setCourseName(courseName);
            course.setThumbnailUrl(thumbnailUrl);
            course.setDescription(description);
            course.setStatus(status);
            course.setInstructorId(instructorId);
            course.setDuration(duration);
            course.setListedPrice(listedPrice);
            course.setSalePrice(salePrice);

            // Use the correct DAO method
            boolean updated = courseDAO.updateCourseWithCategories(course, categoryIds);

            if (updated) {
                response.sendRedirect(request.getContextPath() + "/course-list?success=updated");
                request.getSession().setAttribute("successMessage", "Course updated successfully!");
            } else {
                request.setAttribute("error", "Failed to update course");
                request.getRequestDispatcher("WEB-INF/views/edit-course.jsp").forward(request, response);
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
            request.setAttribute("error", "Invalid number format: " + e.getMessage());
            request.getRequestDispatcher("WEB-INF/views/edit-course.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error updating course: " + e.getMessage());
            request.getRequestDispatcher("WEB-INF/views/edit-course.jsp").forward(request, response);
        }
    }
}