@echo off
echo ========================================
echo Running CineConnect Test Suite
echo ========================================
echo.

cd /d "%~dp0"

echo Setting JAVA_HOME...
set "JAVA_HOME=C:\Program Files\Java\jdk-21"
echo Using: %JAVA_HOME%
echo.

echo Running all tests...
echo.

call mvnw.cmd test

echo.
echo ========================================
echo Test Run Complete
echo ========================================
echo.
echo Check test results above or in:
echo target\surefire-reports\
echo.

pause
