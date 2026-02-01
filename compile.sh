#!/bin/bash
# Compilation script for Tennis League Management System
# This script compiles all source files with the correct classpath

# Define classpath with all required JARs
CLASSPATH=".:mysql-connector-j-9.1.0.jar:jqwik-api-1.9.2.jar:jqwik-engine-1.9.2.jar:junit-platform-console-standalone-1.10.1.jar"

echo "Compiling Tennis League Management System..."
echo "Classpath: $CLASSPATH"
echo ""

# Compile main source files
echo "Compiling main source files..."
javac -cp "$CLASSPATH" com/tennisleague/**/*.java

if [ $? -eq 0 ]; then
    echo "✓ Main source files compiled successfully"
else
    echo "✗ Compilation failed for main source files"
    exit 1
fi

# Compile test files
echo ""
echo "Compiling test files..."
javac -cp "$CLASSPATH" test/com/tennisleague/**/*.java

if [ $? -eq 0 ]; then
    echo "✓ Test files compiled successfully"
else
    echo "✗ Compilation failed for test files"
    exit 1
fi

echo ""
echo "========================================="
echo "Compilation completed successfully!"
echo "========================================="
