package utils;

import java.sql.Timestamp;
import java.text.Normalizer;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;

public class PosterUtil {
    public static String toSlug(String input) {
        if (input == null) return "";

        // 1. Đưa về chữ thường
        String slug = input.toLowerCase(Locale.ROOT);

        slug = Normalizer.normalize(slug, Normalizer.Form.NFD);
        slug = slug.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        // 3. Thay đ bằng d
        slug = slug.replace("đ", "d");

        // 4. Bỏ ký tự đặc biệt
        slug = slug.replaceAll("[^a-z0-9\\s-]", "");

        // 5. Thay khoảng trắng bằng -
        slug = slug.replaceAll("\\s+", "-");

        // 6. Xóa - ở đầu/cuối
        slug = slug.replaceAll("^-+|-+$", "");

        return slug;
    }

    public static String timeAgo(Timestamp publishedAt) {
        if (publishedAt == null) return "";

        long seconds = Duration.between(
                publishedAt.toInstant(),
                Instant.now()
        ).getSeconds();
        if (seconds < 60)
            return "Just now";

        long minutes = seconds / 60;
        if (minutes < 60)
            return minutes + " minute" + (minutes > 1 ? "s" : "") + " ago";

        long hours = minutes / 60;
        if (hours < 24)
            return hours + " hour" + (hours > 1 ? "s" : "") + " ago";

        long days = hours / 24;
        if (days < 7)
            return days + " day" + (days > 1 ? "s" : "") + " ago";

        long weeks = days / 7;
        if (weeks < 4)
            return weeks + " week" + (weeks > 1 ? "s" : "") + " ago";

        long months = days / 30;
        if (months < 12)
            return months + " month" + (months > 1 ? "s" : "") + " ago";

        long years = days / 365;
        return years + " year" + (years > 1 ? "s" : "") + " ago";
    }
}
