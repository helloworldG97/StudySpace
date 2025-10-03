package com.studyspace.views;

import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.Random;

public class StudySpaceLogin extends Application {
    private VBox loginForm;
    private VBox signupForm;
    private Button loginTab;
    private Button signupTab;

    @Override
    public void start(Stage primaryStage) {
        StackPane root = new StackPane();
        
        // Animated gradient background
        Region background = createAnimatedBackground();
        
        // Floating icons layer
        Pane floatingIconsPane = createFloatingIcons();
        
        // Main container
        VBox container = createMainContainer();
        
        root.getChildren().addAll(background, floatingIconsPane, container);
        
        Scene scene = new Scene(root, 1200, 700);
        primaryStage.setTitle("Study Space - Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Region createAnimatedBackground() {
        Region bg = new Region();
        bg.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        
        bg.setStyle("-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, "
            + "#1a0033 0%, #2d1b69 25%, #1e3a8a 50%, #0f1729 75%, #000000 100%);");
        
        return bg;
    }

    private Pane createFloatingIcons() {
        Pane pane = new Pane();
        pane.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        pane.setMouseTransparent(true);
        
        String[] icons = {"📚", "📝", "📁", "🧮", "✏️", "📊", "🎓", "💡", "📐", "🔬",
                         "📖", "🖊️", "📌", "🗂️", "📋", "🧪", "📈", "🎯", "⚗️", "📓",
                         "🔍", "📑", "📄", "📜", "📰", "📒", "📔", "📕", "📗", "📘",
                         "📙", "📚", "📖", "📝", "✏️", "🖊️", "🖋️", "✒️", "🖌️", "🖍️",
                         "📏", "📐", "📊", "📈", "📉", "📋", "📌", "📍", "📎", "🖇️"};
        Random random = new Random();
        
        for (int i = 0; i < 50; i++) {
            Text icon = new Text(icons[i % icons.length]);
            icon.setFont(Font.font(144));
            icon.setOpacity(0.15);
            icon.setFill(Color.web("#64c8ff"));
            
            DropShadow glow = new DropShadow();
            glow.setColor(Color.web("#64c8ff", 0.3));
            glow.setRadius(10);
            icon.setEffect(glow);
            
            double x = random.nextDouble() * 1100;
            double y = random.nextDouble() * 650;
            icon.setLayoutX(x);
            icon.setLayoutY(y);
            
            // Floating animation
            TranslateTransition tt = new TranslateTransition(
                Duration.seconds(18 + random.nextDouble() * 5), icon);
            tt.setByY(-50 + random.nextDouble() * 20);
            tt.setAutoReverse(true);
            tt.setCycleCount(Animation.INDEFINITE);
            tt.setDelay(Duration.seconds(random.nextDouble() * 5));
            tt.play();
            
            RotateTransition rt = new RotateTransition(
                Duration.seconds(20 + random.nextDouble() * 3), icon);
            rt.setByAngle(10 - random.nextDouble() * 20);
            rt.setAutoReverse(true);
            rt.setCycleCount(Animation.INDEFINITE);
            rt.play();
            
            pane.getChildren().add(icon);
        }
        
        return pane;
    }


    private VBox createMainContainer() {
        VBox container = new VBox(20);
        container.setMaxWidth(420);
        container.setPadding(new Insets(50, 40, 50, 40));
        container.setAlignment(Pos.TOP_CENTER);
        
        // Glass effect styling - more transparent
        container.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.08);" +
            "-fx-background-radius: 30;" +
            "-fx-border-color: rgba(255, 255, 255, 0.12);" +
            "-fx-border-radius: 30;" +
            "-fx-border-width: 1;" +
            "-fx-effect: dropshadow(gaussian, rgba(31, 38, 135, 0.25), 20, 0, 0, 6);"
        );
        
        GaussianBlur blur = new GaussianBlur(15);
        container.setEffect(blur);
        
        // Logo section
        VBox logo = createLogo();
        
        // Tabs
        HBox tabs = createTabs();
        
        // Forms
        loginForm = createLoginForm();
        signupForm = createSignupForm();
        signupForm.setVisible(false);
        signupForm.setManaged(false);
        
        container.getChildren().addAll(logo, tabs, loginForm, signupForm);
        
        return container;
    }

    private VBox createLogo() {
        VBox logo = new VBox(5);
        logo.setAlignment(Pos.CENTER);
        
        Label subtitle = new Label("Your learning journey starts here");
        subtitle.setFont(Font.font("Segoe UI", 14));
        subtitle.setTextFill(Color.web("white", 0.8));
        
        logo.getChildren().addAll(subtitle);
        return logo;
    }

    private HBox createTabs() {
        HBox tabs = new HBox(10);
        tabs.setAlignment(Pos.CENTER);
        
        loginTab = createTabButton("Login", true);
        signupTab = createTabButton("Sign Up", false);
        
        loginTab.setOnAction(e -> switchTab(true));
        signupTab.setOnAction(e -> switchTab(false));
        
        tabs.getChildren().addAll(loginTab, signupTab);
        return tabs;
    }

    private Button createTabButton(String text, boolean active) {
        Button btn = new Button(text);
        btn.setPrefWidth(190);
        btn.setPrefHeight(40);
        btn.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 16));
        btn.setTextFill(Color.WHITE);
        
        String style = active ?
            "-fx-background-color: rgba(255, 255, 255, 0.3);" +
            "-fx-background-radius: 15;" +
            "-fx-cursor: hand;" :
            "-fx-background-color: rgba(255, 255, 255, 0.1);" +
            "-fx-background-radius: 15;" +
            "-fx-cursor: hand;";
        
        btn.setStyle(style);
        
        btn.setOnMouseEntered(e -> {
            if (!btn.getStyle().contains("0.3")) {
                btn.setStyle("-fx-background-color: rgba(255, 255, 255, 0.2);" +
                           "-fx-background-radius: 15; -fx-cursor: hand;");
            }
        });
        
        btn.setOnMouseExited(e -> {
            if (!btn.getStyle().contains("0.3")) {
                btn.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1);" +
                           "-fx-background-radius: 15; -fx-cursor: hand;");
            }
        });
        
        return btn;
    }

    private void switchTab(boolean isLogin) {
        if (isLogin) {
            loginTab.setStyle("-fx-background-color: rgba(255, 255, 255, 0.3);" +
                            "-fx-background-radius: 15; -fx-cursor: hand;");
            signupTab.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1);" +
                             "-fx-background-radius: 15; -fx-cursor: hand;");
            loginForm.setVisible(true);
            loginForm.setManaged(true);
            signupForm.setVisible(false);
            signupForm.setManaged(false);
        } else {
            signupTab.setStyle("-fx-background-color: rgba(255, 255, 255, 0.3);" +
                             "-fx-background-radius: 15; -fx-cursor: hand;");
            loginTab.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1);" +
                            "-fx-background-radius: 15; -fx-cursor: hand;");
            signupForm.setVisible(true);
            signupForm.setManaged(true);
            loginForm.setVisible(false);
            loginForm.setManaged(false);
        }
    }

    private VBox createLoginForm() {
        VBox form = new VBox(15);
        
        TextField emailField = createTextField("Enter your email");
        PasswordField passwordField = createPasswordField("Enter your password");
        
        Hyperlink forgotPassword = new Hyperlink("Forgot password?");
        forgotPassword.setTextFill(Color.web("white", 0.8));
        forgotPassword.setAlignment(Pos.CENTER_RIGHT);
        
        Button loginBtn = createSubmitButton("Login");
        loginBtn.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Login");
            alert.setHeaderText(null);
            alert.setContentText("Login functionality would be implemented here!");
            alert.showAndWait();
        });
        
        VBox emailGroup = createFormGroup("Email", emailField);
        VBox passwordGroup = createFormGroup("Password", passwordField);
        
        form.getChildren().addAll(emailGroup, passwordGroup, forgotPassword, loginBtn);
        return form;
    }

    private VBox createSignupForm() {
        VBox form = new VBox(15);
        
        TextField nameField = createTextField("Enter your full name");
        TextField emailField = createTextField("Enter your email");
        PasswordField passwordField = createPasswordField("Create a password");
        
        CheckBox termsCheckbox = new CheckBox("I agree to the Terms & Conditions");
        termsCheckbox.setTextFill(Color.WHITE);
        termsCheckbox.setFont(Font.font(13));
        
        Button signupBtn = createSubmitButton("Create Account");
        signupBtn.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sign Up");
            alert.setHeaderText(null);
            alert.setContentText("Signup functionality would be implemented here!");
            alert.showAndWait();
        });
        
        VBox nameGroup = createFormGroup("Full Name", nameField);
        VBox emailGroup = createFormGroup("Email", emailField);
        VBox passwordGroup = createFormGroup("Password", passwordField);
        
        form.getChildren().addAll(nameGroup, emailGroup, passwordGroup, termsCheckbox, signupBtn);
        return form;
    }

    private VBox createFormGroup(String labelText, Control field) {
        VBox group = new VBox(8);
        
        Label label = new Label(labelText);
        label.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 14));
        label.setTextFill(Color.WHITE);
        
        group.getChildren().addAll(label, field);
        return group;
    }

    private TextField createTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setPrefHeight(45);
        field.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.1);" +
            "-fx-border-color: rgba(255, 255, 255, 0.3);" +
            "-fx-border-radius: 15;" +
            "-fx-background-radius: 15;" +
            "-fx-text-fill: white;" +
            "-fx-prompt-text-fill: rgba(255, 255, 255, 0.5);" +
            "-fx-font-size: 15px;" +
            "-fx-padding: 14 18 14 18;"
        );
        
        field.setOnMouseEntered(e -> field.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.15);" +
            "-fx-border-color: rgba(255, 255, 255, 0.6);" +
            "-fx-border-radius: 15;" +
            "-fx-background-radius: 15;" +
            "-fx-text-fill: white;" +
            "-fx-prompt-text-fill: rgba(255, 255, 255, 0.5);" +
            "-fx-font-size: 15px;" +
            "-fx-padding: 14 18 14 18;"
        ));
        
        field.setOnMouseExited(e -> {
            if (!field.isFocused()) {
                field.setStyle(
                    "-fx-background-color: rgba(255, 255, 255, 0.1);" +
                    "-fx-border-color: rgba(255, 255, 255, 0.3);" +
                    "-fx-border-radius: 15;" +
                    "-fx-background-radius: 15;" +
                    "-fx-text-fill: white;" +
                    "-fx-prompt-text-fill: rgba(255, 255, 255, 0.5);" +
                    "-fx-font-size: 15px;" +
                    "-fx-padding: 14 18 14 18;"
                );
            }
        });
        
        return field;
    }

    private PasswordField createPasswordField(String prompt) {
        PasswordField field = new PasswordField();
        field.setPromptText(prompt);
        field.setPrefHeight(45);
        field.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.1);" +
            "-fx-border-color: rgba(255, 255, 255, 0.3);" +
            "-fx-border-radius: 15;" +
            "-fx-background-radius: 15;" +
            "-fx-text-fill: white;" +
            "-fx-prompt-text-fill: rgba(255, 255, 255, 0.5);" +
            "-fx-font-size: 15px;" +
            "-fx-padding: 14 18 14 18;"
        );
        
        return field;
    }

    private Button createSubmitButton(String text) {
        Button btn = new Button(text);
        btn.setPrefWidth(Double.MAX_VALUE);
        btn.setPrefHeight(50);
        btn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        btn.setTextFill(Color.WHITE);
        btn.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, rgba(255, 255, 255, 0.3), rgba(255, 255, 255, 0.2));" +
            "-fx-background-radius: 15;" +
            "-fx-cursor: hand;"
        );
        
        btn.setOnMouseEntered(e -> {
            btn.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, rgba(255, 255, 255, 0.4), rgba(255, 255, 255, 0.3));" +
                "-fx-background-radius: 15;" +
                "-fx-cursor: hand;"
            );
            btn.setTranslateY(-2);
        });
        
        btn.setOnMouseExited(e -> {
            btn.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, rgba(255, 255, 255, 0.3), rgba(255, 255, 255, 0.2));" +
                "-fx-background-radius: 15;" +
                "-fx-cursor: hand;"
            );
            btn.setTranslateY(0);
        });
        
        return btn;
    }

    public static void main(String[] args) {
        launch(args);
    }
}