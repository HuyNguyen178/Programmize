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
import utils.PosterUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/blog")
public class BlogServlet extends HttpServlet {
    private SettingDAO settingDAO;
    private PosterDAO posterDAO;
    private static final int POSTERS_PER_PAGE = 6;

    @Override
    public void init(ServletConfig config) throws ServletException {
        settingDAO = new SettingDAO();
        posterDAO = new PosterDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String categoryIdStr = request.getParameter("category");
        String keyword = request.getParameter("keyword");
        String pageStr = request.getParameter("page");
        Integer categoryId = null;
        if (categoryIdStr != null) {
            categoryId = Integer.parseInt(categoryIdStr);
        }

        int currentPage = 1;
        if (pageStr != null) {
            currentPage = Integer.parseInt(pageStr);
        }

        int totalPosters = posterDAO.countPoster(categoryId, keyword);
        int totalPages = (int) Math.ceil((double) totalPosters / POSTERS_PER_PAGE);

        List<Poster> posters = posterDAO.getAllPoster(categoryId, keyword, currentPage, POSTERS_PER_PAGE);
        List<Setting> allCategories = settingDAO.getAllCategories();
        List<Poster> popularPosters = posterDAO.getPopularPosters();
        Poster mostPopularPoster = posterDAO.getMostPopularPoster();

        Map<Integer, String> timeAgoMap = new HashMap<>();
        for (Poster p : posters) {
            timeAgoMap.put(
                    p.getPostId(),
                    PosterUtil.timeAgo(p.getPublishedAt())
            );
        }

        request.setAttribute("mostPopularPoster", mostPopularPoster);
        request.setAttribute("popularPosters", popularPosters);
        request.setAttribute("timeAgoMap", timeAgoMap);
        request.setAttribute("posters", posters);
        request.setAttribute("allCategories", allCategories);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.getRequestDispatcher("/WEB-INF/views/blog.jsp").forward(request, response);
    }
}
