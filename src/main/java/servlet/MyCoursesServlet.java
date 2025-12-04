package servlet;

import dao.MyCoursesDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Course;
import model.User;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/my-courses")
public class MyCoursesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // 1. Lấy userId từ session (bắt buộc phải login trước)
            User user = (User) request.getSession().getAttribute("user");
            Integer userId = user.getId();

            if (userId == null) {
                response.sendRedirect("login");
                return;
            }

            // 2. Lấy Connection
            Connection conn = (Connection) getServletContext().getAttribute("DBConnection");
            // Hoặc tự bạn lấy bằng DBConnect.getConnection();

            // 3. Gọi DAO
            MyCoursesDAO dao = new MyCoursesDAO(conn);
            List<Course> courses = dao.getEnrolledCoursesByUser(userId);

            // 4. Gửi sang JSP
            request.setAttribute("courses", courses);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        request.getRequestDispatcher("/WEB-INF/views/my-courses.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }
}
