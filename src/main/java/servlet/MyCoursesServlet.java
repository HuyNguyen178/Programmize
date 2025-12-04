package servlet;

import dao.CourseDAO;
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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Lấy userId từ session (bắt buộc phải login trước)
        User user = (User) request.getSession().getAttribute("user");

        if (user == null) {
            response.sendRedirect("login");
            return;
        }

        Integer userId = user.getId();

        // 2. Gọi DAO (CourseDAO tự quản lý Connection qua DBUtil)
        CourseDAO dao = new CourseDAO();
        List<Course> courses = dao.getEnrolledCoursesByUser(userId);

        // 3. Gửi sang JSP
        request.setAttribute("courses", courses);

        request.getRequestDispatcher("/WEB-INF/views/my-courses.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }
}