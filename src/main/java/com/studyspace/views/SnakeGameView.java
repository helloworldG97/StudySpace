package com.studyspace.views;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * SnakeGameView - A fully functional Snake game for taking breaks
 */
public class SnakeGameView {
    
    private VBox mainContainer;
    private Pane gamePane;
    private Label scoreLabel;
    private Label gameOverLabel;
    private Button startButton;
    private Button pauseButton;
    private Button resetButton;
    
    // Game variables
    private static final int GRID_SIZE = 20;
    private static final int GAME_WIDTH = 600;
    private static final int GAME_HEIGHT = 400;
    private static final int GRID_WIDTH = GAME_WIDTH / GRID_SIZE;
    private static final int GRID_HEIGHT = GAME_HEIGHT / GRID_SIZE;
    
    private List<Rectangle> snake;
    private Rectangle food;
    private Direction direction;
    private Direction nextDirection;
    private Timeline gameLoop;
    private boolean isGameRunning;
    private boolean isPaused;
    private int score;
    private Random random;
    
    private enum Direction {
        UP, DOWN, LEFT, RIGHT
    }
    
    public SnakeGameView() {
        initializeUI();
        initializeGame();
    }
    
    /**
     * Initializes the UI components
     */
    private void initializeUI() {
        mainContainer = new VBox();
        mainContainer.setSpacing(20);
        mainContainer.setPadding(new Insets(20));
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setStyle("-fx-background-color: linear-gradient(to bottom right, #667eea, #764ba2);");
        
        // Title
        Label titleLabel = new Label("🐍 Snake Game - Take a Break!");
        titleLabel.setFont(new Font("Arial", 28));
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        
        // Score display
        scoreLabel = new Label("Score: 0");
        scoreLabel.setFont(new Font("Arial", 18));
        scoreLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        
        // Game pane
        gamePane = new Pane();
        gamePane.setPrefSize(GAME_WIDTH, GAME_HEIGHT);
        gamePane.setStyle("-fx-background-color: #2c3e50; -fx-border-color: #34495e; -fx-border-width: 2;");
        
        // Control buttons
        HBox buttonContainer = new HBox();
        buttonContainer.setSpacing(15);
        buttonContainer.setAlignment(Pos.CENTER);
        
        startButton = new Button("Start Game");
        startButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; " +
                           "-fx-padding: 10 20; -fx-background-radius: 5;");
        
        pauseButton = new Button("Pause");
        pauseButton.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-weight: bold; " +
                            "-fx-padding: 10 20; -fx-background-radius: 5;");
        pauseButton.setDisable(true);
        
        resetButton = new Button("Reset");
        resetButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; " +
                           "-fx-padding: 10 20; -fx-background-radius: 5;");
        
        buttonContainer.getChildren().addAll(startButton, pauseButton, resetButton);
        
        // Game over label
        gameOverLabel = new Label("");
        gameOverLabel.setFont(new Font("Arial", 24));
        gameOverLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
        gameOverLabel.setVisible(false);
        
        // Instructions
        Label instructionsLabel = new Label("Press ENTER to start! Use Arrow Keys or WASD to control the snake. Eat the red food to grow!");
        instructionsLabel.setFont(new Font("Arial", 14));
        instructionsLabel.setStyle("-fx-text-fill: white;");
        
        mainContainer.getChildren().addAll(titleLabel, scoreLabel, gamePane, gameOverLabel, buttonContainer, instructionsLabel);
        
        setupEventHandlers();
    }
    
    /**
     * Sets up event handlers
     */
    private void setupEventHandlers() {
        startButton.setOnAction(e -> startGame());
        pauseButton.setOnAction(e -> togglePause());
        resetButton.setOnAction(e -> resetGame());
        
        // Keyboard controls - Arrow keys, WASD, and Enter to start
        gamePane.setOnKeyPressed(e -> {
            KeyCode key = e.getCode();
            
            // Handle game start with Enter key
            if (key == KeyCode.ENTER && !isGameRunning) {
                startGame();
                return;
            }
            
            // Only process movement keys if game is running
            if (!isGameRunning) return;
            
            switch (key) {
                // Arrow keys
                case UP:
                    if (direction != Direction.DOWN) nextDirection = Direction.UP;
                    break;
                case DOWN:
                    if (direction != Direction.UP) nextDirection = Direction.DOWN;
                    break;
                case LEFT:
                    if (direction != Direction.RIGHT) nextDirection = Direction.LEFT;
                    break;
                case RIGHT:
                    if (direction != Direction.LEFT) nextDirection = Direction.RIGHT;
                    break;
                // WASD keys
                case W:
                    if (direction != Direction.DOWN) nextDirection = Direction.UP;
                    break;
                case S:
                    if (direction != Direction.UP) nextDirection = Direction.DOWN;
                    break;
                case A:
                    if (direction != Direction.RIGHT) nextDirection = Direction.LEFT;
                    break;
                case D:
                    if (direction != Direction.LEFT) nextDirection = Direction.RIGHT;
                    break;
                default:
                    // Ignore other keys
                    break;
            }
        });
        
        // Make sure the game pane can receive focus for keyboard events
        gamePane.setFocusTraversable(true);
    }
    
    /**
     * Initializes the game
     */
    private void initializeGame() {
        snake = new ArrayList<>();
        random = new Random();
        score = 0;
        isGameRunning = false;
        isPaused = false;
        direction = Direction.RIGHT;
        nextDirection = Direction.RIGHT;
        
        // Create initial snake
        createInitialSnake();
        
        // Create initial food
        createFood();
        
        // Update display
        updateScore();
        drawGame();
    }
    
    /**
     * Creates the initial snake
     */
    private void createInitialSnake() {
        snake.clear();
        
        // Create snake head in the middle
        Rectangle head = new Rectangle(GRID_SIZE, GRID_SIZE);
        head.setFill(Color.LIMEGREEN);
        head.setStroke(Color.DARKGREEN);
        head.setStrokeWidth(1);
        head.setX(GRID_WIDTH / 2 * GRID_SIZE);
        head.setY(GRID_HEIGHT / 2 * GRID_SIZE);
        snake.add(head);
        
        // Add initial body segments
        for (int i = 1; i < 3; i++) {
            Rectangle body = new Rectangle(GRID_SIZE, GRID_SIZE);
            body.setFill(Color.LIMEGREEN);
            body.setStroke(Color.DARKGREEN);
            body.setStrokeWidth(1);
            body.setX((GRID_WIDTH / 2 - i) * GRID_SIZE);
            body.setY(GRID_HEIGHT / 2 * GRID_SIZE);
            snake.add(body);
        }
    }
    
    /**
     * Creates food at a random position
     */
    private void createFood() {
        if (food != null) {
            gamePane.getChildren().remove(food);
        }
        
        food = new Rectangle(GRID_SIZE, GRID_SIZE);
        food.setFill(Color.RED);
        food.setStroke(Color.DARKRED);
        food.setStrokeWidth(1);
        
        // Find a random position not occupied by snake
        int x, y;
        do {
            x = random.nextInt(GRID_WIDTH) * GRID_SIZE;
            y = random.nextInt(GRID_HEIGHT) * GRID_SIZE;
        } while (isSnakePosition(x, y));
        
        food.setX(x);
        food.setY(y);
        gamePane.getChildren().add(food);
    }
    
    /**
     * Checks if a position is occupied by the snake
     */
    private boolean isSnakePosition(int x, int y) {
        for (Rectangle segment : snake) {
            if (segment.getX() == x && segment.getY() == y) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Checks if a position is occupied by the snake body (excluding head)
     */
    private boolean isSnakeBodyPosition(int x, int y) {
        // Skip the head (index 0) and check only the body
        for (int i = 1; i < snake.size(); i++) {
            Rectangle segment = snake.get(i);
            if (segment.getX() == x && segment.getY() == y) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Starts the game
     */
    private void startGame() {
        if (isGameRunning) return;
        
        isGameRunning = true;
        isPaused = false;
        startButton.setDisable(true);
        pauseButton.setDisable(false);
        gameOverLabel.setVisible(false);
        
        // Log activity for playing the game
        logGameActivity("Started Snake game for break");
        
        // Start game loop
        gameLoop = new Timeline(new KeyFrame(Duration.millis(150), e -> updateGame()));
        gameLoop.setCycleCount(Timeline.INDEFINITE);
        gameLoop.play();
        
        // Focus the game pane for keyboard input
        gamePane.requestFocus();
    }
    
    /**
     * Toggles pause state
     */
    private void togglePause() {
        if (!isGameRunning) return;
        
        isPaused = !isPaused;
        
        if (isPaused) {
            gameLoop.pause();
            pauseButton.setText("Resume");
        } else {
            gameLoop.play();
            pauseButton.setText("Pause");
            gamePane.requestFocus();
        }
    }
    
    /**
     * Resets the game
     */
    private void resetGame() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
        
        isGameRunning = false;
        isPaused = false;
        score = 0;
        direction = Direction.RIGHT;
        nextDirection = Direction.RIGHT;
        
        startButton.setDisable(false);
        pauseButton.setDisable(true);
        pauseButton.setText("Pause");
        gameOverLabel.setVisible(false);
        
        // Clear game pane
        gamePane.getChildren().clear();
        
        // Reinitialize game
        initializeGame();
    }
    
    /**
     * Updates the game state
     */
    private void updateGame() {
        if (isPaused) return;
        
        direction = nextDirection;
        
        // Get head position
        Rectangle head = snake.get(0);
        double newX = head.getX();
        double newY = head.getY();
        
        // Calculate new head position based on direction
        switch (direction) {
            case UP:
                newY -= GRID_SIZE;
                break;
            case DOWN:
                newY += GRID_SIZE;
                break;
            case LEFT:
                newX -= GRID_SIZE;
                break;
            case RIGHT:
                newX += GRID_SIZE;
                break;
        }
        
        // Check wall collision
        if (newX < 0 || newX >= GAME_WIDTH || newY < 0 || newY >= GAME_HEIGHT) {
            gameOver();
            return;
        }
        
        // Check self collision (exclude current head)
        if (isSnakeBodyPosition((int) newX, (int) newY)) {
            gameOver();
            return;
        }
        
        // Create new head
        Rectangle newHead = new Rectangle(GRID_SIZE, GRID_SIZE);
        newHead.setFill(Color.LIMEGREEN);
        newHead.setStroke(Color.DARKGREEN);
        newHead.setStrokeWidth(1);
        newHead.setX(newX);
        newHead.setY(newY);
        
        snake.add(0, newHead);
        
        // Check if food is eaten
        if (newX == food.getX() && newY == food.getY()) {
            score += 10;
            updateScore();
            createFood();
        } else {
            // Remove tail if no food eaten
            Rectangle tail = snake.remove(snake.size() - 1);
            gamePane.getChildren().remove(tail);
        }
        
        drawGame();
    }
    
    /**
     * Handles game over
     */
    private void gameOver() {
        isGameRunning = false;
        
        // Log activity for game completion
        logGameActivity("Completed Snake game with score: " + score);
        isPaused = false;
        
        if (gameLoop != null) {
            gameLoop.stop();
        }
        
        startButton.setDisable(false);
        pauseButton.setDisable(true);
        pauseButton.setText("Pause");
        
        gameOverLabel.setText("Game Over! Final Score: " + score);
        gameOverLabel.setVisible(true);
    }
    
    /**
     * Draws the game on the pane
     */
    private void drawGame() {
        // Clear previous snake
        for (Rectangle segment : snake) {
            if (!gamePane.getChildren().contains(segment)) {
                gamePane.getChildren().add(segment);
            }
        }
    }
    
    /**
     * Updates the score display
     */
    private void updateScore() {
        scoreLabel.setText("Score: " + score);
    }
    
    /**
     * Gets the main view container
     */
    public VBox getView() {
        return mainContainer;
    }
    
    /**
     * Logs game activity to the activity history
     */
    private void logGameActivity(String description) {
        try {
            com.studyspace.utils.DataStore dataStore = com.studyspace.utils.DataStore.getInstance();
            dataStore.logUserActivity("GAME_PLAYED", description);
            
            // Refresh activity history to show the new activity
            com.studyspace.components.SidebarView.refreshActivityHistoryGlobally();
        } catch (Exception e) {
            System.out.println("Error logging game activity: " + e.getMessage());
        }
    }
}
