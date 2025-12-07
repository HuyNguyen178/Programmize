package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Chapter;
import utils.DBUtil;

public class ChapterDAO extends DBUtil {

    /**
     * Get a chapter by its ID
     */
    public Chapter getChapterById(int chapterId) {
        String sql = "SELECT * FROM chapter WHERE chapter_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, chapterId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToChapter(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting chapter by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get all chapters by course ID
     */
    public List<Chapter> getChaptersByCourseId(int courseId) {
        List<Chapter> chapters = new ArrayList<>();
        String sql = "SELECT * FROM chapter WHERE course_id = ? ORDER BY order_index ASC";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    chapters.add(mapResultSetToChapter(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting chapters by course ID: " + e.getMessage());
            e.printStackTrace();
        }
        return chapters;
    }

    /**
     * Get all chapters
     */
    public List<Chapter> getAllChapters() {
        List<Chapter> chapters = new ArrayList<>();
        String sql = "SELECT * FROM chapter ORDER BY course_id, order_index ASC";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                chapters.add(mapResultSetToChapter(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all chapters: " + e.getMessage());
            e.printStackTrace();
        }
        return chapters;
    }

    /**
     * Get course name by course ID
     */
    public String getCourseNameById(int courseId) {
        String sql = "SELECT course_name FROM course WHERE course_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("course_name");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting course name: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Count total lessons in a chapter
     */
    public int countLessonsByChapterId(int chapterId) {
        String sql = "SELECT COUNT(*) AS total FROM lesson WHERE chapter_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, chapterId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error counting lessons: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Insert a new chapter
     */
    public int insertChapter(Chapter chapter) {
        String sql = "INSERT INTO chapter (course_id, chapter_name, description, order_index, status, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, chapter.getCourseId());
            ps.setString(2, chapter.getChapterName());
            ps.setString(3, chapter.getDescription());
            ps.setInt(4, chapter.getOrderIndex());
            ps.setBoolean(5, chapter.getStatus() != null && chapter.getStatus());
            ps.setDate(6, new java.sql.Date(chapter.getCreatedAt().getTime()));
            ps.setDate(7, new java.sql.Date(chapter.getUpdatedAt().getTime()));

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting chapter: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Update an existing chapter
     */
    public boolean updateChapter(Chapter chapter) {
        String sql = "UPDATE chapter SET course_id = ?, chapter_name = ?, description = ?, " +
                "order_index = ?, status = ?, updated_at = ? WHERE chapter_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, chapter.getCourseId());
            ps.setString(2, chapter.getChapterName());
            ps.setString(3, chapter.getDescription());
            ps.setInt(4, chapter.getOrderIndex());
            ps.setBoolean(5, chapter.getStatus() != null && chapter.getStatus());
            ps.setDate(6, new java.sql.Date(new java.util.Date().getTime()));
            ps.setInt(7, chapter.getChapterId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating chapter: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Delete a chapter by ID
     */
    public boolean deleteChapter(int chapterId) {
        String sql = "DELETE FROM chapter WHERE chapter_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, chapterId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting chapter: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Map a ResultSet row to a Chapter object
     */
    private Chapter mapResultSetToChapter(ResultSet rs) throws SQLException {
        Chapter chapter = new Chapter();
        chapter.setChapterId(rs.getInt("chapter_id"));
        chapter.setCourseId(rs.getInt("course_id"));
        chapter.setChapterName(rs.getString("chapter_name"));
        chapter.setDescription(rs.getString("description"));
        chapter.setOrderIndex(rs.getInt("order_index"));
        chapter.setStatus(rs.getBoolean("status"));
        chapter.setCreatedAt(rs.getDate("created_at"));
        chapter.setUpdatedAt(rs.getDate("updated_at"));
        return chapter;
    }

    // Main method for testing
    public static void main(String[] args) {
        ChapterDAO dao = new ChapterDAO();

        // Test getChapterById
        Chapter chapter = dao.getChapterById(1);
        if (chapter != null) {
            System.out.println("Found chapter: " + chapter);
            System.out.println("Course name: " + dao.getCourseNameById(chapter.getCourseId()));
            System.out.println("Total lessons: " + dao.countLessonsByChapterId(chapter.getChapterId()));
        } else {
            System.out.println("Chapter not found");
        }
    }
}