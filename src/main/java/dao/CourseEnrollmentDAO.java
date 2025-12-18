package dao;

import model.CourseEnrollment;
import utils.DBUtil;

import java.sql.*;

public class CourseEnrollmentDAO {
    private final DBUtil dbUtil;

    public CourseEnrollmentDAO() {dbUtil = new DBUtil();}

    public boolean addEnrollment(CourseEnrollment enrollment) {
        String sql = "INSERT INTO course_enrollment (user_id, course_id, price_paid, payment_method, enrolled_at, status) " +
                "VALUES (?, ?, ?, ?, NOW(), ?)";

        try (Connection conn = dbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, enrollment.getUserId());
            stmt.setInt(2, enrollment.getCourseId());
            stmt.setBigDecimal(3, enrollment.getPricePaid());
            stmt.setString(4, enrollment.getPaymentMethod());
            stmt.setBoolean(5, enrollment.isStatus());

            if (stmt.executeUpdate() > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        enrollment.setEnrollmentId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }

        } catch (SQLIntegrityConstraintViolationException e) {
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


}
