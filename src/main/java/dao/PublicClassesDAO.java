package dao;

import model.Class;
import model.User;
import utils.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PublicClassesDAO {

    public List<Class> getActiveClasses() {
        List<Class> list = new ArrayList<>();

        String sql =
                "SELECT " +
                        "  c.class_id, c.class_name, c.thumbnail_url, c.listed_price, c.sale_price, " +
                        "  c.description, c.status, c.start_date, c.end_date, " +
                        "  u.user_id AS instructor_id, u.fullname AS instructor_name, u.avatar_url AS instructor_avatar " +
                        "FROM class c " +
                        "JOIN class_user cu ON cu.class_id = c.class_id " +
                        "JOIN user u ON u.user_id = cu.user_id " +
                        "JOIN user_role ur ON ur.user_id = u.user_id " +
                        "JOIN setting s ON s.setting_id = ur.role_id " +
                        "WHERE s.setting_name = 'Instructor'";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {

                // Tạo instructor
                User instructor = new User();
                instructor.setId(rs.getInt("instructor_id"));
                instructor.setFullname(rs.getString("instructor_name"));
                instructor.setAvatarUrl(rs.getString("instructor_avatar"));

                // Tạo class
                Class c = new Class();
                c.setId(rs.getInt("class_id"));
                c.setName(rs.getString("class_name"));
                c.setThumbnailUrl(rs.getString("thumbnail_url"));
                c.setListed_price(rs.getBigDecimal("listed_price"));
                c.setSale_price(rs.getBigDecimal("sale_price"));
                c.setDescription(rs.getString("description"));
                c.setStatus(rs.getBoolean("status"));
                c.setStartDate(rs.getDate("start_date"));
                c.setEndDate(rs.getDate("end_date"));

                // Gán instructor vào class
                c.setInstructor(instructor);

                list.add(c);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }


    public Class getClassById(int id) {
        try (Connection connection = DBUtil.getConnection()) {
            String sql = "SELECT class_id, class_name, thumbnail_url, number_of_students, status, description, start_date, end_date FROM class WHERE class_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                Class c = new Class();
                c.setId(rs.getInt("class_id"));
                c.setName(rs.getString("class_name"));
                c.setThumbnailUrl(rs.getString("thumbnail_url"));
                c.setNumberOfStudents(rs.getInt("number_of_students"));
                c.setStatus(rs.getBoolean("status"));
                c.setDescription(rs.getString("description"));
                c.setStartDate(rs.getDate("start_date"));
                c.setEndDate(rs.getDate("end_date"));
                return c;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
