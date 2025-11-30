package dao;

import model.User;
import utils.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Class;

public class ClassDAO {
    private UserDAO userDAO;

    public ClassDAO() {
        userDAO = new UserDAO();
    }

    public List<Class> getClassesByUserId(int userId, Integer status, String search, int offset, int limit) {
        List<Class> classes = new ArrayList<>();

        try (Connection connection = DBUtil.getConnection()) {
            StringBuilder sql = new StringBuilder(
                    "SELECT DISTINCT c.*, u.user_id AS instructor_id, u.fullname AS instructor_name, u.avatar_url AS instructor_avatar " +
                            "FROM class c " +
                            "JOIN class_user cu ON c.class_id = cu.class_id " +
                            "JOIN class_user cu2 ON cu2.class_id = c.class_id " +
                            "JOIN user u ON u.user_id = cu2.user_id " +
                            "JOIN user_role ur ON ur.user_id = u.user_id " +
                            "JOIN setting s ON s.setting_id = ur.role_id " +
                            "WHERE cu.user_id = ? AND s.setting_name = 'Instructor' "
            );

            if (status != null && status != 0) {
                if (status == 1) sql.append("AND c.status = 1 ");
                else if (status == 2) sql.append("AND c.status = 2 ");
            }

            if (search != null && !search.trim().isEmpty()) {
                sql.append("AND (c.class_name LIKE ? OR u.fullname LIKE ?) ");
            }

            sql.append("LIMIT ? OFFSET ?");

            PreparedStatement statement = connection.prepareStatement(sql.toString());
            int index = 1;
            statement.setInt(index++, userId);

            if (search != null && !search.trim().isEmpty()) {
                statement.setString(index++, "%" + search + "%");
                statement.setString(index++, "%" + search + "%");
            }

            statement.setInt(index++, limit);
            statement.setInt(index, offset);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Class c = new Class();
                c.setId(resultSet.getInt("class_id"));
                c.setName(resultSet.getString("class_name"));
                c.setThumbnailUrl(resultSet.getString("thumbnail_url"));
                c.setNumberOfStudents(resultSet.getInt("number_of_students"));
                c.setStatus(resultSet.getBoolean("status"));
                c.setDescription(resultSet.getString("description"));
                c.setStartDate(resultSet.getDate("start_date"));
                c.setEndDate(resultSet.getDate("end_date"));

                User instructor = userDAO.getInstructorByClassId(c.getId());
                c.setInstructor(instructor);
                classes.add(c);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return classes;
    }
}
