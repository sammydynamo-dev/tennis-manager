package com.tennisleague.service;

import com.tennisleague.model.Player;
import com.tennisleague.model.PlayerTeamAssociation;
import com.tennisleague.exception.ValidationException;
import com.tennisleague.exception.DatabaseException;
import com.tennisleague.exception.EntityNotFoundException;
import java.util.List;

/**
 * Service interface for Player business operations.
 * Provides methods for managing players and their team associations.
 */
public interface PlayerService {
    
    /**
     * Adds a new player to the system.
     * 
     * @param player the player to add
     * @return the generated player ID
     * @throws ValidationException if player data is invalid
     * @throws DatabaseException if a database error occurs
     */
    int addPlayer(Player player) throws ValidationException, DatabaseException;
    
    /**
     * Retrieves a player by their ID.
     * 
     * @param playerID the player ID
     * @return the player with the specified ID
     * @throws EntityNotFoundException if the player does not exist
     * @throws DatabaseException if a database error occurs
     */
    Player getPlayer(int playerID) throws EntityNotFoundException, DatabaseException;
    
    /**
     * Retrieves all players in the system.
     * 
     * @return a list of all players
     * @throws DatabaseException if a database error occurs
     */
    List<Player> getAllPlayers() throws DatabaseException;
    
    /**
     * Updates an existing player's information.
     * 
     * @param player the player with updated information
     * @throws ValidationException if player data is invalid
     * @throws EntityNotFoundException if the player does not exist
     * @throws DatabaseException if a database error occurs
     */
    void updatePlayer(Player player) throws ValidationException, EntityNotFoundException, DatabaseException;
    
    /**
     * Deletes a player from the system.
     * 
     * @param playerID the ID of the player to delete
     * @throws EntityNotFoundException if the player does not exist
     * @throws DatabaseException if a database error occurs
     */
    void deletePlayer(int playerID) throws EntityNotFoundException, DatabaseException;
    
    /**
     * Associates a player with a team for a specific year.
     * 
     * @param playerID the player ID
     * @param teamNumber the team number
     * @param yearJoined the year the player joined the team
     * @throws ValidationException if association data is invalid
     * @throws EntityNotFoundException if the player or team does not exist
     * @throws DatabaseException if a database error occurs
     */
    void associatePlayerWithTeam(int playerID, int teamNumber, int yearJoined) 
        throws ValidationException, EntityNotFoundException, DatabaseException;
    
    /**
     * Removes a player from a team by setting the year left.
     * 
     * @param playerID the player ID
     * @param teamNumber the team number
     * @param yearLeft the year the player left the team
     * @throws ValidationException if the year left is invalid
     * @throws EntityNotFoundException if the association does not exist
     * @throws DatabaseException if a database error occurs
     */
    void removePlayerFromTeam(int playerID, int teamNumber, int yearLeft) 
        throws ValidationException, EntityNotFoundException, DatabaseException;
    
    /**
     * Retrieves the complete team history for a player.
     * 
     * @param playerID the player ID
     * @return a list of all team associations for the player
     * @throws DatabaseException if a database error occurs
     */
    List<PlayerTeamAssociation> getPlayerHistory(int playerID) throws DatabaseException;
}
