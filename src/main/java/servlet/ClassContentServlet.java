package servlet;

import dao.ClassDAO;
import dao.SettingDAO;
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

@WebServlet("/class-content")
public class ClassContentServlet extends HttpServlet {
    private ClassDAO classDAO;
    private SettingDAO settingDAO;

    @Override
    public void init() throws ServletException {
        classDAO = new ClassDAO();
        settingDAO = new SettingDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String categoryIdStr = request.getParameter("category");
        String keyword = request.getParameter("search");
        String statusStr = request.getParameter("status");

        Integer categoryId = null;
        if (categoryIdStr != null && !categoryIdStr.trim().isEmpty()) {
            categoryId = Integer.parseInt(categoryIdStr);
        }
        if (keyword != null && keyword.trim().isEmpty()) {
            keyword = "";
        }

        Boolean status = null;
        if ("1".equals(statusStr)) {
            status = true;
        } else if ("0".equals(statusStr)) {
            status = false;
        }

        User user = (User) request.getSession().getAttribute("loginUser");
        List<Class> classes = classDAO.getClassContentByInstructor(user.getId(), categoryId, keyword, status);
        List<Setting> allCategories = settingDAO.getAllCategories();

        request.setAttribute("classes", classes);
        request.setAttribute("allCategories", allCategories);
        request.setAttribute("selectedCategoryId", categoryId);
        request.setAttribute("searchKeyword", keyword);
        request.setAttribute("selectedStatus", statusStr);

        request.getRequestDispatcher("/WEB-INF/views/class-content.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("deleteClass".equals(action)) {
            deleteClass(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/class-content");
        }
    }

    private void deleteClass(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        try {
            int classId = Integer.parseInt(request.getParameter("classId"));

            User user = (User) request.getSession().getAttribute("loginUser");
            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            boolean success = classDAO.deleteClassByInstructor(classId, user.getId());

            if (success) {
                request.getSession().setAttribute(
                        "successMessage", "Class deleted successfully!"
                );
            } else {
                request.getSession().setAttribute(
                        "errorMessage", "Delete failed or you don't have permission!"
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute(
                    "errorMessage", "An error occurred while deleting class."
            );
        }

        response.sendRedirect(request.getContextPath() + "/class-content");
    }
}
