package com.tennisleague.dao.impl;

import com.tennisleague.dao.PlayerTeamAssociationDAO;
import com.tennisleague.model.PlayerTeamAssociation;
import com.tennisleague.exception.DatabaseException;
import com.tennisleague.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of PlayerTeamAssociationDAO interface.
 * Handles all database operations for PlayerTeamAssociation entity using JDBC and PreparedStatements.
 */
public class PlayerTeamAssociationDAOImpl implements PlayerTeamAssociationDAO {
    
    // SQL Queries
    private static final String INSERT_ASSOCIATION = 
        "INSERT INTO PlayerTeamAssociation (PlayerID, TeamNumber, YearJoined, YearLeft) VALUES (?, ?, ?, ?)";
    
    private static final String SELECT_ASSOCIATIONS_BY_PLAYER = 
        "SELECT AssociationID, PlayerID, TeamNumber, YearJoined, YearLeft " +
        "FROM PlayerTeamAssociation WHERE PlayerID = ?";
    
    private static final String SELECT_ASSOCIATIONS_BY_TEAM = 
        "SELECT AssociationID, PlayerID, TeamNumber, YearJoined, YearLeft " +
        "FROM PlayerTeamAssociation WHERE TeamNumber = ?";
    
    private static final String UPDATE_ASSOCIATION = 
        "UPDATE PlayerTeamAssociation SET PlayerID = ?, TeamNumber = ?, YearJoined = ?, YearLeft = ? " +
        "WHERE AssociationID = ?";
    
    private static final String DELETE_ASSOCIATION = 
        "DELETE FROM PlayerTeamAssociation WHERE AssociationID = ?";
    
    private static final String DELETE_ASSOCIATIONS_BY_PLAYER = 
        "DELETE FROM PlayerTeamAssociation WHERE PlayerID = ?";
    
    private static final String CHECK_OVERLAPPING_ASSOCIATION = 
        "SELECT COUNT(*) FROM PlayerTeamAssociation " +
        "WHERE PlayerID = ? AND TeamNumber = ? " +
        "AND (YearLeft IS NULL OR YearLeft >= ?)";
    
    /**
     * Creates a new player-team association in the database.
     * 
     * @param association The association to create
     * @throws DatabaseException if database operation fails
     */
    @Override
    public void createAssociation(PlayerTeamAssociation association) throws DatabaseException {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(INSERT_ASSOCIATION);
            
            stmt.setInt(1, association.getPlayerID());
            stmt.setInt(2, association.getTeamNumber());
            stmt.setInt(3, association.getYearJoined());
            
            // Handle nullable YearLeft
            if (association.getYearLeft() != null) {
                stmt.setInt(4, association.getYearLeft());
            } else {
                stmt.setNull(4, java.sql.Types.INTEGER);
            }
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("SQLException in createAssociation: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState() + ", Error Code: " + e.getErrorCode());
            
            // Check for foreign key constraint violation
            if (e.getErrorCode() == 1452 || e.getMessage().contains("foreign key constraint")) {
                DatabaseException ex = new DatabaseException(
                    "Cannot create association: PlayerID " + association.getPlayerID() + 
                    " or TeamNumber " + association.getTeamNumber() + " does not exist.", e);
                System.err.println("Throwing DatabaseException: " + ex.getMessage());
                throw ex;
            }
            DatabaseException ex = new DatabaseException(
                "Failed to create player-team association: " + e.getMessage(), e);
            System.err.println("Throwing DatabaseException: " + ex.getMessage());
            throw ex;
        } finally {
            DatabaseConnection.closeResources(conn, stmt);
        }
    }
    
    /**
     * Retrieves all associations for a specific player.
     * 
     * @param playerID The player ID to filter by
     * @return List of associations for the specified player
     * @throws DatabaseException if database operation fails
     */
    @Override
    public List<PlayerTeamAssociation> getAssociationsByPlayer(int playerID) throws DatabaseException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<PlayerTeamAssociation> associations = new ArrayList<>();
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(SELECT_ASSOCIATIONS_BY_PLAYER);
            stmt.setInt(1, playerID);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                associations.add(extractAssociationFromResultSet(rs));
            }
            
            return associations;
            
        } catch (SQLException e) {
            System.err.println("SQLException in getAssociationsByPlayer: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState() + ", Error Code: " + e.getErrorCode());
            DatabaseException ex = new DatabaseException(
                "Failed to retrieve associations for PlayerID " + playerID + ": " + e.getMessage(), e);
            System.err.println("Throwing DatabaseException: " + ex.getMessage());
            throw ex;
        } finally {
            DatabaseConnection.closeResources(conn, stmt, rs);
        }
    }
    
    /**
     * Retrieves all associations for a specific team.
     * 
     * @param teamNumber The team number to filter by
     * @return List of associations for the specified team
     * @throws DatabaseException if database operation fails
     */
    @Override
    public List<PlayerTeamAssociation> getAssociationsByTeam(int teamNumber) throws DatabaseException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<PlayerTeamAssociation> associations = new ArrayList<>();
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(SELECT_ASSOCIATIONS_BY_TEAM);
            stmt.setInt(1, teamNumber);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                associations.add(extractAssociationFromResultSet(rs));
            }
            
            return associations;
            
        } catch (SQLException e) {
            System.err.println("SQLException in getAssociationsByTeam: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState() + ", Error Code: " + e.getErrorCode());
            DatabaseException ex = new DatabaseException(
                "Failed to retrieve associations for TeamNumber " + teamNumber + ": " + e.getMessage(), e);
            System.err.println("Throwing DatabaseException: " + ex.getMessage());
            throw ex;
        } finally {
            DatabaseConnection.closeResources(conn, stmt, rs);
        }
    }
    
    /**
     * Updates an existing player-team association in the database.
     * 
     * @param association The association with updated information
     * @throws DatabaseException if database operation fails
     */
    @Override
    public void updateAssociation(PlayerTeamAssociation association) throws DatabaseException {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(UPDATE_ASSOCIATION);
            
            stmt.setInt(1, association.getPlayerID());
            stmt.setInt(2, association.getTeamNumber());
            stmt.setInt(3, association.getYearJoined());
            
            // Handle nullable YearLeft
            if (association.getYearLeft() != null) {
                stmt.setInt(4, association.getYearLeft());
            } else {
                stmt.setNull(4, java.sql.Types.INTEGER);
            }
            
            stmt.setInt(5, association.getAssociationID());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected == 0) {
                throw new DatabaseException(
                    "Association with AssociationID " + association.getAssociationID() + " does not exist.");
            }
            
        } catch (DatabaseException e) {
            System.err.println("DatabaseException in updateAssociation: " + e.getMessage());
            throw e;
        } catch (SQLException e) {
            System.err.println("SQLException in updateAssociation: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState() + ", Error Code: " + e.getErrorCode());
            
            // Check for foreign key constraint violation
            if (e.getErrorCode() == 1452 || e.getMessage().contains("foreign key constraint")) {
                DatabaseException ex = new DatabaseException(
                    "Cannot update association: PlayerID " + association.getPlayerID() + 
                    " or TeamNumber " + association.getTeamNumber() + " does not exist.", e);
                System.err.println("Throwing DatabaseException: " + ex.getMessage());
                throw ex;
            }
            DatabaseException ex = new DatabaseException(
                "Failed to update player-team association: " + e.getMessage(), e);
            System.err.println("Throwing DatabaseException: " + ex.getMessage());
            throw ex;
        } finally {
            DatabaseConnection.closeResources(conn, stmt);
        }
    }
    
    /**
     * Deletes a player-team association from the database.
     * 
     * @param associationID The association ID to delete
     * @throws DatabaseException if database operation fails
     */
    @Override
    public void deleteAssociation(int associationID) throws DatabaseException {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(DELETE_ASSOCIATION);
            stmt.setInt(1, associationID);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected == 0) {
                throw new DatabaseException(
                    "Association with AssociationID " + associationID + " does not exist.");
            }
            
        } catch (DatabaseException e) {
            System.err.println("DatabaseException in deleteAssociation: " + e.getMessage());
            throw e;
        } catch (SQLException e) {
            System.err.println("SQLException in deleteAssociation: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState() + ", Error Code: " + e.getErrorCode());
            DatabaseException ex = new DatabaseException(
                "Failed to delete player-team association: " + e.getMessage(), e);
            System.err.println("Throwing DatabaseException: " + ex.getMessage());
            throw ex;
        } finally {
            DatabaseConnection.closeResources(conn, stmt);
        }
    }
    
    /**
     * Deletes all associations for a specific player.
     * Used for cascading deletes when a player is removed.
     * 
     * @param playerID The player ID whose associations should be deleted
     * @throws DatabaseException if database operation fails
     */
    @Override
    public void deleteAssociationsByPlayer(int playerID) throws DatabaseException {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(DELETE_ASSOCIATIONS_BY_PLAYER);
            stmt.setInt(1, playerID);
            
            stmt.executeUpdate();
            // Note: This method doesn't throw an error if no associations exist
            // because it's used for cascading deletes
            
        } catch (SQLException e) {
            System.err.println("SQLException in deleteAssociationsByPlayer: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState() + ", Error Code: " + e.getErrorCode());
            DatabaseException ex = new DatabaseException(
                "Failed to delete associations for PlayerID " + playerID + ": " + e.getMessage(), e);
            System.err.println("Throwing DatabaseException: " + ex.getMessage());
            throw ex;
        } finally {
            DatabaseConnection.closeResources(conn, stmt);
        }
    }
    
    /**
     * Checks if a player has an overlapping association with a team.
     * An overlap occurs when the player is already associated with the team
     * during the specified year (YearLeft is null or greater than yearJoined).
     * 
     * @param playerID The player ID to check
     * @param teamNumber The team number to check
     * @param yearJoined The year the player is joining
     * @return true if an overlapping association exists, false otherwise
     * @throws DatabaseException if database operation fails
     */
    @Override
    public boolean hasOverlappingAssociation(int playerID, int teamNumber, int yearJoined) throws DatabaseException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(CHECK_OVERLAPPING_ASSOCIATION);
            stmt.setInt(1, playerID);
            stmt.setInt(2, teamNumber);
            stmt.setInt(3, yearJoined);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
            return false;
            
        } catch (SQLException e) {
            System.err.println("SQLException in hasOverlappingAssociation: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState() + ", Error Code: " + e.getErrorCode());
            DatabaseException ex = new DatabaseException(
                "Failed to check for overlapping association: " + e.getMessage(), e);
            System.err.println("Throwing DatabaseException: " + ex.getMessage());
            throw ex;
        } finally {
            DatabaseConnection.closeResources(conn, stmt, rs);
        }
    }
    
    /**
     * Helper method to extract a PlayerTeamAssociation object from a ResultSet.
     * 
     * @param rs The ResultSet positioned at an association record
     * @return A PlayerTeamAssociation object with data from the current ResultSet row
     * @throws SQLException if a database access error occurs
     */
    private PlayerTeamAssociation extractAssociationFromResultSet(ResultSet rs) throws SQLException {
        PlayerTeamAssociation association = new PlayerTeamAssociation();
        association.setAssociationID(rs.getInt("AssociationID"));
        association.setPlayerID(rs.getInt("PlayerID"));
        association.setTeamNumber(rs.getInt("TeamNumber"));
        association.setYearJoined(rs.getInt("YearJoined"));
        
        // Handle nullable YearLeft
        int yearLeft = rs.getInt("YearLeft");
        if (!rs.wasNull()) {
            association.setYearLeft(yearLeft);
        } else {
            association.setYearLeft(null);
        }
        
        return association;
    }
}
