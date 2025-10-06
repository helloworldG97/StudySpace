@echo off
echo ========================================
echo Study Space Document Processor Setup
echo ========================================
echo.

echo 📦 Installing Python requirements...
python -m pip install -r requirements.txt
if %errorlevel% neq 0 (
    echo ❌ Error installing requirements
    pause
    exit /b 1
)

echo.
echo ✅ Python requirements installed successfully!
echo.

echo 🔍 Checking Ollama installation...
curl -s http://localhost:11434/api/tags > nul 2>&1
if %errorlevel% neq 0 (
    echo ⚠️  Ollama is not running or not installed
    echo.
    echo Please:
    echo 1. Install Ollama from https://ollama.ai/
    echo 2. Run: ollama pull qwen3-coder:480b-cloud
    echo 3. Start Ollama service
    echo.
    pause
    exit /b 1
)

echo ✅ Ollama is running
echo.

echo 🔍 Checking database connection...
python -c "from document_processor import DocumentProcessor; p = DocumentProcessor(); print('✅ Database connected' if p.connect_database() else '❌ Database connection failed')"
if %errorlevel% neq 0 (
    echo ❌ Database connection failed
    echo Please make sure XAMPP is running and import_to_xampp.sql is imported
    pause
    exit /b 1
)

echo.
echo ========================================
echo ✅ Setup completed successfully!
echo ========================================
echo.
echo 🚀 To start the document processor:
echo    python api_server.py
echo.
echo 🚀 To start the Java application:
echo    .\mvnw.cmd javafx:run
echo.
pause
