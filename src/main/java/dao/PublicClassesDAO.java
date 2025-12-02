package dao;

import model.Class;
import utils.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PublicClassesDAO {

    public PublicClassesDAO() {
    }

    public List<Class> getActiveClasses() {
        List<Class> list = new ArrayList<>();

        try (Connection connection = DBUtil.getConnection()) {
            String sql = "SELECT class_id, class_name, thumbnail_url, number_of_students, status, description, start_date, end_date FROM class WHERE status = 1 ORDER BY start_date DESC";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Class c = new Class();
                c.setId(rs.getInt("class_id"));
                c.setName(rs.getString("class_name"));
                c.setThumbnailUrl(rs.getString("thumbnail_url"));
                c.setNumberOfStudents(rs.getInt("number_of_students"));
                c.setStatus(rs.getBoolean("status"));
                c.setDescription(rs.getString("description"));
                c.setStartDate(rs.getDate("start_date"));
                c.setEndDate(rs.getDate("end_date"));
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
