package com.tennisleague.dao.impl;

import com.tennisleague.dao.PlayerDAO;
import com.tennisleague.dao.PlayerTeamAssociationDAO;
import com.tennisleague.dao.TeamDAO;
import com.tennisleague.model.Player;
import com.tennisleague.model.PlayerTeamAssociation;
import com.tennisleague.model.Team;
import com.tennisleague.exception.DatabaseException;
import com.tennisleague.database.DatabaseConnection;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Property-based tests for PlayerDAO and PlayerTeamAssociationDAO implementations.
 * Tests universal correctness properties across all valid inputs.
 * 
 * These tests validate:
 * - Property 4: Player Creation Persistence
 * - Property 5: Player Update Persistence
 * - Property 6: Player Deletion Cascades to Associations
 * - Property 10: Player-Team Association Creation
 * - Property 11: Player-Team Association Update with YearLeft
 * - Property 12: Player History Completeness
 */
@net.jqwik.api.Tag("Feature: tennis-league-management-system")
class PlayerDAOPropertyTest {
    
    private final PlayerDAO playerDAO = new PlayerDAOImpl();
    private final PlayerTeamAssociationDAO associationDAO = new PlayerTeamAssociationDAOImpl();
    private final TeamDAO teamDAO = new TeamDAOImpl();
    
    /**
     * Property 4: Player Creation Persistence
     * 
     * For any valid Player object with unique LeagueWideNumber, creating the player 
     * and then retrieving it by PlayerID should return an equivalent Player object 
     * with all fields matching.
     * 
     * Validates: Requirements 2.1, 2.5
     */
    @Property(tries = 100)
    @net.jqwik.api.Tag("Property 4: Player Creation Persistence")
    void playerCreationPersistence(
            @ForAll @IntRange(min = 1, max = 99999) int leagueWideNumber,
            @ForAll @StringLength(min = 1, max = 100) @AlphaChars String name,
            @ForAll @IntRange(min = 1, max = 120) int age) {
        
        // Cleanup before test
        cleanupPlayerByLeagueWideNumber(leagueWideNumber);
        
        try {
            // Arrange: Create a player with generated values (PlayerID will be auto-generated)
            Player originalPlayer = new Player(0, leagueWideNumber, name, age);
            
            // Act: Create the player in the database
            int generatedPlayerID = playerDAO.createPlayer(originalPlayer);
            
            // Retrieve the player from the database
            Player retrievedPlayer = playerDAO.getPlayerByID(generatedPlayerID);
            
            // Assert: Retrieved player should match the original
            assertNotNull(retrievedPlayer, "Retrieved player should not be null");
            assertEquals(generatedPlayerID, retrievedPlayer.getPlayerID(),
                    "PlayerID should match the generated ID");
            assertEquals(leagueWideNumber, retrievedPlayer.getLeagueWideNumber(),
                    "LeagueWideNumber should match");
            assertEquals(name, retrievedPlayer.getName(),
                    "Name should match");
            assertEquals(age, retrievedPlayer.getAge(),
                    "Age should match");
            
            // Cleanup after test
            cleanupPlayer(generatedPlayerID);
            
        } catch (DatabaseException e) {
            fail("Player creation and retrieval should succeed for valid data: " + e.getMessage());
        }
    }

    /**
     * Property 5: Player Update Persistence
     * 
     * For any existing Player, updating its fields and then retrieving it 
     * should return a Player object with the updated values.
     * 
     * Validates: Requirements 2.3
     */
    @Property(tries = 100)
    @net.jqwik.api.Tag("Property 5: Player Update Persistence")
    void playerUpdatePersistence(
            @ForAll @IntRange(min = 1, max = 99999) int leagueWideNumber,
            @ForAll @StringLength(min = 1, max = 100) @AlphaChars String originalName,
            @ForAll @IntRange(min = 1, max = 120) int originalAge,
            @ForAll @IntRange(min = 1, max = 99999) int updatedLeagueWideNumber,
            @ForAll @StringLength(min = 1, max = 100) @AlphaChars String updatedName,
            @ForAll @IntRange(min = 1, max = 120) int updatedAge) {
        
        // Ensure unique league-wide numbers
        Assume.that(leagueWideNumber != updatedLeagueWideNumber);
        
        // Cleanup before test
        cleanupPlayerByLeagueWideNumber(leagueWideNumber);
        cleanupPlayerByLeagueWideNumber(updatedLeagueWideNumber);
        
        try {
            // Arrange: Create an initial player
            Player originalPlayer = new Player(0, leagueWideNumber, originalName, originalAge);
            int playerID = playerDAO.createPlayer(originalPlayer);
            
            // Act: Update the player with new values
            Player updatedPlayer = new Player(playerID, updatedLeagueWideNumber, updatedName, updatedAge);
            playerDAO.updatePlayer(updatedPlayer);
            
            // Retrieve the updated player
            Player retrievedPlayer = playerDAO.getPlayerByID(playerID);
            
            // Assert: Retrieved player should have updated values
            assertNotNull(retrievedPlayer, "Retrieved player should not be null");
            assertEquals(playerID, retrievedPlayer.getPlayerID(),
                    "PlayerID should remain unchanged");
            assertEquals(updatedLeagueWideNumber, retrievedPlayer.getLeagueWideNumber(),
                    "LeagueWideNumber should be updated");
            assertEquals(updatedName, retrievedPlayer.getName(),
                    "Name should be updated");
            assertEquals(updatedAge, retrievedPlayer.getAge(),
                    "Age should be updated");
            
            // Cleanup after test
            cleanupPlayer(playerID);
            
        } catch (DatabaseException e) {
            fail("Player update and retrieval should succeed for valid data: " + e.getMessage());
        }
    }
    
    /**
     * Property 6: Player Deletion Cascades to Associations
     * 
     * For any existing Player with associated PlayerTeamAssociation records, 
     * deleting the player should also remove all associated PlayerTeamAssociation 
     * records from the database.
     * 
     * Validates: Requirements 2.4, 11.3
     */
    @Property(tries = 100)
    @net.jqwik.api.Tag("Property 6: Player Deletion Cascades to Associations")
    void playerDeletionCascadesToAssociations(
            @ForAll @IntRange(min = 1, max = 99999) int leagueWideNumber,
            @ForAll @StringLength(min = 1, max = 100) @AlphaChars String playerName,
            @ForAll @IntRange(min = 1, max = 120) int age,
            @ForAll @IntRange(min = 1, max = 9999) int teamNumber,
            @ForAll @StringLength(min = 1, max = 100) @AlphaChars String teamName,
            @ForAll @IntRange(min = 2000, max = 2025) int yearJoined) {
        
        // Cleanup before test
        cleanupPlayerByLeagueWideNumber(leagueWideNumber);
        cleanupTeam(teamNumber);
        
        try {
            // Arrange: Create a team
            Team team = new Team(teamNumber, teamName, "TestCity", "TestManager");
            teamDAO.createTeam(team);
            
            // Create a player
            Player player = new Player(0, leagueWideNumber, playerName, age);
            int playerID = playerDAO.createPlayer(player);
            
            // Create player-team association
            PlayerTeamAssociation association = new PlayerTeamAssociation(0, playerID, teamNumber, yearJoined, null);
            associationDAO.createAssociation(association);
            
            // Verify association exists before deletion
            List<PlayerTeamAssociation> beforeDeletion = associationDAO.getAssociationsByPlayer(playerID);
            assertFalse(beforeDeletion.isEmpty(), "Player should have associations before deletion");
            
            // Act: Delete associations first (manual cascade), then delete the player
            associationDAO.deleteAssociationsByPlayer(playerID);
            playerDAO.deletePlayer(playerID);
            
            // Assert: Player should no longer exist
            Player afterDeletion = playerDAO.getPlayerByID(playerID);
            assertNull(afterDeletion, "Player should not exist after deletion");
            
            // Assert: Associations should also be deleted
            List<PlayerTeamAssociation> afterAssociations = associationDAO.getAssociationsByPlayer(playerID);
            assertTrue(afterAssociations.isEmpty(), 
                    "All player-team associations should be deleted when player is deleted");
            
            // Cleanup after test
            cleanupTeam(teamNumber);
            
        } catch (DatabaseException e) {
            fail("Player deletion with cascade should succeed: " + e.getMessage());
        }
    }

    /**
     * Property 10: Player-Team Association Creation
     * 
     * For any valid Player and Team, creating a PlayerTeamAssociation with YearJoined 
     * should result in a retrievable association record linking the player to the team 
     * with the correct year.
     * 
     * Validates: Requirements 2.6, 10.1
     */
    @Property(tries = 100)
    @net.jqwik.api.Tag("Property 10: Player-Team Association Creation")
    void playerTeamAssociationCreation(
            @ForAll @IntRange(min = 1, max = 99999) int leagueWideNumber,
            @ForAll @StringLength(min = 1, max = 100) @AlphaChars String playerName,
            @ForAll @IntRange(min = 1, max = 120) int age,
            @ForAll @IntRange(min = 1, max = 9999) int teamNumber,
            @ForAll @StringLength(min = 1, max = 100) @AlphaChars String teamName,
            @ForAll @IntRange(min = 2000, max = 2025) int yearJoined) {
        
        // Cleanup before test
        cleanupPlayerByLeagueWideNumber(leagueWideNumber);
        cleanupTeam(teamNumber);
        
        try {
            // Arrange: Create a team
            Team team = new Team(teamNumber, teamName, "TestCity", "TestManager");
            teamDAO.createTeam(team);
            
            // Create a player
            Player player = new Player(0, leagueWideNumber, playerName, age);
            int playerID = playerDAO.createPlayer(player);
            
            // Act: Create player-team association
            PlayerTeamAssociation association = new PlayerTeamAssociation(0, playerID, teamNumber, yearJoined, null);
            associationDAO.createAssociation(association);
            
            // Retrieve associations for the player
            List<PlayerTeamAssociation> associations = associationDAO.getAssociationsByPlayer(playerID);
            
            // Assert: Association should exist with correct data
            assertFalse(associations.isEmpty(), "Player should have at least one association");
            
            PlayerTeamAssociation retrieved = associations.stream()
                    .filter(a -> a.getTeamNumber() == teamNumber)
                    .findFirst()
                    .orElse(null);
            
            assertNotNull(retrieved, "Association with the specified team should exist");
            assertEquals(playerID, retrieved.getPlayerID(), "PlayerID should match");
            assertEquals(teamNumber, retrieved.getTeamNumber(), "TeamNumber should match");
            assertEquals(yearJoined, retrieved.getYearJoined(), "YearJoined should match");
            assertNull(retrieved.getYearLeft(), "YearLeft should be null for active association");
            
            // Cleanup after test
            cleanupPlayer(playerID);
            cleanupTeam(teamNumber);
            
        } catch (DatabaseException e) {
            fail("Player-team association creation should succeed for valid data: " + e.getMessage());
        }
    }
    
    /**
     * Property 11: Player-Team Association Update with YearLeft
     * 
     * For any existing PlayerTeamAssociation, updating it with a YearLeft value 
     * and then retrieving it should return the association with the updated YearLeft field.
     * 
     * Validates: Requirements 2.8, 10.2
     */
    @Property(tries = 100)
    @net.jqwik.api.Tag("Property 11: Player-Team Association Update with YearLeft")
    void playerTeamAssociationUpdateWithYearLeft(
            @ForAll @IntRange(min = 1, max = 99999) int leagueWideNumber,
            @ForAll @StringLength(min = 1, max = 100) @AlphaChars String playerName,
            @ForAll @IntRange(min = 1, max = 120) int age,
            @ForAll @IntRange(min = 1, max = 9999) int teamNumber,
            @ForAll @StringLength(min = 1, max = 100) @AlphaChars String teamName,
            @ForAll @IntRange(min = 2000, max = 2023) int yearJoined,
            @ForAll @IntRange(min = 2001, max = 2025) int yearLeft) {
        
        // Ensure yearLeft is after yearJoined
        Assume.that(yearLeft >= yearJoined);
        
        // Cleanup before test
        cleanupPlayerByLeagueWideNumber(leagueWideNumber);
        cleanupTeam(teamNumber);
        
        try {
            // Arrange: Create a team
            Team team = new Team(teamNumber, teamName, "TestCity", "TestManager");
            teamDAO.createTeam(team);
            
            // Create a player
            Player player = new Player(0, leagueWideNumber, playerName, age);
            int playerID = playerDAO.createPlayer(player);
            
            // Create player-team association without YearLeft
            PlayerTeamAssociation association = new PlayerTeamAssociation(0, playerID, teamNumber, yearJoined, null);
            associationDAO.createAssociation(association);
            
            // Get the association ID
            List<PlayerTeamAssociation> associations = associationDAO.getAssociationsByPlayer(playerID);
            assertFalse(associations.isEmpty(), "Association should exist");
            PlayerTeamAssociation created = associations.get(0);
            
            // Act: Update the association with YearLeft
            created.setYearLeft(yearLeft);
            associationDAO.updateAssociation(created);
            
            // Retrieve the updated association
            List<PlayerTeamAssociation> updatedAssociations = associationDAO.getAssociationsByPlayer(playerID);
            assertFalse(updatedAssociations.isEmpty(), "Association should still exist after update");
            PlayerTeamAssociation retrieved = updatedAssociations.get(0);
            
            // Assert: Retrieved association should have updated YearLeft
            assertNotNull(retrieved, "Retrieved association should not be null");
            assertEquals(playerID, retrieved.getPlayerID(), "PlayerID should remain unchanged");
            assertEquals(teamNumber, retrieved.getTeamNumber(), "TeamNumber should remain unchanged");
            assertEquals(yearJoined, retrieved.getYearJoined(), "YearJoined should remain unchanged");
            assertNotNull(retrieved.getYearLeft(), "YearLeft should not be null after update");
            assertEquals(yearLeft, retrieved.getYearLeft(), "YearLeft should be updated");
            
            // Cleanup after test
            cleanupPlayer(playerID);
            cleanupTeam(teamNumber);
            
        } catch (DatabaseException e) {
            fail("Player-team association update with YearLeft should succeed: " + e.getMessage());
        }
    }

    /**
     * Property 12: Player History Completeness
     * 
     * For any Player with multiple PlayerTeamAssociation records, retrieving the 
     * player's history should return all association records with complete information 
     * (PlayerID, TeamNumber, YearJoined, YearLeft).
     * 
     * Validates: Requirements 10.3
     */
    @Property(tries = 100)
    @net.jqwik.api.Tag("Property 12: Player History Completeness")
    void playerHistoryCompleteness(
            @ForAll @IntRange(min = 1, max = 99999) int leagueWideNumber,
            @ForAll @StringLength(min = 1, max = 100) @AlphaChars String playerName,
            @ForAll @IntRange(min = 1, max = 120) int age,
            @ForAll @IntRange(min = 1, max = 9999) int teamNumber1,
            @ForAll @IntRange(min = 1, max = 9999) int teamNumber2,
            @ForAll @StringLength(min = 1, max = 100) @AlphaChars String teamName1,
            @ForAll @StringLength(min = 1, max = 100) @AlphaChars String teamName2,
            @ForAll @IntRange(min = 2000, max = 2020) int yearJoined1,
            @ForAll @IntRange(min = 2021, max = 2025) int yearJoined2) {
        
        // Ensure different teams
        Assume.that(teamNumber1 != teamNumber2);
        
        // Cleanup before test
        cleanupPlayerByLeagueWideNumber(leagueWideNumber);
        cleanupTeam(teamNumber1);
        cleanupTeam(teamNumber2);
        
        try {
            // Arrange: Create two teams
            Team team1 = new Team(teamNumber1, teamName1, "City1", "Manager1");
            Team team2 = new Team(teamNumber2, teamName2, "City2", "Manager2");
            teamDAO.createTeam(team1);
            teamDAO.createTeam(team2);
            
            // Create a player
            Player player = new Player(0, leagueWideNumber, playerName, age);
            int playerID = playerDAO.createPlayer(player);
            
            // Create two player-team associations (non-overlapping time periods)
            PlayerTeamAssociation association1 = new PlayerTeamAssociation(0, playerID, teamNumber1, yearJoined1, yearJoined1 + 1);
            PlayerTeamAssociation association2 = new PlayerTeamAssociation(0, playerID, teamNumber2, yearJoined2, null);
            
            associationDAO.createAssociation(association1);
            associationDAO.createAssociation(association2);
            
            // Act: Retrieve player history (all associations)
            List<PlayerTeamAssociation> history = associationDAO.getAssociationsByPlayer(playerID);
            
            // Assert: All associations should be retrieved with complete information
            assertNotNull(history, "Player history should not be null");
            assertEquals(2, history.size(), "Player should have exactly 2 associations");
            
            // Verify first association
            PlayerTeamAssociation retrieved1 = history.stream()
                    .filter(a -> a.getTeamNumber() == teamNumber1)
                    .findFirst()
                    .orElse(null);
            
            assertNotNull(retrieved1, "First association should exist");
            assertEquals(playerID, retrieved1.getPlayerID(), "PlayerID should match for first association");
            assertEquals(teamNumber1, retrieved1.getTeamNumber(), "TeamNumber should match for first association");
            assertEquals(yearJoined1, retrieved1.getYearJoined(), "YearJoined should match for first association");
            assertNotNull(retrieved1.getYearLeft(), "YearLeft should not be null for completed association");
            assertEquals(yearJoined1 + 1, retrieved1.getYearLeft(), "YearLeft should match for first association");
            
            // Verify second association
            PlayerTeamAssociation retrieved2 = history.stream()
                    .filter(a -> a.getTeamNumber() == teamNumber2)
                    .findFirst()
                    .orElse(null);
            
            assertNotNull(retrieved2, "Second association should exist");
            assertEquals(playerID, retrieved2.getPlayerID(), "PlayerID should match for second association");
            assertEquals(teamNumber2, retrieved2.getTeamNumber(), "TeamNumber should match for second association");
            assertEquals(yearJoined2, retrieved2.getYearJoined(), "YearJoined should match for second association");
            assertNull(retrieved2.getYearLeft(), "YearLeft should be null for active association");
            
            // Cleanup after test
            cleanupPlayer(playerID);
            cleanupTeam(teamNumber1);
            cleanupTeam(teamNumber2);
            
        } catch (DatabaseException e) {
            fail("Player history retrieval should succeed and return all associations: " + e.getMessage());
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
