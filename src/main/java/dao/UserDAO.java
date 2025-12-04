package dao;

import model.User;
import org.mindrot.jbcrypt.BCrypt;
import utils.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import static utils.DBUtil.getConnection;

public class UserDAO {
    public User checkLogin(String userOrEmail, String password) {
        String sql =
                "SELECT u.user_id, u.fullname, u.username, u.email, u.status, " +
                        "       u.avatar_url, u.password, s.setting_name AS role_name " +
                        "FROM user u " +
                        "LEFT JOIN setting s ON u.role_id = s.setting_id " +
                        "WHERE u.username = ? OR u.email = ? " +
                        "LIMIT 1";

        try (Connection conn = getConnection();
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
        try (Connection conn = getConnection();
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

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            String hashed = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());

            stmt.setString(1, user.getFullname());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, hashed);
            stmt.setBoolean(5, user.isStatus());
            stmt.setString(6, user.getAvatarUrl());
            stmt.setInt(7, 3);

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

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean updatePasswordByEmail(String email, String newPassword) {
        String sql = "UPDATE user SET password = ? WHERE email = ?";

        try (Connection conn = getConnection();
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

        try (Connection conn = getConnection();
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

        try (Connection conn = getConnection();
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

        try (Connection conn = getConnection();
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

        try (Connection conn = getConnection();
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
        User user = null;

        String sql =
                "SELECT u.*, s.setting_name AS role_name " +
                        "FROM user u " +
                        "LEFT JOIN setting s ON u.role_id = s.setting_id " +
                        "WHERE u.user_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    user = new User();
                    user.setId(rs.getInt("user_id"));
                    user.setFullname(rs.getString("fullname"));
                    user.setUsername(rs.getString("username"));
                    user.setEmail(rs.getString("email"));
                    user.setStatus(rs.getBoolean("status"));
                    user.setAvatarUrl(rs.getString("avatar_url"));
                    user.setRoleName(rs.getString("role_name"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }
}
