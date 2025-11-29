package servlet;

import dao.CourseDAO;
import model.Course;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;

@WebServlet("/publicCourseDetails")
public class PublicCourseDetailsServlet extends HttpServlet {
    private CourseDAO courseDAO;

    @Override
    public void init() throws ServletException {
        courseDAO = new CourseDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get course ID from request parameter
        String courseIdStr = request.getParameter("id");

        // Validate course ID
        if (courseIdStr == null || courseIdStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/publicCourses");
            return;
        }

        try {
            int courseId = Integer.parseInt(courseIdStr);

            // Get course details from database
            // getCourseById now returns course with category names and instructor name from related tables
            Course course = courseDAO.getCourseById(courseId);

            // Check if course exists and is active (status = "1" for public)
            if (course == null || !"1".equals(course.getStatus())) {
                // Course not found or not public, redirect to public courses page
                request.setAttribute("errorMessage", "Course not found or not available");
                response.sendRedirect(request.getContextPath() + "/publicCourses");
                return;
            }

            // Calculate some additional display values
            String priceDisplay = getPriceDisplay(course);
            String durationDisplay = getDurationDisplay(course.getDuration());

            // Get enrollment count
            int enrollmentCount = courseDAO.getCourseEnrollmentCount(courseId);

            // Set attributes for JSP
            request.setAttribute("course", course);
            request.setAttribute("priceDisplay", priceDisplay);
            request.setAttribute("durationDisplay", durationDisplay);
            request.setAttribute("enrollmentCount", enrollmentCount);

            // Forward to JSP
            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/publicCourseDetails.jsp");
            dispatcher.forward(request, response);

        } catch (NumberFormatException e) {
            // Invalid course ID format
            response.sendRedirect(request.getContextPath() + "/publicCourses");
        }
    }

    /**
     * Helper method to format price display
     */
    private String getPriceDisplay(Course course) {
        if (course.getSalePrice() != null && course.getSalePrice().doubleValue() > 0) {
            return "$" + String.format("%.2f", course.getSalePrice());
        } else if (course.getListedPrice() != null && course.getListedPrice().doubleValue() > 0) {
            return "$" + String.format("%.2f", course.getListedPrice());
        } else {
            return "FREE";
        }
    }

    /**
     * Helper method to format duration display
     */
    private String getDurationDisplay(Integer totalMinutes) {
        if (totalMinutes == null || totalMinutes <= 0) {
            return "Self-paced";
        }

        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;

        if (hours > 0 && minutes > 0) {
            return hours + " hours " + minutes + " minutes";
        } else if (hours > 0) {
            return hours + " hours";
        } else {
            return minutes + " minutes";
        }
    }
}