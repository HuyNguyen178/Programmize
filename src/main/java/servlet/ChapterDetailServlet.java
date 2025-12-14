package servlet;

import dao.ChapterDAO;
import dao.LessonDAO;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Chapter;
import model.Lesson;

@WebServlet(name = "ChapterDetailServlet", urlPatterns = {"/chapter-detail"})
public class ChapterDetailServlet extends HttpServlet {

    private ChapterDAO chapterDAO;
    private LessonDAO lessonDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        chapterDAO = new ChapterDAO();
        lessonDAO = new LessonDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");

        if (idParam == null || idParam.trim().isEmpty()) {
            System.err.println("ChapterDetailServlet: Chapter ID is required.");
            response.sendRedirect(request.getContextPath() + "/");         //temp: delete /chapters
            return;
        }

        try {
            int chapterId = Integer.parseInt(idParam);

            Chapter chapter = chapterDAO.getChapterById(chapterId);

            if (chapter == null) {
                System.err.println("ChapterDetailServlet: Chapter not found with ID: " + chapterId);
                response.sendRedirect(request.getContextPath() + "/");
                return;
            }

            String courseName = chapterDAO.getCourseNameById(chapter.getCourseId());

            // Get all lessons for this chapter
            List<Lesson> lessons = lessonDAO.getLessonsByChapterId(chapterId);

            // Get filter parameters
            String typeFilter = request.getParameter("type");
            String statusFilter = request.getParameter("status");
            String searchQuery = request.getParameter("search");

            // Apply filters
            List<Lesson> filteredLessons = filterLessons(lessons, typeFilter, statusFilter, searchQuery);

            // Calculate statistics
            int totalLessons = lessons.size();
            int filteredCount = filteredLessons.size();
            int totalDuration = lessonDAO.getTotalDurationByChapterId(chapterId);
            String totalDurationFormatted = formatDuration(totalDuration);

            // Count by type
            long videoCount = lessons.stream().filter(l -> l.getLessonType() == Lesson.LessonType.VIDEO).count();
            long textCount = lessons.stream().filter(l -> l.getLessonType() == Lesson.LessonType.TEXT).count();
            long quizCount = lessons.stream().filter(l -> l.getLessonType() == Lesson.LessonType.QUIZ).count();
            long assignmentCount = lessons.stream().filter(l -> l.getLessonType() == Lesson.LessonType.ASSIGNMENT).count();

            // Count by status
            long publishedCount = lessons.stream().filter(l -> l.getStatus() != null && l.getStatus()).count();
            long draftCount = lessons.stream().filter(l -> l.getStatus() == null || !l.getStatus()).count();

            request.setAttribute("chapter", chapter);
            request.setAttribute("courseName", courseName);
            request.setAttribute("lessons", filteredLessons);
            request.setAttribute("totalLessons", totalLessons);
            request.setAttribute("filteredCount", filteredCount);
            request.setAttribute("totalDuration", totalDuration);
            request.setAttribute("totalDurationFormatted", totalDurationFormatted);

            request.setAttribute("videoCount", videoCount);
            request.setAttribute("textCount", textCount);
            request.setAttribute("quizCount", quizCount);
            request.setAttribute("assignmentCount", assignmentCount);
            request.setAttribute("publishedCount", publishedCount);
            request.setAttribute("draftCount", draftCount);

            request.setAttribute("currentType", typeFilter);
            request.setAttribute("currentStatus", statusFilter);
            request.setAttribute("currentSearch", searchQuery);

            request.getRequestDispatcher("/WEB-INF/views/chapter-detail.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            System.err.println("ChapterDetailServlet: Invalid chapter ID format - " + idParam);
            response.sendRedirect(request.getContextPath() + "/");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        String idParam = request.getParameter("id");

        if (idParam == null || idParam.trim().isEmpty()) {
            System.err.println("ChapterDetailServlet POST: Chapter ID is required.");
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        try {
            int chapterId = Integer.parseInt(idParam);

            if ("delete".equals(action)) {
                Chapter chapter = chapterDAO.getChapterById(chapterId);
                int courseId = (chapter != null) ? chapter.getCourseId() : 0;

                boolean deleted = chapterDAO.deleteChapter(chapterId);

                if (deleted) {
                    System.out.println("ChapterDetailServlet: Chapter deleted successfully - ID: " + chapterId);
                    request.getSession().setAttribute("successMessage", "Chapter deleted successfully!");
                    response.sendRedirect(request.getContextPath() + "/course-detail?id=" + courseId);
                } else {
                    System.err.println("ChapterDetailServlet: Failed to delete chapter - ID: " + chapterId);
                    request.getSession().setAttribute("errorMessage", "Failed to delete chapter.");
                    response.sendRedirect(request.getContextPath() + "/chapter-detail?id=" + chapterId);
                }
            } else if ("deleteLesson".equals(action)) {
                // lesson deletion
                String lessonIdParam = request.getParameter("lessonId");
                if (lessonIdParam != null && !lessonIdParam.trim().isEmpty()) {
                    int lessonId = Integer.parseInt(lessonIdParam);
                    boolean deleted = lessonDAO.deleteLesson(lessonId);

                    if (deleted) {
                        request.getSession().setAttribute("successMessage", "Lesson deleted successfully!");
                    } else {
                        request.getSession().setAttribute("errorMessage", "Failed to delete lesson.");
                    }
                }
                response.sendRedirect(request.getContextPath() + "/chapter-detail?id=" + chapterId);
            } else {
                response.sendRedirect(request.getContextPath() + "/chapter-detail?id=" + chapterId);
            }

        } catch (NumberFormatException e) {
            System.err.println("ChapterDetailServlet POST: Invalid ID format - " + idParam);
            response.sendRedirect(request.getContextPath() + "/");
        }
    }

    // filter lessons based on type, status, search query
    private List<Lesson> filterLessons(List<Lesson> lessons, String type, String status, String search) {
        return lessons.stream()
                .filter(lesson -> {
                    // Filter by type
                    if (type != null && !type.trim().isEmpty()) {
                        if (lesson.getLessonType() == null) return false;
                        if (!lesson.getLessonType().getValue().equalsIgnoreCase(type)) return false;
                    }
                    return true;
                })
                .filter(lesson -> {
                    // Filter by status
                    if (status != null && !status.trim().isEmpty()) {
                        boolean isPublished = lesson.getStatus() != null && lesson.getStatus();
                        if ("1".equals(status) && !isPublished) return false;
                        if ("0".equals(status) && isPublished) return false;
                    }
                    return true;
                })
                .filter(lesson -> {
                    // Filter by search query
                    if (search != null && !search.trim().isEmpty()) {
                        String searchLower = search.toLowerCase().trim();
                        String lessonName = lesson.getLessonName() != null ? lesson.getLessonName().toLowerCase() : "";
                        String content = lesson.getContent() != null ? lesson.getContent().toLowerCase() : "";
                        return lessonName.contains(searchLower) || content.contains(searchLower);
                    }
                    return true;
                })
                .collect(Collectors.toList());
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