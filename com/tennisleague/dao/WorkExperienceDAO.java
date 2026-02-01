package com.tennisleague.dao;

import com.tennisleague.model.WorkExperience;
import com.tennisleague.exception.DatabaseException;
import java.util.List;

/**
 * Data Access Object interface for WorkExperience entity.
 * Defines CRUD operations and query methods for WorkExperience management.
 */
public interface WorkExperienceDAO {
    
    /**
     * Creates a new work experience record in the database.
     * @param experience The work experience to create
     * @throws DatabaseException if database operation fails
     */
    void createWorkExperience(WorkExperience experience) throws DatabaseException;
    
    /**
     * Retrieves all work experience records for a specific coach.
     * @param coachID The coach ID to filter by
     * @return List of work experience records for the specified coach
     * @throws DatabaseException if database operation fails
     */
    List<WorkExperience> getExperiencesByCoach(int coachID) throws DatabaseException;
    
    /**
     * Updates an existing work experience record in the database.
     * @param experience The work experience with updated information
     * @throws DatabaseException if database operation fails
     */
    void updateWorkExperience(WorkExperience experience) throws DatabaseException;
    
    /**
     * Deletes a work experience record from the database.
     * @param experienceID The experience ID to delete
     * @throws DatabaseException if database operation fails
     */
    void deleteWorkExperience(int experienceID) throws DatabaseException;
    
    /**
     * Deletes all work experience records for a specific coach.
     * Used for cascading deletes when a coach is removed.
     * @param coachID The coach ID whose experiences should be deleted
     * @throws DatabaseException if database operation fails
     */
    void deleteExperiencesByCoach(int coachID) throws DatabaseException;
}
