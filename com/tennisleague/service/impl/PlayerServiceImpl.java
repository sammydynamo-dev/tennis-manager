package com.tennisleague.service.impl;

import com.tennisleague.service.PlayerService;
import com.tennisleague.model.Player;
import com.tennisleague.model.PlayerTeamAssociation;
import com.tennisleague.dao.PlayerDAO;
import com.tennisleague.dao.PlayerTeamAssociationDAO;
import com.tennisleague.dao.TeamDAO;
import com.tennisleague.ui.InputValidator;
import com.tennisleague.exception.ValidationException;
import com.tennisleague.exception.DatabaseException;
import com.tennisleague.exception.EntityNotFoundException;
import java.util.List;

/**
 * Implementation of PlayerService interface.
 * Provides business logic for player management and team association operations.
 */
public class PlayerServiceImpl implements PlayerService {
    
    private final PlayerDAO playerDAO;
    private final PlayerTeamAssociationDAO associationDAO;
    private final TeamDAO teamDAO;
    
    /**
     * Constructs a PlayerServiceImpl with required DAO dependencies.
     * 
     * @param playerDAO the PlayerDAO implementation
     * @param associationDAO the PlayerTeamAssociationDAO implementation
     * @param teamDAO the TeamDAO implementation for foreign key validation
     */
    public PlayerServiceImpl(PlayerDAO playerDAO, PlayerTeamAssociationDAO associationDAO, TeamDAO teamDAO) {
        this.playerDAO = playerDAO;
        this.associationDAO = associationDAO;
        this.teamDAO = teamDAO;
    }
    
    @Override
    public int addPlayer(Player player) throws ValidationException, DatabaseException {
        try {
            // Validate input
            InputValidator.validatePlayer(player);
            
            // Check for duplicate LeagueWideNumber
            if (playerDAO.leagueWideNumberExists(player.getLeagueWideNumber())) {
                ValidationException ex = new ValidationException("Player with LeagueWideNumber " + player.getLeagueWideNumber() + " already exists");
                System.err.println("ValidationException in addPlayer: " + ex.getMessage());
                throw ex;
            }
            
            return playerDAO.createPlayer(player);
        } catch (ValidationException e) {
            System.err.println("ValidationException in addPlayer: " + e.getMessage());
            throw e;
        } catch (DatabaseException e) {
            System.err.println("DatabaseException in addPlayer: " + e.getMessage());
            DatabaseException ex = new DatabaseException("Failed to add player: " + e.getMessage(), e);
            System.err.println("Throwing DatabaseException: " + ex.getMessage());
            throw ex;
        }
    }
    
    @Override
    public Player getPlayer(int playerID) throws EntityNotFoundException, DatabaseException {
        try {
            Player player = playerDAO.getPlayerByID(playerID);
            if (player == null) {
                EntityNotFoundException ex = new EntityNotFoundException("Player with PlayerID " + playerID + " not found");
                System.err.println("EntityNotFoundException in getPlayer: " + ex.getMessage());
                throw ex;
            }
            return player;
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (DatabaseException e) {
            System.err.println("DatabaseException in getPlayer: " + e.getMessage());
            DatabaseException ex = new DatabaseException("Failed to retrieve player: " + e.getMessage(), e);
            System.err.println("Throwing DatabaseException: " + ex.getMessage());
            throw ex;
        }
    }
    
    @Override
    public List<Player> getAllPlayers() throws DatabaseException {
        try {
            return playerDAO.getAllPlayers();
        } catch (DatabaseException e) {
            throw new DatabaseException("Failed to retrieve players: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void updatePlayer(Player player) throws ValidationException, EntityNotFoundException, DatabaseException {
        // Validate input
        InputValidator.validatePlayer(player);
        
        // Check if player exists
        Player existingPlayer = playerDAO.getPlayerByID(player.getPlayerID());
        if (existingPlayer == null) {
            throw new EntityNotFoundException("Player with PlayerID " + player.getPlayerID() + " not found");
        }
        
        // Check for duplicate LeagueWideNumber (if changed)
        if (player.getLeagueWideNumber() != existingPlayer.getLeagueWideNumber()) {
            if (playerDAO.leagueWideNumberExists(player.getLeagueWideNumber())) {
                throw new ValidationException("Player with LeagueWideNumber " + player.getLeagueWideNumber() + " already exists");
            }
        }
        
        try {
            playerDAO.updatePlayer(player);
        } catch (DatabaseException e) {
            throw new DatabaseException("Failed to update player: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void deletePlayer(int playerID) throws EntityNotFoundException, DatabaseException {
        try {
            // Check if player exists
            Player player = playerDAO.getPlayerByID(playerID);
            if (player == null) {
                EntityNotFoundException ex = new EntityNotFoundException("Player with PlayerID " + playerID + " not found");
                System.err.println("EntityNotFoundException in deletePlayer: " + ex.getMessage());
                throw ex;
            }
            
            // Delete player (associations will be cascade deleted by DAO)
            playerDAO.deletePlayer(playerID);
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (DatabaseException e) {
            System.err.println("DatabaseException in deletePlayer: " + e.getMessage());
            DatabaseException ex = new DatabaseException("Failed to delete player: " + e.getMessage(), e);
            System.err.println("Throwing DatabaseException: " + ex.getMessage());
            throw ex;
        }
    }
    
    @Override
    public void associatePlayerWithTeam(int playerID, int teamNumber, int yearJoined) 
            throws ValidationException, EntityNotFoundException, DatabaseException {
        try {
            // Check if player exists
            Player player = playerDAO.getPlayerByID(playerID);
            if (player == null) {
                EntityNotFoundException ex = new EntityNotFoundException("Player with PlayerID " + playerID + " not found");
                System.err.println("EntityNotFoundException in associatePlayerWithTeam: " + ex.getMessage());
                throw ex;
            }
            
            // Check if team exists (foreign key validation)
            if (!teamDAO.teamExists(teamNumber)) {
                EntityNotFoundException ex = new EntityNotFoundException("Team with TeamNumber " + teamNumber + " not found");
                System.err.println("EntityNotFoundException in associatePlayerWithTeam: " + ex.getMessage());
                throw ex;
            }
            
            // Create association object for validation
            PlayerTeamAssociation association = new PlayerTeamAssociation();
            association.setPlayerID(playerID);
            association.setTeamNumber(teamNumber);
            association.setYearJoined(yearJoined);
            association.setYearLeft(null);
            
            // Validate association
            InputValidator.validateAssociation(association);
            
            // Check for overlapping associations
            if (associationDAO.hasOverlappingAssociation(playerID, teamNumber, yearJoined)) {
                ValidationException ex = new ValidationException("Player with PlayerID " + playerID + 
                    " already has an overlapping association with Team " + teamNumber);
                System.err.println("ValidationException in associatePlayerWithTeam: " + ex.getMessage());
                throw ex;
            }
            
            associationDAO.createAssociation(association);
        } catch (ValidationException | EntityNotFoundException e) {
            throw e;
        } catch (DatabaseException e) {
            System.err.println("DatabaseException in associatePlayerWithTeam: " + e.getMessage());
            DatabaseException ex = new DatabaseException("Failed to associate player with team: " + e.getMessage(), e);
            System.err.println("Throwing DatabaseException: " + ex.getMessage());
            throw ex;
        }
    }
    
    @Override
    public void removePlayerFromTeam(int playerID, int teamNumber, int yearLeft) 
            throws ValidationException, EntityNotFoundException, DatabaseException {
        
        // Get all associations for the player
        List<PlayerTeamAssociation> associations = associationDAO.getAssociationsByPlayer(playerID);
        
        // Find the active association for this team (YearLeft is null)
        PlayerTeamAssociation targetAssociation = null;
        for (PlayerTeamAssociation assoc : associations) {
            if (assoc.getTeamNumber() == teamNumber && assoc.getYearLeft() == null) {
                targetAssociation = assoc;
                break;
            }
        }
        
        if (targetAssociation == null) {
            throw new EntityNotFoundException("No active association found for Player " + playerID + 
                " with Team " + teamNumber);
        }
        
        // Update the association with yearLeft
        targetAssociation.setYearLeft(yearLeft);
        
        // Validate the updated association
        InputValidator.validateAssociation(targetAssociation);
        
        try {
            associationDAO.updateAssociation(targetAssociation);
        } catch (DatabaseException e) {
            throw new DatabaseException("Failed to remove player from team: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<PlayerTeamAssociation> getPlayerHistory(int playerID) throws DatabaseException {
        try {
            return associationDAO.getAssociationsByPlayer(playerID);
        } catch (DatabaseException e) {
            throw new DatabaseException("Failed to retrieve player history: " + e.getMessage(), e);
        }
    }
}
