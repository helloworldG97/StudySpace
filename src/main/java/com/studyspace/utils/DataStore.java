package com.studyspace.utils;

import com.studyspace.models.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Comparator;

//============ data store =============
//this is where all application data is managed and stored
//Now uses MySQL database for persistence

public class DataStore {
    private static DataStore instance;
    private MySQLDataStore mysqlDataStore;
    private InMemoryDataStore inMemoryDataStore;
    private DatabaseConnection dbConnection;
    
    private DataStore() {
        this.dbConnection = DatabaseConnection.getInstance();
        this.mysqlDataStore = MySQLDataStore.getInstance();
        this.inMemoryDataStore = InMemoryDataStore.getInstance();
    }
    
    public static DataStore getInstance() {
        if (instance == null) {
            System.out.println("Creating new DataStore instance with MySQL backend");
            instance = new DataStore();
        } else {
            System.out.println("Using existing DataStore instance");
        }
        return instance;
    }
    
    private boolean isDatabaseAvailable() {
        return dbConnection.isDatabaseAvailable();
    }
    
    // Authentication methods
    public boolean authenticateUser(String email, String password) {
        if (isDatabaseAvailable()) {
            return mysqlDataStore.authenticateUser(email, password);
        } else {
            System.out.println("Database not available, using in-memory store for authentication");
            return inMemoryDataStore.authenticateUser(email, password);
        }
    }
    
    public User registerUser(String fullName, String email, String password) {
        if (isDatabaseAvailable()) {
            return mysqlDataStore.registerUser(fullName, email, password);
        } else {
            System.out.println("Database not available, using in-memory store for registration");
            return inMemoryDataStore.registerUser(fullName, email, password);
        }
    }
    
    public void logout() {
        if (isDatabaseAvailable()) {
            mysqlDataStore.logout();
        } else {
            inMemoryDataStore.logout();
        }
    }
    
    public User getCurrentUser() {
        if (isDatabaseAvailable()) {
            return mysqlDataStore.getCurrentUser();
        } else {
            return inMemoryDataStore.getCurrentUser();
        }
    }
    
    public void setCurrentUser(User user) {
        if (isDatabaseAvailable()) {
            mysqlDataStore.setCurrentUser(user);
        } else {
            inMemoryDataStore.setCurrentUser(user);
        }
    }
    
    public void updateUser(User user) {
        if (isDatabaseAvailable()) {
            mysqlDataStore.updateUser(user);
        } else {
            inMemoryDataStore.updateUser(user);
        }
    }
    
    public boolean isEmailTaken(String email) {
        if (isDatabaseAvailable()) {
            return mysqlDataStore.isEmailTaken(email);
        } else {
            return inMemoryDataStore.isEmailTaken(email);
        }
    }
    
    // Data access methods
    public List<FlashcardDeck> getAllFlashcardDecks() {
        if (isDatabaseAvailable()) {
            return mysqlDataStore.getAllFlashcardDecks();
        } else {
            return inMemoryDataStore.getAllFlashcardDecks();
        }
    }
    
    public FlashcardDeck getFlashcardDeck(String id) {
        if (isDatabaseAvailable()) {
            return mysqlDataStore.getFlashcardDeck(id);
        } else {
            return inMemoryDataStore.getFlashcardDeck(id);
        }
    }
    
    public void saveFlashcardDeck(FlashcardDeck deck) {
        if (isDatabaseAvailable()) {
            mysqlDataStore.saveFlashcardDeck(deck);
        } else {
            inMemoryDataStore.saveFlashcardDeck(deck);
        }
    }
    
    public void deleteFlashcardDeck(String id) {
        if (isDatabaseAvailable()) {
            mysqlDataStore.deleteFlashcardDeck(id);
        } else {
            inMemoryDataStore.deleteFlashcardDeck(id);
        }
    }
    
    public List<Quiz> getAllQuizzes() {
        if (isDatabaseAvailable()) {
            return mysqlDataStore.getAllQuizzes();
        } else {
            return inMemoryDataStore.getAllQuizzes();
        }
    }
    
    public Quiz getQuiz(String id) {
        if (isDatabaseAvailable()) {
            return mysqlDataStore.getQuiz(id);
        } else {
            return inMemoryDataStore.getQuiz(id);
        }
    }
    
    public void saveQuiz(Quiz quiz) {
        if (isDatabaseAvailable()) {
            mysqlDataStore.saveQuiz(quiz);
        } else {
            inMemoryDataStore.saveQuiz(quiz);
        }
    }
    
    public void deleteQuiz(String id) {
        if (isDatabaseAvailable()) {
            mysqlDataStore.deleteQuiz(id);
        } else {
            inMemoryDataStore.deleteQuiz(id);
        }
    }
    
    
    public List<Note> getAllNotes() {
        if (isDatabaseAvailable()) {
            return mysqlDataStore.getAllNotes();
        } else {
            return inMemoryDataStore.getAllNotes();
        }
    }
    
    public Note getNote(String id) {
        if (isDatabaseAvailable()) {
            return mysqlDataStore.getNote(id);
        } else {
            return inMemoryDataStore.getNote(id);
        }
    }
    
    public void saveNote(Note note) {
        if (isDatabaseAvailable()) {
            mysqlDataStore.saveNote(note);
        } else {
            inMemoryDataStore.saveNote(note);
        }
    }
    
    public void deleteNote(String id) {
        if (isDatabaseAvailable()) {
            mysqlDataStore.deleteNote(id);
        } else {
            inMemoryDataStore.deleteNote(id);
        }
    }
    
    public void addNote(Note note) {
        saveNote(note);
    }
    
    public void updateNote(Note note) {
        saveNote(note);
    }
    
    public List<Note> getNotes() {
        return getAllNotes();
    }
    
    public List<TodoItem> getTodoItems() {
        return getAllTodoItems();
    }
    
    public List<TodoItem> getAllTodoItems() {
        if (isDatabaseAvailable()) {
            return mysqlDataStore.getAllTodoItems();
        } else {
            return inMemoryDataStore.getAllTodoItems();
        }
    }
    
    public List<TodoItem> getActiveTodoItems() {
        return getAllTodoItems().stream()
                .filter(todo -> !todo.isCompleted())
                .collect(Collectors.toList());
    }
    
    public List<TodoItem> getCompletedTodoItems() {
        return getAllTodoItems().stream()
                .filter(TodoItem::isCompleted)
                .collect(Collectors.toList());
    }
    
    public TodoItem getTodoItem(String id) {
        if (isDatabaseAvailable()) {
            return mysqlDataStore.getTodoItem(id);
        } else {
            return inMemoryDataStore.getTodoItem(id);
        }
    }
    
    public void saveTodoItem(TodoItem todoItem) {
        if (isDatabaseAvailable()) {
            mysqlDataStore.saveTodoItem(todoItem);
        } else {
            inMemoryDataStore.saveTodoItem(todoItem);
        }
    }
    
    public void deleteTodoItem(String id) {
        if (isDatabaseAvailable()) {
            mysqlDataStore.deleteTodoItem(id);
        } else {
            inMemoryDataStore.deleteTodoItem(id);
        }
    }
    
    // Statistics methods
    public int getTotalFlashcards() {
        if (isDatabaseAvailable()) {
            return mysqlDataStore.getTotalFlashcards();
        } else {
            return inMemoryDataStore.getTotalFlashcards();
        }
    }
    
    public int getTotalQuizzes() {
        if (isDatabaseAvailable()) {
            return mysqlDataStore.getTotalQuizzes();
        } else {
            return inMemoryDataStore.getTotalQuizzes();
        }
    }
    
    public int getTotalNotes() {
        if (isDatabaseAvailable()) {
            return mysqlDataStore.getTotalNotes();
        } else {
            return inMemoryDataStore.getTotalNotes();
        }
    }
    
    public int getTotalTodoItems() {
        if (isDatabaseAvailable()) {
            return mysqlDataStore.getTotalTodoItems();
        } else {
            return inMemoryDataStore.getTotalTodoItems();
        }
    }
    
    public int getActiveTodoCount() {
        return (int) getAllTodoItems().stream()
                .filter(todo -> !todo.isCompleted())
                .count();
    }
    
    public int getCompletedTodoCount() {
        return (int) getAllTodoItems().stream()
                .filter(TodoItem::isCompleted)
                .count();
    }
    
    // Activity methods
    public void logUserActivity(String activityType, String description) {
        if (isDatabaseAvailable()) {
            mysqlDataStore.logUserActivity(activityType, description);
        } else {
            inMemoryDataStore.logUserActivity(activityType, description);
        }
    }
    
    // User management methods
    public void deleteUser(String userId) {
        if (isDatabaseAvailable()) {
            mysqlDataStore.deleteUser(userId);
        }
        // In offline mode, we don't delete the demo user
    }
    
    public void updateUserPassword(String userId, String newPassword) {
        if (isDatabaseAvailable()) {
            mysqlDataStore.updateUserPassword(userId, newPassword);
        }
        // In offline mode, password changes are not persisted
    }
    
    public void logActivity(Activity activity) {
        if (activity != null) {
            logUserActivity(activity.getType().toString(), activity.getDescription());
        }
    }
    
    public List<Activity> getActivitiesForUser(String userId, LocalDate date) {
        return getAllActivitiesForUser(userId).stream()
                .filter(activity -> activity.getTimestamp().toLocalDate().equals(date))
                .sorted(Comparator.comparing(Activity::getTimestamp).reversed())
                .collect(Collectors.toList());
    }
    
    public List<Activity> getAllActivitiesForUser(String userId) {
        if (isDatabaseAvailable()) {
            return mysqlDataStore.getAllActivitiesForUser(userId);
        } else {
            return inMemoryDataStore.getAllActivitiesForUser(userId);
        }
    }
    
    public void debugUserActivities(String userId) {
        if (isDatabaseAvailable()) {
            mysqlDataStore.debugUserActivities(userId);
        } else {
            inMemoryDataStore.debugUserActivities(userId);
        }
    }
    
    public Map<String, Activity> getActivities() {
        // Return empty map for compatibility
        return new HashMap<>();
    }
}