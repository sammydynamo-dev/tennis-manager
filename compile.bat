@echo off
REM Compilation script for Tennis League Management System (Windows)
REM This script compiles all source files with the correct classpath

REM Define classpath with all required JARs (Windows uses semicolons)
set CLASSPATH=.;mysql-connector-j-9.1.0.jar;jqwik-api-1.9.2.jar;jqwik-engine-1.9.2.jar;junit-platform-console-standalone-1.10.1.jar

echo Compiling Tennis League Management System...
echo Classpath: %CLASSPATH%
echo.

REM Compile main source files
echo Compiling main source files...
javac -cp "%CLASSPATH%" com\tennisleague\model\*.java com\tennisleague\exception\*.java com\tennisleague\database\*.java com\tennisleague\dao\*.java com\tennisleague\dao\impl\*.java com\tennisleague\service\*.java com\tennisleague\service\impl\*.java com\tennisleague\ui\*.java com\tennisleague\*.java

if %errorlevel% equ 0 (
    echo [OK] Main source files compiled successfully
) else (
    echo [ERROR] Compilation failed for main source files
    exit /b 1
)

REM Compile test files
echo.
echo Compiling test files...
javac -cp "%CLASSPATH%" test\com\tennisleague\database\*.java test\com\tennisleague\dao\impl\*.java test\com\tennisleague\service\impl\*.java test\com\tennisleague\ui\*.java

if %errorlevel% equ 0 (
    echo [OK] Test files compiled successfully
) else (
    echo [ERROR] Compilation failed for test files
    exit /b 1
)

echo.
echo =========================================
echo Compilation completed successfully!
echo =========================================
