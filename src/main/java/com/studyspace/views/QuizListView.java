package com.studyspace.views;

import com.studyspace.models.Quiz;
import com.studyspace.models.FlashcardDeck;
import com.studyspace.models.Flashcard;
import com.studyspace.models.Note;
import com.studyspace.utils.DataStore;
import com.studyspace.utils.SceneManager;
import com.studyspace.utils.IconUtils;
import com.studyspace.utils.QuizGenerationService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;
import java.util.ArrayList;

//============ quiz list view =============
//this is where quizzes are displayed and managed

public class QuizListView {
    
    private final DataStore dataStore;
    private final SceneManager sceneManager;
    private final QuizGenerationService quizGenerationService;
    
    private VBox mainContainer;
    private ScrollPane scrollPane;
    private FlowPane quizzesGrid;
    private ComboBox<String> sortComboBox;
    
    public QuizListView() {
        this.dataStore = DataStore.getInstance();
        this.sceneManager = SceneManager.getInstance();
        this.quizGenerationService = new QuizGenerationService();
        
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
        
        actionBar.getChildren().addAll(createFromFlashcardsButton, createFromNotesButton, sortContainer);
        
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
     * Shows flashcard selection dialog for AI-powered quiz creation
     */
    private void showFlashcardSelectionDialog(List<FlashcardDeck> decks) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Create AI Quiz from Flashcards");
        dialog.setHeaderText("Select flashcard decks and configure your AI-generated quiz:");
        
        VBox content = new VBox();
        content.setSpacing(16);
        content.setPadding(new Insets(20));
        
        // AI-powered instruction
        HBox instructionContainer = new HBox();
        instructionContainer.setSpacing(8);
        instructionContainer.setAlignment(Pos.CENTER_LEFT);
        Label instructionIcon = new Label();
        instructionIcon.setGraphic(IconUtils.createSmallIconView("brain"));
        Label instructionLabel = new Label("AI will analyze your flashcards and create intelligent quiz questions:");
        instructionLabel.setWrapText(true);
        instructionContainer.getChildren().addAll(instructionIcon, instructionLabel);
        
        // Deck selection
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
        scrollPane.setPrefHeight(150);
        
        // Quiz configuration
        VBox configContainer = new VBox();
        configContainer.setSpacing(12);
        
        Label configLabel = new Label("Quiz Configuration:");
        configLabel.getStyleClass().addAll("text-sm", "font-semibold");
        
        // Quiz title
        HBox titleContainer = new HBox();
        titleContainer.setSpacing(8);
        titleContainer.setAlignment(Pos.CENTER_LEFT);
        Label titleLabel = new Label("Quiz Title:");
        TextField titleField = new TextField();
        titleField.setPromptText("Enter quiz title...");
        titleField.setPrefWidth(200);
        titleContainer.getChildren().addAll(titleLabel, titleField);
        
        // Subject
        HBox subjectContainer = new HBox();
        subjectContainer.setSpacing(8);
        subjectContainer.setAlignment(Pos.CENTER_LEFT);
        Label subjectLabel = new Label("Subject:");
        TextField subjectField = new TextField();
        subjectField.setPromptText("Enter subject...");
        subjectField.setPrefWidth(200);
        subjectContainer.getChildren().addAll(subjectLabel, subjectField);
        
        // Difficulty
        HBox difficultyContainer = new HBox();
        difficultyContainer.setSpacing(8);
        difficultyContainer.setAlignment(Pos.CENTER_LEFT);
        Label difficultyLabel = new Label("Difficulty:");
        ComboBox<Flashcard.Difficulty> difficultyCombo = new ComboBox<>();
        difficultyCombo.getItems().addAll(Flashcard.Difficulty.values());
        difficultyCombo.setValue(Flashcard.Difficulty.MEDIUM);
        difficultyContainer.getChildren().addAll(difficultyLabel, difficultyCombo);
        
        // Question count
        HBox questionCountContainer = new HBox();
        questionCountContainer.setSpacing(8);
        questionCountContainer.setAlignment(Pos.CENTER_LEFT);
        Label questionCountLabel = new Label("Questions:");
        ComboBox<Integer> questionCountCombo = new ComboBox<>();
        questionCountCombo.getItems().addAll(5, 10, 15, 20, 25, 30);
        questionCountCombo.setValue(15);
        questionCountContainer.getChildren().addAll(questionCountLabel, questionCountCombo);
        
        // Time limit
        HBox timeLimitContainer = new HBox();
        timeLimitContainer.setSpacing(8);
        timeLimitContainer.setAlignment(Pos.CENTER_LEFT);
        Label timeLimitLabel = new Label("Time Limit (min):");
        ComboBox<Integer> timeLimitCombo = new ComboBox<>();
        timeLimitCombo.getItems().addAll(10, 15, 20, 30, 45, 60);
        timeLimitCombo.setValue(20);
        timeLimitContainer.getChildren().addAll(timeLimitLabel, timeLimitCombo);
        
        configContainer.getChildren().addAll(configLabel, titleContainer, subjectContainer, 
                                           difficultyContainer, questionCountContainer, timeLimitContainer);
        
        content.getChildren().addAll(instructionContainer, scrollPane, configContainer);
        
        dialog.getDialogPane().setContent(content);
        
        ButtonType createButtonType = new ButtonType("Generate AI Quiz", ButtonBar.ButtonData.OK_DONE);
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
                    String quizTitle = titleField.getText().trim();
                    if (quizTitle.isEmpty()) {
                        quizTitle = "AI Quiz from " + selectedDecks.size() + " Deck(s)";
                    }
                    
                    String subject = subjectField.getText().trim();
                    if (subject.isEmpty()) {
                        subject = "Mixed Subjects";
                    }
                    
                    createAIGeneratedQuizFromFlashcards(selectedDecks, quizTitle, subject, 
                                                      difficultyCombo.getValue(), 
                                                      timeLimitCombo.getValue(),
                                                      questionCountCombo.getValue());
                } else {
                    sceneManager.showInfoDialog("No Selection", "Please select at least one flashcard deck.");
                }
            }
            return buttonType;
        });
        
        dialog.showAndWait();
    }
    
    /**
     * Shows notes selection dialog for AI-powered quiz creation
     */
    private void showNotesSelectionDialog(List<Note> notes) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Create AI Quiz from Notes");
        dialog.setHeaderText("Select notes and configure your AI-generated quiz:");
        
        VBox content = new VBox();
        content.setSpacing(16);
        content.setPadding(new Insets(20));
        
        // AI-powered instruction
        HBox instructionContainer = new HBox();
        instructionContainer.setSpacing(8);
        instructionContainer.setAlignment(Pos.CENTER_LEFT);
        Label instructionIcon = new Label();
        instructionIcon.setGraphic(IconUtils.createSmallIconView("brain"));
        Label instructionLabel = new Label("AI will analyze your notes and create intelligent quiz questions:");
        instructionLabel.setWrapText(true);
        instructionContainer.getChildren().addAll(instructionIcon, instructionLabel);
        
        // Note selection
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
        scrollPane.setPrefHeight(150);
        
        // Quiz configuration
        VBox configContainer = new VBox();
        configContainer.setSpacing(12);
        
        Label configLabel = new Label("Quiz Configuration:");
        configLabel.getStyleClass().addAll("text-sm", "font-semibold");
        
        // Quiz title
        HBox titleContainer = new HBox();
        titleContainer.setSpacing(8);
        titleContainer.setAlignment(Pos.CENTER_LEFT);
        Label titleLabel = new Label("Quiz Title:");
        TextField titleField = new TextField();
        titleField.setPromptText("Enter quiz title...");
        titleField.setPrefWidth(200);
        titleContainer.getChildren().addAll(titleLabel, titleField);
        
        // Subject
        HBox subjectContainer = new HBox();
        subjectContainer.setSpacing(8);
        subjectContainer.setAlignment(Pos.CENTER_LEFT);
        Label subjectLabel = new Label("Subject:");
        TextField subjectField = new TextField();
        subjectField.setPromptText("Enter subject...");
        subjectField.setPrefWidth(200);
        subjectContainer.getChildren().addAll(subjectLabel, subjectField);
        
        // Difficulty
        HBox difficultyContainer = new HBox();
        difficultyContainer.setSpacing(8);
        difficultyContainer.setAlignment(Pos.CENTER_LEFT);
        Label difficultyLabel = new Label("Difficulty:");
        ComboBox<Flashcard.Difficulty> difficultyCombo = new ComboBox<>();
        difficultyCombo.getItems().addAll(Flashcard.Difficulty.values());
        difficultyCombo.setValue(Flashcard.Difficulty.MEDIUM);
        difficultyContainer.getChildren().addAll(difficultyLabel, difficultyCombo);
        
        // Question count
        HBox questionCountContainer = new HBox();
        questionCountContainer.setSpacing(8);
        questionCountContainer.setAlignment(Pos.CENTER_LEFT);
        Label questionCountLabel = new Label("Questions:");
        ComboBox<Integer> questionCountCombo = new ComboBox<>();
        questionCountCombo.getItems().addAll(5, 10, 15, 20, 25, 30);
        questionCountCombo.setValue(15);
        questionCountContainer.getChildren().addAll(questionCountLabel, questionCountCombo);
        
        // Time limit
        HBox timeLimitContainer = new HBox();
        timeLimitContainer.setSpacing(8);
        timeLimitContainer.setAlignment(Pos.CENTER_LEFT);
        Label timeLimitLabel = new Label("Time Limit (min):");
        ComboBox<Integer> timeLimitCombo = new ComboBox<>();
        timeLimitCombo.getItems().addAll(10, 15, 20, 30, 45, 60);
        timeLimitCombo.setValue(20);
        timeLimitContainer.getChildren().addAll(timeLimitLabel, timeLimitCombo);
        
        configContainer.getChildren().addAll(configLabel, titleContainer, subjectContainer, 
                                           difficultyContainer, questionCountContainer, timeLimitContainer);
        
        content.getChildren().addAll(instructionContainer, scrollPane, configContainer);
        
        dialog.getDialogPane().setContent(content);
        
        ButtonType createButtonType = new ButtonType("Generate AI Quiz", ButtonBar.ButtonData.OK_DONE);
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
                    String quizTitle = titleField.getText().trim();
                    if (quizTitle.isEmpty()) {
                        quizTitle = "AI Quiz from " + selectedNotes.size() + " Note(s)";
                    }
                    
                    String subject = subjectField.getText().trim();
                    if (subject.isEmpty()) {
                        subject = "Mixed Subjects";
                    }
                    
                    createAIGeneratedQuizFromNotes(selectedNotes, quizTitle, subject, 
                                                 difficultyCombo.getValue(), 
                                                 timeLimitCombo.getValue(),
                                                 questionCountCombo.getValue());
                } else {
                    sceneManager.showInfoDialog("No Selection", "Please select at least one note.");
                }
            }
            return buttonType;
        });
        
        dialog.showAndWait();
    }
    
    
    /**
     * Creates an AI-generated quiz from selected flashcard decks
     */
    private void createAIGeneratedQuizFromFlashcards(List<FlashcardDeck> selectedDecks, String quizTitle, 
                                                    String subject, Flashcard.Difficulty difficulty, 
                                                    int timeLimit, int questionCount) {
        try {
            // Show progress dialog
            Alert progressDialog = new Alert(Alert.AlertType.INFORMATION);
            progressDialog.setTitle("Generating AI Quiz");
            progressDialog.setHeaderText("AI is analyzing your flashcards and creating intelligent questions...");
            progressDialog.setContentText("This may take a few moments. Please wait.");
            progressDialog.show();
            
            // Run AI generation in background thread
            javafx.concurrent.Task<QuizGenerationService.QuizGenerationResult> task = new javafx.concurrent.Task<QuizGenerationService.QuizGenerationResult>() {
                @Override
                protected QuizGenerationService.QuizGenerationResult call() throws Exception {
                    return quizGenerationService.generateQuizFromFlashcards(selectedDecks, quizTitle, subject, difficulty, timeLimit, questionCount);
                }
            };
            
            task.setOnSucceeded(e -> {
                progressDialog.close();
                QuizGenerationService.QuizGenerationResult result = task.getValue();
                
                if (result.isSuccess() && result.getQuiz() != null) {
                    // Save the AI-generated quiz
                    dataStore.saveQuiz(result.getQuiz());
                    
                    // Log activity
                    dataStore.logUserActivity("QUIZ_CREATED", "Created AI quiz: " + result.getQuiz().getTitle());
                    
                    // Refresh activity history and all views
                    com.studyspace.components.SidebarView.refreshActivityHistoryGlobally();
                    com.studyspace.components.SidebarView.refreshAllViewsGlobally();
                    
                    // Force refresh the UI
                    forceCompleteRefresh();
                    
                    // Show success dialog
                    javafx.application.Platform.runLater(() -> {
                        sceneManager.showInfoDialog("AI Quiz Generated Successfully!", 
                            "Created AI quiz '" + result.getQuiz().getTitle() + "' with " + 
                            result.getQuiz().getQuestionCount() + " intelligent questions from your flashcards.\n\n" +
                            "The quiz is now available in your quiz list!");
                    });
                } else {
                    // Show error dialog
                    javafx.application.Platform.runLater(() -> {
                        sceneManager.showErrorDialog("AI Generation Failed", 
                            "Failed to generate AI quiz: " + result.getMessage() + 
                            "\n\nPlease check that the AI service is running and try again.");
                    });
                }
            });
            
            task.setOnFailed(e -> {
                progressDialog.close();
                javafx.application.Platform.runLater(() -> {
                    sceneManager.showErrorDialog("AI Generation Error", 
                        "An error occurred while generating the AI quiz: " + task.getException().getMessage() +
                        "\n\nPlease check that the AI service is running and try again.");
                });
            });
            
            // Start the task
            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();
            
        } catch (Exception e) {
            System.err.println("Error creating AI quiz from flashcards: " + e.getMessage());
            e.printStackTrace();
            sceneManager.showErrorDialog("Error", "Failed to create AI quiz from flashcards: " + e.getMessage());
        }
    }
    
    /**
     * Creates an AI-generated quiz from selected notes
     */
    private void createAIGeneratedQuizFromNotes(List<Note> selectedNotes, String quizTitle, 
                                               String subject, Flashcard.Difficulty difficulty, 
                                               int timeLimit, int questionCount) {
        try {
            // Show progress dialog
            Alert progressDialog = new Alert(Alert.AlertType.INFORMATION);
            progressDialog.setTitle("Generating AI Quiz");
            progressDialog.setHeaderText("AI is analyzing your notes and creating intelligent questions...");
            progressDialog.setContentText("This may take a few moments. Please wait.");
            progressDialog.show();
            
            // Run AI generation in background thread
            javafx.concurrent.Task<QuizGenerationService.QuizGenerationResult> task = new javafx.concurrent.Task<QuizGenerationService.QuizGenerationResult>() {
                @Override
                protected QuizGenerationService.QuizGenerationResult call() throws Exception {
                    return quizGenerationService.generateQuizFromNotes(selectedNotes, quizTitle, subject, difficulty, timeLimit, questionCount);
                }
            };
            
            task.setOnSucceeded(e -> {
                progressDialog.close();
                QuizGenerationService.QuizGenerationResult result = task.getValue();
                
                if (result.isSuccess() && result.getQuiz() != null) {
                    // Save the AI-generated quiz
                    dataStore.saveQuiz(result.getQuiz());
                    
                    // Log activity
                    dataStore.logUserActivity("QUIZ_CREATED", "Created AI quiz: " + result.getQuiz().getTitle());
                    
                    // Refresh activity history and all views
                    com.studyspace.components.SidebarView.refreshActivityHistoryGlobally();
                    com.studyspace.components.SidebarView.refreshAllViewsGlobally();
                    
                    // Force refresh the UI
                    forceCompleteRefresh();
                    
                    // Show success dialog
                    javafx.application.Platform.runLater(() -> {
                        sceneManager.showInfoDialog("AI Quiz Generated Successfully!", 
                            "Created AI quiz '" + result.getQuiz().getTitle() + "' with " + 
                            result.getQuiz().getQuestionCount() + " intelligent questions from your notes.\n\n" +
                            "The quiz is now available in your quiz list!");
                    });
                } else {
                    // Show error dialog
                    javafx.application.Platform.runLater(() -> {
                        sceneManager.showErrorDialog("AI Generation Failed", 
                            "Failed to generate AI quiz: " + result.getMessage() + 
                            "\n\nPlease check that the AI service is running and try again.");
                    });
                }
            });
            
            task.setOnFailed(e -> {
                progressDialog.close();
                javafx.application.Platform.runLater(() -> {
                    sceneManager.showErrorDialog("AI Generation Error", 
                        "An error occurred while generating the AI quiz: " + task.getException().getMessage() +
                        "\n\nPlease check that the AI service is running and try again.");
                });
            });
            
            // Start the task
            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();
            
        } catch (Exception e) {
            System.err.println("Error creating AI quiz from notes: " + e.getMessage());
            e.printStackTrace();
            sceneManager.showErrorDialog("Error", "Failed to create AI quiz from notes: " + e.getMessage());
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
                    dataStore.deleteQuiz(quiz.getId());
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
