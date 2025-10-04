package com.studyspace.views;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;

//============ about us view =============
//this is where application information and team details are displayed

public class AboutUsView {
    
    private VBox mainContainer;
    private static final String GRADIENT_PRIMARY = "-fx-background-color: linear-gradient(to bottom right, #667eea, #764ba2);";
    private static final String GRADIENT_LIGHT = "-fx-background-color: linear-gradient(to bottom right, #f5f7fa, #c3cfe2);";
    
    public AboutUsView() {
        initializeUI();
    }
    
    /**
     * Initializes the UI components
     */
    private void initializeUI() {
        mainContainer = new VBox();
        mainContainer.setSpacing(50);
        mainContainer.setPadding(new Insets(60, 80, 60, 80));
        mainContainer.setStyle("-fx-background-color: linear-gradient(to bottom right, #667eea, #764ba2);");
        
        // Create main card container
        VBox cardContainer = new VBox();
        cardContainer.setSpacing(40);
        cardContainer.setPadding(new Insets(60, 50, 60, 50));
        cardContainer.setStyle("-fx-background-color: rgba(255, 255, 255, 0.95); " +
                              "-fx-background-radius: 30; " +
                              "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 20, 0, 0, 10);");
        cardContainer.setMaxWidth(1200);
        
        // Header section
        VBox headerSection = createHeaderSection();
        
        // Content section
        VBox contentSection = createContentSection();
        
        // Team section
        VBox teamSection = createTeamSection();
        
        // Mission section
        VBox missionSection = createMissionSection();
        
        // Features section
        VBox featuresSection = createFeaturesSection();
        
        // CTA section
        VBox ctaSection = createCTASection();
        
        cardContainer.getChildren().addAll(headerSection, contentSection, teamSection, missionSection, featuresSection, ctaSection);
        
        // Center the card
        HBox centerContainer = new HBox(cardContainer);
        centerContainer.setAlignment(Pos.CENTER);
        
        mainContainer.getChildren().add(centerContainer);
        
        // Add fade-in animation
        addFadeInAnimation(cardContainer);
    }
    
    /**
     * Creates the header section with logo and title
     */
    private VBox createHeaderSection() {
        VBox header = new VBox();
        header.setSpacing(20);
        header.setAlignment(Pos.CENTER);
        
        // University logo
        VBox logoContainer = new VBox();
        logoContainer.setAlignment(Pos.CENTER);
        logoContainer.setSpacing(10);
        
        // Load UM logo
        Image logoImage = new Image(getClass().getResourceAsStream("/images/University_of_Mindanao_Logo.png"));
        ImageView logoView = new ImageView(logoImage);
        logoView.setFitWidth(150);
        logoView.setFitHeight(150);
        logoView.setPreserveRatio(true);
        
        // Add drop shadow effect
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.2));
        shadow.setRadius(20);
        logoView.setEffect(shadow);
        
        logoContainer.getChildren().add(logoView);
        
        // Add hover animation
        addHoverScaleEffect(logoContainer, 1.0, 1.05);
        
        // Title with gradient text effect
        Label titleLabel = new Label("About Us");
        titleLabel.setStyle("-fx-font-size: 56px; " +
                           "-fx-font-weight: bold; " +
                           "-fx-text-fill: #2c3e50;");
        
        // Subtitle
        Label subtitleLabel = new Label("Empowering Students Through Smart Learning");
        subtitleLabel.setStyle("-fx-font-size: 20px; " +
                              "-fx-text-fill: #555; " +
                              "-fx-font-weight: 400;");
        
        header.getChildren().addAll(logoContainer, titleLabel, subtitleLabel);
        
        return header;
    }
    
    /**
     * Creates the content section with description
     */
    private VBox createContentSection() {
        VBox content = new VBox();
        content.setSpacing(25);
        content.setMaxWidth(900);
        content.setAlignment(Pos.CENTER);
        
        Label sentence1 = new Label(
            "We are a team of passionate computer engineering students from the University of Mindanao."
        );
        sentence1.setWrapText(true);
        sentence1.setStyle("-fx-font-size: 18px; " +
                          "-fx-line-spacing: 8px; " +
                          "-fx-text-fill: #333; " +
                          "-fx-text-alignment: center; " +
                          "-fx-font-weight: 400;");
        
        Label sentence2 = new Label(
            "We are dedicated to making studying smarter, easier, and more engaging."
        );
        sentence2.setWrapText(true);
        sentence2.setStyle("-fx-font-size: 18px; " +
                          "-fx-line-spacing: 8px; " +
                          "-fx-text-fill: #333; " +
                          "-fx-text-alignment: center; " +
                          "-fx-font-weight: 400;");
        
        Label sentence3 = new Label(
            "Our project was built with students in mind—designed to help them manage their time, organize their materials, and improve their learning experience."
        );
        sentence3.setWrapText(true);
        sentence3.setStyle("-fx-font-size: 18px; " +
                          "-fx-line-spacing: 8px; " +
                          "-fx-text-fill: #333; " +
                          "-fx-text-alignment: center; " +
                          "-fx-font-weight: 400;");
        
        Label sentence4 = new Label(
            "We believe that learning should be efficient, interactive, and enjoyable."
        );
        sentence4.setWrapText(true);
        sentence4.setStyle("-fx-font-size: 18px; " +
                          "-fx-line-spacing: 8px; " +
                          "-fx-text-fill: #333; " +
                          "-fx-text-alignment: center; " +
                          "-fx-font-weight: 400;");
        
        Label sentence5 = new Label(
            "We're here to support students every step of the way."
        );
        sentence5.setWrapText(true);
        sentence5.setStyle("-fx-font-size: 18px; " +
                          "-fx-line-spacing: 8px; " +
                          "-fx-text-fill: #333; " +
                          "-fx-text-alignment: center; " +
                          "-fx-font-weight: 400;");
        
        content.getChildren().addAll(sentence1, sentence2, sentence3, sentence4, sentence5);
        
        return content;
    }
    
    /**
     * Creates the team section
     */
    private VBox createTeamSection() {
        VBox teamSection = new VBox();
        teamSection.setSpacing(40);
        teamSection.setAlignment(Pos.CENTER);
        
        // Team title
        Label teamTitle = new Label("Meet the Team");
        teamTitle.setStyle("-fx-font-size: 40px; " +
                          "-fx-font-weight: bold; " +
                          "-fx-text-fill: #2c3e50;");
        
        // Team grid
        HBox teamGrid = new HBox();
        teamGrid.setSpacing(30);
        teamGrid.setAlignment(Pos.CENTER);
        
        // Team members
        VBox member1 = createTeamMember("👨‍💻", "Daryl James Padogdog", "Computer Engineering Student");
        VBox member2 = createTeamMember("👩‍💻", "Irish Cassandra Gulen", "Computer Engineering Student");
        VBox member3 = createTeamMember("👩‍💻", "Kristina Cassandra Delos Santos", "Computer Engineering Student");
        
        teamGrid.getChildren().addAll(member1, member2, member3);
        
        teamSection.getChildren().addAll(teamTitle, teamGrid);
        
        return teamSection;
    }
    
    /**
     * Creates a team member card with modern design
     */
    private VBox createTeamMember(String icon, String name, String role) {
        VBox memberCard = new VBox();
        memberCard.setSpacing(15);
        memberCard.setAlignment(Pos.CENTER);
        memberCard.setPadding(new Insets(40, 30, 40, 30));
        memberCard.setMinWidth(250);
        memberCard.setMaxWidth(280);
        memberCard.setStyle(GRADIENT_PRIMARY +
                           "-fx-background-radius: 20; " +
                           "-fx-effect: dropshadow(gaussian, rgba(102, 126, 234, 0.3), 15, 0, 0, 5);");
        
        // Member icon
        Label memberIcon = new Label(icon);
        memberIcon.setStyle("-fx-font-size: 64px;");
        
        // Member name
        Label memberName = new Label(name);
        memberName.setStyle("-fx-font-size: 20px; " +
                           "-fx-font-weight: 600; " +
                           "-fx-text-fill: white;");
        memberName.setWrapText(true);
        memberName.setMaxWidth(240);
        memberName.setAlignment(Pos.CENTER);
        
        // Member role
        Label memberRole = new Label(role);
        memberRole.setStyle("-fx-font-size: 15px; " +
                           "-fx-text-fill: rgba(255, 255, 255, 0.9);");
        memberRole.setWrapText(true);
        memberRole.setMaxWidth(240);
        memberRole.setAlignment(Pos.CENTER);
        
        memberCard.getChildren().addAll(memberIcon, memberName, memberRole);
        
        // Add hover effect
        addHoverScaleEffect(memberCard, 1.0, 1.05);
        
        return memberCard;
    }
    
    /**
     * Creates the mission section with animated background
     */
    private VBox createMissionSection() {
        VBox missionSection = new VBox();
        missionSection.setSpacing(20);
        missionSection.setAlignment(Pos.CENTER);
        missionSection.setPadding(new Insets(40));
        missionSection.setStyle(GRADIENT_PRIMARY +
                               "-fx-background-radius: 20; " +
                               "-fx-effect: dropshadow(gaussian, rgba(102, 126, 234, 0.4), 15, 0, 0, 5);");
        
        Label missionTitle = new Label("Our Mission");
        missionTitle.setStyle("-fx-font-size: 24px; " +
                             "-fx-font-weight: bold; " +
                             "-fx-text-fill: white;");
        
        Label missionText = new Label("To empower students to learn better, stay organized, and achieve more.");
        missionText.setStyle("-fx-font-size: 20px; " +
                            "-fx-font-weight: 500; " +
                            "-fx-text-fill: white;");
        missionText.setWrapText(true);
        missionText.setMaxWidth(700);
        missionText.setAlignment(Pos.CENTER);
        
        missionSection.getChildren().addAll(missionTitle, missionText);
        
        return missionSection;
    }
    
    /**
     * Creates the features section with modern cards
     */
    private VBox createFeaturesSection() {
        VBox featuresSection = new VBox();
        featuresSection.setSpacing(40);
        featuresSection.setAlignment(Pos.CENTER);
        
        // Features grid - 2 rows of 3
        VBox featuresGrid = new VBox();
        featuresGrid.setSpacing(25);
        featuresGrid.setAlignment(Pos.CENTER);
        
        // First row
        HBox row1 = new HBox();
        row1.setSpacing(25);
        row1.setAlignment(Pos.CENTER);
        
        VBox feature1 = createFeatureCard("🃏", "Flashcards", "Quick and effective memorization tools");
        VBox feature2 = createFeatureCard("📝", "Study Notes", "Organized materials at your fingertips");
        VBox feature3 = createFeatureCard("✅", "Quizzes", "Test your knowledge interactively");
        
        row1.getChildren().addAll(feature1, feature2, feature3);
        
        // Second row
        HBox row2 = new HBox();
        row2.setSpacing(25);
        row2.setAlignment(Pos.CENTER);
        
        VBox feature4 = createFeatureCard("📋", "To-Do Lists", "Stay on track with task management");
        VBox feature5 = createFeatureCard("💻", "Coding Practice", "Build your programming skills");
        VBox feature6 = createFeatureCard("📊", "Track Your Progress", "Monitor your growth and achievements");
        
        row2.getChildren().addAll(feature4, feature5, feature6);
        
        featuresGrid.getChildren().addAll(row1, row2);
        featuresSection.getChildren().add(featuresGrid);
        
        return featuresSection;
    }
    
    /**
     * Creates a feature card with modern styling
     */
    private VBox createFeatureCard(String icon, String title, String description) {
        VBox featureCard = new VBox();
        featureCard.setSpacing(15);
        featureCard.setAlignment(Pos.CENTER);
        featureCard.setPadding(new Insets(30));
        featureCard.setMinWidth(220);
        featureCard.setMaxWidth(250);
        featureCard.setMinHeight(180);
        featureCard.setStyle(GRADIENT_LIGHT +
                            "-fx-background-radius: 15; " +
                            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.1), 10, 0, 0, 3);");
        
        // Feature icon
        Label featureIcon = new Label(icon);
        featureIcon.setStyle("-fx-font-size: 48px;");
        
        // Feature title
        Label featureTitle = new Label(title);
        featureTitle.setStyle("-fx-font-size: 18px; " +
                             "-fx-font-weight: bold; " +
                             "-fx-text-fill: #2c3e50;");
        featureTitle.setWrapText(true);
        featureTitle.setMaxWidth(200);
        featureTitle.setAlignment(Pos.CENTER);
        
        // Feature description
        Label featureDescription = new Label(description);
        featureDescription.setStyle("-fx-font-size: 14px; " +
                                   "-fx-text-fill: #444;");
        featureDescription.setWrapText(true);
        featureDescription.setMaxWidth(200);
        featureDescription.setAlignment(Pos.CENTER);
        
        featureCard.getChildren().addAll(featureIcon, featureTitle, featureDescription);
        
        // Add hover effect
        addHoverScaleEffect(featureCard, 1.0, 1.08);
        
        return featureCard;
    }
    
    /**
     * Creates the CTA section with modern button
     */
    private VBox createCTASection() {
        VBox ctaSection = new VBox();
        ctaSection.setSpacing(20);
        ctaSection.setAlignment(Pos.CENTER);
        ctaSection.setPadding(new Insets(20, 0, 0, 0));
        
        Button ctaButton = new Button("Start Learning Today");
        ctaButton.setStyle(GRADIENT_PRIMARY +
                          "-fx-text-fill: white; " +
                          "-fx-font-size: 18px; " +
                          "-fx-font-weight: 600; " +
                          "-fx-padding: 18 45 18 45; " +
                          "-fx-background-radius: 50; " +
                          "-fx-cursor: hand; " +
                          "-fx-effect: dropshadow(gaussian, rgba(102, 126, 234, 0.3), 10, 0, 0, 5);");
        
        ctaButton.setOnAction(e -> {
            System.out.println("Start Learning Today clicked!");
        });
        
        // Add hover effect to button
        addButtonHoverEffect(ctaButton);
        
        ctaSection.getChildren().add(ctaButton);
        
        return ctaSection;
    }
    
    /**
     * Adds a hover scale effect to a node
     */
    private void addHoverScaleEffect(Region node, double normalScale, double hoverScale) {
        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(200), node);
        scaleUp.setToX(hoverScale);
        scaleUp.setToY(hoverScale);
        
        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(200), node);
        scaleDown.setToX(normalScale);
        scaleDown.setToY(normalScale);
        
        node.setOnMouseEntered(e -> scaleUp.playFromStart());
        node.setOnMouseExited(e -> scaleDown.playFromStart());
    }
    
    /**
     * Adds hover effect to button
     */
    private void addButtonHoverEffect(Button button) {
        String normalStyle = button.getStyle();
        String hoverStyle = normalStyle + "-fx-effect: dropshadow(gaussian, rgba(102, 126, 234, 0.5), 15, 0, 0, 8);";
        
        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(200), button);
        scaleUp.setToX(1.03);
        scaleUp.setToY(1.03);
        
        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(200), button);
        scaleDown.setToX(1.0);
        scaleDown.setToY(1.0);
        
        button.setOnMouseEntered(e -> {
            button.setStyle(hoverStyle);
            scaleUp.playFromStart();
        });
        
        button.setOnMouseExited(e -> {
            button.setStyle(normalStyle);
            scaleDown.playFromStart();
        });
    }
    
    /**
     * Adds fade-in animation to the card
     */
    private void addFadeInAnimation(VBox card) {
        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), card);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        
        TranslateTransition slideUp = new TranslateTransition(Duration.millis(800), card);
        slideUp.setFromY(30);
        slideUp.setToY(0);
        
        ParallelTransition animation = new ParallelTransition(fadeIn, slideUp);
        animation.play();
    }
    
    /**
     * Gets the main view container
     */
    public VBox getView() {
        ScrollPane scrollPane = new ScrollPane(mainContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background: linear-gradient(to bottom right, #667eea, #764ba2); " +
                           "-fx-background-color: transparent;");
        scrollPane.setPadding(new Insets(0));
        
        VBox scrollContainer = new VBox();
        scrollContainer.getChildren().add(scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        
        return scrollContainer;
    }
}