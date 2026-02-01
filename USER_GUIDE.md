# Tennis League Management System - User Guide

## 1. Introduction

The Tennis League Management System is a command-line application designed to help league administrators manage teams, players, and coaches efficiently. This guide will walk you through all the features and show you how to perform common tasks.

### What Can You Do?

- **Manage Teams**: Add, view, update, and delete team records
- **Manage Players**: Track players and their team associations over time
- **Manage Coaches**: Maintain coaching staff and their work experience
- **Track History**: View player movements between teams and coach experience records

### Who Should Use This Guide?

This guide is for league administrators, team managers, and anyone responsible for maintaining tennis league records.

---

## 2. Getting Started

### Launching the Application

**On macOS/Linux:**
```bash
./run.sh
```

**On Windows:**
```cmd
run.bat
```

### Main Menu

When you start the application, you'll see the main menu:

```
============================================================
       TENNIS LEAGUE MANAGEMENT SYSTEM
============================================================
  1. Team Management      - Manage teams and rosters
  2. Player Management    - Manage players and associations
  3. Coach Management     - Manage coaches and experience
  4. Exit                 - Close the application
============================================================
Select an option (1-4):
```

**Navigation Tips:**
- Type the number of your choice and press Enter
- You can always return to the main menu from any submenu
- Type `4` to exit the application safely

---

## 3. Team Management

Select option `1` from the main menu to access Team Management.

### 3.1 Adding a New Team

**Steps:**
1. Select `1` (Add Team) from the Team Management menu
2. Enter the following information when prompted:
   - **Team Number**: A unique number (e.g., 101, 202)
   - **Team Name**: The team's name (e.g., "Thunder Strikers")
   - **City**: The team's home city (e.g., "Boston")
   - **Manager Name**: The team manager's name (e.g., "John Smith")

**Example:**
```
Enter Team Number (e.g., 101): 101
Enter Team Name (e.g., Thunder Strikers): Thunder Strikers
Enter City (e.g., Boston): Boston
Enter Manager Name (e.g., John Smith): John Smith

============================================================
  ✓ SUCCESS: Team added successfully!
  Team Number: 101
  Team Name: Thunder Strikers
============================================================
```

**Important Notes:**
- Team numbers must be unique
- All fields are required
- Team numbers cannot be changed after creation

### 3.2 Viewing All Teams

**Steps:**
1. Select `2` (View All Teams) from the Team Management menu
2. The system displays a table of all teams with their details

**What You'll See:**
- Team Number
- Team Name
- City
- Manager Name

### 3.3 Viewing a Specific Team

**Steps:**
1. Select `3` (View Team by Number)
2. Enter the team number when prompted
3. The system displays detailed information for that team

**Tip:** If you don't know the team number, use "View All Teams" first.

### 3.4 Updating Team Information

**Steps:**
1. Select `4` (Update Team)
2. Enter the team number you want to update
3. Enter the new information for all fields
4. The system confirms the update

**Note:** You must provide all fields even if you're only changing one.

### 3.5 Deleting a Team

**Steps:**
1. Select `5` (Delete Team)
2. Enter the team number to delete
3. Confirm the deletion by typing `yes`

**Warning:**
- This action cannot be undone
- If the team has associated coaches or players, you may need to remove those associations first

---

## 4. Player Management

Select option `2` from the main menu to access Player Management.

### 4.1 Adding a New Player

**Steps:**
1. Select `1` (Add Player) from the Player Management menu
2. Enter the following information:
   - **League Wide Number**: A unique identifier (e.g., 1001)
   - **Player Name**: The player's full name (e.g., "Jane Doe")
   - **Age**: The player's age (must be between 1 and 120)

**Example:**
```
Enter League Wide Number (unique, e.g., 1001): 1001
Enter Player Name (e.g., Jane Doe): Jane Doe
Enter Age (1-120): 25

============================================================
  ✓ SUCCESS: Player added successfully!
  Player ID: 15
  League Wide Number: 1001
============================================================
```

**Important Notes:**
- League Wide Numbers must be unique across all players
- The system automatically assigns a Player ID
- Age must be realistic (1-120)

### 4.2 Viewing All Players

**Steps:**
1. Select `2` (View All Players)
2. The system displays a table of all players

**What You'll See:**
- Player ID
- League Wide Number
- Player Name
- Age

### 4.3 Associating a Player with a Team

**Steps:**
1. Select `6` (Associate Player with Team)
2. Enter the following information:
   - **Player ID**: The player's ID number
   - **Team Number**: The team's number
   - **Year Joined**: The year the player joined (e.g., 2024)

**Example:**
```
Enter Player ID (e.g., 1): 15
Enter Team Number (e.g., 101): 101
Enter Year Joined (e.g., 2024): 2024

============================================================
  ✓ SUCCESS: Player associated with team!
  Player ID: 15 → Team: 101
  Year Joined: 2024
============================================================
```

**Note:** Both the player and team must exist before creating an association.

### 4.4 Removing a Player from a Team

**Steps:**
1. Select `7` (Remove Player from Team)
2. Enter the following information:
   - **Player ID**: The player's ID
   - **Team Number**: The team's number
   - **Year Left**: The year the player left (e.g., 2024)

**Important:** This doesn't delete the player or association—it just records when they left the team.

### 4.5 Viewing Player Team History

**Steps:**
1. Select `8` (View Player Team History)
2. Enter the Player ID
3. The system displays all teams the player has been associated with

**What You'll See:**
- Team Number
- Year Joined
- Year Left (if applicable)
- Complete career timeline

---

## 5. Coach Management

Select option `3` from the main menu to access Coach Management.

### 5.1 Adding a New Coach

**Steps:**
1. Select `1` (Add Coach) from the Coach Management menu
2. Enter the following information:
   - **Coach Name**: The coach's full name (e.g., "Mike Johnson")
   - **Telephone Number**: Contact number (e.g., "+1-555-0123")
   - **Team Number**: The team they'll coach

**Example:**
```
Enter Coach Name (e.g., Mike Johnson): Mike Johnson
Enter Telephone Number (e.g., +1-555-0123): +1-555-0123
Enter Team Number (e.g., 101): 101

============================================================
  ✓ SUCCESS: Coach added successfully!
  Coach ID: 8
============================================================
```

**Important:** The team must exist before you can assign a coach to it.

### 5.2 Viewing All Coaches

**Steps:**
1. Select `2` (View All Coaches)
2. The system displays all coaches with their team assignments

### 5.3 Viewing Coaches by Team

**Steps:**
1. Select `8` (View Coaches by Team)
2. Enter the team number
3. The system displays all coaches assigned to that team

**Use Case:** Quickly see all coaching staff for a specific team.

### 5.4 Adding Work Experience

**Steps:**
1. Select `6` (Add Work Experience)
2. Enter the following information:
   - **Coach ID**: The coach's ID number
   - **Experience Type**: Description (e.g., "Assistant Coach at Lakers")
   - **Duration**: Years of experience (e.g., 5)

**Example:**
```
Enter Coach ID (e.g., 8): 8
Enter Experience Type (e.g., Assistant Coach at Lakers): Head Coach at Thunder
Enter Duration in years (e.g., 5): 3

============================================================
  ✓ SUCCESS: Work experience added successfully!
============================================================
```

### 5.5 Viewing Coach Work Experience

**Steps:**
1. Select `7` (View Coach Work Experience)
2. Enter the Coach ID
3. The system displays all work experience records for that coach

---

## 6. Common Tasks & Workflows

### Setting Up a New Team

1. **Add the Team** (Team Management → Add Team)
   - Enter team number, name, city, and manager

2. **Add Coaches** (Coach Management → Add Coach)
   - Add head coach and assistant coaches
   - Assign them to the team number

3. **Add Players** (Player Management → Add Player)
   - Create player records

4. **Associate Players with Team** (Player Management → Associate Player with Team)
   - Link each player to the team

### Tracking a Player's Career

1. **View Player History** (Player Management → View Player Team History)
   - Enter the player's ID
   - See complete timeline of team associations

2. **Update Current Team** (Player Management → Remove Player from Team)
   - Record when they left their previous team

3. **Add New Team** (Player Management → Associate Player with Team)
   - Record their new team and join date

### Managing Coaching Staff Changes

1. **View Current Coaches** (Coach Management → View Coaches by Team)
   - See who's currently coaching the team

2. **Add New Coach** (Coach Management → Add Coach)
   - Assign new coach to the team

3. **Remove Old Coach** (Coach Management → Delete Coach)
   - Remove coach who's leaving (if needed)

---

## 7. Error Messages & Troubleshooting

### Common Error Messages

**"Team Number already exists"**
- **Cause:** You're trying to create a team with a number that's already in use
- **Solution:** Choose a different team number

**"Player with LeagueWideNumber already exists"**
- **Cause:** This League Wide Number is already assigned to another player
- **Solution:** Use a unique League Wide Number

**"Team with TeamNumber X not found"**
- **Cause:** The team doesn't exist in the database
- **Solution:** Verify the team number using "View All Teams"

**"Age must be between 1 and 120"**
- **Cause:** Invalid age value entered
- **Solution:** Enter a realistic age value

**"Year Left must be >= Year Joined"**
- **Cause:** You're trying to set a leave date before the join date
- **Solution:** Ensure the Year Left is the same year or later than Year Joined

**"This team has associated records (coaches/players)"**
- **Cause:** You're trying to delete a team that has coaches or players
- **Solution:** Remove all coaches and player associations first, then delete the team

### Input Tips

- **Numbers Only**: When asked for IDs, numbers, or years, enter only digits (no letters or symbols)
- **Required Fields**: All fields marked as required must be filled in
- **Unique Values**: Team Numbers and League Wide Numbers must be unique
- **Confirmation**: When deleting, type `yes` or `y` to confirm, anything else cancels

### Getting Help

If you encounter an error:
1. Read the error message carefully—it usually tells you what went wrong
2. Check that all required information is entered correctly
3. Verify that referenced records (teams, players, coaches) exist
4. Use the "View All" options to see available records

---

## 8. Tips & Best Practices

### Data Entry Best Practices

- **Use Consistent Numbering**: Establish a numbering scheme for teams (e.g., 100-199 for one division)
- **Keep Names Clear**: Use full names for players and coaches
- **Record Dates Accurately**: Always enter the correct year for joins and leaves
- **Verify Before Deleting**: Always check what you're deleting—deletions cannot be undone

### Workflow Recommendations

- **Start with Teams**: Create all teams before adding players and coaches
- **Add Coaches Early**: Assign coaches to teams before the season starts
- **Track Changes Promptly**: Update player associations when transfers happen
- **Review Regularly**: Use "View All" options to verify data accuracy

### Data Integrity

- **Don't Delete Active Records**: Remove associations before deleting teams, players, or coaches
- **Update, Don't Delete**: When information changes, use Update instead of Delete/Re-add
- **Maintain History**: Use the "Remove from Team" feature to preserve player history

---

## 9. Quick Reference

### Main Menu Options
- `1` - Team Management
- `2` - Player Management
- `3` - Coach Management
- `4` - Exit

### Team Management Options
- `1` - Add Team
- `2` - View All Teams
- `3` - View Team by Number
- `4` - Update Team
- `5` - Delete Team
- `6` - Return to Main Menu

### Player Management Options
- `1` - Add Player
- `2` - View All Players
- `3` - View Player by ID
- `4` - Update Player
- `5` - Delete Player
- `6` - Associate Player with Team
- `7` - Remove Player from Team
- `8` - View Player Team History
- `9` - Return to Main Menu

### Coach Management Options
- `1` - Add Coach
- `2` - View All Coaches
- `3` - View Coach by ID
- `4` - Update Coach
- `5` - Delete Coach
- `6` - Add Work Experience
- `7` - View Coach Work Experience
- `8` - View Coaches by Team
- `9` - Return to Main Menu

---

## 10. Support & Additional Resources

### Documentation
- **README.md**: Project overview and technical details
- **SETUP.md**: Installation and configuration instructions
- **Design Documentation**: `.kiro/specs/tennis-league-management-system/`

### For Technical Issues
- Verify database connection in `config.properties`
- Ensure MySQL server is running
- Check that the TennisLeague database exists
- Review SETUP.md for troubleshooting steps

---

**Version:** 1.0  
**Last Updated:** February 2026  
**For:** CSC422 Tennis League Management System
