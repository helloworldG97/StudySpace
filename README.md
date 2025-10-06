# Study Space - Learning Management System

A comprehensive JavaFX-based learning management system designed to help students organize their study materials, practice coding, take quizzes, and track their academic progress.

## 🎯 Overview

Study Space is a modern desktop application built with JavaFX that provides a complete learning ecosystem for students. The application features a beautiful glassmorphism UI design with multiple study tools and progress tracking capabilities.

## 👥 Team

Developed by passionate computer engineering students from the University of Mindanao:

### 👨‍💻 **Daryl James Padogdog**
- Computer Engineering Student
- University of Mindanao

### 👩‍💻 **Irish Cassandra Gulen**
- Computer Engineering Student
- University of Mindanao

### 👩‍💻 **Kristina Cassandra Delos Santos**
- Computer Engineering Student
- University of Mindanao

**About the Team:**
We are a dedicated team of computer engineering students passionate about creating innovative learning solutions. Our goal is to empower students with modern, efficient, and engaging study tools that make learning more effective and enjoyable.

## ✨ Key Features

### 🔐 **Multi-User System with Complete Isolation**
- **User Registration & Authentication**: Secure account creation and login system
- **Complete User Isolation**: Each user has their own private content space
- **Session Management**: Secure user sessions with automatic logout
- **User-Specific Data**: Notes, flashcards, quizzes, todos, and files are completely private per user
- **Cross-User Security**: Users cannot access, modify, or delete other users' content

### 📚 Study Tools
- **AI-Powered Flashcards**: Generate flashcards from uploaded documents (PDF, DOC, PPT, etc.)
- **Smart Study Notes**: AI-generated structured notes from imported files
- **Interactive Quizzes**: Take quizzes with multiple choice questions and time limits
- **Code Practice**: Solve coding problems with multiple programming languages
- **To-Do Lists**: Manage tasks and assignments with priority levels and due dates

### 🤖 **AI Document Processing**
- **Document Upload**: Support for PDF, DOC, DOCX, PPT, PPTX files
- **AI Content Generation**: Automatic flashcard and note creation from documents
- **Smart Categorization**: AI organizes content by topics and difficulty
- **Educational Format**: Structured flashcards with questions and answers
- **Comprehensive Notes**: Academic-style outlines with key concepts and examples

### 🎮 Interactive Features
- **Snake Game**: Built-in entertainment with a classic Snake game
- **Progress Tracking**: Monitor study sessions and achievements per user
- **Activity Logging**: Track all learning activities and progress
- **User Statistics**: Individual progress metrics and study analytics

### 🎨 User Interface
- **Modern Glassmorphism Design**: Beautiful, modern UI with glass-like effects
- **Responsive Layout**: Adaptive interface that works on different screen sizes
- **Dark Theme**: Eye-friendly dark color scheme
- **Smooth Animations**: Fluid transitions and interactive elements
- **User Dashboard**: Personalized home screen with user-specific content

## 🏗️ Architecture

### Technology Stack
- **Java 21**: Modern Java with latest features
- **JavaFX 21**: Rich desktop application framework
- **Maven**: Dependency management and build tool
- **CSS3**: Advanced styling with glassmorphism effects
- **MySQL**: Relational database for data persistence
- **Python**: AI document processing backend
- **Flask**: REST API server for document processing
- **Qwen3-coder LLM**: AI model for content generation

### Project Structure
```
StudyAPPMain/
├── src/main/java/com/studyspace/     # JavaFX Application
│   ├── Main.java                     # Application entry point
│   ├── models/                       # Data models
│   │   ├── User.java                 # User account model
│   │   ├── Flashcard.java            # Flashcard model
│   │   ├── FlashcardDeck.java        # Flashcard collection
│   │   ├── Note.java                 # Study notes model
│   │   ├── Quiz.java                 # Quiz model
│   │   ├── Question.java             # Quiz question model
│   │   ├── CodeProblem.java          # Coding problem model
│   │   ├── TodoItem.java             # Task model
│   │   └── Activity.java             # Activity tracking model
│   ├── views/                        # User interface views
│   │   ├── AuthView.java             # Authentication interface
│   │   ├── FlashcardListView.java    # Flashcard management
│   │   ├── FlashcardStudyView.java   # Flashcard study interface
│   │   ├── NotesView.java            # Notes management
│   │   ├── QuizListView.java         # Quiz management
│   │   ├── QuizModeView.java         # Quiz taking interface
│   │   ├── CodePracticeView.java     # Coding practice interface
│   │   ├── TodoListView.java         # Task management
│   │   ├── SnakeGameView.java        # Snake game
│   │   └── AboutUsView.java          # About page
│   ├── auth/                         # Authentication system
│   │   ├── AuthController.java       # Authentication logic
│   │   ├── AuthView.java             # Login/signup interface
│   │   └── AuthViewController.java   # Auth view controller
│   ├── utils/                        # Utility classes
│   │   ├── DataStore.java            # Data persistence layer
│   │   ├── MySQLDataStore.java       # MySQL database integration
│   │   ├── DatabaseConnection.java   # Database connection management
│   │   ├── DocumentProcessingService.java # AI document processing
│   │   ├── SceneManager.java         # Navigation management
│   │   └── IconUtils.java            # Icon utilities
│   └── components/                   # Reusable UI components
│       └── SidebarView.java          # Navigation sidebar
├── ai-processor/                     # AI Document Processing Backend
│   ├── python/                       # Python AI processing
│   │   ├── document_processor.py     # Main AI processing engine
│   │   ├── api_server.py             # Flask REST API server
│   │   ├── config.json               # AI model configuration
│   │   └── requirements.txt          # Python dependencies
│   └── docs/                         # Documentation
│       ├── README_DOCUMENT_PROCESSOR.md
│       └── setup_document_processor.bat
├── java-app/database/                # Database setup
│   └── import_to_xampp.sql           # MySQL database schema
└── README.md                         # This file
```

## 🚀 Getting Started

### Prerequisites
- **Java 21 or higher**
- **Maven 3.6 or higher**
- **JavaFX 21 runtime**
- **Python 3.8 or higher** (for AI document processing)
- **MySQL 8.0 or higher** (for data persistence)
- **XAMPP** (recommended for MySQL setup)

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd StudyAPPMain
   ```

2. **Setup Database (XAMPP)**
   ```bash
   # Start XAMPP and MySQL service
   # Import the database schema
   mysql -u root -p < java-app/database/import_to_xampp.sql
   ```

3. **Setup AI Document Processor**
   ```bash
   cd ai-processor/python
   pip install -r requirements.txt
   # Configure AI model in config.json
   ```

4. **Start AI Processing Server**
   ```bash
   cd ai-processor/python
   python api_server.py
   # Server runs on http://localhost:5000
   ```

5. **Build the Java Application**
   ```bash
   mvn clean compile
   ```

6. **Run the Application**
   ```bash
   mvn javafx:run
   ```

### Alternative Run Methods

**Using Maven Wrapper (Windows)**
```bash
./mvnw.cmd clean javafx:run
```

**Using Maven Wrapper (Unix/Linux/Mac)**
```bash
./mvnw clean javafx:run
```

**Direct Java execution**
```bash
java --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml -cp target/classes com.studyspace.Main
```

## 🎨 Algorithm Visualizations

The Study Space system includes interactive HTML visualizations that demonstrate the sorting and searching algorithms used throughout the application. These visualizations help users understand how data is organized and processed.

### 📊 Available Visualizations

#### 1. **Working Algorithm Demo** (`working-algorithm-demo.html`)
- **Purpose**: Interactive demonstration of sorting algorithms with real Study Space data
- **Features**:
  - Color-coded algorithm visualization (Orange=Pivot, Blue=Index i, Green=Index j, Red=Swapping)
  - Real-time step-by-step process tracking
  - Multiple data types: Flashcards, Notes, Quizzes, Code Problems, Todo Items
  - Sorting algorithms: Bubble Sort, Quick Sort, Merge Sort, Insertion Sort, Selection Sort
  - Performance metrics and complexity analysis

#### 2. **Index Page** (`index.html`)
- **Purpose**: Main landing page with system overview and navigation
- **Features**:
  - System architecture overview
  - Feature demonstrations
  - Navigation to different components

### 🚀 How to Run the Visualizations

#### Quick Start (Recommended)
1. **Open Chrome browser**
2. **Navigate to the project root directory**
3. **Double-click on the HTML files:**
   - `working-algorithm-demo.html` - Interactive Algorithm Demo
   - `index.html` - System Architecture Overview
4. **The visualizations will load automatically in Chrome**


### 🎮 Using the Algorithm Demo

1. **Select Data Type**: Choose from Flashcards, Notes, Quizzes, Code Problems, or Todo Items
2. **Choose Algorithm**: Select from Bubble Sort, Quick Sort, Merge Sort, Insertion Sort, or Selection Sort
3. **Pick Sort Criteria**: Sort by Name, Difficulty, Value, or Date
4. **Start Visualization**: Click "Start Sorting" to generate the steps
5. **Control Playback**: Use Play, Pause, Step, or Reset controls
6. **Watch the Process**: Observe the color-coded elements showing:
   - 🟠 **Orange**: Pivot elements
   - 🔵 **Blue**: Index i (current position)
   - 🟢 **Green**: Index j (comparison position)
   - 🔴 **Red**: Elements being swapped
   - 🟡 **Yellow**: Elements being compared

### 🎯 Educational Value

These visualizations help users understand:
- **Algorithm Complexity**: See how different algorithms perform
- **Data Organization**: Understand how Study Space sorts and searches data
- **Step-by-Step Process**: Visualize each comparison and swap
- **Performance Metrics**: Track comparisons, swaps, and time complexity
- **Real Data Context**: See algorithms working with actual Study Space data types

### 🔧 Technical Details

- **Pure HTML/CSS/JavaScript**: No external dependencies required
- **Responsive Design**: Works on desktop, tablet, and mobile
- **Modern Browser Support**: Compatible with Chrome, Firefox, Safari, Edge
- **Interactive Controls**: Play, pause, step-through, and reset functionality
- **Real-time Updates**: Live performance metrics and status messages

## 📱 Features in Detail

### 🔐 **Multi-User Authentication System**
- **User Registration**: Create new accounts with email validation
- **Secure Login**: Password-based authentication with session management
- **User Isolation**: Complete data privacy - users cannot see each other's content
- **Session Management**: Automatic logout and secure session handling
- **User Profile Management**: Update personal information and preferences

### 🤖 **AI Document Processing System**
- **Document Upload**: Support for PDF, DOC, DOCX, PPT, PPTX files
- **AI Content Generation**: Automatic creation of flashcards and notes from documents
- **Smart Categorization**: AI organizes content by topics and difficulty levels
- **Educational Format**: Structured flashcards with clear questions and answers
- **Comprehensive Notes**: Academic-style outlines with key concepts and examples
- **User-Specific Processing**: All AI-generated content is private to the uploading user

### 🗄️ **Database & User Isolation**
- **MySQL Integration**: Persistent data storage with relational database
- **User-Specific Tables**: All content tables include user_id foreign keys
- **Complete Isolation**: Users can only access their own data
- **Cascade Deletion**: User data is automatically cleaned up when accounts are deleted
- **Performance Optimization**: Indexed queries for fast user-specific data retrieval

### 🃏 **Enhanced Flashcard System**
- **AI-Generated Flashcards**: Create flashcards automatically from uploaded documents
- **Custom Flashcard Decks**: Manual creation and organization by subject and difficulty
- **Interactive Study Sessions**: Spaced repetition and progress tracking
- **User-Specific Decks**: Each user has their own private flashcard collections
- **Smart Categorization**: AI organizes flashcards by topics and difficulty levels

### 📝 **Smart Notes Management**
- **AI-Generated Notes**: Automatic note creation from uploaded documents
- **Rich Text Editor**: Manual note creation with formatting options
- **Category Organization**: Organize notes by subjects and topics
- **Search and Filter**: Find notes quickly with advanced filtering
- **Pin Important Notes**: Mark important notes for quick access
- **User-Specific Notes**: Complete privacy - only you can see your notes

### ✅ **Interactive Quiz System**
- **AI-Generated Quizzes**: Create quizzes from uploaded documents
- **Multiple Choice Questions**: Time-limited quizzes with scoring
- **Difficulty Levels**: Easy, medium, and hard difficulty settings
- **Subject Categorization**: Organize quizzes by academic subjects
- **Progress Tracking**: Track quiz performance and improvement
- **User-Specific Quizzes**: Private quiz collections per user

### 💻 **Code Practice Platform**
- **Multiple Programming Languages**: Support for various coding languages
- **Test Case Validation**: Automated code testing and validation
- **Starter Code Templates**: Pre-written code templates for practice
- **Difficulty Progression**: Beginner to advanced coding challenges
- **Solution Tracking**: Monitor coding progress and achievements
- **User-Specific Problems**: Private coding practice sessions

### 📋 **Task Management System**
- **Priority-Based Organization**: High, medium, and low priority tasks
- **Due Date Tracking**: Set and track task deadlines
- **Category Filtering**: Organize tasks by subjects or projects
- **Progress Visualization**: Visual progress tracking and statistics
- **Completion Statistics**: Track productivity and task completion rates
- **User-Specific Tasks**: Private task lists per user

### 🎮 **Entertainment & Breaks**
- **Built-in Snake Game**: Classic Snake game for study breaks
- **Study Break Activities**: Relaxing games and activities
- **Achievement System**: Unlock achievements and track progress
- **User-Specific Statistics**: Individual gaming and study statistics

## 🎨 UI/UX Features

### Design System
- **Color Palette**: Purple/violet primary colors with glassmorphism effects
- **Typography**: Modern font stack with Segoe UI, Roboto, Helvetica Neue
- **Spacing**: Consistent padding and margins throughout
- **Animations**: Smooth transitions and hover effects

### Responsive Design
- Minimum window size: 1200x700
- Default window size: 1400x900
- Adaptive layouts for different screen sizes
- Scalable UI components

## 🔧 Configuration

### Application Settings
- Window dimensions and positioning
- Theme preferences
- Data storage location
- User session management

### Dependencies
The application uses several external libraries:
- **JavaFX Controls**: UI components
- **ControlsFX**: Additional UI controls
- **FormsFX**: Form building
- **ValidatorFX**: Input validation
- **Ikonli**: Icon library
- **BootstrapFX**: Bootstrap-style components
- **TilesFX**: Dashboard tiles
- **FXGL**: Game development framework

## 📊 Data Management & User Isolation

### 🔐 **Complete User Isolation Architecture**
- **Database-Level Isolation**: All content tables include `user_id` foreign keys
- **Application-Level Security**: All queries filter by current user ID
- **Session-Based Access**: Users can only access their own data
- **Cross-User Protection**: Users cannot view, modify, or delete other users' content
- **Cascade Deletion**: When a user is deleted, all their data is automatically removed

### 🗄️ **Database Schema**
- **MySQL Integration**: Relational database with proper foreign key constraints
- **User-Specific Tables**: 
  - `notes` (user_id, title, content, category)
  - `flashcard_decks` (user_id, title, description, subject)
  - `quizzes` (user_id, title, description, difficulty)
  - `todo_items` (user_id, title, description, priority)
  - `code_problems` (user_id, title, description, language)
  - `user_files` (user_id, file_name, file_path, file_type)
- **Performance Optimization**: Indexed queries for fast user-specific data retrieval
- **Data Integrity**: Foreign key constraints ensure data consistency

### 📱 **Data Storage**
- **Persistent Storage**: MySQL database for all user data
- **User Session Management**: Secure session handling with automatic logout
- **Activity Logging**: Track all user activities and progress
- **File Management**: User-specific file storage with metadata tracking

### 🎯 **Data Models**
- **User**: Account information, statistics, and session data
- **Flashcard**: Individual study cards with user ownership
- **FlashcardDeck**: Collections of flashcards per user
- **Note**: Study materials with user-specific content
- **Quiz**: Assessment tools with user-specific questions
- **Question**: Quiz questions linked to user-owned quizzes
- **CodeProblem**: Programming challenges per user
- **TodoItem**: Task management with user ownership
- **Activity**: User activity tracking and logging
- **UserFile**: File metadata and user ownership tracking

## 🧪 Testing

### Running Tests
```bash
mvn test
```

### Test Structure
- Unit tests for models
- Integration tests for views
- UI component testing

## 🚀 Deployment

### Building for Distribution
```bash
mvn clean package
```

### Creating Executable JAR
```bash
mvn clean compile assembly:single
```

### JavaFX Native Packaging
```bash
mvn clean javafx:jlink
```

## 🤝 Contributing

### Development Setup
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

### Code Style
- Follow Java naming conventions
- Use meaningful variable names
- Add comprehensive comments
- Maintain consistent formatting

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

For support and questions:
- Create an issue in the repository
- Contact the development team
- Check the documentation

## 🤖 AI Document Processing

### 🚀 **AI-Powered Content Generation**
- **Document Upload**: Support for PDF, DOC, DOCX, PPT, PPTX files
- **AI Model**: Qwen3-coder LLM for intelligent content processing
- **Automatic Processing**: Extract text and generate educational content
- **User-Specific Generation**: All AI-generated content is private to the uploading user

### 📚 **Smart Flashcard Generation**
- **Educational Format**: Structured flashcards with clear questions and answers
- **Multiple Types**: Definition, Cause-and-Effect, Comparison, Example, and Law/Act cards
- **Smart Categorization**: AI organizes flashcards by topics and difficulty
- **Bulk Generation**: Create 10-30 flashcards from a single document
- **User Isolation**: Each user gets their own private flashcard collections

### 📝 **Intelligent Note Creation**
- **Academic Structure**: Well-organized outlines with main topics and bullet points
- **Comprehensive Coverage**: Historical context, examples, and importance
- **Logical Flow**: From overview to development to impact to solutions
- **Educational Tone**: Academic yet easy-to-understand language
- **User-Specific Notes**: Private note collections per user

### 🔧 **Technical Implementation**
- **Python Backend**: Flask REST API server for document processing
- **Java Integration**: Seamless communication between JavaFX app and Python API
- **File Processing**: Extract text from various document formats
- **AI Configuration**: Configurable AI model settings and parameters
- **Error Handling**: Robust error handling and fallback mechanisms

## 🔮 Future Enhancements

### Planned Features
- **Advanced AI Models**: Integration with more sophisticated AI models
- **Cloud Synchronization**: Sync data across multiple devices
- **Mobile Companion App**: iOS and Android applications
- **Advanced Analytics**: Detailed study analytics and insights
- **Collaborative Features**: Share content with study groups
- **Plugin System**: Extensible architecture for custom features
- **Custom Themes**: More UI theme options
- **Export/Import**: Backup and restore functionality
- **Offline Mode**: Work without internet connection

### Technical Improvements
- **Microservices Architecture**: Scalable backend services
- **Performance Optimizations**: Faster processing and response times
- **Security Enhancements**: Advanced security features
- **API Documentation**: Comprehensive API documentation
- **Automated Testing**: Comprehensive test coverage

## 📈 Performance

### System Requirements
- **RAM**: 4GB minimum, 8GB recommended
- **Storage**: 500MB available space
- **OS**: Windows 10+, macOS 10.14+, Linux
- **Java**: Version 21 or higher

### Optimization Features
- Lazy loading of UI components
- Efficient data caching
- Memory management
- Smooth animations
- Responsive design

## 🔐 User Isolation Examples

### 👥 **Multi-User Scenarios**

**Scenario 1: Two Students Using the System**
```
Student A (alice@university.edu):
├── Notes: "Java Programming Basics", "Data Structures"
├── Flashcards: "Java Concepts" (25 cards), "Algorithms" (18 cards)
├── Quizzes: "Java Fundamentals", "Data Structures Quiz"
├── Todos: "Complete Java Assignment", "Study for Midterm"
└── Files: "lecture_notes.pdf", "assignment_requirements.docx"

Student B (bob@university.edu):
├── Notes: "Physics Laws", "Chemistry Reactions"
├── Flashcards: "Physics Formulas" (30 cards), "Chemistry Elements" (20 cards)
├── Quizzes: "Physics Test", "Chemistry Quiz"
├── Todos: "Physics Lab Report", "Chemistry Homework"
└── Files: "physics_textbook.pdf", "lab_manual.pdf"
```

**Complete Isolation**: Student A cannot see any of Student B's content, and vice versa.

### 🛡️ **Security Features**

- **Database Queries**: All queries include `WHERE user_id = ?` filtering
- **Authentication Required**: Users must be logged in to access any data
- **Ownership Validation**: Users can only modify their own content
- **Cascade Protection**: Deleting a user removes all their data
- **Session Security**: Automatic logout and secure session management

### 📊 **User Data Structure**
```
Database Tables with User Isolation:
├── users (id, full_name, email, password, created_at)
├── notes (id, user_id, title, content, category)
├── flashcard_decks (id, user_id, title, description, subject)
├── flashcards (id, deck_id, question, answer)
├── quizzes (id, user_id, title, description, difficulty)
├── questions (id, quiz_id, question_text, options)
├── todo_items (id, user_id, title, description, priority)
├── code_problems (id, user_id, title, description, language)
├── user_files (id, user_id, file_name, file_path, file_type)
└── activities (id, user_id, activity_type, description)
```

---

**Study Space** - Empowering students with modern learning tools, AI-powered content generation, and complete user privacy. 🚀📚🤖
