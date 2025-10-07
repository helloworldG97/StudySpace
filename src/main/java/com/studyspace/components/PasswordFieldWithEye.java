package com.studyspace.components;

import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.io.InputStream;

/**
 * A custom password field component with an eye icon for show/hide functionality.
 * This component wraps a PasswordField and TextField, toggling between them when the eye icon is clicked.
 */
public class PasswordFieldWithEye extends HBox {
    
    private PasswordField passwordField;
    private TextField textField;
    private Button eyeButton;
    private ImageView eyeIcon;
    private boolean isPasswordVisible = false;
    
    // Eye icons
    private Image eyeImage;
    private Image eyeCrossedImage;
    
    public PasswordFieldWithEye() {
        initializeComponent();
        setupEyeIcons();
        setupEventHandlers();
    }
    
    /**
     * Initialize the component structure
     */
    private void initializeComponent() {
        // Create password field (default visible)
        passwordField = new PasswordField();
        passwordField.getStyleClass().add("form-field");
        passwordField.setMaxWidth(Double.MAX_VALUE);
        
        // Create text field (initially hidden)
        textField = new TextField();
        textField.getStyleClass().add("form-field");
        textField.setMaxWidth(Double.MAX_VALUE);
        textField.setVisible(false);
        textField.setManaged(false);
        
        // Create eye button
        eyeButton = new Button();
        eyeButton.getStyleClass().add("eye-button");
        eyeButton.setPrefWidth(40);
        eyeButton.setPrefHeight(40);
        eyeButton.setMinWidth(40);
        eyeButton.setMinHeight(40);
        eyeButton.setMaxWidth(40);
        eyeButton.setMaxHeight(40);
        
        // Create eye icon
        eyeIcon = new ImageView();
        eyeIcon.setFitWidth(20);
        eyeIcon.setFitHeight(20);
        eyeIcon.setPreserveRatio(true);
        eyeButton.setGraphic(eyeIcon);
        
        // Set up layout
        HBox.setHgrow(passwordField, Priority.ALWAYS);
        HBox.setHgrow(textField, Priority.ALWAYS);
        
        this.getChildren().addAll(passwordField, textField, eyeButton);
        this.setSpacing(8);
        this.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
    }
    
    /**
     * Load and setup eye icons
     */
    private void setupEyeIcons() {
        try {
            // Load eye icons from resources
            InputStream eyeStream = getClass().getResourceAsStream("/images/eye.png");
            InputStream eyeCrossedStream = getClass().getResourceAsStream("/images/eye-crossed.png");
            
            if (eyeStream != null) {
                eyeImage = new Image(eyeStream);
            }
            if (eyeCrossedStream != null) {
                eyeCrossedImage = new Image(eyeCrossedStream);
            }
            
            // Set initial icon (eye icon for hidden password)
            if (eyeImage != null) {
                eyeIcon.setImage(eyeImage);
            }
            
        } catch (Exception e) {
            System.err.println("Error loading eye icons: " + e.getMessage());
            // Fallback to text if images fail to load
            eyeButton.setText("👁");
        }
    }
    
    /**
     * Setup event handlers for the eye button
     */
    private void setupEventHandlers() {
        eyeButton.setOnAction(event -> {
            togglePasswordVisibility();
        });
    }
    
    /**
     * Toggle between showing and hiding the password
     */
    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            // Hide password - switch to password field
            String currentText = textField.getText();
            passwordField.setText(currentText);
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            textField.setVisible(false);
            textField.setManaged(false);
            
            // Update icon to eye (hidden state)
            if (eyeImage != null) {
                eyeIcon.setImage(eyeImage);
            } else {
                eyeButton.setText("👁");
            }
            
            isPasswordVisible = false;
        } else {
            // Show password - switch to text field
            String currentText = passwordField.getText();
            textField.setText(currentText);
            textField.setVisible(true);
            textField.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            
            // Update icon to crossed eye (visible state)
            if (eyeCrossedImage != null) {
                eyeIcon.setImage(eyeCrossedImage);
            } else {
                eyeButton.setText("🙈");
            }
            
            isPasswordVisible = true;
        }
    }
    
    /**
     * Get the current text value
     */
    public String getText() {
        if (isPasswordVisible) {
            return textField.getText();
        } else {
            return passwordField.getText();
        }
    }
    
    /**
     * Set the text value
     */
    public void setText(String text) {
        passwordField.setText(text);
        textField.setText(text);
    }
    
    /**
     * Get the text property for binding
     */
    public StringProperty textProperty() {
        if (isPasswordVisible) {
            return textField.textProperty();
        } else {
            return passwordField.textProperty();
        }
    }
    
    /**
     * Set prompt text
     */
    public void setPromptText(String promptText) {
        passwordField.setPromptText(promptText);
        textField.setPromptText(promptText);
    }
    
    /**
     * Get prompt text property for FXML binding
     */
    public String getPromptText() {
        return passwordField.getPromptText();
    }
    
    /**
     * Set the field as focused
     */
    public void requestFocus() {
        if (isPasswordVisible) {
            textField.requestFocus();
        } else {
            passwordField.requestFocus();
        }
    }
    
    /**
     * Clear the field
     */
    public void clear() {
        passwordField.clear();
        textField.clear();
    }
    
    /**
     * Check if field is empty
     */
    public boolean isEmpty() {
        return getText().trim().isEmpty();
    }
    
    /**
     * Get the password field (for direct access if needed)
     */
    public PasswordField getPasswordField() {
        return passwordField;
    }
    
    /**
     * Get the text field (for direct access if needed)
     */
    public TextField getTextField() {
        return textField;
    }
    
    /**
     * Get the eye button (for direct access if needed)
     */
    public Button getEyeButton() {
        return eyeButton;
    }
    
    /**
     * Set action handler for when Enter is pressed
     */
    public void setOnAction(EventHandler<ActionEvent> eventHandler) {
        passwordField.setOnAction(eventHandler);
        textField.setOnAction(eventHandler);
    }
}
