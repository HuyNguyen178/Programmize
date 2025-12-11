package servlet;

import dao.CourseDAO;
import model.Course;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/learning-enrollment")
public class LearningEnrollmentServlet extends HttpServlet {

    private CourseDAO courseDAO;

    @Override
    public void init() throws ServletException {
        courseDAO = new CourseDAO();
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Use service() to handle both GET (link) and POST (form button) requests

        // 1. Get the course ID sent from the previous page
        // (Check both "courseId" and "course" to be safe)
        String courseIdStr = req.getParameter("courseId");
        if (courseIdStr == null) {
            courseIdStr = req.getParameter("course");
        }

        // 2. Safety Check: If no ID, go back to course list
        if (courseIdStr == null || courseIdStr.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/public-courses");
            return;
        }

        try {
            int courseId = Integer.parseInt(courseIdStr);

            // 3. Get the specific course details
            Course selectedCourse = courseDAO.getCourseById(courseId);

            if (selectedCourse == null) {
                resp.sendRedirect(req.getContextPath() + "/public-courses");
                return;
            }

            // 4. Send the SINGLE course to the JSP
            req.setAttribute("course", selectedCourse);

            // Forward to the enrollment page
            req.getRequestDispatcher("/WEB-INF/views/learning-enrollment.jsp").forward(req, resp);

        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/public-courses");
        }
    }
}