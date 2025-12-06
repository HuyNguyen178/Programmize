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

    public List<Class> getClassesByUserId(int userId, String category, String keyword, int offset, int limit) {
        List<Class> classes = new ArrayList<>();

        try (Connection connection = DBUtil.getConnection()) {
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
                    " LEFT JOIN class_user cu ON cu.class_id = c.class_id" +
                    " LEFT JOIN class_category cc ON c.class_id = cc.class_id" +
                    " LEFT JOIN setting cat ON cc.category_id = cat.setting_id AND cat.type_id = 5" +
                    " LEFT JOIN user u ON c.instructor_id = u.user_id" +
                    " LEFT JOIN setting s ON u.role_id = s.setting_id AND s.setting_name = 'Instructor'" +
                    " WHERE c.status = 1 AND cu.user_id = ?");

            if (keyword != null && !keyword.trim().isEmpty()) {
                sql.append(" AND (c.class_name LIKE ? OR u.fullname LIKE ?) ");
            }

            if (category != null && !category.trim().isEmpty()) {
                sql.append(" AND cat.setting_name = ?");
            }

            sql.append(" GROUP BY " +
                    "    c.class_id, c.class_name, c.thumbnail_url, c.listed_price, " +
                    "    c.sale_price, c.status, c.description, c.start_date," +
                    "    c.end_date, u.user_id, u.fullname");

            sql.append(" LIMIT ? OFFSET ?");

            PreparedStatement statement = connection.prepareStatement(sql.toString());
            int index = 1;
            statement.setInt(index++, userId);

            if (keyword != null && !keyword.trim().isEmpty()) {
                statement.setString(index++, "%" + keyword + "%");
                statement.setString(index++, "%" + keyword + "%");
            }

            if (category != null && !category.trim().isEmpty()) {
                statement.setString(index++, category);
            }

            statement.setInt(index++, limit);
            statement.setInt(index, offset);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Class c = new Class();
                c.setId(resultSet.getInt("class_id"));
                c.setName(resultSet.getString("class_name"));
                c.setThumbnailUrl(resultSet.getString("thumbnail_url"));
                c.setStatus(resultSet.getBoolean("status"));
                c.setDescription(resultSet.getString("description"));
                c.setStartDate(resultSet.getDate("start_date"));
                c.setEndDate(resultSet.getDate("end_date"));

                User instructor = new User();
                instructor.setId(resultSet.getInt("instructor_id"));
                instructor.setFullname(resultSet.getString("instructor_name"));

                c.setInstructor(instructor);
                classes.add(c);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return classes;
    }

    public List<Class> getActiveClasses(String keyword, String category, String priceSort) {
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


            if (category != null && !category.trim().isEmpty()) {
                sql.append(" AND cat.setting_name = ?");
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

            int paramIndex = 1;

            if (keyword != null && !keyword.trim().isEmpty()) {
                stmt.setString(paramIndex++, "%" + keyword + "%");
                stmt.setString(paramIndex++, "%" + keyword + "%");
                stmt.setString(paramIndex++, "%" + keyword + "%");
            }

            if (category != null && !category.trim().isEmpty()) {
                stmt.setString(paramIndex++, category);
            }

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

    public int countClassesByUserId(int userId, String category, String keyword) {
        try (Connection connection = DBUtil.getConnection()) {
            String sql = "SELECT COUNT(*) FROM class c "
                    + "LEFT JOIN class_user cu ON c.class_id = cu.class_id "
                    + "WHERE cu.user_id = ? ";

            if (category != null && !category.isEmpty()) {
                sql += " AND c.category = ? ";
            }

            if (keyword != null && !keyword.isEmpty()) {
                sql += " AND c.class_name LIKE ? ";
            }

            PreparedStatement ps = connection.prepareStatement(sql);

            int idx = 1;
            ps.setInt(idx++, userId);

            if (category != null && !category.isEmpty()) {
                ps.setString(idx++, category);
            }

            if (keyword != null && !keyword.isEmpty()) {
                ps.setString(idx++, "%" + keyword + "%");
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
