package dao;

import model.Course;
import utils.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class HomeDAO {

    public List<Course> getHighlightedCourses() {
        List<Course> list = new ArrayList<>();
        // Query lấy 8 khóa học mới nhất đang active (status = 1)
        // JOIN bảng user để lấy tên giảng viên (fullname)
        // JOIN bảng setting để lấy tên category (nếu cần hiển thị category)
        String sql = "SELECT c.*, u.fullname AS instructor_name, s.value AS category_name " +
                "FROM course c " +
                "JOIN user u ON c.instructor_id = u.user_id " +
                "LEFT JOIN course_category cc ON c.course_id = cc.course_id " +
                "LEFT JOIN setting s ON cc.category_id = s.setting_id " +
                "WHERE c.status = 1 " +
                "ORDER BY c.course_id DESC LIMIT 8";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Course c = new Course();
                c.setCourseId(rs.getInt("course_id"));
                c.setCourseName(rs.getString("course_name"));
                c.setListedPrice(rs.getBigDecimal("listed_price"));
                c.setSalePrice(rs.getBigDecimal("sale_price"));
                c.setThumbnailUrl(rs.getString("thumbnail_url"));
                c.setDescription(rs.getString("description"));
                c.setDuration(rs.getInt("duration"));
                c.setInstructorId(rs.getInt("instructor_id"));
                c.setStatus(rs.getBoolean("status") ? "Active" : "Inactive");

                // Set các trường lấy từ bảng join
                c.setCourseInstructor(rs.getString("instructor_name"));
                c.setCourseCategory(rs.getString("category_name"));

                list.add(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}