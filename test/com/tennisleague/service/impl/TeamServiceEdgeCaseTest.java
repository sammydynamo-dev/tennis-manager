package com.tennisleague.service.impl;

import com.tennisleague.service.TeamService;
import com.tennisleague.model.Team;
import com.tennisleague.dao.TeamDAO;
import com.tennisleague.dao.CoachDAO;
import com.tennisleague.dao.PlayerTeamAssociationDAO;
import com.tennisleague.dao.impl.TeamDAOImpl;
import com.tennisleague.dao.impl.CoachDAOImpl;
import com.tennisleague.dao.impl.PlayerTeamAssociationDAOImpl;
import com.tennisleague.exception.ValidationException;
import com.tennisleague.exception.DatabaseException;
import com.tennisleague.exception.EntityNotFoundException;
import com.tennisleague.database.DatabaseConnection;

import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TeamService edge cases.
 * Tests specific scenarios including:
 * - Validation error handling
 * - Duplicate team rejection
 * - Team deletion with dependencies
 * 
 * Validates: Requirements 1.2, 1.6, 6.6
 */
class TeamServiceEdgeCaseTest {
    
    private TeamService teamService;
    private static final int TEST_TEAM_NUMBER = 9998;
    private static final int TEST_TEAM_NUMBER_2 = 9997;
    
    @BeforeEach
    void setUp() {
        TeamDAO teamDAO = new TeamDAOImpl();
        CoachDAO coachDAO = new CoachDAOImpl();
        PlayerTeamAssociationDAO associationDAO = new PlayerTeamAssociationDAOImpl();
        teamService = new TeamServiceImpl(teamDAO, coachDAO, associationDAO);
        cleanupTestData();
    }
    
    @AfterEach
    void tearDown() {
        cleanupTestData();
    }
    
    /**
     * Test: Validation Error Handling - Null Team
     * 
     * WHEN a user provides a null team object,
     * THEN the system SHALL reject the operation with a ValidationException.
     * 
     * Validates: Requirement 6.1
     */
    @Test
    @DisplayName("Should reject null team with ValidationException")
    void testNullTeamRejection() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> {
            teamService.addTeam(null);
        }, "Adding null team should throw ValidationException");
    }
    
    /**
     * Test: Validation Error Handling - Null Name
     * 
     * WHEN a user provides a team with null name,
     * THEN the system SHALL reject the operation with a ValidationException.
     * 
     * Validates: Requirement 1.6, 6.1
     */
    @Test
    @DisplayName("Should reject team with null name")
    void testNullNameRejection() {
        // Arrange
        Team teamWithNullName = new Team(TEST_TEAM_NUMBER, null, "Boston", "John Manager");
        
        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            teamService.addTeam(teamWithNullName);
        }, "Adding team with null name should throw ValidationException");
        
        assertTrue(exception.getMessage().contains("Name") || exception.getMessage().contains("required"),
                  "Error message should mention name field: " + exception.getMessage());
    }
    
    /**
     * Test: Validation Error Handling - Empty Name
     * 
     * WHEN a user provides a team with empty name,
     * THEN the system SHALL reject the operation with a ValidationException.
     * 
     * Validates: Requirement 1.6, 6.1
     */
    @Test
    @DisplayName("Should reject team with empty name")
    void testEmptyNameRejection() {
        // Arrange
        Team teamWithEmptyName = new Team(TEST_TEAM_NUMBER, "", "Boston", "John Manager");
        
        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            teamService.addTeam(teamWithEmptyName);
        }, "Adding team with empty name should throw ValidationException");
        
        assertTrue(exception.getMessage().contains("Name") || exception.getMessage().contains("required"),
                  "Error message should mention name field: " + exception.getMessage());
    }
    
    /**
     * Test: Validation Error Handling - Whitespace-only Name
     * 
     * WHEN a user provides a team with whitespace-only name,
     * THEN the system SHALL reject the operation with a ValidationException.
     * 
     * Validates: Requirement 1.6, 6.1
     */
    @Test
    @DisplayName("Should reject team with whitespace-only name")
    void testWhitespaceNameRejection() {
        // Arrange
        Team teamWithWhitespaceName = new Team(TEST_TEAM_NUMBER, "   ", "Boston", "John Manager");
        
        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            teamService.addTeam(teamWithWhitespaceName);
        }, "Adding team with whitespace-only name should throw ValidationException");
        
        assertTrue(exception.getMessage().contains("Name") || exception.getMessage().contains("required"),
                  "Error message should mention name field: " + exception.getMessage());
    }
    
    /**
     * Test: Validation Error Handling - Null City
     * 
     * WHEN a user provides a team with null city,
     * THEN the system SHALL reject the operation with a ValidationException.
     * 
     * Validates: Requirement 1.6, 6.1
     */
    @Test
    @DisplayName("Should reject team with null city")
    void testNullCityRejection() {
        // Arrange
        Team teamWithNullCity = new Team(TEST_TEAM_NUMBER, "Test Team", null, "John Manager");
        
        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            teamService.addTeam(teamWithNullCity);
        }, "Adding team with null city should throw ValidationException");
        
        assertTrue(exception.getMessage().contains("City") || exception.getMessage().contains("required"),
                  "Error message should mention city field: " + exception.getMessage());
    }
    
    /**
     * Test: Validation Error Handling - Null Manager Name
     * 
     * WHEN a user provides a team with null manager name,
     * THEN the system SHALL reject the operation with a ValidationException.
     * 
     * Validates: Requirement 1.6, 6.1
     */
    @Test
    @DisplayName("Should reject team with null manager name")
    void testNullManagerNameRejection() {
        // Arrange
        Team teamWithNullManager = new Team(TEST_TEAM_NUMBER, "Test Team", "Boston", null);
        
        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            teamService.addTeam(teamWithNullManager);
        }, "Adding team with null manager name should throw ValidationException");
        
        assertTrue(exception.getMessage().contains("Manager") || exception.getMessage().contains("required"),
                  "Error message should mention manager field: " + exception.getMessage());
    }
    
    /**
     * Test: Duplicate Team Rejection
     * 
     * WHEN a user attempts to create a team with a duplicate TeamNumber,
     * THEN the system SHALL reject the operation with a ValidationException.
     * 
     * Validates: Requirement 1.2
     */
    @Test
    @DisplayName("Should reject duplicate team number")
    void testDuplicateTeamRejection() {
        // Arrange: Create first team
        Team firstTeam = new Team(TEST_TEAM_NUMBER, "First Team", "Boston", "John Manager");
        
        try {
            teamService.addTeam(firstTeam);
            
            // Act & Assert: Attempt to create duplicate team
            Team duplicateTeam = new Team(TEST_TEAM_NUMBER, "Duplicate Team", "New York", "Jane Manager");
            
            ValidationException exception = assertThrows(ValidationException.class, () -> {
                teamService.addTeam(duplicateTeam);
            }, "Adding team with duplicate TeamNumber should throw ValidationException");
            
            assertTrue(exception.getMessage().contains("already exists") || 
                      exception.getMessage().contains(String.valueOf(TEST_TEAM_NUMBER)),
                      "Error message should mention duplicate team: " + exception.getMessage());
            
        } catch (ValidationException | DatabaseException e) {
            fail("First team creation should succeed: " + e.getMessage());
        }
    }
    
    /**
     * Test: Team Deletion with Coach Dependencies
     * 
     * WHEN a user attempts to delete a team with associated coaches,
     * THEN the system SHALL prevent deletion with a DatabaseException.
     * 
     * Validates: Requirement 6.6, 11.2
     */
    @Test
    @DisplayName("Should prevent team deletion when coaches exist")
    void testTeamDeletionWithCoachDependencies() {
        // Arrange: Create team and add a coach
        Team team = new Team(TEST_TEAM_NUMBER, "Test Team", "Boston", "John Manager");
        
        try {
            teamService.addTeam(team);
            
            // Add a coach for this team directly via database
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("INSERT INTO Coach (Name, TelephoneNumber, TeamNumber) " +
                             "VALUES ('Test Coach', '123-456-7890', " + TEST_TEAM_NUMBER + ")");
            DatabaseConnection.closeResources(conn, stmt);
            
            // Act & Assert: Attempt to delete team with coach
            DatabaseException exception = assertThrows(DatabaseException.class, () -> {
                teamService.deleteTeam(TEST_TEAM_NUMBER);
            }, "Deleting team with coaches should throw DatabaseException");
            
            assertTrue(exception.getMessage().contains("coach") || 
                      exception.getMessage().contains("associated") ||
                      exception.getMessage().contains("dependent"),
                      "Error message should mention dependent coaches: " + exception.getMessage());
            
            // Verify team still exists
            Team stillExists = teamService.getTeam(TEST_TEAM_NUMBER);
            assertNotNull(stillExists, "Team should still exist after failed deletion");
            
        } catch (ValidationException | DatabaseException | EntityNotFoundException | SQLException e) {
            fail("Test setup or execution failed: " + e.getMessage());
        }
    }
    
    /**
     * Test: Team Deletion with Player Association Dependencies
     * 
     * WHEN a user attempts to delete a team with player associations,
     * THEN the system SHALL prevent deletion with a DatabaseException.
     * 
     * Validates: Requirement 6.6, 11.2
     */
    @Test
    @DisplayName("Should prevent team deletion when player associations exist")
    void testTeamDeletionWithPlayerAssociationDependencies() {
        // Arrange: Create team and add a player association
        Team team = new Team(TEST_TEAM_NUMBER_2, "Test Team 2", "Boston", "John Manager");
        Connection conn = null;
        Statement stmt = null;
        
        try {
            teamService.addTeam(team);
            
            // Add a player and association directly via database
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            stmt.executeUpdate("INSERT INTO Player (LeagueWideNumber, Name, Age) " +
                             "VALUES (99999, 'Test Player', 25)");
            
            // Get the player ID in the same connection
            var rs = stmt.executeQuery("SELECT PlayerID FROM Player WHERE LeagueWideNumber = 99999");
            rs.next();
            int playerID = rs.getInt(1);
            rs.close();
            
            stmt.executeUpdate("INSERT INTO PlayerTeamAssociation (PlayerID, TeamNumber, YearJoined) " +
                             "VALUES (" + playerID + ", " + TEST_TEAM_NUMBER_2 + ", 2024)");
            
            // Act & Assert: Attempt to delete team with player associations
            DatabaseException exception = assertThrows(DatabaseException.class, () -> {
                teamService.deleteTeam(TEST_TEAM_NUMBER_2);
            }, "Deleting team with player associations should throw DatabaseException");
            
            assertTrue(exception.getMessage().contains("player") || 
                      exception.getMessage().contains("associated") ||
                      exception.getMessage().contains("dependent"),
                      "Error message should mention dependent players: " + exception.getMessage());
            
            // Verify team still exists
            Team stillExists = teamService.getTeam(TEST_TEAM_NUMBER_2);
            assertNotNull(stillExists, "Team should still exist after failed deletion");
            
        } catch (ValidationException | DatabaseException | EntityNotFoundException | SQLException e) {
            fail("Test setup or execution failed: " + e.getMessage());
        } finally {
            DatabaseConnection.closeResources(conn, stmt);
        }
    }
    
    /**
     * Test: Get Non-existent Team
     * 
     * WHEN a user requests a team that doesn't exist,
     * THEN the system SHALL throw EntityNotFoundException.
     * 
     * Validates: Requirement 1.2
     */
    @Test
    @DisplayName("Should throw EntityNotFoundException for non-existent team")
    void testGetNonExistentTeam() {
        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            teamService.getTeam(99999);
        }, "Getting non-existent team should throw EntityNotFoundException");
        
        assertTrue(exception.getMessage().contains("not found") || 
                  exception.getMessage().contains("99999"),
                  "Error message should mention team not found: " + exception.getMessage());
    }
    
    /**
     * Test: Update Non-existent Team
     * 
     * WHEN a user attempts to update a team that doesn't exist,
     * THEN the system SHALL throw EntityNotFoundException.
     * 
     * Validates: Requirement 1.2
     */
    @Test
    @DisplayName("Should throw EntityNotFoundException when updating non-existent team")
    void testUpdateNonExistentTeam() {
        // Arrange
        Team nonExistentTeam = new Team(99999, "Non-existent Team", "Boston", "John Manager");
        
        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            teamService.updateTeam(nonExistentTeam);
        }, "Updating non-existent team should throw EntityNotFoundException");
        
        assertTrue(exception.getMessage().contains("not found") || 
                  exception.getMessage().contains("99999"),
                  "Error message should mention team not found: " + exception.getMessage());
    }
    
    /**
     * Test: Delete Non-existent Team
     * 
     * WHEN a user attempts to delete a team that doesn't exist,
     * THEN the system SHALL throw EntityNotFoundException.
     * 
     * Validates: Requirement 1.2
     */
    @Test
    @DisplayName("Should throw EntityNotFoundException when deleting non-existent team")
    void testDeleteNonExistentTeam() {
        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            teamService.deleteTeam(99999);
        }, "Deleting non-existent team should throw EntityNotFoundException");
        
        assertTrue(exception.getMessage().contains("not found") || 
                  exception.getMessage().contains("99999"),
                  "Error message should mention team not found: " + exception.getMessage());
    }
    
    /**
     * Helper method to clean up test data from the database.
     * Ensures test isolation by removing any test teams and related data.
     */
    private void cleanupTestData() {
        Connection conn = null;
        Statement stmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            
            // Delete test data for both test team numbers
            for (int teamNumber : new int[]{TEST_TEAM_NUMBER, TEST_TEAM_NUMBER_2}) {
                stmt.executeUpdate("DELETE FROM WorkExperience WHERE CoachID IN (SELECT CoachID FROM Coach WHERE TeamNumber = " + teamNumber + ")");
                stmt.executeUpdate("DELETE FROM Coach WHERE TeamNumber = " + teamNumber);
                stmt.executeUpdate("DELETE FROM PlayerTeamAssociation WHERE TeamNumber = " + teamNumber);
                stmt.executeUpdate("DELETE FROM Team WHERE TeamNumber = " + teamNumber);
            }
            
            // Delete test player
            stmt.executeUpdate("DELETE FROM Player WHERE LeagueWideNumber = 99999");
            
        } catch (SQLException e) {
            // Log but don't fail - cleanup is best effort
            System.err.println("Warning: Failed to cleanup test data: " + e.getMessage());
        } finally {
            DatabaseConnection.closeResources(conn, stmt);
        }
    }
}
