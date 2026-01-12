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
import model.Class;
import model.Course;
import model.Setting;
import model.User;
import utils.FileUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
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

        if (filePart == null || filePart.getSize() == 0) {
            response.sendRedirect("course-list?error=NoFile");
            return;
        }

        List<String> errors = new ArrayList<>();
        int totalCourse = 0;
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
                totalCourse++;
                String[] data = line.split(",");

                Course course = new Course();
                String courseName = FileUtil.getValue(indexMap, data, "name");
                if (courseName == null) {
                    errors.add("Course name is blank somewhere, please check your file again!");
                    continue;
                }
                course.setCourseName(courseName);

                String[] categories = FileUtil.parseCategories(indexMap, data);
                String[] categoryIds = null;

                if (categories != null && categories.length > 0) {
                    categoryIds = new String[categories.length];

                    for (int i = 0; i < categories.length; i++) {
                        Setting category = settingDAO.findCategoryByName(categories[i]);

                        if (category == null) {
                            errors.add("Cannot find category " + categories[i] + " at course " + course.getCourseName());
                            continue;
                        }

                        categoryIds[i] = String.valueOf(category.getId());
                    }
                }

                String instructorName = FileUtil.getValue(indexMap, data, "instructor");
                User instructor = userDAO.findInstructorByName(instructorName);
                if (instructor == null) {
                    errors.add("Cannot find instructor " + instructorName + " at course " + course.getCourseName());
                    continue;
                }
                course.setInstructorId(instructor.getId());

                String listedPrice = FileUtil.getValue(indexMap, data, "listed_price");
                if (listedPrice == null) {
                    errors.add("Listed Price is blank at course " + course.getCourseName());
                    continue;
                }
                try {
                    course.setListedPrice(new BigDecimal(listedPrice));
                } catch (NumberFormatException e) {
                    errors.add("Listed Price is invalid at course " + course.getCourseName());
                    continue;
                }

                String salePrice = FileUtil.getValue(indexMap, data, "sale_price");
                if (salePrice == null) {
                    course.setSalePrice(null);
                }
                else {
                    try {
                        course.setSalePrice(new BigDecimal(salePrice));
                    } catch (NumberFormatException e) {
                        errors.add("Sale Price is invalid at course " + course.getCourseName());
                        continue;
                    }
                }

                String duration = FileUtil.getValue(indexMap, data, "duration");
                if (duration == null) {
                    errors.add("Duration is blank at course " + course.getCourseName());
                    continue;
                }
                try {
                    course.setDuration(Integer.valueOf(duration));
                } catch (NumberFormatException e) {
                    errors.add("Duration is invalid at course " + course.getCourseName());
                    continue;
                }

                course.setDescription(FileUtil.getValue(indexMap, data, "description"));
                String statusStr = FileUtil.getValue(indexMap, data, "status");
                if (!"true".equalsIgnoreCase(statusStr) && !"false".equalsIgnoreCase(statusStr)) {
                    errors.add("Status must be true or false at course " + course.getCourseName());
                    continue;
                }
                course.setStatus(Boolean.parseBoolean(statusStr));


                if (course.getListedPrice().compareTo(course.getSalePrice()) < 0) {
                    errors.add("Listed Price must be greater than Sale Price at course " + course.getCourseName());
                    continue;
                }

                courseDAO.addCourse(course, categoryIds);
                successCount++;
            }

            if (!errors.isEmpty()) {
                request.getSession().setAttribute("errors", errors);
            }
            request.getSession().setAttribute("successMessage", "Imported successfully " + successCount + " of " + totalCourse + " course(s)");
            response.sendRedirect("course-list?");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("course-list?error=ImportFailed");
        }
    }
}
