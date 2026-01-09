package dao;

import model.Class;
import model.DaysOfWeek;
import model.Syllabus;
import model.SyllabusLesson;
import utils.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SyllabusDAO {
    public Syllabus getSyllabusByClassId(Integer classId) {
        try (Connection connection = DBUtil.getConnection()) {
            String sql = "SELECT s.*, " +
                    "GROUP_CONCAT(DISTINCT sd.day_of_week SEPARATOR ', ') AS days_of_week, " +
                    "c.class_name, " +
                    "GROUP_CONCAT(DISTINCT CONCAT(sl.title,'|', sl.objectives) ORDER BY sl.lesson_id ASC SEPARATOR ';') AS lessons " +
                    "FROM syllabus s " +
                    "LEFT JOIN syllabus_days sd ON s.syllabus_id = sd.syllabus_id " +
                    "LEFT JOIN class c ON s.class_id = c.class_id " +
                    "LEFT JOIN syllabus_lesson sl ON s.syllabus_id = sl.syllabus_id " +
                    "WHERE s.class_id = ? " +
                    "GROUP BY s.syllabus_id, c.class_name";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, classId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Syllabus syllabus = new Syllabus();

                syllabus.setSyllabusId(resultSet.getInt("syllabus_id"));
                syllabus.setTotalHours(resultSet.getInt("total_hours"));
                syllabus.setStartTime(resultSet.getTime("start_time"));
                syllabus.setEndTime(resultSet.getTime("end_time"));
                syllabus.setAttendance(resultSet.getInt("attendance"));
                syllabus.setAssignments(resultSet.getInt("assignments"));
                syllabus.setFinalExam(resultSet.getInt("final_exam"));
                syllabus.setObjectives(resultSet.getString("objectives"));

                Class c = new Class();
                c.setId(resultSet.getInt("class_id"));
                c.setName(resultSet.getString("class_name"));
                syllabus.setClazz(c);

                String daysStr = resultSet.getString("days_of_week");
                if (daysStr != null && !daysStr.isEmpty()) {
                    List<DaysOfWeek> dayList = Arrays.stream(daysStr.split(",\\s*"))
                            .map(day -> switch (day) {
                                case "Monday" -> DaysOfWeek.Monday;
                                case "Tuesday" -> DaysOfWeek.Tuesday;
                                case "Wednesday" -> DaysOfWeek.Wednesday;
                                case "Thursday" -> DaysOfWeek.Thursday;
                                case "Friday" -> DaysOfWeek.Friday;
                                case "Saturday" -> DaysOfWeek.Saturday;
                                case "Sunday" -> DaysOfWeek.Sunday;
                                default -> throw new IllegalArgumentException("Invalid day: " + day);
                            }).toList();
                    syllabus.setDaysOfWeek(dayList);
                }
                else {
                    syllabus.setDaysOfWeek(new ArrayList<>());
                }

                String lessonsStr = resultSet.getString("lessons");
                List<SyllabusLesson> lessons = new ArrayList<>();
                if (lessonsStr != null && !lessonsStr.isEmpty()) {
                    for (String l : lessonsStr.split(";")) {
                        String[] parts = l.split("\\|");
                        SyllabusLesson lesson = new SyllabusLesson();
                        lesson.setTitle(parts[0]);
                        lesson.setObjectives(parts.length > 1 ? parts[1] : "");
                        lessons.add(lesson);
                    }
                }
                syllabus.setLessons(lessons);

                return syllabus;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void addSyllabus(Syllabus syllabus) {
        String addSyllabus = "INSERT INTO syllabus (class_id, total_hours, start_time, end_time, attendance, assignments, final_exam, objectives) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String addSyllabusDays = "INSERT INTO syllabus_days (syllabus_id, day_of_week) VALUES (?, ?)";
        String addSyllabusLessons = "INSERT INTO syllabus_lesson (title, syllabus_id, objectives) VALUES (?, ?, ?)";

        Connection conn = null;
        PreparedStatement psSyllabus = null;
        PreparedStatement psDay = null;
        PreparedStatement psLesson = null;
        ResultSet rsKeys = null;

        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false); // bắt đầu transaction

            // 1. Insert vào bảng syllabus
            psSyllabus = conn.prepareStatement(addSyllabus, Statement.RETURN_GENERATED_KEYS);
            psSyllabus.setInt(1, syllabus.getClazz().getId());
            psSyllabus.setInt(2, syllabus.getTotalHours());
            psSyllabus.setTime(3, syllabus.getStartTime());
            psSyllabus.setTime(4, syllabus.getEndTime());
            psSyllabus.setInt(5, syllabus.getAttendance());
            psSyllabus.setInt(6, syllabus.getAssignments());
            psSyllabus.setInt(7, syllabus.getFinalExam());
            psSyllabus.setString(8, syllabus.getObjectives());
            psSyllabus.executeUpdate();

            rsKeys = psSyllabus.getGeneratedKeys();
            if (rsKeys.next()) {
                int syllabusId = rsKeys.getInt(1);
                syllabus.setSyllabusId(syllabusId);

                psDay = conn.prepareStatement(addSyllabusDays);
                for (DaysOfWeek day : syllabus.getDaysOfWeek()) {
                    psDay.setInt(1, syllabusId);
                    psDay.setString(2, day.name());
                    psDay.addBatch();
                }
                psDay.executeBatch();

                psLesson = conn.prepareStatement(addSyllabusLessons);
                for (SyllabusLesson lesson : syllabus.getLessons()) {
                    psLesson.setString(1, lesson.getTitle());
                    psLesson.setInt(2, syllabusId);
                    psLesson.setString(3, lesson.getObjectives());
                    psLesson.addBatch();
                }
                psLesson.executeBatch();
            }

            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public void updateSyllabus(Syllabus syllabus) {
        String updateSyllabus = "UPDATE syllabus SET total_hours = ?, start_time = ?, end_time = ?, attendance = ?, assignments = ?, final_exam = ?, objectives = ? WHERE syllabus_id = ?";
        String deleteSyllabusDays = "DELETE FROM syllabus_days WHERE syllabus_id = ?";
        String addSyllabusDays = "INSERT INTO syllabus_days (syllabus_id, day_of_week) VALUES (?, ?)";
        String deleteSyllabusLessons = "DELETE FROM syllabus_lesson WHERE syllabus_id = ?";
        String addSyllabusLessons = "INSERT INTO syllabus_lesson (title, syllabus_id, objectives) VALUES (?, ?, ?)";

        Connection conn = null;
        PreparedStatement psUpdate = null;
        PreparedStatement psDeleteDays = null;
        PreparedStatement psInsertDay = null;
        PreparedStatement psDeleteLessons = null;
        PreparedStatement psInsertLesson = null;

        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            // 1. Update syllabus
            psUpdate = conn.prepareStatement(updateSyllabus);
            psUpdate.setInt(1, syllabus.getTotalHours());
            psUpdate.setTime(2, syllabus.getStartTime());
            psUpdate.setTime(3, syllabus.getEndTime());
            psUpdate.setInt(4, syllabus.getAttendance());
            psUpdate.setInt(5, syllabus.getAssignments());
            psUpdate.setInt(6, syllabus.getFinalExam());
            psUpdate.setString(7, syllabus.getObjectives());
            psUpdate.setInt(8, syllabus.getSyllabusId());
            psUpdate.executeUpdate();

            // 2. Xóa và insert lại days
            psDeleteDays = conn.prepareStatement(deleteSyllabusDays);
            psDeleteDays.setInt(1, syllabus.getSyllabusId());
            psDeleteDays.executeUpdate();

            psInsertDay = conn.prepareStatement(addSyllabusDays);
            for (DaysOfWeek day : syllabus.getDaysOfWeek()) {
                psInsertDay.setInt(1, syllabus.getSyllabusId());
                psInsertDay.setString(2, day.name());
                psInsertDay.addBatch();
            }
            psInsertDay.executeBatch();

            // 3. Xóa và insert lại lessons
            psDeleteLessons = conn.prepareStatement(deleteSyllabusLessons);
            psDeleteLessons.setInt(1, syllabus.getSyllabusId());
            psDeleteLessons.executeUpdate();

            psInsertLesson = conn.prepareStatement(addSyllabusLessons);
            for (SyllabusLesson lesson : syllabus.getLessons()) {
                psInsertLesson.setString(1, lesson.getTitle());
                psInsertLesson.setInt(2, syllabus.getSyllabusId());
                psInsertLesson.setString(3, lesson.getObjectives());
                psInsertLesson.addBatch();
            }
            psInsertLesson.executeBatch();

            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

//    public List<SyllabusLesson> getLessonsBySyllabusId(Integer id) {
//        try (Connection connection = DBUtil.getConnection()) {
//            String sql = "SELECT * FROM syllabus_lesson WHERE syllabus_id = ?";
//            PreparedStatement statement = connection.prepareStatement(sql);
//            statement.setInt(1, id);
//            w
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
