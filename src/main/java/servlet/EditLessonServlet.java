package servlet;

import dao.LessonDAO;
import model.Lesson;
import model.Lesson.LessonType;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@WebServlet("/edit-lesson")
public class EditLessonServlet extends HttpServlet {
    private LessonDAO lessonDAO;

    @Override
    public void init() throws ServletException {
        lessonDAO = new LessonDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String lessonIdParam = request.getParameter("id");

        // check lessonId
        if (lessonIdParam == null || lessonIdParam.trim().isEmpty()) {
            System.err.println("EditLessonServlet: Lesson ID is required.");
            request.getSession().setAttribute("errorMessage", "Lesson ID is required.");
            response.sendRedirect(request.getContextPath() + "/course-content");
            return;
        }

        try {
            int lessonId = Integer.parseInt(lessonIdParam);

            // fetch lesson data from database
            Lesson lesson = lessonDAO.getLessonById(lessonId);

            if (lesson == null) {
                System.err.println("EditLessonServlet: Lesson not found with ID: " + lessonId);
                request.getSession().setAttribute("errorMessage", "Lesson not found.");
                response.sendRedirect(request.getContextPath() + "/course-content");
                return;
            }

            // fetch chapter list to dropdown (Format: [chapter_id, chapter_name, course_name])
            List<String[]> allChapters = lessonDAO.getAllChaptersForDropdown();

            // set attributes JSP
            request.setAttribute("lesson", lesson);
            request.setAttribute("allChapters", allChapters);

            // forward jsp
            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/edit-lesson.jsp");
            dispatcher.forward(request, response);

        } catch (NumberFormatException e) {
            System.err.println("EditLessonServlet: Invalid lesson ID format - " + lessonIdParam);
            request.getSession().setAttribute("errorMessage", "Invalid lesson ID format.");
            response.sendRedirect(request.getContextPath() + "/course-content");
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/course-content");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int chapterId = 0;
        int lessonId = 0;

        try {
            System.out.println("=== EditLessonServlet POST started ===");

            // Lấy lesson ID từ hidden field
            String lessonIdStr = request.getParameter("lessonId");
            if (lessonIdStr == null || lessonIdStr.trim().isEmpty()) {
                request.getSession().setAttribute("errorMessage", "Lesson ID is required.");
                response.sendRedirect(request.getContextPath() + "/course-content");
                return;
            }
            lessonId = Integer.parseInt(lessonIdStr);
            System.out.println("Editing lessonId: " + lessonId);

            // fetch parameters from form
            String lessonName = request.getParameter("lessonName");
            String lessonTypeStr = request.getParameter("lessonType");
            String content = request.getParameter("content");
            String videoUrl = request.getParameter("videoUrl");

            // fetch chapter ID
            String chapterIdStr = request.getParameter("chapterId");
            if (chapterIdStr == null || chapterIdStr.trim().isEmpty()) {
                request.getSession().setAttribute("errorMessage", "Please select a chapter.");
                response.sendRedirect(request.getContextPath() + "/edit-lesson?id=" + lessonId);
                return;
            }
            chapterId = Integer.parseInt(chapterIdStr);

            // duration
            int duration = 0;
            String durationStr = request.getParameter("duration");
            if (durationStr != null && !durationStr.trim().isEmpty()) {
                duration = Integer.parseInt(durationStr);
            }

            // fetch order index
            int orderIndex = 1;
            String orderIndexStr = request.getParameter("orderIndex");
            if (orderIndexStr != null && !orderIndexStr.trim().isEmpty()) {
                orderIndex = Integer.parseInt(orderIndexStr);
            }

            // fetch status
            boolean status = Boolean.parseBoolean(request.getParameter("status"));

            // fetch isPreview
            boolean isPreview = "true".equals(request.getParameter("isPreview"));

            // create new lesson object with new data
            Lesson lesson = new Lesson();
            lesson.setLessonId(lessonId);
            lesson.setChapterId(chapterId);
            lesson.setLessonName(lessonName);
            lesson.setLessonType(LessonType.fromString(lessonTypeStr));
            lesson.setContent(content);
            lesson.setVideoUrl(videoUrl);
            lesson.setDuration(duration);
            lesson.setOrderIndex(orderIndex);
            lesson.setPreview(isPreview);
            lesson.setStatus(status);
            lesson.setUpdatedAt(new Date());

            System.out.println("Lesson object to update: " + lesson);

            // call dao update database
            boolean updated = lessonDAO.updateLesson(lesson);
            System.out.println("Update result: " + updated);

            if (updated) {
                String chapterName = lessonDAO.getChapterNameById(chapterId);
                request.getSession().setAttribute("successMessage",
                        "Lesson '" + lessonName + "' updated successfully!");
            } else {
                request.getSession().setAttribute("errorMessage",
                        "Failed to update lesson. Please try again.");
            }

            // redirect
            response.sendRedirect(request.getContextPath() + "/chapter-detail?id=" + chapterId);

        } catch (NumberFormatException e) {
            System.err.println("NumberFormatException in EditLessonServlet: " + e.getMessage());
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage",
                    "Invalid input format. Please check your data.");
            if (lessonId > 0) {
                response.sendRedirect(request.getContextPath() + "/edit-lesson?id=" + lessonId);
            } else {
                response.sendRedirect(request.getContextPath() + "/course-content");
            }
        } catch (Exception e) {
            System.err.println("Exception in EditLessonServlet: " + e.getMessage());
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage",
                    "An error occurred: " + e.getMessage());
            if (lessonId > 0) {
                response.sendRedirect(request.getContextPath() + "/edit-lesson?id=" + lessonId);
            } else {
                response.sendRedirect(request.getContextPath() + "/course-content");
            }
        }
    }
}