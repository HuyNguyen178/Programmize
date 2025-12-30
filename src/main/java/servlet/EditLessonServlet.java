package servlet;

import dao.LessonDAO;
import dao.ChapterDAO;
import model.Lesson;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.annotation.MultipartConfig;
import service.FileValidationService;
import service.FileValidationService.ValidationResult;

import java.io.File;
import java.io.IOException;
import java.util.List;

@WebServlet("/edit-lesson")
@MultipartConfig(
    maxFileSize = 104857600,     // 100MB for videos
    maxRequestSize = 115343360   // 110MB
)
public class EditLessonServlet extends HttpServlet {
    private LessonDAO lessonDAO;
    private ChapterDAO chapterDAO;
    private FileValidationService fileValidator;

    @Override
    public void init() throws ServletException {
        lessonDAO = new LessonDAO();
        chapterDAO = new ChapterDAO();
        fileValidator = FileValidationService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null) {
            response.sendRedirect(request.getContextPath() + "/courses");
            return;
        }

        int lessonId = Integer.parseInt(idParam);
        Lesson lesson = lessonDAO.getLessonById(lessonId);  // Fixed method name

        List<String[]> allChapters = chapterDAO.getAllChaptersWithCourseName();
        request.setAttribute("allChapters", allChapters);

        request.setAttribute("lesson", lesson);
        request.getRequestDispatcher("/WEB-INF/views/edit-lesson.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int lessonId = Integer.parseInt(request.getParameter("lessonId"));
            Lesson lesson = lessonDAO.getLessonById(lessonId);  // Fixed method name

            if (lesson == null) {
                response.sendRedirect(request.getContextPath() + "/courses?error=notfound");
                return;
            }

            String lessonName = request.getParameter("lessonName");
            String content = request.getParameter("content");
            String lessonType = request.getParameter("lessonType");
            String videoUrl = request.getParameter("videoUrl");

            int orderNumber = lesson.getOrderIndex();  // Fixed: use getOrderIndex()
            String orderStr = request.getParameter("orderNumber");
            if (orderStr != null && !orderStr.trim().isEmpty()) {
                orderNumber = Integer.parseInt(orderStr);
            }

            int duration = lesson.getDuration();
            String durationStr = request.getParameter("duration");
            if (durationStr != null && !durationStr.trim().isEmpty()) {
                duration = Integer.parseInt(durationStr);
            }

            // Handle file upload with validation
            Part filePart = request.getPart("lessonFile");

            if (filePart != null && filePart.getSize() > 0) {
                String filename = filePart.getSubmittedFileName();
                String contentType = filePart.getContentType();
                long fileSize = filePart.getSize();

                ValidationResult validationResult = fileValidator.validate(
                    filename, contentType, fileSize, filePart.getInputStream()
                );

                if (!validationResult.isValid()) {
                    request.setAttribute("error", "File upload failed: " + validationResult.getMessage());
                    request.getRequestDispatcher("/WEB-INF/views/edit-lesson.jsp").forward(request, response);
                    return;
                }

                String subDir = contentType.startsWith("video/") ? "videos" : "documents";
                String safeFilename = fileValidator.sanitizeFilename(filename);
                String uploadDir = getServletContext().getRealPath("/uploads/lessons/" + subDir);
                File dir = new File(uploadDir);
                if (!dir.exists()) dir.mkdirs();

                filePart.write(uploadDir + File.separator + safeFilename);
                videoUrl = "uploads/lessons/" + subDir + "/" + safeFilename;
            } else if (videoUrl == null || videoUrl.trim().isEmpty()) {
                videoUrl = lesson.getVideoUrl();
            }

            // Update lesson object
            lesson.setLessonName(lessonName);
            lesson.setContent(content);
            lesson.setLessonTypeFromString(lessonType);  // Fixed: use setLessonTypeFromString()
            lesson.setVideoUrl(videoUrl);
            lesson.setOrderIndex(orderNumber);  // Fixed: use setOrderIndex()
            lesson.setDuration(duration);

            boolean updated = lessonDAO.updateLesson(lesson);  // Fixed method name

            if (updated) {
                response.sendRedirect(request.getContextPath() + "/chapter-details?id=" + lesson.getChapterId() + "&success=lesson_updated");
            } else {
                request.setAttribute("error", "Failed to update lesson");
                request.getRequestDispatcher("/WEB-INF/views/edit-lesson.jsp").forward(request, response);
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
            request.setAttribute("error", "Invalid number format: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/edit-lesson.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error updating lesson: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/edit-lesson.jsp").forward(request, response);
        }
    }
}