package dao;

import model.Poster;
import model.Setting;
import model.User;
import org.eclipse.tags.shaded.org.apache.regexp.RE;
import utils.DBUtil;
import utils.PosterUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class PosterDAO {
    public void createPoster(Poster poster) {
        try (Connection connection = DBUtil.getConnection()) {
            String sql = "INSERT INTO poster(user_id, title, slug, content, excerpt, thumbnail_url, status, category_id, view_count, created_at, updated_at, published_at) " +
                    " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, poster.getUser().getId());
            statement.setString(2, poster.getTitle());
            statement.setString(3, poster.getSlug());
            statement.setString(4, poster.getContent());
            statement.setString(5, poster.getExcerpt());
            statement.setString(6, poster.getThumbnailUrl());
            statement.setBoolean(7, poster.isStatus());
            statement.setInt(8, poster.getCategory().getId());
            statement.setInt(9, poster.getViewCount());
            statement.setTimestamp(10, poster.getCreatedAt());
            statement.setTimestamp(11, poster.getUpdatedAt());
            statement.setTimestamp(12, poster.getPublishedAt());

            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Poster> getAllPoster(Integer categoryId, String keyword, int page, int pageSize) {
        List<Poster> posters = new ArrayList<>();
        try (Connection connection = DBUtil.getConnection()) {
            StringBuilder sql = new StringBuilder("SELECT p.*, u.fullname as user_name, u.avatar_url as user_avatar, s.setting_name as category_name FROM poster p " +
                    "JOIN user u ON u.user_id = p.user_id " +
                    "JOIN setting s ON s.setting_id = p.category_id " +
                    "WHERE p.status = 1 ");
            if (categoryId != null) {
                sql.append("AND s.setting_id = ? ");
            }

            if (keyword != null && !keyword.isBlank()) {
                sql.append(" AND (p.title LIKE ? OR p.excerpt LIKE ?) ");
            }

            sql.append("ORDER BY p.published_at ASC LIMIT ? OFFSET ? ");

            int offset = (page - 1) * pageSize;

            PreparedStatement statement = connection.prepareStatement(sql.toString());
            int paramIndex = 1;
            if (categoryId != null) {
                statement.setInt(paramIndex++, categoryId);
            }
            if (keyword != null && !keyword.isBlank()) {
                statement.setString(paramIndex++, "%" + keyword + "%");
                statement.setString(paramIndex++, "%" + keyword + "%");
            }

            statement.setInt(paramIndex++, pageSize);
            statement.setInt(paramIndex++, offset);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Poster poster = new Poster();
                poster.setPostId(resultSet.getInt("post_id"));
                poster.setThumbnailUrl(resultSet.getString("thumbnail_url"));
                poster.setTitle(resultSet.getString("title"));
                poster.setSlug(resultSet.getString("slug"));
                poster.setExcerpt(resultSet.getString("excerpt"));
                poster.setViewCount(resultSet.getInt("view_count"));
                poster.setPublishedAt(resultSet.getTimestamp("published_at"));

                User user = new User();
                user.setId(resultSet.getInt("user_id"));
                user.setFullname(resultSet.getString("user_name"));
                user.setAvatarUrl(resultSet.getString("user_avatar"));
                poster.setUser(user);

                Setting category = new Setting();
                category.setId(resultSet.getInt("category_id"));
                category.setName(resultSet.getString("category_name"));
                poster.setCategory(category);

                posters.add(poster);
            }
            return posters;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Poster> getRelatedPosters(Integer categoryId, Integer postId) {
        List<Poster> posters = new ArrayList<>();
        try (Connection connection = DBUtil.getConnection()) {
            String sql = "SELECT p.* FROM poster p " +
                    "JOIN setting s ON p.category_id = s.setting_id " +
                    "WHERE s.setting_id = ? AND post_id <> ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, categoryId);
            statement.setInt(2, postId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Poster poster = new Poster();
                poster.setTitle(resultSet.getString("title"));
                poster.setViewCount(resultSet.getInt("view_count"));
                poster.setThumbnailUrl(resultSet.getString("thumbnail_url"));
                poster.setPublishedAt(resultSet.getTimestamp("published_at"));

                posters.add(poster);
            }
            return posters;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void updateViewCountById(Integer id) {
        try (Connection connection = DBUtil.getConnection()) {
            String sql = "UPDATE poster SET view_count = view_count + 1 WHERE post_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Poster> getPopularPosters() {
        List<Poster> posters = new ArrayList<>();
        try (Connection connection = DBUtil.getConnection()) {
            String sql = "SELECT * FROM poster WHERE status = 1 ORDER BY view_count DESC LIMIT 5";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Poster poster = new Poster();
                poster.setTitle(resultSet.getString("title"));
                poster.setThumbnailUrl(resultSet.getString("thumbnail_url"));
                poster.setSlug(resultSet.getString("slug"));
                poster.setViewCount(resultSet.getInt("view_count"));

                posters.add(poster);
            }
            return posters;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public int countPoster(Integer categoryId, String keyword) {
        try (Connection connection = DBUtil.getConnection()) {
            StringBuilder sql = new StringBuilder("SELECT COUNT(*) " +
                    "FROM poster WHERE status = 1 ");
            if (categoryId != null) {
                sql.append("AND category_id = ? ");
            }
            if (keyword != null && !keyword.isEmpty()) {
                sql.append("AND (title LIKE ? OR excerpt LIKE ?)");
            }

            PreparedStatement statement = connection.prepareStatement(sql.toString());
            int paramIndex = 1;
            if (categoryId != null) {
                statement.setInt(paramIndex++, categoryId);
            }
            if (keyword != null && !keyword.isEmpty()) {
                statement.setString(paramIndex++, "%" + keyword + "%");
                statement.setString(paramIndex++, "%" + keyword + "%");
            }

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Poster getMostPopularPoster() {
        try (Connection connection = DBUtil.getConnection()) {
            String sql = "SELECT p.*, u.fullname as user_name FROM poster p JOIN user u ON p.user_id = u.user_id WHERE p.status = 1 ORDER BY p.view_count DESC LIMIT 1";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Poster poster = new Poster();
                poster.setTitle(resultSet.getString("title"));
                poster.setThumbnailUrl(resultSet.getString("thumbnail_url"));
                poster.setSlug(resultSet.getString("slug"));
                poster.setExcerpt(resultSet.getString("excerpt"));
                poster.setViewCount(resultSet.getInt("view_count"));
                poster.setPublishedAt(resultSet.getTimestamp("published_at"));

                User user = new User();
                user.setFullname(resultSet.getString("user_name"));
                poster.setUser(user);

                return poster;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public Poster getPosterBySlug(String slug) {
        try (Connection connection = DBUtil.getConnection()) {
            String sql = "SELECT p.*,s.setting_id, s.setting_name AS category_name, u.fullname as user_name, u.avatar_url as user_avatar " +
                    "FROM poster p " +
                    "JOIN user u ON p.user_id = u.user_id " +
                    "JOIN setting s ON p.category_id = s.setting_id " +
                    "WHERE p.slug = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, slug);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Poster poster = new Poster();
                poster.setPostId(resultSet.getInt("post_id"));
                poster.setSlug(resultSet.getString("slug"));
                poster.setTitle(resultSet.getString("title"));
                poster.setExcerpt(resultSet.getString("excerpt"));
                poster.setContent(resultSet.getString("content"));
                poster.setThumbnailUrl(resultSet.getString("thumbnail_url"));
                poster.setViewCount(resultSet.getInt("view_count"));
                poster.setPublishedAt(resultSet.getTimestamp("published_at"));

                User user = new User();
                user.setFullname(resultSet.getString("user_name"));
                user.setAvatarUrl(resultSet.getString("user_avatar"));
                poster.setUser(user);

                Setting category = new Setting();
                category.setId(resultSet.getInt("setting_id"));
                category.setName(resultSet.getString("category_name"));
                poster.setCategory(category);

                return poster;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void updatePoster(Poster poster) {
        try (Connection connection = DBUtil.getConnection()) {
            String sql = "UPDATE poster SET title = ?, excerpt = ?, slug = ?, content = ?, thumbnail_url = ?, status = ?, category_id = ?, updated_at = ?, published_at = ? WHERE post_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, poster.getTitle());
            statement.setString(2, poster.getExcerpt());
            statement.setString(3, poster.getSlug());
            statement.setString(4, poster.getContent());
            statement.setString(5, poster.getThumbnailUrl());
            statement.setBoolean(6, poster.isStatus());
            statement.setInt(7, poster.getCategory().getId());
            statement.setTimestamp(8, poster.getUpdatedAt());
            statement.setTimestamp(9, poster.getPublishedAt());
            statement.setInt(10, poster.getPostId());

            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Poster getPosterByUserIdAndSlug(Integer userId, String slug) {
        try (Connection connection = DBUtil.getConnection()) {
            String sql = "SELECT p.*,s.setting_id, s.setting_name AS category_name, u.fullname as user_name, u.avatar_url as user_avatar " +
                    "FROM poster p " +
                    "JOIN user u ON p.user_id = u.user_id " +
                    "JOIN setting s ON p.category_id = s.setting_id " +
                    "WHERE p.slug = ? AND p.user_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, slug);
            statement.setInt(2, userId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Poster poster = new Poster();
                poster.setPostId(resultSet.getInt("post_id"));
                poster.setSlug(resultSet.getString("slug"));
                poster.setTitle(resultSet.getString("title"));
                poster.setExcerpt(resultSet.getString("excerpt"));
                poster.setContent(resultSet.getString("content"));
                poster.setThumbnailUrl(resultSet.getString("thumbnail_url"));
                poster.setViewCount(resultSet.getInt("view_count"));
                poster.setPublishedAt(resultSet.getTimestamp("published_at"));

                User user = new User();
                user.setFullname(resultSet.getString("user_name"));
                user.setAvatarUrl(resultSet.getString("user_avatar"));
                poster.setUser(user);

                Setting category = new Setting();
                category.setId(resultSet.getInt("setting_id"));
                category.setName(resultSet.getString("category_name"));
                poster.setCategory(category);

                return poster;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Poster> getDraftsByUserId(Integer userId, String keyword) {
        List<Poster> posters = new ArrayList<>();
        try (Connection connection = DBUtil.getConnection()) {
            StringBuilder sql = new StringBuilder("SELECT * FROM poster WHERE status = 0 AND user_id = ? ");

            if (keyword != null && !keyword.isEmpty()) {
                sql.append("AND (title LIKE ? OR excerpt LIKE ?)");
            }

            PreparedStatement statement = connection.prepareStatement(sql.toString());
            statement.setInt(1, userId);

            if (keyword != null && !keyword.isEmpty()) {
                statement.setString(2, "%" + keyword + "%");
                statement.setString(3, "%" + keyword + "%");
            }

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Poster poster = new Poster();
                poster.setPostId(resultSet.getInt("post_id"));
                poster.setThumbnailUrl(resultSet.getString("thumbnail_url"));
                poster.setTitle(resultSet.getString("title"));
                poster.setSlug(resultSet.getString("slug"));
                poster.setExcerpt(resultSet.getString("excerpt"));
                poster.setCreatedAt(resultSet.getTimestamp("created_at"));

                posters.add(poster);
            }
            return posters;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void publishDraft(Integer postId, Integer userId) {
        try (Connection connection = DBUtil.getConnection()) {
            String sql = "UPDATE poster SET status = 1, updated_at = ?, published_at = ? WHERE post_id = ? AND user_id = ? AND status = 0";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            statement.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            statement.setInt(3, postId);
            statement.setInt(4, userId);
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getTotalDraftByUserId(Integer userId) {
        try (Connection connection = DBUtil.getConnection()) {
            String sql = "SELECT COUNT(*) AS total_drafts FROM poster WHERE status = 0 AND user_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("total_drafts");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Timestamp getLastUpdatedByUserId(Integer userId) {
        try (Connection connection = DBUtil.getConnection()) {
            String sql = "SELECT MAX(updated_at) AS last_update FROM poster WHERE status = 0 AND user_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getTimestamp("last_update");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean existsBySlug(String slug) {
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT 1 FROM poster WHERE slug = ? LIMIT 1";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, slug);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String generateUniqueSlug(String title) {
        String baseSlug = PosterUtil.toSlug(title);
        String slug = baseSlug;
        int counter = 1;

        while (existsBySlug(slug)) {
            slug = baseSlug + "-" + counter;
            counter++;
        }

        return slug;
    }

    public void deleteDraft(Integer postId, Integer userId) {
        try (Connection connection = DBUtil.getConnection()) {
            String sql = "DELETE FROM poster WHERE post_id = ? AND user_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, postId);
            statement.setInt(2, userId);

            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
