package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = env("DB_URL",
            "jdbc:mysql://localhost:3306/student_management_system");

    private static final String USER = env("DB_USER", "root");

    private static final String PASSWORD = System.getenv("DB_PASSWORD");

    private static String env(String name, String fallback) {
        String value = System.getenv(name);
        return (value == null || value.isBlank()) ? fallback : value;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}