@echo off
echo ========================================
echo Building StudySpace Simple .exe
echo ========================================

REM Check if Java is available
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

REM Create executable using jpackage directly
echo.
echo Creating executable (.exe) using jpackage...
"C:\Program Files\Java\jdk-24\bin\jpackage.exe" --name StudySpace --dest target\dist --app-version 1.0.0 --vendor "StudySpace Team" --runtime-image target\jlink-image --module studyspace/com.studyspace.Main --java-options "--add-opens" --java-options "javafx.controls/com.sun.javafx.scene.control.behavior=ALL-UNNAMED" --java-options "--add-opens" --java-options "javafx.controls/com.sun.javafx.scene.control=ALL-UNNAMED" --java-options "--add-opens" --java-options "javafx.base/com.sun.javafx.binding=ALL-UNNAMED" --java-options "--add-opens" --java-options "javafx.graphics/com.sun.javafx.stage=ALL-UNNAMED" --type app-image

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
echo Your application can be found in:
echo target\dist\StudySpace\
echo.
echo The main executable is:
echo target\dist\StudySpace\StudySpace.exe
echo.
echo This is a portable application that:
echo - Runs without installation
echo - Can be copied to any Windows machine
echo - Contains all necessary dependencies
echo - No Windows registry modifications
echo.
pause
