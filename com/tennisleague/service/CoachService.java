package com.tennisleague.service;

import com.tennisleague.model.Coach;
import com.tennisleague.model.WorkExperience;
import com.tennisleague.exception.ValidationException;
import com.tennisleague.exception.DatabaseException;
import com.tennisleague.exception.EntityNotFoundException;
import java.util.List;

/**
 * Service interface for Coach business operations.
 * Provides methods for managing coaches and their work experience.
 */
public interface CoachService {
    
    /**
     * Adds a new coach to the system.
     * 
     * @param coach the coach to add
     * @return the generated coach ID
     * @throws ValidationException if coach data is invalid
     * @throws DatabaseException if a database error occurs
     */
    int addCoach(Coach coach) throws ValidationException, DatabaseException;
    
    /**
     * Retrieves a coach by their ID.
     * 
     * @param coachID the coach ID
     * @return the coach with the specified ID
     * @throws EntityNotFoundException if the coach does not exist
     * @throws DatabaseException if a database error occurs
     */
    Coach getCoach(int coachID) throws EntityNotFoundException, DatabaseException;
    
    /**
     * Retrieves all coaches in the system.
     * 
     * @return a list of all coaches
     * @throws DatabaseException if a database error occurs
     */
    List<Coach> getAllCoaches() throws DatabaseException;
    
    /**
     * Retrieves all coaches assigned to a specific team.
     * 
     * @param teamNumber the team number to filter by
     * @return a list of coaches for the specified team
     * @throws DatabaseException if a database error occurs
     */
    List<Coach> getCoachesByTeam(int teamNumber) throws DatabaseException;
    
    /**
     * Updates an existing coach's information.
     * 
     * @param coach the coach with updated information
     * @throws ValidationException if coach data is invalid
     * @throws EntityNotFoundException if the coach does not exist
     * @throws DatabaseException if a database error occurs
     */
    void updateCoach(Coach coach) throws ValidationException, EntityNotFoundException, DatabaseException;
    
    /**
     * Deletes a coach from the system.
     * 
     * @param coachID the ID of the coach to delete
     * @throws EntityNotFoundException if the coach does not exist
     * @throws DatabaseException if a database error occurs
     */
    void deleteCoach(int coachID) throws EntityNotFoundException, DatabaseException;
    
    /**
     * Adds work experience for a coach.
     * 
     * @param coachID the coach ID
     * @param experience the work experience to add
     * @throws ValidationException if work experience data is invalid
     * @throws EntityNotFoundException if the coach does not exist
     * @throws DatabaseException if a database error occurs
     */
    void addWorkExperience(int coachID, WorkExperience experience) 
        throws ValidationException, EntityNotFoundException, DatabaseException;
    
    /**
     * Retrieves all work experience records for a coach.
     * 
     * @param coachID the coach ID
     * @return a list of work experience records for the coach
     * @throws DatabaseException if a database error occurs
     */
    List<WorkExperience> getCoachExperience(int coachID) throws DatabaseException;
}
