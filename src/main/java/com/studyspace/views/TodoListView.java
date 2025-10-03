package com.studyspace.views;

import com.studyspace.models.TodoItem;
import com.studyspace.models.Activity;
import com.studyspace.models.ActivityType;
import com.studyspace.utils.DataStore;
import com.studyspace.utils.SceneManager;
import com.studyspace.utils.IconUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TodoListView extends VBox {
    
    private final DataStore dataStore;
    private final SceneManager sceneManager;
    private final ObservableList<TodoItem> todoItems;
    private final FilteredList<TodoItem> filteredItems;
    
    // UI Components
    private TextField taskInput;
    private Button addTaskButton;
    private ToggleGroup filterGroup;
    private RadioButton allFilter;
    private RadioButton activeFilter;
    private RadioButton completedFilter;
    private RadioButton overdueFilter;
    private VBox taskListContainer;
    private Label progressText;
    private Label progressPercentage;
    private ProgressBar progressBar;
    
    public TodoListView() {
        this.dataStore = DataStore.getInstance();
        this.sceneManager = SceneManager.getInstance();
        this.todoItems = FXCollections.observableArrayList(dataStore.getTodoItems());
        this.filteredItems = new FilteredList<>(todoItems);
        
        initializeSampleData();
        createView();
        setupEventHandlers();
    }
    
    private void initializeSampleData() {
        if (todoItems.isEmpty()) {
            // Add sample todo items
            todoItems.add(new TodoItem("Review JavaScript closures", "Go through flashcards and practice problems", 
                TodoItem.Priority.HIGH, "Study", LocalDate.of(2024, 1, 20)));
            todoItems.add(new TodoItem("Complete React project", "Finish the todo app component", 
                TodoItem.Priority.MEDIUM, "Project", LocalDate.of(2024, 1, 22)));
            todoItems.add(new TodoItem("Read CSS Grid documentation", "Learn about CSS Grid layout", 
                TodoItem.Priority.LOW, "Learning", LocalDate.of(2024, 1, 18)));
            todoItems.add(new TodoItem("Practice coding problems", "Solve 3 algorithm problems on LeetCode", 
                TodoItem.Priority.HIGH, "Practice", LocalDate.of(2024, 1, 19)));
            
            // Mark one as completed
            todoItems.get(2).setCompleted(true);
        }
    }
    
    private void createView() {
        setSpacing(24);
        setPadding(new Insets(32));
        getStyleClass().add("todo-list-view");
        
        // Header
        VBox header = createHeader();
        
        // Progress Overview
        HBox progressCard = createProgressCard();
        
        // Add Task Section
        HBox addTaskSection = createAddTaskSection();
        
        // Filter Section
        HBox filterSection = createFilterSection();
        
        // Task List
        taskListContainer = new VBox();
        taskListContainer.setSpacing(12);
        taskListContainer.getStyleClass().add("task-list-container");
        
        ScrollPane scrollPane = new ScrollPane(taskListContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("todo-scroll-pane");
        
        getChildren().addAll(header, progressCard, addTaskSection, filterSection, scrollPane);
        
        updateTaskList();
        updateProgress();
    }
    
    private VBox createHeader() {
        VBox header = new VBox();
        header.setSpacing(8);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label title = new Label("To Do List");
        title.getStyleClass().add("todo-title");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        title.setTextFill(Color.WHITE);
        
        Label subtitle = new Label("Stay organized and track your learning goals");
        subtitle.getStyleClass().add("todo-subtitle");
        subtitle.setFont(Font.font("Segoe UI", 16));
        subtitle.setTextFill(Color.WHITE);
        subtitle.setOpacity(0.8);
        
        header.getChildren().addAll(title, subtitle);
        return header;
    }
    
    private HBox createProgressCard() {
        HBox progressCard = new HBox();
        progressCard.setSpacing(20);
        progressCard.setAlignment(Pos.CENTER_LEFT);
        progressCard.setPadding(new Insets(20));
        progressCard.getStyleClass().add("progress-card");
        
        // Left side - Icon and text
        HBox leftSection = new HBox();
        leftSection.setSpacing(12);
        leftSection.setAlignment(Pos.CENTER_LEFT);
        
        // Progress icon (purple square with checkmark)
        VBox progressIcon = new VBox();
        progressIcon.setPrefSize(40, 40);
        progressIcon.setAlignment(Pos.CENTER);
        progressIcon.getStyleClass().add("progress-icon");
        
        Label checkmark = new Label("✓");
        checkmark.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        checkmark.setTextFill(Color.WHITE);
        progressIcon.getChildren().add(checkmark);
        
        VBox progressTextSection = new VBox();
        progressTextSection.setSpacing(4);
        
        Label progressLabel = new Label("Progress Overview");
        progressLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        progressLabel.setTextFill(Color.WHITE);
        
        progressText = new Label();
        progressText.setFont(Font.font("Segoe UI", 14));
        progressText.setTextFill(Color.WHITE);
        progressText.setOpacity(0.8);
        
        progressTextSection.getChildren().addAll(progressLabel, progressText);
        leftSection.getChildren().addAll(progressIcon, progressTextSection);
        
        // Right side - Percentage
        VBox rightSection = new VBox();
        rightSection.setAlignment(Pos.CENTER_RIGHT);
        
        progressPercentage = new Label();
        progressPercentage.setFont(Font.font("Segoe UI", FontWeight.BOLD, 32));
        progressPercentage.setTextFill(Color.WHITE);
        
        Label completeLabel = new Label("Complete");
        completeLabel.setFont(Font.font("Segoe UI", 14));
        completeLabel.setTextFill(Color.WHITE);
        completeLabel.setOpacity(0.8);
        
        rightSection.getChildren().addAll(progressPercentage, completeLabel);
        
        progressCard.getChildren().addAll(leftSection, rightSection);
        return progressCard;
    }
    
    private HBox createAddTaskSection() {
        HBox addTaskSection = new HBox();
        addTaskSection.setSpacing(12);
        addTaskSection.setAlignment(Pos.CENTER_LEFT);
        
        taskInput = new TextField();
        taskInput.setPromptText("Add a new task...");
        taskInput.getStyleClass().add("task-input");
        taskInput.setPrefWidth(400);
        
        addTaskButton = new Button("+ Add Task");
        addTaskButton.getStyleClass().add("add-task-button");
        
        addTaskSection.getChildren().addAll(taskInput, addTaskButton);
        return addTaskSection;
    }
    
    private HBox createFilterSection() {
        HBox filterSection = new HBox();
        filterSection.setSpacing(8);
        filterSection.setAlignment(Pos.CENTER_LEFT);
        
        filterGroup = new ToggleGroup();
        
        allFilter = new RadioButton("All (0)");
        allFilter.setToggleGroup(filterGroup);
        allFilter.setSelected(true);
        allFilter.getStyleClass().add("filter-button");
        allFilter.getStyleClass().add("filter-active");
        
        activeFilter = new RadioButton("Active (0)");
        activeFilter.setToggleGroup(filterGroup);
        activeFilter.getStyleClass().add("filter-button");
        
        completedFilter = new RadioButton("Completed (0)");
        completedFilter.setToggleGroup(filterGroup);
        completedFilter.getStyleClass().add("filter-button");
        
        // Add separator
        Separator separator = new Separator();
        separator.setOrientation(javafx.geometry.Orientation.VERTICAL);
        separator.getStyleClass().add("filter-separator");
        
        // Add overdue filter with special styling
        overdueFilter = new RadioButton("Overdue (0)");
        overdueFilter.setToggleGroup(filterGroup);
        overdueFilter.getStyleClass().add("filter-button");
        overdueFilter.getStyleClass().add("overdue-filter");
        
        filterSection.getChildren().addAll(allFilter, activeFilter, completedFilter, separator, overdueFilter);
        return filterSection;
    }
    
    private void setupEventHandlers() {
        try {
            // Add task button with proper event handling
            if (addTaskButton != null) {
                addTaskButton.setOnAction(e -> {
                    e.consume();
                    addTask();
                });
                addTaskButton.setOnMouseClicked(e -> {
                    e.consume();
                    addTask();
                });
            }
            
            // Enter key in input field with null check
            if (taskInput != null) {
                taskInput.setOnAction(e -> {
                    e.consume();
                    addTask();
                });
            }
            
            // Filter buttons with null checks
            if (allFilter != null) {
                allFilter.setOnAction(e -> {
                    e.consume();
                    applyFilter("all");
                });
            }
            if (activeFilter != null) {
                activeFilter.setOnAction(e -> {
                    e.consume();
                    applyFilter("active");
                });
            }
            if (completedFilter != null) {
                completedFilter.setOnAction(e -> {
                    e.consume();
                    applyFilter("completed");
                });
            }
            if (overdueFilter != null) {
                overdueFilter.setOnAction(e -> {
                    e.consume();
                    applyFilter("overdue");
                });
            }
        } catch (Exception e) {
            System.err.println("Error setting up event handlers: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void addTask() {
        String taskText = taskInput.getText().trim();
        if (!taskText.isEmpty()) {
            // Create a new todo item with the basic text
            TodoItem newTask = new TodoItem(taskText, "", TodoItem.Priority.MEDIUM, "General", LocalDate.now().plusDays(7));
            
            // Show the edit dialog to let user fill in details
            editTodoItem(newTask);
            
            // Clear the input field
            taskInput.clear();
        }
    }
    
    private void applyFilter(String filter) {
        switch (filter) {
            case "all":
                filteredItems.setPredicate(item -> true);
                break;
            case "active":
                filteredItems.setPredicate(item -> !item.isCompleted());
                break;
            case "completed":
                filteredItems.setPredicate(item -> item.isCompleted());
                break;
            case "overdue":
                java.time.LocalDate today = java.time.LocalDate.now();
                filteredItems.setPredicate(item -> !item.isCompleted() && 
                    item.getDueDate() != null && item.getDueDate().isBefore(today));
                break;
        }
        updateTaskList();
    }
    
    private void updateTaskList() {
        taskListContainer.getChildren().clear();
        
        // Update filter button labels
        long totalCount = todoItems.size();
        long activeCount = todoItems.stream().filter(item -> !item.isCompleted()).count();
        long completedCount = todoItems.stream().filter(item -> item.isCompleted()).count();
        
        // Calculate overdue count
        java.time.LocalDate today = java.time.LocalDate.now();
        long overdueCount = todoItems.stream().filter(item -> 
            !item.isCompleted() && item.getDueDate() != null && item.getDueDate().isBefore(today)
        ).count();
        
        allFilter.setText("All (" + totalCount + ")");
        activeFilter.setText("Active (" + activeCount + ")");
        completedFilter.setText("Completed (" + completedCount + ")");
        overdueFilter.setText("Overdue (" + overdueCount + ")");
        
        // Create task cards
        for (TodoItem item : filteredItems) {
            HBox taskCard = createTaskCard(item);
            taskListContainer.getChildren().add(taskCard);
        }
    }
    
    private HBox createTaskCard(TodoItem item) {
        HBox taskCard = new HBox();
        taskCard.setSpacing(16);
        taskCard.setAlignment(Pos.CENTER_LEFT);
        taskCard.setPadding(new Insets(16));
        taskCard.getStyleClass().add("task-card");
        
        // Checkbox
        CheckBox checkbox = new CheckBox();
        checkbox.setSelected(item.isCompleted());
        checkbox.setOnAction(e -> {
            boolean wasCompleted = item.isCompleted();
            item.setCompleted(checkbox.isSelected());
            dataStore.saveTodoItem(item); // Save to DataStore
            
            // Log activity for completion status change
            if (checkbox.isSelected() && !wasCompleted) {
                logActivity(ActivityType.TODO_ITEM_COMPLETED, "Completed task: " + item.getTitle());
            }
            
            updateProgress();
            updateTaskList();
            refreshSidebarNotifications();
        });
        checkbox.getStyleClass().add("task-checkbox");
        
        // Task content
        VBox taskContent = new VBox();
        taskContent.setSpacing(8);
        taskContent.setAlignment(Pos.CENTER_LEFT);
        
        // Task title
        Label titleLabel = new Label(item.getTitle());
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        titleLabel.setTextFill(Color.WHITE);
        if (item.isCompleted()) {
            titleLabel.setOpacity(0.6);
            titleLabel.setStyle("-fx-strikethrough: true;");
        }
        
        // Tags
        HBox tagsContainer = new HBox();
        tagsContainer.setSpacing(8);
        
        // Priority tag
        Label priorityTag = new Label(item.getPriority().toString().toLowerCase());
        priorityTag.getStyleClass().add("priority-tag");
        priorityTag.getStyleClass().add("priority-" + item.getPriority().toString().toLowerCase());
        
        // Category tag
        Label categoryTag = new Label(item.getCategory());
        categoryTag.getStyleClass().add("category-tag");
        
        tagsContainer.getChildren().addAll(priorityTag, categoryTag);
        
        // Description
        if (!item.getDescription().isEmpty()) {
            Label descriptionLabel = new Label(item.getDescription());
            descriptionLabel.setFont(Font.font("Segoe UI", 14));
            descriptionLabel.setTextFill(Color.WHITE);
            descriptionLabel.setOpacity(0.8);
            descriptionLabel.setWrapText(true);
            taskContent.getChildren().add(descriptionLabel);
        }
        
        // Due date and status
        HBox dateStatusContainer = new HBox();
        dateStatusContainer.setSpacing(8);
        dateStatusContainer.setAlignment(Pos.CENTER_LEFT);
        
        if (item.getDueDate() != null) {
            String dueDateText = "Due: " + item.getDueDate().format(DateTimeFormatter.ofPattern("M/d/yyyy"));
            Label dueDateLabel = new Label(dueDateText);
            dueDateLabel.setFont(Font.font("Segoe UI", 12));
            
            boolean isOverdue = item.getDueDate().isBefore(LocalDate.now()) && !item.isCompleted();
            if (isOverdue) {
                dueDateLabel.setTextFill(Color.web("#ff6b6b"));
            } else {
                dueDateLabel.setTextFill(Color.WHITE);
                dueDateLabel.setOpacity(0.7);
            }
            
            dateStatusContainer.getChildren().add(dueDateLabel);
            
            if (isOverdue) {
                Label overdueLabel = new Label("Overdue");
                overdueLabel.getStyleClass().add("overdue-label");
                dateStatusContainer.getChildren().add(overdueLabel);
            }
        }
        
        taskContent.getChildren().addAll(titleLabel, tagsContainer, dateStatusContainer);
        
        // Action buttons container
        HBox actionButtons = new HBox();
        actionButtons.setSpacing(8);
        actionButtons.setAlignment(Pos.CENTER_RIGHT);
        
        // Edit button
        Button editButton = new Button();
        editButton.setGraphic(IconUtils.createIconView("edit"));
        editButton.getStyleClass().addAll("action-button", "edit");
        editButton.setOnAction(e -> {
            e.consume();
            editTodoItem(item);
        });
        editButton.setOnMouseClicked(e -> {
            e.consume();
            editTodoItem(item);
        });
        
        // Delete button
        Button deleteButton = new Button();
        deleteButton.setGraphic(IconUtils.createIconView("trash"));
        deleteButton.getStyleClass().addAll("action-button", "delete");
        deleteButton.setOnAction(e -> {
            e.consume();
            todoItems.remove(item);
            dataStore.deleteTodoItem(item.getId()); // Remove from DataStore
            updateTaskList();
            updateProgress();
            refreshSidebarNotifications();
        });
        deleteButton.setOnMouseClicked(e -> {
            e.consume();
            todoItems.remove(item);
            dataStore.deleteTodoItem(item.getId()); // Remove from DataStore
            updateTaskList();
            updateProgress();
            refreshSidebarNotifications();
        });
        
        actionButtons.getChildren().addAll(editButton, deleteButton);
        taskCard.getChildren().addAll(checkbox, taskContent, actionButtons);
        return taskCard;
    }
    
    private void updateProgress() {
        int totalTasks = todoItems.size();
        int completedTasks = (int) todoItems.stream().filter(TodoItem::isCompleted).count();
        
        if (totalTasks > 0) {
            double progress = (double) completedTasks / totalTasks;
            int percentage = (int) (progress * 100);
            
            progressText.setText(completedTasks + " of " + totalTasks + " tasks completed");
            progressPercentage.setText(percentage + "%");
        } else {
            progressText.setText("No tasks yet");
            progressPercentage.setText("0%");
        }
        
        // Note: Sidebar notifications will be refreshed when the sidebar is next accessed
    }
    
    /**
     * Refreshes the sidebar notifications by finding and updating the sidebar instance
     */
    private void refreshSidebarNotifications() {
        try {
            // Try to find the sidebar through the scene hierarchy
            javafx.scene.Scene scene = this.getScene();
            if (scene != null) {
                javafx.scene.Parent root = scene.getRoot();
                if (root instanceof javafx.scene.layout.BorderPane) {
                    javafx.scene.layout.BorderPane borderPane = (javafx.scene.layout.BorderPane) root;
                    javafx.scene.Node left = borderPane.getLeft();
                    if (left instanceof javafx.scene.layout.VBox) {
                        // The left side is the sidebar - try to find the notification button
                        findAndUpdateNotificationButton(left);
                    }
                }
            }
            
            // Also refresh the sidebar progress and activity history
            com.studyspace.components.SidebarView.refreshActivityHistoryGlobally();
        } catch (Exception e) {
            // Silently handle any errors - notification refresh is not critical
            System.out.println("Could not refresh sidebar notifications: " + e.getMessage());
        }
    }
    
    /**
     * Recursively finds and updates the notification button in the sidebar
     */
    private void findAndUpdateNotificationButton(javafx.scene.Node node) {
        if (node instanceof javafx.scene.control.Button) {
            javafx.scene.control.Button button = (javafx.scene.control.Button) node;
            if (button.getStyleClass().contains("notification-button")) {
                // Found the notification button - update it
                updateNotificationButton(button);
                return;
            }
        }
        
        // Recursively search children
        if (node instanceof javafx.scene.Parent) {
            javafx.scene.Parent parent = (javafx.scene.Parent) node;
            for (javafx.scene.Node child : parent.getChildrenUnmodifiable()) {
                findAndUpdateNotificationButton(child);
            }
        }
    }
    
    /**
     * Updates the notification button based on current overdue items
     */
    private void updateNotificationButton(javafx.scene.control.Button notificationButton) {
        try {
            // Count overdue items from DataStore to ensure consistency
            java.time.LocalDate today = java.time.LocalDate.now();
            long overdueCount = dataStore.getTodoItems().stream().filter(item -> 
                !item.isCompleted() && item.getDueDate() != null && item.getDueDate().isBefore(today)
            ).count();
            
            if (overdueCount > 0) {
                notificationButton.setVisible(true);
                notificationButton.setManaged(true);
                notificationButton.setText(" " + overdueCount);
            } else {
                notificationButton.setVisible(false);
                notificationButton.setManaged(false);
            }
        } catch (Exception e) {
            System.out.println("Error updating notification button: " + e.getMessage());
        }
    }
    
    /**
     * Handles editing a todo item
     */
    private void editTodoItem(TodoItem item) {
        // Create a dialog for editing the todo item
        Dialog<TodoItem> editDialog = new Dialog<>();
        editDialog.setTitle("Edit Task");
        editDialog.setHeaderText("Edit your task details");
        
        // Create form fields
        TextField titleField = new TextField(item.getTitle());
        titleField.setPromptText("Task title");
        titleField.setPrefWidth(300);
        
        TextArea descriptionField = new TextArea(item.getDescription());
        descriptionField.setPromptText("Task description (optional)");
        descriptionField.setPrefRowCount(3);
        descriptionField.setWrapText(true);
        
        ComboBox<TodoItem.Priority> priorityCombo = new ComboBox<>();
        priorityCombo.getItems().addAll(TodoItem.Priority.values());
        priorityCombo.setValue(item.getPriority());
        priorityCombo.setPrefWidth(150);
        
        TextField categoryField = new TextField(item.getCategory());
        categoryField.setPromptText("Category (optional)");
        categoryField.setPrefWidth(200);
        
        DatePicker dueDatePicker = new DatePicker();
        dueDatePicker.setValue(item.getDueDate());
        dueDatePicker.setPrefWidth(150);
        
        // Create form layout
        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(10);
        formGrid.setPadding(new Insets(20));
        
        formGrid.add(new Label("Title:"), 0, 0);
        formGrid.add(titleField, 1, 0);
        formGrid.add(new Label("Description:"), 0, 1);
        formGrid.add(descriptionField, 1, 1);
        formGrid.add(new Label("Priority:"), 0, 2);
        formGrid.add(priorityCombo, 1, 2);
        formGrid.add(new Label("Category:"), 0, 3);
        formGrid.add(categoryField, 1, 3);
        formGrid.add(new Label("Due Date:"), 0, 4);
        formGrid.add(dueDatePicker, 1, 4);
        
        editDialog.getDialogPane().setContent(formGrid);
        
        // Add buttons
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        editDialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);
        
        // Set result converter
        editDialog.setResultConverter(buttonType -> {
            if (buttonType == saveButtonType) {
                // Create updated todo item
                TodoItem updatedItem = new TodoItem(
                    titleField.getText().trim(),
                    descriptionField.getText().trim(),
                    priorityCombo.getValue(),
                    categoryField.getText().trim(),
                    dueDatePicker.getValue()
                );
                updatedItem.setCompleted(item.isCompleted());
                updatedItem.setId(item.getId()); // Keep the same ID
                return updatedItem;
            }
            return null;
        });
        
        // Show dialog and handle result
        editDialog.showAndWait().ifPresent(updatedItem -> {
            if (updatedItem != null && !updatedItem.getTitle().isEmpty()) {
                // Check if this is a new item (not in the list yet) or an existing item
                int index = todoItems.indexOf(item);
                if (index >= 0) {
                    // Existing item - replace it
                    todoItems.set(index, updatedItem);
                    dataStore.saveTodoItem(updatedItem); // Save to DataStore
                } else {
                    // New item - add it to the list
                    todoItems.add(updatedItem);
                    dataStore.saveTodoItem(updatedItem); // Save to DataStore
                    // Log activity for new todo item
                    logActivity(ActivityType.TODO_ITEM_ADDED, "Added task: " + updatedItem.getTitle());
                }
                updateTaskList();
                updateProgress();
                refreshSidebarNotifications();
            }
        });
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
}
