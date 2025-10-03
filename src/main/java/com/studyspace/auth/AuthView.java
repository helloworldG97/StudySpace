package com.studyspace.auth;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Random;

/**
 * AuthView - FXML-based authentication interface
 * 
 * This class loads the authentication interface from FXML and provides
 * the scene for the authentication flow with glassmorphism design.
 */
public class AuthView {
    
    public AuthView() {
    }
    
    /**
     * Creates and returns the authentication scene
     */
    public Scene createAuthScene() {
        try {
            // Create main container with layered background
            StackPane mainRoot = new StackPane();
            
            // Set the background gradient on the main root
            mainRoot.setStyle("-fx-background-color: linear-gradient(to bottom right, " +
                "#1a1a2e 0%, " +     /* Deep blue */
                "#16213e 25%, " +    /* Darker blue */
                "#0f0f23 50%, " +    /* Deep blue-black */
                "#1e1b4b 75%, " +    /* Deep purple */
                "#000000 100%);");   /* Black */
            
            // Add floating icons background
            Pane floatingIconsPane = createFloatingIcons();
            
            // Add mouse trail effect
            Pane mouseTrailPane = createMouseTrail();
            
            // Load FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/auth-view.fxml"));
            StackPane authRoot = loader.load();
            
            // Layer the components: floating icons, mouse trail, then auth UI on top
            mainRoot.getChildren().addAll(floatingIconsPane, mouseTrailPane, authRoot);
            
            // Add mouse trail effect to the main root
            addMouseTrailToRoot(mainRoot, mouseTrailPane);
            
            // Create scene
            Scene scene = new Scene(mainRoot, 1200, 800);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            
            return scene;
            
        } catch (IOException e) {
            System.err.println("Error loading authentication FXML: " + e.getMessage());
            e.printStackTrace();
            
            // Fallback to simple scene
            return createFallbackScene();
        }
    }
    
    /**
     * Creates a fallback scene in case FXML loading fails
     */
    private Scene createFallbackScene() {
        StackPane fallback = new StackPane();
        fallback.getStyleClass().add("auth-container");
        fallback.getChildren().add(new javafx.scene.control.Label("Authentication interface could not be loaded."));
        
        Scene scene = new Scene(fallback, 1200, 800);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        return scene;
    }
    
    /**
     * Creates floating study icons for the background with responsive positioning
     */
    private Pane createFloatingIcons() {
        Pane pane = new Pane();
        pane.setMouseTransparent(true);
        
        // Study-related icons and symbols - expanded collection
        String[] studyIcons = {
            "📚", "📝", "📊", "🧮", "📐", "🔬", "⚗️", "🧪", "📈", "📉",
            "📋", "📌", "🖊️", "✏️", "🖍️", "📏", "📓", "📖", "📙", "📘",
            "💡", "🎓", "🏆", "⭐", "🔍", "📆", "⏰", "🗂️", "📁", "💻",
            "📄", "🔖", "📎", "🖇️", "✂️", "🖨️", "⌨️", "🖱️", "💾", "🖥️",
            "🔢", "🔤", "🔡", "📑", "📜", "📰", "🗞️", "📇", "🗃️", "🗄️",
            "📱", "💿", "💽", "💾", "💻", "🖨️", "⌨", "🖱️", "🖲️", "💡",
            "📕", "📗", "📘", "📙", "📚", "📖", "📝", "✏️", "🖊️", "🖋️",
            "✒️", "🖌️", "🖍️", "📏", "📐", "📊", "📈", "📉", "📋", "📌",
            "📍", "📎", "🖇️", "🔍", "📑", "📄", "📜", "📰", "📒", "📔"
        };
        
        Random random = new Random();
        
        // Create 50 randomly positioned icons with collision detection
        for (int i = 0; i < 50; i++) {
            Text icon = new Text(studyIcons[i % studyIcons.length]);
            
            // Set font size to 12% of screen dimensions (2x increase from 6%)
            double fontSize = Math.min(1200, 800) * 0.12;
            icon.setFont(Font.font("Segoe UI", FontWeight.BOLD, fontSize));
            
            // Use color palette
            String[] colors = {"#64c8ff", "#7B68EE", "#FF6B6B", "#4ECDC4", "#45B7D1", 
                              "#96CEB4", "#FECA57", "#FF9FF3", "#A8E6CF", "#FFD93D"};
            String color = colors[random.nextInt(colors.length)];
            icon.setFill(Color.web(color));
            icon.setOpacity(0.3);
            
            // Add glow effect
            DropShadow glow = new DropShadow();
            glow.setColor(Color.web(color, 0.4));
            glow.setRadius(15);
            icon.setEffect(glow);
            
            // Generate random position with collision detection
            double xPercent, yPercent;
            boolean validPosition = false;
            int attempts = 0;
            
            do {
                // Generate random position
                xPercent = 0.02 + random.nextDouble() * 0.96; // 2-98% width
                yPercent = 0.02 + random.nextDouble() * 0.96; // 2-98% height
                
                // Check if position avoids protected areas (only center login area)
                boolean avoidsCenterLogin = !(xPercent > 0.25 && xPercent < 0.75 && yPercent > 0.25 && yPercent < 0.75);
                
                validPosition = avoidsCenterLogin;
                attempts++;
                
            } while (!validPosition && attempts < 50); // Fallback after 50 attempts
            
            // If no valid position found, use safe fallback positions
            if (!validPosition) {
                if (i < 12) {
                    // Top area
                    xPercent = 0.1 + random.nextDouble() * 0.8;
                    yPercent = 0.05 + random.nextDouble() * 0.15;
                } else if (i < 25) {
                    // Bottom area
                    xPercent = 0.1 + random.nextDouble() * 0.8;
                    yPercent = 0.85 + random.nextDouble() * 0.1;
                } else if (i < 37) {
                    // Left area
                    xPercent = 0.02 + random.nextDouble() * 0.18;
                    yPercent = 0.1 + random.nextDouble() * 0.8;
                } else {
                    // Right area
                    xPercent = 0.8 + random.nextDouble() * 0.18;
                    yPercent = 0.1 + random.nextDouble() * 0.8;
                }
            }
            
            icon.setMouseTransparent(true);
            
            // Bind position to pane size for responsiveness
            icon.layoutXProperty().bind(pane.widthProperty().multiply(xPercent));
            icon.layoutYProperty().bind(pane.heightProperty().multiply(yPercent));
            
            // Randomized floating animation
            TranslateTransition tt = new TranslateTransition(
                Duration.seconds(10 + random.nextDouble() * 40), icon);
            tt.setByY(-30 + random.nextDouble() * 60);
            tt.setAutoReverse(true);
            tt.setCycleCount(Animation.INDEFINITE);
            tt.setDelay(Duration.seconds(random.nextDouble() * 15));
            tt.play();
            
            // Randomized rotation
            if (random.nextDouble() > 0.2) {
                RotateTransition rt = new RotateTransition(
                    Duration.seconds(20 + random.nextDouble() * 60), icon);
                rt.setByAngle(30 - random.nextDouble() * 60);
                rt.setAutoReverse(true);
                rt.setCycleCount(Animation.INDEFINITE);
                rt.play();
            }
            
            pane.getChildren().add(icon);
        }
        
        return pane;
    }
    
    /**
     * Creates mouse trail effect that follows the cursor
     */
    private Pane createMouseTrail() {
        Pane trailPane = new Pane();
        trailPane.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        trailPane.setMouseTransparent(true);
        
        return trailPane;
    }
    
    /**
     * Adds mouse trail effect to the main root
     */
    private void addMouseTrailToRoot(StackPane mainRoot, Pane trailPane) {
        mainRoot.setOnMouseMoved(event -> {
            // Create a glowing circle at mouse position
            Circle trailDot = new Circle(4);
            trailDot.setFill(Color.web("#64c8ff", 0.9));
            trailDot.setCenterX(event.getX());
            trailDot.setCenterY(event.getY());
            
            // Add glow effect
            Glow glow = new Glow();
            glow.setLevel(1.0);
            trailDot.setEffect(glow);
            
            // Add to trail pane
            trailPane.getChildren().add(trailDot);
            
            // Fade out animation
            FadeTransition fadeOut = new FadeTransition(Duration.millis(1000), trailDot);
            fadeOut.setFromValue(0.9);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(e -> trailPane.getChildren().remove(trailDot));
            fadeOut.play();
            
            // Scale animation for pulsing effect
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(500), trailDot);
            scaleTransition.setFromX(0.3);
            scaleTransition.setFromY(0.3);
            scaleTransition.setToX(1.2);
            scaleTransition.setToY(1.2);
            scaleTransition.setAutoReverse(true);
            scaleTransition.setCycleCount(2);
            scaleTransition.play();
        });
    }
    
    
    /**
     * Gets the authentication scene (for compatibility with existing code)
     */
    public Scene getView() {
        return createAuthScene();
    }
}