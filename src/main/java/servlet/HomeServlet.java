package servlet;

import dao.CourseDAO;
import model.Course;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

@WebServlet("/")
public class HomeServlet extends HttpServlet {
    private final Logger logger = LogManager.getLogger(HomeServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        CourseDAO courseDAO = new CourseDAO();

        List<Course> highlightedCourses = courseDAO.getHighlightedCourses();

        request.setAttribute("highlightedCourses", highlightedCourses);

        request.getRequestDispatcher("/WEB-INF/views/home.jsp").forward(request, response);
    }
}