package servlet;

import dao.CourseDAO;
import dao.StudentDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;

import java.io.IOException;
import java.util.List;

@WebServlet("/add-course-student")
public class AddCourseStudentServlet extends HttpServlet {
    private StudentDAO studentDAO;
    private CourseDAO courseDAO;

    @Override
    public void init() {
        studentDAO = new StudentDAO();
        courseDAO = new CourseDAO();
    }

//    protected void doGet (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        HttpSession session = request.getSession();
//        User loginUser = (User) session.getAttribute("loginUser");
//        List<String> courseNamesList = studentDAO.getAllCourseNames(loginUser.getId());
//        request.setAttribute("courseNamesList", courseNamesList);
//        String classIdParam = request.getParameter("courseId");
//        if (classIdParam != null && !classIdParam.isEmpty()) {
//            try {
//                int classId = Integer.parseInt(classIdParam);
//                String targetName = courseDAO.getCourseById(classId).getCourseName();
//                request.setAttribute("targetClassName", targetName);
//            } catch (NumberFormatException e) {
//                e.printStackTrace();
//            }
//        }
//        request.getRequestDispatcher("/WEB-INF/views/add-student.jsp").forward(request, response);
//    }
}
