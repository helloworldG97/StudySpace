package com.studyspace.utils;

import com.studyspace.models.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Comparator;

/**
 * DataStore - Singleton class for managing application data
 * In a real application, this would interface with a database
 */
public class DataStore {
    private static DataStore instance;
    
    // Data storage
    private Map<String, User> users;
    private Map<String, FlashcardDeck> flashcardDecks;
    private Map<String, Quiz> quizzes;
    private Map<String, CodeProblem> codeProblems;
    private Map<String, Note> notes;
    private Map<String, TodoItem> todoItems;
    private Map<String, Activity> activities;
    
    // Current session
    private User currentUser;
    
    // Flag to prevent data re-initialization
    private boolean dataInitialized = false;
    
    private DataStore() {
        initializeData();
    }
    
    public static DataStore getInstance() {
        if (instance == null) {
            System.out.println("Creating new DataStore instance");
            instance = new DataStore();
        } else {
            System.out.println("Using existing DataStore instance");
        }
        return instance;
    }
    
    private void initializeData() {
        // Only initialize if not already done
        if (dataInitialized) {
            return;
        }
        
        users = new HashMap<>();
        flashcardDecks = new HashMap<>();
        quizzes = new HashMap<>();
        codeProblems = new HashMap<>();
        notes = new HashMap<>();
        todoItems = new HashMap<>();
        activities = new HashMap<>();
        
        initializeSampleData();
        dataInitialized = true;
    }
    
    private void initializeSampleData() {
        // Create sample users
        createSampleUsers();
        
        // Create sample flashcard decks
        createSampleFlashcardDecks();
        
        // Create sample quizzes
        createSampleQuizzes();
        
        // Create sample code problems
        createSampleCodeProblems();
        
        // Create sample notes
        createSampleNotes();
        
        // Create sample todo items
        createSampleTodoItems();
        
        // Create sample activities
        createSampleActivities();
    }
    
    private void createSampleUsers() {
        User demoUser = new User("John Doe", "john@studyspace.com", "password123");
        demoUser.setFlashcardsStudied(127);
        demoUser.setCodeProblemsCompleted(15);
        demoUser.setQuizzesTaken(8);
        demoUser.setCurrentStreak(5);
        demoUser.setTotalStudyHours(45);
        users.put(demoUser.getEmail(), demoUser);
        
        User testUser = new User("Jane Smith", "jane@studyspace.com", "test123");
        testUser.setFlashcardsStudied(89);
        testUser.setCodeProblemsCompleted(12);
        testUser.setQuizzesTaken(6);
        testUser.setCurrentStreak(3);
        testUser.setTotalStudyHours(32);
        users.put(testUser.getEmail(), testUser);
    }
    
    private void createSampleFlashcardDecks() {
        // JavaScript Fundamentals Deck
        FlashcardDeck jsDeck = new FlashcardDeck(
            "JavaScript Fundamentals",
            "Essential JavaScript concepts for beginners",
            "JavaScript",
            Flashcard.Difficulty.MEDIUM
        );
        
        jsDeck.addFlashcard(new Flashcard(
            "What is a closure in JavaScript?",
            "A closure is a function that has access to variables in its outer (enclosing) scope even after the outer function has returned. It gives you access to an outer function's scope from an inner function.",
            Flashcard.Difficulty.MEDIUM
        ));
        
        jsDeck.addFlashcard(new Flashcard(
            "What is the difference between let and var?",
            "let has block scope and cannot be redeclared in the same scope, while var has function scope and can be redeclared. let also has a temporal dead zone.",
            Flashcard.Difficulty.EASY
        ));
        
        jsDeck.setLastStudied(LocalDateTime.now().minusHours(2));
        flashcardDecks.put(jsDeck.getId(), jsDeck);
        
        // React Concepts Deck
        FlashcardDeck reactDeck = new FlashcardDeck(
            "React Concepts",
            "Core React concepts and patterns",
            "React",
            Flashcard.Difficulty.HARD
        );
        
        reactDeck.addFlashcard(new Flashcard(
            "What is the virtual DOM in React?",
            "The virtual DOM is a JavaScript representation of the real DOM. React uses it to optimize rendering by comparing the virtual DOM with the previous version and only updating the parts that changed.",
            Flashcard.Difficulty.HARD
        ));
        
        reactDeck.setLastStudied(LocalDateTime.now().minusDays(1));
        flashcardDecks.put(reactDeck.getId(), reactDeck);
        
        // Algorithm Analysis Deck
        FlashcardDeck algoDeck = new FlashcardDeck(
            "Algorithm Analysis",
            "Big O notation and algorithm complexity",
            "Computer Science",
            Flashcard.Difficulty.MEDIUM
        );
        
        algoDeck.addFlashcard(new Flashcard(
            "What is the Big O notation for binary search?",
            "O(log n) - Binary search divides the search space in half with each comparison, resulting in logarithmic time complexity.",
            Flashcard.Difficulty.MEDIUM
        ));
        
        flashcardDecks.put(algoDeck.getId(), algoDeck);
    }
    
    private void createSampleQuizzes() {
        // JavaScript Fundamentals Quiz
        Quiz jsQuiz = new Quiz(
            "JavaScript Fundamentals",
            "Test your knowledge of JavaScript basics",
            "JavaScript",
            Flashcard.Difficulty.MEDIUM,
            30
        );
        
        jsQuiz.addQuestion(new Question(
            "Which of the following is NOT a primitive data type in JavaScript?",
            Arrays.asList("string", "number", "object", "boolean"),
            2, // object is not primitive
            "Object is a reference type, not a primitive type. The primitive types in JavaScript are: string, number, boolean, undefined, null, symbol, and bigint.",
            Flashcard.Difficulty.MEDIUM
        ));
        
        jsQuiz.addQuestion(new Question(
            "What does the '===' operator do in JavaScript?",
            Arrays.asList("Assigns a value", "Compares values only", "Compares values and types", "Compares references"),
            2, // Compares values and types
            "The '===' operator performs strict equality comparison, checking both value and type without type coercion.",
            Flashcard.Difficulty.EASY
        ));
        
        jsQuiz.setBestScore(85);
        jsQuiz.setTimesTaken(2);
        jsQuiz.setLastTaken(LocalDateTime.now().minusHours(4));
        quizzes.put(jsQuiz.getId(), jsQuiz);
        
        // React Components & Hooks Quiz
        Quiz reactQuiz = new Quiz(
            "React Components & Hooks",
            "Advanced React concepts including hooks and component lifecycle",
            "React",
            Flashcard.Difficulty.HARD,
            25
        );
        
        reactQuiz.addQuestion(new Question(
            "Which React hook is used for side effects?",
            Arrays.asList("useState", "useEffect", "useContext", "useMemo"),
            1, // useEffect
            "useEffect is used for side effects like data fetching, subscriptions, or manually changing the DOM.",
            Flashcard.Difficulty.MEDIUM
        ));
        
        reactQuiz.addQuestion(new Question(
            "What is the purpose of the dependency array in useEffect?",
            Arrays.asList("To store dependencies", "To control when the effect runs", "To pass props", "To manage state"),
            1, // To control when the effect runs
            "The dependency array controls when the effect runs. If empty, it runs once. If it contains values, it runs when those values change.",
            Flashcard.Difficulty.HARD
        ));
        
        quizzes.put(reactQuiz.getId(), reactQuiz);
        
        // Data Structures & Algorithms Quiz
        Quiz dsaQuiz = new Quiz(
            "Data Structures & Algorithms",
            "Test your knowledge of common data structures and algorithms",
            "Computer Science",
            Flashcard.Difficulty.HARD,
            45
        );
        
        dsaQuiz.addQuestion(new Question(
            "What is the time complexity of inserting an element at the beginning of an array?",
            Arrays.asList("O(1)", "O(log n)", "O(n)", "O(n²)"),
            2, // O(n)
            "Inserting at the beginning requires shifting all existing elements, which takes O(n) time.",
            Flashcard.Difficulty.MEDIUM
        ));
        
        quizzes.put(dsaQuiz.getId(), dsaQuiz);
        
        // CSS Layout Quiz
        Quiz cssQuiz = new Quiz(
            "CSS Layout",
            "Master CSS layout techniques",
            "CSS",
            Flashcard.Difficulty.EASY,
            20
        );
        
        cssQuiz.addQuestion(new Question(
            "Which CSS property is used to create flexible layouts?",
            Arrays.asList("display: block", "display: flex", "display: inline", "display: none"),
            1, // display: flex
            "Flexbox (display: flex) is a powerful layout method that allows flexible arrangement of elements.",
            Flashcard.Difficulty.EASY
        ));
        
        quizzes.put(cssQuiz.getId(), cssQuiz);
    }
    
    private void createSampleCodeProblems() {
        // Reverse a String
        CodeProblem reverseString = new CodeProblem(
            "Reverse a String",
            "Write a function that takes a string as input and returns the string reversed.\n\nExample:\n- Input: \"hello\"\n- Output: \"olleh\"",
            Flashcard.Difficulty.EASY,
            "JavaScript",
            "function reverseString(str) {\n    // Your code here\n    \n}"
        );
        
        reverseString.addTestCase("\"hello\"", "\"olleh\"", "Basic string reversal");
        reverseString.addTestCase("\"world\"", "\"dlrow\"", "Another basic test");
        reverseString.addTestCase("\"\"", "\"\"", "Empty string");
        reverseString.addTestCase("\"a\"", "\"a\"", "Single character");
        
        codeProblems.put(reverseString.getId(), reverseString);
        
        // Find Maximum in Array
        CodeProblem findMax = new CodeProblem(
            "Find Maximum in Array",
            "Write a function that takes an array of numbers and returns the maximum value.\n\nExample:\n- Input: [1, 3, 2, 8, 5]\n- Output: 8",
            Flashcard.Difficulty.EASY,
            "JavaScript",
            "function findMax(arr) {\n    // Your code here\n    \n}"
        );
        
        findMax.addTestCase("[1, 3, 2, 8, 5]", "8", "Basic array");
        findMax.addTestCase("[10]", "10", "Single element");
        findMax.addTestCase("[-1, -5, -2]", "-1", "Negative numbers");
        findMax.addTestCase("[0, 0, 0]", "0", "All zeros");
        
        codeProblems.put(findMax.getId(), findMax);
        
        // FizzBuzz
        CodeProblem fizzBuzz = new CodeProblem(
            "FizzBuzz",
            "Write a function that returns an array of strings from 1 to n, where:\n- Multiples of 3 are replaced with \"Fizz\"\n- Multiples of 5 are replaced with \"Buzz\"\n- Multiples of both 3 and 5 are replaced with \"FizzBuzz\"\n\nExample:\n- Input: 15\n- Output: [\"1\", \"2\", \"Fizz\", \"4\", \"Buzz\", \"Fizz\", \"7\", \"8\", \"Fizz\", \"Buzz\", \"11\", \"Fizz\", \"13\", \"14\", \"FizzBuzz\"]",
            Flashcard.Difficulty.MEDIUM,
            "JavaScript",
            "function fizzBuzz(n) {\n    // Your code here\n    \n}"
        );
        
        fizzBuzz.addTestCase("3", "[\"1\", \"2\", \"Fizz\"]", "Basic test");
        fizzBuzz.addTestCase("5", "[\"1\", \"2\", \"Fizz\", \"4\", \"Buzz\"]", "Include Buzz");
        fizzBuzz.addTestCase("15", "[\"1\", \"2\", \"Fizz\", \"4\", \"Buzz\", \"Fizz\", \"7\", \"8\", \"Fizz\", \"Buzz\", \"11\", \"Fizz\", \"13\", \"14\", \"FizzBuzz\"]", "Include FizzBuzz");
        
        fizzBuzz.markAsAttempted();
        codeProblems.put(fizzBuzz.getId(), fizzBuzz);
    }
    
    private void createSampleNotes() {
        Note jsNote = new Note(
            "JavaScript ES6 Features",
            "# ES6 Features\n\n## Arrow Functions\n- Shorter syntax for function expressions\n- Lexical this binding\n\n## Destructuring\n- Extract values from arrays and objects\n- const {name, age} = person;\n\n## Template Literals\n- Use backticks for string interpolation\n- `Hello ${name}!`",
            "JavaScript"
        );
        jsNote.addTag("ES6");
        jsNote.addTag("JavaScript");
        jsNote.addTag("Functions");
        notes.put(jsNote.getId(), jsNote);
        
        Note reactNote = new Note(
            "React Hooks Cheat Sheet",
            "# React Hooks\n\n## useState\n```jsx\nconst [state, setState] = useState(initialValue);\n```\n\n## useEffect\n```jsx\nuseEffect(() => {\n  // Side effect\n  return () => {\n    // Cleanup\n  };\n}, [dependencies]);\n```\n\n## useContext\n```jsx\nconst value = useContext(MyContext);\n```",
            "React"
        );
        reactNote.addTag("React");
        reactNote.addTag("Hooks");
        reactNote.addTag("Cheat Sheet");
        reactNote.setPinned(true);
        notes.put(reactNote.getId(), reactNote);
        
        Note algoNote = new Note(
            "Big O Notation",
            "# Big O Notation\n\n## Common Complexities\n- O(1) - Constant time\n- O(log n) - Logarithmic time\n- O(n) - Linear time\n- O(n log n) - Linearithmic time\n- O(n²) - Quadratic time\n\n## Examples\n- Array access: O(1)\n- Binary search: O(log n)\n- Linear search: O(n)\n- Merge sort: O(n log n)\n- Bubble sort: O(n²)",
            "Computer Science"
        );
        algoNote.addTag("Algorithms");
        algoNote.addTag("Big O");
        algoNote.addTag("Complexity");
        notes.put(algoNote.getId(), algoNote);
    }
    
    private void createSampleTodoItems() {
        TodoItem todo1 = new TodoItem(
            "Complete JavaScript fundamentals quiz",
            "Take the quiz on JavaScript basics and ES6 features",
            TodoItem.Priority.HIGH,
            "Study",
            LocalDate.now().plusDays(1)
        );
        todoItems.put(todo1.getId(), todo1);
        
        TodoItem todo2 = new TodoItem(
            "Review React hooks documentation",
            "Read through the official React hooks documentation",
            TodoItem.Priority.MEDIUM,
            "Learning",
            LocalDate.now().plusDays(3)
        );
        todoItems.put(todo2.getId(), todo2);
        
        // Add some overdue items to test notifications
        TodoItem overdue1 = new TodoItem(
            "Finish Python project",
            "Complete the Python web scraping project",
            TodoItem.Priority.HIGH,
            "Project",
            LocalDate.now().minusDays(2) // Overdue by 2 days
        );
        todoItems.put(overdue1.getId(), overdue1);
        
        TodoItem overdue2 = new TodoItem(
            "Submit assignment",
            "Submit the data structures assignment",
            TodoItem.Priority.HIGH,
            "Study",
            LocalDate.now().minusDays(1) // Overdue by 1 day
        );
        todoItems.put(overdue2.getId(), overdue2);
        
        TodoItem todo3 = new TodoItem(
            "Practice binary search algorithm",
            "Implement and test binary search in different languages",
            TodoItem.Priority.MEDIUM,
            "Practice"
        );
        todoItems.put(todo3.getId(), todo3);
        
        TodoItem todo4 = new TodoItem(
            "Study for data structures exam",
            "Review all data structures and algorithms for the exam",
            TodoItem.Priority.HIGH,
            "Study",
            LocalDate.now().plusDays(7)
        );
        todoItems.put(todo4.getId(), todo4);
        
        TodoItem completedTodo = new TodoItem(
            "Set up development environment",
            "Install and configure all necessary development tools",
            TodoItem.Priority.LOW,
            "Setup"
        );
        completedTodo.markAsCompleted();
        todoItems.put(completedTodo.getId(), completedTodo);
    }
    
    private void createSampleActivities() {
        if (currentUser != null) {
            String userId = currentUser.getId();
            
            // Today's activities (more comprehensive)
            Activity activity1 = new Activity(userId, ActivityType.QUIZ_COMPLETED, "Completed JavaScript Basics Quiz with 85% score");
            activity1.setTimestamp(LocalDateTime.now().minusHours(2));
            activities.put(activity1.getId(), activity1);
            
            Activity activity2 = new Activity(userId, ActivityType.FLASHCARDS_REVIEWED, "Studied 'Array Methods' correctly in Python Fundamentals deck");
            activity2.setTimestamp(LocalDateTime.now().minusHours(3));
            activities.put(activity2.getId(), activity2);
            
            Activity activity3 = new Activity(userId, ActivityType.STUDY_SESSION_STARTED, "Started studying Python Fundamentals flashcard deck");
            activity3.setTimestamp(LocalDateTime.now().minusHours(4));
            activities.put(activity3.getId(), activity3);
            
            Activity activity4 = new Activity(userId, ActivityType.CODE_PROBLEM_SOLVED, "Solved Two Sum problem in Java");
            activity4.setTimestamp(LocalDateTime.now().minusHours(5));
            activities.put(activity4.getId(), activity4);
            
            Activity activity5 = new Activity(userId, ActivityType.NOTES_ADDED, "Added notes on React Hooks and State Management");
            activity5.setTimestamp(LocalDateTime.now().minusHours(6));
            activities.put(activity5.getId(), activity5);
            
            Activity activity6 = new Activity(userId, ActivityType.TODO_ITEM_COMPLETED, "Completed task: Review algorithm complexity");
            activity6.setTimestamp(LocalDateTime.now().minusHours(7));
            activities.put(activity6.getId(), activity6);
            
            // Yesterday's activities
            Activity activity7 = new Activity(userId, ActivityType.STUDY_SESSION_ENDED, "Completed studying Python Fundamentals deck - 12 cards, 83.3% accuracy");
            activity7.setTimestamp(LocalDateTime.now().minusDays(1).minusHours(2));
            activities.put(activity7.getId(), activity7);
            
            Activity activity8 = new Activity(userId, ActivityType.QUIZ_COMPLETED, "Completed CSS Layout Quiz with 92% score");
            activity8.setTimestamp(LocalDateTime.now().minusDays(1).minusHours(4));
            activities.put(activity8.getId(), activity8);
            
            Activity activity9 = new Activity(userId, ActivityType.FLASHCARD_DECK_CREATED, "Created flashcard deck: JavaScript ES6 Features");
            activity9.setTimestamp(LocalDateTime.now().minusDays(1).minusHours(6));
            activities.put(activity9.getId(), activity9);
            
            // 2 days ago activities
            Activity activity10 = new Activity(userId, ActivityType.CODE_PROBLEM_SOLVED, "Solved Palindrome Checker problem");
            activity10.setTimestamp(LocalDateTime.now().minusDays(2).minusHours(1));
            activities.put(activity10.getId(), activity10);
            
            Activity activity11 = new Activity(userId, ActivityType.NOTE_EDITED, "Edited note: Database Design Principles");
            activity11.setTimestamp(LocalDateTime.now().minusDays(2).minusHours(3));
            activities.put(activity11.getId(), activity11);
        }
    }
    
    // User management methods
    public boolean authenticateUser(String email, String password) {
        System.out.println("Authenticating user: " + email);
        System.out.println("Total users in store: " + users.size());
        System.out.println("User keys: " + users.keySet());
        
        User user = users.get(email.toLowerCase());
        if (user != null && user.getPassword().equals(password)) {
            currentUser = user;
            user.updateLastLogin();
            System.out.println("Authentication successful for: " + user.getFullName());
            return true;
        }
        System.out.println("Authentication failed for: " + email);
        return false;
    }
    
    public User registerUser(String fullName, String email, String password) {
        System.out.println("Registering user: " + email);
        System.out.println("Total users before registration: " + users.size());
        
        if (users.containsKey(email.toLowerCase())) {
            System.out.println("User already exists: " + email);
            return null; // User already exists
        }
        
        User newUser = new User(fullName, email.toLowerCase(), password);
        users.put(newUser.getEmail(), newUser);
        currentUser = newUser;
        System.out.println("User registered successfully: " + newUser.getFullName());
        System.out.println("Total users after registration: " + users.size());
        return newUser;
    }
    
    public void logout() {
        currentUser = null;
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    
    /**
     * Updates an existing user in the store
     */
    public void updateUser(User user) {
        if (user != null) {
            users.put(user.getEmail(), user);
            if (currentUser != null && currentUser.getId().equals(user.getId())) {
                currentUser = user;
            }
            System.out.println("User updated: " + user.getFullName());
        }
    }
    
    /**
     * Checks if an email is already taken by another user
     */
    public boolean isEmailTaken(String email) {
        return users.containsKey(email.toLowerCase());
    }
    
    // Data access methods
    public List<FlashcardDeck> getAllFlashcardDecks() {
        return new ArrayList<>(flashcardDecks.values());
    }
    
    public FlashcardDeck getFlashcardDeck(String id) {
        return flashcardDecks.get(id);
    }
    
    public void saveFlashcardDeck(FlashcardDeck deck) {
        flashcardDecks.put(deck.getId(), deck);
    }
    
    public void deleteFlashcardDeck(String id) {
        flashcardDecks.remove(id);
    }
    
    public List<Quiz> getAllQuizzes() {
        return new ArrayList<>(quizzes.values());
    }
    
    public Quiz getQuiz(String id) {
        return quizzes.get(id);
    }
    
    public void saveQuiz(Quiz quiz) {
        quizzes.put(quiz.getId(), quiz);
    }
    
    public void deleteQuiz(Quiz quiz) {
        quizzes.remove(quiz.getId());
    }
    
    public List<CodeProblem> getAllCodeProblems() {
        return new ArrayList<>(codeProblems.values());
    }
    
    public CodeProblem getCodeProblem(String id) {
        return codeProblems.get(id);
    }
    
    public void saveCodeProblem(CodeProblem problem) {
        codeProblems.put(problem.getId(), problem);
    }
    
    public void deleteCodeProblem(String id) {
        codeProblems.remove(id);
    }
    
    public List<Note> getAllNotes() {
        return new ArrayList<>(notes.values());
    }
    
    public Note getNote(String id) {
        return notes.get(id);
    }
    
    public void saveNote(Note note) {
        notes.put(note.getId(), note);
    }
    
    public void deleteNote(String id) {
        notes.remove(id);
    }
    
    public void addNote(Note note) {
        notes.put(note.getId(), note);
    }
    
    public void updateNote(Note note) {
        notes.put(note.getId(), note);
    }
    
    public List<Note> getNotes() {
        return new ArrayList<>(notes.values());
    }
    
    public List<TodoItem> getTodoItems() {
        return new ArrayList<>(todoItems.values());
    }
    
    public List<TodoItem> getAllTodoItems() {
        return new ArrayList<>(todoItems.values());
    }
    
    public List<TodoItem> getActiveTodoItems() {
        return todoItems.values().stream()
                .filter(todo -> !todo.isCompleted())
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    public List<TodoItem> getCompletedTodoItems() {
        return todoItems.values().stream()
                .filter(TodoItem::isCompleted)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    public TodoItem getTodoItem(String id) {
        return todoItems.get(id);
    }
    
    public void saveTodoItem(TodoItem todoItem) {
        todoItems.put(todoItem.getId(), todoItem);
    }
    
    public void deleteTodoItem(String id) {
        todoItems.remove(id);
    }
    
    // Statistics methods
    public int getTotalFlashcards() {
        return flashcardDecks.values().stream()
                .mapToInt(FlashcardDeck::getCardCount)
                .sum();
    }
    
    public int getTotalQuizzes() {
        return quizzes.size();
    }
    
    public int getTotalCodeProblems() {
        return codeProblems.size();
    }
    
    public int getTotalNotes() {
        return notes.size();
    }
    
    public int getTotalTodoItems() {
        return todoItems.size();
    }
    
    public int getActiveTodoCount() {
        return (int) todoItems.values().stream()
                .filter(todo -> !todo.isCompleted())
                .count();
    }
    
    public int getCompletedTodoCount() {
        return (int) todoItems.values().stream()
                .filter(TodoItem::isCompleted)
                .count();
    }
    
    // Activity management methods
    public void logActivity(Activity activity) {
        activities.put(activity.getId(), activity);
        System.out.println("Activity logged: " + activity.getDescription());
    }
    
    /**
     * Logs a new activity for the current user
     */
    public void logUserActivity(String type, String description) {
        if (currentUser != null) {
            ActivityType activityType = ActivityType.valueOf(type);
            Activity activity = new Activity(currentUser.getId(), activityType, description);
            logActivity(activity);
        }
    }
    
    public List<Activity> getActivitiesForUser(String userId, LocalDate date) {
        return activities.values().stream()
                .filter(activity -> activity.getUserId().equals(userId) &&
                                     activity.getTimestamp().toLocalDate().equals(date))
                .sorted(Comparator.comparing(Activity::getTimestamp).reversed())
                .collect(Collectors.toList());
    }
    
    public List<Activity> getAllActivitiesForUser(String userId) {
        return activities.values().stream()
                .filter(activity -> activity.getUserId().equals(userId))
                .sorted(Comparator.comparing(Activity::getTimestamp).reversed())
                .collect(Collectors.toList());
    }
    
    public Map<String, Activity> getActivities() {
        return activities;
    }
}
