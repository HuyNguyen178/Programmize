package servlet;

import dao.StudentDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/add-student")
public class AddStudentServlet extends HttpServlet {

    private StudentDAO studentDAO;

    @Override
    public void init() {
        studentDAO = new StudentDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User loginUser = (User) session.getAttribute("loginUser");
        List<String> classNames = studentDAO.getAllClassNames(loginUser.getId());
        request.setAttribute("classNames", classNames);
        // Hiển thị form thêm sinh viên (cần tạo file JSP mới: add-student.jsp)
        request.getRequestDispatcher("/WEB-INF/views/add-student.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String identifier = request.getParameter("identifier"); // Có thể là email hoặc username
        String className = request.getParameter("class"); // Tên lớp

        if (identifier == null || identifier.trim().isEmpty() || className == null || className.trim().isEmpty()) {
            request.setAttribute("message", "Username/Email và Class Name không được để trống.");
            request.getRequestDispatcher("/WEB-INF/views/add-student.jsp").forward(request, response);
            return;
        }

        // Giả định: Kiểm tra nếu có '@' thì là email
        boolean isEmail = identifier.contains("@");

        try {
            boolean success = studentDAO.addStudentToClass(identifier.trim(), isEmail, className.trim());

            if (success) {
                // Thêm thành công, redirect về trang danh sách
                response.sendRedirect(request.getContextPath() + "/student-list?message=Thêm sinh viên vào lớp thành công!");
            } else {
                // Không tìm thấy User hoặc Class
                request.setAttribute("message", "Lỗi: Không tìm thấy User hoặc Class với thông tin đã nhập.");
                request.getRequestDispatcher("/WEB-INF/views/add-student.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("message", "Lỗi database khi thêm sinh viên: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/add-student.jsp").forward(request, response);
        }
    }
}