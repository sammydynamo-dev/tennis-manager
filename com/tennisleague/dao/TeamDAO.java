package com.tennisleague.dao;

import com.tennisleague.model.Team;
import com.tennisleague.exception.DatabaseException;
import java.util.List;

/**
 * Data Access Object interface for Team entity.
 * Defines CRUD operations and query methods for Team management.
 */
public interface TeamDAO {
    
    /**
     * Creates a new team in the database.
     * @param team The team to create
     * @throws DatabaseException if database operation fails
     */
    void createTeam(Team team) throws DatabaseException;
    
    /**
     * Retrieves a team by its team number.
     * @param teamNumber The team number to search for
     * @return The team with the specified number, or null if not found
     * @throws DatabaseException if database operation fails
     */
    Team getTeamByNumber(int teamNumber) throws DatabaseException;
    
    /**
     * Retrieves all teams from the database.
     * @return List of all teams
     * @throws DatabaseException if database operation fails
     */
    List<Team> getAllTeams() throws DatabaseException;
    
    /**
     * Updates an existing team in the database.
     * @param team The team with updated information
     * @throws DatabaseException if database operation fails
     */
    void updateTeam(Team team) throws DatabaseException;
    
    /**
     * Deletes a team from the database.
     * @param teamNumber The team number to delete
     * @throws DatabaseException if database operation fails
     */
    void deleteTeam(int teamNumber) throws DatabaseException;
    
    /**
     * Checks if a team exists with the given team number.
     * @param teamNumber The team number to check
     * @return true if team exists, false otherwise
     * @throws DatabaseException if database operation fails
     */
    boolean teamExists(int teamNumber) throws DatabaseException;
}
