@echo off
cd /d "%~dp0"

where javac >nul 2>nul
if errorlevel 1 (
    if defined JAVA_HOME set "PATH=%JAVA_HOME%\bin;%PATH%"
)

where javac >nul 2>nul
if errorlevel 1 (
    echo.
    echo [ERROR] javac not found. JDK is not in PATH.
    echo Install JDK or add its "bin" folder to PATH / set JAVA_HOME.
    echo.
    pause
    exit /b 1
)

if not exist out mkdir out
javac -encoding UTF-8 -d out src\*.java
if errorlevel 1 (
    echo.
    echo [ERROR] Compilation failed. See messages above.
    echo.
    pause
    exit /b 1
)

chcp 65001 >nul
echo Starting TechParts... the site will open in your browser.
java -Dfile.encoding=UTF-8 -cp out Main
echo.
pause
