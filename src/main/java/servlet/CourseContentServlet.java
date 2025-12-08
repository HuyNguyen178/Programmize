package servlet;

import dao.CourseDAO;
import dao.ChapterDAO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

        System.out.println("=== CourseContentServlet doGet ===");
        System.out.println("Category: " + category);
        System.out.println("Status: " + status);
        System.out.println("Search: " + search);

        List<Course> courses = courseDAO.getAllCourses(category, null, status, search, null, null);
        System.out.println("Number of courses loaded: " + courses.size());
        
        // Cmap stores chapters for course
        Map<Integer, List<Chapter>> courseChaptersMap = new HashMap<>();

        // each course get chapters
        for (Course course : courses) {
            int courseId = course.getCourseId();
            List<Chapter> chapters = chapterDAO.getChaptersByCourseId(course.getCourseId());
            courseChaptersMap.put(course.getCourseId(), chapters);
            System.out.println("Course ID " + courseId + " (" + course.getCourseName() + "): " + chapters.size() + " chapters");
        }

        List<String[]> categories = courseDAO.getAllCategoriesFromSettings();

        request.setAttribute("courses", courses);
        request.setAttribute("courseChaptersMap", courseChaptersMap);
        request.setAttribute("categories", categories);

        request.setAttribute("selectedCategory", category);
        request.setAttribute("selectedStatus", status);
        request.setAttribute("searchKeyword", search);

        System.out.println("CourseContentServlet: Loaded " + courses.size() + " courses");

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

        response.sendRedirect(request.getContextPath() + "/WEB-INF/views/course-content");
    }
}