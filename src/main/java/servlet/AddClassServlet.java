package servlet;

import dao.ClassDAO;
import dao.SettingDAO;
import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Class;
import model.Setting;
import model.User;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebServlet("/add-class")
public class AddClassServlet extends HttpServlet {
    private ClassDAO classDAO;
    private UserDAO userDAO;
    private SettingDAO settingDAO;

    @Override
    public void init() throws ServletException {
        classDAO = new ClassDAO();
        userDAO = new UserDAO();
        settingDAO = new SettingDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<User> instructors = userDAO.getAllInstructors();
        request.setAttribute("instructors", instructors);

        List<Setting> categories = settingDAO.getAllCategories();
        request.setAttribute("categories", categories);

        request.getRequestDispatcher("/WEB-INF/views/add-class.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            request.setCharacterEncoding("UTF-8");

            String className = request.getParameter("className");
            String thumbnailUrl = request.getParameter("thumbnailUrl");
            String description = request.getParameter("description");

            BigDecimal listedPrice = new BigDecimal(request.getParameter("listedPrice"));

            BigDecimal salePrice = null;
            String salePriceStr = request.getParameter("salePrice");
            if (salePriceStr != null && !salePriceStr.isBlank()) {
                salePrice = new BigDecimal(salePriceStr);
            }

            int instructorId = Integer.parseInt(request.getParameter("instructorId"));
            boolean status = "1".equals(request.getParameter("status"));
            String[] categoryIds = request.getParameterValues("categoryIds");

            LocalDate startDate = null;
            LocalDate endDate = null;

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            String startDateStr = request.getParameter("startDate");
            if (startDateStr != null && !startDateStr.isBlank()) {
                startDate = LocalDate.parse(startDateStr, formatter);
            }

            String endDateStr = request.getParameter("endDate");
            if (endDateStr != null && !endDateStr.isBlank()) {
                endDate = LocalDate.parse(endDateStr, formatter);
            }

            if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
                throw new IllegalArgumentException("End date must be after start date");
            }

            Class c = new Class();
            c.setName(className);
            c.setThumbnailUrl(thumbnailUrl);
            c.setDescription(description);
            c.setListedPrice(listedPrice);
            c.setSalePrice(salePrice);
            c.setStartDate(startDate);
            c.setEndDate(endDate);
            c.setStatus(status);
            c.setNumberOfStudents(0);

            User instructor = new User();
            instructor.setId(instructorId);
            c.setInstructor(instructor);

            // ===== SAVE =====
            classDAO.insertClass(c, categoryIds);
            request.getSession().setAttribute("successMessage", "Class added successfully!");
            response.sendRedirect(request.getContextPath() + "/class-list");
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/add-class");
        }
    }
}
