package servlet;

import dao.ClassDAO;
import dao.SettingDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.Class;
import model.Setting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/public-classes")
public class PublicClassesServlet extends HttpServlet {

    private ClassDAO classDAO;
    private SettingDAO settingDAO;
    private static final int CLASSES_PER_PAGE = 12;

    @Override
    public void init() throws ServletException {
        classDAO = new ClassDAO();
        settingDAO = new SettingDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String categoryIdStr = request.getParameter("category");
        String keyword = request.getParameter("keyword");
        String priceSort = request.getParameter("price");
        String pageParam = request.getParameter("page");

        Integer categoryId = null;
        if (categoryIdStr != null && !categoryIdStr.isEmpty()) {
            categoryId = Integer.parseInt(categoryIdStr);
        }

        int page = 1;
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                page = Integer.parseInt(pageParam);
            } catch (Exception ignored) { }
        }

        int offset = (page - 1) * CLASSES_PER_PAGE;

        List<Class> classes = classDAO.getActiveClasses(keyword, categoryId, priceSort, CLASSES_PER_PAGE, offset);

        int totalClasses = classDAO.countActiveClasses(keyword, categoryId);
        int totalPages = (int) Math.ceil((double) totalClasses / CLASSES_PER_PAGE);

        if (page > totalPages && totalPages > 0) {
            page = totalPages;
        }

        List<Setting> allCategories = settingDAO.getAllCategories();

        request.setAttribute("classes", classes);
        request.setAttribute("allCategories", allCategories);
        request.setAttribute("selectedCategoryId", categoryId != null ? categoryId : "");
        request.setAttribute("searchKeyword", keyword != null ? keyword : "");
        request.setAttribute("price", priceSort != null ? priceSort : "");
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalClasses", totalClasses);

        request.getRequestDispatcher("/WEB-INF/views/public-classes.jsp").forward(request, response);
    }
}
