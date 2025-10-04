# Study Space - Learning Management System

A comprehensive JavaFX-based learning management system designed to help students organize their study materials, practice coding, take quizzes, and track their academic progress.

## 🎯 Overview

Study Space is a modern desktop application built with JavaFX that provides a complete learning ecosystem for students. The application features a beautiful glassmorphism UI design with multiple study tools and progress tracking capabilities.

## ✨ Key Features

### 📚 Study Tools
- **Flashcards**: Create and study with interactive flashcards
- **Study Notes**: Organize and manage study materials with rich text support
- **Quizzes**: Take interactive quizzes with multiple choice questions
- **Code Practice**: Solve coding problems with multiple programming languages
- **To-Do Lists**: Manage tasks and assignments with priority levels

### 🎮 Interactive Features
- **Snake Game**: Built-in entertainment with a classic Snake game
- **Progress Tracking**: Monitor study sessions and achievements
- **Activity Logging**: Track all learning activities and progress

### 🎨 User Interface
- **Modern Glassmorphism Design**: Beautiful, modern UI with glass-like effects
- **Responsive Layout**: Adaptive interface that works on different screen sizes
- **Dark Theme**: Eye-friendly dark color scheme
- **Smooth Animations**: Fluid transitions and interactive elements

## 🏗️ Architecture

### Technology Stack
- **Java 21**: Modern Java with latest features
- **JavaFX 21**: Rich desktop application framework
- **Maven**: Dependency management and build tool
- **CSS3**: Advanced styling with glassmorphism effects

### Project Structure
```
src/main/java/com/studyspace/
├── Main.java                    # Application entry point
├── models/                      # Data models
│   ├── User.java               # User account model
│   ├── Flashcard.java          # Flashcard model
│   ├── FlashcardDeck.java      # Flashcard collection
│   ├── Note.java               # Study notes model
│   ├── Quiz.java               # Quiz model
│   ├── Question.java           # Quiz question model
│   ├── CodeProblem.java        # Coding problem model
│   ├── TodoItem.java           # Task model
│   └── Activity.java           # Activity tracking model
├── views/                      # User interface views
│   ├── AuthView.java           # Authentication interface
│   ├── FlashcardListView.java  # Flashcard management
│   ├── FlashcardStudyView.java # Flashcard study interface
│   ├── NotesView.java          # Notes management
│   ├── QuizListView.java       # Quiz management
│   ├── QuizModeView.java       # Quiz taking interface
│   ├── CodePracticeView.java   # Coding practice interface
│   ├── TodoListView.java       # Task management
│   ├── SnakeGameView.java      # Snake game
│   └── AboutUsView.java        # About page
├── auth/                       # Authentication system
│   ├── AuthController.java     # Authentication logic
│   ├── AuthView.java           # Login/signup interface
│   └── AuthViewController.java # Auth view controller
├── utils/                      # Utility classes
│   ├── DataStore.java       # Data persistence layer
│   ├── SceneManager.java      # Navigation management
│   └── IconUtils.java         # Icon utilities
└── components/                 # Reusable UI components
    └── SidebarView.java       # Navigation sidebar
```

## 🚀 Getting Started

### Prerequisites
- Java 21 or higher
- Maven 3.6 or higher
- JavaFX 21 runtime

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd StudyAPPMain
   ```

2. **Build the project**
   ```bash
   mvn clean compile
   ```

3. **Run the application**
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

#### Alternative Methods
If you prefer using a local server:
```bash
# Using Python 3
python -m http.server 8000

# Using Node.js (if you have it installed)
npx http-server

# Using PHP (if you have it installed)
php -S localhost:8000
```

Then open your browser and navigate to:
- `http://localhost:8000/working-algorithm-demo.html` - Algorithm Demo
- `http://localhost:8000/index.html` - System Overview

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

### 🔐 Authentication System
- User registration and login
- Secure password handling
- Session management
- User profile management

### 🃏 Flashcard System
- Create custom flashcard decks
- Organize by subject and difficulty
- Interactive study sessions
- Progress tracking
- Spaced repetition support

### 📝 Notes Management
- Rich text note creation
- Category organization
- Search and filter functionality
- Tag-based organization
- Pin important notes

### ✅ Quiz System
- Multiple choice questions
- Time-limited quizzes
- Score tracking
- Difficulty levels
- Subject categorization

### 💻 Code Practice
- Multiple programming languages
- Test case validation
- Starter code templates
- Difficulty progression
- Solution tracking

### 📋 Task Management
- Priority-based task organization
- Due date tracking
- Category filtering
- Progress visualization
- Completion statistics

### 🎮 Entertainment
- Built-in Snake game
- Study break activities
- Achievement system

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

## 📊 Data Management

### Data Storage
- In-memory data storage (DataStore singleton)
- User session persistence
- Activity logging
- Progress tracking

### Data Models
- **User**: Account information and statistics
- **Flashcard**: Individual study cards
- **Note**: Study materials
- **Quiz**: Assessment tools
- **CodeProblem**: Programming challenges
- **TodoItem**: Task management
- **Activity**: User activity tracking

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

## 👥 Team

Developed by the Study Space development team at University of Mindanao.

## 🆘 Support

For support and questions:
- Create an issue in the repository
- Contact the development team
- Check the documentation

## 🔮 Future Enhancements

### Planned Features
- Cloud synchronization
- Mobile companion app
- Advanced analytics
- Collaborative features
- Plugin system
- Custom themes
- Export/import functionality
- Offline mode improvements

### Technical Improvements
- Database integration
- REST API development
- Microservices architecture
- Performance optimizations
- Security enhancements

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

---

**Study Space** - Empowering students with modern learning tools and beautiful interfaces. 🚀📚
