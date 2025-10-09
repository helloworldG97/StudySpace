package com.studyspace.auth;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Dialog to inform users about offline mode and provide demo credentials
 */
public class OfflineLoginDialog {
    
    public static void showOfflineModeDialog(Stage parentStage) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(parentStage);
        dialog.setTitle("StudySpace - Offline Mode");
        dialog.setResizable(false);
        
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #f5f5f5;");
        
        // Title
        Label titleLabel = new Label("🔌 Offline Mode");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        // Message
        Label messageLabel = new Label("Database connection is not available.\nThe application is running in offline mode with demo data.");
        messageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495e; -fx-text-alignment: center;");
        messageLabel.setWrapText(true);
        
        // Demo credentials
        VBox credentialsBox = new VBox(10);
        credentialsBox.setAlignment(Pos.CENTER);
        credentialsBox.setStyle("-fx-background-color: #ecf0f1; -fx-padding: 15; -fx-background-radius: 8;");
        
        Label credentialsTitle = new Label("Demo Login Credentials:");
        credentialsTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        
        Label emailLabel = new Label("Email: demo@studyspace.com");
        emailLabel.setStyle("-fx-font-family: monospace; -fx-font-size: 14px;");
        
        Label passwordLabel = new Label("Password: demo123");
        passwordLabel.setStyle("-fx-font-family: monospace; -fx-font-size: 14px;");
        
        credentialsBox.getChildren().addAll(credentialsTitle, emailLabel, passwordLabel);
        
        // Features info
        Label featuresLabel = new Label("✓ Access to demo flashcards, notes, and quizzes\n✓ All features work in offline mode\n✓ Data is stored in memory (not persistent)");
        featuresLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #27ae60;");
        
        // OK button
        Button okButton = new Button("Got it!");
        okButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
        okButton.setOnAction(e -> dialog.close());
        
        root.getChildren().addAll(titleLabel, messageLabel, credentialsBox, featuresLabel, okButton);
        
        Scene scene = new Scene(root, 400, 350);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
}

