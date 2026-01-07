package servlet;

import dao.ClassDAO;
import dao.EnrollmentDAO;
import dao.SyllabusDAO;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.*;
import configuration.SessionConfig;
import model.Class;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@WebServlet("/my-class-details")
public class MyClassDetailsServlet extends HttpServlet {
    private ClassDAO classDAO;
    private EnrollmentDAO enrollmentDAO;
    private SyllabusDAO syllabusDAO;

    @Override
    public void init(ServletConfig config) throws ServletException {
        classDAO = new ClassDAO();
        enrollmentDAO = new EnrollmentDAO();
        syllabusDAO = new SyllabusDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute(SessionConfig.ATTR_LOGIN_USER);
        String classId = request.getParameter("id");
        Class clazz = classDAO.getClassByUserIdAndClassId(Integer.parseInt(classId), user.getId());
        if (clazz == null) {
            response.sendError(404);
            return;
        }
        Date now = new Date();
        if (now.before(clazz.getStartDate())) {
            clazz.setClassStatus("Upcoming");
        }
        else if (now.after(clazz.getEndDate())) {
            clazz.setClassStatus("Completed");
        }
        else {
            clazz.setClassStatus("Ongoing");
        }
        Syllabus syllabus = syllabusDAO.getSyllabusByClassId(Integer.parseInt(classId));
        List<Setting> categories = classDAO.getCategoriesByClassId(Integer.parseInt(classId));
        ClassEnrollment classEnrollment = enrollmentDAO.getEnrollmentByUserIdAndClassId(user.getId(), Integer.parseInt(classId));
        request.setAttribute("syllabus", syllabus);
        request.setAttribute("clazz", clazz);
        request.setAttribute("categories", categories);
        request.setAttribute("classEnrollment", classEnrollment);
        request.getRequestDispatcher("/WEB-INF/views/my-class-details.jsp").forward(request, response);
    }
}
