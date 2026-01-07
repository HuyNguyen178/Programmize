package servlet;

import dao.QuizDAO;
import dao.ChapterDAO;
import dao.CourseDAO;
import dao.LessonDAO;
import model.Quiz;
import model.Chapter;
import model.Lesson;
import model.User;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/quiz-details")
public class QuizDetailServlet extends HttpServlet {

    private QuizDAO quizDAO;
    private ChapterDAO chapterDAO;
    private CourseDAO courseDAO;
    private LessonDAO lessonDAO;

    @Override
    public void init() throws ServletException {
        quizDAO = new QuizDAO();
        chapterDAO = new ChapterDAO();
        courseDAO = new CourseDAO();
        lessonDAO = new LessonDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String quizIdParam = request.getParameter("id");
        if (quizIdParam == null || quizIdParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/public-courses");
            return;
        }

        try {
            int quizId = Integer.parseInt(quizIdParam);
            Quiz quiz = quizDAO.getQuizById(quizId);

            if (quiz == null) {
                response.sendRedirect(request.getContextPath() + "/public-courses");
                return;
            }

            int chapterId = quiz.getChapter().getChapterId();
            Chapter currentChapter = chapterDAO.getChapterById(chapterId);
            int courseId = currentChapter.getCourseId();

            List<Chapter> chapters = chapterDAO.getActiveChaptersByCourseId(courseId);
            Map<Integer, List<Lesson>> chapterLessonsMap = new HashMap<>();
            Map<Integer, List<Quiz>> chapterQuizzesMap = new HashMap<>();

            for (Chapter ch : chapters) {
                chapterLessonsMap.put(ch.getChapterId(), lessonDAO.getActiveLessonsByChapterId(ch.getChapterId()));

                List<Quiz> quizzes = quizDAO.getQuizzesByChapterId(ch.getChapterId());
                chapterQuizzesMap.put(ch.getChapterId(), quizzes);
            }

            User user = (User) request.getSession().getAttribute("loginUser");
            if (user == null) user = (User) request.getSession().getAttribute("user");

            boolean isEnrolled = false;
            boolean isAdminOrInstructor = false;

            if (user != null) {
                isEnrolled = courseDAO.isUserEnrolled(user.getId(), courseId);
                String role = user.getRoleName();
                isAdminOrInstructor = "Admin".equals(role) || "Instructor".equals(role);
            }

            // Gửi dữ liệu sang JSP
            request.setAttribute("quiz", quiz);
            request.setAttribute("chapterName", currentChapter.getChapterName());
            request.setAttribute("courseId", courseId);
            request.setAttribute("courseName", chapterDAO.getCourseNameById(courseId));
            request.setAttribute("chapters", chapters);
            request.setAttribute("chapterLessonsMap", chapterLessonsMap);
            request.setAttribute("chapterQuizzesMap", chapterQuizzesMap);
            request.setAttribute("isEnrolled", isEnrolled);
            request.setAttribute("isAdminOrInstructor", isAdminOrInstructor);

            request.getRequestDispatcher("/WEB-INF/views/quiz-details.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/public-courses");
        }
    }
}