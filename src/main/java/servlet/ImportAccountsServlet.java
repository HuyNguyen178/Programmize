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
                user.setFullname(data[indexMap.get("fullName")].trim());

                String username = data[indexMap.get("username")].trim();
                if (userDAO.checkUserOrEmailExists(username)) {
                    request.getSession().setAttribute("errorMessage", "Username " + username + " has already existed at account " + user.getFullname());
                    response.sendRedirect("account-list?success=false");
                    return;
                }
                user.setUsername(username);

                String email = data[indexMap.get("email")].trim();
                if (userDAO.checkUserOrEmailExists(email)) {
                    request.getSession().setAttribute("errorMessage", "Email " + email + " has already existed at account " + user.getFullname());
                    response.sendRedirect("account-list?success=false");
                    return;
                }
                user.setEmail(email);

                String roleName = data[indexMap.get("role")].trim();
                Setting setting = settingDAO.findRoleByName(roleName);
                if (setting == null) {
                    request.getSession().setAttribute("errorMessage", "Cannot find role " + roleName + " at account " + user.getFullname());
                    response.sendRedirect("account-list?success=false");
                    return;
                }
                user.setRoleName(roleName);

                String password = data[indexMap.get("password")].trim();
                if (!PasswordUtil.isValidPassword(password)) {
                    request.getSession().setAttribute("errorMessage", "Invalid password at account " + user.getFullname());
                    response.sendRedirect("account-list?success=false");
                    return;
                }
                user.setPassword(PasswordUtil.hash(password));

                user.setStatus(Boolean.parseBoolean(data[indexMap.get("status")].trim()));

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
