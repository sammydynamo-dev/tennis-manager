package com.tennisleague.ui;

import com.tennisleague.exception.ValidationException;
import com.tennisleague.model.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for InputValidator class.
 * Tests validation rules for all entity types.
 */
public class InputValidatorTest {
    
    // Team Validation Tests
    
    @Test
    public void testValidateTeam_ValidTeam_NoException() {
        Team team = new Team(1, "Eagles", "Boston", "John Smith");
        assertDoesNotThrow(() -> InputValidator.validateTeam(team));
    }
    
    @Test
    public void testValidateTeam_NullTeam_ThrowsException() {
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> InputValidator.validateTeam(null));
        assertEquals("Team cannot be null", exception.getMessage());
    }
    
    @Test
    public void testValidateTeam_InvalidTeamNumber_ThrowsException() {
        Team team = new Team(0, "Eagles", "Boston", "John Smith");
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> InputValidator.validateTeam(team));
        assertEquals("TeamNumber must be a positive integer", exception.getMessage());
    }
    
    @Test
    public void testValidateTeam_NullName_ThrowsException() {
        Team team = new Team(1, null, "Boston", "John Smith");
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> InputValidator.validateTeam(team));
        assertTrue(exception.getMessage().contains("Team Name"));
    }
    
    @Test
    public void testValidateTeam_EmptyName_ThrowsException() {
        Team team = new Team(1, "  ", "Boston", "John Smith");
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> InputValidator.validateTeam(team));
        assertTrue(exception.getMessage().contains("Team Name"));
    }
    
    @Test
    public void testValidateTeam_NullCity_ThrowsException() {
        Team team = new Team(1, "Eagles", null, "John Smith");
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> InputValidator.validateTeam(team));
        assertTrue(exception.getMessage().contains("City"));
    }
    
    @Test
    public void testValidateTeam_NullManagerName_ThrowsException() {
        Team team = new Team(1, "Eagles", "Boston", null);
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> InputValidator.validateTeam(team));
        assertTrue(exception.getMessage().contains("Manager Name"));
    }
    
    @Test
    public void testValidateTeam_NameTooLong_ThrowsException() {
        String longName = "A".repeat(101);
        Team team = new Team(1, longName, "Boston", "John Smith");
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> InputValidator.validateTeam(team));
        assertTrue(exception.getMessage().contains("cannot exceed 100 characters"));
    }
    
    // Player Validation Tests
    
    @Test
    public void testValidatePlayer_ValidPlayer_NoException() {
        Player player = new Player(1, 100, "Jane Doe", 25);
        assertDoesNotThrow(() -> InputValidator.validatePlayer(player));
    }
    
    @Test
    public void testValidatePlayer_NullPlayer_ThrowsException() {
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> InputValidator.validatePlayer(null));
        assertEquals("Player cannot be null", exception.getMessage());
    }
    
    @Test
    public void testValidatePlayer_InvalidLeagueWideNumber_ThrowsException() {
        Player player = new Player(1, -5, "Jane Doe", 25);
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> InputValidator.validatePlayer(player));
        assertEquals("LeagueWideNumber must be a positive integer", exception.getMessage());
    }
    
    @Test
    public void testValidatePlayer_NullName_ThrowsException() {
        Player player = new Player(1, 100, null, 25);
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> InputValidator.validatePlayer(player));
        assertTrue(exception.getMessage().contains("Player Name"));
    }
    
    @Test
    public void testValidatePlayer_AgeTooLow_ThrowsException() {
        Player player = new Player(1, 100, "Jane Doe", 0);
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> InputValidator.validatePlayer(player));
        assertTrue(exception.getMessage().contains("Age must be between"));
    }
    
    @Test
    public void testValidatePlayer_AgeTooHigh_ThrowsException() {
        Player player = new Player(1, 100, "Jane Doe", 150);
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> InputValidator.validatePlayer(player));
        assertTrue(exception.getMessage().contains("Age must be between"));
    }
    
    @Test
    public void testValidatePlayer_AgeAtBoundary_NoException() {
        Player player1 = new Player(1, 100, "Jane Doe", 1);
        Player player2 = new Player(2, 101, "John Doe", 120);
        assertDoesNotThrow(() -> InputValidator.validatePlayer(player1));
        assertDoesNotThrow(() -> InputValidator.validatePlayer(player2));
    }
    
    // Coach Validation Tests
    
    @Test
    public void testValidateCoach_ValidCoach_NoException() {
        Coach coach = new Coach(1, "Mike Johnson", "555-1234", 1);
        assertDoesNotThrow(() -> InputValidator.validateCoach(coach));
    }
    
    @Test
    public void testValidateCoach_NullCoach_ThrowsException() {
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> InputValidator.validateCoach(null));
        assertEquals("Coach cannot be null", exception.getMessage());
    }
    
    @Test
    public void testValidateCoach_NullName_ThrowsException() {
        Coach coach = new Coach(1, null, "555-1234", 1);
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> InputValidator.validateCoach(coach));
        assertTrue(exception.getMessage().contains("Coach Name"));
    }
    
    @Test
    public void testValidateCoach_NullTelephoneNumber_ThrowsException() {
        Coach coach = new Coach(1, "Mike Johnson", null, 1);
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> InputValidator.validateCoach(coach));
        assertTrue(exception.getMessage().contains("Telephone Number"));
    }
    
    @Test
    public void testValidateCoach_EmptyTelephoneNumber_ThrowsException() {
        Coach coach = new Coach(1, "Mike Johnson", "  ", 1);
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> InputValidator.validateCoach(coach));
        assertTrue(exception.getMessage().contains("Telephone Number"));
    }
    
    @Test
    public void testValidateCoach_InvalidPhoneFormat_ThrowsException() {
        Coach coach = new Coach(1, "Mike Johnson", "abc-defg", 1);
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> InputValidator.validateCoach(coach));
        assertTrue(exception.getMessage().contains("invalid characters"));
    }
    
    @Test
    public void testValidateCoach_ValidPhoneFormats_NoException() {
        Coach coach1 = new Coach(1, "Mike Johnson", "555-1234", 1);
        Coach coach2 = new Coach(2, "Jane Smith", "(555) 123-4567", 1);
        Coach coach3 = new Coach(3, "Bob Brown", "+1 555 123 4567", 1);
        Coach coach4 = new Coach(4, "Alice Green", "5551234567", 1);
        
        assertDoesNotThrow(() -> InputValidator.validateCoach(coach1));
        assertDoesNotThrow(() -> InputValidator.validateCoach(coach2));
        assertDoesNotThrow(() -> InputValidator.validateCoach(coach3));
        assertDoesNotThrow(() -> InputValidator.validateCoach(coach4));
    }
    
    @Test
    public void testValidateCoach_PhoneWithoutDigits_ThrowsException() {
        Coach coach = new Coach(1, "Mike Johnson", "---", 1);
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> InputValidator.validateCoach(coach));
        assertTrue(exception.getMessage().contains("must contain at least one digit"));
    }
    
    @Test
    public void testValidateCoach_InvalidTeamNumber_ThrowsException() {
        Coach coach = new Coach(1, "Mike Johnson", "555-1234", 0);
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> InputValidator.validateCoach(coach));
        assertEquals("TeamNumber must be a positive integer", exception.getMessage());
    }
    
    // WorkExperience Validation Tests
    
    @Test
    public void testValidateWorkExperience_ValidExperience_NoException() {
        WorkExperience experience = new WorkExperience(1, 1, "Head Coach", 5);
        assertDoesNotThrow(() -> InputValidator.validateWorkExperience(experience));
    }
    
    @Test
    public void testValidateWorkExperience_NullExperience_ThrowsException() {
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> InputValidator.validateWorkExperience(null));
        assertEquals("WorkExperience cannot be null", exception.getMessage());
    }
    
    @Test
    public void testValidateWorkExperience_InvalidCoachID_ThrowsException() {
        WorkExperience experience = new WorkExperience(1, -1, "Head Coach", 5);
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> InputValidator.validateWorkExperience(experience));
        assertEquals("CoachID must be a positive integer", exception.getMessage());
    }
    
    @Test
    public void testValidateWorkExperience_NullExperienceType_ThrowsException() {
        WorkExperience experience = new WorkExperience(1, 1, null, 5);
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> InputValidator.validateWorkExperience(experience));
        assertTrue(exception.getMessage().contains("Experience Type"));
    }
    
    @Test
    public void testValidateWorkExperience_EmptyExperienceType_ThrowsException() {
        WorkExperience experience = new WorkExperience(1, 1, "", 5);
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> InputValidator.validateWorkExperience(experience));
        assertTrue(exception.getMessage().contains("Experience Type"));
    }
    
    @Test
    public void testValidateWorkExperience_NegativeDuration_ThrowsException() {
        WorkExperience experience = new WorkExperience(1, 1, "Head Coach", -5);
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> InputValidator.validateWorkExperience(experience));
        assertEquals("Duration must be a positive integer", exception.getMessage());
    }
    
    @Test
    public void testValidateWorkExperience_ZeroDuration_ThrowsException() {
        WorkExperience experience = new WorkExperience(1, 1, "Head Coach", 0);
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> InputValidator.validateWorkExperience(experience));
        assertEquals("Duration must be a positive integer", exception.getMessage());
    }
    
    // PlayerTeamAssociation Validation Tests
    
    @Test
    public void testValidateAssociation_ValidAssociation_NoException() {
        PlayerTeamAssociation association = new PlayerTeamAssociation(1, 1, 1, 2020, null);
        assertDoesNotThrow(() -> InputValidator.validateAssociation(association));
    }
    
    @Test
    public void testValidateAssociation_ValidAssociationWithYearLeft_NoException() {
        PlayerTeamAssociation association = new PlayerTeamAssociation(1, 1, 1, 2020, 2022);
        assertDoesNotThrow(() -> InputValidator.validateAssociation(association));
    }
    
    @Test
    public void testValidateAssociation_NullAssociation_ThrowsException() {
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> InputValidator.validateAssociation(null));
        assertEquals("PlayerTeamAssociation cannot be null", exception.getMessage());
    }
    
    @Test
    public void testValidateAssociation_InvalidPlayerID_ThrowsException() {
        PlayerTeamAssociation association = new PlayerTeamAssociation(1, 0, 1, 2020, null);
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> InputValidator.validateAssociation(association));
        assertEquals("PlayerID must be a positive integer", exception.getMessage());
    }
    
    @Test
    public void testValidateAssociation_InvalidTeamNumber_ThrowsException() {
        PlayerTeamAssociation association = new PlayerTeamAssociation(1, 1, -1, 2020, null);
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> InputValidator.validateAssociation(association));
        assertEquals("TeamNumber must be a positive integer", exception.getMessage());
    }
    
    @Test
    public void testValidateAssociation_YearJoinedTooEarly_ThrowsException() {
        PlayerTeamAssociation association = new PlayerTeamAssociation(1, 1, 1, 1800, null);
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> InputValidator.validateAssociation(association));
        assertTrue(exception.getMessage().contains("Year Joined must be between"));
    }
    
    @Test
    public void testValidateAssociation_YearJoinedInFuture_ThrowsException() {
        PlayerTeamAssociation association = new PlayerTeamAssociation(1, 1, 1, 2100, null);
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> InputValidator.validateAssociation(association));
        assertTrue(exception.getMessage().contains("Year Joined must be between"));
    }
    
    @Test
    public void testValidateAssociation_YearLeftBeforeYearJoined_ThrowsException() {
        PlayerTeamAssociation association = new PlayerTeamAssociation(1, 1, 1, 2020, 2019);
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> InputValidator.validateAssociation(association));
        assertEquals("Year Left must be greater than or equal to Year Joined", exception.getMessage());
    }
    
    @Test
    public void testValidateAssociation_YearLeftEqualsYearJoined_NoException() {
        PlayerTeamAssociation association = new PlayerTeamAssociation(1, 1, 1, 2020, 2020);
        assertDoesNotThrow(() -> InputValidator.validateAssociation(association));
    }
    
    @Test
    public void testValidateAssociation_YearLeftInFuture_ThrowsException() {
        PlayerTeamAssociation association = new PlayerTeamAssociation(1, 1, 1, 2020, 2100);
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> InputValidator.validateAssociation(association));
        assertTrue(exception.getMessage().contains("Year Left must be between"));
    }
}
