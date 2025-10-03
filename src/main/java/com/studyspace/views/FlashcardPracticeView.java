package com.studyspace.views;

import com.studyspace.models.Flashcard;
import com.studyspace.models.FlashcardDeck;
import com.studyspace.utils.DataStore;
import com.studyspace.utils.IconUtils;
import com.studyspace.utils.SceneManager;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import java.util.List;

/**
 * FlashcardPracticeView - Enhanced interactive flashcard practice interface
 */
public class FlashcardPracticeView {
    
    private final DataStore dataStore;
    private final SceneManager sceneManager;
    private final FlashcardDeck deck;
    private final FlashcardListView parentView;
    private final List<Flashcard> cards;
    
    private VBox mainContainer;
    private StackPane flashcardContainer;
    private VBox flashcardFront;
    private VBox flashcardBack;
    private Label frontContent;
    private Label backContent;
    private HBox navigationButtons;
    private Button previousButton;
    private Button nextButton;
    private ProgressBar progressBar;
    private Label progressLabel;
    
    private int currentCardIndex;
    private boolean isShowingAnswer;
    
    public FlashcardPracticeView(FlashcardDeck deck, FlashcardListView parentView) {
        this.dataStore = DataStore.getInstance();
        this.sceneManager = SceneManager.getInstance();
        this.deck = deck;
        this.parentView = parentView;
        this.cards = deck.getFlashcards();
        this.currentCardIndex = 0;
        this.isShowingAnswer = false;
        
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
        mainContainer.setPadding(new Insets(32));
        
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
        
        HBox titleRow = new HBox();
        titleRow.setAlignment(Pos.CENTER);
        titleRow.setSpacing(12);
        
        Label titleIcon = new Label();
        titleIcon.setGraphic(IconUtils.createMediumIconView("cards"));
        
        Label titleLabel = new Label(deck.getTitle());
        titleLabel.getStyleClass().addAll("text-2xl", "font-bold", "text-primary");
        
        titleRow.getChildren().addAll(titleIcon, titleLabel);
        
        HBox metaInfo = new HBox();
        metaInfo.setSpacing(16);
        metaInfo.setAlignment(Pos.CENTER);
        
        Label difficultyLabel = new Label(deck.getDifficulty().getDisplayName());
        difficultyLabel.getStyleClass().addAll("difficulty-badge", 
            deck.getDifficulty().name().toLowerCase());
        
        Label subjectLabel = new Label();
        subjectLabel.setGraphic(IconUtils.createIconTextHBox("folder", deck.getSubject()));
        subjectLabel.getStyleClass().addAll("text-sm", "text-secondary");
        
        progressLabel = new Label();
        progressLabel.getStyleClass().addAll("text-sm", "text-muted");
        
        metaInfo.getChildren().addAll(difficultyLabel, subjectLabel, progressLabel);
        
        // Back button
        Button backButton = new Button();
        backButton.setGraphic(IconUtils.createIconTextHBox("home", "Back to Deck"));
        backButton.getStyleClass().add("secondary-button");
        backButton.setOnAction(e -> handleBackToDeck());
        
        header.getChildren().addAll(titleRow, metaInfo, backButton);
        
        return header;
    }
    
    /**
     * Creates the flashcard container with front and back sides
     */
    private StackPane createFlashcardContainer() {
        StackPane container = new StackPane();
        container.getStyleClass().add("flashcard-practice-container");
        container.setMinHeight(350);
        container.setPrefHeight(350);
        container.setMaxWidth(600);
        
        // Create front side of card
        flashcardFront = new VBox();
        flashcardFront.getStyleClass().add("flashcard-practice-front");
        flashcardFront.setAlignment(Pos.CENTER);
        flashcardFront.setPadding(new Insets(24));
        flashcardFront.setSpacing(16);
        
        Label frontLabel = new Label("QUESTION");
        frontLabel.getStyleClass().add("flashcard-side-label");
        
        frontContent = new Label();
        frontContent.getStyleClass().add("flashcard-content");
        frontContent.setWrapText(true);
        frontContent.setAlignment(Pos.CENTER);
        frontContent.setMaxWidth(550);
        
        flashcardFront.getChildren().addAll(frontLabel, frontContent);
        
        // Create back side of card
        flashcardBack = new VBox();
        flashcardBack.getStyleClass().add("flashcard-practice-back");
        flashcardBack.setAlignment(Pos.CENTER);
        flashcardBack.setPadding(new Insets(24));
        flashcardBack.setSpacing(16);
        flashcardBack.setVisible(false);
        
        Label backLabel = new Label("ANSWER");
        backLabel.getStyleClass().add("flashcard-side-label");
        
        backContent = new Label();
        backContent.getStyleClass().add("flashcard-content");
        backContent.setWrapText(true);
        backContent.setAlignment(Pos.CENTER);
        backContent.setMaxWidth(550);
        
        flashcardBack.getChildren().addAll(backLabel, backContent);
        
        // Add click handler for flip animation
        container.setOnMouseClicked(e -> handleFlipCard());
        
        // Add instruction label
        Label instructionLabel = new Label("Click the card to flip");
        instructionLabel.getStyleClass().addAll("text-sm", "text-muted");
        instructionLabel.setAlignment(Pos.CENTER);
        
        // Add to container
        container.getChildren().addAll(flashcardFront, flashcardBack);
        
        // Add clip to ensure content stays within bounds during animation
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(container.widthProperty());
        clip.heightProperty().bind(container.heightProperty());
        clip.setArcWidth(20);
        clip.setArcHeight(20);
        container.setClip(clip);
        
        // Add cursor style to indicate clickable
        container.setStyle("-fx-cursor: hand;");
        
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
        
        previousButton = new Button();
        previousButton.setGraphic(IconUtils.createIconTextHBox("arrow-left", "Previous"));
        previousButton.getStyleClass().add("secondary-button");
        previousButton.setOnAction(e -> handlePreviousCard());
        
        Button flipButton = new Button();
        flipButton.setGraphic(IconUtils.createIconTextHBox("cards", "Flip Card"));
        flipButton.getStyleClass().add("primary-button");
        flipButton.setOnAction(e -> handleFlipCard());
        
        nextButton = new Button();
        nextButton.setGraphic(IconUtils.createIconTextHBox("arrow-right", "Next"));
        nextButton.getStyleClass().add("secondary-button");
        nextButton.setOnAction(e -> handleNextCard());
        
        navigationButtons.getChildren().addAll(previousButton, flipButton, nextButton);
        
        controls.getChildren().add(navigationButtons);
        
        return controls;
    }
    
    /**
     * Creates the progress section
     */
    private VBox createProgressSection() {
        VBox progress = new VBox();
        progress.setSpacing(8);

        progress.setAlignment(Pos.CENTER);
        // Progress bar
        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(400);
        progressBar.getStyleClass().add("flashcard-progress-bar");
        
        progress.getChildren().add(progressBar);
        
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
        frontContent.setText(currentCard.getQuestion());
        backContent.setText(currentCard.getAnswer());
        
        // Reset flip state
        isShowingAnswer = false;
        flashcardFront.setVisible(true);
        flashcardBack.setVisible(false);
        
        // Update navigation buttons
        previousButton.setDisable(currentCardIndex == 0);
        nextButton.setDisable(currentCardIndex == cards.size() - 1);
        
        // Update progress bar
        updateProgressBar();
    }
    
    /**
     * Handles flipping the current card
     */
    private void handleFlipCard() {
        if (!isShowingAnswer) {
            // Flip to answer with animation
            flipToAnswer();
        } else {
            // Flip back to question
            flipToQuestion();
        }
    }
    
    /**
     * Animates flipping to answer side
     */
    private void flipToAnswer() {
        // Create rotation transform
        Rotate rotation = new Rotate(0, Rotate.Y_AXIS);
        flashcardFront.getTransforms().add(rotation);
        
        // Create rotation animation
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(rotation.angleProperty(), 0)),
            new KeyFrame(Duration.millis(150), event -> {
                // Halfway through animation, switch to back side
                flashcardFront.setVisible(false);
                flashcardBack.setVisible(true);
                isShowingAnswer = true;
            }, new KeyValue(rotation.angleProperty(), 90)),
            new KeyFrame(Duration.millis(300), new KeyValue(rotation.angleProperty(), 180))
        );
        
        // Play animation
        timeline.play();
    }
    
    /**
     * Animates flipping to question side
     */
    private void flipToQuestion() {
        // Create rotation transform
        Rotate rotation = new Rotate(0, Rotate.Y_AXIS);
        flashcardBack.getTransforms().add(rotation);
        
        // Create rotation animation
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(rotation.angleProperty(), 0)),
            new KeyFrame(Duration.millis(150), event -> {
                // Halfway through animation, switch to front side
                flashcardBack.setVisible(false);
                flashcardFront.setVisible(true);
                isShowingAnswer = false;
            }, new KeyValue(rotation.angleProperty(), 90)),
            new KeyFrame(Duration.millis(300), new KeyValue(rotation.angleProperty(), 180))
        );
        
        // Play animation
        timeline.play();
    }
    
    /**
     * Handles moving to the previous card
     */
    private void handlePreviousCard() {
        if (currentCardIndex > 0) {
            currentCardIndex--;
            showCurrentCard();
        }
    }
    
    /**
     * Handles moving to the next card
     */
    private void handleNextCard() {
        if (currentCardIndex < cards.size() - 1) {
            currentCardIndex++;
            showCurrentCard();
        } else {
            showCompletionDialog();
        }
    }
    
    /**
     * Updates the progress bar
     */
    private void updateProgressBar() {
        double progress = (double) (currentCardIndex + 1) / cards.size();
        progressBar.setProgress(progress);
    }
    
    /**
     * Shows completion dialog when all cards have been reviewed
     */
    private void showCompletionDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Practice Complete");
        alert.setHeaderText("Great job!");
        alert.setContentText("You've completed practicing all flashcards in this deck.");
        
        ButtonType restartButtonType = new ButtonType("Practice Again");
        ButtonType backButtonType = new ButtonType("Back to Deck", ButtonBar.ButtonData.CANCEL_CLOSE);
        
        alert.getButtonTypes().setAll(restartButtonType, backButtonType);
        
        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == restartButtonType) {
                resetPracticeSession();
            } else {
                handleBackToDeck();
            }
        });
    }
    
    /**
     * Resets the practice session to start over
     */
    private void resetPracticeSession() {
        currentCardIndex = 0;
        showCurrentCard();
    }
    
    /**
     * Handles returning to the deck management view
     */
    private void handleBackToDeck() {
        // Create deck management view
        FlashcardDeckManagementView deckView = new FlashcardDeckManagementView(deck, parentView);
        
        // Replace current content with deck management view
        Parent parent = mainContainer.getParent();
        if (parent != null) {
            if (parent instanceof VBox) {
                ((VBox) parent).getChildren().clear();
                ((VBox) parent).getChildren().add(deckView.getView());
            } else if (parent instanceof StackPane) {
                ((StackPane) parent).getChildren().clear();
                ((StackPane) parent).getChildren().add(deckView.getView());
            }
        }
    }
    
    /**
     * Returns the main view container
     */
    public VBox getView() {
        return mainContainer;
    }
}