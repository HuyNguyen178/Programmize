package servlet;

import dao.CourseDAO;
import model.Course;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

@WebServlet("/public-courses")
public class PublicCoursesServlet extends HttpServlet {
    private CourseDAO publicCourseDAO;
    private static final int COURSES_PER_PAGE = 12;

    @Override
    public void init() throws ServletException {
        publicCourseDAO = new CourseDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get filter parameters
        String keyword = request.getParameter("search");
        String category = request.getParameter("category");
        String pageParam = request.getParameter("page");
        String priceSort = request.getParameter("price");

        // Parse page number
        int page = 1;
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                page = Integer.parseInt(pageParam);
            } catch (Exception ignored) { }
        }

        int offset = (page - 1) * COURSES_PER_PAGE;

        // Get all active courses (status = "1") with filters
        // Note: categories can be either category IDs or category names
        List<Course> courses = publicCourseDAO.getPublicCourses(category, keyword, priceSort, offset, COURSES_PER_PAGE);

        // Calculate pagination
        int totalCourses = publicCourseDAO.countPublicCourses(category, keyword);
        int totalPages = (int) Math.ceil((double) totalCourses / COURSES_PER_PAGE);

        // Get all categories for filter dropdown
        // Using getAllCategoryNames() which returns List<String> for backward compatibility
        List<String> allCategories = publicCourseDAO.getAllCategoryNames();

        // Set attributes for JSP
        request.setAttribute("courses", courses);
        request.setAttribute("allCategories", allCategories);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalCourses", totalCourses);
        request.setAttribute("category", category);
        request.setAttribute("search", keyword);
        request.setAttribute("price", priceSort);


        // Forward to JSP
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/public-courses.jsp");
        dispatcher.forward(request, response);
    }
}