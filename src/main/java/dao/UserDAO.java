package dao;
//Kien
import model.User;
import org.mindrot.jbcrypt.BCrypt;
import utils.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
//Kien added
import static utils.DBUtil.getConnection;

public class UserDAO {
    public User checkLogin(String userOrEmail, String password) {
        String sql = "SELECT * FROM user WHERE username = ? OR email = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userOrEmail);
            stmt.setString(2, userOrEmail);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {

                    String hashedPassword = rs.getString("password");

                    // So sánh bằng BCrypt
                    if (BCrypt.checkpw(password, hashedPassword)) {
                        User u = new User();
                        u.setId(rs.getInt("user_id"));
                        u.setUsername(rs.getString("username"));
                        u.setEmail(rs.getString("email"));
                        u.setFullname(rs.getString("fullname"));
                        u.setStatus(rs.getBoolean("status"));
                        u.setAvatarUrl(rs.getString("avatar_url"));
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
        String sql = "INSERT INTO user (fullname, username, email, password, status, avatar_url) VALUES (?, ?, ?, ?, ?, ?)";

        String sqlUserRole = "INSERT INTO user_role (user_id, role_id) VALUES (?, ?)";

        Connection conn ;
        PreparedStatement stmt ;
        PreparedStatement statement;

        try {
            conn = getConnection();
            // 1. Tắt Auto-commit để bắt đầu Transaction
            conn.setAutoCommit(false);

            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement = conn.prepareStatement(sqlUserRole);

            String hashed = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());

            stmt.setString(1, user.getFullname());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, hashed);
            stmt.setBoolean(5, user.isStatus());
            stmt.setString(6, user.getAvatarUrl());

            // 2. Thực thi INSERT user
            if (stmt.executeUpdate() > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getInt(1));
                    }
                }

                statement.setInt(1, user.getId());
                statement.setInt(2, 3);

                // 3. Thực thi INSERT user_role
                if(statement.executeUpdate() > 0){
                    conn.commit();
                    return true;
                }
            }
            
            conn.rollback();

        } catch (SQLIntegrityConstraintViolationException e) {
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void updateStatusByEmail(String email) {
        String sql = "UPDATE user SET status = true WHERE email = ?";
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
        try (Connection connection = DBUtil.getConnection()) {
            String sql = "SELECT u.user_id, u.fullname, u.avatar_url FROM user u JOIN class_user cu ON cu.user_id = u.user_id JOIN user_role ur ON ur.user_id = u.user_id JOIN setting s ON s.setting_id = ur.role_id WHERE cu.class_id = ? AND s.setting_name = 'Instructor'";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, classId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getInt("user_id"));
                user.setFullname(resultSet.getString("fullname"));
                user.setAvatarUrl(resultSet.getString("avatar_url"));
                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<User> searchUsers(String keyword, String status, String roleName) {
        List<User> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT u.user_id, u.fullname, u.username, u.email, u.status, u.avatar_url, " +
                        "MIN(s.setting_name) AS role_name " +
                        "FROM user u " +
                        "LEFT JOIN user_role ur ON u.user_id = ur.user_id " +
                        "LEFT JOIN setting s ON ur.role_id = s.setting_id " +
                        "WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();

        // 1. FILTER BY KEYWORD (Search by fullname or email)
        if (keyword != null && !keyword.isEmpty()) {
            sql.append(" AND (u.fullname LIKE ? OR u.email LIKE ?)");
            params.add("%" + keyword + "%");
            params.add("%" + keyword + "%");
        }

        // 2. FILTER BY STATUS
        if (status != null && !status.isEmpty()) {
            sql.append(" AND u.status = ?");
            params.add(status.equals("1"));
        }

        // 3. FILTER BY ROLE (Lọc theo vai trò duy nhất)
        if (roleName != null && !roleName.isEmpty()) {
            sql.append(" AND u.user_id IN (");
            sql.append("    SELECT ur.user_id FROM user_role ur ");
            sql.append("    JOIN setting s ON ur.role_id = s.setting_id ");
            sql.append("    WHERE s.setting_name = ?");
            sql.append(" )");
            params.add(roleName);
        }

        sql.append(" GROUP BY u.user_id, u.fullname, u.username, u.email, u.status, u.avatar_url");
        sql.append(" ORDER BY u.user_id ASC");

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            // Thiết lập các tham số cho PreparedStatement
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof String) {
                    ps.setString(i + 1, (String) param);
                } else if (param instanceof Boolean) {
                    ps.setBoolean(i + 1, (Boolean) param);
                } else if (param instanceof Integer) {
                    ps.setInt(i + 1, (Integer) param);
                }
            }

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

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, newStatus);
            stmt.setInt(2, userId);

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
