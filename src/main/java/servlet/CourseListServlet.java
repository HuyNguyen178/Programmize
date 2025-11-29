package servlet;

import dao.CourseDAO;
import model.Course;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;
import java.util.List;

@WebServlet("/courseList")
public class CourseListServlet extends HttpServlet {
    private CourseDAO courseDAO;

    @Override
    public void init() throws ServletException {
        courseDAO = new CourseDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get filter parameters
        String category = request.getParameter("category");       // category_id from setting table
        String instructor = request.getParameter("instructor");   // user_id from user table
        String status = request.getParameter("status");
        String searchKeyword = request.getParameter("search");
        String sortColumn = request.getParameter("sortColumn");
        String sortOrder = request.getParameter("sortOrder");

        // Get courses based on filters
        List<Course> courses = courseDAO.getAllCourses(category, instructor,
                status, searchKeyword,
                sortColumn, sortOrder);

        // Get filter options - now returns List<String[]> with [id, name]
        List<String[]> categories = courseDAO.getAllCategoriesFromSettings(); // Get all categories from settings
        List<String[]> instructors = courseDAO.getAllInstructors();

        // Set attributes for JSP
        request.setAttribute("courses", courses);
        request.setAttribute("categories", categories);       // List of [setting_id, setting_name]
        request.setAttribute("instructors", instructors);     // List of [user_id, fullname]
        request.setAttribute("selectedCategory", category);
        request.setAttribute("selectedInstructor", instructor);
        request.setAttribute("selectedStatus", status);
        request.setAttribute("searchKeyword", searchKeyword);

        // Forward to JSP
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/courseList.jsp");
        dispatcher.forward(request, response);
    }
}