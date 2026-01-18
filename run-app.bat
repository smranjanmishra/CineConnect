@echo off
echo ========================================
echo CineConnect Application Launcher
echo ========================================
echo.

REM Set JAVA_HOME if not set
if not defined JAVA_HOME (
    echo Setting JAVA_HOME to C:\Program Files\Java\jdk-21
    set "JAVA_HOME=C:\Program Files\Java\jdk-21"
)

echo Using Java: %JAVA_HOME%
echo.

REM Check if Maven is installed
where mvn >nul 2>nul
if %ERRORLEVEL% EQU 0 (
    echo Maven found. Building and running application...
    call mvn clean spring-boot:run
) else (
    echo Maven not found in PATH.
    echo.
    echo Please run the application using one of these methods:
    echo 1. Open the project in IntelliJ IDEA or Eclipse
    echo 2. Right-click on BookMyShowApplication.java
    echo 3. Select "Run" or "Debug"
    echo.
    echo Or install Maven and add it to your PATH.
    echo.
    pause
)
