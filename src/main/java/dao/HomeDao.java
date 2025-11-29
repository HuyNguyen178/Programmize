package dao;

import model.Course;
import utils.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HomeDao {
    public List<Course> getFeaturedCourses() {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT c.course_id, c.course_name, c.thumbnail_url, c.description, " +
                "c.listed_price, c.sale_price, c.status, c.duration, c.instructor_id, " +
                "u.fullname AS instructor_name, " +
                "s.value AS category_name " +
                "FROM course c " +
                "JOIN user u ON c.instructor_id = u.user_id " +
                "LEFT JOIN course_category cc ON c.course_id = cc.course_id " +
                "LEFT JOIN setting s ON cc.category_id = s.setting_id " +
                "WHERE c.status = 1 " +
                "ORDER BY c.course_id DESC " +
                "LIMIT 8";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Course course = new Course();

                course.setCourseId(rs.getInt("course_id"));
                course.setCourseName(rs.getString("course_name"));
                course.setThumbnailUrl(rs.getString("thumbnail_url"));
                course.setDescription(rs.getString("description"));
                course.setListedPrice(rs.getBigDecimal("listed_price"));
                course.setSalePrice(rs.getBigDecimal("sale_price"));

                course.setStatus(rs.getBoolean("status") ? "Active" : "Inactive");
                course.setDuration(rs.getInt("duration"));
                course.setInstructorId(rs.getInt("instructor_id"));

                course.setCourseInstructor(rs.getString("instructor_name"));

                String category = rs.getString("category_name");
                course.setCourseCategory(category != null ? category : "Uncategorized");

                list.add(course);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Course> getCoursesByCategory(String categoryName) {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT c.*, u.fullname AS instructor_name, s.value AS category_name " +
                "FROM course c " +
                "JOIN user u ON c.instructor_id = u.user_id " +
                "JOIN course_category cc ON c.course_id = cc.course_id " +
                "JOIN setting s ON cc.category_id = s.setting_id " +
                "WHERE s.value LIKE ? AND c.status = 1 " +
                "LIMIT 8";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + categoryName + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Course course = mapResultSetToCourse(rs);
                    list.add(course);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Course mapResultSetToCourse(ResultSet rs) throws SQLException {
        Course course = new Course();
        course.setCourseId(rs.getInt("course_id"));
        course.setCourseName(rs.getString("course_name"));
        course.setThumbnailUrl(rs.getString("thumbnail_url"));
        course.setDescription(rs.getString("description"));
        course.setListedPrice(rs.getBigDecimal("listed_price"));
        course.setSalePrice(rs.getBigDecimal("sale_price"));
        course.setStatus(rs.getBoolean("status") ? "Active" : "Inactive");
        course.setDuration(rs.getInt("duration"));
        course.setInstructorId(rs.getInt("instructor_id"));
        course.setCourseInstructor(rs.getString("instructor_name"));
        course.setCourseCategory(rs.getString("category_name"));
        return course;
    }
}