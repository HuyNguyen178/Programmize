package servlet;

import dao.ClassDAO;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Class;
import model.User;

import java.io.IOException;
import java.util.List;

@WebServlet("/my-classes")
public class MyClassesServlet extends HttpServlet {

    private ClassDAO classDAO;
    private static final int PAGE_SIZE = 12;

    @Override
    public void init(ServletConfig config) throws ServletException {
        classDAO = new ClassDAO();
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

        // Lấy parameter
        String keyword = request.getParameter("search");
        String category = request.getParameter("category");
        String pageParam = request.getParameter("page");

        if (keyword == null) keyword = "";
        if (category == null) category = "";

        // Page handling
        int page = 1;
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                page = Integer.parseInt(pageParam);
            } catch (Exception ignored) { }
        }

        int offset = (page - 1) * PAGE_SIZE;

        List<Class> classes = classDAO.getClassesByUserId(userId, category, keyword, offset, PAGE_SIZE);

        int totalClasses = classDAO.countClassesByUserId(userId, category, keyword);
        int totalPages = (int) Math.ceil((double) totalClasses / PAGE_SIZE);

        List<String> allCategories = classDAO.getAllCategories();

        request.setAttribute("allCategories", allCategories);
        request.setAttribute("classes", classes);
        request.setAttribute("category", category);
        request.setAttribute("keyword", keyword);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalClasses", totalClasses);

        request.getRequestDispatcher("/WEB-INF/views/my-classes.jsp")
                .forward(request, response);
    }
}
