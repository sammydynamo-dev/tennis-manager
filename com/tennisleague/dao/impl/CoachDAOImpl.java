package com.tennisleague.dao.impl;

import com.tennisleague.dao.CoachDAO;
import com.tennisleague.model.Coach;
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
 * Implementation of CoachDAO interface.
 * Handles all database operations for Coach entity using JDBC.
 */
public class CoachDAOImpl implements CoachDAO {
    
    @Override
    public int createCoach(Coach coach) throws DatabaseException {
        String sql = "INSERT INTO Coach (Name, TelephoneNumber, TeamNumber) VALUES (?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, coach.getName());
            stmt.setString(2, coach.getTelephoneNumber());
            stmt.setInt(3, coach.getTeamNumber());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected == 0) {
                throw new DatabaseException("Creating coach failed, no rows affected.");
            }
            
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int generatedID = rs.getInt(1);
                coach.setCoachID(generatedID);
                return generatedID;
            } else {
                throw new DatabaseException("Creating coach failed, no ID obtained.");
            }
            
        } catch (SQLException e) {
            System.err.println("SQLException in createCoach: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState() + ", Error Code: " + e.getErrorCode());
            DatabaseException ex = new DatabaseException("Failed to create coach: " + e.getMessage(), e);
            System.err.println("Throwing DatabaseException: " + ex.getMessage());
            throw ex;
        } finally {
            DatabaseConnection.closeResources(conn, stmt, rs);
        }
    }
    
    @Override
    public Coach getCoachByID(int coachID) throws DatabaseException {
        String sql = "SELECT CoachID, Name, TelephoneNumber, TeamNumber FROM Coach WHERE CoachID = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, coachID);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractCoachFromResultSet(rs);
            }
            
            return null;
            
        } catch (SQLException e) {
            System.err.println("SQLException in getCoachByID: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState() + ", Error Code: " + e.getErrorCode());
            DatabaseException ex = new DatabaseException("Failed to retrieve coach with ID " + coachID + ": " + e.getMessage(), e);
            System.err.println("Throwing DatabaseException: " + ex.getMessage());
            throw ex;
        } finally {
            DatabaseConnection.closeResources(conn, stmt, rs);
        }
    }
    
    @Override
    public List<Coach> getAllCoaches() throws DatabaseException {
        String sql = "SELECT CoachID, Name, TelephoneNumber, TeamNumber FROM Coach";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Coach> coaches = new ArrayList<>();
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                coaches.add(extractCoachFromResultSet(rs));
            }
            
            return coaches;
            
        } catch (SQLException e) {
            System.err.println("SQLException in getAllCoaches: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState() + ", Error Code: " + e.getErrorCode());
            DatabaseException ex = new DatabaseException("Failed to retrieve all coaches: " + e.getMessage(), e);
            System.err.println("Throwing DatabaseException: " + ex.getMessage());
            throw ex;
        } finally {
            DatabaseConnection.closeResources(conn, stmt, rs);
        }
    }
    
    @Override
    public List<Coach> getCoachesByTeam(int teamNumber) throws DatabaseException {
        String sql = "SELECT CoachID, Name, TelephoneNumber, TeamNumber FROM Coach WHERE TeamNumber = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Coach> coaches = new ArrayList<>();
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, teamNumber);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                coaches.add(extractCoachFromResultSet(rs));
            }
            
            return coaches;
            
        } catch (SQLException e) {
            System.err.println("SQLException in getCoachesByTeam: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState() + ", Error Code: " + e.getErrorCode());
            DatabaseException ex = new DatabaseException("Failed to retrieve coaches for team " + teamNumber + ": " + e.getMessage(), e);
            System.err.println("Throwing DatabaseException: " + ex.getMessage());
            throw ex;
        } finally {
            DatabaseConnection.closeResources(conn, stmt, rs);
        }
    }
    
    @Override
    public void updateCoach(Coach coach) throws DatabaseException {
        String sql = "UPDATE Coach SET Name = ?, TelephoneNumber = ?, TeamNumber = ? WHERE CoachID = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, coach.getName());
            stmt.setString(2, coach.getTelephoneNumber());
            stmt.setInt(3, coach.getTeamNumber());
            stmt.setInt(4, coach.getCoachID());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected == 0) {
                throw new DatabaseException("Updating coach failed, no rows affected. Coach ID " + coach.getCoachID() + " may not exist.");
            }
            
        } catch (SQLException e) {
            System.err.println("SQLException in updateCoach: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState() + ", Error Code: " + e.getErrorCode());
            DatabaseException ex = new DatabaseException("Failed to update coach: " + e.getMessage(), e);
            System.err.println("Throwing DatabaseException: " + ex.getMessage());
            throw ex;
        } finally {
            DatabaseConnection.closeResources(conn, stmt);
        }
    }
    
    @Override
    public void deleteCoach(int coachID) throws DatabaseException {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            
            // First, delete all work experience records for this coach (cascade)
            String deleteWorkExpSql = "DELETE FROM WorkExperience WHERE CoachID = ?";
            stmt = conn.prepareStatement(deleteWorkExpSql);
            stmt.setInt(1, coachID);
            stmt.executeUpdate();
            stmt.close();
            
            // Then delete the coach
            String deleteCoachSql = "DELETE FROM Coach WHERE CoachID = ?";
            stmt = conn.prepareStatement(deleteCoachSql);
            stmt.setInt(1, coachID);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected == 0) {
                throw new DatabaseException("Deleting coach failed, no rows affected. Coach ID " + coachID + " may not exist.");
            }
            
        } catch (SQLException e) {
            System.err.println("SQLException in deleteCoach: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState() + ", Error Code: " + e.getErrorCode());
            DatabaseException ex = new DatabaseException("Failed to delete coach: " + e.getMessage(), e);
            System.err.println("Throwing DatabaseException: " + ex.getMessage());
            throw ex;
        } finally {
            DatabaseConnection.closeResources(conn, stmt);
        }
    }
    
    /**
     * Helper method to extract a Coach object from a ResultSet.
     * 
     * @param rs The ResultSet positioned at a coach record
     * @return A Coach object with data from the current ResultSet row
     * @throws SQLException if a database access error occurs
     */
    private Coach extractCoachFromResultSet(ResultSet rs) throws SQLException {
        Coach coach = new Coach();
        coach.setCoachID(rs.getInt("CoachID"));
        coach.setName(rs.getString("Name"));
        coach.setTelephoneNumber(rs.getString("TelephoneNumber"));
        coach.setTeamNumber(rs.getInt("TeamNumber"));
        return coach;
    }
}
