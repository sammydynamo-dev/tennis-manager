package com.tennisleague.service.impl;

import com.tennisleague.service.TeamService;
import com.tennisleague.model.Team;
import com.tennisleague.dao.TeamDAO;
import com.tennisleague.dao.CoachDAO;
import com.tennisleague.dao.PlayerTeamAssociationDAO;
import com.tennisleague.ui.InputValidator;
import com.tennisleague.exception.ValidationException;
import com.tennisleague.exception.DatabaseException;
import com.tennisleague.exception.EntityNotFoundException;
import java.util.List;

/**
 * Implementation of TeamService interface.
 * Provides business logic for team management operations.
 */
public class TeamServiceImpl implements TeamService {
    
    private final TeamDAO teamDAO;
    private final CoachDAO coachDAO;
    private final PlayerTeamAssociationDAO associationDAO;
    
    /**
     * Constructs a TeamServiceImpl with required DAO dependencies.
     * 
     * @param teamDAO the TeamDAO implementation
     * @param coachDAO the CoachDAO implementation for checking dependencies
     * @param associationDAO the PlayerTeamAssociationDAO implementation for checking dependencies
     */
    public TeamServiceImpl(TeamDAO teamDAO, CoachDAO coachDAO, PlayerTeamAssociationDAO associationDAO) {
        this.teamDAO = teamDAO;
        this.coachDAO = coachDAO;
        this.associationDAO = associationDAO;
    }
    
    @Override
    public void addTeam(Team team) throws ValidationException, DatabaseException {
        try {
            // Validate input
            InputValidator.validateTeam(team);
            
            // Check for duplicate TeamNumber
            if (teamDAO.teamExists(team.getTeamNumber())) {
                ValidationException ex = new ValidationException("Team with TeamNumber " + team.getTeamNumber() + " already exists");
                System.err.println("ValidationException in addTeam: " + ex.getMessage());
                throw ex;
            }
            
            teamDAO.createTeam(team);
        } catch (ValidationException e) {
            System.err.println("ValidationException in addTeam: " + e.getMessage());
            throw e;
        } catch (DatabaseException e) {
            System.err.println("DatabaseException in addTeam: " + e.getMessage());
            DatabaseException ex = new DatabaseException("Failed to add team: " + e.getMessage(), e);
            System.err.println("Throwing DatabaseException: " + ex.getMessage());
            throw ex;
        }
    }
    
    @Override
    public Team getTeam(int teamNumber) throws EntityNotFoundException, DatabaseException {
        try {
            Team team = teamDAO.getTeamByNumber(teamNumber);
            if (team == null) {
                EntityNotFoundException ex = new EntityNotFoundException("Team with TeamNumber " + teamNumber + " not found");
                System.err.println("EntityNotFoundException in getTeam: " + ex.getMessage());
                throw ex;
            }
            return team;
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (DatabaseException e) {
            System.err.println("DatabaseException in getTeam: " + e.getMessage());
            DatabaseException ex = new DatabaseException("Failed to retrieve team: " + e.getMessage(), e);
            System.err.println("Throwing DatabaseException: " + ex.getMessage());
            throw ex;
        }
    }
    
    @Override
    public List<Team> getAllTeams() throws DatabaseException {
        try {
            return teamDAO.getAllTeams();
        } catch (DatabaseException e) {
            System.err.println("DatabaseException in getAllTeams: " + e.getMessage());
            DatabaseException ex = new DatabaseException("Failed to retrieve teams: " + e.getMessage(), e);
            System.err.println("Throwing DatabaseException: " + ex.getMessage());
            throw ex;
        }
    }
    
    @Override
    public void updateTeam(Team team) throws ValidationException, EntityNotFoundException, DatabaseException {
        try {
            // Validate input
            InputValidator.validateTeam(team);
            
            // Check if team exists
            if (!teamDAO.teamExists(team.getTeamNumber())) {
                EntityNotFoundException ex = new EntityNotFoundException("Team with TeamNumber " + team.getTeamNumber() + " not found");
                System.err.println("EntityNotFoundException in updateTeam: " + ex.getMessage());
                throw ex;
            }
            
            teamDAO.updateTeam(team);
        } catch (ValidationException e) {
            System.err.println("ValidationException in updateTeam: " + e.getMessage());
            throw e;
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (DatabaseException e) {
            System.err.println("DatabaseException in updateTeam: " + e.getMessage());
            DatabaseException ex = new DatabaseException("Failed to update team: " + e.getMessage(), e);
            System.err.println("Throwing DatabaseException: " + ex.getMessage());
            throw ex;
        }
    }
    
    @Override
    public void deleteTeam(int teamNumber) throws EntityNotFoundException, DatabaseException {
        try {
            // Check if team exists
            if (!teamDAO.teamExists(teamNumber)) {
                EntityNotFoundException ex = new EntityNotFoundException("Team with TeamNumber " + teamNumber + " not found");
                System.err.println("EntityNotFoundException in deleteTeam: " + ex.getMessage());
                throw ex;
            }
            
            // Check for dependent records - coaches
            List<com.tennisleague.model.Coach> coaches = coachDAO.getCoachesByTeam(teamNumber);
            if (!coaches.isEmpty()) {
                DatabaseException ex = new DatabaseException("Cannot delete team with TeamNumber " + teamNumber + 
                    ". Team has " + coaches.size() + " associated coach(es). Please remove or reassign coaches first.");
                System.err.println("DatabaseException in deleteTeam: " + ex.getMessage());
                throw ex;
            }
            
            // Check for dependent records - player associations
            List<com.tennisleague.model.PlayerTeamAssociation> associations = associationDAO.getAssociationsByTeam(teamNumber);
            if (!associations.isEmpty()) {
                DatabaseException ex = new DatabaseException("Cannot delete team with TeamNumber " + teamNumber + 
                    ". Team has " + associations.size() + " associated player(s). Please remove associations first.");
                System.err.println("DatabaseException in deleteTeam: " + ex.getMessage());
                throw ex;
            }
            
            teamDAO.deleteTeam(teamNumber);
        } catch (EntityNotFoundException | DatabaseException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("Unexpected exception in deleteTeam: " + e.getMessage());
            DatabaseException ex = new DatabaseException("Failed to delete team: " + e.getMessage(), e);
            System.err.println("Throwing DatabaseException: " + ex.getMessage());
            throw ex;
        }
    }
}
