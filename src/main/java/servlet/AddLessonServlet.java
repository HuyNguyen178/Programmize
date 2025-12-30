package servlet;

import dao.LessonDAO;
import model.Lesson;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.annotation.MultipartConfig;
import service.FileValidationService;
import service.FileValidationService.ValidationResult;

import java.io.File;
import java.io.IOException;

@WebServlet("/add-lesson")
@MultipartConfig(
    maxFileSize = 104857600,     // 100MB for videos
    maxRequestSize = 115343360   // 110MB
)
public class AddLessonServlet extends HttpServlet {
    private LessonDAO lessonDAO;
    private FileValidationService fileValidator;

    @Override
    public void init() throws ServletException {
        lessonDAO = new LessonDAO();
        fileValidator = FileValidationService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String chapterIdParam = request.getParameter("chapterId");
        if (chapterIdParam == null) {
            response.sendRedirect(request.getContextPath() + "/courses");
            return;
        }

        int chapterId = Integer.parseInt(chapterIdParam);
        int nextOrder = lessonDAO.getNextOrderIndex(chapterId);
        String chapterName = lessonDAO.getChapterNameById(chapterId);

        request.setAttribute("chapterId", chapterId);
        request.setAttribute("chapterName", chapterName);
        request.setAttribute("nextOrderIndex", nextOrder);
        request.getRequestDispatcher("/WEB-INF/views/add-lesson.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int chapterId = Integer.parseInt(request.getParameter("chapterId"));
            String lessonName = request.getParameter("lessonName");
            String content = request.getParameter("content");
            String lessonType = request.getParameter("lessonType");
            String videoUrl = request.getParameter("videoUrl");

            int orderNumber = lessonDAO.getNextOrderIndex(chapterId);
            String orderStr = request.getParameter("orderNumber");
            if (orderStr != null && !orderStr.trim().isEmpty()) {
                orderNumber = Integer.parseInt(orderStr);
            }

            int duration = 0;
            String durationStr = request.getParameter("duration");
            if (durationStr != null && !durationStr.trim().isEmpty()) {
                duration = Integer.parseInt(durationStr);
            }

            boolean isPreview = "true".equals(request.getParameter("isPreview"));
            boolean status = "true".equals(request.getParameter("status"));

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
                    request.setAttribute("chapterId", chapterId);
                    request.getRequestDispatcher("/WEB-INF/views/add-lesson.jsp").forward(request, response);
                    return;
                }

                String subDir = contentType.startsWith("video/") ? "videos" : "documents";
                String safeFilename = fileValidator.sanitizeFilename(filename);
                String uploadDir = getServletContext().getRealPath("/uploads/lessons/" + subDir);
                File dir = new File(uploadDir);
                if (!dir.exists()) dir.mkdirs();

                filePart.write(uploadDir + File.separator + safeFilename);
                videoUrl = "uploads/lessons/" + subDir + "/" + safeFilename;
            }

            // Create new lesson object
            Lesson lesson = new Lesson();
            lesson.setChapterId(chapterId);
            lesson.setLessonName(lessonName);
            lesson.setContent(content);
            lesson.setLessonTypeFromString(lessonType);
            lesson.setVideoUrl(videoUrl);
            lesson.setOrderIndex(orderNumber);
            lesson.setDuration(duration);
            lesson.setPreview(isPreview);
            lesson.setStatus(status);
            lesson.setCreatedAt(new java.util.Date());
            lesson.setUpdatedAt(new java.util.Date());

            int lessonId = lessonDAO.insertLesson(lesson);

            if (lessonId > 0) {
                response.sendRedirect(request.getContextPath() + "/chapter?id=" + chapterId + "&success=lesson_added");
            } else {
                request.setAttribute("error", "Failed to add lesson");
                request.setAttribute("chapterId", chapterId);
                request.getRequestDispatcher("/WEB-INF/views/add-lesson.jsp").forward(request, response);
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
            request.setAttribute("error", "Invalid number format: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/add-lesson.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error adding lesson: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/add-lesson.jsp").forward(request, response);
        }
    }
}