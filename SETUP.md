# Tennis League Management System - Setup Guide

## Overview

This guide provides detailed technical setup instructions for developers and system administrators. For end-user instructions on how to use the application, see **[USER_GUIDE.md](USER_GUIDE.md)**.

## Project Structure

The project follows a layered architecture with the following package structure:

```
com.tennisleague/
├── model/              # Domain entities (Team, Player, Coach, etc.)
├── dao/                # Data Access Object interfaces
│   └── impl/           # DAO implementations
├── service/            # Business logic interfaces
│   └── impl/           # Service implementations
├── database/           # Database connection management
├── ui/                 # User interface (CLI)
└── exception/          # Custom exception classes
```

## Prerequisites

- Java 24 or higher
- MySQL Server
- MySQL Connector/J 9.1.0 (included: `mysql-connector-j-9.1.0.jar`)
- JUnit Platform Console Standalone 1.10.1 (included: `junit-platform-console-standalone-1.10.1.jar`)
- jqwik 1.9.2 (included: `jqwik-api-1.9.2.jar`, `jqwik-engine-1.9.2.jar`)

## Required JAR Files

The following JAR files must be present in the project root directory:

1. **mysql-connector-j-9.1.0.jar** - MySQL JDBC driver for database connectivity
2. **junit-platform-console-standalone-1.10.1.jar** - JUnit 5 test runner
3. **jqwik-api-1.9.2.jar** - jqwik property-based testing API
4. **jqwik-engine-1.9.2.jar** - jqwik property-based testing engine

## Database Configuration

1. Edit the `config.properties` file with your database credentials:
   ```properties
   db.url=jdbc:mysql://localhost:3306/TennisLeague
   db.user=your_username
   db.password=your_password
   ```

2. Initialize the database using the provided SQL script:
   ```bash
   mysql -u your_username -p < seed-data.sql
   ```

## Quick Start (Using Helper Scripts)

### Unix/Linux/macOS

1. **Compile the project:**
   ```bash
   ./compile.sh
   ```

2. **Run the application:**
   ```bash
   ./run.sh
   ```

3. **Run all tests:**
   ```bash
   ./test.sh
   ```

### Windows

1. **Compile the project:**
   ```cmd
   compile.bat
   ```

2. **Run the application:**
   ```cmd
   run.bat
   ```

3. **Run all tests:**
   ```cmd
   test.bat
   ```

## Manual Compilation and Execution

### Compilation

**Unix/Linux/macOS:**
```bash
javac -cp .:mysql-connector-j-9.1.0.jar:jqwik-api-1.9.2.jar:jqwik-engine-1.9.2.jar:junit-platform-console-standalone-1.10.1.jar com/tennisleague/**/*.java test/com/tennisleague/**/*.java
```

**Windows:**
```cmd
javac -cp .;mysql-connector-j-9.1.0.jar;jqwik-api-1.9.2.jar;jqwik-engine-1.9.2.jar;junit-platform-console-standalone-1.10.1.jar com\tennisleague\**\*.java test\com\tennisleague\**\*.java
```

### Running the Application

**Unix/Linux/macOS:**
```bash
java -cp .:mysql-connector-j-9.1.0.jar com.tennisleague.TennisLeagueApp
```

**Windows:**
```cmd
java -cp .;mysql-connector-j-9.1.0.jar com.tennisleague.TennisLeagueApp
```

### Running Tests

**Unix/Linux/macOS:**
```bash
java -jar junit-platform-console-standalone-1.10.1.jar --class-path .:test:mysql-connector-j-9.1.0.jar:jqwik-api-1.9.2.jar:jqwik-engine-1.9.2.jar --scan-class-path --include-classname ".*Test"
```

**Windows:**
```cmd
java -jar junit-platform-console-standalone-1.10.1.jar --class-path .;test;mysql-connector-j-9.1.0.jar;jqwik-api-1.9.2.jar;jqwik-engine-1.9.2.jar --scan-class-path --include-classname ".*Test"
```

## Security Note

⚠️ **IMPORTANT**: The `config.properties` file contains sensitive database credentials and is excluded from version control via `.gitignore`. Never commit this file to the repository.

## Next Steps

1. Configure your database credentials in `config.properties`
2. Run the `seed-data.sql` script to create the database schema
3. Compile and run the application

For detailed information about each component, refer to the Javadoc documentation and individual class comments.


## Classpath Details

### For Compilation
The classpath must include:
- Current directory (`.` or `.;` on Windows)
- `mysql-connector-j-9.1.0.jar` - Database connectivity
- `jqwik-api-1.9.2.jar` - Property-based testing API
- `jqwik-engine-1.9.2.jar` - Property-based testing engine
- `junit-platform-console-standalone-1.10.1.jar` - JUnit 5 platform

### For Running the Application
The classpath must include:
- Current directory (`.` or `.;` on Windows)
- `mysql-connector-j-9.1.0.jar` - Database connectivity

### For Running Tests
The classpath must include:
- Current directory (`.` or `.;` on Windows)
- `test` directory - Test classes location
- `mysql-connector-j-9.1.0.jar` - Database connectivity
- `jqwik-api-1.9.2.jar` - Property-based testing API
- `jqwik-engine-1.9.2.jar` - Property-based testing engine

**Note:** On Unix/Linux/macOS, classpath entries are separated by colons (`:`). On Windows, they are separated by semicolons (`;`).

## Troubleshooting

### ClassNotFoundException
If you encounter `ClassNotFoundException`, ensure:
1. All required JAR files are in the project root directory
2. The classpath includes all necessary JARs
3. You're using the correct path separator for your OS (`:` for Unix, `;` for Windows)

### Compilation Errors
If compilation fails:
1. Verify Java 24 or higher is installed: `java -version`
2. Check that all source files are present
3. Ensure the classpath includes all required JARs

### Database Connection Errors
If the application can't connect to the database:
1. Verify MySQL server is running
2. Check `config.properties` has correct credentials
3. Ensure the TennisLeague database exists
4. Verify `mysql-connector-j-9.1.0.jar` is in the classpath

### Test Failures
If tests fail:
1. Ensure the database is properly initialized with `seed-data.sql`
2. Verify database credentials in `config.properties`
3. Check that all test dependencies are in the classpath
4. Ensure no other application is using the test database

## Project Files

### Helper Scripts
- `compile.sh` / `compile.bat` - Compile all source and test files
- `run.sh` / `run.bat` - Run the application
- `test.sh` / `test.bat` - Run all tests

### Configuration Files
- `config.properties` - Database connection settings (not in version control)
- `seed-data.sql` - Database schema and seed data

### JAR Dependencies
- `mysql-connector-j-9.1.0.jar` - MySQL JDBC driver
- `junit-platform-console-standalone-1.10.1.jar` - JUnit 5 test runner
- `jqwik-api-1.9.2.jar` - Property-based testing API
- `jqwik-engine-1.9.2.jar` - Property-based testing engine

## Security Note

⚠️ **IMPORTANT**: The `config.properties` file contains sensitive database credentials and is excluded from version control via `.gitignore`. Never commit this file to the repository.

## Next Steps

1. Configure your database credentials in `config.properties`
2. Run the `seed-data.sql` script to create the database schema
3. Use `./compile.sh` (or `compile.bat` on Windows) to compile the project
4. Use `./test.sh` (or `test.bat` on Windows) to verify all tests pass
5. Use `./run.sh` (or `run.bat` on Windows) to start the application

For detailed information about each component, refer to the Javadoc documentation and individual class comments.
