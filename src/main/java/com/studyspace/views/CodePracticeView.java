package com.studyspace.views;

import com.studyspace.models.CodeProblem;
import com.studyspace.models.Activity;
import com.studyspace.models.ActivityType;
import com.studyspace.utils.DataStore;
import com.studyspace.utils.SceneManager;
import com.studyspace.utils.IconUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.Comparator;

//============ code practice view =============
//this is where programming problems are listed and practiced

public class CodePracticeView {
    
    private final DataStore dataStore;
    private final SceneManager sceneManager;
    
    private VBox mainContainer;
    private ScrollPane scrollPane;
    private FlowPane problemsGrid;
    private TextField searchField;
    private ComboBox<String> sortComboBox;
    private ObservableList<CodeProblem> problemsList;
    private FilteredList<CodeProblem> filteredProblems;
    private SortedList<CodeProblem> sortedProblems;
    
    public CodePracticeView() {
        this.dataStore = DataStore.getInstance();
        this.sceneManager = SceneManager.getInstance();
        
        initializeData();
        initializeUI();
        setupEventHandlers();
        loadCodeProblems();
    }
    
    /**
     * Initialize data and filtering
     */
    private void initializeData() {
        problemsList = FXCollections.observableArrayList(dataStore.getAllCodeProblems());
        filteredProblems = new FilteredList<>(problemsList);
        sortedProblems = new SortedList<>(filteredProblems);
    }
    
    /**
     * Initializes the UI components
     */
    private void initializeUI() {
        mainContainer = new VBox();
        mainContainer.setSpacing(24);
        mainContainer.getStyleClass().add("content-area");
        
        // Header section
        VBox headerSection = new VBox();
        headerSection.setSpacing(8);
        
        Label titleLabel = new Label("💻 Code Practice");
        titleLabel.getStyleClass().addAll("text-3xl", "font-bold", "text-primary");
        
        Label subtitleLabel = new Label("Improve your programming skills with coding challenges");
        subtitleLabel.getStyleClass().addAll("text-base", "text-secondary");
        
        headerSection.getChildren().addAll(titleLabel, subtitleLabel);
        
        // Action bar
        HBox actionBar = new HBox();
        actionBar.setSpacing(16);
        actionBar.setAlignment(Pos.CENTER_LEFT);
        
        Button createProblemButton = new Button("+ Create New Problem");
        createProblemButton.getStyleClass().add("primary-button");
        createProblemButton.setOnAction(e -> handleCreateNewProblem());
        
        Button refreshButton = new Button("🔄 Refresh");
        refreshButton.getStyleClass().add("secondary-button");
        refreshButton.setOnAction(e -> loadCodeProblems());
        
        // Search and sort section
        HBox searchSortSection = createSearchSortSection();
        
        actionBar.getChildren().addAll(createProblemButton, refreshButton, searchSortSection);
        
        // Problems grid
        problemsGrid = new FlowPane();
        problemsGrid.setHgap(20);
        problemsGrid.setVgap(20);
        problemsGrid.setAlignment(Pos.TOP_LEFT);
        
        // Scroll pane for the grid
        scrollPane = new ScrollPane(problemsGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        
        mainContainer.getChildren().addAll(headerSection, actionBar, scrollPane);
    }
    
    /**
     * Creates the search and sort section
     */
    private HBox createSearchSortSection() {
        HBox searchSortSection = new HBox();
        searchSortSection.setSpacing(16);
        searchSortSection.setAlignment(Pos.CENTER_LEFT);
        
        // Search field
        HBox searchBox = new HBox();
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setSpacing(8);
        searchBox.getStyleClass().add("search-box");
        
        Label searchIcon = new Label("🔍");
        searchIcon.getStyleClass().add("text-sm");
        
        searchField = new TextField();
        searchField.getStyleClass().add("search-field");
        searchField.setPromptText("Search problems...");
        searchField.setPrefWidth(250);
        
        searchBox.getChildren().addAll(searchIcon, searchField);
        
        // Sort dropdown
        HBox sortContainer = new HBox();
        sortContainer.setSpacing(8);
        sortContainer.setAlignment(Pos.CENTER_LEFT);
        
        Label sortLabel = new Label("Sort By:");
        sortLabel.getStyleClass().add("text-sm");
        
        sortComboBox = new ComboBox<>();
        sortComboBox.getStyleClass().add("filter-combobox");
        sortComboBox.getItems().addAll(
            "Title (A-Z)",
            "Title (Z-A)",
            "Difficulty (Easy to Hard)",
            "Difficulty (Hard to Easy)",
            "Language (A-Z)",
            "Language (Z-A)",
            "Date Created (Newest)",
            "Date Created (Oldest)",
            "Last Attempted (Recent)",
            "Last Attempted (Oldest)",
            "Status (Completed First)",
            "Status (Incomplete First)",
            "Attempts (Low to High)",
            "Attempts (High to Low)"
        );
        sortComboBox.setValue("Date Created (Newest)");
        sortComboBox.setPrefWidth(200);
        sortComboBox.setOnAction(e -> applySorting(sortComboBox.getValue()));
        
        sortContainer.getChildren().addAll(sortLabel, sortComboBox);
        
        searchSortSection.getChildren().addAll(searchBox, sortContainer);
        return searchSortSection;
    }
    
    /**
     * Sets up event handlers for search and sort
     */
    private void setupEventHandlers() {
        // Search functionality
        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredProblems.setPredicate(problem -> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }
                    String lowerCaseFilter = newValue.toLowerCase();
                    return problem.getTitle().toLowerCase().contains(lowerCaseFilter) ||
                           problem.getDescription().toLowerCase().contains(lowerCaseFilter) ||
                           problem.getLanguage().toLowerCase().contains(lowerCaseFilter);
                });
                
                // Reload problems to show filtered results
                loadCodeProblems();
            });
        }
        
        // Sort functionality
        if (sortComboBox != null) {
            sortComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    applySorting(newValue);
                }
            });
        }
    }
    
    /**
     * Loads all code problems from the data store
     */
    private void loadCodeProblems() {
        problemsGrid.getChildren().clear();
        
        if (sortedProblems.isEmpty()) {
            // Show empty state - replace the entire scroll pane content
            showEmptyState();
        } else {
            // Create problem cards
            for (CodeProblem problem : sortedProblems) {
                VBox problemCard = createProblemCard(problem);
                problemsGrid.getChildren().add(problemCard);
            }
            // Set the problems grid as the scroll pane content
            scrollPane.setContent(problemsGrid);
        }
    }
    
    /**
     * Creates a code problem card
     */
    private VBox createProblemCard(CodeProblem problem) {
        VBox card = new VBox();
        card.getStyleClass().addAll("grid-tile", "hover-lift");
        card.setSpacing(12);
        card.setPrefWidth(350);
        card.setMinWidth(320);
        card.setMaxWidth(380);
        card.setMinHeight(200);
        card.setMaxHeight(320); // Fixed maximum height
        card.setPrefHeight(300); // Fixed preferred height
        
        // Header with difficulty badge and language
        HBox header = new HBox();
        header.setSpacing(8);
        header.setAlignment(Pos.TOP_LEFT);
        
        Label difficultyBadge = new Label(problem.getDifficulty().getDisplayName());
        difficultyBadge.getStyleClass().addAll("difficulty-badge", 
            problem.getDifficulty().name().toLowerCase());
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label languageLabel = new Label("⚡ " + problem.getLanguage());
        languageLabel.getStyleClass().addAll("text-xs", "text-muted");
        
        header.getChildren().addAll(difficultyBadge, spacer, languageLabel);
        
        // Title and description
        Label titleLabel = new Label(problem.getTitle());
        titleLabel.getStyleClass().add("grid-tile-title");
        titleLabel.setWrapText(true);
        titleLabel.setMaxHeight(40); // Limit title height
        titleLabel.setMaxWidth(330); // Limit title width
        
        Label descriptionLabel = new Label(problem.getDescription());
        descriptionLabel.getStyleClass().add("grid-tile-description");
        descriptionLabel.setWrapText(true);
        descriptionLabel.setMaxHeight(60); // Keep existing limit
        descriptionLabel.setMaxWidth(330); // Limit description width
        
        // Problem metadata
        VBox metaInfo = new VBox();
        metaInfo.setSpacing(4);
        
        Label testCasesLabel = new Label("🧪 " + problem.getTestCases().size() + " test cases");
        testCasesLabel.getStyleClass().addAll("text-xs", "text-muted");
        
        Label statusLabel = new Label("📊 " + problem.getStatusText());
        statusLabel.getStyleClass().addAll("text-xs", 
            problem.isCompleted() ? "text-success" : "text-secondary");
        
        Label lastAttemptLabel = new Label("🕒 " + problem.getLastAttemptedText());
        lastAttemptLabel.getStyleClass().addAll("text-xs", "text-muted");
        
        metaInfo.getChildren().addAll(testCasesLabel, statusLabel, lastAttemptLabel);
        
        // Action buttons
        HBox actionButtons = new HBox();
        actionButtons.setSpacing(8);
        actionButtons.setAlignment(Pos.CENTER_LEFT);
        
        Button solveButton = new Button();
        solveButton.setGraphic(IconUtils.createIconView("code"));
        solveButton.getStyleClass().add("primary-button");
        solveButton.setOnAction(e -> {
            e.consume();
            handleSolveProblem(problem);
        });
        solveButton.setOnMouseClicked(e -> {
            e.consume();
            handleSolveProblem(problem);
        });
        
        Button viewButton = new Button();
        viewButton.setGraphic(IconUtils.createIconView("eye"));
        viewButton.getStyleClass().add("secondary-button");
        viewButton.setOnAction(e -> {
            e.consume();
            handleViewProblem(problem);
        });
        viewButton.setOnMouseClicked(e -> {
            e.consume();
            handleViewProblem(problem);
        });
        
        Button deleteButton = new Button();
        deleteButton.setGraphic(IconUtils.createIconView("trash"));
        deleteButton.getStyleClass().add("danger-button");
        deleteButton.setOnAction(e -> {
            e.consume();
            handleDeleteProblem(problem);
        });
        deleteButton.setOnMouseClicked(e -> {
            e.consume();
            handleDeleteProblem(problem);
        });
        
        actionButtons.getChildren().addAll(solveButton, viewButton, deleteButton);
        
        card.getChildren().addAll(header, titleLabel, descriptionLabel, metaInfo, actionButtons);
        
        // Add click handler for the entire card
        card.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                handleSolveProblem(problem);
            }
        });
        
        return card;
    }
    
    /**
     * Creates an empty state view
     */
    private VBox createEmptyState() {
        VBox emptyState = new VBox();
        emptyState.setAlignment(Pos.CENTER);
        emptyState.setSpacing(16);
        emptyState.setPrefWidth(400);
        emptyState.setPrefHeight(300);
        
        Label iconLabel = new Label("💻");
        iconLabel.setStyle("-fx-font-size: 48px;");
        
        Label titleLabel = new Label("No Coding Problems Yet");
        titleLabel.getStyleClass().addAll("text-xl", "font-semibold", "text-primary");
        
        Label messageLabel = new Label("Create your first coding problem to start practicing!");
        messageLabel.getStyleClass().addAll("text-base", "text-secondary");
        messageLabel.setWrapText(true);
        messageLabel.setStyle("-fx-text-alignment: center;");
        
        Button createButton = new Button("Create Your First Problem");
        createButton.getStyleClass().add("primary-button");
        createButton.setOnAction(e -> handleCreateNewProblem());
        
        emptyState.getChildren().addAll(iconLabel, titleLabel, messageLabel, createButton);
        
        return emptyState;
    }
    
    /**
     * Shows empty state by replacing the entire scroll pane content
     */
    private void showEmptyState() {
        // Clear the scroll pane content
        scrollPane.setContent(null);
        
        // Create centered empty state
        VBox emptyState = new VBox();
        emptyState.getStyleClass().add("empty-state");
        emptyState.setAlignment(Pos.CENTER);
        emptyState.setSpacing(16);
        emptyState.setPadding(new Insets(48));
        emptyState.setPrefWidth(400);
        emptyState.setPrefHeight(300);
        
        Label iconLabel = new Label();
        iconLabel.setGraphic(IconUtils.createLargeIconView("code"));
        iconLabel.getStyleClass().add("empty-icon");
        
        Label titleLabel = new Label("No Coding Problems Yet");
        titleLabel.getStyleClass().addAll("text-xl", "font-semibold", "text-primary");
        
        Label messageLabel = new Label("Create your first coding problem to start practicing!");
        messageLabel.getStyleClass().addAll("text-base", "text-secondary");
        messageLabel.setWrapText(true);
        messageLabel.setStyle("-fx-text-alignment: center;");
        
        Button createButton = new Button("Create Your First Problem");
        createButton.getStyleClass().add("primary-button");
        createButton.setOnAction(e -> handleCreateNewProblem());
        
        emptyState.getChildren().addAll(iconLabel, titleLabel, messageLabel, createButton);
        
        // Set the empty state as the scroll pane content
        scrollPane.setContent(emptyState);
    }
    
    /**
     * Handles solving a code problem
     */
    private void handleSolveProblem(CodeProblem problem) {
        // Log activity for attempting to solve a problem
        logActivity(ActivityType.CODE_PROBLEM_ATTEMPTED, "Started solving: " + problem.getTitle());
        
        // Create and show code editor view
        CodeEditorView editorView = new CodeEditorView(problem, this);
        
        // Replace current content with editor view
        javafx.scene.Parent parent = mainContainer.getParent();
        if (parent != null) {
            try {
                // Handle different types of parent containers
                if (parent instanceof VBox) {
                    VBox vboxParent = (VBox) parent;
                    vboxParent.getChildren().clear();
                    vboxParent.getChildren().add(editorView.getView());
                } else if (parent instanceof StackPane) {
                    StackPane stackParent = (StackPane) parent;
                    stackParent.getChildren().clear();
                    stackParent.getChildren().add(editorView.getView());
                } else if (parent instanceof BorderPane) {
                    BorderPane borderParent = (BorderPane) parent;
                    borderParent.setCenter(editorView.getView());
                } else if (parent instanceof HBox) {
                    HBox hboxParent = (HBox) parent;
                    hboxParent.getChildren().clear();
                    hboxParent.getChildren().add(editorView.getView());
                } else {
                    // Generic approach for other container types
                    System.out.println("Parent type: " + parent.getClass().getSimpleName());
                    // Try to use reflection to get children property
                    try {
                        java.lang.reflect.Method getChildrenMethod = parent.getClass().getMethod("getChildren");
                        @SuppressWarnings("unchecked")
                        javafx.collections.ObservableList<javafx.scene.Node> children = 
                            (javafx.collections.ObservableList<javafx.scene.Node>) getChildrenMethod.invoke(parent);
                        children.clear();
                        children.add(editorView.getView());
                    } catch (Exception e) {
                        System.err.println("Could not navigate to editor using reflection: " + e.getMessage());
                        // Fallback: show error
                        sceneManager.showErrorDialog("Navigation Error", 
                            "Could not open code editor. Please try again.");
                    }
                }
            } catch (Exception e) {
                System.err.println("Error navigating to code editor: " + e.getMessage());
                e.printStackTrace();
                sceneManager.showErrorDialog("Navigation Error", 
                    "Could not open code editor. Please try again.");
            }
        } else {
            System.err.println("No parent container found for navigation");
            sceneManager.showErrorDialog("Navigation Error", 
                "Could not open code editor. Please try again.");
        }
    }
    
    /**
     * Handles viewing a code problem (read-only)
     */
    private void handleViewProblem(CodeProblem problem) {
        String problemInfo = String.format(
            "Problem: %s\n\n" +
            "Description:\n%s\n\n" +
            "Difficulty: %s\n" +
            "Language: %s\n" +
            "Test Cases: %d\n" +
            "Status: %s\n" +
            "Attempts: %d",
            problem.getTitle(),
            problem.getDescription(),
            problem.getDifficulty().getDisplayName(),
            problem.getLanguage(),
            problem.getTestCases().size(),
            problem.getStatusText(),
            problem.getAttempts()
        );
        
        sceneManager.showInfoDialog("Problem Details", problemInfo);
    }
    
    /**
     * Handles deleting a code problem
     */
    private void handleDeleteProblem(CodeProblem problem) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Delete Problem");
        confirmDialog.setHeaderText("Are you sure you want to delete this problem?");
        confirmDialog.setContentText("Problem: " + problem.getTitle() + "\nLanguage: " + problem.getLanguage() + "\n\nThis action cannot be undone.");
        
        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    dataStore.deleteCodeProblem(problem.getId());
                    problemsList.remove(problem);
                    loadCodeProblems(); // Refresh the UI
                    sceneManager.showInfoDialog("Problem Deleted", 
                        "The code problem '" + problem.getTitle() + "' has been successfully deleted.");
                } catch (Exception e) {
                    sceneManager.showErrorDialog("Error", "Failed to delete problem: " + e.getMessage());
                }
            }
        });
    }
    
    /**
     * Handles creating a new code problem
     */
    private void handleCreateNewProblem() {
        sceneManager.showInfoDialog("Create New Problem", 
            "Problem creation functionality will be available soon!\n\n" +
            "You'll be able to:\n" +
            "• Create custom coding challenges\n" +
            "• Add test cases with expected outputs\n" +
            "• Set difficulty levels and languages\n" +
            "• Include starter code templates");
    }
    
    /**
     * Applies sorting based on selected option
     */
    private void applySorting(String sortOption) {
        if (sortOption == null) return;
        
        switch (sortOption) {
            case "Title (A-Z)":
                sortedProblems.setComparator(Comparator.comparing(CodeProblem::getTitle, String.CASE_INSENSITIVE_ORDER));
                break;
            case "Title (Z-A)":
                sortedProblems.setComparator(Comparator.comparing(CodeProblem::getTitle, String.CASE_INSENSITIVE_ORDER).reversed());
                break;
            case "Difficulty (Easy to Hard)":
                sortedProblems.setComparator(Comparator.comparing(CodeProblem::getDifficulty, Comparator.comparing(Enum::ordinal)));
                break;
            case "Difficulty (Hard to Easy)":
                sortedProblems.setComparator(Comparator.comparing(CodeProblem::getDifficulty, Comparator.comparing(Enum::ordinal)).reversed());
                break;
            case "Language (A-Z)":
                sortedProblems.setComparator(Comparator.comparing(CodeProblem::getLanguage, String.CASE_INSENSITIVE_ORDER));
                break;
            case "Language (Z-A)":
                sortedProblems.setComparator(Comparator.comparing(CodeProblem::getLanguage, String.CASE_INSENSITIVE_ORDER).reversed());
                break;
            case "Date Created (Newest)":
                sortedProblems.setComparator(Comparator.comparing(CodeProblem::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())));
                break;
            case "Date Created (Oldest)":
                sortedProblems.setComparator(Comparator.comparing(CodeProblem::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())));
                break;
            case "Last Attempted (Recent)":
                sortedProblems.setComparator(Comparator.comparing(CodeProblem::getLastAttempted, Comparator.nullsLast(Comparator.reverseOrder())));
                break;
            case "Last Attempted (Oldest)":
                sortedProblems.setComparator(Comparator.comparing(CodeProblem::getLastAttempted, Comparator.nullsLast(Comparator.naturalOrder())));
                break;
            case "Status (Completed First)":
                sortedProblems.setComparator(Comparator.comparing(CodeProblem::isCompleted).reversed());
                break;
            case "Status (Incomplete First)":
                sortedProblems.setComparator(Comparator.comparing(CodeProblem::isCompleted));
                break;
            case "Attempts (Low to High)":
                sortedProblems.setComparator(Comparator.comparing(CodeProblem::getAttempts));
                break;
            case "Attempts (High to Low)":
                sortedProblems.setComparator(Comparator.comparing(CodeProblem::getAttempts).reversed());
                break;
        }
        
        // Reload problems to show sorted results
        loadCodeProblems();
    }
    
    /**
     * Logs an activity to the DataStore
     */
    private void logActivity(ActivityType type, String description) {
        if (dataStore.getCurrentUser() != null) {
            Activity activity = new Activity(dataStore.getCurrentUser().getId(), type, description);
            dataStore.logActivity(activity);
        }
    }
    
    /**
     * Gets the main view container
     */
    public VBox getView() {
        return mainContainer;
    }
    
    /**
     * Refreshes the problems list (useful when returning from editor)
     */
    public void refresh() {
        // Refresh the data from the data store
        problemsList.clear();
        problemsList.addAll(dataStore.getAllCodeProblems());
        loadCodeProblems();
    }
}
