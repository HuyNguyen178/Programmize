package servlet;

import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.User;
import utils.PasswordUtil;
import utils.SessionConfig;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

@WebServlet("/profile")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 5 * 1024 * 1024,
        maxRequestSize = 10 * 1024 * 1024
)
public class UserProfileServlet extends HttpServlet {
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User loginUser = (User) session.getAttribute(SessionConfig.ATTR_LOGIN_USER);

        if (loginUser == null) {
            response.sendRedirect("login");
            return;
        }

        User user = userDAO.getUserById(loginUser.getId());

        if (user == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        request.setAttribute("user", user);
        request.getRequestDispatcher("/WEB-INF/views/user-profile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(SessionConfig.ATTR_LOGIN_USER);
        User u = userDAO.getUserById(user.getId());
        if (u == null) {
            response.sendRedirect("login");
            return;
        }

        String newUsername = request.getParameter("newUsername");
        String currentPassword = request.getParameter("password");
        if (newUsername != null && currentPassword != null) {
            if (!PasswordUtil.check(currentPassword, u.getPassword())) {
                session.setAttribute("modalError", "Incorrect password!");
                session.setAttribute("openModal", "username");
                session.setAttribute("success", false);
                response.sendRedirect("profile");
                return;
            }

            if (userDAO.checkUserOrEmailExists(newUsername)) {
                session.setAttribute("modalError", "Username has already existed!");
                session.setAttribute("openModal", "username");
                session.setAttribute("success", false);
                response.sendRedirect("profile");
                return;
            }

            user.setUsername(newUsername);
            userDAO.updateUser(user);
            session.setAttribute(SessionConfig.ATTR_LOGIN_USER, user);
            session.setAttribute("message", "Username updated successfully!");
            session.setAttribute("success", true);
            response.sendRedirect("profile");
            return;
        }

        String oldPass = request.getParameter("oldPass");
        String newPass = request.getParameter("newPass");
        String confirmPass = request.getParameter("confirmPass");
        if (newPass != null) {
            if (!PasswordUtil.check(oldPass, u.getPassword())) {
                session.setAttribute("modalError", "Incorrect password!");
                session.setAttribute("openModal", "password");
                session.setAttribute("success", false);
                response.sendRedirect("profile");
                return;
            }

            if (!newPass.equals(confirmPass)) {
                session.setAttribute("modalError", "Password do not match!");
                session.setAttribute("openModal", "password");
                session.setAttribute("success", false);
                response.sendRedirect("profile");
                return;
            }
            if (!PasswordUtil.isValidPassword(newPass)) {
                session.setAttribute("modalError", "Password must be at least 8 characters, contain at least 1 uppercase, 1 lowercase and 1 special character!");
                session.setAttribute("openModal", "password");
                session.setAttribute("success", false);
                response.sendRedirect("profile");
                return;
            }

            userDAO.updatePassword(user, newPass);
            session.setAttribute(SessionConfig.ATTR_LOGIN_USER, user);
            session.setAttribute("message", "Password updated successfully!");
            session.setAttribute("success", true);
            response.sendRedirect("profile");
            return;
        }

        boolean updated = false;

        String fullName = request.getParameter("fullname");
        if (fullName != null && !fullName.equals(user.getFullname())) {
            user.setFullname(fullName);
            updated = true;
        }

        Part avatarPart = request.getPart("avatar");
        if (avatarPart != null && avatarPart.getSize() > 0) {

            String contentType = avatarPart.getContentType();
            if (!contentType.startsWith("image/")) {
                session.setAttribute("message", "Only image files are allowed!");
                session.setAttribute("success", false);
                response.sendRedirect("profile");
                return;
            }

            String uploadDir = getServletContext().getRealPath("/assets/img/user_avt");
            File uploadFolder = new File(uploadDir);
            if (!uploadFolder.exists()) {
                uploadFolder.mkdirs();
            }

            String fileName = Paths.get(avatarPart.getSubmittedFileName()).getFileName().toString();
            String ext = fileName.substring(fileName.lastIndexOf("."));
            String newFileName = "user_" + user.getId() + "_" + System.currentTimeMillis() + ext;

            // Lưu file
            avatarPart.write(uploadDir + File.separator + newFileName);

            String avatarUrl = "assets/img/user_avt/" + newFileName;
            user.setAvatarUrl(avatarUrl);

            updated = true;
        }

        if (updated) {
            userDAO.updateUser(user);
            session.setAttribute(SessionConfig.ATTR_LOGIN_USER, user);
            session.setAttribute("message", "Updated successfully!");
            session.setAttribute("success", true);
            response.sendRedirect("profile");
        }
    }
}
