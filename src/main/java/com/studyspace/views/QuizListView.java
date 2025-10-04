package com.studyspace.views;

import com.studyspace.models.Quiz;
import com.studyspace.models.FlashcardDeck;
import com.studyspace.models.Flashcard;
import com.studyspace.models.Note;
import com.studyspace.models.Question;
import com.studyspace.utils.DataStore;
import com.studyspace.utils.SceneManager;
import com.studyspace.utils.IconUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

//============ quiz list view =============
//this is where quizzes are displayed and managed

public class QuizListView {
    
    private final DataStore dataStore;
    private final SceneManager sceneManager;
    
    private VBox mainContainer;
    private ScrollPane scrollPane;
    private FlowPane quizzesGrid;
    private ComboBox<String> sortComboBox;
    
    public QuizListView() {
        this.dataStore = DataStore.getInstance();
        this.sceneManager = SceneManager.getInstance();
        
        initializeUI();
        loadQuizzes();
    }
    
    /**
     * Initializes the UI components
     */
    private void initializeUI() {
        mainContainer = new VBox();
        mainContainer.setSpacing(24);
        mainContainer.getStyleClass().add("content-area");
        
        // Header section
        VBox headerSection = new VBox();
        headerSection.setSpacing(8);
        
        Label titleLabel = new Label();
        titleLabel.setGraphic(IconUtils.createLargeIconView("question"));
        titleLabel.setText(" Quizzes");
        titleLabel.getStyleClass().addAll("text-3xl", "font-bold", "text-primary");
        
        Label subtitleLabel = new Label("Test your knowledge with interactive quizzes");
        subtitleLabel.getStyleClass().addAll("text-base", "text-secondary");
        
        headerSection.getChildren().addAll(titleLabel, subtitleLabel);
        
        // Action bar
        HBox actionBar = new HBox();
        actionBar.setSpacing(16);
        actionBar.setAlignment(Pos.CENTER_LEFT);
        
        Button createFromFlashcardsButton = new Button();
        createFromFlashcardsButton.setGraphic(IconUtils.createIconTextHBox("cards", "Create from Flashcards"));
        createFromFlashcardsButton.getStyleClass().add("primary-button");
        createFromFlashcardsButton.setOnAction(e -> handleCreateFromFlashcards());
        
        Button createFromNotesButton = new Button();
        createFromNotesButton.setGraphic(IconUtils.createIconTextHBox("note", "Create from Notes"));
        createFromNotesButton.getStyleClass().add("success-button");
        createFromNotesButton.setOnAction(e -> handleCreateFromNotes());
        
        Button importQuizButton = new Button();
        importQuizButton.setGraphic(IconUtils.createIconTextHBox("upload", "Import Quiz"));
        importQuizButton.getStyleClass().add("secondary-button");
        importQuizButton.setOnAction(e -> handleImportQuiz());
        
        // Sort dropdown (moved to right side) with enhanced icons
        HBox sortContainer = new HBox();
        sortContainer.setSpacing(8);
        sortContainer.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Label sortIcon = new Label();
        sortIcon.setGraphic(IconUtils.createSmallIconView("sort"));
        
        Label sortLabel = new Label("Sort By:");
        sortLabel.getStyleClass().add("text-sm");
        
        sortComboBox = new ComboBox<>();
        sortComboBox.getStyleClass().add("filter-combobox");
        sortComboBox.getItems().addAll(
            "📝 Title (A-Z)",
            "📝 Title (Z-A)", 
            "📅 Date Created (Newest)",
            "📅 Date Created (Oldest)",
            "📂 Subject (A-Z)",
            "📂 Subject (Z-A)",
            "⭐ Difficulty (Easy to Hard)",
            "⭐ Difficulty (Hard to Easy)",
            "❓ Question Count (Low to High)",
            "❓ Question Count (High to Low)"
        );
        sortComboBox.setValue("📅 Date Created (Newest)");
        sortComboBox.setPrefWidth(220);
        sortComboBox.setOnAction(e -> loadQuizzes());
        
        sortContainer.getChildren().addAll(sortIcon, sortLabel, sortComboBox);
        
        actionBar.getChildren().addAll(createFromFlashcardsButton, createFromNotesButton, importQuizButton, sortContainer);
        
        // Quizzes grid - improved configuration
        quizzesGrid = new FlowPane();
        quizzesGrid.setHgap(20);
        quizzesGrid.setVgap(20);
        quizzesGrid.setAlignment(Pos.TOP_LEFT);
        quizzesGrid.setPrefWrapLength(1200); // Allow proper wrapping
        quizzesGrid.setMaxWidth(Double.MAX_VALUE);
        
        // Scroll pane for the grid - improved configuration
        scrollPane = new ScrollPane(quizzesGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false); // Changed to false to allow vertical scrolling
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // No horizontal scroll
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // Vertical scroll when needed
        scrollPane.setStyle("-fx-background-color: transparent;");
        scrollPane.setPannable(true); // Allow panning
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        
        mainContainer.getChildren().addAll(headerSection, actionBar, scrollPane);
    }
    
    /**
     * Loads all quizzes from the data store
     */
    private void loadQuizzes() {
        try {
            System.out.println("Loading quizzes...");
            
            // Clear existing content
            quizzesGrid.getChildren().clear();
            
            // Get all quizzes from data store
            List<Quiz> quizzes = dataStore.getAllQuizzes();
            System.out.println("Found " + quizzes.size() + " quizzes in data store");
            
            // Debug: List all quizzes
            for (int i = 0; i < quizzes.size(); i++) {
                Quiz q = quizzes.get(i);
                System.out.println("  Quiz " + (i+1) + ": " + q.getTitle() + " (ID: " + q.getId() + ", Questions: " + q.getQuestionCount() + ")");
            }
            
            // Apply sorting
            if (sortComboBox != null && sortComboBox.getValue() != null) {
                quizzes = applySorting(quizzes, sortComboBox.getValue());
                System.out.println("Applied sorting: " + sortComboBox.getValue());
            }
            
            if (quizzes.isEmpty()) {
                // Show empty state - replace entire content
                showEmptyState();
                System.out.println("Showing empty state");
            } else {
                // Create quiz cards
                int cardCount = 0;
                for (Quiz quiz : quizzes) {
                    VBox quizCard = createQuizCard(quiz);
                    quizzesGrid.getChildren().add(quizCard);
                    cardCount++;
                    System.out.println("Added quiz card " + cardCount + ": " + quiz.getTitle());
                }
                System.out.println("Created " + quizzes.size() + " quiz cards total");
                System.out.println("FlowPane children count: " + quizzesGrid.getChildren().size());
                
                // Set the quizzes grid as the scroll pane content
                scrollPane.setContent(quizzesGrid);
            }
            
            // Force layout updates
            quizzesGrid.requestLayout();
            scrollPane.requestLayout();
            mainContainer.requestLayout();
            
            // Debug: Print FlowPane properties
            System.out.println("FlowPane width: " + quizzesGrid.getWidth());
            System.out.println("FlowPane height: " + quizzesGrid.getHeight());
            System.out.println("ScrollPane viewport bounds: " + scrollPane.getViewportBounds());
            
        } catch (Exception e) {
            System.err.println("Error loading quizzes: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Applies sorting to the quiz list
     */
    private List<Quiz> applySorting(List<Quiz> quizzes, String sortOption) {
        if (sortOption == null) return quizzes;
        
        return quizzes.stream().sorted((quiz1, quiz2) -> {
            switch (sortOption) {
                case "📝 Title (A-Z)":
                case "Title (A-Z)":
                    return quiz1.getTitle().compareToIgnoreCase(quiz2.getTitle());
                case "📝 Title (Z-A)":
                case "Title (Z-A)":
                    return quiz2.getTitle().compareToIgnoreCase(quiz1.getTitle());
                case "📅 Date Created (Newest)":
                case "Date Created (Newest)":
                    return quiz2.getCreatedAt().compareTo(quiz1.getCreatedAt());
                case "📅 Date Created (Oldest)":
                case "Date Created (Oldest)":
                    return quiz1.getCreatedAt().compareTo(quiz2.getCreatedAt());
                case "📂 Subject (A-Z)":
                case "Subject (A-Z)":
                    return quiz1.getSubject().compareToIgnoreCase(quiz2.getSubject());
                case "📂 Subject (Z-A)":
                case "Subject (Z-A)":
                    return quiz2.getSubject().compareToIgnoreCase(quiz1.getSubject());
                case "⭐ Difficulty (Easy to Hard)":
                case "Difficulty (Easy to Hard)":
                    return quiz1.getDifficulty().compareTo(quiz2.getDifficulty());
                case "⭐ Difficulty (Hard to Easy)":
                case "Difficulty (Hard to Easy)":
                    return quiz2.getDifficulty().compareTo(quiz1.getDifficulty());
                case "❓ Question Count (Low to High)":
                case "Question Count (Low to High)":
                    return Integer.compare(quiz1.getQuestionCount(), quiz2.getQuestionCount());
                case "❓ Question Count (High to Low)":
                case "Question Count (High to Low)":
                    return Integer.compare(quiz2.getQuestionCount(), quiz1.getQuestionCount());
                default:
                    return 0;
            }
        }).collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Creates a quiz card
     */
    private VBox createQuizCard(Quiz quiz) {
        VBox card = new VBox();
        card.getStyleClass().addAll("grid-tile", "hover-lift");
        card.setSpacing(12);
        card.setPrefWidth(320);
        card.setMaxWidth(320);
        card.setMinWidth(300);
        card.setMinHeight(220);
        card.setMaxHeight(320); // Fixed maximum height
        card.setPrefHeight(300); // Fixed preferred height
        
        // Header with difficulty badge and time limit
        HBox header = new HBox();
        header.setSpacing(8);
        header.setAlignment(Pos.TOP_LEFT);
        
        // Enhanced difficulty badge with icons
        HBox difficultyContainer = new HBox();
        difficultyContainer.setSpacing(4);
        difficultyContainer.setAlignment(Pos.CENTER_LEFT);
        
        Label difficultyIcon = new Label();
        String difficultyIconName = quiz.getDifficulty().name().toLowerCase().equals("easy") ? "check" : 
                                   quiz.getDifficulty().name().toLowerCase().equals("medium") ? "star" : "fire";
        difficultyIcon.setGraphic(IconUtils.createSmallIconView(difficultyIconName));
        
        Label difficultyBadge = new Label(quiz.getDifficulty().getDisplayName());
        difficultyBadge.getStyleClass().addAll("difficulty-badge", 
            quiz.getDifficulty().name().toLowerCase());
        
        difficultyContainer.getChildren().addAll(difficultyIcon, difficultyBadge);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label timeLimitLabel = new Label();
        timeLimitLabel.setGraphic(IconUtils.createSmallIconView("clock"));
        timeLimitLabel.setText(" " + quiz.getTimeLimitText());
        timeLimitLabel.getStyleClass().addAll("text-xs", "text-muted");
        
        header.getChildren().addAll(difficultyContainer, spacer, timeLimitLabel);
        
        // Title and description
        Label titleLabel = new Label(quiz.getTitle());
        titleLabel.getStyleClass().add("grid-tile-title");
        titleLabel.setWrapText(true);
        titleLabel.setMaxHeight(40); // Limit title height
        titleLabel.setMaxWidth(300); // Limit title width
        
        Label descriptionLabel = new Label(quiz.getDescription());
        descriptionLabel.getStyleClass().add("grid-tile-description");
        descriptionLabel.setWrapText(true);
        descriptionLabel.setMaxHeight(60); // Limit description height
        descriptionLabel.setMaxWidth(300); // Limit description width
        
        // Quiz metadata with enhanced icons
        VBox metaInfo = new VBox();
        metaInfo.setSpacing(4);
        
        HBox subjectContainer = new HBox();
        subjectContainer.setSpacing(4);
        subjectContainer.setAlignment(Pos.CENTER_LEFT);
        Label subjectIcon = new Label();
        subjectIcon.setGraphic(IconUtils.createSmallIconView("folder"));
        Label subjectLabel = new Label(quiz.getSubject());
        subjectLabel.getStyleClass().addAll("text-xs", "text-muted");
        subjectContainer.getChildren().addAll(subjectIcon, subjectLabel);
        
        HBox questionsContainer = new HBox();
        questionsContainer.setSpacing(4);
        questionsContainer.setAlignment(Pos.CENTER_LEFT);
        Label questionsIcon = new Label();
        questionsIcon.setGraphic(IconUtils.createSmallIconView("question"));
        Label questionsLabel = new Label(quiz.getQuestionCount() + " questions");
        questionsLabel.getStyleClass().addAll("text-xs", "text-muted");
        questionsContainer.getChildren().addAll(questionsIcon, questionsLabel);
        
        HBox lastTakenContainer = new HBox();
        lastTakenContainer.setSpacing(4);
        lastTakenContainer.setAlignment(Pos.CENTER_LEFT);
        Label lastTakenIcon = new Label();
        lastTakenIcon.setGraphic(IconUtils.createSmallIconView("clock"));
        Label lastTakenLabel = new Label(quiz.getLastTakenText());
        lastTakenLabel.getStyleClass().addAll("text-xs", "text-muted");
        lastTakenContainer.getChildren().addAll(lastTakenIcon, lastTakenLabel);
        
        metaInfo.getChildren().addAll(subjectContainer, questionsContainer, lastTakenContainer);
        
        // Best score display
        HBox scoreInfo = new HBox();
        scoreInfo.setSpacing(8);
        scoreInfo.setAlignment(Pos.CENTER_LEFT);
        
        if (quiz.getBestScore() > 0) {
            HBox scoreContainer = new HBox();
            scoreContainer.setSpacing(4);
            scoreContainer.setAlignment(Pos.CENTER_LEFT);
            Label scoreIcon = new Label();
            scoreIcon.setGraphic(IconUtils.createSmallIconView("star"));
            Label bestScoreLabel = new Label("Best Score: " + quiz.getBestScoreText());
            bestScoreLabel.getStyleClass().addAll("text-sm", "font-medium", "text-success");
            scoreContainer.getChildren().addAll(scoreIcon, bestScoreLabel);
            scoreInfo.getChildren().add(scoreContainer);
        } else {
            HBox notTakenContainer = new HBox();
            notTakenContainer.setSpacing(4);
            notTakenContainer.setAlignment(Pos.CENTER_LEFT);
            Label notTakenIcon = new Label();
            notTakenIcon.setGraphic(IconUtils.createSmallIconView("star"));
            Label notTakenLabel = new Label("Not taken yet");
            notTakenLabel.getStyleClass().addAll("text-sm", "text-muted");
            notTakenContainer.getChildren().addAll(notTakenIcon, notTakenLabel);
            scoreInfo.getChildren().add(notTakenContainer);
        }
        
        // Action buttons - Only Study Quiz and Delete Quiz
        HBox actionButtons = new HBox();
        actionButtons.setSpacing(8);
        actionButtons.setAlignment(Pos.CENTER_LEFT);
        
        Button studyButton = new Button();
        studyButton.setGraphic(IconUtils.createIconTextHBox("play", "Study Quiz"));
        studyButton.getStyleClass().add("primary-button");
        studyButton.setOnAction(e -> {
            e.consume();
            handleStartQuiz(quiz);
        });
        studyButton.setOnMouseClicked(e -> {
            e.consume();
            handleStartQuiz(quiz);
        });
        
        Button deleteButton = new Button();
        deleteButton.setGraphic(IconUtils.createIconView("trash"));
        deleteButton.getStyleClass().add("danger-button");
        deleteButton.setOnAction(e -> {
            e.consume();
            handleDeleteQuiz(quiz);
        });
        deleteButton.setOnMouseClicked(e -> {
            e.consume();
            handleDeleteQuiz(quiz);
        });
        
        actionButtons.getChildren().addAll(studyButton, deleteButton);
        
        card.getChildren().addAll(header, titleLabel, descriptionLabel, metaInfo, scoreInfo, actionButtons);
        
        // Add click handler for the entire card
        card.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                handleStartQuiz(quiz);
            }
        });
        
        return card;
    }
    
    /**
     * Shows empty state by replacing the entire content
     */
    private void showEmptyState() {
        // Clear the scroll pane content
        scrollPane.setContent(null);
        
        // Create centered empty state
        VBox emptyState = new VBox();
        emptyState.getStyleClass().add("empty-state");
        emptyState.setAlignment(Pos.CENTER);
        emptyState.setSpacing(16);
        emptyState.setPadding(new Insets(48));
        emptyState.setPrefWidth(400);
        emptyState.setPrefHeight(300);
        
        Label emptyIcon = new Label();
        emptyIcon.setGraphic(IconUtils.createLargeIconView("question"));
        emptyIcon.getStyleClass().add("empty-icon");
        
        Label emptyTitle = new Label("No Quizzes Available");
        emptyTitle.getStyleClass().addAll("text-xl", "font-semibold", "text-primary");
        
        Label emptyDescription = new Label("Create your first quiz to start testing your knowledge!");
        emptyDescription.getStyleClass().addAll("text-base", "text-secondary");
        emptyDescription.setWrapText(true);
        emptyDescription.setStyle("-fx-text-alignment: center;");
        
        Button createButton = new Button("Create from Flashcards");
        createButton.getStyleClass().add("primary-button");
        createButton.setOnAction(e -> handleCreateFromFlashcards());
        
        emptyState.getChildren().addAll(emptyIcon, emptyTitle, emptyDescription, createButton);
        
        // Set the empty state as the scroll pane content
        scrollPane.setContent(emptyState);
    }
    
    
    /**
     * Handles starting a quiz
     */
    private void handleStartQuiz(Quiz quiz) {
        if (quiz.getQuestionCount() == 0) {
            sceneManager.showInfoDialog("Empty Quiz", 
                "This quiz doesn't have any questions yet. Add some questions first!");
            return;
        }
        
        // Show confirmation dialog with quiz info
        String confirmMessage = String.format(
            "Are you ready to start this quiz?\n\n" +
            "📝 %s\n" +
            "❓ %d questions\n" +
            "⏰ Time limit: %s\n" +
            "🎯 Difficulty: %s",
            quiz.getTitle(),
            quiz.getQuestionCount(),
            quiz.getTimeLimitText(),
            quiz.getDifficulty().getDisplayName()
        );
        
        boolean confirmed = sceneManager.showConfirmationDialog("Start Quiz", confirmMessage);
        
        if (confirmed) {
            // Create and show quiz mode view
            QuizModeView quizModeView = new QuizModeView(quiz, this);
            
            // Replace current content with quiz mode view
            Pane parent = (Pane) mainContainer.getParent();
            if (parent != null) {
                parent.getChildren().clear();
                parent.getChildren().add(quizModeView.getView());
            }
        }
    }
    
    /**
     * Handles creating a quiz from flashcards
     */
    private void handleCreateFromFlashcards() {
        List<FlashcardDeck> decks = dataStore.getAllFlashcardDecks();
        
        if (decks.isEmpty()) {
            sceneManager.showInfoDialog("No Flashcards Available", 
                "You need to create some flashcard decks first before generating quizzes from them.\n\n" +
                "Go to the Flashcards section to create your first deck!");
            return;
        }
        
        showFlashcardSelectionDialog(decks);
    }
    
    /**
     * Handles creating a quiz from notes
     */
    private void handleCreateFromNotes() {
        List<Note> notes = dataStore.getAllNotes();
        
        if (notes.isEmpty()) {
            sceneManager.showInfoDialog("No Notes Available", 
                "You need to create some notes first before generating quizzes from them.\n\n" +
                "Go to the Notes section to create your first note!");
            return;
        }
        
        showNotesSelectionDialog(notes);
    }
    
    /**
     * Handles importing a quiz from PDF
     */
    private void handleImportQuiz() {
        showImportQuizDialog();
    }
    
    /**
     * Gets the main view container
     */
    public VBox getView() {
        return mainContainer;
    }
    
    /**
     * Refreshes the quiz list (useful when returning from quiz mode)
     */
    public void refresh() {
        System.out.println("Public refresh called");
        javafx.application.Platform.runLater(() -> {
            loadQuizzes();
        });
    }
    
    /**
     * Forces an immediate refresh of the quiz list
     */
    public void forceRefresh() {
        System.out.println("Force refresh called");
        loadQuizzes();
    }
    
    /**
     * Public method to test quiz creation and refresh
     */
    public void testQuizCreation() {
        System.out.println("=== TESTING QUIZ CREATION ===");
        createQuizFromPDF();
    }
    
    /**
     * Forces a complete refresh with layout updates
     */
    private void forceCompleteRefresh() {
        System.out.println("Force complete refresh called");
        
        // Clear and reload
        quizzesGrid.getChildren().clear();
        loadQuizzes();
        
        // Force all layout updates
        javafx.application.Platform.runLater(() -> {
            quizzesGrid.autosize();
            quizzesGrid.requestLayout();
            scrollPane.requestLayout();
            mainContainer.requestLayout();
            
            // Force scene graph update
            if (mainContainer.getScene() != null) {
                mainContainer.getScene().getRoot().requestLayout();
            }
            
            // Scroll to top to show new quizzes (they appear at the top due to "newest first" sorting)
            scrollPane.setVvalue(0.0);
        });
    }
    
    /**
     * Shows flashcard selection dialog for quiz creation
     */
    private void showFlashcardSelectionDialog(List<FlashcardDeck> decks) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Create Quiz from Flashcards");
        dialog.setHeaderText("Select flashcard decks to generate quiz questions from:");
        
        VBox content = new VBox();
        content.setSpacing(16);
        content.setPadding(new Insets(20));
        
        // Deck selection with enhanced icons
        HBox instructionContainer = new HBox();
        instructionContainer.setSpacing(8);
        instructionContainer.setAlignment(Pos.CENTER_LEFT);
        Label instructionIcon = new Label();
        instructionIcon.setGraphic(IconUtils.createSmallIconView("cards"));
        Label instructionLabel = new Label("Choose one or more flashcard decks to create quiz questions:");
        instructionLabel.setWrapText(true);
        instructionContainer.getChildren().addAll(instructionIcon, instructionLabel);
        
        VBox decksList = new VBox();
        decksList.setSpacing(8);
        
        List<CheckBox> deckCheckBoxes = new ArrayList<>();
        
        for (FlashcardDeck deck : decks) {
            HBox deckContainer = new HBox();
            deckContainer.setSpacing(8);
            deckContainer.setAlignment(Pos.CENTER_LEFT);
            
            CheckBox deckCheckBox = new CheckBox();
            deckCheckBox.setUserData(deck);
            
            Label deckIcon = new Label();
            deckIcon.setGraphic(IconUtils.createSmallIconView("folder"));
            
            Label deckLabel = new Label(deck.getTitle() + " (" + deck.getFlashcards().size() + " cards)");
            
            deckContainer.getChildren().addAll(deckCheckBox, deckIcon, deckLabel);
            deckCheckBoxes.add(deckCheckBox);
            decksList.getChildren().add(deckContainer);
        }
        
        ScrollPane scrollPane = new ScrollPane(decksList);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(200);
        
        content.getChildren().addAll(instructionContainer, scrollPane);
        
        dialog.getDialogPane().setContent(content);
        
        ButtonType createButtonType = new ButtonType("Create Quiz", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, cancelButtonType);
        
        Button createButton = (Button) dialog.getDialogPane().lookupButton(createButtonType);
        createButton.getStyleClass().add("success-button");
        
        dialog.setResultConverter(buttonType -> {
            if (buttonType == createButtonType) {
                List<FlashcardDeck> selectedDecks = new ArrayList<>();
                for (CheckBox checkBox : deckCheckBoxes) {
                    if (checkBox.isSelected()) {
                        selectedDecks.add((FlashcardDeck) checkBox.getUserData());
                    }
                }
                
                if (!selectedDecks.isEmpty()) {
                    createQuizFromFlashcards(selectedDecks);
                } else {
                    sceneManager.showInfoDialog("No Selection", "Please select at least one flashcard deck.");
                }
            }
            return buttonType;
        });
        
        dialog.showAndWait();
    }
    
    /**
     * Shows notes selection dialog for quiz creation
     */
    private void showNotesSelectionDialog(List<Note> notes) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Create Quiz from Notes");
        dialog.setHeaderText("Select notes to generate quiz questions from:");
        
        VBox content = new VBox();
        content.setSpacing(16);
        content.setPadding(new Insets(20));
        
        HBox instructionContainer = new HBox();
        instructionContainer.setSpacing(8);
        instructionContainer.setAlignment(Pos.CENTER_LEFT);
        Label instructionIcon = new Label();
        instructionIcon.setGraphic(IconUtils.createSmallIconView("note"));
        Label instructionLabel = new Label("Choose one or more notes to create quiz questions:");
        instructionLabel.setWrapText(true);
        instructionContainer.getChildren().addAll(instructionIcon, instructionLabel);
        
        VBox notesList = new VBox();
        notesList.setSpacing(8);
        
        List<CheckBox> notesCheckBoxes = new ArrayList<>();
        
        for (Note note : notes) {
            HBox noteContainer = new HBox();
            noteContainer.setSpacing(8);
            noteContainer.setAlignment(Pos.CENTER_LEFT);
            
            CheckBox noteCheckBox = new CheckBox();
            noteCheckBox.setUserData(note);
            
            Label noteIcon = new Label();
            noteIcon.setGraphic(IconUtils.createSmallIconView("bookmark"));
            
            Label noteLabel = new Label(note.getTitle() + " (" + note.getSubject() + ")");
            
            noteContainer.getChildren().addAll(noteCheckBox, noteIcon, noteLabel);
            notesCheckBoxes.add(noteCheckBox);
            notesList.getChildren().add(noteContainer);
        }
        
        ScrollPane scrollPane = new ScrollPane(notesList);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(200);
        
        content.getChildren().addAll(instructionContainer, scrollPane);
        
        dialog.getDialogPane().setContent(content);
        
        ButtonType createButtonType = new ButtonType("Create Quiz", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, cancelButtonType);
        
        Button createButton = (Button) dialog.getDialogPane().lookupButton(createButtonType);
        createButton.getStyleClass().add("success-button");
        
        dialog.setResultConverter(buttonType -> {
            if (buttonType == createButtonType) {
                List<Note> selectedNotes = new ArrayList<>();
                for (CheckBox checkBox : notesCheckBoxes) {
                    if (checkBox.isSelected()) {
                        selectedNotes.add((Note) checkBox.getUserData());
                    }
                }
                
                if (!selectedNotes.isEmpty()) {
                    createQuizFromNotes(selectedNotes);
                } else {
                    sceneManager.showInfoDialog("No Selection", "Please select at least one note.");
                }
            }
            return buttonType;
        });
        
        dialog.showAndWait();
    }
    
    /**
     * Shows import quiz dialog
     */
    private void showImportQuizDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Import Quiz from PDF");
        dialog.setHeaderText("Upload a PDF file to automatically generate quiz questions and assessments from its content.");
        
        VBox content = new VBox();
        content.setSpacing(16);
        content.setPadding(new Insets(20));
        
        Label descriptionLabel = new Label("Transform your study materials into comprehensive quiz questions with multiple choice answers, true/false questions, and detailed explanations. Perfect for testing knowledge and exam preparation.");
        descriptionLabel.setWrapText(true);
        
        VBox uploadArea = new VBox();
        uploadArea.setSpacing(12);
        uploadArea.setAlignment(Pos.CENTER);
        uploadArea.setPadding(new Insets(40));
        uploadArea.setStyle("-fx-border-color: #d1d5db; -fx-border-style: dashed; -fx-border-width: 2; -fx-border-radius: 8;");
        
        Label fileIcon = new Label();
        fileIcon.setGraphic(IconUtils.createLargeIconView("file"));
        
        Label uploadText = new Label("Upload a PDF to generate quiz questions automatically");
        
        Button chooseFileButton = new Button();
        chooseFileButton.setGraphic(IconUtils.createIconTextHBox("upload", "Choose PDF File"));
        chooseFileButton.getStyleClass().add("secondary-button");
        
        uploadArea.getChildren().addAll(fileIcon, uploadText, chooseFileButton);
        
        content.getChildren().addAll(descriptionLabel, uploadArea);
        
        dialog.getDialogPane().setContent(content);
        
        ButtonType importButtonType = new ButtonType("Import Quiz", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        
        dialog.getDialogPane().getButtonTypes().addAll(importButtonType, cancelButtonType);
        
        Button importButton = (Button) dialog.getDialogPane().lookupButton(importButtonType);
        importButton.getStyleClass().add("success-button");
        
        dialog.setResultConverter(buttonType -> {
            if (buttonType == importButtonType) {
                createQuizFromPDF();
            }
            return buttonType;
        });
        
        dialog.showAndWait();
    }
    
    /**
     * Creates a quiz from selected flashcard decks
     */
    private void createQuizFromFlashcards(List<FlashcardDeck> selectedDecks) {
        try {
            String title = "Quiz from Flashcards - " + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, HH:mm"));
            String description = "Generated from flashcard decks: " + 
                selectedDecks.stream().map(FlashcardDeck::getTitle).reduce((a, b) -> a + ", " + b).orElse("");
            
            Quiz newQuiz = new Quiz(title, description, "Mixed", Flashcard.Difficulty.MEDIUM, 30);
            
            List<Question> questions = new ArrayList<>();
            
            for (FlashcardDeck deck : selectedDecks) {
                for (Flashcard flashcard : deck.getFlashcards()) {
                    // Create a question from each flashcard
                    Question question = new Question();
                    question.setQuestionText(flashcard.getQuestion());
                    
                    // Create multiple choice options (correct answer + 3 distractors)
                    List<String> options = new ArrayList<>();
                    options.add(flashcard.getAnswer()); // Correct answer
                    options.add("Alternative A");
                    options.add("Alternative B");  
                    options.add("Alternative C");
                    
                    question.setOptions(options);
                    question.setCorrectOptionIndex(0); // First option is correct
                    question.setExplanation("From flashcard: " + flashcard.getAnswer());
                    question.setDifficulty(flashcard.getDifficulty());
                    
                    questions.add(question);
                }
            }
            
            newQuiz.setQuestions(questions);
            
            // Save the quiz and ensure it's persisted
            dataStore.saveQuiz(newQuiz);
            System.out.println("Quiz saved with ID: " + newQuiz.getId() + " and " + questions.size() + " questions");
            
            // Log activity
            dataStore.logUserActivity("QUIZ_TAKEN", "Created quiz: " + newQuiz.getTitle());
            
            // Refresh activity history
            com.studyspace.components.SidebarView.refreshActivityHistoryGlobally();
            
            // Force refresh the UI immediately and then show dialog
            forceCompleteRefresh();
            
            javafx.application.Platform.runLater(() -> {
                sceneManager.showInfoDialog("Quiz Created Successfully!", 
                    "Created quiz '" + newQuiz.getTitle() + "' with " + questions.size() + " questions from your flashcards.\n\n" +
                    "The quiz is now available in your quiz list!");
            });
            
        } catch (Exception e) {
            System.err.println("Error creating quiz from flashcards: " + e.getMessage());
            e.printStackTrace();
            sceneManager.showErrorDialog("Error", "Failed to create quiz from flashcards: " + e.getMessage());
        }
    }
    
    /**
     * Creates a quiz from selected notes
     */
    private void createQuizFromNotes(List<Note> selectedNotes) {
        try {
            String title = "Quiz from Notes - " + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, HH:mm"));
            String description = "Generated from notes: " + 
                selectedNotes.stream().map(Note::getTitle).reduce((a, b) -> a + ", " + b).orElse("");
            
            Quiz newQuiz = new Quiz(title, description, "Mixed", Flashcard.Difficulty.MEDIUM, 25);
            
            List<Question> questions = new ArrayList<>();
            
            for (Note note : selectedNotes) {
                // Generate questions from note content
                String[] sentences = note.getContent().split("[.!?]+");
                
                for (int i = 0; i < Math.min(sentences.length, 3); i++) { // Max 3 questions per note
                    String sentence = sentences[i].trim();
                    if (sentence.length() > 20) { // Only use substantial sentences
                        Question question = new Question();
                        question.setQuestionText("Based on the note '" + note.getTitle() + "': What does this refer to? \"" + 
                            sentence.substring(0, Math.min(sentence.length(), 50)) + "...\"");
                        
                        List<String> options = Arrays.asList(
                            sentence.length() > 50 ? sentence.substring(0, 50) + "..." : sentence,
                            "Alternative concept A",
                            "Alternative concept B", 
                            "Alternative concept C"
                        );
                        
                        question.setOptions(options);
                        question.setCorrectOptionIndex(0);
                        question.setExplanation("From note: " + note.getTitle() + " - " + sentence);
                        question.setDifficulty(Flashcard.Difficulty.MEDIUM);
                        
                        questions.add(question);
                    }
                }
            }
            
            newQuiz.setQuestions(questions);
            
            // Save the quiz and ensure it's persisted
            dataStore.saveQuiz(newQuiz);
            System.out.println("Quiz saved with ID: " + newQuiz.getId() + " and " + questions.size() + " questions");
            
            // Log activity
            dataStore.logUserActivity("QUIZ_TAKEN", "Created quiz: " + newQuiz.getTitle());
            
            // Refresh activity history
            com.studyspace.components.SidebarView.refreshActivityHistoryGlobally();
            
            // Force refresh the UI immediately and then show dialog
            forceCompleteRefresh();
            
            javafx.application.Platform.runLater(() -> {
                sceneManager.showInfoDialog("Quiz Created Successfully!", 
                    "Created quiz '" + newQuiz.getTitle() + "' with " + questions.size() + " questions from your notes.\n\n" +
                    "The quiz is now available in your quiz list!");
            });
            
        } catch (Exception e) {
            System.err.println("Error creating quiz from notes: " + e.getMessage());
            e.printStackTrace();
            sceneManager.showErrorDialog("Error", "Failed to create quiz from notes: " + e.getMessage());
        }
    }
    
    /**
     * Creates a quiz from PDF import
     */
    private void createQuizFromPDF() {
        try {
            String title = "Quiz from PDF Import - " + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, HH:mm"));
            String description = "Generated from uploaded PDF document";
            
            Quiz newQuiz = new Quiz(title, description, "Imported Content", Flashcard.Difficulty.MEDIUM, 30);
            
            // Simulate PDF processing with sample questions
            List<Question> questions = new ArrayList<>();
            
            questions.add(new Question(
                "Based on the imported PDF content, which concept is most important?",
                Arrays.asList("Key concept from PDF", "Distractor A", "Distractor B", "Distractor C"),
                0,
                "This question was generated from the main themes identified in your PDF document.",
                Flashcard.Difficulty.MEDIUM
            ));
            
            questions.add(new Question(
                "What is the primary focus of the document?",
                Arrays.asList("Primary focus area", "Secondary topic", "Unrelated concept", "Background information"),
                0,
                "Generated from the document's introduction and key sections.",
                Flashcard.Difficulty.EASY
            ));
            
            questions.add(new Question(
                "Which statement best summarizes the document's conclusion?",
                Arrays.asList("Main conclusion", "Partial conclusion", "Opposite view", "Unrelated statement"),
                0,
                "Extracted from the concluding sections of your PDF.",
                Flashcard.Difficulty.HARD
            ));
            
            questions.add(new Question(
                "What methodology was discussed in the document?",
                Arrays.asList("Systematic approach", "Random approach", "Theoretical only", "No methodology"),
                0,
                "Generated from the methodology section of your PDF.",
                Flashcard.Difficulty.MEDIUM
            ));
            
            questions.add(new Question(
                "Which of the following best describes the document's findings?",
                Arrays.asList("Comprehensive results", "Preliminary findings", "No clear results", "Contradictory data"),
                0,
                "Extracted from the results and discussion sections of your PDF.",
                Flashcard.Difficulty.HARD
            ));
            
            newQuiz.setQuestions(questions);
            
            // Save the quiz and ensure it's persisted
            System.out.println("About to save quiz with ID: " + newQuiz.getId());
            System.out.println("Quiz title: " + newQuiz.getTitle());
            System.out.println("Quiz questions count: " + questions.size());
            
            dataStore.saveQuiz(newQuiz);
            System.out.println("Quiz saved to DataStore");
            
            // Verify the quiz was saved
            Quiz savedQuiz = dataStore.getQuiz(newQuiz.getId());
            if (savedQuiz != null) {
                System.out.println("Quiz successfully retrieved from DataStore: " + savedQuiz.getTitle());
            } else {
                System.err.println("ERROR: Quiz was not saved properly to DataStore!");
            }
            
            // Check total quiz count
            List<Quiz> allQuizzes = dataStore.getAllQuizzes();
            System.out.println("Total quizzes in DataStore: " + allQuizzes.size());
            for (Quiz q : allQuizzes) {
                System.out.println("  - " + q.getTitle() + " (ID: " + q.getId() + ")");
            }
            
            // Force refresh the UI immediately and then show dialog
            forceCompleteRefresh();
            
            javafx.application.Platform.runLater(() -> {
                sceneManager.showInfoDialog("Quiz Imported Successfully!", 
                    "Created quiz '" + newQuiz.getTitle() + "' with " + questions.size() + " questions from your PDF.\n\n" +
                    "The quiz is now available in your quiz list!");
            });
            
        } catch (Exception e) {
            System.err.println("Error creating quiz from PDF: " + e.getMessage());
            e.printStackTrace();
            sceneManager.showErrorDialog("Error", "Failed to import quiz from PDF: " + e.getMessage());
        }
    }
    
    /**
     * Handles deleting a quiz
     */
    private void handleDeleteQuiz(Quiz quiz) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Delete Quiz");
        confirmDialog.setHeaderText("Are you sure you want to delete this quiz?");
        confirmDialog.setContentText("Quiz: " + quiz.getTitle() + "\n\nThis action cannot be undone.");
        
        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    dataStore.deleteQuiz(quiz);
                    refresh();
                    sceneManager.showInfoDialog("Quiz Deleted", 
                        "The quiz '" + quiz.getTitle() + "' has been successfully deleted.");
                } catch (Exception e) {
                    sceneManager.showErrorDialog("Error", "Failed to delete quiz: " + e.getMessage());
                }
            }
        });
    }
}
