package com.studyspace.utils;

import com.studyspace.models.*;
import java.util.*;

//============ algorithm examples =============
//this is where algorithm usage examples and demonstrations are provided

public class AlgorithmUtilsExample {
    
    /**
     * Example: Using AlgorithmUtils for flashcard deck sorting
     */
    public static void demonstrateFlashcardDeckSorting() {
        System.out.println("=== Flashcard Deck Sorting Example ===");
        
        // Create sample flashcard decks
        List<FlashcardDeck> decks = createSampleFlashcardDecks();
        
        System.out.println("Original order:");
        printDeckTitles(decks);
        
        // Sort by title A-Z
        List<FlashcardDeck> sortedByTitle = AlgorithmUtils.sortFlashcardDecks(decks, "Title (A-Z)");
        System.out.println("\nSorted by Title (A-Z):");
        printDeckTitles(sortedByTitle);
        
        // Sort by difficulty
        List<FlashcardDeck> sortedByDifficulty = AlgorithmUtils.sortFlashcardDecks(decks, "Difficulty (Easy to Hard)");
        System.out.println("\nSorted by Difficulty (Easy to Hard):");
        printDeckTitles(sortedByDifficulty);
        
        // Sort by card count
        List<FlashcardDeck> sortedByCardCount = AlgorithmUtils.sortFlashcardDecks(decks, "Card Count (High to Low)");
        System.out.println("\nSorted by Card Count (High to Low):");
        printDeckTitles(sortedByCardCount);
    }
    
    /**
     * Example: Using AlgorithmUtils for searching
     */
    public static void demonstrateSearching() {
        System.out.println("\n=== Searching Example ===");
        
        List<Flashcard> flashcards = createSampleFlashcards();
        
        // Linear search
        Flashcard target = flashcards.get(2);
        int linearIndex = AlgorithmUtils.linearSearch(flashcards, target);
        System.out.println("Linear search for '" + target.getQuestion() + "': Index " + linearIndex);
        
        // Binary search example with integers (since Flashcard doesn't implement Comparable)
        List<Integer> numbers = Arrays.asList(1, 3, 5, 7, 9, 11, 13, 15);
        int targetNumber = 7;
        int binaryIndex = AlgorithmUtils.binarySearch(numbers, targetNumber);
        System.out.println("Binary search for number " + targetNumber + ": Index " + binaryIndex);
    }
    
    /**
     * Example: Using AlgorithmUtils for filtering
     */
    public static void demonstrateFiltering() {
        System.out.println("\n=== Filtering Example ===");
        
        List<Note> notes = createSampleNotes();
        
        // Filter notes by search text
        List<Note> filteredNotes = AlgorithmUtils.filterNotes(notes, "java");
        System.out.println("Notes containing 'java': " + filteredNotes.size());
        for (Note note : filteredNotes) {
            System.out.println("- " + note.getTitle());
        }
    }
    
    /**
     * Example: Using different sorting algorithms
     */
    public static void demonstrateSortingAlgorithms() {
        System.out.println("\n=== Sorting Algorithms Example ===");
        
        List<Integer> numbers = Arrays.asList(64, 34, 25, 12, 22, 11, 90, 5);
        System.out.println("Original: " + numbers);
        
        // Bubble Sort
        List<Integer> bubbleSorted = new ArrayList<>(numbers);
        AlgorithmUtils.bubbleSort(bubbleSorted);
        System.out.println("Bubble Sort: " + bubbleSorted);
        
        // Quick Sort
        List<Integer> quickSorted = new ArrayList<>(numbers);
        AlgorithmUtils.quickSort(quickSorted);
        System.out.println("Quick Sort: " + quickSorted);
        
        // Merge Sort
        List<Integer> mergeSorted = new ArrayList<>(numbers);
        AlgorithmUtils.mergeSort(mergeSorted);
        System.out.println("Merge Sort: " + mergeSorted);
        
        // Heap Sort
        List<Integer> heapSorted = new ArrayList<>(numbers);
        AlgorithmUtils.heapSort(heapSorted);
        System.out.println("Heap Sort: " + heapSorted);
        
        // Smart Sort (chooses best algorithm)
        List<Integer> smartSorted = new ArrayList<>(numbers);
        AlgorithmUtils.smartSort(smartSorted);
        System.out.println("Smart Sort: " + smartSorted);
    }
    
    /**
     * Example: Performance comparison
     */
    public static void demonstratePerformanceComparison() {
        System.out.println("\n=== Performance Comparison ===");
        
        // Create large dataset
        List<Integer> largeList = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 10000; i++) {
            largeList.add(random.nextInt(1000));
        }
        
        // Test different sorting algorithms
        testSortingPerformance("Quick Sort", new ArrayList<>(largeList), AlgorithmUtils::quickSort);
        testSortingPerformance("Merge Sort", new ArrayList<>(largeList), AlgorithmUtils::mergeSort);
        testSortingPerformance("Heap Sort", new ArrayList<>(largeList), AlgorithmUtils::heapSort);
        testSortingPerformance("Smart Sort", new ArrayList<>(largeList), AlgorithmUtils::smartSort);
    }
    
    private static void testSortingPerformance(String algorithmName, List<Integer> list, java.util.function.Consumer<List<Integer>> sortFunction) {
        long startTime = System.currentTimeMillis();
        sortFunction.accept(list);
        long endTime = System.currentTimeMillis();
        
        System.out.println(algorithmName + ": " + (endTime - startTime) + "ms");
    }
    
    // Helper methods to create sample data
    private static List<FlashcardDeck> createSampleFlashcardDecks() {
        List<FlashcardDeck> decks = new ArrayList<>();
        
        FlashcardDeck deck1 = new FlashcardDeck("Java Basics", "Fundamental Java concepts", "Programming", Flashcard.Difficulty.EASY);
        FlashcardDeck deck2 = new FlashcardDeck("Data Structures", "Advanced data structures", "Computer Science", Flashcard.Difficulty.HARD);
        FlashcardDeck deck3 = new FlashcardDeck("Algorithms", "Sorting and searching algorithms", "Computer Science", Flashcard.Difficulty.MEDIUM);
        
        // Add some flashcards to each deck
        deck1.addFlashcard(new Flashcard("What is Java?", "A programming language", Flashcard.Difficulty.EASY));
        deck1.addFlashcard(new Flashcard("What is OOP?", "Object-Oriented Programming", Flashcard.Difficulty.EASY));
        
        deck2.addFlashcard(new Flashcard("What is a binary tree?", "A tree data structure", Flashcard.Difficulty.HARD));
        deck2.addFlashcard(new Flashcard("What is a hash table?", "A data structure for key-value pairs", Flashcard.Difficulty.HARD));
        
        deck3.addFlashcard(new Flashcard("What is quicksort?", "A sorting algorithm", Flashcard.Difficulty.MEDIUM));
        deck3.addFlashcard(new Flashcard("What is binary search?", "A searching algorithm", Flashcard.Difficulty.MEDIUM));
        
        decks.add(deck1);
        decks.add(deck2);
        decks.add(deck3);
        
        return decks;
    }
    
    private static List<Flashcard> createSampleFlashcards() {
        List<Flashcard> flashcards = new ArrayList<>();
        flashcards.add(new Flashcard("What is Java?", "A programming language", Flashcard.Difficulty.EASY));
        flashcards.add(new Flashcard("What is Python?", "A programming language", Flashcard.Difficulty.EASY));
        flashcards.add(new Flashcard("What is C++?", "A programming language", Flashcard.Difficulty.MEDIUM));
        flashcards.add(new Flashcard("What is JavaScript?", "A programming language", Flashcard.Difficulty.MEDIUM));
        return flashcards;
    }
    
    private static List<Note> createSampleNotes() {
        List<Note> notes = new ArrayList<>();
        notes.add(new Note("Java Programming", "Programming", "Java is a versatile programming language..."));
        notes.add(new Note("Python Basics", "Programming", "Python is known for its simplicity..."));
        notes.add(new Note("Data Structures", "Computer Science", "Data structures are ways of organizing data..."));
        notes.add(new Note("Algorithms", "Computer Science", "Algorithms are step-by-step procedures..."));
        return notes;
    }
    
    private static void printDeckTitles(List<FlashcardDeck> decks) {
        for (int i = 0; i < decks.size(); i++) {
            FlashcardDeck deck = decks.get(i);
            System.out.println((i + 1) + ". " + deck.getTitle() + " (" + deck.getDifficulty() + ", " + deck.getCardCount() + " cards)");
        }
    }
    
    /**
     * Main method to run all examples
     */
    public static void main(String[] args) {
        System.out.println("🚀 AlgorithmUtils Examples for Study Space");
        System.out.println("==========================================");
        
        demonstrateFlashcardDeckSorting();
        demonstrateSearching();
        demonstrateFiltering();
        demonstrateSortingAlgorithms();
        demonstratePerformanceComparison();
        
        System.out.println("\n✅ All examples completed successfully!");
    }
}
