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

@WebServlet("/add-course")
@MultipartConfig(
    maxFileSize = 10485760,      // 10MB for images
    maxRequestSize = 20971520    // 20MB
)
public class AddCourseServlet extends HttpServlet {
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
        // Load categories and instructors for the form
        request.setAttribute("categories", courseDAO.getAllCategoriesFromSettings());
        request.setAttribute("instructors", courseDAO.getAllUsersAsInstructors());
        request.getRequestDispatcher("WEB-INF/views/add-course.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Get form parameters
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
            BigDecimal listedPrice = null;
            BigDecimal salePrice = null;
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
                    request.setAttribute("categories", courseDAO.getAllCategoriesFromSettings());
                    request.setAttribute("instructors", courseDAO.getAllUsersAsInstructors());
                    request.getRequestDispatcher("WEB-INF/views/add-course.jsp").forward(request, response);
                    return;
                }

                String safeFilename = fileValidator.sanitizeFilename(filename);
                String uploadDir = getServletContext().getRealPath("/uploads/courses");
                File dir = new File(uploadDir);
                if (!dir.exists()) dir.mkdirs();

                thumbnailPart.write(uploadDir + File.separator + safeFilename);
                thumbnailUrl = "uploads/courses/" + safeFilename;
            }

            // Create course object
            Course course = new Course();
            course.setCourseName(courseName);
            course.setThumbnailUrl(thumbnailUrl);
            course.setDescription(description);
            course.setStatus(status);
            course.setInstructorId(instructorId);
            course.setDuration(duration);
            course.setListedPrice(listedPrice);
            course.setSalePrice(salePrice);

            // Use the correct DAO method
            int courseId = courseDAO.addCourseWithCategories(course, categoryIds);

            if (courseId > 0) {
                response.sendRedirect(request.getContextPath() + "/courses?success=added");
            } else {
                request.setAttribute("error", "Failed to add course");
                request.setAttribute("categories", courseDAO.getAllCategoriesFromSettings());
                request.setAttribute("instructors", courseDAO.getAllUsersAsInstructors());
                request.getRequestDispatcher("WEB-INF/views/add-course.jsp").forward(request, response);
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
            request.setAttribute("error", "Invalid number format: " + e.getMessage());
            request.setAttribute("categories", courseDAO.getAllCategoriesFromSettings());
            request.setAttribute("instructors", courseDAO.getAllUsersAsInstructors());
            request.getRequestDispatcher("WEB-INF/views/add-course.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error adding course: " + e.getMessage());
            request.setAttribute("categories", courseDAO.getAllCategoriesFromSettings());
            request.setAttribute("instructors", courseDAO.getAllUsersAsInstructors());
            request.getRequestDispatcher("WEB-INF/views/add-course.jsp").forward(request, response);
        }
    }
}