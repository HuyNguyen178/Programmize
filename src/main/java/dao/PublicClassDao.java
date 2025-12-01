package dao;

import model.PublicClass;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PublicClassDao {

    private final Connection conn;

    public PublicClassDao(Connection conn) {
        this.conn = conn;
    }

    // Lấy tất cả class đang active (status = 1)
    public List<PublicClass> getActiveClasses() {
        List<PublicClass> list = new ArrayList<>();

        String sql =
                "SELECT class_id, class_name, thumbnail_url, number_of_students, " +
                        "       status, description, start_date, end_date " +
                        "FROM class " +
                        "WHERE status = 1 " +
                        "ORDER BY start_date DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                PublicClass c = new PublicClass();
                c.setClassId(rs.getInt("class_id"));
                c.setClassName(rs.getString("class_name"));
                c.setThumbnailUrl(rs.getString("thumbnail_url"));
                c.setNumberOfStudents(rs.getInt("number_of_students"));
                c.setStatus(rs.getInt("status"));
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
    public PublicClass getClassById(int classId) {
        String sql =
                "SELECT class_id, class_name, thumbnail_url, number_of_students, " +
                        "       status, description, start_date, end_date " +
                        "FROM class " +
                        "WHERE class_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, classId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                PublicClass c = new PublicClass();
                c.setClassId(rs.getInt("class_id"));
                c.setClassName(rs.getString("class_name"));
                c.setThumbnailUrl(rs.getString("thumbnail_url"));
                c.setNumberOfStudents(rs.getInt("number_of_students"));
                c.setStatus(rs.getInt("status"));
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
