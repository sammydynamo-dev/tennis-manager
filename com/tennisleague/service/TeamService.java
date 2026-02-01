package com.tennisleague.service;

import com.tennisleague.model.Team;
import com.tennisleague.exception.ValidationException;
import com.tennisleague.exception.DatabaseException;
import com.tennisleague.exception.EntityNotFoundException;
import java.util.List;

/**
 * Service interface for Team business operations.
 * Provides methods for managing teams with validation and error handling.
 */
public interface TeamService {
    
    /**
     * Adds a new team to the system.
     * 
     * @param team the team to add
     * @throws ValidationException if team data is invalid
     * @throws DatabaseException if a database error occurs
     */
    void addTeam(Team team) throws ValidationException, DatabaseException;
    
    /**
     * Retrieves a team by its team number.
     * 
     * @param teamNumber the team number
     * @return the team with the specified number
     * @throws EntityNotFoundException if the team does not exist
     * @throws DatabaseException if a database error occurs
     */
    Team getTeam(int teamNumber) throws EntityNotFoundException, DatabaseException;
    
    /**
     * Retrieves all teams in the system.
     * 
     * @return a list of all teams
     * @throws DatabaseException if a database error occurs
     */
    List<Team> getAllTeams() throws DatabaseException;
    
    /**
     * Updates an existing team's information.
     * 
     * @param team the team with updated information
     * @throws ValidationException if team data is invalid
     * @throws EntityNotFoundException if the team does not exist
     * @throws DatabaseException if a database error occurs
     */
    void updateTeam(Team team) throws ValidationException, EntityNotFoundException, DatabaseException;
    
    /**
     * Deletes a team from the system.
     * 
     * @param teamNumber the team number of the team to delete
     * @throws EntityNotFoundException if the team does not exist
     * @throws DatabaseException if a database error occurs
     */
    void deleteTeam(int teamNumber) throws EntityNotFoundException, DatabaseException;
}
