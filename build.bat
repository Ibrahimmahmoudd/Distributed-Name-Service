@echo off
REM Build script for Distributed Name Service
REM Creates bin directory and compiles all Java source files

if not exist bin mkdir bin
javac -d bin src/Group_SourceCode/*.java

if errorlevel 1 (
    echo Compilation failed!
    exit /b 1
) else (
    echo Compilation successful!
    echo.
    echo Run the application with one of these commands:
    echo   java -cp bin Group_SourceCode.Main              (interactive menu)
    echo   java -cp bin Group_SourceCode.Main server
    echo   java -cp bin Group_SourceCode.Main client [host] [port]
)
