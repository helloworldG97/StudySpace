package com.studyspace;

import com.studyspace.auth.AuthView;
import com.studyspace.utils.SceneManager;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

//============ main application entry point =============
//this is where the application starts and initializes

public class Main extends Application {
    
    public static final String APP_TITLE = "Study Space - Learning Management System";
    public static final int WINDOW_WIDTH = 1400;
    public static final int WINDOW_HEIGHT = 900;
    public static final int MIN_WIDTH = 1200;
    public static final int MIN_HEIGHT = 700;
    
    @Override
    public void start(Stage primaryStage) {
        try {
            primaryStage.setTitle(APP_TITLE);
            primaryStage.setMinWidth(MIN_WIDTH);
            primaryStage.setMinHeight(MIN_HEIGHT);
            primaryStage.setMaximized(false);
            
            SceneManager.getInstance().initialize(primaryStage);
            
            AuthView authView = new AuthView();
            Scene authScene = authView.getView();
            
            try {
                String cssPath = getClass().getResource("/css/styles.css").toExternalForm();
                if (cssPath != null) {
                    authScene.getStylesheets().add(cssPath);
                }
            } catch (Exception cssError) {
                System.err.println("Warning: Could not load CSS stylesheet: " + cssError.getMessage());
            }
        
            primaryStage.setScene(authScene);
            primaryStage.centerOnScreen();
            primaryStage.show();

            primaryStage.setOnCloseRequest(event -> {
                try {
                    System.exit(0);
                } catch (Exception e) {
                    System.err.println("Error during application shutdown: " + e.getMessage());
                    e.printStackTrace();
                }
            });
            
            System.out.println("Study Space Application started successfully!");
            
        } catch (Exception e) {
            System.err.println("Error starting application: " + e.getMessage());
            e.printStackTrace();

            try {
                SceneManager.getInstance().showErrorDialog(
                    "Application Error", 
                    "Failed to start Study Space application: " + e.getMessage()
                );
            } catch (Exception dialogError) {
                System.err.println("Could not show error dialog: " + dialogError.getMessage());
            }
        }
    }
    
    public static void main(String[] args) {
        System.setProperty("javafx.animation.fullspeed", "true");
        System.setProperty("javafx.animation.pulse", "60");
        
        launch(args);
    }
    
    @Override
    public void stop() throws Exception {
        System.out.println("Study Space Application is shutting down...");
        super.stop();
    }
}
