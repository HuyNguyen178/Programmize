package servlet;

import dao.ClassDAO;
import dao.SettingDAO;
import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Class;
import model.Setting;
import model.User;
import java.io.IOException;
import java.util.List;

@WebServlet("/class-list")
public class ClassListServlet extends HttpServlet {
    private ClassDAO classDAO;
    private UserDAO userDAO;
    private SettingDAO settingDAO;

    @Override
    public void init() throws ServletException {
        classDAO = new ClassDAO();
        userDAO = new UserDAO();
        settingDAO = new SettingDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String categoryIdStr= request.getParameter("category");
        String instructorIdStr = request.getParameter("instructor");
        String keyword = request.getParameter("search");
        String statusStr = request.getParameter("status");

        String action = request.getParameter("action");
        String idStr = request.getParameter("id");
        String newStatusStr = request.getParameter("newStatus");

        Integer categoryId = null;
        Integer instructorId = null;
        Boolean status = null;
        if (statusStr != null && !statusStr.isEmpty()) {
            status = statusStr.equals("1");
        }

        if (categoryIdStr != null && !categoryIdStr.isEmpty()) {
            categoryId = Integer.parseInt(categoryIdStr);
        }
        if (instructorIdStr != null && !instructorIdStr.isEmpty()) {
            instructorId = Integer.parseInt(instructorIdStr);
        }
        if (keyword != null && keyword.trim().isEmpty()) keyword = null;

        if ("toggleStatus".equals(action) && idStr != null && newStatusStr != null) {
            int classId = Integer.parseInt(idStr);
            boolean newStatus = newStatusStr.equals("1");

            classDAO.updateStatus(classId, newStatus);

            response.sendRedirect(request.getContextPath() + "/class-list");
            return;
        }

        List<Setting> categories = settingDAO.getAllCategories();

        List<User> instructors = userDAO.getAllInstructors();
        List<Class> classes = classDAO.getAllClasses(categoryId, instructorId, status, keyword);

        request.setAttribute("classes", classes);

        request.setAttribute("categories", categories);
        request.setAttribute("instructors", instructors);
        request.setAttribute("selectedCategoryId", categoryId);
        request.setAttribute("selectedInstructorId", instructorId);
        request.setAttribute("searchKeyword", keyword);
        request.setAttribute("selectedStatus", statusStr);

        request.getRequestDispatcher("/WEB-INF/views/class-list.jsp").forward(request, response);
    }
}
