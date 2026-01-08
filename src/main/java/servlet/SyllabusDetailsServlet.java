package servlet;

import dao.ClassDAO;
import dao.SyllabusDAO;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Class;
import model.DaysOfWeek;
import model.Syllabus;
import model.SyllabusLesson;

import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/syllabus-details")
public class SyllabusDetailsServlet extends HttpServlet {
    private SyllabusDAO syllabusDAO;
    private ClassDAO classDAO;

    @Override
    public void init(ServletConfig config) throws ServletException {
        syllabusDAO = new SyllabusDAO();
        classDAO = new ClassDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String classIdStr = request.getParameter("classId");

        Syllabus syllabus = syllabusDAO.getSyllabusByClassId(Integer.parseInt(classIdStr));

        if (syllabus == null) {
            syllabus = new Syllabus();
            Class c = classDAO.getClassById(Integer.parseInt(classIdStr));
            syllabus.setClazz(c);
            syllabus.setLessons(new ArrayList<>());
        }

        List<String> days = syllabus.getDaysOfWeek().stream()
                .map(Enum::name)
                .toList();

        request.setAttribute("syllabusDays", days);
        request.setAttribute("syllabus", syllabus);
        request.getRequestDispatcher("/WEB-INF/views/syllabus-details.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        int classId = Integer.parseInt(request.getParameter("classId"));
        int totalHours = Integer.parseInt(request.getParameter("totalHours"));
        String startTime = request.getParameter("startTime");
        String endTime = request.getParameter("endTime");
        String[] days = request.getParameterValues("daysOfWeek");

        int attendancePercent = Integer.parseInt(request.getParameter("attendancePercent"));
        int assignmentsPercent = Integer.parseInt(request.getParameter("assignmentsPercent"));
        int finalExamPercent = Integer.parseInt(request.getParameter("finalExamPercent"));
        String objectives = request.getParameter("objectives");

        String[] lessonTitles = request.getParameterValues("lessonTitle[]");
        String[] lessonObjectives = request.getParameterValues("lessonObjectives[]");

        // Validate % tổng
        if (attendancePercent + assignmentsPercent + finalExamPercent != 100) {
            request.getSession().setAttribute("errorMessage", "Total percentage must equal 100%");
            response.sendRedirect(request.getContextPath() + "/syllabus-details?classId=" + classId);
            return;
        }

        // Lấy syllabus từ DB
        Syllabus syllabus = syllabusDAO.getSyllabusByClassId(classId);
        boolean isNew = false;
        if (syllabus == null) {
            syllabus = new Syllabus();
            Class c = classDAO.getClassById(classId);
            syllabus.setClazz(c);
            syllabus.setLessons(new ArrayList<>());
            isNew = true;
        }

        syllabus.setTotalHours(totalHours);
        syllabus.setStartTime(Time.valueOf(startTime));
        syllabus.setEndTime(Time.valueOf(endTime));

        List<DaysOfWeek> daysList = new ArrayList<>();
        if (days != null) {
            for (String d : days) {
                switch(d) {
                    case "Monday": daysList.add(DaysOfWeek.Monday); break;
                    case "Tuesday": daysList.add(DaysOfWeek.Tuesday); break;
                    case "Wednesday": daysList.add(DaysOfWeek.Wednesday); break;
                    case "Thursday": daysList.add(DaysOfWeek.Thursday); break;
                    case "Friday": daysList.add(DaysOfWeek.Friday); break;
                    case "Saturday": daysList.add(DaysOfWeek.Saturday); break;
                    case "Sunday": daysList.add(DaysOfWeek.Sunday); break;
                }
            }
        }
        syllabus.setDaysOfWeek(daysList);

        syllabus.setAttendance(attendancePercent);
        syllabus.setAssignments(assignmentsPercent);
        syllabus.setFinalExam(finalExamPercent);
        syllabus.setObjectives(objectives);

        // Xử lý lessons
        List<SyllabusLesson> lessons = new ArrayList<>();
        if (lessonTitles != null && lessonObjectives != null) {
            for (int i = 0; i < lessonTitles.length; i++) {
                SyllabusLesson lesson = new SyllabusLesson();
                lesson.setTitle(lessonTitles[i]);
                lesson.setObjectives(lessonObjectives[i]);
                lessons.add(lesson);
            }
        }
        syllabus.setLessons(lessons);

        // Lưu vào DB
        if (isNew) {
            syllabusDAO.addSyllabus(syllabus);
        } else {
            syllabusDAO.updateSyllabus(syllabus);
        }

        request.getSession().setAttribute("successMessage", "Syllabus saved successfully!");
        response.sendRedirect(request.getContextPath() + "/syllabus-details?classId=" + classId);
    }
}
