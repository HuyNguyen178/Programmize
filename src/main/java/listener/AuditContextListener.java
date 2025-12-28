package listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import service.AuditLogService;

@WebListener
public class AuditContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        AuditLogService service = AuditLogService.getInstance();

        String realPath = sce.getServletContext().getRealPath("/");
        if (realPath != null) {
            service.setLogDirectory(realPath);
        }

        System.out.println("AuditLogService initialized - logs at: " + service.getLogDirectory());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        AuditLogService.getInstance().shutdown();
        System.out.println("AuditLogService shutdown complete");
    }
}