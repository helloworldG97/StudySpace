@echo off
echo ========================================
echo Building StudySpace Application to .exe
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

REM Create native executable with jpackage
echo.
echo Creating native executable (.exe)...
call .\mvnw jpackage:jpackage -Djpackage.executable="C:\Program Files\Java\jdk-24\bin\jpackage.exe"

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
echo Your executable can be found in:
echo target\dist\StudySpace-1.0.0.exe
echo.
echo You can also find the application folder at:
echo target\dist\StudySpace\
echo.
echo The .exe file is a Windows installer that will:
echo - Install the application to Program Files
echo - Create Start Menu shortcuts
echo - Create Desktop shortcut (optional)
echo - Register the application with Windows
echo.
pause
