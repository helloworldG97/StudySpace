package com.studyspace.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//============ code problem model =============
//this is where programming challenges and test cases are stored

public class CodeProblem {
    private String id;
    private String title;
    private String description;
    private Flashcard.Difficulty difficulty;
    private String language; // JavaScript, Python, etc.
    private String starterCode;
    private List<TestCase> testCases;
    private LocalDateTime createdAt;
    private LocalDateTime lastAttempted;
    private boolean isCompleted;
    private int attempts;
    
    public static class TestCase {
        private String input;
        private String expectedOutput;
        private String description;
        
        public TestCase(String input, String expectedOutput, String description) {
            this.input = input;
            this.expectedOutput = expectedOutput;
            this.description = description;
        }
        
        // Getters and Setters
        public String getInput() { return input; }
        public void setInput(String input) { this.input = input; }
        
        public String getExpectedOutput() { return expectedOutput; }
        public void setExpectedOutput(String expectedOutput) { this.expectedOutput = expectedOutput; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        @Override
        public String toString() {
            return "TestCase{" +
                    "input='" + input + '\'' +
                    ", expectedOutput='" + expectedOutput + '\'' +
                    ", description='" + description + '\'' +
                    '}';
        }
    }
    
    public CodeProblem() {
        this.testCases = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.isCompleted = false;
        this.attempts = 0;
        this.language = "JavaScript";
    }
    
    public CodeProblem(String title, String description, Flashcard.Difficulty difficulty, String language, String starterCode) {
        this();
        this.id = generateId();
        this.title = title;
        this.description = description;
        this.difficulty = difficulty;
        this.language = language;
        this.starterCode = starterCode;
    }
    
    private String generateId() {
        return "problem_" + System.currentTimeMillis();
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Flashcard.Difficulty getDifficulty() { return difficulty; }
    public void setDifficulty(Flashcard.Difficulty difficulty) { this.difficulty = difficulty; }
    
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    
    public String getStarterCode() { return starterCode; }
    public void setStarterCode(String starterCode) { this.starterCode = starterCode; }
    
    public List<TestCase> getTestCases() { return testCases; }
    public void setTestCases(List<TestCase> testCases) { this.testCases = testCases; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getLastAttempted() { return lastAttempted; }
    public void setLastAttempted(LocalDateTime lastAttempted) { this.lastAttempted = lastAttempted; }
    
    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }
    
    public int getAttempts() { return attempts; }
    public void setAttempts(int attempts) { this.attempts = attempts; }
    
    // Utility methods
    public void addTestCase(String input, String expectedOutput, String description) {
        this.testCases.add(new TestCase(input, expectedOutput, description));
    }
    
    public void addTestCase(TestCase testCase) {
        this.testCases.add(testCase);
    }
    
    public void markAsAttempted() {
        this.lastAttempted = LocalDateTime.now();
        this.attempts++;
    }
    
    public void markAsCompleted() {
        this.isCompleted = true;
        markAsAttempted();
    }
    
    public String getStatusText() {
        if (isCompleted) {
            return "Completed";
        } else if (attempts > 0) {
            return "In Progress (" + attempts + " attempts)";
        } else {
            return "Not Started";
        }
    }
    
    public String getLastAttemptedText() {
        if (lastAttempted == null) {
            return "Never attempted";
        }
        
        LocalDateTime now = LocalDateTime.now();
        long hours = java.time.Duration.between(lastAttempted, now).toHours();
        
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
        return "CodeProblem{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", difficulty=" + difficulty +
                ", language='" + language + '\'' +
                ", isCompleted=" + isCompleted +
                ", attempts=" + attempts +
                '}';
    }
}
