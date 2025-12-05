package dao;

import model.User;
import utils.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    private boolean isAllCategoriesSelected(String[] categories) {
        if (categories == null) return false;
        for (String cat : categories) {
            if ("all".equals(cat) || cat == null || cat.isEmpty()) return true;
        }
        return false;
    }

    public List<Class> getActiveClasses(String keyword, String[] selectedCategories, String priceSort) {
        List<Class> classes = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection()) {
            StringBuilder sql = new StringBuilder("SELECT" +
                    "    c.class_id," +
                    "    c.class_name," +
                    "    c.thumbnail_url," +
                    "    c.listed_price," +
                    "    c.sale_price," +
                    "    c.status," +
                    "    c.description," +
                    "    c.start_date," +
                    "    c.end_date," +
                    "    GROUP_CONCAT(cat.setting_name SEPARATOR ', ') AS categories," +
                    "    u.user_id as instructor_id," +
                    "    u.fullname AS instructor_name" +
                    " FROM class c" +
                    " LEFT JOIN class_category cc ON c.class_id = cc.class_id" +
                    " LEFT JOIN setting cat ON cc.category_id = cat.setting_id AND cat.type_id = 5" +
                    " LEFT JOIN user u ON c.instructor_id = u.user_id" +
                    " LEFT JOIN setting s ON u.role_id = s.setting_id AND s.setting_name = 'Instructor'" +
                    " WHERE c.status = 1");


            if (keyword != null && !keyword.trim().isEmpty()) {
                sql.append(" AND (c.class_name LIKE ? OR c.description LIKE ? OR u.fullname LIKE ?) ");
            }


            if (selectedCategories != null && selectedCategories.length > 0) {
                sql.append(" AND s.setting_name IN (");
                for (int i = 0; i < selectedCategories.length; i++) {
                    sql.append("?");
                    if (i < selectedCategories.length - 1) sql.append(",");
                }
                sql.append(")");
            }

            sql.append(" GROUP BY " +
                    "    c.class_id, c.class_name, c.thumbnail_url, c.listed_price, " +
                    "    c.sale_price, c.status, c.description, c.start_date," +
                    "    c.end_date, u.user_id, u.fullname");

            if ("low".equalsIgnoreCase(priceSort)) {
                sql.append(" ORDER BY COALESCE(c.sale_price, c.listed_price) ASC");
            } else if ("high".equalsIgnoreCase(priceSort)) {
                sql.append(" ORDER BY COALESCE(c.sale_price, c.listed_price) DESC");
            } else {
                sql.append(" ORDER BY c.class_id ASC");
            }

            PreparedStatement stmt = conn.prepareStatement(sql.toString());

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Class cls = new Class();
                cls.setId(rs.getInt("class_id"));
                cls.setName(rs.getString("class_name"));
                cls.setThumbnailUrl(rs.getString("thumbnail_url"));
                cls.setListedPrice(rs.getBigDecimal("listed_price"));
                cls.setSalePrice(rs.getBigDecimal("sale_price"));
                cls.setDescription(rs.getString("description"));
                cls.setStatus(rs.getBoolean("status"));
                cls.setStartDate(rs.getDate("start_date"));
                cls.setEndDate(rs.getDate("end_date"));

                User instructor = new User();
                instructor.setId(rs.getInt("instructor_id"));
                instructor.setFullname(rs.getString("instructor_name"));
                cls.setInstructor(instructor);

                classes.add(cls);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return classes;
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

    public List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT DISTINCT s.setting_name " +
                "FROM setting s " +
                "INNER JOIN class_category cc ON s.setting_id = cc.category_id " +
                "WHERE s.status = 1 " +
                "ORDER BY s.setting_name";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                categories.add(rs.getString("setting_name"));
            }
        } catch (SQLException e) {
            System.err.println("Error getting category names: " + e.getMessage());
            e.printStackTrace();
        }
        return categories;
    }
}
