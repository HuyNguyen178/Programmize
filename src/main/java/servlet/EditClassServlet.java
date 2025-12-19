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

            classDAO.updateClass(c, categoryIds);
            request.getSession().setAttribute("successMessage", "Class added successfully!");
            response.sendRedirect(request.getContextPath() + "/class-list");
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/edit-class?id=" + request.getParameter("classId"));
        }
    }
}
