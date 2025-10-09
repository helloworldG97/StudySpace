package com.studyspace.utils;

import com.studyspace.models.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory data store for offline mode when database is not available
 */
public class InMemoryDataStore {
    private static InMemoryDataStore instance;
    private Map<String, User> users;
    private Map<String, FlashcardDeck> flashcardDecks;
    private Map<String, Quiz> quizzes;
    private Map<String, Note> notes;
    private Map<String, TodoItem> todoItems;
    private Map<String, Activity> activities;
    
    private User currentUser;
    
    private InMemoryDataStore() {
        this.users = new ConcurrentHashMap<>();
        this.flashcardDecks = new ConcurrentHashMap<>();
        this.quizzes = new ConcurrentHashMap<>();
        this.notes = new ConcurrentHashMap<>();
        this.todoItems = new ConcurrentHashMap<>();
        this.activities = new ConcurrentHashMap<>();
        
        // Create a demo user for offline mode
        createDemoUser();
    }
    
    public static InMemoryDataStore getInstance() {
        if (instance == null) {
            instance = new InMemoryDataStore();
        }
        return instance;
    }
    
    private void createDemoUser() {
        User demoUser = new User("Demo User", "demo@studyspace.com", "demo123");
        demoUser.setId("demo_user");
        demoUser.setCreatedAt(LocalDateTime.now());
        users.put("demo_user", demoUser);
        currentUser = demoUser;
        
        // Create some demo data
        createDemoData();
    }
    
    private void createDemoData() {
        // Demo flashcard deck
        FlashcardDeck demoDeck = new FlashcardDeck();
        demoDeck.setId("demo_deck_1");
        demoDeck.setTitle("Java Basics");
        demoDeck.setDescription("Basic Java programming concepts");
        demoDeck.setSubject("Programming");
        demoDeck.setDifficulty(Flashcard.Difficulty.EASY);
        demoDeck.setCreatedAt(LocalDateTime.now());
        
        // Add some demo flashcards
        Flashcard card1 = new Flashcard();
        card1.setId("card_1");
        card1.setQuestion("What is Java?");
        card1.setAnswer("Java is a programming language");
        card1.setDifficulty(Flashcard.Difficulty.EASY);
        card1.setCreatedAt(LocalDateTime.now());
        demoDeck.addFlashcard(card1);
        
        Flashcard card2 = new Flashcard();
        card2.setId("card_2");
        card2.setQuestion("What is a class in Java?");
        card2.setAnswer("A class is a blueprint for creating objects");
        card2.setDifficulty(Flashcard.Difficulty.MEDIUM);
        card2.setCreatedAt(LocalDateTime.now());
        demoDeck.addFlashcard(card2);
        
        flashcardDecks.put("demo_deck_1", demoDeck);
        
        // Demo note
        Note demoNote = new Note();
        demoNote.setId("note_1");
        demoNote.setTitle("Study Tips");
        demoNote.setContent("1. Review regularly\n2. Practice with flashcards\n3. Take breaks");
        demoNote.setCategory("General");
        demoNote.setCreatedAt(LocalDateTime.now());
        demoNote.setModifiedAt(LocalDateTime.now());
        notes.put("note_1", demoNote);
        
        // Demo todo item
        TodoItem demoTodo = new TodoItem();
        demoTodo.setId("todo_1");
        demoTodo.setTitle("Complete Java study session");
        demoTodo.setDescription("Study Java basics for 30 minutes");
        demoTodo.setCategory("Study");
        demoTodo.setPriority(TodoItem.Priority.HIGH);
        demoTodo.setCreatedAt(LocalDateTime.now());
        todoItems.put("todo_1", demoTodo);
    }
    
    // Authentication methods
    public boolean authenticateUser(String email, String password) {
        if ("demo@studyspace.com".equals(email) && "demo123".equals(password)) {
            currentUser = users.get("demo_user");
            return true;
        }
        return false;
    }
    
    public User registerUser(String fullName, String email, String password) {
        // In offline mode, just return the demo user
        if (currentUser == null) {
            currentUser = users.get("demo_user");
        }
        return currentUser;
    }
    
    public void logout() {
        // Keep demo user logged in for offline mode
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    
    public void updateUser(User user) {
        if (currentUser != null) {
            currentUser.setFullName(user.getFullName());
            currentUser.setFlashcardsStudied(user.getFlashcardsStudied());
            currentUser.setQuizzesTaken(user.getQuizzesTaken());
            currentUser.setCurrentStreak(user.getCurrentStreak());
            currentUser.setTotalStudyHours(user.getTotalStudyHours());
            currentUser.setLastLoginAt(user.getLastLoginAt());
            
            System.out.println("User updated successfully (offline): " + currentUser.getFullName() + 
                             " (Streak: " + currentUser.getCurrentStreak() + " days)");
        }
    }
    
    public boolean isEmailTaken(String email) {
        return "demo@studyspace.com".equals(email);
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
    
    public List<TodoItem> getAllTodoItems() {
        return new ArrayList<>(todoItems.values());
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
                .mapToInt(deck -> deck.getFlashcards().size())
                .sum();
    }
    
    public int getTotalQuizzes() {
        return quizzes.size();
    }
    
    public int getTotalNotes() {
        return notes.size();
    }
    
    public int getTotalTodoItems() {
        return todoItems.size();
    }
    
    // Activity methods
    public void logUserActivity(String activityType, String description) {
        if (currentUser != null) {
            Activity activity = new Activity();
            activity.setId("activity_" + System.currentTimeMillis());
            activity.setUserId(currentUser.getId());
            activity.setType(ActivityType.valueOf(activityType));
            activity.setDescription(description);
            activity.setTimestamp(LocalDateTime.now());
            activities.put(activity.getId(), activity);
            
            // Update streak when user completes activities
            currentUser.updateStreakOnActivity();
            updateUser(currentUser); // Save the updated streak
        }
    }
    
    public List<Activity> getAllActivitiesForUser(String userId) {
        return activities.values().stream()
                .filter(activity -> activity.getUserId().equals(userId))
                .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                .collect(java.util.stream.Collectors.toList());
    }
    
    public void debugUserActivities(String userId) {
        System.out.println("=== In-Memory Activities for User " + userId + " ===");
        activities.values().stream()
                .filter(activity -> activity.getUserId().equals(userId))
                .forEach(activity -> System.out.println(activity.getType() + ": " + activity.getDescription()));
        System.out.println("=== End In-Memory Activities ===");
    }
}

