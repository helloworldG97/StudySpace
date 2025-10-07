package com.studyspace.models;

import java.time.LocalDateTime;
import java.util.Objects;

//============ user model =============
//this is where user data and statistics are stored

public class User {
    private String id;
    private String fullName;
    private String email;
    private String password;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
    
    // Study statistics
    private int flashcardsStudied;
    private int quizzesTaken;
    private int currentStreak;
    private int totalStudyHours;
    
    public User() {
        this.createdAt = LocalDateTime.now();
        this.flashcardsStudied = 0;
        this.quizzesTaken = 0;
        this.currentStreak = 0;
        this.totalStudyHours = 0;
    }
    
    public User(String fullName, String email, String password) {
        this();
        this.id = generateId();
        this.fullName = fullName;
        this.email = email;
        this.password = password;
    }
    
    private String generateId() {
        return "user_" + System.currentTimeMillis();
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }
    
    public int getFlashcardsStudied() { return flashcardsStudied; }
    public void setFlashcardsStudied(int flashcardsStudied) { this.flashcardsStudied = flashcardsStudied; }
    
    
    public int getQuizzesTaken() { return quizzesTaken; }
    public void setQuizzesTaken(int quizzesTaken) { this.quizzesTaken = quizzesTaken; }
    
    public int getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }
    
    public int getTotalStudyHours() { return totalStudyHours; }
    public void setTotalStudyHours(int totalStudyHours) { this.totalStudyHours = totalStudyHours; }
    
    // Utility methods
    public void updateLastLogin() {
        this.lastLoginAt = LocalDateTime.now();
    }
    
    public void incrementFlashcardsStudied() {
        this.flashcardsStudied++;
    }
    
    
    public void incrementQuizzesTaken() {
        this.quizzesTaken++;
    }
    
    public void incrementStreak() {
        this.currentStreak++;
    }
    
    public void resetStreak() {
        this.currentStreak = 0;
    }
    
    public void addStudyHours(int hours) {
        this.totalStudyHours += hours;
    }
    
    public boolean isValidForRegistration() {
        return fullName != null && !fullName.trim().isEmpty() &&
               email != null && !email.trim().isEmpty() &&
               password != null && !password.trim().isEmpty() &&
               isValidEmail(email);
    }
    
    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".") && 
               email.indexOf("@") < email.lastIndexOf(".");
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(email, user.email);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", flashcardsStudied=" + flashcardsStudied +
                ", quizzesTaken=" + quizzesTaken +
                ", currentStreak=" + currentStreak +
                '}';
    }
}
