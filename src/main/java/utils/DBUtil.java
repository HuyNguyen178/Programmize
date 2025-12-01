package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {

    private static final String HOST = System.getenv("DB_HOST");
    private static final String PORT = System.getenv("DB_PORT");
    private static final String DB_NAME = System.getenv("DB_NAME");
    private static final String USER = System.getenv("DB_USER");
    private static final String PASSWORD = System.getenv("DB_PASSWORD");
    private static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DB_NAME + "?sslMode=REQUIRED" + "&useSSL=true" + "&allowPublicKeyRetrieval=true";

    static {
        try {
            // Nạp Driver MySQL (Đảm bảo project của bạn đã có thư viện mysql-connector-java)
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Không tìm thấy Driver MySQL! Hãy kiểm tra lại thư viện.");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        System.out.println("Connecting to: " + URL);
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
