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

    public List<Poster> getAllPoster() {
        List<Poster> posters = new ArrayList<>();
        try (Connection connection = DBUtil.getConnection()) {
            StringBuilder sql = new StringBuilder("SELECT p.*, u.fullname as user_name, u.avatar_url as user_avatar, s.setting_name as category_name FROM poster p " +
                    "JOIN user u ON u.user_id = p.user_id " +
                    "JOIN setting s ON s.setting_id = p.category_id ");
            sql.append("WHERE p.status = 1");
            PreparedStatement statement = connection.prepareStatement(sql.toString());
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
