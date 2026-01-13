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
        List<String> classNamesList = studentDAO.getAllClassNames(loginUser.getId());
        request.setAttribute("classNamesList", classNamesList);
        request.getRequestDispatcher("/WEB-INF/views/add-student.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String identifier = request.getParameter("identifier");
        String[] classNames = request.getParameterValues("classes");

        if (identifier == null || identifier.trim().isEmpty()
                || classNames == null || classNames.length == 0) {

            request.setAttribute("message", "Username/Email and Class can't be empty.");
            request.getRequestDispatcher("/WEB-INF/views/add-student.jsp").forward(request, response);
            return;
        }

        boolean isEmail = identifier.contains("@");

        try {
            boolean success = studentDAO.addStudentToClasses(
                    identifier.trim(),
                    isEmail,
                    classNames
            );

            if (success) {
                response.sendRedirect(
                        request.getContextPath() + "/student-list?success=true"
                );
            } else {
                request.setAttribute("message", "Error: Can't add student to class(es).");
                request.getRequestDispatcher("/WEB-INF/views/add-student.jsp").forward(request, response);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("message", "Database error occurred.");
            request.getRequestDispatcher("/WEB-INF/views/add-student.jsp").forward(request, response);
        }
    }
}