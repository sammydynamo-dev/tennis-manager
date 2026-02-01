# Tennis League Management System

A Java CLI application for managing a Tennis League database using JDBC and MySQL. This system provides comprehensive CRUD operations for teams, players, and coaches with support for tracking player-team associations and coach work experience.

## 🎯 Features

- **Team Management**: Create, read, update, and delete team records
- **Player Management**: Manage players with league-wide identification and team associations
- **Coach Management**: Track coaches assigned to teams with work experience history
- **Player-Team History**: Track player movements between teams over time
- **Work Experience Tracking**: Maintain comprehensive coaching history
- **Data Integrity**: Referential integrity enforcement and validation
- **Reporting** (Optional): Analytical reports for team composition, coach experience, player tenure, and more

## 🏗️ Architecture

The system follows a clean three-tier architecture:

```
Presentation Layer (CLI)
         ↓
Service Layer (Business Logic)
         ↓
Data Access Layer (DAO Pattern)
         ↓
Database (MySQL)
```

### Package Structure

```
com.tennisleague/
├── model/              # Domain entities
├── dao/                # Data Access Objects
├── service/            # Business logic
├── database/           # Connection management
├── ui/                 # CLI interface
├── exception/          # Custom exceptions
└── TennisLeagueApp.java
```

## 🛠️ Technology Stack

- **Language**: Java 24
- **Database**: MySQL 9.3.0
- **JDBC Driver**: MySQL Connector/J 9.1.0
- **Testing**: JUnit 5 + jqwik (property-based testing)

## 📋 Prerequisites

- Java Development Kit (JDK) 24 or higher
- MySQL 9.3.0 or higher
- MySQL Connector/J 9.1.0 (included in repository)

## 🚀 Setup Instructions

### 1. Database Setup

```bash
# Create and populate the database
mysql -u your_username -p < seed-data.sql
```

### 2. Configuration

Create a `config.properties` file in the project root:

```properties
db.url=jdbc:mysql://localhost:3306/TennisLeague
db.user=your_username
db.password=your_password
```

**Important**: Never commit `config.properties` to version control!

### 3. Compilation

```bash
javac -cp .:mysql-connector-j-9.1.0.jar com/tennisleague/**/*.java
```

### 4. Running the Application

```bash
java -cp .:mysql-connector-j-9.1.0.jar com.tennisleague.TennisLeagueApp
```

## 📊 Database Schema

The system uses five main tables:

- **Team**: League teams with manager information
- **Player**: Individual players with league-wide identification
- **Coach**: Coaching staff assigned to teams
- **WorkExperience**: Coach employment and experience records
- **PlayerTeamAssociation**: Historical tracking of player team memberships

### Entity Relationships

```
Team (1) ──────< (N) Coach
  │
  └──────< (N) PlayerTeamAssociation >──────< (N) Player

Coach (1) ──────< (N) WorkExperience
```

## 📖 Documentation

Detailed documentation is available in the `.kiro/specs/tennis-league-management-system/` directory:

- **requirements.md**: Complete requirements with acceptance criteria
- **design.md**: System architecture and design decisions
- **tasks.md**: Implementation plan with task breakdown

## 🧪 Testing

The project includes both unit tests and property-based tests:

```bash
# Run all tests (command depends on build tool setup)
# Details to be added after test implementation
```

### Testing Strategy

- **Unit Tests**: Specific examples and edge cases
- **Property-Based Tests**: Universal properties across all inputs
- **Integration Tests**: End-to-end workflows

## 🎓 Academic Context

This project was developed as part of CSC422 coursework, demonstrating:

- Java programming with JDBC
- Database design and SQL
- Software architecture patterns (DAO, Service Layer)
- Clean code principles
- Collaborative development practices

## 📝 Project Status

- [x] Requirements gathering
- [x] System design
- [x] Implementation planning
- [ ] Core implementation
- [ ] Testing
- [ ] Optional reporting features

## 👥 Contributors

- Brandon Besher
- Frank Gaebel
- Matthew Johnston
- Temitope Olugbemi

## 📄 License

This project is for educational purposes as part of CSC422 coursework.

## 🔗 Related Documents

- CSC422 Week 3 Assignment Seed Data
- Tennis League Information System Report
- Project Plan and Notes

---

**Note**: This is an academic project. Database credentials should be configured locally and never committed to version control.
