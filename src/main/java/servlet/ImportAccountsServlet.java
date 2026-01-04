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
import utils.FileUtil;
import utils.PasswordUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
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

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(filePart.getInputStream()))) {
            String headerLine = bufferedReader.readLine();
            String[] headers = headerLine.split(",");

            Map<String, Integer> indexMap = new HashMap<>();
            for (int i = 0; i < headers.length; i++) {
                indexMap.put(headers[i].trim(), i);
            }

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                String[] data = line.split(",");

                User user = new User();
                String fullName = FileUtil.getValue(indexMap, data, "full_name");
                if (fullName == null) {
                    request.getSession().setAttribute("errorMessage", "Full name is null somewhere, please check your file again");
                    response.sendRedirect("account-list?success=false");
                    return;
                }
                user.setFullname(fullName);

                String username = FileUtil.getValue(indexMap, data, "username");
                if (username == null) {
                    request.getSession().setAttribute("errorMessage", "Username is null at user " + user.getFullname());
                    response.sendRedirect("account-list?success=false");
                    return;
                }
                if (userDAO.checkUserOrEmailExists(username)) {
                    request.getSession().setAttribute("errorMessage", "Username " + username + " has already existed at account " + user.getFullname());
                    response.sendRedirect("account-list?success=false");
                    return;
                }
                user.setUsername(username);

                String email = FileUtil.getValue(indexMap, data, "email");
                if (email == null) {
                    request.getSession().setAttribute("errorMessage", "Email is null at user " + user.getFullname());
                    response.sendRedirect("account-list?success=false");
                    return;
                }
                if (userDAO.checkUserOrEmailExists(email)) {
                    request.getSession().setAttribute("errorMessage", "Email " + email + " has already existed at account " + user.getFullname());
                    response.sendRedirect("account-list?success=false");
                    return;
                }
                user.setEmail(email);

                String roleName = FileUtil.getValue(indexMap, data, "role");
                if (roleName == null) {
                    request.getSession().setAttribute("errorMessage", "Role is null at user " + user.getFullname());
                    response.sendRedirect("account-list?success=false");
                    return;
                }

                Setting setting = settingDAO.findRoleByName(roleName);
                if (setting == null) {
                    request.getSession().setAttribute("errorMessage", "Cannot find role " + roleName + " at account " + user.getFullname());
                    response.sendRedirect("account-list?success=false");
                    return;
                }
                user.setRoleName(roleName);

                String password = FileUtil.getValue(indexMap, data, "password");
                if (password == null) {
                    request.getSession().setAttribute("errorMessage", "Password is null at user " + user.getFullname());
                    response.sendRedirect("account-list?success=false");
                    return;
                }
                if (!PasswordUtil.isValidPassword(password)) {
                    request.getSession().setAttribute("errorMessage", "Invalid password at account " + user.getFullname());
                    response.sendRedirect("account-list?success=false");
                    return;
                }
                user.setPassword(PasswordUtil.hash(password));

                user.setAvatarUrl(FileUtil.getValue(indexMap, data, "avatar_url"));
                user.setStatus(Boolean.parseBoolean(FileUtil.getValue(indexMap, data, "status")));

                userDAO.addUser(user);
            }

            request.getSession().setAttribute("successMessage", "Import successfully!");
            response.sendRedirect("account-list?success=true");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("account-list?error=ImportFailed");
        }
    }
}
