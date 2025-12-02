package servlet;

import dao.ClassDAO;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Class;
import java.io.IOException;
import java.util.List;

@WebServlet("/my-classes")
public class MyClassesServlet extends HttpServlet {
    private ClassDAO classDAO;
    private static final int PAGE_SIZE = 6;

    @Override
    public void init(ServletConfig config) throws ServletException {
        classDAO = new ClassDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer userId = (Integer) request.getSession().getAttribute("userId");
        if (userId == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String search = request.getParameter("search");
        String statusParam = request.getParameter("status");
        String pageParam = request.getParameter("page");

        Integer status = null;
        if (statusParam != null && !statusParam.isEmpty()) {
            status = Integer.parseInt(statusParam);
        }

        int page = 1;
        if (pageParam != null && !pageParam.isEmpty()) {
            page = Integer.parseInt(pageParam);
        }
        int offset = (page - 1) * PAGE_SIZE;

        List<Class> classes = classDAO.getClassesByUserId(userId, status, search, offset, PAGE_SIZE);

        request.setAttribute("classes", classes);
        request.setAttribute("search", search);
        request.setAttribute("status", status);
        request.setAttribute("currentPage", page);

        request.getRequestDispatcher("/WEB-INF/views/my-classes.jsp").forward(request, response);
    }
}

