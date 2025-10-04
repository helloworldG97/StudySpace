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

//============ authentication view =============
//this is where the login and registration interface is created

public class AuthView {
    
    public AuthView() {
    }
    
    public Scene createAuthScene() {
        try {
            StackPane mainRoot = new StackPane();
            
            mainRoot.setStyle("-fx-background-color: linear-gradient(to bottom right, " +
                "#1a1a2e 0%, " +
                "#16213e 25%, " +
                "#0f0f23 50%, " +
                "#1e1b4b 75%, " +
                "#000000 100%);");
            
            Pane floatingIconsPane = createFloatingIcons();
            Pane mouseTrailPane = createMouseTrail();
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/auth-view.fxml"));
            StackPane authRoot = loader.load();
            
            mainRoot.getChildren().addAll(floatingIconsPane, mouseTrailPane, authRoot);
            addMouseTrailToRoot(mainRoot, mouseTrailPane);
            
            Scene scene = new Scene(mainRoot, 1200, 800);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            
            return scene;
            
        } catch (IOException e) {
            System.err.println("Error loading authentication FXML: " + e.getMessage());
            e.printStackTrace();
            
            return createFallbackScene();
        }
    }
    
    private Scene createFallbackScene() {
        StackPane fallback = new StackPane();
        fallback.getStyleClass().add("auth-container");
        fallback.getChildren().add(new javafx.scene.control.Label("Authentication interface could not be loaded."));
        
        Scene scene = new Scene(fallback, 1200, 800);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        return scene;
    }
    
    private Pane createFloatingIcons() {
        Pane pane = new Pane();
        pane.setMouseTransparent(true);
        
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
        
        for (int i = 0; i < 50; i++) {
            Text icon = new Text(studyIcons[i % studyIcons.length]);
            
            double fontSize = Math.min(1200, 800) * 0.12;
            icon.setFont(Font.font("Segoe UI", FontWeight.BOLD, fontSize));
            
            String[] colors = {"#64c8ff", "#7B68EE", "#FF6B6B", "#4ECDC4", "#45B7D1", 
                              "#96CEB4", "#FECA57", "#FF9FF3", "#A8E6CF", "#FFD93D"};
            String color = colors[random.nextInt(colors.length)];
            icon.setFill(Color.web(color));
            icon.setOpacity(0.3);
            
            DropShadow glow = new DropShadow();
            glow.setColor(Color.web(color, 0.4));
            glow.setRadius(15);
            icon.setEffect(glow);
            
            double xPercent, yPercent;
            boolean validPosition = false;
            int attempts = 0;
            
            do {
                xPercent = 0.02 + random.nextDouble() * 0.96;
                yPercent = 0.02 + random.nextDouble() * 0.96;
                
                boolean avoidsCenterLogin = !(xPercent > 0.25 && xPercent < 0.75 && yPercent > 0.25 && yPercent < 0.75);
                
                validPosition = avoidsCenterLogin;
                attempts++;
                
            } while (!validPosition && attempts < 50);
            
            if (!validPosition) {
                if (i < 12) {
                    xPercent = 0.1 + random.nextDouble() * 0.8;
                    yPercent = 0.05 + random.nextDouble() * 0.15;
                } else if (i < 25) {
                    xPercent = 0.1 + random.nextDouble() * 0.8;
                    yPercent = 0.85 + random.nextDouble() * 0.1;
                } else if (i < 37) {
                    xPercent = 0.02 + random.nextDouble() * 0.18;
                    yPercent = 0.1 + random.nextDouble() * 0.8;
                } else {
                    xPercent = 0.8 + random.nextDouble() * 0.18;
                    yPercent = 0.1 + random.nextDouble() * 0.8;
                }
            }
            
            icon.setMouseTransparent(true);
            
            icon.layoutXProperty().bind(pane.widthProperty().multiply(xPercent));
            icon.layoutYProperty().bind(pane.heightProperty().multiply(yPercent));
            
            TranslateTransition tt = new TranslateTransition(
                Duration.seconds(10 + random.nextDouble() * 40), icon);
            tt.setByY(-30 + random.nextDouble() * 60);
            tt.setAutoReverse(true);
            tt.setCycleCount(Animation.INDEFINITE);
            tt.setDelay(Duration.seconds(random.nextDouble() * 15));
            tt.play();
            
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
    
    private Pane createMouseTrail() {
        Pane trailPane = new Pane();
        trailPane.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        trailPane.setMouseTransparent(true);
        
        return trailPane;
    }
    
    private void addMouseTrailToRoot(StackPane mainRoot, Pane trailPane) {
        mainRoot.setOnMouseMoved(event -> {
            Circle trailDot = new Circle(4);
            trailDot.setFill(Color.web("#64c8ff", 0.9));
            trailDot.setCenterX(event.getX());
            trailDot.setCenterY(event.getY());
            
            Glow glow = new Glow();
            glow.setLevel(1.0);
            trailDot.setEffect(glow);
            
            trailPane.getChildren().add(trailDot);
            
            FadeTransition fadeOut = new FadeTransition(Duration.millis(1000), trailDot);
            fadeOut.setFromValue(0.9);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(e -> trailPane.getChildren().remove(trailDot));
            fadeOut.play();
            
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
    
    public Scene getView() {
        return createAuthScene();
    }
}