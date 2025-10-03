package com.studyspace.utils;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.InputStream;

/**
 * Utility class for handling icons and creating icon-based UI elements
 * Uses Unicode symbols and emoji that JavaFX can display properly
 */
public class IconUtils {
    
    // Icon mapping to Unicode symbols and PNG files
    private static final String[][] ICON_MAP = {
        // Icons with PNG files (your custom images)
        {"folder", "📂", "folder.png"},
        {"eye", "👁️", "eye.png"},
        {"eye-off", "🙈", "eye-crossed.png"},
        {"add", "➕", "add.png"},
        {"edit", "✏️", "edit.png"},
        {"trash", "🗑️", "trash.png"},
        {"bookmark", "🔖", "bookmark.png"},
        {"settings", "⚙️", "icons8-setting-50.png"},
        {"upload", "📤", "icons8-upload-64.png"},

        // Icons without PNG files (Unicode fallback only)
        {"question", "❓", null},
        {"check", "✅", null},
        {"x", "❌", null},
        {"clock", "⏰", null},
        {"star", "⭐", null},
        {"hand-clap", "👏", null},
        {"thumbs-up", "👍", null},
        {"book", "📚", null},
        {"trending-up", "📈", null},
        {"party", "🎉", null},
        {"home", "🏠", null},
        {"note", "📝", null},
        {"cards", "🃏", null},
        {"code", "💻", null},
        {"fire", "🔥", null},
        {"user", "👤", null},
        {"mail", "✉️", null},
        {"logout", "🚪", null},
        {"arrow-left", "⬅️", null},
        {"arrow-right", "➡️", null},
        {"search", "🔍", null},
        {"filter", "🔎", null},
        {"sort", "↕️", null},
        {"calendar", "📅", null}
    };
    
    /**
     * Gets the Unicode symbol for an icon name
     */
    public static String getIconSymbol(String iconName) {
        for (String[] mapping : ICON_MAP) {
            if (mapping[0].equals(iconName)) {
                return mapping[1];
            }
        }
        return "❓"; // Default fallback
    }
    
    /**
     * Gets the PNG file name for an icon name
     */
    private static String getIconFileName(String iconName) {
        for (String[] mapping : ICON_MAP) {
            if (mapping[0].equals(iconName)) {
                return mapping[2]; // Returns null if no PNG file exists
            }
        }
        return null; // No icon found
    }
    
    /**
     * Checks if an icon has a PNG file
     */
    private static boolean hasPngFile(String iconName) {
        String fileName = getIconFileName(iconName);
        return fileName != null;
    }
    
    /**
     * Loads an image from the resources
     */
    private static Image loadImage(String fileName) {
        try {
            return new Image(IconUtils.class.getResourceAsStream("/images/" + fileName));
        } catch (Exception e) {
            System.out.println("Failed to load image: " + fileName);
            return null;
        }
    }
    
    /**
     * Creates a symbol label with proper font styling
     */
    private static Label createSymbolLabel(String iconName, double fontSize) {
        Label label = new Label(getIconSymbol(iconName));
        label.setStyle("-fx-font-size: " + fontSize + "px; -fx-font-family: 'Segoe UI Emoji', 'Apple Color Emoji', 'Noto Color Emoji', sans-serif;");
        return label;
    }
    
    /**
     * Creates a label with an icon symbol
     */
    public static Label createIconLabel(String iconName, String text) {
        Label label = new Label();
        label.setText(getIconSymbol(iconName));
        if (text != null && !text.isEmpty()) {
            label.setText(getIconSymbol(iconName) + " " + text);
        }
        return label;
    }
    
    /**
     * Creates an ImageView with an icon symbol (as a label)
     */
    public static javafx.scene.Node createIconView(String iconName) {
        if (hasPngFile(iconName)) {
            String fileName = getIconFileName(iconName);
            Image image = loadImage(fileName);
            if (image != null) {
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(18);
                imageView.setFitHeight(18);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
                return imageView;
            }
        }
        return createSymbolLabel(iconName, 18);
    }
    
    /**
     * Creates a label with an icon and text, arranged horizontally
     */
    public static HBox createIconTextHBox(String iconName, String text) {
        HBox container = new HBox();
        container.setSpacing(4);
        container.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Label iconLabel = new Label(getIconSymbol(iconName));
        iconLabel.setStyle("-fx-font-size: 16px;");
        Label textLabel = new Label(text);
        
        container.getChildren().addAll(iconLabel, textLabel);
        return container;
    }
    
    /**
     * Creates a label with an icon and text, arranged vertically
     */
    public static VBox createIconTextVBox(String iconName, String text) {
        VBox container = new VBox();
        container.setSpacing(4);
        container.setAlignment(javafx.geometry.Pos.CENTER);
        
        Label iconLabel = new Label(getIconSymbol(iconName));
        iconLabel.setStyle("-fx-font-size: 16px;");
        Label textLabel = new Label(text);
        
        container.getChildren().addAll(iconLabel, textLabel);
        return container;
    }
    
    /**
     * Creates a large icon view for headers and important elements
     */
    public static Label createLargeIconView(String iconName) {
        Label iconLabel = new Label(getIconSymbol(iconName));
        iconLabel.setStyle("-fx-font-size: 24px;");
        return iconLabel;
    }
    
    /**
     * Creates a small icon view for inline elements
     */
    public static javafx.scene.Node createSmallIconView(String iconName) {
        if (hasPngFile(iconName)) {
            String fileName = getIconFileName(iconName);
            Image image = loadImage(fileName);
            if (image != null) {
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(16);
                imageView.setFitHeight(16);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
                return imageView;
            }
        }
        return createSymbolLabel(iconName, 16);
    }
    
    /**
     * Creates a medium icon view for buttons and navigation
     */
    public static javafx.scene.Node createMediumIconView(String iconName) {
        if (hasPngFile(iconName)) {
            String fileName = getIconFileName(iconName);
            Image image = loadImage(fileName);
            if (image != null) {
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(20);
                imageView.setFitHeight(20);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
                return imageView;
            }
        }
        return createSymbolLabel(iconName, 20);
    }
    
    /**
     * Creates a logo image view with proper fallback system
     */
    public static ImageView createLogoView() {
        ImageView logoView = new ImageView();
        logoView.setFitWidth(32);
        logoView.setFitHeight(32);
        logoView.setPreserveRatio(true);
        logoView.setSmooth(true);
        
        try {
            // Try to load logo.png first
            Image logoImage = new Image(IconUtils.class.getResourceAsStream("/images/logo.png"));
            if (logoImage != null && !logoImage.isError()) {
                logoView.setImage(logoImage);
                return logoView;
            }
        } catch (Exception e) {
            System.out.println("Logo.png not found, trying fallback...");
        }
        
        try {
            // Fallback to bookmark.png
            Image bookmarkImage = new Image(IconUtils.class.getResourceAsStream("/images/bookmark.png"));
            if (bookmarkImage != null && !bookmarkImage.isError()) {
                logoView.setImage(bookmarkImage);
                return logoView;
            }
        } catch (Exception e) {
            System.out.println("Bookmark.png not found, trying final fallback...");
        }
        
        try {
            // Final fallback to folder.png
            Image fallbackImage = new Image(IconUtils.class.getResourceAsStream("/images/folder.png"));
            if (fallbackImage != null && !fallbackImage.isError()) {
                logoView.setImage(fallbackImage);
                return logoView;
            }
        } catch (Exception e) {
            System.out.println("No images found, using text fallback");
        }
        
        // Ultimate fallback - return null to indicate fallback needed
        return null;
    }
}
