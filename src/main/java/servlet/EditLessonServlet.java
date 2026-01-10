package servlet;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import dao.LessonDAO;
import dao.ChapterDAO;
import model.Lesson;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.annotation.MultipartConfig;
import service.FileValidationService;
import service.FileValidationService.ValidationResult;
import utils.CloudinaryUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

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

            int orderNumber = lesson.getOrderIndex();
            String orderStr = request.getParameter("orderNumber");
            if (orderStr != null && !orderStr.trim().isEmpty()) {
                orderNumber = Integer.parseInt(orderStr);
            }

            int duration = lesson.getDuration();
            String durationStr = request.getParameter("duration");
            if (durationStr != null && !durationStr.trim().isEmpty()) {
                duration = Integer.parseInt(durationStr);
            }

            String pdfUrl = lesson.getPdfUrl();
            Part pdfPart = request.getPart("pdfFile");
            if (pdfPart != null && pdfPart.getSize() > 0) {

                if (!"application/pdf".equals(pdfPart.getContentType())) {
                    request.getSession().setAttribute("errorMessage", "Only PDF files are allowed");
                    request.setAttribute("lessonName", lessonName);
                    request.setAttribute("content", content);
                    request.setAttribute("lessonType", lessonType);
                    request.setAttribute("videoUrl", videoUrl);
                    request.getRequestDispatcher("/WEB-INF/views/add-lesson.jsp").forward(request, response);
                    return;
                }

                Cloudinary cloudinary = CloudinaryUtil.getCloudinary();

                byte[] fileBytes = pdfPart.getInputStream().readAllBytes();

                Map uploadResult = cloudinary.uploader().upload(
                        fileBytes,
                        ObjectUtils.asMap(
                                "resource_type", "raw",
                                "public_id", "lesson_" + lessonId,
                                "overwrite", true,
                                "folder", "lessons/pdfs"
                        )
                );

                pdfUrl = uploadResult.get("secure_url").toString();
            }

            // Update lesson object
            lesson.setLessonName(lessonName);
            lesson.setContent(content);
            lesson.setLessonTypeFromString(lessonType);  // Fixed: use setLessonTypeFromString()
            lesson.setVideoUrl(videoUrl);
            lesson.setPdfUrl(pdfUrl);
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