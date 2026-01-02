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

    @Override
    public void init(ServletConfig config) throws ServletException {
        settingDAO = new SettingDAO();
        posterDAO = new PosterDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Poster> posters = posterDAO.getAllPoster();
        List<Setting> allCategories = settingDAO.getAllCategories();
        Map<Integer, String> timeAgoMap = new HashMap<>();

        for (Poster p : posters) {
            timeAgoMap.put(
                    p.getPostId(),
                    PosterUtil.timeAgo(p.getPublishedAt())
            );
        }
        request.setAttribute("timeAgoMap", timeAgoMap);
        request.setAttribute("posters", posters);
        request.setAttribute("allCategories", allCategories);
        request.getRequestDispatcher("/WEB-INF/views/blog.jsp").forward(request, response);
    }
}
