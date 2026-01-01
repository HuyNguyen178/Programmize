package servlet;

import dao.ClassDAO;
import dao.EnrollmentDAO;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Class;
import model.ClassEnrollment;
import model.Setting;
import model.User;
import utils.SessionConfig;

import java.io.IOException;
import java.util.List;

@WebServlet("/my-class-details")
public class MyClassDetailsServlet extends HttpServlet {
    private ClassDAO classDAO;
    private EnrollmentDAO enrollmentDAO;

    @Override
    public void init(ServletConfig config) throws ServletException {
        classDAO = new ClassDAO();
        enrollmentDAO = new EnrollmentDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute(SessionConfig.ATTR_LOGIN_USER);
        String classId = request.getParameter("id");
        Class clazz = classDAO.getClassById(Integer.parseInt(classId));
        List<Setting> categories = classDAO.getCategoriesByClassId(Integer.parseInt(classId));
        ClassEnrollment classEnrollment = enrollmentDAO.getEnrollmentByUserIdAndClassId(user.getId(), Integer.parseInt(classId));
        request.setAttribute("clazz", clazz);
        request.setAttribute("categories", categories);
        request.setAttribute("classEnrollment", classEnrollment);
        request.getRequestDispatcher("/WEB-INF/views/my-class-details.jsp").forward(request, response);
    }
}
