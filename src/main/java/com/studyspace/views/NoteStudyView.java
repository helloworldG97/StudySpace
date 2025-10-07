package com.studyspace.views;

import com.studyspace.models.Note;
import com.studyspace.utils.DataStore;
import com.studyspace.utils.SceneManager;
import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * NoteStudyView - Interactive note study interface
 */
public class NoteStudyView {
    
    private final DataStore dataStore;
    private final SceneManager sceneManager;
    private final Note note;
    private final NotesView parentView;
    
    private VBox mainContainer;
    private VBox noteContainer;
    private ScrollPane scrollPane;
    private Label noteContent;
    private TextArea noteTextArea;
    private Button backButton;
    private Button editButton;
    private Button saveButton;
    private Button cancelButton;
    private Label progressLabel;
    
    private boolean isHighlightMode;
    private boolean isEditMode;
    
    public NoteStudyView(Note note, NotesView parentView) {
        this.dataStore = DataStore.getInstance();
        this.sceneManager = SceneManager.getInstance();
        this.note = note;
        this.parentView = parentView;
        this.isHighlightMode = false;
        
        initializeUI();
        // Defer showNote() to avoid scene-related issues during construction
        javafx.application.Platform.runLater(() -> showNote());
    }
    
    /**
     * Initializes the UI components
     */
    private void initializeUI() {
        mainContainer = new VBox();
        mainContainer.setSpacing(0);
        mainContainer.getStyleClass().add("readme-container");
        mainContainer.setAlignment(Pos.TOP_LEFT);
        mainContainer.setStyle("-fx-background-color: white; -fx-padding: 0;");
        
        // Navigation section (moved to top left)
        HBox navigationSection = createNavigationSection();
        
        // Note container (enlarged)
        noteContainer = createNoteContainer();
        
        mainContainer.getChildren().addAll(navigationSection, noteContainer);
    }
    
    /**
     * Creates the navigation section (top left)
     */
    private HBox createNavigationSection() {
        HBox navigation = new HBox();
        navigation.setSpacing(12);
        navigation.setAlignment(Pos.TOP_LEFT);
        navigation.setPadding(new javafx.geometry.Insets(16, 0, 16, 0));
        
        // Back button
        backButton = new Button("← Back to Notes");
        backButton.getStyleClass().add("readme-button");
        backButton.setOnAction(e -> handleBackToNotes());
        
        // Edit button
        editButton = new Button("✏️ Edit Document");
        editButton.getStyleClass().add("readme-button");
        editButton.setOnAction(e -> handleEditNote());
        
        // Save button (initially hidden)
        saveButton = new Button("💾 Save Changes");
        saveButton.getStyleClass().add("readme-button");
        saveButton.setOnAction(e -> handleSaveNote());
        saveButton.setVisible(false);
        
        // Cancel button (initially hidden)
        cancelButton = new Button("❌ Cancel");
        cancelButton.getStyleClass().add("readme-button");
        cancelButton.setOnAction(e -> handleCancelEdit());
        cancelButton.setVisible(false);
        
        // Progress label
        progressLabel = new Label("Document View Active");
        progressLabel.getStyleClass().addAll("text-success", "progress-label");
        
        navigation.getChildren().addAll(backButton, editButton, saveButton, cancelButton, progressLabel);
        
        return navigation;
    }
    
    
    /**
     * Creates the note container
     */
    private VBox createNoteContainer() {
        VBox container = new VBox();
        container.getStyleClass().add("note-study-container");
        container.setAlignment(Pos.TOP_LEFT);
        container.setSpacing(0);
        container.setPrefWidth(1200);
        container.setMinHeight(700); // Increased minimum height for better editing
        container.setPrefHeight(800); // Set preferred height for better editing experience
        container.setPadding(new javafx.geometry.Insets(0));
        container.setStyle("-fx-background-color: white;");
        
        // Create a scrollable area for the content
        scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false); // Allow content to expand beyond viewport
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("readme-scroll-pane");
        scrollPane.setStyle("-fx-background-color: white;");
        scrollPane.setMinHeight(500); // Set minimum height for the scroll pane
        scrollPane.setPrefHeight(600); // Set preferred height for better editing
        scrollPane.setMaxHeight(Double.MAX_VALUE); // Allow unlimited height
        // Allow scroll pane to expand to fill available space
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        
        // Note content with readme-like formatting
        noteContent = new Label();
        noteContent.getStyleClass().addAll("readme-note-content");
        noteContent.setWrapText(true);
        noteContent.setMaxWidth(1100);
        noteContent.setAlignment(Pos.TOP_LEFT);
        // Remove MinHeight constraint to allow content to expand naturally
        noteContent.setStyle("-fx-cursor: text;");
        
        // Add click handler for highlight mode
        noteContent.setOnMouseClicked(e -> toggleHighlightMode());
        
        // Create text area for editing (initially hidden)
        noteTextArea = new TextArea();
        noteTextArea.getStyleClass().addAll("readme-text-area");
        noteTextArea.setWrapText(true);
        noteTextArea.setMaxWidth(1100);
        noteTextArea.setMinHeight(500); // Set a proper minimum height for editing
        noteTextArea.setPrefHeight(600); // Set preferred height for better editing experience
        noteTextArea.setMaxHeight(Double.MAX_VALUE); // Allow unlimited height for long content
        noteTextArea.setStyle("-fx-font-size: 14px; -fx-font-family: 'Segoe UI', Arial, sans-serif;");
        noteTextArea.setVisible(false);
        
        // Create content container
        VBox contentContainer = new VBox();
        contentContainer.setSpacing(0);
        contentContainer.setPadding(new javafx.geometry.Insets(24));
        contentContainer.setStyle("-fx-background-color: white;");
        contentContainer.getChildren().add(noteContent);
        // Allow content container to expand to fit all content
        VBox.setVgrow(noteContent, Priority.ALWAYS);
        
        scrollPane.setContent(contentContainer);
        container.getChildren().add(scrollPane);
        
        return container;
    }
    
    
    /**
     * Shows the note content
     */
    private void showNote() {
        if (!isEditMode) {
            // Format content for better readability
            String formattedContent = formatContentForReadme(note.getContent());
            noteContent.setText(formattedContent);
            noteContent.getStyleClass().removeAll("note-highlight-mode", "note-focus-mode");
            noteContent.getStyleClass().add("note-normal-mode");
            
            // Ensure the content is properly displayed and scrollable
            refreshScrollPane();
            
            // Update progress label
            progressLabel.setText("Document View Active");
            progressLabel.getStyleClass().removeAll("text-success", "text-warning");
            progressLabel.getStyleClass().add("text-success");
        }
    }
    
    /**
     * Refreshes the scroll pane to ensure all content is visible
     */
    private void refreshScrollPane() {
        // Force the scroll pane to recalculate its content size
        scrollPane.requestLayout();
        
        // Ensure the content container expands to fit all content
        VBox contentContainer = (VBox) scrollPane.getContent();
        if (contentContainer != null) {
            contentContainer.requestLayout();
            
            // Force the content to recalculate its size
            if (isEditMode && noteTextArea.isVisible()) {
                noteTextArea.requestLayout();
            } else if (!isEditMode && noteContent.isVisible()) {
                noteContent.requestLayout();
            }
        }
        
        // Reset scroll position to top
        scrollPane.setVvalue(0);
        
        // Force a complete layout pass (only if scene is available)
        if (scrollPane.getScene() != null) {
            scrollPane.getScene().getRoot().requestLayout();
        }
    }
    
    /**
     * Formats content for readme-like display
     */
    private String formatContentForReadme(String content) {
        if (content == null || content.trim().isEmpty()) {
            return "No content available.";
        }
        
        // Clean up the content and ensure proper formatting
        StringBuilder formatted = new StringBuilder();
        String[] lines = content.split("\n");
        
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) {
                formatted.append("\n");
            } else {
                // Preserve the original line structure and add proper spacing
                formatted.append(trimmed).append("\n");
            }
        }
        
        // Ensure the content is properly formatted and not truncated
        String result = formatted.toString();
        
        // Add some padding at the end to ensure all content is visible
        if (!result.endsWith("\n\n")) {
            result += "\n";
        }
        
        return result;
    }
    
    /**
     * Toggles highlight mode
     */
    private void toggleHighlightMode() {
        isHighlightMode = !isHighlightMode;
        
        if (isHighlightMode) {
            // Enter highlight mode
            noteContent.getStyleClass().removeAll("note-normal-mode", "note-focus-mode");
            noteContent.getStyleClass().add("note-highlight-mode");
            
            // Add highlight animation
            FadeTransition fadeTransition = new FadeTransition(Duration.millis(300), noteContent);
            fadeTransition.setFromValue(1.0);
            fadeTransition.setToValue(0.7);
            fadeTransition.setAutoReverse(true);
            fadeTransition.setCycleCount(2);
            fadeTransition.play();
            
            progressLabel.setText("Highlight Mode Active - Click text to highlight");
            progressLabel.getStyleClass().removeAll("text-success", "text-warning");
            progressLabel.getStyleClass().add("text-warning");
            
        } else {
            // Exit highlight mode
            noteContent.getStyleClass().removeAll("note-highlight-mode", "note-focus-mode");
            noteContent.getStyleClass().add("note-normal-mode");
            
            progressLabel.setText("Study Mode Active");
            progressLabel.getStyleClass().removeAll("text-success", "text-warning");
            progressLabel.getStyleClass().add("text-success");
        }
    }
    
    /**
     * Toggles focus mode
     */
    private void toggleFocusMode() {
        // Toggle focus mode (simplified version)
        if (noteContent.getStyleClass().contains("note-focus-mode")) {
            noteContent.getStyleClass().remove("note-focus-mode");
            noteContent.getStyleClass().add("note-normal-mode");
            progressLabel.setText("Document View Active");
        } else {
            noteContent.getStyleClass().removeAll("note-normal-mode", "note-highlight-mode");
            noteContent.getStyleClass().add("note-focus-mode");
            progressLabel.setText("Focus Mode Active");
        }
        
        // Add focus animation
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), noteContainer);
        scaleTransition.setFromX(1.0);
        scaleTransition.setToX(1.05);
        scaleTransition.setAutoReverse(true);
        scaleTransition.setCycleCount(2);
        scaleTransition.play();
    }
    
    /**
     * Handles print view functionality
     */
    private void handlePrintView() {
        // For now, show a message about print functionality
        sceneManager.showInfoDialog("Print View", 
            "Print functionality will be implemented soon! This would allow you to print or export the document.");
    }
    
    /**
     * Handles previous note navigation
     */
    private void handlePreviousNote() {
        // For now, just show a message
        sceneManager.showInfoDialog("Navigation", 
            "Previous note navigation will be implemented soon!");
    }
    
    /**
     * Handles next note navigation
     */
    private void handleNextNote() {
        // For now, just show a message
        sceneManager.showInfoDialog("Navigation", 
            "Next note navigation will be implemented soon!");
    }
    
    /**
     * Handles editing the note - switches to inline edit mode
     */
    private void handleEditNote() {
        if (!isEditMode) {
            // Enter edit mode
            isEditMode = true;
            
            // Replace the content in the container
            VBox contentContainer = (VBox) scrollPane.getContent();
            contentContainer.getChildren().clear();
            contentContainer.getChildren().add(noteTextArea);
            
            // Set the text area content and make it visible
            noteTextArea.setText(note.getContent());
            noteTextArea.setVisible(true);
            
            // Configure text area for proper editing
            noteTextArea.setMinHeight(500);
            noteTextArea.setPrefHeight(600);
            noteTextArea.setMaxHeight(Double.MAX_VALUE); // Allow unlimited height
            
            // Allow text area to expand to fit all content
            VBox.setVgrow(noteTextArea, Priority.ALWAYS);
            
            // Update button visibility
            editButton.setVisible(false);
            saveButton.setVisible(true);
            cancelButton.setVisible(true);
            
            // Update progress label
            progressLabel.setText("Edit Mode Active");
            progressLabel.getStyleClass().removeAll("text-success", "text-warning");
            progressLabel.getStyleClass().add("text-warning");
            
            // Focus on the text area
            noteTextArea.requestFocus();
            
            // Refresh scroll pane to ensure proper display
            refreshScrollPane();
        }
    }
    
    /**
     * Handles saving the note changes
     */
    private void handleSaveNote() {
        String newContent = noteTextArea.getText();
        note.setContent(newContent);
        note.updateModifiedTime();
        dataStore.updateNote(note);
        
        // Log activity
        dataStore.logUserActivity("NOTE_EDITED", "Edited note: " + note.getTitle());
        
        // Refresh activity history
        com.studyspace.components.SidebarView.refreshActivityHistoryGlobally();
        
        // Exit edit mode
        exitEditMode();
        
        // Show success message
        sceneManager.showInfoDialog("Success", "Note has been updated successfully!");
    }
    
    /**
     * Handles canceling the edit
     */
    private void handleCancelEdit() {
        // Exit edit mode without saving
        exitEditMode();
    }
    
    /**
     * Exits edit mode and returns to view mode
     */
    private void exitEditMode() {
        isEditMode = false;
        
        // Replace the content in the container
        VBox contentContainer = (VBox) scrollPane.getContent();
        contentContainer.getChildren().clear();
        contentContainer.getChildren().add(noteContent);
        
        // Hide the text area and show the label
        noteTextArea.setVisible(false);
        noteContent.setVisible(true);
        
        // Ensure note content can expand to fit all content
        VBox.setVgrow(noteContent, Priority.ALWAYS);
        
        // Update button visibility
        editButton.setVisible(true);
        saveButton.setVisible(false);
        cancelButton.setVisible(false);
        
        // Update progress label
        progressLabel.setText("Document View Active");
        progressLabel.getStyleClass().removeAll("text-success", "text-warning");
        progressLabel.getStyleClass().add("text-success");
        
        // Refresh the content display and scroll pane
        showNote();
        refreshScrollPane();
    }
    
    /**
     * Handles returning to the notes list
     */
    private void handleBackToNotes() {
        // Use the new returnToNotesList method for proper StackPane navigation
        parentView.returnToNotesList();
    }
    
    /**
     * Formats date for display
     */
    private String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "Unknown";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        return dateTime.format(formatter);
    }
    
    /**
     * Gets the main view container
     */
    public VBox getView() {
        return mainContainer;
    }
}
