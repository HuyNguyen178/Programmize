package listener;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import java.io.File;

import jakarta.servlet.annotation.WebListener;
import org.apache.log4j.PropertyConfigurator;

@WebListener
public class LogInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        ServletContext context = event.getServletContext();
        
        File tempDir = (File) context.getAttribute(ServletContext.TEMPDIR);
        System.out.println("tempDir=" + tempDir);
        
        String basePath = tempDir.getAbsolutePath();
        System.out.println("basePath=" + basePath);
        
        String logDir = basePath + File.separator + "logs"; 
        
        System.out.println("logDir=" + logDir);

        File dir = new File(logDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 4. THI?T L?P SYSTEM PROPERTY (PH?I TR??C KHI LOG4J KH?I T?O)
        // Thi?t l?p bi?n 'logfile' kh?p v?i bi?n trong file c?u h�nh
        System.setProperty("logfile", logDir);
        System.out.println("logDirectory=" + logDir);
        
        // ********** B??C KH?I T?O LOG4J **********
        
        // 5. T�m ???ng d?n ??n file c?u h�nh (th??ng n?m trong WEB-INF/classes)
        //String log4jConfigPath = context.getRealPath("/WEB-INF/classes/log4j.properties"); 
        String log4jConfigPath = context.getRealPath("/WEB-INF/classes/log4j.properties");
        System.out.println("log4jConfigPath=" + log4jConfigPath);
        if (log4jConfigPath != null) {
            // 6. C?u h�nh Log4j b?ng file ?� (DOMConfigurator cho XML)
            //DOMConfigurator.configure(log4jConfigPath);
            PropertyConfigurator.configure(log4jConfigPath);
            System.out.println("Log4j configured in: " + logDir + File.separator + "WebDemo.log");
        } else {
            System.err.println("Can't find Log4J config file - log4j.properties!");
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        // T?t Log4j khi ?ng d?ng d?ng (?? gi?i ph�ng t�i nguy�n/kh�a file)
        org.apache.log4j.LogManager.shutdown();
    }
}