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

@WebServlet("/add-lesson")
@MultipartConfig(
    maxFileSize = 104857600,
    maxRequestSize = 115343360
)
public class AddLessonServlet extends HttpServlet {
    private LessonDAO lessonDAO;
    private ChapterDAO chapterDAO;

    @Override
    public void init() throws ServletException {
        lessonDAO = new LessonDAO();
        chapterDAO = new ChapterDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<String[]> allChapters = chapterDAO.getAllChaptersWithCourseName();
        request.setAttribute("allChapters", allChapters);

        String chapterIdParam = request.getParameter("chapterId");
        if (chapterIdParam != null && !chapterIdParam.isEmpty()) {
            int chapterId = Integer.parseInt(chapterIdParam);
            int nextOrder = lessonDAO.getNextOrderIndex(chapterId);
            request.setAttribute("chapterId", chapterId);
            request.setAttribute("nextOrderIndex", nextOrder);
        } else {
            request.setAttribute("nextOrderIndex", 1);
        }

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

            String pdfUrl = null;

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
                                "resource_type", "raw",     // BẮT BUỘC cho PDF
                                "folder", "lessons/pdfs",
                                "public_id", System.currentTimeMillis() + "_lesson_pdf"
                        )
                );

                pdfUrl = uploadResult.get("secure_url").toString();
            }

            // Create new lesson object
            Lesson lesson = new Lesson();
            lesson.setChapterId(chapterId);
            lesson.setLessonName(lessonName);
            lesson.setContent(content);
            lesson.setLessonTypeFromString(lessonType);
            lesson.setVideoUrl(videoUrl);
            lesson.setPdfUrl(pdfUrl);
            lesson.setOrderIndex(orderNumber);
            lesson.setDuration(duration);
            lesson.setPreview(isPreview);
            lesson.setStatus(status);
            lesson.setCreatedAt(new java.util.Date());
            lesson.setUpdatedAt(new java.util.Date());

            int lessonId = lessonDAO.insertLesson(lesson);

            if (lessonId > 0) {
                response.sendRedirect(request.getContextPath() + "/chapter-details?id=" + chapterId + "&success=lesson_added");
            } else {
                request.getSession().setAttribute("errorMessage", "Failed to add lesson");
                request.setAttribute("lessonName", lessonName);
                request.setAttribute("content", content);
                request.setAttribute("lessonType", lessonType);
                request.setAttribute("videoUrl", videoUrl);
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