package com.tennisleague.dao.impl;

import com.tennisleague.dao.TeamDAO;
import com.tennisleague.model.Team;
import com.tennisleague.exception.DatabaseException;
import com.tennisleague.database.DatabaseConnection;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Property-based tests for TeamDAO implementation.
 * Tests universal correctness properties across all valid inputs.
 * 
 * These tests validate:
 * - Property 1: Team Creation Persistence
 * - Property 2: Team Update Persistence
 * - Property 3: Team Deletion Completeness
 */
@net.jqwik.api.Tag("Feature: tennis-league-management-system")
class TeamDAOPropertyTest {
    
    private final TeamDAO teamDAO = new TeamDAOImpl();
    
    /**
     * Property 1: Team Creation Persistence
     * 
     * For any valid Team object with unique TeamNumber, creating the team 
     * and then retrieving it by TeamNumber should return an equivalent Team 
     * object with all fields matching.
     * 
     * Validates: Requirements 1.1, 1.5
     */
    @Property(tries = 100)
    @net.jqwik.api.Tag("Property 1: Team Creation Persistence")
    void teamCreationPersistence(
            @ForAll @IntRange(min = 1, max = 9999) int teamNumber,
            @ForAll @StringLength(min = 1, max = 100) @AlphaChars String name,
            @ForAll @StringLength(min = 1, max = 100) @AlphaChars String city,
            @ForAll @StringLength(min = 1, max = 100) @AlphaChars String managerName) {
        
        // Cleanup before test
        cleanupTeam(teamNumber);
        
        // Arrange: Create a team with generated values
        Team originalTeam = new Team(teamNumber, name, city, managerName);
        
        try {
            // Act: Create the team in the database
            teamDAO.createTeam(originalTeam);
            
            // Retrieve the team from the database
            Team retrievedTeam = teamDAO.getTeamByNumber(teamNumber);
            
            // Assert: Retrieved team should match the original
            assertNotNull(retrievedTeam, "Retrieved team should not be null");
            assertEquals(originalTeam.getTeamNumber(), retrievedTeam.getTeamNumber(),
                    "TeamNumber should match");
            assertEquals(originalTeam.getName(), retrievedTeam.getName(),
                    "Name should match");
            assertEquals(originalTeam.getCity(), retrievedTeam.getCity(),
                    "City should match");
            assertEquals(originalTeam.getManagerName(), retrievedTeam.getManagerName(),
                    "ManagerName should match");
            assertEquals(originalTeam, retrievedTeam,
                    "Retrieved team should be equal to original team");
            
            // Cleanup after test
            cleanupTeam(teamNumber);
            
        } catch (DatabaseException e) {
            fail("Team creation and retrieval should succeed for valid data: " + e.getMessage());
        }
    }
    
    /**
     * Property 2: Team Update Persistence
     * 
     * For any existing Team, updating its fields and then retrieving it 
     * should return a Team object with the updated values.
     * 
     * Validates: Requirements 1.3
     */
    @Property(tries = 100)
    @net.jqwik.api.Tag("Property 2: Team Update Persistence")
    void teamUpdatePersistence(
            @ForAll @IntRange(min = 1, max = 9999) int teamNumber,
            @ForAll @StringLength(min = 1, max = 100) @AlphaChars String originalName,
            @ForAll @StringLength(min = 1, max = 100) @AlphaChars String originalCity,
            @ForAll @StringLength(min = 1, max = 100) @AlphaChars String originalManager,
            @ForAll @StringLength(min = 1, max = 100) @AlphaChars String updatedName,
            @ForAll @StringLength(min = 1, max = 100) @AlphaChars String updatedCity,
            @ForAll @StringLength(min = 1, max = 100) @AlphaChars String updatedManager) {
        
        // Cleanup before test
        cleanupTeam(teamNumber);
        
        try {
            // Arrange: Create an initial team
            Team originalTeam = new Team(teamNumber, originalName, originalCity, originalManager);
            teamDAO.createTeam(originalTeam);
            
            // Act: Update the team with new values
            Team updatedTeam = new Team(teamNumber, updatedName, updatedCity, updatedManager);
            teamDAO.updateTeam(updatedTeam);
            
            // Retrieve the updated team
            Team retrievedTeam = teamDAO.getTeamByNumber(teamNumber);
            
            // Assert: Retrieved team should have updated values
            assertNotNull(retrievedTeam, "Retrieved team should not be null");
            assertEquals(teamNumber, retrievedTeam.getTeamNumber(),
                    "TeamNumber should remain unchanged");
            assertEquals(updatedName, retrievedTeam.getName(),
                    "Name should be updated");
            assertEquals(updatedCity, retrievedTeam.getCity(),
                    "City should be updated");
            assertEquals(updatedManager, retrievedTeam.getManagerName(),
                    "ManagerName should be updated");
            assertEquals(updatedTeam, retrievedTeam,
                    "Retrieved team should equal updated team");
            
            // Cleanup after test
            cleanupTeam(teamNumber);
            
        } catch (DatabaseException e) {
            fail("Team update and retrieval should succeed for valid data: " + e.getMessage());
        }
    }
    
    /**
     * Property 3: Team Deletion Completeness
     * 
     * For any existing Team, after deletion, attempting to retrieve that team 
     * by TeamNumber should indicate the team no longer exists.
     * 
     * Validates: Requirements 1.4
     */
    @Property(tries = 100)
    @net.jqwik.api.Tag("Property 3: Team Deletion Completeness")
    void teamDeletionCompleteness(
            @ForAll @IntRange(min = 1, max = 9999) int teamNumber,
            @ForAll @StringLength(min = 1, max = 100) @AlphaChars String name,
            @ForAll @StringLength(min = 1, max = 100) @AlphaChars String city,
            @ForAll @StringLength(min = 1, max = 100) @AlphaChars String managerName) {
        
        // Cleanup before test
        cleanupTeam(teamNumber);
        
        try {
            // Arrange: Create a team
            Team team = new Team(teamNumber, name, city, managerName);
            teamDAO.createTeam(team);
            
            // Verify team exists before deletion
            Team beforeDeletion = teamDAO.getTeamByNumber(teamNumber);
            assertNotNull(beforeDeletion, "Team should exist before deletion");
            
            // Act: Delete the team
            teamDAO.deleteTeam(teamNumber);
            
            // Assert: Team should no longer exist
            Team afterDeletion = teamDAO.getTeamByNumber(teamNumber);
            assertNull(afterDeletion, 
                    "Team should not exist after deletion (getTeamByNumber should return null)");
            
            // Verify teamExists also returns false
            boolean exists = teamDAO.teamExists(teamNumber);
            assertFalse(exists, "teamExists should return false after deletion");
            
        } catch (DatabaseException e) {
            fail("Team deletion should succeed for existing team: " + e.getMessage());
        }
    }
    
    /**
     * Helper method to clean up test data from the database.
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
