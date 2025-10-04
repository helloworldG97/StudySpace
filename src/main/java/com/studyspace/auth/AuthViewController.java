package com.studyspace.auth;

import com.studyspace.utils.SceneManager;
import com.studyspace.utils.IconUtils;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

//============ authentication view controller =============
//this is where the login and registration form interactions are handled

public class AuthViewController implements Initializable {
    
    private final AuthController authController;
    private final SceneManager sceneManager;
    
    // FXML injected components
    @FXML private StackPane formsContainer;
    @FXML private VBox signInForm;
    @FXML private VBox signUpForm;
    @FXML private ImageView authLogoIcon;
    
    // Toggle buttons - now without container wrapper
    @FXML private Button signInToggle;
    @FXML private Button signUpToggle;
    @FXML private Button signInToggle2;
    @FXML private Button signUpToggle2;
    
    // Sign In form fields
    @FXML private TextField signInEmail;
    @FXML private PasswordField signInPassword;
    @FXML private Button signInButton;
    @FXML private Label signInError;
    
    // Sign Up form fields
    @FXML private TextField signUpFullName;
    @FXML private TextField signUpEmail;
    @FXML private PasswordField signUpPassword;
    @FXML private Button signUpButton;
    @FXML private Label signUpError;
    
    public AuthViewController() {
        this.authController = new AuthController();
        this.sceneManager = SceneManager.getInstance();
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupEventHandlers();
        setupInitialState();
        setupLogoIcon();
    }
    
    /**
     * Sets up the logo icon using IconUtils with proper fallback system
     */
    private void setupLogoIcon() {
        if (authLogoIcon != null) {
            // Use IconUtils to create logo with fallback system
            ImageView logoView = IconUtils.createLogoView();
            if (logoView != null && logoView.getImage() != null) {
                authLogoIcon.setImage(logoView.getImage());
            } else {
                // Ultimate fallback - use book emoji
                System.out.println("No logo images found, using text fallback");
                // The ImageView will remain empty, which is fine
            }
        }
    }
    
    /**
     * Sets up event handlers for all interactive elements
     */
    private void setupEventHandlers() {
        try {
            // Toggle button handlers with null checks
            if (signInToggle != null) {
                signInToggle.setOnAction(e -> {
                    e.consume();
                    switchToSignIn();
                });
            }
            if (signUpToggle != null) {
                signUpToggle.setOnAction(e -> {
                    e.consume();
                    switchToSignUp();
                });
            }
            if (signInToggle2 != null) {
                signInToggle2.setOnAction(e -> {
                    e.consume();
                    switchToSignIn();
                });
            }
            if (signUpToggle2 != null) {
                signUpToggle2.setOnAction(e -> {
                    e.consume();
                    switchToSignUp();
                });
            }
            
            // Sign In form handlers with null checks
            if (signInButton != null) {
                signInButton.setOnAction(e -> {
                    e.consume();
                    handleSignIn();
                });
                // Add mouse click handler for better responsiveness
                signInButton.setOnMouseClicked(e -> {
                    e.consume();
                    handleSignIn();
                });
            }
            if (signInPassword != null) {
                signInPassword.setOnAction(e -> {
                    e.consume();
                    handleSignIn();
                });
            }
            
            // Sign Up form handlers with null checks
            if (signUpButton != null) {
                signUpButton.setOnAction(e -> {
                    e.consume();
                    handleSignUp();
                });
                // Add mouse click handler for better responsiveness
                signUpButton.setOnMouseClicked(e -> {
                    e.consume();
                    handleSignUp();
                });
            }
            if (signUpPassword != null) {
                signUpPassword.setOnAction(e -> {
                    e.consume();
                    handleSignUp();
                });
            }
            
            // Add mouse click handlers for toggle buttons for better responsiveness
            if (signInToggle != null) {
                signInToggle.setOnMouseClicked(e -> {
                    e.consume();
                    switchToSignIn();
                });
            }
            if (signUpToggle != null) {
                signUpToggle.setOnMouseClicked(e -> {
                    e.consume();
                    switchToSignUp();
                });
            }
            if (signInToggle2 != null) {
                signInToggle2.setOnMouseClicked(e -> {
                    e.consume();
                    switchToSignIn();
                });
            }
            if (signUpToggle2 != null) {
                signUpToggle2.setOnMouseClicked(e -> {
                    e.consume();
                    switchToSignUp();
                });
            }
            
        } catch (Exception e) {
            System.err.println("Error setting up event handlers: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Sets up the initial state of the form
     */
    private void setupInitialState() {
        // Start with Sign In form visible
        signInForm.setVisible(true);
        signUpForm.setVisible(false);
        
        // Set initial toggle states - Sign In form is visible initially
        if (signInToggle != null) {
            signInToggle.getStyleClass().remove("active");
            signInToggle.getStyleClass().add("active");
        }
        if (signUpToggle != null) {
            signUpToggle.getStyleClass().remove("active");
        }
        if (signInToggle2 != null) {
            signInToggle2.getStyleClass().remove("active");
        }
        if (signUpToggle2 != null) {
            signUpToggle2.getStyleClass().remove("active");
            signUpToggle2.getStyleClass().add("active");
        }
    }
    
    /**
     * Switches to Sign In form with smooth transition
     */
    private void switchToSignIn() {
        // Update toggle button states
        updateToggleStates(true);
        
        // Animate form transition
        animateFormTransition(signInForm, signUpForm);
    }
    
    /**
     * Switches to Sign Up form with smooth transition
     */
    private void switchToSignUp() {
        // Update toggle button states
        updateToggleStates(false);
        
        // Animate form transition
        animateFormTransition(signUpForm, signInForm);
    }
    
    /**
     * Updates the visual state of toggle buttons
     */
    private void updateToggleStates(boolean isSignIn) {
        // Clear all active states first
        if (signInToggle != null) signInToggle.getStyleClass().remove("active");
        if (signUpToggle != null) signUpToggle.getStyleClass().remove("active");
        if (signInToggle2 != null) signInToggle2.getStyleClass().remove("active");
        if (signUpToggle2 != null) signUpToggle2.getStyleClass().remove("active");
        
        // Set active state for the correct buttons
        if (isSignIn) {
            if (signInToggle != null) signInToggle.getStyleClass().add("active");
            if (signInToggle2 != null) signInToggle2.getStyleClass().add("active");
        } else {
            if (signUpToggle != null) signUpToggle.getStyleClass().add("active");
            if (signUpToggle2 != null) signUpToggle2.getStyleClass().add("active");
        }
    }
    
    /**
     * Animates the transition between forms
     */
    private void animateFormTransition(VBox showForm, VBox hideForm) {
        // Fade out current form
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), hideForm);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        
        // Slide out current form
        TranslateTransition slideOut = new TranslateTransition(Duration.millis(200), hideForm);
        slideOut.setFromX(0);
        slideOut.setToX(-50);
        
        // Fade in new form
        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), showForm);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        
        // Slide in new form
        TranslateTransition slideIn = new TranslateTransition(Duration.millis(200), showForm);
        slideIn.setFromX(50);
        slideIn.setToX(0);
        
        // Chain animations
        fadeOut.setOnFinished(e -> {
            hideForm.setVisible(false);
            showForm.setVisible(true);
            fadeIn.play();
            slideIn.play();
        });
        
        fadeOut.play();
        slideOut.play();
    }
    
    /**
     * Handles Sign In form submission
     */
    private void handleSignIn() {
        try {
            // Get form data with null checks
            String email = (signInEmail != null) ? signInEmail.getText().trim() : "";
            String password = (signInPassword != null) ? signInPassword.getText() : "";
            
            // Clear previous errors
            hideError(signInError);
            
            // Validate input
            if (email.isEmpty() || password.isEmpty()) {
                showError(signInError, "Please fill in all fields.");
                return;
            }
            
            if (!isValidEmail(email)) {
                showError(signInError, "Please enter a valid email address.");
                return;
            }
        
            // Attempt authentication
            AuthController.AuthResult result = authController.loginUser(email, password);
            
            if (result.isSuccess()) {
                try {
                    // Clear form
                    if (signInEmail != null) signInEmail.clear();
                    if (signInPassword != null) signInPassword.clear();
                    
                    // Switch to main application
                    com.studyspace.components.SidebarView sidebarView = new com.studyspace.components.SidebarView();
                    javafx.scene.layout.BorderPane mainView = sidebarView.getView();
                    Scene mainScene = new Scene(mainView, 1200, 800);
                    mainScene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                    sceneManager.switchToCustomScene(mainScene);
                } catch (Exception e) {
                    System.err.println("Error switching to main application: " + e.getMessage());
                    e.printStackTrace();
                    showError(signInError, "Error loading main application. Please try again.");
                }
            } else {
                showError(signInError, result.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Error in handleSignIn: " + e.getMessage());
            e.printStackTrace();
            showError(signInError, "An error occurred. Please try again.");
        }
    }
    
    /**
     * Handles Sign Up form submission
     */
    private void handleSignUp() {
        try {
            // Get form data with null checks
            String fullName = (signUpFullName != null) ? signUpFullName.getText().trim() : "";
            String email = (signUpEmail != null) ? signUpEmail.getText().trim() : "";
            String password = (signUpPassword != null) ? signUpPassword.getText() : "";
            
            // Clear previous errors
            hideError(signUpError);
            
            // Validate input
            if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                showError(signUpError, "Please fill in all fields.");
                return;
            }
            
            if (!isValidEmail(email)) {
                showError(signUpError, "Please enter a valid email address.");
                return;
            }
            
            if (password.length() < 6) {
                showError(signUpError, "Password must be at least 6 characters long.");
                return;
            }
            
            // Attempt registration
            AuthController.AuthResult result = authController.registerUser(fullName, email, password);
            
            if (result.isSuccess()) {
                try {
                    // Clear form
                    if (signUpFullName != null) signUpFullName.clear();
                    if (signUpEmail != null) signUpEmail.clear();
                    if (signUpPassword != null) signUpPassword.clear();
                    
                    // Switch to main application
                    com.studyspace.components.SidebarView sidebarView = new com.studyspace.components.SidebarView();
                    javafx.scene.layout.BorderPane mainView = sidebarView.getView();
                    Scene mainScene = new Scene(mainView, 1200, 800);
                    mainScene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                    sceneManager.switchToCustomScene(mainScene);
                } catch (Exception e) {
                    System.err.println("Error switching to main application: " + e.getMessage());
                    e.printStackTrace();
                    showError(signUpError, "Error loading main application. Please try again.");
                }
            } else {
                showError(signUpError, result.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Error in handleSignUp: " + e.getMessage());
            e.printStackTrace();
            showError(signUpError, "An error occurred. Please try again.");
        }
    }
    
    /**
     * Validates email format
     */
    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".") && email.length() > 5;
    }
    
    /**
     * Shows error message
     */
    private void showError(Label errorLabel, String message) {
        if (errorLabel != null && message != null) {
            try {
                errorLabel.setText(message);
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
            } catch (Exception e) {
                System.err.println("Error showing error message: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Hides error message
     */
    private void hideError(Label errorLabel) {
        if (errorLabel != null) {
            try {
                errorLabel.setVisible(false);
                errorLabel.setManaged(false);
            } catch (Exception e) {
                System.err.println("Error hiding error message: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
