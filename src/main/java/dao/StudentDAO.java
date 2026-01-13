package dao;

import model.Student;
import utils.DBUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StudentDAO {

    private StringBuilder buildBaseSql(String keyword, String status, String className) {
        StringBuilder sql = new StringBuilder(
                "FROM user u " +
                        "JOIN setting s ON u.role_id = s.setting_id " +
                        "LEFT JOIN class_enrollment ce ON u.user_id = ce.user_id " +
                        "LEFT JOIN class c ON ce.class_id = c.class_id " +
                        "WHERE s.setting_name = 'Student' " +
                        "AND c.instructor_id = ? "
        );

        if (status != null && !status.isEmpty()) {
            sql.append(" AND ce.status = ? ");
        }

        if (className != null && !className.isEmpty()) {
            sql.append(" AND c.class_name = ? ");
        }

        if (keyword != null && !keyword.isEmpty()) {
            sql.append(" AND (u.fullname LIKE ? OR u.email LIKE ?) ");
        }

        return sql;
    }

    private List<Object> getSearchParams(String keyword, String status, String className) {
        List<Object> params = new ArrayList<>();


        if (status != null && !status.isEmpty()) {
            params.add(Integer.parseInt(status));
        }

        if (className != null && !className.isEmpty()) {
            params.add(className);
        }

        if (keyword != null && !keyword.isEmpty()) {
            String keywordWithWildcards = "%" + keyword + "%";

            params.add(keywordWithWildcards);
            params.add(keywordWithWildcards);
        }

        return params;
    }

    public int countStudents(String keyword, String status, String className, int instructorId) {
        StringBuilder baseSql = buildBaseSql(keyword, status, className);

        String sql = "SELECT COUNT(DISTINCT u.user_id) " + baseSql;

        List<Object> params = getSearchParams(keyword, status, className);
        params.add(instructorId);

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int idx = 1;
            for (Object p : params) {
                if (p instanceof String) ps.setString(idx++, (String) p);
                else if (p instanceof Integer) ps.setInt(idx++, (Integer) p);
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }


    public List<Student> searchStudents(String keyword, String status, String className,
                                        int pageIndex, int pageSize, int instructorId) {
        List<Student> students = new ArrayList<>();

        StringBuilder baseSql = buildBaseSql(keyword, status, className);

        String finalSql = "SELECT u.user_id, u.fullname, u.email, "
                + "ce.status AS enrollment_status, "
                + "u.avatar_url, "
                + "GROUP_CONCAT(c.class_name SEPARATOR ', ') AS class_name "
                + baseSql.toString()
                + "GROUP BY u.user_id, u.fullname, u.email, ce.status, u.avatar_url "
                + "ORDER BY u.fullname ASC "
                + "LIMIT ? OFFSET ?";

        List<Object> params = getSearchParams(keyword, status, className);

        int offset = (pageIndex - 1) * pageSize;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(finalSql)) {

            int index = 1;
            
            ps.setInt(index++, instructorId);

            for (Object param : params) {
                if (param instanceof Integer) {
                    ps.setInt(index++, (Integer) param);
                } else if (param instanceof String) {
                    ps.setString(index++, (String) param);
                }
            }

            ps.setInt(index++, pageSize); // LIMIT
            ps.setInt(index++, offset);  // OFFSET

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Student student = new Student();
                    student.setId(rs.getInt("user_id"));
                    student.setFullname(rs.getString("fullname"));
                    student.setEmail(rs.getString("email"));
                    student.setStatus(rs.getBoolean("enrollment_status"));
                    student.setAvatarUrl(rs.getString("avatar_url"));
                    student.setClassName(rs.getString("class_name"));
                    students.add(student);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }



    public boolean addStudentToClasses(String identifier, boolean isEmail, String[] classNames)
            throws SQLException {

        if (classNames == null || classNames.length == 0) return false;

        String findUserSql = isEmail
                ? "SELECT user_id FROM user WHERE email = ?"
                : "SELECT user_id FROM user WHERE username = ?";

        String findClassSql =
                "SELECT class_id FROM class WHERE class_name = ?";

        String insertEnrollSql =
                "INSERT INTO class_enrollment (user_id, class_id, price_paid, payment_method, enrolled_at, status) " +
                        "VALUES (?, ?, ?, ?, NOW(), ?)";

        String updateClassSql =
                "UPDATE class SET number_of_students = number_of_students + 1 WHERE class_id = ?";

        Connection conn = null;

        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            // 1. Find user
            int userId;
            try (PreparedStatement ps = conn.prepareStatement(findUserSql)) {
                ps.setString(1, identifier);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return false;
                    }
                    userId = rs.getInt("user_id");
                }
            }

            // 2. Loop qua các class
            for (String className : classNames) {
                if (className == null || className.trim().isEmpty()) continue;

                int classId;

                // Find class
                try (PreparedStatement ps = conn.prepareStatement(findClassSql)) {
                    ps.setString(1, className.trim());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            conn.rollback();
                            return false;
                        }
                        classId = rs.getInt("class_id");
                    }
                }

                // Insert enrollment
                try (PreparedStatement ps = conn.prepareStatement(insertEnrollSql)) {
                    ps.setInt(1, userId);
                    ps.setInt(2, classId);
                    ps.setBigDecimal(3, BigDecimal.ZERO);
                    ps.setString(4, "Teacher Added");
                    ps.setBoolean(5, true);
                    ps.executeUpdate();
                }

                // Update number of students
                try (PreparedStatement ps = conn.prepareStatement(updateClassSql)) {
                    ps.setInt(1, classId);
                    ps.executeUpdate();
                }   
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) conn.close();
        }
    }



    public Student getStudentById(int id) {
        Student st = null;

        String sql =
                "SELECT u.user_id, u.fullname, u.username, u.email, u.status, u.avatar_url, " +
                        "IFNULL(GROUP_CONCAT(DISTINCT c.class_name SEPARATOR ', '), '') AS class_names " +
                        "FROM user u " +
                        "JOIN setting s ON u.role_id = s.setting_id " +
                        "LEFT JOIN class_enrollment ce ON u.user_id = ce.user_id " +
                        "LEFT JOIN class c ON ce.class_id = c.class_id " +
                        "WHERE u.user_id = ? AND s.setting_name = 'Student' " +
                        "GROUP BY u.user_id";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                st = new Student();
                st.setId(rs.getInt("user_id"));
                st.setFullname(rs.getString("fullname"));
                st.setEmail(rs.getString("email"));
                st.setStatus(rs.getBoolean("status"));
                st.setUsername(rs.getString("username"));
                st.setAvatarUrl(rs.getString("avatar_url"));
                st.setClassName(rs.getString("class_names"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return st;
    }

    public Set<String> getEnrolledClassNames(String identifier, boolean isEmail) throws SQLException {
        String sql = isEmail
                ? "SELECT c.class_name FROM class_enrollment ce " +
                "JOIN user u ON ce.user_id = u.user_id " +
                "JOIN class c ON ce.class_id = c.class_id " +
                "WHERE u.email = ?"
                : "SELECT c.class_name FROM class_enrollment ce " +
                "JOIN user u ON ce.user_id = u.user_id " +
                "JOIN class c ON ce.class_id = c.class_id " +
                "WHERE u.username = ?";

        Set<String> result = new HashSet<>();

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, identifier);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                result.add(rs.getString("class_name").trim().toLowerCase());
            }
        }
        return result;
    }


    public boolean updateStudentStatus(int userId, boolean newStatus) {
        String sql = "UPDATE class_enrollment SET status = ? WHERE user_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBoolean(1, newStatus);
            ps.setInt(2, userId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getFullnameById(int userId) {
        String sql = "SELECT fullname FROM user WHERE user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getString(1) : null;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> getAllClassNames(int instructorId) {
        List<String> list = new ArrayList<>();

        String sql = "SELECT class_name " +
                "FROM class " +
                "WHERE status = 1 AND instructor_id = ? " +
                "ORDER BY class_name ASC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, instructorId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(rs.getString("class_name"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}