package servlet;

import dao.ClassDAO;
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
import model.Class;
import model.Setting;
import model.User;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import utils.ExcelFileUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@WebServlet("/import-classes")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,    // 1MB
        maxFileSize = 5 * 1024 * 1024,       // 5MB
        maxRequestSize = 10 * 1024 * 1024    // 10MB
)
public class ImportClassesServlet extends HttpServlet {
    private UserDAO userDAO;
    private ClassDAO classDAO;
    private SettingDAO settingDAO;

    @Override
    public void init(ServletConfig config) throws ServletException {
        userDAO = new UserDAO();
        classDAO = new ClassDAO();
        settingDAO = new SettingDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Part filePart = request.getPart("classFile");
        List<String> errors = new ArrayList<>();
        int totalClasses = 0;
        int successCount = 0;

        if (filePart == null || filePart.getSize() == 0) {
            errors.add("No file chosen!");
            request.getSession().setAttribute("errors", errors);
            response.sendRedirect("class-list");
            return;
        }

        String fileName = filePart.getSubmittedFileName().toLowerCase();
        if (!fileName.endsWith(".xlsx")) {
            errors.add("Invalid file type. Please upload an Excel file!");
            request.getSession().setAttribute("errors", errors);
            response.sendRedirect("class-list");
            return;
        }

        try (Workbook workbook = WorkbookFactory.create(filePart.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            Row headerRow = sheet.getRow(1);
            if (headerRow == null) {
                errors.add("Excel file has no header row!");
                request.getSession().setAttribute("errors", errors);
                response.sendRedirect("class-list");
                return;
            }

            Map<String, Integer> indexMap = new HashMap<>();
            for (Cell cell : headerRow) {
                indexMap.put(cell.getStringCellValue().trim(), cell.getColumnIndex());
            }

            for (int i = 2; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                totalClasses++;
                Class clazz = new Class();

                String className = ExcelFileUtil.getCell(row, indexMap.get("name"));
                if (className == null || className.isEmpty()) {
                    errors.add("Class name is blank at row " + (i + 1));
                    continue;
                }
                if (classDAO.doesClassNameExist(className)) {
                    errors.add("Class name already exists at row " + (i + 1));
                    continue;
                }
                clazz.setName(className);

                Cell categoryCell = row.getCell(indexMap.get("categories"));
                String[] categories = ExcelFileUtil.parseCategories(categoryCell);
                String[] categoryIds = null;
                if (categories.length > 0) {
                    boolean error = false;
                    categoryIds = new String[categories.length];
                    for (int j = 0; j < categories.length; j++) {
                        String catName = categories[j].substring(0, 1).toUpperCase() + categories[j].substring(1);
                        Setting category = settingDAO.findCategoryByName(catName);
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
                if (instructorName == null || instructorName.isEmpty()) {
                    errors.add("Instructor is blank at row " + (i + 1));
                    continue;
                }
                User instructor = userDAO.findInstructorByName(instructorName);
                if (instructor == null) {
                    errors.add("Cannot find instructor " + instructorName + " at row " + (i + 1));
                    continue;
                }
                clazz.setInstructor(instructor);

                String listedPrice = ExcelFileUtil.getCell(row, indexMap.get("listed_price"));
                if (listedPrice == null || listedPrice.isEmpty()) {
                    errors.add("Listed Price is blank at row " + (i + 1));
                    continue;
                }
                try {
                    clazz.setListedPrice(new BigDecimal(listedPrice));
                } catch (NumberFormatException e) {
                    errors.add("Listed Price is invalid at row " + (i + 1));
                    continue;
                }

                String salePrice = ExcelFileUtil.getCell(row, indexMap.get("sale_price"));
                if (salePrice == null || salePrice.isEmpty()) {
                    clazz.setSalePrice(null);
                } else {
                    try {
                        clazz.setSalePrice(new BigDecimal(salePrice));
                    } catch (NumberFormatException e) {
                        errors.add("Sale Price is invalid at row " + (i + 1));
                        continue;
                    }
                }

                // Dates
                String startDateStr = ExcelFileUtil.getCell(row, indexMap.get("start_date"));
                String endDateStr = ExcelFileUtil.getCell(row, indexMap.get("end_date"));
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yy");
                Date startDate, endDate;
                try {
                    startDate = formatter.parse(startDateStr);
                } catch (ParseException e) {
                    errors.add("Start Date is invalid at row " + (i + 1));
                    continue;
                }
                try {
                    endDate = formatter.parse(endDateStr);
                } catch (ParseException e) {
                    errors.add("End Date is invalid at row " + (i + 1));
                    continue;
                }
                if (endDate.before(startDate)) {
                    errors.add("End date must be after start date at row " + (i + 1));
                    continue;
                }
                clazz.setStartDate(startDate);
                clazz.setEndDate(endDate);

                clazz.setDescription(ExcelFileUtil.getCell(row, indexMap.get("description")));

                String statusStr = ExcelFileUtil.getCell(row, indexMap.get("status"));
                if (!"true".equalsIgnoreCase(statusStr) && !"false".equalsIgnoreCase(statusStr)) {
                    errors.add("Status must be true or false at row " + (i + 1));
                    continue;
                }
                clazz.setStatus(Boolean.parseBoolean(statusStr));

                if (clazz.getSalePrice() != null && clazz.getListedPrice().compareTo(clazz.getSalePrice()) < 0) {
                    errors.add("Listed Price must be greater than Sale Price at row " + (i + 1));
                    continue;
                }

                classDAO.insertClass(clazz, categoryIds);
                successCount++;
            }

            if (!errors.isEmpty()) {
                request.getSession().setAttribute("errors", errors);
            }
            request.getSession().setAttribute("successMessage","Imported successfully " + successCount + " of " + totalClasses + " class(es)");
            response.sendRedirect("class-list");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("class-list?error=ImportFailed");
        }
    }
}
