package com.studyspace.utils;

import com.studyspace.models.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Comparator;

//============ data store =============
//this is where all application data is managed and stored
//Now uses MySQL database for persistence

public class DataStore {
    private static DataStore instance;
    private MySQLDataStore mysqlDataStore;
    
    private DataStore() {
        this.mysqlDataStore = MySQLDataStore.getInstance();
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
    
    // Authentication methods
    public boolean authenticateUser(String email, String password) {
        return mysqlDataStore.authenticateUser(email, password);
    }
    
    public User registerUser(String fullName, String email, String password) {
        return mysqlDataStore.registerUser(fullName, email, password);
    }
    
    public void logout() {
        mysqlDataStore.logout();
    }
    
    public User getCurrentUser() {
        return mysqlDataStore.getCurrentUser();
    }
    
    public void setCurrentUser(User user) {
        mysqlDataStore.setCurrentUser(user);
    }
    
    public void updateUser(User user) {
        mysqlDataStore.updateUser(user);
    }
    
    public boolean isEmailTaken(String email) {
        return mysqlDataStore.isEmailTaken(email);
    }
    
    // Data access methods
    public List<FlashcardDeck> getAllFlashcardDecks() {
        return mysqlDataStore.getAllFlashcardDecks();
    }
    
    public FlashcardDeck getFlashcardDeck(String id) {
        return mysqlDataStore.getFlashcardDeck(id);
    }
    
    public void saveFlashcardDeck(FlashcardDeck deck) {
        mysqlDataStore.saveFlashcardDeck(deck);
    }
    
    public void deleteFlashcardDeck(String id) {
        mysqlDataStore.deleteFlashcardDeck(id);
    }
    
    public List<Quiz> getAllQuizzes() {
        return mysqlDataStore.getAllQuizzes();
    }
    
    public Quiz getQuiz(String id) {
        return mysqlDataStore.getQuiz(id);
    }
    
    public void saveQuiz(Quiz quiz) {
        mysqlDataStore.saveQuiz(quiz);
    }
    
    public void deleteQuiz(String id) {
        mysqlDataStore.deleteQuiz(id);
    }
    
    public List<CodeProblem> getAllCodeProblems() {
        // Code problems not implemented in MySQL yet
        return new ArrayList<>();
    }
    
    public CodeProblem getCodeProblem(String id) {
        return null;
    }
    
    public void saveCodeProblem(CodeProblem problem) {
        // Not implemented yet
    }
    
    public void deleteCodeProblem(String id) {
        // Not implemented yet
    }
    
    public List<Note> getAllNotes() {
        return mysqlDataStore.getAllNotes();
    }
    
    public Note getNote(String id) {
        return mysqlDataStore.getNote(id);
    }
    
    public void saveNote(Note note) {
        mysqlDataStore.saveNote(note);
    }
    
    public void deleteNote(String id) {
        mysqlDataStore.deleteNote(id);
    }
    
    public void addNote(Note note) {
        mysqlDataStore.saveNote(note);
    }
    
    public void updateNote(Note note) {
        mysqlDataStore.saveNote(note);
    }
    
    public List<Note> getNotes() {
        return mysqlDataStore.getAllNotes();
    }
    
    public List<TodoItem> getTodoItems() {
        return mysqlDataStore.getAllTodoItems();
    }
    
    public List<TodoItem> getAllTodoItems() {
        return mysqlDataStore.getAllTodoItems();
    }
    
    public List<TodoItem> getActiveTodoItems() {
        return mysqlDataStore.getAllTodoItems().stream()
                .filter(todo -> !todo.isCompleted())
                .collect(Collectors.toList());
    }
    
    public List<TodoItem> getCompletedTodoItems() {
        return mysqlDataStore.getAllTodoItems().stream()
                .filter(TodoItem::isCompleted)
                .collect(Collectors.toList());
    }
    
    public TodoItem getTodoItem(String id) {
        return mysqlDataStore.getTodoItem(id);
    }
    
    public void saveTodoItem(TodoItem todoItem) {
        mysqlDataStore.saveTodoItem(todoItem);
    }
    
    public void deleteTodoItem(String id) {
        mysqlDataStore.deleteTodoItem(id);
    }
    
    // Statistics methods
    public int getTotalFlashcards() {
        return mysqlDataStore.getTotalFlashcards();
    }
    
    public int getTotalQuizzes() {
        return mysqlDataStore.getTotalQuizzes();
    }
    
    public int getTotalCodeProblems() {
        return 0; // Not implemented yet
    }
    
    public int getTotalNotes() {
        return mysqlDataStore.getTotalNotes();
    }
    
    public int getTotalTodoItems() {
        return mysqlDataStore.getTotalTodoItems();
    }
    
    public int getActiveTodoCount() {
        return (int) mysqlDataStore.getAllTodoItems().stream()
                .filter(todo -> !todo.isCompleted())
                .count();
    }
    
    public int getCompletedTodoCount() {
        return (int) mysqlDataStore.getAllTodoItems().stream()
                .filter(TodoItem::isCompleted)
                .count();
    }
    
    // Activity methods
    public void logUserActivity(String activityType, String description) {
        mysqlDataStore.logUserActivity(activityType, description);
    }
    
    public void logActivity(Activity activity) {
        if (activity != null) {
            mysqlDataStore.logUserActivity(activity.getType().toString(), activity.getDescription());
        }
    }
    
    public List<Activity> getActivitiesForUser(String userId, LocalDate date) {
        return mysqlDataStore.getAllActivitiesForUser(userId).stream()
                .filter(activity -> activity.getTimestamp().toLocalDate().equals(date))
                .sorted(Comparator.comparing(Activity::getTimestamp).reversed())
                .collect(Collectors.toList());
    }
    
    public List<Activity> getAllActivitiesForUser(String userId) {
        return mysqlDataStore.getAllActivitiesForUser(userId);
    }
    
    public Map<String, Activity> getActivities() {
        // Return empty map for compatibility
        return new HashMap<>();
    }
}