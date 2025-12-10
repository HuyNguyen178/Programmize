package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Lesson;
import model.Lesson.LessonType;

import static utils.DBUtil.getConnection;

public class LessonDAO {

    // get a lesson by ID
    public Lesson getLessonById(int lessonId) {
        String sql = "SELECT * FROM lesson WHERE lesson_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, lessonId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToLesson(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting lesson by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // get all lessons by chapter ID (order by order_index)
    public List<Lesson> getLessonsByChapterId(int chapterId) {
        List<Lesson> lessons = new ArrayList<>();
        String sql = "SELECT * FROM lesson WHERE chapter_id = ? ORDER BY order_index ASC";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, chapterId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lessons.add(mapResultSetToLesson(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting lessons by chapter ID: " + e.getMessage());
            e.printStackTrace();
        }
        return lessons;
    }

    // get all active (status=1) lessons by chapter ID (public pages)
    public List<Lesson> getActiveLessonsByChapterId(int chapterId) {
        List<Lesson> lessons = new ArrayList<>();
        String sql = "SELECT * FROM lesson WHERE chapter_id = ? AND status = 1 ORDER BY order_index ASC";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, chapterId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lessons.add(mapResultSetToLesson(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting active lessons by chapter ID: " + e.getMessage());
            e.printStackTrace();
        }
        return lessons;
    }

    // get all lessons for a course (through chapters)
    public List<Lesson> getLessonsByCourseId(int courseId) {
        List<Lesson> lessons = new ArrayList<>();
        String sql = "SELECT l.* FROM lesson l " +
                "INNER JOIN chapter c ON l.chapter_id = c.chapter_id " +
                "WHERE c.course_id = ? " +
                "ORDER BY c.order_index ASC, l.order_index ASC";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lessons.add(mapResultSetToLesson(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting lessons by course ID: " + e.getMessage());
            e.printStackTrace();
        }
        return lessons;
    }

    // get all lessons
    public List<Lesson> getAllLessons() {
        List<Lesson> lessons = new ArrayList<>();
        String sql = "SELECT * FROM lesson ORDER BY chapter_id, order_index ASC";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lessons.add(mapResultSetToLesson(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all lessons: " + e.getMessage());
            e.printStackTrace();
        }
        return lessons;
    }

    // count total lessons in chapter
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

    // count active (status = 1) lessons in a chapter
    public int countActiveLessonsByChapterId(int chapterId) {
        String sql = "SELECT COUNT(*) AS total FROM lesson WHERE chapter_id = ? AND status = 1";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, chapterId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error counting active lessons: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    // Get total duration of all lessons in a chapter
    public int getTotalDurationByChapterId(int chapterId) {
        String sql = "SELECT COALESCE(SUM(duration), 0) AS total_duration FROM lesson WHERE chapter_id = ? AND status = 1";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, chapterId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total_duration");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting total duration: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    // get total time  of all lessons in a course
    public int getTotalDurationByCourseId(int courseId) {
        String sql = "SELECT COALESCE(SUM(l.duration), 0) AS total_duration " +
                "FROM lesson l " +
                "INNER JOIN chapter c ON l.chapter_id = c.chapter_id " +
                "WHERE c.course_id = ? AND l.status = 1";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total_duration");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting total course duration: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    // insert new lesson
    public int insertLesson(Lesson lesson) {
        String sql = "INSERT INTO lesson (chapter_id, lesson_name, lesson_type, content, video_url, " +
                "duration, order_index, is_preview, status, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, lesson.getChapterId());
            ps.setString(2, lesson.getLessonName());
            ps.setString(3, lesson.getLessonType() != null ? lesson.getLessonType().getValue() : "video");
            ps.setString(4, lesson.getContent());
            ps.setString(5, lesson.getVideoUrl());
            ps.setInt(6, lesson.getDuration());
            ps.setInt(7, lesson.getOrderIndex());
            ps.setBoolean(8, lesson.isPreview());
            ps.setBoolean(9, lesson.getStatus() != null && lesson.getStatus());
            ps.setDate(10, new java.sql.Date(lesson.getCreatedAt() != null ?
                    lesson.getCreatedAt().getTime() : new java.util.Date().getTime()));
            ps.setDate(11, new java.sql.Date(lesson.getUpdatedAt() != null ?
                    lesson.getUpdatedAt().getTime() : new java.util.Date().getTime()));

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting lesson: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    // update lesson
    public boolean updateLesson(Lesson lesson) {
        String sql = "UPDATE lesson SET chapter_id = ?, lesson_name = ?, lesson_type = ?, " +
                "content = ?, video_url = ?, duration = ?, order_index = ?, " +
                "is_preview = ?, status = ?, updated_at = ? WHERE lesson_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, lesson.getChapterId());
            ps.setString(2, lesson.getLessonName());
            ps.setString(3, lesson.getLessonType() != null ? lesson.getLessonType().getValue() : "video");
            ps.setString(4, lesson.getContent());
            ps.setString(5, lesson.getVideoUrl());
            ps.setInt(6, lesson.getDuration());
            ps.setInt(7, lesson.getOrderIndex());
            ps.setBoolean(8, lesson.isPreview());
            ps.setBoolean(9, lesson.getStatus() != null && lesson.getStatus());
            ps.setDate(10, new java.sql.Date(new java.util.Date().getTime()));
            ps.setInt(11, lesson.getLessonId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating lesson: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // delete a lesson by ID
    public boolean deleteLesson(int lessonId) {
        String sql = "DELETE FROM lesson WHERE lesson_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, lessonId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting lesson: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // get next order index for a chapter (for add new lesson)
    public int getNextOrderIndex(int chapterId) {
        String sql = "SELECT COALESCE(MAX(order_index), 0) + 1 AS next_index FROM lesson WHERE chapter_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, chapterId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("next_index");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting next order index: " + e.getMessage());
            e.printStackTrace();
        }
        return 1;
    }

    // get chapter name by chapter ID
    public String getChapterNameById(int chapterId) {
        String sql = "SELECT chapter_name FROM chapter WHERE chapter_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, chapterId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("chapter_name");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting chapter name: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<String[]> getAllChaptersForDropdown() {
        List<String[]> chapters = new ArrayList<>();
        String sql = "SELECT c.chapter_id, c.chapter_name, co.course_name " +
                "FROM chapter c " +
                "INNER JOIN course co ON c.course_id = co.course_id " +
                "ORDER BY co.course_name ASC, c.order_index ASC";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String[] chapter = new String[3];
                chapter[0] = String.valueOf(rs.getInt("chapter_id"));
                chapter[1] = rs.getString("chapter_name");
                chapter[2] = rs.getString("course_name");
                chapters.add(chapter);
            }
        } catch (SQLException e) {
            System.err.println("Error getting chapters for dropdown: " + e.getMessage());
            e.printStackTrace();
        }
        return chapters;
    }

    // map ResultSet row to Lesson object
    private Lesson mapResultSetToLesson(ResultSet rs) throws SQLException {
        Lesson lesson = new Lesson();
        lesson.setLessonId(rs.getInt("lesson_id"));
        lesson.setChapterId(rs.getInt("chapter_id"));
        lesson.setLessonName(rs.getString("lesson_name"));
        lesson.setLessonTypeFromString(rs.getString("lesson_type"));
        lesson.setContent(rs.getString("content"));
        lesson.setVideoUrl(rs.getString("video_url"));
        lesson.setDuration(rs.getInt("duration"));
        lesson.setOrderIndex(rs.getInt("order_index"));
        lesson.setPreview(rs.getBoolean("is_preview"));
        lesson.setStatus(rs.getBoolean("status"));
        lesson.setCreatedAt(rs.getDate("created_at"));
        lesson.setUpdatedAt(rs.getDate("updated_at"));
        return lesson;
    }

    // ==================== Main method for testing ====================
    public static void main(String[] args) {
        LessonDAO dao = new LessonDAO();

        // Test getLessonById
        System.out.println("=== Test getLessonById ===");
        Lesson lesson = dao.getLessonById(1);
        if (lesson != null) {
            System.out.println("Found lesson: " + lesson);
            System.out.println("Duration formatted: " + lesson.getDurationFormatted());
            System.out.println("Type icon: " + lesson.getTypeIcon());
        } else {
            System.out.println("Lesson not found");
        }

        // Test getLessonsByChapterId
        System.out.println("\n=== Test getLessonsByChapterId (Chapter 1) ===");
        List<Lesson> lessons = dao.getLessonsByChapterId(1);
        System.out.println("Found " + lessons.size() + " lessons");
        for (Lesson l : lessons) {
            System.out.println("  - " + l.getLessonName() + " (" + l.getDurationFormatted() + ")");
        }

        // Test count and duration
        System.out.println("\n=== Test count and duration ===");
        System.out.println("Total lessons in chapter 1: " + dao.countLessonsByChapterId(1));
        System.out.println("Total duration in chapter 1: " + dao.getTotalDurationByChapterId(1) + " seconds");
    }
}