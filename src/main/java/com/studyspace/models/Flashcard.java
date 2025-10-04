package com.studyspace.models;

import java.time.LocalDateTime;

//============ flashcard model =============
//this is where flashcard data and study progress is stored

public class Flashcard {
    private String id;
    private String question;
    private String answer;
    private Difficulty difficulty;
    private LocalDateTime createdAt;
    private LocalDateTime lastStudied;
    private int timesStudied;
    private boolean isCorrect; // For tracking last answer
    
    public enum Difficulty {
        EASY("Easy", "#10b981"),
        MEDIUM("Medium", "#f59e0b"),
        HARD("Hard", "#ef4444");
        
        private final String displayName;
        private final String color;
        
        Difficulty(String displayName, String color) {
            this.displayName = displayName;
            this.color = color;
        }
        
        public String getDisplayName() { return displayName; }
        public String getColor() { return color; }
    }
    
    public Flashcard() {
        this.createdAt = LocalDateTime.now();
        this.timesStudied = 0;
        this.difficulty = Difficulty.MEDIUM;
    }
    
    public Flashcard(String question, String answer, Difficulty difficulty) {
        this();
        this.id = generateId();
        this.question = question;
        this.answer = answer;
        this.difficulty = difficulty;
    }
    
    private String generateId() {
        return "flashcard_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    
    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
    
    public Difficulty getDifficulty() { return difficulty; }
    public void setDifficulty(Difficulty difficulty) { this.difficulty = difficulty; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getLastStudied() { return lastStudied; }
    public void setLastStudied(LocalDateTime lastStudied) { this.lastStudied = lastStudied; }
    
    public int getTimesStudied() { return timesStudied; }
    public void setTimesStudied(int timesStudied) { this.timesStudied = timesStudied; }
    
    public boolean isCorrect() { return isCorrect; }
    public void setCorrect(boolean correct) { isCorrect = correct; }
    
    // Utility methods
    public void markAsStudied() {
        this.lastStudied = LocalDateTime.now();
        this.timesStudied++;
    }
    
    public void markAsCorrect() {
        this.isCorrect = true;
        markAsStudied();
    }
    
    public void markAsIncorrect() {
        this.isCorrect = false;
        markAsStudied();
    }
    
    @Override
    public String toString() {
        return "Flashcard{" +
                "id='" + id + '\'' +
                ", question='" + question + '\'' +
                ", difficulty=" + difficulty +
                ", timesStudied=" + timesStudied +
                '}';
    }
}
