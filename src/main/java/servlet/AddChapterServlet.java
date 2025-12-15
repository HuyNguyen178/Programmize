package servlet;

import dao.ChapterDAO;
import model.Chapter;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

@WebServlet("/add-chapter")
public class AddChapterServlet extends HttpServlet {
    private ChapterDAO chapterDAO;

    @Override
    public void init() throws ServletException {
        chapterDAO = new ChapterDAO();
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
            // fetch course list to dropdown
            List<String[]> allCourses = chapterDAO.getAllCoursesForDropdown();

            // set attributes for JSP
            request.setAttribute("allCourses", allCourses);  // List of [course_id, course_name]

            // if got course id from course detail, auto nextOrderIndex
            String courseIdParam = request.getParameter("courseId");
            if (courseIdParam != null && !courseIdParam.trim().isEmpty()) {
                try {
                    int courseId = Integer.parseInt(courseIdParam);
                    int nextOrderIndex = chapterDAO.getNextOrderIndex(courseId);
                    request.setAttribute("nextOrderIndex", nextOrderIndex);
                } catch (NumberFormatException e) {
                    // Ignore invalid courseId
                }
            } else {
                // default order index
                request.setAttribute("nextOrderIndex", 1);
            }

            // forward
            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/add-chapter.jsp");
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

        try {
            // fetch parameters from form
            String chapterName = request.getParameter("chapterName");
            String description = request.getParameter("description");

            // fetch course ID
            int courseId = Integer.parseInt(request.getParameter("courseId"));

            // fetcg order index
            int orderIndex = 1;
            String orderIndexStr = request.getParameter("orderIndex");
            if (orderIndexStr != null && !orderIndexStr.trim().isEmpty()) {
                orderIndex = Integer.parseInt(orderIndexStr);
            } else {
                // auto fetch next index if not assigned
                orderIndex = chapterDAO.getNextOrderIndex(courseId);
            }

            // fetch status
            boolean status = Boolean.parseBoolean(request.getParameter("status"));

            // create new chap object
            Chapter chapter = new Chapter();
            chapter.setCourseId(courseId);
            chapter.setChapterName(chapterName);
            chapter.setDescription(description);
            chapter.setOrderIndex(orderIndex);
            chapter.setStatus(status);
            chapter.setCreatedAt(new Date());
            chapter.setUpdatedAt(new Date());

            // call dao add database
            int newChapterId = chapterDAO.insertChapter(chapter);

            if (newChapterId > 0) {
                String courseName = chapterDAO.getCourseNameById(courseId);
                request.getSession().setAttribute("successMessage",
                        "Chapter '" + chapterName + "' added successfully to course '" + courseName + "'!");
            } else {
                request.getSession().setAttribute("errorMessage",
                        "Failed to add chapter. Please try again.");
            }
            // redirect
            response.sendRedirect(request.getContextPath() + "/course-content");

        } catch (NumberFormatException e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage",
                    "Invalid input format. Please check your data.");
            response.sendRedirect(request.getContextPath() + "/add-chapter");
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage",
                    "An error occurred: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/add-chapter");
        }
    }

    /**
     * Handle AJAX request to get next order index for a course
     */
    private void handleGetNextOrderIndex(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        try {
            String courseIdParam = request.getParameter("courseId");
            if (courseIdParam != null && !courseIdParam.trim().isEmpty()) {
                int courseId = Integer.parseInt(courseIdParam);
                int nextOrderIndex = chapterDAO.getNextOrderIndex(courseId);
                out.print("{\"nextOrderIndex\": " + nextOrderIndex + "}");
            } else {
                out.print("{\"nextOrderIndex\": 1}");
            }
        } catch (NumberFormatException e) {
            out.print("{\"error\": \"Invalid course ID\", \"nextOrderIndex\": 1}");
        }

        out.flush();
    }
}