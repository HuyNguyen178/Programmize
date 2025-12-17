package servlet;

import dao.ClassDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Class;
import model.User;

import java.io.IOException;
import java.util.List;

@WebServlet("/class-content")
public class ClassContentServlet extends HttpServlet {
    private ClassDAO classDAO;

    @Override
    public void init() throws ServletException {
        classDAO = new ClassDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String category = request.getParameter("category");
        String keyword = request.getParameter("search");

        if (category != null && category.trim().isEmpty()) {
            category = "";
        }
        if (keyword != null && keyword.trim().isEmpty()) {
            keyword = "";
        }

        User user = (User) request.getSession().getAttribute("loginUser");
        List<Class> classes = classDAO.getClassesByInstructor(user.getId(), category, keyword, 0, Integer.MAX_VALUE);
        List<String> allCategories = classDAO.getAllCategories();

        request.setAttribute("classes", classes);
        request.setAttribute("allCategories", allCategories);
        request.setAttribute("category", category);
        request.setAttribute("searchKeyword", keyword);

        request.getRequestDispatcher("/WEB-INF/views/class-content.jsp").forward(request, response);
    }
}
