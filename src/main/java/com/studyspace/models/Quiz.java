package com.studyspace.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Quiz model representing a collection of questions
 */
public class Quiz {
    private String id;
    private String title;
    private String description;
    private String subject;
    private Flashcard.Difficulty difficulty;
    private int timeLimit; // in minutes
    private List<Question> questions;
    private LocalDateTime createdAt;
    private LocalDateTime lastTaken;
    private int bestScore; // percentage
    private int timesTaken;
    
    public Quiz() {
        this.questions = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.bestScore = 0;
        this.timesTaken = 0;
    }
    
    public Quiz(String title, String description, String subject, Flashcard.Difficulty difficulty, int timeLimit) {
        this();
        this.id = generateId();
        this.title = title;
        this.description = description;
        this.subject = subject;
        this.difficulty = difficulty;
        this.timeLimit = timeLimit;
    }
    
    private String generateId() {
        return "quiz_" + System.currentTimeMillis();
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
    
    public int getTimeLimit() { return timeLimit; }
    public void setTimeLimit(int timeLimit) { this.timeLimit = timeLimit; }
    
    public List<Question> getQuestions() { return questions; }
    public void setQuestions(List<Question> questions) { this.questions = questions; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getLastTaken() { return lastTaken; }
    public void setLastTaken(LocalDateTime lastTaken) { this.lastTaken = lastTaken; }
    
    public int getBestScore() { return bestScore; }
    public void setBestScore(int bestScore) { this.bestScore = bestScore; }
    
    public int getTimesTaken() { return timesTaken; }
    public void setTimesTaken(int timesTaken) { this.timesTaken = timesTaken; }
    
    // Utility methods
    public void addQuestion(Question question) {
        this.questions.add(question);
    }
    
    public void removeQuestion(Question question) {
        this.questions.remove(question);
    }
    
    public int getQuestionCount() {
        return questions.size();
    }
    
    public void recordScore(int score) {
        this.lastTaken = LocalDateTime.now();
        this.timesTaken++;
        if (score > this.bestScore) {
            this.bestScore = score;
        }
    }
    
    public String getLastTakenText() {
        if (lastTaken == null) {
            return "Never taken";
        }
        
        LocalDateTime now = LocalDateTime.now();
        long hours = java.time.Duration.between(lastTaken, now).toHours();
        
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
    
    public String getBestScoreText() {
        if (bestScore == 0) {
            return "Not taken";
        }
        return bestScore + "%";
    }
    
    public String getTimeLimitText() {
        if (timeLimit <= 0) {
            return "No time limit";
        }
        return timeLimit + " minutes";
    }
    
    @Override
    public String toString() {
        return "Quiz{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", subject='" + subject + '\'' +
                ", difficulty=" + difficulty +
                ", questionCount=" + getQuestionCount() +
                ", bestScore=" + bestScore +
                ", timesTaken=" + timesTaken +
                '}';
    }
}
