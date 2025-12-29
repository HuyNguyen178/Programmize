package servlet;

import dao.CourseDAO;
import model.Course;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.annotation.MultipartConfig;
import service.FileValidationService;
import service.FileValidationService.ValidationResult;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet("/edit-course")
@MultipartConfig(
    maxFileSize = 10485760,      // 10MB for images
    maxRequestSize = 20971520    // 20MB total
)
public class EditCourseServlet extends HttpServlet {
    private CourseDAO courseDAO;
    private FileValidationService fileValidator;

    @Override
    public void init() throws ServletException {
        courseDAO = new CourseDAO();
        fileValidator = FileValidationService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null) {
            response.sendRedirect(request.getContextPath() + "/courses");
            return;
        }

        int courseId = Integer.parseInt(idParam);
        Course course = courseDAO.getCourseById(courseId);

        List<String[]> allCategories = courseDAO.getAllCategoriesFromSettings();
        List<String[]> courseCategories = courseDAO.getCategoriesForCourse(courseId);
        List<String[]> instructors = courseDAO.getAllUsersAsInstructors();

        request.setAttribute("course", course);
        request.setAttribute("allCategories", allCategories);
        request.setAttribute("courseCategories", courseCategories);
        request.setAttribute("instructors", instructors);
        request.getRequestDispatcher("/views/admin/edit-course.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int courseId = Integer.parseInt(request.getParameter("courseId"));
            Course course = courseDAO.getCourseById(courseId);

            if (course == null) {
                response.sendRedirect(request.getContextPath() + "/courses?error=notfound");
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
            String thumbnailUrl = request.getParameter("thumbnailUrl");
            Part thumbnailPart = request.getPart("thumbnailFile");

            if (thumbnailPart != null && thumbnailPart.getSize() > 0) {
                String filename = thumbnailPart.getSubmittedFileName();
                String contentType = thumbnailPart.getContentType();
                long fileSize = thumbnailPart.getSize();

                ValidationResult validationResult = fileValidator.validate(
                    filename, contentType, fileSize, thumbnailPart.getInputStream()
                );

                if (!validationResult.isValid()) {
                    request.setAttribute("error", "Thumbnail upload failed: " + validationResult.getMessage());
                    request.getRequestDispatcher("/views/admin/edit-course.jsp").forward(request, response);
                    return;
                }

                String safeFilename = fileValidator.sanitizeFilename(filename);
                String uploadDir = getServletContext().getRealPath("/uploads/courses");
                File dir = new File(uploadDir);
                if (!dir.exists()) dir.mkdirs();

                thumbnailPart.write(uploadDir + File.separator + safeFilename);
                thumbnailUrl = "uploads/courses/" + safeFilename;
            } else if (thumbnailUrl == null || thumbnailUrl.trim().isEmpty()) {
                thumbnailUrl = course.getThumbnailUrl();
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
                response.sendRedirect(request.getContextPath() + "/courses?success=updated");
            } else {
                request.setAttribute("error", "Failed to update course");
                request.getRequestDispatcher("/views/admin/edit-course.jsp").forward(request, response);
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
            request.setAttribute("error", "Invalid number format: " + e.getMessage());
            request.getRequestDispatcher("/views/admin/edit-course.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error updating course: " + e.getMessage());
            request.getRequestDispatcher("/views/admin/edit-course.jsp").forward(request, response);
        }
    }
}