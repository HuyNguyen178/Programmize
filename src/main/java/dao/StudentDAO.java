package dao;

import model.Student;
import utils.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    public List<Student> searchStudents(String keyword, String status, String className) {
        List<Student> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT u.user_id, u.fullname, u.email, u.status, c.class_name " +
                        "FROM user u " +
                        "LEFT JOIN class_user cu ON u.user_id = cu.user_id " +
                        "LEFT JOIN `class` c ON cu.class_id = c.class_id " +
                        "WHERE u.user_id IN (SELECT user_id FROM user_role WHERE role_id = 1) " // chưa có gì trong db nên sửa sau
        );

        if (status != null && !status.isEmpty()) {
            sql.append(" AND u.status = ").append(status);
        }

        if (className != null && !className.isEmpty()) {
            sql.append(" AND c.class_name = ?");
        }

        if (keyword != null && !keyword.isEmpty()) {
            sql.append(" AND (u.fullname LIKE ? OR u.email LIKE ?)");
        }

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int index = 1;
            if (className != null && !className.isEmpty()) {
                ps.setString(index++, className);
            }
            if (keyword != null && !keyword.isEmpty()) {
                ps.setString(index++, "%" + keyword + "%");
                ps.setString(index++, "%" + keyword + "%");
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Student st = new Student();
                st.setId(rs.getInt("user_id"));
                st.setFullname(rs.getString("fullname"));
                st.setEmail(rs.getString("email"));
                st.setStatus(rs.getBoolean("status"));
                st.setClassName(rs.getString("class_name"));
                list.add(st);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}
