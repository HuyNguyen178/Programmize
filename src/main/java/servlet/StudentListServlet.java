package servlet;

import dao.StudentDAO;
import model.Student;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/student-list")
public class StudentListServlet extends HttpServlet {

    private StudentDAO studentDAO;

    @Override
    public void init() throws ServletException {
        studentDAO = new StudentDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Lấy filter + search
        String keyword = request.getParameter("search");
        String status = request.getParameter("status");
        String className = request.getParameter("class");

        if (keyword != null && keyword.trim().isEmpty()) keyword = null;
        if (status != null && status.trim().isEmpty()) status = null;
        if (className != null && className.trim().isEmpty()) className = null;

        // Lấy danh sách student
        List<Student> students = studentDAO.searchStudents(keyword, status, className);

        // Đưa vào request
        request.setAttribute("students", students);
        request.setAttribute("search", keyword);
        request.setAttribute("status", status);
        request.setAttribute("className", className);

        // Forward đến JSP
        request.getRequestDispatcher("/WEB-INF/views/student-list.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
