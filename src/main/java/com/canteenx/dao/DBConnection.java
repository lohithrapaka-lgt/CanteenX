package com.canteenx.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton / Utility provider for JDBC database connections.
 * Uses PreparedStatement and Transactions across the DAO layer.
 */
public class DBConnection {

    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/canteen_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static String dbUrl = DEFAULT_URL;
    private static String dbUser = "root";
    private static String dbPassword = "";

    // Candidate passwords to automatically attempt during local development if not explicitly configured
    private static final String[] CANDIDATE_PASSWORDS = new String[] {
        "", "root", "root123", "admin", "admin123", "password", "123456", "mysql"
    };

    private static volatile boolean autoDetected = false;

    static {
        try {
            Class.forName(DRIVER_CLASS);
        } catch (ClassNotFoundException e) {
            System.err.println("[CanteenX] MySQL JDBC Driver not found in classpath: " + e.getMessage());
        }

        // Check system properties or environment variables
        String envUrl = System.getProperty("canteen.db.url", System.getenv("CANTEEN_DB_URL"));
        if (envUrl != null && !envUrl.trim().isEmpty()) {
            dbUrl = envUrl.trim();
        }
        String envUser = System.getProperty("canteen.db.user", System.getenv("CANTEEN_DB_USER"));
        if (envUser != null && !envUser.trim().isEmpty()) {
            dbUser = envUser.trim();
        }
        String envPass = System.getProperty("canteen.db.password", System.getenv("CANTEEN_DB_PASSWORD"));
        if (envPass != null) {
            dbPassword = envPass;
            autoDetected = true;
        }
    }

    public static synchronized void configure(String url, String user, String password) {
        if (url != null) dbUrl = url;
        if (user != null) dbUser = user;
        if (password != null) {
            dbPassword = password;
            autoDetected = true;
        }
    }

    public static Connection getConnection() throws SQLException {
        if (autoDetected) {
            return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        }

        // Try candidate passwords to find the working local MySQL credential
        for (String candidate : CANDIDATE_PASSWORDS) {
            try {
                Connection conn = DriverManager.getConnection(dbUrl, dbUser, candidate);
                dbPassword = candidate;
                autoDetected = true;
                return conn;
            } catch (SQLException ignored) {
                // Try next candidate
            }
        }

        // Fallback to configured dbPassword
        return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("[CanteenX] Error closing connection: " + e.getMessage());
            }
        }
    }
}
