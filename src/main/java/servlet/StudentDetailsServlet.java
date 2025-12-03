package servlet;

import dao.StudentDAO;
import model.Student;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/student-detail")
public class StudentDetailsServlet extends HttpServlet {

    private StudentDAO studentDAO;

    @Override
    public void init() {
        studentDAO = new StudentDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");
        int studentId = -1;

        try {
            if (idParam != null) {
                studentId = Integer.parseInt(idParam);
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Student ID format.");
            return;
        }

        if (studentId > 0) {
            Student student = studentDAO.getStudentById(studentId);

            if (student != null) {
                request.setAttribute("student", student);
                request.getRequestDispatcher("/WEB-INF/views/student-details.jsp").forward(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Student not found.");
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/student-list");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}