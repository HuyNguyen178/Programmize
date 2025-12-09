package servlet;

import dao.ClassDAO;
import dao.CourseDAO;
import dao.UserDAO;
import model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/dashboard")
public class DashBoardServlet extends HttpServlet {

    private UserDAO userDAO;
    private CourseDAO courseDAO;
    private ClassDAO classDAO;
    // private OrderDAO orderDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        userDAO = new UserDAO();
        courseDAO = new CourseDAO();
        classDAO = new ClassDAO();
        // orderDAO = new OrderDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int totalUsers = userDAO.countTotalUsers(null, null, null);
        int totalCourses = courseDAO.getTotalCourses();
        int totalClasses = classDAO.getTotalClasses();

        List<User> recentUsers = userDAO.searchUsers(null, null, null, totalUsers - 3, 3);
        List<Object[]> topCourses = courseDAO.getTopSellingCourses(3);
        List<Object[]> topClasses = classDAO.getTopSellingClasses(3);
        List<Object[]> topInstructors = userDAO.getTopInstructorsByEnrollment(3);

        request.setAttribute("totalUsers", totalUsers);
        request.setAttribute("recentUsers", recentUsers);
        request.setAttribute("totalCourses", totalCourses);
        request.setAttribute("topCourses", topCourses);
        request.setAttribute("totalClasses", totalClasses);
        request.setAttribute("topClasses", topClasses);
        request.setAttribute("topInstructors", topInstructors);

        request.getRequestDispatcher("/WEB-INF/views/dashboard.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }
}