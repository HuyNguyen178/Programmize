package dao;

import model.Class;
import model.User;
import utils.DBUtil;
import model.Course;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static utils.DBUtil.getConnection;

public class CourseDAO {

    // ==================== GET ALL COURSES WITH FILTERS ====================
    // Used by: CourseListServlet
    public List<Course> getAllCourses(String category, String instructor,
                                      String status, String searchKeyword,
                                      String sortColumn, String sortOrder) {
        List<Course> courses = new ArrayList<>();

        // Use JOIN to get category name from setting table and instructor name from user table
        StringBuilder sql = new StringBuilder(
                "SELECT DISTINCT c.course_id, c.course_name, c.listed_price, c.sale_price, " +
                        "c.thumbnail_url, c.instructor_id, c.duration, c.description, c.status, " +
                        "u.fullname AS instructor_name, " +
                        "GROUP_CONCAT(DISTINCT s.setting_name SEPARATOR ', ') AS category_names " +
                        "FROM course c " +
                        "LEFT JOIN user u ON c.instructor_id = u.user_id " +
                        "LEFT JOIN course_category cc ON c.course_id = cc.course_id " +
                        "LEFT JOIN setting s ON cc.category_id = s.setting_id AND s.type_id = 5 " +
                        "WHERE 1=1"
        );

        // Filter by category (setting_id)
        if (category != null && !category.isEmpty() && !category.equals("All Categories")) {
            sql.append(" AND EXISTS (SELECT 1 FROM course_category cc2 WHERE cc2.course_id = c.course_id AND cc2.category_id = ?)");
        }

        // Filter by instructor (user_id)
        if (instructor != null && !instructor.isEmpty() && !instructor.equals("All Instructors")) {
            sql.append(" AND c.instructor_id = ?");
        }

        // Filter by status - SỬA: so sánh với boolean (0 hoặc 1)
        if (status != null && !status.isEmpty() && !status.equals("All Statuses") && !status.equals("")) {
            sql.append(" AND c.status = ?");
        }

        // Search keyword
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            sql.append(" AND (c.course_name LIKE ? OR c.description LIKE ? OR u.fullname LIKE ?)");
        }

        // Group by to handle multiple categories
        sql.append(" GROUP BY c.course_id, c.course_name, c.listed_price, c.sale_price, " +
                "c.thumbnail_url, c.instructor_id, c.duration, c.description, c.status, u.fullname");

        // Add sorting
        if (sortColumn != null && !sortColumn.isEmpty()) {
            String actualColumn = sortColumn;
            if (sortColumn.equals("sales_price")) {
                actualColumn = "c.sale_price";
            } else if (sortColumn.equals("id")) {
                actualColumn = "c.course_id";
            } else if (sortColumn.equals("course_name")) {
                actualColumn = "c.course_name";
            } else if (sortColumn.equals("instructor")) {
                actualColumn = "u.fullname";
            }

            sql.append(" ORDER BY ").append(actualColumn);
            if (sortOrder != null && sortOrder.equalsIgnoreCase("desc")) {
                sql.append(" DESC");
            } else {
                sql.append(" ASC");
            }
        } else {
            sql.append(" ORDER BY c.course_id ASC");
        }

        System.out.println("Executing SQL: " + sql.toString());

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            if (conn == null) {
                System.err.println("Failed to get database connection!");
                return courses;
            }

            stmt = conn.prepareStatement(sql.toString());

            int paramIndex = 1;

            // Set category parameter
            if (category != null && !category.isEmpty() && !category.equals("All Categories")) {
                stmt.setInt(paramIndex++, Integer.parseInt(category));
                System.out.println("Setting category parameter: " + category);
            }

            // Set instructor parameter
            if (instructor != null && !instructor.isEmpty() && !instructor.equals("All Instructors")) {
                stmt.setInt(paramIndex++, Integer.parseInt(instructor));
                System.out.println("Setting instructor parameter: " + instructor);
            }

            // Set status parameter - SỬA: dùng setInt cho boolean trong MySQL
            if (status != null && !status.isEmpty() && !status.equals("All Statuses") && !status.equals("")) {
                stmt.setInt(paramIndex++, Integer.parseInt(status));
                System.out.println("Setting status parameter: " + status);
            }

            // Set search keyword parameters
            if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
                String searchPattern = "%" + searchKeyword + "%";
                stmt.setString(paramIndex++, searchPattern);
                stmt.setString(paramIndex++, searchPattern);
                stmt.setString(paramIndex++, searchPattern);
                System.out.println("Setting search parameter: " + searchPattern);
            }

            rs = stmt.executeQuery();

            while (rs.next()) {
                Course course = new Course();
                course.setCourseId(rs.getInt("course_id"));
                course.setThumbnailUrl(rs.getString("thumbnail_url"));
                course.setCourseName(rs.getString("course_name"));
                course.setCourseInstructor(rs.getString("instructor_name"));
                course.setListedPrice(rs.getBigDecimal("listed_price"));
                course.setSalePrice(rs.getBigDecimal("sale_price"));
                course.setDescription(rs.getString("description"));
                course.setStatus(rs.getBoolean("status"));
                course.setDuration(rs.getInt("duration"));
                course.setInstructorId(rs.getInt("instructor_id"));

                // ===== SỬA: THÊM ĐOẠN NÀY ĐỂ SET courseCategories =====
                String categoryNames = rs.getString("category_names");
                if (categoryNames != null && !categoryNames.isEmpty()) {
                    course.setCourseCategories(categoryNames.split(", "));
                } else {
                    course.setCourseCategories(new String[0]);
                }
                // ========================================================

                courses.add(course);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return courses;
    }

    // ==================== CATEGORY METHODS ====================

    // Get all categories from setting table (all active categories)
    // Returns List<String[]> where each String[] = {setting_id, setting_name}
    public List<String[]> getAllCategoriesFromSettings() {
        List<String[]> categories = new ArrayList<>();
        String sql = "SELECT setting_id, setting_name FROM setting WHERE status = 1 AND type_id = 5 ORDER BY setting_name";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String[] category = new String[2];
                category[0] = String.valueOf(rs.getInt("setting_id"));
                category[1] = rs.getString("setting_name");
                categories.add(category);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    // Get category names only (for PublicCourseServlet compatibility)
    // Returns List<String> of category names
    public List<String> getAllCategoryNames() {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT DISTINCT s.setting_name " +
                "FROM setting s " +
                "INNER JOIN course_category cc ON s.setting_id = cc.category_id " +
                "WHERE s.status = 1 AND s.type_id = 5 " +
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

    // Get categories for a specific course
    public List<String[]> getCategoriesForCourse(int courseId) {
        List<String[]> categories = new ArrayList<>();
        String sql = "SELECT s.setting_id, s.setting_name " +
                "FROM setting s " +
                "INNER JOIN course_category cc ON s.setting_id = cc.category_id " +
                "WHERE cc.course_id = ? AND s.type_id = 5";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String[] category = new String[2];
                category[0] = String.valueOf(rs.getInt("setting_id"));
                category[1] = rs.getString("setting_name");
                categories.add(category);
            }
        } catch (SQLException e) {
            System.err.println("Error getting categories for course: " + e.getMessage());
            e.printStackTrace();
        }
        return categories;
    }

    // Add category to course
    public boolean addCategoryToCourse(int courseId, int categoryId) {
        String sql = "INSERT INTO course_category (course_id, category_id) VALUES (?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            stmt.setInt(2, categoryId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding category to course: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Remove all categories from course
    public boolean removeCategoriesFromCourse(int courseId) {
        String sql = "DELETE FROM course_category WHERE course_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error removing categories from course: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateCourseStatus(int courseId, boolean newStatus) {
        String sql = "UPDATE course SET status = ? WHERE course_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, newStatus);
            stmt.setInt(2, courseId);

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // ==================== INSTRUCTOR METHODS ====================

    // Get all instructors (users who are instructors of at least one course)
    // Returns List<String[]> where each String[] = {user_id, fullname}
    public List<String[]> getAllInstructors() {
        List<String[]> instructors = new ArrayList<>();
        String sql = "SELECT DISTINCT u.user_id, u.fullname " +
                "FROM user u " +
                "INNER JOIN course c ON u.user_id = c.instructor_id " +
                "WHERE u.status = 1 " +
                "ORDER BY u.fullname";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String[] instructor = new String[2];
                instructor[0] = String.valueOf(rs.getInt("user_id"));
                instructor[1] = rs.getString("fullname");
                instructors.add(instructor);
            }
            System.out.println("Retrieved " + instructors.size() + " instructors");
        } catch (SQLException e) {
            System.err.println("Error getting instructors: " + e.getMessage());
            e.printStackTrace();
        }
        return instructors;
    }

    // Get all users who can be instructors (all active users)
    // Returns List<String[]> where each String[] = {user_id, fullname}
    public List<String[]> getAllUsersAsInstructors() {
        List<String[]> instructors = new ArrayList<>();
        String sql = "SELECT user_id, fullname FROM user WHERE status = 1 ORDER BY fullname";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String[] instructor = new String[2];
                instructor[0] = String.valueOf(rs.getInt("user_id"));
                instructor[1] = rs.getString("fullname");
                instructors.add(instructor);
            }
        } catch (SQLException e) {
            System.err.println("Error getting users as instructors: " + e.getMessage());
            e.printStackTrace();
        }
        return instructors;
    }

    // ==================== COURSE CRUD OPERATIONS ====================

    // Get course by ID (with category and instructor from related tables)
    // Used by: EditCourseServlet, PublicCourseDetailsServlet
    public Course getCourseById(int courseId) {
        String sql = "SELECT c.*, u.fullname AS instructor_name, " +
                "GROUP_CONCAT(DISTINCT s.setting_name SEPARATOR ', ') AS category_names " +
                "FROM course c " +
                "LEFT JOIN user u ON c.instructor_id = u.user_id " +
                "LEFT JOIN course_category cc ON c.course_id = cc.course_id " +
                "LEFT JOIN setting s ON cc.category_id = s.setting_id AND s.type_id = 5 " +
                "WHERE c.course_id = ? " +
                "GROUP BY c.course_id";
        Course course = null;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, courseId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                course = new Course();
                course.setCourseId(rs.getInt("course_id"));
                course.setThumbnailUrl(rs.getString("thumbnail_url"));
                course.setCourseName(rs.getString("course_name"));
                course.setCourseInstructor(rs.getString("instructor_name"));
                course.setListedPrice(rs.getBigDecimal("listed_price"));
                course.setSalePrice(rs.getBigDecimal("sale_price"));

                String cats = rs.getString("category_names");
                if (cats != null && !cats.isEmpty()) {
                    course.setCourseCategories(cats.split(", "));
                } else {
                    course.setCourseCategories(new String[0]);
                }

                course.setDescription(rs.getString("description"));
                course.setStatus(rs.getBoolean("status"));
                course.setDuration(rs.getInt("duration"));
                course.setInstructorId(rs.getInt("instructor_id"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return course;
    }

    // Delete course by ID
    public boolean deleteCourse(int courseId) {
        // First delete from course_category
        String deleteCategoriesSql = "DELETE FROM course_category WHERE course_id = ?";
        String deleteCourseSql = "DELETE FROM course WHERE course_id = ?";

        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            // Delete categories first
            try (PreparedStatement stmt = conn.prepareStatement(deleteCategoriesSql)) {
                stmt.setInt(1, courseId);
                stmt.executeUpdate();
            }

            // Then delete course
            try (PreparedStatement stmt = conn.prepareStatement(deleteCourseSql)) {
                stmt.setInt(1, courseId);
                int rowsAffected = stmt.executeUpdate();

                conn.commit();
                System.out.println("Delete course ID " + courseId + ": " + rowsAffected + " rows affected");
                return rowsAffected > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error deleting course: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Update course with categories (combined operation)
    public boolean updateCourseWithCategories(Course course, int[] categoryIds) {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            // Update course info - SỬA: dùng boolean cho status
            String updateSql = "UPDATE course SET course_name = ?, listed_price = ?, sale_price = ?, " +
                    "thumbnail_url = ?, description = ?, status = ?, duration = ?, instructor_id = ? " +
                    "WHERE course_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                stmt.setString(1, course.getCourseName());
                stmt.setBigDecimal(2, course.getListedPrice());
                stmt.setBigDecimal(3, course.getSalePrice());
                stmt.setString(4, course.getThumbnailUrl());
                stmt.setString(5, course.getDescription());
                stmt.setBoolean(6, course.getStatus()); // SỬA: dùng boolean
                stmt.setInt(7, course.getDuration() != null ? course.getDuration() : 0);
                stmt.setInt(8, course.getInstructorId() != null ? course.getInstructorId() : 0);
                stmt.setInt(9, course.getCourseId());
                stmt.executeUpdate();
            }

            // Delete old categories
            String deleteSql = "DELETE FROM course_category WHERE course_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteSql)) {
                stmt.setInt(1, course.getCourseId());
                stmt.executeUpdate();
            }

            // Insert new categories
            if (categoryIds != null && categoryIds.length > 0) {
                String insertSql = "INSERT INTO course_category (course_id, category_id) VALUES (?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                    for (int categoryId : categoryIds) {
                        stmt.setInt(1, course.getCourseId());
                        stmt.setInt(2, categoryId);
                        stmt.addBatch();
                    }
                    stmt.executeBatch();
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("Error updating course: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Add new course with categories
    public int addCourseWithCategories(Course course, int[] categoryIds) {
        Connection conn = null;
        int courseId = -1;

        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            // Insert course - SỬA: dùng boolean cho status
            String insertSql = "INSERT INTO course (course_name, listed_price, sale_price, thumbnail_url, " +
                    "description, status, duration, instructor_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, course.getCourseName());
                stmt.setBigDecimal(2, course.getListedPrice());
                stmt.setBigDecimal(3, course.getSalePrice());
                stmt.setString(4, course.getThumbnailUrl());
                stmt.setString(5, course.getDescription());
                stmt.setBoolean(6, course.getStatus()); // SỬA: dùng boolean
                stmt.setInt(7, course.getDuration() != null ? course.getDuration() : 0);
                stmt.setInt(8, course.getInstructorId() != null ? course.getInstructorId() : 0);
                stmt.executeUpdate();

                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    courseId = rs.getInt(1);
                }
            }

            // Insert categories
            if (courseId > 0 && categoryIds != null && categoryIds.length > 0) {
                String insertCatSql = "INSERT INTO course_category (course_id, category_id) VALUES (?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(insertCatSql)) {
                    for (int categoryId : categoryIds) {
                        stmt.setInt(1, courseId);
                        stmt.setInt(2, categoryId);
                        stmt.addBatch();
                    }
                    stmt.executeBatch();
                }
            }

            conn.commit();
            System.out.println("Added new course with ID: " + courseId);

        } catch (SQLException e) {
            System.err.println("Error adding course: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return -1;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return courseId;
    }

    public int getTotalCourses() {
        String sql = "SELECT COUNT(course_id) FROM course";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Object[]> getTopSellingCourses(int limit) {
        List<Object[]> topStatsList = new ArrayList<>();

        String sql =
                "SELECT " +
                        "   c.course_id, c.course_name, c.listed_price, c.sale_price, " +
                        "   COUNT(cu.user_id) AS enrollments_count, " +
                        "   (COUNT(cu.user_id) * c.sale_price) AS total_revenue " +
                        "FROM " +
                        "   course c " +
                        "LEFT JOIN " +
                        "   course_user cu ON c.course_id = cu.course_id " +
                        "GROUP BY " +
                        "   c.course_id, c.course_name, c.listed_price, c.sale_price " +
                        "ORDER BY " +
                        "   total_revenue DESC " +
                        "LIMIT ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Course course = new Course();
                    course.setId(rs.getInt("course_id"));
                    course.setCourseName(rs.getString("course_name"));

                    Integer enrollments = rs.getInt("enrollments_count");
                    BigDecimal revenue = rs.getBigDecimal("total_revenue");

                    Object[] statArray = new Object[3];
                    statArray[0] = course;
                    statArray[1] = enrollments;
                    statArray[2] = revenue;
                    topStatsList.add(statArray);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return topStatsList;
    }

    public List<Course> getHighlightedCourses() {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT c.*, u.fullname AS instructor_name, " +
                "GROUP_CONCAT(DISTINCT s.setting_name SEPARATOR ', ') AS category_names " +
                "FROM course c " +
                "JOIN user u ON c.instructor_id = u.user_id " +
                "LEFT JOIN course_category cc ON c.course_id = cc.course_id " +
                "LEFT JOIN setting s ON cc.category_id = s.setting_id AND s.type_id = 5 " +
                "WHERE c.status = 1 " +
                "GROUP BY c.course_id " +
                "ORDER BY c.course_id DESC LIMIT 8";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Course c = new Course();
                c.setCourseId(rs.getInt("course_id"));
                c.setCourseName(rs.getString("course_name"));
                c.setListedPrice(rs.getBigDecimal("listed_price"));
                c.setSalePrice(rs.getBigDecimal("sale_price"));
                c.setThumbnailUrl(rs.getString("thumbnail_url"));
                c.setDescription(rs.getString("description"));
                c.setDuration(rs.getInt("duration"));
                c.setInstructorId(rs.getInt("instructor_id"));
                c.setStatus(rs.getBoolean("status"));
                c.setCourseInstructor(rs.getString("instructor_name"));

                // SỬA: Set courseCategories thay vì courseCategory
                String cats = rs.getString("category_names");
                if (cats != null && !cats.isEmpty()) {
                    c.setCourseCategories(cats.split(", "));
                } else {
                    c.setCourseCategories(new String[0]);
                }

                list.add(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public int countCoursesByUserId(int userId, String category, String keyword) {
        try (Connection connection = DBUtil.getConnection()) {

            StringBuilder sql = new StringBuilder(
                    "SELECT COUNT(DISTINCT c.course_id) " +
                            "FROM course c " +
                            "LEFT JOIN course_user cu ON cu.course_id = c.course_id " +
                            "LEFT JOIN course_category cc ON c.course_id = cc.course_id " +
                            "LEFT JOIN setting cat ON cc.category_id = cat.setting_id AND cat.type_id = 5 " +
                            "LEFT JOIN user u ON c.instructor_id = u.user_id " +
                            "WHERE cu.user_id = ? AND c.status = 1 "
            );

            if (keyword != null && !keyword.trim().isEmpty()) {
                sql.append(" AND (c.course_name LIKE ? OR u.fullname LIKE ?) ");
            }

            if (category != null && !category.trim().isEmpty()) {
                sql.append(" AND cat.setting_name = ? ");
            }

            PreparedStatement ps = connection.prepareStatement(sql.toString());
            int idx = 1;

            ps.setInt(idx++, userId);

            if (keyword != null && !keyword.trim().isEmpty()) {
                ps.setString(idx++, "%" + keyword + "%");
                ps.setString(idx++, "%" + keyword + "%");
            }

            if (category != null && !category.trim().isEmpty()) {
                ps.setString(idx++, category);
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int countPublicCourses(String category, String keyword) {
        try (Connection connection = DBUtil.getConnection()) {

            StringBuilder sql = new StringBuilder(
                    "SELECT COUNT(DISTINCT c.course_id) " +
                            "FROM course c " +
                            "LEFT JOIN course_user cu ON cu.course_id = c.course_id " +
                            "LEFT JOIN course_category cc ON c.course_id = cc.course_id " +
                            "LEFT JOIN setting cat ON cc.category_id = cat.setting_id AND cat.type_id = 5 " +
                            "LEFT JOIN user u ON c.instructor_id = u.user_id " +
                            "WHERE c.status = 1 "
            );

            if (keyword != null && !keyword.trim().isEmpty()) {
                sql.append(" AND (c.course_name LIKE ? OR u.fullname LIKE ?) ");
            }

            if (category != null && !category.trim().isEmpty()) {
                sql.append(" AND cat.setting_name = ? ");
            }

            PreparedStatement ps = connection.prepareStatement(sql.toString());
            int idx = 1;

            if (keyword != null && !keyword.trim().isEmpty()) {
                ps.setString(idx++, "%" + keyword + "%");
                ps.setString(idx++, "%" + keyword + "%");
            }

            if (category != null && !category.trim().isEmpty()) {
                ps.setString(idx++, category);
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Add new course
    public int addCourse(Course course) {
        String sql = "INSERT INTO course (course_name, listed_price, sale_price, thumbnail_url, " +
                "description, status, duration, instructor_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, course.getCourseName());
            stmt.setBigDecimal(2, course.getListedPrice());
            stmt.setBigDecimal(3, course.getSalePrice());
            stmt.setString(4, course.getThumbnailUrl());
            stmt.setString(5, course.getDescription());
            stmt.setBoolean(6, course.getStatus());
            stmt.setInt(7, course.getDuration() != null ? course.getDuration() : 0);
            stmt.setInt(8, course.getInstructorId() != null ? course.getInstructorId() : 0);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int newId = generatedKeys.getInt(1);
                    System.out.println("Added new course with ID: " + newId);
                    return newId;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding course: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    // ==================== PUBLIC COURSE METHODS ====================
    // Used by: PublicCourseServlet, PublicCourseDetailsServlet
    // Get public courses (active status) with filters
    // categoryIds can be setting_id values or category names
    public List<Course> getPublicCourses(String category, String keyword, String priceSort, int offset, int limit) {
        List<Course> courses = new ArrayList<>();
        try (Connection connection = DBUtil.getConnection()) {
            StringBuilder sql = new StringBuilder("SELECT" +
                    "    c.course_id," +
                    "    c.course_name," +
                    "    c.thumbnail_url," +
                    "    c.listed_price," +
                    "    c.sale_price," +
                    "    c.status," +
                    "    c.description," +
                    "    GROUP_CONCAT(cat.setting_name SEPARATOR ', ') AS categories," +
                    "    u.user_id as instructor_id," +
                    "    u.fullname AS instructor_name" +
                    " FROM course c" +
                    " LEFT JOIN course_user cu ON cu.course_id = c.course_id" +
                    " LEFT JOIN course_category cc ON c.course_id = cc.course_id" +
                    " LEFT JOIN setting cat ON cc.category_id = cat.setting_id AND cat.type_id = 5" +
                    " LEFT JOIN user u ON c.instructor_id = u.user_id" +
                    " LEFT JOIN setting s ON u.role_id = s.setting_id AND s.setting_name = 'Instructor'" +
                    " WHERE c.status = 1");
            if (keyword != null && !keyword.trim().isEmpty()) {
                sql.append(" AND (c.course_name LIKE ? OR u.fullname LIKE ?) ");
            }
            if (category != null && !category.trim().isEmpty()) {
                sql.append(" AND cat.setting_name = ?");
            }
            sql.append(" GROUP BY " +
                    "    c.course_id, c.course_name, c.thumbnail_url, c.listed_price, " +
                    "    c.sale_price, c.status, c.description," +
                    "    u.user_id, u.fullname");
            if ("low".equalsIgnoreCase(priceSort)) {
                sql.append(" ORDER BY COALESCE(c.sale_price, c.listed_price) ASC");
            } else if ("high".equalsIgnoreCase(priceSort)) {
                sql.append(" ORDER BY COALESCE(c.sale_price, c.listed_price) DESC");
            } else {
                sql.append(" ORDER BY c.course_id ASC");
            }
            sql.append(" LIMIT ? OFFSET ?");
            PreparedStatement statement = connection.prepareStatement(sql.toString());
            int index = 1;
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
                Course c = new Course();
                c.setId(resultSet.getInt("course_id"));
                c.setCourseName(resultSet.getString("course_name"));
                c.setThumbnailUrl(resultSet.getString("thumbnail_url"));
                c.setStatus(resultSet.getBoolean("status"));
                c.setListedPrice(resultSet.getBigDecimal("listed_price"));
                c.setSalePrice(resultSet.getBigDecimal("sale_price"));
                c.setDescription(resultSet.getString("description"));
                c.setInstructorId(resultSet.getInt("instructor_id"));
                c.setCourseInstructor(resultSet.getString("instructor_name"));

                String cats = resultSet.getString("categories");
                if (cats != null && !cats.isEmpty()) {
                    c.setCourseCategories(cats.split(", "));
                } else {
                    c.setCourseCategories(new String[0]);
                }

                courses.add(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return courses;
    }
    // Helper method to check if "All Categories" is selected
    private boolean isAllCategoriesSelected(String[] categories) {
        if (categories == null) return false;
        for (String cat : categories) {
            if ("all".equals(cat) || cat == null || cat.isEmpty()) return true;
        }
        return false;
    }
    // ==================== ENROLLMENT METHODS ====================
    // Get course enrollment count
    public int getCourseEnrollmentCount(int courseId) {
        String sql = "SELECT COUNT(*) as enrollment_count FROM course_enrollment WHERE course_id = ?";
        int count = 0;
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                count = rs.getInt("enrollment_count");
            }
        } catch (SQLException e) {
            System.err.println("Error getting enrollment count: " + e.getMessage());
            e.printStackTrace();
        }
        return count;
    }
    /**
     * Returns all courses a user has enrolled/bought.
     */
    public List<Course> getEnrolledCoursesByUser(int userId, String category, String keyword, int offset, int limit) {
        List<Course> courses = new ArrayList<>();
        try (Connection connection = DBUtil.getConnection()) {
            StringBuilder sql = new StringBuilder("SELECT" +
                    "    c.course_id," +
                    "    c.course_name," +
                    "    c.thumbnail_url," +
                    "    c.listed_price," +
                    "    c.sale_price," +
                    "    c.status," +
                    "    c.description," +
                    "    GROUP_CONCAT(cat.setting_name SEPARATOR ', ') AS categories," +
                    "    u.user_id as instructor_id," +
                    "    u.fullname AS instructor_name" +
                    " FROM course c" +
                    " LEFT JOIN course_user cu ON cu.course_id = c.course_id" +
                    " LEFT JOIN course_category cc ON c.course_id = cc.course_id" +
                    " LEFT JOIN setting cat ON cc.category_id = cat.setting_id AND cat.type_id = 5" +
                    " LEFT JOIN user u ON c.instructor_id = u.user_id" +
                    " LEFT JOIN setting s ON u.role_id = s.setting_id AND s.setting_name = 'Instructor'" +
                    " WHERE c.status = 1 AND cu.user_id = ?");
            if (keyword != null && !keyword.trim().isEmpty()) {
                sql.append(" AND (c.course_name LIKE ? OR u.fullname LIKE ?) ");
            }
            if (category != null && !category.trim().isEmpty()) {
                sql.append(" AND cat.setting_name = ?");
            }
            sql.append(" GROUP BY " +
                    "    c.course_id, c.course_name, c.thumbnail_url, c.listed_price, " +
                    "    c.sale_price, c.status, c.description," +
                    "    u.user_id, u.fullname");
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
                Course c = new Course();
                c.setId(resultSet.getInt("course_id"));
                c.setCourseName(resultSet.getString("course_name"));
                c.setThumbnailUrl(resultSet.getString("thumbnail_url"));
                c.setStatus(resultSet.getBoolean("status"));
                c.setDescription(resultSet.getString("description"));
                c.setInstructorId(resultSet.getInt("instructor_id"));
                c.setCourseInstructor(resultSet.getString("instructor_name"));

                String cats = resultSet.getString("categories");
                if (cats != null && !cats.isEmpty()) {
                    c.setCourseCategories(cats.split(", "));
                } else {
                    c.setCourseCategories(new String[0]);
                }
                
                courses.add(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return courses;
    }
    // Check if user is enrolled in a course
    public boolean isUserEnrolled(int userId, int courseId) {
        String sql = "SELECT COUNT(*) FROM course_enrollment WHERE user_id = ? AND course_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, courseId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking enrollment: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    // Enroll user in a course
    public boolean enrollUser(int userId, int courseId) {
        String sql = "INSERT INTO course_enrollment (user_id, course_id, enrolled_at) VALUES (?, ?, NOW())";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, courseId);
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Enrolled user " + userId + " in course " + courseId + ": " + rowsAffected + " rows affected");
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error enrolling user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
// ==================== INSTRUCTOR SPECIFIC METHODS (Added) ====================

    public List<Course> getCoursesByInstructor(int instructorId, String category, String keyword, int offset, int limit) {
        List<Course> courses = new ArrayList<>();
        // Query lấy khóa học do instructorId dạy
        StringBuilder sql = new StringBuilder(
                "SELECT c.*, u.fullname AS instructor_name, " +
                        "GROUP_CONCAT(DISTINCT s.setting_name SEPARATOR ', ') AS category_names " +
                        "FROM course c " +
                        "JOIN user u ON c.instructor_id = u.user_id " +
                        "LEFT JOIN course_category cc ON c.course_id = cc.course_id " +
                        "LEFT JOIN setting s ON cc.category_id = s.setting_id AND s.type_id = 5 " +
                        "WHERE c.instructor_id = ? ");

        if (keyword != null && !keyword.isEmpty()) {
            sql.append(" AND c.course_name LIKE ? ");
        }
        if (category != null && !category.isEmpty()) {
            sql.append(" AND EXISTS (SELECT 1 FROM course_category cc2 WHERE cc2.course_id = c.course_id AND cc2.category_id = ?) ");
        }

        sql.append(" GROUP BY c.course_id ORDER BY c.course_id DESC LIMIT ? OFFSET ?");

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int idx = 1;
            ps.setInt(idx++, instructorId);

            if (keyword != null && !keyword.isEmpty()) ps.setString(idx++, "%" + keyword + "%");
            if (category != null && !category.isEmpty()) ps.setString(idx++, category);

            ps.setInt(idx++, limit);
            ps.setInt(idx++, offset);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Course c = new Course();
                c.setCourseId(rs.getInt("course_id"));
                c.setCourseName(rs.getString("course_name"));
                c.setThumbnailUrl(rs.getString("thumbnail_url"));
                c.setListedPrice(rs.getBigDecimal("listed_price"));
                c.setSalePrice(rs.getBigDecimal("sale_price"));
                c.setStatus(rs.getBoolean("status"));
                c.setCourseInstructor(rs.getString("instructor_name"));
                c.setDescription(rs.getString("description"));

                String cats = rs.getString("category_names");
                if (cats != null && !cats.isEmpty()) {
                    c.setCourseCategories(cats.split(", "));
                } else {
                    c.setCourseCategories(new String[0]);
                }

                courses.add(c);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return courses;
    }

    public int countCoursesByInstructor(int instructorId, String category, String keyword) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(DISTINCT c.course_id) FROM course c " +
                        "LEFT JOIN course_category cc ON c.course_id = cc.course_id " +
                        "WHERE c.instructor_id = ? ");

        if (keyword != null && !keyword.isEmpty()) sql.append(" AND c.course_name LIKE ? ");
        if (category != null && !category.isEmpty()) sql.append(" AND cc.category_id = ? ");

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            ps.setInt(idx++, instructorId);
            if (keyword != null && !keyword.isEmpty()) ps.setString(idx++, "%" + keyword + "%");
            if (category != null && !category.isEmpty()) ps.setString(idx++, category);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }
}