package com.tennisleague;

import com.tennisleague.dao.*;
import com.tennisleague.dao.impl.*;
import com.tennisleague.service.*;
import com.tennisleague.service.impl.*;
import com.tennisleague.ui.MenuHandler;
import com.tennisleague.database.DatabaseConnection;
import com.tennisleague.exception.DatabaseException;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Main application entry point for the Tennis League Management System.
 * Initializes all components and starts the CLI menu interface.
 */
public class TennisLeagueApp {
    
    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("  TENNIS LEAGUE MANAGEMENT SYSTEM");
        System.out.println("=".repeat(60));
        System.out.println();
        
        // Test database connection on startup
        try {
            System.out.println("Testing database connection...");
            Connection testConn = DatabaseConnection.getConnection();
            System.out.println("✓ Database connection successful!");
            DatabaseConnection.closeResources(testConn, null, null);
            System.out.println();
        } catch (SQLException e) {
            System.err.println("✗ Failed to connect to database!");
            System.err.println("Error: " + e.getMessage());
            System.err.println();
            System.err.println("Please check your database configuration in config.properties:");
            System.err.println("  - Verify database URL, username, and password");
            System.err.println("  - Ensure MySQL server is running");
            System.err.println("  - Ensure TennisLeague database exists");
            System.err.println();
            System.exit(1);
        }
        
        try {
            // Initialize DAO layer
            TeamDAO teamDAO = new TeamDAOImpl();
            PlayerDAO playerDAO = new PlayerDAOImpl();
            CoachDAO coachDAO = new CoachDAOImpl();
            WorkExperienceDAO workExperienceDAO = new WorkExperienceDAOImpl();
            PlayerTeamAssociationDAO associationDAO = new PlayerTeamAssociationDAOImpl();
            
            // Initialize Service layer with DAO dependencies
            TeamService teamService = new TeamServiceImpl(teamDAO, coachDAO, associationDAO);
            PlayerService playerService = new PlayerServiceImpl(playerDAO, associationDAO, teamDAO);
            CoachService coachService = new CoachServiceImpl(coachDAO, workExperienceDAO, teamDAO);
            
            // Initialize UI layer with Service dependencies
            MenuHandler menuHandler = new MenuHandler(teamService, playerService, coachService);
            
            // Start the application
            menuHandler.displayMainMenu();
            
        } catch (Exception e) {
            System.err.println("\n✗ An unexpected error occurred:");
            System.err.println("Exception type: " + e.getClass().getName());
            System.err.println("Message: " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("Caused by: " + e.getCause().getMessage());
            }
            System.err.println("\nStack trace:");
            e.printStackTrace();
            System.exit(1);
        }
        
        System.out.println("\nApplication terminated.");
    }
}
