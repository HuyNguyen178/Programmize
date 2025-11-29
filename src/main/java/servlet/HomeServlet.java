package servlet;

import dao.HomeDAO;
import model.Course;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

// Map servlet này với đường dẫn gốc hoặc /home
@WebServlet(name = "HomeServlet", urlPatterns = {"/home", ""})
public class HomeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HomeDAO homeDAO = new HomeDAO();

        // 1. Lấy danh sách khóa học
        List<Course> highlightedCourses = homeDAO.getHighlightedCourses();

        // 2. Đẩy dữ liệu vào request attribute
        request.setAttribute("highlightedCourses", highlightedCourses);

        // 3. Forward về trang home.jsp
        // Lưu ý: Đường dẫn này phụ thuộc vào cấu trúc thư mục của bạn.
        // Dựa vào code JSP cũ, tôi giả định home.jsp nằm trong thư mục views
        request.getRequestDispatcher("/WEB-INF/views/home.jsp").forward(request, response);
    }
}