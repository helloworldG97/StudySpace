package com.studyspace.models;

import java.time.LocalDateTime;
import java.util.UUID;

public class Activity {
    private String id;
    private String userId;
    private ActivityType type;
    private String description;
    private LocalDateTime timestamp;
    
    public Activity() {
        this.id = generateId();
        this.timestamp = LocalDateTime.now();
    }
    
    public Activity(String userId, ActivityType type, String description) {
        this();
        this.userId = userId;
        this.type = type;
        this.description = description;
    }
    
    private String generateId() {
        return "activity_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public ActivityType getType() { return type; }
    public void setType(ActivityType type) { this.type = type; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    @Override
    public String toString() {
        return "Activity{" +
               "id='" + id + '\'' +
               ", userId='" + userId + '\'' +
               ", type=" + type +
               ", description='" + description + '\'' +
               ", timestamp=" + timestamp +
               '}';
    }
}

