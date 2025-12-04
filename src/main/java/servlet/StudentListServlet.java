package servlet;

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

@WebServlet("/student-list")
public class StudentListServlet extends HttpServlet {

    private StudentDAO studentDAO;
    private final int PAGE_SIZE = 10;

    @Override
    public void init() throws ServletException {
        studentDAO = new StudentDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User loginUser = (User) session.getAttribute("loginUser");
        // --- Xử lý hành động Cập nhật Trạng thái (Toggle Status) ---
        String action = request.getParameter("action");
        String idParam = request.getParameter("id");
        String statusParam = request.getParameter("newStatus");

        String actionMessage = null;

        if ("toggleStatus".equals(action) && idParam != null && statusParam != null) {
            try {
                int studentId = Integer.parseInt(idParam);
                boolean newStatus = "1".equals(statusParam);

                boolean success = studentDAO.updateStudentStatus(studentId, newStatus);

                String statusText = newStatus ? "Active" : "Inactive";
                String fullname = studentDAO.getFullnameById(studentId);

                actionMessage = success
                        ? "Status update successful: Student " + (fullname != null ? fullname : studentId) + " changed to " + statusText + "."
                        : "Error: Unable to update status for student " + studentId + ".";

            } catch (NumberFormatException e) {
                actionMessage = "Error: Invalid ID or state.";
            }
        }

        // --- Lấy filter + search ---
        String keyword = request.getParameter("search");
        String status = request.getParameter("status");
        String className = request.getParameter("class");

        if (keyword != null && keyword.trim().isEmpty()) keyword = null;
        if (status != null && status.trim().isEmpty()) status = null;
        if (className != null && className.trim().isEmpty()) className = null;

        // --- Xử lý Phân trang ---
        String pageIndexParam = request.getParameter("pageIndex");
        int pageIndex = 1;
        try {
            if (pageIndexParam != null) {
                pageIndex = Integer.parseInt(pageIndexParam);
            }
        } catch (NumberFormatException e) {
            pageIndex = 1;
        }

        int totalStudents = studentDAO.countStudents(keyword, status, className, loginUser.getId());
        int totalPage = (int) Math.ceil((double) totalStudents / PAGE_SIZE);

        if (totalStudents == 0) {
            totalPage = 1;
        }

        if (pageIndex > totalPage) {
            pageIndex = totalPage;
        } else if (pageIndex < 1) {
            pageIndex = 1;
        }

        List<Student> students = studentDAO.searchStudents(keyword, status, className, pageIndex, PAGE_SIZE, loginUser.getId());

        List<String> classNamesList = studentDAO.getAllClassNames(loginUser.getId());
        request.setAttribute("classNamesList", classNamesList);

        request.setAttribute("students", students);
        request.setAttribute("search", keyword);
        request.setAttribute("status", status);
        request.setAttribute("className", className);

        request.setAttribute("pageIndex", pageIndex);
        request.setAttribute("totalPage", totalPage);

        if (actionMessage != null) {
            request.setAttribute("actionMessage", actionMessage);
        } else {
            // Kiểm tra xem có thông báo từ action add-student không
            String paramMsg = request.getParameter("message");
            if (paramMsg != null && !paramMsg.isEmpty()) {
                request.setAttribute("actionMessage", paramMsg);
            }
        }


        request.getRequestDispatcher("/WEB-INF/views/student-list.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}