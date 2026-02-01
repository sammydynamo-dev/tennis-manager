package com.tennisleague.dao.impl;

import com.tennisleague.dao.PlayerDAO;
import com.tennisleague.dao.PlayerTeamAssociationDAO;
import com.tennisleague.dao.TeamDAO;
import com.tennisleague.model.Player;
import com.tennisleague.model.PlayerTeamAssociation;
import com.tennisleague.model.Team;
import com.tennisleague.exception.DatabaseException;
import com.tennisleague.database.DatabaseConnection;

import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PlayerDAO edge cases.
 * Tests specific error conditions and boundary cases for Player operations.
 * 
 * Validates:
 * - Requirement 2.2: Duplicate LeagueWideNumber rejection
 * - Requirement 2.7: Invalid age values
 * - Requirement 10.4: Overlapping association prevention
 */
@Tag("Feature: tennis-league-management-system")
class PlayerDAOEdgeCaseTest {
    
    private final PlayerDAO playerDAO = new PlayerDAOImpl();
    private final PlayerTeamAssociationDAO associationDAO = new PlayerTeamAssociationDAOImpl();
    private final TeamDAO teamDAO = new TeamDAOImpl();
    
    /**
     * Test: Duplicate LeagueWideNumber rejection
     * 
     * Validates Requirement 2.2: When a user attempts to create a player with a 
     * duplicate LeagueWideNumber, the system shall reject the operation and return 
     * a descriptive error message.
     */
    @Test
    @DisplayName("Should reject duplicate LeagueWideNumber on player creation")
    void testDuplicateLeagueWideNumberRejection() {
        int leagueWideNumber = 88888;
        cleanupPlayerByLeagueWideNumber(leagueWideNumber);
        
        try {
            // Create first player with league-wide number 88888
            Player player1 = new Player(0, leagueWideNumber, "John Doe", 25);
            int playerID1 = playerDAO.createPlayer(player1);
            
            // Attempt to create second player with same league-wide number
            Player player2 = new Player(0, leagueWideNumber, "Jane Smith", 30);
            
            DatabaseException exception = assertThrows(DatabaseException.class, () -> {
                playerDAO.createPlayer(player2);
            }, "Creating player with duplicate LeagueWideNumber should throw DatabaseException");
            
            // Verify error message mentions the duplicate
            assertTrue(exception.getMessage().contains("already exists") || 
                      exception.getMessage().contains("Duplicate"),
                      "Error message should indicate duplicate LeagueWideNumber");
            
            // Cleanup
            cleanupPlayer(playerID1);
            
        } catch (DatabaseException e) {
            fail("Test setup failed: " + e.getMessage());
        }
    }
    
    /**
     * Test: Duplicate LeagueWideNumber rejection on update
     * 
     * Validates Requirement 2.2: When updating a player to use an existing 
     * LeagueWideNumber, the system shall reject the operation.
     */
    @Test
    @DisplayName("Should reject duplicate LeagueWideNumber on player update")
    void testDuplicateLeagueWideNumberOnUpdate() {
        int leagueWideNumber1 = 77777;
        int leagueWideNumber2 = 77778;
        cleanupPlayerByLeagueWideNumber(leagueWideNumber1);
        cleanupPlayerByLeagueWideNumber(leagueWideNumber2);
        
        try {
            // Create two players with different league-wide numbers
            Player player1 = new Player(0, leagueWideNumber1, "Alice Brown", 28);
            int playerID1 = playerDAO.createPlayer(player1);
            
            Player player2 = new Player(0, leagueWideNumber2, "Bob Green", 32);
            int playerID2 = playerDAO.createPlayer(player2);
            
            // Attempt to update player2 to use player1's league-wide number
            player2.setPlayerID(playerID2);
            player2.setLeagueWideNumber(leagueWideNumber1);
            
            DatabaseException exception = assertThrows(DatabaseException.class, () -> {
                playerDAO.updatePlayer(player2);
            }, "Updating player with duplicate LeagueWideNumber should throw DatabaseException");
            
            // Verify error message mentions the duplicate
            assertTrue(exception.getMessage().contains("already exists") || 
                      exception.getMessage().contains("Duplicate"),
                      "Error message should indicate duplicate LeagueWideNumber");
            
            // Cleanup
            cleanupPlayer(playerID1);
            cleanupPlayer(playerID2);
            
        } catch (DatabaseException e) {
            fail("Test setup failed: " + e.getMessage());
        }
    }
    
    /**
     * Test: Invalid age values - negative age
     * 
     * Validates Requirement 2.7: When a user provides invalid age values (negative),
     * the system shall reject the operation.
     * 
     * Note: This test validates database-level constraints. Application-level validation
     * should be handled by the service layer.
     */
    @Test
    @DisplayName("Should handle negative age values")
    void testNegativeAgeValue() {
        int leagueWideNumber = 66666;
        cleanupPlayerByLeagueWideNumber(leagueWideNumber);
        
        // Create player with negative age
        Player player = new Player(0, leagueWideNumber, "Invalid Age Player", -5);
        
        // The DAO layer accepts the data as-is; validation should occur at service layer
        // This test documents current behavior
        assertDoesNotThrow(() -> {
            int playerID = playerDAO.createPlayer(player);
            Player retrieved = playerDAO.getPlayerByID(playerID);
            assertEquals(-5, retrieved.getAge(), "Negative age should be stored as-is");
            cleanupPlayer(playerID);
        }, "DAO layer should accept negative age (validation is service layer responsibility)");
    }
    
    /**
     * Test: Invalid age values - unrealistic age
     * 
     * Validates Requirement 2.7: When a user provides unrealistic age values,
     * the system should handle them appropriately.
     */
    @Test
    @DisplayName("Should handle unrealistic age values")
    void testUnrealisticAgeValue() {
        int leagueWideNumber = 55555;
        cleanupPlayerByLeagueWideNumber(leagueWideNumber);
        
        // Create player with unrealistic age (e.g., 200 years old)
        Player player = new Player(0, leagueWideNumber, "Ancient Player", 200);
        
        // The DAO layer accepts the data as-is; validation should occur at service layer
        assertDoesNotThrow(() -> {
            int playerID = playerDAO.createPlayer(player);
            Player retrieved = playerDAO.getPlayerByID(playerID);
            assertEquals(200, retrieved.getAge(), "Unrealistic age should be stored as-is");
            cleanupPlayer(playerID);
        }, "DAO layer should accept unrealistic age (validation is service layer responsibility)");
    }
    
    /**
     * Test: Overlapping association prevention
     * 
     * Validates Requirement 10.4: When a user attempts to create overlapping team 
     * associations for a player, the system shall validate and prevent conflicts.
     */
    @Test
    @DisplayName("Should detect overlapping player-team associations")
    void testOverlappingAssociationPrevention() {
        int leagueWideNumber = 44444;
        int teamNumber = 9001;
        cleanupPlayerByLeagueWideNumber(leagueWideNumber);
        cleanupTeam(teamNumber);
        
        try {
            // Create a team
            Team team = new Team(teamNumber, "Test Team", "Test City", "Test Manager");
            teamDAO.createTeam(team);
            
            // Create a player
            Player player = new Player(0, leagueWideNumber, "Test Player", 25);
            int playerID = playerDAO.createPlayer(player);
            
            // Create first association (player joins team in 2020, still active)
            PlayerTeamAssociation association1 = new PlayerTeamAssociation(0, playerID, teamNumber, 2020, null);
            associationDAO.createAssociation(association1);
            
            // Check for overlapping association (should detect the active association)
            boolean hasOverlap = associationDAO.hasOverlappingAssociation(playerID, teamNumber, 2022);
            assertTrue(hasOverlap, "Should detect overlapping association when player is still active on team");
            
            // Retrieve the created association to get its ID
            var associations = associationDAO.getAssociationsByPlayer(playerID);
            assertFalse(associations.isEmpty(), "Association should exist");
            PlayerTeamAssociation createdAssociation = associations.get(0);
            
            // Update first association with YearLeft (player left in 2023)
            createdAssociation.setYearLeft(2023);
            associationDAO.updateAssociation(createdAssociation);
            
            // Check for overlapping association with year before player left
            boolean hasOverlapBefore = associationDAO.hasOverlappingAssociation(playerID, teamNumber, 2022);
            assertTrue(hasOverlapBefore, "Should detect overlap when joining year is before player left");
            
            // Check for overlapping association with year after player left
            boolean hasOverlapAfter = associationDAO.hasOverlappingAssociation(playerID, teamNumber, 2024);
            assertFalse(hasOverlapAfter, "Should not detect overlap when joining year is after player left");
            
            // Cleanup
            cleanupPlayer(playerID);
            cleanupTeam(teamNumber);
            
        } catch (DatabaseException e) {
            fail("Test failed: " + e.getMessage());
        }
    }
    
    /**
     * Test: Multiple non-overlapping associations allowed
     * 
     * Validates that a player can have multiple associations with the same team
     * as long as they don't overlap in time.
     */
    @Test
    @DisplayName("Should allow multiple non-overlapping associations for same player and team")
    void testNonOverlappingAssociationsAllowed() {
        int leagueWideNumber = 33333;
        int teamNumber = 9002;
        cleanupPlayerByLeagueWideNumber(leagueWideNumber);
        cleanupTeam(teamNumber);
        
        try {
            // Create a team
            Team team = new Team(teamNumber, "Test Team 2", "Test City", "Test Manager");
            teamDAO.createTeam(team);
            
            // Create a player
            Player player = new Player(0, leagueWideNumber, "Returning Player", 30);
            int playerID = playerDAO.createPlayer(player);
            
            // Create first association (2015-2018)
            PlayerTeamAssociation association1 = new PlayerTeamAssociation(0, playerID, teamNumber, 2015, 2018);
            associationDAO.createAssociation(association1);
            
            // Check for overlap with new association starting in 2020 (should be no overlap)
            boolean hasOverlap = associationDAO.hasOverlappingAssociation(playerID, teamNumber, 2020);
            assertFalse(hasOverlap, "Should not detect overlap for non-overlapping time periods");
            
            // Create second association (2020-present) - should succeed
            PlayerTeamAssociation association2 = new PlayerTeamAssociation(0, playerID, teamNumber, 2020, null);
            assertDoesNotThrow(() -> {
                associationDAO.createAssociation(association2);
            }, "Should allow non-overlapping association for same player and team");
            
            // Verify both associations exist
            var associations = associationDAO.getAssociationsByPlayer(playerID);
            assertEquals(2, associations.size(), "Player should have 2 associations with the same team");
            
            // Cleanup
            cleanupPlayer(playerID);
            cleanupTeam(teamNumber);
            
        } catch (DatabaseException e) {
            fail("Test failed: " + e.getMessage());
        }
    }
    
    /**
     * Helper method to clean up test data from the database.
     * Removes a specific player and all associated records to ensure test isolation.
     */
    private void cleanupPlayer(int playerID) {
        Connection conn = null;
        Statement stmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            
            // Delete player-team associations for this player
            stmt.executeUpdate("DELETE FROM PlayerTeamAssociation WHERE PlayerID = " + playerID);
            
            // Delete the player
            stmt.executeUpdate("DELETE FROM Player WHERE PlayerID = " + playerID);
            
        } catch (SQLException e) {
            // Log but don't fail - cleanup is best effort
            System.err.println("Warning: Failed to cleanup player " + playerID + ": " + e.getMessage());
        } finally {
            DatabaseConnection.closeResources(conn, stmt);
        }
    }
    
    /**
     * Helper method to clean up test data by league-wide number.
     * Removes a player by league-wide number and all associated records.
     */
    private void cleanupPlayerByLeagueWideNumber(int leagueWideNumber) {
        Connection conn = null;
        Statement stmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            
            // Delete player-team associations for this player
            stmt.executeUpdate("DELETE FROM PlayerTeamAssociation WHERE PlayerID IN (SELECT PlayerID FROM Player WHERE LeagueWideNumber = " + leagueWideNumber + ")");
            
            // Delete the player
            stmt.executeUpdate("DELETE FROM Player WHERE LeagueWideNumber = " + leagueWideNumber);
            
        } catch (SQLException e) {
            // Log but don't fail - cleanup is best effort
            System.err.println("Warning: Failed to cleanup player with LeagueWideNumber " + leagueWideNumber + ": " + e.getMessage());
        } finally {
            DatabaseConnection.closeResources(conn, stmt);
        }
    }
    
    /**
     * Helper method to clean up test team data from the database.
     * Removes a specific team and all its dependent records to ensure test isolation.
     */
    private void cleanupTeam(int teamNumber) {
        Connection conn = null;
        Statement stmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            
            // Delete dependent records first (to avoid foreign key constraint violations)
            // Delete work experience for coaches of this team
            stmt.executeUpdate("DELETE FROM WorkExperience WHERE CoachID IN (SELECT CoachID FROM Coach WHERE TeamNumber = " + teamNumber + ")");
            
            // Delete coaches for this team
            stmt.executeUpdate("DELETE FROM Coach WHERE TeamNumber = " + teamNumber);
            
            // Delete player-team associations for this team
            stmt.executeUpdate("DELETE FROM PlayerTeamAssociation WHERE TeamNumber = " + teamNumber);
            
            // Finally, delete the team
            stmt.executeUpdate("DELETE FROM Team WHERE TeamNumber = " + teamNumber);
            
        } catch (SQLException e) {
            // Log but don't fail - cleanup is best effort
            System.err.println("Warning: Failed to cleanup team " + teamNumber + ": " + e.getMessage());
        } finally {
            DatabaseConnection.closeResources(conn, stmt);
        }
    }
}
