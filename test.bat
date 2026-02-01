@echo off
REM Test script for Tennis League Management System (Windows)
REM This script runs all tests (unit tests and property-based tests)

REM Define classpath with all required JARs (Windows uses semicolons)
set CLASSPATH=.;test;mysql-connector-j-9.1.0.jar;jqwik-api-1.9.2.jar;jqwik-engine-1.9.2.jar;junit-platform-console-standalone-1.10.1.jar

echo Running Tennis League Management System Tests...
echo Classpath: %CLASSPATH%
echo.

REM Run all tests
java -jar junit-platform-console-standalone-1.10.1.jar --class-path "%CLASSPATH%" --scan-class-path --include-classname ".*Test"

echo.
echo =========================================
echo Test execution completed!
echo =========================================
