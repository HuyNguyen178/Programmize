package servlet;

import dao.CourseDAO;
import dao.StudentDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Student;
import model.User;

import java.io.IOException;
import java.util.List;

@WebServlet("/course-students")
public class CourseStudentListServlet extends HttpServlet {
    private StudentDAO studentDAO;
    private CourseDAO courseDAO;
    private final int PAGE_SIZE = 10;

    @Override
    public void init() throws ServletException {
        studentDAO = new StudentDAO();
        courseDAO = new CourseDAO();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User loginUser = (User) session.getAttribute("loginUser");

        if (loginUser == null) {
            response.sendRedirect("login");
            return;
        }

        Integer courseId = Integer.parseInt(request.getParameter("courseId"));

        String courseName = courseDAO.getCourseById(courseId).getCourseName();
        String action = request.getParameter("action");
        String idParam = request.getParameter("id");
        String statusParam = request.getParameter("newStatus");
        String actionMessage = null;

        if ("toggleStatus".equals(action) && idParam != null && statusParam != null && courseId != null) {
            try {
                int studentId = Integer.parseInt(idParam);
                boolean newStatus = "1".equals(statusParam);
                boolean success = studentDAO.updateCourseStudentStatus(studentId, courseId, newStatus);

                if (success) {
                    String fullname = studentDAO.getFullnameById(studentId);
                    actionMessage = "Updated status for student: " + (fullname != null ? fullname : studentId);
                } else {
                    actionMessage = "Failed to update status.";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String keyword = request.getParameter("search");
        String status = request.getParameter("status");
        if (keyword != null && keyword.trim().isEmpty()) keyword = null;
        if (status != null && status.trim().isEmpty()) status = null;

        String pageIndexParam = request.getParameter("pageIndex");
        int pageIndex = 1;
        try {
            if (pageIndexParam != null) pageIndex = Integer.parseInt(pageIndexParam);
        } catch (NumberFormatException e) {
            pageIndex = 1;
        }


        int totalStudents = studentDAO.countStudentsByCourseId(keyword, status, courseId, loginUser.getId());
        int totalPage = (int) Math.ceil((double) totalStudents / PAGE_SIZE);
        if (totalPage <= 0) totalPage = 1;

        if (pageIndex > totalPage) pageIndex = totalPage;
        if (pageIndex < 1) pageIndex = 1;

        List<Student> students = studentDAO.searchStudentsByCourseId(keyword, status, courseId, pageIndex, PAGE_SIZE, loginUser.getId());


        request.setAttribute("students", students);
        request.setAttribute("search", keyword);
        request.setAttribute("status", status);
        request.setAttribute("courseId", courseId);
        request.setAttribute("courseName", courseName);
        request.setAttribute("pageIndex", pageIndex);
        request.setAttribute("totalPage", totalPage);
        request.setAttribute("actionMessage", actionMessage);

        request.getRequestDispatcher("/WEB-INF/views/course-students.jsp").forward(request, response);
    }
}
