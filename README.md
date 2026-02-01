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
- **Testing Framework**: JUnit 5 (Platform Console Standalone 1.10.1)
- **Property-Based Testing**: jqwik 1.9.2

### Required JAR Files

All JAR files are included in the repository:

1. `mysql-connector-j-9.1.0.jar` - MySQL JDBC driver
2. `junit-platform-console-standalone-1.10.1.jar` - JUnit 5 test runner
3. `jqwik-api-1.9.2.jar` - jqwik property-based testing API
4. `jqwik-engine-1.9.2.jar` - jqwik property-based testing engine

## 📋 Prerequisites

- Java Development Kit (JDK) 24 or higher
- MySQL 9.3.0 or higher
- MySQL Connector/J 9.1.0 (included in repository)

## 🚀 Quick Start

### Using Helper Scripts (Recommended)

**Unix/Linux/macOS:**
```bash
./compile.sh    # Compile all source and test files
./run.sh        # Run the application
./test.sh       # Run all tests
```

**Windows:**
```cmd
compile.bat     # Compile all source and test files
run.bat         # Run the application
test.bat        # Run all tests
```

### Manual Setup

#### 1. Database Setup

```bash
# Create and populate the database
mysql -u your_username -p < seed-data.sql
```

#### 2. Configuration

Create a `config.properties` file in the project root:

```properties
db.url=jdbc:mysql://localhost:3306/TennisLeague
db.user=your_username
db.password=your_password
```

**Important**: Never commit `config.properties` to version control!

#### 3. Compilation

**Unix/Linux/macOS:**
```bash
javac -cp .:mysql-connector-j-9.1.0.jar:jqwik-api-1.9.2.jar:jqwik-engine-1.9.2.jar:junit-platform-console-standalone-1.10.1.jar com/tennisleague/**/*.java test/com/tennisleague/**/*.java
```

**Windows:**
```cmd
javac -cp .;mysql-connector-j-9.1.0.jar;jqwik-api-1.9.2.jar;jqwik-engine-1.9.2.jar;junit-platform-console-standalone-1.10.1.jar com\tennisleague\**\*.java test\com\tennisleague\**\*.java
```

#### 4. Running the Application

**Unix/Linux/macOS:**
```bash
java -cp .:mysql-connector-j-9.1.0.jar com.tennisleague.TennisLeagueApp
```

**Windows:**
```cmd
java -cp .;mysql-connector-j-9.1.0.jar com.tennisleague.TennisLeagueApp
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

Comprehensive documentation is available:

- **[USER_GUIDE.md](USER_GUIDE.md)**: Complete user guide for end users (3-5 pages)
  - How to use all features
  - Step-by-step instructions with examples
  - Common workflows and troubleshooting
  
- **[README.md](README.md)**: Project overview and quick start (this file)
  - Architecture and technology stack
  - Setup and installation
  - Testing information

- **[SETUP.md](SETUP.md)**: Detailed setup and configuration guide
  - Manual compilation and execution
  - Troubleshooting common issues
  - Classpath configuration

- **Technical Specifications** (`.kiro/specs/tennis-league-management-system/`):
  - `requirements.md`: Complete requirements with acceptance criteria
  - `design.md`: System architecture and design decisions
  - `tasks.md`: Implementation plan with task breakdown

## 🧪 Testing

The project includes comprehensive testing with 103 tests (all passing):

- **86 Unit Tests**: Specific examples and edge cases
- **17 Property-Based Tests**: Universal properties validated across 100+ iterations each

### Running Tests

**Using Helper Script (Recommended):**

Unix/Linux/macOS:
```bash
./test.sh
```

Windows:
```cmd
test.bat
```

**Manual Execution:**

Unix/Linux/macOS:
```bash
java -jar junit-platform-console-standalone-1.10.1.jar \
    --class-path .:test:mysql-connector-j-9.1.0.jar:jqwik-api-1.9.2.jar:jqwik-engine-1.9.2.jar \
    --scan-class-path \
    --include-classname ".*Test"
```

Windows:
```cmd
java -jar junit-platform-console-standalone-1.10.1.jar --class-path .;test;mysql-connector-j-9.1.0.jar;jqwik-api-1.9.2.jar;jqwik-engine-1.9.2.jar --scan-class-path --include-classname ".*Test"
```

### Test Coverage

✅ **17 Correctness Properties Validated:**
- Properties 1-12: CRUD operations and associations
- Properties 15-18: Work experience and validation
- Property 20: Referential integrity

### Testing Strategy

- **Unit Tests**: Specific examples and edge cases
- **Property-Based Tests**: Universal properties across all inputs (100 iterations minimum)
- **Edge Case Tests**: Boundary conditions, null handling, constraint violations

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
- [x] Core implementation (MVP complete)
- [x] Testing (103 tests, all passing)
- [x] Database connection management
- [x] DAO layer implementation
- [x] Service layer implementation
- [x] CLI interface
- [x] Input validation
- [x] Error handling
- [x] User documentation
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
