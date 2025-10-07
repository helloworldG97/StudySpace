package com.studyspace.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

//============ scene manager =============
//this is where application navigation and scene switching is handled

public class SceneManager {
    private static SceneManager instance;
    private Stage primaryStage;
    private final Map<String, Scene> sceneCache;
    private Scene currentScene;
    
    // Scene identifiers
    public static final String HOME_SCENE = "home";
    public static final String FLASHCARD_LIST_SCENE = "flashcard-list";
    public static final String FLASHCARD_STUDY_SCENE = "flashcard-study";
    public static final String QUIZ_LIST_SCENE = "quiz-list";
    public static final String QUIZ_MODE_SCENE = "quiz-mode";
    public static final String NOTES_SCENE = "notes";
    public static final String TODO_SCENE = "todo";
    public static final String TIMER_SCENE = "timer";
    public static final String DASHBOARD_SCENE = "dashboard";
    
    // FXML file paths
    private static final String HOME_FXML = "/fxml/home.fxml";
    private static final String FLASHCARD_LIST_FXML = "/fxml/flashcard-list.fxml";
    private static final String FLASHCARD_STUDY_FXML = "/fxml/flashcard-study.fxml";
    private static final String QUIZ_LIST_FXML = "/fxml/quiz-list.fxml";
    private static final String QUIZ_MODE_FXML = "/fxml/quiz-mode.fxml";
    private static final String NOTES_FXML = "/fxml/notes.fxml";
    private static final String TODO_FXML = "/fxml/todo.fxml";
    private static final String TIMER_FXML = "/fxml/timer.fxml";
    private static final String DASHBOARD_FXML = "/fxml/dashboard.fxml";
    
    // CSS stylesheet path
    private static final String STYLES_CSS = "/css/styles.css";
    
    private SceneManager() {
        this.sceneCache = new HashMap<>();
    }
    
    public static SceneManager getInstance() {
        if (instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }
    
    public void initialize(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
    
    public void switchToScene(String sceneId) {
        switchToScene(sceneId, null);
    }
    
    public void switchToScene(String sceneId, Object data) {
        if (primaryStage == null) {
            throw new IllegalStateException("SceneManager not initialized. Call initialize() first.");
        }
        
        try {
            Scene scene = getScene(sceneId);
            if (scene != null) {
                currentScene = scene;
                primaryStage.setScene(scene);
                
                // Pass data to controller if provided
                if (data != null) {
                    passDataToController(sceneId, data);
                }
            } else {
                System.err.println("Scene not found: " + sceneId);
            }
        } catch (Exception e) {
            System.err.println("Error switching to scene " + sceneId + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private Scene getScene(String sceneId) {
        // Check cache first
        if (sceneCache.containsKey(sceneId)) {
            return sceneCache.get(sceneId);
        }
        
        Scene scene = null;
        String fxmlPath = getFXMLPath(sceneId);
        
        if (fxmlPath != null) {
            scene = loadFXMLScene(fxmlPath);
        }
        
        // Cache the scene if successfully created
        if (scene != null) {
            sceneCache.put(sceneId, scene);
        }
        
        return scene;
    }
    
    private String getFXMLPath(String sceneId) {
        switch (sceneId) {
            case HOME_SCENE:
                return HOME_FXML;
            case FLASHCARD_LIST_SCENE:
                return FLASHCARD_LIST_FXML;
            case FLASHCARD_STUDY_SCENE:
                return FLASHCARD_STUDY_FXML;
            case QUIZ_LIST_SCENE:
                return QUIZ_LIST_FXML;
            case QUIZ_MODE_SCENE:
                return QUIZ_MODE_FXML;
            case NOTES_SCENE:
                return NOTES_FXML;
            case TODO_SCENE:
                return TODO_FXML;
            case TIMER_SCENE:
                return TIMER_FXML;
            case DASHBOARD_SCENE:
                return DASHBOARD_FXML;
            default:
                System.err.println("Unknown scene ID: " + sceneId);
                return null;
        }
    }
    
    private Scene loadFXMLScene(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            
            // Add CSS stylesheet
            String cssPath = getClass().getResource(STYLES_CSS).toExternalForm();
            scene.getStylesheets().add(cssPath);
            
            return scene;
            
        } catch (IOException e) {
            System.err.println("Error loading FXML file " + fxmlPath + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    private void passDataToController(String sceneId, Object data) {
        // This method would be used to pass data to controllers
        // For now, we'll implement a simple approach
        // In a more complex application, you might use a more sophisticated pattern
    }
    
    public void switchToCustomScene(Scene scene) {
        if (primaryStage == null) {
            throw new IllegalStateException("SceneManager not initialized. Call initialize() first.");
        }
        
        currentScene = scene;
        primaryStage.setScene(scene);
    }
    
    public void clearSceneCache() {
        sceneCache.clear();
    }
    
    public void removeFromCache(String sceneId) {
        sceneCache.remove(sceneId);
    }
    
    public Stage getPrimaryStage() {
        return primaryStage;
    }
    
    public Scene getCurrentScene() {
        return currentScene;
    }
    
    // Utility methods for dialogs
    public void showErrorDialog(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public void showInfoDialog(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public boolean showConfirmationDialog(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        return alert.showAndWait().orElse(javafx.scene.control.ButtonType.CANCEL) == javafx.scene.control.ButtonType.OK;
    }
}
