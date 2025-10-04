package com.studyspace.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//============ note model =============
//this is where study notes and content are stored

public class Note {
    private String id;
    private String title;
    private String content;
    private List<String> tags;
    private String category;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private boolean isPinned;
    
    public Note() {
        this.tags = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
        this.isPinned = false;
    }
    
    public Note(String title, String content, String category) {
        this();
        this.id = generateId();
        this.title = title;
        this.content = content;
        this.category = category;
    }
    
    public Note(String title, String subject, String content, LocalDateTime createdAt, LocalDateTime lastModified) {
        this.id = generateId();
        this.title = title;
        this.category = subject; // Using category as subject
        this.content = content;
        this.createdAt = createdAt;
        this.modifiedAt = lastModified;
        this.tags = new ArrayList<>();
        this.isPinned = false;
    }
    
    private String generateId() {
        return "note_" + System.currentTimeMillis();
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { 
        this.title = title;
        updateModifiedTime();
    }
    
    public String getContent() { return content; }
    public void setContent(String content) { 
        this.content = content;
        updateModifiedTime();
    }
    
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getSubject() { return category; } // Alias for category
    public void setSubject(String subject) { this.category = subject; }
    
    public LocalDateTime getLastModified() { return modifiedAt; }
    public void setLastModified(LocalDateTime lastModified) { this.modifiedAt = lastModified; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getModifiedAt() { return modifiedAt; }
    public void setModifiedAt(LocalDateTime modifiedAt) { this.modifiedAt = modifiedAt; }
    
    public boolean isPinned() { return isPinned; }
    public void setPinned(boolean pinned) { isPinned = pinned; }
    
    // Utility methods
    public void addTag(String tag) {
        if (!tags.contains(tag)) {
            tags.add(tag);
            updateModifiedTime();
        }
    }
    
    public void removeTag(String tag) {
        if (tags.remove(tag)) {
            updateModifiedTime();
        }
    }
    
    public void updateModifiedTime() {
        this.modifiedAt = LocalDateTime.now();
    }
    
    public void togglePin() {
        this.isPinned = !this.isPinned;
        updateModifiedTime();
    }
    
    public String getPreview() {
        if (content == null || content.trim().isEmpty()) {
            return "No content";
        }
        
        String cleanContent = content.replaceAll("\\s+", " ").trim();
        if (cleanContent.length() <= 100) {
            return cleanContent;
        }
        return cleanContent.substring(0, 97) + "...";
    }
    
    public String getTagsAsString() {
        if (tags.isEmpty()) {
            return "";
        }
        return String.join(", ", tags);
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
    
    public String getModifiedAtText() {
        if (modifiedAt.equals(createdAt)) {
            return getCreatedAtText();
        }
        
        LocalDateTime now = LocalDateTime.now();
        long hours = java.time.Duration.between(modifiedAt, now).toHours();
        
        if (hours < 1) {
            return "Modified just now";
        } else if (hours < 24) {
            return "Modified " + hours + " hours ago";
        } else if (hours < 48) {
            return "Modified yesterday";
        } else {
            long days = hours / 24;
            return "Modified " + days + " days ago";
        }
    }
    
    @Override
    public String toString() {
        return "Note{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", tags=" + tags.size() +
                ", isPinned=" + isPinned +
                ", createdAt=" + createdAt +
                '}';
    }
}
