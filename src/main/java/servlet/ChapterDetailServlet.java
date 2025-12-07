package servlet;

import dao.ChapterDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Chapter;

@WebServlet(name = "ChapterDetailServlet", urlPatterns = {"/chapter-detail"})
public class ChapterDetailServlet extends HttpServlet {

    private ChapterDAO chapterDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        chapterDAO = new ChapterDAO();
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
            int totalLessons = chapterDAO.countLessonsByChapterId(chapterId);

            request.setAttribute("chapter", chapter);
            request.setAttribute("courseName", courseName);
            request.setAttribute("totalLessons", totalLessons);

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
            } else {
                response.sendRedirect(request.getContextPath() + "/chapter-detail?id=" + chapterId);
            }

        } catch (NumberFormatException e) {
            System.err.println("ChapterDetailServlet POST: Invalid chapter ID format - " + idParam);
            response.sendRedirect(request.getContextPath() + "/");
        }
    }
}