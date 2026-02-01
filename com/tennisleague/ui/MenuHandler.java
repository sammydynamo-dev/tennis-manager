package com.tennisleague.ui;

import com.tennisleague.model.*;
import com.tennisleague.service.*;
import com.tennisleague.exception.*;
import java.util.List;
import java.util.Scanner;

/**
 * Menu-driven CLI handler for the Tennis League Management System.
 * Provides interactive menus for managing teams, players, and coaches.
 */
public class MenuHandler {
    
    private final TeamService teamService;
    private final PlayerService playerService;
    private final CoachService coachService;
    private final Scanner scanner;
    
    /**
     * Constructs a MenuHandler with the required services.
     * 
     * @param teamService the team service
     * @param playerService the player service
     * @param coachService the coach service
     */
    public MenuHandler(TeamService teamService, PlayerService playerService, CoachService coachService) {
        this.teamService = teamService;
        this.playerService = playerService;
        this.coachService = coachService;
        this.scanner = new Scanner(System.in);
    }
    
    /**
     * Displays the main menu and handles user navigation.
     */
    public void displayMainMenu() {
        boolean running = true;
        
        while (running) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("       TENNIS LEAGUE MANAGEMENT SYSTEM");
            System.out.println("=".repeat(60));
            System.out.println("  1. Team Management      - Manage teams and rosters");
            System.out.println("  2. Player Management    - Manage players and associations");
            System.out.println("  3. Coach Management     - Manage coaches and experience");
            System.out.println("  4. Exit                 - Close the application");
            System.out.println("=".repeat(60));
            System.out.print("Select an option (1-4): ");
            
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    displayTeamMenu();
                    break;
                case "2":
                    displayPlayerMenu();
                    break;
                case "3":
                    displayCoachMenu();
                    break;
                case "4":
                    System.out.println("\n" + "=".repeat(60));
                    System.out.println("  Thank you for using Tennis League Management System!");
                    System.out.println("  Goodbye!");
                    System.out.println("=".repeat(60));
                    running = false;
                    break;
                default:
                    System.out.println("\n✗ Invalid option. Please enter a number between 1 and 4.");
            }
        }
    }
    
    /**
     * Displays the Team Management submenu.
     */
    private void displayTeamMenu() {
        boolean inTeamMenu = true;
        
        while (inTeamMenu) {
            System.out.println("\n" + "-".repeat(60));
            System.out.println("       TEAM MANAGEMENT");
            System.out.println("-".repeat(60));
            System.out.println("  1. Add Team              - Create a new team");
            System.out.println("  2. View All Teams        - List all teams");
            System.out.println("  3. View Team by Number   - View specific team details");
            System.out.println("  4. Update Team           - Modify team information");
            System.out.println("  5. Delete Team           - Remove a team");
            System.out.println("  6. Return to Main Menu   - Go back");
            System.out.println("-".repeat(60));
            System.out.print("Select an option (1-6): ");
            
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    addTeam();
                    break;
                case "2":
                    viewAllTeams();
                    break;
                case "3":
                    viewTeamByNumber();
                    break;
                case "4":
                    updateTeam();
                    break;
                case "5":
                    deleteTeam();
                    break;
                case "6":
                    inTeamMenu = false;
                    break;
                default:
                    System.out.println("\n✗ Invalid option. Please enter a number between 1 and 6.");
            }
        }
    }
    
    /**
     * Displays the Player Management submenu.
     */
    private void displayPlayerMenu() {
        boolean inPlayerMenu = true;
        
        while (inPlayerMenu) {
            System.out.println("\n" + "-".repeat(60));
            System.out.println("       PLAYER MANAGEMENT");
            System.out.println("-".repeat(60));
            System.out.println("  1. Add Player                  - Create a new player");
            System.out.println("  2. View All Players            - List all players");
            System.out.println("  3. View Player by ID           - View specific player details");
            System.out.println("  4. Update Player               - Modify player information");
            System.out.println("  5. Delete Player               - Remove a player");
            System.out.println("  6. Associate Player with Team  - Add player to team");
            System.out.println("  7. Remove Player from Team     - Update player departure");
            System.out.println("  8. View Player Team History    - View player's team history");
            System.out.println("  9. Return to Main Menu         - Go back");
            System.out.println("-".repeat(60));
            System.out.print("Select an option (1-9): ");
            
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    addPlayer();
                    break;
                case "2":
                    viewAllPlayers();
                    break;
                case "3":
                    viewPlayerByID();
                    break;
                case "4":
                    updatePlayer();
                    break;
                case "5":
                    deletePlayer();
                    break;
                case "6":
                    associatePlayerWithTeam();
                    break;
                case "7":
                    removePlayerFromTeam();
                    break;
                case "8":
                    viewPlayerHistory();
                    break;
                case "9":
                    inPlayerMenu = false;
                    break;
                default:
                    System.out.println("\n✗ Invalid option. Please enter a number between 1 and 9.");
            }
        }
    }
    
    /**
     * Displays the Coach Management submenu.
     */
    private void displayCoachMenu() {
        boolean inCoachMenu = true;
        
        while (inCoachMenu) {
            System.out.println("\n" + "-".repeat(60));
            System.out.println("       COACH MANAGEMENT");
            System.out.println("-".repeat(60));
            System.out.println("  1. Add Coach                   - Create a new coach");
            System.out.println("  2. View All Coaches            - List all coaches");
            System.out.println("  3. View Coach by ID            - View specific coach details");
            System.out.println("  4. Update Coach                - Modify coach information");
            System.out.println("  5. Delete Coach                - Remove a coach");
            System.out.println("  6. Add Work Experience         - Add coach experience record");
            System.out.println("  7. View Coach Work Experience  - View coach's experience");
            System.out.println("  8. View Coaches by Team        - Filter coaches by team");
            System.out.println("  9. Return to Main Menu         - Go back");
            System.out.println("-".repeat(60));
            System.out.print("Select an option (1-9): ");
            
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    addCoach();
                    break;
                case "2":
                    viewAllCoaches();
                    break;
                case "3":
                    viewCoachByID();
                    break;
                case "4":
                    updateCoach();
                    break;
                case "5":
                    deleteCoach();
                    break;
                case "6":
                    addWorkExperience();
                    break;
                case "7":
                    viewCoachWorkExperience();
                    break;
                case "8":
                    viewCoachesByTeam();
                    break;
                case "9":
                    inCoachMenu = false;
                    break;
                default:
                    System.out.println("\n✗ Invalid option. Please enter a number between 1 and 9.");
            }
        }
    }
    
    // ==================== TEAM OPERATIONS ====================
    
    /**
     * Adds a new team.
     */
    private void addTeam() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  ADD NEW TEAM");
        System.out.println("=".repeat(60));
        
        try {
            System.out.print("Enter Team Number (e.g., 101): ");
            int teamNumber = Integer.parseInt(scanner.nextLine().trim());
            
            System.out.print("Enter Team Name (e.g., Thunder Strikers): ");
            String name = scanner.nextLine().trim();
            
            System.out.print("Enter City (e.g., Boston): ");
            String city = scanner.nextLine().trim();
            
            System.out.print("Enter Manager Name (e.g., John Smith): ");
            String managerName = scanner.nextLine().trim();
            
            Team team = new Team(teamNumber, name, city, managerName);
            teamService.addTeam(team);
            
            System.out.println("\n" + "=".repeat(60));
            System.out.println("  ✓ SUCCESS: Team added successfully!");
            System.out.println("  Team Number: " + teamNumber);
            System.out.println("  Team Name: " + name);
            System.out.println("=".repeat(60));
            
        } catch (NumberFormatException e) {
            logException("addTeam", e);
            System.out.println("\n" + "=".repeat(60));
            System.out.println("  ✗ ERROR: Invalid Input");
            System.out.println("  Team Number must be a valid integer (e.g., 101, 202)");
            System.out.println("  Please try again with a numeric value.");
            System.out.println("=".repeat(60));
        } catch (ValidationException e) {
            logException("addTeam", e);
            System.out.println("\n" + "=".repeat(60));
            System.out.println("  ✗ VALIDATION ERROR");
            System.out.println("  " + e.getMessage());
            System.out.println("  Please check your input and try again.");
            System.out.println("=".repeat(60));
        } catch (DatabaseException e) {
            logException("addTeam", e);
            System.out.println("\n" + "=".repeat(60));
            System.out.println("  ✗ DATABASE ERROR");
            System.out.println("  " + e.getMessage());
            if (e.getMessage().contains("Duplicate")) {
                System.out.println("  Hint: This team number already exists. Try a different number.");
            }
            System.out.println("=".repeat(60));
        }
    }
    
    /**
     * Views all teams.
     */
    private void viewAllTeams() {
        System.out.println("\n--- All Teams ---");
        
        try {
            List<Team> teams = teamService.getAllTeams();
            
            if (teams.isEmpty()) {
                System.out.println("No teams found.");
            } else {
                displayTeamsTable(teams);
            }
            
        } catch (DatabaseException e) {
            System.out.println("\n✗ Database Error: " + e.getMessage());
        }
    }
    
    /**
     * Views a team by its number.
     */
    private void viewTeamByNumber() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  VIEW TEAM BY NUMBER");
        System.out.println("=".repeat(60));
        
        try {
            System.out.print("Enter Team Number (e.g., 101): ");
            int teamNumber = Integer.parseInt(scanner.nextLine().trim());
            
            Team team = teamService.getTeam(teamNumber);
            displayTeamDetails(team);
            
        } catch (NumberFormatException e) {
            System.out.println("\n✗ ERROR: Invalid input. Team Number must be a valid integer.");
        } catch (EntityNotFoundException e) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("  ✗ NOT FOUND");
            System.out.println("  " + e.getMessage());
            System.out.println("  Hint: Use 'View All Teams' to see available team numbers.");
            System.out.println("=".repeat(60));
        } catch (DatabaseException e) {
            System.out.println("\n✗ DATABASE ERROR: " + e.getMessage());
        }
    }
    
    /**
     * Updates an existing team.
     */
    private void updateTeam() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  UPDATE TEAM");
        System.out.println("=".repeat(60));
        
        try {
            System.out.print("Enter Team Number to update (e.g., 101): ");
            int teamNumber = Integer.parseInt(scanner.nextLine().trim());
            
            System.out.print("Enter New Team Name: ");
            String name = scanner.nextLine().trim();
            
            System.out.print("Enter New City: ");
            String city = scanner.nextLine().trim();
            
            System.out.print("Enter New Manager Name: ");
            String managerName = scanner.nextLine().trim();
            
            Team team = new Team(teamNumber, name, city, managerName);
            teamService.updateTeam(team);
            
            System.out.println("\n" + "=".repeat(60));
            System.out.println("  ✓ SUCCESS: Team updated successfully!");
            System.out.println("=".repeat(60));
            
        } catch (NumberFormatException e) {
            System.out.println("\n✗ ERROR: Invalid input. Team Number must be a valid integer.");
        } catch (ValidationException e) {
            System.out.println("\n✗ VALIDATION ERROR: " + e.getMessage());
        } catch (EntityNotFoundException e) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("  ✗ NOT FOUND");
            System.out.println("  " + e.getMessage());
            System.out.println("  Hint: Use 'View All Teams' to see available team numbers.");
            System.out.println("=".repeat(60));
        } catch (DatabaseException e) {
            System.out.println("\n✗ DATABASE ERROR: " + e.getMessage());
        }
    }
    
    /**
     * Deletes a team.
     */
    private void deleteTeam() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  DELETE TEAM");
        System.out.println("=".repeat(60));
        System.out.println("  WARNING: This action cannot be undone!");
        System.out.println("  Deleting a team may affect associated coaches and players.");
        System.out.println("=".repeat(60));
        
        try {
            System.out.print("Enter Team Number to delete (e.g., 101): ");
            int teamNumber = Integer.parseInt(scanner.nextLine().trim());
            
            System.out.print("\nAre you sure you want to delete this team? (yes/no): ");
            String confirmation = scanner.nextLine().trim().toLowerCase();
            
            if (confirmation.equals("yes") || confirmation.equals("y")) {
                teamService.deleteTeam(teamNumber);
                System.out.println("\n" + "=".repeat(60));
                System.out.println("  ✓ SUCCESS: Team deleted successfully!");
                System.out.println("=".repeat(60));
            } else {
                System.out.println("\n" + "=".repeat(60));
                System.out.println("  ℹ Deletion cancelled. No changes were made.");
                System.out.println("=".repeat(60));
            }
            
        } catch (NumberFormatException e) {
            System.out.println("\n✗ ERROR: Invalid input. Team Number must be a valid integer.");
        } catch (EntityNotFoundException e) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("  ✗ NOT FOUND");
            System.out.println("  " + e.getMessage());
            System.out.println("=".repeat(60));
        } catch (DatabaseException e) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("  ✗ DATABASE ERROR");
            System.out.println("  " + e.getMessage());
            if (e.getMessage().contains("foreign key") || e.getMessage().contains("constraint")) {
                System.out.println("  Hint: This team has associated records (coaches/players).");
                System.out.println("  Remove those associations first, then try again.");
            }
            System.out.println("=".repeat(60));
        }
    }
    
    // ==================== PLAYER OPERATIONS ====================
    
    /**
     * Adds a new player.
     */
    private void addPlayer() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  ADD NEW PLAYER");
        System.out.println("=".repeat(60));
        
        try {
            System.out.print("Enter League Wide Number (unique, e.g., 1001): ");
            int leagueWideNumber = Integer.parseInt(scanner.nextLine().trim());
            
            System.out.print("Enter Player Name (e.g., Jane Doe): ");
            String name = scanner.nextLine().trim();
            
            System.out.print("Enter Age (1-120): ");
            int age = Integer.parseInt(scanner.nextLine().trim());
            
            Player player = new Player(0, leagueWideNumber, name, age);
            int playerID = playerService.addPlayer(player);
            
            System.out.println("\n" + "=".repeat(60));
            System.out.println("  ✓ SUCCESS: Player added successfully!");
            System.out.println("  Player ID: " + playerID);
            System.out.println("  League Wide Number: " + leagueWideNumber);
            System.out.println("=".repeat(60));
            
        } catch (NumberFormatException e) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("  ✗ ERROR: Invalid Input");
            System.out.println("  League Wide Number and Age must be valid integers.");
            System.out.println("  Example: League Wide Number: 1001, Age: 25");
            System.out.println("=".repeat(60));
        } catch (ValidationException e) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("  ✗ VALIDATION ERROR");
            System.out.println("  " + e.getMessage());
            System.out.println("  Hint: Age must be between 1 and 120.");
            System.out.println("=".repeat(60));
        } catch (DatabaseException e) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("  ✗ DATABASE ERROR");
            System.out.println("  " + e.getMessage());
            if (e.getMessage().contains("Duplicate") || e.getMessage().contains("unique")) {
                System.out.println("  Hint: This League Wide Number already exists.");
                System.out.println("  Each player must have a unique League Wide Number.");
            }
            System.out.println("=".repeat(60));
        }
    }
    
    /**
     * Views all players.
     */
    private void viewAllPlayers() {
        System.out.println("\n--- All Players ---");
        
        try {
            List<Player> players = playerService.getAllPlayers();
            
            if (players.isEmpty()) {
                System.out.println("No players found.");
            } else {
                displayPlayersTable(players);
            }
            
        } catch (DatabaseException e) {
            System.out.println("\n✗ Database Error: " + e.getMessage());
        }
    }
    
    /**
     * Views a player by their ID.
     */
    private void viewPlayerByID() {
        System.out.println("\n--- View Player ---");
        
        try {
            System.out.print("Enter Player ID: ");
            int playerID = Integer.parseInt(scanner.nextLine().trim());
            
            Player player = playerService.getPlayer(playerID);
            displayPlayerDetails(player);
            
        } catch (NumberFormatException e) {
            System.out.println("\n✗ Error: Invalid number format. Please enter a valid integer.");
        } catch (EntityNotFoundException e) {
            System.out.println("\n✗ " + e.getMessage());
        } catch (DatabaseException e) {
            System.out.println("\n✗ Database Error: " + e.getMessage());
        }
    }
    
    /**
     * Updates an existing player.
     */
    private void updatePlayer() {
        System.out.println("\n--- Update Player ---");
        
        try {
            System.out.print("Enter Player ID to update: ");
            int playerID = Integer.parseInt(scanner.nextLine().trim());
            
            System.out.print("Enter New League Wide Number: ");
            int leagueWideNumber = Integer.parseInt(scanner.nextLine().trim());
            
            System.out.print("Enter New Player Name: ");
            String name = scanner.nextLine().trim();
            
            System.out.print("Enter New Age: ");
            int age = Integer.parseInt(scanner.nextLine().trim());
            
            Player player = new Player(playerID, leagueWideNumber, name, age);
            playerService.updatePlayer(player);
            
            System.out.println("\n✓ Player updated successfully!");
            
        } catch (NumberFormatException e) {
            System.out.println("\n✗ Error: Invalid number format. Please enter a valid integer.");
        } catch (ValidationException e) {
            System.out.println("\n✗ Validation Error: " + e.getMessage());
        } catch (EntityNotFoundException e) {
            System.out.println("\n✗ " + e.getMessage());
        } catch (DatabaseException e) {
            System.out.println("\n✗ Database Error: " + e.getMessage());
        }
    }
    
    /**
     * Deletes a player.
     */
    private void deletePlayer() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  DELETE PLAYER");
        System.out.println("=".repeat(60));
        System.out.println("  WARNING: This action cannot be undone!");
        System.out.println("  All team associations for this player will also be deleted.");
        System.out.println("=".repeat(60));
        
        try {
            System.out.print("Enter Player ID to delete (e.g., 1): ");
            int playerID = Integer.parseInt(scanner.nextLine().trim());
            
            System.out.print("\nAre you sure you want to delete this player? (yes/no): ");
            String confirmation = scanner.nextLine().trim().toLowerCase();
            
            if (confirmation.equals("yes") || confirmation.equals("y")) {
                playerService.deletePlayer(playerID);
                System.out.println("\n" + "=".repeat(60));
                System.out.println("  ✓ SUCCESS: Player and all associations deleted!");
                System.out.println("=".repeat(60));
            } else {
                System.out.println("\n" + "=".repeat(60));
                System.out.println("  ℹ Deletion cancelled. No changes were made.");
                System.out.println("=".repeat(60));
            }
            
        } catch (NumberFormatException e) {
            System.out.println("\n✗ ERROR: Invalid input. Player ID must be a valid integer.");
        } catch (EntityNotFoundException e) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("  ✗ NOT FOUND");
            System.out.println("  " + e.getMessage());
            System.out.println("  Hint: Use 'View All Players' to see available player IDs.");
            System.out.println("=".repeat(60));
        } catch (DatabaseException e) {
            System.out.println("\n✗ DATABASE ERROR: " + e.getMessage());
        }
    }
    
    /**
     * Associates a player with a team.
     */
    private void associatePlayerWithTeam() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  ASSOCIATE PLAYER WITH TEAM");
        System.out.println("=".repeat(60));
        
        try {
            System.out.print("Enter Player ID (e.g., 1): ");
            int playerID = Integer.parseInt(scanner.nextLine().trim());
            
            System.out.print("Enter Team Number (e.g., 101): ");
            int teamNumber = Integer.parseInt(scanner.nextLine().trim());
            
            System.out.print("Enter Year Joined (e.g., 2024): ");
            int yearJoined = Integer.parseInt(scanner.nextLine().trim());
            
            playerService.associatePlayerWithTeam(playerID, teamNumber, yearJoined);
            
            System.out.println("\n" + "=".repeat(60));
            System.out.println("  ✓ SUCCESS: Player associated with team!");
            System.out.println("  Player ID: " + playerID + " → Team: " + teamNumber);
            System.out.println("  Year Joined: " + yearJoined);
            System.out.println("=".repeat(60));
            
        } catch (NumberFormatException e) {
            System.out.println("\n✗ ERROR: All inputs must be valid integers.");
        } catch (ValidationException e) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("  ✗ VALIDATION ERROR");
            System.out.println("  " + e.getMessage());
            if (e.getMessage().contains("overlap")) {
                System.out.println("  Hint: This player already has an active association");
                System.out.println("  with this team. Update the existing association first.");
            }
            System.out.println("=".repeat(60));
        } catch (EntityNotFoundException e) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("  ✗ NOT FOUND");
            System.out.println("  " + e.getMessage());
            System.out.println("  Hint: Verify that both Player ID and Team Number exist.");
            System.out.println("=".repeat(60));
        } catch (DatabaseException e) {
            System.out.println("\n✗ DATABASE ERROR: " + e.getMessage());
        }
    }
    
    /**
     * Removes a player from a team.
     */
    private void removePlayerFromTeam() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  REMOVE PLAYER FROM TEAM");
        System.out.println("=".repeat(60));
        System.out.println("  This updates the player's team association with a leave date.");
        System.out.println("=".repeat(60));
        
        try {
            System.out.print("Enter Player ID (e.g., 1): ");
            int playerID = Integer.parseInt(scanner.nextLine().trim());
            
            System.out.print("Enter Team Number (e.g., 101): ");
            int teamNumber = Integer.parseInt(scanner.nextLine().trim());
            
            System.out.print("Enter Year Left (e.g., 2024): ");
            int yearLeft = Integer.parseInt(scanner.nextLine().trim());
            
            playerService.removePlayerFromTeam(playerID, teamNumber, yearLeft);
            
            System.out.println("\n" + "=".repeat(60));
            System.out.println("  ✓ SUCCESS: Player removed from team!");
            System.out.println("  Association updated with Year Left: " + yearLeft);
            System.out.println("=".repeat(60));
            
        } catch (NumberFormatException e) {
            System.out.println("\n✗ ERROR: All inputs must be valid integers.");
        } catch (ValidationException e) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("  ✗ VALIDATION ERROR");
            System.out.println("  " + e.getMessage());
            System.out.println("  Hint: Year Left must be >= Year Joined.");
            System.out.println("=".repeat(60));
        } catch (EntityNotFoundException e) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("  ✗ NOT FOUND");
            System.out.println("  " + e.getMessage());
            System.out.println("  Hint: Verify the player-team association exists.");
            System.out.println("=".repeat(60));
        } catch (DatabaseException e) {
            System.out.println("\n✗ DATABASE ERROR: " + e.getMessage());
        }
    }
    
    /**
     * Views a player's team history.
     */
    private void viewPlayerHistory() {
        System.out.println("\n--- Player Team History ---");
        
        try {
            System.out.print("Enter Player ID: ");
            int playerID = Integer.parseInt(scanner.nextLine().trim());
            
            List<PlayerTeamAssociation> history = playerService.getPlayerHistory(playerID);
            
            if (history.isEmpty()) {
                System.out.println("No team history found for this player.");
            } else {
                displayPlayerHistoryTable(history);
            }
            
        } catch (NumberFormatException e) {
            System.out.println("\n✗ Error: Invalid number format. Please enter a valid integer.");
        } catch (DatabaseException e) {
            System.out.println("\n✗ Database Error: " + e.getMessage());
        }
    }
    
    // ==================== COACH OPERATIONS ====================
    
    /**
     * Adds a new coach.
     */
    private void addCoach() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  ADD NEW COACH");
        System.out.println("=".repeat(60));
        
        try {
            System.out.print("Enter Coach Name (e.g., Mike Johnson): ");
            String name = scanner.nextLine().trim();
            
            System.out.print("Enter Telephone Number (e.g., 555-1234): ");
            String telephoneNumber = scanner.nextLine().trim();
            
            System.out.print("Enter Team Number (e.g., 101): ");
            int teamNumber = Integer.parseInt(scanner.nextLine().trim());
            
            Coach coach = new Coach(0, name, telephoneNumber, teamNumber);
            int coachID = coachService.addCoach(coach);
            
            System.out.println("\n" + "=".repeat(60));
            System.out.println("  ✓ SUCCESS: Coach added successfully!");
            System.out.println("  Coach ID: " + coachID);
            System.out.println("  Assigned to Team: " + teamNumber);
            System.out.println("=".repeat(60));
            
        } catch (NumberFormatException e) {
            System.out.println("\n✗ ERROR: Team Number must be a valid integer.");
        } catch (ValidationException e) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("  ✗ VALIDATION ERROR");
            System.out.println("  " + e.getMessage());
            System.out.println("  Hint: Check telephone number format (digits, dashes allowed).");
            System.out.println("=".repeat(60));
        } catch (DatabaseException e) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("  ✗ DATABASE ERROR");
            System.out.println("  " + e.getMessage());
            if (e.getMessage().contains("foreign key") || e.getMessage().contains("Team")) {
                System.out.println("  Hint: The specified team does not exist.");
                System.out.println("  Use 'View All Teams' to see available teams.");
            }
            System.out.println("=".repeat(60));
        }
    }
    
    /**
     * Views all coaches.
     */
    private void viewAllCoaches() {
        System.out.println("\n--- All Coaches ---");
        
        try {
            List<Coach> coaches = coachService.getAllCoaches();
            
            if (coaches.isEmpty()) {
                System.out.println("No coaches found.");
            } else {
                displayCoachesTable(coaches);
            }
            
        } catch (DatabaseException e) {
            System.out.println("\n✗ Database Error: " + e.getMessage());
        }
    }
    
    /**
     * Views a coach by their ID.
     */
    private void viewCoachByID() {
        System.out.println("\n--- View Coach ---");
        
        try {
            System.out.print("Enter Coach ID: ");
            int coachID = Integer.parseInt(scanner.nextLine().trim());
            
            Coach coach = coachService.getCoach(coachID);
            displayCoachDetails(coach);
            
        } catch (NumberFormatException e) {
            System.out.println("\n✗ Error: Invalid number format. Please enter a valid integer.");
        } catch (EntityNotFoundException e) {
            System.out.println("\n✗ " + e.getMessage());
        } catch (DatabaseException e) {
            System.out.println("\n✗ Database Error: " + e.getMessage());
        }
    }
    
    /**
     * Updates an existing coach.
     */
    private void updateCoach() {
        System.out.println("\n--- Update Coach ---");
        
        try {
            System.out.print("Enter Coach ID to update: ");
            int coachID = Integer.parseInt(scanner.nextLine().trim());
            
            System.out.print("Enter New Coach Name: ");
            String name = scanner.nextLine().trim();
            
            System.out.print("Enter New Telephone Number: ");
            String telephoneNumber = scanner.nextLine().trim();
            
            System.out.print("Enter New Team Number: ");
            int teamNumber = Integer.parseInt(scanner.nextLine().trim());
            
            Coach coach = new Coach(coachID, name, telephoneNumber, teamNumber);
            coachService.updateCoach(coach);
            
            System.out.println("\n✓ Coach updated successfully!");
            
        } catch (NumberFormatException e) {
            System.out.println("\n✗ Error: Invalid number format. Please enter a valid integer.");
        } catch (ValidationException e) {
            System.out.println("\n✗ Validation Error: " + e.getMessage());
        } catch (EntityNotFoundException e) {
            System.out.println("\n✗ " + e.getMessage());
        } catch (DatabaseException e) {
            System.out.println("\n✗ Database Error: " + e.getMessage());
        }
    }
    
    /**
     * Deletes a coach.
     */
    private void deleteCoach() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  DELETE COACH");
        System.out.println("=".repeat(60));
        System.out.println("  WARNING: This action cannot be undone!");
        System.out.println("  All work experience records for this coach will be deleted.");
        System.out.println("=".repeat(60));
        
        try {
            System.out.print("Enter Coach ID to delete (e.g., 1): ");
            int coachID = Integer.parseInt(scanner.nextLine().trim());
            
            System.out.print("\nAre you sure you want to delete this coach? (yes/no): ");
            String confirmation = scanner.nextLine().trim().toLowerCase();
            
            if (confirmation.equals("yes") || confirmation.equals("y")) {
                coachService.deleteCoach(coachID);
                System.out.println("\n" + "=".repeat(60));
                System.out.println("  ✓ SUCCESS: Coach and all work experience deleted!");
                System.out.println("=".repeat(60));
            } else {
                System.out.println("\n" + "=".repeat(60));
                System.out.println("  ℹ Deletion cancelled. No changes were made.");
                System.out.println("=".repeat(60));
            }
            
        } catch (NumberFormatException e) {
            System.out.println("\n✗ ERROR: Invalid input. Coach ID must be a valid integer.");
        } catch (EntityNotFoundException e) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("  ✗ NOT FOUND");
            System.out.println("  " + e.getMessage());
            System.out.println("  Hint: Use 'View All Coaches' to see available coach IDs.");
            System.out.println("=".repeat(60));
        } catch (DatabaseException e) {
            System.out.println("\n✗ DATABASE ERROR: " + e.getMessage());
        }
    }
    
    /**
     * Adds work experience for a coach.
     */
    private void addWorkExperience() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  ADD WORK EXPERIENCE");
        System.out.println("=".repeat(60));
        
        try {
            System.out.print("Enter Coach ID (e.g., 1): ");
            int coachID = Integer.parseInt(scanner.nextLine().trim());
            
            System.out.print("Enter Experience Type (e.g., Head Coach, Assistant): ");
            String experienceType = scanner.nextLine().trim();
            
            System.out.print("Enter Duration in years (e.g., 5): ");
            int duration = Integer.parseInt(scanner.nextLine().trim());
            
            WorkExperience experience = new WorkExperience(0, coachID, experienceType, duration);
            coachService.addWorkExperience(coachID, experience);
            
            System.out.println("\n" + "=".repeat(60));
            System.out.println("  ✓ SUCCESS: Work experience added!");
            System.out.println("  Type: " + experienceType);
            System.out.println("  Duration: " + duration + " years");
            System.out.println("=".repeat(60));
            
        } catch (NumberFormatException e) {
            System.out.println("\n✗ ERROR: Coach ID and Duration must be valid integers.");
        } catch (ValidationException e) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("  ✗ VALIDATION ERROR");
            System.out.println("  " + e.getMessage());
            System.out.println("  Hint: Duration must be a positive number.");
            System.out.println("=".repeat(60));
        } catch (EntityNotFoundException e) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("  ✗ NOT FOUND");
            System.out.println("  " + e.getMessage());
            System.out.println("  Hint: Verify the Coach ID exists.");
            System.out.println("=".repeat(60));
        } catch (DatabaseException e) {
            System.out.println("\n✗ DATABASE ERROR: " + e.getMessage());
        }
    }
    
    /**
     * Views work experience for a coach.
     */
    private void viewCoachWorkExperience() {
        System.out.println("\n--- Coach Work Experience ---");
        
        try {
            System.out.print("Enter Coach ID: ");
            int coachID = Integer.parseInt(scanner.nextLine().trim());
            
            List<WorkExperience> experiences = coachService.getCoachExperience(coachID);
            
            if (experiences.isEmpty()) {
                System.out.println("No work experience found for this coach.");
            } else {
                displayWorkExperienceTable(experiences);
            }
            
        } catch (NumberFormatException e) {
            System.out.println("\n✗ Error: Invalid number format. Please enter a valid integer.");
        } catch (DatabaseException e) {
            System.out.println("\n✗ Database Error: " + e.getMessage());
        }
    }
    
    /**
     * Views coaches filtered by team.
     */
    private void viewCoachesByTeam() {
        System.out.println("\n--- View Coaches by Team ---");
        
        try {
            System.out.print("Enter Team Number: ");
            int teamNumber = Integer.parseInt(scanner.nextLine().trim());
            
            List<Coach> coaches = coachService.getCoachesByTeam(teamNumber);
            
            if (coaches.isEmpty()) {
                System.out.println("No coaches found for this team.");
            } else {
                displayCoachesTable(coaches);
            }
            
        } catch (NumberFormatException e) {
            System.out.println("\n✗ Error: Invalid number format. Please enter a valid integer.");
        } catch (DatabaseException e) {
            System.out.println("\n✗ Database Error: " + e.getMessage());
        }
    }
    
    // ==================== DISPLAY METHODS ====================
    
    /**
     * Displays a table of teams.
     */
    private void displayTeamsTable(List<Team> teams) {
        System.out.println("\n" + "=".repeat(80));
        System.out.printf("%-12s %-20s %-20s %-20s%n", "Team Number", "Name", "City", "Manager");
        System.out.println("=".repeat(80));
        
        for (Team team : teams) {
            System.out.printf("%-12d %-20s %-20s %-20s%n",
                team.getTeamNumber(),
                truncate(team.getName(), 20),
                truncate(team.getCity(), 20),
                truncate(team.getManagerName(), 20));
        }
        
        System.out.println("=".repeat(80));
    }
    
    /**
     * Displays details of a single team.
     */
    private void displayTeamDetails(Team team) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  TEAM DETAILS");
        System.out.println("=".repeat(60));
        System.out.println("  Team Number:  " + team.getTeamNumber());
        System.out.println("  Name:         " + team.getName());
        System.out.println("  City:         " + team.getCity());
        System.out.println("  Manager:      " + team.getManagerName());
        System.out.println("=".repeat(60));
    }
    
    /**
     * Displays a table of players.
     */
    private void displayPlayersTable(List<Player> players) {
        System.out.println("\n" + "=".repeat(70));
        System.out.printf("%-10s %-15s %-30s %-10s%n", "Player ID", "League Number", "Name", "Age");
        System.out.println("=".repeat(70));
        
        for (Player player : players) {
            System.out.printf("%-10d %-15d %-30s %-10d%n",
                player.getPlayerID(),
                player.getLeagueWideNumber(),
                truncate(player.getName(), 30),
                player.getAge());
        }
        
        System.out.println("=".repeat(70));
    }
    
    /**
     * Displays details of a single player.
     */
    private void displayPlayerDetails(Player player) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  PLAYER DETAILS");
        System.out.println("=".repeat(60));
        System.out.println("  Player ID:           " + player.getPlayerID());
        System.out.println("  League Wide Number:  " + player.getLeagueWideNumber());
        System.out.println("  Name:                " + player.getName());
        System.out.println("  Age:                 " + player.getAge());
        System.out.println("=".repeat(60));
    }
    
    /**
     * Displays a table of coaches.
     */
    private void displayCoachesTable(List<Coach> coaches) {
        System.out.println("\n" + "=".repeat(80));
        System.out.printf("%-10s %-25s %-20s %-12s%n", "Coach ID", "Name", "Telephone", "Team Number");
        System.out.println("=".repeat(80));
        
        for (Coach coach : coaches) {
            System.out.printf("%-10d %-25s %-20s %-12d%n",
                coach.getCoachID(),
                truncate(coach.getName(), 25),
                truncate(coach.getTelephoneNumber(), 20),
                coach.getTeamNumber());
        }
        
        System.out.println("=".repeat(80));
    }
    
    /**
     * Displays details of a single coach.
     */
    private void displayCoachDetails(Coach coach) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  COACH DETAILS");
        System.out.println("=".repeat(60));
        System.out.println("  Coach ID:     " + coach.getCoachID());
        System.out.println("  Name:         " + coach.getName());
        System.out.println("  Telephone:    " + coach.getTelephoneNumber());
        System.out.println("  Team Number:  " + coach.getTeamNumber());
        System.out.println("=".repeat(60));
    }
    
    /**
     * Displays a table of player team history.
     */
    private void displayPlayerHistoryTable(List<PlayerTeamAssociation> history) {
        System.out.println("\n" + "=".repeat(70));
        System.out.printf("%-15s %-15s %-15s %-15s%n", "Association ID", "Team Number", "Year Joined", "Year Left");
        System.out.println("=".repeat(70));
        
        for (PlayerTeamAssociation assoc : history) {
            String yearLeft = (assoc.getYearLeft() != null) ? String.valueOf(assoc.getYearLeft()) : "Current";
            System.out.printf("%-15d %-15d %-15d %-15s%n",
                assoc.getAssociationID(),
                assoc.getTeamNumber(),
                assoc.getYearJoined(),
                yearLeft);
        }
        
        System.out.println("=".repeat(70));
    }
    
    /**
     * Displays a table of work experience records.
     */
    private void displayWorkExperienceTable(List<WorkExperience> experiences) {
        System.out.println("\n" + "=".repeat(70));
        System.out.printf("%-15s %-35s %-15s%n", "Experience ID", "Experience Type", "Duration (years)");
        System.out.println("=".repeat(70));
        
        for (WorkExperience exp : experiences) {
            System.out.printf("%-15d %-35s %-15d%n",
                exp.getExperienceID(),
                truncate(exp.getExperienceType(), 35),
                exp.getDuration());
        }
        
        System.out.println("=".repeat(70));
    }
    
    /**
     * Truncates a string to the specified length.
     */
    private String truncate(String str, int maxLength) {
        if (str == null) {
            return "";
        }
        if (str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength - 3) + "...";
    }
    
    /**
     * Logs an exception to System.err with context.
     * 
     * @param context The context where the exception occurred (e.g., method name)
     * @param e The exception to log
     */
    private void logException(String context, Exception e) {
        System.err.println(e.getClass().getSimpleName() + " in " + context + ": " + e.getMessage());
        if (e.getCause() != null) {
            System.err.println("Caused by: " + e.getCause().getMessage());
        }
    }
}
