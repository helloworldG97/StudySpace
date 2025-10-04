package com.studyspace.views;

import com.studyspace.models.CodeProblem;
import com.studyspace.models.User;
import com.studyspace.utils.DataStore;
import com.studyspace.utils.SceneManager;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

//============ code editor view =============
//this is where programming problems are solved and code is written

public class CodeEditorView {
    
    private final DataStore dataStore;
    private final SceneManager sceneManager;
    private final CodeProblem problem;
    private final CodePracticeView parentView;
    
    private VBox mainContainer;
    private TextArea codeEditor;
    private VBox resultsContainer;
    private ScrollPane resultsScrollPane;
    private Label statusLabel;
    
    public CodeEditorView(CodeProblem problem, CodePracticeView parentView) {
        this.dataStore = DataStore.getInstance();
        this.sceneManager = SceneManager.getInstance();
        this.problem = problem;
        this.parentView = parentView;
        
        initializeUI();
        loadProblem();
    }
    
    /**
     * Initializes the UI components
     */
    private void initializeUI() {
        mainContainer = new VBox();
        mainContainer.setSpacing(24);
        mainContainer.getStyleClass().add("content-area");
        
        // Header section
        VBox headerSection = createHeaderSection();
        
        // Main content area with split layout
        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(javafx.geometry.Orientation.HORIZONTAL);
        splitPane.setPrefHeight(500); // Reduced height to leave space for buttons
        splitPane.setMaxHeight(500); // Set maximum height to prevent expansion
        
        // Left side - Problem description
        VBox problemSection = createProblemSection();
        problemSection.setPrefWidth(400);
        
        // Right side - Code editor and results
        VBox editorSection = createEditorSection();
        editorSection.setPrefWidth(600);
        
        splitPane.getItems().addAll(problemSection, editorSection);
        splitPane.setDividerPositions(0.4);
        
        // Controls section
        VBox controlsSection = createControlsSection();
        
        mainContainer.getChildren().addAll(headerSection, splitPane, controlsSection);
    }
    
    /**
     * Creates the header section
     */
    private VBox createHeaderSection() {
        VBox header = new VBox();
        header.setSpacing(8);
        
        Label titleLabel = new Label("💻 " + problem.getTitle());
        titleLabel.getStyleClass().addAll("text-2xl", "font-bold", "text-primary");
        
        HBox metaInfo = new HBox();
        metaInfo.setSpacing(16);
        metaInfo.setAlignment(Pos.CENTER_LEFT);
        
        Label difficultyLabel = new Label(problem.getDifficulty().getDisplayName());
        difficultyLabel.getStyleClass().addAll("difficulty-badge", 
            problem.getDifficulty().name().toLowerCase());
        
        Label languageLabel = new Label("⚡ " + problem.getLanguage());
        languageLabel.getStyleClass().addAll("text-sm", "text-secondary");
        
        Label testCasesLabel = new Label("🧪 " + problem.getTestCases().size() + " test cases");
        testCasesLabel.getStyleClass().addAll("text-sm", "text-muted");
        
        statusLabel = new Label("📊 " + problem.getStatusText());
        statusLabel.getStyleClass().addAll("text-sm", 
            problem.isCompleted() ? "text-success" : "text-secondary");
        
        metaInfo.getChildren().addAll(difficultyLabel, languageLabel, testCasesLabel, statusLabel);
        
        header.getChildren().addAll(titleLabel, metaInfo);
        
        return header;
    }
    
    /**
     * Creates the problem description section
     */
    private VBox createProblemSection() {
        VBox section = new VBox();
        section.getStyleClass().add("code-practice-container");
        section.setSpacing(16);
        
        // Problem description card
        VBox descriptionCard = new VBox();
        descriptionCard.getStyleClass().add("code-problem-description");
        descriptionCard.setSpacing(12);
        
        Label descTitle = new Label("📋 Problem Description");
        descTitle.getStyleClass().addAll("text-lg", "font-semibold", "text-primary");
        
        Label descContent = new Label(problem.getDescription());
        descContent.getStyleClass().addAll("text-sm", "text-primary");
        descContent.setWrapText(true);
        
        descriptionCard.getChildren().addAll(descTitle, descContent);
        
        // Test cases display
        VBox testCasesCard = new VBox();
        testCasesCard.getStyleClass().add("code-problem-description");
        testCasesCard.setSpacing(12);
        
        Label testCasesTitle = new Label("🧪 Test Cases");
        testCasesTitle.getStyleClass().addAll("text-lg", "font-semibold", "text-primary");
        
        VBox testCasesList = new VBox();
        testCasesList.setSpacing(8);
        
        List<CodeProblem.TestCase> testCases = problem.getTestCases();
        for (int i = 0; i < Math.min(testCases.size(), 3); i++) {
            CodeProblem.TestCase testCase = testCases.get(i);
            VBox testCaseItem = createTestCaseDisplay(i + 1, testCase);
            testCasesList.getChildren().add(testCaseItem);
        }
        
        if (testCases.size() > 3) {
            Label moreLabel = new Label("... and " + (testCases.size() - 3) + " more test cases");
            moreLabel.getStyleClass().addAll("text-xs", "text-muted");
            testCasesList.getChildren().add(moreLabel);
        }
        
        testCasesCard.getChildren().addAll(testCasesTitle, testCasesList);
        
        section.getChildren().addAll(descriptionCard, testCasesCard);
        
        return section;
    }
    
    /**
     * Creates a test case display item
     */
    private VBox createTestCaseDisplay(int caseNumber, CodeProblem.TestCase testCase) {
        VBox item = new VBox();
        item.getStyleClass().add("test-case");
        item.setSpacing(4);
        
        Label caseLabel = new Label("Test Case " + caseNumber + ":");
        caseLabel.getStyleClass().addAll("text-xs", "font-semibold", "text-secondary");
        
        Label inputLabel = new Label("Input: " + testCase.getInput());
        inputLabel.getStyleClass().addAll("text-xs", "text-primary");
        inputLabel.setWrapText(true);
        
        Label outputLabel = new Label("Expected: " + testCase.getExpectedOutput());
        outputLabel.getStyleClass().addAll("text-xs", "text-primary");
        outputLabel.setWrapText(true);
        
        if (testCase.getDescription() != null && !testCase.getDescription().isEmpty()) {
            Label descLabel = new Label(testCase.getDescription());
            descLabel.getStyleClass().addAll("text-xs", "text-muted");
            descLabel.setWrapText(true);
            item.getChildren().addAll(caseLabel, inputLabel, outputLabel, descLabel);
        } else {
            item.getChildren().addAll(caseLabel, inputLabel, outputLabel);
        }
        
        return item;
    }
    
    /**
     * Creates the code editor section
     */
    private VBox createEditorSection() {
        VBox section = new VBox();
        section.getStyleClass().add("code-editor-container");
        section.setSpacing(16);
        
        // Code editor
        Label editorTitle = new Label("✏️ Code Editor");
        editorTitle.getStyleClass().addAll("text-lg", "font-semibold", "text-primary");
        
        codeEditor = new TextArea();
        codeEditor.getStyleClass().add("code-editor");
        codeEditor.setPrefRowCount(15); // Reduced row count
        codeEditor.setWrapText(false);
        codeEditor.setPrefHeight(300); // Set preferred height
        codeEditor.setMaxHeight(300); // Set maximum height
        
        // Results section with scroll pane
        resultsContainer = new VBox();
        resultsContainer.getStyleClass().add("code-results");
        resultsContainer.setSpacing(12);
        resultsContainer.setPrefHeight(150); // Reduced height
        resultsContainer.setMaxHeight(150); // Set maximum height
        resultsContainer.setVisible(false);
        resultsContainer.setManaged(false);
        
        Label resultsTitle = new Label("📊 Test Results");
        resultsTitle.getStyleClass().addAll("text-lg", "font-semibold", "text-primary");
        
        // Wrap results in scroll pane
        resultsScrollPane = new ScrollPane(resultsContainer);
        resultsScrollPane.setFitToWidth(true);
        resultsScrollPane.setPrefHeight(150);
        resultsScrollPane.setMaxHeight(150);
        resultsScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        resultsScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        resultsScrollPane.getStyleClass().add("code-results-scroll");
        
        resultsContainer.getChildren().add(resultsTitle);
        
        section.getChildren().addAll(editorTitle, codeEditor, resultsScrollPane);
        
        return section;
    }
    
    /**
     * Creates the controls section
     */
    private VBox createControlsSection() {
        VBox controls = new VBox();
        controls.setSpacing(16);
        controls.setAlignment(Pos.CENTER);
        
        // Action buttons
        HBox actionButtons = new HBox();
        actionButtons.setSpacing(16);
        actionButtons.setAlignment(Pos.CENTER);
        
        Button runButton = new Button("▶️ Run Code");
        runButton.getStyleClass().add("primary-button");
        runButton.setOnAction(e -> handleRunCode());
        
        Button submitButton = new Button("✅ Submit Solution");
        submitButton.getStyleClass().add("success-button");
        submitButton.setOnAction(e -> handleSubmitSolution());
        
        Button resetButton = new Button("🔄 Reset Code");
        resetButton.getStyleClass().add("secondary-button");
        resetButton.setOnAction(e -> handleResetCode());
        
        actionButtons.getChildren().addAll(runButton, submitButton, resetButton);
        
        // Back button
        Button backButton = new Button("← Back to Problems");
        backButton.getStyleClass().add("secondary-button");
        backButton.setOnAction(e -> handleBackToProblems());
        
        controls.getChildren().addAll(actionButtons, backButton);
        
        return controls;
    }
    
    /**
     * Loads the problem and initializes the editor
     */
    private void loadProblem() {
        // Load starter code
        if (problem.getStarterCode() != null && !problem.getStarterCode().isEmpty()) {
            codeEditor.setText(problem.getStarterCode());
        } else {
            // Provide basic template based on language
            String template = getLanguageTemplate(problem.getLanguage());
            codeEditor.setText(template);
        }
        
        // Position cursor at the end of the starter code
        codeEditor.positionCaret(codeEditor.getText().length());
    }
    
    /**
     * Gets a basic template for the programming language
     */
    private String getLanguageTemplate(String language) {
        switch (language.toLowerCase()) {
            case "javascript":
                return "function solution() {\n    // Your code here\n    \n}";
            case "python":
                return "def solution():\n    # Your code here\n    pass";
            case "java":
                return "public class Solution {\n    public static void main(String[] args) {\n        // Your code here\n        \n    }\n}";
            default:
                return "// Your code here\n";
        }
    }
    
    /**
     * Handles running the code
     */
    private void handleRunCode() {
        String code = codeEditor.getText().trim();
        
        if (code.isEmpty()) {
            sceneManager.showErrorDialog("Empty Code", "Please write some code before running!");
            return;
        }
        
        // Mark problem as attempted
        problem.markAsAttempted();
        
        // Simulate code execution (in a real app, this would use a code execution engine)
        simulateCodeExecution(code, false);
        
        // Update status
        statusLabel.setText("📊 " + problem.getStatusText());
    }
    
    /**
     * Handles submitting the solution
     */
    private void handleSubmitSolution() {
        String code = codeEditor.getText().trim();
        
        if (code.isEmpty()) {
            sceneManager.showErrorDialog("Empty Code", "Please write some code before submitting!");
            return;
        }
        
        boolean confirmed = sceneManager.showConfirmationDialog(
            "Submit Solution", 
            "Are you sure you want to submit your solution? This will run all test cases."
        );
        
        if (confirmed) {
            // Mark problem as attempted
            problem.markAsAttempted();
            
            // Simulate code execution with all test cases
            boolean allPassed = simulateCodeExecution(code, true);
            
            if (allPassed) {
                // Mark as completed
                problem.markAsCompleted();
                
                // Update user statistics
                User currentUser = dataStore.getCurrentUser();
                if (currentUser != null) {
                    currentUser.incrementCodeProblemsCompleted();
                }
                
                // Log activity
                dataStore.logUserActivity("CODE_PROBLEM_SOLVED", "Solved " + problem.getTitle() + " code problem");
                
                // Refresh activity history
                com.studyspace.components.SidebarView.refreshActivityHistoryGlobally();
                
                // Show success dialog
                sceneManager.showInfoDialog("Success!", 
                    "🎉 Congratulations! You've successfully solved this problem!\n\n" +
                    "All test cases passed. Great job!");
            }
            
            // Update status
            statusLabel.setText("📊 " + problem.getStatusText());
        }
    }
    
    /**
     * Handles resetting the code
     */
    private void handleResetCode() {
        boolean confirmed = sceneManager.showConfirmationDialog(
            "Reset Code", 
            "Are you sure you want to reset your code? All changes will be lost."
        );
        
        if (confirmed) {
            loadProblem();
            
            // Hide results
            resultsScrollPane.setVisible(false);
            resultsScrollPane.setManaged(false);
        }
    }
    
    /**
     * Simulates code execution (in a real app, this would use a proper execution engine)
     */
    private boolean simulateCodeExecution(String code, boolean isSubmission) {
        // Show results container
        resultsScrollPane.setVisible(true);
        resultsScrollPane.setManaged(true);
        
        // Clear previous results
        resultsContainer.getChildren().removeIf(node -> 
            !(node instanceof Label && ((Label) node).getText().equals("📊 Test Results")));
        
        List<CodeProblem.TestCase> testCases = problem.getTestCases();
        int passedTests = 0;
        
        // Simulate test case execution
        for (int i = 0; i < testCases.size(); i++) {
            CodeProblem.TestCase testCase = testCases.get(i);
            
            // Simple simulation - randomly pass/fail based on code quality indicators
            boolean passed = simulateTestCase(code, testCase);
            if (passed) passedTests++;
            
            // Create test result display
            VBox testResult = createTestResultDisplay(i + 1, testCase, passed);
            resultsContainer.getChildren().add(testResult);
            
            // For run mode, only show first few test cases
            if (!isSubmission && i >= 2) {
                Label moreLabel = new Label("Run 'Submit Solution' to see all test results");
                moreLabel.getStyleClass().addAll("text-sm", "text-muted");
                resultsContainer.getChildren().add(moreLabel);
                break;
            }
        }
        
        // Add summary
        Label summaryLabel = new Label(String.format(
            "Results: %d/%d test cases passed", 
            passedTests, 
            isSubmission ? testCases.size() : Math.min(3, testCases.size())
        ));
        summaryLabel.getStyleClass().addAll("text-sm", "font-semibold", 
            passedTests == testCases.size() ? "text-success" : "text-warning");
        resultsContainer.getChildren().add(summaryLabel);
        
        return passedTests == testCases.size();
    }
    
    /**
     * Simulates a single test case execution
     */
    private boolean simulateTestCase(String code, CodeProblem.TestCase testCase) {
        // Simple heuristic-based simulation
        // In a real app, this would execute the code with the test input
        
        // Check for common patterns that indicate a good solution
        String lowerCode = code.toLowerCase();
        
        // For string reversal problems
        if (problem.getTitle().toLowerCase().contains("reverse")) {
            return lowerCode.contains("reverse") || lowerCode.contains("split") || 
                   lowerCode.contains("for") || lowerCode.contains("while");
        }
        
        // For array maximum problems
        if (problem.getTitle().toLowerCase().contains("maximum")) {
            return lowerCode.contains("max") || lowerCode.contains("math.max") || 
                   lowerCode.contains("for") || lowerCode.contains("while");
        }
        
        // For FizzBuzz problems
        if (problem.getTitle().toLowerCase().contains("fizzbuzz")) {
            return lowerCode.contains("fizz") && lowerCode.contains("buzz") && 
                   (lowerCode.contains("for") || lowerCode.contains("while"));
        }
        
        // Default: pass if code is non-trivial
        return code.length() > 50 && (lowerCode.contains("for") || lowerCode.contains("while") || 
                                     lowerCode.contains("if") || lowerCode.contains("return"));
    }
    
    /**
     * Creates a test result display item
     */
    private VBox createTestResultDisplay(int testNumber, CodeProblem.TestCase testCase, boolean passed) {
        VBox result = new VBox();
        result.getStyleClass().addAll("test-case", passed ? "passed" : "failed");
        result.setSpacing(4);
        
        Label testLabel = new Label("Test " + testNumber + ": " + (passed ? "✅ PASSED" : "❌ FAILED"));
        testLabel.getStyleClass().addAll("text-sm", "font-semibold", 
            passed ? "text-success" : "text-error");
        
        Label inputLabel = new Label("Input: " + testCase.getInput());
        inputLabel.getStyleClass().addAll("text-xs", "text-primary");
        
        Label expectedLabel = new Label("Expected: " + testCase.getExpectedOutput());
        expectedLabel.getStyleClass().addAll("text-xs", "text-primary");
        
        result.getChildren().addAll(testLabel, inputLabel, expectedLabel);
        
        if (!passed) {
            Label actualLabel = new Label("Your output: (simulated result)");
            actualLabel.getStyleClass().addAll("text-xs", "text-error");
            result.getChildren().add(actualLabel);
        }
        
        return result;
    }
    
    /**
     * Handles returning to the problems list
     */
    private void handleBackToProblems() {
        // Refresh the parent view to show updated statistics
        parentView.refresh();
        
        // Replace current content with the problems list
        javafx.scene.Parent parent = mainContainer.getParent();
        if (parent != null) {
            try {
                // Handle different types of parent containers
                if (parent instanceof VBox) {
                    VBox vboxParent = (VBox) parent;
                    vboxParent.getChildren().clear();
                    vboxParent.getChildren().add(parentView.getView());
                } else if (parent instanceof StackPane) {
                    StackPane stackParent = (StackPane) parent;
                    stackParent.getChildren().clear();
                    stackParent.getChildren().add(parentView.getView());
                } else if (parent instanceof BorderPane) {
                    BorderPane borderParent = (BorderPane) parent;
                    borderParent.setCenter(parentView.getView());
                } else if (parent instanceof HBox) {
                    HBox hboxParent = (HBox) parent;
                    hboxParent.getChildren().clear();
                    hboxParent.getChildren().add(parentView.getView());
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
                        children.add(parentView.getView());
                    } catch (Exception e) {
                        System.err.println("Could not navigate back using reflection: " + e.getMessage());
                        // Fallback: use SceneManager to navigate back to code practice
                        sceneManager.showInfoDialog("Navigation", "Returning to Code Practice section...");
                    }
                }
            } catch (Exception e) {
                System.err.println("Error navigating back to problems: " + e.getMessage());
                e.printStackTrace();
                // Show error dialog and provide alternative
                sceneManager.showErrorDialog("Navigation Error", 
                    "Could not return to problems list. Please use the sidebar to navigate to Code Practice.");
            }
        } else {
            System.err.println("No parent container found for navigation");
            sceneManager.showErrorDialog("Navigation Error", 
                "Could not return to problems list. Please use the sidebar to navigate to Code Practice.");
        }
    }
    
    /**
     * Gets the main view container
     */
    public VBox getView() {
        return mainContainer;
    }
}
