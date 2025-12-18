package dao;

import model.User;
import org.mindrot.jbcrypt.BCrypt;
import utils.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private final DBUtil dbUtil;

    public UserDAO() {
        dbUtil = new DBUtil();
    }

    public User checkLogin(String userOrEmail, String password) {
        String sql =
                "SELECT u.user_id, u.fullname, u.username, u.email, u.status, " +
                        "       u.avatar_url, u.password, s.setting_name AS role_name " +
                        "FROM user u " +
                        "LEFT JOIN setting s ON u.role_id = s.setting_id " +
                        "WHERE u.username = ? OR u.email = ? " +
                        "LIMIT 1";

        try (Connection conn = dbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userOrEmail);
            stmt.setString(2, userOrEmail);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {

                    String hashedPassword = rs.getString("password");

                    if (BCrypt.checkpw(password, hashedPassword)) {
                        User u = new User();
                        u.setId(rs.getInt("user_id"));
                        u.setUsername(rs.getString("username"));
                        u.setEmail(rs.getString("email"));
                        u.setFullname(rs.getString("fullname"));
                        u.setStatus(rs.getBoolean("status"));
                        u.setAvatarUrl(rs.getString("avatar_url"));
                        u.setRoleName(rs.getString("role_name"));
                        return u;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean checkUserOrEmailExists(String userOrEmail) {
        String sql = "SELECT 1 FROM user WHERE username = ? OR email = ?";
        try (Connection conn = dbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userOrEmail);
            stmt.setString(2, userOrEmail);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean addUser(User user) {
        String sql =
                "INSERT INTO user (fullname, username, email, password, status, avatar_url, role_id) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            String hashed = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());

            stmt.setString(1, user.getFullname());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, hashed);
            stmt.setBoolean(5, user.isStatus());
            stmt.setString(6, user.getAvatarUrl());
            stmt.setInt(7, getRoleIdByRoleName(user.getRoleName()));

            if (stmt.executeUpdate() > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getInt(1));
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

    public void updateStatusByEmail(String email) {
        String sql = "UPDATE user SET status = TRUE WHERE email = ?";

        try (Connection conn = dbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean updatePasswordByEmail(String email, String newPassword) {
        String sql = "UPDATE user SET password = ? WHERE email = ?";

        try (Connection conn = dbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String hashed = BCrypt.hashpw(newPassword, BCrypt.gensalt());

            stmt.setString(1, hashed);
            stmt.setString(2, email);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public User getInstructorByClassId(int classId) {
        String sql =
                "SELECT u.user_id, u.fullname, u.avatar_url " +
                        "FROM user u " +
                        "JOIN class_user cu ON cu.user_id = u.user_id " +
                        "JOIN setting s ON s.setting_id = u.role_id " +
                        "WHERE cu.class_id = ? AND s.setting_name = 'Instructor'";

        try (Connection conn = dbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, classId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("user_id"));
                user.setFullname(rs.getString("fullname"));
                user.setAvatarUrl(rs.getString("avatar_url"));
                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<User> searchUsers(String keyword, String status, String roleName, int offset, int limit) {
        List<User> list = new ArrayList<>();
        List<Object> params = new ArrayList<>();

        String filterClause = buildFilterConditions(keyword, status, roleName, params);

        String sql =
                "SELECT u.user_id, u.fullname, u.username, u.email, u.status, u.avatar_url, " +
                        "       MIN(s.setting_name) AS role_name " +
                        "FROM user u " +
                        "LEFT JOIN setting s ON u.role_id = s.setting_id " +
                        filterClause +
                        " GROUP BY u.user_id, u.fullname, u.username, u.email, u.status, u.avatar_url " +
                        " ORDER BY u.user_id ASC " +
                        " LIMIT ? OFFSET ?";

        try (Connection conn = dbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            setFilterParameters(ps, params, 1);
            int paramIndex = params.size() + 1;
            ps.setInt(paramIndex++, limit);
            ps.setInt(paramIndex, offset);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("user_id"));
                u.setFullname(rs.getString("fullname"));
                u.setUsername(rs.getString("username"));
                u.setEmail(rs.getString("email"));
                u.setStatus(rs.getBoolean("status"));
                u.setAvatarUrl(rs.getString("avatar_url"));
                u.setRoleName(rs.getString("role_name"));
                list.add(u);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean updateUserStatus(int userId, boolean newStatus) {
        String sql = "UPDATE user SET status = ? WHERE user_id = ?";

        try (Connection conn = dbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, newStatus);
            stmt.setInt(2, userId);

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public int countTotalUsers(String keyword, String status, String roleName) {
        int count = 0;
        List<Object> params = new ArrayList<>();

        String filterClause = buildFilterConditions(keyword, status, roleName, params);

        String sql =
                "SELECT COUNT(DISTINCT u.user_id) " +
                        "FROM user u " +
                        "LEFT JOIN setting s ON u.role_id = s.setting_id " +
                        filterClause;

        try (Connection conn = dbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            setFilterParameters(ps, params, 1);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    private String buildFilterConditions(String keyword, String status, String roleName, List<Object> params) {
        StringBuilder filterSql = new StringBuilder(" WHERE 1=1 ");

        if (keyword != null && !keyword.isEmpty()) {
            filterSql.append(" AND (u.fullname LIKE ? OR u.email LIKE ?) ");
            params.add("%" + keyword + "%");
            params.add("%" + keyword + "%");
        }

        if (status != null && !status.isEmpty()) {
            filterSql.append(" AND u.status = ? ");
            params.add(status.equals("1"));
        }

        if (roleName != null && !roleName.isEmpty()) {
            filterSql.append(" AND s.setting_name = ? ");
            params.add(roleName);
        }

        return filterSql.toString();
    }

    private void setFilterParameters(PreparedStatement ps, List<Object> params, int startIndex) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            int idx = startIndex + i;
            Object param = params.get(i);

            if (param instanceof String) ps.setString(idx, (String) param);
            else if (param instanceof Boolean) ps.setBoolean(idx, (Boolean) param);
            else if (param instanceof Integer) ps.setInt(idx, (Integer) param);
        }
    }

    public User getUserById(int id) {
        String sql =
                "SELECT u.*, s.setting_name AS role_name " +
                        "FROM user u " +
                        "LEFT JOIN setting s ON u.role_id = s.setting_id " +
                        "WHERE u.user_id = ?";

        try (Connection conn = dbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("user_id"));
                    user.setFullname(rs.getString("fullname"));
                    user.setUsername(rs.getString("username"));
                    user.setEmail(rs.getString("email"));
                    user.setStatus(rs.getBoolean("status"));
                    user.setAvatarUrl(rs.getString("avatar_url"));
                    user.setRoleName(rs.getString("role_name"));
                    user.setPassword(rs.getString("password"));
                    return user;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void saveRememberToken(int userId, String token) {
        String sql = "UPDATE user SET remember_token = ? WHERE user_id = ?";

        try (Connection conn = dbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, token);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public User loginWithToken(String token) {
        String sql =
                "SELECT u.user_id, u.fullname, u.username, u.email, u.status, " +
                        "       u.avatar_url, s.setting_name AS role_name " +
                        "FROM user u " +
                        "LEFT JOIN setting s ON u.role_id = s.setting_id " +
                        "WHERE u.remember_token = ? " +
                        "LIMIT 1";

        try (Connection conn = dbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, token);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User u = new User();
                    u.setId(rs.getInt("user_id"));
                    u.setUsername(rs.getString("username"));
                    u.setEmail(rs.getString("email"));
                    u.setFullname(rs.getString("fullname"));
                    u.setAvatarUrl(rs.getString("avatar_url"));
                    u.setRoleName(rs.getString("role_name")); // <<< QUAN TRỌNG
                    return u;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getRoleIdByRoleName(String roleName) {
        String sql = "SELECT setting_id FROM setting WHERE setting_name = ? AND type_id = 1 ";

        int roleId = -1;

        try (Connection conn = dbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, roleName);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    roleId = rs.getInt("setting_id");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return roleId;
    }

    public boolean updateUser(User user, String password) {
        String sql = "UPDATE user SET fullname = ?, email = ?, status = ?, avatar_url = ?, password = ?, role_id = ? " +
                "WHERE user_id = ?";
        int roleId = getRoleIdByRoleName(user.getRoleName());
        String newPassword ;
        try (Connection conn = dbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if(password == null || password.isEmpty()) {
                newPassword = user.getPassword();
            }else {
                newPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            }

            stmt.setString(1, user.getFullname());
            stmt.setString(2, user.getEmail());
            stmt.setBoolean(3, user.isStatus());
            stmt.setString(4, user.getAvatarUrl());
            stmt.setString(5, newPassword);
            stmt.setInt(6, roleId);
            stmt.setInt(7, user.getId());

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Object[]> getTopInstructorsByEnrollment(int limit) {
        List<Object[]> topStatsList = new ArrayList<>();

        String sql = "SELECT u.user_id, u.fullname, u.status, " +
                "COUNT(cl_u.user_id) AS enrollments_count " +
                "FROM user u " +
                "JOIN class cl ON u.user_id = cl.instructor_id " +
                "LEFT JOIN class_user cl_u ON cl.class_id = cl_u.class_id " +
                "WHERE u.role_id = (SELECT setting_id FROM setting WHERE setting_name = 'Instructor') " +
                "GROUP BY u.user_id, u.fullname " +
                "ORDER BY enrollments_count DESC " +
                "LIMIT ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User instructor = new User();
                    instructor.setId(rs.getInt("user_id"));
                    instructor.setFullname(rs.getString("fullname"));
                    instructor.setStatus(rs.getBoolean("status"));
                    Integer enrollments = rs.getInt("enrollments_count");

                    Object[] statArray = new Object[2];
                    statArray[0] = instructor;
                    statArray[1] = enrollments;

                    topStatsList.add(statArray);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return topStatsList;
    }

    public List<User> getAllInstructors() {
        List<User> instructors = new ArrayList<>();
        try (Connection connection = DBUtil.getConnection()) {
            String sql = "SELECT u.*, s.setting_name AS role FROM user u " +
                    "JOIN setting s ON u.role_id = s.setting_id " +
                    "WHERE s.setting_name = 'Instructor'";

            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                User instructor = new User();
                instructor.setId(resultSet.getInt("user_id"));
                instructor.setFullname(resultSet.getString("fullname"));

                instructors.add(instructor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return instructors;
    }

    public User findInstructorByName(String name) {
        User user = null;
        try (Connection connection = DBUtil.getConnection()) {
            String sql = "SELECT * FROM user WHERE fullname = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                user = new User();
                user.setId(resultSet.getInt("user_id"));
                user.setFullname(resultSet.getString("fullname"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }
}
