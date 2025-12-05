package dao;

import model.Student;
import utils.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    private StringBuilder buildBaseSql(String keyword, String status, String className) {
        StringBuilder sql = new StringBuilder(
                "FROM user u " +
                        "JOIN setting s ON u.role_id = s.setting_id " +
                        "LEFT JOIN class_user cu ON u.user_id = cu.user_id " +
                        "LEFT JOIN class c ON cu.class_id = c.class_id " +
                        "WHERE s.setting_name = 'Student' " +
                        "AND c.instructor_id = ? "
        );

        if (status != null && !status.isEmpty()) {
            sql.append(" AND u.status = ? ");
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

        // ... (logic cho status và className)

        if (status != null && !status.isEmpty()) {
            params.add(Integer.parseInt(status));
        }

        if (className != null && !className.isEmpty()) {
            params.add(className);
        }

        // >> LỖI TÌM KIẾM: PHẢI THÊM KÝ TỰ ĐẠI DIỆN (%) VÀO KEYWORD <<
        if (keyword != null && !keyword.isEmpty()) {
            String keywordWithWildcards = "%" + keyword + "%";

            // Vì SQL dùng 'OR u.fullname LIKE ? OR u.email LIKE ?', nên cần thêm 2 lần
            params.add(keywordWithWildcards); // Tham số 1 cho fullname
            params.add(keywordWithWildcards); // Tham số 2 cho email
        }

        return params;
    }

    public int countStudents(String keyword, String status, String className, int instructorId) {
        StringBuilder baseSql = buildBaseSql(keyword, status, className);

        String sql = "SELECT COUNT(DISTINCT u.user_id) " + baseSql;

        List<Object> params = getSearchParams(keyword, status, className);
        params.add(instructorId); // thêm instructorId vào cuối

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

        // 1. Xây dựng mệnh đề FROM...WHERE
        StringBuilder baseSql = buildBaseSql(keyword, status, className);

        // 2. Xây dựng câu SELECT hoàn chỉnh và phân trang (LIMIT/OFFSET)
        String finalSql = "SELECT u.user_id, u.fullname, u.email, u.status, u.avatar_url, "
                + "GROUP_CONCAT(c.class_name SEPARATOR ', ') AS class_name "
                + baseSql.toString()
                + "GROUP BY u.user_id, u.fullname, u.email, u.status, u.avatar_url "
                + "ORDER BY u.user_id DESC " // Sắp xếp theo ID mới nhất
                + "LIMIT ? OFFSET ?"; // Tham số cuối cùng: limit và offset

        // 3. Lấy danh sách tham số (trừ instructorId)
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
                    student.setStatus(rs.getBoolean("status"));
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



    public boolean addStudentToClass(String identifier, boolean isEmail, String className) throws SQLException {

        String findUserSql = isEmail
                ? "SELECT user_id, role_id FROM user WHERE email = ?"
                : "SELECT user_id, role_id FROM user WHERE username = ?";

        String sqlFindClass = "SELECT class_id FROM class WHERE class_name = ?";
        String sqlFindStudentRole = "SELECT setting_id FROM setting WHERE setting_name = 'Student'";
        String sqlInsertClassUser = "INSERT IGNORE INTO class_user (user_id, class_id) VALUES (?, ?)";
        String sqlUpdateRole = "UPDATE user SET role_id = ? WHERE user_id = ?";

        Connection conn = null;

        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            int userId, currentRoleId;

            // 1. Lấy user
            try (PreparedStatement ps = conn.prepareStatement(findUserSql)) {
                ps.setString(1, identifier);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return false;
                    }
                    userId = rs.getInt("user_id");
                    currentRoleId = rs.getInt("role_id");
                }
            }

            // 2. Lấy class_id
            int classId;
            try (PreparedStatement ps = conn.prepareStatement(sqlFindClass)) {
                ps.setString(1, className);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return false;
                    }
                    classId = rs.getInt(1);
                }
            }

            // 3. Thêm class_user
            try (PreparedStatement ps = conn.prepareStatement(sqlInsertClassUser)) {
                ps.setInt(1, userId);
                ps.setInt(2, classId);
                ps.executeUpdate();
            }

            // 4. Role Student
            int studentRoleId;
            try (PreparedStatement ps = conn.prepareStatement(sqlFindStudentRole);
                 ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    conn.rollback();
                    return false;
                }
                studentRoleId = rs.getInt(1);
            }

            // 5. Nếu role chưa phải Student → update
            if (currentRoleId != studentRoleId) {
                try (PreparedStatement ps = conn.prepareStatement(sqlUpdateRole)) {
                    ps.setInt(1, studentRoleId);
                    ps.setInt(2, userId);
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
                        "LEFT JOIN class_user cu ON u.user_id = cu.user_id " +
                        "LEFT JOIN class c ON cu.class_id = c.class_id " +
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

    public boolean updateStudentStatus(int userId, boolean newStatus) {
        String sql = "UPDATE user SET status = ? WHERE user_id = ?";

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