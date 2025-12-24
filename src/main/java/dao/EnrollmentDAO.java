package dao;

import model.ClassEnrollment;
import model.CourseEnrollment;
import utils.DBUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnrollmentDAO {

    public boolean addEnrollment(CourseEnrollment enrollment) {
        String sql = "INSERT INTO course_enrollment (user_id, course_id, price_paid, payment_method, enrolled_at, status) " +
                "VALUES (?, ?, ?, ?, NOW(), ?)";

        try (Connection conn = DBUtil.getConnection();
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

    public boolean addEnrollment(ClassEnrollment enrollment) {
        String sql = "INSERT INTO class_enrollment (user_id, class_id, price_paid, payment_method, enrolled_at, status) " +
                "VALUES (?, ?, ?, ?, NOW(), ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, enrollment.getUserId());
            stmt.setInt(2, enrollment.getClassId());
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

    public List<BigDecimal> getMonthlyRevenueList(int year) {
        List<BigDecimal> monthlyData = new ArrayList<>();
        for (int i = 0; i < 12; i++) monthlyData.add(BigDecimal.ZERO);
        String sql = "SELECT MONTH(enrolled_at) as month, SUM(price_paid) as total " +
                "FROM (SELECT enrolled_at, price_paid, status FROM course_enrollment " +
                "      UNION ALL " +
                "      SELECT enrolled_at, price_paid, status FROM class_enrollment) as combined " +
                "WHERE YEAR(enrolled_at) = ? AND status = true GROUP BY MONTH(enrolled_at)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, year);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    monthlyData.set(rs.getInt("month") - 1, rs.getBigDecimal("total"));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return monthlyData;
    }

    public List<Object> getAllEnrollmentsWithDetails(int userId, String keyword, String type, String status) {
        List<Object> enrollments = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT * FROM ( " +
                        "  SELECT 'COURSE' as item_type, ce.enrollment_id, ce.user_id, ce.course_id as item_id, " +
                        "         ce.price_paid, ce.payment_method, ce.enrolled_at, ce.status, " +
                        "         c.course_name as item_name, c.thumbnail_url as item_thumb " +
                        "  FROM course_enrollment ce " +
                        "  JOIN course c ON ce.course_id = c.course_id " +
                        "  UNION ALL " +
                        "  SELECT 'CLASS' as item_type, cle.enrollment_id, cle.user_id, cle.class_id as item_id, " +
                        "         cle.price_paid, cle.payment_method, cle.enrolled_at, cle.status, " +
                        "         cl.class_name as item_name, cl.thumbnail_url as item_thumb " +
                        "  FROM class_enrollment cle " +
                        "  JOIN class cl ON cle.class_id = cl.class_id " +
                        ") as combined WHERE user_id = ? "
        );

        if (type != null && !type.isEmpty()) sql.append(" AND item_type = ? ");
        if (status != null && !status.isEmpty()) sql.append(" AND status = ? ");
        if (keyword != null && !keyword.isEmpty()) sql.append(" AND item_name LIKE ? ");

        sql.append(" ORDER BY enrolled_at DESC");

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int idx = 1;
            stmt.setInt(idx++, userId);
            if (type != null && !type.isEmpty()) stmt.setString(idx++, type);
            if (status != null && !status.isEmpty()) stmt.setBoolean(idx++, Boolean.parseBoolean(status));
            if (keyword != null && !keyword.isEmpty()) stmt.setString(idx++, "%" + keyword + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("type", rs.getString("item_type"));
                    map.put("enrollmentId", rs.getInt("enrollment_id"));
                    map.put("itemId", rs.getInt("item_id"));
                    map.put("itemName", rs.getString("item_name"));
                    map.put("itemThumb", rs.getString("item_thumb"));
                    map.put("price", rs.getBigDecimal("price_paid"));
                    map.put("date", rs.getTimestamp("enrolled_at"));
                    map.put("status", rs.getBoolean("status"));
                    map.put("payment", rs.getString("payment_method"));
                    enrollments.add(map);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return enrollments;
    }
}
