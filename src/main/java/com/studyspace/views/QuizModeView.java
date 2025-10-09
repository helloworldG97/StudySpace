package com.studyspace.views;

import com.studyspace.models.Question;
import com.studyspace.models.Quiz;
import com.studyspace.models.User;
import com.studyspace.utils.DataStore;
import com.studyspace.utils.SceneManager;
import com.studyspace.utils.IconUtils;
import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.Parent;
import javafx.util.Duration;

import java.util.List;
import java.util.Collections;
import java.util.ArrayList;

/**
 * QuizModeView - Interactive quiz taking interface with timer and scoring
 */
public class QuizModeView {
    
    private final DataStore dataStore;
    private final SceneManager sceneManager;
    private final Quiz quiz;
    private final QuizListView parentView;
    private final List<Question> questions;
    
    private VBox mainContainer;
    private Label timerLabel;
    private Label progressLabel;
    private VBox questionContainer;
    private Label questionText;
    private VBox optionsContainer;
    private Button submitButton;
    private VBox explanationContainer;
    private Button nextButton;
    private Button finishButton;
    
    private int currentQuestionIndex;
    private int selectedOptionIndex;
    private boolean isAnswered;
    private int correctAnswers;
    private int totalQuestions;
    private Timeline timer;
    private int timeRemaining; // in seconds
    private List<String> shuffledOptions; // Store shuffled options for current question
    private int correctAnswerIndex; // Store the correct answer index after shuffling
    
    public QuizModeView(Quiz quiz, QuizListView parentView) {
        this.dataStore = DataStore.getInstance();
        this.sceneManager = SceneManager.getInstance();
        this.quiz = quiz;
        this.parentView = parentView;
        this.questions = quiz.getQuestions();
        this.currentQuestionIndex = 0;
        this.selectedOptionIndex = -1;
        this.isAnswered = false;
        this.correctAnswers = 0;
        this.totalQuestions = questions.size();
        this.timeRemaining = quiz.getTimeLimit() * 60; // Convert minutes to seconds
        
        initializeUI();
        showCurrentQuestion();
        startTimer();
    }
    
    /**
     * Initializes the UI components
     */
    private void initializeUI() {
        mainContainer = new VBox();
        mainContainer.setSpacing(32);
        mainContainer.getStyleClass().add("content-area");
        mainContainer.setAlignment(Pos.TOP_CENTER);
        
        // Header section
        VBox headerSection = createHeaderSection();
        
        // Question container
        questionContainer = createQuestionContainer();
        
        // Controls section
        VBox controlsSection = createControlsSection();
        
        mainContainer.getChildren().addAll(headerSection, questionContainer, controlsSection);
    }
    
    /**
     * Creates the header section with timer and progress
     */
    private VBox createHeaderSection() {
        VBox header = new VBox();
        header.setSpacing(16);
        header.setAlignment(Pos.CENTER);
        
        // Quiz title and info
        Label titleLabel = new Label();
        titleLabel.setGraphic(IconUtils.createLargeIconView("question"));
        titleLabel.setText(" " + quiz.getTitle());
        titleLabel.getStyleClass().addAll("text-2xl", "font-bold", "text-primary");
        
        HBox metaInfo = new HBox();
        metaInfo.setSpacing(24);
        metaInfo.setAlignment(Pos.CENTER);
        
        Label difficultyLabel = new Label(quiz.getDifficulty().getDisplayName());
        difficultyLabel.getStyleClass().addAll("difficulty-badge", 
            quiz.getDifficulty().name().toLowerCase());
        
        Label subjectLabel = new Label();
        subjectLabel.setGraphic(IconUtils.createIconView("folder"));
        subjectLabel.setText(" " + quiz.getSubject());
        subjectLabel.getStyleClass().addAll("text-sm", "text-secondary");
        
        progressLabel = new Label();
        progressLabel.setGraphic(IconUtils.createIconView("trending-up"));
        progressLabel.getStyleClass().addAll("text-sm", "text-muted");
        
        metaInfo.getChildren().addAll(difficultyLabel, subjectLabel, progressLabel);
        
        // Timer (if quiz has time limit)
        timerLabel = new Label();
        if (quiz.getTimeLimit() > 0) {
            timerLabel.setGraphic(IconUtils.createIconView("clock"));
            timerLabel.getStyleClass().addAll("quiz-timer", "text-center");
            timerLabel.setVisible(true);
        } else {
            timerLabel.setVisible(false);
        }
        
        header.getChildren().addAll(titleLabel, metaInfo, timerLabel);
        
        return header;
    }
    
    /**
     * Creates the question container
     */
    private VBox createQuestionContainer() {
        VBox container = new VBox();
        container.getStyleClass().add("quiz-container");
        container.setAlignment(Pos.CENTER);
        container.setSpacing(24);
        container.setMaxWidth(800);
        
        // Question text
        questionText = new Label();
        questionText.getStyleClass().add("quiz-question");
        questionText.setWrapText(true);
        questionText.setMaxWidth(800);
        questionText.setMaxHeight(Region.USE_COMPUTED_SIZE); // Auto-size to content
        
        // Options container
        optionsContainer = new VBox();
        optionsContainer.getStyleClass().add("quiz-options");
        optionsContainer.setSpacing(12);
        optionsContainer.setMaxWidth(750);
        
        // Explanation container (initially hidden)
        explanationContainer = new VBox();
        explanationContainer.setSpacing(12);
        explanationContainer.setVisible(false);
        explanationContainer.setManaged(false);
        
        container.getChildren().addAll(questionText, optionsContainer, explanationContainer);
        
        return container;
    }
    
    /**
     * Creates the controls section
     */
    private VBox createControlsSection() {
        VBox controls = new VBox();
        controls.setSpacing(16);
        controls.setAlignment(Pos.CENTER);
        
        // Submit/Next buttons
        HBox buttonContainer = new HBox();
        buttonContainer.setSpacing(16);
        buttonContainer.setAlignment(Pos.CENTER);
        
        submitButton = new Button();
        submitButton.setGraphic(IconUtils.createIconTextHBox("check", "Submit Answer"));
        submitButton.getStyleClass().add("primary-button");
        submitButton.setDisable(true);
        submitButton.setOnAction(e -> handleSubmitAnswer());
        
        nextButton = new Button();
        nextButton.setGraphic(IconUtils.createIconTextHBox("arrow-right", "Next Question"));
        nextButton.getStyleClass().add("primary-button");
        nextButton.setVisible(false);
        nextButton.setManaged(false);
        nextButton.setOnAction(e -> handleNextQuestion());
        
        finishButton = new Button();
        finishButton.setGraphic(IconUtils.createIconTextHBox("star", "Finish Quiz"));
        finishButton.getStyleClass().add("success-button");
        finishButton.setVisible(false);
        finishButton.setManaged(false);
        finishButton.setOnAction(e -> handleFinishQuiz());
        
        buttonContainer.getChildren().addAll(submitButton, nextButton, finishButton);
        
        // Back button
        Button backButton = new Button();
        backButton.setGraphic(IconUtils.createIconTextHBox("arrow-left", "Exit Quiz"));
        backButton.getStyleClass().add("secondary-button");
        backButton.setOnAction(e -> handleExitQuiz());
        
        controls.getChildren().addAll(buttonContainer, backButton);
        
        return controls;
    }
    
    /**
     * Shows the current question
     */
    private void showCurrentQuestion() {
        if (questions.isEmpty() || currentQuestionIndex >= questions.size()) {
            return;
        }
        
        Question currentQuestion = questions.get(currentQuestionIndex);
        
        // Update progress
        progressLabel.setText((currentQuestionIndex + 1) + " / " + totalQuestions);
        
        // Show question text
        questionText.setText(currentQuestion.getQuestionText());
        
        // Clear and recreate options
        optionsContainer.getChildren().clear();
        selectedOptionIndex = -1;
        isAnswered = false;
        
        // Shuffle options to randomize answer positions
        List<String> originalOptions = currentQuestion.getOptions();
        shuffledOptions = new ArrayList<>(originalOptions);
        Collections.shuffle(shuffledOptions);
        
        // Find the new position of the correct answer after shuffling
        String correctAnswer = currentQuestion.getCorrectAnswer();
        correctAnswerIndex = shuffledOptions.indexOf(correctAnswer);
        
        // Create option buttons with shuffled options
        for (int i = 0; i < shuffledOptions.size(); i++) {
            Button optionButton = createOptionButton(shuffledOptions.get(i), i);
            optionsContainer.getChildren().add(optionButton);
        }
        
        // Reset controls
        submitButton.setDisable(true);
        submitButton.setVisible(true);
        submitButton.setManaged(true);
        nextButton.setVisible(false);
        nextButton.setManaged(false);
        finishButton.setVisible(false);
        finishButton.setManaged(false);
        
        // Hide explanation
        explanationContainer.setVisible(false);
        explanationContainer.setManaged(false);
    }
    
    /**
     * Creates an option button
     */
    private Button createOptionButton(String optionText, int optionIndex) {
        Button button = new Button((char)('A' + optionIndex) + ". " + optionText);
        button.getStyleClass().add("quiz-option");
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setWrapText(true);  // Enable text wrapping for long options
        
        button.setOnAction(e -> handleOptionSelected(optionIndex, button));
        
        return button;
    }
    
    /**
     * Handles option selection
     */
    private void handleOptionSelected(int optionIndex, Button selectedButton) {
        if (isAnswered) return;
        
        // Clear previous selection
        for (javafx.scene.Node node : optionsContainer.getChildren()) {
            if (node instanceof Button) {
                Button btn = (Button) node;
                btn.getStyleClass().removeAll("selected");
            }
        }
        
        // Mark new selection
        selectedButton.getStyleClass().add("selected");
        selectedOptionIndex = optionIndex;
        
        // Enable submit button
        submitButton.setDisable(false);
    }
    
    /**
     * Handles answer submission
     */
    private void handleSubmitAnswer() {
        if (selectedOptionIndex == -1 || isAnswered) return;
        
        Question currentQuestion = questions.get(currentQuestionIndex);
        // Use the shuffled correct answer index instead of the original
        boolean isCorrect = (selectedOptionIndex == correctAnswerIndex);
        
        if (isCorrect) {
            correctAnswers++;
        }
        
        isAnswered = true;
        
        // Update option buttons to show correct/incorrect
        updateOptionsAfterSubmission(currentQuestion);
        
        // Show explanation
        showExplanation(currentQuestion, isCorrect);
        
        // Update controls
        submitButton.setVisible(false);
        submitButton.setManaged(false);
        
        if (currentQuestionIndex < totalQuestions - 1) {
            nextButton.setVisible(true);
            nextButton.setManaged(true);
        } else {
            finishButton.setVisible(true);
            finishButton.setManaged(true);
        }
    }
    
    /**
     * Updates option buttons after submission
     */
    private void updateOptionsAfterSubmission(Question question) {
        for (int i = 0; i < optionsContainer.getChildren().size(); i++) {
            Button button = (Button) optionsContainer.getChildren().get(i);
            
            // Use the shuffled correct answer index
            if (i == correctAnswerIndex) {
                button.getStyleClass().add("correct");
            } else if (i == selectedOptionIndex && i != correctAnswerIndex) {
                button.getStyleClass().add("incorrect");
            }
            
            button.setDisable(true);
        }
    }
    
    /**
     * Shows the explanation for the current question
     */
    private void showExplanation(Question question, boolean wasCorrect) {
        explanationContainer.getChildren().clear();
        
        // Result indicator
        Label resultLabel = new Label();
        resultLabel.setGraphic(IconUtils.createIconView(wasCorrect ? "check" : "x"));
        resultLabel.setText(wasCorrect ? " Correct!" : " Incorrect");
        resultLabel.getStyleClass().addAll("text-lg", "font-semibold", 
            wasCorrect ? "text-success" : "text-error");
        
        // Correct answer (use shuffled options)
        String correctAnswerText = shuffledOptions.get(correctAnswerIndex);
        Label correctAnswerLabel = new Label("Correct Answer: " + correctAnswerText);
        correctAnswerLabel.getStyleClass().addAll("text-sm", "font-medium", "text-primary");
        correctAnswerLabel.setWrapText(true);
        correctAnswerLabel.setMaxWidth(700);  // Increased width
        correctAnswerLabel.setMaxHeight(Region.USE_COMPUTED_SIZE); // Auto-size to content
        
        // Explanation
        Label explanationLabel = new Label(question.getExplanation());
        explanationLabel.getStyleClass().add("quiz-explanation");
        explanationLabel.setWrapText(true);
        explanationLabel.setMaxWidth(700);  // Increased width
        explanationLabel.setMaxHeight(Region.USE_COMPUTED_SIZE); // Auto-size to content
        
        explanationContainer.getChildren().addAll(resultLabel, correctAnswerLabel, explanationLabel);
        explanationContainer.setVisible(true);
        explanationContainer.setManaged(true);
        
        // Fade in animation
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), explanationContainer);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }
    
    /**
     * Handles moving to the next question
     */
    private void handleNextQuestion() {
        currentQuestionIndex++;
        showCurrentQuestion();
    }
    
    /**
     * Handles finishing the quiz
     */
    private void handleFinishQuiz() {
        stopTimer();
        
        // Calculate final score
        double scorePercentage = (double) correctAnswers / totalQuestions * 100;
        int finalScore = (int) Math.round(scorePercentage);
        
        // Update quiz and user statistics
        quiz.recordScore(finalScore);
        
        // Save quiz to database with updated score
        dataStore.saveQuiz(quiz);
        
        User currentUser = dataStore.getCurrentUser();
        if (currentUser != null) {
            currentUser.incrementQuizzesTaken();
            // Update user in database
            dataStore.updateUser(currentUser);
        }
        
        // Log activity
        dataStore.logUserActivity("QUIZ_COMPLETED", "Completed " + quiz.getTitle() + " quiz with " + finalScore + "% score");
        
        // Refresh activity history
        com.studyspace.components.SidebarView.refreshActivityHistoryGlobally();
        
        // Show results dialog
        showResultsDialog(finalScore);
    }
    
    /**
     * Shows the quiz results dialog
     */
    private void showResultsDialog(int finalScore) {
        String performance;
        if (finalScore >= 90) {
            performance = "Excellent! ⭐";
        } else if (finalScore >= 80) {
            performance = "Great job! 👏";
        } else if (finalScore >= 70) {
            performance = "Good work! 👍";
        } else if (finalScore >= 60) {
            performance = "Not bad! 📖";
        } else {
            performance = "Keep studying! 💪";
        }
        
        String message = String.format(
            "%s\n\n" +
            "📈 Quiz Results:\n" +
            "• Score: %d%%\n" +
            "• Correct answers: %d out of %d\n" +
            "• Time taken: %s\n\n" +
            "%s",
            performance,
            finalScore,
            correctAnswers,
            totalQuestions,
            getTimeElapsedText(),
            finalScore < 70 ? "Review the explanations and try again!" : "Keep up the great work!"
        );
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Quiz Completed!");
        alert.setHeaderText("🎊 " + quiz.getTitle() + " - Results");
        alert.setContentText(message);
        
        ButtonType retakeButton = new ButtonType("Retake Quiz");
        ButtonType backToQuizzesButton = new ButtonType("Back to Quizzes");
        
        alert.getButtonTypes().setAll(retakeButton, backToQuizzesButton);
        
        alert.showAndWait().ifPresent(response -> {
            if (response == retakeButton) {
                // Reset and start over
                resetQuiz();
            } else {
                // Go back to quizzes
                handleBackToQuizzes();
            }
        });
    }
    
    /**
     * Resets the quiz to start over
     */
    private void resetQuiz() {
        currentQuestionIndex = 0;
        selectedOptionIndex = -1;
        isAnswered = false;
        correctAnswers = 0;
        timeRemaining = quiz.getTimeLimit() * 60;
        
        showCurrentQuestion();
        startTimer();
    }
    
    /**
     * Handles exiting the quiz
     */
    private void handleExitQuiz() {
        boolean confirmed = sceneManager.showConfirmationDialog(
            "Exit Quiz", 
            "Are you sure you want to exit? Your progress will be lost."
        );
        
        if (confirmed) {
            stopTimer();
            handleBackToQuizzes();
        }
    }
    
    /**
     * Handles returning to the quiz list
     */
    private void handleBackToQuizzes() {
        stopTimer();
        
        // Refresh the parent view to show updated statistics
        parentView.refresh();
        
        // Replace current content with the quiz list
        Parent parent = mainContainer.getParent();
        if (parent != null) {
            if (parent instanceof VBox) {
                ((VBox) parent).getChildren().clear();
                ((VBox) parent).getChildren().add(parentView.getView());
            } else if (parent instanceof StackPane) {
                ((StackPane) parent).getChildren().clear();
                ((StackPane) parent).getChildren().add(parentView.getView());
            } else if (parent instanceof Pane) {
                ((Pane) parent).getChildren().clear();
                ((Pane) parent).getChildren().add(parentView.getView());
            }
        }
    }
    
    /**
     * Starts the quiz timer
     */
    private void startTimer() {
        if (quiz.getTimeLimit() <= 0) return;
        
        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeRemaining--;
            updateTimerDisplay();
            
            if (timeRemaining <= 0) {
                handleTimeUp();
            }
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }
    
    /**
     * Stops the quiz timer
     */
    private void stopTimer() {
        if (timer != null) {
            timer.stop();
        }
    }
    
    /**
     * Updates the timer display
     */
    private void updateTimerDisplay() {
        if (quiz.getTimeLimit() <= 0) return;
        
        int minutes = timeRemaining / 60;
        int seconds = timeRemaining % 60;
        
        String timeText = String.format("⏰ %02d:%02d", minutes, seconds);
        timerLabel.setText(timeText);
        
        // Change color when time is running low
        if (timeRemaining <= 60) {
            timerLabel.getStyleClass().removeAll("quiz-timer");
            timerLabel.getStyleClass().add("text-error");
        } else if (timeRemaining <= 300) {
            timerLabel.getStyleClass().removeAll("quiz-timer");
            timerLabel.getStyleClass().add("text-warning");
        }
    }
    
    /**
     * Handles time up scenario
     */
    private void handleTimeUp() {
        stopTimer();
        
        sceneManager.showInfoDialog("Time's Up!", 
            "The quiz time limit has been reached. Your quiz will be submitted automatically.");
        
        handleFinishQuiz();
    }
    
    /**
     * Gets the elapsed time text
     */
    private String getTimeElapsedText() {
        if (quiz.getTimeLimit() <= 0) {
            return "No time limit";
        }
        
        int totalTime = quiz.getTimeLimit() * 60;
        int elapsed = totalTime - timeRemaining;
        int minutes = elapsed / 60;
        int seconds = elapsed % 60;
        
        return String.format("%02d:%02d", minutes, seconds);
    }
    
    /**
     * Gets the main view container
     */
    public VBox getView() {
        return mainContainer;
    }
}
