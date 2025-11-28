package dao;
//Kien
import model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
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
        String sql = "INSERT INTO user (fullname, username, email, password, status) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Mã hóa password
            String hashed = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());

            stmt.setString(1, user.getFullname());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, hashed);
            stmt.setBoolean(5, user.isStatus());

            if (stmt.executeUpdate() > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            return false; // trùng email/username
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

}
