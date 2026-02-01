package com.tennisleague.dao.impl;

import com.tennisleague.dao.CoachDAO;
import com.tennisleague.dao.WorkExperienceDAO;
import com.tennisleague.dao.TeamDAO;
import com.tennisleague.model.Coach;
import com.tennisleague.model.WorkExperience;
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
 * Property-based tests for CoachDAO and WorkExperienceDAO implementations.
 * Tests universal correctness properties across all valid inputs.
 * 
 * These tests validate:
 * - Property 7: Coach Creation Persistence
 * - Property 8: Coach Update Persistence
 * - Property 9: Coach Deletion Cascades to Work Experience
 * - Property 15: Work Experience Association Creation
 * - Property 16: Work Experience Validation
 * - Property 17: Coach Details Include Work Experience
 */
@net.jqwik.api.Tag("Feature: tennis-league-management-system")
class CoachDAOPropertyTest {
    
    private final CoachDAO coachDAO = new CoachDAOImpl();
    private final WorkExperienceDAO workExperienceDAO = new WorkExperienceDAOImpl();
    private final TeamDAO teamDAO = new TeamDAOImpl();
    
    /**
     * Property 7: Coach Creation Persistence
     * 
     * For any valid Coach object with a valid TeamNumber reference, creating 
     * the coach and then retrieving it by CoachID should return an equivalent 
     * Coach object with all fields matching.
     * 
     * Validates: Requirements 3.1, 3.5
     */
    @Property(tries = 100)
    @net.jqwik.api.Tag("Property 7: Coach Creation Persistence")
    void coachCreationPersistence(
            @ForAll @StringLength(min = 1, max = 100) @AlphaChars String name,
            @ForAll @StringLength(min = 10, max = 20) @NumericChars String telephoneNumber,
            @ForAll @IntRange(min = 1, max = 9999) int teamNumber) {
        
        // Setup: Create a team for the coach to reference
        Team team = new Team(teamNumber, "Test Team", "Test City", "Test Manager");
        cleanupTeam(teamNumber);
        
        try {
            teamDAO.createTeam(team);
            
            // Arrange: Create a coach with generated values
            Coach originalCoach = new Coach(0, name, telephoneNumber, teamNumber);
            
            // Act: Create the coach in the database
            int generatedCoachID = coachDAO.createCoach(originalCoach);
            
            // Retrieve the coach from the database
            Coach retrievedCoach = coachDAO.getCoachByID(generatedCoachID);
            
            // Assert: Retrieved coach should match the original
            assertNotNull(retrievedCoach, "Retrieved coach should not be null");
            assertEquals(generatedCoachID, retrievedCoach.getCoachID(),
                    "CoachID should match generated ID");
            assertEquals(originalCoach.getName(), retrievedCoach.getName(),
                    "Name should match");
            assertEquals(originalCoach.getTelephoneNumber(), retrievedCoach.getTelephoneNumber(),
                    "TelephoneNumber should match");
            assertEquals(originalCoach.getTeamNumber(), retrievedCoach.getTeamNumber(),
                    "TeamNumber should match");
            
            // Cleanup after test
            cleanupCoach(generatedCoachID);
            cleanupTeam(teamNumber);
            
        } catch (DatabaseException e) {
            fail("Coach creation and retrieval should succeed for valid data: " + e.getMessage());
        }
    }
    
    /**
     * Property 8: Coach Update Persistence
     * 
     * For any existing Coach, updating its fields and then retrieving it 
     * should return a Coach object with the updated values.
     * 
     * Validates: Requirements 3.3
     */
    @Property(tries = 100)
    @net.jqwik.api.Tag("Property 8: Coach Update Persistence")
    void coachUpdatePersistence(
            @ForAll @StringLength(min = 1, max = 100) @AlphaChars String originalName,
            @ForAll @StringLength(min = 10, max = 20) @NumericChars String originalPhone,
            @ForAll @IntRange(min = 1, max = 9999) int teamNumber,
            @ForAll @StringLength(min = 1, max = 100) @AlphaChars String updatedName,
            @ForAll @StringLength(min = 10, max = 20) @NumericChars String updatedPhone) {
        
        // Setup: Create a team for the coach to reference
        Team team = new Team(teamNumber, "Test Team", "Test City", "Test Manager");
        cleanupTeam(teamNumber);
        
        try {
            teamDAO.createTeam(team);
            
            // Arrange: Create an initial coach
            Coach originalCoach = new Coach(0, originalName, originalPhone, teamNumber);
            int coachID = coachDAO.createCoach(originalCoach);
            
            // Act: Update the coach with new values
            Coach updatedCoach = new Coach(coachID, updatedName, updatedPhone, teamNumber);
            coachDAO.updateCoach(updatedCoach);
            
            // Retrieve the updated coach
            Coach retrievedCoach = coachDAO.getCoachByID(coachID);
            
            // Assert: Retrieved coach should have updated values
            assertNotNull(retrievedCoach, "Retrieved coach should not be null");
            assertEquals(coachID, retrievedCoach.getCoachID(),
                    "CoachID should remain unchanged");
            assertEquals(updatedName, retrievedCoach.getName(),
                    "Name should be updated");
            assertEquals(updatedPhone, retrievedCoach.getTelephoneNumber(),
                    "TelephoneNumber should be updated");
            assertEquals(teamNumber, retrievedCoach.getTeamNumber(),
                    "TeamNumber should remain unchanged");
            
            // Cleanup after test
            cleanupCoach(coachID);
            cleanupTeam(teamNumber);
            
        } catch (DatabaseException e) {
            fail("Coach update and retrieval should succeed for valid data: " + e.getMessage());
        }
    }
    
    /**
     * Property 9: Coach Deletion Cascades to Work Experience
     * 
     * For any existing Coach with associated WorkExperience records, deleting 
     * the coach should also remove all associated WorkExperience records from 
     * the database.
     * 
     * Validates: Requirements 3.4, 9.3, 11.4
     */
    @Property(tries = 100)
    @net.jqwik.api.Tag("Property 9: Coach Deletion Cascades to Work Experience")
    void coachDeletionCascadesToWorkExperience(
            @ForAll @StringLength(min = 1, max = 100) @AlphaChars String coachName,
            @ForAll @StringLength(min = 10, max = 20) @NumericChars String phone,
            @ForAll @IntRange(min = 1, max = 9999) int teamNumber,
            @ForAll @StringLength(min = 1, max = 100) @AlphaChars String experienceType,
            @ForAll @IntRange(min = 1, max = 50) int duration) {
        
        // Setup: Create a team for the coach to reference
        Team team = new Team(teamNumber, "Test Team", "Test City", "Test Manager");
        cleanupTeam(teamNumber);
        
        try {
            teamDAO.createTeam(team);
            
            // Arrange: Create a coach
            Coach coach = new Coach(0, coachName, phone, teamNumber);
            int coachID = coachDAO.createCoach(coach);
            
            // Create work experience for the coach
            WorkExperience experience = new WorkExperience(0, coachID, experienceType, duration);
            workExperienceDAO.createWorkExperience(experience);
            int experienceID = experience.getExperienceID();
            
            // Verify work experience exists before deletion
            List<WorkExperience> beforeDeletion = workExperienceDAO.getExperiencesByCoach(coachID);
            assertFalse(beforeDeletion.isEmpty(), "Work experience should exist before coach deletion");
            
            // Act: Delete the coach (should cascade to work experience)
            coachDAO.deleteCoach(coachID);
            
            // Assert: Coach should no longer exist
            Coach afterDeletion = coachDAO.getCoachByID(coachID);
            assertNull(afterDeletion, "Coach should not exist after deletion");
            
            // Work experience should also be deleted (cascade)
            List<WorkExperience> afterCoachDeletion = workExperienceDAO.getExperiencesByCoach(coachID);
            assertTrue(afterCoachDeletion.isEmpty(), 
                    "Work experience should be deleted when coach is deleted (cascade)");
            
            // Cleanup
            cleanupTeam(teamNumber);
            
        } catch (DatabaseException e) {
            fail("Coach deletion with cascade should succeed: " + e.getMessage());
        }
    }
    
    /**
     * Property 15: Work Experience Association Creation
     * 
     * For any valid Coach and WorkExperience data, creating a WorkExperience 
     * record associated with the CoachID should result in a retrievable work 
     * experience record with correct CoachID and details.
     * 
     * Validates: Requirements 9.1
     */
    @Property(tries = 100)
    @net.jqwik.api.Tag("Property 15: Work Experience Association Creation")
    void workExperienceAssociationCreation(
            @ForAll @StringLength(min = 1, max = 100) @AlphaChars String coachName,
            @ForAll @StringLength(min = 10, max = 20) @NumericChars String phone,
            @ForAll @IntRange(min = 1, max = 9999) int teamNumber,
            @ForAll @StringLength(min = 1, max = 100) @AlphaChars String experienceType,
            @ForAll @IntRange(min = 1, max = 50) int duration) {
        
        // Setup: Create a team and coach
        Team team = new Team(teamNumber, "Test Team", "Test City", "Test Manager");
        cleanupTeam(teamNumber);
        
        try {
            teamDAO.createTeam(team);
            
            Coach coach = new Coach(0, coachName, phone, teamNumber);
            int coachID = coachDAO.createCoach(coach);
            
            // Arrange: Create work experience for the coach
            WorkExperience originalExperience = new WorkExperience(0, coachID, experienceType, duration);
            
            // Act: Create the work experience in the database
            workExperienceDAO.createWorkExperience(originalExperience);
            int experienceID = originalExperience.getExperienceID();
            
            // Retrieve work experiences for the coach
            List<WorkExperience> experiences = workExperienceDAO.getExperiencesByCoach(coachID);
            
            // Assert: Work experience should be retrievable
            assertFalse(experiences.isEmpty(), "Work experience list should not be empty");
            
            WorkExperience retrievedExperience = experiences.stream()
                    .filter(e -> e.getExperienceID() == experienceID)
                    .findFirst()
                    .orElse(null);
            
            assertNotNull(retrievedExperience, "Work experience should be retrievable");
            assertEquals(coachID, retrievedExperience.getCoachID(),
                    "CoachID should match");
            assertEquals(experienceType, retrievedExperience.getExperienceType(),
                    "ExperienceType should match");
            assertEquals(duration, retrievedExperience.getDuration(),
                    "Duration should match");
            
            // Cleanup
            cleanupCoach(coachID);
            cleanupTeam(teamNumber);
            
        } catch (DatabaseException e) {
            fail("Work experience creation and retrieval should succeed: " + e.getMessage());
        }
    }
    
    /**
     * Property 16: Work Experience Validation
     * 
     * For any WorkExperience data with valid ExperienceType and positive 
     * Duration, the system should accept and persist the record.
     * 
     * Validates: Requirements 9.2
     */
    @Property(tries = 100)
    @net.jqwik.api.Tag("Property 16: Work Experience Validation")
    void workExperienceValidation(
            @ForAll @StringLength(min = 1, max = 100) @AlphaChars String coachName,
            @ForAll @StringLength(min = 10, max = 20) @NumericChars String phone,
            @ForAll @IntRange(min = 1, max = 9999) int teamNumber,
            @ForAll @StringLength(min = 1, max = 100) @AlphaChars String experienceType,
            @ForAll @IntRange(min = 1, max = 50) int duration) {
        
        // Setup: Create a team and coach
        Team team = new Team(teamNumber, "Test Team", "Test City", "Test Manager");
        cleanupTeam(teamNumber);
        
        try {
            teamDAO.createTeam(team);
            
            Coach coach = new Coach(0, coachName, phone, teamNumber);
            int coachID = coachDAO.createCoach(coach);
            
            // Arrange: Create work experience with valid data
            WorkExperience experience = new WorkExperience(0, coachID, experienceType, duration);
            
            // Act & Assert: Should succeed for valid data
            assertDoesNotThrow(() -> {
                workExperienceDAO.createWorkExperience(experience);
            }, "Work experience creation should succeed for valid data");
            
            // Verify it was persisted
            List<WorkExperience> experiences = workExperienceDAO.getExperiencesByCoach(coachID);
            assertFalse(experiences.isEmpty(), "Work experience should be persisted");
            
            WorkExperience retrieved = experiences.get(0);
            assertEquals(experienceType, retrieved.getExperienceType(),
                    "ExperienceType should be persisted correctly");
            assertEquals(duration, retrieved.getDuration(),
                    "Duration should be persisted correctly");
            assertTrue(retrieved.getDuration() > 0, "Duration should be positive");
            
            // Cleanup
            cleanupCoach(coachID);
            cleanupTeam(teamNumber);
            
        } catch (DatabaseException e) {
            fail("Work experience validation should succeed for valid data: " + e.getMessage());
        }
    }
    
    /**
     * Property 17: Coach Details Include Work Experience
     * 
     * For any Coach with associated WorkExperience records, retrieving the 
     * coach's details should include all associated work experience records.
     * 
     * Validates: Requirements 9.4
     */
    @Property(tries = 100)
    @net.jqwik.api.Tag("Property 17: Coach Details Include Work Experience")
    void coachDetailsIncludeWorkExperience(
            @ForAll @StringLength(min = 1, max = 100) @AlphaChars String coachName,
            @ForAll @StringLength(min = 10, max = 20) @NumericChars String phone,
            @ForAll @IntRange(min = 1, max = 9999) int teamNumber,
            @ForAll @StringLength(min = 1, max = 100) @AlphaChars String experienceType1,
            @ForAll @IntRange(min = 1, max = 50) int duration1,
            @ForAll @StringLength(min = 1, max = 100) @AlphaChars String experienceType2,
            @ForAll @IntRange(min = 1, max = 50) int duration2) {
        
        // Setup: Create a team and coach
        Team team = new Team(teamNumber, "Test Team", "Test City", "Test Manager");
        cleanupTeam(teamNumber);
        
        try {
            teamDAO.createTeam(team);
            
            Coach coach = new Coach(0, coachName, phone, teamNumber);
            int coachID = coachDAO.createCoach(coach);
            
            // Arrange: Create multiple work experience records for the coach
            WorkExperience experience1 = new WorkExperience(0, coachID, experienceType1, duration1);
            WorkExperience experience2 = new WorkExperience(0, coachID, experienceType2, duration2);
            
            workExperienceDAO.createWorkExperience(experience1);
            workExperienceDAO.createWorkExperience(experience2);
            
            // Act: Retrieve coach and their work experiences
            Coach retrievedCoach = coachDAO.getCoachByID(coachID);
            List<WorkExperience> experiences = workExperienceDAO.getExperiencesByCoach(coachID);
            
            // Assert: Coach should exist and have all work experiences
            assertNotNull(retrievedCoach, "Coach should be retrievable");
            assertEquals(coachID, retrievedCoach.getCoachID(), "CoachID should match");
            
            assertEquals(2, experiences.size(), 
                    "Should retrieve all work experience records for the coach");
            
            // Verify both experiences are present
            boolean hasExperience1 = experiences.stream()
                    .anyMatch(e -> e.getExperienceType().equals(experienceType1) && 
                                   e.getDuration() == duration1);
            boolean hasExperience2 = experiences.stream()
                    .anyMatch(e -> e.getExperienceType().equals(experienceType2) && 
                                   e.getDuration() == duration2);
            
            assertTrue(hasExperience1, "First work experience should be included");
            assertTrue(hasExperience2, "Second work experience should be included");
            
            // All experiences should have correct CoachID
            for (WorkExperience exp : experiences) {
                assertEquals(coachID, exp.getCoachID(), 
                        "All work experiences should be associated with the correct coach");
            }
            
            // Cleanup
            cleanupCoach(coachID);
            cleanupTeam(teamNumber);
            
        } catch (DatabaseException e) {
            fail("Coach details with work experience should be retrievable: " + e.getMessage());
        }
    }
    
    /**
     * Helper method to clean up test data for a coach.
     * Removes work experience records and the coach.
     */
    private void cleanupCoach(int coachID) {
        Connection conn = null;
        Statement stmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            
            // Delete work experience for this coach
            stmt.executeUpdate("DELETE FROM WorkExperience WHERE CoachID = " + coachID);
            
            // Delete the coach
            stmt.executeUpdate("DELETE FROM Coach WHERE CoachID = " + coachID);
            
        } catch (SQLException e) {
            // Log but don't fail - cleanup is best effort
            System.err.println("Warning: Failed to cleanup coach " + coachID + ": " + e.getMessage());
        } finally {
            DatabaseConnection.closeResources(conn, stmt);
        }
    }
    
    /**
     * Helper method to clean up test data for a team.
     * Removes all dependent records and the team.
     */
    private void cleanupTeam(int teamNumber) {
        Connection conn = null;
        Statement stmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            
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
