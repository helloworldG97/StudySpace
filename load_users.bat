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
echo 5. View quiz performance
echo 6. Exit
echo.
set /p choice="Enter your choice (1-6): "

if "%choice%"=="1" goto view_users
if "%choice%"=="2" goto delete_user
if "%choice%"=="3" goto view_details
if "%choice%"=="4" goto view_streaks
if "%choice%"=="5" goto view_quiz_performance
if "%choice%"=="6" goto exit
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

:view_quiz_performance
cls
echo ========================================
echo         QUIZ PERFORMANCE ANALYTICS
echo ========================================
echo.
echo Quiz Scores (sorted by highest):
C:\xampp\mysql\bin\mysql.exe -u root -e "USE studyspace_db; SELECT u.id, u.full_name, q.title as 'Quiz Title', q.best_score, q.times_taken, u.current_streak FROM users u JOIN quizzes q ON u.id = q.user_id WHERE q.best_score > 0 ORDER BY q.best_score DESC, q.times_taken DESC;"
echo.
echo Quiz Activity Summary:
C:\xampp\mysql\bin\mysql.exe -u root -e "USE studyspace_db; SELECT u.id, u.full_name, u.quizzes_taken, AVG(q.best_score) as 'Avg Score', MAX(q.best_score) as 'Best Score', u.current_streak FROM users u LEFT JOIN quizzes q ON u.id = q.user_id WHERE u.quizzes_taken > 0 GROUP BY u.id, u.full_name, u.quizzes_taken, u.current_streak ORDER BY AVG(q.best_score) DESC;"
echo.
echo ========================================
echo Top Quiz Performers:
C:\xampp\mysql\bin\mysql.exe -u root -e "USE studyspace_db; SELECT CONCAT('User: ', u.full_name, ' | Quizzes Taken: ', u.quizzes_taken, ' | Avg Score: ', ROUND(AVG(q.best_score), 1), '% | Best Score: ', MAX(q.best_score), '% | Study Streak: ', u.current_streak, ' days') as 'Quiz Performance Summary' FROM users u LEFT JOIN quizzes q ON u.id = q.user_id WHERE u.quizzes_taken > 0 GROUP BY u.id, u.full_name, u.quizzes_taken, u.current_streak ORDER BY AVG(q.best_score) DESC;"
echo.
echo ========================================
echo Users with No Quiz Activity:
C:\xampp\mysql\bin\mysql.exe -u root -e "USE studyspace_db; SELECT id, full_name, 'No quizzes taken yet' as 'Quiz Status' FROM users WHERE quizzes_taken = 0 OR quizzes_taken IS NULL;"
echo.
pause
goto menu

:exit
echo Goodbye!
exit
