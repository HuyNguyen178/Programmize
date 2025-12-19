package servlet;

import dao.LessonDAO;
import dao.ChapterDAO;
import dao.CourseDAO;
import model.Lesson;
import model.Chapter;
import model.Course;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/lesson-detail")
public class LessonDetailServlet extends HttpServlet {

    private LessonDAO lessonDAO;
    private ChapterDAO chapterDAO;
    private CourseDAO courseDAO;

    @Override
    public void init() throws ServletException {
        lessonDAO = new LessonDAO();
        chapterDAO = new ChapterDAO();
        courseDAO = new CourseDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String lessonIdParam = request.getParameter("id");

        // validate lesson ID
        if (lessonIdParam == null || lessonIdParam.trim().isEmpty()) {
            System.err.println("LessonDetailServlet: Lesson ID is required.");
            response.sendRedirect(request.getContextPath() + "/public-courses");
            return;
        }

        try {
            int lessonId = Integer.parseInt(lessonIdParam);
            System.out.println("=== LessonDetailServlet: Loading lesson ID " + lessonId + " ===");

            // Get the lesson
            Lesson lesson = lessonDAO.getLessonById(lessonId);
            if (lesson == null) {
                System.err.println("LessonDetailServlet: Lesson not found - ID: " + lessonId);
                request.getSession().setAttribute("errorMessage", "Lesson not found.");
                response.sendRedirect(request.getContextPath() + "/public-courses");
                return;
            }

            // chapter info
            Chapter chapter = chapterDAO.getChapterById(lesson.getChapterId());
            if (chapter == null) {
                System.err.println("LessonDetailServlet: Chapter not found for lesson - ID: " + lessonId);
                response.sendRedirect(request.getContextPath() + "/public-courses");
                return;
            }

            // course info
            int courseId = chapter.getCourseId();
            String courseName = chapterDAO.getCourseNameById(courseId);

            // all chapters for this course
            List<Chapter> chapters = chapterDAO.getChaptersByCourseId(courseId);

            // all lessons for each chapter (for sidebar)
            Map<Integer, List<Lesson>> chapterLessonsMap = new HashMap<>();
            List<Lesson> allCourseLessons = new ArrayList<>();

            for (Chapter ch : chapters) {
                List<Lesson> chapterLessons = lessonDAO.getActiveLessonsByChapterId(ch.getChapterId());
                chapterLessonsMap.put(ch.getChapterId(), chapterLessons);
                allCourseLessons.addAll(chapterLessons);
            }

            // find previous, next lessons
            Lesson prevLesson = null;
            Lesson nextLesson = null;

            for (int i = 0; i < allCourseLessons.size(); i++) {
                if (allCourseLessons.get(i).getLessonId().equals(lessonId)) {
                    if (i > 0) {
                        prevLesson = allCourseLessons.get(i - 1);
                    }
                    if (i < allCourseLessons.size() - 1) {
                        nextLesson = allCourseLessons.get(i + 1);
                    }
                    break;
                }
            }

            // set attributes
            request.setAttribute("lesson", lesson);
            request.setAttribute("chapterName", chapter.getChapterName());
            request.setAttribute("courseId", courseId);
            request.setAttribute("courseName", courseName);
            request.setAttribute("chapters", chapters);
            request.setAttribute("chapterLessonsMap", chapterLessonsMap);
            request.setAttribute("prevLesson", prevLesson);
            request.setAttribute("nextLesson", nextLesson);

            System.out.println("LessonDetailServlet: Loaded lesson '" + lesson.getLessonName() +
                    "' from chapter '" + chapter.getChapterName() + "'");

            // forward
            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/lesson-detail.jsp");
            dispatcher.forward(request, response);

        } catch (NumberFormatException e) {
            System.err.println("LessonDetailServlet: Invalid lesson ID format - " + lessonIdParam);
            response.sendRedirect(request.getContextPath() + "/public-courses");
        } catch (Exception e) {
            System.err.println("LessonDetailServlet: Error - " + e.getMessage());
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/public-courses");
        }
    }
}