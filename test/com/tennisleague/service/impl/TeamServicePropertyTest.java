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
import com.tennisleague.database.DatabaseConnection;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Property-based tests for TeamService implementation.
 * Tests universal correctness properties for business logic layer.
 * 
 * These tests validate:
 * - Property 18: Required Field Validation (for Teams)
 * - Property 20: Referential Integrity on Team Deletion
 */
@net.jqwik.api.Tag("Feature: tennis-league-management-system")
class TeamServicePropertyTest {
    
    private final TeamDAO teamDAO = new TeamDAOImpl();
    private final CoachDAO coachDAO = new CoachDAOImpl();
    private final PlayerTeamAssociationDAO associationDAO = new PlayerTeamAssociationDAOImpl();
    private final TeamService teamService = new TeamServiceImpl(teamDAO, coachDAO, associationDAO);
    
    /**
     * Property 18: Required Field Validation (for Teams)
     * 
     * For any Team entity, attempting to create or update with null or empty 
     * required fields should be rejected with a validation error.
     * 
     * Validates: Requirements 6.1
     */
    @Property(tries = 100)
    @net.jqwik.api.Tag("Property 18: Required Field Validation")
    void requiredFieldValidation(
            @ForAll @IntRange(min = 1, max = 9999) int teamNumber,
            @ForAll("nullableStrings") String name,
            @ForAll("nullableStrings") String city,
            @ForAll("nullableStrings") String managerName) {
        
        // Cleanup before test
        cleanupTeam(teamNumber);
        
        // Arrange: Create a team with potentially null/empty fields
        Team team = new Team(teamNumber, name, city, managerName);
        
        // Determine if any required field is null or empty
        boolean hasInvalidField = isNullOrEmpty(name) || isNullOrEmpty(city) || isNullOrEmpty(managerName);
        
        if (hasInvalidField) {
            // Act & Assert: Should throw ValidationException
            assertThrows(ValidationException.class, () -> {
                teamService.addTeam(team);
            }, "Adding team with null/empty required fields should throw ValidationException");
        } else {
            // Valid data - should succeed
            try {
                teamService.addTeam(team);
                // Cleanup after successful add
                cleanupTeam(teamNumber);
            } catch (ValidationException | DatabaseException e) {
                fail("Adding team with valid fields should succeed: " + e.getMessage());
            }
        }
    }
    
    /**
     * Property 20: Referential Integrity on Team Deletion
     * 
     * For any Team with dependent records (Coaches or PlayerTeamAssociations), 
     * attempting to delete the team should either cascade delete all dependent 
     * records or prevent the deletion with a descriptive error message.
     * 
     * This implementation prevents deletion with an error message.
     * 
     * Validates: Requirements 6.6, 11.2
     */
    @Property(tries = 100)
    @net.jqwik.api.Tag("Property 20: Referential Integrity on Team Deletion")
    void referentialIntegrityOnTeamDeletion(
            @ForAll @IntRange(min = 1, max = 9999) int teamNumber,
            @ForAll @StringLength(min = 1, max = 100) @AlphaChars String teamName,
            @ForAll @StringLength(min = 1, max = 100) @AlphaChars String city,
            @ForAll @StringLength(min = 1, max = 100) @AlphaChars String managerName,
            @ForAll boolean hasDependentRecords) {
        
        // Cleanup before test
        cleanupTeam(teamNumber);
        
        try {
            // Arrange: Create a team
            Team team = new Team(teamNumber, teamName, city, managerName);
            teamService.addTeam(team);
            
            if (hasDependentRecords) {
                // Add a dependent coach record
                Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                stmt.executeUpdate("INSERT INTO Coach (Name, TelephoneNumber, TeamNumber) VALUES ('Test Coach', '123-456-7890', " + teamNumber + ")");
                DatabaseConnection.closeResources(conn, stmt);
                
                // Act & Assert: Deletion should be prevented with descriptive error
                DatabaseException exception = assertThrows(DatabaseException.class, () -> {
                    teamService.deleteTeam(teamNumber);
                }, "Deleting team with dependent records should throw DatabaseException");
                
                // Verify error message is descriptive
                assertTrue(exception.getMessage().contains("coach") || exception.getMessage().contains("associated"),
                        "Error message should mention dependent records: " + exception.getMessage());
                
                // Verify team still exists
                Team stillExists = teamService.getTeam(teamNumber);
                assertNotNull(stillExists, "Team should still exist after failed deletion");
                
            } else {
                // No dependent records - deletion should succeed
                teamService.deleteTeam(teamNumber);
                
                // Verify team no longer exists
                assertThrows(com.tennisleague.exception.EntityNotFoundException.class, () -> {
                    teamService.getTeam(teamNumber);
                }, "Team should not exist after successful deletion");
            }
            
            // Cleanup after test
            cleanupTeam(teamNumber);
            
        } catch (Exception e) {
            fail("Test setup or execution failed: " + e.getMessage());
        }
    }
    
    /**
     * Provides nullable strings for testing validation.
     * Generates null, empty, whitespace-only, and valid strings.
     */
    @Provide
    Arbitrary<String> nullableStrings() {
        return Arbitraries.oneOf(
                Arbitraries.just(null),
                Arbitraries.just(""),
                Arbitraries.just("   "),
                Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(100)
        );
    }
    
    /**
     * Helper method to check if a string is null or empty.
     */
    private boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
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
