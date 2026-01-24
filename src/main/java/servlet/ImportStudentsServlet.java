package servlet;

import configuration.SessionConfig;
import dao.ClassDAO;
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
import model.Class;
import model.User;
import org.apache.poi.ss.usermodel.*;
import utils.ExcelFileUtil;

import java.io.IOException;
import java.util.*;

@WebServlet("/import-students")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 5 * 1024 * 1024,
        maxRequestSize = 10 * 1024 * 1024
)
public class ImportStudentsServlet extends HttpServlet {

    private StudentDAO studentDAO;
    private UserDAO userDAO;
    private ClassDAO classDAO;

    @Override
    public void init(ServletConfig config) throws ServletException {
        studentDAO = new StudentDAO();
        userDAO = new UserDAO();
        classDAO = new ClassDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User instructor = (User) request.getSession().getAttribute(SessionConfig.ATTR_LOGIN_USER);
        Part filePart = request.getPart("studentFile");
        String classIdParam = request.getParameter("classId");

        List<String> errors = new ArrayList<>();
        int totalStudents = 0;
        int addedStudents = 0;

        if (filePart == null || filePart.getSize() == 0) {
            errors.add("No file chosen!");
            request.getSession().setAttribute("errors", errors);
            response.sendRedirect("class-students?classId=" +  classIdParam);
            return;
        }

        String fileName = filePart.getSubmittedFileName().toLowerCase();
        if (!fileName.endsWith(".xlsx")) {
            errors.add("Invalid file type. Please upload an Excel file!");
            request.getSession().setAttribute("errors", errors);
            response.sendRedirect("class-students?classId=" +  classIdParam);
            return;
        }

        try (Workbook workbook = WorkbookFactory.create(filePart.getInputStream())) {

            Sheet sheet = workbook.getSheetAt(0);

            Row headerRow = sheet.getRow(1);
            if (headerRow == null) {
                errors.add("Excel file has no header row!");
                request.getSession().setAttribute("errors", errors);
                response.sendRedirect("class-students?classId=" +  classIdParam);
                return;
            }

            Map<String, Integer> indexMap = new HashMap<>();
            for (Cell cell : headerRow) {
                indexMap.put(cell.getStringCellValue().trim().toLowerCase(), cell.getColumnIndex());
            }

            List<Class> instructorClasses =
                    classDAO.getClassContentByInstructor(instructor.getId(), null, null, true);

            Set<String> instructorClassNames = new HashSet<>();
            for (Class c : instructorClasses) {
                instructorClassNames.add(c.getName().trim().toLowerCase());
            }

            for (int i = 2; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                totalStudents++;

                String identifier =
                        ExcelFileUtil.getCell(row, indexMap.get("username/email"));

                if (identifier == null || identifier.trim().isEmpty()) {
                    errors.add("Username/Email is blank at row " + (i + 1));
                    continue;
                }

                identifier = identifier.trim();
                boolean isEmail = identifier.contains("@");

                if (!userDAO.checkUserOrEmailExists(identifier)) {
                    errors.add("User does not exist at row " + (i + 1));
                    continue;
                }

                Cell classCell = row.getCell(indexMap.get("classes"));
                String[] classNames = ExcelFileUtil.parseMultipleData(classCell);

                if (classNames.length == 0) {
                    continue;
                }

                Set<String> enrolledClasses =
                        studentDAO.getEnrolledClassNames(identifier, isEmail);

                List<String> classesToAdd = new ArrayList<>();
                boolean error = false;

                int validClassCount = 0;
                for (String className : classNames) {
                    if (className == null || className.trim().isEmpty()) continue;

                    String normalized = className.trim().toLowerCase();

                    Class clazz = classDAO.getClassByName(className.trim());
                    if (clazz == null) {
                        errors.add("Cannot find class '" + className + "' at row " + (i + 1));
                        error = true;
                        break;
                    }

                    if (!instructorClassNames.contains(normalized)) {
                        errors.add("You are not instructor of class '" + className + "' at row " + (i + 1));
                        error = true;
                        break;
                    }

                    validClassCount++;

                    if (enrolledClasses.contains(normalized)) {
                        continue;
                    }

                    classesToAdd.add(className.trim());
                }

                if (error) {
                    continue;
                }

                if (validClassCount > 0 && classesToAdd.isEmpty()) {
                    errors.add("Student " + identifier + " has already enrolled in classes at row " + (i + 1));
                    continue;
                }

                studentDAO.addStudentToClasses(
                        identifier,
                        isEmail,
                        classesToAdd.toArray(new String[0])
                );

                addedStudents++;
            }

            if (!errors.isEmpty()) {
                request.getSession().setAttribute("errors", errors);
            }

            request.getSession().setAttribute(
                    "successMessage",
                    "Imported successfully " + addedStudents + " of " + totalStudents + " student(s)"
            );

            response.sendRedirect("class-students?classId=" + classIdParam);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("class-students?classId=" + classIdParam + "&error=ImportFailed");
        }
    }
}
