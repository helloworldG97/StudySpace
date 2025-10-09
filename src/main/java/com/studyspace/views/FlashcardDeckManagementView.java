package com.studyspace.views;

import com.studyspace.models.Flashcard;
import com.studyspace.models.FlashcardDeck;
import com.studyspace.utils.DataStore;
import com.studyspace.utils.SceneManager;
import com.studyspace.utils.IconUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.Parent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * FlashcardDeckManagementView - Manage flashcards within a deck
 */
public class FlashcardDeckManagementView {
    
    private final DataStore dataStore;
    private final SceneManager sceneManager;
    private final FlashcardDeck deck;
    private final FlashcardListView parentView;
    
    private VBox mainContainer;
    private VBox flashcardsContainer;
    private ScrollPane scrollPane;
    private ObservableList<Flashcard> flashcardsList;
    private javafx.collections.transformation.FilteredList<Flashcard> filteredFlashcards;
    private javafx.collections.transformation.SortedList<Flashcard> sortedFlashcards;
    private TextField searchField;
    private ComboBox<String> sortComboBox;
    
    // Form field references for flashcard creation/editing
    private TextArea questionArea;
    private TextArea answerArea;
    private ToggleGroup difficultyGroup;
    
    public FlashcardDeckManagementView(FlashcardDeck deck, FlashcardListView parentView) {
        this.dataStore = DataStore.getInstance();
        this.sceneManager = SceneManager.getInstance();
        this.deck = deck;
        this.parentView = parentView;
        this.flashcardsList = FXCollections.observableArrayList(deck.getFlashcards());
        this.filteredFlashcards = new javafx.collections.transformation.FilteredList<>(flashcardsList);
        this.sortedFlashcards = new javafx.collections.transformation.SortedList<>(filteredFlashcards);
        this.sortedFlashcards.setComparator((a, b) -> b.getLastStudied() != null && a.getLastStudied() != null 
            ? b.getLastStudied().compareTo(a.getLastStudied()) 
            : a.getQuestion().compareTo(b.getQuestion()));
        
        initializeUI();
        setupEventHandlers();
        loadFlashcards();
    }
    
    /**
     * Initializes the UI components
     */
    private void initializeUI() {
        mainContainer = new VBox();
        mainContainer.getStyleClass().add("flashcard-management-container");
        mainContainer.setSpacing(24);
        mainContainer.setPadding(new Insets(32));
        
        // Header section
        VBox headerSection = createHeaderSection();
        
        // Action buttons section
        HBox actionSection = createActionSection();
        
        // Search and filter section
        HBox searchFilterSection = createSearchFilterSection();
        
        // Flashcards container
        flashcardsContainer = new VBox();
        flashcardsContainer.getStyleClass().add("flashcards-management-container");
        flashcardsContainer.setSpacing(16);
        
        // Create scroll pane for flashcards
        scrollPane = new ScrollPane(flashcardsContainer);
        scrollPane.getStyleClass().add("flashcards-scroll-pane");
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        
        mainContainer.getChildren().addAll(headerSection, actionSection, searchFilterSection, scrollPane);
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
        searchField.setPromptText("Search flashcards...");
        searchField.setPrefWidth(300);
        
        searchBox.getChildren().addAll(searchIcon, searchField);
        
        // Sort dropdown
        HBox sortBox = new HBox();
        sortBox.setAlignment(Pos.CENTER_LEFT);
        sortBox.setSpacing(8);
        
        Label sortIcon = new Label();
        sortIcon.setGraphic(IconUtils.createSmallIconView("sort"));
        
        sortComboBox = new ComboBox<>();
        sortComboBox.getStyleClass().add("sort-combo");
        sortComboBox.getItems().addAll(
            "Recently Studied", 
            "Least Recently Studied", 
            "Most Studied", 
            "Least Studied", 
            "Date Created (Newest)",
            "Date Created (Oldest)",
            "Alphabetical (A-Z)", 
            "Alphabetical (Z-A)",
            "Difficulty (Easy-Hard)",
            "Difficulty (Hard-Easy)"
        );
        sortComboBox.setValue("Recently Studied");
        
        sortBox.getChildren().addAll(sortIcon, sortComboBox);
        
        searchFilterSection.getChildren().addAll(searchBox, sortBox);
        return searchFilterSection;
    }
    
    /**
     * Creates the header section
     */
    private VBox createHeaderSection() {
        VBox header = new VBox();
        header.setSpacing(12);
        
        // Title and deck info
        HBox titleSection = new HBox();
        titleSection.setAlignment(Pos.CENTER_LEFT);
        titleSection.setSpacing(16);
        
        Label titleLabel = new Label("🃏 " + deck.getTitle());
        titleLabel.getStyleClass().addAll("text-2xl", "font-bold", "text-primary");
        
        Label cardCountLabel = new Label("(" + deck.getCardCount() + " cards)");
        cardCountLabel.getStyleClass().addAll("text-lg", "text-secondary");
        
        titleSection.getChildren().addAll(titleLabel, cardCountLabel);
        
        // Deck metadata
        HBox metaInfo = new HBox();
        metaInfo.setSpacing(16);
        metaInfo.setAlignment(Pos.CENTER_LEFT);
        
        Label difficultyLabel = new Label(deck.getDifficulty().getDisplayName());
        difficultyLabel.getStyleClass().addAll("difficulty-badge", 
            deck.getDifficulty().name().toLowerCase());
        
        Label subjectLabel = new Label("📂 " + deck.getSubject());
        subjectLabel.getStyleClass().addAll("text-sm", "text-secondary");
        
        Label descriptionLabel = new Label(deck.getDescription());
        descriptionLabel.getStyleClass().addAll("text-sm", "text-muted");
        descriptionLabel.setWrapText(true);
        
        metaInfo.getChildren().addAll(difficultyLabel, subjectLabel, descriptionLabel);
        
        header.getChildren().addAll(titleSection, metaInfo);
        
        return header;
    }
    
    /**
     * Creates the action buttons section
     */
    private HBox createActionSection() {
        HBox actionSection = new HBox();
        actionSection.setSpacing(12);
        actionSection.setAlignment(Pos.CENTER_LEFT);
        
        Button addFlashcardButton = new Button();
        addFlashcardButton.setGraphic(IconUtils.createIconTextHBox("add", "Add Flashcard"));
        addFlashcardButton.getStyleClass().add("primary-button");
        addFlashcardButton.setOnAction(e -> handleAddFlashcard());
        
        // Removed: Import from Notes button
        // Removed: Edit Deck Details button
        
        Button studyButton = new Button();
        studyButton.setGraphic(IconUtils.createIconTextHBox("book", "Study Deck"));
        studyButton.getStyleClass().add("success-button");
        studyButton.setOnAction(e -> handleStudyDeck());
        studyButton.setDisable(deck.getCardCount() == 0);
        
        // Removed: Delete Deck button (trash button)
        
        Button backButton = new Button();
        backButton.setGraphic(IconUtils.createIconTextHBox("home", "Back to Decks"));
        backButton.getStyleClass().add("secondary-button");
        backButton.setOnAction(e -> handleBackToDecks());
        
        actionSection.getChildren().addAll(addFlashcardButton, studyButton, backButton);
        
        return actionSection;
    }
    
    
    /**
     * Sets up event handlers for search and sort
     */
    private void setupEventHandlers() {
        // Search functionality
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredFlashcards.setPredicate(flashcard -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return flashcard.getQuestion().toLowerCase().contains(lowerCaseFilter) ||
                       flashcard.getAnswer().toLowerCase().contains(lowerCaseFilter);
            });
            
            // Reload flashcards to show filtered results
            loadFlashcards();
        });
        
        // Sort functionality
        sortComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                applySorting(newValue);
            }
        });
    }
    
    /**
     * Applies sorting based on selected option
     */
    private void applySorting(String sortOption) {
        switch (sortOption) {
            case "Recently Studied":
                sortedFlashcards.setComparator((a, b) -> {
                    if (a.getLastStudied() == null && b.getLastStudied() == null) return 0;
                    if (a.getLastStudied() == null) return 1;
                    if (b.getLastStudied() == null) return -1;
                    return b.getLastStudied().compareTo(a.getLastStudied());
                });
                break;
            case "Least Recently Studied":
                sortedFlashcards.setComparator((a, b) -> {
                    if (a.getLastStudied() == null && b.getLastStudied() == null) return 0;
                    if (a.getLastStudied() == null) return -1;
                    if (b.getLastStudied() == null) return 1;
                    return a.getLastStudied().compareTo(b.getLastStudied());
                });
                break;
            case "Most Studied":
                sortedFlashcards.setComparator((a, b) -> Integer.compare(b.getTimesStudied(), a.getTimesStudied()));
                break;
            case "Least Studied":
                sortedFlashcards.setComparator((a, b) -> Integer.compare(a.getTimesStudied(), b.getTimesStudied()));
                break;
            case "Alphabetical (A-Z)":
                sortedFlashcards.setComparator((a, b) -> a.getQuestion().compareToIgnoreCase(b.getQuestion()));
                break;
            case "Alphabetical (Z-A)":
                sortedFlashcards.setComparator((a, b) -> b.getQuestion().compareToIgnoreCase(a.getQuestion()));
                break;
            case "Difficulty (Easy-Hard)":
                sortedFlashcards.setComparator((a, b) -> a.getDifficulty().compareTo(b.getDifficulty()));
                break;
            case "Difficulty (Hard-Easy)":
                sortedFlashcards.setComparator((a, b) -> b.getDifficulty().compareTo(a.getDifficulty()));
                break;
            case "Date Created (Newest)":
                sortedFlashcards.setComparator((a, b) -> {
                    if (a.getCreatedAt() == null && b.getCreatedAt() == null) return 0;
                    if (a.getCreatedAt() == null) return 1;
                    if (b.getCreatedAt() == null) return -1;
                    return b.getCreatedAt().compareTo(a.getCreatedAt());
                });
                break;
            case "Date Created (Oldest)":
                sortedFlashcards.setComparator((a, b) -> {
                    if (a.getCreatedAt() == null && b.getCreatedAt() == null) return 0;
                    if (a.getCreatedAt() == null) return 1;
                    if (b.getCreatedAt() == null) return -1;
                    return a.getCreatedAt().compareTo(b.getCreatedAt());
                });
                break;
            default:
                // Default to recently studied
                sortedFlashcards.setComparator((a, b) -> {
                    if (a.getLastStudied() == null && b.getLastStudied() == null) return 0;
                    if (a.getLastStudied() == null) return 1;
                    if (b.getLastStudied() == null) return -1;
                    return b.getLastStudied().compareTo(a.getLastStudied());
                });
        }
        
        // Reload flashcards to show sorted results
        loadFlashcards();
    }
    
    
    /**
     * Loads and displays flashcards
     */
    private void loadFlashcards() {
        flashcardsContainer.getChildren().clear();
        
        if (sortedFlashcards.isEmpty()) {
            showEmptyState();
        } else {
            for (Flashcard flashcard : sortedFlashcards) {
                VBox flashcardCard = createFlashcardCard(flashcard);
                flashcardsContainer.getChildren().add(flashcardCard);
            }
        }
    }
    
    /**
     * Creates a flashcard card for management
     */
    private VBox createFlashcardCard(Flashcard flashcard) {
        VBox card = new VBox();
        card.getStyleClass().add("flashcard-management-card");
        card.setSpacing(12);
        card.setPadding(new Insets(16));
        
        // Add hover effect for better visual feedback
        card.setOnMouseEntered(e -> {
            card.getStyleClass().add("flashcard-management-card-hover");
        });
        
        card.setOnMouseExited(e -> {
            card.getStyleClass().remove("flashcard-management-card-hover");
        });
        
        // Header with question and actions
        HBox cardHeader = new HBox();
        cardHeader.setAlignment(Pos.CENTER_LEFT);
        cardHeader.setSpacing(12);
        
        VBox contentSection = new VBox();
        contentSection.setSpacing(8);
        contentSection.setPrefWidth(400);
        contentSection.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(contentSection, Priority.ALWAYS);
        
        Label questionLabel = new Label("Q: " + flashcard.getQuestion());
        questionLabel.getStyleClass().add("flashcard-question-text");
        questionLabel.setWrapText(true);
        
        Label answerLabel = new Label("A: " + flashcard.getAnswer());
        answerLabel.getStyleClass().add("flashcard-answer-text");
        answerLabel.setWrapText(true);
        
        contentSection.getChildren().addAll(questionLabel, answerLabel);
        
        // Action buttons
        HBox actionButtons = new HBox();
        actionButtons.setSpacing(8);
        actionButtons.setAlignment(Pos.CENTER_RIGHT);
        
        Button editButton = new Button();
        editButton.setGraphic(IconUtils.createSmallIconView("edit"));
        editButton.getStyleClass().addAll("action-button", "edit");
        editButton.setTooltip(new Tooltip("Edit Flashcard"));
        editButton.setOnAction(e -> handleEditFlashcard(flashcard));
        
        Button deleteButton = new Button();
        deleteButton.setGraphic(IconUtils.createSmallIconView("trash"));
        deleteButton.getStyleClass().addAll("action-button", "delete");
        deleteButton.setTooltip(new Tooltip("Delete Flashcard"));
        deleteButton.setOnAction(e -> handleDeleteFlashcard(flashcard));
        
        actionButtons.getChildren().addAll(editButton, deleteButton);
        
        cardHeader.getChildren().addAll(contentSection, actionButtons);
        
        // Footer with metadata
        HBox cardFooter = new HBox();
        cardFooter.setSpacing(16);
        cardFooter.setAlignment(Pos.CENTER_LEFT);
        
        Label difficultyLabel = new Label(flashcard.getDifficulty().getDisplayName());
        difficultyLabel.getStyleClass().addAll("difficulty-badge", 
            flashcard.getDifficulty().name().toLowerCase());
        
        HBox studiedInfo = new HBox(5);
        studiedInfo.setAlignment(Pos.CENTER_LEFT);
        Label studiedIcon = new Label(IconUtils.getIconSymbol("clock"));
        studiedIcon.getStyleClass().add("icon-small");
        Label studiedLabel = new Label("Studied: " + flashcard.getTimesStudied() + " times");
        studiedLabel.getStyleClass().addAll("text-xs", "text-muted");
        studiedInfo.getChildren().addAll(studiedIcon, studiedLabel);
        
        HBox lastStudiedInfo = new HBox(5);
        lastStudiedInfo.setAlignment(Pos.CENTER_LEFT);
        Label calendarIcon = new Label(IconUtils.getIconSymbol("clock"));
        calendarIcon.getStyleClass().add("icon-small");
        Label lastStudiedLabel = new Label("Last: " + formatDate(flashcard.getLastStudied()));
        lastStudiedLabel.getStyleClass().addAll("text-xs", "text-muted");
        lastStudiedInfo.getChildren().addAll(calendarIcon, lastStudiedLabel);
        
        cardFooter.getChildren().addAll(difficultyLabel, studiedInfo, lastStudiedInfo);
        
        card.getChildren().addAll(cardHeader, cardFooter);
        
        // Add click handler for inline editing
        card.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                handleEditFlashcard(flashcard);
            }
        });
        
        // Add cursor style to indicate clickable
        card.setStyle("-fx-cursor: hand;");
        
        return card;
    }
    
    /**
     * Shows empty state when no flashcards exist
     */
    private void showEmptyState() {
        VBox emptyState = new VBox();
        emptyState.getStyleClass().add("empty-state");
        emptyState.setAlignment(Pos.CENTER);
        emptyState.setSpacing(16);
        emptyState.setPadding(new Insets(48));
        
        Label emptyIcon = new Label("🃏");
        emptyIcon.getStyleClass().add("empty-icon");
        
        Label emptyTitle = new Label("No flashcards in this deck");
        emptyTitle.getStyleClass().add("empty-title");
        
        Label emptyDescription = new Label("Add your first flashcard to start building this deck!");
        emptyDescription.getStyleClass().add("empty-description");
        
        Button addFirstCardButton = new Button("+ Add Your First Flashcard");
        addFirstCardButton.getStyleClass().add("primary-button");
        addFirstCardButton.setOnAction(e -> handleAddFlashcard());
        
        emptyState.getChildren().addAll(emptyIcon, emptyTitle, emptyDescription, addFirstCardButton);
        flashcardsContainer.getChildren().add(emptyState);
    }
    
    /**
     * Handles adding a new flashcard
     */
    private void handleAddFlashcard() {
        Dialog<Flashcard> dialog = new Dialog<>();
        dialog.setTitle("Add New Flashcard");
        dialog.setHeaderText("Create a new flashcard for this deck");
        
        // Create dialog content
        VBox content = createFlashcardForm(null);
        dialog.getDialogPane().setContent(content);
        
        // Add buttons
        ButtonType createButtonType = new ButtonType("Add Flashcard", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, cancelButtonType);
        
        // Set result converter
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                return createFlashcardFromForm();
            }
            return null;
        });
        
        // Show dialog and handle result
        dialog.showAndWait().ifPresent(newFlashcard -> {
            if (newFlashcard != null) {
                deck.addFlashcard(newFlashcard);
                flashcardsList.add(newFlashcard);
                dataStore.saveFlashcardDeck(deck);
                loadFlashcards();
                
                // Log activity
                dataStore.logUserActivity("FLASHCARD_DECK_CREATED", "Added flashcard to deck: " + deck.getTitle());
                
                // Refresh activity history and all views
                com.studyspace.components.SidebarView.refreshActivityHistoryGlobally();
                com.studyspace.components.SidebarView.refreshAllViewsGlobally();
            }
        });
    }
    
    /**
     * Handles editing an existing flashcard
     */
    private void handleEditFlashcard(Flashcard flashcard) {
        Dialog<Flashcard> dialog = new Dialog<>();
        dialog.setTitle("Edit Flashcard");
        dialog.setHeaderText("Edit this flashcard");
        
        // Create dialog content
        VBox content = createFlashcardForm(flashcard);

        dialog.getDialogPane().setContent(content);
        
        // Add buttons
        ButtonType saveButtonType = new ButtonType("Save Changes", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);
        
        // Set result converter
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return updateFlashcardFromForm(flashcard);
            }
            return null;
        });
        
        // Show dialog and handle result
        dialog.showAndWait().ifPresent(updatedFlashcard -> {
            if (updatedFlashcard != null) {
                int index = flashcardsList.indexOf(flashcard);
                flashcardsList.set(index, updatedFlashcard);
                dataStore.saveFlashcardDeck(deck);

                loadFlashcards();
            }
        });
    }
    
    // Removed: handleEditDeckDetails method
    
    // Removed: refreshHeader method (no longer needed)
    
    // Removed: handleImportFromNotes method
    
    // Removed: createFlashcardsFromNotes method (no longer needed)
    
    // Removed: generateFlashcardsFromNote method (no longer needed)
    
    // Removed: generateFlashcardsFromSection method (no longer needed)
    
    /**
     * Handles deleting a flashcard
     */
    private void handleDeleteFlashcard(Flashcard flashcard) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Delete Flashcard");
        confirmDialog.setHeaderText("Are you sure you want to delete this flashcard?");
        confirmDialog.setContentText("This action cannot be undone.");
        
        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                deck.removeFlashcard(flashcard);
                flashcardsList.remove(flashcard);
                dataStore.saveFlashcardDeck(deck);
                loadFlashcards();
            }
        });
    }
    
    /**
     * Handles studying the deck
     */
    private void handleStudyDeck() {
        if (deck.getCardCount() == 0) {
            sceneManager.showInfoDialog("Empty Deck", 
                "This deck doesn't have any flashcards yet. Add some cards first!");
            return;
        }

        // Create and show flashcard study view
        FlashcardStudyView studyView = new FlashcardStudyView(deck, parentView);
        
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
     * Handles returning to the flashcard decks list
     */
    private void handleBackToDecks() {
        // Refresh the parent view to show updated statistics
        parentView.refresh();
        
        // Replace current content with the decks list
        Parent parent = mainContainer.getParent();
        if (parent != null) {
            if (parent instanceof VBox) {
                ((VBox) parent).getChildren().clear();
                ((VBox) parent).getChildren().add(parentView.getView());
            } else if (parent instanceof StackPane) {
                ((StackPane) parent).getChildren().clear();
                ((StackPane) parent).getChildren().add(parentView.getView());
            }
        }
    }
    
    /**
     * Handles deleting the entire deck
     */
    // Removed: handleDeleteDeck method
    
    /**
     * Creates the flashcard form
     */
    private VBox createFlashcardForm(Flashcard flashcard) {
        VBox form = new VBox();
        form.getStyleClass().add("flashcard-form");
        form.setSpacing(16);
        form.setPadding(new Insets(20));
        
        // Question field
        VBox questionContainer = new VBox();
        questionContainer.setSpacing(8);
        
        Label questionLabel = new Label("Question *");
        questionLabel.getStyleClass().add("form-label");
        
        questionArea = new TextArea();
        questionArea.getStyleClass().add("form-field");
        questionArea.setPromptText("Enter the question...");
        questionArea.setPrefRowCount(3);
        questionArea.setWrapText(true);
        questionArea.setId("flashcardQuestion");
        if (flashcard != null) {
            questionArea.setText(flashcard.getQuestion());
        }
        
        questionContainer.getChildren().addAll(questionLabel, questionArea);
        
        // Answer field
        VBox answerContainer = new VBox();
        answerContainer.setSpacing(8);
        
        Label answerLabel = new Label("Answer *");
        answerLabel.getStyleClass().add("form-label");
        
        answerArea = new TextArea();
        answerArea.getStyleClass().add("form-field");
        answerArea.setPromptText("Enter the answer...");
        answerArea.setPrefRowCount(3);
        answerArea.setWrapText(true);
        answerArea.setId("flashcardAnswer");
        if (flashcard != null) {
            answerArea.setText(flashcard.getAnswer());
        }
        
        answerContainer.getChildren().addAll(answerLabel, answerArea);
        
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
        easyButton.setId("easyDifficulty");
        
        RadioButton mediumButton = new RadioButton("Medium");
        mediumButton.getStyleClass().add("difficulty-radio");
        mediumButton.setToggleGroup(difficultyGroup);
        mediumButton.setId("mediumDifficulty");
        
        RadioButton hardButton = new RadioButton("Hard");
        hardButton.getStyleClass().add("difficulty-radio");
        hardButton.setToggleGroup(difficultyGroup);
        hardButton.setId("hardDifficulty");
        
        // Set default selection
        if (flashcard != null) {
            switch (flashcard.getDifficulty()) {
                case EASY:
                    easyButton.setSelected(true);
                    break;
                case MEDIUM:
                    mediumButton.setSelected(true);
                    break;
                case HARD:
                    hardButton.setSelected(true);
                    break;
            }
        } else {
            mediumButton.setSelected(true);
        }
        
        difficultyButtons.getChildren().addAll(easyButton, mediumButton, hardButton);
        difficultyContainer.getChildren().addAll(difficultyLabel, difficultyButtons);
        
        form.getChildren().addAll(questionContainer, answerContainer, difficultyContainer);
        return form;
    }
    
    /**
     * Creates a flashcard from the form data
     */
    private Flashcard createFlashcardFromForm() {
        try {
            String question = questionArea.getText().trim();
            String answer = answerArea.getText().trim();
            
            if (question.isEmpty() || answer.isEmpty()) {
                sceneManager.showErrorDialog("Invalid Input", "Please fill in both question and answer fields.");
                return null;
            }
            
            // Determine difficulty from radio button selection
            Flashcard.Difficulty difficulty = Flashcard.Difficulty.MEDIUM; // Default
            if (difficultyGroup.getSelectedToggle() != null) {
                RadioButton selectedButton = (RadioButton) difficultyGroup.getSelectedToggle();
                String buttonId = selectedButton.getId();
                if ("easyDifficulty".equals(buttonId)) {
                    difficulty = Flashcard.Difficulty.EASY;
                } else if ("mediumDifficulty".equals(buttonId)) {
                    difficulty = Flashcard.Difficulty.MEDIUM;
                } else if ("hardDifficulty".equals(buttonId)) {
                    difficulty = Flashcard.Difficulty.HARD;
                }
            }
            
            Flashcard newFlashcard = new Flashcard(question, answer, difficulty);
            return newFlashcard;
            
        } catch (Exception e) {
            sceneManager.showErrorDialog("Error", "Failed to create flashcard. Please try again.");
            return null;
        }
    }
    
    /**
     * Updates a flashcard from the form data
     */
    private Flashcard updateFlashcardFromForm(Flashcard flashcard) {
        try {
            String question = questionArea.getText().trim();
            String answer = answerArea.getText().trim();
            
            if (question.isEmpty() || answer.isEmpty()) {
                sceneManager.showErrorDialog("Invalid Input", "Please fill in both question and answer fields.");
                return null;
            }
            
            // Determine difficulty from radio button selection
            Flashcard.Difficulty difficulty = Flashcard.Difficulty.MEDIUM; // Default
            if (difficultyGroup.getSelectedToggle() != null) {
                RadioButton selectedButton = (RadioButton) difficultyGroup.getSelectedToggle();
                String buttonId = selectedButton.getId();
                if ("easyDifficulty".equals(buttonId)) {
                    difficulty = Flashcard.Difficulty.EASY;
                } else if ("mediumDifficulty".equals(buttonId)) {
                    difficulty = Flashcard.Difficulty.MEDIUM;
                } else if ("hardDifficulty".equals(buttonId)) {
                    difficulty = Flashcard.Difficulty.HARD;
                }
            }
            
            // Update the flashcard with new data
            flashcard.setQuestion(question);
            flashcard.setAnswer(answer);
            flashcard.setDifficulty(difficulty);
            
            return flashcard;
            
        } catch (Exception e) {
            sceneManager.showErrorDialog("Error", "Failed to update flashcard. Please try again.");
            return null;
        }
    }
    
    /**
     * Formats date for display
     */
    private String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "Never";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
        return dateTime.format(formatter);
    }
    
    /**
     * Gets the main view container
     */
    public VBox getView() {
        return mainContainer;
    }
}
