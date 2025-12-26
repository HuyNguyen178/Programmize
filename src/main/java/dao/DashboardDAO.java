package dao;

import model.Class;
import model.Course;
import model.User;
import utils.DBUtil;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

public class DashboardDAO {

    public static class DashboardData {
        public int totalUsers;
        public int totalCourses;
        public int totalClasses;
        public List<BigDecimal> monthlyRevenue = new ArrayList<>(Collections.nCopies(12, BigDecimal.ZERO));
        public List<Object[]> topCourses = new ArrayList<>();
        public List<Object[]> topClasses = new ArrayList<>();
        public List<Object[]> topInstructors = new ArrayList<>();
        public List<User> recentUsers = new ArrayList<>();
    }

    public DashboardData getEverything(int year, int limit) {
        DashboardData data = new DashboardData();
        String start = year + "-01-01";
        String end = year + "-12-31 23:59:59";

        // SQL dùng 6 cột (v1 -> v5 + type) để chứa đủ dữ liệu
        // v1: ID/Month, v2: Revenue/Total1, v3: Count/Total2, v4: Name, v5: Status (boolean/int)
        String sql =
                "SELECT 'stats' as type, (SELECT COUNT(*) FROM user) as v1, (SELECT COUNT(*) FROM course) as v2, (SELECT COUNT(*) FROM class) as v3, '' as v4, 0 as v5 " +
                        "UNION ALL " +
                        "SELECT 'rev' as type, m as v1, SUM(total) as v2, 0 as v3, '' as v4, 0 as v5 FROM (" +
                        "  SELECT MONTH(enrolled_at) as m, SUM(price_paid) as total FROM course_enrollment WHERE status=1 AND enrolled_at BETWEEN ? AND ? GROUP BY m " +
                        "  UNION ALL " +
                        "  SELECT MONTH(enrolled_at) as m, SUM(price_paid) as total FROM class_enrollment WHERE status=1 AND enrolled_at BETWEEN ? AND ? GROUP BY m" +
                        ") as r GROUP BY m " +
                        "UNION ALL " +
                        "SELECT * FROM (SELECT 't_course' as type, c.course_id as v1, SUM(ce.price_paid) as v2, COUNT(ce.user_id) as v3, c.course_name as v4, 1 as v5 FROM course c " +
                        "JOIN course_enrollment ce ON c.course_id = ce.course_id WHERE ce.status=1 GROUP BY c.course_id ORDER BY v2 DESC LIMIT ?) tc " +
                        "UNION ALL " +
                        "SELECT * FROM (SELECT 't_class' as type, cl.class_id as v1, SUM(cle.price_paid) as v2, COUNT(cle.user_id) as v3, cl.class_name as v4, 1 as v5 FROM class cl " +
                        "JOIN class_enrollment cle ON cl.class_id = cle.class_id WHERE cle.status=1 GROUP BY cl.class_id ORDER BY v2 DESC LIMIT ?) tcl " +
                        "UNION ALL " +
                        "SELECT * FROM (SELECT 't_inst' as type, u.user_id as v1, COUNT(cl_u.user_id) as v2, 0 as v3, u.fullname as v4, u.status as v5 FROM user u " +
                        "JOIN class cl ON u.user_id = cl.instructor_id LEFT JOIN class_enrollment cl_u ON cl.class_id = cl_u.class_id " +
                        "GROUP BY u.user_id, u.fullname, u.status ORDER BY v2 DESC LIMIT ?) ti " +
                        "UNION ALL " +
                        "SELECT * FROM (SELECT 'recent_u' as type, u.user_id as v1, 0 as v2, 0 as v3, u.fullname as v4, u.status as v5 FROM user u ORDER BY u.user_id DESC LIMIT ?) ru";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, start); ps.setString(2, end);
            ps.setString(3, start); ps.setString(4, end);
            ps.setInt(5, limit);
            ps.setInt(6, limit);
            ps.setInt(7, limit);
            ps.setInt(8, limit);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String type = rs.getString("type");
                switch (type) {
                    case "stats":
                        data.totalUsers = rs.getInt("v1");
                        data.totalCourses = rs.getInt("v2");
                        data.totalClasses = rs.getInt("v3");
                        break;
                    case "rev":
                        int mIdx = rs.getInt("v1") - 1;
                        if (mIdx >= 0 && mIdx < 12) data.monthlyRevenue.set(mIdx, rs.getBigDecimal("v2"));
                        break;
                    case "t_course":
                        Course c = new Course();
                        c.setId(rs.getInt("v1"));
                        c.setCourseName(rs.getString("v4"));
                        data.topCourses.add(new Object[]{c, rs.getInt("v3"), rs.getBigDecimal("v2")});
                        break;
                    case "t_class":
                        Class cl = new Class();
                        cl.setId(rs.getInt("v1"));
                        cl.setName(rs.getString("v4"));
                        data.topClasses.add(new Object[]{cl, rs.getInt("v3"), rs.getBigDecimal("v2")});
                        break;
                    case "t_inst":
                        User inst = new User();
                        inst.setId(rs.getInt("v1"));
                        inst.setFullname(rs.getString("v4"));
                        inst.setStatus(rs.getBoolean("v5"));
                        data.topInstructors.add(new Object[]{inst, rs.getInt("v2")});
                        break;
                    case "recent_u":
                        User u = new User();
                        u.setId(rs.getInt("v1"));
                        u.setFullname(rs.getString("v4"));
                        u.setStatus(rs.getBoolean("v5"));
                        data.recentUsers.add(u);
                        break;
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return data;
    }
}