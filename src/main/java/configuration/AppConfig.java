package configuration;

import java.util.ResourceBundle;

public class AppConfig {

    public static final String DB_URL;
    public static final String DB_USERNAME;
    public static final String DB_PASSWORD;
    public static final String DB_DRIVER;

    public static final String MAILER_HOST;
    public static final String MAILER_PORT;
    public static final String MAILER_USERNAME;
    public static final String MAILER_PASSWORD;
    public static final String MAILER_FROM_MAIL;
    public static final String MAILER_FROM_NAME;
    public static final String MAILER_REPLY2;

    public static final int PAGE_SIZE = 10;

    public static final String FORM_ERROR_MSG = "formErrorMsg";
    public static final String FORM_SUCCESS_MSG = "formSuccessMsg";
    public static final String FORM_EXISTING_MSG = "formExistingMsg";

    static {
        // Táº£i tá»‡p cáº¥u hÃ¬nh báº±ng ResourceBundle
        ResourceBundle bundle = ResourceBundle.getBundle("app");

        DB_URL = bundle.getString("db.url");
        DB_USERNAME = bundle.getString("db.username");
        DB_PASSWORD = bundle.getString("db.password");
        DB_DRIVER = bundle.getString("db.driver");

        MAILER_HOST = bundle.getString("mailer.host");
        MAILER_PORT = bundle.getString("mailer.port");
        MAILER_USERNAME = bundle.getString("mailer.username");
        MAILER_PASSWORD = bundle.getString("mailer.password");
        MAILER_FROM_MAIL = bundle.getString("mailer.fromMail");
        MAILER_FROM_NAME = bundle.getString("mailer.fromName");
        MAILER_REPLY2 = bundle.getString("mailer.reply2");
    }

    public static void main(String[] args) {
        // Sá»­ dá»¥ng cÃ¡c háº±ng sá»‘
        System.out.println("Database URL: " + DB_URL);
        System.out.println("Username: " + DB_USERNAME);
        System.out.println("Password: " + DB_PASSWORD);
    }
}