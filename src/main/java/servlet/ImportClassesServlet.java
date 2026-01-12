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
import utils.FileUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

        if (filePart == null || filePart.getSize() == 0) {
            response.sendRedirect("class-list?error=NoFile");
            return;
        }

        List<String> errors = new ArrayList<>();
        int totalClasses = 0;
        int successCount = 0;
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(filePart.getInputStream()))) {
            bufferedReader.readLine();
            String headerLine = bufferedReader.readLine();
            String[] headers = headerLine.split(",");

            Map<String, Integer> indexMap = new HashMap<>();
            for (int i = 0; i < headers.length; i++) {
                indexMap.put(headers[i].trim(), i);
            }

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                totalClasses++;
                String[] data = line.split(",");

                Class clazz = new Class();
                String className = FileUtil.getValue(indexMap, data, "name");
                if (className == null) {
                    errors.add("Class name is blank somewhere, please check your file again!");
                    continue;
                }
                if (classDAO.doesClassNameExist(clazz.getName())) {
                    errors.add("Class name has already existed at class " + clazz.getName());
                    continue;
                }
                clazz.setName(className);

                String[] categories = FileUtil.parseCategories(indexMap, data);
                String[] categoryIds = null;

                if (categories != null && categories.length > 0) {
                    categoryIds = new String[categories.length];

                    for (int i = 0; i < categories.length; i++) {
                        String categoryName = categories[i].substring(0, 1).toUpperCase() + categories[i].substring(1);
                        Setting category = settingDAO.findCategoryByName(categoryName);

                        if (category == null) {
                            errors.add("Cannot find category " + categories[i] + " at class " + clazz.getName());
                            continue;
                        }

                        categoryIds[i] = String.valueOf(category.getId());
                    }
                }

                String instructorName = FileUtil.getValue(indexMap, data, "instructor");
                if (instructorName == null) {
                    errors.add("Instructor is blank at class " + clazz.getName());
                    continue;
                }
                User instructor = userDAO.findInstructorByName(instructorName);
                if (instructor == null) {
                    errors.add("Cannot find instructor " + instructorName + " at class " + clazz.getName());
                    continue;
                }
                clazz.setInstructor(instructor);

                String listedPrice = FileUtil.getValue(indexMap, data, "listed_price");
                if (listedPrice == null) {
                    errors.add("Listed Price is blank at class " + clazz.getName());
                    continue;
                }
                try {
                    clazz.setListedPrice(new BigDecimal(listedPrice));
                } catch (NumberFormatException e) {
                    errors.add("Listed Price is invalid at class " + clazz.getName());
                    continue;
                }

                String salePrice = FileUtil.getValue(indexMap, data, "sale_price");
                if (salePrice == null) {
                    clazz.setSalePrice(null);
                }
                else {
                    try {
                        clazz.setSalePrice(new BigDecimal(salePrice));
                    } catch (NumberFormatException e) {
                        errors.add("Invalid Sale Price at class " + clazz.getName());
                        continue;
                    }
                }

                String startDateStr = FileUtil.getValue(indexMap, data, "start_date");
                String endDateStr = FileUtil.getValue(indexMap, data, "end_date");
                if (startDateStr == null) {
                    errors.add("Start date is blank at class " + clazz.getName());
                    continue;
                }
                if (endDateStr == null) {
                    errors.add("End date is blank at class " + clazz.getName());
                    continue;
                }
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yy");
                Date startDate;
                Date endDate;

                try {
                    startDate = formatter.parse(startDateStr);
                } catch (ParseException e) {
                    errors.add("Start Date is invalid at class " + clazz.getName());
                    continue;
                }
                clazz.setStartDate(startDate);
                try {
                    endDate = formatter.parse(endDateStr);
                } catch (ParseException e) {
                    errors.add("End Date is invalid at class " + clazz.getName());
                    continue;
                }
                clazz.setEndDate(endDate);

                clazz.setDescription(FileUtil.getValue(indexMap, data, "description"));

                String statusStr = FileUtil.getValue(indexMap, data, "status");
                if (!"true".equalsIgnoreCase(statusStr) && !"false".equalsIgnoreCase(statusStr)) {
                    errors.add("Status must be true or false at course " + clazz.getName());
                    continue;
                }
                clazz.setStatus(Boolean.parseBoolean(statusStr));

                if (startDate != null && endDate != null && endDate.before(startDate)) {
                    errors.add("End date must be after start date at class " + clazz.getName());
                    continue;
                }

                if (clazz.getListedPrice().compareTo(clazz.getSalePrice()) < 0) {
                    errors.add("Listed Price must be greater than Sale Price at class " + clazz.getName());
                    continue;
                }

                classDAO.insertClass(clazz, categoryIds);
                successCount++;
            }

            if (!errors.isEmpty()) {
                request.getSession().setAttribute("errors", errors);
            }
            request.getSession().setAttribute("successMessage", "Imported successfully " + successCount + " of " + totalClasses + " class(es)");
            response.sendRedirect("class-list?success=true");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("class-list?error=ImportFailed");
        }
    }
}
