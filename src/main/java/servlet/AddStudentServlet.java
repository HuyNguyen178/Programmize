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
        String classIdParam = request.getParameter("classId");
        if (classIdParam != null && !classIdParam.isEmpty()) {
            try {
                int classId = Integer.parseInt(classIdParam);
                String targetName = studentDAO.getClassNameById(classId);
                request.setAttribute("targetClassName", targetName);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        request.getRequestDispatcher("/WEB-INF/views/add-student.jsp").forward(request, response);
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User loginUser = (User) session.getAttribute("loginUser");

        String identifier = request.getParameter("identifier");
        String[] classNames = request.getParameterValues("classes");
        String classIdParam = request.getParameter("classId");

        if (identifier == null || identifier.trim().isEmpty() || classNames == null || classNames.length == 0) {
            request.setAttribute("classNamesList", studentDAO.getAllClassNames(loginUser.getId()));
            request.setAttribute("message", "Username/Email and Class can't be empty.");
            request.getRequestDispatcher("/WEB-INF/views/add-student.jsp").forward(request, response);
            return;
        }

        boolean isEmail = identifier.contains("@");

        try {
            boolean success = studentDAO.addStudentToClasses(identifier.trim(), isEmail, classNames);

            if (success) {
                String message = java.net.URLEncoder.encode("Student added to class successfully!", "UTF-8");
                String redirectUrl = request.getContextPath() + "/student-list?message=" + message;
                if (classIdParam != null && !classIdParam.isEmpty()) {
                    redirectUrl += "&classId=" + classIdParam;
                }
                response.sendRedirect(redirectUrl);
            } else {
                request.setAttribute("classNamesList", studentDAO.getAllClassNames(loginUser.getId()));
                request.setAttribute("message", "Could not add student. The user might not exist or is already enrolled in all selected classes.");
                request.getRequestDispatcher("/WEB-INF/views/add-student.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("classNamesList", studentDAO.getAllClassNames(loginUser.getId()));
            request.setAttribute("message", "Database error occurred.");
            request.getRequestDispatcher("/WEB-INF/views/add-student.jsp").forward(request, response);
        }
    }
}