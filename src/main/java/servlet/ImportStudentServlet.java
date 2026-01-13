package servlet;

import dao.StudentDAO;
import dao.UserDAO;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import model.Student;
import org.apache.poi.ss.usermodel.*;
import utils.ExcelFileUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/import-student")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,    // 1MB
        maxFileSize = 5 * 1024 * 1024,       // 5MB
        maxRequestSize = 10 * 1024 * 1024    // 10MB
)

public class ImportStudentServlet extends HttpServlet {
    private StudentDAO studentDAO;
    private UserDAO userDAO;

    public void init(ServletConfig config) throws ServletException {
        studentDAO = new StudentDAO();
        userDAO = new UserDAO();

    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Part filePart = request.getPart("studentFile");
        List<String> errors = new ArrayList<>();
        int totalStudents = 0;
        int addedStudents = 0;

        if (filePart == null || filePart.getSize() == 0) {
            errors.add("No file chosen!");
            request.getSession().setAttribute("errors", errors);
            response.sendRedirect("student-list");
            return;
        }

        String fileName = filePart.getSubmittedFileName().toLowerCase();
        if (!fileName.endsWith(".xlsx")) {
            errors.add("Invalid file type. Please upload an Excel file!");
            request.getSession().setAttribute("errors", errors);
            response.sendRedirect("student-list");
            return;
        }

        try (Workbook workbook = WorkbookFactory.create(filePart.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            Row headerRow = sheet.getRow(1);
            if (headerRow == null) {
                errors.add("Excel file has no header row!");
                request.getSession().setAttribute("errors", errors);
                response.sendRedirect("student-list");
                return;
            }
            Map<String, Integer> indexMap = new HashMap<>();
            for (Cell cell : headerRow) {
                indexMap.put(cell.getStringCellValue().trim(), cell.getColumnIndex());
            }

            for (int i = 2; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                totalStudents++;

                String username_email = ExcelFileUtil.getCell(row, indexMap.get("username/email"));
                boolean checkUserOrEmailExists = userDAO.checkUserOrEmailExists(username_email);

                if (!checkUserOrEmailExists || username_email == null) {
                    errors.add("Cannot find username or email " + username_email + " at row " + (i + 1));
                    continue;
                }
                boolean isEmail = username_email.contains("@");

                String className = ExcelFileUtil.getCell(row, indexMap.get("classes"));
                boolean checkClass = true;
                if (className == null || className.isEmpty() && checkClass) {
                    errors.add("Cannot find className " + className + " at row " + (i + 1));
                    continue;
                }
                studentDAO.addStudentToClass(username_email, isEmail, className);
                addedStudents++;
            }
            if (!errors.isEmpty()) {
                request.getSession().setAttribute("errors", errors);
            }

            request.getSession().setAttribute("successMessage", "Imported successfully " + addedStudents + " of " + totalStudents + " Student(s)");
            response.sendRedirect("student-list");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("student-list?error=ImportFailed");
        }
    }
}
