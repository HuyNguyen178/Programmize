package servlet;

import configuration.SessionConfig;
import dao.PosterDAO;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Poster;
import model.User;
import utils.PosterUtil;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/blog/my-posters")
public class MyPostersServlet extends HttpServlet {
    private PosterDAO posterDAO;

    @Override
    public void init(ServletConfig config) throws ServletException {
        posterDAO = new PosterDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        User user = (User) request.getSession().getAttribute(SessionConfig.ATTR_LOGIN_USER);
        List<Poster> allPosters = posterDAO.getPublishedPostersByUserId(user.getId(), keyword);
        int totalPosters = posterDAO.getTotalPublishedPosterByUserId(user.getId());

        Map<Integer, String> timeAgoMap = new HashMap<>();
        for (Poster p : allPosters) {
            timeAgoMap.put(
                    p.getPostId(),
                    PosterUtil.timeAgo(p.getPublishedAt())
            );
        }

        request.setAttribute("totalPosters", totalPosters);
        request.setAttribute("timeAgoMap", timeAgoMap);
        request.setAttribute("allPosters", allPosters);
        request.getRequestDispatcher("/WEB-INF/views/my-posters.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute(SessionConfig.ATTR_LOGIN_USER);

        String postIdStr = request.getParameter("postId");

        if (postIdStr == null) {
            response.sendRedirect(request.getContextPath() + "/blog/my-posters");
            return;
        }

        int postId = Integer.parseInt(postIdStr);

        posterDAO.deletePoster(postId, user.getId());
        request.getSession().setAttribute("successMessage", "Deleted successfully!");
        response.sendRedirect(request.getContextPath() + "/blog/my-posters");
    }
}
