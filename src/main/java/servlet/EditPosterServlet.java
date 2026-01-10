package servlet;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import configuration.SessionConfig;
import dao.PosterDAO;
import dao.SettingDAO;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.Poster;
import model.Setting;
import model.User;
import utils.CloudinaryUtil;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Map;

@WebServlet("/blog/edit-poster/*")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 5 * 1024 * 1024,
        maxRequestSize = 10 * 1024 * 1024
)
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
        User user = (User) request.getSession().getAttribute(SessionConfig.ATTR_LOGIN_USER);

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String slug = request.getPathInfo().substring(1);
        Poster poster = posterDAO.getPosterByUserIdAndSlug(user.getId(), slug);

        if (poster == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        request.setAttribute("allCategories", settingDAO.getAllCategories());
        request.setAttribute("poster", poster);
        request.getRequestDispatcher("/WEB-INF/views/edit-poster.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String slug = request.getPathInfo().substring(1);
        Poster poster = posterDAO.getPosterBySlug(slug);

        String title = request.getParameter("title");
        String excerpt = request.getParameter("excerpt");
        String content = request.getParameter("content");
        boolean newStatus = poster.isStatus();
        if (!poster.isStatus()) {
            String statusParam = request.getParameter("status");
            if (statusParam != null) {
                newStatus = "1".equals(statusParam);
            }
        }
        int categoryId = Integer.parseInt(request.getParameter("categoryId"));
        boolean removeThumbnail = "true".equals(request.getParameter("removeThumbnail"));
        Part thumbnailPart = request.getPart("thumbnail");

        if (title == null || title.isBlank()
                || excerpt == null || excerpt.isBlank()
                || content == null || content.isBlank()) {

            request.setAttribute("errorMessage", "All required fields must be filled!");
            request.setAttribute("title", title);
            request.setAttribute("excerpt", excerpt);
            request.setAttribute("content", content);
            request.setAttribute("categoryId", categoryId);
            request.setAttribute("allCategories", settingDAO.getAllCategories());
            request.getRequestDispatcher("/WEB-INF/views/edit-poster.jsp").forward(request, response);
            return;
        }

        String thumbnailUrl = poster.getThumbnailUrl();

        if (removeThumbnail) {
            thumbnailUrl = null;
        }

        // Upload ảnh mới
        if (thumbnailPart != null && thumbnailPart.getSize() > 0) {
            if (!thumbnailPart.getContentType().startsWith("image/")) {
                request.getSession().setAttribute("errorMessage", "Only image files are allowed!");
                response.sendRedirect(request.getRequestURI());
                return;
            }

            Cloudinary cloudinary = CloudinaryUtil.getCloudinary();
            byte[] bytes = thumbnailPart.getInputStream().readAllBytes();

            Map uploadResult = cloudinary.uploader().upload(
                    bytes,
                    ObjectUtils.asMap(
                            "folder", "blog_thumbnail",
                            "public_id", "poster_" + poster.getPostId(),
                            "overwrite", true,
                            "resource_type", "image"
                    )
            );

            thumbnailUrl = (String) uploadResult.get("secure_url");
        }

        Setting category = settingDAO.findById(categoryId);

        String newSlug = poster.getSlug();
        if (!title.equals(poster.getTitle())) {
            newSlug = posterDAO.generateUniqueSlug(title);
        }

        poster.setTitle(title);
        poster.setSlug(newSlug);
        poster.setExcerpt(excerpt);
        poster.setContent(content);
        poster.setThumbnailUrl(thumbnailUrl);
        poster.setCategory(category);
        poster.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        if (!poster.isStatus() && newStatus) {
            poster.setPublishedAt(new Timestamp(System.currentTimeMillis()));
        }
        poster.setStatus(newStatus);

        posterDAO.updatePoster(poster);

        request.getSession().setAttribute("successMessage", "Updated successfully!");
        response.sendRedirect(request.getContextPath() + "/blog/edit-poster/" + poster.getSlug());
    }
}
