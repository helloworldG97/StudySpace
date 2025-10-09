@echo off
echo ========================================
echo Building StudySpace Portable .exe
echo ========================================

REM Check if Java 21+ is available
java -version 2>&1 | findstr /C:"version" >nul
if %errorlevel% neq 0 (
    echo ERROR: Java is required but not found in PATH
    echo Please install Java 21 or later and add it to your PATH
    pause
    exit /b 1
)

echo Java found. Proceeding with build...

REM Clean previous builds
echo.
echo Cleaning previous builds...
call .\mvnw clean

REM Compile the application
echo.
echo Compiling application...
call .\mvnw compile

if %errorlevel% neq 0 (
    echo ERROR: Compilation failed
    pause
    exit /b 1
)

REM Create jlink image (custom runtime)
echo.
echo Creating custom runtime image...
call .\mvnw javafx:jlink

if %errorlevel% neq 0 (
    echo ERROR: jlink failed
    pause
    exit /b 1
)

REM Create portable executable with jpackage
echo.
echo Creating portable executable (.exe)...
call .\mvnw jpackage:jpackage -Djpackage.executable="C:\Program Files\Java\jdk-24\bin\jpackage.exe" -Djpackage.type=exe -Djpackage.winDirChooser=false -Djpackage.winMenu=false -Djpackage.winShortcut=false

if %errorlevel% neq 0 (
    echo ERROR: jpackage failed
    pause
    exit /b 1
)

echo.
echo ========================================
echo Build completed successfully!
echo ========================================
echo.
echo Your portable executable can be found in:
echo target\dist\StudySpace.exe
echo.
echo This is a portable .exe file that:
echo - Runs without installation
echo - Can be copied to any Windows machine
echo - Contains all necessary dependencies
echo - No Windows registry modifications
echo.
pause
