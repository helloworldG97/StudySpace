package com.studyspace.views;

import java.util.List;
import com.studyspace.models.Question;
import com.studyspace.models.Quiz;
import com.studyspace.utils.DataStore;
import com.studyspace.utils.IconUtils;
import com.studyspace.utils.SceneManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * QuizManagementView - Manage questions within a quiz
 */
public class QuizManagementView {
    
    private final DataStore dataStore;
    private final SceneManager sceneManager;
    private final Quiz quiz;
    private final QuizListView parentView;
    
    private VBox mainContainer;
    private VBox questionsContainer;
    private ScrollPane scrollPane;
    private ObservableList<Question> questionsList;
    private javafx.collections.transformation.FilteredList<Question> filteredQuestions;
    private javafx.collections.transformation.SortedList<Question> sortedQuestions;
    private TextField searchField;
    private ComboBox<String> sortComboBox;
    
    public QuizManagementView(Quiz quiz, QuizListView parentView) {
        this.dataStore = DataStore.getInstance();
        this.sceneManager = SceneManager.getInstance();
        this.quiz = quiz;
        this.parentView = parentView;
        this.questionsList = FXCollections.observableArrayList(quiz.getQuestions());
        this.filteredQuestions = new javafx.collections.transformation.FilteredList<>(questionsList);
        this.sortedQuestions = new javafx.collections.transformation.SortedList<>(filteredQuestions);
        
        initializeUI();
        setupEventHandlers();
        loadQuestions();
    }
    
    /**
     * Sets up event handlers for search and sort
     */
    private void setupEventHandlers() {
        // Search functionality
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredQuestions.setPredicate(question -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return question.getQuestionText().toLowerCase().contains(lowerCaseFilter) ||
                       question.getCorrectAnswer().toLowerCase().contains(lowerCaseFilter) ||
                       question.getOptions().stream().anyMatch(option -> 
                           option.toLowerCase().contains(lowerCaseFilter));
            });
            
            // Reload questions to show filtered results
            loadQuestions();
        });
        
        // Sort functionality
        if (sortComboBox != null) {
            sortComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    applySorting(newValue);
                }
            });
        }
    }
    
    /**
     * Applies sorting based on selected option
     */
    private void applySorting(String sortOption) {
        switch (sortOption) {
            case "Recently Added":
                // Sort by question text (alphabetical) since no creation date available
                sortedQuestions.setComparator((a, b) -> a.getQuestionText().compareToIgnoreCase(b.getQuestionText()));
                break;
            case "Alphabetical (A-Z)":
                sortedQuestions.setComparator((a, b) -> a.getQuestionText().compareToIgnoreCase(b.getQuestionText()));
                break;
            case "Alphabetical (Z-A)":
                sortedQuestions.setComparator((a, b) -> b.getQuestionText().compareToIgnoreCase(a.getQuestionText()));
                break;
            case "Difficulty (Easy to Hard)":
                sortedQuestions.setComparator((a, b) -> a.getDifficulty().compareTo(b.getDifficulty()));
                break;
            case "Difficulty (Hard to Easy)":
                sortedQuestions.setComparator((a, b) -> b.getDifficulty().compareTo(a.getDifficulty()));
                break;
            case "Type (Multiple Choice First)":
                sortedQuestions.setComparator((a, b) -> {
                    // Multiple choice questions first, then others
                    boolean aIsMultiple = a.getOptions().size() > 1;
                    boolean bIsMultiple = b.getOptions().size() > 1;
                    if (aIsMultiple && !bIsMultiple) return -1;
                    if (!aIsMultiple && bIsMultiple) return 1;
                    return 0;
                });
                break;
            default:
                // Default to alphabetical
                sortedQuestions.setComparator((a, b) -> a.getQuestionText().compareToIgnoreCase(b.getQuestionText()));
        }
        
        // Reload questions to show sorted results
        loadQuestions();
    }
    
    /**
     * Initializes the UI components
     */
    private void initializeUI() {
        mainContainer = new VBox();
        mainContainer.getStyleClass().add("quiz-management-container");
        mainContainer.setSpacing(24);
        mainContainer.setPadding(new Insets(32));
        
        // Header section
        VBox headerSection = createHeaderSection();
        
        // Action buttons section
        HBox actionSection = createActionSection();
        
        // Search and filter section
        HBox searchFilterSection = createSearchFilterSection();
        
        // Questions container
        questionsContainer = new VBox();
        questionsContainer.getStyleClass().add("questions-management-container");
        questionsContainer.setSpacing(16);
        
        // Create scroll pane for questions
        scrollPane = new ScrollPane(questionsContainer);
        scrollPane.getStyleClass().add("questions-scroll-pane");
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
        searchField.setPromptText("Search questions...");
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
            "Recently Added", 
            "Alphabetical (A-Z)", 
            "Alphabetical (Z-A)",
            "Difficulty (Easy to Hard)",
            "Difficulty (Hard to Easy)",
            "Type (Multiple Choice First)"
        );
        sortComboBox.setValue("Recently Added");
        
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
        
        // Title and quiz info
        HBox titleRow = new HBox();
        titleRow.setAlignment(Pos.CENTER_LEFT);
        titleRow.setSpacing(12);
        
        Label titleIcon = new Label();
        titleIcon.setGraphic(IconUtils.createMediumIconView("question"));
        
        VBox titleSection = new VBox();
        titleSection.setSpacing(4);
        
        Label titleLabel = new Label(quiz.getTitle());
        titleLabel.getStyleClass().addAll("text-2xl", "font-bold", "text-primary");
        
        Label questionCountLabel = new Label("(" + quiz.getQuestionCount() + " questions)");
        questionCountLabel.getStyleClass().addAll("text-lg", "text-secondary");
        
        titleSection.getChildren().addAll(titleLabel, questionCountLabel);
        titleRow.getChildren().addAll(titleIcon, titleSection);
        
        // Quiz metadata
        HBox metaInfo = new HBox();
        metaInfo.setSpacing(16);
        metaInfo.setAlignment(Pos.CENTER_LEFT);
        
        Label difficultyLabel = new Label(quiz.getDifficulty().getDisplayName());
        difficultyLabel.getStyleClass().addAll("difficulty-badge", 
            quiz.getDifficulty().name().toLowerCase());
        
        Label subjectLabel = new Label();
        subjectLabel.setGraphic(IconUtils.createIconTextHBox("folder", quiz.getSubject()));
        subjectLabel.getStyleClass().addAll("text-sm", "text-secondary");
        
        Label timeLimitLabel = new Label();
        timeLimitLabel.setGraphic(IconUtils.createIconTextHBox("clock", quiz.getTimeLimitText()));
        timeLimitLabel.getStyleClass().addAll("text-sm", "text-secondary");
        
        Label descriptionLabel = new Label(quiz.getDescription());
        descriptionLabel.getStyleClass().addAll("text-sm", "text-muted");
        descriptionLabel.setWrapText(true);
        
        metaInfo.getChildren().addAll(difficultyLabel, subjectLabel, timeLimitLabel, descriptionLabel);
        
        header.getChildren().addAll(titleRow, metaInfo);
        
        return header;
    }
    
    /**
     * Creates the action buttons section
     */
    private HBox createActionSection() {
        HBox actionSection = new HBox();
        actionSection.setSpacing(12);
        actionSection.setAlignment(Pos.CENTER_LEFT);
        
        Button addQuestionButton = new Button();
        addQuestionButton.setGraphic(IconUtils.createIconTextHBox("add", "Add Question"));
        addQuestionButton.getStyleClass().add("primary-button");
        addQuestionButton.setOnAction(e -> handleAddQuestion());
        
        Button startQuizButton = new Button();
        startQuizButton.setGraphic(IconUtils.createIconTextHBox("question", "Start Quiz"));
        startQuizButton.getStyleClass().add("success-button");
        startQuizButton.setOnAction(e -> handleStartQuiz());
        startQuizButton.setDisable(quiz.getQuestionCount() == 0);
        
        Button backButton = new Button();
        backButton.setGraphic(IconUtils.createIconTextHBox("home", "Back to Quizzes"));
        backButton.getStyleClass().add("secondary-button");
        backButton.setOnAction(e -> handleBackToQuizzes());
        
        actionSection.getChildren().addAll(addQuestionButton, startQuizButton, backButton);
        
        return actionSection;
    }
    
    /**
     * Loads and displays questions
     */
    private void loadQuestions() {
        questionsContainer.getChildren().clear();
        
        if (sortedQuestions.isEmpty()) {
            showEmptyState();
        } else {
            int questionNumber = 1;
            for (Question question : sortedQuestions) {
                VBox questionCard = createQuestionCard(question, questionNumber++);
                questionsContainer.getChildren().add(questionCard);
            }
        }
    }
    
    /**
     * Creates a question card for management
     */
    private VBox createQuestionCard(Question question, int questionNumber) {
        VBox card = new VBox();
        card.getStyleClass().add("question-management-card");
        card.setSpacing(12);
        card.setPadding(new Insets(16));
        
        // Add hover effect for better visual feedback
        card.setOnMouseEntered(e -> {
            card.getStyleClass().add("question-management-card-hover");
        });
        
        card.setOnMouseExited(e -> {
            card.getStyleClass().remove("question-management-card-hover");
        });
        
        // Header with question number and actions
        HBox cardHeader = new HBox();
        cardHeader.setAlignment(Pos.CENTER_LEFT);
        cardHeader.setSpacing(12);
        
        VBox contentSection = new VBox();
        contentSection.setSpacing(8);
        contentSection.setPrefWidth(400);
        contentSection.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(contentSection, Priority.ALWAYS);
        
        HBox questionNumberRow = new HBox();
        questionNumberRow.setAlignment(Pos.CENTER_LEFT);
        questionNumberRow.setSpacing(8);
        
        Label questionIcon = new Label();
        questionIcon.setGraphic(IconUtils.createSmallIconView("question"));
        
        Label questionNumberLabel = new Label("Question " + questionNumber);
        questionNumberLabel.getStyleClass().addAll("text-sm", "font-semibold", "text-primary");
        
        questionNumberRow.getChildren().addAll(questionIcon, questionNumberLabel);
        
        Label questionTextLabel = new Label(question.getQuestionText());
        questionTextLabel.getStyleClass().add("question-text");
        questionTextLabel.setWrapText(true);
        
        // Show correct answer
        HBox correctAnswerRow = new HBox();
        correctAnswerRow.setAlignment(Pos.CENTER_LEFT);
        correctAnswerRow.setSpacing(8);
        
        Label checkIcon = new Label();
        checkIcon.setGraphic(IconUtils.createSmallIconView("check"));
        
        Label correctAnswerLabel = new Label("Correct: " + question.getCorrectAnswer());
        correctAnswerLabel.getStyleClass().addAll("text-sm", "text-success");
        correctAnswerLabel.setWrapText(true);
        
        correctAnswerRow.getChildren().addAll(checkIcon, correctAnswerLabel);
        
        // Show options
        VBox optionsSection = new VBox();
        optionsSection.setSpacing(4);
        optionsSection.setPadding(new Insets(8, 0, 0, 0));
        
        Label optionsLabel = new Label("Options:");
        optionsLabel.getStyleClass().addAll("text-sm", "font-medium");
        
        optionsSection.getChildren().add(optionsLabel);
        
        // Add options
        List<String> options = question.getOptions();
        
        for (int i = 0; i < options.size(); i++) {
            String option = options.get(i);
            String prefix = (char)('A' + i) + ". ";
            Label optionLabel = new Label(prefix + option);
            optionLabel.getStyleClass().addAll("text-sm", "text-muted");
            optionsSection.getChildren().add(optionLabel);
        }
        
        contentSection.getChildren().addAll(questionNumberRow, questionTextLabel, correctAnswerRow, optionsSection);
        
        // Action buttons
        HBox actionButtons = new HBox();
        actionButtons.setSpacing(8);
        actionButtons.setAlignment(Pos.CENTER_RIGHT);
        
        Button editButton = new Button();
        editButton.setGraphic(IconUtils.createSmallIconView("edit"));
        editButton.getStyleClass().addAll("action-button", "edit");
        editButton.setTooltip(new Tooltip("Edit Question"));
        editButton.setOnAction(e -> {
            e.consume(); // Prevent event bubbling to the card
            handleEditQuestion(question);
        });
        
        Button deleteButton = new Button();
        deleteButton.setGraphic(IconUtils.createSmallIconView("trash"));
        deleteButton.getStyleClass().addAll("action-button", "delete");
        deleteButton.setTooltip(new Tooltip("Delete Question"));
        deleteButton.setOnAction(e -> {
            e.consume(); // Prevent event bubbling to the card
            handleDeleteQuestion(question);
        });
        
        actionButtons.getChildren().addAll(editButton, deleteButton);
        
        cardHeader.getChildren().addAll(contentSection, actionButtons);
        
        // Footer with metadata
        HBox cardFooter = new HBox();
        cardFooter.setSpacing(16);
        cardFooter.setAlignment(Pos.CENTER_LEFT);
        
        Label typeLabel = new Label("Multiple Choice");
        typeLabel.getStyleClass().addAll("text-xs", "badge", "type-badge");
        
        Label difficultyLabel = new Label(question.getDifficulty().getDisplayName());
        difficultyLabel.getStyleClass().addAll("difficulty-badge", 
            question.getDifficulty().name().toLowerCase());
        
        cardFooter.getChildren().addAll(typeLabel, difficultyLabel);
        
        card.getChildren().addAll(cardHeader, cardFooter);
        
        // Add click handler for inline editing
        card.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                handleEditQuestion(question);
            }
        });
        
        // Add cursor style to indicate clickable
        card.setStyle("-fx-cursor: hand;");
        
        return card;
    }
    
    /**
     * Shows empty state when no questions exist
     */
    private void showEmptyState() {
        VBox emptyState = new VBox();
        emptyState.getStyleClass().add("empty-state");
        emptyState.setAlignment(Pos.CENTER);
        emptyState.setSpacing(16);
        emptyState.setPadding(new Insets(48));
        
        Label emptyIcon = new Label("❓");
        emptyIcon.getStyleClass().add("empty-icon");
        
        Label emptyTitle = new Label("No questions in this quiz");
        emptyTitle.getStyleClass().add("empty-title");
        
        Label emptyDescription = new Label("Add your first question to start building this quiz!");
        emptyDescription.getStyleClass().add("empty-description");
        
        Button addFirstQuestionButton = new Button("+ Add Your First Question");
        addFirstQuestionButton.getStyleClass().add("primary-button");
        addFirstQuestionButton.setOnAction(e -> handleAddQuestion());
        
        emptyState.getChildren().addAll(emptyIcon, emptyTitle, emptyDescription, addFirstQuestionButton);
        questionsContainer.getChildren().add(emptyState);
    }
    
    /**
     * Handles adding a new question
     */
    private void handleAddQuestion() {
        Dialog<Question> dialog = new Dialog<>();
        dialog.setTitle("Add New Question");
        dialog.setHeaderText("Create a new question for this quiz");
        
        // Create dialog content
        VBox content = createQuestionForm(null);
        dialog.getDialogPane().setContent(content);
        
        // Add buttons
        ButtonType createButtonType = new ButtonType("Add Question", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, cancelButtonType);
        
        // Set result converter
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                return createQuestionFromForm();
            }
            return null;
        });
        
        // Show dialog and handle result
        dialog.showAndWait().ifPresent(newQuestion -> {
            if (newQuestion != null) {
                quiz.addQuestion(newQuestion);
                questionsList.add(newQuestion);
                dataStore.saveQuiz(quiz);
                loadQuestions();
            }
        });
    }
    
    /**
     * Handles editing an existing question
     */
    private void handleEditQuestion(Question question) {
        Dialog<Question> dialog = new Dialog<>();
        dialog.setTitle("Edit Question");
        dialog.setHeaderText("Edit this question");
        
        // Create dialog content
        VBox content = createQuestionForm(question);
        dialog.getDialogPane().setContent(content);
        
        // Add buttons
        ButtonType saveButtonType = new ButtonType("Save Changes", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);
        
        // Set result converter
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return updateQuestionFromForm(question);
            }
            return null;
        });
        
        // Show dialog and handle result
        dialog.showAndWait().ifPresent(updatedQuestion -> {
            if (updatedQuestion != null) {
                int index = questionsList.indexOf(question);
                questionsList.set(index, updatedQuestion);
                dataStore.saveQuiz(quiz);
                loadQuestions();
            }
        });
    }
    
    /**
     * Handles deleting a question
     */
    private void handleDeleteQuestion(Question question) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Delete Question");
        confirmDialog.setHeaderText("Are you sure you want to delete this question?");
        confirmDialog.setContentText("This action cannot be undone.");
        
        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                quiz.removeQuestion(question);
                questionsList.remove(question);
                dataStore.saveQuiz(quiz);
                loadQuestions();
            }
        });
    }
    
    /**
     * Handles starting the quiz
     */
    private void handleStartQuiz() {
        if (quiz.getQuestionCount() == 0) {
            sceneManager.showInfoDialog("Empty Quiz", 
                "This quiz doesn't have any questions yet. Add some questions first!");
            return;
        }
        
        // Create and show quiz mode view
        QuizModeView quizModeView = new QuizModeView(quiz, parentView);
        
        // Replace current content with quiz mode view
        VBox parent = (VBox) mainContainer.getParent();
        if (parent != null) {
            parent.getChildren().clear();
            parent.getChildren().add(quizModeView.getView());
        }
    }
    
    /**
     * Handles returning to the quiz list
     */
    private void handleBackToQuizzes() {
        // Refresh the parent view to show updated statistics
        parentView.refresh();
        
        // Replace current content with the quiz list
        VBox parent = (VBox) mainContainer.getParent();
        if (parent != null) {
            parent.getChildren().clear();
            parent.getChildren().add(parentView.getView());
        }
    }
    
    /**
     * Creates the question form
     */
    private VBox createQuestionForm(Question question) {
        VBox form = new VBox();
        form.getStyleClass().add("question-form");
        form.setSpacing(16);
        form.setPadding(new Insets(20));
        
        // Question text field
        VBox questionContainer = new VBox();
        questionContainer.setSpacing(8);
        
        Label questionLabel = new Label("Question Text *");
        questionLabel.getStyleClass().add("form-label");
        
        TextArea questionArea = new TextArea();
        questionArea.getStyleClass().add("form-field");
        questionArea.setPromptText("Enter the question...");
        questionArea.setPrefRowCount(3);
        questionArea.setWrapText(true);
        questionArea.setId("questionText");
        if (question != null) {
            questionArea.setText(question.getQuestionText());
        }
        
        questionContainer.getChildren().addAll(questionLabel, questionArea);
        
        // Options field (for multiple choice)
        VBox optionsContainer = new VBox();
        optionsContainer.setSpacing(8);
        
        Label optionsLabel = new Label("Options (one per line)");
        optionsLabel.getStyleClass().add("form-label");
        
        TextArea optionsArea = new TextArea();
        optionsArea.getStyleClass().add("form-field");
        optionsArea.setPromptText("Enter options, one per line...");
        optionsArea.setPrefRowCount(4);
        optionsArea.setWrapText(true);
        optionsArea.setId("questionOptions");
        if (question != null && question.getOptions() != null) {
            optionsArea.setText(String.join("\n", question.getOptions()));
        }
        
        optionsContainer.getChildren().addAll(optionsLabel, optionsArea);
        
        // Correct answer field
        VBox answerContainer = new VBox();
        answerContainer.setSpacing(8);
        
        Label answerLabel = new Label("Correct Answer *");
        answerLabel.getStyleClass().add("form-label");
        
        TextField answerField = new TextField();
        answerField.getStyleClass().add("form-field");
        answerField.setPromptText("Enter the correct answer...");
        answerField.setId("correctAnswer");
        if (question != null) {
            answerField.setText(question.getCorrectAnswer());
        }
        
        answerContainer.getChildren().addAll(answerLabel, answerField);
        
        // Difficulty selection
        VBox difficultyContainer = new VBox();
        difficultyContainer.setSpacing(8);
        
        Label difficultyLabel = new Label("Difficulty Level");
        difficultyLabel.getStyleClass().add("form-label");
        
        HBox difficultyButtons = new HBox();
        difficultyButtons.setSpacing(8);
        
        ToggleGroup difficultyGroup = new ToggleGroup();
        
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
        if (question != null) {
            switch (question.getDifficulty()) {
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
        
        form.getChildren().addAll(questionContainer, optionsContainer, answerContainer, difficultyContainer);
        return form;
    }
    
    /**
     * Creates a question from the form data
     */
    private Question createQuestionFromForm() {
        // For now, create a simple question
        // This would need to be implemented with proper form handling
        List<String> options = java.util.Arrays.asList("Option A", "Option B", "Option C", "Option D");
        Question newQuestion = new Question("Sample Question", options, 0, "Sample explanation", com.studyspace.models.Flashcard.Difficulty.MEDIUM);
        return newQuestion;
    }
    
    /**
     * Updates a question from the form data
     */
    private Question updateQuestionFromForm(Question question) {
        // For now, just return the question as-is
        // This would need to be implemented with proper form handling
        return question;
    }
    
    
    
    /**
     * Gets the main view container
     */
    public VBox getView() {
        return mainContainer;
    }
}
