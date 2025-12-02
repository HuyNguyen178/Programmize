package dao;

import model.Class;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PublicClassDAO {

    private final Connection conn;

    public PublicClassDAO(Connection conn) {
        this.conn = conn;
    }

    // Lấy tất cả class đang active (status = 1)
    public List<Class> getActiveClasses() {
        List<Class> list = new ArrayList<>();

        String sql =
                "SELECT class_id, class_name, thumbnail_url, number_of_students, " +
                        "       status, description, start_date, end_date " +
                        "FROM class " +
                        "WHERE status = 1 " +
                        "ORDER BY start_date DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
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

    // Lấy chi tiết 1 class theo id
    public Class getClassById(int id) {
        String sql =
                "SELECT class_id, class_name, thumbnail_url, number_of_students, " +
                        "       status, description, start_date, end_date " +
                        "FROM class " +
                        "WHERE class_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
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
