package service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class RateLimiterService {

    private static RateLimiterService instance;

    private final ConcurrentHashMap<String, AttemptInfo> loginAttempts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, RequestInfo> requestCounts = new ConcurrentHashMap<>();

    private static final int MAX_LOGIN_ATTEMPTS = 10;
    private static final long LOCKOUT_DURATION_MINUTES = 15;
    private static final int MAX_REQUESTS_PER_MINUTE = 100;

    private RateLimiterService() {}

    public static synchronized RateLimiterService getInstance() {
        if (instance == null) {
            instance = new RateLimiterService();
        }
        return instance;
    }

    public boolean isLocked(String ip) {
        AttemptInfo info = loginAttempts.get(ip);
        if (info == null) {
            return false;
        }

        if (info.isLocked()) {
            long lockoutEndTime = info.getLastAttemptTime() +
                TimeUnit.MINUTES.toMillis(LOCKOUT_DURATION_MINUTES);
            if (System.currentTimeMillis() > lockoutEndTime) {
                loginAttempts.remove(ip);
                return false;
            }
            return true;
        }
        return false;
    }

    public void recordFailedLogin(String ip, String username) {
        loginAttempts.compute(ip, (key, info) -> {
            if (info == null) {
                info = new AttemptInfo();
            }
            info.incrementAttempts();
            info.setLastAttemptTime(System.currentTimeMillis());
            info.setUsername(username);

            if (info.getAttempts() >= MAX_LOGIN_ATTEMPTS) {
                info.setLocked(true);
            }
            return info;
        });
    }

    public void recordSuccessfulLogin(String ip) {
        loginAttempts.remove(ip);
    }

    public int getRemainingAttempts(String ip) {
        AttemptInfo info = loginAttempts.get(ip);
        if (info == null) {
            return MAX_LOGIN_ATTEMPTS;
        }
        return Math.max(0, MAX_LOGIN_ATTEMPTS - info.getAttempts());
    }

    public long getRemainingLockoutMinutes(String ip) {
        AttemptInfo info = loginAttempts.get(ip);
        if (info == null || !info.isLocked()) {
            return 0;
        }
        long lockoutEndTime = info.getLastAttemptTime() +
            TimeUnit.MINUTES.toMillis(LOCKOUT_DURATION_MINUTES);
        long remaining = lockoutEndTime - System.currentTimeMillis();
        return Math.max(0, TimeUnit.MILLISECONDS.toMinutes(remaining) + 1);
    }

    public boolean isRateLimited(String ip) {
        long currentMinute = System.currentTimeMillis() / 60000;

        RequestInfo info = requestCounts.compute(ip, (key, existing) -> {
            if (existing == null || existing.getMinute() != currentMinute) {
                return new RequestInfo(currentMinute, 1);
            }
            existing.incrementCount();
            return existing;
        });

        return info.getCount() > MAX_REQUESTS_PER_MINUTE;
    }

    private static class AttemptInfo {
        private int attempts = 0;
        private long lastAttemptTime;
        private boolean locked = false;
        private String username;

        public int getAttempts() { return attempts; }
        public void incrementAttempts() { attempts++; }
        public long getLastAttemptTime() { return lastAttemptTime; }
        public void setLastAttemptTime(long time) { lastAttemptTime = time; }
        public boolean isLocked() { return locked; }
        public void setLocked(boolean locked) { this.locked = locked; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
    }

    private static class RequestInfo {
        private final long minute;
        private int count;

        public RequestInfo(long minute, int count) {
            this.minute = minute;
            this.count = count;
        }

        public long getMinute() { return minute; }
        public int getCount() { return count; }
        public void incrementCount() { count++; }
    }
}