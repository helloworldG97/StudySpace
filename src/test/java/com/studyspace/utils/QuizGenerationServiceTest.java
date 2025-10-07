package com.studyspace.utils;

import com.studyspace.models.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Test class for QuizGenerationService
 */
public class QuizGenerationServiceTest {
    
    private QuizGenerationService quizGenerationService;
    
    @BeforeEach
    void setUp() {
        quizGenerationService = new QuizGenerationService();
    }
    
    @Test
    void testQuizGenerationServiceInitialization() {
        assertNotNull(quizGenerationService, "QuizGenerationService should be initialized");
    }
    
    @Test
    void testGenerateQuizFromFlashcardsWithEmptyList() {
        List<FlashcardDeck> emptyDecks = new ArrayList<>();
        
        QuizGenerationService.QuizGenerationResult result = quizGenerationService.generateQuizFromFlashcards(
            emptyDecks, "Test Quiz", "Test Subject", Flashcard.Difficulty.MEDIUM, 20, 10
        );
        
        assertFalse(result.isSuccess(), "Should fail with empty flashcard list");
        assertNotNull(result.getMessage(), "Should provide error message");
        assertNull(result.getQuiz(), "Should not return a quiz");
    }
    
    @Test
    void testGenerateQuizFromNotesWithEmptyList() {
        List<Note> emptyNotes = new ArrayList<>();
        
        QuizGenerationService.QuizGenerationResult result = quizGenerationService.generateQuizFromNotes(
            emptyNotes, "Test Quiz", "Test Subject", Flashcard.Difficulty.MEDIUM, 20, 10
        );
        
        assertFalse(result.isSuccess(), "Should fail with empty notes list");
        assertNotNull(result.getMessage(), "Should provide error message");
        assertNull(result.getQuiz(), "Should not return a quiz");
    }
    
    @Test
    void testGenerateQuizFromFlashcardsWithValidData() {
        // Create test flashcard deck
        FlashcardDeck deck = new FlashcardDeck("Test Deck", "Test Description", "Test Subject", Flashcard.Difficulty.MEDIUM);
        
        // Add test flashcards
        Flashcard card1 = new Flashcard("What is Java?", "A programming language", Flashcard.Difficulty.EASY);
        Flashcard card2 = new Flashcard("What is OOP?", "Object-Oriented Programming", Flashcard.Difficulty.MEDIUM);
        deck.addFlashcard(card1);
        deck.addFlashcard(card2);
        
        List<FlashcardDeck> decks = new ArrayList<>();
        decks.add(deck);
        
        QuizGenerationService.QuizGenerationResult result = quizGenerationService.generateQuizFromFlashcards(
            decks, "Test Quiz", "Programming", Flashcard.Difficulty.MEDIUM, 20, 5
        );
        
        // Note: This test will likely fail if AI service is not running
        // That's expected behavior - the service should handle this gracefully
        if (!result.isSuccess()) {
            assertNotNull(result.getMessage(), "Should provide error message when AI service is unavailable");
            assertTrue(result.getMessage().contains("AI service") || result.getMessage().contains("Error"), 
                      "Error message should indicate AI service issue");
        } else {
            assertNotNull(result.getQuiz(), "Should return a quiz when successful");
            assertTrue(result.getQuiz().getQuestionCount() > 0, "Quiz should have questions");
        }
    }
    
    @Test
    void testGenerateQuizFromNotesWithValidData() {
        // Create test notes
        Note note1 = new Note("Java Basics", "Java is a programming language", "Programming");
        Note note2 = new Note("OOP Concepts", "Object-Oriented Programming uses classes and objects", "Programming");
        
        List<Note> notes = new ArrayList<>();
        notes.add(note1);
        notes.add(note2);
        
        QuizGenerationService.QuizGenerationResult result = quizGenerationService.generateQuizFromNotes(
            notes, "Test Quiz", "Programming", Flashcard.Difficulty.MEDIUM, 20, 5
        );
        
        // Note: This test will likely fail if AI service is not running
        // That's expected behavior - the service should handle this gracefully
        if (!result.isSuccess()) {
            assertNotNull(result.getMessage(), "Should provide error message when AI service is unavailable");
            assertTrue(result.getMessage().contains("AI service") || result.getMessage().contains("Error"), 
                      "Error message should indicate AI service issue");
        } else {
            assertNotNull(result.getQuiz(), "Should return a quiz when successful");
            assertTrue(result.getQuiz().getQuestionCount() > 0, "Quiz should have questions");
        }
    }
    
    @Test
    void testQuizGenerationResultStructure() {
        // Test the result class structure
        Quiz quiz = new Quiz("Test Quiz", "Test Description", "Test Subject", Flashcard.Difficulty.MEDIUM, 20);
        
        QuizGenerationService.QuizGenerationResult successResult = new QuizGenerationService.QuizGenerationResult(
            true, "Success", quiz
        );
        
        assertTrue(successResult.isSuccess());
        assertEquals("Success", successResult.getMessage());
        assertEquals(quiz, successResult.getQuiz());
        
        QuizGenerationService.QuizGenerationResult failureResult = new QuizGenerationService.QuizGenerationResult(
            false, "Failed", null
        );
        
        assertFalse(failureResult.isSuccess());
        assertEquals("Failed", failureResult.getMessage());
        assertNull(failureResult.getQuiz());
    }
}
