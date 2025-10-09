# Building StudySpace Application to .exe

This guide explains how to build your StudySpace JavaFX application into a Windows executable (.exe) file.

## Prerequisites

1. **Java 24 JDK** - Make sure you have Java 24 (or Java 21+) installed and added to your PATH
2. **Maven** - Should be installed and available in PATH
   - Download from: https://maven.apache.org/download.cgi
   - Extract and add to PATH environment variable
   - Or use the Maven wrapper: `mvnw` (included in project)
3. **Windows 10/11** - jpackage works on Windows 10 and later

## Build Options

### Option 1: Windows Installer (.exe)
Creates a Windows installer that installs the application to Program Files and creates shortcuts.

```bash
# Run the installer build script
build-exe.bat
```

**Output:** `target\dist\StudySpace-1.0.0.exe` (Windows installer)

### Option 2: Portable Executable (.exe)
Creates a portable .exe file that runs without installation.

```bash
# Run the portable build script
build-portable-exe.bat
```

**Output:** `target\dist\StudySpace.exe` (Portable executable)

## Manual Build Commands

If you prefer to run the commands manually:

```bash
# 1. Clean previous builds
mvn clean

# 2. Compile the application
mvn compile

# 3. Create custom runtime image
mvn javafx:jlink

# 4. Create executable (installer)
mvn jpackage:jpackage

# OR for portable version:
mvn jpackage:jpackage -Djpackage.type=exe -Djpackage.winDirChooser=false -Djpackage.winMenu=false -Djpackage.winShortcut=false
```

## What Gets Created

### Windows Installer Version
- **File:** `StudySpace-1.0.0.exe`
- **Features:**
  - Installs to Program Files
  - Creates Start Menu shortcuts
  - Creates Desktop shortcut (optional)
  - Registers with Windows
  - Uninstaller included

### Portable Version
- **File:** `StudySpace.exe`
- **Features:**
  - No installation required
  - Can be copied to any Windows machine
  - Self-contained with all dependencies
  - No registry modifications

## File Structure After Build

```
target/
├── dist/
│   ├── StudySpace-1.0.0.exe          # Windows installer
│   └── StudySpace/                    # Application folder
│       ├── StudySpace.exe             # Main executable
│       ├── runtime/                   # Java runtime
│       └── app/                       # Application files
└── jlink-image/                       # Custom Java runtime
```

## Troubleshooting

### Common Issues

1. **"Java 21 not found"**
   - Install Java 21 JDK
   - Add Java to your PATH environment variable
   - Verify with: `java -version`

2. **"jpackage command not found"**
   - Ensure you're using Java 21 (jpackage is included)
   - Check that JAVA_HOME points to Java 21

3. **"Module not found" errors**
   - Check `module-info.java` for missing dependencies
   - Ensure all required modules are declared

4. **Build fails with "jlink" errors**
   - Clean the project: `mvn clean`
   - Check that all dependencies are properly declared in `module-info.java`

### Performance Tips

- The first build will take longer as it downloads dependencies
- Subsequent builds will be faster
- The resulting .exe file will be large (100-200MB) as it includes the Java runtime

## Distribution

### For End Users
- **Installer version:** Best for regular users who want a proper Windows application
- **Portable version:** Best for users who want to run without installation

### System Requirements for End Users
- Windows 10 or later
- No Java installation required (included in the .exe)
- Minimum 200MB free disk space

## Advanced Configuration

You can modify the build configuration in `pom.xml`:

```xml
<plugin>
    <groupId>org.panteleyev</groupId>
    <artifactId>jpackage-maven-plugin</artifactId>
    <configuration>
        <name>StudySpace</name>                    <!-- Application name -->
        <appVersion>1.0.0</appVersion>             <!-- Version -->
        <vendor>StudySpace Team</vendor>           <!-- Vendor name -->
        <destination>target/dist</destination>     <!-- Output directory -->
        <!-- Add custom icon -->
        <icon>src/main/resources/images/logo.png</icon>
        <!-- Add custom JVM options -->
        <javaOptions>
            <option>-Xmx2g</option>
        </javaOptions>
    </configuration>
</plugin>
```

## Support

If you encounter issues:
1. Check the console output for specific error messages
2. Ensure all prerequisites are installed
3. Try cleaning and rebuilding: `mvn clean compile javafx:jlink jpackage:jpackage`
4. Check that your `module-info.java` includes all required dependencies
