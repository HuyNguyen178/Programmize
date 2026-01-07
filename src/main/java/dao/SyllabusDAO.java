package dao;

import model.DaysOfWeek;
import model.Syllabus;
import utils.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SyllabusDAO {
    public Syllabus getSyllabusByClassId(Integer classId) {
        try (Connection connection = DBUtil.getConnection()) {
            String sql = "SELECT s.*, GROUP_CONCAT(DISTINCT sd.day_of_week SEPARATOR ', ') AS days_of_week " +
                    "FROM syllabus s " +
                    "JOIN syllabus_days sd ON s.syllabus_id = sd.syllabus_id " +
                    "WHERE s.class_id = ? " +
                    "GROUP BY s.syllabus_id";
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

                return syllabus;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
