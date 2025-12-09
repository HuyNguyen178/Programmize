package dao;

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

        // Filter by status
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

            // Set status parameter
            if (status != null && !status.isEmpty() && !status.equals("All Statuses") && !status.equals("")) {
                stmt.setString(paramIndex++, status);
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
                course.setCourseCategory(rs.getString("category_names"));
                course.setCourseInstructor(rs.getString("instructor_name"));
                course.setListedPrice(rs.getBigDecimal("listed_price"));
                course.setSalePrice(rs.getBigDecimal("sale_price"));
                course.setDescription(rs.getString("description"));
                course.setStatus(rs.getString("status"));
                course.setDuration(rs.getInt("duration"));
                course.setInstructorId(rs.getInt("instructor_id"));
                courses.add(course);
            }

            System.out.println("Number of courses retrieved: " + courses.size());

        } catch (SQLException e) {
            System.err.println("SQL Error in getAllCourses: " + e.getMessage());
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
            System.out.println("Retrieved " + categories.size() + " categories from settings");
        } catch (SQLException e) {
            System.err.println("Error getting categories from settings: " + e.getMessage());
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

        System.out.println("Getting course by ID: " + courseId);

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, courseId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                course = new Course();
                course.setCourseId(rs.getInt("course_id"));
                course.setThumbnailUrl(rs.getString("thumbnail_url"));
                course.setCourseName(rs.getString("course_name"));
                course.setCourseCategory(rs.getString("category_names"));
                course.setCourseInstructor(rs.getString("instructor_name"));
                course.setListedPrice(rs.getBigDecimal("listed_price"));
                course.setSalePrice(rs.getBigDecimal("sale_price"));
                course.setDescription(rs.getString("description"));
                course.setStatus(rs.getString("status"));
                course.setDuration(rs.getInt("duration"));
                course.setInstructorId(rs.getInt("instructor_id"));

                System.out.println("Found course: " + course.getCourseName());
            }

        } catch (SQLException e) {
            System.err.println("Error getting course by ID: " + e.getMessage());
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

            // Update course info
            String updateSql = "UPDATE course SET course_name = ?, listed_price = ?, sale_price = ?, " +
                    "thumbnail_url = ?, description = ?, status = ?, duration = ?, instructor_id = ? " +
                    "WHERE course_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                stmt.setString(1, course.getCourseName());
                stmt.setBigDecimal(2, course.getListedPrice());
                stmt.setBigDecimal(3, course.getSalePrice());
                stmt.setString(4, course.getThumbnailUrl());
                stmt.setString(5, course.getDescription());
                stmt.setString(6, course.getStatus());
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
            System.err.println("Error updating course with categories: " + e.getMessage());
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
            stmt.setString(6, course.getStatus());
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

    // Add new course with categories
    public int addCourseWithCategories(Course course, int[] categoryIds) {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            // Insert course
            String insertCourseSql = "INSERT INTO course (course_name, listed_price, sale_price, thumbnail_url, " +
                    "description, status, duration, instructor_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            int courseId = -1;
            try (PreparedStatement stmt = conn.prepareStatement(insertCourseSql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, course.getCourseName());
                stmt.setBigDecimal(2, course.getListedPrice());
                stmt.setBigDecimal(3, course.getSalePrice());
                stmt.setString(4, course.getThumbnailUrl());
                stmt.setString(5, course.getDescription());
                stmt.setString(6, course.getStatus());
                stmt.setInt(7, course.getDuration() != null ? course.getDuration() : 0);
                stmt.setInt(8, course.getInstructorId() != null ? course.getInstructorId() : 0);
                stmt.executeUpdate();

                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    courseId = generatedKeys.getInt(1);
                }
            }

            // Insert categories
            if (courseId > 0 && categoryIds != null && categoryIds.length > 0) {
                String insertCategorySql = "INSERT INTO course_category (course_id, category_id) VALUES (?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(insertCategorySql)) {
                    for (int categoryId : categoryIds) {
                        stmt.setInt(1, courseId);
                        stmt.setInt(2, categoryId);
                        stmt.addBatch();
                    }
                    stmt.executeBatch();
                }
            }

            conn.commit();
            return courseId;

        } catch (SQLException e) {
            System.err.println("Error adding course with categories: " + e.getMessage());
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
    }

    // ==================== PUBLIC COURSE METHODS ====================
    // Used by: PublicCourseServlet, PublicCourseDetailsServlet

    // Get public courses (active status) with filters
    // categoryIds can be setting_id values or category names
    public List<Course> getPublicCourses(String searchKeyword, String[] categoryIds, String priceSort) {
        List<Course> courses = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT DISTINCT c.course_id, c.course_name, c.listed_price, c.sale_price, " +
                        "c.thumbnail_url, c.instructor_id, c.duration, c.description, c.status, " +
                        "u.fullname AS instructor_name, " +
                        "GROUP_CONCAT(DISTINCT s.setting_name SEPARATOR ', ') AS category_names " +
                        "FROM course c " +
                        "LEFT JOIN user u ON c.instructor_id = u.user_id " +
                        "LEFT JOIN course_category cc ON c.course_id = cc.course_id " +
                        "LEFT JOIN setting s ON cc.category_id = s.setting_id AND s.type_id = 5 " +
                        "WHERE c.status = 1"
        );

        // Add search keyword filter
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            sql.append(" AND (c.course_name LIKE ? OR c.description LIKE ? OR u.fullname LIKE ?)");
        }

        // Add category filter - support both ID and name
        if (categoryIds != null && categoryIds.length > 0 && !isAllCategoriesSelected(categoryIds)) {
            // Check if first element is numeric (ID) or string (name)
            boolean isNumeric = false;
            try {
                Integer.parseInt(categoryIds[0]);
                isNumeric = true;
            } catch (NumberFormatException e) {
                isNumeric = false;
            }

            if (isNumeric) {
                // Filter by category ID
                sql.append(" AND EXISTS (SELECT 1 FROM course_category cc2 WHERE cc2.course_id = c.course_id AND cc2.category_id IN (");
            } else {
                // Filter by category name
                sql.append(" AND EXISTS (SELECT 1 FROM course_category cc2 " +
                        "INNER JOIN setting s2 ON cc2.category_id = s2.setting_id AND s2.type_id = 5 " +
                        "WHERE cc2.course_id = c.course_id AND s2.setting_name IN (");
            }
            for (int i = 0; i < categoryIds.length; i++) {
                sql.append("?");
                if (i < categoryIds.length - 1) sql.append(",");
            }
            sql.append("))");
        }

        sql.append(" GROUP BY c.course_id, c.course_name, c.listed_price, c.sale_price, " +
                "c.thumbnail_url, c.instructor_id, c.duration, c.description, c.status, u.fullname");

        if ("low".equals(priceSort)) {
            sql.append(" ORDER BY COALESCE(c.sale_price, c.listed_price) ASC");
        } else if ("high".equals(priceSort)) {
            sql.append(" ORDER BY COALESCE(c.sale_price, c.listed_price) DESC");
        } else {
            sql.append(" ORDER BY c.course_id ASC");
        }

        System.out.println("Public courses SQL: " + sql.toString());

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            stmt = conn.prepareStatement(sql.toString());

            int paramIndex = 1;

            if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
                String searchPattern = "%" + searchKeyword + "%";
                stmt.setString(paramIndex++, searchPattern);
                stmt.setString(paramIndex++, searchPattern);
                stmt.setString(paramIndex++, searchPattern);
            }

            if (categoryIds != null && categoryIds.length > 0 && !isAllCategoriesSelected(categoryIds)) {
                boolean isNumeric = false;
                try {
                    Integer.parseInt(categoryIds[0]);
                    isNumeric = true;
                } catch (NumberFormatException e) {
                    isNumeric = false;
                }

                for (String categoryId : categoryIds) {
                    if (isNumeric) {
                        stmt.setInt(paramIndex++, Integer.parseInt(categoryId));
                    } else {
                        stmt.setString(paramIndex++, categoryId);
                    }
                }
            }

            rs = stmt.executeQuery();

            while (rs.next()) {
                Course course = new Course();
                course.setCourseId(rs.getInt("course_id"));
                course.setThumbnailUrl(rs.getString("thumbnail_url"));
                course.setCourseName(rs.getString("course_name"));
                course.setCourseCategory(rs.getString("category_names"));
                course.setCourseInstructor(rs.getString("instructor_name"));
                course.setListedPrice(rs.getBigDecimal("listed_price"));
                course.setSalePrice(rs.getBigDecimal("sale_price"));
                course.setDescription(rs.getString("description"));
                course.setStatus(rs.getString("status"));
                course.setDuration(rs.getInt("duration"));
                course.setInstructorId(rs.getInt("instructor_id"));
                courses.add(course);
            }

            System.out.println("Found " + courses.size() + " public courses");

        } catch (SQLException e) {
            System.err.println("Error getting public courses: " + e.getMessage());
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
        String sql = "SELECT COUNT(*) as enrollment_count FROM enrollment WHERE course_id = ?";
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
    public List<Course> getEnrolledCoursesByUser(int userId) {
        List<Course> list = new ArrayList<>();

        String sql = "SELECT c.course_id, c.course_name, c.listed_price, c.sale_price, " +
                "c.thumbnail_url, c.instructor_id, c.duration, c.description, c.status, " +
                "u.fullname AS instructor_name, " +
                "GROUP_CONCAT(DISTINCT s.setting_name SEPARATOR ', ') AS category_names " +
                "FROM enrollment e " +
                "INNER JOIN course c ON e.course_id = c.course_id " +
                "LEFT JOIN user u ON c.instructor_id = u.user_id " +
                "LEFT JOIN course_category cc ON c.course_id = cc.course_id " +
                "LEFT JOIN setting s ON cc.category_id = s.setting_id AND s.type_id = 5 " +
                "WHERE e.user_id = ? " +
                "GROUP BY c.course_id, c.course_name, c.listed_price, c.sale_price, " +
                "c.thumbnail_url, c.instructor_id, c.duration, c.description, c.status, u.fullname";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Course c = new Course();
                c.setCourseId(rs.getInt("course_id"));
                c.setCourseName(rs.getString("course_name"));
                c.setListedPrice(rs.getBigDecimal("listed_price"));
                c.setSalePrice(rs.getBigDecimal("sale_price"));
                c.setThumbnailUrl(rs.getString("thumbnail_url"));
                c.setInstructorId(rs.getInt("instructor_id"));
                c.setDuration(rs.getInt("duration"));
                c.setDescription(rs.getString("description"));
                c.setStatus(rs.getString("status"));
                c.setCourseInstructor(rs.getString("instructor_name"));
                c.setCourseCategory(rs.getString("category_names"));

                list.add(c);
            }

            System.out.println("Found " + list.size() + " enrolled courses for user ID: " + userId);

        } catch (SQLException e) {
            System.err.println("Error getting enrolled courses: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    // Check if user is enrolled in a course
    public boolean isUserEnrolled(int userId, int courseId) {
        String sql = "SELECT COUNT(*) FROM enrollment WHERE user_id = ? AND course_id = ?";

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
        String sql = "INSERT INTO enrollment (user_id, course_id, enrolled_at) VALUES (?, ?, NOW())";

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
        String sql = "SELECT c.*, u.fullname AS instructor_name, s.value AS category_name " +
                "FROM course c " +
                "JOIN user u ON c.instructor_id = u.user_id " +
                "LEFT JOIN course_category cc ON c.course_id = cc.course_id " +
                "LEFT JOIN setting s ON cc.category_id = s.setting_id " +
                "WHERE c.status = 1 " +
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
                c.setStatus(rs.getBoolean("status") ? "Active" : "Inactive");

                // Set các trường lấy từ bảng join
                c.setCourseInstructor(rs.getString("instructor_name"));
                c.setCourseCategory(rs.getString("category_name"));

                list.add(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}