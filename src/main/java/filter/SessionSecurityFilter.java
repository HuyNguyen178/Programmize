package filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import utils.SessionConfig;

import java.io.IOException;
import java.util.Enumeration;

public class SessionSecurityFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        // Configure session security
        configureSessionSecurity(req, res);

        if (session != null) {
            // Initialize session tracking if needed
            if (session.getAttribute(SessionConfig.ATTR_CREATION_TIME) == null) {
                session.setAttribute(SessionConfig.ATTR_CREATION_TIME, System.currentTimeMillis());
                session.setAttribute(SessionConfig.ATTR_LAST_ROTATION, System.currentTimeMillis());
            }

            // Check session timeout
            if (isSessionExpired(session)) {
                invalidateSession(session);
                redirectToLogin(req, res);
                return;
            }

            // Rotate session ID if needed
            rotateSessionIfNeeded(req, session);
        }

        chain.doFilter(request, response);
    }

    private void configureSessionSecurity(HttpServletRequest req, HttpServletResponse res) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            // Set session timeout
            session.setMaxInactiveInterval(SessionConfig.SESSION_TIMEOUT_MINUTES * 60);
        }
    }

    private boolean isSessionExpired(HttpSession session) {
        Object creationTimeObj = session.getAttribute(SessionConfig.ATTR_CREATION_TIME);
        if (creationTimeObj == null) {
            return false;
        }

        long creationTime = (long) creationTimeObj;
        long currentTime = System.currentTimeMillis();
        long timeoutMillis = SessionConfig.SESSION_TIMEOUT_MINUTES * 60 * 1000;

        return (currentTime - creationTime) > timeoutMillis;
    }

    private void rotateSessionIfNeeded(HttpServletRequest req, HttpSession oldSession) {
        Long lastRotation = (Long) oldSession.getAttribute(SessionConfig.ATTR_LAST_ROTATION);
        long currentTime = System.currentTimeMillis();
        long rotationInterval = SessionConfig.SESSION_ROTATION_MINUTES * 60 * 1000;

        if (lastRotation == null || (currentTime - lastRotation) > rotationInterval) {
            // Lưu tất cả attributes từ session cũ
            java.util.Map<String, Object> oldAttributes = new java.util.HashMap<>();
            Enumeration<String> attributeNames = oldSession.getAttributeNames();
            while (attributeNames.hasMoreElements()) {
                String name = attributeNames.nextElement();
                oldAttributes.put(name, oldSession.getAttribute(name));
            }

            // Invalidate old session
            oldSession.invalidate();

            // Tạo session mới với ID mới
            HttpSession newSession = req.getSession(true);

            // Restore tất cả attributes
            for (java.util.Map.Entry<String, Object> entry : oldAttributes.entrySet()) {
                newSession.setAttribute(entry.getKey(), entry.getValue());
            }

            // Update both rotation time and creation time
            newSession.setAttribute(SessionConfig.ATTR_LAST_ROTATION, currentTime);
            newSession.setAttribute(SessionConfig.ATTR_CREATION_TIME, currentTime);
            newSession.setMaxInactiveInterval(SessionConfig.SESSION_TIMEOUT_MINUTES * 60);
        }
    }

    private void invalidateSession(HttpSession session) {
        session.invalidate();
    }

    private void redirectToLogin(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String contextPath = req.getContextPath();
        res.sendRedirect(contextPath + "/login?timeout=true");
    }
}