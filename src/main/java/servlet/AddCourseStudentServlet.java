package servlet;

import dao.CourseDAO;
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

@WebServlet("/add-course-student")
public class AddCourseStudentServlet extends HttpServlet {
    private StudentDAO studentDAO;
    private CourseDAO courseDAO;

    @Override
    public void init() {
        studentDAO = new StudentDAO();
        courseDAO = new CourseDAO();
    }

    protected void doGet (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User loginUser = (User) session.getAttribute("loginUser");
        List<String> courseNamesList = studentDAO.getAllCourseNames(loginUser.getId());
        request.setAttribute("courseNamesList", courseNamesList);
        String courseIdParam = request.getParameter("courseId");
        if (courseIdParam != null && !courseIdParam.isEmpty()) {
            try {
                int courseId = Integer.parseInt(courseIdParam);
                String targetName = courseDAO.getCourseById(courseId).getCourseName();
                request.setAttribute("targetCourseName", targetName);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        request.getRequestDispatcher("/WEB-INF/views/add-course-student.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User loginUser = (User) session.getAttribute("loginUser");

        String identifier = request.getParameter("identifier");
        String[] courseNames = request.getParameterValues("courses");
        String courseIdParam = request.getParameter("courseId");

        if (identifier == null || identifier.trim().isEmpty() || courseNames == null || courseNames.length == 0) {
            request.setAttribute("courseNamesList", studentDAO.getAllCourseNames(loginUser.getId()));
            request.setAttribute("message", "Username/Email and Course can't be empty.");
            request.getRequestDispatcher("/WEB-INF/views/add-course-student.jsp").forward(request, response);
            return;
        }

        boolean isEmail = identifier.contains("@");

        try {
            boolean success = studentDAO.addStudentToCourses(identifier.trim(), isEmail, courseNames);

            if (success) {
                String message = java.net.URLEncoder.encode("Student added to course successfully!", "UTF-8");
                String redirectUrl = request.getContextPath() + "/class-students?message=" + message;
                if (courseIdParam != null && !courseIdParam.isEmpty()) {
                    redirectUrl += "&courseId=" + courseIdParam;
                }
                response.sendRedirect(redirectUrl);
            } else {
                request.setAttribute("courseNamesList", studentDAO.getAllCourseNames(loginUser.getId()));
                request.setAttribute("message", "Could not add student. The user might not exist or is already enrolled in all selected classes.");
                request.getRequestDispatcher("/WEB-INF/views/add-course-student.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("courseNamesList", studentDAO.getAllCourseNames(loginUser.getId()));
            request.setAttribute("message", "Database error occurred.");
            request.getRequestDispatcher("/WEB-INF/views/add-student.jsp").forward(request, response);
        }
    }
}
