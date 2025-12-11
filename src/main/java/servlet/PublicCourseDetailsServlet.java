package servlet;

import dao.CourseDAO;
import dao.ChapterDAO;
import dao.LessonDAO;
import model.Course;
import model.Chapter;
import model.Lesson;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@WebServlet("/public-course-details")
public class PublicCourseDetailsServlet extends HttpServlet {
    private CourseDAO publicCourseDAO;
    private ChapterDAO chapterDAO;
    private LessonDAO lessonDAO;

    @Override
    public void init() throws ServletException {
        publicCourseDAO = new CourseDAO();
        chapterDAO = new ChapterDAO();
        lessonDAO = new LessonDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // get course ID from request parameter
        String courseIdStr = request.getParameter("id");

        // validate course ID
        if (courseIdStr == null || courseIdStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/public-courses");
            return;
        }

        try {
            int courseId = Integer.parseInt(courseIdStr);

            // get course details from database
            Course course = publicCourseDAO.getCourseById(courseId);

            // calculate some display values
            String priceDisplay = getPriceDisplay(course);
            String durationDisplay = getDurationDisplay(course.getDuration());

            // get enrollment count
            int enrollmentCount = publicCourseDAO.getCourseEnrollmentCount(courseId);

            // get chapters for this course
            List<Chapter> chapters = chapterDAO.getChaptersByCourseId(courseId);

            // map to store lessons for each chapter: chapterId -> List<Lesson>
            Map<Integer, List<Lesson>> chapterLessonsMap = new HashMap<>();

            // calculate totals
            int totalChapters = chapters.size();
            int totalLessons = 0;
            int totalDurationSeconds = 0;

            for (Chapter chapter : chapters) {
                // get active lessons for this chapter
                List<Lesson> lessons = lessonDAO.getActiveLessonsByChapterId(chapter.getChapterId());
                chapterLessonsMap.put(chapter.getChapterId(), lessons);

                // count lessons
                int lessonCount = lessons.size();
                chapter.setLessonCount(lessonCount);
                totalLessons += lessonCount;

                // sum duration
                for (Lesson lesson : lessons) {
                    totalDurationSeconds += lesson.getDuration();
                }
            }

            // format total duration from actual lessons
            String totalDurationFromLessons = formatDuration(totalDurationSeconds);

            request.setAttribute("course", course);
            request.setAttribute("priceDisplay", priceDisplay);
            request.setAttribute("durationDisplay", durationDisplay);
            request.setAttribute("enrollmentCount", enrollmentCount);
            request.setAttribute("chapters", chapters);
            request.setAttribute("chapterLessonsMap", chapterLessonsMap);
            request.setAttribute("totalChapters", totalChapters);
            request.setAttribute("totalLessons", totalLessons);
            request.setAttribute("totalDurationSeconds", totalDurationSeconds);
            request.setAttribute("totalDurationFromLessons", totalDurationFromLessons);

            // forward to JSP
            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/public-course-details.jsp");
            dispatcher.forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/public-courses");
        }
    }


    private String getPriceDisplay(Course course) {
        if (course.getSalePrice() != null && course.getSalePrice().doubleValue() > 0) {
            return "$" + String.format("%.2f", course.getSalePrice());
        } else if (course.getListedPrice() != null && course.getListedPrice().doubleValue() > 0) {
            return "$" + String.format("%.2f", course.getListedPrice());
        } else {
            return "FREE";
        }
    }


    private String getDurationDisplay(Integer totalMinutes) {
        if (totalMinutes == null || totalMinutes <= 0) {
            return "Self-paced";
        }

        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;

        if (hours > 0 && minutes > 0) {
            return hours + " hours " + minutes + " minutes";
        } else if (hours > 0) {
            return hours + " hours";
        } else {
            return minutes + " minutes";
        }
    }


    private String formatDuration(int totalSeconds) {
        if (totalSeconds <= 0) {
            return "0 min";
        }

        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;

        if (hours > 0 && minutes > 0) {
            return hours + " hr " + minutes + " min";
        } else if (hours > 0) {
            return hours + " hr";
        } else if (minutes > 0) {
            return minutes + " min";
        } else {
            return totalSeconds + " sec";
        }
    }
}