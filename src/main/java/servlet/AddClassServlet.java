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
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
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
        String className = request.getParameter("className");
        String thumbnailUrl = request.getParameter("thumbnailUrl");
        String description = request.getParameter("description");
        BigDecimal listedPrice = new BigDecimal(request.getParameter("listedPrice"));
        BigDecimal salePrice = new BigDecimal(request.getParameter("salePrice"));
        String[] categoryIds = request.getParameterValues("categoryIds");
        String instructorIdStr = request.getParameter("instructorId");
        boolean status = "1".equals(request.getParameter("status"));
        String startDateStr = request.getParameter("startDate");
        String endDateStr = request.getParameter("endDate");
        try {
            request.setCharacterEncoding("UTF-8");

            int instructorId = Integer.parseInt(instructorIdStr);

            Date startDate = null;
            Date endDate = null;

            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            if (startDateStr != null && !startDateStr.isBlank()) {
                startDate = formatter.parse(startDateStr);
            }

            if (endDateStr != null && !endDateStr.isBlank()) {
                endDate = formatter.parse(endDateStr);
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

            if (startDate != null && endDate != null && endDate.before(startDate)) {
                request.getSession().setAttribute("errorMessage", "End date must be after start date!");
                request.setAttribute("clazz", c);
                request.setAttribute("instructors", userDAO.getAllInstructors());
                request.setAttribute("categories", settingDAO.getAllCategories());
                request.getRequestDispatcher("/WEB-INF/views/add-class.jsp").forward(request, response);
                return;
            }

            if (listedPrice.compareTo(salePrice) < 0) {
                request.getSession().setAttribute("errorMessage", "Listed Price must be greater than Sale Price!");
                request.setAttribute("clazz", c);
                request.setAttribute("instructors", userDAO.getAllInstructors());
                request.setAttribute("categories", settingDAO.getAllCategories());
                request.getRequestDispatcher("/WEB-INF/views/add-class.jsp").forward(request, response);
                return;
            }

            if (classDAO.doesClassNameExist(className)) {
                request.getSession().setAttribute("errorMessage", "Class name has already existed!");
                request.setAttribute("clazz", c);
                request.setAttribute("instructors", userDAO.getAllInstructors());
                request.setAttribute("categories", settingDAO.getAllCategories());
                request.getRequestDispatcher("/WEB-INF/views/add-class.jsp").forward(request, response);
                return;
            }

            classDAO.insertClass(c, categoryIds);
            request.getSession().setAttribute("successMessage", "Class added successfully!");
            response.sendRedirect(request.getContextPath() + "/class-list");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
