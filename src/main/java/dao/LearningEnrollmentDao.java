package dao;

import model.Course;
import utils.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LearningEnrollmentDao {

    // Lấy danh sách tất cả các khóa học đang active
    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT c.*, u.fullname FROM course c " +
                "JOIN user u ON c.instructor_id = u.user_id " +
                "WHERE c.status = 1"; // Chỉ lấy khóa học đang hoạt động (status = true)

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Course course = new Course();
                course.setId(rs.getInt("course_id"));
                course.setCourseName(rs.getString("course_name"));
                course.setListedPrice(rs.getBigDecimal("listed_price"));
                course.setSalePrice(rs.getBigDecimal("sale_price"));
                course.setThumbnailUrl(rs.getString("thumbnail_url"));
                course.setInstructorId(rs.getInt("instructor_id"));
                course.setDuration(rs.getInt("duration"));
                course.setDescription(rs.getString("description"));
                // Chuyển đổi boolean status trong DB sang String theo model Course
                course.setStatus(rs.getBoolean("status") ? "Active" : "Inactive");
                // Gán tên giảng viên vào field courseInstructor
                course.setCourseInstructor(rs.getString("fullname"));

                courses.add(course);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }


    public int getCourseIdByCode(String code) {
        String searchTerm = "%" + code + "%";
        if(code.equals("web-dev")) searchTerm = "%Web Development%";
        else if(code.equals("data-sci")) searchTerm = "%Data Science%";
        else if(code.equals("python")) searchTerm = "%Python%";

        String sql = "SELECT course_id FROM course WHERE course_name LIKE ? LIMIT 1";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, searchTerm);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("course_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Kiểm tra xem user đã đăng ký khóa học này chưa
    public boolean isEnrolled(int userId, int courseId) {
        String sql = "SELECT 1 FROM enrollment WHERE user_id = ? AND course_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, courseId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Thực hiện đăng ký (Insert vào bảng enrollment)
    public boolean insertEnrollment(int userId, int courseId) {
        // Kiểm tra trước khi insert để tránh lỗi Duplicate Key
        if (isEnrolled(userId, courseId)) {
            return false;
        }

        String sql = "INSERT INTO enrollment (user_id, course_id) VALUES (?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, courseId);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void updateUserPhone(int userId, String phone) {

         String sql = "UPDATE user SET fullname = ? WHERE user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, phone); 
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}