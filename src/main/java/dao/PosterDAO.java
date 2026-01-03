package dao;

import model.Poster;
import model.Setting;
import model.User;
import utils.DBUtil;
import utils.PosterUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

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

    public List<Poster> getPopularPosters() {
        List<Poster> posters = new ArrayList<>();
        try (Connection connection = DBUtil.getConnection()) {
            String sql = "SELECT * FROM poster ORDER BY view_count DESC LIMIT 5";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Poster poster = new Poster();
                poster.setTitle(resultSet.getString("title"));
                poster.setThumbnailUrl(resultSet.getString("thumbnail_url"));
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
            String sql = "SELECT p.*, u.fullname as user_name FROM poster p JOIN user u ON p.user_id = u.user_id ORDER BY p.view_count DESC LIMIT 1";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Poster poster = new Poster();
                poster.setTitle(resultSet.getString("title"));
                poster.setThumbnailUrl(resultSet.getString("thumbnail_url"));
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
}
