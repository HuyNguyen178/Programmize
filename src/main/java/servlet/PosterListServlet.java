package servlet;

import dao.PosterDAO;
import dao.SettingDAO;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Poster;
import model.Setting;

import java.io.IOException;
import java.util.List;

@WebServlet("/poster-list")
public class PosterListServlet extends HttpServlet {
    private PosterDAO posterDAO;
    private SettingDAO settingDAO;

    @Override
    public void init(ServletConfig config) throws ServletException {
        posterDAO = new PosterDAO();
        settingDAO = new SettingDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String keyword = request.getParameter("search");
        String categoryIdStr = request.getParameter("category");
        Integer categoryId = null;
        if (categoryIdStr != null && !categoryIdStr.isEmpty()) {
            categoryId = Integer.parseInt(categoryIdStr);
        }

        String sortColumn = request.getParameter("sortColumn");
        String sortOrder = request.getParameter("sortOrder");

        List<Poster> posters = posterDAO.getAllPosters(keyword, categoryId, sortColumn, sortOrder);
        List<Setting> categories = settingDAO.getAllCategories();

        request.setAttribute("selectedCategoryId", categoryId);
        request.setAttribute("searchKeyword", keyword);
        request.setAttribute("categories", categories);
        request.setAttribute("posters", posters);
        request.setAttribute("sortOrder", sortOrder);
        request.setAttribute("sortColumn", sortColumn);
        request.getRequestDispatcher("/WEB-INF/views/poster-list.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer postId = Integer.parseInt(request.getParameter("postId"));
        Integer userId = Integer.parseInt(request.getParameter("userId"));
        posterDAO.deletePoster(postId, userId);

        response.sendRedirect("poster-list");
    }
}
