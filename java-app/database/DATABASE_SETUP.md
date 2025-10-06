# Study Space Database Setup for XAMPP

This guide will help you set up the Study Space database in XAMPP.

## Prerequisites

- XAMPP installed and running
- MySQL service started in XAMPP Control Panel
- phpMyAdmin accessible (usually at http://localhost/phpmyadmin)

## Setup Instructions

### Method 1: Using phpMyAdmin (Recommended)

1. **Start XAMPP Services**
   - Open XAMPP Control Panel
   - Start Apache and MySQL services

2. **Access phpMyAdmin**
   - Open your browser and go to `http://localhost/phpmyadmin`
   - Login with your XAMPP credentials (usually no password needed)

3. **Import Database**
   - Click on "Import" tab in phpMyAdmin
   - Click "Choose File" and select `import_to_xampp.sql`
   - Click "Go" to execute the import

### Method 2: Using MySQL Command Line

1. **Open Command Prompt/Terminal**
   - Navigate to your XAMPP MySQL bin directory
   - Usually: `C:\xampp\mysql\bin\` (Windows) or `/Applications/XAMPP/mysql/bin/` (Mac)

2. **Run Import Command**
   ```bash
   mysql -u root -p < import_to_xampp.sql
   ```
   - Enter password when prompted (usually empty for XAMPP)

### Method 3: Using Full Database Schema

If you want the complete database with all features:

1. **Import Full Schema**
   - Use `studyspace_database.sql` instead of `import_to_xampp.sql`
   - This includes all tables, views, stored procedures, and sample data

## Database Structure

### Core Tables

- **users** - User accounts and study statistics
- **notes** - Study notes and content
- **flashcard_decks** - Collections of flashcards
- **flashcards** - Individual flashcard questions and answers
- **quizzes** - Quiz information and settings
- **questions** - Quiz questions with multiple choice options
- **code_problems** - Programming challenges
- **todo_items** - Task management
- **activities** - User activity tracking

### Key Features

- **User Management**: Complete user profiles with study statistics
- **Study Materials**: Notes, flashcards, and quizzes
- **Progress Tracking**: Activity logs and study statistics
- **Task Management**: Todo items with priorities and due dates
- **Code Practice**: Programming problems with test cases

## Sample Data

The database includes sample data for testing:

- 1 sample user (John Doe)
- 2 sample notes (Java Basics, Data Structures)
- 1 flashcard deck with 2 cards
- 1 quiz with 1 question
- 1 code problem
- 1 todo item
- 1 activity record

## Verification

After importing, verify the setup:

1. **Check Database**
   ```sql
   SHOW DATABASES;
   USE studyspace_db;
   SHOW TABLES;
   ```

2. **Check Sample Data**
   ```sql
   SELECT * FROM users;
   SELECT * FROM notes;
   SELECT * FROM flashcard_decks;
   ```

## Connection Details

- **Host**: localhost
- **Port**: 3306 (default MySQL port)
- **Database**: studyspace_db
- **Username**: root
- **Password**: (usually empty for XAMPP)

## Troubleshooting

### Common Issues

1. **Import Fails**
   - Check MySQL service is running
   - Verify file path is correct
   - Check for syntax errors in SQL file

2. **Permission Denied**
   - Run XAMPP as administrator (Windows)
   - Check MySQL user permissions

3. **Database Already Exists**
   - Drop existing database: `DROP DATABASE studyspace_db;`
   - Then re-import

### Useful Commands

```sql
-- Check database exists
SHOW DATABASES LIKE 'studyspace_db';

-- Check table structure
DESCRIBE users;

-- Check sample data
SELECT COUNT(*) FROM users;
SELECT COUNT(*) FROM notes;
SELECT COUNT(*) FROM flashcard_decks;
```

## Next Steps

1. **Update Java Application**
   - Modify DataStore.java to use MySQL instead of file storage
   - Add MySQL JDBC driver to project dependencies
   - Update connection strings to point to XAMPP MySQL

2. **Configure Connection**
   - Update database connection settings in your Java application
   - Test connection with sample queries

3. **Data Migration**
   - If you have existing data, create migration scripts
   - Test data integrity after migration

## Support

If you encounter issues:

1. Check XAMPP error logs
2. Verify MySQL service status
3. Check phpMyAdmin for any error messages
4. Ensure all SQL syntax is correct

The database is now ready for your Study Space application!
