package utils;

import java.util.Arrays;
import java.util.Map;

public class FileUtil {
    public static String getValue(Map<String, Integer> indexMap, String[] data, String column) {
        Integer index = indexMap.get(column);
        if (index == null || index >= data.length) {
            return null;
        }

        String value = data[index].trim();
        return value.isEmpty() ? null : value;
    }

    public static String[] parseClassCategories(Map<String, Integer> indexMap, String[] data) {
        String raw = getValue(indexMap, data, "class_categories");
        if (raw == null) {
            return null;
        }

        return Arrays.stream(raw.split("\\|")).map(String::trim).filter(s -> !s.isEmpty()).toArray(String[]::new);
    }

    public static String[] parseCourseCategories(Map<String, Integer> indexMap, String[] data) {
        String raw = getValue(indexMap, data, "course_categories");
        if (raw == null) {
            return null;
        }

        return Arrays.stream(raw.split("\\|")).map(String::trim).filter(s -> !s.isEmpty()).toArray(String[]::new);
    }
}
