package servlet;

import dao.ChapterDAO;
import dao.CourseDAO;
import model.Chapter;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@WebServlet("/edit-chapter")
public class EditChapterServlet extends HttpServlet {
    private ChapterDAO chapterDAO;
    private CourseDAO courseDAO;

    @Override
    public void init() throws ServletException {
        chapterDAO = new ChapterDAO();
        courseDAO = new CourseDAO();
    }

    // hiển thị form edit chapter
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String idParam = request.getParameter("id");

            if (idParam == null || idParam.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/chapter-list");
                return;
            }

            int chapterId = Integer.parseInt(idParam);

            Chapter chapter = chapterDAO.getChapterById(chapterId);

            if (chapter == null) {
                request.getSession().setAttribute("errorMessage", "Chapter not found!");
                response.sendRedirect(request.getContextPath() + "/chapter-list");
                return;
            }

            List<String[]> allCourses = chapterDAO.getAllCoursesForDropdown();

            String courseName = chapterDAO.getCourseNameById(chapter.getCourseId());

            request.setAttribute("chapter", chapter);
            request.setAttribute("allCourses", allCourses);
            request.setAttribute("courseName", courseName);

            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/edit-chapter.jsp");
            dispatcher.forward(request, response);

        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMessage", "Invalid chapter ID!");
            response.sendRedirect(request.getContextPath() + "/chapter-list");
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/chapter-list");
        }
    }

    // xử lý submit form edit database
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int chapterId = Integer.parseInt(request.getParameter("chapterId"));
            int courseId = Integer.parseInt(request.getParameter("courseId"));
            String chapterName = request.getParameter("chapterName");
            String description = request.getParameter("description");

            int orderIndex = 0;
            String orderIndexStr = request.getParameter("orderIndex");
            if (orderIndexStr != null && !orderIndexStr.trim().isEmpty()) {
                orderIndex = Integer.parseInt(orderIndexStr);
            }

            String statusStr = request.getParameter("status");
            boolean status = "1".equals(statusStr) || "true".equals(statusStr) || "on".equals(statusStr);

            Chapter existingChapter = chapterDAO.getChapterById(chapterId);

            Chapter chapter = new Chapter();
            chapter.setChapterId(chapterId);
            chapter.setCourseId(courseId);
            chapter.setChapterName(chapterName);
            chapter.setDescription(description);
            chapter.setOrderIndex(orderIndex);
            chapter.setStatus(status);
            chapter.setCreatedAt(existingChapter != null ? existingChapter.getCreatedAt() : new Date());
            chapter.setUpdatedAt(new Date());

            boolean success = chapterDAO.updateChapter(chapter);

            if (success) {
                request.getSession().setAttribute("successMessage",
                        "Chapter '" + chapterName + "' updated successfully!");
            } else {
                request.getSession().setAttribute("errorMessage",
                        "Failed to update chapter. Please try again.");
            }

            response.sendRedirect(request.getContextPath() + "/chapter-detail?courseId=" + courseId);

        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMessage",
                    "Invalid input format. Please check your data.");
            response.sendRedirect(request.getContextPath() + "/chapter-list");
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage",
                    "An error occurred: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/chapter-list");
        }
    }
}