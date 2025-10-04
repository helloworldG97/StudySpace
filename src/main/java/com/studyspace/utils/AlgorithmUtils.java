package com.studyspace.utils;

import com.studyspace.models.*;
import java.util.*;
import java.util.stream.Collectors;

//============ algorithm utilities =============
//this is where sorting, searching, and data processing algorithms are implemented

public class AlgorithmUtils {
    
    // ===================================================================
    // SORTING ALGORITHMS
    // ===================================================================
    
    /**
     * Bubble Sort - Simple but inefficient O(n²) algorithm
     * Best for: Small datasets, educational purposes
     */
    public static <T extends Comparable<T>> void bubbleSort(List<T> list) {
        if (list == null || list.size() <= 1) return;
        
        int n = list.size();
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                if (list.get(j).compareTo(list.get(j + 1)) > 0) {
                    Collections.swap(list, j, j + 1);
                    swapped = true;
                }
            }
            if (!swapped) break; // Optimization: early termination
        }
    }
    
    /**
     * Quick Sort - Efficient O(n log n) average case algorithm
     * Best for: General purpose sorting, large datasets
     */
    public static <T extends Comparable<T>> void quickSort(List<T> list) {
        if (list == null || list.size() <= 1) return;
        quickSortHelper(list, 0, list.size() - 1);
    }
    
    private static <T extends Comparable<T>> void quickSortHelper(List<T> list, int low, int high) {
        if (low < high) {
            int pivotIndex = partition(list, low, high);
            quickSortHelper(list, low, pivotIndex - 1);
            quickSortHelper(list, pivotIndex + 1, high);
        }
    }
    
    private static <T extends Comparable<T>> int partition(List<T> list, int low, int high) {
        T pivot = list.get(high);
        int i = low - 1;
        
        for (int j = low; j < high; j++) {
            if (list.get(j).compareTo(pivot) <= 0) {
                i++;
                Collections.swap(list, i, j);
            }
        }
        Collections.swap(list, i + 1, high);
        return i + 1;
    }
    
    /**
     * Merge Sort - Stable O(n log n) algorithm
     * Best for: When stability is required, large datasets
     */
    public static <T extends Comparable<T>> void mergeSort(List<T> list) {
        if (list == null || list.size() <= 1) return;
        mergeSortHelper(list, 0, list.size() - 1);
    }
    
    private static <T extends Comparable<T>> void mergeSortHelper(List<T> list, int left, int right) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            mergeSortHelper(list, left, mid);
            mergeSortHelper(list, mid + 1, right);
            merge(list, left, mid, right);
        }
    }
    
    private static <T extends Comparable<T>> void merge(List<T> list, int left, int mid, int right) {
        List<T> leftArray = new ArrayList<>(list.subList(left, mid + 1));
        List<T> rightArray = new ArrayList<>(list.subList(mid + 1, right + 1));
        
        int i = 0, j = 0, k = left;
        
        while (i < leftArray.size() && j < rightArray.size()) {
            if (leftArray.get(i).compareTo(rightArray.get(j)) <= 0) {
                list.set(k++, leftArray.get(i++));
            } else {
                list.set(k++, rightArray.get(j++));
            }
        }
        
        while (i < leftArray.size()) {
            list.set(k++, leftArray.get(i++));
        }
        
        while (j < rightArray.size()) {
            list.set(k++, rightArray.get(j++));
        }
    }
    
    /**
     * Heap Sort - O(n log n) in-place algorithm
     * Best for: When memory is limited, guaranteed O(n log n) performance
     */
    public static <T extends Comparable<T>> void heapSort(List<T> list) {
        if (list == null || list.size() <= 1) return;
        
        int n = list.size();
        
        // Build max heap
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(list, n, i);
        }
        
        // Extract elements from heap
        for (int i = n - 1; i > 0; i--) {
            Collections.swap(list, 0, i);
            heapify(list, i, 0);
        }
    }
    
    private static <T extends Comparable<T>> void heapify(List<T> list, int n, int i) {
        int largest = i;
        int left = 2 * i + 1;
        int right = 2 * i + 2;
        
        if (left < n && list.get(left).compareTo(list.get(largest)) > 0) {
            largest = left;
        }
        
        if (right < n && list.get(right).compareTo(list.get(largest)) > 0) {
            largest = right;
        }
        
        if (largest != i) {
            Collections.swap(list, i, largest);
            heapify(list, n, largest);
        }
    }
    
    /**
     * Insertion Sort - O(n²) but efficient for small datasets
     * Best for: Small datasets, nearly sorted data
     */
    public static <T extends Comparable<T>> void insertionSort(List<T> list) {
        if (list == null || list.size() <= 1) return;
        
        for (int i = 1; i < list.size(); i++) {
            T key = list.get(i);
            int j = i - 1;
            
            while (j >= 0 && list.get(j).compareTo(key) > 0) {
                list.set(j + 1, list.get(j));
                j--;
            }
            list.set(j + 1, key);
        }
    }
    
    /**
     * Selection Sort - O(n²) simple algorithm
     * Best for: Small datasets, educational purposes
     */
    public static <T extends Comparable<T>> void selectionSort(List<T> list) {
        if (list == null || list.size() <= 1) return;
        
        for (int i = 0; i < list.size() - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < list.size(); j++) {
                if (list.get(j).compareTo(list.get(minIndex)) < 0) {
                    minIndex = j;
                }
            }
            if (minIndex != i) {
                Collections.swap(list, i, minIndex);
            }
        }
    }
    
    // ===================================================================
    // SEARCH ALGORITHMS
    // ===================================================================
    
    /**
     * Linear Search - O(n) algorithm
     * Best for: Unsorted data, small datasets
     */
    public static <T> int linearSearch(List<T> list, T target) {
        if (list == null) return -1;
        
        for (int i = 0; i < list.size(); i++) {
            if (Objects.equals(list.get(i), target)) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * Binary Search - O(log n) algorithm
     * Best for: Sorted data, large datasets
     */
    public static <T extends Comparable<T>> int binarySearch(List<T> sortedList, T target) {
        if (sortedList == null) return -1;
        
        int left = 0;
        int right = sortedList.size() - 1;
        
        while (left <= right) {
            int mid = left + (right - left) / 2;
            int comparison = sortedList.get(mid).compareTo(target);
            
            if (comparison == 0) {
                return mid;
            } else if (comparison < 0) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return -1;
    }
    
    /**
     * Interpolation Search - O(log log n) average case
     * Best for: Uniformly distributed sorted data
     */
    public static <T extends Comparable<T>> int interpolationSearch(List<T> sortedList, T target) {
        if (sortedList == null || sortedList.isEmpty()) return -1;
        
        int left = 0;
        int right = sortedList.size() - 1;
        
        while (left <= right && sortedList.get(left).compareTo(target) <= 0 && 
               sortedList.get(right).compareTo(target) >= 0) {
            
            // Calculate interpolation position
            int pos = left + ((right - left) / 2);
            
            int comparison = sortedList.get(pos).compareTo(target);
            if (comparison == 0) {
                return pos;
            } else if (comparison < 0) {
                left = pos + 1;
            } else {
                right = pos - 1;
            }
        }
        return -1;
    }
    
    // ===================================================================
    // STUDY SPACE SPECIFIC SORTING UTILITIES
    // ===================================================================
    
    /**
     * Sort flashcards by various criteria
     */
    public static List<Flashcard> sortFlashcards(List<Flashcard> flashcards, String sortOption) {
        if (flashcards == null || sortOption == null) return flashcards;
        
        List<Flashcard> sortedList = new ArrayList<>(flashcards);
        
        switch (sortOption) {
            case "Recently Studied":
                sortedList.sort((a, b) -> {
                    if (a.getLastStudied() == null && b.getLastStudied() == null) return 0;
                    if (a.getLastStudied() == null) return 1;
                    if (b.getLastStudied() == null) return -1;
                    return b.getLastStudied().compareTo(a.getLastStudied());
                });
                break;
            case "Least Recently Studied":
                sortedList.sort((a, b) -> {
                    if (a.getLastStudied() == null && b.getLastStudied() == null) return 0;
                    if (a.getLastStudied() == null) return -1;
                    if (b.getLastStudied() == null) return 1;
                    return a.getLastStudied().compareTo(b.getLastStudied());
                });
                break;
            case "Most Studied":
                sortedList.sort((a, b) -> Integer.compare(b.getTimesStudied(), a.getTimesStudied()));
                break;
            case "Least Studied":
                sortedList.sort((a, b) -> Integer.compare(a.getTimesStudied(), b.getTimesStudied()));
                break;
            case "Alphabetical (A-Z)":
                sortedList.sort((a, b) -> a.getQuestion().compareToIgnoreCase(b.getQuestion()));
                break;
            case "Alphabetical (Z-A)":
                sortedList.sort((a, b) -> b.getQuestion().compareToIgnoreCase(a.getQuestion()));
                break;
            case "Difficulty (Easy-Hard)":
                sortedList.sort((a, b) -> a.getDifficulty().compareTo(b.getDifficulty()));
                break;
            case "Difficulty (Hard-Easy)":
                sortedList.sort((a, b) -> b.getDifficulty().compareTo(a.getDifficulty()));
                break;
        }
        
        return sortedList;
    }
    
    /**
     * Sort flashcard decks by various criteria
     */
    public static List<FlashcardDeck> sortFlashcardDecks(List<FlashcardDeck> decks, String sortOption) {
        if (decks == null || sortOption == null) return decks;
        
        List<FlashcardDeck> sortedList = new ArrayList<>(decks);
        
        switch (sortOption) {
            case "Title (A-Z)":
                sortedList.sort((a, b) -> a.getTitle().compareToIgnoreCase(b.getTitle()));
                break;
            case "Title (Z-A)":
                sortedList.sort((a, b) -> b.getTitle().compareToIgnoreCase(a.getTitle()));
                break;
            case "Date Created (Newest)":
                sortedList.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
                break;
            case "Date Created (Oldest)":
                sortedList.sort((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()));
                break;
            case "Subject (A-Z)":
                sortedList.sort((a, b) -> a.getSubject().compareToIgnoreCase(b.getSubject()));
                break;
            case "Subject (Z-A)":
                sortedList.sort((a, b) -> b.getSubject().compareToIgnoreCase(a.getSubject()));
                break;
            case "Difficulty (Easy to Hard)":
                sortedList.sort((a, b) -> a.getDifficulty().compareTo(b.getDifficulty()));
                break;
            case "Difficulty (Hard to Easy)":
                sortedList.sort((a, b) -> b.getDifficulty().compareTo(a.getDifficulty()));
                break;
            case "Card Count (Low to High)":
                sortedList.sort((a, b) -> Integer.compare(a.getCardCount(), b.getCardCount()));
                break;
            case "Card Count (High to Low)":
                sortedList.sort((a, b) -> Integer.compare(b.getCardCount(), a.getCardCount()));
                break;
        }
        
        return sortedList;
    }
    
    /**
     * Sort notes by various criteria
     */
    public static List<Note> sortNotes(List<Note> notes, String sortOption) {
        if (notes == null || sortOption == null) return notes;
        
        List<Note> sortedList = new ArrayList<>(notes);
        
        switch (sortOption) {
            case "Date Created (Newest)":
                sortedList.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
                break;
            case "Date Created (Oldest)":
                sortedList.sort((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()));
                break;
            case "Title (A-Z)":
                sortedList.sort((a, b) -> a.getTitle().compareToIgnoreCase(b.getTitle()));
                break;
            case "Title (Z-A)":
                sortedList.sort((a, b) -> b.getTitle().compareToIgnoreCase(a.getTitle()));
                break;
            case "Subject (A-Z)":
                sortedList.sort((a, b) -> a.getCategory().compareToIgnoreCase(b.getCategory()));
                break;
            case "Subject (Z-A)":
                sortedList.sort((a, b) -> b.getCategory().compareToIgnoreCase(a.getCategory()));
                break;
            case "Last Modified (Newest)":
                sortedList.sort((a, b) -> b.getModifiedAt().compareTo(a.getModifiedAt()));
                break;
            case "Last Modified (Oldest)":
                sortedList.sort((a, b) -> a.getModifiedAt().compareTo(b.getModifiedAt()));
                break;
        }
        
        return sortedList;
    }
    
    /**
     * Sort quizzes by various criteria
     */
    public static List<Quiz> sortQuizzes(List<Quiz> quizzes, String sortOption) {
        if (quizzes == null || sortOption == null) return quizzes;
        
        List<Quiz> sortedList = new ArrayList<>(quizzes);
        
        switch (sortOption) {
            case "Title (A-Z)":
                sortedList.sort((a, b) -> a.getTitle().compareToIgnoreCase(b.getTitle()));
                break;
            case "Title (Z-A)":
                sortedList.sort((a, b) -> b.getTitle().compareToIgnoreCase(a.getTitle()));
                break;
            case "Date Created (Newest)":
                sortedList.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
                break;
            case "Date Created (Oldest)":
                sortedList.sort((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()));
                break;
            case "Subject (A-Z)":
                sortedList.sort((a, b) -> a.getSubject().compareToIgnoreCase(b.getSubject()));
                break;
            case "Subject (Z-A)":
                sortedList.sort((a, b) -> b.getSubject().compareToIgnoreCase(a.getSubject()));
                break;
            case "Difficulty (Easy to Hard)":
                sortedList.sort((a, b) -> a.getDifficulty().compareTo(b.getDifficulty()));
                break;
            case "Difficulty (Hard to Easy)":
                sortedList.sort((a, b) -> b.getDifficulty().compareTo(a.getDifficulty()));
                break;
            case "Question Count (Low to High)":
                sortedList.sort((a, b) -> Integer.compare(a.getQuestionCount(), b.getQuestionCount()));
                break;
            case "Question Count (High to Low)":
                sortedList.sort((a, b) -> Integer.compare(b.getQuestionCount(), a.getQuestionCount()));
                break;
        }
        
        return sortedList;
    }
    
    /**
     * Sort code problems by various criteria
     */
    public static List<CodeProblem> sortCodeProblems(List<CodeProblem> problems, String sortOption) {
        if (problems == null || sortOption == null) return problems;
        
        List<CodeProblem> sortedList = new ArrayList<>(problems);
        
        switch (sortOption) {
            case "Title (A-Z)":
                sortedList.sort((a, b) -> a.getTitle().compareToIgnoreCase(b.getTitle()));
                break;
            case "Title (Z-A)":
                sortedList.sort((a, b) -> b.getTitle().compareToIgnoreCase(a.getTitle()));
                break;
            case "Difficulty (Easy to Hard)":
                sortedList.sort((a, b) -> a.getDifficulty().compareTo(b.getDifficulty()));
                break;
            case "Difficulty (Hard to Easy)":
                sortedList.sort((a, b) -> b.getDifficulty().compareTo(a.getDifficulty()));
                break;
            case "Language (A-Z)":
                sortedList.sort((a, b) -> a.getLanguage().compareToIgnoreCase(b.getLanguage()));
                break;
            case "Language (Z-A)":
                sortedList.sort((a, b) -> b.getLanguage().compareToIgnoreCase(a.getLanguage()));
                break;
            case "Date Created (Newest)":
                sortedList.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
                break;
            case "Date Created (Oldest)":
                sortedList.sort((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()));
                break;
            case "Last Attempted (Recent)":
                sortedList.sort((a, b) -> {
                    if (a.getLastAttempted() == null && b.getLastAttempted() == null) return 0;
                    if (a.getLastAttempted() == null) return 1;
                    if (b.getLastAttempted() == null) return -1;
                    return b.getLastAttempted().compareTo(a.getLastAttempted());
                });
                break;
            case "Last Attempted (Oldest)":
                sortedList.sort((a, b) -> {
                    if (a.getLastAttempted() == null && b.getLastAttempted() == null) return 0;
                    if (a.getLastAttempted() == null) return -1;
                    if (b.getLastAttempted() == null) return 1;
                    return a.getLastAttempted().compareTo(b.getLastAttempted());
                });
                break;
        }
        
        return sortedList;
    }
    
    /**
     * Sort todo items by various criteria
     */
    public static List<TodoItem> sortTodoItems(List<TodoItem> items, String sortOption) {
        if (items == null || sortOption == null) return items;
        
        List<TodoItem> sortedList = new ArrayList<>(items);
        
        switch (sortOption) {
            case "Priority (High to Low)":
                sortedList.sort((a, b) -> b.getPriority().compareTo(a.getPriority()));
                break;
            case "Priority (Low to High)":
                sortedList.sort((a, b) -> a.getPriority().compareTo(b.getPriority()));
                break;
            case "Due Date (Earliest)":
                sortedList.sort((a, b) -> {
                    if (a.getDueDate() == null && b.getDueDate() == null) return 0;
                    if (a.getDueDate() == null) return 1;
                    if (b.getDueDate() == null) return -1;
                    return a.getDueDate().compareTo(b.getDueDate());
                });
                break;
            case "Due Date (Latest)":
                sortedList.sort((a, b) -> {
                    if (a.getDueDate() == null && b.getDueDate() == null) return 0;
                    if (a.getDueDate() == null) return -1;
                    if (b.getDueDate() == null) return 1;
                    return b.getDueDate().compareTo(a.getDueDate());
                });
                break;
            case "Title (A-Z)":
                sortedList.sort((a, b) -> a.getTitle().compareToIgnoreCase(b.getTitle()));
                break;
            case "Title (Z-A)":
                sortedList.sort((a, b) -> b.getTitle().compareToIgnoreCase(a.getTitle()));
                break;
            case "Date Created (Newest)":
                sortedList.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
                break;
            case "Date Created (Oldest)":
                sortedList.sort((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()));
                break;
        }
        
        return sortedList;
    }
    
    // ===================================================================
    // FILTERING ALGORITHMS
    // ===================================================================
    
    /**
     * Filter flashcards by search criteria
     */
    public static List<Flashcard> filterFlashcards(List<Flashcard> flashcards, String searchText) {
        if (flashcards == null || searchText == null || searchText.trim().isEmpty()) {
            return flashcards;
        }
        
        String lowerCaseFilter = searchText.toLowerCase();
        return flashcards.stream()
            .filter(flashcard -> 
                flashcard.getQuestion().toLowerCase().contains(lowerCaseFilter) ||
                flashcard.getAnswer().toLowerCase().contains(lowerCaseFilter))
            .collect(Collectors.toList());
    }
    
    /**
     * Filter flashcard decks by search criteria
     */
    public static List<FlashcardDeck> filterFlashcardDecks(List<FlashcardDeck> decks, String searchText) {
        if (decks == null || searchText == null || searchText.trim().isEmpty()) {
            return decks;
        }
        
        String lowerCaseFilter = searchText.toLowerCase();
        return decks.stream()
            .filter(deck -> 
                deck.getTitle().toLowerCase().contains(lowerCaseFilter) ||
                deck.getDescription().toLowerCase().contains(lowerCaseFilter) ||
                deck.getSubject().toLowerCase().contains(lowerCaseFilter))
            .collect(Collectors.toList());
    }
    
    /**
     * Filter notes by search criteria
     */
    public static List<Note> filterNotes(List<Note> notes, String searchText) {
        if (notes == null || searchText == null || searchText.trim().isEmpty()) {
            return notes;
        }
        
        String lowerCaseFilter = searchText.toLowerCase();
        return notes.stream()
            .filter(note -> 
                note.getTitle().toLowerCase().contains(lowerCaseFilter) ||
                note.getContent().toLowerCase().contains(lowerCaseFilter) ||
                note.getCategory().toLowerCase().contains(lowerCaseFilter))
            .collect(Collectors.toList());
    }
    
    /**
     * Filter quizzes by search criteria
     */
    public static List<Quiz> filterQuizzes(List<Quiz> quizzes, String searchText) {
        if (quizzes == null || searchText == null || searchText.trim().isEmpty()) {
            return quizzes;
        }
        
        String lowerCaseFilter = searchText.toLowerCase();
        return quizzes.stream()
            .filter(quiz -> 
                quiz.getTitle().toLowerCase().contains(lowerCaseFilter) ||
                quiz.getDescription().toLowerCase().contains(lowerCaseFilter) ||
                quiz.getSubject().toLowerCase().contains(lowerCaseFilter))
            .collect(Collectors.toList());
    }
    
    /**
     * Filter code problems by search criteria
     */
    public static List<CodeProblem> filterCodeProblems(List<CodeProblem> problems, String searchText) {
        if (problems == null || searchText == null || searchText.trim().isEmpty()) {
            return problems;
        }
        
        String lowerCaseFilter = searchText.toLowerCase();
        return problems.stream()
            .filter(problem -> 
                problem.getTitle().toLowerCase().contains(lowerCaseFilter) ||
                problem.getDescription().toLowerCase().contains(lowerCaseFilter) ||
                problem.getLanguage().toLowerCase().contains(lowerCaseFilter))
            .collect(Collectors.toList());
    }
    
    /**
     * Filter todo items by search criteria
     */
    public static List<TodoItem> filterTodoItems(List<TodoItem> items, String searchText) {
        if (items == null || searchText == null || searchText.trim().isEmpty()) {
            return items;
        }
        
        String lowerCaseFilter = searchText.toLowerCase();
        return items.stream()
            .filter(item -> 
                item.getTitle().toLowerCase().contains(lowerCaseFilter) ||
                item.getDescription().toLowerCase().contains(lowerCaseFilter) ||
                item.getCategory().toLowerCase().contains(lowerCaseFilter))
            .collect(Collectors.toList());
    }
    
    // ===================================================================
    // PERFORMANCE UTILITIES
    // ===================================================================
    
    /**
     * Choose the best sorting algorithm based on data size and characteristics
     */
    public static <T extends Comparable<T>> void smartSort(List<T> list) {
        if (list == null || list.size() <= 1) return;
        
        int size = list.size();
        
        if (size <= 10) {
            // Use insertion sort for very small lists
            insertionSort(list);
        } else if (size <= 50) {
            // Use selection sort for small lists
            selectionSort(list);
        } else if (size <= 1000) {
            // Use heap sort for medium lists (guaranteed O(n log n))
            heapSort(list);
        } else {
            // Use quick sort for large lists (average O(n log n))
            quickSort(list);
        }
    }
    
    /**
     * Check if a list is already sorted
     */
    public static <T extends Comparable<T>> boolean isSorted(List<T> list) {
        if (list == null || list.size() <= 1) return true;
        
        for (int i = 1; i < list.size(); i++) {
            if (list.get(i - 1).compareTo(list.get(i)) > 0) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Get the median of a list (for pivot selection in quick sort)
     */
    public static <T extends Comparable<T>> T getMedian(List<T> list, int left, int right) {
        int mid = left + (right - left) / 2;
        T a = list.get(left);
        T b = list.get(mid);
        T c = list.get(right);
        
        if (a.compareTo(b) <= 0 && b.compareTo(c) <= 0) return b;
        if (a.compareTo(c) <= 0 && c.compareTo(b) <= 0) return c;
        return a;
    }
    
    // ===================================================================
    // UTILITY METHODS
    // ===================================================================
    
    /**
     * Reverse a list in-place
     */
    public static <T> void reverse(List<T> list) {
        if (list == null || list.size() <= 1) return;
        
        int left = 0;
        int right = list.size() - 1;
        
        while (left < right) {
            Collections.swap(list, left, right);
            left++;
            right--;
        }
    }
    
    /**
     * Shuffle a list using Fisher-Yates algorithm
     */
    public static <T> void shuffle(List<T> list) {
        if (list == null || list.size() <= 1) return;
        
        Random random = new Random();
        for (int i = list.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            Collections.swap(list, i, j);
        }
    }
    
    /**
     * Get the k-th smallest element using quick select
     */
    public static <T extends Comparable<T>> T quickSelect(List<T> list, int k) {
        if (list == null || k < 0 || k >= list.size()) {
            throw new IllegalArgumentException("Invalid parameters");
        }
        
        List<T> copy = new ArrayList<>(list);
        return quickSelectHelper(copy, 0, copy.size() - 1, k);
    }
    
    private static <T extends Comparable<T>> T quickSelectHelper(List<T> list, int left, int right, int k) {
        if (left == right) return list.get(left);
        
        int pivotIndex = partition(list, left, right);
        
        if (k == pivotIndex) {
            return list.get(k);
        } else if (k < pivotIndex) {
            return quickSelectHelper(list, left, pivotIndex - 1, k);
        } else {
            return quickSelectHelper(list, pivotIndex + 1, right, k);
        }
    }
    
    /**
     * Calculate the performance metrics for sorting algorithms
     */
    public static class PerformanceMetrics {
        private final long executionTime;
        private final int comparisons;
        private final int swaps;
        
        public PerformanceMetrics(long executionTime, int comparisons, int swaps) {
            this.executionTime = executionTime;
            this.comparisons = comparisons;
            this.swaps = swaps;
        }
        
        public long getExecutionTime() { return executionTime; }
        public int getComparisons() { return comparisons; }
        public int getSwaps() { return swaps; }
        
        @Override
        public String toString() {
            return String.format("Execution Time: %dms, Comparisons: %d, Swaps: %d", 
                executionTime, comparisons, swaps);
        }
    }
}
