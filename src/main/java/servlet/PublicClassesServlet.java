package servlet;

import dao.ClassDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.Class;
import model.Course;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/public-classes")
public class PublicClassesServlet extends HttpServlet {

    private ClassDAO classDAO;
    private static final int CLASSES_PER_PAGE = 16;

    @Override
    public void init() throws ServletException {
        classDAO = new ClassDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String[] selectedCategories = request.getParameterValues("category");
        String keyword = request.getParameter("keyword");
        String priceSort = request.getParameter("price");
        String pageStr = request.getParameter("page");

        int currentPage = 1;
        if (pageStr != null && !pageStr.isEmpty()) {
            try {
                currentPage = Integer.parseInt(pageStr);
                if (currentPage < 1) currentPage = 1;
            } catch (NumberFormatException e) {
                currentPage = 1;
            }
        }

        List<Class> classes = classDAO.getActiveClasses(keyword, selectedCategories, priceSort);

        int totalClasses = classes.size();
        int totalPages = (int) Math.ceil((double) totalClasses / CLASSES_PER_PAGE);

        // Make sure current page doesn't exceed total pages
        if (currentPage > totalPages && totalPages > 0) {
            currentPage = totalPages;
        }

        int startIndex = (currentPage - 1) * CLASSES_PER_PAGE;
        int endIndex = Math.min(startIndex + CLASSES_PER_PAGE, totalClasses);

        List<Class> classesForPage = new ArrayList<>();
        if (startIndex < totalClasses) {
            classesForPage = classes.subList(startIndex, endIndex);
        }

        List<String> allCategories = classDAO.getAllCategories();

        request.setAttribute("classes", classesForPage);
        request.setAttribute("allCategories", allCategories);
        request.setAttribute("selectedCategories", selectedCategories != null ? List.of(selectedCategories) : List.of());
        request.setAttribute("searchKeyword", keyword != null ? keyword : "");
        request.setAttribute("price", priceSort != null ? priceSort : "");
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalClasses", totalClasses);

        request.getRequestDispatcher("/WEB-INF/views/public-classes.jsp").forward(request, response);
    }
}
