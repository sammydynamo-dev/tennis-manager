package com.tennisleague.dao.impl;

import com.tennisleague.dao.WorkExperienceDAO;
import com.tennisleague.model.WorkExperience;
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
 * Implementation of WorkExperienceDAO interface.
 * Handles all database operations for WorkExperience entity using JDBC.
 */
public class WorkExperienceDAOImpl implements WorkExperienceDAO {
    
    @Override
    public void createWorkExperience(WorkExperience experience) throws DatabaseException {
        String sql = "INSERT INTO WorkExperience (CoachID, ExperienceType, Duration) VALUES (?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, experience.getCoachID());
            stmt.setString(2, experience.getExperienceType());
            stmt.setInt(3, experience.getDuration());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected == 0) {
                throw new DatabaseException("Creating work experience failed, no rows affected.");
            }
            
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int generatedID = rs.getInt(1);
                experience.setExperienceID(generatedID);
            } else {
                throw new DatabaseException("Creating work experience failed, no ID obtained.");
            }
            
        } catch (SQLException e) {
            System.err.println("SQLException in createWorkExperience: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState() + ", Error Code: " + e.getErrorCode());
            DatabaseException ex = new DatabaseException("Failed to create work experience: " + e.getMessage(), e);
            System.err.println("Throwing DatabaseException: " + ex.getMessage());
            throw ex;
        } finally {
            DatabaseConnection.closeResources(conn, stmt, rs);
        }
    }
    
    @Override
    public List<WorkExperience> getExperiencesByCoach(int coachID) throws DatabaseException {
        String sql = "SELECT ExperienceID, CoachID, ExperienceType, Duration FROM WorkExperience WHERE CoachID = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<WorkExperience> experiences = new ArrayList<>();
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, coachID);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                experiences.add(extractWorkExperienceFromResultSet(rs));
            }
            
            return experiences;
            
        } catch (SQLException e) {
            System.err.println("SQLException in getExperiencesByCoach: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState() + ", Error Code: " + e.getErrorCode());
            DatabaseException ex = new DatabaseException("Failed to retrieve work experiences for coach " + coachID + ": " + e.getMessage(), e);
            System.err.println("Throwing DatabaseException: " + ex.getMessage());
            throw ex;
        } finally {
            DatabaseConnection.closeResources(conn, stmt, rs);
        }
    }
    
    @Override
    public void updateWorkExperience(WorkExperience experience) throws DatabaseException {
        String sql = "UPDATE WorkExperience SET CoachID = ?, ExperienceType = ?, Duration = ? WHERE ExperienceID = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, experience.getCoachID());
            stmt.setString(2, experience.getExperienceType());
            stmt.setInt(3, experience.getDuration());
            stmt.setInt(4, experience.getExperienceID());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected == 0) {
                throw new DatabaseException("Updating work experience failed, no rows affected. Experience ID " + experience.getExperienceID() + " may not exist.");
            }
            
        } catch (SQLException e) {
            System.err.println("SQLException in updateWorkExperience: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState() + ", Error Code: " + e.getErrorCode());
            DatabaseException ex = new DatabaseException("Failed to update work experience: " + e.getMessage(), e);
            System.err.println("Throwing DatabaseException: " + ex.getMessage());
            throw ex;
        } finally {
            DatabaseConnection.closeResources(conn, stmt);
        }
    }
    
    @Override
    public void deleteWorkExperience(int experienceID) throws DatabaseException {
        String sql = "DELETE FROM WorkExperience WHERE ExperienceID = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, experienceID);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected == 0) {
                throw new DatabaseException("Deleting work experience failed, no rows affected. Experience ID " + experienceID + " may not exist.");
            }
            
        } catch (SQLException e) {
            System.err.println("SQLException in deleteWorkExperience: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState() + ", Error Code: " + e.getErrorCode());
            DatabaseException ex = new DatabaseException("Failed to delete work experience: " + e.getMessage(), e);
            System.err.println("Throwing DatabaseException: " + ex.getMessage());
            throw ex;
        } finally {
            DatabaseConnection.closeResources(conn, stmt);
        }
    }
    
    @Override
    public void deleteExperiencesByCoach(int coachID) throws DatabaseException {
        String sql = "DELETE FROM WorkExperience WHERE CoachID = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, coachID);
            
            // Note: This may affect 0 rows if the coach has no work experience records
            // This is not an error condition
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("SQLException in deleteExperiencesByCoach: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState() + ", Error Code: " + e.getErrorCode());
            DatabaseException ex = new DatabaseException("Failed to delete work experiences for coach " + coachID + ": " + e.getMessage(), e);
            System.err.println("Throwing DatabaseException: " + ex.getMessage());
            throw ex;
        } finally {
            DatabaseConnection.closeResources(conn, stmt);
        }
    }
    
    /**
     * Helper method to extract a WorkExperience object from a ResultSet.
     * 
     * @param rs The ResultSet positioned at a work experience record
     * @return A WorkExperience object with data from the current ResultSet row
     * @throws SQLException if a database access error occurs
     */
    private WorkExperience extractWorkExperienceFromResultSet(ResultSet rs) throws SQLException {
        WorkExperience experience = new WorkExperience();
        experience.setExperienceID(rs.getInt("ExperienceID"));
        experience.setCoachID(rs.getInt("CoachID"));
        experience.setExperienceType(rs.getString("ExperienceType"));
        experience.setDuration(rs.getInt("Duration"));
        return experience;
    }
}
