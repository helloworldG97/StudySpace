package com.studyspace.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

//============ todo item model =============
//this is where task data and completion status are stored

public class TodoItem {
    private String id;
    private String title;
    private String description;
    private String category;
    private boolean isCompleted;
    private Priority priority;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private LocalDate dueDate;
    
    public enum Priority {
        LOW("Low", "#10b981"),
        MEDIUM("Medium", "#f59e0b"),
        HIGH("High", "#ef4444");
        
        private final String displayName;
        private final String color;
        
        Priority(String displayName, String color) {
            this.displayName = displayName;
            this.color = color;
        }
        
        public String getDisplayName() { return displayName; }
        public String getColor() { return color; }
    }
    
    public TodoItem() {
        this.createdAt = LocalDateTime.now();
        this.isCompleted = false;
        this.priority = Priority.MEDIUM;
        this.id = generateId();
    }
    
    public TodoItem(String title, String description, Priority priority, String category, LocalDate dueDate) {
        this();
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.category = category;
        this.dueDate = dueDate;
    }
    
    public TodoItem(String title, String description, Priority priority, String category) {
        this(title, description, priority, category, null);
    }
    
    private String generateId() {
        return "todo_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { 
        this.isCompleted = completed;
        if (completed && completedAt == null) {
            this.completedAt = LocalDateTime.now();
        } else if (!completed) {
            this.completedAt = null;
        }
    }
    
    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    
    // Utility methods
    public void toggleCompleted() {
        setCompleted(!isCompleted);
    }
    
    public void markAsCompleted() {
        setCompleted(true);
    }
    
    public void markAsIncomplete() {
        setCompleted(false);
    }
    
    public boolean isOverdue() {
        if (dueDate == null || isCompleted) {
            return false;
        }
        return LocalDate.now().isAfter(dueDate);
    }
    
    public boolean isDueSoon() {
        if (dueDate == null || isCompleted || isOverdue()) {
            return false;
        }
        LocalDate now = LocalDate.now();
        LocalDate soon = now.plusDays(1); // Due within 1 day
        return dueDate.isBefore(soon);
    }
    
    public String getDueDateText() {
        if (dueDate == null) {
            return "No due date";
        }
        
        LocalDate now = LocalDate.now();
        
        if (isOverdue()) {
            long days = java.time.temporal.ChronoUnit.DAYS.between(dueDate, now);
            if (days == 1) {
                return "Overdue by 1 day";
            } else {
                return "Overdue by " + days + " days";
            }
        } else {
            long days = java.time.temporal.ChronoUnit.DAYS.between(now, dueDate);
            if (days == 0) {
                return "Due today";
            } else if (days == 1) {
                return "Due tomorrow";
            } else {
                return "Due in " + days + " days";
            }
        }
    }
    
    public String getStatusText() {
        if (isCompleted) {
            return "Completed";
        } else if (isOverdue()) {
            return "Overdue";
        } else if (isDueSoon()) {
            return "Due Soon";
        } else {
            return "Pending";
        }
    }
    
    public String getCreatedAtText() {
        LocalDateTime now = LocalDateTime.now();
        long hours = java.time.Duration.between(createdAt, now).toHours();
        
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
        return "TodoItem{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", isCompleted=" + isCompleted +
                ", priority=" + priority +
                ", createdAt=" + createdAt +
                ", dueDate=" + dueDate +
                '}';
    }
}
