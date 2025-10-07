@echo off
:menu
cls
echo ========================================
echo    Study Space Database Manager
echo ========================================
echo.
echo 1. View all users
echo 2. Delete a user
echo 3. View all user details
echo 4. View study streaks
echo 5. Exit
echo.
set /p choice="Enter your choice (1-5): "

if "%choice%"=="1" goto view_users
if "%choice%"=="2" goto delete_user
if "%choice%"=="3" goto view_details
if "%choice%"=="4" goto view_streaks
if "%choice%"=="5" goto exit
echo Invalid choice. Please try again.
pause
goto menu

:view_users
cls
echo Loading all users from studyspace_db database...
echo.
echo ========================================
echo           USER OVERVIEW
echo ========================================
C:\xampp\mysql\bin\mysql.exe -u root -e "USE studyspace_db; SELECT id, full_name, email, created_at, last_login_at FROM users ORDER BY created_at DESC;"
echo.
echo ========================================
echo           STUDY STATISTICS
echo ========================================
C:\xampp\mysql\bin\mysql.exe -u root -e "USE studyspace_db; SELECT id, full_name, current_streak, total_study_hours, flashcards_studied, quizzes_taken FROM users ORDER BY current_streak DESC, total_study_hours DESC;"
echo.
pause
goto menu

:delete_user
cls
echo Current users:
echo.
C:\xampp\mysql\bin\mysql.exe -u root -e "USE studyspace_db; SELECT id, full_name, email FROM users ORDER BY created_at DESC;"
echo.
set /p user_id="Enter the user ID to delete: "
if "%user_id%"=="" (
    echo No user ID entered. Returning to menu.
    pause
    goto menu
)
echo.
echo WARNING: This will permanently delete user %user_id% and all their data!
set /p confirm="Are you sure? Type 'DELETE' to confirm: "
if "%confirm%"=="DELETE" (
    echo Deleting user %user_id%...
    C:\xampp\mysql\bin\mysql.exe -u root -e "USE studyspace_db; DELETE FROM users WHERE id='%user_id%';"
    echo User deleted successfully.
) else (
    echo Deletion cancelled.
)
echo.
pause
goto menu

:view_details
cls
echo Loading all user details...
echo.
C:\xampp\mysql\bin\mysql.exe -u root -e "USE studyspace_db; SELECT * FROM users ORDER BY created_at DESC;"
echo.
pause
goto menu

:view_streaks
cls
echo ========================================
echo         STUDY STREAKS & PROGRESS
echo ========================================
echo.
echo Current Streaks (sorted by highest):
C:\xampp\mysql\bin\mysql.exe -u root -e "USE studyspace_db; SELECT id, full_name, current_streak, total_study_hours FROM users ORDER BY current_streak DESC, total_study_hours DESC;"
echo.
echo Study Activity Summary:
C:\xampp\mysql\bin\mysql.exe -u root -e "USE studyspace_db; SELECT id, full_name, flashcards_studied, quizzes_taken, current_streak FROM users ORDER BY flashcards_studied DESC, quizzes_taken DESC;"
echo.
echo ========================================
echo Top Performers:
C:\xampp\mysql\bin\mysql.exe -u root -e "USE studyspace_db; SELECT CONCAT('User: ', full_name, ' | Streak: ', current_streak, ' days | Study Hours: ', total_study_hours, ' | Flashcards: ', flashcards_studied, ' | Quizzes: ', quizzes_taken) as 'Performance Summary' FROM users ORDER BY current_streak DESC, total_study_hours DESC;"
echo.
pause
goto menu

:exit
echo Goodbye!
exit
