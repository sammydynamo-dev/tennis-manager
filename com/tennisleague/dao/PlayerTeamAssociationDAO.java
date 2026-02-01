package com.tennisleague.dao;

import com.tennisleague.model.PlayerTeamAssociation;
import com.tennisleague.exception.DatabaseException;
import java.util.List;

/**
 * Data Access Object interface for PlayerTeamAssociation entity.
 * Defines CRUD operations and query methods for player-team association management.
 */
public interface PlayerTeamAssociationDAO {
    
    /**
     * Creates a new player-team association in the database.
     * @param association The association to create
     * @throws DatabaseException if database operation fails
     */
    void createAssociation(PlayerTeamAssociation association) throws DatabaseException;
    
    /**
     * Retrieves all associations for a specific player.
     * @param playerID The player ID to filter by
     * @return List of associations for the specified player
     * @throws DatabaseException if database operation fails
     */
    List<PlayerTeamAssociation> getAssociationsByPlayer(int playerID) throws DatabaseException;
    
    /**
     * Retrieves all associations for a specific team.
     * @param teamNumber The team number to filter by
     * @return List of associations for the specified team
     * @throws DatabaseException if database operation fails
     */
    List<PlayerTeamAssociation> getAssociationsByTeam(int teamNumber) throws DatabaseException;
    
    /**
     * Updates an existing player-team association in the database.
     * @param association The association with updated information
     * @throws DatabaseException if database operation fails
     */
    void updateAssociation(PlayerTeamAssociation association) throws DatabaseException;
    
    /**
     * Deletes a player-team association from the database.
     * @param associationID The association ID to delete
     * @throws DatabaseException if database operation fails
     */
    void deleteAssociation(int associationID) throws DatabaseException;
    
    /**
     * Deletes all associations for a specific player.
     * Used for cascading deletes when a player is removed.
     * @param playerID The player ID whose associations should be deleted
     * @throws DatabaseException if database operation fails
     */
    void deleteAssociationsByPlayer(int playerID) throws DatabaseException;
    
    /**
     * Checks if a player has an overlapping association with a team.
     * An overlap occurs when the player is already associated with the team
     * during the specified year (YearLeft is null or greater than yearJoined).
     * @param playerID The player ID to check
     * @param teamNumber The team number to check
     * @param yearJoined The year the player is joining
     * @return true if an overlapping association exists, false otherwise
     * @throws DatabaseException if database operation fails
     */
    boolean hasOverlappingAssociation(int playerID, int teamNumber, int yearJoined) throws DatabaseException;
}
