package servlet;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import dao.SettingDAO;
import dao.UserDAO;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.Part;
import model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.CloudinaryUtil;
import utils.PasswordUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@WebServlet("/add-account")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 5 * 1024 * 1024,
        maxRequestSize = 10 * 1024 * 1024
)
public class AddAccountServlet extends HttpServlet {
    private UserDAO userDAO;
    private SettingDAO settingDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        settingDAO = new SettingDAO();
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<String> roles = settingDAO.getRoleNames();
        request.setAttribute("roles", roles);

        request.getRequestDispatcher("WEB-INF/views/account-detail.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String fullname = request.getParameter("fullname");
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String newRoleName = request.getParameter("roleName");
        boolean status = "1".equals(request.getParameter("status"));

        String avatarUrl = "assets/img/user_avt/admin_avatar.png";
        Part avatarPart = request.getPart("avatar");
        if (avatarPart != null && avatarPart.getSize() > 0) {

            String contentType = avatarPart.getContentType();
            if (!contentType.startsWith("image/")) {
                request.setAttribute("errorMsg", "Only image files can be accepted!");

                request.setAttribute("fullnameValue", fullname);
                request.setAttribute("emailValue", email);
                request.setAttribute("roleValue", newRoleName);
                request.setAttribute("statusValue", status ? "1" : "0");
                request.setAttribute("avatarUrlValue", avatarUrl);

                List<String> roles = settingDAO.getRoleNames();
                request.setAttribute("roles", roles);
                request.getRequestDispatcher("WEB-INF/views/account-detail.jsp").forward(request, response);
                return;
            }

            byte[] fileBytes = avatarPart.getInputStream().readAllBytes();

            Cloudinary cloudinary = CloudinaryUtil.getCloudinary();

            Map uploadResult = cloudinary.uploader().upload(
                    fileBytes,
                    ObjectUtils.asMap(
                            "folder", "user_avt",
                            "public_id", "user_" + "new_avt",
                            "overwrite", true,
                            "resource_type", "image"
                    )
            );

            avatarUrl = (String) uploadResult.get("secure_url");
        }

        if (userDAO.checkUserOrEmailExists(username) || userDAO.checkUserOrEmailExists(email)) {
            request.setAttribute("errorMsg", "Username or Email already exists.");

            request.setAttribute("fullnameValue", fullname);
            request.setAttribute("emailValue", email);
            request.setAttribute("roleValue", newRoleName);
            request.setAttribute("statusValue", status ? "1" : "0");
            request.setAttribute("avatarUrlValue", avatarUrl);

            List<String> roles = settingDAO.getRoleNames();
            request.setAttribute("roles", roles);
            request.getRequestDispatcher("WEB-INF/views/account-detail.jsp").forward(request, response);
            return;
        }

        if (!PasswordUtil.isValidPassword(password)) {
            request.setAttribute("errorMsg", "Password must be at least 8 characters, contain at least 1 uppercase, 1 lowercase and 1 special character!");

            request.setAttribute("fullnameValue", fullname);
            request.setAttribute("emailValue", email);
            request.setAttribute("roleValue", newRoleName);
            request.setAttribute("statusValue", status ? "1" : "0");
            request.setAttribute("avatarUrlValue", avatarUrl);

            List<String> roles = settingDAO.getRoleNames();
            request.setAttribute("roles", roles);
            request.getRequestDispatcher("WEB-INF/views/account-detail.jsp").forward(request, response);
            return;
        }

        User newUser = new User();
        newUser.setFullname(fullname);
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(password);
        newUser.setStatus(status);
        newUser.setAvatarUrl(avatarUrl);
        newUser.setRoleName(newRoleName);

        if (userDAO.addUser(newUser)) {
            request.setAttribute("addSuccess", true);

        } else {
            request.setAttribute("errorMsg", "Failed to add new account! A database error occurred.");
            request.setAttribute("addSuccess", false);

            request.setAttribute("fullnameValue", fullname);
            request.setAttribute("emailValue", email);
            request.setAttribute("roleValue", newRoleName);
            request.setAttribute("statusValue", status ? "1" : "0");
            request.setAttribute("avatarUrlValue", avatarUrl);
        }
        List<String> roles = settingDAO.getRoleNames();
        request.setAttribute("roles", roles);
        response.sendRedirect("account-list?status=success");
    }
}