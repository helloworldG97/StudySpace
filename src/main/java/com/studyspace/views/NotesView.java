package com.studyspace.views;

import com.studyspace.models.Note;
import com.studyspace.utils.DataStore;
import com.studyspace.utils.IconUtils;
import com.studyspace.utils.SceneManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

//============ notes view =============
//this is where study notes are managed, created, and organized

public class NotesView {
    private StackPane mainContainer;
    private VBox contentContainer;
    private DataStore dataStore;
    private ObservableList<Note> notesList;
    private FilteredList<Note> filteredNotes;
    private SortedList<Note> sortedNotes;
    private TextField searchField;
    private VBox notesContainer;
    private Button createNoteButton;
    
    public NotesView() {
        this.dataStore = DataStore.getInstance();
        initializeData();
        createView();
        setupEventHandlers();
    }
    
    /**
     * Initialize data and filtering
     */
    private void initializeData() {
        notesList = FXCollections.observableArrayList(dataStore.getNotes());
        filteredNotes = new FilteredList<>(notesList);
        sortedNotes = new SortedList<>(filteredNotes);
    }
    
    /**
     * Creates the main view
     */
    private void createView() {
        // Create StackPane as root container for proper mouse click handling
        mainContainer = new StackPane();
        mainContainer.getStyleClass().add("notes-main-container");
        
        // Create content container with the original VBox layout
        contentContainer = new VBox();
        contentContainer.getStyleClass().add("notes-content-container");
        contentContainer.setSpacing(24);
        contentContainer.setPadding(new Insets(32));
        
        // Header section
        VBox headerSection = createHeaderSection();
        
        // Search and sort section
        HBox searchSortSection = createSearchSortSection();
        
        // Notes container
        notesContainer = new VBox();
        notesContainer.getStyleClass().add("notes-container");
        notesContainer.setSpacing(16);
        
        // Create scroll pane for notes
        ScrollPane scrollPane = new ScrollPane(notesContainer);
        scrollPane.getStyleClass().add("notes-scroll-pane");
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        
        contentContainer.getChildren().addAll(headerSection, searchSortSection, scrollPane);
        
        // Add content container to StackPane
        mainContainer.getChildren().add(contentContainer);
        
        // Load notes
        loadNotes();
    }
    
    /**
     * Creates the header section with icon, title, subtitle and action buttons (similar to flashcard view)
     */
    private VBox createHeaderSection() {
        VBox headerSection = new VBox();
        headerSection.getStyleClass().add("notes-header-section");
        headerSection.setSpacing(12);
        
        // Top section with title and action buttons
        HBox topSection = new HBox();
        topSection.setAlignment(Pos.CENTER_LEFT);
        topSection.setSpacing(12);
        
        // Title section with icon
        HBox titleRow = new HBox();
        titleRow.setAlignment(Pos.CENTER_LEFT);
        titleRow.setSpacing(12);
        
        // Icon
        Label titleIcon = new Label();
        titleIcon.setGraphic(IconUtils.createMediumIconView("note"));
        
        // Title and subtitle
        VBox titleSection = new VBox();
        titleSection.setSpacing(4);
        
        Label titleLabel = new Label("Notes");
        titleLabel.getStyleClass().add("notes-title");
        
        Label subtitleLabel = new Label("Organize your study notes by subject");
        subtitleLabel.getStyleClass().add("notes-subtitle");
        
        titleSection.getChildren().addAll(titleLabel, subtitleLabel);
        titleRow.getChildren().addAll(titleIcon, titleSection);
        
        // Action buttons
        HBox actionButtons = new HBox();
        actionButtons.setSpacing(12);
        actionButtons.setAlignment(Pos.CENTER_RIGHT);
        
        Button importFlashcardButton = new Button();
        importFlashcardButton.setGraphic(IconUtils.createIconTextHBox("upload", "Import Notes"));
        importFlashcardButton.getStyleClass().add("secondary-button");
        importFlashcardButton.setOnAction(e -> showImportFlashcardDialog());
        
        createNoteButton = new Button();
        createNoteButton.setGraphic(IconUtils.createIconTextHBox("add", "Create Note"));
        createNoteButton.getStyleClass().add("success-button");
        
        actionButtons.getChildren().addAll(importFlashcardButton, createNoteButton);
        
        // Top section layout
        HBox.setHgrow(titleRow, Priority.ALWAYS);
        topSection.getChildren().addAll(titleRow, actionButtons);
        
        headerSection.getChildren().add(topSection);
        return headerSection;
    }
    
    /**
     * Creates the search and filter section (similar to flashcard view)
     */
    private HBox createSearchSortSection() {
        HBox searchFilterSection = new HBox();
        searchFilterSection.getStyleClass().add("search-filter-section");
        searchFilterSection.setSpacing(16);
        searchFilterSection.setAlignment(Pos.CENTER_LEFT);
        
        // Search field with icon
        HBox searchBox = new HBox();
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setSpacing(8);
        searchBox.getStyleClass().add("search-box");
        
        Label searchIcon = new Label();
        searchIcon.setGraphic(IconUtils.createSmallIconView("search"));
        
        searchField = new TextField();
        searchField.getStyleClass().add("search-field");
        searchField.setPromptText("Search notes...");
        searchField.setPrefWidth(300);
        
        searchBox.getChildren().addAll(searchIcon, searchField);
        
        // Sort dropdown
        HBox sortContainer = new HBox();
        sortContainer.setSpacing(8);
        sortContainer.setAlignment(Pos.CENTER_LEFT);
        
        Label sortLabel = new Label("Sort By:");
        sortLabel.getStyleClass().add("text-sm");
        
        ComboBox<String> sortComboBox = new ComboBox<>();
        sortComboBox.getStyleClass().add("filter-combobox");
        sortComboBox.getItems().addAll(
            "Title (A-Z)",
            "Title (Z-A)", 
            "Date Created (Newest)",
            "Date Created (Oldest)",
            "Subject (A-Z)",
            "Subject (Z-A)",
            "Last Modified (Newest)",
            "Last Modified (Oldest)"
        );
        sortComboBox.setValue("Date Created (Newest)");
        sortComboBox.setPrefWidth(200);
        sortComboBox.setOnAction(e -> applySorting(sortComboBox.getValue()));
        
        sortContainer.getChildren().addAll(sortLabel, sortComboBox);
        
        searchFilterSection.getChildren().addAll(searchBox, sortContainer);
        return searchFilterSection;
    }
    
    /**
     * Sets up event handlers
     */
    private void setupEventHandlers() {
        try {
            // Create note button with proper event handling
            if (createNoteButton != null) {
                createNoteButton.setOnAction(e -> {
                    e.consume();
                    createNewNote();
                });
                createNoteButton.setOnMouseClicked(e -> {
                    e.consume();
                    createNewNote();
                });
            }
            
            // Search functionality with null checks
            if (searchField != null) {
                searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                    try {
                        filteredNotes.setPredicate(note -> {
                            if (newValue == null || newValue.isEmpty()) {
                                return true;
                            }
                            String lowerCaseFilter = newValue.toLowerCase();
                            return note.getTitle().toLowerCase().contains(lowerCaseFilter) ||
                                   note.getContent().toLowerCase().contains(lowerCaseFilter) ||
                                   note.getSubject().toLowerCase().contains(lowerCaseFilter);
                        });
                        // Reload notes to show filtered results
                        loadNotes();
                    } catch (Exception ex) {
                        System.err.println("Error in search filter: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                });
            }
            
            // Sort functionality - ensure sorting is applied when sortComboBox changes
            // This will be handled by the sortComboBox.setOnAction in createSearchFilterSection
        } catch (Exception e) {
            System.err.println("Error setting up event handlers: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Applies sorting based on selected option
     */
    private void applySorting(String sortOption) {
        if (sortOption == null) return;
        
        switch (sortOption) {
            case "Title (A-Z)":
                sortedNotes.setComparator(Comparator.comparing(Note::getTitle, String.CASE_INSENSITIVE_ORDER));
                break;
            case "Title (Z-A)":
                sortedNotes.setComparator(Comparator.comparing(Note::getTitle, String.CASE_INSENSITIVE_ORDER).reversed());
                break;
            case "Date Created (Newest)":
                sortedNotes.setComparator(Comparator.comparing(Note::getCreatedAt).reversed());
                break;
            case "Date Created (Oldest)":
                sortedNotes.setComparator(Comparator.comparing(Note::getCreatedAt));
                break;
            case "Subject (A-Z)":
                sortedNotes.setComparator(Comparator.comparing(Note::getSubject, String.CASE_INSENSITIVE_ORDER));
                break;
            case "Subject (Z-A)":
                sortedNotes.setComparator(Comparator.comparing(Note::getSubject, String.CASE_INSENSITIVE_ORDER).reversed());
                break;
            case "Last Modified (Newest)":
                sortedNotes.setComparator(Comparator.comparing(Note::getModifiedAt).reversed());
                break;
            case "Last Modified (Oldest)":
                sortedNotes.setComparator(Comparator.comparing(Note::getModifiedAt));
                break;
        }
        
        // Reload notes to show sorted results
        loadNotes();
    }
    
    /**
     * Loads and displays notes in a grid layout similar to flashcard view
     */
    private void loadNotes() {
        notesContainer.getChildren().clear();
        
        if (sortedNotes.isEmpty()) {
            showEmptyState();
        } else {
            // Create a grid layout for note cards (similar to flashcard view)
            FlowPane notesGrid = new FlowPane();
            notesGrid.getStyleClass().add("notes-grid");
            notesGrid.setHgap(16);
            notesGrid.setVgap(16);
            notesGrid.setAlignment(Pos.TOP_LEFT);
            notesGrid.setPrefWrapLength(1200); // Allow wrapping at 1200px width
            
            for (Note note : sortedNotes) {
                VBox noteCard = createNoteCard(note);
                notesGrid.getChildren().add(noteCard);
            }
            
            notesContainer.getChildren().add(notesGrid);
        }
    }
    
    /**
     * Shows empty state when no notes exist
     */
    private void showEmptyState() {
        VBox emptyState = new VBox();
        emptyState.getStyleClass().add("empty-state");
        emptyState.setAlignment(Pos.CENTER);
        emptyState.setSpacing(16);
        
        Label emptyIcon = new Label("📝");
        emptyIcon.getStyleClass().add("empty-icon");
        
        Label emptyTitle = new Label("No notes yet");
        emptyTitle.getStyleClass().add("empty-title");
        
        Label emptyDescription = new Label("Create your first note to start organizing your study materials");
        emptyDescription.getStyleClass().add("empty-description");
        
        Button createFirstNoteButton = new Button("📝 Create Your First Note");
        createFirstNoteButton.getStyleClass().add("primary-button");
        createFirstNoteButton.setOnAction(e -> createNewNote());
        
        emptyState.getChildren().addAll(emptyIcon, emptyTitle, emptyDescription, createFirstNoteButton);
        notesContainer.getChildren().add(emptyState);
    }
    
    /**
     * Creates a note card with button placement similar to flashcard view
     */
    private VBox createNoteCard(Note note) {
        VBox noteCard = new VBox();
        noteCard.getStyleClass().add("note-card");
        noteCard.setSpacing(16);
        noteCard.setPrefWidth(300);
        noteCard.setMinWidth(280);
        noteCard.setMaxWidth(320);
        noteCard.setMinHeight(280);
        noteCard.setMaxHeight(320); // Fixed maximum height
        noteCard.setPrefHeight(300); // Fixed preferred height
        
        // Add hover effect for better visual feedback
        noteCard.setOnMouseEntered(e -> {
            noteCard.getStyleClass().add("note-card-hover");
        });
        
        noteCard.setOnMouseExited(e -> {
            noteCard.getStyleClass().remove("note-card-hover");
        });
        
        // Header with title and edit/delete buttons
        HBox cardHeader = new HBox();
        cardHeader.setAlignment(Pos.CENTER_LEFT);
        cardHeader.setSpacing(12);
        
        VBox titleSection = new VBox();
        titleSection.setSpacing(4);
        
        Label titleLabel = new Label(note.getTitle());
        titleLabel.getStyleClass().add("note-title");
        titleLabel.setWrapText(true);
        titleLabel.setMaxHeight(40); // Limit title height
        titleLabel.setMaxWidth(250); // Limit title width
        
        titleSection.getChildren().add(titleLabel);
        
        // Edit and delete buttons in header
        HBox headerActionButtons = new HBox();
        headerActionButtons.setSpacing(8);
        
        Button editButton = new Button();
        editButton.setGraphic(IconUtils.createSmallIconView("edit"));
        editButton.getStyleClass().addAll("action-button", "edit");
        editButton.setOnAction(e -> {
            e.consume();
            editNote(note);
        });
        editButton.setOnMouseClicked(e -> {
            e.consume();
            editNote(note);
        });
        
        Button deleteButton = new Button();
        deleteButton.setGraphic(IconUtils.createSmallIconView("trash"));
        deleteButton.getStyleClass().addAll("action-button", "delete");
        deleteButton.setOnAction(e -> {
            e.consume();
            deleteNote(note);
        });
        deleteButton.setOnMouseClicked(e -> {
            e.consume();
            deleteNote(note);
        });
        
        headerActionButtons.getChildren().addAll(editButton, deleteButton);
        
        cardHeader.getChildren().addAll(titleSection, headerActionButtons);
        
        // Subject badge
        Label subjectBadge = new Label(note.getSubject());
        subjectBadge.getStyleClass().add("subject-badge");
        
        // Content preview
        Label contentPreview = new Label(note.getPreview());
        contentPreview.getStyleClass().add("note-content-preview");
        contentPreview.setWrapText(true);
        contentPreview.setMaxHeight(60); // Slightly increased but still limited
        contentPreview.setMaxWidth(280); // Limit content width
        
        // Date information
        HBox dateInfo = new HBox();
        dateInfo.setSpacing(8);
        dateInfo.setAlignment(Pos.CENTER_LEFT);
        
        Label dateLabel = new Label(formatDate(note.getCreatedAt()));
        dateLabel.getStyleClass().add("note-date");
        
        dateInfo.getChildren().add(dateLabel);
        
        noteCard.getChildren().addAll(cardHeader, subjectBadge, contentPreview, dateInfo);
        
        // Study button at the bottom (similar to flashcard layout)
        HBox studyButtons = new HBox();
        studyButtons.setSpacing(8);
        studyButtons.setAlignment(Pos.CENTER_LEFT);
        
        Button studyButton = new Button();
        studyButton.setGraphic(IconUtils.createIconTextHBox("book", "Study"));
        studyButton.getStyleClass().add("success-button");
        studyButton.setOnAction(e -> {
            e.consume();
            System.out.println("Study button clicked for note: " + note.getTitle());
            handleStudyNote(note);
        });
        studyButton.setOnMouseClicked(e -> {
            e.consume();
            System.out.println("Study button mouse clicked for note: " + note.getTitle());
            handleStudyNote(note);
        });
        
        studyButtons.getChildren().addAll(studyButton);
        
        noteCard.getChildren().add(studyButtons);
        
        // Add click handler for the entire card (opens readme-like view)
        noteCard.setOnMouseClicked(e -> {
            System.out.println("Card clicked - target: " + e.getTarget().getClass().getSimpleName());
            // Only handle card click if not clicking on buttons or button children
            if (e.getTarget() instanceof javafx.scene.Node && !isButtonOrChild((javafx.scene.Node) e.getTarget())) {
                System.out.println("Not a button - calling handleViewNote");
                handleViewNote(note);
            } else {
                System.out.println("Button clicked - not handling card click");
            }
        });
        
        // Add cursor style to indicate clickable
        noteCard.setStyle("-fx-cursor: hand;");
        
        return noteCard;
    }
    
    /**
     * Creates a new note
     */
    private void createNewNote() {
        NoteDialog dialog = new NoteDialog("Create New Note", null);
        dialog.showAndWait().ifPresent(note -> {
            dataStore.addNote(note);
            notesList.add(note);
            loadNotes();
            
            // Log activity
            dataStore.logUserActivity("NOTE_CREATED", "Created note: " + note.getTitle());
            
            // Refresh activity history and all views
            com.studyspace.components.SidebarView.refreshActivityHistoryGlobally();
            com.studyspace.components.SidebarView.refreshAllViewsGlobally();
            
            // Force refresh the notes view to show the new note immediately
            javafx.application.Platform.runLater(() -> {
                loadNotes();
            });
            
            // Show success notification
            SceneManager.getInstance().showInfoDialog("Note Created", 
                "Successfully created note: " + note.getTitle() + "\n\n" +
                "Your new note is now ready for study!");
        });
    }
    
    /**
     * Edits an existing note
     */
    private void editNote(Note note) {
        NoteDialog dialog = new NoteDialog("Edit Note", note);
        dialog.showAndWait().ifPresent(updatedNote -> {
            dataStore.updateNote(updatedNote);
            notesList.set(notesList.indexOf(note), updatedNote);
            loadNotes();
        });
    }
    
    /**
     * Handles studying a note
     */
    private void handleStudyNote(Note note) {
        System.out.println("=== handleStudyNote called ===");
        System.out.println("Note: " + note.getTitle());
        
        try {
            // Create a study view for the note
            NoteStudyView studyView = new NoteStudyView(note, this);
            System.out.println("Created NoteStudyView");
            
            // Replace current content with study view using StackPane
            mainContainer.getChildren().setAll(studyView.getView());
            System.out.println("StackPane children count: " + mainContainer.getChildren().size());
            System.out.println("=== handleStudyNote completed ===");
        } catch (Exception e) {
            System.err.println("Error in handleStudyNote: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Handles viewing a note in readme-like format
     */
    private void handleViewNote(Note note) {
        System.out.println("=== handleViewNote called ===");
        System.out.println("Note: " + note.getTitle());
        
        // Create a readme-like view for the note
        NoteStudyView readmeView = new NoteStudyView(note, this);
        System.out.println("Created NoteStudyView");
        
        // Replace current content with readme view using StackPane
        mainContainer.getChildren().setAll(readmeView.getView());
        System.out.println("StackPane children count: " + mainContainer.getChildren().size());
        System.out.println("=== handleViewNote completed ===");
    }
    
    
    /**
     * Deletes a note
     */
    private void deleteNote(Note note) {
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
     * Formats date for display
     */
    private String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "Unknown";
        }
        System.out.println("DEBUG: Formatting date: " + dateTime + " -> " + dateTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' HH:mm")));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' HH:mm");
        return dateTime.format(formatter);
    }
    
    /**
     * Helper method to check if a node is a button or child of a button
     */
    private boolean isButtonOrChild(javafx.scene.Node node) {
        javafx.scene.Node current = node;
        while (current != null) {
            if (current instanceof Button) {
                return true;
            }
            current = current.getParent();
        }
        return false;
    }
    
    /**
     * Shows the Import PDF for Notes dialog
     */
    private void showImportFlashcardDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Import Document for Notes");
        dialog.setHeaderText("Upload a document (PDF, Word, PowerPoint) to extract and organize content into structured notes.");
        
        // Set dialog properties for proper close behavior
        dialog.setResizable(true);
        
        // Create dialog content
        VBox content = new VBox();
        content.setSpacing(16);
        content.setPadding(new Insets(20));
        content.getStyleClass().add("dialog-content");
        
        // Description text
        Label descriptionLabel = new Label("Transform your documents (PDF, Word, PowerPoint) into organized study notes with automatic text extraction and formatting.");
        descriptionLabel.getStyleClass().add("dialog-description");
        descriptionLabel.setWrapText(true);
        
        // File selection area
        VBox fileSelectionArea = new VBox();
        fileSelectionArea.setSpacing(12);
        fileSelectionArea.setAlignment(Pos.CENTER);
        fileSelectionArea.getStyleClass().add("upload-area");
        fileSelectionArea.setPadding(new Insets(40));
        fileSelectionArea.setStyle("-fx-border-color: #d1d5db; -fx-border-style: dashed; -fx-border-width: 2; -fx-border-radius: 8;");
        
        Label fileIcon = new Label();
        fileIcon.setGraphic(IconUtils.createLargeIconView("file"));
        
        Label uploadText = new Label("Upload a document to generate organized notes automatically");
        uploadText.getStyleClass().add("upload-text");
        
        Button chooseFileButton = new Button();
        chooseFileButton.setGraphic(IconUtils.createIconTextHBox("upload", "Choose Document File"));
        chooseFileButton.getStyleClass().add("secondary-button");
        
        // Selected file display
        Label selectedFileLabel = new Label("No file selected");
        selectedFileLabel.getStyleClass().add("selected-file-label");
        selectedFileLabel.setVisible(false);
        
        fileSelectionArea.getChildren().addAll(fileIcon, uploadText, chooseFileButton, selectedFileLabel);
        
        content.getChildren().addAll(descriptionLabel, fileSelectionArea);
        
        // Set dialog content
        dialog.getDialogPane().setContent(content);
        
        // Create custom button types
        ButtonType importButtonType = new ButtonType("Import Notes", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        
        dialog.getDialogPane().getButtonTypes().addAll(importButtonType, cancelButtonType);
        
        // Style the buttons
        Button importButton = (Button) dialog.getDialogPane().lookupButton(importButtonType);
        Button cancelButton = (Button) dialog.getDialogPane().lookupButton(cancelButtonType);
        
        importButton.getStyleClass().add("success-button");
        cancelButton.getStyleClass().add("secondary-button");
        importButton.setDisable(true); // Initially disabled until file is selected
        
        // File selection handler
        chooseFileButton.setOnAction(e -> {
            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("Select Document File");
            fileChooser.getExtensionFilters().addAll(
                new javafx.stage.FileChooser.ExtensionFilter("PDF Files", "*.pdf"),
                new javafx.stage.FileChooser.ExtensionFilter("Word Documents", "*.doc", "*.docx"),
                new javafx.stage.FileChooser.ExtensionFilter("PowerPoint Presentations", "*.ppt", "*.pptx"),
                new javafx.stage.FileChooser.ExtensionFilter("All Supported Files", "*.pdf", "*.doc", "*.docx", "*.ppt", "*.pptx")
            );
            
            javafx.stage.Window window = dialog.getDialogPane().getScene().getWindow();
            java.io.File selectedFile = fileChooser.showOpenDialog(window);
            
            if (selectedFile != null) {
                selectedFileLabel.setText("Selected: " + selectedFile.getName());
                selectedFileLabel.setVisible(true);
                importButton.setDisable(false);
                
                // Store the selected file for processing
                chooseFileButton.setUserData(selectedFile);
            }
        });
        
        // Handle button actions
        dialog.setResultConverter(buttonType -> {
            if (buttonType == importButtonType) {
                java.io.File selectedFile = (java.io.File) chooseFileButton.getUserData();
                if (selectedFile != null) {
                    processPDFForNotes(selectedFile);
                }
            }
            return buttonType;
        });
        
        // Handle window close (X button)
        dialog.getDialogPane().getScene().getWindow().setOnCloseRequest(e -> {
            dialog.setResult(cancelButtonType);
            dialog.close();
        });
        
        dialog.showAndWait();
    }
    
    /**
     * Processes the selected PDF file and creates notes
     */
    private void processPDFForNotes(java.io.File pdfFile) {
        try {
            // Check if file type is supported
            String fileName = pdfFile.getName().toLowerCase();
            if (!fileName.endsWith(".pdf") && !fileName.endsWith(".doc") && 
                !fileName.endsWith(".docx") && !fileName.endsWith(".ppt") && 
                !fileName.endsWith(".pptx")) {
                SceneManager.getInstance().showErrorDialog("Unsupported File Type", 
                    "Please select a PDF, Word document (.doc/.docx), or PowerPoint presentation (.ppt/.pptx) file.");
                return;
            }
            
            // Show simple notification like quiz generation
            SceneManager.getInstance().showInfoDialog("Creating Notes", 
                "Creating notes from your document... We will notify you directly as we compile your notes.");
            
            // Process document with AI service in background
            javafx.concurrent.Task<com.studyspace.utils.DocumentProcessingService.DocumentProcessingResult> task = 
                new javafx.concurrent.Task<com.studyspace.utils.DocumentProcessingService.DocumentProcessingResult>() {
                @Override
                protected com.studyspace.utils.DocumentProcessingService.DocumentProcessingResult call() throws Exception {
                    com.studyspace.utils.DocumentProcessingService service = 
                        com.studyspace.utils.DocumentProcessingService.getInstance();
                    return service.processDocument(pdfFile, "notes");
                }
            };
            
            task.setOnSucceeded(e -> {
                com.studyspace.utils.DocumentProcessingService.DocumentProcessingResult result = task.getValue();
                
                if (result.isSuccess()) {
                    // Refresh the notes list to show the new AI-generated content
                    notesList.clear();
                    notesList.addAll(dataStore.getNotes());
                    loadNotes();
                    
                    // Show success message with details
                    String successMessage = String.format(
                        "✅ AI Processing Complete!\n\n" +
                        "📝 Note: %s\n" +
                        "📖 Subject: %s\n" +
                        "📊 Difficulty: %s\n\n" +
                        "Summary: %s\n\n" +
                        "Your AI-generated note is now available in the Notes section!",
                        result.getNoteTitle(),
                        result.getSubject(),
                        result.getDifficulty(),
                        result.getSummary()
                    );
                    
                    SceneManager.getInstance().showInfoDialog("AI Processing Complete", successMessage);
                    
                    // Force refresh all views to show any new flashcards or decks created
                    com.studyspace.components.SidebarView.refreshAllViewsGlobally();
                    
                    // Force refresh the notes view to show the new notes immediately
                    javafx.application.Platform.runLater(() -> {
                        notesList.clear();
                        notesList.addAll(dataStore.getNotes());
                        loadNotes();
                    });
                } else {
                    SceneManager.getInstance().showErrorDialog("AI Processing Failed", 
                        "Failed to process document: " + result.getMessage());
                }
            });
            
            task.setOnFailed(e -> {
                SceneManager.getInstance().showErrorDialog("AI Processing Error", 
                    "An error occurred while processing the document: " + task.getException().getMessage());
            });
            
            // Start the task in a background thread
            Thread processingThread = new Thread(task);
            processingThread.setDaemon(true);
            processingThread.start();
            
        } catch (Exception e) {
            SceneManager.getInstance().showErrorDialog("Import Error", 
                "Failed to process PDF file: " + e.getMessage());
        }
    }
    
    /**
     * Creates sample notes from document processing (DEPRECATED - replaced with AI processing)
     */
    @SuppressWarnings("unused")
    private void createNotesFromPDF(String fileName) {
        try {
            // Create sample notes based on document processing
            String baseTitle = fileName.replaceAll("\\.(pdf|doc|docx|ppt|pptx)$", "").replace("_", " ");
            String fileType = getFileType(fileName);
            
            // Check for existing notes with the same base title to prevent duplicates
            String keyConceptsTitle = baseTitle + " - Key Concepts";
            String summaryTitle = baseTitle + " - Summary";
            String definitionsTitle = baseTitle + " - Important Definitions";
            
            // Check if notes already exist
            boolean keyConceptsExists = dataStore.getNotes().stream()
                .anyMatch(note -> note.getTitle().equals(keyConceptsTitle));
            boolean summaryExists = dataStore.getNotes().stream()
                .anyMatch(note -> note.getTitle().equals(summaryTitle));
            boolean definitionsExists = dataStore.getNotes().stream()
                .anyMatch(note -> note.getTitle().equals(definitionsTitle));
            
            if (keyConceptsExists || summaryExists || definitionsExists) {
                SceneManager.getInstance().showInfoDialog("Import Skipped", 
                    "Notes from this document already exist. No duplicates were created.");
                return;
            }
            
            // Note 1: Key Concepts
            LocalDateTime importTime = LocalDateTime.now();
            Note keyConceptsNote = new Note(
                keyConceptsTitle,
                "Extracted from " + fileType + " content",
                "## Key Concepts from " + fileName + "\n\n" +
                "### Main Topics Covered:\n" +
                "• Primary concept identified from document analysis\n" +
                "• Secondary topics and supporting information\n" +
                "• Important definitions and terminology\n" +
                "• Core principles and methodologies\n\n" +
                "### Important Points:\n" +
                "• Key insight 1: Detailed explanation of the first major concept\n" +
                "• Key insight 2: Analysis of the second important topic\n" +
                "• Key insight 3: Summary of the third critical element\n\n" +
                "### Study Notes:\n" +
                "This content was automatically extracted and organized from your " + fileType + " document. " +
                "Review and edit these notes to personalize them for your study needs.",
                importTime,
                importTime
            );
            keyConceptsNote.setTags(java.util.Arrays.asList("document-import", "key-concepts", "study-notes"));
            dataStore.addNote(keyConceptsNote);
            
            // Note 2: Summary
            Note summaryNote = new Note(
                summaryTitle,
                "Document Summary",
                "## Document Summary\n\n" +
                "### Overview:\n" +
                "This " + fileType + " document covers the main topics and concepts extracted from " + fileName + ". " +
                "The content has been organized into digestible sections for effective study.\n\n" +
                "### Main Sections:\n" +
                "1. **Introduction**: Overview of the topic and its importance\n" +
                "2. **Core Content**: Detailed explanation of main concepts\n" +
                "3. **Examples**: Practical applications and case studies\n" +
                "4. **Conclusion**: Key takeaways and summary points\n\n" +
                "### Study Tips:\n" +
                "• Focus on understanding the main concepts first\n" +
                "• Create flashcards for key definitions\n" +
                "• Practice with the examples provided\n" +
                "• Review the conclusion for key takeaways",
                importTime,
                importTime
            );
            summaryNote.setTags(java.util.Arrays.asList("document-import", "summary", "overview"));
            dataStore.addNote(summaryNote);
            
            // Note 3: Important Definitions
            Note definitionsNote = new Note(
                definitionsTitle,
                "Terminology",
                "## Important Definitions\n\n" +
                "### Key Terms:\n\n" +
                "**Term 1**: Definition extracted from the document with context and examples.\n\n" +
                "**Term 2**: Comprehensive explanation including usage and related concepts.\n\n" +
                "**Term 3**: Detailed definition with practical applications and significance.\n\n" +
                "### Related Concepts:\n" +
                "• Concept A: Brief explanation and relationship to main topics\n" +
                "• Concept B: Key details and importance in the overall context\n" +
                "• Concept C: Additional information and connections\n\n" +
                "### Study Strategy:\n" +
                "Create flashcards for each definition and practice recalling them regularly.",
                importTime,
                importTime
            );
            definitionsNote.setTags(java.util.Arrays.asList("document-import", "definitions", "terminology"));
            dataStore.addNote(definitionsNote);
            
            // Refresh the notes list
            notesList.clear();
            notesList.addAll(dataStore.getNotes());
            
            // Refresh the UI to show new notes immediately
            loadNotes();
            
            // Log activity for activity history
            dataStore.logUserActivity("NOTE_CREATED", "Imported 3 notes from " + fileType + " document: " + fileName);
            
            // Refresh activity history and all views to show the new activity
            com.studyspace.components.SidebarView.refreshActivityHistoryGlobally();
            com.studyspace.components.SidebarView.refreshAllViewsGlobally();
            
            // Show success message
            SceneManager.getInstance().showInfoDialog("Import Successful!", 
                "Successfully created 3 organized notes from your " + fileType + " document:\n\n" +
                "• " + keyConceptsNote.getTitle() + "\n" +
                "• " + summaryNote.getTitle() + "\n" +
                "• " + definitionsNote.getTitle() + "\n\n" +
                "Your notes are now ready for study!");
                
        } catch (Exception e) {
            SceneManager.getInstance().showErrorDialog("Import Error", 
                "Failed to create notes from document: " + e.getMessage());
        }
    }
    
    /**
     * Gets the file type description based on file extension
     */
    private String getFileType(String fileName) {
        String extension = fileName.toLowerCase();
        if (extension.endsWith(".pdf")) {
            return "PDF";
        } else if (extension.endsWith(".doc") || extension.endsWith(".docx")) {
            return "Word";
        } else if (extension.endsWith(".ppt") || extension.endsWith(".pptx")) {
            return "PowerPoint";
        } else {
            return "document";
        }
    }
    
    
    /**
     * Returns to the notes list view from readme/study view
     */
    public void returnToNotesList() {
        // Restore the original content container
        mainContainer.getChildren().setAll(contentContainer);
    }
    
    /**
     * Refreshes the notes list (useful when returning from other views)
     */
    public void refresh() {
        System.out.println("NotesView refresh called");
        javafx.application.Platform.runLater(() -> {
            notesList.clear();
            notesList.addAll(dataStore.getNotes());
            loadNotes();
        });
    }
    
    /**
     * Gets the main view
     */
    public StackPane getView() {
        return mainContainer;
    }
}
