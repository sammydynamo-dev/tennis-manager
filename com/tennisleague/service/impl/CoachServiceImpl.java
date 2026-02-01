package com.tennisleague.service.impl;

import com.tennisleague.service.CoachService;
import com.tennisleague.model.Coach;
import com.tennisleague.model.WorkExperience;
import com.tennisleague.dao.CoachDAO;
import com.tennisleague.dao.WorkExperienceDAO;
import com.tennisleague.dao.TeamDAO;
import com.tennisleague.ui.InputValidator;
import com.tennisleague.exception.ValidationException;
import com.tennisleague.exception.DatabaseException;
import com.tennisleague.exception.EntityNotFoundException;
import java.util.List;

/**
 * Implementation of CoachService interface.
 * Provides business logic for coach management and work experience operations.
 */
public class CoachServiceImpl implements CoachService {
    
    private final CoachDAO coachDAO;
    private final WorkExperienceDAO workExperienceDAO;
    private final TeamDAO teamDAO;
    
    /**
     * Constructs a CoachServiceImpl with required DAO dependencies.
     * 
     * @param coachDAO the CoachDAO implementation
     * @param workExperienceDAO the WorkExperienceDAO implementation
     * @param teamDAO the TeamDAO implementation for foreign key validation
     */
    public CoachServiceImpl(CoachDAO coachDAO, WorkExperienceDAO workExperienceDAO, TeamDAO teamDAO) {
        this.coachDAO = coachDAO;
        this.workExperienceDAO = workExperienceDAO;
        this.teamDAO = teamDAO;
    }
    
    @Override
    public int addCoach(Coach coach) throws ValidationException, DatabaseException {
        try {
            // Validate input
            InputValidator.validateCoach(coach);
            
            // Validate foreign key reference - TeamNumber must exist
            if (!teamDAO.teamExists(coach.getTeamNumber())) {
                ValidationException ex = new ValidationException("Team with TeamNumber " + coach.getTeamNumber() + " does not exist");
                System.err.println("ValidationException in addCoach: " + ex.getMessage());
                throw ex;
            }
            
            return coachDAO.createCoach(coach);
        } catch (ValidationException e) {
            System.err.println("ValidationException in addCoach: " + e.getMessage());
            throw e;
        } catch (DatabaseException e) {
            System.err.println("DatabaseException in addCoach: " + e.getMessage());
            DatabaseException ex = new DatabaseException("Failed to add coach: " + e.getMessage(), e);
            System.err.println("Throwing DatabaseException: " + ex.getMessage());
            throw ex;
        }
    }
    
    @Override
    public Coach getCoach(int coachID) throws EntityNotFoundException, DatabaseException {
        try {
            Coach coach = coachDAO.getCoachByID(coachID);
            if (coach == null) {
                EntityNotFoundException ex = new EntityNotFoundException("Coach with CoachID " + coachID + " not found");
                System.err.println("EntityNotFoundException in getCoach: " + ex.getMessage());
                throw ex;
            }
            return coach;
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (DatabaseException e) {
            System.err.println("DatabaseException in getCoach: " + e.getMessage());
            DatabaseException ex = new DatabaseException("Failed to retrieve coach: " + e.getMessage(), e);
            System.err.println("Throwing DatabaseException: " + ex.getMessage());
            throw ex;
        }
    }
    
    @Override
    public List<Coach> getAllCoaches() throws DatabaseException {
        try {
            return coachDAO.getAllCoaches();
        } catch (DatabaseException e) {
            throw new DatabaseException("Failed to retrieve coaches: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<Coach> getCoachesByTeam(int teamNumber) throws DatabaseException {
        try {
            return coachDAO.getCoachesByTeam(teamNumber);
        } catch (DatabaseException e) {
            throw new DatabaseException("Failed to retrieve coaches for team: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void updateCoach(Coach coach) throws ValidationException, EntityNotFoundException, DatabaseException {
        // Validate input
        InputValidator.validateCoach(coach);
        
        // Check if coach exists
        Coach existingCoach = coachDAO.getCoachByID(coach.getCoachID());
        if (existingCoach == null) {
            throw new EntityNotFoundException("Coach with CoachID " + coach.getCoachID() + " not found");
        }
        
        // Validate foreign key reference - TeamNumber must exist
        if (!teamDAO.teamExists(coach.getTeamNumber())) {
            throw new ValidationException("Team with TeamNumber " + coach.getTeamNumber() + " does not exist");
        }
        
        try {
            coachDAO.updateCoach(coach);
        } catch (DatabaseException e) {
            throw new DatabaseException("Failed to update coach: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void deleteCoach(int coachID) throws EntityNotFoundException, DatabaseException {
        try {
            // Check if coach exists
            Coach coach = coachDAO.getCoachByID(coachID);
            if (coach == null) {
                EntityNotFoundException ex = new EntityNotFoundException("Coach with CoachID " + coachID + " not found");
                System.err.println("EntityNotFoundException in deleteCoach: " + ex.getMessage());
                throw ex;
            }
            
            // Delete associated work experience records first (cascade delete)
            workExperienceDAO.deleteExperiencesByCoach(coachID);
            
            // Delete the coach
            coachDAO.deleteCoach(coachID);
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (DatabaseException e) {
            System.err.println("DatabaseException in deleteCoach: " + e.getMessage());
            DatabaseException ex = new DatabaseException("Failed to delete coach: " + e.getMessage(), e);
            System.err.println("Throwing DatabaseException: " + ex.getMessage());
            throw ex;
        }
    }
    
    @Override
    public void addWorkExperience(int coachID, WorkExperience experience) 
            throws ValidationException, EntityNotFoundException, DatabaseException {
        try {
            // Validate input
            InputValidator.validateWorkExperience(experience);
            
            // Validate foreign key reference - CoachID must exist
            Coach coach = coachDAO.getCoachByID(coachID);
            if (coach == null) {
                EntityNotFoundException ex = new EntityNotFoundException("Coach with CoachID " + coachID + " does not exist");
                System.err.println("EntityNotFoundException in addWorkExperience: " + ex.getMessage());
                throw ex;
            }
            
            // Ensure the experience has the correct CoachID
            experience.setCoachID(coachID);
            
            workExperienceDAO.createWorkExperience(experience);
        } catch (ValidationException | EntityNotFoundException e) {
            throw e;
        } catch (DatabaseException e) {
            System.err.println("DatabaseException in addWorkExperience: " + e.getMessage());
            DatabaseException ex = new DatabaseException("Failed to add work experience: " + e.getMessage(), e);
            System.err.println("Throwing DatabaseException: " + ex.getMessage());
            throw ex;
        }
    }
    
    @Override
    public List<WorkExperience> getCoachExperience(int coachID) throws DatabaseException {
        try {
            return workExperienceDAO.getExperiencesByCoach(coachID);
        } catch (DatabaseException e) {
            throw new DatabaseException("Failed to retrieve coach experience: " + e.getMessage(), e);
        }
    }
}
