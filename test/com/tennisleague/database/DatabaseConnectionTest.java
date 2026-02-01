package com.tennisleague.database;

import org.junit.jupiter.api.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DatabaseConnection class.
 * Tests connection establishment, failure scenarios, and resource cleanup.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DatabaseConnectionTest {
    
    private static final String CONFIG_FILE = "config.properties";
    private static final String BACKUP_FILE = "config.properties.backup";
    private static final String TEST_CONFIG_FILE = "config.properties.test";
    
    @BeforeAll
    static void backupConfig() throws IOException {
        // Backup the original config file
        if (Files.exists(Paths.get(CONFIG_FILE))) {
            Files.copy(Paths.get(CONFIG_FILE), Paths.get(BACKUP_FILE), 
                      StandardCopyOption.REPLACE_EXISTING);
        }
    }
    
    @AfterAll
    static void restoreConfig() throws IOException {
        // Restore the original config file
        if (Files.exists(Paths.get(BACKUP_FILE))) {
            Files.copy(Paths.get(BACKUP_FILE), Paths.get(CONFIG_FILE), 
                      StandardCopyOption.REPLACE_EXISTING);
            Files.delete(Paths.get(BACKUP_FILE));
        }
    }
    
    /**
     * Test successful connection with valid credentials.
     * This test verifies that a connection can be established when valid
     * database credentials are provided in the config file.
     */
    @Test
    @Order(1)
    @DisplayName("Test successful connection with valid credentials")
    void testSuccessfulConnection() {
        Connection conn = null;
        try {
            // Attempt to get a connection
            conn = DatabaseConnection.getConnection();
            
            // Verify connection is not null
            assertNotNull(conn, "Connection should not be null");
            
            // Verify connection is valid
            assertTrue(conn.isValid(5), "Connection should be valid");
            
            // Verify connection is not closed
            assertFalse(conn.isClosed(), "Connection should not be closed");
            
        } catch (SQLException e) {
            fail("Should successfully connect with valid credentials: " + e.getMessage());
        } finally {
            // Clean up
            DatabaseConnection.closeResources(conn);
        }
    }
    
    /**
     * Test connection failure with invalid credentials.
     * This test creates a temporary config file with invalid credentials
     * and verifies that an appropriate SQLException is thrown.
     * 
     * Note: This test requires restarting the JVM to reload the static configuration,
     * so we test the error message format instead.
     */
    @Test
    @Order(2)
    @DisplayName("Test connection failure with invalid credentials")
    void testConnectionFailureWithInvalidCredentials() {
        // This test verifies that invalid credentials result in a SQLException
        // We cannot easily test this without reloading the class, but we can
        // verify the error handling logic by attempting a connection with
        // intentionally wrong credentials directly
        
        try {
            // Attempt connection with invalid credentials
            java.sql.DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/TennisLeague",
                "invalid_user",
                "invalid_password"
            );
            fail("Should throw SQLException with invalid credentials");
        } catch (SQLException e) {
            // Verify that an SQLException is thrown
            assertNotNull(e, "SQLException should be thrown");
            assertTrue(e.getErrorCode() == 1045 || e.getMessage().contains("Access denied"),
                      "Error should indicate authentication failure");
        }
    }
    
    /**
     * Test connection failure with invalid database URL.
     * This test verifies that attempting to connect to a non-existent
     * database server results in an appropriate SQLException.
     */
    @Test
    @Order(3)
    @DisplayName("Test connection failure with invalid database URL")
    void testConnectionFailureWithInvalidURL() {
        try {
            // Attempt connection to non-existent server
            java.sql.DriverManager.getConnection(
                "jdbc:mysql://nonexistent-host:3306/TennisLeague",
                "root",
                "password"
            );
            fail("Should throw SQLException with invalid URL");
        } catch (SQLException e) {
            // Verify that an SQLException is thrown
            assertNotNull(e, "SQLException should be thrown");
            assertTrue(e.getMessage().contains("Communications link failure") ||
                      e.getMessage().contains("Unknown host"),
                      "Error should indicate connection failure");
        }
    }
    
    /**
     * Test resource cleanup with all resources.
     * This test verifies that closeResources properly closes
     * Connection, Statement, and ResultSet without throwing exceptions.
     */
    @Test
    @Order(4)
    @DisplayName("Test resource cleanup with all resources")
    void testResourceCleanupAllResources() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            // Create resources
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT 1");
            
            // Verify resources are open
            assertFalse(conn.isClosed(), "Connection should be open");
            assertFalse(stmt.isClosed(), "Statement should be open");
            assertFalse(rs.isClosed(), "ResultSet should be open");
            
            // Close resources
            DatabaseConnection.closeResources(conn, stmt, rs);
            
            // Verify resources are closed
            assertTrue(conn.isClosed(), "Connection should be closed");
            assertTrue(stmt.isClosed(), "Statement should be closed");
            assertTrue(rs.isClosed(), "ResultSet should be closed");
            
        } catch (SQLException e) {
            fail("Resource cleanup should not throw exception: " + e.getMessage());
        }
    }
    
    /**
     * Test resource cleanup with Connection and Statement only.
     * This test verifies the overloaded closeResources method
     * that accepts Connection and Statement.
     */
    @Test
    @Order(5)
    @DisplayName("Test resource cleanup with Connection and Statement")
    void testResourceCleanupConnectionAndStatement() {
        Connection conn = null;
        Statement stmt = null;
        
        try {
            // Create resources
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            
            // Verify resources are open
            assertFalse(conn.isClosed(), "Connection should be open");
            assertFalse(stmt.isClosed(), "Statement should be open");
            
            // Close resources
            DatabaseConnection.closeResources(conn, stmt);
            
            // Verify resources are closed
            assertTrue(conn.isClosed(), "Connection should be closed");
            assertTrue(stmt.isClosed(), "Statement should be closed");
            
        } catch (SQLException e) {
            fail("Resource cleanup should not throw exception: " + e.getMessage());
        }
    }
    
    /**
     * Test resource cleanup with Connection only.
     * This test verifies the overloaded closeResources method
     * that accepts only a Connection.
     */
    @Test
    @Order(6)
    @DisplayName("Test resource cleanup with Connection only")
    void testResourceCleanupConnectionOnly() {
        Connection conn = null;
        
        try {
            // Create connection
            conn = DatabaseConnection.getConnection();
            
            // Verify connection is open
            assertFalse(conn.isClosed(), "Connection should be open");
            
            // Close connection
            DatabaseConnection.closeResources(conn);
            
            // Verify connection is closed
            assertTrue(conn.isClosed(), "Connection should be closed");
            
        } catch (SQLException e) {
            fail("Resource cleanup should not throw exception: " + e.getMessage());
        }
    }
    
    /**
     * Test resource cleanup with null resources.
     * This test verifies that closeResources handles null values gracefully
     * without throwing NullPointerException.
     */
    @Test
    @Order(7)
    @DisplayName("Test resource cleanup with null resources")
    void testResourceCleanupWithNulls() {
        // This should not throw any exception
        assertDoesNotThrow(() -> {
            DatabaseConnection.closeResources(null, null, null);
        }, "closeResources should handle null values gracefully");
        
        assertDoesNotThrow(() -> {
            DatabaseConnection.closeResources(null, null);
        }, "closeResources should handle null values gracefully");
        
        assertDoesNotThrow(() -> {
            DatabaseConnection.closeResources(null);
        }, "closeResources should handle null values gracefully");
    }
    
    /**
     * Test resource cleanup with mixed null and non-null resources.
     * This test verifies that closeResources properly handles scenarios
     * where some resources are null and others are not.
     */
    @Test
    @Order(8)
    @DisplayName("Test resource cleanup with mixed null and non-null resources")
    void testResourceCleanupWithMixedNulls() {
        Connection conn1 = null;
        Statement stmt1 = null;
        Connection conn2 = null;
        Statement stmt2 = null;
        
        try {
            // Create only connection
            conn1 = DatabaseConnection.getConnection();
            final Connection finalConn1 = conn1;
            
            // Close with null statement and resultset
            assertDoesNotThrow(() -> {
                DatabaseConnection.closeResources(finalConn1, null, null);
            }, "closeResources should handle mixed null values");
            
            // Verify connection is closed
            assertTrue(conn1.isClosed(), "Connection should be closed");
            
            // Create connection and statement
            conn2 = DatabaseConnection.getConnection();
            stmt2 = conn2.createStatement();
            final Connection finalConn2 = conn2;
            final Statement finalStmt2 = stmt2;
            
            // Close with null resultset
            assertDoesNotThrow(() -> {
                DatabaseConnection.closeResources(finalConn2, finalStmt2, null);
            }, "closeResources should handle null ResultSet");
            
            // Verify resources are closed
            assertTrue(conn2.isClosed(), "Connection should be closed");
            assertTrue(stmt2.isClosed(), "Statement should be closed");
            
        } catch (SQLException e) {
            fail("Resource cleanup should not throw exception: " + e.getMessage());
        }
    }
    
    /**
     * Test multiple sequential connections.
     * This test verifies that multiple connections can be obtained
     * and closed without resource leaks.
     */
    @Test
    @Order(9)
    @DisplayName("Test multiple sequential connections")
    void testMultipleSequentialConnections() {
        for (int i = 0; i < 5; i++) {
            Connection conn = null;
            try {
                conn = DatabaseConnection.getConnection();
                assertNotNull(conn, "Connection " + i + " should not be null");
                assertTrue(conn.isValid(5), "Connection " + i + " should be valid");
            } catch (SQLException e) {
                fail("Should successfully create connection " + i + ": " + e.getMessage());
            } finally {
                DatabaseConnection.closeResources(conn);
            }
        }
    }
    
    /**
     * Test connection validity after obtaining.
     * This test verifies that a newly obtained connection is valid
     * and can execute queries.
     */
    @Test
    @Order(10)
    @DisplayName("Test connection validity and query execution")
    void testConnectionValidityAndQueryExecution() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            
            // Verify connection is valid
            assertTrue(conn.isValid(5), "Connection should be valid");
            
            // Execute a simple query
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT 1 AS test_value");
            
            // Verify query executed successfully
            assertTrue(rs.next(), "ResultSet should have at least one row");
            assertEquals(1, rs.getInt("test_value"), "Query should return correct value");
            
        } catch (SQLException e) {
            fail("Connection should be able to execute queries: " + e.getMessage());
        } finally {
            DatabaseConnection.closeResources(conn, stmt, rs);
        }
    }
}
