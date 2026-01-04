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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

                Course course = new Course();
                String courseName = FileUtil.getValue(indexMap, data, "course_name");
                if (courseName == null) {
                    request.getSession().setAttribute("errorMessage", "Course name cannot be null, please check your file again!");
                    response.sendRedirect("course-list?success=false");
                    return;
                }
                course.setCourseName(courseName);

                String[] categories = FileUtil.parseCourseCategories(indexMap, data);
                String[] categoryIds = null;

                if (categories != null && categories.length > 0) {
                    categoryIds = new String[categories.length];

                    for (int i = 0; i < categories.length; i++) {
                        Setting category = settingDAO.findCategoryByName(categories[i]);

                        if (category == null) {
                            request.getSession().setAttribute(
                                    "errorMessage",
                                    "Cannot find category " + categories[i] + " at course " + course.getCourseName()
                            );
                            response.sendRedirect("course-list?success=false");
                            return;
                        }

                        categoryIds[i] = String.valueOf(category.getId());
                    }
                }

                String instructorName = FileUtil.getValue(indexMap, data, "course_instructor");
                User instructor = userDAO.findInstructorByName(instructorName);
                if (instructor == null) {
                    request.getSession().setAttribute("errorMessage", "Cannot find instructor " + instructorName + " at course " + course.getCourseName());
                    response.sendRedirect("course-list?success=false");
                    return;
                }
                course.setInstructorId(instructor.getId());

                String listedPrice = FileUtil.getValue(indexMap, data, "listed_price");
                if (listedPrice == null) {
                    request.getSession().setAttribute("errorMessage", "Listed Price is null at course " + course.getCourseName());
                    response.sendRedirect("course-list?success=false");
                    return;
                }
                course.setListedPrice(new BigDecimal(listedPrice));

                String salePrice = FileUtil.getValue(indexMap, data, "sale_price");
                if (salePrice == null) {
                    request.getSession().setAttribute("errorMessage", "Sale Price is null at course " + course.getCourseName());
                    response.sendRedirect("course-list?success=false");
                    return;
                }
                course.setSalePrice(new BigDecimal(salePrice));

                String duration = FileUtil.getValue(indexMap, data, "duration");
                if (duration == null) {
                    request.getSession().setAttribute("errorMessage", "Duration is null at course " + course.getCourseName());
                    response.sendRedirect("course-list?success=false");
                    return;
                }
                course.setDuration(Integer.valueOf(duration));

                course.setDescription(FileUtil.getValue(indexMap, data, "description"));
                course.setThumbnailUrl(FileUtil.getValue(indexMap, data, "thumbnail_url"));
                course.setStatus(Boolean.parseBoolean(FileUtil.getValue(indexMap, data, "status")));


                if (course.getListedPrice().compareTo(course.getSalePrice()) < 0) {
                    request.getSession().setAttribute("errorMessage", "Listed Price must be greater than Sale Price at course " + course.getCourseName());
                    response.sendRedirect("course-list?success=false");
                    return;
                }

                courseDAO.addCourse(course, categoryIds);
            }

            request.getSession().setAttribute("successMessage", "Imported successfully!");
            response.sendRedirect("course-list?success=true");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("course-list?error=ImportFailed");
        }
    }
}
