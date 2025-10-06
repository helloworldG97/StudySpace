-- =====================================================
-- Study Space Database - XAMPP Import Script
-- Fixed version with proper file storage and user isolation
-- =====================================================

-- Create database
CREATE DATABASE IF NOT EXISTS studyspace_db;
USE studyspace_db;

-- =====================================================
-- Core Tables
-- =====================================================

-- Users Table
CREATE TABLE users (
    id VARCHAR(50) PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP NULL,
    flashcards_studied INT DEFAULT 0,
    code_problems_completed INT DEFAULT 0,
    quizzes_taken INT DEFAULT 0,
    current_streak INT DEFAULT 0,
    total_study_hours INT DEFAULT 0
);

-- **NEW: User Files Table - Stores uploaded files with user ownership**
CREATE TABLE user_files (
    id VARCHAR(50) PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_type VARCHAR(100),
    file_size BIGINT,
    category VARCHAR(100),
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_accessed TIMESTAMP NULL,
    is_shared BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_files_user_id (user_id),
    INDEX idx_user_files_category (category)
);

-- Notes Table
CREATE TABLE notes (
    id VARCHAR(50) PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT,
    category VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_pinned BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_notes_user_id (user_id)
);

-- Flashcard Decks Table
CREATE TABLE flashcard_decks (
    id VARCHAR(50) PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    subject VARCHAR(100),
    difficulty ENUM('EASY', 'MEDIUM', 'HARD') DEFAULT 'MEDIUM',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_studied TIMESTAMP NULL,
    total_study_sessions INT DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_flashcard_decks_user_id (user_id)
);

-- Flashcards Table
CREATE TABLE flashcards (
    id VARCHAR(50) PRIMARY KEY,
    deck_id VARCHAR(50),
    question TEXT NOT NULL,
    answer TEXT NOT NULL,
    difficulty ENUM('EASY', 'MEDIUM', 'HARD') DEFAULT 'MEDIUM',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_studied TIMESTAMP NULL,
    times_studied INT DEFAULT 0,
    is_correct BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (deck_id) REFERENCES flashcard_decks(id) ON DELETE CASCADE
);

-- Quizzes Table
CREATE TABLE quizzes (
    id VARCHAR(50) PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    subject VARCHAR(100),
    difficulty ENUM('EASY', 'MEDIUM', 'HARD') DEFAULT 'MEDIUM',
    time_limit INT DEFAULT 30,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_taken TIMESTAMP NULL,
    best_score INT DEFAULT 0,
    times_taken INT DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_quizzes_user_id (user_id)
);

-- Questions Table
CREATE TABLE questions (
    id VARCHAR(50) PRIMARY KEY,
    quiz_id VARCHAR(50),
    question_text TEXT NOT NULL,
    options JSON,
    correct_option_index INT NOT NULL,
    explanation TEXT,
    difficulty ENUM('EASY', 'MEDIUM', 'HARD') DEFAULT 'MEDIUM',
    FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE
);

-- Code Problems Table
CREATE TABLE code_problems (
    id VARCHAR(50) PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    difficulty ENUM('EASY', 'MEDIUM', 'HARD') DEFAULT 'MEDIUM',
    language VARCHAR(50) DEFAULT 'JavaScript',
    starter_code TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_attempted TIMESTAMP NULL,
    is_completed BOOLEAN DEFAULT FALSE,
    attempts INT DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_code_problems_user_id (user_id)
);

-- Todo Items Table
CREATE TABLE todo_items (
    id VARCHAR(50) PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    category VARCHAR(100),
    is_completed BOOLEAN DEFAULT FALSE,
    priority ENUM('LOW', 'MEDIUM', 'HIGH') DEFAULT 'MEDIUM',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP NULL,
    due_date DATE NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_todo_items_user_id (user_id)
);

-- Activities Table
CREATE TABLE activities (
    id VARCHAR(50) PRIMARY KEY,
    user_id VARCHAR(50),
    activity_type VARCHAR(50) NOT NULL,
    description TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- =====================================================
-- Sample Data
-- =====================================================

-- Sample Users
INSERT INTO users (id, full_name, email, password) VALUES 
('user_001', 'John Doe', 'john@example.com', 'password123'),
('user_002', 'Jane Smith', 'jane@example.com', 'password456');

-- Sample Notes
INSERT INTO notes (id, user_id, title, content, category) VALUES
('note_001', 'user_001', 'Java Basics', 'Java is an object-oriented programming language...', 'Programming'),
('note_002', 'user_001', 'Data Structures', 'Arrays, linked lists, stacks, and queues...', 'Computer Science');

-- Sample Flashcard Deck
INSERT INTO flashcard_decks (id, user_id, title, description, subject, difficulty) VALUES
('deck_001', 'user_001', 'Java Programming', 'Basic Java concepts', 'Programming', 'MEDIUM');

-- Sample Flashcards
INSERT INTO flashcards (id, deck_id, question, answer, difficulty) VALUES
('card_001', 'deck_001', 'What is Java?', 'A programming language', 'EASY'),
('card_002', 'deck_001', 'What is inheritance?', 'A mechanism to create new classes from existing ones', 'MEDIUM');

-- Sample Quiz
INSERT INTO quizzes (id, user_id, title, description, subject, difficulty) VALUES
('quiz_001', 'user_001', 'Java Quiz', 'Test your Java knowledge', 'Programming', 'MEDIUM');

-- Sample Questions
INSERT INTO questions (id, quiz_id, question_text, options, correct_option_index, explanation) VALUES
('q_001', 'quiz_001', 'What is Java?', '["Programming Language", "Database", "Operating System", "Browser"]', 0, 'Java is a programming language');

-- Sample Code Problem
INSERT INTO code_problems (id, user_id, title, description, difficulty, language) VALUES
('prob_001', 'user_001', 'Two Sum', 'Find two numbers that add up to target', 'MEDIUM', 'JavaScript');

-- Sample Todo
INSERT INTO todo_items (id, user_id, title, description, category, priority) VALUES
('todo_001', 'user_001', 'Study Java', 'Review Java concepts', 'Study', 'HIGH');

-- Sample Activity
INSERT INTO activities (id, user_id, activity_type, description) VALUES
('act_001', 'user_001', 'QUIZ_TAKEN', 'Completed Java Quiz');

-- Sample Files (demonstrating user isolation)
INSERT INTO user_files (id, user_id, file_name, file_path, file_type, file_size, category) VALUES
('file_001', 'user_001', 'study_notes.pdf', '/uploads/user_001/study_notes.pdf', 'application/pdf', 2048576, 'Notes'),
('file_002', 'user_002', 'presentation.pptx', '/uploads/user_002/presentation.pptx', 'application/vnd.ms-powerpoint', 4096000, 'Presentations');

SELECT 'Database imported successfully with file storage!' as Status;