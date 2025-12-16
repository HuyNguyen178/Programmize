package servlet;

import dao.CourseDAO;
import dao.ChapterDAO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Course;
import model.Chapter;

@WebServlet(name = "CourseContentServlet", urlPatterns = {"/course-content"})
public class CourseContentServlet extends HttpServlet {

    private CourseDAO courseDAO;
    private ChapterDAO chapterDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        courseDAO = new CourseDAO();
        chapterDAO = new ChapterDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String category = request.getParameter("category");
        String status = request.getParameter("status");
        String search = request.getParameter("search");

        long startTime = System.currentTimeMillis();
        System.out.println("=== CourseContentServlet doGet ===");

        // query 1: get all courses
        List<Course> courses = courseDAO.getAllCourses(category, null, status, search, null, null);
        System.out.println("Query 1 - Courses loaded: " + courses.size() + " (" + (System.currentTimeMillis() - startTime) + "ms)");

        // fixed: Get all chapters in 1 query instead of N
        Map<Integer, List<Chapter>> courseChaptersMap;

        if (!courses.isEmpty()) {
            List<Integer> courseIds = courses.stream()
                    .map(Course::getCourseId)
                    .collect(Collectors.toList());

            // query 2: Get all chapters for all courses in 1 query
            courseChaptersMap = chapterDAO.getChaptersForCourses(courseIds);
            System.out.println("Query 2 - All chapters loaded in ONE query (" + (System.currentTimeMillis() - startTime) + "ms)");
        } else {
            courseChaptersMap = new HashMap<>();
        }

        // query 3: get categories for filter dropdown
        List<String[]> categories = courseDAO.getAllCategoriesFromSettings();

        request.setAttribute("courses", courses);
        request.setAttribute("courseChaptersMap", courseChaptersMap);
        request.setAttribute("categories", categories);

        request.setAttribute("selectedCategory", category);
        request.setAttribute("selectedStatus", status);
        request.setAttribute("searchKeyword", search);

        long totalTime = System.currentTimeMillis() - startTime;
        System.out.println("CourseContentServlet: Total load time: " + totalTime + "ms (was N+1 queries, now only 3 queries)");

        request.getRequestDispatcher("/WEB-INF/views/course-content.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("deleteChapter".equals(action)) {
            String chapterIdStr = request.getParameter("chapterId");
            if (chapterIdStr != null && !chapterIdStr.isEmpty()) {
                try {
                    int chapterId = Integer.parseInt(chapterIdStr);
                    boolean deleted = chapterDAO.deleteChapter(chapterId);
                    if (deleted) {
                        System.out.println("CourseContentServlet: Chapter deleted - ID: " + chapterId);
                        request.getSession().setAttribute("successMessage", "Chapter deleted successfully!");
                    } else {
                        System.err.println("CourseContentServlet: Failed to delete chapter - ID: " + chapterId);
                        request.getSession().setAttribute("errorMessage", "Failed to delete chapter.");
                    }
                } catch (NumberFormatException e) {
                    System.err.println("CourseContentServlet: Invalid chapter ID format");
                }
            }
        }

        response.sendRedirect(request.getContextPath() + "/course-content");
    }
}