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
import model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
                course.setCourseName(data[indexMap.get("courseName")].trim());

                String[] categories = data[indexMap.get("courseCategories")].trim().split("\\|");
                Integer[] categoryIds = new Integer[categories.length];

                for (int i = 0; i < categories.length; i++) {
                    String category = categories[i].trim();
                    Integer categoryId = settingDAO.findCategoryIdByName(category);

                    if (categoryId == null) {
                        throw new RuntimeException("Category not found: " + category);
                    }
                    categoryIds[i] = categoryId;
                }

                String instructorName = data[indexMap.get("courseInstructor")].trim();
                User instructor = userDAO.findInstructorByName(instructorName);
                if (instructor == null) {
                    throw new RuntimeException("Instructor not found: " + instructorName);
                }
                course.setInstructorId(instructor.getId());

                course.setListedPrice(new BigDecimal(data[indexMap.get("listedPrice")].trim()));
                course.setSalePrice(new BigDecimal(data[indexMap.get("salePrice")].trim()));
                course.setThumbnailUrl(data[indexMap.get("thumbnailUrl")].trim());
                course.setDescription(data[indexMap.get("description")].trim());
                course.setStatus(Boolean.parseBoolean(data[indexMap.get("status")].trim()));
                course.setDuration(Integer.parseInt(data[indexMap.get("duration")].trim()));

                courseDAO.addCourse(course, categoryIds);
            }

            request.getSession().setAttribute("successMessage", "Import successfully!");
            response.sendRedirect("course-list?success=true");
        }

        catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("course-list?error=ImportFailed");
        }
    }
}
