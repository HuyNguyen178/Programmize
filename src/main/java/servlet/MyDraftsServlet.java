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
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/blog/my-drafts")
public class MyDraftsServlet extends HttpServlet {
    private PosterDAO posterDAO;

    @Override
    public void init(ServletConfig config) throws ServletException {
        posterDAO = new PosterDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        User user = (User) request.getSession().getAttribute(SessionConfig.ATTR_LOGIN_USER);
        List<Poster> allDrafts = posterDAO.getDraftsByUserId(user.getId(), keyword);
        int totalDrafts = posterDAO.getTotalDraftByUserId(user.getId());
        Timestamp lastUpdated = posterDAO.getLastUpdatedByUserId(user.getId());

        Map<Integer, String> timeAgoMap = new HashMap<>();
        for (Poster p : allDrafts) {
            timeAgoMap.put(
                    p.getPostId(),
                    PosterUtil.timeAgo(p.getCreatedAt())
            );
        }

        request.setAttribute("lastUpdated", lastUpdated);
        request.setAttribute("totalDrafts", totalDrafts);
        request.setAttribute("timeAgoMap", timeAgoMap);
        request.setAttribute("allDrafts", allDrafts);
        request.getRequestDispatcher("/WEB-INF/views/my-drafts.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute(SessionConfig.ATTR_LOGIN_USER);

        String action = request.getParameter("action");
        String postIdStr = request.getParameter("postId");

        if (action == null || postIdStr == null) {
            response.sendRedirect(request.getContextPath() + "/blog/my-drafts");
            return;
        }

        int postId = Integer.parseInt(postIdStr);

        switch (action) {
            case "publish":
                posterDAO.publishDraft(postId, user.getId());
                request.getSession().setAttribute("successMessage", "Published successfully!");
                break;

            case "delete":
                posterDAO.deleteDraft(postId, user.getId());
                request.getSession().setAttribute("successMessage", "Deleted successfully!");
                break;

            default:
                break;
        }

        response.sendRedirect(request.getContextPath() + "/blog/my-drafts");
    }
}
