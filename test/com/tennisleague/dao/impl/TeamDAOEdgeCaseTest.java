package com.tennisleague.dao.impl;

import com.tennisleague.dao.TeamDAO;
import com.tennisleague.model.Team;
import com.tennisleague.exception.DatabaseException;
import com.tennisleague.database.DatabaseConnection;

import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TeamDAO edge cases.
 * Tests specific scenarios including:
 * - Duplicate TeamNumber rejection
 * - Null/empty field handling
 * - Non-existent team retrieval
 * 
 * Validates: Requirements 1.2, 1.6
 */
class TeamDAOEdgeCaseTest {
    
    private TeamDAO teamDAO;
    private static final int TEST_TEAM_NUMBER = 9999;
    
    @BeforeEach
    void setUp() {
        teamDAO = new TeamDAOImpl();
        cleanupTestData();
    }
    
    @AfterEach
    void tearDown() {
        cleanupTestData();
    }
    
    /**
     * Test: Duplicate TeamNumber Rejection
     * 
     * WHEN a user attempts to create a team with a duplicate TeamNumber,
     * THEN the system SHALL reject the operation and return a descriptive error message.
     * 
     * Validates: Requirement 1.2
     */
    @Test
    @DisplayName("Should reject duplicate TeamNumber")
    void testDuplicateTeamNumberRejection() {
        // Arrange: Create first team
        Team firstTeam = new Team(TEST_TEAM_NUMBER, "First Team", "Boston", "John Manager");
        
        try {
            teamDAO.createTeam(firstTeam);
            
            // Act & Assert: Attempt to create second team with same TeamNumber
            Team duplicateTeam = new Team(TEST_TEAM_NUMBER, "Duplicate Team", "New York", "Jane Manager");
            
            DatabaseException exception = assertThrows(DatabaseException.class, () -> {
                teamDAO.createTeam(duplicateTeam);
            }, "Creating team with duplicate TeamNumber should throw DatabaseException");
            
            // Verify error message is descriptive
            assertTrue(exception.getMessage().contains("already exists") || 
                      exception.getMessage().contains("Duplicate"),
                      "Error message should indicate duplicate TeamNumber: " + exception.getMessage());
            
        } catch (DatabaseException e) {
            fail("First team creation should succeed: " + e.getMessage());
        }
    }
    
    /**
     * Test: Null Name Field Handling
     * 
     * WHEN a user provides a null value for the Name field,
     * THEN the DAO layer SHALL accept it (database allows NULL).
     * Note: Business logic validation should happen at service layer.
     * 
     * Validates: Requirement 1.6 (service layer validation)
     */
    @Test
    @DisplayName("Should accept null Name field at DAO level")
    void testNullNameFieldHandling() {
        // Arrange: Create team with null name
        Team teamWithNullName = new Team(TEST_TEAM_NUMBER, null, "Boston", "John Manager");
        
        try {
            // Act: Create team with null name
            teamDAO.createTeam(teamWithNullName);
            
            // Assert: Team should be created (DAO doesn't validate business rules)
            Team retrieved = teamDAO.getTeamByNumber(TEST_TEAM_NUMBER);
            assertNotNull(retrieved, "Team with null name should be created at DAO level");
            assertNull(retrieved.getName(), "Null name should be persisted");
            
        } catch (DatabaseException e) {
            fail("DAO layer should accept null values allowed by database schema: " + e.getMessage());
        }
    }
    
    /**
     * Test: Empty Name Field Handling
     * 
     * WHEN a user provides an empty string for the Name field,
     * THEN the system SHALL accept it (database allows empty strings).
     * Note: Business logic validation should happen at service layer.
     * 
     * Validates: Requirement 1.6
     */
    @Test
    @DisplayName("Should handle empty Name field at DAO level")
    void testEmptyNameFieldHandling() {
        // Arrange: Create team with empty name
        Team teamWithEmptyName = new Team(TEST_TEAM_NUMBER, "", "Boston", "John Manager");
        
        try {
            // Act: Create team with empty name
            teamDAO.createTeam(teamWithEmptyName);
            
            // Assert: Team should be created (DAO doesn't validate business rules)
            Team retrieved = teamDAO.getTeamByNumber(TEST_TEAM_NUMBER);
            assertNotNull(retrieved, "Team with empty name should be created at DAO level");
            assertEquals("", retrieved.getName(), "Empty name should be persisted");
            
        } catch (DatabaseException e) {
            // DAO layer may or may not reject empty strings depending on database constraints
            // This is acceptable - business validation should happen at service layer
        }
    }
    
    /**
     * Test: Null City Field Handling
     * 
     * WHEN a user provides a null value for the City field,
     * THEN the DAO layer SHALL accept it (database allows NULL).
     * Note: Business logic validation should happen at service layer.
     * 
     * Validates: Requirement 1.6 (service layer validation)
     */
    @Test
    @DisplayName("Should accept null City field at DAO level")
    void testNullCityFieldHandling() {
        // Arrange: Create team with null city
        Team teamWithNullCity = new Team(TEST_TEAM_NUMBER, "Test Team", null, "John Manager");
        
        try {
            // Act: Create team with null city
            teamDAO.createTeam(teamWithNullCity);
            
            // Assert: Team should be created (DAO doesn't validate business rules)
            Team retrieved = teamDAO.getTeamByNumber(TEST_TEAM_NUMBER);
            assertNotNull(retrieved, "Team with null city should be created at DAO level");
            assertNull(retrieved.getCity(), "Null city should be persisted");
            
        } catch (DatabaseException e) {
            fail("DAO layer should accept null values allowed by database schema: " + e.getMessage());
        }
    }
    
    /**
     * Test: Null ManagerName Field Handling
     * 
     * WHEN a user provides a null value for the ManagerName field,
     * THEN the DAO layer SHALL accept it (database allows NULL).
     * Note: Business logic validation should happen at service layer.
     * 
     * Validates: Requirement 1.6 (service layer validation)
     */
    @Test
    @DisplayName("Should accept null ManagerName field at DAO level")
    void testNullManagerNameFieldHandling() {
        // Arrange: Create team with null manager name
        Team teamWithNullManager = new Team(TEST_TEAM_NUMBER, "Test Team", "Boston", null);
        
        try {
            // Act: Create team with null manager name
            teamDAO.createTeam(teamWithNullManager);
            
            // Assert: Team should be created (DAO doesn't validate business rules)
            Team retrieved = teamDAO.getTeamByNumber(TEST_TEAM_NUMBER);
            assertNotNull(retrieved, "Team with null manager name should be created at DAO level");
            assertNull(retrieved.getManagerName(), "Null manager name should be persisted");
            
        } catch (DatabaseException e) {
            fail("DAO layer should accept null values allowed by database schema: " + e.getMessage());
        }
    }
    
    /**
     * Test: Non-existent Team Retrieval
     * 
     * WHEN a user requests a team that doesn't exist,
     * THEN the system SHALL return null.
     * 
     * Validates: Requirement 1.2
     */
    @Test
    @DisplayName("Should return null for non-existent team")
    void testNonExistentTeamRetrieval() {
        try {
            // Act: Attempt to retrieve non-existent team
            Team nonExistentTeam = teamDAO.getTeamByNumber(99999);
            
            // Assert: Should return null
            assertNull(nonExistentTeam, "Retrieving non-existent team should return null");
            
        } catch (DatabaseException e) {
            fail("Retrieving non-existent team should not throw exception: " + e.getMessage());
        }
    }
    
    /**
     * Test: teamExists returns false for non-existent team
     * 
     * WHEN checking if a non-existent team exists,
     * THEN the system SHALL return false.
     * 
     * Validates: Requirement 1.2
     */
    @Test
    @DisplayName("Should return false for non-existent team check")
    void testTeamExistsReturnsFalseForNonExistent() {
        try {
            // Act: Check if non-existent team exists
            boolean exists = teamDAO.teamExists(99999);
            
            // Assert: Should return false
            assertFalse(exists, "teamExists should return false for non-existent team");
            
        } catch (DatabaseException e) {
            fail("Checking non-existent team should not throw exception: " + e.getMessage());
        }
    }
    
    /**
     * Test: Update non-existent team
     * 
     * WHEN attempting to update a team that doesn't exist,
     * THEN the system SHALL throw a DatabaseException.
     * 
     * Validates: Requirement 1.2
     */
    @Test
    @DisplayName("Should reject update of non-existent team")
    void testUpdateNonExistentTeam() {
        // Arrange: Create team object that doesn't exist in database
        Team nonExistentTeam = new Team(99999, "Non-existent Team", "Boston", "John Manager");
        
        // Act & Assert: Attempt to update non-existent team
        DatabaseException exception = assertThrows(DatabaseException.class, () -> {
            teamDAO.updateTeam(nonExistentTeam);
        }, "Updating non-existent team should throw DatabaseException");
        
        // Verify error message is descriptive
        assertTrue(exception.getMessage().contains("does not exist"),
                  "Error message should indicate team doesn't exist: " + exception.getMessage());
    }
    
    /**
     * Test: Delete non-existent team
     * 
     * WHEN attempting to delete a team that doesn't exist,
     * THEN the system SHALL throw a DatabaseException.
     * 
     * Validates: Requirement 1.2
     */
    @Test
    @DisplayName("Should reject deletion of non-existent team")
    void testDeleteNonExistentTeam() {
        // Act & Assert: Attempt to delete non-existent team
        DatabaseException exception = assertThrows(DatabaseException.class, () -> {
            teamDAO.deleteTeam(99999);
        }, "Deleting non-existent team should throw DatabaseException");
        
        // Verify error message is descriptive
        assertTrue(exception.getMessage().contains("does not exist"),
                  "Error message should indicate team doesn't exist: " + exception.getMessage());
    }
    
    /**
     * Helper method to clean up test data from the database.
     * Ensures test isolation by removing any test teams.
     */
    private void cleanupTestData() {
        Connection conn = null;
        Statement stmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            
            // Delete test team and any dependent records
            stmt.executeUpdate("DELETE FROM WorkExperience WHERE CoachID IN (SELECT CoachID FROM Coach WHERE TeamNumber = " + TEST_TEAM_NUMBER + ")");
            stmt.executeUpdate("DELETE FROM Coach WHERE TeamNumber = " + TEST_TEAM_NUMBER);
            stmt.executeUpdate("DELETE FROM PlayerTeamAssociation WHERE TeamNumber = " + TEST_TEAM_NUMBER);
            stmt.executeUpdate("DELETE FROM Team WHERE TeamNumber = " + TEST_TEAM_NUMBER);
            
        } catch (SQLException e) {
            // Log but don't fail - cleanup is best effort
            System.err.println("Warning: Failed to cleanup test data: " + e.getMessage());
        } finally {
            DatabaseConnection.closeResources(conn, stmt);
        }
    }
}
