package com.studyspace.views;

import com.studyspace.models.User;
import com.studyspace.utils.DataStore;
import com.studyspace.utils.SceneManager;
import com.studyspace.components.PasswordFieldWithEye;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

//============ profile settings view =============
//this is where user profile information is managed and edited

public class ProfileSettingsView {
    
    private final DataStore dataStore;
    private final SceneManager sceneManager;
    private User currentUser;
    
    private VBox mainContainer;
    private TextField fullNameField;
    private TextField emailField;
    private PasswordField passwordField;
    private Button editButton;
    private Button saveButton;
    private Button cancelButton;
    private Button successButton;
    private boolean isEditMode = false;
    private boolean showSuccessState = false;
    
    // Password change fields
    private PasswordFieldWithEye currentPasswordField;
    private PasswordFieldWithEye newPasswordField;
    private PasswordFieldWithEye confirmNewPasswordField;
    private Button editPasswordButton;
    private Button savePasswordButton;
    private Button cancelPasswordButton;
    
    public ProfileSettingsView() {
        this.dataStore = DataStore.getInstance();
        this.sceneManager = SceneManager.getInstance();
        this.currentUser = dataStore.getCurrentUser();
        
        initializeUI();
        loadUserData();
    }
    
    /**
     * Initializes the UI components
     */
    private void initializeUI() {
        mainContainer = new VBox();
        mainContainer.setSpacing(32);
        mainContainer.getStyleClass().add("content-area");
        mainContainer.setPadding(new Insets(24));
        
        // Header section
        VBox headerSection = createHeaderSection();
        
        // Profile information section
        VBox profileSection = createProfileSection();
        
        // Password settings section
        VBox passwordSection = createPasswordSection();
        
        // Statistics section
        VBox statisticsSection = createStatisticsSection();
        
        mainContainer.getChildren().addAll(headerSection, profileSection, passwordSection, statisticsSection);
    }
    
    /**
     * Creates the header section
     */
    private VBox createHeaderSection() {
        VBox header = new VBox();
        header.setSpacing(8);
        header.setAlignment(Pos.CENTER);
        
        Label titleLabel = new Label();
        Image settingsIcon = new Image(getClass().getResourceAsStream("/images/icons8-setting-50.png"));
        ImageView settingsIconView = new ImageView(settingsIcon);
        settingsIconView.setFitWidth(32);
        settingsIconView.setFitHeight(32);
        titleLabel.setGraphic(settingsIconView);
        titleLabel.setText(" Profile Settings");
        titleLabel.getStyleClass().addAll("text-3xl", "font-bold", "text-primary");
        
        Label subtitleLabel = new Label("Manage your account information and preferences");
        subtitleLabel.getStyleClass().addAll("text-base", "text-secondary");
        
        header.getChildren().addAll(titleLabel, subtitleLabel);
        
        return header;
    }
    
    /**
     * Creates the profile information section
     */
    private VBox createProfileSection() {
        VBox section = new VBox();
        section.setSpacing(16);
        section.getStyleClass().add("card");
        section.setPadding(new Insets(24));
        
        // Header with title and edit button
        HBox headerContainer = new HBox();
        headerContainer.setSpacing(16);
        headerContainer.setAlignment(Pos.CENTER_LEFT);
        
        Label sectionTitle = new Label("⚙️ Profile Settings");
        sectionTitle.getStyleClass().addAll("text-xl", "font-semibold", "text-primary");
        
        editButton = new Button();
        Image editIcon = new Image(getClass().getResourceAsStream("/images/edit.png"));
        ImageView editIconView = new ImageView(editIcon);
        editIconView.setFitWidth(16);
        editIconView.setFitHeight(16);
        editButton.setGraphic(editIconView);
        editButton.setText(" Edit Profile");
        editButton.getStyleClass().add("secondary-button");
        editButton.setOnAction(e -> handleEditProfile());
        
        HBox.setHgrow(sectionTitle, Priority.ALWAYS);
        headerContainer.getChildren().addAll(sectionTitle, editButton);
        
        // Full Name field
        VBox fullNameContainer = new VBox();
        fullNameContainer.setSpacing(8);
        
        Label fullNameLabel = new Label("Full Name");
        fullNameLabel.getStyleClass().addAll("text-sm", "font-medium", "text-primary");
        
        fullNameField = new TextField();
        fullNameField.getStyleClass().add("form-input");
        fullNameField.setPromptText("Enter your full name");
        fullNameField.setDisable(true);
        
        fullNameContainer.getChildren().addAll(fullNameLabel, fullNameField);
        
        // Email field
        VBox emailContainer = new VBox();
        emailContainer.setSpacing(8);
        
        Label emailLabel = new Label("Email Address");
        emailLabel.getStyleClass().addAll("text-sm", "font-medium", "text-primary");
        
        emailField = new TextField();
        emailField.getStyleClass().add("form-input");
        emailField.setPromptText("Enter your email address");
        emailField.setDisable(true);
        
        emailContainer.getChildren().addAll(emailLabel, emailField);
        
        // Password field (hidden by default)
        VBox passwordContainer = new VBox();
        passwordContainer.setSpacing(8);
        passwordContainer.setVisible(false);
        passwordContainer.setManaged(false);
        passwordContainer.setId("password-container");
        
        Label passwordLabel = new Label("Password");
        passwordLabel.getStyleClass().addAll("text-sm", "font-medium", "text-primary");
        
        passwordField = new PasswordField();
        passwordField.getStyleClass().add("form-input");
        passwordField.setPromptText("Enter your password to confirm changes");
        
        passwordContainer.getChildren().addAll(passwordLabel, passwordField);
        
        // Action buttons inside the profile container
        HBox actionButtons = createActionButtons();
        actionButtons.setId("action-buttons");
        actionButtons.setVisible(false);
        actionButtons.setManaged(false);
        
        section.getChildren().addAll(headerContainer, fullNameContainer, emailContainer, actionButtons);
        
        return section;
    }
    
    /**
     * Creates the password settings section
     */
    private VBox createPasswordSection() {
        VBox section = new VBox();
        section.setSpacing(16);
        section.getStyleClass().add("card");
        section.setPadding(new Insets(24));
        
        // Header with title
        HBox headerContainer = new HBox();
        headerContainer.setSpacing(16);
        headerContainer.setAlignment(Pos.CENTER_LEFT);
        
        Label sectionTitle = new Label("🔒 Password Settings");
        sectionTitle.getStyleClass().addAll("text-xl", "font-semibold", "text-primary");
        
        HBox.setHgrow(sectionTitle, Priority.ALWAYS);
        headerContainer.getChildren().addAll(sectionTitle);
        
        // Password fields (initially hidden)
        currentPasswordField = new PasswordFieldWithEye();
        currentPasswordField.setPromptText("Current Password");
        currentPasswordField.getStyleClass().add("password-field-container");
        currentPasswordField.setMaxWidth(300);
        currentPasswordField.setVisible(false);
        currentPasswordField.setManaged(false);
        
        newPasswordField = new PasswordFieldWithEye();
        newPasswordField.setPromptText("New Password");
        newPasswordField.getStyleClass().add("password-field-container");
        newPasswordField.setMaxWidth(300);
        newPasswordField.setVisible(false);
        newPasswordField.setManaged(false);
        
        confirmNewPasswordField = new PasswordFieldWithEye();
        confirmNewPasswordField.setPromptText("Confirm New Password");
        confirmNewPasswordField.getStyleClass().add("password-field-container");
        confirmNewPasswordField.setMaxWidth(300);
        confirmNewPasswordField.setVisible(false);
        confirmNewPasswordField.setManaged(false);
        
        // Action buttons for password changes (initially hidden)
        HBox passwordActionButtons = new HBox(10);
        passwordActionButtons.setAlignment(Pos.CENTER_LEFT);
        
        savePasswordButton = new Button("💾 Save Changes");
        savePasswordButton.getStyleClass().add("primary-button");
        savePasswordButton.setOnAction(e -> handleSavePasswordChanges());
        
        cancelPasswordButton = new Button("❌ Cancel");
        cancelPasswordButton.getStyleClass().add("secondary-button");
        cancelPasswordButton.setOnAction(e -> handleCancelPasswordChanges());
        
        passwordActionButtons.getChildren().addAll(savePasswordButton, cancelPasswordButton);
        passwordActionButtons.setVisible(false);
        passwordActionButtons.setManaged(false);
        
        // Edit Password button
        editPasswordButton = new Button("🔑 Change Password");
        editPasswordButton.getStyleClass().add("primary-button");
        editPasswordButton.setOnAction(e -> handleEditPassword());
        
        // Delete Account button
        Button deleteAccountButton = new Button("🗑️ Delete Account");
        deleteAccountButton.getStyleClass().add("danger-button");
        deleteAccountButton.setOnAction(e -> handleDeleteAccount());
        
        // Button container
        HBox buttonContainer = new HBox();
        buttonContainer.setSpacing(16);
        buttonContainer.setAlignment(Pos.CENTER_LEFT);
        buttonContainer.getChildren().addAll(editPasswordButton, deleteAccountButton);
        
        section.getChildren().addAll(headerContainer, currentPasswordField, newPasswordField, confirmNewPasswordField, passwordActionButtons, buttonContainer);
        
        return section;
    }
    
    /**
     * Creates the statistics section
     */
    private VBox createStatisticsSection() {
        VBox section = new VBox();
        section.setSpacing(16);
        section.getStyleClass().add("card");
        section.setPadding(new Insets(24));
        
        Label sectionTitle = new Label("📊 Study Statistics");
        sectionTitle.getStyleClass().addAll("text-xl", "font-semibold", "text-primary");
        
        // Statistics grid
        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(24);
        statsGrid.setVgap(16);
        
        // Flashcards studied
        VBox flashcardsStat = createStatCard("📚", "Flashcards Studied", String.valueOf(currentUser.getFlashcardsStudied()));
        GridPane.setConstraints(flashcardsStat, 0, 0);
        
        // Quizzes taken
        VBox quizzesStat = createStatCard("📝", "Quizzes Taken", String.valueOf(currentUser.getQuizzesTaken()));
        GridPane.setConstraints(quizzesStat, 1, 0);
        
        // Current streak
        VBox streakStat = createStatCard("🔥", "Current Streak", currentUser.getCurrentStreak() + " days");
        GridPane.setConstraints(streakStat, 2, 0);
        
        // Total study hours
        VBox hoursStat = createStatCard("⏰", "Study Hours", currentUser.getTotalStudyHours() + "h");
        GridPane.setConstraints(hoursStat, 0, 1);
        
        // Last login
        VBox lastLoginStat = createStatCard("📅", "Last Login", formatDate(currentUser.getLastLoginAt()));
        GridPane.setConstraints(lastLoginStat, 1, 1);
        
        // Account created
        VBox accountCreatedStat = createStatCard("🎂", "Account Created", formatDate(currentUser.getCreatedAt()));
        GridPane.setConstraints(accountCreatedStat, 2, 1);
        
        statsGrid.getChildren().addAll(flashcardsStat, quizzesStat, streakStat, hoursStat, lastLoginStat, accountCreatedStat);
        
        section.getChildren().addAll(sectionTitle, statsGrid);
        
        return section;
    }
    
    /**
     * Creates a statistics card
     */
    private VBox createStatCard(String icon, String title, String value) {
        VBox card = new VBox();
        card.setSpacing(8);
        card.setAlignment(Pos.CENTER);
        card.getStyleClass().add("stat-card");
        card.setPadding(new Insets(16));
        
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 24px;");
        
        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().addAll("text-lg", "font-bold", "text-primary");
        
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().addAll("text-sm", "text-secondary");
        
        card.getChildren().addAll(iconLabel, valueLabel, titleLabel);
        
        return card;
    }
    
    /**
     * Creates the action buttons
     */
    private HBox createActionButtons() {
        HBox buttonContainer = new HBox();
        buttonContainer.setSpacing(16);
        buttonContainer.setAlignment(Pos.CENTER);
        
        saveButton = new Button("💾 Save Changes");
        saveButton.getStyleClass().add("primary-button");
        saveButton.setOnAction(e -> handleSaveChanges());
        
        cancelButton = new Button("❌ Cancel");
        cancelButton.getStyleClass().add("secondary-button");
        cancelButton.setOnAction(e -> handleCancel());
        
        successButton = new Button("✅ Profile Updated!");
        successButton.getStyleClass().add("success-button");
        successButton.setVisible(false);
        successButton.setManaged(false);
        successButton.setOnAction(e -> handleSuccessConfirmation());
        
        buttonContainer.getChildren().addAll(saveButton, cancelButton, successButton);
        
        return buttonContainer;
    }
    
    /**
     * Loads current user data into the form
     */
    private void loadUserData() {
        if (currentUser != null) {
            fullNameField.setText(currentUser.getFullName());
            emailField.setText(currentUser.getEmail());
        }
    }
    
    /**
     * Handles edit profile button click
     */
    private void handleEditProfile() {
        if (!isEditMode) {
            // Show confirmation dialog
            boolean confirmed = sceneManager.showConfirmationDialog(
                "Edit Profile", 
                "Are you sure you want to edit your profile information?"
            );
            
            if (confirmed) {
                enterEditMode();
            }
        } else {
            exitEditMode();
        }
    }
    
    /**
     * Enters edit mode
     */
    private void enterEditMode() {
        isEditMode = true;
        
        // Enable form fields
        fullNameField.setDisable(false);
        emailField.setDisable(false);
        
        // Show action buttons (now inside profile container)
        javafx.scene.Node actionButtons = findNodeById("action-buttons");
        if (actionButtons instanceof HBox) {
            actionButtons.setVisible(true);
            actionButtons.setManaged(true);
        }
        
        // Update edit button
        editButton.setText("❌ Cancel Edit");
        
        // Focus on name field
        fullNameField.requestFocus();
    }
    
    /**
     * Exits edit mode
     */
    private void exitEditMode() {
        isEditMode = false;
        showSuccessState = false;
        
        // Disable form fields
        fullNameField.setDisable(true);
        emailField.setDisable(true);
        
        // Hide action buttons (now inside profile container)
        javafx.scene.Node actionButtons = findNodeById("action-buttons");
        if (actionButtons instanceof HBox) {
            actionButtons.setVisible(false);
            actionButtons.setManaged(false);
        }
        
        // Reset buttons to normal state
        saveButton.setVisible(true);
        saveButton.setManaged(true);
        cancelButton.setVisible(true);
        cancelButton.setManaged(true);
        successButton.setVisible(false);
        successButton.setManaged(false);
        
        // Update edit button
        Image editIcon = new Image(getClass().getResourceAsStream("/images/edit.png"));
        ImageView editIconView = new ImageView(editIcon);
        editIconView.setFitWidth(16);
        editIconView.setFitHeight(16);
        editButton.setGraphic(editIconView);
        editButton.setText(" Edit Profile");
        editButton.setDisable(false);
        
        // Reset form to original values
        loadUserData();
    }
    
    /**
     * Helper method to find a node by ID
     */
    private javafx.scene.Node findNodeById(String id) {
        return findNodeByIdRecursive(mainContainer, id);
    }
    
    /**
     * Helper method to find the password action buttons container
     */
    private javafx.scene.Node findPasswordActionButtons() {
        return findPasswordActionButtonsRecursive(mainContainer);
    }
    
    /**
     * Recursively finds the password action buttons container
     */
    private javafx.scene.Node findPasswordActionButtonsRecursive(javafx.scene.Node node) {
        if (node instanceof HBox) {
            HBox hbox = (HBox) node;
            // Check if this HBox contains the password action buttons
            if (hbox.getChildren().contains(savePasswordButton) && hbox.getChildren().contains(cancelPasswordButton)) {
                return hbox;
            }
        }
        
        if (node instanceof javafx.scene.Parent) {
            for (javafx.scene.Node child : ((javafx.scene.Parent) node).getChildrenUnmodifiable()) {
                javafx.scene.Node result = findPasswordActionButtonsRecursive(child);
                if (result != null) {
                    return result;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Recursively finds a node by ID
     */
    private javafx.scene.Node findNodeByIdRecursive(javafx.scene.Node node, String id) {
        if (id.equals(node.getId())) {
            return node;
        }
        
        if (node instanceof javafx.scene.Parent) {
            for (javafx.scene.Node child : ((javafx.scene.Parent) node).getChildrenUnmodifiable()) {
                javafx.scene.Node result = findNodeByIdRecursive(child, id);
                if (result != null) {
                    return result;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Handles saving changes
     */
    private void handleSaveChanges() {
        if (validateForm()) {
            // Show simple yes/no confirmation instead of detailed changes
            boolean confirmed = sceneManager.showConfirmationDialog(
                "Confirm Profile Changes", 
                "Are you sure you want to save these changes?\n\nThis action cannot be undone."
            );
            
            if (confirmed) {
                try {
                    // Update user information
                    String newName = fullNameField.getText().trim();
                    String newEmail = emailField.getText().trim().toLowerCase();
                    
                    currentUser.setFullName(newName);
                    currentUser.setEmail(newEmail);
                    
                    // Save to DataStore
                    dataStore.updateUser(currentUser);
                    
                    // Show success state with check button
                    showSuccessState();
                    
                    // Show popup notification
                    showProfileUpdateNotification();
                    
                    // Refresh sidebar user data
                    com.studyspace.components.SidebarView.getCurrentInstance().refreshUserData();
                    
                } catch (Exception e) {
                    sceneManager.showErrorDialog("Error", "Failed to update profile: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Handles cancel action
     */
    private void handleCancel() {
        // Exit edit mode and reset form
        exitEditMode();
    }
    
    /**
     * Shows success state with check button
     */
    private void showSuccessState() {
        showSuccessState = true;
        
        // Hide save and cancel buttons
        saveButton.setVisible(false);
        saveButton.setManaged(false);
        cancelButton.setVisible(false);
        cancelButton.setManaged(false);
        
        // Show success button
        successButton.setVisible(true);
        successButton.setManaged(true);
        
        // Disable form fields
        fullNameField.setDisable(true);
        emailField.setDisable(true);
        
        // Update edit button to show success
        editButton.setText("✅ Success!");
        editButton.setDisable(true);
    }
    
    /**
     * Handles success confirmation button click
     */
    private void handleSuccessConfirmation() {
        // Exit success state and return to normal view mode
        showSuccessState = false;
        exitEditMode();
    }
    
    /**
     * Shows popup notification for profile update
     */
    private void showProfileUpdateNotification() {
        // Create a custom notification popup
        javafx.stage.Stage notificationStage = new javafx.stage.Stage();
        notificationStage.initStyle(javafx.stage.StageStyle.UNDECORATED);
        notificationStage.setAlwaysOnTop(true);
        
        VBox notificationContent = new VBox();
        notificationContent.setSpacing(12);
        notificationContent.setPadding(new Insets(20));
        notificationContent.getStyleClass().add("notification-popup");
        notificationContent.setAlignment(Pos.CENTER);
        
        // Success icon and message
        Label successIcon = new Label("✅");
        successIcon.setStyle("-fx-font-size: 32px;");
        
        Label successMessage = new Label("User Profile Updated");
        successMessage.getStyleClass().addAll("text-lg", "font-semibold", "text-primary");
        
        notificationContent.getChildren().addAll(successIcon, successMessage);
        
        javafx.scene.Scene notificationScene = new javafx.scene.Scene(notificationContent, 250, 120);
        notificationScene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        
        notificationStage.setScene(notificationScene);
        
        // Position the notification
        notificationStage.setX(javafx.stage.Screen.getPrimary().getVisualBounds().getWidth() - 270);
        notificationStage.setY(50);
        
        // Show the notification
        notificationStage.show();
        
        // Auto-hide after 3 seconds
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
            new javafx.animation.KeyFrame(javafx.util.Duration.seconds(3), e -> notificationStage.close())
        );
        timeline.play();
    }
    
    /**
     * Validates the form
     */
    private boolean validateForm() {
        StringBuilder errors = new StringBuilder();
        
        // Validate full name
        if (fullNameField.getText().trim().isEmpty()) {
            errors.append("• Full name is required\n");
        }
        
        // Validate email
        String email = emailField.getText().trim().toLowerCase();
        if (email.isEmpty()) {
            errors.append("• Email address is required\n");
        } else if (!isValidEmail(email)) {
            errors.append("• Please enter a valid email address\n");
        }
        
        // Check if email is already taken by another user
        if (!email.equals(currentUser.getEmail()) && dataStore.isEmailTaken(email)) {
            errors.append("• This email address is already in use\n");
        }
        
        if (errors.length() > 0) {
            sceneManager.showErrorDialog("Validation Error", "Please fix the following errors:\n\n" + errors.toString());
            return false;
        }
        
        return true;
    }
    
    /**
     * Validates email format
     */
    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".") && 
               email.indexOf("@") < email.lastIndexOf(".");
    }
    
    
    /**
     * Formats date for display with time
     */
    private String formatDate(LocalDateTime date) {
        if (date == null) {
            return "Unknown";
        }
        return date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' hh:mm a"));
    }
    
    /**
     * Gets the main view container
     */
    public VBox getView() {
        // Wrap main container in ScrollPane for scroll functionality
        ScrollPane scrollPane = new ScrollPane(mainContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("profile-scroll-pane");
        scrollPane.setPadding(new Insets(0));
        
        // Create container for scroll pane
        VBox scrollContainer = new VBox();
        scrollContainer.getChildren().add(scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        
        return scrollContainer;
    }
    
    /**
     * Handles edit password action - shows embedded password fields
     */
    private void handleEditPassword() {
        // Show password fields and action buttons
        currentPasswordField.setVisible(true);
        currentPasswordField.setManaged(true);
        newPasswordField.setVisible(true);
        newPasswordField.setManaged(true);
        confirmNewPasswordField.setVisible(true);
        confirmNewPasswordField.setManaged(true);
        
        // Show action buttons - make sure they are visible
        savePasswordButton.setVisible(true);
        savePasswordButton.setManaged(true);
        cancelPasswordButton.setVisible(true);
        cancelPasswordButton.setManaged(true);
        
        // Also show the password action buttons container
        javafx.scene.Node passwordActionButtons = findPasswordActionButtons();
        if (passwordActionButtons != null) {
            passwordActionButtons.setVisible(true);
            passwordActionButtons.setManaged(true);
        }
        
        // Hide edit button
        editPasswordButton.setVisible(false);
        editPasswordButton.setManaged(false);
        
        // Focus on current password field
        currentPasswordField.requestFocus();
    }
    
    /**
     * Handles saving password changes
     */
    private void handleSavePasswordChanges() {
        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmNewPasswordField.getText();
        
        // Validate inputs
        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            sceneManager.showErrorDialog("Validation Error", "All password fields are required.");
            return;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            sceneManager.showErrorDialog("Validation Error", "New password and confirmation do not match.");
            return;
        }
        
        if (newPassword.length() < 6) {
            sceneManager.showErrorDialog("Validation Error", "New password must be at least 6 characters long.");
            return;
        }
        
        // Verify current password
        if (!currentUser.getPassword().equals(currentPassword)) {
            sceneManager.showErrorDialog("Validation Error", "Current password is incorrect.");
            return;
        }
        
        // Show confirmation dialog
        boolean confirmed = sceneManager.showConfirmationDialog(
            "Confirm Password Change", 
            "Are you sure you want to change your password? This action cannot be undone."
        );
        
        if (!confirmed) {
            return;
        }
        
        try {
            // Update password in database
            dataStore.updateUserPassword(currentUser.getId(), newPassword);
            
            // Update current user object
            currentUser.setPassword(newPassword);
            
            // Show success message
            sceneManager.showInfoDialog("Success", "Password updated successfully!");
            
            // Exit password edit mode
            handleCancelPasswordChanges();
            
        } catch (Exception e) {
            sceneManager.showErrorDialog("Error", "Failed to update password: " + e.getMessage());
        }
    }
    
    /**
     * Handles canceling password changes
     */
    private void handleCancelPasswordChanges() {
        // Hide password fields and action buttons
        currentPasswordField.setVisible(false);
        currentPasswordField.setManaged(false);
        newPasswordField.setVisible(false);
        newPasswordField.setManaged(false);
        confirmNewPasswordField.setVisible(false);
        confirmNewPasswordField.setManaged(false);
        
        // Hide action buttons
        savePasswordButton.setVisible(false);
        savePasswordButton.setManaged(false);
        cancelPasswordButton.setVisible(false);
        cancelPasswordButton.setManaged(false);
        
        // Also hide the password action buttons container
        javafx.scene.Node passwordActionButtons = findPasswordActionButtons();
        if (passwordActionButtons != null) {
            passwordActionButtons.setVisible(false);
            passwordActionButtons.setManaged(false);
        }
        
        // Show edit button
        editPasswordButton.setVisible(true);
        editPasswordButton.setManaged(true);
        
        // Clear fields
        currentPasswordField.clear();
        newPasswordField.clear();
        confirmNewPasswordField.clear();
    }
    
    /**
     * Handles delete account action
     */
    private void handleDeleteAccount() {
        // Show confirmation dialog
        boolean confirmed = sceneManager.showConfirmationDialog(
            "Delete Account", 
            "Are you sure you want to delete your account?\n\n" +
            "This action is PERMANENT and cannot be undone.\n" +
            "All your data (notes, flashcards, quizzes, etc.) will be lost.\n\n" +
            "Type 'DELETE' to confirm:"
        );
        
        if (confirmed) {
            // Show text input dialog for confirmation
            javafx.scene.control.TextInputDialog confirmDialog = new javafx.scene.control.TextInputDialog();
            confirmDialog.setTitle("Confirm Account Deletion");
            confirmDialog.setHeaderText("This action is PERMANENT!");
            confirmDialog.setContentText("Type 'DELETE' to confirm account deletion:");
            
            confirmDialog.showAndWait().ifPresent(confirmation -> {
                if ("DELETE".equals(confirmation)) {
                    try {
                        // Delete user account from database
                        dataStore.deleteUser(currentUser.getId());
                        
                        // Sign out first
                        dataStore.logout();
                        
                        // Show success message
                        sceneManager.showInfoDialog("Account Deleted", "Your account has been permanently deleted.");
                        
                        // Redirect to login page
                        redirectToLoginPage();
                        
                    } catch (Exception e) {
                        sceneManager.showErrorDialog("Error", "Failed to delete account: " + e.getMessage());
                    }
                } else {
                    sceneManager.showInfoDialog("Cancelled", "Account deletion cancelled.");
                }
            });
        }
    }
    
    /**
     * Redirects to the login page after account deletion
     */
    private void redirectToLoginPage() {
        try {
            // Create new AuthView (login page)
            com.studyspace.auth.AuthView authView = new com.studyspace.auth.AuthView();
            javafx.scene.Scene authScene = authView.getView();
            
            // Add CSS stylesheet
            String cssPath = getClass().getResource("/css/styles.css").toExternalForm();
            if (cssPath != null) {
                authScene.getStylesheets().add(cssPath);
            }
            
            // Switch to login scene
            sceneManager.switchToCustomScene(authScene);
            
        } catch (Exception e) {
            System.err.println("Error redirecting to login page: " + e.getMessage());
            e.printStackTrace();
            // Fallback: show error and try to restart application
            sceneManager.showErrorDialog("Error", "Failed to redirect to login page. Please restart the application.");
        }
    }
}
