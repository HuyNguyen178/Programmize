package servlet;

import dao.UserDAO;
import model.User;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.annotation.MultipartConfig;
import service.FileValidationService;
import service.FileValidationService.ValidationResult;

import java.io.File;
import java.io.IOException;

@WebServlet("/edit-profile")
@MultipartConfig(
    maxFileSize = 5242880,       // 5MB for avatar
    maxRequestSize = 10485760    // 10MB
)
public class EditProfileServlet extends HttpServlet {
    private UserDAO userDAO;
    private FileValidationService fileValidator;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
        fileValidator = FileValidationService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        request.getRequestDispatcher("/views/edit-profile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");

        try {
            String fullname = request.getParameter("fullname");
            String email = request.getParameter("email");

            // Xử lý avatar upload với validation
            Part avatarPart = request.getPart("avatar");

            if (avatarPart != null && avatarPart.getSize() > 0) {
                String filename = avatarPart.getSubmittedFileName();
                String contentType = avatarPart.getContentType();
                long fileSize = avatarPart.getSize();

                // Only allow images for avatar
                if (!contentType.startsWith("image/")) {
                    request.setAttribute("error", "Avatar must be an image file (JPG, PNG, GIF)");
                    request.getRequestDispatcher("/views/edit-profile.jsp").forward(request, response);
                    return;
                }

                // Validate file
                ValidationResult validationResult = fileValidator.validate(
                    filename, contentType, fileSize, avatarPart.getInputStream()
                );

                if (!validationResult.isValid()) {
                    request.setAttribute("error", "Avatar upload failed: " + validationResult.getMessage());
                    request.getRequestDispatcher("/views/edit-profile.jsp").forward(request, response);
                    return;
                }

                // Delete old avatar if exists
                String oldAvatar = user.getAvatarUrl();
                if (oldAvatar != null && oldAvatar.startsWith("uploads/")) {
                    File oldFile = new File(getServletContext().getRealPath("/" + oldAvatar));
                    if (oldFile.exists()) oldFile.delete();
                }

                // Save new avatar
                String safeFilename = fileValidator.sanitizeFilename(filename);
                String uploadDir = getServletContext().getRealPath("/uploads/avatars");
                File dir = new File(uploadDir);
                if (!dir.exists()) dir.mkdirs();

                avatarPart.write(uploadDir + File.separator + safeFilename);
                user.setAvatarUrl("uploads/avatars/" + safeFilename);
            }

            // Update user info
            user.setFullname(fullname);
            user.setEmail(email);

            // Update trong database
            boolean success = userDAO.updateUser(user);

            if (success) {
                session.setAttribute("user", user);
                request.setAttribute("success", "Profile updated successfully!");
            } else {
                request.setAttribute("error", "Failed to update profile");
            }

            request.getRequestDispatcher("/views/edit-profile.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error updating profile: " + e.getMessage());
            request.getRequestDispatcher("/views/edit-profile.jsp").forward(request, response);
        }
    }
}