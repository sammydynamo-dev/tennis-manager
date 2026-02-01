package com.tennisleague.dao.impl;

import com.tennisleague.dao.PlayerDAO;
import com.tennisleague.model.Player;
import com.tennisleague.exception.DatabaseException;
import com.tennisleague.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of PlayerDAO interface.
 * Handles all database operations for Player entity using JDBC and PreparedStatements.
 */
public class PlayerDAOImpl implements PlayerDAO {
    
    // SQL Queries
    private static final String INSERT_PLAYER = 
        "INSERT INTO Player (LeagueWideNumber, Name, Age) VALUES (?, ?, ?)";
    
    private static final String SELECT_PLAYER_BY_ID = 
        "SELECT PlayerID, LeagueWideNumber, Name, Age FROM Player WHERE PlayerID = ?";
    
    private static final String SELECT_ALL_PLAYERS = 
        "SELECT PlayerID, LeagueWideNumber, Name, Age FROM Player";
    
    private static final String UPDATE_PLAYER = 
        "UPDATE Player SET LeagueWideNumber = ?, Name = ?, Age = ? WHERE PlayerID = ?";
    
    private static final String DELETE_PLAYER = 
        "DELETE FROM Player WHERE PlayerID = ?";
    
    private static final String CHECK_LEAGUE_WIDE_NUMBER_EXISTS = 
        "SELECT COUNT(*) FROM Player WHERE LeagueWideNumber = ?";
    
    /**
     * Creates a new player in the database.
     * 
     * @param player The player to create
     * @return The generated PlayerID
     * @throws DatabaseException if database operation fails or league-wide number already exists
     */
    @Override
    public int createPlayer(Player player) throws DatabaseException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(INSERT_PLAYER, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setInt(1, player.getLeagueWideNumber());
            stmt.setString(2, player.getName());
            stmt.setInt(3, player.getAge());
            
            stmt.executeUpdate();
            
            // Retrieve the generated PlayerID
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new DatabaseException("Failed to retrieve generated PlayerID after creating player.");
            }
            
        } catch (SQLException e) {
            System.err.println("SQLException in createPlayer: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState() + ", Error Code: " + e.getErrorCode());
            
            // Check for duplicate key violation
            if (e.getErrorCode() == 1062 || e.getMessage().contains("Duplicate entry")) {
                DatabaseException ex = new DatabaseException(
                    "Player with LeagueWideNumber " + player.getLeagueWideNumber() + " already exists.", e);
                System.err.println("Throwing DatabaseException: " + ex.getMessage());
                throw ex;
            }
            DatabaseException ex = new DatabaseException(
                "Failed to create player: " + e.getMessage(), e);
            System.err.println("Throwing DatabaseException: " + ex.getMessage());
            throw ex;
        } finally {
            DatabaseConnection.closeResources(conn, stmt, rs);
        }
    }
    
    /**
     * Retrieves a player by their player ID.
     * 
     * @param playerID The player ID to search for
     * @return The player with the specified ID, or null if not found
     * @throws DatabaseException if database operation fails
     */
    @Override
    public Player getPlayerByID(int playerID) throws DatabaseException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(SELECT_PLAYER_BY_ID);
            stmt.setInt(1, playerID);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractPlayerFromResultSet(rs);
            }
            
            return null;
            
        } catch (SQLException e) {
            System.err.println("SQLException in getPlayerByID: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState() + ", Error Code: " + e.getErrorCode());
            DatabaseException ex = new DatabaseException(
                "Failed to retrieve player with PlayerID " + playerID + ": " + e.getMessage(), e);
            System.err.println("Throwing DatabaseException: " + ex.getMessage());
            throw ex;
        } finally {
            DatabaseConnection.closeResources(conn, stmt, rs);
        }
    }
    
    /**
     * Retrieves all players from the database.
     * 
     * @return List of all players
     * @throws DatabaseException if database operation fails
     */
    @Override
    public List<Player> getAllPlayers() throws DatabaseException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Player> players = new ArrayList<>();
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(SELECT_ALL_PLAYERS);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                players.add(extractPlayerFromResultSet(rs));
            }
            
            return players;
            
        } catch (SQLException e) {
            System.err.println("SQLException in getAllPlayers: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState() + ", Error Code: " + e.getErrorCode());
            DatabaseException ex = new DatabaseException(
                "Failed to retrieve all players: " + e.getMessage(), e);
            System.err.println("Throwing DatabaseException: " + ex.getMessage());
            throw ex;
        } finally {
            DatabaseConnection.closeResources(conn, stmt, rs);
        }
    }
    
    /**
     * Updates an existing player in the database.
     * 
     * @param player The player with updated information
     * @throws DatabaseException if database operation fails
     */
    @Override
    public void updatePlayer(Player player) throws DatabaseException {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(UPDATE_PLAYER);
            
            stmt.setInt(1, player.getLeagueWideNumber());
            stmt.setString(2, player.getName());
            stmt.setInt(3, player.getAge());
            stmt.setInt(4, player.getPlayerID());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected == 0) {
                throw new DatabaseException(
                    "Player with PlayerID " + player.getPlayerID() + " does not exist.");
            }
            
        } catch (DatabaseException e) {
            System.err.println("DatabaseException in updatePlayer: " + e.getMessage());
            throw e;
        } catch (SQLException e) {
            System.err.println("SQLException in updatePlayer: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState() + ", Error Code: " + e.getErrorCode());
            
            // Check for duplicate key violation
            if (e.getErrorCode() == 1062 || e.getMessage().contains("Duplicate entry")) {
                DatabaseException ex = new DatabaseException(
                    "Player with LeagueWideNumber " + player.getLeagueWideNumber() + " already exists.", e);
                System.err.println("Throwing DatabaseException: " + ex.getMessage());
                throw ex;
            }
            DatabaseException ex = new DatabaseException(
                "Failed to update player: " + e.getMessage(), e);
            System.err.println("Throwing DatabaseException: " + ex.getMessage());
            throw ex;
        } finally {
            DatabaseConnection.closeResources(conn, stmt);
        }
    }
    
    /**
     * Deletes a player from the database.
     * 
     * @param playerID The player ID to delete
     * @throws DatabaseException if database operation fails
     */
    @Override
    public void deletePlayer(int playerID) throws DatabaseException {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(DELETE_PLAYER);
            stmt.setInt(1, playerID);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected == 0) {
                throw new DatabaseException(
                    "Player with PlayerID " + playerID + " does not exist.");
            }
            
        } catch (DatabaseException e) {
            System.err.println("DatabaseException in deletePlayer: " + e.getMessage());
            throw e;
        } catch (SQLException e) {
            System.err.println("SQLException in deletePlayer: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState() + ", Error Code: " + e.getErrorCode());
            
            // Check for foreign key constraint violation
            if (e.getErrorCode() == 1451 || e.getMessage().contains("foreign key constraint")) {
                DatabaseException ex = new DatabaseException(
                    "Cannot delete player with PlayerID " + playerID + 
                    " because it has associated records (team associations).", e);
                System.err.println("Throwing DatabaseException: " + ex.getMessage());
                throw ex;
            }
            DatabaseException ex = new DatabaseException(
                "Failed to delete player: " + e.getMessage(), e);
            System.err.println("Throwing DatabaseException: " + ex.getMessage());
            throw ex;
        } finally {
            DatabaseConnection.closeResources(conn, stmt);
        }
    }
    
    /**
     * Checks if a league-wide number is already in use.
     * 
     * @param leagueWideNumber The league-wide number to check
     * @return true if the number exists, false otherwise
     * @throws DatabaseException if database operation fails
     */
    @Override
    public boolean leagueWideNumberExists(int leagueWideNumber) throws DatabaseException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(CHECK_LEAGUE_WIDE_NUMBER_EXISTS);
            stmt.setInt(1, leagueWideNumber);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
            return false;
            
        } catch (SQLException e) {
            System.err.println("SQLException in leagueWideNumberExists: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState() + ", Error Code: " + e.getErrorCode());
            DatabaseException ex = new DatabaseException(
                "Failed to check if league-wide number exists: " + e.getMessage(), e);
            System.err.println("Throwing DatabaseException: " + ex.getMessage());
            throw ex;
        } finally {
            DatabaseConnection.closeResources(conn, stmt, rs);
        }
    }
    
    /**
     * Helper method to extract a Player object from a ResultSet.
     * 
     * @param rs The ResultSet positioned at a player record
     * @return A Player object with data from the current ResultSet row
     * @throws SQLException if a database access error occurs
     */
    private Player extractPlayerFromResultSet(ResultSet rs) throws SQLException {
        Player player = new Player();
        player.setPlayerID(rs.getInt("PlayerID"));
        player.setLeagueWideNumber(rs.getInt("LeagueWideNumber"));
        player.setName(rs.getString("Name"));
        player.setAge(rs.getInt("Age"));
        return player;
    }
}
