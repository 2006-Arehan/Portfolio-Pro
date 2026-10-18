@echo off
title PortfolioPro - Stock Trading & Portfolio Management System
color 0A
echo ===============================================================================
echo                PortfolioPro - Stock Trading & Portfolio Management
echo                    College Capstone Project Submission for HCL
echo ===============================================================================
echo.
echo [1/3] Checking Java runtime environment...
java -version
if %errorlevel% neq 0 (
    echo [ERROR] Java is not installed or not in PATH! Please install JDK 17+ or 21+.
    pause
    exit /b %errorlevel%
)
echo.
echo [2/3] Starting Spring Boot Backend and Real-Time Market Simulator Engine...
echo [INFO] Server will bind to http://localhost:8080
echo [INFO] Persistent Database: H2 at ./data/portfoliopro
echo [INFO] Database Web Console: http://localhost:8080/h2-console
echo.
start "" http://localhost:8080/login.html

REM Use portable maven if available in scratch/tools, else fallback to mvn
if exist "..\tools\apache-maven-3.9.6\bin\mvn.cmd" (
    "..\tools\apache-maven-3.9.6\bin\mvn.cmd" spring-boot:run
) else (
    mvn spring-boot:run
)

pause
