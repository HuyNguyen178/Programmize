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

@WebServlet("/edit-class")
public class EditClassServlet extends HttpServlet {
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
        try {
            String idParam = request.getParameter("id");

            if (idParam == null || idParam.trim().isEmpty()) {
                request.getSession().setAttribute("errorMessage", "Invalid course ID!");
                response.sendRedirect(request.getContextPath() + "/class-list");
                return;
            }

            int classId = Integer.parseInt(idParam);

            Class c = classDAO.getClassById(classId);
            if (c == null) {
                request.getSession().setAttribute("errorMessage", "Class not found!");
                response.sendRedirect(request.getContextPath() + "/class-list");
                return;
            }

            List<Setting> allCategories = settingDAO.getAllCategories();
            List<User> allInstructors = userDAO.getAllInstructors();

            List<Setting> classCategories = classDAO.getCategoriesByClassId(classId);

            request.setAttribute("clazz", c);
            request.setAttribute("allCategories", allCategories);
            request.setAttribute("allInstructors", allInstructors);
            request.setAttribute("classCategories", classCategories);

            request.getRequestDispatcher("/WEB-INF/views/edit-class.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMessage", "Invalid course ID!");
            response.sendRedirect(request.getContextPath() + "/class-list");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            request.setCharacterEncoding("UTF-8");

            int classId = Integer.parseInt(request.getParameter("classId"));
            String className = request.getParameter("className");
            String thumbnailUrl = request.getParameter("thumbnailUrl");
            String description = request.getParameter("description");
            BigDecimal listedPrice = new BigDecimal(request.getParameter("listedPrice"));
            BigDecimal salePrice = new BigDecimal(request.getParameter("salePrice"));
            String[] categoryIds = request.getParameterValues("categoryIds");
            int instructorId = Integer.parseInt(request.getParameter("instructorId"));
            boolean status = "1".equals(request.getParameter("status"));
            String startDateStr = request.getParameter("startDate");
            String endDateStr = request.getParameter("endDate");

            Date startDate = null;
            Date endDate = null;
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

            if (startDateStr != null && !startDateStr.isBlank()) {
                startDate = formatter.parse(startDateStr);
            }

            if (endDateStr != null && !endDateStr.isBlank()) {
                endDate = formatter.parse(endDateStr);
            }

            Class c = classDAO.getClassById(classId);
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
                request.setAttribute("allInstructors", userDAO.getAllInstructors());
                request.setAttribute("allCategories", settingDAO.getAllCategories());
                request.setAttribute("classCategories", classDAO.getCategoriesByClassId(classId));
                request.getRequestDispatcher("/WEB-INF/views/edit-class.jsp").forward(request, response);
                return;
            }

            if (listedPrice.compareTo(salePrice) < 0) {
                request.getSession().setAttribute("errorMessage", "Listed Price must be greater than Sale Price!");
                request.setAttribute("clazz", c);
                request.setAttribute("allInstructors", userDAO.getAllInstructors());
                request.setAttribute("allCategories", settingDAO.getAllCategories());
                request.setAttribute("classCategories", classDAO.getCategoriesByClassId(classId));
                request.getRequestDispatcher("/WEB-INF/views/edit-class.jsp").forward(request, response);
                return;
            }

            if (classDAO.existsByNameAndNotId(className, classId)) {
                request.getSession().setAttribute("errorMessage", "Class name has already existed!");
                request.setAttribute("clazz", c);
                request.setAttribute("allInstructors", userDAO.getAllInstructors());
                request.setAttribute("allCategories", settingDAO.getAllCategories());
                request.setAttribute("classCategories", classDAO.getCategoriesByClassId(classId));
                request.getRequestDispatcher("/WEB-INF/views/edit-class.jsp").forward(request, response);
                return;
            }
            classDAO.updateClass(c, categoryIds);

            request.getSession().setAttribute("successMessage", "Class updated successfully!");
            response.sendRedirect(request.getContextPath() + "/class-list");

        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "Error in updating class: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/class-list");
        }
    }
}
