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
import org.apache.poi.ss.usermodel.*;
import utils.EmailUtil;
import utils.ExcelFileUtil;
import utils.PasswordUtil;

import java.io.IOException;
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
        List<String> errors = new ArrayList<>();
        int totalAccount = 0;
        int successCount = 0;

        if (filePart == null || filePart.getSize() == 0) {
            errors.add("No file chosen!");
            request.getSession().setAttribute("errors", errors);
            response.sendRedirect("account-list");
            return;
        }

        String fileName = filePart.getSubmittedFileName().toLowerCase();
        if (!fileName.endsWith(".xlsx")) {
            errors.add("Invalid file type. Please upload an Excel file!");
            request.getSession().setAttribute("errors", errors);
            response.sendRedirect("account-list");
            return;
        }

        try (Workbook workbook = WorkbookFactory.create(filePart.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                errors.add("Excel File has no header row!");
                request.getSession().setAttribute("errors", errors);
                response.sendRedirect("account-list");
                return;
            }

            Map<String, Integer> indexMap = new HashMap<>();
            for (Cell cell : headerRow) {
                indexMap.put(cell.getStringCellValue().trim(), cell.getColumnIndex());
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                totalAccount++;
                User user = new User();

                String fullName = ExcelFileUtil.getCell(row, indexMap.get("full_name"));
                if (fullName.isEmpty()) {
                    errors.add("Full name is blank at row " + (i + 1));
                    continue;
                }
                user.setFullname(fullName);

                String username = ExcelFileUtil.getCell(row, indexMap.get("username"));
                if (username.isEmpty()) {
                    errors.add("Username is blank at row " + (i + 1));
                    continue;
                }
                if (userDAO.checkUserOrEmailExists(username)) {
                    errors.add("Username at row " + (i + 1) + " has already existed!");
                    continue;
                }
                user.setUsername(username);

                String email = ExcelFileUtil.getCell(row, indexMap.get("email"));
                if (!EmailUtil.isValidEmail(email)) {
                    errors.add("Email is invalid at row " + (i + 1));
                    continue;
                }
                if (userDAO.checkUserOrEmailExists(email)) {
                    errors.add("Email at row " + (i + 1) + " has already existed!");
                    continue;
                }
                user.setEmail(email);

                String roleName = ExcelFileUtil.getCell(row, indexMap.get("role"));
                Setting setting = settingDAO.findRoleByName(roleName);
                if (setting == null) {
                    errors.add("Role not found at row " + (i + 1));
                    continue;
                }
                user.setRoleName(roleName.substring(0, 1).toUpperCase() + roleName.substring(1));

                String password = ExcelFileUtil.getCell(row, indexMap.get("password"));
                if (!PasswordUtil.isValidPassword(password)) {
                    errors.add("Invalid password at row " + (i + 1));
                    continue;
                }
                user.setPassword(PasswordUtil.hash(password));

                String statusStr = ExcelFileUtil.getCell(row, indexMap.get("status"));
                if (!"true".equalsIgnoreCase(statusStr) && !"false".equalsIgnoreCase(statusStr)) {
                    errors.add("Status must be true or false at row " + (i + 1));
                    continue;
                }
                user.setStatus(Boolean.parseBoolean(statusStr));

                userDAO.addUser(user);
                successCount++;
            }

            if (!errors.isEmpty()) {
                request.getSession().setAttribute("errors", errors);
            }

            request.getSession().setAttribute("successMessage","Import successfully " + successCount + " of " + totalAccount + " account(s)");
            response.sendRedirect("account-list");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("account-list?error=ImportFailed");
        }
    }
}
