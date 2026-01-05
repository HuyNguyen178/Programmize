package dao;

import model.Course;
import model.Setting;
import model.User;
import utils.DBUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Class;

public class ClassDAO {
    private UserDAO userDAO;

    public ClassDAO() {
        userDAO = new UserDAO();
    }

    public List<Class> getClassesByUserId(int userId, Integer categoryId, String keyword, int offset, int limit) {
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
                    " LEFT JOIN class_enrollment cu ON cu.class_id = c.class_id" +
                    " LEFT JOIN class_category cc ON c.class_id = cc.class_id" +
                    " LEFT JOIN setting cat ON cc.category_id = cat.setting_id AND cat.type_id = 5" +
                    " LEFT JOIN user u ON c.instructor_id = u.user_id" +
                    " LEFT JOIN setting s ON u.role_id = s.setting_id AND s.setting_name = 'Instructor'" +
                    " WHERE c.status = 1 AND cu.user_id = ?");

            if (keyword != null && !keyword.trim().isEmpty()) {
                sql.append(" AND (c.class_name LIKE ? OR u.fullname LIKE ?) ");
            }

            if (categoryId != null) {
                sql.append(" AND cat.setting_id = ?");
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

            if (categoryId != null) {
                statement.setInt(index++, categoryId);
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

    public List<Class> getActiveClasses(String keyword, Integer categoryId, String priceSort, int limit, int offset) {
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


            if (categoryId != null) {
                sql.append(" AND cat.setting_id = ?");
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

            sql.append(" LIMIT ? OFFSET ? ");

            PreparedStatement stmt = conn.prepareStatement(sql.toString());

            int paramIndex = 1;

            if (keyword != null && !keyword.trim().isEmpty()) {
                stmt.setString(paramIndex++, "%" + keyword + "%");
                stmt.setString(paramIndex++, "%" + keyword + "%");
                stmt.setString(paramIndex++, "%" + keyword + "%");
            }

            if (categoryId != null) {
                stmt.setInt(paramIndex++, categoryId);
            }

            stmt.setInt(paramIndex++, limit);
            stmt.setInt(paramIndex++, offset);

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
        String sql = "SELECT c.*, u.avatar_url, u.user_id AS instructor_id, u.fullname AS instructor_name, " +
                "GROUP_CONCAT(DISTINCT s.setting_name SEPARATOR ', ') AS category_names " +
                "FROM class c " +
                "LEFT JOIN user u ON c.instructor_id = u.user_id " +
                "LEFT JOIN class_category cc ON c.class_id = cc.class_id " +
                "LEFT JOIN setting s ON cc.category_id = s.setting_id AND s.type_id = 5 " +
                "WHERE c.class_id = ? " +
                "GROUP BY c.class_id";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Class c = new Class();
                c.setId(rs.getInt("class_id"));
                c.setThumbnailUrl(rs.getString("thumbnail_url"));
                c.setName(rs.getString("class_name"));

                User instructor = new User();
                instructor.setId(rs.getInt("instructor_id"));
                instructor.setFullname(rs.getString("instructor_name"));
                instructor.setAvatarUrl(rs.getString("avatar_url"));
                c.setInstructor(instructor);

                c.setStartDate(rs.getDate("start_date"));
                c.setEndDate(rs.getDate("end_date"));
                c.setListedPrice(rs.getBigDecimal("listed_price"));
                c.setSalePrice(rs.getBigDecimal("sale_price"));

                String cats = rs.getString("category_names");
                if (cats != null && !cats.isEmpty()) {
                    c.setCategories(cats.split(", "));
                } else {
                    c.setCategories(new String[0]);
                }

                c.setDescription(rs.getString("description"));
                c.setStatus(rs.getBoolean("status"));
                return c;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public int countClassesByUserId(int userId, Integer categoryId, String keyword) {
        try (Connection connection = DBUtil.getConnection()) {

            StringBuilder sql = new StringBuilder(
                    "SELECT COUNT(DISTINCT c.class_id) " +
                            "FROM class c " +
                            "LEFT JOIN class_enrollment cu ON cu.class_id = c.class_id " +
                            "LEFT JOIN class_category cc ON c.class_id = cc.class_id " +
                            "LEFT JOIN setting cat ON cc.category_id = cat.setting_id AND cat.type_id = 5 " +
                            "LEFT JOIN user u ON c.instructor_id = u.user_id " +
                            "WHERE cu.user_id = ? AND c.status = 1 "
            );

            if (keyword != null && !keyword.trim().isEmpty()) {
                sql.append(" AND (c.class_name LIKE ? OR u.fullname LIKE ?) ");
            }

            if (categoryId != null) {
                sql.append(" AND cat.setting_id = ? ");
            }

            PreparedStatement ps = connection.prepareStatement(sql.toString());
            int idx = 1;

            ps.setInt(idx++, userId);

            if (keyword != null && !keyword.trim().isEmpty()) {
                ps.setString(idx++, "%" + keyword + "%");
                ps.setString(idx++, "%" + keyword + "%");
            }

            if (categoryId != null) {
                ps.setInt(idx++, categoryId);
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    public int getTotalClasses() {
        String sql = "SELECT COUNT(class_id) FROM class";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    public int countActiveClasses(String keyword, Integer categoryId) {
        try (Connection connection = DBUtil.getConnection()) {

            StringBuilder sql = new StringBuilder(
                    "SELECT COUNT(DISTINCT c.class_id) " +
                            "FROM class c " +
                            "LEFT JOIN class_enrollment cu ON cu.class_id = c.class_id " +
                            "LEFT JOIN class_category cc ON c.class_id = cc.class_id " +
                            "LEFT JOIN setting cat ON cc.category_id = cat.setting_id AND cat.type_id = 5 " +
                            "LEFT JOIN user u ON c.instructor_id = u.user_id " +
                            "WHERE c.status = 1 "
            );

            if (keyword != null && !keyword.trim().isEmpty()) {
                sql.append(" AND (c.class_name LIKE ? OR u.fullname LIKE ?) ");
            }

            if (categoryId != null) {
                sql.append(" AND cat.setting_id= ? ");
            }

            PreparedStatement ps = connection.prepareStatement(sql.toString());
            int idx = 1;

            if (keyword != null && !keyword.trim().isEmpty()) {
                ps.setString(idx++, "%" + keyword + "%");
                ps.setString(idx++, "%" + keyword + "%");
            }

            if (categoryId != null) {
                ps.setInt(idx++, categoryId);
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
// ==================== INSTRUCTOR SPECIFIC METHODS (Added) ====================

    public List<Class> getClassesByInstructor(int instructorId, Integer categoryId, String keyword, int offset, int limit) {
        List<Class> classes = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT c.*, u.fullname AS instructor_name " +
                        "FROM class c " +
                        "JOIN user u ON c.instructor_id = u.user_id " +
                        "LEFT JOIN class_category cc ON c.class_id = cc.class_id " +
                        "LEFT JOIN setting cat ON cc.category_id = cat.setting_id AND cat.type_id = 5 " +
                        "WHERE c.instructor_id = ? ");

        if (keyword != null && !keyword.isEmpty()) sql.append(" AND c.class_name LIKE ? ");
        if (categoryId != null) sql.append(" AND cat.setting_id = ? ");

        sql.append(" GROUP BY c.class_id ORDER BY c.class_id ASC LIMIT ? OFFSET ?");

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            ps.setInt(idx++, instructorId);
            if (keyword != null && !keyword.isEmpty()) ps.setString(idx++, "%" + keyword + "%");
            if (categoryId != null) ps.setInt(idx++, categoryId);
            ps.setInt(idx++, limit);
            ps.setInt(idx++, offset);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Class c = new Class();
                c.setId(rs.getInt("class_id"));
                c.setName(rs.getString("class_name"));
                c.setThumbnailUrl(rs.getString("thumbnail_url"));
                c.setStartDate(rs.getDate("start_date"));
                c.setEndDate(rs.getDate("end_date"));
                c.setStatus(rs.getBoolean("status"));
                c.setListedPrice(rs.getBigDecimal("listed_price"));
                c.setSalePrice(rs.getBigDecimal("sale_price"));
                c.setDescription(rs.getString("description"));

                User u = new User();
                u.setFullname(rs.getString("instructor_name"));
                c.setInstructor(u);

                classes.add(c);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return classes;
    }

    public int countClassesByInstructor(int instructorId, Integer categoryId, String keyword) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(DISTINCT c.class_id) FROM class c " +
                        "LEFT JOIN class_category cc ON c.class_id = cc.class_id " +
                        "LEFT JOIN setting cat ON cc.category_id = cat.setting_id " +
                        "WHERE c.instructor_id = ? ");

        if (keyword != null && !keyword.isEmpty()) sql.append(" AND c.class_name LIKE ? ");
        if (categoryId != null) sql.append(" AND cat.setting_name = ? ");

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            ps.setInt(idx++, instructorId);
            if (keyword != null && !keyword.isEmpty()) ps.setString(idx++, "%" + keyword + "%");
            if (categoryId != null) ps.setInt(idx++, categoryId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    public List<Class> getClassContentByInstructor(int instructorId, Integer categoryId, String keyword, Boolean status) {
        List<Class> classes = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT c.*, u.fullname AS instructor_name, " +
                        "GROUP_CONCAT(DISTINCT cat.setting_name SEPARATOR ', ') AS category_names " +
                        "FROM class c " +
                        "JOIN user u ON c.instructor_id = u.user_id " +
                        "LEFT JOIN class_category cc ON c.class_id = cc.class_id " +
                        "LEFT JOIN setting cat ON cc.category_id = cat.setting_id AND cat.type_id = 5 " +
                        "WHERE c.instructor_id = ? ");

        if (keyword != null && !keyword.isEmpty()) sql.append(" AND c.class_name LIKE ? ");
        if (categoryId != null) {
            sql.append(
                    " AND EXISTS (" +
                            "   SELECT 1 FROM class_category cc2 " +
                            "   JOIN setting s2 ON cc2.category_id = s2.setting_id " +
                            "   WHERE cc2.class_id = c.class_id " +
                            "     AND s2.setting_id = ? " +
                            "     AND s2.type_id = 5 " +
                            " ) "
            );
        }
        if (status != null) sql.append(" AND c.status = ? ");

        sql.append(" GROUP BY c.class_id ORDER BY c.class_id ASC ");

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            ps.setInt(idx++, instructorId);
            if (keyword != null && !keyword.isEmpty()) ps.setString(idx++, "%" + keyword + "%");
            if (categoryId != null) ps.setInt(idx++, categoryId);
            if (status != null) ps.setBoolean(idx++, status);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Class c = new Class();
                c.setId(rs.getInt("class_id"));
                c.setName(rs.getString("class_name"));
                c.setThumbnailUrl(rs.getString("thumbnail_url"));
                String catStr = rs.getString("category_names");
                if (catStr != null) {
                    c.setCategories(catStr.split(",\\s*"));
                } else {
                    c.setCategories(new String[0]);
                }
                c.setStartDate(rs.getDate("start_date"));
                c.setEndDate(rs.getDate("end_date"));
                c.setStatus(rs.getBoolean("status"));
                c.setNumberOfStudents(rs.getInt("number_of_students"));
                c.setDescription(rs.getString("description"));

                User u = new User();
                u.setFullname(rs.getString("instructor_name"));
                c.setInstructor(u);

                classes.add(c);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return classes;
    }

    public boolean deleteClassByInstructor(int classId, int instructorId) {
        String sql = "DELETE FROM class WHERE class_id = ? AND instructor_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, classId);
            ps.setInt(2, instructorId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Class> getAllClasses(Integer categoryId, Integer instructorId, Boolean status, String keyword, String sortColumn, String sortOrder) {
        List<Class> classes = new ArrayList<>();
        try (Connection connection = DBUtil.getConnection()) {
            StringBuilder sql = new StringBuilder(
                    "SELECT c.*, u.fullname AS instructor, " +
                    "GROUP_CONCAT(DISTINCT cat.setting_name SEPARATOR ', ') AS category_names " +
                    "FROM class c " +
                    "LEFT JOIN user u ON c.instructor_id = u.user_id " +
                    "LEFT JOIN class_category cc ON c.class_id = cc.class_id " +
                    "LEFT JOIN setting cat ON cc.category_id = cat.setting_id " +
                    "WHERE 1=1 ");
            List<Object> params = new ArrayList<>();

            if (categoryId != null) {
                sql.append(
                        " AND EXISTS (" +
                                "   SELECT 1 FROM class_category cc2 " +
                                "   JOIN setting s2 ON cc2.category_id = s2.setting_id " +
                                "   WHERE cc2.class_id = c.class_id " +
                                "     AND s2.setting_id = ? " +
                                "     AND s2.type_id = 5 " +
                                " ) "
                );
                params.add(categoryId);
            }

            if (instructorId != null) {
                sql.append(" AND u.user_id = ?");
                params.add(instructorId);
            }

            if (status != null) {
                sql.append(" AND c.status = ?");
                params.add(status);
            }

            if (keyword != null) {
                sql.append(" AND c.class_name LIKE ?");
                params.add("%" + keyword + "%");
            }

            sql.append(" GROUP BY c.class_id, c.class_name ");

            String orderBy = "c.class_id";

            if (sortColumn != null) {
                switch (sortColumn) {
                    case "id":
                        orderBy = "c.class_id";
                        break;
                    case "name":
                        orderBy = "c.class_name";
                        break;
                    case "start_date":
                        orderBy = "c.start_date";
                        break;
                    case "end_date":
                        orderBy = "c.end_date";
                        break;
                }
            }

            String direction = "ASC";
            if ("desc".equalsIgnoreCase(sortOrder)) {
                direction = "DESC";
            }

            sql.append(" ORDER BY ").append(orderBy).append(" ").append(direction);

            PreparedStatement statement = connection.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                statement.setObject(i + 1, params.get(i));
            }
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Class c = new Class();

                c.setId(resultSet.getInt("class_id"));
                c.setName(resultSet.getString("class_name"));
                c.setThumbnailUrl(resultSet.getString("thumbnail_url"));
                c.setNumberOfStudents(resultSet.getInt("number_of_students"));
                c.setListedPrice(resultSet.getBigDecimal("listed_price"));
                c.setSalePrice(resultSet.getBigDecimal("sale_price"));
                c.setStatus(resultSet.getBoolean("status"));
                c.setDescription(resultSet.getString("description"));
                c.setStartDate(resultSet.getDate("start_date"));
                c.setEndDate(resultSet.getDate("end_date"));

                String catStr = resultSet.getString("category_names");
                if (catStr != null) {
                    c.setCategories(catStr.split(",\\s*"));
                } else {
                    c.setCategories(new String[0]);
                }

                User inst = new User();
                inst.setId(resultSet.getInt("instructor_id"));
                inst.setFullname(resultSet.getString("instructor"));
                c.setInstructor(inst);

                classes.add(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return classes;
    }

    public void updateStatus(int classId, boolean newStatus) {
        try (Connection connection = DBUtil.getConnection()) {
            String sql = "UPDATE class SET status = ? WHERE class_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setBoolean(1, newStatus);
            statement.setInt(2, classId);
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertClass(Class clazz, String[] categoryIds) throws SQLException {
        Connection con = DBUtil.getConnection();

        String sql = "INSERT INTO class" +
                "        (class_name, thumbnail_url, listed_price, sale_price," +
                "         description, start_date, end_date, instructor_id, status)" +
                "        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        ps.setString(1, clazz.getName());
        ps.setString(2, clazz.getThumbnailUrl());
        ps.setBigDecimal(3, clazz.getListedPrice());

        if (clazz.getSalePrice() != null) {
            ps.setBigDecimal(4, clazz.getSalePrice());
        } else {
            ps.setNull(4, Types.DECIMAL);
        }

        ps.setString(5, clazz.getDescription());
        ps.setObject(6, clazz.getStartDate());
        ps.setObject(7, clazz.getEndDate());
        ps.setInt(8, clazz.getInstructor().getId());
        ps.setBoolean(9, clazz.isStatus());

        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            int classId = rs.getInt(1);

            if (categoryIds != null) {
                String sqlCat = "INSERT INTO class_category (class_id, category_id) VALUES (?, ?)";
                PreparedStatement psCat = con.prepareStatement(sqlCat);

                for (String catId : categoryIds) {
                    psCat.setInt(1, classId);
                    psCat.setInt(2, Integer.parseInt(catId));
                    psCat.addBatch();
                }
                psCat.executeBatch();
            }
        }
    }

    public List<Setting> getCategoriesByClassId(int classId) {
        List<Setting> categories = new ArrayList<>();
        try (Connection connection = DBUtil.getConnection()) {
            String sql = "SELECT cat.setting_id, cat.setting_name " +
                    "FROM setting cat " +
                    "LEFT JOIN class_category cc on cat.setting_id = cc.category_id " +
                    "LEFT JOIN class c on cc.class_id = c.class_id " +
                    "WHERE cat.type_id = 5 AND c.class_id = ? ";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, classId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Setting setting = new Setting();
                setting.setId(resultSet.getInt("setting_id"));
                setting.setName(resultSet.getString("setting_name"));

                categories.add(setting);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return categories;
    }

    public void updateClass(Class c, String[] categoryIds) {

        String updateClassSql = "UPDATE class" +
                "        SET class_name = ?, thumbnail_url = ?, description = ?," +
                "            listed_price = ?, sale_price = ?, start_date = ?, end_date = ?," +
                "            status = ?, instructor_id = ?" +
                "        WHERE class_id = ?";

        String deleteCategorySql =
                "DELETE FROM class_category WHERE class_id = ?";

        String insertCategorySql =
                "INSERT INTO class_category (class_id, category_id) VALUES (?, ?)";

        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);

            // 1️⃣ Update class
            try (PreparedStatement ps = conn.prepareStatement(updateClassSql)) {
                ps.setString(1, c.getName());
                ps.setString(2, c.getThumbnailUrl());
                ps.setString(3, c.getDescription());
                ps.setBigDecimal(4, c.getListedPrice());
                ps.setBigDecimal(5, c.getSalePrice());
                ps.setObject(6, c.getStartDate());
                ps.setObject(7, c.getEndDate());
                ps.setBoolean(8, c.isStatus());
                ps.setInt(9, c.getInstructor().getId());
                ps.setInt(10, c.getId());
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(deleteCategorySql)) {
                ps.setInt(1, c.getId());
                ps.executeUpdate();
            }

            if (categoryIds != null) {
                try (PreparedStatement ps = conn.prepareStatement(insertCategorySql)) {
                    for (String catId : categoryIds) {
                        ps.setInt(1, c.getId());
                        ps.setInt(2, Integer.parseInt(catId));
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }

            conn.commit();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to update class!");
        }
    }

    public boolean doesClassNameExist(String className) {
        try (Connection connection = DBUtil.getConnection()) {
            String sql = "SELECT * FROM class WHERE class_name = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, className);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean existsByNameAndNotId(String className, int classId) {
        try (Connection con = DBUtil.getConnection()) {
            String sql = "SELECT 1 FROM class WHERE LOWER(class_name) = LOWER(?) AND class_id <> ?";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setString(1, className);
            statement.setInt(2, classId);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean isUserEnrolled(int userId, int classId) {
        String sql = "SELECT COUNT(*) FROM class_enrollment WHERE user_id = ? AND class_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, classId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking class enrollment: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean enrollUserInClass(int userId, int classId, double pricePaid, String paymentMethod) {
        String sql = "INSERT INTO class_enrollment (user_id, class_id, price_paid, payment_method, enrolled_at, status) " +
                "VALUES (?, ?, ?, ?, NOW(), 1)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, classId);
            stmt.setDouble(3, pricePaid);
            stmt.setString(4, paymentMethod);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error enrolling user in class: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
