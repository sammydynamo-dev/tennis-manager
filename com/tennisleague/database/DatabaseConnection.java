package com.tennisleague.database;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * DatabaseConnection manages database connections for the Tennis League Management System.
 * This class provides static methods to obtain database connections and close resources properly.
 */
public class DatabaseConnection {
    
    private static final String CONFIG_FILE = "config.properties";
    private static String dbUrl;
    private static String dbUser;
    private static String dbPassword;
    
    // Static initializer to load configuration
    static {
        loadConfiguration();
    }
    
    /**
     * Loads database configuration from config.properties file.
     * This method is called once when the class is first loaded.
     */
    private static void loadConfiguration() {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            props.load(fis);
            dbUrl = props.getProperty("db.url");
            dbUser = props.getProperty("db.user");
            dbPassword = props.getProperty("db.password");
            
            // Validate that required properties are present
            if (dbUrl == null || dbUrl.trim().isEmpty()) {
                throw new IllegalStateException("Database URL (db.url) is not configured in " + CONFIG_FILE);
            }
            if (dbUser == null || dbUser.trim().isEmpty()) {
                throw new IllegalStateException("Database user (db.user) is not configured in " + CONFIG_FILE);
            }
            if (dbPassword == null) {
                throw new IllegalStateException("Database password (db.password) is not configured in " + CONFIG_FILE);
            }
            
        } catch (IOException e) {
            throw new IllegalStateException(
                "Failed to load database configuration from " + CONFIG_FILE + ". " +
                "Please ensure the file exists and is readable. Error: " + e.getMessage(), e);
        }
    }
    
    /**
     * Establishes and returns a connection to the database.
     * 
     * @return A Connection object to the database
     * @throws SQLException if a database access error occurs or the connection cannot be established
     */
    public static Connection getConnection() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            return conn;
        } catch (SQLException e) {
            // Provide descriptive error messages based on the type of SQL error
            String errorMessage;
            int errorCode = e.getErrorCode();
            
            // MySQL error codes
            if (errorCode == 1045) {
                errorMessage = "Authentication failed: Invalid database username or password. " +
                              "Please check your credentials in " + CONFIG_FILE;
            } else if (errorCode == 0 && e.getMessage().contains("Communications link failure")) {
                errorMessage = "Unable to connect to database server at " + dbUrl + ". " +
                              "Please ensure the MySQL server is running and accessible.";
            } else if (e.getMessage().contains("Unknown database")) {
                errorMessage = "Database does not exist. Please create the TennisLeague database " +
                              "or check the database name in " + CONFIG_FILE;
            } else {
                errorMessage = "Failed to establish database connection: " + e.getMessage();
            }
            
            throw new SQLException(errorMessage, e.getSQLState(), e.getErrorCode(), e);
        }
    }
    
    /**
     * Closes database resources in the proper order.
     * This method safely closes ResultSet, Statement, and Connection objects,
     * handling null values and suppressing exceptions to ensure all resources are closed.
     * 
     * @param conn The Connection to close (can be null)
     * @param stmt The Statement to close (can be null)
     * @param rs The ResultSet to close (can be null)
     */
    public static void closeResources(Connection conn, Statement stmt, ResultSet rs) {
        // Close ResultSet first
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                System.err.println("Warning: Failed to close ResultSet: " + e.getMessage());
            }
        }
        
        // Close Statement second
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                System.err.println("Warning: Failed to close Statement: " + e.getMessage());
            }
        }
        
        // Close Connection last
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Warning: Failed to close Connection: " + e.getMessage());
            }
        }
    }
    
    /**
     * Overloaded method to close Connection and Statement only.
     * 
     * @param conn The Connection to close (can be null)
     * @param stmt The Statement to close (can be null)
     */
    public static void closeResources(Connection conn, Statement stmt) {
        closeResources(conn, stmt, null);
    }
    
    /**
     * Overloaded method to close Connection only.
     * 
     * @param conn The Connection to close (can be null)
     */
    public static void closeResources(Connection conn) {
        closeResources(conn, null, null);
    }
}
