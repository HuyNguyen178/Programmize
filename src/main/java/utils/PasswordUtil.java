package utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {
    public static String hash(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean check(String password, String hashed) {
        return BCrypt.checkpw(password, hashed);
    }

    public static boolean isValidPassword(String password) {
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).{8,}$";
        return password != null && password.matches(regex);
    }
}
