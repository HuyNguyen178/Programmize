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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
                String className = FileUtil.getValue(indexMap, data, "class_name");
                if (className == null) {
                    request.getSession().setAttribute("errorMessage", "Class name cannot be null, please check your file again!");
                    response.sendRedirect("class-list?success=false");
                    return;
                }
                clazz.setName(className);

                String[] categories = FileUtil.parseClassCategories(indexMap, data);
                String[] categoryIds = null;

                if (categories != null && categories.length > 0) {
                    categoryIds = new String[categories.length];

                    for (int i = 0; i < categories.length; i++) {
                        Setting category = settingDAO.findCategoryByName(categories[i]);

                        if (category == null) {
                            request.getSession().setAttribute(
                                    "errorMessage",
                                    "Cannot find category " + categories[i] + " at class " + clazz.getName()
                            );
                            response.sendRedirect("class-list?success=false");
                            return;
                        }

                        categoryIds[i] = String.valueOf(category.getId());
                    }
                }

                String instructorName = FileUtil.getValue(indexMap, data, "class_instructor");
                User instructor = userDAO.findInstructorByName(instructorName);
                if (instructor == null) {
                    request.getSession().setAttribute("errorMessage", "Cannot find instructor " + instructorName + " at class " + clazz.getName());
                    response.sendRedirect("class-list?success=false");
                    return;
                }
                clazz.setInstructor(instructor);

                String listedPrice = FileUtil.getValue(indexMap, data, "listed_price");
                if (listedPrice == null) {
                    request.getSession().setAttribute("errorMessage", "Listed Price is null at class " + clazz.getName());
                    response.sendRedirect("class-list?success=false");
                    return;
                }
                clazz.setListedPrice(new BigDecimal(listedPrice));

                String salePrice = FileUtil.getValue(indexMap, data, "sale_price");
                if (salePrice == null) {
                    request.getSession().setAttribute("errorMessage", "Sale Price is null at class " + clazz.getName());
                    response.sendRedirect("class-list?success=false");
                    return;
                }
                clazz.setSalePrice(new BigDecimal(salePrice));

                String startDateStr = FileUtil.getValue(indexMap, data, "start_date");
                String endDateStr = FileUtil.getValue(indexMap, data, "end_date");
                if (startDateStr == null) {
                    request.getSession().setAttribute("errorMessage", "Start date is null at class " + clazz.getName());
                    response.sendRedirect("class-list?success=false");
                    return;
                }
                if (endDateStr == null) {
                    request.getSession().setAttribute("errorMessage", "End date is null at class " + clazz.getName());
                    response.sendRedirect("class-list?success=false");
                    return;
                }
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yy");
                Date startDate = formatter.parse(startDateStr);
                Date endDate = formatter.parse(endDateStr);
                clazz.setStartDate(startDate);
                clazz.setEndDate(endDate);

                clazz.setDescription(FileUtil.getValue(indexMap, data, "description"));
                clazz.setThumbnailUrl(FileUtil.getValue(indexMap, data, "thumbnail_url"));
                clazz.setStatus(Boolean.parseBoolean(FileUtil.getValue(indexMap, data, "status")));

                if (startDate != null && endDate != null && endDate.before(startDate)) {
                    request.getSession().setAttribute("errorMessage", "End date must be after start date at class " + clazz.getName());
                    response.sendRedirect("class-list?success=false");
                    return;
                }

                if (clazz.getListedPrice().compareTo(clazz.getSalePrice()) < 0) {
                    request.getSession().setAttribute("errorMessage", "Listed Price must be greater than Sale Price at class " + clazz.getName());
                    response.sendRedirect("class-list?success=false");
                    return;
                }

                if (classDAO.doesClassNameExist(clazz.getName())) {
                    request.getSession().setAttribute("errorMessage", "Class name has already existed at class " + clazz.getName());
                    response.sendRedirect("class-list?success=false");
                    return;
                }

                classDAO.insertClass(clazz, categoryIds);
            }

            request.getSession().setAttribute("successMessage", "Imported successfully!");
            response.sendRedirect("class-list?success=true");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("class-list?error=ImportFailed");
        }
    }
}
