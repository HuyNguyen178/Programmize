package servlet;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@WebServlet("/create-poster")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,     // 1MB
        maxFileSize = 5 * 1024 * 1024,        // 5MB
        maxRequestSize = 10 * 1024 * 1024     // 10MB
)
public class CreatePosterServlet extends HttpServlet {
    private SettingDAO settingDAO;
    private PosterDAO posterDAO;

    @Override
    public void init(ServletConfig config) throws ServletException {
        settingDAO = new SettingDAO();
        posterDAO = new PosterDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Setting> allCategories = settingDAO.getAllCategories();
        request.setAttribute("allCategories", allCategories);
        request.getRequestDispatcher("/WEB-INF/views/create-poster.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("loginUser") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String title = request.getParameter("title");
        String excerpt = request.getParameter("excerpt");
        String content = request.getParameter("content");
        String statusParam = request.getParameter("status");
        boolean status = "1".equals(statusParam);
        int categoryId = Integer.parseInt(request.getParameter("categoryId"));
        Part thumbnailPart = request.getPart("thumbnail");

        if (title == null || title.isBlank()
                || excerpt == null || excerpt.isBlank()
                || content == null || content.isBlank()
                || thumbnailPart == null || thumbnailPart.getSize() == 0) {

            request.setAttribute("errorMessage", "All fields are required!");
            request.setAttribute("title", title);
            request.setAttribute("excerpt", excerpt);
            request.setAttribute("content", content);
            request.setAttribute("status", status);
            request.setAttribute("categoryId", categoryId);
            request.setAttribute("allCategories", settingDAO.getAllCategories());

            request.getRequestDispatcher("/WEB-INF/views/create-poster.jsp").forward(request, response);
            return;
        }

        String thumbnailUrl = "/assets/img/user_avt/admin_avatar.png";
        if (thumbnailPart != null && thumbnailPart.getSize() > 0) {

            String contentType = thumbnailPart.getContentType();
            if (!contentType.startsWith("image/")) {
                request.getSession().setAttribute("errorMessage", "Only image files are allowed!");
                response.sendRedirect("create-blog");
                return;
            }

            byte[] fileBytes = thumbnailPart.getInputStream().readAllBytes();

            Cloudinary cloudinary = CloudinaryUtil.getCloudinary();

            Map uploadResult = cloudinary.uploader().upload(
                    fileBytes,
                    ObjectUtils.asMap(
                            "folder", "blog_thumbnail",
                            "resource_type", "image"
                    )
            );

            thumbnailUrl = (String) uploadResult.get("secure_url");
        }

        Setting category = settingDAO.findById(categoryId);
        String slug = posterDAO.generateUniqueSlug(title);

        Poster poster = new Poster();
        poster.setTitle(title);
        poster.setExcerpt(excerpt);
        poster.setContent(content);
        poster.setSlug(slug);
        poster.setThumbnailUrl(thumbnailUrl);
        poster.setStatus(status);
        poster.setCategory(category);
        poster.setViewCount(0);
        if (status) {
            poster.setPublishedAt(new Timestamp(System.currentTimeMillis()));
        } else {
            poster.setPublishedAt(null);
        }
        poster.setUser(user);
        poster.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        poster.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        posterDAO.createPoster(poster);
        response.sendRedirect("blog");
    }
}
