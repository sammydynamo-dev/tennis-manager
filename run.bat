@echo off
REM Run script for Tennis League Management System (Windows)
REM This script runs the application with the correct classpath

REM Define classpath with all required JARs (Windows uses semicolons)
set CLASSPATH=.;mysql-connector-j-9.1.0.jar

echo Starting Tennis League Management System...
echo Classpath: %CLASSPATH%
echo.

REM Run the application
java -cp "%CLASSPATH%" com.tennisleague.TennisLeagueApp
