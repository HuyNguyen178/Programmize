package servlet;

import dao.PosterDAO;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Poster;
import utils.PosterUtil;

import java.io.IOException;
import java.util.*;

@WebServlet("/poster-details/*")
public class PosterDetailsServlet extends HttpServlet {
    private PosterDAO posterDAO;

    @Override
    public void init(ServletConfig config) throws ServletException {
        posterDAO = new PosterDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendRedirect(request.getContextPath() + "/blog");
            return;
        }
        String slug = pathInfo.substring(1);

        Poster poster = posterDAO.getPosterBySlug(slug);
        if (poster == null) {
            response.sendError(404);
            return;
        }

        HttpSession session = request.getSession();
        @SuppressWarnings("unchecked")
        Set<Integer> viewedPosters = (Set<Integer>) session.getAttribute("viewedPosters");
        if (viewedPosters == null) {
            viewedPosters = new HashSet<>();
        }

        if (!viewedPosters.contains(poster.getPostId())) {
            posterDAO.updateViewCountById(poster.getPostId());
            viewedPosters.add(poster.getPostId());
            session.setAttribute("viewedPosters", viewedPosters);
            poster.setViewCount(poster.getViewCount() + 1);
        }

        List<Poster> relatedPosters = posterDAO.getRelatedPosters(poster.getCategory().getId(), poster.getPostId());

        Map<Integer, String> timeAgoMap = new HashMap<>();
        for (Poster p : relatedPosters) {
            timeAgoMap.put(
                    p.getPostId(),
                    PosterUtil.timeAgo(p.getPublishedAt())
            );
        }

        request.setAttribute("poster", poster);
        request.setAttribute("relatedPosters", relatedPosters);
        request.setAttribute("timeAgoMap", timeAgoMap);
        request.getRequestDispatcher("/WEB-INF/views/poster-details.jsp").forward(request, response);
    }
}

