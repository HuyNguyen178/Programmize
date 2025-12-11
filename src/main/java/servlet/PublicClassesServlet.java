package servlet;

import dao.ClassDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.Class;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/public-classes")
public class PublicClassesServlet extends HttpServlet {

    private ClassDAO classDAO;
    private static final int CLASSES_PER_PAGE = 12;

    @Override
    public void init() throws ServletException {
        classDAO = new ClassDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String category = request.getParameter("category");
        String keyword = request.getParameter("keyword");
        String priceSort = request.getParameter("price");
        String pageParam = request.getParameter("page");

        int page = 1;
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                page = Integer.parseInt(pageParam);
            } catch (Exception ignored) { }
        }

        int offset = (page - 1) * CLASSES_PER_PAGE;

        List<Class> classes = classDAO.getActiveClasses(keyword, category, priceSort, CLASSES_PER_PAGE, offset);

        int totalClasses = classDAO.countActiveClasses(keyword, category);
        int totalPages = (int) Math.ceil((double) totalClasses / CLASSES_PER_PAGE);

        if (page > totalPages && totalPages > 0) {
            page = totalPages;
        }

        List<String> allCategories = classDAO.getAllCategories();

        request.setAttribute("classes", classes);
        request.setAttribute("allCategories", allCategories);
        request.setAttribute("category", category != null ? category : "");
        request.setAttribute("searchKeyword", keyword != null ? keyword : "");
        request.setAttribute("price", priceSort != null ? priceSort : "");
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalClasses", totalClasses);

        request.getRequestDispatcher("/WEB-INF/views/public-classes.jsp").forward(request, response);
    }
}
