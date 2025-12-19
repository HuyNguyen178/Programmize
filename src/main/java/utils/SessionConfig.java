package utils;

public class SessionConfig {
    // Session attribute names
    public static final String ATTR_LOGIN_USER = "loginUser";
    public static final String ATTR_CREATION_TIME = "sessionCreationTime";
    public static final String ATTR_LAST_ROTATION = "lastRotationTime";
    public static final String ATTR_LAST_ACCESS = "lastAccessTime";

    // Session configuration
    public static final int SESSION_TIMEOUT_MINUTES = 30;
    public static final int SESSION_ROTATION_MINUTES = 15;

    // Cookie configuration
    public static final String SESSION_COOKIE_NAME = "JSESSIONID";
    public static final boolean COOKIE_HTTP_ONLY = true;
    public static final boolean COOKIE_SECURE = false; // Set to true in production with HTTPS
    public static final int COOKIE_MAX_AGE = SESSION_TIMEOUT_MINUTES * 60;

    // Remember me configuration
    public static final String REMEMBER_COOKIE_NAME = "rememberToken";
    public static final int REMEMBER_ME_DAYS = 30;
    public static final int REMEMBER_ME_MAX_AGE = REMEMBER_ME_DAYS * 24 * 60 * 60;

    private SessionConfig() {
        // Prevent instantiation
    }
}