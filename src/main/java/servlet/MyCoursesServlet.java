package servlet;

import dao.CourseDAO;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Course;
import model.User;

import java.io.IOException;
import java.util.List;

@WebServlet("/my-courses")
public class MyCoursesServlet extends HttpServlet {

    private CourseDAO courseDAO;
    private static final int PAGE_SIZE = 12;

    @Override
    public void init(ServletConfig config) throws ServletException {
        courseDAO = new CourseDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User user = (User) request.getSession().getAttribute("loginUser");
        if (user == null) {
            response.sendRedirect("login");
            return;
        }

        int userId = user.getId();

        String keyword = request.getParameter("search");
        String category = request.getParameter("category");
        String pageParam = request.getParameter("page");

        int page = 1;
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                page = Integer.parseInt(pageParam);
            } catch (Exception ignored) { }
        }

        int offset = (page - 1) * PAGE_SIZE;

        List<Course> courses = courseDAO.getEnrolledCoursesByUser(userId, category, keyword, offset, PAGE_SIZE);

        int totalCourses = courseDAO.countCoursesByUserId(userId, category, keyword);
        int totalPages = (int) Math.ceil((double) totalCourses / PAGE_SIZE);

        List<String[]> allCategories = courseDAO.getAllCategoriesFromSettings();

        request.setAttribute("allCategories", allCategories);
        request.setAttribute("courses", courses);
        request.setAttribute("category", category);
        request.setAttribute("search", keyword);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalCourses", totalCourses);

        request.getRequestDispatcher("/WEB-INF/views/my-courses.jsp")
                .forward(request, response);
    }
}
