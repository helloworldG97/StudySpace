package com.studyspace.components;

import com.studyspace.auth.AuthView;
import com.studyspace.models.User;
import com.studyspace.models.Quiz;
import com.studyspace.utils.DataStore;
import com.studyspace.utils.SceneManager;
import com.studyspace.utils.IconUtils;
import com.studyspace.views.TodoListView;
import com.studyspace.models.Activity;
import com.studyspace.models.ActivityType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

//============ sidebar view =============
//this is where the main application layout and navigation is handled

public class SidebarView {
    
    private static SidebarView currentInstance;
    
    private final DataStore dataStore;
    private final SceneManager sceneManager;
    
    // Main layout components
    private BorderPane mainContainer;
    private VBox sidebar;
    private StackPane contentArea;
    
    // Sidebar components
    private VBox userInfoSection;
    private Label userAvatar;
    private Label userName;
    private Label userEmail;
    private Button signOutButton;
    
    // User profile dropdown
    private VBox userProfileDropdown;
    private boolean isDropdownOpen = false;
    
    // Navigation items
    private VBox navigationSection;
    private HBox homeNav;
    private HBox notesNav;
    private HBox flashcardsNav;
    private HBox quizzesNav;
    private HBox todoNav;
    private HBox timerNav;
    private HBox progressNav;
    
    // Notification system
    private Button notificationButton;
    private int overdueCount = 0;
    
    
    // Current active navigation item
    private HBox activeNavItem;
    
    // Current user
    private User currentUser;
    
    public SidebarView() {
        this.dataStore = DataStore.getInstance();
        this.sceneManager = SceneManager.getInstance();
        this.currentUser = dataStore.getCurrentUser();
        
        // Set current instance
        currentInstance = this;
        
        initializeUI();
        setupEventHandlers();
        loadUserData();
        
        // Set home as initial active navigation
        activeNavItem = homeNav;
        homeNav.getStyleClass().add("active");
        
        // Load home view by default
        loadHomeView();
    }
    
    /**
     * Gets the current SidebarView instance
     */
    public static SidebarView getCurrentInstance() {
        return currentInstance;
    }
    
    /**
     * Static method to refresh activity history from anywhere in the application
     */
    public static void refreshActivityHistoryGlobally() {
        if (currentInstance != null) {
            System.out.println("🌐 Global activity history refresh requested");
            currentInstance.refreshActivityHistory();
        } else {
            System.out.println("⚠️ No SidebarView instance available for global refresh");
        }
    }
    
    /**
     * Static method to refresh all views globally
     */
    public static void refreshAllViewsGlobally() {
        if (currentInstance != null) {
            System.out.println("🔄 Global refresh requested for all views");
            currentInstance.refreshAllViews();
        } else {
            System.out.println("⚠️ No SidebarView instance available for global refresh");
        }
    }
    
    /**
     * Refreshes all views in the application
     */
    private void refreshAllViews() {
        // Refresh the current active view
        if (activeNavItem != null) {
            String currentSection = getCurrentSection();
            System.out.println("🔄 Refreshing current section: " + currentSection);
            
            switch (currentSection.toLowerCase()) {
                case "home":
                    loadHomeView();
                    break;
                case "notes":
                    loadNotesView();
                    break;
                case "flashcards":
                    loadFlashcardsView();
                    break;
                case "quizzes":
                    loadQuizzesView();
                    break;
                case "todo":
                    loadTodoView();
                    break;
                case "timer":
                    loadTimerView();
                    break;
                case "progress":
                    loadProgressView();
                    break;
            }
        }
    }
    
    /**
     * Gets the current active section
     */
    private String getCurrentSection() {
        if (activeNavItem == homeNav) return "home";
        if (activeNavItem == notesNav) return "notes";
        if (activeNavItem == flashcardsNav) return "flashcards";
        if (activeNavItem == quizzesNav) return "quizzes";
        if (activeNavItem == todoNav) return "todo";
        if (activeNavItem == timerNav) return "timer";
        if (activeNavItem == progressNav) return "progress";
        return "home";
    }
    
    /**
     * Initializes the UI components
     */
    private void initializeUI() {
        createMainContainer();
        createSidebar();
        createUserInfoSection();
        createNotificationButton();
        createNavigationSection();
        createContentArea();
        assembleSidebar();
        assembleMainLayout();
    }
    
    /**
     * Creates the main container
     */
    private void createMainContainer() {
        mainContainer = new BorderPane();
        mainContainer.getStyleClass().add("main-container");
    }
    
    /**
     * Creates the sidebar
     */
    private void createSidebar() {
        sidebar = new VBox();
        sidebar.getStyleClass().add("sidebar");
        sidebar.setSpacing(0);
    }
    
    /**
     * Creates the user info section at the top of sidebar
     */
    private void createUserInfoSection() {
        userInfoSection = new VBox();
        userInfoSection.getStyleClass().add("sidebar-header");
        
        // User info container (clickable)
        HBox userInfo = new HBox();
        userInfo.getStyleClass().add("sidebar-user-info");
        userInfo.setCursor(javafx.scene.Cursor.HAND);
        
        // User avatar
        userAvatar = new Label();
        userAvatar.getStyleClass().add("sidebar-user-avatar");
        
        // User details
        VBox userDetails = new VBox();
        userDetails.setSpacing(2);
        
        userName = new Label();
        userName.getStyleClass().add("sidebar-user-name");
        
        userEmail = new Label();
        userEmail.getStyleClass().add("sidebar-user-email");
        
        userDetails.getChildren().addAll(userName, userEmail);
        userInfo.getChildren().addAll(userAvatar, userDetails);
        
        // Create dropdown menu
        createUserProfileDropdown();
        
        // Add click handler to user info
        userInfo.setOnMouseClicked(e -> toggleUserProfileDropdown());
        
        userInfoSection.getChildren().addAll(userInfo, userProfileDropdown);
    }
    
    /**
     * Creates the notification button
     */
    private void createNotificationButton() {
        notificationButton = new Button();
        notificationButton.setGraphic(IconUtils.createIconView("clock"));
        notificationButton.getStyleClass().add("notification-button");
        notificationButton.setVisible(false);
        notificationButton.setManaged(false);
        notificationButton.setOnAction(e -> showOverdueNotification());
        
        // Check for overdue items
        checkOverdueItems();
    }
    
    /**
     * Checks for overdue todo items and updates notification
     */
    private void checkOverdueItems() {
        overdueCount = 0;
        java.time.LocalDate today = java.time.LocalDate.now();
        
        for (com.studyspace.models.TodoItem item : dataStore.getTodoItems()) {
            if (!item.isCompleted() && item.getDueDate() != null && item.getDueDate().isBefore(today)) {
                overdueCount++;
            }
        }
        
        if (overdueCount > 0) {
            notificationButton.setVisible(true);
            notificationButton.setManaged(true);
            notificationButton.setText(" " + overdueCount);
        } else {
            notificationButton.setVisible(false);
            notificationButton.setManaged(false);
        }
    }
    
    /**
     * Shows overdue notification dialog
     */
    private void showOverdueNotification() {
        // Recalculate overdue count to ensure accuracy
        int currentOverdueCount = 0;
        java.time.LocalDate today = java.time.LocalDate.now();
        
        for (com.studyspace.models.TodoItem item : dataStore.getTodoItems()) {
            if (!item.isCompleted() && item.getDueDate() != null && item.getDueDate().isBefore(today)) {
                currentOverdueCount++;
            }
        }
        
        if (currentOverdueCount > 0) {
            String message = "You have " + currentOverdueCount + " overdue todo item" + (currentOverdueCount > 1 ? "s" : "") + "!\n\n";
            message += "Please check your Todo List to complete them.";
            
            sceneManager.showInfoDialog("Todo List Overdue", message);
        } else {
            // No overdue items, hide notification
            notificationButton.setVisible(false);
            notificationButton.setManaged(false);
        }
    }
    
    /**
     * Creates the user profile dropdown menu
     */
    private void createUserProfileDropdown() {
        userProfileDropdown = new VBox();
        userProfileDropdown.getStyleClass().add("user-profile-dropdown");
        userProfileDropdown.setSpacing(4);
        userProfileDropdown.setVisible(false);
        userProfileDropdown.setManaged(false);
        
        // Settings button
        Button settingsButton = new Button();
        settingsButton.setGraphic(IconUtils.createIconView("settings"));
        settingsButton.setText(" Settings");
        settingsButton.getStyleClass().add("dropdown-menu-item");
        settingsButton.setMaxWidth(Double.MAX_VALUE);
        settingsButton.setOnAction(e -> handleSettings());
        
        // About us button
        Button aboutButton = new Button("ℹ️ About Us");
        aboutButton.getStyleClass().add("dropdown-menu-item");
        aboutButton.setMaxWidth(Double.MAX_VALUE);
        aboutButton.setOnAction(e -> handleAboutUs());
        
        // Sign out button
        signOutButton = new Button("🚪 Sign Out");
        signOutButton.getStyleClass().add("dropdown-menu-item");
        signOutButton.setMaxWidth(Double.MAX_VALUE);
        signOutButton.setOnAction(e -> handleSignOut());
        
        userProfileDropdown.getChildren().addAll(settingsButton, aboutButton, signOutButton);
    }
    
    /**
     * Toggles the user profile dropdown visibility
     */
    private void toggleUserProfileDropdown() {
        if (isDropdownOpen) {
            hideUserProfileDropdown();
        } else {
            showUserProfileDropdown();
        }
    }
    
    /**
     * Shows the user profile dropdown
     */
    private void showUserProfileDropdown() {
        userProfileDropdown.setVisible(true);
        userProfileDropdown.setManaged(true);
        isDropdownOpen = true;
        
        // Add fade in animation
        javafx.animation.FadeTransition fadeIn = new javafx.animation.FadeTransition(
            javafx.util.Duration.millis(200), userProfileDropdown);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }
    
    /**
     * Hides the user profile dropdown
     */
    private void hideUserProfileDropdown() {
        javafx.animation.FadeTransition fadeOut = new javafx.animation.FadeTransition(
            javafx.util.Duration.millis(200), userProfileDropdown);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> {
            userProfileDropdown.setVisible(false);
            userProfileDropdown.setManaged(false);
            isDropdownOpen = false;
        });
        fadeOut.play();
    }
    
    /**
     * Handles settings button click
     */
    private void handleSettings() {
        hideUserProfileDropdown();
        loadProfileSettingsView();
    }
    
    /**
     * Loads the profile settings view
     */
    private void loadProfileSettingsView() {
        try {
            contentArea.getChildren().clear();
            
            // Create the profile settings view
            com.studyspace.views.ProfileSettingsView profileSettingsView = new com.studyspace.views.ProfileSettingsView();
            VBox profileViewContainer = profileSettingsView.getView();
            
            // Add to StackPane with proper alignment
            contentArea.getChildren().add(profileViewContainer);
            StackPane.setAlignment(profileViewContainer, Pos.TOP_LEFT);
            
            System.out.println("Profile settings view loaded successfully");
            
        } catch (Exception e) {
            System.err.println("Error loading profile settings view: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    
    /**
     * Handles about us button click
     */
    private void handleAboutUs() {
        hideUserProfileDropdown();
        
        // Load About Us view
        contentArea.getChildren().clear();
        
        com.studyspace.views.AboutUsView aboutUsView = new com.studyspace.views.AboutUsView();
        VBox aboutUsContent = aboutUsView.getView();
        
        contentArea.getChildren().add(aboutUsContent);
    }
    
    /**
     * Handles sign out button click
     */
    private void handleSignOut() {
        hideUserProfileDropdown();
        
        boolean confirmed = sceneManager.showConfirmationDialog(
            "Sign Out Confirmation", 
            "Are you sure you want to sign out?"
        );
        
        if (confirmed) {
            // Clear user session
            dataStore.logout();
            
            // Create new auth view and switch to it
            AuthView authView = new AuthView();
            Scene authScene = authView.getView();
            
            // Add CSS stylesheet
            String cssPath = getClass().getResource("/css/styles.css").toExternalForm();
            authScene.getStylesheets().add(cssPath);
            
            sceneManager.switchToCustomScene(authScene);
        }
    }
    
    /**
     * Creates the navigation section
     */
    private void createNavigationSection() {
        navigationSection = new VBox();
        navigationSection.setSpacing(0);
        
        // Main section
        VBox mainSection = new VBox();
        mainSection.getStyleClass().add("sidebar-section");
        
        Label mainTitle = new Label("MAIN");
        mainTitle.getStyleClass().add("sidebar-section-title");
        
        // Navigation items
        homeNav = createNavItemWithIcon("home", "Home");
        notesNav = createNavItemWithIcon("note", "Notes");
        flashcardsNav = createNavItemWithIcon("cards", "Flashcards");
        quizzesNav = createNavItemWithIcon("question", "Quizzes");
        mainSection.getChildren().addAll(mainTitle, homeNav, notesNav, flashcardsNav, quizzesNav);
        
        // Study tools section
        VBox toolsSection = new VBox();
        toolsSection.getStyleClass().add("sidebar-section");
        
        Label toolsTitle = new Label("STUDY TOOLS");
        toolsTitle.getStyleClass().add("sidebar-section-title");
        
        todoNav = createNavItemWithIcon("check", "Todo List");
        timerNav = createNavItemWithIcon("clock", "Take a Break");
        progressNav = createNavItemWithIcon("trending-up", "Progress");
        
        toolsSection.getChildren().addAll(toolsTitle, todoNav, timerNav, progressNav);
        
        navigationSection.getChildren().addAll(mainSection, toolsSection);
    }
    
    /**
     * Creates a navigation item
     */
    private HBox createNavItem(String icon, String text) {
        HBox navItem = new HBox();
        navItem.getStyleClass().add("sidebar-item");
        
        Label iconLabel = new Label(icon);
        iconLabel.getStyleClass().add("sidebar-icon");
        
        Label textLabel = new Label(text);
        textLabel.getStyleClass().add("sidebar-text");
        
        navItem.getChildren().addAll(iconLabel, textLabel);
        
        return navItem;
    }
    
    /**
     * Creates a navigation item with SVG icon
     */
    private HBox createNavItemWithIcon(String iconName, String text) {
        HBox navItem = new HBox();
        navItem.getStyleClass().add("sidebar-item");
        
        Label iconLabel = new Label();
        iconLabel.setGraphic(IconUtils.createIconView(iconName));
        iconLabel.getStyleClass().add("sidebar-icon");
        
        Label textLabel = new Label(text);
        textLabel.getStyleClass().add("sidebar-text");
        
        navItem.getChildren().addAll(iconLabel, textLabel);
        
        return navItem;
    }
    
    /**
     * Creates the content area with optimized StackPane
     */
    private void createContentArea() {
        contentArea = new StackPane();
        contentArea.getStyleClass().add("content-area");
        
        // Set up proper event handling for StackPane
        contentArea.setOnMouseClicked(e -> {
            // Handle clicks on empty areas of the content area
            e.consume();
        });
        
        // Ensure proper sizing and alignment
        contentArea.setAlignment(Pos.TOP_LEFT);
        contentArea.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
    }
    
    /**
     * Assembles the sidebar components
     */
    private void assembleSidebar() {
        // Create today's progress section
        VBox progressSection = createTodaysProgressSection();
        
        sidebar.getChildren().addAll(userInfoSection, new Separator(), navigationSection, new Separator(), progressSection, new Separator(), notificationButton);
    }
    
    /**
     * Assembles the main layout
     */
    private void assembleMainLayout() {
        mainContainer.setLeft(sidebar);
        mainContainer.setCenter(contentArea);
    }
    
    /**
     * Sets up event handlers
     */
    private void setupEventHandlers() {
        // Sign out button
        signOutButton.setOnAction(e -> handleSignOut());
        
        // Navigation items
        homeNav.setOnMouseClicked(e -> handleNavigation("home", homeNav));
        notesNav.setOnMouseClicked(e -> handleNavigation("notes", notesNav));
        flashcardsNav.setOnMouseClicked(e -> handleNavigation("flashcards", flashcardsNav));
        quizzesNav.setOnMouseClicked(e -> handleNavigation("quizzes", quizzesNav));
        todoNav.setOnMouseClicked(e -> handleNavigation("todo", todoNav));
        timerNav.setOnMouseClicked(e -> handleNavigation("timer", timerNav));
        progressNav.setOnMouseClicked(e -> handleNavigation("progress", progressNav));
    }
    
    /**
     * Loads user data into the sidebar
     */
    private void loadUserData() {
        if (currentUser != null) {
            // Set user avatar with initials
            String initials = "";
            if (currentUser.getFullName() != null && !currentUser.getFullName().trim().isEmpty()) {
                String[] names = currentUser.getFullName().trim().split("\\s+");
                if (names.length > 0) {
                    initials += names[0].charAt(0);
                    if (names.length > 1) {
                        initials += names[names.length - 1].charAt(0);
                    }
                }
            }
            userAvatar.setText(initials.toUpperCase());
            
            // Set user name and email
            userName.setText(currentUser.getFullName());
            userEmail.setText(currentUser.getEmail());
        }
    }
    
    /**
     * Handles navigation between different sections
     */
    private void handleNavigation(String section, HBox navItem) {
        System.out.println("=== handleNavigation called ===");
        System.out.println("Section: " + section);
        System.out.println("NavItem: " + navItem.getClass().getSimpleName());
        
        // Update active navigation item
        updateActiveNavigation(navItem);
        
        // Handle navigation based on section
        switch (section.toLowerCase()) {
            case "home":
                loadHomeView();
                break;
            case "notes":
                System.out.println("Calling loadNotesView()");
                loadNotesView();
                break;
            case "flashcards":
                loadFlashcardsView();
                break;
            case "quizzes":
                loadQuizzesView();
                break;
            case "todo":
                loadTodoView();
                break;
            case "timer":
                loadTimerView();
                break;
            case "progress":
                loadProgressView();
                break;
            default:
                System.out.println("Unknown navigation: " + section);
        }
    }
    
    /**
     * Updates the active navigation item styling
     */
    private void updateActiveNavigation(HBox newActiveItem) {
        // Remove active class from current active item
        if (activeNavItem != null) {
            activeNavItem.getStyleClass().remove("active");
        }
        
        // Add active class to new active item
        newActiveItem.getStyleClass().add("active");
        activeNavItem = newActiveItem;
    }
    
    /**
     * Loads the home view with proper StackPane management
     */
    private void loadHomeView() {
        try {
            contentArea.getChildren().clear();
            
            // Create the home dashboard
            VBox homeView = createHomeDashboard();
            
            // Add to StackPane with proper event handling
            contentArea.getChildren().add(homeView);
            
            // Ensure the content is properly positioned
            StackPane.setAlignment(homeView, Pos.TOP_LEFT);
            
            // Set selected date to today and refresh activity history
            selectedDate = LocalDate.now();
            System.out.println("🏠 Loading home view - refreshing activity history for today: " + selectedDate);
            refreshActivityHistory();
            
        } catch (Exception e) {
            System.err.println("Error loading home view: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Loads the notes view with proper StackPane management
     */
    private void loadNotesView() {
        try {
            System.out.println("=== loadNotesView called ===");
            // Only load NotesView when the notes navigation is clicked
            contentArea.getChildren().clear();
            com.studyspace.views.NotesView notesView = new com.studyspace.views.NotesView();
            StackPane notesViewContainer = notesView.getView();
            // Add to StackPane with proper alignment
            contentArea.getChildren().add(notesViewContainer);
            StackPane.setAlignment(notesViewContainer, Pos.TOP_LEFT);
            System.out.println("Notes view loaded successfully. Content area children: " + contentArea.getChildren().size());
            
        } catch (Exception e) {
            System.err.println("Error loading notes view: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Loads the flashcards view with proper StackPane management
     */
    private void loadFlashcardsView() {
        try {
            contentArea.getChildren().clear();
            
            // Create the flashcard list view
            com.studyspace.views.FlashcardListView flashcardListView = new com.studyspace.views.FlashcardListView();
            VBox flashcardViewContainer = flashcardListView.getView();
            
            // Add to StackPane with proper alignment
            contentArea.getChildren().add(flashcardViewContainer);
            StackPane.setAlignment(flashcardViewContainer, Pos.TOP_LEFT);
            
        } catch (Exception e) {
            System.err.println("Error loading flashcards view: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Loads the quizzes view with proper StackPane management
     */
    private void loadQuizzesView() {
        try {
            contentArea.getChildren().clear();
            
            // Create the quiz list view
            com.studyspace.views.QuizListView quizListView = new com.studyspace.views.QuizListView();
            VBox quizViewContainer = quizListView.getView();
            
            // Add to StackPane with proper alignment
            contentArea.getChildren().add(quizViewContainer);
            StackPane.setAlignment(quizViewContainer, Pos.TOP_LEFT);
            
        } catch (Exception e) {
            System.err.println("Error loading quizzes view: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    
    /**
     * Loads the todo view with proper StackPane management
     */
    private void loadTodoView() {
        try {
            contentArea.getChildren().clear();
            
            TodoListView todoView = new TodoListView();
            
            // Add to StackPane with proper alignment
            contentArea.getChildren().add(todoView);
            StackPane.setAlignment(todoView, Pos.TOP_LEFT);
            
        } catch (Exception e) {
            System.err.println("Error loading todo view: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Loads the snake game view for taking a break
     */
    private void loadTimerView() {
        try {
            contentArea.getChildren().clear();
            
            // Create the snake game view
            com.studyspace.views.SnakeGameView snakeGameView = new com.studyspace.views.SnakeGameView();
            VBox snakeGameViewContainer = snakeGameView.getView();
            
            // Add to StackPane with proper alignment
            contentArea.getChildren().add(snakeGameViewContainer);
            StackPane.setAlignment(snakeGameViewContainer, Pos.CENTER);
            
        } catch (Exception e) {
            System.err.println("Error loading snake game view: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Loads the progress view
     */
    private void loadProgressView() {
        try {
            contentArea.getChildren().clear();
            
            VBox progressView = createProgressDashboard();
            
            // Add to StackPane with proper alignment
            contentArea.getChildren().add(progressView);
            StackPane.setAlignment(progressView, Pos.TOP_LEFT);
            
            System.out.println("Progress view loaded successfully");
            
        } catch (Exception e) {
            System.err.println("Error loading progress view: " + e.getMessage());
            e.printStackTrace();
            
            // Show error message to user
            VBox errorView = new VBox();
            errorView.setAlignment(Pos.CENTER);
            errorView.setSpacing(16);
            
            Label errorLabel = new Label("Error loading progress view");
            errorLabel.getStyleClass().addAll("text-xl", "font-semibold", "text-primary");
            
            Label detailsLabel = new Label("Please try again or contact support if the issue persists.");
            detailsLabel.getStyleClass().addAll("text-base", "text-secondary");
            
            errorView.getChildren().addAll(errorLabel, detailsLabel);
            contentArea.getChildren().add(errorView);
        }
    }
    
    
    /**
     * Creates the home dashboard with quick access buttons and todo list
     */
    private VBox createHomeDashboard() {
        VBox dashboard = new VBox();
        dashboard.setSpacing(32);
        dashboard.getStyleClass().add("content-area");
        
        // Welcome section
        VBox welcomeSection = new VBox();
        welcomeSection.setSpacing(8);
        
        String greeting = getTimeBasedGreeting();
        Label welcomeLabel = new Label(greeting + ", " + (currentUser != null ? currentUser.getFullName().split(" ")[0] : "Student") + "!");
        welcomeLabel.getStyleClass().addAll("text-3xl", "font-bold", "text-primary");
        
        Label subtitleLabel = new Label("Quick access to your study tools and tasks.");
        subtitleLabel.getStyleClass().addAll("text-base", "text-secondary");
        
        welcomeSection.getChildren().addAll(welcomeLabel, subtitleLabel);
        
        // Top section with Todo List and Recent Activity
        HBox topSection = new HBox();
        topSection.setSpacing(24);
        topSection.setAlignment(Pos.TOP_LEFT);
        
        // Todo List section
        VBox todoListCard = createTodoListCard();
        
        // Recent Activity section
        VBox recentActivityCard = createRecentActivityCard();
        
        HBox.setHgrow(todoListCard, Priority.ALWAYS);
        HBox.setHgrow(recentActivityCard, Priority.ALWAYS);
        
        topSection.getChildren().addAll(todoListCard, recentActivityCard);
        
        // Quick Access section
        VBox quickAccessCard = createQuickAccessCard();
        
        dashboard.getChildren().addAll(welcomeSection, topSection, quickAccessCard);
        
        // Wrap dashboard in scroll pane
        ScrollPane scrollPane = new ScrollPane(dashboard);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("home-scroll-pane");
        scrollPane.setPadding(new Insets(0));
        
        // Create container for scroll pane
        VBox scrollContainer = new VBox();
        scrollContainer.getChildren().add(scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        
        // Initialize activity list with today's date
        selectedDate = LocalDate.now();
        System.out.println("🏗️ Creating home dashboard - initializing activity list for today: " + selectedDate);
        updateActivityList();
        
        return scrollContainer;
    }
    
    /**
     * Creates a statistics card
     */
    private VBox createStatCard(String icon, String value, String label) {
        VBox card = new VBox();
        card.getStyleClass().add("stat-card");
        card.setAlignment(Pos.CENTER_LEFT);
        card.setSpacing(8);
        
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 24px;");
        
        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("stat-value");
        
        Label labelText = new Label(label);
        labelText.getStyleClass().add("stat-label");
        
        card.getChildren().addAll(iconLabel, valueLabel, labelText);
        
        return card;
    }
    
    /**
     * Creates a statistics card with SVG icon
     */
    private VBox createStatCardWithIcon(String iconName, String value, String label) {
        VBox card = new VBox();
        card.getStyleClass().add("stat-card");
        card.setAlignment(Pos.CENTER_LEFT);
        card.setSpacing(8);
        
        Label iconLabel = new Label();
        iconLabel.setGraphic(IconUtils.createLargeIconView(iconName));
        
        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("stat-value");
        
        Label labelText = new Label(label);
        labelText.getStyleClass().add("stat-label");
        
        card.getChildren().addAll(iconLabel, valueLabel, labelText);
        
        return card;
    }
    
    /**
     * Creates the recent activity card with calendar
     */
    private VBox createRecentActivityCard() {
        VBox card = new VBox();
        card.getStyleClass().add("card");
        card.setSpacing(16);
        
        Label title = new Label("Activity History");
        title.getStyleClass().addAll("text-xl", "font-semibold", "text-primary");
        
        // Calendar section
        VBox calendarSection = createActivityCalendar();
        
        // Activity list section
        VBox activityListSection = createActivityListSection();
        
        card.getChildren().addAll(title, calendarSection, activityListSection);
        
        return card;
    }
    
    /**
     * Creates the activity calendar section
     */
    private VBox createActivityCalendar() {
        VBox calendarSection = new VBox();
        calendarSection.setSpacing(12);
        
        // Calendar header with navigation and minimize button
        HBox calendarHeader = new HBox();
        calendarHeader.setSpacing(8);
        calendarHeader.setAlignment(Pos.CENTER_LEFT);
        
        Button prevWeekButton = new Button("←");
        prevWeekButton.getStyleClass().add("calendar-nav-button");
        prevWeekButton.setOnAction(e -> navigateCalendar(-7));
        
        Label weekLabel = new Label();
        weekLabel.getStyleClass().addAll("text-lg", "font-semibold", "text-primary");
        weekLabel.setId("week-label");
        
        Button nextWeekButton = new Button("→");
        nextWeekButton.getStyleClass().add("calendar-nav-button");
        nextWeekButton.setOnAction(e -> navigateCalendar(7));
        
        Button todayButton = new Button("Today");
        todayButton.getStyleClass().add("calendar-today-button");
        todayButton.setOnAction(e -> goToToday());
        
        // Minimize/Expand button
        Button minimizeButton = new Button("−");
        minimizeButton.getStyleClass().add("calendar-minimize-button");
        minimizeButton.setId("calendar-minimize-button");
        minimizeButton.setOnAction(e -> toggleCalendarMinimize());
        
        calendarHeader.getChildren().addAll(prevWeekButton, weekLabel, nextWeekButton, todayButton, minimizeButton);
        
        // Calendar grid container
        VBox calendarGridContainer = new VBox();
        calendarGridContainer.getStyleClass().add("calendar-grid-container");
        calendarGridContainer.setId("calendar-grid-container");
        
        // Calendar grid
        VBox calendarGrid = createCalendarGrid();
        calendarGrid.setId("calendar-grid");
        
        calendarGridContainer.getChildren().add(calendarGrid);
        
        calendarSection.getChildren().addAll(calendarHeader, calendarGridContainer);
        
        // Initialize calendar
        updateCalendar();
        
        return calendarSection;
    }
    
    /**
     * Creates the calendar grid
     */
    private VBox createCalendarGrid() {
        VBox calendarGrid = new VBox();
        calendarGrid.getStyleClass().add("calendar-grid");
        calendarGrid.setSpacing(4);
        
        // Day headers
        HBox dayHeaders = new HBox();
        dayHeaders.setSpacing(4);
        dayHeaders.getStyleClass().add("calendar-day-headers");
        
        String[] dayNames = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (String dayName : dayNames) {
            Label dayHeader = new Label(dayName);
            dayHeader.getStyleClass().add("calendar-day-header");
            dayHeader.setPrefWidth(40);
            dayHeader.setAlignment(Pos.CENTER);
            dayHeaders.getChildren().add(dayHeader);
        }
        
        calendarGrid.getChildren().add(dayHeaders);
        
        // Calendar rows (we'll add these dynamically)
        for (int week = 0; week < 6; week++) {
            HBox weekRow = new HBox();
            weekRow.setSpacing(4);
            weekRow.getStyleClass().add("calendar-week-row");
            weekRow.setId("week-row-" + week);
            calendarGrid.getChildren().add(weekRow);
        }
        
        return calendarGrid;
    }
    
    /**
     * Creates the activity list section with optimized scrollable view
     */
    private VBox createActivityListSection() {
        VBox activityListSection = new VBox();
        activityListSection.setSpacing(8); // Reduced spacing
        activityListSection.setPadding(new Insets(0, 0, 0, 0)); // Remove extra padding
        
        // Selected date label with compact styling
        Label selectedDateLabel = new Label();
        selectedDateLabel.getStyleClass().addAll("text-sm", "font-medium", "text-secondary");
        selectedDateLabel.setId("selected-date-label");
        selectedDateLabel.setPadding(new Insets(0, 0, 4, 0)); // Minimal padding
        
        // Activity list container with optimized spacing
        VBox activityListContainer = new VBox();
        activityListContainer.getStyleClass().add("home-activity-list");
        activityListContainer.setSpacing(4); // Reduced spacing between activities
        activityListContainer.setId("home-activity-list");
        activityListContainer.setPadding(new Insets(4, 0, 4, 0)); // Minimal padding
        activityListContainer.setPrefWidth(300); // Match container width
        activityListContainer.setMaxWidth(300); // Prevent width expansion
        
        // Create scrollable container for activities with fixed dimensions
        ScrollPane activityScrollPane = new ScrollPane(activityListContainer);
        activityScrollPane.setFitToWidth(true);
        activityScrollPane.setFitToHeight(false);
        activityScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        activityScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        activityScrollPane.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        
        // Fixed dimensions to prevent container from enlarging
        activityScrollPane.setPrefHeight(250); // Fixed height
        activityScrollPane.setMinHeight(250); // Minimum height
        activityScrollPane.setMaxHeight(250); // Maximum height - prevents enlargement
        activityScrollPane.setPrefWidth(300); // Fixed width to match container
        activityScrollPane.setMinWidth(300); // Minimum width
        activityScrollPane.setMaxWidth(300); // Maximum width
        
        activityListSection.getChildren().addAll(selectedDateLabel, activityScrollPane);
        
        return activityListSection;
    }
    
    // Calendar state variables
    private LocalDate currentCalendarDate = LocalDate.now();
    private LocalDate selectedDate = LocalDate.now();
    private boolean calendarMinimized = false;
    
    /**
     * Navigates the calendar by the specified number of days
     */
    private void navigateCalendar(int days) {
        currentCalendarDate = currentCalendarDate.plusDays(days);
        updateCalendar();
    }
    
    /**
     * Goes to today's date
     */
    private void goToToday() {
        currentCalendarDate = LocalDate.now();
        selectedDate = LocalDate.now();
        updateCalendar();
        updateActivityList();
    }
    
    /**
     * Toggles calendar minimize/expand state
     */
    private void toggleCalendarMinimize() {
        calendarMinimized = !calendarMinimized;
        
        // Find calendar grid container
        javafx.scene.Node gridContainerNode = findNodeById("calendar-grid-container");
        if (gridContainerNode instanceof VBox) {
            VBox gridContainer = (VBox) gridContainerNode;
            gridContainer.setVisible(!calendarMinimized);
            gridContainer.setManaged(!calendarMinimized);
        }
        
        // Update minimize button text
        javafx.scene.Node minimizeButtonNode = findNodeById("calendar-minimize-button");
        if (minimizeButtonNode instanceof Button) {
            Button minimizeButton = (Button) minimizeButtonNode;
            minimizeButton.setText(calendarMinimized ? "+" : "−");
        }
    }
    
    /**
     * Updates the calendar display
     */
    private void updateCalendar() {
        // Update week label
        LocalDate startOfWeek = currentCalendarDate.minusDays(currentCalendarDate.getDayOfWeek().getValue() % 7);
        LocalDate endOfWeek = startOfWeek.plusDays(6);
        
        String weekText = startOfWeek.format(DateTimeFormatter.ofPattern("MMM dd")) + " - " + 
                         endOfWeek.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
        
        // Find and update week label
        javafx.scene.Node weekLabelNode = findNodeById("week-label");
        if (weekLabelNode instanceof Label) {
            ((Label) weekLabelNode).setText(weekText);
        }
        
        // Clear existing calendar days
        for (int week = 0; week < 6; week++) {
            javafx.scene.Node weekRowNode = findNodeById("week-row-" + week);
            if (weekRowNode instanceof HBox) {
                ((HBox) weekRowNode).getChildren().clear();
            }
        }
        
        // Fill calendar with days
        LocalDate firstDayOfMonth = currentCalendarDate.withDayOfMonth(1);
        LocalDate startDate = firstDayOfMonth.minusDays(firstDayOfMonth.getDayOfWeek().getValue() % 7);
        
        for (int week = 0; week < 6; week++) {
            javafx.scene.Node weekRowNode = findNodeById("week-row-" + week);
            if (weekRowNode instanceof HBox) {
                HBox weekRow = (HBox) weekRowNode;
                
                for (int day = 0; day < 7; day++) {
                    LocalDate dayDate = startDate.plusDays(week * 7 + day);
                    Button dayButton = createCalendarDayButton(dayDate);
                    weekRow.getChildren().add(dayButton);
                }
            }
        }
    }
    
    /**
     * Creates a calendar day button
     */
    private Button createCalendarDayButton(LocalDate date) {
        Button dayButton = new Button(String.valueOf(date.getDayOfMonth()));
        dayButton.getStyleClass().add("calendar-day-button");
        dayButton.setPrefWidth(40);
        dayButton.setPrefHeight(32);
        
        // Set default text color to black for visibility
        dayButton.setStyle("-fx-text-fill: #000000;");
        
        // Style based on date
        if (date.equals(LocalDate.now())) {
            dayButton.getStyleClass().add("calendar-today");
            dayButton.setStyle("-fx-text-fill: #ffffff;"); // White text for today
        } else if (date.equals(selectedDate)) {
            dayButton.getStyleClass().add("calendar-selected");
            dayButton.setStyle("-fx-text-fill: #ffffff;"); // White text for selected
        } else if (date.getMonth() != currentCalendarDate.getMonth()) {
            dayButton.getStyleClass().add("calendar-other-month");
            dayButton.setStyle("-fx-text-fill: #9ca3af;"); // Gray text for other months
        } else {
            dayButton.setStyle("-fx-text-fill: #000000;"); // Black text for current month
        }
        
        // Check if there are activities on this date
        if (currentUser != null) {
            List<Activity> dayActivities = dataStore.getActivitiesForUser(currentUser.getId(), date);
            if (!dayActivities.isEmpty()) {
                dayButton.getStyleClass().add("calendar-has-activities");
                // Keep text color black even with activity highlight
                if (!date.equals(LocalDate.now()) && !date.equals(selectedDate)) {
                    dayButton.setStyle("-fx-text-fill: #000000;");
                }
            }
        }
        
        // Set click handler
        dayButton.setOnAction(e -> {
            selectedDate = date;
            updateCalendar(); // Refresh to update selection
            updateActivityList();
        });
        
        return dayButton;
    }
    
    /**
     * Updates the activity list for the selected date with enhanced recent activities view
     */
    private void updateActivityList() {
        System.out.println("🔍 Updating activity list for date: " + selectedDate);
        
        // Update selected date label
        javafx.scene.Node selectedDateLabelNode = findNodeById("selected-date-label");
        if (selectedDateLabelNode instanceof Label) {
            String dateText = selectedDate.format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy"));
            Label dateLabel = (Label) selectedDateLabelNode;
            dateLabel.setText(dateText);
            dateLabel.setStyle("-fx-text-fill: #000000; -fx-font-weight: bold; -fx-font-size: 14px;");
            System.out.println("📅 Updated date label to: " + dateText);
        } else {
            System.out.println("⚠️ Could not find selected-date-label node");
        }
        
        // Clear and populate activity list
        javafx.scene.Node activityListNode = findNodeById("home-activity-list");
        if (activityListNode instanceof VBox) {
            VBox activityList = (VBox) activityListNode;
            activityList.getChildren().clear();
            
            if (currentUser != null) {
                // Debug: Check what activities exist for this user
                dataStore.debugUserActivities(currentUser.getId());
                
                List<Activity> activities = dataStore.getActivitiesForUser(currentUser.getId(), selectedDate);
                System.out.println("📊 Found " + activities.size() + " activities for user: " + currentUser.getId() + " on date: " + selectedDate);
                
                if (activities.isEmpty()) {
                    // Show recent activities from the last 3 days if no activities for selected date
                    if (selectedDate.equals(LocalDate.now())) {
                        showRecentActivities(activityList);
                    } else {
                        Label noActivityLabel = new Label("No activities for this date.");
                        noActivityLabel.getStyleClass().addAll("text-sm", "text-muted");
                        noActivityLabel.setStyle("-fx-text-fill: #6b7280; -fx-font-weight: normal; -fx-font-size: 12px;");
                        noActivityLabel.setAlignment(Pos.CENTER);
                        activityList.getChildren().add(noActivityLabel);
                        System.out.println("📝 Added 'No activities' message");
                    }
                } else {
                    // Sort activities by timestamp (most recent first)
                    activities.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));
                    
                    for (Activity activity : activities) {
                        activityList.getChildren().add(createHomeActivityItem(activity));
                        System.out.println("➕ Added activity: " + activity.getDescription());
                    }
                }
            } else {
                System.out.println("⚠️ No current user found");
            }
        } else {
            System.out.println("⚠️ Could not find home-activity-list node");
        }
    }
    
    /**
     * Shows recent activities from the last few days when no activities for today
     */
    private void showRecentActivities(VBox activityList) {
        try {
            // Get activities from the last 3 days
            LocalDate startDate = LocalDate.now().minusDays(3);
            List<Activity> recentActivities = new ArrayList<>();
            
            for (int i = 0; i < 3; i++) {
                LocalDate date = startDate.plusDays(i);
                List<Activity> dayActivities = dataStore.getActivitiesForUser(currentUser.getId(), date);
                recentActivities.addAll(dayActivities);
            }
            
            // Sort by timestamp (most recent first)
            recentActivities.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));
            
            if (recentActivities.isEmpty()) {
                Label noActivityLabel = new Label("No recent activities found.");
                noActivityLabel.getStyleClass().addAll("text-sm", "text-muted");
                noActivityLabel.setStyle("-fx-text-fill: #6b7280; -fx-font-weight: normal; -fx-font-size: 12px;");
                noActivityLabel.setAlignment(Pos.CENTER);
                activityList.getChildren().add(noActivityLabel);
            } else {
                // Limit to 15 most recent activities (increased for better space utilization)
                int limit = Math.min(15, recentActivities.size());
                for (int i = 0; i < limit; i++) {
                    Activity activity = recentActivities.get(i);
                    activityList.getChildren().add(createHomeActivityItem(activity));
                    System.out.println("➕ Added recent activity: " + activity.getDescription());
                }
                
                // Add a header to indicate these are recent activities
                if (recentActivities.size() > 15) {
                    Label moreLabel = new Label("... and " + (recentActivities.size() - 15) + " more recent activities");
                    moreLabel.getStyleClass().addAll("text-xs", "text-muted");
                    moreLabel.setStyle("-fx-text-fill: #9ca3af; -fx-font-style: italic; -fx-font-size: 9px;");
                    moreLabel.setAlignment(Pos.CENTER);
                    moreLabel.setPadding(new Insets(2, 0, 2, 0));
                    activityList.getChildren().add(moreLabel);
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Error loading recent activities: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Creates a compact activity item optimized for space utilization
     */
    private HBox createHomeActivityItem(Activity activity) {
        HBox item = new HBox();
        item.setSpacing(8); // Reduced spacing
        item.setAlignment(Pos.CENTER_LEFT);
        item.getStyleClass().add("home-activity-item");
        
        // Compact padding and background styling
        item.setPadding(new Insets(6, 8, 6, 8)); // Reduced padding
        item.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 6; -fx-border-color: #e2e8f0; -fx-border-radius: 6; -fx-border-width: 1;");
        item.setPrefHeight(50); // Fixed height for each activity item
        item.setMaxHeight(50);
        item.setPrefWidth(292); // Match container width minus padding
        item.setMaxWidth(292); // Prevent width expansion
        
        // Compact icon with smaller styling
        Node icon = getActivityIcon(activity.getType());
        if (icon != null) {
            VBox iconContainer = new VBox();
            iconContainer.setAlignment(Pos.CENTER);
            iconContainer.setStyle("-fx-background-color: #3b82f6; -fx-background-radius: 12; -fx-padding: 4;"); // Smaller padding
            iconContainer.setPrefSize(24, 24); // Fixed icon size
            iconContainer.getChildren().add(icon);
            item.getChildren().add(iconContainer);
        }
        
        // Compact text container
        VBox textContainer = new VBox();
        textContainer.setSpacing(2); // Reduced spacing
        textContainer.setMaxWidth(Double.MAX_VALUE);
        
        // Compact description with truncated text
        String enhancedDescription = getEnhancedActivityDescription(activity);
        // Truncate long descriptions to fit better
        if (enhancedDescription.length() > 60) {
            enhancedDescription = enhancedDescription.substring(0, 57) + "...";
        }
        Label descriptionLabel = new Label(enhancedDescription);
        descriptionLabel.getStyleClass().addAll("text-sm", "font-medium");
        descriptionLabel.setStyle("-fx-text-fill: #1f2937; -fx-font-weight: 600; -fx-font-size: 12px;");
        descriptionLabel.setWrapText(false); // No wrapping for compact display
        descriptionLabel.setMaxWidth(Double.MAX_VALUE);
        
        // Compact bottom row with time and type
        HBox bottomRow = new HBox();
        bottomRow.setSpacing(6);
        bottomRow.setAlignment(Pos.CENTER_LEFT);
        
        // Compact time label
        String timeContext = formatTimestamp(activity.getTimestamp());
        Label timeLabel = new Label(timeContext);
        timeLabel.getStyleClass().addAll("text-xs", "text-muted");
        timeLabel.setStyle("-fx-text-fill: #6b7280; -fx-font-weight: 400; -fx-font-size: 10px;");
        
        // Compact type badge
        Label typeBadge = new Label(getActivityTypeDisplayName(activity.getType()));
        typeBadge.setStyle("-fx-background-color: #dbeafe; -fx-text-fill: #1e40af; -fx-font-size: 9px; -fx-font-weight: 500; -fx-padding: 1 4 1 4; -fx-background-radius: 8;");
        
        bottomRow.getChildren().addAll(timeLabel, typeBadge);
        textContainer.getChildren().addAll(descriptionLabel, bottomRow);
        item.getChildren().add(textContainer);
        
        return item;
    }
    
    /**
     * Gets a display name for the activity type
     */
    private String getActivityTypeDisplayName(ActivityType type) {
        switch (type) {
            case FLASHCARD_DECK_CREATED:
            case FLASHCARD_CREATED:
            case FLASHCARDS_REVIEWED:
                return "Flashcard";
            case NOTES_ADDED:
            case NOTE_CREATED:
            case NOTE_EDITED:
                return "Note";
            case QUIZ_COMPLETED:
            case QUIZ_CREATED:
            case QUIZ_TAKEN:
                return "Quiz";
            case TODO_ITEM_ADDED:
            case TODO_ITEM_COMPLETED:
                return "Todo";
            case CODE_PROBLEM_SOLVED:
            case CODE_PROBLEM_ATTEMPTED:
                return "Code";
            case STUDY_SESSION_STARTED:
            case STUDY_SESSION_ENDED:
                return "Study";
            case GAME_PLAYED:
                return "Game";
            case DOCUMENT_IMPORTED:
                return "Import";
            case UNKNOWN:
                return "Activity";
            default:
                return "Activity";
        }
    }
    
    /**
     * Helper method to find a node by ID
     */
    private javafx.scene.Node findNodeById(String id) {
        return findNodeByIdRecursive(mainContainer, id);
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
     * Public method to refresh activity history from other views
     */
    public void refreshActivityHistory() {
        System.out.println("🔄 Refreshing activity history and progress...");
        refreshActivityHistoryInternal();
        refreshSidebarProgress();
        System.out.println("✅ Activity history and progress refreshed successfully");
    }
    
    /**
     * Refreshes the sidebar progress metrics
     */
    public void refreshSidebarProgress() {
        try {
            System.out.println("📊 Refreshing sidebar progress metrics...");
            
            // Find and update the progress section
            javafx.scene.Node progressSectionNode = findNodeById("progress-section");
            if (progressSectionNode instanceof VBox) {
                VBox progressSection = (VBox) progressSectionNode;
                progressSection.getChildren().clear();
                
                // Recreate the progress metrics with updated data
                String streak = calculateStreak();
                String tasksDone = calculateTasksDone();
                
                System.out.println("📈 Progress metrics - Streak: " + streak + ", Tasks Done: " + tasksDone);
                
                HBox streakMetric = createProgressMetric("Streak", streak, "streak");
                HBox tasksMetric = createProgressMetric("Tasks Done", tasksDone, "tasks-done");
                
                VBox metricsContainer = new VBox();
                metricsContainer.setSpacing(8);
                metricsContainer.getChildren().addAll(streakMetric, tasksMetric);
                
                Label progressTitle = new Label("Today's Progress");
                progressTitle.getStyleClass().add("sidebar-section-title");
                
                progressSection.getChildren().addAll(progressTitle, metricsContainer);
                System.out.println("✅ Sidebar progress metrics refreshed successfully");
            } else {
                System.out.println("⚠️ Could not find progress-section node");
            }
        } catch (Exception e) {
            System.out.println("❌ Error refreshing sidebar progress: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Internal method to refresh the activity history in the home dashboard
     */
    private void refreshActivityHistoryInternal() {
        try {
            System.out.println("📅 Updating activity list for date: " + selectedDate);
            
            // Update the activity list (this also updates the date label)
            updateActivityList();
            
            // Update calendar to show activity indicators
            updateCalendar();
            
            System.out.println("✅ Activity history refreshed for date: " + selectedDate);
        } catch (Exception e) {
            System.out.println("❌ Error refreshing activity history: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Creates an activity item
     */
    private HBox createActivityItem(String icon, String text, String time) {
        HBox item = new HBox();
        item.setSpacing(12);
        item.setAlignment(Pos.CENTER_LEFT);
        
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 16px;");
        
        VBox textContainer = new VBox();
        textContainer.setSpacing(2);
        
        Label textLabel = new Label(text);
        textLabel.getStyleClass().addAll("text-sm", "font-medium");
        
        Label timeLabel = new Label(time);
        timeLabel.getStyleClass().addAll("text-xs", "text-muted");
        
        textContainer.getChildren().addAll(textLabel, timeLabel);
        
        HBox.setHgrow(textContainer, Priority.ALWAYS);
        
        item.getChildren().addAll(iconLabel, textContainer);
        
        return item;
    }
    
    /**
     * Creates an activity item with SVG icon
     */
    private HBox createActivityItemWithIcon(String iconName, String text, String time) {
        HBox item = new HBox();
        item.setSpacing(12);
        item.setAlignment(Pos.CENTER_LEFT);
        
        Label iconLabel = new Label();
        iconLabel.setGraphic(IconUtils.createIconView(iconName));
        
        VBox textContainer = new VBox();
        textContainer.setSpacing(2);
        
        Label textLabel = new Label(text);
        textLabel.getStyleClass().addAll("text-sm", "font-medium");
        
        Label timeLabel = new Label(time);
        timeLabel.getStyleClass().addAll("text-xs", "text-muted");
        
        textContainer.getChildren().addAll(textLabel, timeLabel);
        
        HBox.setHgrow(textContainer, Priority.ALWAYS);
        
        item.getChildren().addAll(iconLabel, textContainer);
        
        return item;
    }
    
    /**
     * Creates the quick access card with colorful buttons
     */
    private VBox createQuickAccessCard() {
        VBox card = new VBox();
        card.getStyleClass().add("card");
        card.setSpacing(16);
        
        Label title = new Label("Quick Access");
        title.getStyleClass().addAll("text-xl", "font-semibold", "text-primary");
        
        // Create horizontal layout for quick access buttons
        HBox buttonContainer = new HBox();
        buttonContainer.setSpacing(16);
        buttonContainer.setAlignment(Pos.CENTER);
        
        // Notes button (blue)
        Button notesButton = createQuickAccessButton("note", "Notes", "#3b82f6");
        notesButton.setOnAction(e -> handleNavigation("notes", notesNav));
        buttonContainer.getChildren().add(notesButton);
        
        // Flashcards button (green)
        Button flashcardsButton = createQuickAccessButton("cards", "Flashcards", "#10b981");
        flashcardsButton.setOnAction(e -> handleNavigation("flashcards", flashcardsNav));
        buttonContainer.getChildren().add(flashcardsButton);
        
        // Quizzes button (yellow)
        Button quizzesButton = createQuickAccessButton("question", "Quizzes", "#f59e0b");
        quizzesButton.setOnAction(e -> handleNavigation("quizzes", quizzesNav));
        buttonContainer.getChildren().add(quizzesButton);
        
        
        // Todo List button (pink)
        Button todoButton = createQuickAccessButton("check", "To-Do List", "#ec4899");
        todoButton.setOnAction(e -> handleNavigation("todo", todoNav));
        buttonContainer.getChildren().add(todoButton);
        
        // Take a Break button (orange)
        Button timerButton = createQuickAccessButton("clock", "Take a Break", "#f97316");
        timerButton.setOnAction(e -> handleNavigation("timer", timerNav));
        buttonContainer.getChildren().add(timerButton);
        
        // Progress button (purple)
        Button progressButton = createQuickAccessButton("trending-up", "Progress", "#8b5cf6");
        progressButton.setOnAction(e -> handleNavigation("progress", progressNav));
        buttonContainer.getChildren().add(progressButton);
        
        card.getChildren().addAll(title, buttonContainer);
        
        return card;
    }
    
    /**
     * Creates a quick access button with specified icon, text, and color
     */
    private Button createQuickAccessButton(String iconName, String text, String color) {
        Button button = new Button();
        button.getStyleClass().add("quick-access-button");
        button.setPrefSize(120, 100);
        button.setMaxSize(120, 100);
        
        // Create icon
        Node icon = IconUtils.createIconView(iconName);
        if (icon != null) {
            icon.setStyle("-fx-text-fill: white; -fx-font-size: 24px;");
        }
        
        // Create text label
        Label textLabel = new Label(text);
        textLabel.getStyleClass().add("quick-access-text");
        textLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px;");
        
        // Create vertical layout
        VBox content = new VBox();
        content.setAlignment(Pos.CENTER);
        content.setSpacing(8);
        content.getChildren().addAll(icon, textLabel);
        
        button.setGraphic(content);
        button.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 12; -fx-border-radius: 12;");
        
        return button;
    }
    
    /**
     * Creates the todo list card
     */
    private VBox createTodoListCard() {
        VBox card = new VBox();
        card.getStyleClass().add("card");
        card.setSpacing(16);
        
        Label title = new Label("Your Tasks");
        title.getStyleClass().addAll("text-xl", "font-semibold", "text-primary");
        
        // Get todo items from DataStore
        List<com.studyspace.models.TodoItem> todoItems = new ArrayList<>(dataStore.getTodoItems());
        
        VBox todoList = new VBox();
        todoList.setSpacing(8);
        
        if (todoItems.isEmpty()) {
            Label noTasksLabel = new Label("No tasks yet. Add some tasks to get started!");
            noTasksLabel.getStyleClass().addAll("text-sm", "text-muted");
            noTasksLabel.setStyle("-fx-text-fill: #6b7280; -fx-font-style: italic;");
            todoList.getChildren().add(noTasksLabel);
        } else {
            // Show first 5 tasks
            int maxTasks = Math.min(5, todoItems.size());
            for (int i = 0; i < maxTasks; i++) {
                com.studyspace.models.TodoItem item = todoItems.get(i);
                VBox taskItem = createTodoItem(item);
                todoList.getChildren().add(taskItem);
            }
            
            if (todoItems.size() > 5) {
                Label moreTasksLabel = new Label("... and " + (todoItems.size() - 5) + " more tasks");
                moreTasksLabel.getStyleClass().addAll("text-sm", "text-muted");
                moreTasksLabel.setStyle("-fx-text-fill: #6b7280; -fx-font-style: italic;");
                todoList.getChildren().add(moreTasksLabel);
            }
        }
        
        // View all tasks button
        Button viewAllButton = new Button("View All Tasks");
        viewAllButton.getStyleClass().add("secondary-button");
        viewAllButton.setMaxWidth(Double.MAX_VALUE);
        viewAllButton.setOnAction(e -> handleNavigation("todo", todoNav));
        
        card.getChildren().addAll(title, todoList, viewAllButton);
        
        return card;
    }
    
    /**
     * Creates a todo item display
     */
    private VBox createTodoItem(com.studyspace.models.TodoItem item) {
        // Create container for the todo item
        VBox itemContainer = new VBox();
        itemContainer.getStyleClass().add("todo-item-container");
        itemContainer.setSpacing(8);
        itemContainer.setPadding(new Insets(12));
        
        HBox itemBox = new HBox();
        itemBox.setSpacing(12);
        itemBox.setAlignment(Pos.CENTER_LEFT);
        itemBox.getStyleClass().add("todo-item-preview");
        
        // Checkbox
        CheckBox checkbox = new CheckBox();
        checkbox.setSelected(item.isCompleted());
        checkbox.setDisable(true); // Read-only in preview
        checkbox.setStyle("-fx-text-fill: white;");
        
        // Task text
        Label taskLabel = new Label(item.getTitle());
        taskLabel.getStyleClass().add("todo-item-text");
        if (item.isCompleted()) {
            taskLabel.setStyle("-fx-text-fill: #9ca3af; -fx-text-decoration: line-through;");
        } else {
            taskLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        }
        
        // Priority indicator
        Label priorityLabel = new Label();
        priorityLabel.getStyleClass().add("todo-priority");
        switch (item.getPriority()) {
            case HIGH:
                priorityLabel.setText("!");
                priorityLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
                break;
            case MEDIUM:
                priorityLabel.setText("•");
                priorityLabel.setStyle("-fx-text-fill: #f59e0b; -fx-font-weight: bold;");
                break;
            case LOW:
                priorityLabel.setText("•");
                priorityLabel.setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold;");
                break;
        }
        
        itemBox.getChildren().addAll(checkbox, taskLabel, priorityLabel);
        
        // Add due date if available
        if (item.getDueDate() != null) {
            Label dueDateLabel = new Label("Due: " + item.getDueDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
            dueDateLabel.getStyleClass().add("todo-due-date");
            dueDateLabel.setStyle("-fx-text-fill: #9ca3af; -fx-font-size: 11px;");
            itemContainer.getChildren().addAll(itemBox, dueDateLabel);
        } else {
            itemContainer.getChildren().add(itemBox);
        }
        
        return itemContainer;
    }
    
    /**
     * Creates the progress dashboard
     */
    private VBox createProgressDashboard() {
        try {
            VBox dashboard = new VBox();
            dashboard.setSpacing(32);
            dashboard.getStyleClass().add("progress-dashboard");
            dashboard.setPadding(new Insets(24));
            
            // Header section
            VBox headerSection = new VBox();
            headerSection.setSpacing(8);
            
            Label title = new Label("Progress Dashboard");
            title.getStyleClass().addAll("progress-dashboard-title");
            
            Label subtitle = new Label("Track your learning journey and achievements");
            subtitle.getStyleClass().addAll("progress-dashboard-subtitle");
            
            headerSection.getChildren().addAll(title, subtitle);
            
            // Statistics cards section
            VBox statsSection = new VBox();
            statsSection.setSpacing(24);
            
            // Top row - Main statistics
            HBox topStatsRow = new HBox();
            topStatsRow.setSpacing(20);
            topStatsRow.setAlignment(Pos.CENTER_LEFT);
            
            VBox notesCard = createStatCard("📝", "Notes Created", String.valueOf(getNotesCount()), "#3b82f6");
            VBox cardsCard = createStatCard("📚", "Cards Reviewed", String.valueOf(getCardsReviewedCount()), "#10b981");
            VBox quizzesCard = createStatCard("❓", "Quizzes Taken", String.valueOf(getQuizzesTakenCount()), "#f59e0b");
            
            HBox.setHgrow(notesCard, Priority.ALWAYS);
            HBox.setHgrow(cardsCard, Priority.ALWAYS);
            HBox.setHgrow(quizzesCard, Priority.ALWAYS);
            
            topStatsRow.getChildren().addAll(notesCard, cardsCard, quizzesCard);
            
            // Bottom row - Additional statistics
            HBox bottomStatsRow = new HBox();
            bottomStatsRow.setSpacing(20);
            bottomStatsRow.setAlignment(Pos.CENTER_LEFT);
            
            VBox averageScoreCard = createStatCard("📊", "Average Score", getAverageScoreFormatted(), "#ef4444");
            VBox streakCard = createStatCard("🔥", "Current Streak", getCurrentStreakFormatted(), "#f97316");
            VBox tasksCard = createStatCard("✅", "Tasks Completed", String.valueOf(getTasksCompletedCount()), "#06b6d4");
            VBox decksCard = createStatCard("📖", "Decks Created", String.valueOf(getDecksCreatedCount()), "#ec4899");
            
            HBox.setHgrow(averageScoreCard, Priority.ALWAYS);
            HBox.setHgrow(streakCard, Priority.ALWAYS);
            HBox.setHgrow(tasksCard, Priority.ALWAYS);
            HBox.setHgrow(decksCard, Priority.ALWAYS);
            
            bottomStatsRow.getChildren().addAll(averageScoreCard, streakCard, tasksCard, decksCard);
            
            statsSection.getChildren().addAll(topStatsRow, bottomStatsRow);
            
            // Progress overview section
            VBox progressOverviewCard = createProgressOverviewCard();
            
            // Create main content container
            VBox mainContent = new VBox();
            mainContent.setSpacing(32);
            mainContent.getChildren().addAll(headerSection, statsSection, progressOverviewCard);
            
            // Wrap entire dashboard in ScrollPane
            ScrollPane scrollPane = new ScrollPane(mainContent);
            scrollPane.setFitToWidth(true);
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            scrollPane.setStyle(
                "-fx-background-color: transparent; " +
                "-fx-border-color: transparent; " +
                "-fx-padding: 0;"
            );
            
            // Create container for scroll pane
            VBox scrollContainer = new VBox();
            scrollContainer.getChildren().add(scrollPane);
            VBox.setVgrow(scrollPane, Priority.ALWAYS);
            
            return scrollContainer;
            
        } catch (Exception e) {
            System.err.println("Error creating progress dashboard: " + e.getMessage());
            e.printStackTrace();
            
            // Return a simple error view
            VBox errorView = new VBox();
            errorView.setAlignment(Pos.CENTER);
            errorView.setSpacing(16);
            errorView.setPadding(new Insets(24));
            
            Label errorLabel = new Label("Unable to load progress dashboard");
            errorLabel.getStyleClass().addAll("text-xl", "font-semibold", "text-primary");
            
            Label detailsLabel = new Label("Please try refreshing the page or contact support if the issue persists.");
            detailsLabel.getStyleClass().addAll("text-base", "text-secondary");
            
            errorView.getChildren().addAll(errorLabel, detailsLabel);
            return errorView;
        }
    }
    
    /**
     * Creates a statistics card
     */
    private VBox createStatCard(String icon, String title, String value, String color) {
        VBox card = new VBox();
        card.getStyleClass().add("progress-stat-card");
        card.setSpacing(12);
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.CENTER);
        
        // Icon
        Label iconLabel = new Label(icon);
        iconLabel.getStyleClass().add("progress-stat-icon");
        iconLabel.setStyle("-fx-font-size: 32px;");
        
        // Value
        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("progress-stat-value");
        valueLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        
        // Title
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("progress-stat-title");
        titleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #6b7280; -fx-font-weight: 500;");
        
        card.getChildren().addAll(iconLabel, valueLabel, titleLabel);
        
        return card;
    }
    
    /**
     * Gets the number of notes created
     */
    private int getNotesCount() {
        try {
            if (dataStore != null) {
                return dataStore.getNotes().size();
            }
            return 0;
        } catch (Exception e) {
            System.err.println("Error getting notes count: " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * Gets the number of cards reviewed
     */
    private int getCardsReviewedCount() {
        try {
            if (currentUser != null) {
                return currentUser.getFlashcardsStudied();
            }
            return 0;
        } catch (Exception e) {
            System.err.println("Error getting cards reviewed count: " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * Gets the number of quizzes taken
     */
    private int getQuizzesTakenCount() {
        try {
            if (currentUser != null) {
                return currentUser.getQuizzesTaken();
            }
            return 0;
        } catch (Exception e) {
            System.err.println("Error getting quizzes taken count: " + e.getMessage());
            return 0;
        }
    }
    
    
    /**
     * Gets the average score formatted
     */
    private String getAverageScoreFormatted() {
        double average = getAverageScorePercentage();
        return String.format("%.1f%%", average);
    }
    
    /**
     * Gets the average score percentage from actual quiz scores
     */
    private double getAverageScorePercentage() {
        try {
            if (dataStore == null) {
                return 0.0;
            }
            
            // Get all quizzes from the database
            List<com.studyspace.models.Quiz> quizzes = dataStore.getAllQuizzes();
            
            if (quizzes.isEmpty()) {
                return 0.0;
            }
            
            // Calculate average from quizzes that have been taken (bestScore > 0)
            List<com.studyspace.models.Quiz> takenQuizzes = quizzes.stream()
                .filter(quiz -> quiz.getBestScore() > 0)
                .collect(java.util.stream.Collectors.toList());
            
            if (takenQuizzes.isEmpty()) {
                return 0.0;
            }
            
            // Calculate average score
            double totalScore = takenQuizzes.stream()
                .mapToInt(com.studyspace.models.Quiz::getBestScore)
                .sum();
            
            double average = totalScore / takenQuizzes.size();
            
            System.out.println("Calculated average score: " + average + "% from " + takenQuizzes.size() + " quizzes");
            
            return average;
            
        } catch (Exception e) {
            System.err.println("Error calculating average score: " + e.getMessage());
            return 0.0;
        }
    }
    
    /**
     * Gets the current streak formatted
     */
    private String getCurrentStreakFormatted() {
        if (currentUser != null) {
            int streak = currentUser.getCurrentStreak();
            return streak + " days";
        }
        return "0 days";
    }
    
    /**
     * Gets the current streak days as integer
     */
    private int getCurrentStreakDays() {
        if (currentUser != null) {
            return currentUser.getCurrentStreak();
        }
        return 0;
    }
    
    /**
     * Gets the number of tasks completed
     */
    private int getTasksCompletedCount() {
        try {
            if (dataStore != null) {
                return (int) dataStore.getTodoItems().stream()
                    .filter(com.studyspace.models.TodoItem::isCompleted)
                    .count();
            }
            return 0;
        } catch (Exception e) {
            System.err.println("Error getting tasks completed count: " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * Gets the number of decks created
     */
    private int getDecksCreatedCount() {
        try {
            if (dataStore != null) {
                return dataStore.getAllFlashcardDecks().size();
            }
            return 0;
        } catch (Exception e) {
            System.err.println("Error getting decks created count: " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * Creates the progress overview card
     */
    private VBox createProgressOverviewCard() {
        try {
            VBox card = new VBox();
            card.getStyleClass().add("card");
            card.setSpacing(20);
            card.setPadding(new Insets(24));
            card.setStyle("-fx-background-color: #1f2937; -fx-background-radius: 12;");
            
            Label title = new Label("Study Progress Overview");
            title.getStyleClass().addAll("text-xl", "font-semibold");
            title.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: 600;");
            
            VBox progressContainer = new VBox();
            progressContainer.setSpacing(20);
            
            // Flashcard Deck Created Progress
            VBox deckProgress = createWeeklyGoalProgress("Flashcard Deck Created", 
                getDecksCreatedCount(), 10, "");
            
            // Notes Created Progress  
            VBox notesProgress = createWeeklyGoalProgress("Notes Created",
                getNotesCount(), 15, "");
            
            // Quizzes Taken Progress
            VBox quizzesProgress = createWeeklyGoalProgress("Quizzes Taken",
                getQuizzesTakenCount(), 5, "");
            
            // Daily Streak Progress
            VBox streakProgress = createWeeklyGoalProgress("Daily Streak",
                getCurrentStreakDays(), 7, "");
            
            // Average Score Progress
            VBox averageScoreProgress = createWeeklyGoalProgress("Average Score",
                (int) getAverageScorePercentage(), 100, "%");
            
            progressContainer.getChildren().addAll(deckProgress, notesProgress, quizzesProgress, streakProgress, averageScoreProgress);
            
            card.getChildren().addAll(title, progressContainer);
            
            return card;
            
        } catch (Exception e) {
            System.err.println("Error creating progress overview card: " + e.getMessage());
            e.printStackTrace();
            
            // Return a simple card with error message
            VBox errorCard = new VBox();
            errorCard.getStyleClass().add("card");
            errorCard.setSpacing(16);
            errorCard.setAlignment(Pos.CENTER);
            
            Label title = new Label("Study Progress Overview");
            title.getStyleClass().addAll("text-xl", "font-semibold", "text-primary");
            
            Label errorLabel = new Label("Unable to load progress data");
            errorLabel.getStyleClass().addAll("text-base", "text-secondary");
            
            errorCard.getChildren().addAll(title, errorLabel);
            return errorCard;
        }
    }
    
    /**
     * Creates a weekly goal progress item
     */
    private VBox createWeeklyGoalProgress(String title, int current, int target, String unit) {
        VBox item = new VBox();
        item.setSpacing(8);
        
        // Title
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().addAll("text-sm", "font-medium");
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: 500; -fx-text-fill: white;");
        
        // Progress value (e.g., "8 / 15")
        String progressText = current + " / " + target + unit;
        Label progressLabel = new Label(progressText);
        progressLabel.getStyleClass().addAll("text-sm", "font-semibold");
        progressLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: white;");
        
        // Progress bar
        double progress = Math.min((double) current / target, 1.0);
        ProgressBar progressBar = new ProgressBar(progress);
        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressBar.setPrefHeight(8);
        progressBar.getStyleClass().add("weekly-goal-progress-bar");
        progressBar.setStyle(
            "-fx-background-color: #374151; " +
            "-fx-background-radius: 4; " +
            "-fx-border-radius: 4; " +
            "-fx-accent: #8b5cf6;"
        );
        
        item.getChildren().addAll(titleLabel, progressLabel, progressBar);
        
        return item;
    }
    
    /**
     * Creates a progress item
     */
    private VBox createProgressItem(String subject, double progress, String percentage) {
        VBox item = new VBox();
        item.setSpacing(8);
        
        HBox header = new HBox();
        header.setSpacing(8);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label subjectLabel = new Label(subject);
        subjectLabel.getStyleClass().addAll("text-sm", "font-medium");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label percentageLabel = new Label(percentage);
        percentageLabel.getStyleClass().addAll("text-xs", "text-muted");
        
        header.getChildren().addAll(subjectLabel, spacer, percentageLabel);
        
        ProgressBar progressBar = new ProgressBar(progress);
        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressBar.getStyleClass().add("progress-bar");
        
        item.getChildren().addAll(header, progressBar);
        
        return item;
    }
    
    /**
     * Calculates the average score for a specific subject
     */
    private double calculateSubjectAverage(String subject) {
        try {
            if (dataStore == null) {
                System.err.println("DataStore is null, cannot calculate subject average");
                return 0.0;
            }
            
            List<Quiz> quizzes = dataStore.getAllQuizzes();
            if (quizzes == null || quizzes.isEmpty()) {
                return 0.0;
            }
            
            List<Quiz> subjectQuizzes = quizzes.stream()
                .filter(quiz -> quiz != null && quiz.getSubject() != null)
                .filter(quiz -> quiz.getSubject().toLowerCase().contains(subject.toLowerCase()))
                .filter(quiz -> quiz.getBestScore() > 0) // Only quizzes that have been taken
                .collect(java.util.stream.Collectors.toList());
            
            if (subjectQuizzes.isEmpty()) {
                return 0.0;
            }
            
            double totalScore = subjectQuizzes.stream()
                .mapToInt(Quiz::getBestScore)
                .sum();
            
            return totalScore / subjectQuizzes.size();
        } catch (Exception e) {
            System.err.println("Error calculating subject average for " + subject + ": " + e.getMessage());
            e.printStackTrace();
            return 0.0;
        }
    }
    
    /**
     * Gets a time-based greeting
     */
    private String getTimeBasedGreeting() {
        int hour = java.time.LocalDateTime.now().getHour();
        if (hour < 12) {
            return "Good morning";
        } else if (hour < 17) {
            return "Good afternoon";
        } else {
            return "Good evening";
        }
    }
    
    /**
     * Creates a placeholder view for sections
     */
    private VBox createPlaceholderView(String title, String subtitle, String description) {
        VBox view = new VBox();
        view.setAlignment(Pos.CENTER);
        view.setSpacing(16);
        view.getStyleClass().add("content-area");
        
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().addAll("text-3xl", "font-bold", "text-primary");
        
        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.getStyleClass().addAll("text-lg", "text-secondary");
        
        Label descriptionLabel = new Label(description);
        descriptionLabel.getStyleClass().addAll("text-base", "text-muted");
        descriptionLabel.setWrapText(true);
        descriptionLabel.setMaxWidth(600);
        descriptionLabel.setStyle("-fx-text-alignment: center;");
        
        Button comingSoonButton = new Button("Coming Soon");
        comingSoonButton.getStyleClass().add("primary-button");
        comingSoonButton.setOnAction(e -> 
            sceneManager.showInfoDialog("Coming Soon", 
                "This feature is under development and will be available soon!"));
        
        view.getChildren().addAll(titleLabel, subtitleLabel, descriptionLabel, comingSoonButton);
        
        return view;
    }
    
    /**
     * Creates a placeholder view with SVG icon
     */
    private VBox createPlaceholderViewWithIcon(String iconName, String title, String subtitle, String description) {
        VBox view = new VBox();
        view.setAlignment(Pos.CENTER);
        view.setSpacing(16);
        view.getStyleClass().add("content-area");
        
        Label titleLabel = new Label();
        titleLabel.setGraphic(IconUtils.createLargeIconView(iconName));
        titleLabel.setText(" " + title);
        titleLabel.getStyleClass().addAll("text-3xl", "font-bold", "text-primary");
        
        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.getStyleClass().addAll("text-lg", "text-secondary");
        
        Label descriptionLabel = new Label(description);
        descriptionLabel.getStyleClass().addAll("text-base", "text-muted");
        descriptionLabel.setWrapText(true);
        descriptionLabel.setMaxWidth(600);
        descriptionLabel.setStyle("-fx-text-alignment: center;");
        
        Button comingSoonButton = new Button("Coming Soon");
        comingSoonButton.getStyleClass().add("primary-button");
        comingSoonButton.setOnAction(e -> 
            sceneManager.showInfoDialog("Coming Soon", 
                "This feature is under development and will be available soon!"));
        
        view.getChildren().addAll(titleLabel, subtitleLabel, descriptionLabel, comingSoonButton);
        
        return view;
    }
    
    
    /**
     * Gets the main view container
     */
    public BorderPane getView() {
        return mainContainer;
    }
    
    /**
     * Gets the content area for external manipulation
     */
    public StackPane getContentArea() {
        return contentArea;
    }
    
    /**
     * Utility method to safely switch content in StackPane
     */
    private void switchContentInStackPane(Node newContent) {
        try {
            if (contentArea != null && newContent != null) {
                contentArea.getChildren().clear();
                contentArea.getChildren().add(newContent);
                StackPane.setAlignment(newContent, Pos.TOP_LEFT);
            }
        } catch (Exception e) {
            System.err.println("Error switching content in StackPane: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Utility method to add content to StackPane with proper event handling
     */
    private void addContentToStackPane(Node content) {
        try {
            if (contentArea != null && content != null) {
                // Set up proper event handling for the content
                content.setOnMouseClicked(e -> {
                    // Allow content to handle its own mouse events
                    // Don't consume here to allow child components to handle events
                });
                
                contentArea.getChildren().add(content);
                StackPane.setAlignment(content, Pos.TOP_LEFT);
            }
        } catch (Exception e) {
            System.err.println("Error adding content to StackPane: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Refreshes user data in the sidebar
     */
    public void refreshUserData() {
        this.currentUser = dataStore.getCurrentUser();
        loadUserData();
    }
    
    /**
     * Refreshes the notification status
     */
    public void refreshNotifications() {
        checkOverdueItems();
    }
    
    /**
     * Creates the Today's Progress section
     */
    private VBox createTodaysProgressSection() {
        VBox progressSection = new VBox();
        progressSection.getStyleClass().add("sidebar-section");
        progressSection.setSpacing(12);
        progressSection.setId("progress-section");
        
        // Section title
        Label progressTitle = new Label("Today's Progress");
        progressTitle.getStyleClass().add("sidebar-section-title");
        
        // Progress metrics
        VBox metricsContainer = new VBox();
        metricsContainer.setSpacing(8);
        
        // Calculate actual progress data
        String streak = calculateStreak();
        String tasksDone = calculateTasksDone();
        
        // Streak metric
        HBox streakMetric = createProgressMetric("Streak", streak, "streak");
        
        // Tasks Done metric
        HBox tasksMetric = createProgressMetric("Tasks Done", tasksDone, "tasks-done");
        
        metricsContainer.getChildren().addAll(streakMetric, tasksMetric);
        
        progressSection.getChildren().addAll(progressTitle, metricsContainer);
        
        return progressSection;
    }
    
    /**
     * Creates a progress metric row
     */
    private HBox createProgressMetric(String label, String value, String metricId) {
        HBox metric = new HBox();
        metric.setSpacing(8);
        metric.setAlignment(Pos.CENTER_LEFT);
        metric.getStyleClass().add("progress-metric");
        
        Label labelText = new Label(label);
        labelText.getStyleClass().add("progress-label");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label valueText = new Label(value);
        valueText.getStyleClass().add("progress-value");
        valueText.getStyleClass().add(metricId);
        
        metric.getChildren().addAll(labelText, spacer, valueText);
        
        return metric;
    }
    
    
    /**
     * Calculates current streak
     */
    private String calculateStreak() {
        if (currentUser != null) {
            int streak = currentUser.getCurrentStreak();
            return streak + " days";
        }
        return "0 days";
    }
    
    /**
     * Calculates completed tasks for today
     */
    private String calculateTasksDone() {
        // Count completed todo items for today
        LocalDate today = LocalDate.now();
        long completedCount = dataStore.getTodoItems().stream()
            .filter(item -> item.isCompleted() && 
                item.getCompletedAt() != null && 
                item.getCompletedAt().toLocalDate().equals(today))
            .count();
        return String.valueOf(completedCount);
    }
    
    /**
     * Returns an enhanced activity description with context
     */
    private String getEnhancedActivityDescription(Activity activity) {
        String baseDescription = activity.getDescription();
        
        // Add contextual information based on activity type
        switch (activity.getType()) {
            case QUIZ_COMPLETED:
                return "🎯 " + baseDescription;
            case FLASHCARDS_REVIEWED:
                return "📚 " + baseDescription;
            case NOTES_ADDED:
                return "📝 " + baseDescription;
            case CODE_PROBLEM_SOLVED:
                return "💻 " + baseDescription;
            case STUDY_SESSION_STARTED:
                return "🚀 " + baseDescription;
            case STUDY_SESSION_ENDED:
                return "✅ " + baseDescription;
            case TODO_ITEM_ADDED:
                return "➕ " + baseDescription;
            case TODO_ITEM_COMPLETED:
                return "🎉 " + baseDescription;
            case FLASHCARD_DECK_CREATED:
                return "📖 " + baseDescription;
            case QUIZ_TAKEN:
                return "📊 " + baseDescription;
            case NOTE_EDITED:
                return "✏️ " + baseDescription;
            case CODE_PROBLEM_ATTEMPTED:
                return "🔧 " + baseDescription;
            default:
                return "📋 " + baseDescription;
        }
    }
    
    /**
     * Returns an icon based on the activity type
     */
    private Node getActivityIcon(ActivityType type) {
        String iconName = "";
        switch (type) {
            case QUIZ_COMPLETED:
                iconName = "question";
                break;
            case FLASHCARDS_REVIEWED:
                iconName = "cards";
                break;
            case NOTES_ADDED:
                iconName = "note";
                break;
            case CODE_PROBLEM_SOLVED:
                iconName = "code";
                break;
            case STUDY_SESSION_STARTED:
            case STUDY_SESSION_ENDED:
                iconName = "clock";
                break;
            case TODO_ITEM_ADDED:
            case TODO_ITEM_COMPLETED:
                iconName = "check";
                break;
            default:
                iconName = "info";
                break;
        }
        return IconUtils.createIconView(iconName);
    }
    
    /**
     * Formats the activity timestamp into a human-readable string
     */
    private String formatTimestamp(LocalDateTime timestamp) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(timestamp, now);
        
        if (duration.toMinutes() < 60) {
            long minutes = duration.toMinutes();
            return minutes == 0 ? "Just now" : minutes + " minute" + (minutes == 1 ? " ago" : "s ago");
        } else if (duration.toHours() < 24) {
            long hours = duration.toHours();
            return hours + " hour" + (hours == 1 ? " ago" : "s ago");
        } else if (duration.toDays() < 2) {
            return "Yesterday";
        } else if (duration.toDays() < 7) {
            long days = duration.toDays();
            return days + " day" + (days == 1 ? " ago" : "s ago");
        } else {
            return timestamp.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
        }
    }
}
