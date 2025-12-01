package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {

    private static final String HOST = "programmize-doanminhcuong0702-0a97.g.aivencloud.com";
    private static final String PORT = "11985";
    private static final String DB_NAME = "programmize";
    private static final String USER = "avnadmin";
    private static final String PASSWORD = "AVNS_Qs2V0mmVj8lWkpgzDhJ";
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
