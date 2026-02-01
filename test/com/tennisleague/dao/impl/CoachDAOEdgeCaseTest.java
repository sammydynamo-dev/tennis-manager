package com.tennisleague.dao.impl;

import com.tennisleague.dao.CoachDAO;
import com.tennisleague.dao.TeamDAO;
import com.tennisleague.dao.WorkExperienceDAO;
import com.tennisleague.model.Coach;
import com.tennisleague.model.Team;
import com.tennisleague.model.WorkExperience;
import com.tennisleague.exception.DatabaseException;
import com.tennisleague.database.DatabaseConnection;

import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CoachDAO and WorkExperienceDAO edge cases.
 * Tests specific scenarios including:
 * - Coach assignment to non-existent team
 * - Invalid telephone number format (at DAO level)
 * - Invalid work experience duration
 * 
 * Validates: Requirements 3.2, 3.6, 9.5
 */
class CoachDAOEdgeCaseTest {
    
    private CoachDAO coachDAO;
    private TeamDAO teamDAO;
    private WorkExperienceDAO workExperienceDAO;
    private static final int TEST_TEAM_NUMBER = 9998;
    private static final int NON_EXISTENT_TEAM_NUMBER = 99999;
    
    @BeforeEach
    void setUp() {
        coachDAO = new CoachDAOImpl();
        teamDAO = new TeamDAOImpl();
        workExperienceDAO = new WorkExperienceDAOImpl();
        cleanupTestData();
    }
    
    @AfterEach
    void tearDown() {
        cleanupTestData();
    }
    
    /**
     * Test: Coach Assignment to Non-existent Team
     * 
     * WHEN a user attempts to assign a coach to a non-existent team,
     * THEN the system SHALL reject the operation and return a descriptive error message.
     * 
     * Validates: Requirement 3.2
     */
    @Test
    @DisplayName("Should reject coach assignment to non-existent team")
    void testCoachAssignmentToNonExistentTeam() {
        // Arrange: Create coach with non-existent team reference
        Coach coach = new Coach(0, "John Coach", "555-1234", NON_EXISTENT_TEAM_NUMBER);
        
        // Act & Assert: Attempt to create coach with invalid team reference
        DatabaseException exception = assertThrows(DatabaseException.class, () -> {
            coachDAO.createCoach(coach);
        }, "Creating coach with non-existent team should throw DatabaseException");
        
        // Verify error message indicates foreign key constraint violation
        String errorMessage = exception.getMessage().toLowerCase();
        assertTrue(errorMessage.contains("foreign key") || 
                  errorMessage.contains("constraint") ||
                  errorMessage.contains("cannot add"),
                  "Error message should indicate foreign key constraint violation: " + exception.getMessage());
    }
    
    /**
     * Test: Invalid Telephone Number Format (DAO Level)
     * 
     * Note: The DAO layer accepts any string for telephone number as the database
     * schema allows VARCHAR. Business logic validation should happen at service layer.
     * This test verifies that the DAO layer doesn't reject valid database values.
     * 
     * Validates: Requirement 3.6 (service layer validation)
     */
    @Test
    @DisplayName("Should accept various telephone number formats at DAO level")
    void testTelephoneNumberFormatAtDAOLevel() {
        // Setup: Create a test team
        Team team = new Team(TEST_TEAM_NUMBER, "Test Team", "Test City", "Test Manager");
        
        try {
            teamDAO.createTeam(team);
            
            // Test various telephone number formats
            String[] phoneNumbers = {
                "555-1234",           // Standard format
                "(555) 123-4567",     // With parentheses
                "5551234567",         // No formatting
                "+1-555-123-4567",    // International format
                "invalid",            // Invalid format (should be caught at service layer)
                ""                    // Empty string (should be caught at service layer)
            };
            
            for (int i = 0; i < phoneNumbers.length; i++) {
                final String phoneNumber = phoneNumbers[i];
                Coach coach = new Coach(0, "Coach " + i, phoneNumber, TEST_TEAM_NUMBER);
                
                // Act: DAO should accept any string value
                assertDoesNotThrow(() -> {
                    int coachID = coachDAO.createCoach(coach);
                    Coach retrieved = coachDAO.getCoachByID(coachID);
                    assertEquals(phoneNumber, retrieved.getTelephoneNumber(),
                            "Telephone number should be persisted as-is");
                    coachDAO.deleteCoach(coachID);
                }, "DAO should accept any telephone number format allowed by database schema");
            }
            
        } catch (DatabaseException e) {
            fail("DAO layer should accept values allowed by database schema: " + e.getMessage());
        }
    }
    
    /**
     * Test: Null Telephone Number Handling
     * 
     * WHEN a user provides a null telephone number,
     * THEN the DAO layer SHALL accept it (database allows NULL).
     * Note: Business logic validation should happen at service layer.
     * 
     * Validates: Requirement 3.6
     */
    @Test
    @DisplayName("Should accept null telephone number at DAO level")
    void testNullTelephoneNumberHandling() {
        // Setup: Create a test team
        Team team = new Team(TEST_TEAM_NUMBER, "Test Team", "Test City", "Test Manager");
        
        try {
            teamDAO.createTeam(team);
            
            // Arrange: Create coach with null telephone number
            Coach coach = new Coach(0, "John Coach", null, TEST_TEAM_NUMBER);
            
            // Act: Create coach with null telephone number
            int coachID = coachDAO.createCoach(coach);
            
            // Assert: Coach should be created (DAO doesn't validate business rules)
            Coach retrieved = coachDAO.getCoachByID(coachID);
            assertNotNull(retrieved, "Coach with null telephone number should be created at DAO level");
            assertNull(retrieved.getTelephoneNumber(), "Null telephone number should be persisted");
            
        } catch (DatabaseException e) {
            fail("DAO layer should accept null values allowed by database schema: " + e.getMessage());
        }
    }
    
    /**
     * Test: Invalid Work Experience Duration (Negative Value)
     * 
     * Note: The DAO layer accepts any integer for duration as the database
     * schema allows INT. Business logic validation should happen at service layer.
     * This test verifies that the DAO layer doesn't reject valid database values.
     * 
     * Validates: Requirement 9.5 (service layer validation)
     */
    @Test
    @DisplayName("Should accept negative duration at DAO level")
    void testNegativeDurationAtDAOLevel() {
        // Setup: Create a test team and coach
        Team team = new Team(TEST_TEAM_NUMBER, "Test Team", "Test City", "Test Manager");
        
        try {
            teamDAO.createTeam(team);
            
            Coach coach = new Coach(0, "John Coach", "555-1234", TEST_TEAM_NUMBER);
            int coachID = coachDAO.createCoach(coach);
            
            // Arrange: Create work experience with negative duration
            WorkExperience experience = new WorkExperience(0, coachID, "Head Coach", -5);
            
            // Act: DAO should accept negative values (database allows INT)
            assertDoesNotThrow(() -> {
                workExperienceDAO.createWorkExperience(experience);
                
                // Verify it was persisted
                var experiences = workExperienceDAO.getExperiencesByCoach(coachID);
                assertFalse(experiences.isEmpty(), "Work experience should be persisted");
                assertEquals(-5, experiences.get(0).getDuration(),
                        "Negative duration should be persisted (validation should happen at service layer)");
            }, "DAO should accept negative duration values allowed by database schema");
            
        } catch (DatabaseException e) {
            fail("DAO layer should accept values allowed by database schema: " + e.getMessage());
        }
    }
    
    /**
     * Test: Zero Duration Work Experience
     * 
     * WHEN a user provides zero duration for work experience,
     * THEN the DAO layer SHALL accept it (database allows any INT).
     * Note: Business logic validation should happen at service layer.
     * 
     * Validates: Requirement 9.5
     */
    @Test
    @DisplayName("Should accept zero duration at DAO level")
    void testZeroDurationAtDAOLevel() {
        // Setup: Create a test team and coach
        Team team = new Team(TEST_TEAM_NUMBER, "Test Team", "Test City", "Test Manager");
        
        try {
            teamDAO.createTeam(team);
            
            Coach coach = new Coach(0, "John Coach", "555-1234", TEST_TEAM_NUMBER);
            int coachID = coachDAO.createCoach(coach);
            
            // Arrange: Create work experience with zero duration
            WorkExperience experience = new WorkExperience(0, coachID, "Assistant Coach", 0);
            
            // Act: DAO should accept zero values
            assertDoesNotThrow(() -> {
                workExperienceDAO.createWorkExperience(experience);
                
                // Verify it was persisted
                var experiences = workExperienceDAO.getExperiencesByCoach(coachID);
                assertFalse(experiences.isEmpty(), "Work experience should be persisted");
                assertEquals(0, experiences.get(0).getDuration(),
                        "Zero duration should be persisted (validation should happen at service layer)");
            }, "DAO should accept zero duration values allowed by database schema");
            
        } catch (DatabaseException e) {
            fail("DAO layer should accept values allowed by database schema: " + e.getMessage());
        }
    }
    
    /**
     * Test: Null Experience Type Handling
     * 
     * WHEN a user provides a null experience type,
     * THEN the DAO layer SHALL accept it (database allows NULL).
     * Note: Business logic validation should happen at service layer.
     * 
     * Validates: Requirement 9.5
     */
    @Test
    @DisplayName("Should accept null experience type at DAO level")
    void testNullExperienceTypeHandling() {
        // Setup: Create a test team and coach
        Team team = new Team(TEST_TEAM_NUMBER, "Test Team", "Test City", "Test Manager");
        
        try {
            teamDAO.createTeam(team);
            
            Coach coach = new Coach(0, "John Coach", "555-1234", TEST_TEAM_NUMBER);
            int coachID = coachDAO.createCoach(coach);
            
            // Arrange: Create work experience with null experience type
            WorkExperience experience = new WorkExperience(0, coachID, null, 5);
            
            // Act: Create work experience with null experience type
            workExperienceDAO.createWorkExperience(experience);
            
            // Assert: Work experience should be created (DAO doesn't validate business rules)
            var experiences = workExperienceDAO.getExperiencesByCoach(coachID);
            assertFalse(experiences.isEmpty(), "Work experience with null type should be created at DAO level");
            assertNull(experiences.get(0).getExperienceType(), "Null experience type should be persisted");
            
        } catch (DatabaseException e) {
            fail("DAO layer should accept null values allowed by database schema: " + e.getMessage());
        }
    }
    
    /**
     * Test: Get Coaches by Non-existent Team
     * 
     * WHEN requesting coaches for a non-existent team,
     * THEN the system SHALL return an empty list.
     * 
     * Validates: Requirement 3.6
     */
    @Test
    @DisplayName("Should return empty list for coaches of non-existent team")
    void testGetCoachesByNonExistentTeam() {
        try {
            // Act: Get coaches for non-existent team
            var coaches = coachDAO.getCoachesByTeam(NON_EXISTENT_TEAM_NUMBER);
            
            // Assert: Should return empty list
            assertNotNull(coaches, "Result should not be null");
            assertTrue(coaches.isEmpty(), "Should return empty list for non-existent team");
            
        } catch (DatabaseException e) {
            fail("Getting coaches for non-existent team should not throw exception: " + e.getMessage());
        }
    }
    
    /**
     * Test: Get Work Experiences for Non-existent Coach
     * 
     * WHEN requesting work experiences for a non-existent coach,
     * THEN the system SHALL return an empty list.
     * 
     * Validates: Requirement 9.5
     */
    @Test
    @DisplayName("Should return empty list for work experiences of non-existent coach")
    void testGetWorkExperiencesForNonExistentCoach() {
        try {
            // Act: Get work experiences for non-existent coach
            var experiences = workExperienceDAO.getExperiencesByCoach(99999);
            
            // Assert: Should return empty list
            assertNotNull(experiences, "Result should not be null");
            assertTrue(experiences.isEmpty(), "Should return empty list for non-existent coach");
            
        } catch (DatabaseException e) {
            fail("Getting work experiences for non-existent coach should not throw exception: " + e.getMessage());
        }
    }
    
    /**
     * Helper method to clean up test data from the database.
     * Ensures test isolation by removing any test teams, coaches, and work experiences.
     */
    private void cleanupTestData() {
        Connection conn = null;
        Statement stmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            
            // Delete test data in proper order (respecting foreign key constraints)
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
