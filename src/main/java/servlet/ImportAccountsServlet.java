package servlet;

import dao.SettingDAO;
import dao.UserDAO;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import model.Setting;
import model.User;
import utils.EmailUtil;
import utils.FileUtil;
import utils.PasswordUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/import-accounts")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,    // 1MB
        maxFileSize = 5 * 1024 * 1024,       // 5MB
        maxRequestSize = 10 * 1024 * 1024    // 10MB
)
public class ImportAccountsServlet extends HttpServlet {
    private UserDAO userDAO;
    private SettingDAO settingDAO;

    @Override
    public void init(ServletConfig config) throws ServletException {
        userDAO = new UserDAO();
        settingDAO = new SettingDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Part filePart = request.getPart("accountFile");

        if (filePart == null || filePart.getSize() == 0) {
            response.sendRedirect("account-list?error=NoFile");
            return;
        }

        String contentType = filePart.getContentType();
        if (!"text/csv".equals(contentType) && !"application/vnd.ms-excel".equals(contentType)) {
            request.getSession().setAttribute("errorMessage", "Invalid file type. Please upload a CSV file.");
            response.sendRedirect("account-list?success=false");
            return;
        }

        int totalAccount = 0;
        int successCount = 0;
        List<String> errors = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(filePart.getInputStream()))) {
            String headerLine = bufferedReader.readLine();
            String[] headers = headerLine.split(",");

            Map<String, Integer> indexMap = new HashMap<>();
            for (int i = 0; i < headers.length; i++) {
                indexMap.put(headers[i].trim(), i);
            }

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                totalAccount++;
                String[] data = line.split(",");

                User user = new User();
                String fullName = FileUtil.getValue(indexMap, data, "full_name");
                if (fullName == null) {
                    errors.add("Full name is null somewhere, please check your file again");
                    continue;
                }
                user.setFullname(fullName);

                String username = FileUtil.getValue(indexMap, data, "username");
                if (username == null) {
                    errors.add("Username is blank at user " + user.getFullname());
                    continue;
                }
                if (userDAO.checkUserOrEmailExists(username)) {
                    errors.add("Username " + username + " at account " + user.getFullname() + " has already existed!");
                    continue;
                }
                user.setUsername(username);

                String email = FileUtil.getValue(indexMap, data, "email");
                if (email == null) {
                    errors.add("Email is blank at user " + user.getFullname());
                    continue;
                }
                if (!EmailUtil.isValidEmail(email)) {
                    errors.add("Email is invalid at user " + user.getFullname());
                    continue;
                }
                if (userDAO.checkUserOrEmailExists(email)) {
                    errors.add("Email " + email + " at account " + user.getFullname() + " has already existed!");
                    continue;
                }
                user.setEmail(email);

                String roleName = FileUtil.getValue(indexMap, data, "role");
                if (roleName == null) {
                    errors.add("Role is blank at user " + user.getFullname());
                    continue;
                }

                String role = roleName.substring(0, 1).toUpperCase() + roleName.substring(1);
                Setting setting = settingDAO.findRoleByName(roleName);
                if (setting == null) {
                    errors.add("Cannot find role " + roleName + " at account " + user.getFullname());
                    continue;
                }
                user.setRoleName(role);

                String password = FileUtil.getValue(indexMap, data, "password");
                if (password == null) {
                    errors.add("Password is blank at user " + user.getFullname());
                    continue;
                }
                if (!PasswordUtil.isValidPassword(password)) {
                    errors.add("Invalid password at account " + user.getFullname());
                    continue;
                }
                user.setPassword(PasswordUtil.hash(password));

                String statusStr = FileUtil.getValue(indexMap, data, "status");
                if (!"true".equalsIgnoreCase(statusStr) && !"false".equalsIgnoreCase(statusStr)) {
                    errors.add("Status must be true or false at user " + user.getFullname());
                    continue;
                }
                user.setStatus(Boolean.parseBoolean(statusStr));

                userDAO.addUser(user);
                successCount++;
            }

            if (!errors.isEmpty()) {
                request.getSession().setAttribute("errors", errors);
            }
            request.getSession().setAttribute("successMessage", "Import successfully " + successCount + " of " + totalAccount + " account(s)");
            response.sendRedirect("account-list?success=true");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("account-list?error=ImportFailed");
        }
    }
}
