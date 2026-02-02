package servlet;

import dao.ClassDAO;
import dao.StudentDAO;
import jakarta.servlet.http.HttpSession;
import model.Student;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import java.io.IOException;
import java.util.List;

@WebServlet("/class-students")
public class ClassStudentListServlet extends HttpServlet {

    private StudentDAO studentDAO;
    private ClassDAO classDAO;
    private final int PAGE_SIZE = 10;

    @Override
    public void init() throws ServletException {
        studentDAO = new StudentDAO();
        classDAO = new ClassDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User loginUser = (User) session.getAttribute("loginUser");

        if (loginUser == null) {
            response.sendRedirect("login");
            return;
        }

        Integer classId = Integer.parseInt(request.getParameter("classId"));

        String className = classDAO.getClassById(classId).getName();
        String action = request.getParameter("action");
        String idParam = request.getParameter("id");
        String statusParam = request.getParameter("newStatus");
        String actionMessage = null;

        if ("toggleStatus".equals(action) && idParam != null && statusParam != null && classId != null) {
            try {
                int studentId = Integer.parseInt(idParam);
                boolean newStatus = "1".equals(statusParam);

                boolean success = studentDAO.updateClassStudentStatus(studentId, classId, newStatus);

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


        int totalStudents = studentDAO.countStudentsByClassId(keyword, status, classId, loginUser.getId());
        int totalPage = (int) Math.ceil((double) totalStudents / PAGE_SIZE);
        if (totalPage <= 0) totalPage = 1;

        if (pageIndex > totalPage) pageIndex = totalPage;
        if (pageIndex < 1) pageIndex = 1;

        List<Student> students = studentDAO.searchStudentsByClassId(keyword, status, classId, pageIndex, PAGE_SIZE);


        request.setAttribute("students", students);
        request.setAttribute("search", keyword);
        request.setAttribute("status", status);
        request.setAttribute("classId", classId);
        request.setAttribute("className", className);
        request.setAttribute("pageIndex", pageIndex);
        request.setAttribute("totalPage", totalPage);
        request.setAttribute("actionMessage", actionMessage);

        request.getRequestDispatcher("/WEB-INF/views/class-students.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}