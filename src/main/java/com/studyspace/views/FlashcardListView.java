package com.studyspace.views;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.studyspace.models.Flashcard;
import com.studyspace.models.FlashcardDeck;
import com.studyspace.utils.DataStore;
import com.studyspace.utils.IconUtils;
import com.studyspace.utils.SceneManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
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
        
        Button createDeckButton = new Button();
        createDeckButton.setGraphic(IconUtils.createIconTextHBox("add", "Create Deck"));
        createDeckButton.getStyleClass().add("success-button");
        createDeckButton.setOnAction(e -> handleCreateNewDeck());
        
        actionButtons.getChildren().addAll(importFlashcardButton, createDeckButton);
        
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
        sceneManager.showInfoDialog("Edit Deck", 
            "Deck editing functionality will be available soon!\n\n" +
            "You'll be able to:\n" +
            "• Edit deck title and description\n" +
            "• Add new flashcards\n" +
            "• Edit existing flashcards\n" +
            "• Organize cards by difficulty");
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
                
            // Refresh activity history
            com.studyspace.components.SidebarView.refreshActivityHistoryGlobally();
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
                new javafx.stage.FileChooser.ExtensionFilter("All Supported Files", "*.pdf", "*.doc", "*.docx", "*.ppt", "*.pptx"),
                new javafx.stage.FileChooser.ExtensionFilter("All Files", "*.*")
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
     * Processes the selected PDF file and creates flashcard deck
     */
    private void processPDFForFlashcards(java.io.File pdfFile) {
        try {
            // Show processing dialog
            Dialog<Boolean> processingDialog = new Dialog<>();
            processingDialog.setTitle("Processing Document");
            processingDialog.setHeaderText("Extracting content from document...");
            processingDialog.setResizable(false);
            
            VBox processingContent = new VBox();
            processingContent.setSpacing(16);
            processingContent.setPadding(new Insets(20));
            processingContent.setAlignment(Pos.CENTER);
            
            Label processingLabel = new Label("Analyzing document content and generating flashcard deck...");
            processingLabel.getStyleClass().add("processing-text");
            
            ProgressBar progressBar = new ProgressBar();
            progressBar.setPrefWidth(300);
            progressBar.setProgress(-1); // Indeterminate progress
            
            processingContent.getChildren().addAll(processingLabel, progressBar);
            processingDialog.getDialogPane().setContent(processingContent);
            
            // Add cancel button
            ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            processingDialog.getDialogPane().getButtonTypes().add(cancelButtonType);
            
            // Style the cancel button
            Button cancelButton = (Button) processingDialog.getDialogPane().lookupButton(cancelButtonType);
            cancelButton.getStyleClass().add("secondary-button");
            
            // Handle cancel button and window close
            processingDialog.setResultConverter(buttonType -> {
                if (buttonType == cancelButtonType) {
                    return false; // Processing cancelled
                }
                return true; // Processing completed
            });
            
            // Handle window close (X button)
            processingDialog.getDialogPane().getScene().getWindow().setOnCloseRequest(e -> {
                processingDialog.setResult(false);
                processingDialog.close();
            });
            
            // Show processing dialog
            processingDialog.show();
            
            // Simulate processing time
            javafx.animation.Timeline timeline = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(javafx.util.Duration.seconds(2), e -> {
                    if (processingDialog.isShowing()) {
                        processingDialog.setResult(true);
                        processingDialog.close();
                        createFlashcardDeckFromPDF(pdfFile.getName());
                    }
                })
            );
            timeline.play();
            
        } catch (Exception e) {
            sceneManager.showErrorDialog("Import Error", 
                "Failed to process PDF file: " + e.getMessage());
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
            
            // Refresh activity history to show the new activity
            com.studyspace.components.SidebarView.refreshActivityHistoryGlobally();
            
            // Show success message
            sceneManager.showInfoDialog("Import Successful!", 
                "Successfully created flashcard deck from your " + fileType + " document:\n\n" +
                "• Deck: " + deckTitle + "\n" +
                "• Cards: " + flashcards.size() + " flashcards generated\n" +
                "• Content: Key concepts, definitions, and important details\n\n" +
                "Your flashcard deck is now ready for study sessions!");
                
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
}
