package com.studyspace.views;

import com.studyspace.models.Flashcard;
import com.studyspace.models.FlashcardDeck;
import com.studyspace.models.User;
import com.studyspace.utils.DataStore;
import com.studyspace.utils.SceneManager;
import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.Parent;
import javafx.util.Duration;

import java.util.List;

/**
 * FlashcardStudyView - Interactive flashcard study interface
 */
public class FlashcardStudyView {
    
    private final DataStore dataStore;
    private final SceneManager sceneManager;
    private final FlashcardDeck deck;
    private final FlashcardListView parentView;
    private final List<Flashcard> cards;
    
    private VBox mainContainer;
    private VBox flashcardContainer;
    private Label flashcardContent;
    private Button flipButton;
    private HBox navigationButtons;
    private Button previousButton;
    private Button nextButton;
    private Label progressLabel;
    private Button backButton;
    
    private int currentCardIndex;
    private boolean isShowingAnswer;
    private int correctCount;
    private int totalStudied;
    
    public FlashcardStudyView(FlashcardDeck deck, FlashcardListView parentView) {
        this.dataStore = DataStore.getInstance();
        this.sceneManager = SceneManager.getInstance();
        this.deck = deck;
        this.parentView = parentView;
        this.cards = deck.getFlashcards();
        this.currentCardIndex = 0;
        this.isShowingAnswer = false;
        this.correctCount = 0;
        this.totalStudied = 0;
        
        // Log study session start
        dataStore.logUserActivity("STUDY_SESSION_STARTED", "Started studying " + deck.getTitle() + " flashcard deck");
        
        initializeUI();
        showCurrentCard();
    }
    
    /**
     * Initializes the UI components
     */
    private void initializeUI() {
        mainContainer = new VBox();
        mainContainer.setSpacing(32);
        mainContainer.getStyleClass().add("content-area");
        mainContainer.setAlignment(Pos.CENTER);
        
        // Header section
        VBox headerSection = createHeaderSection();
        
        // Flashcard container
        flashcardContainer = createFlashcardContainer();
        
        // Controls section
        VBox controlsSection = createControlsSection();
        
        // Progress section
        VBox progressSection = createProgressSection();
        
        mainContainer.getChildren().addAll(headerSection, flashcardContainer, controlsSection, progressSection);
    }
    
    /**
     * Creates the header section
     */
    private VBox createHeaderSection() {
        VBox header = new VBox();
        header.setSpacing(8);
        header.setAlignment(Pos.CENTER);
        
        Label titleLabel = new Label("🃏 " + deck.getTitle());
        titleLabel.getStyleClass().addAll("text-2xl", "font-bold", "text-primary");
        
        HBox metaInfo = new HBox();
        metaInfo.setSpacing(16);
        metaInfo.setAlignment(Pos.CENTER);
        
        Label difficultyLabel = new Label(deck.getDifficulty().getDisplayName());
        difficultyLabel.getStyleClass().addAll("difficulty-badge", 
            deck.getDifficulty().name().toLowerCase());
        
        Label subjectLabel = new Label("📂 " + deck.getSubject());
        subjectLabel.getStyleClass().addAll("text-sm", "text-secondary");
        
        progressLabel = new Label();
        progressLabel.getStyleClass().addAll("text-sm", "text-muted");
        
        metaInfo.getChildren().addAll(difficultyLabel, subjectLabel, progressLabel);
        
        header.getChildren().addAll(titleLabel, metaInfo);
        
        return header;
    }
    
    /**
     * Creates the flashcard container
     */
    private VBox createFlashcardContainer() {
        VBox container = new VBox();
        container.getStyleClass().add("flashcard-container");
        container.setAlignment(Pos.CENTER);
        container.setSpacing(24);
        container.setPrefWidth(600);
        container.setPrefHeight(350);
        
        // Card content with click-to-flip functionality
        flashcardContent = new Label();
        flashcardContent.getStyleClass().addAll("flashcard-content", "text-center");
        flashcardContent.setWrapText(true);
        flashcardContent.setMaxWidth(550);
        flashcardContent.setAlignment(Pos.CENTER);
        flashcardContent.setMinHeight(200);
        flashcardContent.setStyle("-fx-cursor: hand;");
        
        // Add click handler for flip animation
        flashcardContent.setOnMouseClicked(e -> handleFlipCard());
        
        // Flip button
        flipButton = new Button("🔄 Flip Card");
        flipButton.getStyleClass().add("primary-button");
        flipButton.setOnAction(e -> handleFlipCard());
        
        // Add instruction label
        Label instructionLabel = new Label("Click the card or button to flip");
        instructionLabel.getStyleClass().addAll("text-sm", "text-muted");
        instructionLabel.setAlignment(Pos.CENTER);
        
        container.getChildren().addAll(flashcardContent, flipButton, instructionLabel);
        
        return container;
    }
    
    /**
     * Creates the controls section
     */
    private VBox createControlsSection() {
        VBox controls = new VBox();
        controls.setSpacing(16);
        controls.setAlignment(Pos.CENTER);
        
        // Navigation buttons
        navigationButtons = new HBox();
        navigationButtons.setSpacing(16);
        navigationButtons.setAlignment(Pos.CENTER);
        
        previousButton = new Button("← Previous");
        previousButton.getStyleClass().add("secondary-button");
        previousButton.setOnAction(e -> handlePreviousCard());
        
        nextButton = new Button("Next →");
        nextButton.getStyleClass().add("secondary-button");
        nextButton.setOnAction(e -> handleNextCard());
        
        navigationButtons.getChildren().addAll(previousButton, nextButton);
        
        // Knowledge buttons (shown when answer is visible)
        HBox knowledgeButtons = new HBox();
        knowledgeButtons.setSpacing(12);
        knowledgeButtons.setAlignment(Pos.CENTER);
        
        Button incorrectButton = new Button("❌ Didn't Know");
        incorrectButton.getStyleClass().add("error-button");
        incorrectButton.setOnAction(e -> handleKnowledgeResponse(false));
        
        Button correctButton = new Button("✅ Got It Right");
        correctButton.getStyleClass().add("success-button");
        correctButton.setOnAction(e -> handleKnowledgeResponse(true));
        
        knowledgeButtons.getChildren().addAll(incorrectButton, correctButton);
        knowledgeButtons.setVisible(false);
        knowledgeButtons.setManaged(false);
        
        controls.getChildren().addAll(navigationButtons, knowledgeButtons);
        
        return controls;
    }
    
    /**
     * Creates the progress section
     */
    private VBox createProgressSection() {
        VBox progress = new VBox();
        progress.setSpacing(16);
        progress.setAlignment(Pos.CENTER);
        
        // Progress bar
        ProgressBar progressBar = new ProgressBar();
        progressBar.setPrefWidth(400);
        progressBar.getStyleClass().add("progress-bar");
        
        // Back button
        backButton = new Button("← Back to Decks");
        backButton.getStyleClass().add("secondary-button");
        backButton.setOnAction(e -> handleBackToDecks());
        
        // Study session stats
        HBox statsContainer = new HBox();
        statsContainer.setSpacing(24);
        statsContainer.setAlignment(Pos.CENTER);
        
        Label studiedLabel = new Label("Total Cards: " + cards.size());
        studiedLabel.getStyleClass().addAll("text-sm", "text-secondary");
        
        Label correctLabel = new Label("Studied: " + totalStudied + " | Correct: " + correctCount);
        correctLabel.getStyleClass().addAll("text-sm", "text-success");
        
        statsContainer.getChildren().addAll(studiedLabel, correctLabel);
        
        progress.getChildren().addAll(progressBar, statsContainer, backButton);
        
        return progress;
    }
    
    /**
     * Shows the current flashcard
     */
    private void showCurrentCard() {
        if (cards.isEmpty()) {
            return;
        }
        
        Flashcard currentCard = cards.get(currentCardIndex);
        
        // Update progress label
        progressLabel.setText((currentCardIndex + 1) + " / " + cards.size());
        
        // Show question side
        flashcardContent.setText(currentCard.getQuestion());
        flashcardContent.getStyleClass().removeAll("flashcard-answer");
        flashcardContent.getStyleClass().add("flashcard-question");
        
        // Reset flip state
        isShowingAnswer = false;
        flipButton.setText("Flip Card");
        flipButton.setVisible(true);
        
        // Update navigation buttons
        previousButton.setDisable(currentCardIndex == 0);
        nextButton.setDisable(currentCardIndex == cards.size() - 1);
        
        // Hide knowledge buttons
        hideKnowledgeButtons();
        
        // Update progress bar
        updateProgressBar();
    }
    
    /**
     * Handles flipping the current card
     */
    private void handleFlipCard() {
        Flashcard currentCard = cards.get(currentCardIndex);
        
        if (!isShowingAnswer) {
            // Flip to answer with animation
            flipToAnswer(currentCard.getAnswer());
        } else {
            // Flip back to question
            flipToQuestion(currentCard.getQuestion());
        }
    }
    
    /**
     * Animates flipping to answer side
     */
    private void flipToAnswer(String answer) {
        // Scale down animation
        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(150), flashcardContainer);
        scaleDown.setToX(0.0);
        scaleDown.setOnFinished(e -> {
            // Change content
            flashcardContent.setText(answer);
            flashcardContent.getStyleClass().removeAll("flashcard-question");
            flashcardContent.getStyleClass().add("flashcard-answer");
            
            isShowingAnswer = true;
            flipButton.setText("Flip Back");
            
            // Show knowledge buttons
            showKnowledgeButtons();
            
            // Scale up animation
            ScaleTransition scaleUp = new ScaleTransition(Duration.millis(150), flashcardContainer);
            scaleUp.setToX(1.0);
            scaleUp.play();
        });
        
        scaleDown.play();
    }
    
    /**
     * Animates flipping to question side
     */
    private void flipToQuestion(String question) {
        // Scale down animation
        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(150), flashcardContainer);
        scaleDown.setToX(0.0);
        scaleDown.setOnFinished(e -> {
            // Change content
            flashcardContent.setText(question);
            flashcardContent.getStyleClass().removeAll("flashcard-answer");
            flashcardContent.getStyleClass().add("flashcard-question");
            
            isShowingAnswer = false;
            flipButton.setText("Flip Card");
            
            // Hide knowledge buttons
            hideKnowledgeButtons();
            
            // Scale up animation
            ScaleTransition scaleUp = new ScaleTransition(Duration.millis(150), flashcardContainer);
            scaleUp.setToX(1.0);
            scaleUp.play();
        });
        
        scaleDown.play();
    }
    
    /**
     * Shows knowledge response buttons
     */
    private void showKnowledgeButtons() {
        VBox controlsSection = (VBox) navigationButtons.getParent();
        HBox knowledgeButtons = (HBox) controlsSection.getChildren().get(1);
        
        knowledgeButtons.setVisible(true);
        knowledgeButtons.setManaged(true);
        
        // Fade in animation
        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), knowledgeButtons);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }
    
    /**
     * Hides knowledge response buttons
     */
    private void hideKnowledgeButtons() {
        VBox controlsSection = (VBox) navigationButtons.getParent();
        HBox knowledgeButtons = (HBox) controlsSection.getChildren().get(1);
        
        knowledgeButtons.setVisible(false);
        knowledgeButtons.setManaged(false);
    }
    
    /**
     * Handles knowledge response (correct/incorrect)
     */
    private void handleKnowledgeResponse(boolean wasCorrect) {
        Flashcard currentCard = cards.get(currentCardIndex);
        
        // Update card statistics
        if (wasCorrect) {
            currentCard.markAsCorrect();
            correctCount++;
        } else {
            currentCard.markAsIncorrect();
        }
        
        totalStudied++;
        
        // Update user statistics
        User currentUser = dataStore.getCurrentUser();
        if (currentUser != null) {
            currentUser.incrementFlashcardsStudied();
        }
        
        // Log activity for each card studied
        String result = wasCorrect ? "correctly" : "incorrectly";
        dataStore.logUserActivity("FLASHCARDS_REVIEWED", "Studied '" + currentCard.getQuestion() + "' " + result + " in " + deck.getTitle());
        
        // Refresh activity history
        com.studyspace.components.SidebarView.refreshActivityHistoryGlobally();
        
        // Update stats display
        updateStatsDisplay();
        
        // Auto-advance to next card or show completion
        if (currentCardIndex < cards.size() - 1) {
            // Small delay before moving to next card
            Timeline timeline = new Timeline(new KeyFrame(Duration.millis(500), e -> {
                currentCardIndex++;
                showCurrentCard();
            }));
            timeline.play();
        } else {
            // Show completion dialog
            showCompletionDialog();
        }
    }
    
    /**
     * Handles previous card navigation
     */
    private void handlePreviousCard() {
        if (currentCardIndex > 0) {
            currentCardIndex--;
            showCurrentCard();
        }
    }
    
    /**
     * Handles next card navigation
     */
    private void handleNextCard() {
        if (currentCardIndex < cards.size() - 1) {
            currentCardIndex++;
            showCurrentCard();
        }
    }
    
    /**
     * Updates the progress bar
     */
    private void updateProgressBar() {
        VBox progressSection = (VBox) backButton.getParent();
        ProgressBar progressBar = (ProgressBar) progressSection.getChildren().get(0);
        
        double progress = (double) (currentCardIndex + 1) / cards.size();
        progressBar.setProgress(progress);
    }
    
    /**
     * Updates the stats display
     */
    private void updateStatsDisplay() {
        VBox progressSection = (VBox) backButton.getParent();
        HBox statsContainer = (HBox) progressSection.getChildren().get(1);
        
        Label studiedLabel = (Label) statsContainer.getChildren().get(0);
        Label correctLabel = (Label) statsContainer.getChildren().get(1);
        
        studiedLabel.setText("Total Cards: " + cards.size());
        correctLabel.setText("Studied: " + totalStudied + " | Correct: " + correctCount);
    }
    
    /**
     * Shows completion dialog when all cards are studied
     */
    private void showCompletionDialog() {
        deck.markAsStudied();
        
        double accuracy = totalStudied > 0 ? (double) correctCount / totalStudied * 100 : 0;
        
        // Log study session completion
        dataStore.logUserActivity("STUDY_SESSION_ENDED", 
            String.format("Completed studying %s deck - %d cards, %.1f%% accuracy", 
                deck.getTitle(), totalStudied, accuracy));
        
        String message = String.format(
            "Great job! You've completed this deck.\n\n" +
            "📊 Study Session Results:\n" +
            "• Cards studied: %d\n" +
            "• Correct answers: %d\n" +
            "• Accuracy: %.1f%%\n\n" +
            "Keep up the excellent work!",
            totalStudied, correctCount, accuracy
        );
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Deck Completed!");
        alert.setHeaderText("🎉 Congratulations!");
        alert.setContentText(message);
        
        ButtonType studyAgainButton = new ButtonType("Study Again");
        ButtonType backToDecksButton = new ButtonType("Back to Decks");
        
        alert.getButtonTypes().setAll(studyAgainButton, backToDecksButton);
        
        alert.showAndWait().ifPresent(response -> {
            if (response == studyAgainButton) {
                // Reset and start over
                resetStudySession();
            } else {
                // Go back to decks
                handleBackToDecks();
            }
        });
    }
    
    /**
     * Resets the study session to start over
     */
    private void resetStudySession() {
        currentCardIndex = 0;
        totalStudied = 0;
        correctCount = 0;
        showCurrentCard();
        updateStatsDisplay();
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
     * Gets the main view container
     */
    public VBox getView() {
        return mainContainer;
    }
}
