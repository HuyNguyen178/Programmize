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
import model.Course;
import model.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/import-class")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,    // 1MB
        maxFileSize = 5 * 1024 * 1024,       // 5MB
        maxRequestSize = 10 * 1024 * 1024    // 10MB
)
public class ImportClassServlet extends HttpServlet {
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

                Class clazz = new Class();
                clazz.setName(data[indexMap.get("className")].trim());

                String[] categories = data[indexMap.get("classCategories")].trim().split("\\|");
                String[] categoryIds = new String[categories.length];

                for (int i = 0; i < categories.length; i++) {
                    String category = categories[i].trim();
                    Integer categoryId = settingDAO.findCategoryIdByName(category);

                    if (categoryId == null) {
                        request.getSession().setAttribute("errorMessage", "Cannot find category " + category + " at class " + clazz.getName());
                        response.sendRedirect("class-list?success=false");
                        return;
                    }
                    categoryIds[i] = String.valueOf(categoryId);
                }

                String instructorName = data[indexMap.get("classInstructor")].trim();
                User instructor = userDAO.findInstructorByName(instructorName);
                if (instructor == null) {
                    request.getSession().setAttribute("errorMessage", "Cannot find instructor " + instructorName + " at class " + clazz.getName());
                    response.sendRedirect("class-list?success=false");
                    return;
                }
                clazz.setInstructor(instructor);
                clazz.setListedPrice(new BigDecimal(data[indexMap.get("listedPrice")].trim()));
                clazz.setSalePrice(new BigDecimal(data[indexMap.get("salePrice")].trim()));

                String startDateStr = data[indexMap.get("startDate")].trim();
                String endDateStr = data[indexMap.get("endDate")].trim();
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yy");
                Date startDate = formatter.parse(startDateStr);
                Date endDate = formatter.parse(endDateStr);
                clazz.setStartDate(startDate);
                clazz.setEndDate(endDate);

                clazz.setThumbnailUrl(data[indexMap.get("thumbnailUrl")].trim());
                clazz.setDescription(data[indexMap.get("description")].trim());
                clazz.setStatus(Boolean.parseBoolean(data[indexMap.get("status")].trim()));

                if (startDate != null && endDate != null && endDate.before(startDate)) {
                    request.getSession().setAttribute("errorMessage", "End date must be after start date!");
                    response.sendRedirect("class-list?success=false");
                    return;
                }

                if (clazz.getListedPrice().compareTo(clazz.getSalePrice()) < 0) {
                    request.getSession().setAttribute("errorMessage", "Listed Price must be greater than Sale Price!");
                    response.sendRedirect("class-list?success=false");
                    return;
                }

                if (classDAO.doesClassNameExist(clazz.getName())) {
                    request.getSession().setAttribute("errorMessage", "Class name has already existed!");
                    response.sendRedirect("class-list?success=false");
                    return;
                }

                classDAO.insertClass(clazz, categoryIds);
            }

            request.getSession().setAttribute("successMessage", "Import successfully!");
            response.sendRedirect("class-list?success=true");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("class-list?error=ImportFailed");
        }
    }
}
