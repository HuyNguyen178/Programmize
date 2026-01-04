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

import java.io.IOException;

@WebServlet("/blog/edit-poster/*")
public class EditPosterServlet extends HttpServlet {
    private PosterDAO posterDAO;
    private SettingDAO settingDAO;

    @Override
    public void init(ServletConfig config) throws ServletException {
        posterDAO = new PosterDAO();
        settingDAO = new SettingDAO();
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

        request.setAttribute("allCategories", settingDAO.getAllCategories());
        request.setAttribute("poster", poster);
        request.getRequestDispatcher("/WEB-INF/views/edit-poster.jsp").forward(request, response);
    }
}
