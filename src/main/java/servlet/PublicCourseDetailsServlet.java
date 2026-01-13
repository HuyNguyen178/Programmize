package servlet;

import dao.CourseDAO;
import dao.ChapterDAO;
import dao.LessonDAO;
import model.Course;
import model.Chapter;
import model.Lesson;
import model.User;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;

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
            Course course = publicCourseDAO.getActiveCourseById(courseId);

            // calculate some display values
            String priceDisplay = getPriceDisplay(course);
            String durationDisplay = getDurationDisplay(course.getDuration());

            // get enrollment count
            int enrollmentCount = publicCourseDAO.getCourseEnrollmentCount(courseId);

            // get chapters for this course
            List<Chapter> chapters = chapterDAO.getActiveChaptersByCourseId(courseId);

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
            String html = course.getDescription();
            String decoded = StringEscapeUtils.unescapeHtml4(html);
            String descPlainText = Jsoup.parse(decoded).text();

            request.setAttribute("course", course);
            request.setAttribute("description", descPlainText);
            request.setAttribute("priceDisplay", priceDisplay);
            request.setAttribute("durationDisplay", durationDisplay);
            request.setAttribute("enrollmentCount", enrollmentCount);
            request.setAttribute("chapters", chapters);
            request.setAttribute("chapterLessonsMap", chapterLessonsMap);
            request.setAttribute("totalChapters", totalChapters);
            request.setAttribute("totalLessons", totalLessons);
            request.setAttribute("totalDurationSeconds", totalDurationSeconds);
            request.setAttribute("totalDurationFromLessons", totalDurationFromLessons);

            boolean isEnrolled = false;
            boolean isAdminOrInstructor = false;

            HttpSession session = request.getSession(false);
            if (session != null) {
                User user = (User) session.getAttribute("loginUser");
                if (user != null) {
                    String role = user.getRoleName();
                    if ("Admin".equals(role) || "Instructor".equals(role)) {
                        isAdminOrInstructor = true;
                    }
                    // check if enrolled
                    isEnrolled = publicCourseDAO.isUserEnrolled(user.getId(), courseId);
                }
            }
            request.setAttribute("isEnrolled", isEnrolled);
            request.setAttribute("isAdminOrInstructor", isAdminOrInstructor);

            // forward
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
        return totalSeconds + " min";
    }
}