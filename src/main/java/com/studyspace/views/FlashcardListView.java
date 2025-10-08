package com.studyspace.views;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.studyspace.models.Flashcard;
import com.studyspace.models.FlashcardDeck;
import com.studyspace.models.Note;
import com.studyspace.utils.DataStore;
import com.studyspace.utils.IconUtils;
import com.studyspace.utils.QuizGenerationService;
import com.studyspace.utils.SceneManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

//============ flashcard list view =============
//this is where flashcard decks are displayed and managed
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.*;
import javafx.scene.Parent;


/**
 * FlashcardListView - Displays all flashcard decks with search and filtering
 */
public class FlashcardListView {
    
    private final DataStore dataStore;
    private final SceneManager sceneManager;
    
    private VBox mainContainer;
    private TextField searchField;
    private VBox decksContainer;
    private ScrollPane scrollPane;
    private ObservableList<FlashcardDeck> decksList;
    private FilteredList<FlashcardDeck> filteredDecks;
    private SortedList<FlashcardDeck> sortedDecks;
    
    public FlashcardListView() {
        this.dataStore = DataStore.getInstance();
        this.sceneManager = SceneManager.getInstance();
        
        initializeData();
        initializeUI();
        loadFlashcardDecks();
    }
    
    /**
     * Initialize data and filtering
     */
    private void initializeData() {
        decksList = FXCollections.observableArrayList(dataStore.getAllFlashcardDecks());
        filteredDecks = new FilteredList<>(decksList);
        sortedDecks = new SortedList<>(filteredDecks);
    }
    
    /**
     * Initializes the UI components
     */
    private void initializeUI() {
        mainContainer = new VBox();
        mainContainer.getStyleClass().add("flashcards-main-container");
        mainContainer.setSpacing(24);
        mainContainer.setPadding(new Insets(32));
        
        // Header section
        VBox headerSection = createHeaderSection();
        
        // Search and filter section
        HBox searchFilterSection = createSearchFilterSection();
        
        // Decks container
        decksContainer = new VBox();
        decksContainer.getStyleClass().add("flashcards-container");
        decksContainer.setSpacing(16);
        
        // Create scroll pane for decks
        scrollPane = new ScrollPane(decksContainer);
        scrollPane.getStyleClass().add("flashcards-scroll-pane");
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        
        mainContainer.getChildren().addAll(headerSection, searchFilterSection, scrollPane);
        
        // Setup event handlers
        setupEventHandlers();
    }
    
    /**
     * Creates the header section with title and action buttons
     */
    private VBox createHeaderSection() {
        VBox headerSection = new VBox();
        headerSection.getStyleClass().add("flashcards-header-section");
        headerSection.setSpacing(12);
        
        // Title and subtitle
        HBox titleRow = new HBox();
        titleRow.setAlignment(Pos.CENTER_LEFT);
        titleRow.setSpacing(12);
        
        Label titleIcon = new Label();
        titleIcon.setGraphic(IconUtils.createMediumIconView("cards"));
        
        VBox titleSection = new VBox();
        titleSection.setSpacing(4);
        
        Label titleLabel = new Label("Flashcards");
        titleLabel.getStyleClass().add("flashcards-title");
        
        Label subtitleLabel = new Label("Create and study with flashcard decks");
        subtitleLabel.getStyleClass().add("flashcards-subtitle");
        
        titleSection.getChildren().addAll(titleLabel, subtitleLabel);
        titleRow.getChildren().addAll(titleIcon, titleSection);
        
        // Action buttons
        HBox actionButtons = new HBox();
        actionButtons.setSpacing(12);
        actionButtons.setAlignment(Pos.CENTER_RIGHT);
        
        Button importFlashcardButton = new Button();
        importFlashcardButton.setGraphic(IconUtils.createIconTextHBox("upload", "Import PDF for Deck"));
        importFlashcardButton.getStyleClass().add("secondary-button");
        importFlashcardButton.setOnAction(e -> showImportFlashcardDialog());
        
        Button createFromNotesButton = new Button();
        createFromNotesButton.setGraphic(IconUtils.createIconTextHBox("book", "Create from Notes"));
        createFromNotesButton.getStyleClass().add("info-button");
        createFromNotesButton.setOnAction(e -> showCreateFlashcardsFromNotesDialog());
        
        Button createDeckButton = new Button();
        createDeckButton.setGraphic(IconUtils.createIconTextHBox("add", "Create Deck"));
        createDeckButton.getStyleClass().add("success-button");
        createDeckButton.setOnAction(e -> handleCreateNewDeck());
        
        actionButtons.getChildren().addAll(importFlashcardButton, createFromNotesButton, createDeckButton);
        
        // Header layout
        HBox headerLayout = new HBox();
        headerLayout.setAlignment(Pos.CENTER_LEFT);
        headerLayout.setSpacing(16);
        HBox.setHgrow(titleRow, Priority.ALWAYS);
        headerLayout.getChildren().addAll(titleRow, actionButtons);
        
        headerSection.getChildren().add(headerLayout);
        return headerSection;
    }
    
    /**
     * Creates the search and filter section
     */
    private HBox createSearchFilterSection() {
        HBox searchFilterSection = new HBox();
        searchFilterSection.getStyleClass().add("search-filter-section");
        searchFilterSection.setSpacing(16);
        searchFilterSection.setAlignment(Pos.CENTER_LEFT);
        
        // Search field
        HBox searchBox = new HBox();
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setSpacing(8);
        searchBox.getStyleClass().add("search-box");
        
        Label searchIcon = new Label();
        searchIcon.setGraphic(IconUtils.createSmallIconView("search"));
        
        searchField = new TextField();
        searchField.getStyleClass().add("search-field");
        searchField.setPromptText("Search flashcard decks...");
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
            "Difficulty (Easy to Hard)",
            "Difficulty (Hard to Easy)",
            "Card Count (Low to High)",
            "Card Count (High to Low)"
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
            // Search functionality with null checks and error handling
            if (searchField != null) {
                searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                    try {
                        filteredDecks.setPredicate(deck -> {
                            if (newValue == null || newValue.isEmpty()) {
                                return true;
                            }
                            String lowerCaseFilter = newValue.toLowerCase();
                            return deck.getTitle().toLowerCase().contains(lowerCaseFilter) ||
                                   deck.getDescription().toLowerCase().contains(lowerCaseFilter) ||
                                   deck.getSubject().toLowerCase().contains(lowerCaseFilter);
                        });

                        // Reload decks to show filtered results
                        loadFlashcardDecks();
                    } catch (Exception ex) {
                        System.err.println("Error in search filter: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                });
            }

            // Setup filter button functionality
            setupFilterButtons();
        } catch (Exception e) {
            System.err.println("Error setting up event handlers: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Sets up filter button functionality
     */
    private void setupFilterButtons() {
        // These are placeholders for future functionality
        // For now, they just show info dialogs
    }
    
    /**
     * Loads all flashcard decks from the data store
     */
    private void loadFlashcardDecks() {
        decksContainer.getChildren().clear();
        
        if (sortedDecks.isEmpty()) {
            showEmptyState();
        } else {
            // Create a grid layout for deck cards
            FlowPane decksGrid = new FlowPane();
            decksGrid.getStyleClass().add("decks-grid");
            decksGrid.setHgap(16);
            decksGrid.setVgap(16);
            decksGrid.setAlignment(Pos.TOP_LEFT);
            decksGrid.setPrefWrapLength(1200); // Allow wrapping at 1200px width
            
            for (FlashcardDeck deck : sortedDecks) {
                VBox deckCard = createDeckCard(deck);
                decksGrid.getChildren().add(deckCard);
            }
            
            decksContainer.getChildren().add(decksGrid);
        }
    }
    
    /**
     * Creates a flashcard deck card
     */
    private VBox createDeckCard(FlashcardDeck deck) {
        VBox card = new VBox();
        card.getStyleClass().add("flashcard-deck-card");
        card.setSpacing(16);
        card.setPrefWidth(300);
        card.setMinWidth(280);
        card.setMaxWidth(320);
        card.setMinHeight(280);
        card.setMaxHeight(320); // Fixed maximum height
        card.setPrefHeight(300); // Fixed preferred height
        
        // Add hover effect for better visual feedback
        card.setOnMouseEntered(e -> {
            card.getStyleClass().add("flashcard-deck-card-hover");
        });
        
        card.setOnMouseExited(e -> {
            card.getStyleClass().remove("flashcard-deck-card-hover");
        });
        
        // Header with title and action buttons
        HBox cardHeader = new HBox();
        cardHeader.setAlignment(Pos.CENTER_LEFT);
        cardHeader.setSpacing(12);
        
        VBox titleSection = new VBox();
        titleSection.setSpacing(4);
        
        Label titleLabel = new Label(deck.getTitle());
        titleLabel.getStyleClass().add("deck-title");
        titleLabel.setWrapText(true);
        titleLabel.setMaxHeight(40); // Limit title height
        titleLabel.setMaxWidth(250); // Limit title width
        
        titleSection.getChildren().add(titleLabel);

        // Action buttons (edit and delete)
        HBox actionButtons = new HBox();
        actionButtons.setSpacing(8);
        
        Button editButton = new Button();
        editButton.setGraphic(IconUtils.createSmallIconView("edit"));
        editButton.getStyleClass().addAll("action-button", "edit");
        editButton.setOnAction(e -> {
            e.consume(); // Prevent event bubbling to the card
            handleEditDeck(deck);
        });
        editButton.setOnMouseClicked(e -> {
            e.consume(); // Prevent event bubbling to the card
            handleEditDeck(deck);
        });
        
        Button deleteButton = new Button();
        deleteButton.setGraphic(IconUtils.createSmallIconView("trash"));
        deleteButton.getStyleClass().addAll("action-button", "delete");
        deleteButton.setOnAction(e -> {
            e.consume(); // Prevent event bubbling to the card
            handleDeleteDeck(deck);
        });
        deleteButton.setOnMouseClicked(e -> {
            e.consume(); // Prevent event bubbling to the card
            handleDeleteDeck(deck);
        });
        
        actionButtons.getChildren().addAll(editButton, deleteButton);
        
        cardHeader.getChildren().addAll(titleSection, actionButtons);
        
        // Difficulty badge
        Label difficultyBadge = new Label(deck.getDifficulty().getDisplayName());
        difficultyBadge.getStyleClass().addAll("difficulty-badge", 
            deck.getDifficulty().name().toLowerCase());
        
        // Subject badge
        Label subjectBadge = new Label(deck.getSubject());
        subjectBadge.getStyleClass().add("subject-badge");
        
        // Description
        Label descriptionLabel = new Label(deck.getDescription());
        descriptionLabel.getStyleClass().add("deck-description");
        descriptionLabel.setWrapText(true);
        descriptionLabel.setMaxHeight(60); // Slightly increased but still limited
        descriptionLabel.setMaxWidth(280); // Limit description width
        
        // Card count
        Label cardCountLabel = new Label(deck.getCardCount() + " cards");
        cardCountLabel.getStyleClass().add("card-count");
        
        // Date information
        HBox dateInfo = new HBox();
        dateInfo.setSpacing(8);
        dateInfo.setAlignment(Pos.CENTER_LEFT);
        
        HBox dateContent = IconUtils.createIconTextHBox("clock", formatDate(deck.getCreatedAt()));
        dateContent.getStyleClass().add("date-info");
        
        dateInfo.getChildren().add(dateContent);
        
        card.getChildren().addAll(cardHeader, difficultyBadge, subjectBadge, 
            descriptionLabel, cardCountLabel, dateInfo);
        
        // Study button
        HBox studyButtons = new HBox();
        studyButtons.setSpacing(8);
        studyButtons.setAlignment(Pos.CENTER_LEFT);
        
        Button studyButton = new Button();
        studyButton.setGraphic(IconUtils.createIconTextHBox("book", "Study"));
        studyButton.getStyleClass().add("success-button");
        studyButton.setOnAction(e -> {
            e.consume(); // Prevent event bubbling to the card
            handleStudyDeck(deck);
        });
        studyButton.setOnMouseClicked(e -> {
            e.consume(); // Prevent event bubbling to the card
            handleStudyDeck(deck);
        });
        studyButton.setDisable(deck.getCardCount() == 0);
        
        studyButtons.getChildren().add(studyButton);
        
        card.getChildren().add(studyButtons);
        
        // Add click handler for the entire card (opens management view)
        card.setOnMouseClicked(e -> handleManageDeck(deck));
        
        // Add cursor style to indicate clickable
        card.setStyle("-fx-cursor: hand;");
        
        return card;
    }
    
    /**
     * Shows empty state when no decks exist
     */
    private void showEmptyState() {
        VBox emptyState = new VBox();
        emptyState.getStyleClass().add("empty-state");
        emptyState.setAlignment(Pos.CENTER);
        emptyState.setSpacing(16);
        emptyState.setPadding(new Insets(48));
        
        Label emptyIcon = new Label();
        emptyIcon.setGraphic(IconUtils.createLargeIconView("book"));
        emptyIcon.getStyleClass().add("empty-icon");
        
        Label emptyTitle = new Label("No flashcard decks yet");
        emptyTitle.getStyleClass().add("empty-title");
        
        Label emptyDescription = new Label("Create your first flashcard deck to start studying!");
        emptyDescription.getStyleClass().add("empty-description");
        
        Button createFirstDeckButton = new Button();
        createFirstDeckButton.setGraphic(IconUtils.createIconView("book"));
        createFirstDeckButton.setText(" Create Your First Deck");
        createFirstDeckButton.getStyleClass().add("primary-button");
        createFirstDeckButton.setOnAction(e -> handleCreateNewDeck());
        
        emptyState.getChildren().addAll(emptyIcon, emptyTitle, emptyDescription, createFirstDeckButton);
        decksContainer.getChildren().add(emptyState);
    }
    
    /**
     * Formats date for display
     */
    private String formatDate(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
        return dateTime.format(formatter);
    }
    
    /**
     * Handles managing a flashcard deck (edit cards, add/remove)
     */
    private void handleManageDeck(FlashcardDeck deck) {
        FlashcardDeckManagementView managementView = new FlashcardDeckManagementView(deck, this);

        if (mainContainer.getParent() instanceof StackPane parent) {
            parent.getChildren().setAll(managementView.getView());
        }
    }
    
    /**
     * Handles studying a flashcard deck
     */
    private void handleStudyDeck(FlashcardDeck deck) {
        if (deck.getCardCount() == 0) {
            sceneManager.showInfoDialog("Empty Deck",
                "This deck doesn't have any flashcards yet. Add some cards first!");
            return;
        }

        // Create and show flashcard study view
        FlashcardStudyView studyView = new FlashcardStudyView(deck, this);

        // Replace current content with study view
        Parent parent = mainContainer.getParent();
        if (parent != null) {
            if (parent instanceof VBox) {
                ((VBox) parent).getChildren().clear();
                ((VBox) parent).getChildren().add(studyView.getView());
            } else if (parent instanceof StackPane) {
                ((StackPane) parent).getChildren().clear();
                ((StackPane) parent).getChildren().add(studyView.getView());
            }
        }
    }
    
    /**
     * Handles editing a flashcard deck
     */
    private void handleEditDeck(FlashcardDeck deck) {
        // Create a dialog for editing deck details
        Dialog<FlashcardDeck> dialog = new Dialog<>();
        dialog.setTitle("Edit Deck Details");
        dialog.setHeaderText("Edit deck information");
        
        // Create form content
        VBox content = new VBox();
        content.setSpacing(16);
        content.setPadding(new Insets(20));
        
        // Title field
        Label titleLabel = new Label("Deck Title:");
        TextField titleField = new TextField(deck.getTitle());
        titleField.setPromptText("Enter deck title");
        
        // Description field
        Label descriptionLabel = new Label("Description:");
        TextArea descriptionArea = new TextArea(deck.getDescription());
        descriptionArea.setPromptText("Enter deck description");
        descriptionArea.setPrefRowCount(3);
        descriptionArea.setWrapText(true);
        
        // Subject field
        Label subjectLabel = new Label("Subject:");
        TextField subjectField = new TextField(deck.getSubject());
        subjectField.setPromptText("Enter subject (e.g., JavaScript, Math, etc.)");
        
        // Difficulty selection
        Label difficultyLabel = new Label("Difficulty:");
        ComboBox<Flashcard.Difficulty> difficultyCombo = new ComboBox<>();
        difficultyCombo.getItems().addAll(Flashcard.Difficulty.values());
        difficultyCombo.setValue(deck.getDifficulty());
        
        content.getChildren().addAll(
            titleLabel, titleField,
            descriptionLabel, descriptionArea,
            subjectLabel, subjectField,
            difficultyLabel, difficultyCombo
        );
        
        dialog.getDialogPane().setContent(content);
        
        // Add buttons
        ButtonType saveButtonType = new ButtonType("Save Changes", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);
        
        // Set result converter
        dialog.setResultConverter(buttonType -> {
            if (buttonType == saveButtonType) {
                // Validate input
                if (titleField.getText().trim().isEmpty()) {
                    sceneManager.showErrorDialog("Invalid Input", "Deck title cannot be empty.");
                    return null;
                }
                
                // Update deck with new values
                deck.setTitle(titleField.getText().trim());
                deck.setDescription(descriptionArea.getText().trim());
                deck.setSubject(subjectField.getText().trim());
                deck.setDifficulty(difficultyCombo.getValue());
                
                // Save to data store
                dataStore.saveFlashcardDeck(deck);
                
                // Log activity
                dataStore.logUserActivity("FLASHCARD_DECK_CREATED", "Updated deck: " + deck.getTitle());
                
                return deck;
            }
            return null;
        });
        
        // Show dialog and handle result
        dialog.showAndWait().ifPresent(updatedDeck -> {
            if (updatedDeck != null) {
                // Refresh the deck list to show updated information
                loadFlashcardDecks();
                sceneManager.showInfoDialog("Deck Updated", 
                    "Deck details have been successfully updated!");
            }
        });
    }
    
    /**
     * Handles deleting a flashcard deck
     */
    private void handleDeleteDeck(FlashcardDeck deck) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Delete Deck");
        confirmDialog.setHeaderText("Are you sure you want to delete this deck?");
        confirmDialog.setContentText("Deck: " + deck.getTitle() + "\nCards: " + deck.getCardCount() + "\n\nThis action cannot be undone.");
        
        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    dataStore.deleteFlashcardDeck(deck.getId());
                    decksList.remove(deck);
                    loadFlashcardDecks();
                    sceneManager.showInfoDialog("Deck Deleted", 
                        "The flashcard deck '" + deck.getTitle() + "' has been successfully deleted.");
                } catch (Exception e) {
                    sceneManager.showErrorDialog("Error", "Failed to delete deck: " + e.getMessage());
                }
            }
        });
    }
    
    /**
     * Handles creating a new flashcard deck
     */
    private void handleCreateNewDeck() {
        // Create a dialog for deck creation
        Dialog<FlashcardDeck> dialog = new Dialog<>();
        dialog.setTitle("Create New Flashcard Deck");
        dialog.setHeaderText("Create a new flashcard deck to start studying");

        // Create dialog content
        VBox content = createDeckCreationForm();
        dialog.getDialogPane().setContent(content);

        // Add buttons
        ButtonType createButtonType = new ButtonType("Create Deck", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, cancelButtonType);

        // Set result converter
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                return createDeckFromForm();
            }
            return null;
        });

        // Show dialog and handle result
        dialog.showAndWait().ifPresent(newDeck -> {
            if (newDeck != null) {
                dataStore.saveFlashcardDeck(newDeck);
                decksList.add(newDeck);
                loadFlashcardDecks();
                
                // Log activity
                dataStore.logUserActivity("FLASHCARD_DECK_CREATED", "Created flashcard deck: " + newDeck.getTitle());
                
                // Refresh activity history and all views
                com.studyspace.components.SidebarView.refreshActivityHistoryGlobally();
                com.studyspace.components.SidebarView.refreshAllViewsGlobally();
            }
        });
    }
    
    /**
     * Shows the Import PDF for Flashcards dialog
     */
    private void showImportFlashcardDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Import Document for Flashcards");
        dialog.setHeaderText("Upload a document (PDF, Word, PowerPoint) to automatically generate interactive flashcard decks from its content.");
        
        // Set dialog properties for proper close behavior
        dialog.setResizable(true);
        
        // Create dialog content
        VBox content = new VBox();
        content.setSpacing(16);
        content.setPadding(new Insets(20));
        content.getStyleClass().add("dialog-content");
        
        // Description text
        Label descriptionLabel = new Label("Create engaging flashcard decks by extracting key concepts, definitions, and important information from your documents (PDF, Word, PowerPoint). Perfect for active recall and spaced repetition study methods.");
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
        
        Label uploadText = new Label("Upload a document to generate interactive flashcards automatically");
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
        ButtonType importButtonType = new ButtonType("Import PDF for Deck", ButtonBar.ButtonData.OK_DONE);
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
                    processPDFForFlashcards(selectedFile);
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
     * Shows dialog to create flashcards from notes
     */
    private void showCreateFlashcardsFromNotesDialog() {
        // Get all available notes
        List<Note> availableNotes = new ArrayList<>(dataStore.getNotes());
        
        if (availableNotes.isEmpty()) {
            sceneManager.showInfoDialog("No Notes Available", 
                "You don't have any notes yet. Create some notes first before generating flashcards.");
            return;
        }
        
        // Create dialog for note selection and deck creation
        Dialog<FlashcardDeck> dialog = new Dialog<>();
        dialog.setTitle("Create Flashcards from Notes");
        dialog.setHeaderText("Select notes to generate flashcards from and create a new deck");
        
        // Create content
        VBox content = new VBox();
        content.setSpacing(16);
        content.setPadding(new Insets(20));
        
        Label instructionLabel = new Label("Select the notes you want to generate flashcards from:");
        instructionLabel.getStyleClass().addAll("text-sm", "text-secondary");
        
        // Create list view for note selection
        javafx.scene.control.ListView<Note> noteListView = new javafx.scene.control.ListView<>();
        noteListView.getItems().addAll(availableNotes);
        noteListView.setPrefHeight(200);
        noteListView.setCellFactory(listView -> new javafx.scene.control.ListCell<Note>() {
            @Override
            protected void updateItem(Note note, boolean empty) {
                super.updateItem(note, empty);
                if (empty || note == null) {
                    setText(null);
                } else {
                    setText(note.getTitle() + " - " + note.getSubject());
                }
            }
        });
        
        // Allow multiple selection
        noteListView.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE);
        
        // Deck creation form
        VBox deckForm = createDeckCreationForm();
        
        content.getChildren().addAll(instructionLabel, noteListView, deckForm);
        dialog.getDialogPane().setContent(content);
        
        // Add buttons
        ButtonType createButtonType = new ButtonType("Create Deck from Notes", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, cancelButtonType);
        
        // Set result converter
        dialog.setResultConverter(buttonType -> {
            if (buttonType == createButtonType) {
                List<Note> selectedNotes = new ArrayList<>(noteListView.getSelectionModel().getSelectedItems());
                if (selectedNotes.isEmpty()) {
                    sceneManager.showErrorDialog("No Notes Selected", "Please select at least one note to generate flashcards from.");
                    return null;
                }
                return createFlashcardsFromNotes(selectedNotes);
            }
            return null;
        });
        
        // Show dialog and handle result
        dialog.showAndWait().ifPresent(newDeck -> {
            if (newDeck != null) {
                // The deck will be created and populated asynchronously
                // It will be added to the list and saved when AI processing completes
                // No need to add it here since it's not saved yet
            }
        });
    }
    
    /**
     * Creates flashcards from selected notes using AI
     */
    private FlashcardDeck createFlashcardsFromNotes(List<Note> selectedNotes) {
        try {
            // Get form data
            String deckTitle = getDeckTitleFromForm();
            String deckSubject = getDeckSubjectFromForm();
            String deckDescription = getDeckDescriptionFromForm();
            Flashcard.Difficulty deckDifficulty = getDeckDifficultyFromForm();
            
            if (deckTitle.trim().isEmpty()) {
                sceneManager.showErrorDialog("Invalid Input", "Deck title cannot be empty.");
                return null;
            }
            
            // Show simple notification like quiz generation
            sceneManager.showInfoDialog("Creating Flashcards", 
                "Creating flashcards from your notes... We will notify you directly as we compile your flashcards.");
            
            // Create new deck
            FlashcardDeck newDeck = new FlashcardDeck(deckTitle, deckDescription, deckSubject, deckDifficulty);
            
            // Use AI to generate flashcards from notes in background thread
            javafx.concurrent.Task<QuizGenerationService.FlashcardGenerationResult> task = 
                new javafx.concurrent.Task<QuizGenerationService.FlashcardGenerationResult>() {
                @Override
                protected QuizGenerationService.FlashcardGenerationResult call() throws Exception {
                    QuizGenerationService quizGenerationService = new QuizGenerationService();
                    return quizGenerationService.generateFlashcardsFromNotes(selectedNotes, deckTitle, deckSubject, deckDifficulty);
                }
            };
            
            task.setOnSucceeded(e -> {
                QuizGenerationService.FlashcardGenerationResult result = task.getValue();
                
                System.out.println("=== Flashcard Generation Result ===");
                System.out.println("Success: " + result.isSuccess());
                System.out.println("Message: " + result.getMessage());
                System.out.println("Flashcards count: " + (result.getFlashcards() != null ? result.getFlashcards().size() : "null"));
                
                if (result.isSuccess() && result.getFlashcards() != null) {
                    // Add AI-generated flashcards to the deck
                    for (Flashcard flashcard : result.getFlashcards()) {
                        newDeck.addFlashcard(flashcard);
                    }
                    
                    // Save the deck to the data store (THIS WAS MISSING!)
                    System.out.println("Saving deck to data store: " + newDeck.getTitle() + " with " + newDeck.getCardCount() + " cards");
                    dataStore.saveFlashcardDeck(newDeck);
                    System.out.println("Deck saved successfully!");
                    
                    // Update the local deck list to include the new deck
                    decksList.clear();
                    decksList.addAll(dataStore.getAllFlashcardDecks());
                    System.out.println("Updated local deck list. Total decks: " + decksList.size());
                    
                    // Refresh the deck list
                    loadFlashcardDecks();
                    
                    // Log activity
                    dataStore.logUserActivity("FLASHCARD_DECK_CREATED", 
                        "Created deck '" + newDeck.getTitle() + "' with " + result.getFlashcards().size() + " AI-generated flashcards from " + selectedNotes.size() + " notes");
                    
                    // Show success message
                    sceneManager.showInfoDialog("Flashcards Created Successfully", 
                        "Created " + result.getFlashcards().size() + " flashcards from your notes!");
                    
                    // Refresh activity history and all views
                    com.studyspace.components.SidebarView.refreshActivityHistoryGlobally();
                    com.studyspace.components.SidebarView.refreshAllViewsGlobally();
                } else {
                    sceneManager.showErrorDialog("AI Generation Failed", 
                        "Failed to generate flashcards: " + result.getMessage());
                }
            });
            
            task.setOnFailed(e -> {
                sceneManager.showErrorDialog("AI Generation Error", 
                    "An error occurred while generating flashcards: " + task.getException().getMessage());
            });
            
            // Start the task in a background thread
            Thread processingThread = new Thread(task);
            processingThread.setDaemon(true);
            processingThread.start();
            
            // Return the deck immediately (it will be populated in the background)
            return newDeck;
            
        } catch (Exception e) {
            sceneManager.showErrorDialog("Creation Error", 
                "Failed to create flashcards from notes: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Generates flashcards from a single note
     */
    private List<Flashcard> generateFlashcardsFromNote(Note note) {
        List<Flashcard> flashcards = new ArrayList<>();
        String content = note.getContent();
        
        // Split content into sections (by headers, bullet points, etc.)
        String[] sections = content.split("\\n\\s*\\n|\\n\\s*[-*]\\s*|\\n\\s*\\d+\\.\\s*");
        
        for (String section : sections) {
            section = section.trim();
            if (section.length() > 20) { // Only process substantial sections
                List<Flashcard> sectionFlashcards = generateFlashcardsFromSection(section, note.getTitle());
                flashcards.addAll(sectionFlashcards);
            }
        }
        
        // If no flashcards were generated from sections, create a general one
        if (flashcards.isEmpty() && content.length() > 50) {
            String question = "What is the main topic of: " + note.getTitle() + "?";
            String answer = content.length() > 200 ? content.substring(0, 200) + "..." : content;
            flashcards.add(new Flashcard(question, answer, Flashcard.Difficulty.MEDIUM));
        }
        
        return flashcards;
    }
    
    /**
     * Generates flashcards from a content section
     */
    private List<Flashcard> generateFlashcardsFromSection(String section, String noteTitle) {
        List<Flashcard> flashcards = new ArrayList<>();
        
        // Look for question-answer patterns
        java.util.regex.Pattern qaPattern = java.util.regex.Pattern.compile("(?:Q:|Question:)\\s*(.+?)\\s*(?:A:|Answer:)\\s*(.+?)(?=\\n|$)", java.util.regex.Pattern.DOTALL);
        java.util.regex.Matcher qaMatcher = qaPattern.matcher(section);
        
        while (qaMatcher.find()) {
            String question = qaMatcher.group(1).trim();
            String answer = qaMatcher.group(2).trim();
            
            if (question.length() > 5 && answer.length() > 5) {
                flashcards.add(new Flashcard(question, answer, Flashcard.Difficulty.MEDIUM));
            }
        }
        
        // Look for definition patterns (term: definition)
        java.util.regex.Pattern definitionPattern = java.util.regex.Pattern.compile("([A-Za-z][A-Za-z\\s]+?):\\s*(.+?)(?=\\n|$)", java.util.regex.Pattern.DOTALL);
        java.util.regex.Matcher definitionMatcher = definitionPattern.matcher(section);
        
        while (definitionMatcher.find()) {
            String term = definitionMatcher.group(1).trim();
            String definition = definitionMatcher.group(2).trim();
            
            if (term.length() > 2 && definition.length() > 10) {
                String question = "What is " + term + "?";
                flashcards.add(new Flashcard(question, definition, Flashcard.Difficulty.EASY));
            }
        }
        
        // Look for bullet points or numbered lists
        java.util.regex.Pattern listPattern = java.util.regex.Pattern.compile("(?:^|\\n)\\s*[-*]\\s*(.+?)(?=\\n|$)", java.util.regex.Pattern.MULTILINE);
        java.util.regex.Matcher listMatcher = listPattern.matcher(section);
        
        while (listMatcher.find()) {
            String item = listMatcher.group(1).trim();
            if (item.length() > 10) {
                String question = "Explain: " + item;
                String answer = "This is a key point from " + noteTitle + ": " + item;
                flashcards.add(new Flashcard(question, answer, Flashcard.Difficulty.MEDIUM));
            }
        }
        
        return flashcards;
    }
    
    /**
     * Gets deck title from form
     */
    private String getDeckTitleFromForm() {
        return titleField.getText().trim();
    }
    
    /**
     * Gets deck subject from form
     */
    private String getDeckSubjectFromForm() {
        return subjectCombo.getValue() != null ? subjectCombo.getValue() : "Other";
    }
    
    /**
     * Gets deck description from form
     */
    private String getDeckDescriptionFromForm() {
        return descriptionArea.getText().trim();
    }
    
    /**
     * Gets deck difficulty from form
     */
    private Flashcard.Difficulty getDeckDifficultyFromForm() {
        if (difficultyGroup.getSelectedToggle() != null) {
            RadioButton selectedButton = (RadioButton) difficultyGroup.getSelectedToggle();
            String buttonId = selectedButton.getId();
            if ("easyDifficulty".equals(buttonId)) {
                return Flashcard.Difficulty.EASY;
            } else if ("mediumDifficulty".equals(buttonId)) {
                return Flashcard.Difficulty.MEDIUM;
            } else if ("hardDifficulty".equals(buttonId)) {
                return Flashcard.Difficulty.HARD;
            }
        }
        return Flashcard.Difficulty.EASY; // Default
    }
    
    /**
     * Processes the selected document file using AI-powered processing
     */
    private void processPDFForFlashcards(java.io.File pdfFile) {
        try {
            // Check if file type is supported
            String fileName = pdfFile.getName().toLowerCase();
            if (!fileName.endsWith(".pdf") && !fileName.endsWith(".doc") && 
                !fileName.endsWith(".docx") && !fileName.endsWith(".ppt") && 
                !fileName.endsWith(".pptx")) {
                sceneManager.showErrorDialog("Unsupported File Type", 
                    "Please select a PDF, Word document (.doc/.docx), or PowerPoint presentation (.ppt/.pptx) file.");
                return;
            }
            
            // Show simple notification like quiz generation
            sceneManager.showInfoDialog("Creating Flashcards", 
                "Creating flashcards from your document... We will notify you directly as we compile your flashcards.");
            
            // Process document with AI service in background
            javafx.concurrent.Task<com.studyspace.utils.DocumentProcessingService.DocumentProcessingResult> task = 
                new javafx.concurrent.Task<com.studyspace.utils.DocumentProcessingService.DocumentProcessingResult>() {
                @Override
                protected com.studyspace.utils.DocumentProcessingService.DocumentProcessingResult call() throws Exception {
                    com.studyspace.utils.DocumentProcessingService service = 
                        com.studyspace.utils.DocumentProcessingService.getInstance();
                    return service.processDocument(pdfFile, "flashcards");
                }
            };
            
            task.setOnSucceeded(e -> {
                com.studyspace.utils.DocumentProcessingService.DocumentProcessingResult result = task.getValue();
                
                if (result.isSuccess()) {
                    // Refresh the deck list to show the new AI-generated content
                    loadFlashcardDecks();
                    
                    // Show success message with details
                    String successMessage = String.format(
                        "✅ AI Processing Complete!\n\n" +
                        "📝 Note: %s\n" +
                        "🃏 Deck: %s\n" +
                        "📚 Flashcards: %d created\n" +
                        "📖 Subject: %s\n" +
                        "📊 Difficulty: %s\n\n" +
                        "Summary: %s",
                        result.getNoteTitle(),
                        result.getDeckTitle(),
                        result.getFlashcardsCreated(),
                        result.getSubject(),
                        result.getDifficulty(),
                        result.getSummary()
                    );
                    
                    sceneManager.showInfoDialog("AI Processing Complete", successMessage);
                    
                    // Auto-open the flashcard practice view for the newly created deck
                    try {
                        // Find the newly created deck by title
                        FlashcardDeck newDeck = dataStore.getAllFlashcardDecks().stream()
                            .filter(deck -> deck.getTitle().equals(result.getDeckTitle()))
                            .findFirst()
                            .orElse(null);
                        
                        if (newDeck != null) {
                            autoOpenFlashcardPractice(newDeck);
                        }
                    } catch (Exception ex) {
                        System.err.println("Error auto-opening AI-generated flashcard deck: " + ex.getMessage());
                        // Don't show error to user, just log it
                    }
                } else {
                    sceneManager.showErrorDialog("AI Processing Failed", 
                        "Failed to process document: " + result.getMessage());
                }
            });
            
            task.setOnFailed(e -> {
                sceneManager.showErrorDialog("AI Processing Error", 
                    "An error occurred while processing the document: " + task.getException().getMessage());
            });
            
            // Start the task in a background thread
            Thread processingThread = new Thread(task);
            processingThread.setDaemon(true);
            processingThread.start();
            
        } catch (Exception e) {
            sceneManager.showErrorDialog("Import Error", 
                "Failed to process document file: " + e.getMessage());
        }
    }
    
    /**
     * Creates flashcard deck from document processing
     */
    private void createFlashcardDeckFromPDF(String fileName) {
        try {
            // Create flashcard deck based on document processing
            String baseTitle = fileName.replaceAll("\\.(pdf|doc|docx|ppt|pptx)$", "").replace("_", " ");
            String fileType = getFileType(fileName);
            String deckTitle = baseTitle + " - Imported from " + fileType;
            String deckDescription = "Flashcard deck automatically generated from " + fileName + " with key concepts, definitions, and important information.";
            
            // Check if deck already exists to prevent duplicates
            boolean deckExists = dataStore.getAllFlashcardDecks().stream()
                .anyMatch(deck -> deck.getTitle().equals(deckTitle));
            
            if (deckExists) {
                sceneManager.showInfoDialog("Import Skipped", 
                    "Flashcard deck from this document already exists. No duplicates were created.");
                return;
            }
            
            // Create the deck
            FlashcardDeck newDeck = new FlashcardDeck(deckTitle, deckDescription, "Document Import", Flashcard.Difficulty.MEDIUM);
            
            // Create sample flashcards based on PDF content
            List<Flashcard> flashcards = new ArrayList<>();
            
            // Flashcard 1: Key Concept
            Flashcard card1 = new Flashcard(
                "What is the main concept discussed in " + fileName + "?",
                "The main concept is a fundamental principle extracted from the document that serves as the foundation for understanding the topic. It represents the core idea that ties together all other information in the document.",
                Flashcard.Difficulty.MEDIUM
            );
            // card1.setTags(java.util.Arrays.asList("main-concept", "foundation")); // Tags not supported in Flashcard model
            flashcards.add(card1);
            
            // Flashcard 2: Definition
            Flashcard card2 = new Flashcard(
                "Define the key term from the PDF document",
                "Key Term: A specific term or concept that is central to understanding the document's content. This definition was extracted from the document and represents an important piece of knowledge for study purposes.",
                Flashcard.Difficulty.EASY
            );
            // card2.setTags(java.util.Arrays.asList("definition", "terminology")); // Tags not supported in Flashcard model
            flashcards.add(card2);
            
            // Flashcard 3: Application
            Flashcard card3 = new Flashcard(
                "How is the concept from the PDF applied in practice?",
                "The concept is applied through practical examples and real-world scenarios. It demonstrates the practical relevance of the theoretical knowledge and shows how the information can be used in actual situations.",
                Flashcard.Difficulty.HARD
            );
            // card3.setTags(java.util.Arrays.asList("application", "practical")); // Tags not supported in Flashcard model
            flashcards.add(card3);
            
            // Flashcard 4: Important Detail
            Flashcard card4 = new Flashcard(
                "What is an important detail mentioned in the PDF?",
                "Important Detail: A specific piece of information that is crucial for understanding the topic. This detail provides context and depth to the main concepts and helps in comprehensive understanding of the subject matter.",
                Flashcard.Difficulty.MEDIUM
            );
            // card4.setTags(java.util.Arrays.asList("detail", "context")); // Tags not supported in Flashcard model
            flashcards.add(card4);
            
            // Flashcard 5: Summary Question
            Flashcard card5 = new Flashcard(
                "What are the key takeaways from the PDF document?",
                "Key Takeaways: 1) Main concept and its importance, 2) Supporting details and evidence, 3) Practical applications and examples, 4) Connections to broader topics, 5) Implications for further study.",
                Flashcard.Difficulty.HARD
            );
            // card5.setTags(java.util.Arrays.asList("summary", "takeaways")); // Tags not supported in Flashcard model
            flashcards.add(card5);
            
            // Add flashcards to deck
            for (Flashcard card : flashcards) {
                newDeck.addFlashcard(card);
            }
            
            // Add deck to data store
            dataStore.saveFlashcardDeck(newDeck);
            
            // Refresh the deck list
            decksList.clear();
            decksList.addAll(dataStore.getAllFlashcardDecks());
            
            // Refresh the UI to show new deck immediately
            loadFlashcardDecks();
            
            // Log activity for activity history
            dataStore.logUserActivity("FLASHCARD_CREATED", "Imported flashcard deck from " + fileType + " document: " + fileName);
            
            // Refresh activity history and all views to show the new activity
            com.studyspace.components.SidebarView.refreshActivityHistoryGlobally();
            com.studyspace.components.SidebarView.refreshAllViewsGlobally();
            
            // Show success message
            sceneManager.showInfoDialog("Import Successful!", 
                "Successfully created flashcard deck from your " + fileType + " document:\n\n" +
                "• Deck: " + deckTitle + "\n" +
                "• Cards: " + flashcards.size() + " flashcards generated\n" +
                "• Content: Key concepts, definitions, and important details\n\n" +
                "Your flashcard deck is now ready for study sessions!");
            
            // Auto-open the flashcard practice view
            autoOpenFlashcardPractice(newDeck);
                
        } catch (Exception e) {
            sceneManager.showErrorDialog("Import Error", 
                "Failed to create flashcard deck from document: " + e.getMessage());
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
     * Creates the deck creation form
     */
    private VBox createDeckCreationForm() {
        VBox form = new VBox();
        form.getStyleClass().add("deck-creation-form");
        form.setSpacing(16);
        form.setPadding(new Insets(20));
        form.setOnMouseClicked(e-> loadFlashcardDecks());
        // Title field
        VBox titleContainer = new VBox();
        titleContainer.setSpacing(8);

        Label titleLabel = new Label("Deck Title *");
        titleLabel.getStyleClass().add("form-label");

        titleField = new TextField();
        titleField.getStyleClass().add("form-field");
        titleField.setPromptText("Enter deck title...");
        titleField.setId("deckTitle");

        titleContainer.getChildren().addAll(titleLabel, titleField);

        // Subject field
        VBox subjectContainer = new VBox();
        subjectContainer.setSpacing(8);

        Label subjectLabel = new Label("Subject *");
        subjectLabel.getStyleClass().add("form-label");

        subjectCombo = new ComboBox<>();
        subjectCombo.getStyleClass().add("form-field");
        subjectCombo.getItems().addAll(
            "Mathematics", "Computer Science", "Physics", "Chemistry",
            "Biology", "History", "Literature", "Languages", "Other"
        );
        subjectCombo.setPromptText("Select subject...");
        subjectCombo.setId("deckSubject");

        subjectContainer.getChildren().addAll(subjectLabel, subjectCombo);

        // Description field
        VBox descriptionContainer = new VBox();
        descriptionContainer.setSpacing(8);

        Label descriptionLabel = new Label("Description");
        descriptionLabel.getStyleClass().add("form-label");

        descriptionArea = new TextArea();
        descriptionArea.getStyleClass().add("form-field");
        descriptionArea.setPromptText("Enter deck description...");
        descriptionArea.setPrefRowCount(3);
        descriptionArea.setWrapText(true);
        descriptionArea.setId("deckDescription");

        descriptionContainer.getChildren().addAll(descriptionLabel, descriptionArea);

        // Difficulty selection
        VBox difficultyContainer = new VBox();
        difficultyContainer.setSpacing(8);

        Label difficultyLabel = new Label("Difficulty Level");
        difficultyLabel.getStyleClass().add("form-label");

        HBox difficultyButtons = new HBox();
        difficultyButtons.setSpacing(8);

        difficultyGroup = new ToggleGroup();

        RadioButton easyButton = new RadioButton("Easy");
        easyButton.getStyleClass().add("difficulty-radio");
        easyButton.setToggleGroup(difficultyGroup);
        easyButton.setSelected(true);
        easyButton.setId("easyDifficulty");

        RadioButton mediumButton = new RadioButton("Medium");
        mediumButton.getStyleClass().add("difficulty-radio");
        mediumButton.setToggleGroup(difficultyGroup);
        mediumButton.setId("mediumDifficulty");

        RadioButton hardButton = new RadioButton("Hard");
        hardButton.getStyleClass().add("difficulty-radio");
        hardButton.setToggleGroup(difficultyGroup);
        hardButton.setId("hardDifficulty");

        difficultyButtons.getChildren().addAll(easyButton, mediumButton, hardButton);
        difficultyContainer.getChildren().addAll(difficultyLabel, difficultyButtons);

        form.getChildren().addAll(titleContainer, subjectContainer, descriptionContainer, difficultyContainer);
        return form;
    }

    // Form field references for deck creation
    private TextField titleField;
    private ComboBox<String> subjectCombo;
    private TextArea descriptionArea;
    private ToggleGroup difficultyGroup;
    
    /**
     * Creates a deck from the form data
     */
    private FlashcardDeck createDeckFromForm() {
        try {
            String title = titleField.getText().trim();
            String subject = subjectCombo.getValue();
            String description = descriptionArea.getText().trim();

            if (title.isEmpty() || subject == null) {
                sceneManager.showErrorDialog("Invalid Input", "Please fill in all required fields.");
                return null;
            }

            // Determine difficulty from radio button selection
            Flashcard.Difficulty difficulty = Flashcard.Difficulty.EASY;
            if (difficultyGroup.getSelectedToggle() != null) {
                RadioButton selectedButton = (RadioButton) difficultyGroup.getSelectedToggle();
                String buttonId = selectedButton.getId();
                if ("mediumDifficulty".equals(buttonId)) {
                    difficulty = Flashcard.Difficulty.MEDIUM;
                } else if ("hardDifficulty".equals(buttonId)) {
                    difficulty = Flashcard.Difficulty.HARD;
                }
            }

            FlashcardDeck newDeck = new FlashcardDeck(title, description, subject, difficulty);
            return newDeck;

        } catch (Exception e) {
            sceneManager.showErrorDialog("Error", "Failed to create deck. Please try again.");
            return null;
        }
    }

    /**
     * Applies sorting based on selected option
     */
    private void applySorting(String sortOption) {
        if (sortOption == null) return;
        
        switch (sortOption) {
            case "Title (A-Z)":
                sortedDecks.setComparator(Comparator.comparing(FlashcardDeck::getTitle, String.CASE_INSENSITIVE_ORDER));
                break;
            case "Title (Z-A)":
                sortedDecks.setComparator(Comparator.comparing(FlashcardDeck::getTitle, String.CASE_INSENSITIVE_ORDER).reversed());
                break;
            case "Date Created (Newest)":
                sortedDecks.setComparator(Comparator.comparing(FlashcardDeck::getCreatedAt).reversed());
                break;
            case "Date Created (Oldest)":
                sortedDecks.setComparator(Comparator.comparing(FlashcardDeck::getCreatedAt));
                break;
            case "Subject (A-Z)":
                sortedDecks.setComparator(Comparator.comparing(FlashcardDeck::getSubject, String.CASE_INSENSITIVE_ORDER));
                break;
            case "Subject (Z-A)":
                sortedDecks.setComparator(Comparator.comparing(FlashcardDeck::getSubject, String.CASE_INSENSITIVE_ORDER).reversed());
                break;
            case "Difficulty (Easy to Hard)":
                sortedDecks.setComparator(Comparator.comparing(FlashcardDeck::getDifficulty, Comparator.comparing(Enum::ordinal)));
                break;
            case "Difficulty (Hard to Easy)":
                sortedDecks.setComparator(Comparator.comparing(FlashcardDeck::getDifficulty, Comparator.comparing(Enum::ordinal)).reversed());
                break;
            case "Card Count (Low to High)":
                sortedDecks.setComparator(Comparator.comparing(FlashcardDeck::getCardCount));
                break;
            case "Card Count (High to Low)":
                sortedDecks.setComparator(Comparator.comparing(FlashcardDeck::getCardCount).reversed());
                break;
        }
        
        // Reload decks to show sorted results
        loadFlashcardDecks();
    }
    
    /**
     * Gets the main view container
     */
    public VBox getView() {
        return mainContainer;
    }

    /**
     * Refreshes the deck list (useful when returning from study mode)
     */
    public void refresh() {
        loadFlashcardDecks();
    }
    
    /**
     * Automatically opens the flashcard practice view for a newly created deck
     */
    private void autoOpenFlashcardPractice(FlashcardDeck deck) {
        try {
            // Create and show flashcard practice view
            FlashcardPracticeView practiceView = new FlashcardPracticeView(deck, this);
            
            // Replace current content with practice view
            Pane parent = (Pane) mainContainer.getParent();
            if (parent != null) {
                parent.getChildren().clear();
                parent.getChildren().add(practiceView.getView());
            }
        } catch (Exception e) {
            System.err.println("Error auto-opening flashcard practice: " + e.getMessage());
            e.printStackTrace();
            // If auto-open fails, just show an error but don't crash
            sceneManager.showErrorDialog("Auto-Open Failed", 
                "Flashcard deck was created successfully, but couldn't open practice mode automatically. " +
                "You can find your new deck in the list and click 'Practice' to start studying.");
        }
    }
}
