package service;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class AuditLogService {

    private static AuditLogService instance;
    private String logDirectory;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private final BlockingQueue<AuditEntry> logQueue = new LinkedBlockingQueue<>();
    private final Thread writerThread;
    private volatile boolean running = true;

    public enum ActionType {
        LOGIN_SUCCESS,
        LOGIN_FAILED,
        LOGOUT,
        SESSION_EXPIRED,
        PASSWORD_CHANGE,
        PASSWORD_RESET_REQUEST,
        USER_CREATED,
        USER_UPDATED,
        USER_DELETED,
        USER_ACTIVATED,
        USER_DEACTIVATED,
        ROLE_CHANGED,
        SETTINGS_CHANGED,
        DATA_EXPORT,
        DATA_IMPORT,
        BULK_OPERATION,
        ACCOUNT_LOCKED,
        RATE_LIMIT_EXCEEDED,
        UNAUTHORIZED_ACCESS,
        SUSPICIOUS_ACTIVITY
    }

    public enum Severity {
        INFO,
        WARNING,
        CRITICAL
    }

    private AuditLogService() {
        // Default fallback path
        logDirectory = System.getProperty("user.dir") + File.separator + "logs" + File.separator + "audit";
        createLogDirectory();

        writerThread = new Thread(this::processLogQueue, "AuditLogWriter");
        writerThread.setDaemon(true);
        writerThread.start();

        System.out.println("Audit logs initialized at: " + logDirectory);
    }

    public static synchronized AuditLogService getInstance() {
        if (instance == null) {
            instance = new AuditLogService();
        }
        return instance;
    }

    public void setLogDirectory(String webappRealPath) {
        if (webappRealPath != null) {
            this.logDirectory = webappRealPath + File.separator + "logs" + File.separator + "audit";
            createLogDirectory();
            System.out.println("Audit log directory set to: " + logDirectory);
        }
    }

    public String getLogDirectory() {
        return logDirectory;
    }

    private void createLogDirectory() {
        try {
            Files.createDirectories(Paths.get(logDirectory));
        } catch (IOException e) {
            System.err.println("Failed to create audit log directory: " + e.getMessage());
        }
    }

    public void log(ActionType action, String userId, String username,
                    String ipAddress, String details) {
        log(action, userId, username, ipAddress, details, Severity.INFO);
    }

    public void log(ActionType action, String userId, String username,
                    String ipAddress, String details, Severity severity) {
        AuditEntry entry = new AuditEntry(
                LocalDateTime.now(),
                action,
                severity,
                userId,
                username,
                ipAddress,
                details
        );
        logQueue.offer(entry);
    }

    public void logLoginSuccess(String userId, String username, String ipAddress) {
        log(ActionType.LOGIN_SUCCESS, userId, username, ipAddress,
                "User logged in successfully", Severity.INFO);
    }

    public void logLoginFailed(String username, String ipAddress, String reason) {
        log(ActionType.LOGIN_FAILED, null, username, ipAddress,
                "Login failed: " + reason, Severity.WARNING);
    }

    public void logLogout(String userId, String username, String ipAddress) {
        log(ActionType.LOGOUT, userId, username, ipAddress,
                "User logged out", Severity.INFO);
    }

    public void logAccountLocked(String username, String ipAddress, int attempts) {
        log(ActionType.ACCOUNT_LOCKED, null, username, ipAddress,
                "Account locked after " + attempts + " failed attempts", Severity.CRITICAL);
    }

    public void logUnauthorizedAccess(String userId, String username,
                                      String ipAddress, String resource) {
        log(ActionType.UNAUTHORIZED_ACCESS, userId, username, ipAddress,
                "Unauthorized access attempt to: " + resource, Severity.CRITICAL);
    }

    public void logUserCreated(String adminId, String adminUsername,
                               String ipAddress, String newUserId, String newUsername) {
        log(ActionType.USER_CREATED, adminId, adminUsername, ipAddress,
                "Created user: " + newUsername + " (ID: " + newUserId + ")", Severity.INFO);
    }

    public void logUserUpdated(String adminId, String adminUsername,
                               String ipAddress, String targetUserId, String changes) {
        log(ActionType.USER_UPDATED, adminId, adminUsername, ipAddress,
                "Updated user ID " + targetUserId + ": " + changes, Severity.INFO);
    }

    public void logUserDeleted(String adminId, String adminUsername,
                               String ipAddress, String deletedUserId, String deletedUsername) {
        log(ActionType.USER_DELETED, adminId, adminUsername, ipAddress,
                "Deleted user: " + deletedUsername + " (ID: " + deletedUserId + ")", Severity.WARNING);
    }

    public void logRoleChanged(String adminId, String adminUsername, String ipAddress,
                               String targetUserId, String oldRole, String newRole) {
        log(ActionType.ROLE_CHANGED, adminId, adminUsername, ipAddress,
                "Changed role for user ID " + targetUserId + ": " + oldRole + " -> " + newRole,
                Severity.WARNING);
    }

    public void logPasswordChange(String userId, String username, String ipAddress) {
        log(ActionType.PASSWORD_CHANGE, userId, username, ipAddress,
                "Password changed", Severity.INFO);
    }

    public void logRateLimitExceeded(String ipAddress) {
        log(ActionType.RATE_LIMIT_EXCEEDED, null, null, ipAddress,
                "Rate limit exceeded", Severity.WARNING);
    }

    public void logSettingsChanged(String adminId, String adminUsername,
                                   String ipAddress, String settingName, String oldValue, String newValue) {
        log(ActionType.SETTINGS_CHANGED, adminId, adminUsername, ipAddress,
                "Setting '" + settingName + "' changed from '" + oldValue + "' to '" + newValue + "'",
                Severity.WARNING);
    }

    private void processLogQueue() {
        while (running || !logQueue.isEmpty()) {
            try {
                AuditEntry entry = logQueue.take();
                writeToFile(entry);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void writeToFile(AuditEntry entry) {
        String fileName = logDirectory + File.separator + "audit_" + entry.timestamp.format(DATE_FORMAT) + ".log";
        String logLine = formatLogEntry(entry);

        try (FileWriter fw = new FileWriter(fileName, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(logLine);
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Failed to write audit log: " + e.getMessage());
        }
    }

    private String formatLogEntry(AuditEntry entry) {
        return String.format("[%s] [%s] [%s] IP=%s | User=%s (ID=%s) | %s",
                entry.timestamp.format(TIMESTAMP_FORMAT),
                entry.severity,
                entry.action,
                entry.ipAddress != null ? entry.ipAddress : "N/A",
                entry.username != null ? entry.username : "N/A",
                entry.userId != null ? entry.userId : "N/A",
                entry.details
        );
    }

    public void shutdown() {
        running = false;
        writerThread.interrupt();
        try {
            writerThread.join(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static class AuditEntry {
        final LocalDateTime timestamp;
        final ActionType action;
        final Severity severity;
        final String userId;
        final String username;
        final String ipAddress;
        final String details;

        AuditEntry(LocalDateTime timestamp, ActionType action, Severity severity,
                   String userId, String username, String ipAddress, String details) {
            this.timestamp = timestamp;
            this.action = action;
            this.severity = severity;
            this.userId = userId;
            this.username = username;
            this.ipAddress = ipAddress;
            this.details = details;
        }
    }
}