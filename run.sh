#!/bin/bash
# Run script for Tennis League Management System
# This script runs the application with the correct classpath

# Define classpath with all required JARs
CLASSPATH=".:mysql-connector-j-9.1.0.jar"

echo "Starting Tennis League Management System..."
echo "Classpath: $CLASSPATH"
echo ""

# Run the application
java -cp "$CLASSPATH" com.tennisleague.TennisLeagueApp
