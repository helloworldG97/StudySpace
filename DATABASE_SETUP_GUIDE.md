# Database Setup Guide for StudySpace

## 🔍 **Why Database Connection Fails**

Your application tries to connect to MySQL database with these settings:
- **Host**: localhost:3306
- **Database**: studyspace_db
- **User**: root
- **Password**: (empty)

## 🚀 **Quick Solution: Use Offline Mode (Recommended)**

The application I just built automatically handles database failures! It will:
- ✅ Detect when database is not available
- ✅ Switch to offline mode automatically
- ✅ Show demo credentials dialog
- ✅ Work perfectly without any setup

**Just use these credentials:**
- Email: `demo@studyspace.com`
- Password: `demo123`

## 🛠️ **Option 1: Set Up MySQL Database (For Persistent Data)**

### Step 1: Install MySQL
1. Download MySQL from: https://dev.mysql.com/downloads/mysql/
2. Install with default settings
3. Remember the root password you set

### Step 2: Create Database
1. Open MySQL Command Line Client or MySQL Workbench
2. Run the setup script: `setup-database.sql`
3. Or manually create database:
   ```sql
   CREATE DATABASE studyspace_db;
   USE studyspace_db;
   ```

### Step 3: Update Connection Settings (if needed)
If your MySQL setup is different, edit `DatabaseConnection.java`:
```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/studyspace_db";
private static final String DB_USER = "root";
private static final String DB_PASSWORD = "your_password_here";
```

### Step 4: Rebuild Application
```bash
.\build-simple-exe.bat
```

## 🔧 **Option 2: Use Different Database**

### SQLite (No Server Required)
1. Add SQLite dependency to `pom.xml`
2. Update `DatabaseConnection.java` to use SQLite
3. No server setup needed!

### PostgreSQL
1. Install PostgreSQL
2. Update connection settings
3. Run setup script with PostgreSQL syntax

## 🎯 **Recommended Approach**

**For Development/Demo**: Use the offline mode (already implemented)
**For Production**: Set up MySQL database with the provided script

## 📋 **Troubleshooting**

### Common Issues:
1. **"Connection refused"** → MySQL not running
2. **"Access denied"** → Wrong username/password
3. **"Database doesn't exist"** → Run setup-database.sql
4. **"Port 3306 in use"** → MySQL already running or port conflict

### Check MySQL Status:
```bash
# Windows
net start mysql

# Check if MySQL is running
netstat -an | findstr 3306
```

## 🎉 **Current Status**

Your application now works in **both modes**:
- ✅ **Online mode**: When database is available
- ✅ **Offline mode**: When database is not available (automatic fallback)

No more login errors! The app handles everything gracefully.

