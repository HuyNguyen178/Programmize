package service;

import java.io.*;
import java.util.*;

public class FileValidationService {

    private static FileValidationService instance;

    // Allowed file types
    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(Arrays.asList(
        "jpg", "jpeg", "png", "gif", "webp",
        "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt", "csv",
        "mp4", "webm"
    ));

    private static final Set<String> ALLOWED_MIME_TYPES = new HashSet<>(Arrays.asList(
        "image/jpeg", "image/png", "image/gif", "image/webp",
        "application/pdf", "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "application/vnd.ms-excel",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        "text/plain", "text/csv",
        "video/mp4", "video/webm"
    ));

    // Magic bytes signatures
    private static final Map<String, byte[]> MAGIC_BYTES = new HashMap<>();

    static {
        MAGIC_BYTES.put("jpg", new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF});
        MAGIC_BYTES.put("jpeg", new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF});
        MAGIC_BYTES.put("png", new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47});
        MAGIC_BYTES.put("gif", new byte[]{0x47, 0x49, 0x46, 0x38});
        MAGIC_BYTES.put("pdf", new byte[]{0x25, 0x50, 0x44, 0x46});
        MAGIC_BYTES.put("docx", new byte[]{0x50, 0x4B, 0x03, 0x04});
        MAGIC_BYTES.put("xlsx", new byte[]{0x50, 0x4B, 0x03, 0x04});
        MAGIC_BYTES.put("pptx", new byte[]{0x50, 0x4B, 0x03, 0x04});
    }

    // Dangerous patterns
    private static final String[] DANGEROUS_PATTERNS = {
        "<script", "javascript:", "vbscript:", "onload=", "onerror=",
        "onclick=", "eval(", "<?php", "<%", "system(", "exec("
    };

    // Max file size: 50MB
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024;

    private FileValidationService() {}

    public static synchronized FileValidationService getInstance() {
        if (instance == null) {
            instance = new FileValidationService();
        }
        return instance;
    }

    /**
     * Main validation method - validates everything
     */
    public ValidationResult validate(String filename, String contentType,
                                     long fileSize, InputStream inputStream) {
        // 1. Check filename
        ValidationResult result = validateFilename(filename);
        if (!result.isValid()) return result;

        // 2. Check extension
        String extension = getExtension(filename).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            return new ValidationResult(false, "File type '" + extension + "' is not allowed");
        }

        // 3. Check MIME type
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
            return new ValidationResult(false, "Invalid content type: " + contentType);
        }

        // 4. Check file size
        if (fileSize > MAX_FILE_SIZE) {
            return new ValidationResult(false, "File exceeds maximum size of 50MB");
        }

        // 5. Check magic bytes and scan content
        try {
            byte[] fileBytes = readBytes(inputStream, (int) Math.min(fileSize, 8192));

            if (!validateMagicBytes(fileBytes, extension)) {
                return new ValidationResult(false, "File content does not match its extension");
            }

            if (containsMaliciousContent(fileBytes)) {
                return new ValidationResult(false, "File contains potentially malicious content");
            }
        } catch (IOException e) {
            return new ValidationResult(false, "Error reading file: " + e.getMessage());
        }

        return new ValidationResult(true, "File is valid");
    }

    /**
     * Validates filename for security issues
     */
    public ValidationResult validateFilename(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return new ValidationResult(false, "Filename is empty");
        }

        // Check for null bytes
        if (filename.contains("\0")) {
            return new ValidationResult(false, "Invalid filename");
        }

        // Check for path traversal
        if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            return new ValidationResult(false, "Invalid filename - path traversal detected");
        }

        // Check for dangerous characters
        if (filename.matches(".*[<>:\"|?*].*")) {
            return new ValidationResult(false, "Filename contains invalid characters");
        }

        // Check length
        if (filename.length() > 255) {
            return new ValidationResult(false, "Filename is too long");
        }

        return new ValidationResult(true, "Valid");
    }

    /**
     * Validates magic bytes match expected file type
     */
    private boolean validateMagicBytes(byte[] fileBytes, String extension) {
        byte[] expected = MAGIC_BYTES.get(extension);
        if (expected == null) {
            return true; // No signature to check
        }

        if (fileBytes.length < expected.length) {
            return false;
        }

        for (int i = 0; i < expected.length; i++) {
            if (fileBytes[i] != expected[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Scans for malicious content in file
     */
    private boolean containsMaliciousContent(byte[] fileBytes) {
        String content = new String(fileBytes).toLowerCase();

        for (String pattern : DANGEROUS_PATTERNS) {
            if (content.contains(pattern.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Reads bytes from input stream
     */
    private byte[] readBytes(InputStream is, int maxBytes) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int totalRead = 0;
        int read;

        while (totalRead < maxBytes && (read = is.read(data)) != -1) {
            buffer.write(data, 0, read);
            totalRead += read;
        }

        return buffer.toByteArray();
    }

    private String getExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        if (lastDot == -1 || lastDot == filename.length() - 1) {
            return "";
        }
        return filename.substring(lastDot + 1);
    }

    /**
     * Sanitizes filename for safe storage
     */
    public String sanitizeFilename(String filename) {
        String name = filename.replaceAll("[^a-zA-Z0-9._-]", "_");
        String ext = getExtension(name);
        String base = ext.isEmpty() ? name : name.substring(0, name.length() - ext.length() - 1);
        return base + "_" + System.currentTimeMillis() + (ext.isEmpty() ? "" : "." + ext);
    }

    // Result class
    public static class ValidationResult {
        private final boolean valid;
        private final String message;

        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        public boolean isValid() { return valid; }
        public String getMessage() { return message; }
    }
}