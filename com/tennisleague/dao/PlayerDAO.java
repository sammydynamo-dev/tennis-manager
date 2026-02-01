package com.tennisleague.dao;

import com.tennisleague.model.Player;
import com.tennisleague.exception.DatabaseException;
import java.util.List;

/**
 * Data Access Object interface for Player entity.
 * Defines CRUD operations and query methods for Player management.
 */
public interface PlayerDAO {
    
    /**
     * Creates a new player in the database.
     * @param player The player to create
     * @return The generated PlayerID
     * @throws DatabaseException if database operation fails
     */
    int createPlayer(Player player) throws DatabaseException;
    
    /**
     * Retrieves a player by their player ID.
     * @param playerID The player ID to search for
     * @return The player with the specified ID, or null if not found
     * @throws DatabaseException if database operation fails
     */
    Player getPlayerByID(int playerID) throws DatabaseException;
    
    /**
     * Retrieves all players from the database.
     * @return List of all players
     * @throws DatabaseException if database operation fails
     */
    List<Player> getAllPlayers() throws DatabaseException;
    
    /**
     * Updates an existing player in the database.
     * @param player The player with updated information
     * @throws DatabaseException if database operation fails
     */
    void updatePlayer(Player player) throws DatabaseException;
    
    /**
     * Deletes a player from the database.
     * @param playerID The player ID to delete
     * @throws DatabaseException if database operation fails
     */
    void deletePlayer(int playerID) throws DatabaseException;
    
    /**
     * Checks if a league-wide number is already in use.
     * @param leagueWideNumber The league-wide number to check
     * @return true if the number exists, false otherwise
     * @throws DatabaseException if database operation fails
     */
    boolean leagueWideNumberExists(int leagueWideNumber) throws DatabaseException;
}
