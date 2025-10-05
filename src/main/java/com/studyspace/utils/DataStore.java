package com.studyspace.utils;

import com.studyspace.models.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Comparator;

//============ data store =============
//this is where all application data is managed and stored

public class DataStore {
    private static DataStore instance;
    
    // Data storage
    private Map<String, User> users;
    private Map<String, FlashcardDeck> flashcardDecks;
    private Map<String, Quiz> quizzes;
    private Map<String, CodeProblem> codeProblems;
    private Map<String, Note> notes;
    private Map<String, TodoItem> todoItems;
    private Map<String, Activity> activities;
    
    // Current session
    private User currentUser;
    
    // Flag to prevent data re-initialization
    private boolean dataInitialized = false;
    
    private DataStore() {
        initializeData();
    }
    
    public static DataStore getInstance() {
        if (instance == null) {
            System.out.println("Creating new DataStore instance");
            instance = new DataStore();
        } else {
            System.out.println("Using existing DataStore instance");
        }
        return instance;
    }
    
    private void initializeData() {
        // Only initialize if not already done
        if (dataInitialized) {
            return;
        }
        
        users = new HashMap<>();
        flashcardDecks = new HashMap<>();
        quizzes = new HashMap<>();
        codeProblems = new HashMap<>();
        notes = new HashMap<>();
        todoItems = new HashMap<>();
        activities = new HashMap<>();
        
        // No sample data - start with empty collections
        dataInitialized = true;
    }
    
    // Authentication methods
    public boolean authenticateUser(String email, String password) {
        System.out.println("Authenticating user: " + email);
        System.out.println("Total users in store: " + users.size());
        System.out.println("User keys: " + users.keySet());
        
        User user = users.get(email.toLowerCase());
        if (user != null && user.getPassword().equals(password)) {
            currentUser = user;
            user.updateLastLogin();
            System.out.println("Authentication successful for: " + user.getFullName());
            return true;
        }
        System.out.println("Authentication failed for: " + email);
        return false;
    }
    
    public User registerUser(String fullName, String email, String password) {
        System.out.println("Registering user: " + email);
        System.out.println("Total users before registration: " + users.size());
        
        if (users.containsKey(email.toLowerCase())) {
            System.out.println("User already exists: " + email);
            return null; // User already exists
        }
        
        User newUser = new User(fullName, email.toLowerCase(), password);
        users.put(newUser.getEmail(), newUser);
        currentUser = newUser;
        System.out.println("User registered successfully: " + newUser.getFullName());
        System.out.println("Total users after registration: " + users.size());
        return newUser;
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
    
    /**
     * Updates an existing user in the store
     */
    public void updateUser(User user) {
        if (user != null) {
            users.put(user.getEmail(), user);
            System.out.println("User updated: " + user.getFullName());
        }
    }
    
    public boolean isEmailTaken(String email) {
        return users.containsKey(email.toLowerCase());
    }
    
    // Data access methods
    public List<FlashcardDeck> getAllFlashcardDecks() {
        return new ArrayList<>(flashcardDecks.values());
    }
    
    public FlashcardDeck getFlashcardDeck(String id) {
        return flashcardDecks.get(id);
    }
    
    public void saveFlashcardDeck(FlashcardDeck deck) {
        flashcardDecks.put(deck.getId(), deck);
    }
    
    public void deleteFlashcardDeck(String id) {
        flashcardDecks.remove(id);
    }
    
    public List<Quiz> getAllQuizzes() {
        return new ArrayList<>(quizzes.values());
    }
    
    public Quiz getQuiz(String id) {
        return quizzes.get(id);
    }
    
    public void saveQuiz(Quiz quiz) {
        quizzes.put(quiz.getId(), quiz);
    }
    
    public void deleteQuiz(String id) {
        quizzes.remove(id);
    }
    
    public List<CodeProblem> getAllCodeProblems() {
        return new ArrayList<>(codeProblems.values());
    }
    
    public CodeProblem getCodeProblem(String id) {
        return codeProblems.get(id);
    }
    
    public void saveCodeProblem(CodeProblem problem) {
        codeProblems.put(problem.getId(), problem);
    }
    
    public void deleteCodeProblem(String id) {
        codeProblems.remove(id);
    }
    
    public List<Note> getAllNotes() {
        return new ArrayList<>(notes.values());
    }
    
    public Note getNote(String id) {
        return notes.get(id);
    }
    
    public void saveNote(Note note) {
        notes.put(note.getId(), note);
    }
    
    public void deleteNote(String id) {
        notes.remove(id);
    }
    
    public void addNote(Note note) {
        notes.put(note.getId(), note);
    }
    
    public void updateNote(Note note) {
        notes.put(note.getId(), note);
    }
    
    public List<Note> getNotes() {
        return new ArrayList<>(notes.values());
    }
    
    public List<TodoItem> getTodoItems() {
        return new ArrayList<>(todoItems.values());
    }
    
    public List<TodoItem> getAllTodoItems() {
        return new ArrayList<>(todoItems.values());
    }
    
    public List<TodoItem> getActiveTodoItems() {
        return todoItems.values().stream()
                .filter(todo -> !todo.isCompleted())
                .collect(Collectors.toList());
    }
    
    public List<TodoItem> getCompletedTodoItems() {
        return todoItems.values().stream()
                .filter(TodoItem::isCompleted)
                .collect(Collectors.toList());
    }
    
    public TodoItem getTodoItem(String id) {
        return todoItems.get(id);
    }
    
    public void saveTodoItem(TodoItem todoItem) {
        todoItems.put(todoItem.getId(), todoItem);
    }
    
    public void deleteTodoItem(String id) {
        todoItems.remove(id);
    }
    
    // Statistics methods
    public int getTotalFlashcards() {
        return flashcardDecks.values().stream()
                .mapToInt(FlashcardDeck::getCardCount)
                .sum();
    }
    
    public int getTotalQuizzes() {
        return quizzes.size();
    }
    
    public int getTotalCodeProblems() {
        return codeProblems.size();
    }
    
    public int getTotalNotes() {
        return notes.size();
    }
    
    public int getTotalTodoItems() {
        return todoItems.size();
    }
    
    public int getActiveTodoCount() {
        return (int) todoItems.values().stream()
                .filter(todo -> !todo.isCompleted())
                .count();
    }
    
    public int getCompletedTodoCount() {
        return (int) todoItems.values().stream()
                .filter(TodoItem::isCompleted)
                .count();
    }
    
    // Activity methods
    public void logUserActivity(String activityType, String description) {
        if (currentUser != null) {
            ActivityType type = ActivityType.valueOf(activityType);
            Activity activity = new Activity(currentUser.getId(), type, description);
            activities.put(activity.getId(), activity);
            System.out.println("Activity logged: " + description);
        }
    }
    
    public void logActivity(Activity activity) {
        if (activity != null) {
        activities.put(activity.getId(), activity);
        System.out.println("Activity logged: " + activity.getDescription());
        }
    }
    
    public List<Activity> getActivitiesForUser(String userId, LocalDate date) {
        return activities.values().stream()
                .filter(activity -> activity.getUserId().equals(userId) &&
                                     activity.getTimestamp().toLocalDate().equals(date))
                .sorted(Comparator.comparing(Activity::getTimestamp).reversed())
                .collect(Collectors.toList());
    }
    
    public List<Activity> getAllActivitiesForUser(String userId) {
        return activities.values().stream()
                .filter(activity -> activity.getUserId().equals(userId))
                .sorted(Comparator.comparing(Activity::getTimestamp).reversed())
                .collect(Collectors.toList());
    }
    
    public Map<String, Activity> getActivities() {
        return activities;
    }
}