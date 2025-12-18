package servlet;

import dao.ClassDAO;
import dao.SettingDAO;
import jakarta.servlet.ServletConfig;
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

@WebServlet("/my-classes")
public class MyClassesServlet extends HttpServlet {

    private ClassDAO classDAO;
    private SettingDAO settingDAO;
    private static final int PAGE_SIZE = 12;

    @Override
    public void init(ServletConfig config) throws ServletException {
        classDAO = new ClassDAO();
        settingDAO = new SettingDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User user = (User) request.getSession().getAttribute("loginUser");
        if (user == null) {
            response.sendRedirect("login");
            return;
        }

        int userId = user.getId();

        String keyword = request.getParameter("search");
        String categoryIdStr = request.getParameter("category");
        String pageParam = request.getParameter("page");

        Integer categoryId = null;
        if (categoryIdStr != null && !categoryIdStr.isEmpty()) {
            categoryId = Integer.parseInt(categoryIdStr);
        }

        if (keyword == null) keyword = "";

        int page = 1;
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                page = Integer.parseInt(pageParam);
            } catch (Exception ignored) { }
        }

        int offset = (page - 1) * PAGE_SIZE;

        List<Class> classes;
        int totalClasses;
        String roleName = user.getRoleName();

        if ("Instructor".equalsIgnoreCase(roleName)) {
            classes = classDAO.getClassesByInstructor(userId, categoryId, keyword, offset, PAGE_SIZE);
            totalClasses = classDAO.countClassesByInstructor(userId, categoryId, keyword);
        } else {
            // Học viên: Xem lớp đã ghi danh
            classes = classDAO.getClassesByUserId(userId, categoryId, keyword, offset, PAGE_SIZE);
            totalClasses = classDAO.countClassesByUserId(userId, categoryId, keyword);
        }
        // ----------------------------

        int totalPages = (int) Math.ceil((double) totalClasses / PAGE_SIZE);

        List<Setting> allCategories = settingDAO.getAllCategories();

        request.setAttribute("allCategories", allCategories);
        request.setAttribute("classes", classes);
        request.setAttribute("category", categoryId);
        request.setAttribute("keyword", keyword);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalClasses", totalClasses);

        request.getRequestDispatcher("/WEB-INF/views/my-classes.jsp")
                .forward(request, response);
    }
}