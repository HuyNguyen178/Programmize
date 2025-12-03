package servlet;

import dao.PublicClassesDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.Class;
import java.io.IOException;
import java.util.List;

@WebServlet("/public-classes")
public class PublicClassesServlet extends HttpServlet {

    private PublicClassesDAO classDAO;

    @Override
    public void init() throws ServletException {
        classDAO = new PublicClassesDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String[] selectedCategories = request.getParameterValues("category");
        String keyword = request.getParameter("keyword");
        String priceSort = request.getParameter("price");

        List<Class> classes = classDAO.getActiveClasses(keyword, selectedCategories, priceSort);

        List<String> allCategories = classDAO.getAllCategories();

        request.setAttribute("classes", classes);
        request.setAttribute("allCategories", allCategories);
        request.setAttribute("selectedCategories", selectedCategories != null ? List.of(selectedCategories) : List.of());
        request.setAttribute("searchKeyword", keyword != null ? keyword : "");
        request.setAttribute("price", priceSort != null ? priceSort : "");

        request.getRequestDispatcher("/WEB-INF/views/public-classes.jsp").forward(request, response);
    }
}
