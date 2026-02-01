package com.tennisleague.ui;

import com.tennisleague.exception.ValidationException;
import com.tennisleague.model.*;
import java.time.Year;

/**
 * Utility class for validating entity input data.
 * Validates required fields, data types, formats, and business rules.
 */
public class InputValidator {
    
    private static final int MAX_STRING_LENGTH = 100;
    private static final int MIN_AGE = 1;
    private static final int MAX_AGE = 120;
    private static final int MIN_YEAR = 1900;
    private static final int CURRENT_YEAR = Year.now().getValue();
    
    /**
     * Validates a Team object.
     * 
     * @param team the Team to validate
     * @throws ValidationException if validation fails
     */
    public static void validateTeam(Team team) throws ValidationException {
        if (team == null) {
            throw new ValidationException("Team cannot be null");
        }
        
        // Validate TeamNumber
        if (team.getTeamNumber() <= 0) {
            throw new ValidationException("TeamNumber must be a positive integer");
        }
        
        // Validate Name
        validateRequiredString(team.getName(), "Team Name");
        validateStringLength(team.getName(), "Team Name", MAX_STRING_LENGTH);
        
        // Validate City
        validateRequiredString(team.getCity(), "City");
        validateStringLength(team.getCity(), "City", MAX_STRING_LENGTH);
        
        // Validate ManagerName
        validateRequiredString(team.getManagerName(), "Manager Name");
        validateStringLength(team.getManagerName(), "Manager Name", MAX_STRING_LENGTH);
    }
    
    /**
     * Validates a Player object.
     * 
     * @param player the Player to validate
     * @throws ValidationException if validation fails
     */
    public static void validatePlayer(Player player) throws ValidationException {
        if (player == null) {
            throw new ValidationException("Player cannot be null");
        }
        
        // Validate LeagueWideNumber
        if (player.getLeagueWideNumber() <= 0) {
            throw new ValidationException("LeagueWideNumber must be a positive integer");
        }
        
        // Validate Name
        validateRequiredString(player.getName(), "Player Name");
        validateStringLength(player.getName(), "Player Name", MAX_STRING_LENGTH);
        
        // Validate Age
        if (player.getAge() < MIN_AGE || player.getAge() > MAX_AGE) {
            throw new ValidationException("Age must be between " + MIN_AGE + " and " + MAX_AGE);
        }
    }
    
    /**
     * Validates a Coach object.
     * 
     * @param coach the Coach to validate
     * @throws ValidationException if validation fails
     */
    public static void validateCoach(Coach coach) throws ValidationException {
        if (coach == null) {
            throw new ValidationException("Coach cannot be null");
        }
        
        // Validate Name
        validateRequiredString(coach.getName(), "Coach Name");
        validateStringLength(coach.getName(), "Coach Name", MAX_STRING_LENGTH);
        
        // Validate TelephoneNumber
        validateRequiredString(coach.getTelephoneNumber(), "Telephone Number");
        validatePhoneNumber(coach.getTelephoneNumber());
        
        // Validate TeamNumber
        if (coach.getTeamNumber() <= 0) {
            throw new ValidationException("TeamNumber must be a positive integer");
        }
    }
    
    /**
     * Validates a WorkExperience object.
     * 
     * @param experience the WorkExperience to validate
     * @throws ValidationException if validation fails
     */
    public static void validateWorkExperience(WorkExperience experience) throws ValidationException {
        if (experience == null) {
            throw new ValidationException("WorkExperience cannot be null");
        }
        
        // Validate CoachID
        if (experience.getCoachID() <= 0) {
            throw new ValidationException("CoachID must be a positive integer");
        }
        
        // Validate ExperienceType
        validateRequiredString(experience.getExperienceType(), "Experience Type");
        validateStringLength(experience.getExperienceType(), "Experience Type", MAX_STRING_LENGTH);
        
        // Validate Duration
        if (experience.getDuration() <= 0) {
            throw new ValidationException("Duration must be a positive integer");
        }
    }
    
    /**
     * Validates a PlayerTeamAssociation object.
     * 
     * @param association the PlayerTeamAssociation to validate
     * @throws ValidationException if validation fails
     */
    public static void validateAssociation(PlayerTeamAssociation association) throws ValidationException {
        if (association == null) {
            throw new ValidationException("PlayerTeamAssociation cannot be null");
        }
        
        // Validate PlayerID
        if (association.getPlayerID() <= 0) {
            throw new ValidationException("PlayerID must be a positive integer");
        }
        
        // Validate TeamNumber
        if (association.getTeamNumber() <= 0) {
            throw new ValidationException("TeamNumber must be a positive integer");
        }
        
        // Validate YearJoined
        validateYear(association.getYearJoined(), "Year Joined");
        
        // Validate YearLeft (if provided)
        if (association.getYearLeft() != null) {
            validateYear(association.getYearLeft(), "Year Left");
            
            // YearLeft must be >= YearJoined
            if (association.getYearLeft() < association.getYearJoined()) {
                throw new ValidationException("Year Left must be greater than or equal to Year Joined");
            }
        }
    }
    
    /**
     * Validates that a string is not null or empty.
     * 
     * @param value the string to validate
     * @param fieldName the name of the field for error messages
     * @throws ValidationException if the string is null or empty
     */
    private static void validateRequiredString(String value, String fieldName) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " is required and cannot be empty");
        }
    }
    
    /**
     * Validates that a string does not exceed maximum length.
     * 
     * @param value the string to validate
     * @param fieldName the name of the field for error messages
     * @param maxLength the maximum allowed length
     * @throws ValidationException if the string exceeds maximum length
     */
    private static void validateStringLength(String value, String fieldName, int maxLength) throws ValidationException {
        if (value != null && value.length() > maxLength) {
            throw new ValidationException(fieldName + " cannot exceed " + maxLength + " characters");
        }
    }
    
    /**
     * Validates a phone number format.
     * Allows digits, spaces, dashes, parentheses, and plus sign.
     * 
     * @param phoneNumber the phone number to validate
     * @throws ValidationException if the phone number format is invalid
     */
    private static void validatePhoneNumber(String phoneNumber) throws ValidationException {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new ValidationException("Telephone Number is required");
        }
        
        // Allow digits, spaces, dashes, parentheses, and plus sign
        if (!phoneNumber.matches("^[0-9\\s\\-()+]+$")) {
            throw new ValidationException("Telephone Number contains invalid characters. Only digits, spaces, dashes, parentheses, and plus sign are allowed");
        }
        
        // Ensure there are at least some digits
        if (!phoneNumber.matches(".*\\d.*")) {
            throw new ValidationException("Telephone Number must contain at least one digit");
        }
    }
    
    /**
     * Validates a year value.
     * 
     * @param year the year to validate
     * @param fieldName the name of the field for error messages
     * @throws ValidationException if the year is invalid
     */
    private static void validateYear(int year, String fieldName) throws ValidationException {
        if (year < MIN_YEAR || year > CURRENT_YEAR) {
            throw new ValidationException(fieldName + " must be between " + MIN_YEAR + " and " + CURRENT_YEAR);
        }
    }
}
