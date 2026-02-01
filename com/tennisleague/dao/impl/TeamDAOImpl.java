package com.tennisleague.dao.impl;

import com.tennisleague.dao.TeamDAO;
import com.tennisleague.model.Team;
import com.tennisleague.exception.DatabaseException;
import com.tennisleague.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of TeamDAO interface.
 * Handles all database operations for Team entity using JDBC and PreparedStatements.
 */
public class TeamDAOImpl implements TeamDAO {
    
    // SQL Queries
    private static final String INSERT_TEAM = 
        "INSERT INTO Team (TeamNumber, Name, City, ManagerName) VALUES (?, ?, ?, ?)";
    
    private static final String SELECT_TEAM_BY_NUMBER = 
        "SELECT TeamNumber, Name, City, ManagerName FROM Team WHERE TeamNumber = ?";
    
    private static final String SELECT_ALL_TEAMS = 
        "SELECT TeamNumber, Name, City, ManagerName FROM Team";
    
    private static final String UPDATE_TEAM = 
        "UPDATE Team SET Name = ?, City = ?, ManagerName = ? WHERE TeamNumber = ?";
    
    private static final String DELETE_TEAM = 
        "DELETE FROM Team WHERE TeamNumber = ?";
    
    private static final String CHECK_TEAM_EXISTS = 
        "SELECT COUNT(*) FROM Team WHERE TeamNumber = ?";
    
    /**
     * Creates a new team in the database.
     * 
     * @param team The team to create
     * @throws DatabaseException if database operation fails or team already exists
     */
    @Override
    public void createTeam(Team team) throws DatabaseException {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(INSERT_TEAM);
            
            stmt.setInt(1, team.getTeamNumber());
            stmt.setString(2, team.getName());
            stmt.setString(3, team.getCity());
            stmt.setString(4, team.getManagerName());
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            // Log the exception
            System.err.println("SQLException in createTeam: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState() + ", Error Code: " + e.getErrorCode());
            
            // Check for duplicate key violation
            if (e.getErrorCode() == 1062 || e.getMessage().contains("Duplicate entry")) {
                DatabaseException ex = new DatabaseException(
                    "Team with TeamNumber " + team.getTeamNumber() + " already exists.", e);
                System.err.println("Throwing DatabaseException: " + ex.getMessage());
                throw ex;
            }
            DatabaseException ex = new DatabaseException(
                "Failed to create team: " + e.getMessage(), e);
            System.err.println("Throwing DatabaseException: " + ex.getMessage());
            throw ex;
        } finally {
            DatabaseConnection.closeResources(conn, stmt);
        }
    }
    
    /**
     * Retrieves a team by its team number.
     * 
     * @param teamNumber The team number to search for
     * @return The team with the specified number, or null if not found
     * @throws DatabaseException if database operation fails
     */
    @Override
    public Team getTeamByNumber(int teamNumber) throws DatabaseException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(SELECT_TEAM_BY_NUMBER);
            stmt.setInt(1, teamNumber);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractTeamFromResultSet(rs);
            }
            
            return null;
            
        } catch (SQLException e) {
            System.err.println("SQLException in getTeamByNumber: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState() + ", Error Code: " + e.getErrorCode());
            DatabaseException ex = new DatabaseException(
                "Failed to retrieve team with TeamNumber " + teamNumber + ": " + e.getMessage(), e);
            System.err.println("Throwing DatabaseException: " + ex.getMessage());
            throw ex;
        } finally {
            DatabaseConnection.closeResources(conn, stmt, rs);
        }
    }
    
    /**
     * Retrieves all teams from the database.
     * 
     * @return List of all teams
     * @throws DatabaseException if database operation fails
     */
    @Override
    public List<Team> getAllTeams() throws DatabaseException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Team> teams = new ArrayList<>();
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(SELECT_ALL_TEAMS);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                teams.add(extractTeamFromResultSet(rs));
            }
            
            return teams;
            
        } catch (SQLException e) {
            System.err.println("SQLException in getAllTeams: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState() + ", Error Code: " + e.getErrorCode());
            DatabaseException ex = new DatabaseException(
                "Failed to retrieve all teams: " + e.getMessage(), e);
            System.err.println("Throwing DatabaseException: " + ex.getMessage());
            throw ex;
        } finally {
            DatabaseConnection.closeResources(conn, stmt, rs);
        }
    }
    
    /**
     * Updates an existing team in the database.
     * 
     * @param team The team with updated information
     * @throws DatabaseException if database operation fails
     */
    @Override
    public void updateTeam(Team team) throws DatabaseException {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(UPDATE_TEAM);
            
            stmt.setString(1, team.getName());
            stmt.setString(2, team.getCity());
            stmt.setString(3, team.getManagerName());
            stmt.setInt(4, team.getTeamNumber());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected == 0) {
                throw new DatabaseException(
                    "Team with TeamNumber " + team.getTeamNumber() + " does not exist.");
            }
            
        } catch (DatabaseException e) {
            System.err.println("DatabaseException in updateTeam: " + e.getMessage());
            throw e;
        } catch (SQLException e) {
            System.err.println("SQLException in updateTeam: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState() + ", Error Code: " + e.getErrorCode());
            DatabaseException ex = new DatabaseException(
                "Failed to update team: " + e.getMessage(), e);
            System.err.println("Throwing DatabaseException: " + ex.getMessage());
            throw ex;
        } finally {
            DatabaseConnection.closeResources(conn, stmt);
        }
    }
    
    /**
     * Deletes a team from the database.
     * 
     * @param teamNumber The team number to delete
     * @throws DatabaseException if database operation fails
     */
    @Override
    public void deleteTeam(int teamNumber) throws DatabaseException {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(DELETE_TEAM);
            stmt.setInt(1, teamNumber);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected == 0) {
                throw new DatabaseException(
                    "Team with TeamNumber " + teamNumber + " does not exist.");
            }
            
        } catch (DatabaseException e) {
            System.err.println("DatabaseException in deleteTeam: " + e.getMessage());
            throw e;
        } catch (SQLException e) {
            System.err.println("SQLException in deleteTeam: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState() + ", Error Code: " + e.getErrorCode());
            
            // Check for foreign key constraint violation
            if (e.getErrorCode() == 1451 || e.getMessage().contains("foreign key constraint")) {
                DatabaseException ex = new DatabaseException(
                    "Cannot delete team with TeamNumber " + teamNumber + 
                    " because it has associated records (coaches or player associations).", e);
                System.err.println("Throwing DatabaseException: " + ex.getMessage());
                throw ex;
            }
            DatabaseException ex = new DatabaseException(
                "Failed to delete team: " + e.getMessage(), e);
            System.err.println("Throwing DatabaseException: " + ex.getMessage());
            throw ex;
        } finally {
            DatabaseConnection.closeResources(conn, stmt);
        }
    }
    
    /**
     * Checks if a team exists with the given team number.
     * 
     * @param teamNumber The team number to check
     * @return true if team exists, false otherwise
     * @throws DatabaseException if database operation fails
     */
    @Override
    public boolean teamExists(int teamNumber) throws DatabaseException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(CHECK_TEAM_EXISTS);
            stmt.setInt(1, teamNumber);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
            return false;
            
        } catch (SQLException e) {
            System.err.println("SQLException in teamExists: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState() + ", Error Code: " + e.getErrorCode());
            DatabaseException ex = new DatabaseException(
                "Failed to check if team exists: " + e.getMessage(), e);
            System.err.println("Throwing DatabaseException: " + ex.getMessage());
            throw ex;
        } finally {
            DatabaseConnection.closeResources(conn, stmt, rs);
        }
    }
    
    /**
     * Helper method to extract a Team object from a ResultSet.
     * 
     * @param rs The ResultSet positioned at a team record
     * @return A Team object with data from the current ResultSet row
     * @throws SQLException if a database access error occurs
     */
    private Team extractTeamFromResultSet(ResultSet rs) throws SQLException {
        Team team = new Team();
        team.setTeamNumber(rs.getInt("TeamNumber"));
        team.setName(rs.getString("Name"));
        team.setCity(rs.getString("City"));
        team.setManagerName(rs.getString("ManagerName"));
        return team;
    }
}
