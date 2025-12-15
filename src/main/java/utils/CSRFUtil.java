package utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.security.SecureRandom;
import java.util.Base64;

public class CSRFUtil {
    private static final String CSRF_TOKEN_ATTRIBUTE = "csrfToken";
    private static final int TOKEN_LENGTH = 32;

    public static String generateToken(HttpSession session) {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[TOKEN_LENGTH];
        random.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        session.setAttribute(CSRF_TOKEN_ATTRIBUTE, token);
        return token;
    }

    public static String getToken(HttpSession session) {
        String token = (String) session.getAttribute(CSRF_TOKEN_ATTRIBUTE);
        if (token == null) {
            token = generateToken(session);
        }
        return token;
    }

    public static boolean validateToken(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }

        String sessionToken = (String) session.getAttribute(CSRF_TOKEN_ATTRIBUTE);
        String requestToken = request.getParameter("csrfToken");

        if (requestToken == null) {
            requestToken = request.getHeader("X-CSRF-Token");
        }

        return sessionToken != null && sessionToken.equals(requestToken);
    }

    public static void clearToken(HttpSession session) {
        if (session != null) {
            session.removeAttribute(CSRF_TOKEN_ATTRIBUTE);
        }
    }
}