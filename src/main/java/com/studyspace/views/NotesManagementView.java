package com.studyspace.views;

import com.studyspace.models.Note;
import com.studyspace.utils.DataStore;
import com.studyspace.utils.IconUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * NotesManagementView - Manage and edit notes
 */
public class NotesManagementView {
    
    private final DataStore dataStore;
    private final NotesView parentView;
    
    private VBox mainContainer;
    private VBox notesContainer;
    private ScrollPane scrollPane;
    private ObservableList<Note> notesList;
    private TextField searchField;
    private ComboBox<String> sortComboBox;
    
    public NotesManagementView(NotesView parentView) {
        this.dataStore = DataStore.getInstance();
        this.parentView = parentView;
        this.notesList = FXCollections.observableArrayList(dataStore.getNotes());
        
        initializeUI();
        loadNotes();
    }
    
    /**
     * Initializes the UI components
     */
    private void initializeUI() {
        mainContainer = new VBox();
        mainContainer.getStyleClass().add("notes-management-container");
        mainContainer.setSpacing(24);
        mainContainer.setPadding(new Insets(32));
        
        // Header section
        VBox headerSection = createHeaderSection();
        
        // Search and sort section
        HBox searchSortSection = createSearchSortSection();
        
        // Action buttons section
        HBox actionSection = createActionSection();
        
        // Notes container
        notesContainer = new VBox();
        notesContainer.getStyleClass().add("notes-management-container");
        notesContainer.setSpacing(16);
        
        // Create scroll pane for notes
        scrollPane = new ScrollPane(notesContainer);
        scrollPane.getStyleClass().add("notes-scroll-pane");
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        
        mainContainer.getChildren().addAll(headerSection, searchSortSection, actionSection, scrollPane);
        
        // Setup event handlers
        setupEventHandlers();
    }
    
    /**
     * Creates the header section
     */
    private VBox createHeaderSection() {
        VBox header = new VBox();
        header.setSpacing(12);
        
        // Title and notes info
        HBox titleSection = new HBox();
        titleSection.setAlignment(Pos.CENTER_LEFT);
        titleSection.setSpacing(16);
        
        Label titleLabel = new Label("📝 Notes Management");
        titleLabel.getStyleClass().addAll("text-2xl", "font-bold", "text-primary");
        
        Label notesCountLabel = new Label("(" + notesList.size() + " notes)");
        notesCountLabel.getStyleClass().addAll("text-lg", "text-secondary");
        
        titleSection.getChildren().addAll(titleLabel, notesCountLabel);
        
        header.getChildren().add(titleSection);
        
        return header;
    }
    
    /**
     * Creates the search and sort section
     */
    private HBox createSearchSortSection() {
        HBox searchSortSection = new HBox();
        searchSortSection.getStyleClass().add("search-sort-section");
        searchSortSection.setSpacing(16);
        searchSortSection.setAlignment(Pos.CENTER_LEFT);
        
        // Search field
        VBox searchContainer = new VBox();
        searchContainer.setSpacing(4);
        
        Label searchLabel = new Label("Search Notes");
        searchLabel.getStyleClass().add("search-label");
        
        searchField = new TextField();
        searchField.getStyleClass().add("search-field");
        searchField.setPromptText("Search by title or content...");
        searchField.setPrefWidth(300);
        
        searchContainer.getChildren().addAll(searchLabel, searchField);
        
        // Sort dropdown
        VBox sortContainer = new VBox();
        sortContainer.setSpacing(4);
        
        Label sortLabel = new Label("Sort By");
        sortLabel.getStyleClass().add("sort-label");
        
        sortComboBox = new ComboBox<>();
        sortComboBox.getStyleClass().add("sort-combobox");
        sortComboBox.getItems().addAll(
            "Date Created (Newest)",
            "Date Created (Oldest)",
            "Title (A-Z)",
            "Title (Z-A)",
            "Subject (A-Z)",
            "Subject (Z-A)",
            "Last Modified (Newest)",
            "Last Modified (Oldest)"
        );
        sortComboBox.setValue("Date Created (Newest)");
        sortComboBox.setPrefWidth(200);
        
        sortContainer.getChildren().addAll(sortLabel, sortComboBox);
        
        searchSortSection.getChildren().addAll(searchContainer, sortContainer);
        return searchSortSection;
    }
    
    /**
     * Creates the action buttons section
     */
    private HBox createActionSection() {
        HBox actionSection = new HBox();
        actionSection.setSpacing(12);
        actionSection.setAlignment(Pos.CENTER_LEFT);
        
        Button addNoteButton = new Button("+ Add Note");
        addNoteButton.getStyleClass().add("primary-button");
        addNoteButton.setOnAction(e -> handleAddNote());
        
        Button backButton = new Button("← Back to Notes");
        backButton.getStyleClass().add("secondary-button");
        backButton.setOnAction(e -> handleBackToNotes());
        
        actionSection.getChildren().addAll(addNoteButton, backButton);
        
        return actionSection;
    }
    
    /**
     * Sets up event handlers
     */
    private void setupEventHandlers() {
        // Search functionality
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            loadNotes();
        });
        
        // Sort functionality
        sortComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            loadNotes();
        });
    }
    
    /**
     * Loads and displays notes
     */
    private void loadNotes() {
        notesContainer.getChildren().clear();
        
        // Filter notes based on search
        List<Note> filteredNotes = notesList.stream()
            .filter(note -> {
                String searchText = searchField.getText().toLowerCase();
                if (searchText.isEmpty()) {
                    return true;
                }
                return note.getTitle().toLowerCase().contains(searchText) ||
                       note.getContent().toLowerCase().contains(searchText) ||
                       note.getSubject().toLowerCase().contains(searchText);
            })
            .sorted((note1, note2) -> {
                String sortOption = sortComboBox.getValue();
                switch (sortOption) {
                    case "Date Created (Newest)":
                        return note2.getCreatedAt().compareTo(note1.getCreatedAt());
                    case "Date Created (Oldest)":
                        return note1.getCreatedAt().compareTo(note2.getCreatedAt());
                    case "Title (A-Z)":
                        return note1.getTitle().compareToIgnoreCase(note2.getTitle());
                    case "Title (Z-A)":
                        return note2.getTitle().compareToIgnoreCase(note1.getTitle());
                    case "Subject (A-Z)":
                        return note1.getSubject().compareToIgnoreCase(note2.getSubject());
                    case "Subject (Z-A)":
                        return note2.getSubject().compareToIgnoreCase(note1.getSubject());
                    case "Last Modified (Newest)":
                        return note2.getLastModified().compareTo(note1.getLastModified());
                    case "Last Modified (Oldest)":
                        return note1.getLastModified().compareTo(note2.getLastModified());
                    default:
                        return 0;
                }
            })
            .collect(java.util.stream.Collectors.toList());
        
        if (filteredNotes.isEmpty()) {
            showEmptyState();
        } else {
            for (Note note : filteredNotes) {
                VBox noteCard = createNoteCard(note);
                notesContainer.getChildren().add(noteCard);
            }
        }
    }
    
    /**
     * Creates a note card for management
     */
    private VBox createNoteCard(Note note) {
        VBox card = new VBox();
        card.getStyleClass().add("note-management-card");
        card.setSpacing(12);
        card.setPadding(new Insets(16));
        
        // Header with title and actions
        HBox cardHeader = new HBox();
        cardHeader.setAlignment(Pos.CENTER_LEFT);
        cardHeader.setSpacing(12);
        
        VBox contentSection = new VBox();
        contentSection.setSpacing(8);
        
        Label titleLabel = new Label(note.getTitle());
        titleLabel.getStyleClass().add("note-title");
        
        Label subjectLabel = new Label("📂 " + note.getSubject());
        subjectLabel.getStyleClass().addAll("text-sm", "text-secondary");
        
        contentSection.getChildren().addAll(titleLabel, subjectLabel);
        
        // Action buttons
        HBox actionButtons = new HBox();
        actionButtons.setSpacing(8);
        
        Button editButton = new Button();
        editButton.setGraphic(IconUtils.createSmallIconView("edit"));
        editButton.getStyleClass().addAll("action-button", "edit");
        editButton.setOnAction(e -> handleEditNote(note));
        
        Button deleteButton = new Button();
        deleteButton.setGraphic(IconUtils.createSmallIconView("trash"));
        deleteButton.getStyleClass().addAll("action-button", "delete");
        deleteButton.setOnAction(e -> handleDeleteNote(note));
        
        actionButtons.getChildren().addAll(editButton, deleteButton);
        
        cardHeader.getChildren().addAll(contentSection, actionButtons);
        
        // Content preview
        Label contentPreview = new Label(note.getPreview());
        contentPreview.getStyleClass().add("note-content-preview");
        contentPreview.setWrapText(true);
        contentPreview.setMaxHeight(80);
        
        // Footer with metadata
        HBox cardFooter = new HBox();
        cardFooter.setSpacing(16);
        cardFooter.setAlignment(Pos.CENTER_LEFT);
        
        Label dateLabel = new Label("📅 " + formatDate(note.getCreatedAt()));
        dateLabel.getStyleClass().addAll("text-xs", "text-muted");
        
        Label modifiedLabel = new Label("✏️ " + formatDate(note.getLastModified()));
        modifiedLabel.getStyleClass().addAll("text-xs", "text-muted");
        
        if (note.isPinned()) {
            Label pinnedLabel = new Label("📌 Pinned");
            pinnedLabel.getStyleClass().addAll("text-xs", "text-primary");
            cardFooter.getChildren().add(pinnedLabel);
        }
        
        cardFooter.getChildren().addAll(dateLabel, modifiedLabel);
        
        card.getChildren().addAll(cardHeader, contentPreview, cardFooter);
        
        // Add click handler to open note
        card.setOnMouseClicked(e -> handleEditNote(note));
        
        return card;
    }
    
    /**
     * Shows empty state when no notes exist
     */
    private void showEmptyState() {
        VBox emptyState = new VBox();
        emptyState.getStyleClass().add("empty-state");
        emptyState.setAlignment(Pos.CENTER);
        emptyState.setSpacing(16);
        emptyState.setPadding(new Insets(48));
        
        Label emptyIcon = new Label("📝");
        emptyIcon.getStyleClass().add("empty-icon");
        
        Label emptyTitle = new Label("No notes found");
        emptyTitle.getStyleClass().add("empty-title");
        
        Label emptyDescription = new Label("Try adjusting your search or create a new note!");
        emptyDescription.getStyleClass().add("empty-description");
        
        Button addFirstNoteButton = new Button("+ Add Your First Note");
        addFirstNoteButton.getStyleClass().add("primary-button");
        addFirstNoteButton.setOnAction(e -> handleAddNote());
        
        emptyState.getChildren().addAll(emptyIcon, emptyTitle, emptyDescription, addFirstNoteButton);
        notesContainer.getChildren().add(emptyState);
    }
    
    /**
     * Handles adding a new note
     */
    private void handleAddNote() {
        NoteDialog dialog = new NoteDialog("Create New Note", null);
        dialog.showAndWait().ifPresent(note -> {
            dataStore.addNote(note);
            notesList.add(note);
            loadNotes();
            
            // Log activity
            dataStore.logUserActivity("NOTES_ADDED", "Added note: " + note.getTitle());
            
            // Refresh activity history and all views
            com.studyspace.components.SidebarView.refreshActivityHistoryGlobally();
            com.studyspace.components.SidebarView.refreshAllViewsGlobally();
        });
    }
    
    /**
     * edit note
     */
    private void handleEditNote(Note note) {
        NoteDialog dialog = new NoteDialog("Edit Note", note);
        dialog.showAndWait().ifPresent(updatedNote -> {
            dataStore.updateNote(updatedNote);
            int index = notesList.indexOf(note);
            if (index >= 0) {
                notesList.set(index, updatedNote);
            }
            loadNotes();
            
            // Log activity
            dataStore.logUserActivity("NOTE_EDITED", "Edited note: " + updatedNote.getTitle());
            
            // Refresh activity history
            com.studyspace.components.SidebarView.refreshActivityHistoryGlobally();
        });
    }
    
    /**
     * Handles deleting a note
     */
    private void handleDeleteNote(Note note) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Delete Note");
        confirmDialog.setHeaderText("Are you sure you want to delete this note?");
        confirmDialog.setContentText("This action cannot be undone.");
        
        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                dataStore.deleteNote(note.getId());
                notesList.remove(note);
                loadNotes();
            }
        });
    }
    
    /**
     * Handles returning to the notes list
     */
    private void handleBackToNotes() {
        // Refresh the parent view to show updated statistics
        parentView.getView(); // This will refresh the view
        
        // Replace current content with the notes list
        VBox parent = (VBox) mainContainer.getParent();
        if (parent != null) {
            parent.getChildren().clear();
            parent.getChildren().add(parentView.getView());
        }
    }
    
    /**
     * Formats date for display
     */
    private String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "Unknown";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' HH:mm");
        return dateTime.format(formatter);
    }
    
    /**
     * Gets the main view container
     */
    public VBox getView() {
        return mainContainer;
    }
}
