package servlet;

import dao.ChapterDAO;
import dao.EnrollmentDAO;
import dao.LessonDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Chapter;
import model.Course;
import model.Lesson;
import model.User;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@WebServlet("/my-enrollments")
public class MyEnrollmentServlet extends HttpServlet {
    private EnrollmentDAO enrollmentDAO;
    private ChapterDAO chapterDAO;
    private LessonDAO lessonDAO;

    @Override
    public void init() {
        enrollmentDAO = new EnrollmentDAO();
        chapterDAO = new ChapterDAO();
        lessonDAO = new LessonDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("loginUser");
        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String keyword = request.getParameter("keyword");
        String type = request.getParameter("type");
        String status = request.getParameter("status");

        List<Object> enrollments = enrollmentDAO.getAllEnrollmentsWithDetails(
                user.getId(), keyword, type, status);

        Map<Integer, Integer> firstLessonMap = new HashMap<>();

        for (Object obj : enrollments) {
            Map<String, Object> enrollment = (Map<String, Object>) obj;

            String type1 = (String) enrollment.get("type");

            if (!"COURSE".equals(type1)) continue;

            int courseId = (Integer) enrollment.get("itemId");

            // fetch chapters of course
            List<Chapter> chapters = chapterDAO.getChaptersByCourseId(courseId);
            if (!chapters.isEmpty()) {
                List<Lesson> lessons =
                        lessonDAO.getActiveLessonsByChapterId(chapters.get(0).getChapterId());

                if (!lessons.isEmpty()) {
                    firstLessonMap.put(courseId, lessons.get(0).getLessonId());
                }
            }
        }

        request.setAttribute("firstLessonMap", firstLessonMap);
        request.setAttribute("enrollments", enrollments);
        request.setAttribute("keyword", keyword);
        request.setAttribute("type", type);
        request.setAttribute("status", status);

        request.getRequestDispatcher("WEB-INF/views/my-enrollment.jsp").forward(request, response);
    }
}