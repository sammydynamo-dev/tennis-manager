package com.tennisleague.dao;

import com.tennisleague.model.Coach;
import com.tennisleague.exception.DatabaseException;
import java.util.List;

/**
 * Data Access Object interface for Coach entity.
 * Defines CRUD operations and query methods for Coach management.
 */
public interface CoachDAO {
    
    /**
     * Creates a new coach in the database.
     * @param coach The coach to create
     * @return The generated CoachID
     * @throws DatabaseException if database operation fails
     */
    int createCoach(Coach coach) throws DatabaseException;
    
    /**
     * Retrieves a coach by their coach ID.
     * @param coachID The coach ID to search for
     * @return The coach with the specified ID, or null if not found
     * @throws DatabaseException if database operation fails
     */
    Coach getCoachByID(int coachID) throws DatabaseException;
    
    /**
     * Retrieves all coaches from the database.
     * @return List of all coaches
     * @throws DatabaseException if database operation fails
     */
    List<Coach> getAllCoaches() throws DatabaseException;
    
    /**
     * Retrieves all coaches assigned to a specific team.
     * @param teamNumber The team number to filter by
     * @return List of coaches for the specified team
     * @throws DatabaseException if database operation fails
     */
    List<Coach> getCoachesByTeam(int teamNumber) throws DatabaseException;
    
    /**
     * Updates an existing coach in the database.
     * @param coach The coach with updated information
     * @throws DatabaseException if database operation fails
     */
    void updateCoach(Coach coach) throws DatabaseException;
    
    /**
     * Deletes a coach from the database.
     * @param coachID The coach ID to delete
     * @throws DatabaseException if database operation fails
     */
    void deleteCoach(int coachID) throws DatabaseException;
}
