package servlet;

import dao.CourseDAO;
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
import model.Course;
import model.Setting;
import model.User;
import org.apache.poi.ss.usermodel.*;
import utils.ExcelFileUtil;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@WebServlet("/import-courses")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,    // 1MB
        maxFileSize = 5 * 1024 * 1024,       // 5MB
        maxRequestSize = 10 * 1024 * 1024    // 10MB
)
public class ImportCoursesServlet extends HttpServlet {
    private CourseDAO courseDAO;
    private UserDAO userDAO;
    private SettingDAO settingDAO;

    @Override
    public void init(ServletConfig config) throws ServletException {
        courseDAO = new CourseDAO();
        userDAO = new UserDAO();
        settingDAO = new SettingDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Part filePart = request.getPart("courseFile");
        List<String> errors = new ArrayList<>();
        int totalCourse = 0;
        int successCount = 0;

        if (filePart == null || filePart.getSize() == 0) {
            errors.add("No file chosen!");
            request.getSession().setAttribute("errors", errors);
            response.sendRedirect("course-list");
            return;
        }

        String fileName = filePart.getSubmittedFileName().toLowerCase();
        if (!fileName.endsWith(".xlsx")) {
            errors.add("Invalid file type. Please upload an Excel file!");
            request.getSession().setAttribute("errors", errors);
            response.sendRedirect("course-list");
            return;
        }

        try (Workbook workbook = WorkbookFactory.create(filePart.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            Row headerRow = sheet.getRow(1);
            if (headerRow == null) {
                errors.add("Excel file has no header row!");
                request.getSession().setAttribute("errors", errors);
                response.sendRedirect("course-list");
                return;
            }
            Map<String, Integer> indexMap = new HashMap<>();
            for (Cell cell : headerRow) {
                indexMap.put(cell.getStringCellValue().trim(), cell.getColumnIndex());
            }

            for (int i = 2; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                totalCourse++;
                Course course = new Course();

                String courseName = ExcelFileUtil.getCell(row, indexMap.get("name"));
                if (courseName == null || courseName.isEmpty()) {
                    errors.add("Course name is blank at row " + (i + 1));
                    continue;
                }
                course.setCourseName(courseName);

                Cell categoryCell = row.getCell(indexMap.get("categories"));
                String[] categories = ExcelFileUtil.parseMultipleData(categoryCell);
                String[] categoryIds = null;
                if (categories.length > 0) {
                    boolean error = false;
                    categoryIds = new String[categories.length];
                    for (int j = 0; j < categories.length; j++) {
                        Setting category = settingDAO.findCategoryByName(categories[j]);
                        if (category == null) {
                            errors.add("Cannot find category " + categories[j] + " at row " + (i + 1));
                            error = true;
                            break;
                        }
                        categoryIds[j] = String.valueOf(category.getId());
                    }
                    if (error) {
                        continue;
                    }
                }

                String instructorName = ExcelFileUtil.getCell(row, indexMap.get("instructor"));
                User instructor = userDAO.findInstructorByName(instructorName);
                if (instructor == null) {
                    errors.add("Cannot find instructor " + instructorName + " at row " + (i + 1));
                    continue;
                }
                course.setInstructorId(instructor.getId());

                String listedPrice = ExcelFileUtil.getCell(row, indexMap.get("listed_price"));
                if (listedPrice == null || listedPrice.isEmpty()) {
                    errors.add("Listed Price is blank at row " + (i + 1));
                    continue;
                }
                try {
                    course.setListedPrice(new BigDecimal(listedPrice));
                } catch (NumberFormatException e) {
                    errors.add("Listed Price is invalid at row " + (i + 1));
                    continue;
                }

                String salePrice = ExcelFileUtil.getCell(row, indexMap.get("sale_price"));
                if (salePrice == null || salePrice.isEmpty()) {
                    course.setSalePrice(null);
                } else {
                    try {
                        course.setSalePrice(new BigDecimal(salePrice));
                    } catch (NumberFormatException e) {
                        errors.add("Sale Price is invalid at row " + (i + 1));
                        continue;
                    }
                }

                String duration = ExcelFileUtil.getCell(row, indexMap.get("duration"));
                if (duration == null || duration.isEmpty()) {
                    errors.add("Duration is blank at row " + (i + 1));
                    continue;
                }
                try {
                    course.setDuration(Integer.valueOf(duration));
                } catch (NumberFormatException e) {
                    errors.add("Duration is invalid at row " + (i + 1));
                    continue;
                }

                course.setDescription(ExcelFileUtil.getCell(row, indexMap.get("description")));

                String statusStr = ExcelFileUtil.getCell(row, indexMap.get("status"));
                if (!"true".equalsIgnoreCase(statusStr) && !"false".equalsIgnoreCase(statusStr)) {
                    errors.add("Status must be true or false at row " + (i + 1));
                    continue;
                }
                course.setStatus(Boolean.parseBoolean(statusStr));

                if (course.getSalePrice() != null && course.getListedPrice().compareTo(course.getSalePrice()) < 0) {
                    errors.add("Listed Price must be greater than Sale Price at row " + (i + 1));
                    continue;
                }

                courseDAO.addCourse(course, categoryIds);
                successCount++;
            }

            if (!errors.isEmpty()) {
                request.getSession().setAttribute("errors", errors);
            }

            request.getSession().setAttribute("successMessage","Imported successfully " + successCount + " of " + totalCourse + " course(s)");
            response.sendRedirect("course-list");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("course-list?error=ImportFailed");
        }
    }
}
