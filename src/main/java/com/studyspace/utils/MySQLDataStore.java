package com.studyspace.utils;

import com.studyspace.models.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * MySQL-enabled DataStore implementation
 */
public class MySQLDataStore {
    private static MySQLDataStore instance;
    private DatabaseConnection dbConnection;
    private ObjectMapper objectMapper;
    
    // Current session
    private User currentUser;
    
    private MySQLDataStore() {
        this.dbConnection = DatabaseConnection.getInstance();
        this.objectMapper = new ObjectMapper();
    }
    
    public static MySQLDataStore getInstance() {
        if (instance == null) {
            instance = new MySQLDataStore();
        }
        return instance;
    }
    
    // Authentication methods
    public boolean authenticateUser(String email, String password) {
        try {
            String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
            ResultSet rs = dbConnection.executeQuery(sql, email.toLowerCase(), password);
            
            if (rs.next()) {
                currentUser = mapResultSetToUser(rs);
                System.out.println("Authentication successful for: " + currentUser.getFullName());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Authentication error: " + e.getMessage());
        }
        return false;
    }
    
    public User registerUser(String fullName, String email, String password) {
        try {
            // Check if user already exists
            String checkSql = "SELECT id FROM users WHERE email = ?";
            ResultSet rs = dbConnection.executeQuery(checkSql, email.toLowerCase());
            if (rs.next()) {
                System.out.println("User already exists: " + email);
                return null;
            }
            
            // Create new user
            String id = "user_" + System.currentTimeMillis();
            String sql = "INSERT INTO users (id, full_name, email, password, created_at) VALUES (?, ?, ?, ?, ?)";
            dbConnection.executeUpdate(sql, id, fullName, email.toLowerCase(), password, Timestamp.valueOf(LocalDateTime.now()));
            
            currentUser = new User(fullName, email.toLowerCase(), password);
            currentUser.setId(id);
            System.out.println("User registered successfully: " + currentUser.getFullName());
            return currentUser;
            
        } catch (SQLException e) {
            System.err.println("Registration error: " + e.getMessage());
        }
        return null;
    }
    
    public void logout() {
        currentUser = null;
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    
    public void updateUser(User user) {
        try {
            String sql = "UPDATE users SET full_name = ?, flashcards_studied = ?, " +
                        "quizzes_taken = ?, current_streak = ?, total_study_hours = ?, " +
                        "last_login_at = ? WHERE id = ?";
            
            Timestamp lastLoginTimestamp = user.getLastLoginAt() != null ? 
                Timestamp.valueOf(user.getLastLoginAt()) : null;
            
            dbConnection.executeUpdate(sql, user.getFullName(), user.getFlashcardsStudied(), 
                                    user.getQuizzesTaken(), 
                                    user.getCurrentStreak(), user.getTotalStudyHours(), 
                                    lastLoginTimestamp, user.getId());
            
            System.out.println("User updated successfully: " + user.getFullName() + 
                             " (Streak: " + user.getCurrentStreak() + " days)");
        } catch (SQLException e) {
            System.err.println("Update user error: " + e.getMessage());
        }
    }
    
    public boolean isEmailTaken(String email) {
        try {
            String sql = "SELECT id FROM users WHERE email = ?";
            ResultSet rs = dbConnection.executeQuery(sql, email.toLowerCase());
            return rs.next();
        } catch (SQLException e) {
            System.err.println("Check email error: " + e.getMessage());
        }
        return false;
    }
    
    public void deleteUser(String userId) {
        try {
            // Delete user and all related data (cascade delete will handle related tables)
            String sql = "DELETE FROM users WHERE id = ?";
            dbConnection.executeUpdate(sql, userId);
            System.out.println("User account deleted: " + userId);
        } catch (SQLException e) {
            System.err.println("Delete user error: " + e.getMessage());
            throw new RuntimeException("Failed to delete user account", e);
        }
    }
    
    public void updateUserPassword(String userId, String newPassword) {
        try {
            String sql = "UPDATE users SET password = ? WHERE id = ?";
            dbConnection.executeUpdate(sql, newPassword, userId);
            System.out.println("Password updated for user: " + userId);
        } catch (SQLException e) {
            System.err.println("Update password error: " + e.getMessage());
            throw new RuntimeException("Failed to update password", e);
        }
    }
    
    // Flashcard Deck methods
    public List<FlashcardDeck> getAllFlashcardDecks() {
        List<FlashcardDeck> decks = new ArrayList<>();
        if (currentUser == null) {
            System.err.println("No user logged in - cannot retrieve flashcard decks");
            return decks;
        }
        try {
            String sql = "SELECT * FROM flashcard_decks WHERE user_id = ? ORDER BY created_at DESC";
            ResultSet rs = dbConnection.executeQuery(sql, currentUser.getId());
            
            while (rs.next()) {
                FlashcardDeck deck = mapResultSetToFlashcardDeck(rs);
                // Load flashcards for this deck
                deck.setFlashcards(getFlashcardsForDeck(deck.getId()));
                decks.add(deck);
            }
        } catch (SQLException e) {
            System.err.println("Get flashcard decks error: " + e.getMessage());
        }
        return decks;
    }
    
    public FlashcardDeck getFlashcardDeck(String id) {
        try {
            String sql = "SELECT * FROM flashcard_decks WHERE id = ?";
            ResultSet rs = dbConnection.executeQuery(sql, id);
            
            if (rs.next()) {
                FlashcardDeck deck = mapResultSetToFlashcardDeck(rs);
                deck.setFlashcards(getFlashcardsForDeck(id));
                return deck;
            }
        } catch (SQLException e) {
            System.err.println("Get flashcard deck error: " + e.getMessage());
        }
        return null;
    }
    
    public void saveFlashcardDeck(FlashcardDeck deck) {
        try {
            // Check if deck exists
            String checkSql = "SELECT id FROM flashcard_decks WHERE id = ?";
            ResultSet rs = dbConnection.executeQuery(checkSql, deck.getId());
            
            if (rs.next()) {
                // Update existing deck
                String updateSql = "UPDATE flashcard_decks SET title = ?, description = ?, subject = ?, " +
                                 "difficulty = ?, last_studied = ?, total_study_sessions = ? WHERE id = ?";
                dbConnection.executeUpdate(updateSql, deck.getTitle(), deck.getDescription(), 
                                        deck.getSubject(), deck.getDifficulty().toString(),
                                        deck.getLastStudied() != null ? Timestamp.valueOf(deck.getLastStudied()) : null,
                                        deck.getTotalStudySessions(), deck.getId());
            } else {
                // Insert new deck
                if (currentUser == null) {
                    System.err.println("No user logged in - cannot save flashcard deck");
                    return;
                }
                String insertSql = "INSERT INTO flashcard_decks (id, user_id, title, description, subject, difficulty, created_at) " +
                                 "VALUES (?, ?, ?, ?, ?, ?, ?)";
                dbConnection.executeUpdate(insertSql, deck.getId(), currentUser.getId(), deck.getTitle(), deck.getDescription(),
                                        deck.getSubject(), deck.getDifficulty().toString(), 
                                        Timestamp.valueOf(deck.getCreatedAt()));
            }
            
            // Save flashcards
            for (Flashcard card : deck.getFlashcards()) {
                saveFlashcard(card, deck.getId());
            }
            
        } catch (SQLException e) {
            System.err.println("Save flashcard deck error: " + e.getMessage());
        }
    }
    
    public void deleteFlashcardDeck(String id) {
        if (currentUser == null) {
            System.err.println("No user logged in - cannot delete flashcard deck");
            return;
        }
        try {
            String sql = "DELETE FROM flashcard_decks WHERE id = ? AND user_id = ?";
            dbConnection.executeUpdate(sql, id, currentUser.getId());
        } catch (SQLException e) {
            System.err.println("Delete flashcard deck error: " + e.getMessage());
        }
    }
    
    private List<Flashcard> getFlashcardsForDeck(String deckId) {
        List<Flashcard> flashcards = new ArrayList<>();
        try {
            String sql = "SELECT * FROM flashcards WHERE deck_id = ?";
            ResultSet rs = dbConnection.executeQuery(sql, deckId);
            
            while (rs.next()) {
                flashcards.add(mapResultSetToFlashcard(rs));
            }
        } catch (SQLException e) {
            System.err.println("Get flashcards error: " + e.getMessage());
        }
        return flashcards;
    }
    
    private void saveFlashcard(Flashcard card, String deckId) {
        try {
            String checkSql = "SELECT id FROM flashcards WHERE id = ?";
            ResultSet rs = dbConnection.executeQuery(checkSql, card.getId());
            
            if (rs.next()) {
                // Update existing flashcard
                String updateSql = "UPDATE flashcards SET question = ?, answer = ?, difficulty = ?, " +
                                 "last_studied = ?, times_studied = ?, is_correct = ? WHERE id = ?";
                dbConnection.executeUpdate(updateSql, card.getQuestion(), card.getAnswer(),
                                        card.getDifficulty().toString(),
                                        card.getLastStudied() != null ? Timestamp.valueOf(card.getLastStudied()) : null,
                                        card.getTimesStudied(), card.isCorrect(), card.getId());
            } else {
                // Insert new flashcard
                String insertSql = "INSERT INTO flashcards (id, deck_id, question, answer, difficulty, created_at) " +
                                 "VALUES (?, ?, ?, ?, ?, ?)";
                dbConnection.executeUpdate(insertSql, card.getId(), deckId, card.getQuestion(), 
                                        card.getAnswer(), card.getDifficulty().toString(),
                                        Timestamp.valueOf(card.getCreatedAt()));
            }
        } catch (SQLException e) {
            System.err.println("Save flashcard error: " + e.getMessage());
        }
    }
    
    // Quiz methods
    public List<Quiz> getAllQuizzes() {
        List<Quiz> quizzes = new ArrayList<>();
        if (currentUser == null) {
            System.err.println("No user logged in - cannot retrieve quizzes");
            return quizzes;
        }
        try {
            String sql = "SELECT * FROM quizzes WHERE user_id = ? ORDER BY created_at DESC";
            ResultSet rs = dbConnection.executeQuery(sql, currentUser.getId());
            
            while (rs.next()) {
                Quiz quiz = mapResultSetToQuiz(rs);
                quiz.setQuestions(getQuestionsForQuiz(quiz.getId()));
                quizzes.add(quiz);
            }
        } catch (SQLException e) {
            System.err.println("Get quizzes error: " + e.getMessage());
        }
        return quizzes;
    }
    
    public Quiz getQuiz(String id) {
        try {
            String sql = "SELECT * FROM quizzes WHERE id = ?";
            ResultSet rs = dbConnection.executeQuery(sql, id);
            
            if (rs.next()) {
                Quiz quiz = mapResultSetToQuiz(rs);
                quiz.setQuestions(getQuestionsForQuiz(id));
                return quiz;
            }
        } catch (SQLException e) {
            System.err.println("Get quiz error: " + e.getMessage());
        }
        return null;
    }
    
    public void saveQuiz(Quiz quiz) {
        try {
            String checkSql = "SELECT id FROM quizzes WHERE id = ?";
            ResultSet rs = dbConnection.executeQuery(checkSql, quiz.getId());
            
            if (rs.next()) {
                // Update existing quiz
                String updateSql = "UPDATE quizzes SET title = ?, description = ?, subject = ?, " +
                                 "difficulty = ?, time_limit = ?, last_taken = ?, best_score = ?, " +
                                 "times_taken = ? WHERE id = ?";
                dbConnection.executeUpdate(updateSql, quiz.getTitle(), quiz.getDescription(),
                                        quiz.getSubject(), quiz.getDifficulty().toString(),
                                        quiz.getTimeLimit(),
                                        quiz.getLastTaken() != null ? Timestamp.valueOf(quiz.getLastTaken()) : null,
                                        quiz.getBestScore(), quiz.getTimesTaken(), quiz.getId());
            } else {
                // Insert new quiz
                if (currentUser == null) {
                    System.err.println("No user logged in - cannot save quiz");
                    return;
                }
                String insertSql = "INSERT INTO quizzes (id, user_id, title, description, subject, difficulty, " +
                                 "time_limit, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                dbConnection.executeUpdate(insertSql, quiz.getId(), currentUser.getId(), quiz.getTitle(), quiz.getDescription(),
                                        quiz.getSubject(), quiz.getDifficulty().toString(),
                                        quiz.getTimeLimit(), Timestamp.valueOf(quiz.getCreatedAt()));
            }
            
            // Save questions
            for (Question question : quiz.getQuestions()) {
                saveQuestion(question, quiz.getId());
            }
            
        } catch (SQLException e) {
            System.err.println("Save quiz error: " + e.getMessage());
        }
    }
    
    public void deleteQuiz(String id) {
        if (currentUser == null) {
            System.err.println("No user logged in - cannot delete quiz");
            return;
        }
        try {
            String sql = "DELETE FROM quizzes WHERE id = ? AND user_id = ?";
            dbConnection.executeUpdate(sql, id, currentUser.getId());
        } catch (SQLException e) {
            System.err.println("Delete quiz error: " + e.getMessage());
        }
    }
    
    private List<Question> getQuestionsForQuiz(String quizId) {
        List<Question> questions = new ArrayList<>();
        try {
            String sql = "SELECT * FROM questions WHERE quiz_id = ?";
            ResultSet rs = dbConnection.executeQuery(sql, quizId);
            
            while (rs.next()) {
                questions.add(mapResultSetToQuestion(rs));
            }
        } catch (SQLException e) {
            System.err.println("Get questions error: " + e.getMessage());
        }
        return questions;
    }
    
    private void saveQuestion(Question question, String quizId) {
        try {
            String checkSql = "SELECT id FROM questions WHERE id = ?";
            ResultSet rs = dbConnection.executeQuery(checkSql, question.getId());
            
            if (rs.next()) {
                // Update existing question
                String updateSql = "UPDATE questions SET question_text = ?, options = ?, " +
                                 "correct_option_index = ?, explanation = ?, difficulty = ? WHERE id = ?";
                String optionsJson = objectMapper.writeValueAsString(question.getOptions());
                dbConnection.executeUpdate(updateSql, question.getQuestionText(), optionsJson,
                                        question.getCorrectOptionIndex(), question.getExplanation(),
                                        question.getDifficulty().toString(), question.getId());
            } else {
                // Insert new question
                String insertSql = "INSERT INTO questions (id, quiz_id, question_text, options, " +
                                 "correct_option_index, explanation, difficulty) VALUES (?, ?, ?, ?, ?, ?, ?)";
                String optionsJson = objectMapper.writeValueAsString(question.getOptions());
                dbConnection.executeUpdate(insertSql, question.getId(), quizId, question.getQuestionText(),
                                        optionsJson, question.getCorrectOptionIndex(), question.getExplanation(),
                                        question.getDifficulty().toString());
            }
        } catch (Exception e) {
            System.err.println("Save question error: " + e.getMessage());
        }
    }
    
    // Note methods
    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<>();
        if (currentUser == null) {
            System.err.println("No user logged in - cannot retrieve notes");
            return notes;
        }
        try {
            String sql = "SELECT * FROM notes WHERE user_id = ? ORDER BY created_at DESC";
            ResultSet rs = dbConnection.executeQuery(sql, currentUser.getId());
            
            while (rs.next()) {
                notes.add(mapResultSetToNote(rs));
            }
        } catch (SQLException e) {
            System.err.println("Get notes error: " + e.getMessage());
        }
        return notes;
    }
    
    public Note getNote(String id) {
        try {
            String sql = "SELECT * FROM notes WHERE id = ?";
            ResultSet rs = dbConnection.executeQuery(sql, id);
            
            if (rs.next()) {
                return mapResultSetToNote(rs);
            }
        } catch (SQLException e) {
            System.err.println("Get note error: " + e.getMessage());
        }
        return null;
    }
    
    public void saveNote(Note note) {
        try {
            String checkSql = "SELECT id FROM notes WHERE id = ?";
            ResultSet rs = dbConnection.executeQuery(checkSql, note.getId());
            
            if (rs.next()) {
                // Update existing note
                String updateSql = "UPDATE notes SET title = ?, content = ?, category = ?, " +
                                 "modified_at = ?, is_pinned = ? WHERE id = ?";
                dbConnection.executeUpdate(updateSql, note.getTitle(), note.getContent(),
                                        note.getCategory(), Timestamp.valueOf(LocalDateTime.now()),
                                        note.isPinned(), note.getId());
            } else {
                // Insert new note
                if (currentUser == null) {
                    System.err.println("No user logged in - cannot save note");
                    return;
                }
                String insertSql = "INSERT INTO notes (id, user_id, title, content, category, created_at, modified_at, is_pinned) " +
                                 "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                dbConnection.executeUpdate(insertSql, note.getId(), currentUser.getId(), note.getTitle(), note.getContent(),
                                        note.getCategory(), Timestamp.valueOf(note.getCreatedAt()),
                                        Timestamp.valueOf(note.getModifiedAt()), note.isPinned());
            }
        } catch (SQLException e) {
            System.err.println("Save note error: " + e.getMessage());
        }
    }
    
    public void deleteNote(String id) {
        if (currentUser == null) {
            System.err.println("No user logged in - cannot delete note");
            return;
        }
        try {
            String sql = "DELETE FROM notes WHERE id = ? AND user_id = ?";
            dbConnection.executeUpdate(sql, id, currentUser.getId());
        } catch (SQLException e) {
            System.err.println("Delete note error: " + e.getMessage());
        }
    }
    
    // Todo methods
    public List<TodoItem> getAllTodoItems() {
        List<TodoItem> todos = new ArrayList<>();
        if (currentUser == null) {
            System.err.println("No user logged in - cannot retrieve todo items");
            return todos;
        }
        try {
            String sql = "SELECT * FROM todo_items WHERE user_id = ? ORDER BY created_at DESC";
            ResultSet rs = dbConnection.executeQuery(sql, currentUser.getId());
            
            while (rs.next()) {
                todos.add(mapResultSetToTodoItem(rs));
            }
        } catch (SQLException e) {
            System.err.println("Get todo items error: " + e.getMessage());
        }
        return todos;
    }
    
    public TodoItem getTodoItem(String id) {
        try {
            String sql = "SELECT * FROM todo_items WHERE id = ?";
            ResultSet rs = dbConnection.executeQuery(sql, id);
            
            if (rs.next()) {
                return mapResultSetToTodoItem(rs);
            }
        } catch (SQLException e) {
            System.err.println("Get todo item error: " + e.getMessage());
        }
        return null;
    }
    
    public void saveTodoItem(TodoItem todoItem) {
        try {
            String checkSql = "SELECT id FROM todo_items WHERE id = ?";
            ResultSet rs = dbConnection.executeQuery(checkSql, todoItem.getId());
            
            if (rs.next()) {
                // Update existing todo
                String updateSql = "UPDATE todo_items SET title = ?, description = ?, category = ?, " +
                                 "is_completed = ?, priority = ?, completed_at = ?, due_date = ? WHERE id = ?";
                dbConnection.executeUpdate(updateSql, todoItem.getTitle(), todoItem.getDescription(),
                                        todoItem.getCategory(), todoItem.isCompleted(),
                                        todoItem.getPriority().toString(),
                                        todoItem.getCompletedAt() != null ? Timestamp.valueOf(todoItem.getCompletedAt()) : null,
                                        todoItem.getDueDate(), todoItem.getId());
            } else {
                // Insert new todo
                if (currentUser == null) {
                    System.err.println("No user logged in - cannot save todo item");
                    return;
                }
                String insertSql = "INSERT INTO todo_items (id, user_id, title, description, category, " +
                                 "is_completed, priority, created_at, due_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                dbConnection.executeUpdate(insertSql, todoItem.getId(), currentUser.getId(), todoItem.getTitle(),
                                        todoItem.getDescription(), todoItem.getCategory(),
                                        todoItem.isCompleted(), todoItem.getPriority().toString(),
                                        Timestamp.valueOf(todoItem.getCreatedAt()), todoItem.getDueDate());
            }
        } catch (SQLException e) {
            System.err.println("Save todo item error: " + e.getMessage());
        }
    }
    
    public void deleteTodoItem(String id) {
        if (currentUser == null) {
            System.err.println("No user logged in - cannot delete todo item");
            return;
        }
        try {
            String sql = "DELETE FROM todo_items WHERE id = ? AND user_id = ?";
            dbConnection.executeUpdate(sql, id, currentUser.getId());
        } catch (SQLException e) {
            System.err.println("Delete todo item error: " + e.getMessage());
        }
    }
    
    // Activity methods
    public void logUserActivity(String activityType, String description) {
        if (currentUser != null) {
            try {
                String id = "activity_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
                String sql = "INSERT INTO activities (id, user_id, activity_type, description, timestamp) " +
                           "VALUES (?, ?, ?, ?, ?)";
                dbConnection.executeUpdate(sql, id, currentUser.getId(), activityType, description,
                                        Timestamp.valueOf(LocalDateTime.now()));
                System.out.println("Activity logged: " + description);
                
                // Update streak when user completes activities
                currentUser.updateStreakOnActivity();
                updateUser(currentUser); // Save the updated streak
                
            } catch (SQLException e) {
                System.err.println("Log activity error: " + e.getMessage());
            }
        }
    }
    
    public List<Activity> getAllActivitiesForUser(String userId) {
        List<Activity> activities = new ArrayList<>();
        try {
            String sql = "SELECT * FROM activities WHERE user_id = ? ORDER BY timestamp DESC";
            ResultSet rs = dbConnection.executeQuery(sql, userId);
            
            int count = 0;
            while (rs.next()) {
                try {
                    Activity activity = mapResultSetToActivity(rs);
                    activities.add(activity);
                    count++;
                } catch (Exception e) {
                    System.err.println("Error mapping activity for user " + userId + ": " + e.getMessage());
                    // Continue processing other activities even if one fails
                }
            }
            System.out.println("Retrieved " + count + " activities for user: " + userId);
        } catch (SQLException e) {
            System.err.println("Get activities error for user " + userId + ": " + e.getMessage());
            e.printStackTrace();
        }
        return activities;
    }
    
    /**
     * Debug method to check what activity types exist in the database for a user
     */
    public void debugUserActivities(String userId) {
        try {
            String sql = "SELECT activity_type, COUNT(*) as count FROM activities WHERE user_id = ? GROUP BY activity_type ORDER BY count DESC";
            ResultSet rs = dbConnection.executeQuery(sql, userId);
            
            System.out.println("=== Activity Types for User " + userId + " ===");
            while (rs.next()) {
                String activityType = rs.getString("activity_type");
                int count = rs.getInt("count");
                System.out.println(activityType + ": " + count + " activities");
            }
            System.out.println("=== End Activity Types ===");
        } catch (SQLException e) {
            System.err.println("Debug activities error: " + e.getMessage());
        }
    }
    
    // Statistics methods
    public int getTotalFlashcards() {
        try {
            String sql = "SELECT COUNT(*) as total FROM flashcards";
            ResultSet rs = dbConnection.executeQuery(sql);
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Get total flashcards error: " + e.getMessage());
        }
        return 0;
    }
    
    public int getTotalQuizzes() {
        try {
            String sql = "SELECT COUNT(*) as total FROM quizzes";
            ResultSet rs = dbConnection.executeQuery(sql);
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Get total quizzes error: " + e.getMessage());
        }
        return 0;
    }
    
    public int getTotalNotes() {
        try {
            String sql = "SELECT COUNT(*) as total FROM notes";
            ResultSet rs = dbConnection.executeQuery(sql);
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Get total notes error: " + e.getMessage());
        }
        return 0;
    }
    
    public int getTotalTodoItems() {
        try {
            String sql = "SELECT COUNT(*) as total FROM todo_items";
            ResultSet rs = dbConnection.executeQuery(sql);
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Get total todo items error: " + e.getMessage());
        }
        return 0;
    }
    
    // Mapping methods
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getString("id"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        if (rs.getTimestamp("last_login_at") != null) {
            user.setLastLoginAt(rs.getTimestamp("last_login_at").toLocalDateTime());
        }
        user.setFlashcardsStudied(rs.getInt("flashcards_studied"));
        user.setQuizzesTaken(rs.getInt("quizzes_taken"));
        user.setCurrentStreak(rs.getInt("current_streak"));
        user.setTotalStudyHours(rs.getInt("total_study_hours"));
        return user;
    }
    
    private FlashcardDeck mapResultSetToFlashcardDeck(ResultSet rs) throws SQLException {
        FlashcardDeck deck = new FlashcardDeck();
        deck.setId(rs.getString("id"));
        deck.setTitle(rs.getString("title"));
        deck.setDescription(rs.getString("description"));
        deck.setSubject(rs.getString("subject"));
        deck.setDifficulty(Flashcard.Difficulty.valueOf(rs.getString("difficulty")));
        deck.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        if (rs.getTimestamp("last_studied") != null) {
            deck.setLastStudied(rs.getTimestamp("last_studied").toLocalDateTime());
        }
        deck.setTotalStudySessions(rs.getInt("total_study_sessions"));
        return deck;
    }
    
    private Flashcard mapResultSetToFlashcard(ResultSet rs) throws SQLException {
        Flashcard card = new Flashcard();
        card.setId(rs.getString("id"));
        card.setQuestion(rs.getString("question"));
        card.setAnswer(rs.getString("answer"));
        card.setDifficulty(Flashcard.Difficulty.valueOf(rs.getString("difficulty")));
        card.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        if (rs.getTimestamp("last_studied") != null) {
            card.setLastStudied(rs.getTimestamp("last_studied").toLocalDateTime());
        }
        card.setTimesStudied(rs.getInt("times_studied"));
        card.setCorrect(rs.getBoolean("is_correct"));
        return card;
    }
    
    private Quiz mapResultSetToQuiz(ResultSet rs) throws SQLException {
        Quiz quiz = new Quiz();
        quiz.setId(rs.getString("id"));
        quiz.setTitle(rs.getString("title"));
        quiz.setDescription(rs.getString("description"));
        quiz.setSubject(rs.getString("subject"));
        quiz.setDifficulty(Flashcard.Difficulty.valueOf(rs.getString("difficulty")));
        quiz.setTimeLimit(rs.getInt("time_limit"));
        quiz.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        if (rs.getTimestamp("last_taken") != null) {
            quiz.setLastTaken(rs.getTimestamp("last_taken").toLocalDateTime());
        }
        quiz.setBestScore(rs.getInt("best_score"));
        quiz.setTimesTaken(rs.getInt("times_taken"));
        return quiz;
    }
    
    private Question mapResultSetToQuestion(ResultSet rs) throws SQLException {
        Question question = new Question();
        question.setId(rs.getString("id"));
        question.setQuestionText(rs.getString("question_text"));
        question.setCorrectOptionIndex(rs.getInt("correct_option_index"));
        question.setExplanation(rs.getString("explanation"));
        question.setDifficulty(Flashcard.Difficulty.valueOf(rs.getString("difficulty")));
        
        // Parse JSON options
        try {
            String optionsJson = rs.getString("options");
            if (optionsJson != null) {
                List<String> options = objectMapper.readValue(optionsJson, new TypeReference<List<String>>() {});
                question.setOptions(options);
            }
        } catch (Exception e) {
            System.err.println("Error parsing question options: " + e.getMessage());
        }
        
        return question;
    }
    
    private Note mapResultSetToNote(ResultSet rs) throws SQLException {
        Note note = new Note();
        note.setId(rs.getString("id"));
        note.setTitle(rs.getString("title"));
        note.setContent(rs.getString("content"));
        note.setCategory(rs.getString("category"));
        note.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        note.setModifiedAt(rs.getTimestamp("modified_at").toLocalDateTime());
        note.setPinned(rs.getBoolean("is_pinned"));
        return note;
    }
    
    private TodoItem mapResultSetToTodoItem(ResultSet rs) throws SQLException {
        TodoItem todo = new TodoItem();
        todo.setId(rs.getString("id"));
        todo.setTitle(rs.getString("title"));
        todo.setDescription(rs.getString("description"));
        todo.setCategory(rs.getString("category"));
        todo.setCompleted(rs.getBoolean("is_completed"));
        todo.setPriority(TodoItem.Priority.valueOf(rs.getString("priority")));
        todo.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        if (rs.getTimestamp("completed_at") != null) {
            todo.setCompletedAt(rs.getTimestamp("completed_at").toLocalDateTime());
        }
        if (rs.getDate("due_date") != null) {
            todo.setDueDate(rs.getDate("due_date").toLocalDate());
        }
        return todo;
    }
    
    private Activity mapResultSetToActivity(ResultSet rs) throws SQLException {
        Activity activity = new Activity();
        activity.setId(rs.getString("id"));
        activity.setUserId(rs.getString("user_id"));
        
        // Safely convert activity type string to enum
        String activityTypeStr = rs.getString("activity_type");
        try {
            activity.setType(ActivityType.valueOf(activityTypeStr));
        } catch (IllegalArgumentException e) {
            System.err.println("Unknown activity type: " + activityTypeStr + ", using UNKNOWN");
            activity.setType(ActivityType.UNKNOWN);
        }
        
        activity.setDescription(rs.getString("description"));
        activity.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
        return activity;
    }
}
