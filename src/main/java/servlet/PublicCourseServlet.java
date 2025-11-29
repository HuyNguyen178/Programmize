package servlet;

import dao.CourseDAO;
import model.Course;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

@WebServlet("/publicCourses")
public class PublicCourseServlet extends HttpServlet {
    private CourseDAO courseDAO;
    private static final int COURSES_PER_PAGE = 16;

    @Override
    public void init() throws ServletException {
        courseDAO = new CourseDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get filter parameters
        String searchKeyword = request.getParameter("keyword");
        String[] categories = request.getParameterValues("category");
        String pageStr = request.getParameter("page");

        // Parse page number
        int currentPage = 1;
        if (pageStr != null && !pageStr.isEmpty()) {
            try {
                currentPage = Integer.parseInt(pageStr);
                if (currentPage < 1) currentPage = 1;
            } catch (NumberFormatException e) {
                currentPage = 1;
            }
        }

        // Get all active courses (status = "1") with filters
        // Note: categories can be either category IDs or category names
        List<Course> allCourses = courseDAO.getPublicCourses(searchKeyword, categories);

        // Calculate pagination
        int totalCourses = allCourses.size();
        int totalPages = (int) Math.ceil((double) totalCourses / COURSES_PER_PAGE);

        // Make sure current page doesn't exceed total pages
        if (currentPage > totalPages && totalPages > 0) {
            currentPage = totalPages;
        }

        // Get courses for current page
        int startIndex = (currentPage - 1) * COURSES_PER_PAGE;
        int endIndex = Math.min(startIndex + COURSES_PER_PAGE, totalCourses);

        List<Course> coursesForPage = new ArrayList<>();
        if (startIndex < totalCourses) {
            coursesForPage = allCourses.subList(startIndex, endIndex);
        }

        // Get all categories for filter dropdown
        // Using getAllCategoryNames() which returns List<String> for backward compatibility
        List<String> allCategories = courseDAO.getAllCategoryNames();

        // Set attributes for JSP
        request.setAttribute("courses", coursesForPage);
        request.setAttribute("allCategories", allCategories);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalCourses", totalCourses);
        request.setAttribute("selectedCategories", categories);
        request.setAttribute("searchKeyword", searchKeyword);

        // Forward to JSP
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/publicCourses.jsp");
        dispatcher.forward(request, response);
    }
}