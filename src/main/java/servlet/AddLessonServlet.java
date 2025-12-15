package servlet;

import dao.LessonDAO;
import model.Lesson;
import model.Lesson.LessonType;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

@WebServlet("/add-lesson")
public class AddLessonServlet extends HttpServlet {
    private LessonDAO lessonDAO;

    @Override
    public void init() throws ServletException {
        lessonDAO = new LessonDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Check if this is an AJAX request to get next order index
        String action = request.getParameter("action");
        if ("getNextOrder".equals(action)) {
            handleGetNextOrderIndex(request, response);
            return;
        }

        try {
            // fetch chapter list to dropdown (Format: [chapter_id, chapter_name, course_name])
            List<String[]> allChapters = lessonDAO.getAllChaptersForDropdown();

            // set attributes for JSP
            request.setAttribute("allChapters", allChapters);

            // if got chap id from course content, auto nextOrderIndex
            String chapterIdParam = request.getParameter("chapterId");
            if (chapterIdParam != null && !chapterIdParam.trim().isEmpty()) {
                try {
                    int chapterId = Integer.parseInt(chapterIdParam);
                    int nextOrderIndex = lessonDAO.getNextOrderIndex(chapterId);
                    request.setAttribute("nextOrderIndex", nextOrderIndex);
                } catch (NumberFormatException e) {
                    // Ignore invalid chapterId
                }
            } else {
                // default order index
                request.setAttribute("nextOrderIndex", 1);
            }

            // Forward
            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/add-lesson.jsp");
            dispatcher.forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/course-content");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // chapterId for redirect
        int chapterId = 0;

        try {
            // fetch parameters from form
            String lessonName = request.getParameter("lessonName");
            String lessonTypeStr = request.getParameter("lessonType");
            String content = request.getParameter("content");
            String videoUrl = request.getParameter("videoUrl");

            // fetch chapter ID
            chapterId = Integer.parseInt(request.getParameter("chapterId"));

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
            } else {
                // auto fetch next index if not assigned
                orderIndex = lessonDAO.getNextOrderIndex(chapterId);
            }

            // fetch status
            boolean status = Boolean.parseBoolean(request.getParameter("status"));

            // fetch isPreview
            boolean isPreview = "true".equals(request.getParameter("isPreview"));

            // create new lesson object
            Lesson lesson = new Lesson();
            lesson.setChapterId(chapterId);
            lesson.setLessonName(lessonName);
            lesson.setLessonType(LessonType.fromString(lessonTypeStr));
            lesson.setContent(content);
            lesson.setVideoUrl(videoUrl);
            lesson.setDuration(duration);
            lesson.setOrderIndex(orderIndex);
            lesson.setPreview(isPreview);
            lesson.setStatus(status);
            lesson.setCreatedAt(new Date());
            lesson.setUpdatedAt(new Date());

            // call dao add database
            int newLessonId = lessonDAO.insertLesson(lesson);

            if (newLessonId > 0) {
                String chapterName = lessonDAO.getChapterNameById(chapterId);
                request.getSession().setAttribute("successMessage",
                        "Lesson '" + lessonName + "' added successfully to chapter '" + chapterName + "'!");
            } else {
                request.getSession().setAttribute("errorMessage",
                        "Failed to add lesson. Please try again.");
            }

            // Redirect
            response.sendRedirect(request.getContextPath() + "/chapter-detail?id=" + chapterId);

        } catch (NumberFormatException e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage",
                    "Invalid input format. Please check your data.");
            // redirect
            if (chapterId > 0) {
                response.sendRedirect(request.getContextPath() + "/add-lesson?chapterId=" + chapterId);
            } else {
                response.sendRedirect(request.getContextPath() + "/add-lesson");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage",
                    "An error occurred: " + e.getMessage());
            // redirect
            if (chapterId > 0) {
                response.sendRedirect(request.getContextPath() + "/add-lesson?chapterId=" + chapterId);
            } else {
                response.sendRedirect(request.getContextPath() + "/add-lesson");
            }
        }
    }

    /**
     * Handle AJAX request to get next order index for a chapter
     */
    private void handleGetNextOrderIndex(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        try {
            String chapterIdParam = request.getParameter("chapterId");
            if (chapterIdParam != null && !chapterIdParam.trim().isEmpty()) {
                int chapterId = Integer.parseInt(chapterIdParam);
                int nextOrderIndex = lessonDAO.getNextOrderIndex(chapterId);
                out.print("{\"nextOrderIndex\": " + nextOrderIndex + "}");
            } else {
                out.print("{\"nextOrderIndex\": 1}");
            }
        } catch (NumberFormatException e) {
            out.print("{\"error\": \"Invalid chapter ID\", \"nextOrderIndex\": 1}");
        }

        out.flush();
    }
}