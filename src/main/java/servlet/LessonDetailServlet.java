package servlet;

import dao.LessonDAO;
import dao.ChapterDAO;
import dao.CourseDAO;
import dao.QuizDAO;
import model.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/lesson-details")
public class LessonDetailServlet extends HttpServlet {

    private LessonDAO lessonDAO;
    private ChapterDAO chapterDAO;
    private CourseDAO courseDAO;
    private QuizDAO quizDAO;

    @Override
    public void init() throws ServletException {
        lessonDAO = new LessonDAO();
        chapterDAO = new ChapterDAO();
        courseDAO = new CourseDAO();
        quizDAO = new QuizDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String lessonIdParam = request.getParameter("id");

        if (lessonIdParam == null || lessonIdParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/public-courses");
            return;
        }

        try {
            int lessonId = Integer.parseInt(lessonIdParam);

            Lesson lesson = lessonDAO.getLessonById(lessonId);
            if (lesson == null) {
                request.getSession().setAttribute("errorMessage", "Lesson not found.");
                response.sendRedirect(request.getContextPath() + "/public-courses");
                return;
            }

            Chapter chapter = chapterDAO.getChapterById(lesson.getChapterId());
            if (chapter == null) {
                response.sendRedirect(request.getContextPath() + "/public-courses");
                return;
            }

            int courseId = chapter.getCourseId();
            String courseName = chapterDAO.getCourseNameById(courseId);

            List<Chapter> chapters = chapterDAO.getActiveChaptersByCourseId(courseId);
            Map<Integer, List<Lesson>> chapterLessonsMap = new HashMap<>();
            Map<Integer, List<Quiz>> chapterQuizzesMap = new HashMap<>();
            List<Lesson> allCourseLessons = new ArrayList<>();

            for (Chapter ch : chapters) {
                List<Lesson> chapterLessons = lessonDAO.getActiveLessonsByChapterId(ch.getChapterId());
                chapterLessonsMap.put(ch.getChapterId(), chapterLessons);
                allCourseLessons.addAll(chapterLessons);

                List<Quiz> chapterQuizzes = quizDAO.getQuizzesByChapterId(ch.getChapterId());
                chapterQuizzesMap.put(ch.getChapterId(), chapterQuizzes);
            }

            Lesson prevLesson = null;
            Lesson nextLesson = null;
            for (int i = 0; i < allCourseLessons.size(); i++) {
                if (allCourseLessons.get(i).getLessonId().equals(lessonId)) {
                    if (i > 0) prevLesson = allCourseLessons.get(i - 1);
                    if (i < allCourseLessons.size() - 1) nextLesson = allCourseLessons.get(i + 1);
                    break;
                }
            }

            User user = (User) request.getSession().getAttribute("loginUser");
            if (user == null) {
                user = (User) request.getSession().getAttribute("user");
            }

            boolean isEnrolled = false;
            boolean isAdminOrInstructor = false;

            if (user != null) {
                String role = user.getRoleName();
                isAdminOrInstructor = "Admin".equals(role) || "Instructor".equals(role);
                isEnrolled = courseDAO.isUserEnrolled(user.getId(), courseId);
            }

            if (!lesson.isPreview()) {
                if (user == null) {
                    request.getSession().setAttribute("errorMessage", "Please login to access this lesson.");
                    response.sendRedirect(request.getContextPath() + "/login");
                    return;
                }
                if (!isAdminOrInstructor && !isEnrolled) {
                    request.getSession().setAttribute("errorMessage", "Please enroll in this course to access this lesson.");
                    response.sendRedirect(request.getContextPath() + "/public-course-details?id=" + courseId);
                    return;
                }
            }


            request.setAttribute("lesson", lesson);
            request.setAttribute("chapterName", chapter.getChapterName());
            request.setAttribute("courseId", courseId);
            request.setAttribute("courseName", courseName);
            request.setAttribute("chapters", chapters);
            request.setAttribute("chapterLessonsMap", chapterLessonsMap);
            request.setAttribute("chapterQuizzesMap", chapterQuizzesMap);
            request.setAttribute("prevLesson", prevLesson);
            request.setAttribute("nextLesson", nextLesson);
            request.setAttribute("isEnrolled", isEnrolled);
            request.setAttribute("isAdminOrInstructor", isAdminOrInstructor);

            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/lesson-details.jsp");
            dispatcher.forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/public-courses");
        }
    }
}