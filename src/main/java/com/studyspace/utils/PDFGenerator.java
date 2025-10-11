package com.studyspace.utils;

import com.studyspace.models.Flashcard;
import com.studyspace.models.FlashcardDeck;
import com.studyspace.models.Note;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * PDFGenerator - Utility class for generating well-structured PDF files
 * from flashcards and notes for download functionality
 */
public class PDFGenerator {
    
    // PDF constants
    private static final float MARGIN = 50;
    private static final float PAGE_WIDTH = PDRectangle.A4.getWidth();
    private static final float PAGE_HEIGHT = PDRectangle.A4.getHeight();
    private static final float CONTENT_WIDTH = PAGE_WIDTH - (2 * MARGIN);
    
    // Font sizes
    private static final int TITLE_FONT_SIZE = 18;
    private static final int HEADING_FONT_SIZE = 14;
    private static final int BODY_FONT_SIZE = 12;
    private static final int SMALL_FONT_SIZE = 10;
    
    // Line spacing
    private static final float TITLE_LINE_SPACING = 20;
    private static final float HEADING_LINE_SPACING = 16;
    private static final float BODY_LINE_SPACING = 14;
    private static final float SMALL_LINE_SPACING = 12;
    
    /**
     * Helper class to manage PDF pages and content streams
     */
    private static class PDFPageManager {
        private final PDDocument document;
        private PDPage currentPage;
        private PDPageContentStream currentStream;
        private float yPosition;
        private int currentPageNumber;
        private int totalPages;
        
        public PDFPageManager(PDDocument document) throws IOException {
            if (document == null) {
                throw new IllegalArgumentException("Document cannot be null");
            }
            this.document = document;
            this.currentPageNumber = 1;
            this.totalPages = 1; // Will be updated as pages are added
            addNewPage();
        }
        
        public void addNewPage() throws IOException {
            if (currentStream != null) {
                try {
                    currentStream.close();
                } catch (IOException e) {
                    System.err.println("Warning: Error closing previous content stream: " + e.getMessage());
                }
            }
            
            currentPage = new PDPage(PDRectangle.A4);
            document.addPage(currentPage);
            currentStream = new PDPageContentStream(document, currentPage);
            yPosition = PAGE_HEIGHT - MARGIN;
            currentPageNumber++;
            totalPages = currentPageNumber;
        }
        
        public void checkAndCreateNewPage(float requiredSpace) throws IOException {
            if (yPosition - requiredSpace < MARGIN + 100) {
                addNewPage();
            }
        }
        
        public float getCurrentYPosition() {
            return yPosition;
        }
        
        public void setYPosition(float yPosition) {
            this.yPosition = yPosition;
        }
        
        public PDPageContentStream getCurrentStream() {
            return currentStream;
        }
        
        public int getCurrentPageNumber() {
            return currentPageNumber;
        }
        
        public int getTotalPages() {
            return totalPages;
        }
        
        public void close() throws IOException {
            if (currentStream != null) {
                try {
                    currentStream.close();
                } catch (IOException e) {
                    System.err.println("Warning: Error closing content stream: " + e.getMessage());
                    throw e;
                }
            }
        }
    }
    
    /**
     * Generates a PDF file for a flashcard deck
     */
    public static boolean generateFlashcardDeckPDF(FlashcardDeck deck, Stage primaryStage) {
        try {
            // Validate input
            if (deck == null) {
                System.err.println("Error: FlashcardDeck is null");
                return false;
            }
            
            if (deck.getTitle() == null || deck.getTitle().trim().isEmpty()) {
                System.err.println("Error: FlashcardDeck title is null or empty");
                return false;
            }
            
            if (deck.getFlashcards() == null || deck.getFlashcards().isEmpty()) {
                System.err.println("Error: FlashcardDeck has no flashcards");
                return false;
            }
            
            // Show file chooser dialog
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Flashcard Deck as PDF");
            fileChooser.setInitialFileName(sanitizeFileName(deck.getTitle()) + "_flashcards.pdf");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
            );
            
            File selectedFile = fileChooser.showSaveDialog(primaryStage);
            if (selectedFile == null) {
                return false; // User cancelled
            }
            
            // Create PDF document
            try (PDDocument document = new PDDocument()) {
                PDFPageManager pageManager = new PDFPageManager(document);
                
                try {
                    // Add title
                    pageManager.checkAndCreateNewPage(50);
                    float yPosition = addTitle(pageManager.getCurrentStream(), deck.getTitle(), pageManager.getCurrentYPosition());
                    pageManager.setYPosition(yPosition - 20); // Extra space after title
                    
                    // Add deck information
                    pageManager.checkAndCreateNewPage(100);
                    yPosition = addDeckInfo(pageManager.getCurrentStream(), deck, pageManager.getCurrentYPosition());
                    pageManager.setYPosition(yPosition - 20); // Extra space after info
                    
                    // Add flashcards
                    List<Flashcard> flashcards = deck.getFlashcards();
                    for (int i = 0; i < flashcards.size(); i++) {
                        Flashcard card = flashcards.get(i);
                        if (card != null) {
                            pageManager.checkAndCreateNewPage(150); // Space needed for a flashcard
                            yPosition = addFlashcard(pageManager.getCurrentStream(), card, i + 1, pageManager.getCurrentYPosition());
                            pageManager.setYPosition(yPosition - 20); // Space between cards
                        }
                    }
                    
                    // Add footer to last page
                    addFooter(pageManager.getCurrentStream(), "Generated by Study Space on " + formatDate(LocalDateTime.now()));
                    
                } finally {
                    pageManager.close();
                }
                
                // Save the document
                document.save(selectedFile);
            }
            
            return true;
            
        } catch (IOException e) {
            System.err.println("Error generating flashcard PDF: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Generates a PDF file for a single note
     */
    public static boolean generateNotePDF(Note note, Stage primaryStage) {
        try {
            // Show file chooser dialog
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Note as PDF");
            fileChooser.setInitialFileName(sanitizeFileName(note.getTitle()) + "_note.pdf");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
            );
            
            File selectedFile = fileChooser.showSaveDialog(primaryStage);
            if (selectedFile == null) {
                return false; // User cancelled
            }
            
            // Create PDF document
            try (PDDocument document = new PDDocument()) {
                PDFPageManager pageManager = new PDFPageManager(document);
                
                try {
                    // Add title
                    pageManager.checkAndCreateNewPage(50);
                    float yPosition = addTitle(pageManager.getCurrentStream(), note.getTitle(), pageManager.getCurrentYPosition());
                    pageManager.setYPosition(yPosition - 20); // Extra space after title
                    
                    // Add note information
                    pageManager.checkAndCreateNewPage(100);
                    yPosition = addNoteInfo(pageManager.getCurrentStream(), note, pageManager.getCurrentYPosition());
                    pageManager.setYPosition(yPosition - 20); // Extra space after info
                    
                    // Add content
                    pageManager.checkAndCreateNewPage(100);
                    yPosition = addNoteContent(pageManager.getCurrentStream(), note.getContent(), pageManager.getCurrentYPosition());
                    pageManager.setYPosition(yPosition);
                    
                    // Add footer to last page
                    addFooter(pageManager.getCurrentStream(), "Generated by Study Space on " + formatDate(LocalDateTime.now()));
                    
                } finally {
                    pageManager.close();
                }
                
                // Save the document
                document.save(selectedFile);
            }
            
            return true;
            
        } catch (IOException e) {
            System.err.println("Error generating note PDF: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Generates a PDF file for multiple notes
     */
    public static boolean generateNotesCollectionPDF(List<Note> notes, String collectionTitle, Stage primaryStage) {
        try {
            // Show file chooser dialog
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Notes Collection as PDF");
            fileChooser.setInitialFileName(sanitizeFileName(collectionTitle) + "_notes.pdf");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
            );
            
            File selectedFile = fileChooser.showSaveDialog(primaryStage);
            if (selectedFile == null) {
                return false; // User cancelled
            }
            
            // Create PDF document
            try (PDDocument document = new PDDocument()) {
                PDFPageManager pageManager = new PDFPageManager(document);
                
                try {
                    // Add title
                    pageManager.checkAndCreateNewPage(50);
                    float yPosition = addTitle(pageManager.getCurrentStream(), collectionTitle, pageManager.getCurrentYPosition());
                    pageManager.setYPosition(yPosition - 10);
                    
                    pageManager.checkAndCreateNewPage(30);
                    yPosition = addText(pageManager.getCurrentStream(), "Notes Collection - " + notes.size() + " notes", 
                                      MARGIN, pageManager.getCurrentYPosition(), BODY_FONT_SIZE, false);
                    pageManager.setYPosition(yPosition - 30); // Extra space
                    
                    // Add table of contents
                    pageManager.checkAndCreateNewPage(50);
                    yPosition = addHeading(pageManager.getCurrentStream(), "Table of Contents", pageManager.getCurrentYPosition());
                    pageManager.setYPosition(yPosition);
                    
                    for (int i = 0; i < notes.size(); i++) {
                        Note note = notes.get(i);
                        pageManager.checkAndCreateNewPage(20);
                        yPosition = addText(pageManager.getCurrentStream(), (i + 1) + ". " + note.getTitle(), 
                                          MARGIN + 20, pageManager.getCurrentYPosition(), BODY_FONT_SIZE, false);
                        pageManager.setYPosition(yPosition);
                    }
                    pageManager.setYPosition(pageManager.getCurrentYPosition() - 30); // Extra space
                    
                    // Add each note
                    for (int i = 0; i < notes.size(); i++) {
                        Note note = notes.get(i);
                        pageManager.checkAndCreateNewPage(50);
                        yPosition = addHeading(pageManager.getCurrentStream(), (i + 1) + ". " + note.getTitle(), pageManager.getCurrentYPosition());
                        pageManager.setYPosition(yPosition);
                        
                        pageManager.checkAndCreateNewPage(100);
                        yPosition = addNoteInfo(pageManager.getCurrentStream(), note, pageManager.getCurrentYPosition());
                        pageManager.setYPosition(yPosition);
                        
                        pageManager.checkAndCreateNewPage(100);
                        yPosition = addNoteContent(pageManager.getCurrentStream(), note.getContent(), pageManager.getCurrentYPosition());
                        pageManager.setYPosition(yPosition - 20); // Space between notes
                    }
                    
                    // Add footer to last page
                    addFooter(pageManager.getCurrentStream(), "Generated by Study Space on " + formatDate(LocalDateTime.now()));
                    
                } finally {
                    pageManager.close();
                }
                
                // Save the document
                document.save(selectedFile);
            }
            
            return true;
            
        } catch (IOException e) {
            System.err.println("Error generating notes collection PDF: " + e.getMessage());
            return false;
        }
    }
    
    // Helper methods for PDF generation
    
    /**
     * Adds a title to the PDF
     */
    private static float addTitle(PDPageContentStream contentStream, String title, float yPosition) throws IOException {
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), TITLE_FONT_SIZE);
        contentStream.beginText();
        contentStream.newLineAtOffset(MARGIN, yPosition);
        contentStream.showText(title);
        contentStream.endText();
        return yPosition - TITLE_LINE_SPACING;
    }
    
    /**
     * Adds a heading to the PDF
     */
    private static float addHeading(PDPageContentStream contentStream, String heading, float yPosition) throws IOException {
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), HEADING_FONT_SIZE);
        contentStream.beginText();
        contentStream.newLineAtOffset(MARGIN, yPosition);
        contentStream.showText(heading);
        contentStream.endText();
        return yPosition - HEADING_LINE_SPACING;
    }
    
    /**
     * Adds text to the PDF with word wrapping
     */
    private static float addText(PDPageContentStream contentStream, String text, float x, float y, 
                                int fontSize, boolean bold) throws IOException {
        if (text == null || text.trim().isEmpty()) {
            return y - fontSize;
        }
        
        // Clean the text to remove problematic characters
        String cleanText = cleanTextForPDF(text);
        if (cleanText.trim().isEmpty()) {
            return y - fontSize;
        }
        
        contentStream.setFont(new PDType1Font(bold ? Standard14Fonts.FontName.HELVETICA_BOLD : Standard14Fonts.FontName.HELVETICA), fontSize);
        
        // Split by both spaces and newlines
        String[] words = cleanText.split("\\s+");
        StringBuilder line = new StringBuilder();
        float currentY = y;
        
        for (String word : words) {
            if (word.trim().isEmpty()) continue;
            
            String testLine = line.length() == 0 ? word : line + " " + word;
            // More accurate text width estimation
            float textWidth = testLine.length() * fontSize * 0.5f;
            
            if (textWidth > CONTENT_WIDTH && line.length() > 0) {
                // Draw the current line
                try {
                    contentStream.beginText();
                    contentStream.newLineAtOffset(x, currentY);
                    contentStream.showText(line.toString());
                    contentStream.endText();
                } catch (Exception e) {
                    // If text rendering fails, try with cleaned text
                    contentStream.endText();
                    contentStream.beginText();
                    contentStream.newLineAtOffset(x, currentY);
                    contentStream.showText(cleanTextForPDF(line.toString()));
                    contentStream.endText();
                }
                
                // Start a new line
                line = new StringBuilder(word);
                currentY -= fontSize + 2;
            } else {
                line = new StringBuilder(testLine);
            }
        }
        
        // Draw the last line
        if (line.length() > 0) {
            try {
                contentStream.beginText();
                contentStream.newLineAtOffset(x, currentY);
                contentStream.showText(line.toString());
                contentStream.endText();
            } catch (Exception e) {
                // If text rendering fails, try with cleaned text
                contentStream.endText();
                contentStream.beginText();
                contentStream.newLineAtOffset(x, currentY);
                contentStream.showText(cleanTextForPDF(line.toString()));
                contentStream.endText();
            }
        }
        
        return currentY - fontSize;
    }
    
    /**
     * Adds deck information to the PDF
     */
    private static float addDeckInfo(PDPageContentStream contentStream, FlashcardDeck deck, float yPosition) throws IOException {
        yPosition = addText(contentStream, "Subject: " + deck.getSubject(), MARGIN, yPosition, BODY_FONT_SIZE, true);
        yPosition = addText(contentStream, "Difficulty: " + deck.getDifficulty().getDisplayName(), MARGIN, yPosition, BODY_FONT_SIZE, true);
        yPosition = addText(contentStream, "Total Cards: " + deck.getCardCount(), MARGIN, yPosition, BODY_FONT_SIZE, true);
        yPosition = addText(contentStream, "Created: " + formatDate(deck.getCreatedAt()), MARGIN, yPosition, BODY_FONT_SIZE, true);
        
        if (deck.getDescription() != null && !deck.getDescription().trim().isEmpty()) {
            yPosition = addText(contentStream, "Description: " + deck.getDescription(), MARGIN, yPosition, BODY_FONT_SIZE, true);
        }
        
        return yPosition;
    }
    
    /**
     * Adds note information to the PDF
     */
    private static float addNoteInfo(PDPageContentStream contentStream, Note note, float yPosition) throws IOException {
        yPosition = addText(contentStream, "Subject: " + note.getSubject(), MARGIN, yPosition, BODY_FONT_SIZE, true);
        yPosition = addText(contentStream, "Created: " + formatDate(note.getCreatedAt()), MARGIN, yPosition, BODY_FONT_SIZE, true);
        yPosition = addText(contentStream, "Last Modified: " + formatDate(note.getLastModified()), MARGIN, yPosition, BODY_FONT_SIZE, true);
        
        if (note.isPinned()) {
            yPosition = addText(contentStream, "Status: Pinned", MARGIN, yPosition, BODY_FONT_SIZE, true);
        }
        
        return yPosition;
    }
    
    /**
     * Adds a flashcard to the PDF
     */
    private static float addFlashcard(PDPageContentStream contentStream, Flashcard card, int cardNumber, float yPosition) throws IOException {
        // Card header
        String difficultyText = (card.getDifficulty() != null) ? card.getDifficulty().getDisplayName() : "Unknown";
        yPosition = addText(contentStream, "Card " + cardNumber + " - " + difficultyText, 
                           MARGIN, yPosition, HEADING_FONT_SIZE, true);
        
        // Question
        yPosition = addText(contentStream, "Question:", MARGIN, yPosition, BODY_FONT_SIZE, true);
        String question = (card.getQuestion() != null) ? card.getQuestion() : "[No question provided]";
        yPosition = addText(contentStream, question, MARGIN + 20, yPosition, BODY_FONT_SIZE, false);
        
        // Answer
        yPosition = addText(contentStream, "Answer:", MARGIN, yPosition, BODY_FONT_SIZE, true);
        String answer = (card.getAnswer() != null) ? card.getAnswer() : "[No answer provided]";
        yPosition = addText(contentStream, answer, MARGIN + 20, yPosition, BODY_FONT_SIZE, false);
        
        return yPosition;
    }
    
    /**
     * Adds note content to the PDF
     */
    private static float addNoteContent(PDPageContentStream contentStream, String content, float yPosition) throws IOException {
        yPosition = addText(contentStream, "Content:", MARGIN, yPosition, HEADING_FONT_SIZE, true);
        
        // Split content into paragraphs and handle them properly
        String[] paragraphs = content.split("\n\n");
        for (String paragraph : paragraphs) {
            if (!paragraph.trim().isEmpty()) {
                // Clean the paragraph text
                String cleanParagraph = cleanTextForPDF(paragraph.trim());
                if (!cleanParagraph.isEmpty()) {
                    yPosition = addText(contentStream, cleanParagraph, MARGIN + 20, yPosition, BODY_FONT_SIZE, false);
                    yPosition -= 5; // Extra space between paragraphs
                }
            }
        }
        
        return yPosition;
    }
    
    /**
     * Adds a footer to the PDF
     */
    private static void addFooter(PDPageContentStream contentStream, String footerText) throws IOException {
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), SMALL_FONT_SIZE);
        contentStream.beginText();
        contentStream.newLineAtOffset(MARGIN, MARGIN);
        contentStream.showText(footerText);
        contentStream.endText();
    }
    
    /**
     * Sanitizes filename by removing invalid characters
     */
    private static String sanitizeFileName(String fileName) {
        return fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
    
    /**
     * Formats date for display
     */
    private static String formatDate(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' HH:mm");
        return dateTime.format(formatter);
    }
    
    /**
     * Cleans text for PDF generation by removing problematic characters
     */
    private static String cleanTextForPDF(String text) {
        if (text == null) return "";
        
        // Remove or replace problematic characters
        return text
            .replace("\n", " ")           // Replace newlines with spaces
            .replace("\r", " ")           // Replace carriage returns with spaces
            .replace("\t", " ")           // Replace tabs with spaces
            .replaceAll("\\p{Cntrl}", " ") // Remove all control characters
            .replaceAll("[\\x00-\\x1F\\x7F-\\x9F]", " ") // Remove additional control characters
            .replaceAll("[^\\x20-\\x7E]", "?") // Replace non-ASCII characters with ?
            .replaceAll("\\s+", " ")      // Replace multiple spaces with single space
            .trim();                      // Remove leading/trailing whitespace
    }
}
