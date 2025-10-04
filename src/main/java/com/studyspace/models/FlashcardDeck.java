package com.studyspace.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//============ flashcard deck model =============
//this is where flashcard collections and study progress are stored

public class FlashcardDeck {
    private String id;
    private String title;
    private String description;
    private String subject;
    private Flashcard.Difficulty difficulty;
    private List<Flashcard> flashcards;
    private LocalDateTime createdAt;
    private LocalDateTime lastStudied;
    private int totalStudySessions;
    
    public FlashcardDeck() {
        this.flashcards = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.totalStudySessions = 0;
    }
    
    public FlashcardDeck(String title, String description, String subject, Flashcard.Difficulty difficulty) {
        this();
        this.id = generateId();
        this.title = title;
        this.description = description;
        this.subject = subject;
        this.difficulty = difficulty;
    }
    
    private String generateId() {
        return "deck_" + System.currentTimeMillis();
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    
    public Flashcard.Difficulty getDifficulty() { return difficulty; }
    public void setDifficulty(Flashcard.Difficulty difficulty) { this.difficulty = difficulty; }
    
    public List<Flashcard> getFlashcards() { return flashcards; }
    public void setFlashcards(List<Flashcard> flashcards) { this.flashcards = flashcards; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getLastStudied() { return lastStudied; }
    public void setLastStudied(LocalDateTime lastStudied) { this.lastStudied = lastStudied; }
    
    public int getTotalStudySessions() { return totalStudySessions; }
    public void setTotalStudySessions(int totalStudySessions) { this.totalStudySessions = totalStudySessions; }
    
    // Utility methods
    public void addFlashcard(Flashcard flashcard) {
        this.flashcards.add(flashcard);
    }
    
    public void removeFlashcard(Flashcard flashcard) {
        this.flashcards.remove(flashcard);
    }
    
    public int getCardCount() {
        return flashcards.size();
    }
    
    public void markAsStudied() {
        this.lastStudied = LocalDateTime.now();
        this.totalStudySessions++;
    }
    
    public String getLastStudiedText() {
        if (lastStudied == null) {
            return "Never studied";
        }
        
        LocalDateTime now = LocalDateTime.now();
        long hours = java.time.Duration.between(lastStudied, now).toHours();
        
        if (hours < 1) {
            return "Just now";
        } else if (hours < 24) {
            return hours + " hours ago";
        } else if (hours < 48) {
            return "Yesterday";
        } else {
            long days = hours / 24;
            return days + " days ago";
        }
    }
    
    @Override
    public String toString() {
        return "FlashcardDeck{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", subject='" + subject + '\'' +
                ", difficulty=" + difficulty +
                ", cardCount=" + getCardCount() +
                ", totalStudySessions=" + totalStudySessions +
                '}';
    }
}
