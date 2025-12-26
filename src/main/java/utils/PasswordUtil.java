package utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {
    public static String hash(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean check(String password, String hashed) {
        return BCrypt.checkpw(password, hashed);
    }
}
