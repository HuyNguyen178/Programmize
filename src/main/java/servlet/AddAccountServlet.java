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

@WebServlet("/add-account")
@MultipartConfig(
    maxFileSize = 5242880,       // 5MB for avatar
    maxRequestSize = 10485760    // 10MB
)
public class AddAccountServlet extends HttpServlet {
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
        request.getRequestDispatcher("WEB-INF/views/account-detail.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            String email = request.getParameter("email");
            String fullname = request.getParameter("fullname");
            String roleName = request.getParameter("roleName");
            String statusStr = request.getParameter("status");

            // Kiểm tra username hoặc email đã tồn tại
            if (userDAO.checkUserOrEmailExists(username) || userDAO.checkUserOrEmailExists(email)) {
                request.setAttribute("error", "Username or email already exists");
                request.getRequestDispatcher("WEB-INF/views/account-detail.jsp").forward(request, response);
                return;
            }

            // Tạo user mới
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(password); // UserDAO.addUser() sẽ hash password
            newUser.setEmail(email);
            newUser.setFullname(fullname);
            newUser.setRoleName(roleName);
            newUser.setStatus(statusStr != null && statusStr.equals("1"));

            // Xử lý avatar upload với validation
            Part avatarPart = request.getPart("avatar");

            if (avatarPart != null && avatarPart.getSize() > 0) {
                String filename = avatarPart.getSubmittedFileName();
                String contentType = avatarPart.getContentType();
                long fileSize = avatarPart.getSize();

                // Only allow images for avatar
                if (!contentType.startsWith("image/")) {
                    request.setAttribute("error", "Avatar must be an image file (JPG, PNG, GIF)");
                    request.getRequestDispatcher("WEB-INF/views/account-detail.jsp").forward(request, response);
                    return;
                }

                // Validate file
                ValidationResult validationResult = fileValidator.validate(
                    filename, contentType, fileSize, avatarPart.getInputStream()
                );

                if (!validationResult.isValid()) {
                    request.setAttribute("error", "Avatar upload failed: " + validationResult.getMessage());
                    request.getRequestDispatcher("WEB-INF/views/account-detail.jsp").forward(request, response);
                    return;
                }

                // Save avatar
                String safeFilename = fileValidator.sanitizeFilename(filename);
                String uploadDir = getServletContext().getRealPath("/uploads/avatars");
                File dir = new File(uploadDir);
                if (!dir.exists()) dir.mkdirs();

                avatarPart.write(uploadDir + File.separator + safeFilename);
                newUser.setAvatarUrl("uploads/avatars/" + safeFilename);
            }

            // Insert vào database
            boolean success = userDAO.addUser(newUser);

            if (success) {
                response.sendRedirect(request.getContextPath() + "/accounts?success=added");
            } else {
                request.setAttribute("error", "Failed to create account");
                request.getRequestDispatcher("WEB-INF/views/account-detail.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error creating account: " + e.getMessage());
            request.getRequestDispatcher("WEB-INF/views/account-detail.jsp").forward(request, response);
        }
    }
}