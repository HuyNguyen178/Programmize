package dao;

import model.Student;
import utils.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StudentDAO {
    public boolean addStudentToClasses(String identifier, boolean isEmail, String[] classNames)
            throws SQLException {

        if (classNames == null || classNames.length == 0) return false;

        String findUserSql = isEmail
                ? "SELECT user_id FROM user WHERE email = ?"
                : "SELECT user_id FROM user WHERE username = ?";

        String findClassSql = "SELECT class_id FROM class WHERE class_name = ?";

        String checkEnrollmentSql = "SELECT 1 FROM class_enrollment WHERE user_id = ? AND class_id = ?";

        String insertEnrollSql = "INSERT INTO class_enrollment (user_id, class_id, price_paid, payment_method, enrolled_at, status) " +
                "VALUES (?, ?, ?, ?, NOW(), ?)";

        String updateClassSql = "UPDATE class SET number_of_students = number_of_students + 1 WHERE class_id = ?";

        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

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

            boolean anyAdded = false;

            for (String className : classNames) {
                if (className == null || className.trim().isEmpty()) continue;

                int classId;
                try (PreparedStatement ps = conn.prepareStatement(findClassSql)) {
                    ps.setString(1, className.trim());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) continue;
                        classId = rs.getInt("class_id");
                    }
                }

                try (PreparedStatement ps = conn.prepareStatement(checkEnrollmentSql)) {
                    ps.setInt(1, userId);
                    ps.setInt(2, classId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            continue;
                        }
                    }
                }

                try (PreparedStatement ps = conn.prepareStatement(insertEnrollSql)) {
                    ps.setInt(1, userId);
                    ps.setInt(2, classId);
                    ps.setBigDecimal(3, java.math.BigDecimal.ZERO);
                    ps.setString(4, "Teacher Added");
                    ps.setBoolean(5, true);
                    ps.executeUpdate();
                }

                // Cập nhật sĩ số lớp
                try (PreparedStatement ps = conn.prepareStatement(updateClassSql)) {
                    ps.setInt(1, classId);
                    ps.executeUpdate();
                }
                anyAdded = true;
            }

            conn.commit();
            return anyAdded;

        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) conn.close();
        }
    }

    public boolean addStudentToCourses(String identifier, boolean isEmail, String[] courseNames)
            throws SQLException {

        if (courseNames == null || courseNames.length == 0) return false;

        String findUserSql = isEmail
                ? "SELECT user_id FROM user WHERE email = ?"
                : "SELECT user_id FROM user WHERE username = ?";

        String findClassSql = "SELECT course_id FROM course WHERE course_name = ?";

        String checkEnrollmentSql = "SELECT 1 FROM course_enrollment WHERE user_id = ? AND course_id = ?";

        String insertEnrollSql = "INSERT INTO course_enrollment (user_id, course_id, price_paid, payment_method, enrolled_at, status) " +
                "VALUES (?, ?, ?, ?, NOW(), ?)";

        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

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

            boolean success = false;

            for (String courseName : courseNames) {
                if (courseName == null || courseName.trim().isEmpty()) continue;

                int courseId;
                try (PreparedStatement ps = conn.prepareStatement(findClassSql)) {
                    ps.setString(1, courseName.trim());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) continue;
                        courseId = rs.getInt("course_id");
                    }
                }

                try (PreparedStatement ps = conn.prepareStatement(checkEnrollmentSql)) {
                    ps.setInt(1, userId);
                    ps.setInt(2, courseId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            continue;
                        }
                    }
                }

                try (PreparedStatement ps = conn.prepareStatement(insertEnrollSql)) {
                    ps.setInt(1, userId);
                    ps.setInt(2, courseId);
                    ps.setBigDecimal(3, java.math.BigDecimal.ZERO);
                    ps.setString(4, "Teacher Added");
                    ps.setBoolean(5, true);
                    if(ps.executeUpdate() > 0 ){
                        success = true;
                    };
                }


            }
            conn.commit();
            return success;
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


    public boolean updateClassStudentStatus(int userId, int classId, boolean newStatus) {
        String sql = "UPDATE class_enrollment SET status = ? WHERE user_id = ? AND class_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBoolean(1, newStatus);
            ps.setInt(2, userId);
            ps.setInt(3, classId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateCourseStudentStatus(int userId, int classId, boolean newStatus) {
        String sql = "UPDATE course_enrollment SET status = ? WHERE user_id = ? AND class_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBoolean(1, newStatus);
            ps.setInt(2, userId);
            ps.setInt(3, classId);

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

    public int countStudentsByClassId(String keyword, String status, Integer classId, int instructorId) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(DISTINCT u.user_id) FROM user u " +
                        "LEFT JOIN class_enrollment ce ON u.user_id = ce.user_id " +
                        "LEFT JOIN class c ON ce.class_id = c.class_id " +
                        "WHERE c.instructor_id = ? "
        );

        if (status != null && !status.isEmpty()) sql.append(" AND ce.status = ? ");
        if (classId != null) sql.append(" AND c.class_id = ? ");
        if (keyword != null && !keyword.isEmpty()) sql.append(" AND (u.fullname LIKE ? OR u.email LIKE ?) ");

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            ps.setInt(idx++, instructorId);
            if (status != null && !status.isEmpty()) ps.setInt(idx++, Integer.parseInt(status));
            if (classId != null) ps.setInt(idx++, classId);
            if (keyword != null && !keyword.isEmpty()) {
                String kw = "%" + keyword + "%";
                ps.setString(idx++, kw);
                ps.setString(idx++, kw);
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public int countStudentsByCourseId(String keyword, String status, Integer courseId, int instructorId) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(DISTINCT u.user_id) FROM user u " +
                        "LEFT JOIN course_enrollment ce ON u.user_id = ce.user_id " +
                        "LEFT JOIN course c ON ce.course_id = c.course_id " +
                        "WHERE c.instructor_id = ? "
        );

        if (status != null && !status.isEmpty()) sql.append(" AND ce.status = ? ");
        if (courseId != null) sql.append(" AND c.course_id = ? ");
        if (keyword != null && !keyword.isEmpty()) sql.append(" AND (u.fullname LIKE ? OR u.email LIKE ?) ");

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            ps.setInt(idx++, instructorId);
            if (status != null && !status.isEmpty()) ps.setInt(idx++, Integer.parseInt(status));
            if (courseId != null) ps.setInt(idx++, courseId);
            if (keyword != null && !keyword.isEmpty()) {
                String kw = "%" + keyword + "%";
                ps.setString(idx++, kw);
                ps.setString(idx++, kw);
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public List<Student> searchStudentsByClassId(String keyword, String status, Integer classId,
                                                 int pageIndex, int pageSize) {
        List<Student> students = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT u.user_id, u.fullname, u.email, ce.status AS enrollment_status, u.avatar_url, c.class_name " +
                        "FROM user u " +
                        "LEFT JOIN class_enrollment ce ON u.user_id = ce.user_id " +
                        "LEFT JOIN class c ON ce.class_id = c.class_id " +
                        "WHERE true "
        );

        if (status != null && !status.isEmpty()) sql.append(" AND ce.status = ? ");
        if (classId != null) sql.append(" AND c.class_id = ? ");
        if (keyword != null && !keyword.isEmpty()) sql.append(" AND (u.fullname LIKE ? OR u.email LIKE ?) ");
        sql.append(" ORDER BY u.fullname ASC LIMIT ? OFFSET ?");

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (status != null && !status.isEmpty()) ps.setInt(idx++, Integer.parseInt(status));
            if (classId != null) ps.setInt(idx++, classId);
            if (keyword != null && !keyword.isEmpty()) {
                String kw = "%" + keyword + "%";
                ps.setString(idx++, kw);
                ps.setString(idx++, kw);
            }
            ps.setInt(idx++, pageSize);
            ps.setInt(idx++, (pageIndex - 1) * pageSize);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Student student = new Student();
                student.setId(rs.getInt("user_id"));
                student.setFullname(rs.getString("fullname"));
                student.setEmail(rs.getString("email"));
                student.setStatus(rs.getBoolean("enrollment_status"));
                students.add(student);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return students;
    }

    public List<Student> searchStudentsByCourseId(String keyword, String status, Integer courseId,
                                                  int pageIndex, int pageSize){
        List<Student> students = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT u.user_id, u.fullname, u.email, ce.status AS enrollment_status, u.avatar_url, c.course_name " +
                        "FROM user u " +
                        "LEFT JOIN course_enrollment ce ON u.user_id = ce.user_id " +
                        "LEFT JOIN course c ON ce.course_id = c.course_id " +
                        "WHERE true "
        );

        if (status != null && !status.isEmpty()) sql.append(" AND ce.status = ? ");
        if (courseId != null) sql.append(" AND c.course_id = ? ");
        if (keyword != null && !keyword.isEmpty()) sql.append(" AND (u.fullname LIKE ? OR u.email LIKE ?) ");
        sql.append(" ORDER BY u.fullname ASC LIMIT ? OFFSET ?");

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (status != null && !status.isEmpty()) ps.setInt(idx++, Integer.parseInt(status));
            if (courseId != null) ps.setInt(idx++, courseId);
            if (keyword != null && !keyword.isEmpty()) {
                String kw = "%" + keyword + "%";
                ps.setString(idx++, kw);
                ps.setString(idx++, kw);
            }
            ps.setInt(idx++, pageSize);
            ps.setInt(idx++, (pageIndex - 1) * pageSize);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Student student = new Student();
                student.setId(rs.getInt("user_id"));
                student.setFullname(rs.getString("fullname"));
                student.setEmail(rs.getString("email"));
                student.setStatus(rs.getBoolean("enrollment_status"));
                students.add(student);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return students;
    }

    public String getClassNameById(int classId) {
        String sql = "SELECT class_name FROM class WHERE class_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, classId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("class_name");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> getAllCourseNames(int instructorId){
        List<String> list = new ArrayList<>();

        String sql = "SELECT course_name " +
                "FROM course " +
                "WHERE status = 1 AND instructor_id = ? " +
                "ORDER BY course_name ASC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, instructorId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(rs.getString("course_name"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;

    }
}