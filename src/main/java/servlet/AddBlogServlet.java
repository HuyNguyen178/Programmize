package servlet;

import dao.SettingDAO;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Setting;
import java.io.IOException;
import java.util.List;

@WebServlet("/add-blog")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,     // 1MB
        maxFileSize = 5 * 1024 * 1024,        // 5MB
        maxRequestSize = 10 * 1024 * 1024     // 10MB
)
public class AddBlogServlet extends HttpServlet {
    private SettingDAO settingDAO;

    @Override
    public void init(ServletConfig config) throws ServletException {
        settingDAO = new SettingDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Setting> allCategories = settingDAO.getAllCategories();
        request.setAttribute("allCategories", allCategories);
        request.getRequestDispatcher("/WEB-INF/views/add-blog.jsp").forward(request, response);
    }
}
