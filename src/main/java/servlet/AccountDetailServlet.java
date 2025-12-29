package servlet;

import dao.SettingDAO;
import dao.UserDAO;
import jakarta.servlet.http.Part;
import model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

@WebServlet("/account-detail")
public class AccountDetailServlet extends HttpServlet {

    private UserDAO userDAO;
    private SettingDAO settingDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
        settingDAO = new SettingDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String userIdParam = request.getParameter("id");

        List<String> roles = settingDAO.getRoleNames();
        request.setAttribute("roles", roles);

        if (userIdParam != null && !userIdParam.isBlank()) {
            try {
                int userId = Integer.parseInt(userIdParam);
                User user = userDAO.getUserById(userId);

                if (user != null) {
                    request.setAttribute("user", user);
                    request.getRequestDispatcher("WEB-INF/views/account-detail.jsp").forward(request, response);
                    return;
                }
            } catch (NumberFormatException ignored) {}
        }

        response.sendRedirect("account-list");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int userId = Integer.parseInt(request.getParameter("userId"));
        String fullname = request.getParameter("fullname");
        String email = request.getParameter("email");
        String username = request.getParameter("username");
        String roleName = request.getParameter("roleName");
        String password = request.getParameter("password");
        boolean status = "1".equals(request.getParameter("status"));

        String avatarUrl = request.getParameter("avatarUrl");

        Part avatarPart = request.getPart("avatar");
        if (avatarPart != null && avatarPart.getSize() > 0) {

            String contentType = avatarPart.getContentType();
            if (!contentType.startsWith("image/")) {
                forwardWithError(request, response, userId,
                        "Only img can be accepted!");
                response.sendRedirect("account-detail");
                return;
            }

            String uploadDir = getServletContext().getRealPath("/assets/img/user_avt");
            File uploadFolder = new File(uploadDir);
            if (!uploadFolder.exists()) {
                uploadFolder.mkdirs();
            }

            String fileName = Paths.get(avatarPart.getSubmittedFileName()).getFileName().toString();
            String ext = fileName.substring(fileName.lastIndexOf("."));
            String newFileName = "user_" + userId + "_" + System.currentTimeMillis() + ext;

            // Lưu file
            avatarPart.write(uploadDir + File.separator + newFileName);

            avatarUrl = "assets/img/user_avt/" + newFileName;
        }

        if (fullname == null || fullname.isBlank()
                || email == null || email.isBlank()
                || roleName == null || roleName.isBlank()) {

            forwardWithError(request, response, userId,
                    "Required fields must not be empty.");
            return;
        }

        if (password != null && !password.isBlank() && password.length() < 8) {
            forwardWithError(request, response, userId,
                    "Password must be at least 8 characters.");
            return;
        }

        User user = new User();
        user.setId(userId);
        user.setFullname(fullname);
        user.setUsername(username);
        user.setEmail(email);
        user.setRoleName(roleName);
        user.setStatus(status);
        user.setAvatarUrl(avatarUrl);

        if (password != null && !password.isBlank()) {
            user.setPassword(password);
        }

        if (userDAO.updateUser(user) || userDAO.updatePassword(user, password)) {
            response.sendRedirect("account-list?updated=true");
        } else {
            forwardWithError(request, response, userId,
                    "Update failed! Email may already exist.");
        }
    }

    private void forwardWithError(HttpServletRequest request,
                                  HttpServletResponse response,
                                  int userId,
                                  String errorMsg)
            throws ServletException, IOException {

        request.setAttribute("errorMsg", errorMsg);

        request.setAttribute("fullnameValue", request.getParameter("fullname"));
        request.setAttribute("emailValue", request.getParameter("email"));
        request.setAttribute("roleValue", request.getParameter("roleName"));
        request.setAttribute("statusValue", request.getParameter("status"));
        request.setAttribute("avatarUrlValue", request.getParameter("avatarUrl"));

        User user = userDAO.getUserById(userId);
        List<String> roles = settingDAO.getRoleNames();

        if (user != null) {
            request.setAttribute("user", user);
            request.setAttribute("roles", roles);
            request.getRequestDispatcher("WEB-INF/views/account-detail.jsp")
                    .forward(request, response);
        } else {
            response.sendRedirect("account-list?error=notfound");
        }
    }
}
