package com.studyspace.views;

import com.studyspace.models.Note;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Dialog for creating and editing notes
 */
public class NoteDialog extends Dialog<Note> {
    private TextField titleField;
    private TextField subjectField;
    private TextArea contentArea;
    private Note existingNote;
    
    public NoteDialog(String title, Note existingNote) {
        this.existingNote = existingNote;
        
        setTitle(title);
        setHeaderText(existingNote == null ? "Create a new note" : "Edit your note");
        
        // Create dialog content
        VBox content = createDialogContent();
        getDialogPane().setContent(content);
        
        // Add buttons
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);
        
        // Set result converter
        setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return createNoteFromFields();
            }
            return null;
        });
        
        // Validate input
        Button saveButton = (Button) getDialogPane().lookupButton(saveButtonType);
        saveButton.disableProperty().bind(
            titleField.textProperty().isEmpty()
            .or(subjectField.textProperty().isEmpty())
            .or(contentArea.textProperty().isEmpty())
        );
    }
    
    /**
     * Creates the dialog content
     */
    private VBox createDialogContent() {
        VBox content = new VBox();
        content.getStyleClass().add("note-dialog-content");
        content.setSpacing(20);
        content.setPadding(new Insets(20));
        
        // Title field
        VBox titleContainer = new VBox();
        titleContainer.setSpacing(8);
        
        Label titleLabel = new Label("Title *");
        titleLabel.getStyleClass().add("dialog-label");
        
        titleField = new TextField();
        titleField.getStyleClass().add("dialog-text-field");
        titleField.setPromptText("Enter note title...");
        if (existingNote != null) {
            titleField.setText(existingNote.getTitle());
        }
        
        titleContainer.getChildren().addAll(titleLabel, titleField);
        
        // Subject field
        VBox subjectContainer = new VBox();
        subjectContainer.setSpacing(8);
        
        Label subjectLabel = new Label("Subject *");
        subjectLabel.getStyleClass().add("dialog-label");
        
        subjectField = new TextField();
        subjectField.getStyleClass().add("dialog-text-field");
        subjectField.setPromptText("Enter subject (e.g., Mathematics, Science, History)...");
        if (existingNote != null) {
            subjectField.setText(existingNote.getSubject());
        }
        
        subjectContainer.getChildren().addAll(subjectLabel, subjectField);
        
        // Content area
        VBox contentContainer = new VBox();
        contentContainer.setSpacing(8);
        
        Label contentLabel = new Label("Content *");
        contentLabel.getStyleClass().add("dialog-label");
        
        contentArea = new TextArea();
        contentArea.getStyleClass().add("dialog-text-area");
        contentArea.setPromptText("Write your note content here...");
        contentArea.setPrefRowCount(10);
        contentArea.setWrapText(true);
        if (existingNote != null) {
            contentArea.setText(existingNote.getContent());
        }
        
        contentContainer.getChildren().addAll(contentLabel, contentArea);
        
        content.getChildren().addAll(titleContainer, subjectContainer, contentContainer);
        return content;
    }
    
    /**
     * Creates a note from the form fields
     */
    private Note createNoteFromFields() {
        String title = titleField.getText().trim();
        String subject = subjectField.getText().trim();
        String content = contentArea.getText().trim();
        
        if (existingNote != null) {
            // Update existing note
            existingNote.setTitle(title);
            existingNote.setSubject(subject);
            existingNote.setContent(content);
            existingNote.setLastModified(LocalDateTime.now());
            return existingNote;
        } else {
            // Create new note
            return new Note(
                title,
                subject,
                content,
                LocalDateTime.now(),
                LocalDateTime.now()
            );
        }
    }
}
