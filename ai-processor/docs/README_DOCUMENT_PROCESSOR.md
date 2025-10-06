# 🤖 AI Document Processor for Study Space

This Python-based document processor integrates with your Study Space application to automatically extract content from PDF, PPT, and DOC files, process it with your local Qwen3-coder LLM, and create intelligent flashcards and notes.

## ✨ Features

- **📄 Multi-format Support**: PDF, PowerPoint (.ppt/.pptx), Word (.doc/.docx)
- **🧠 AI Processing**: Uses Qwen3-coder:480b-cloud for intelligent content analysis
- **🃏 Auto Flashcard Generation**: Creates flashcards from definitions and key topics
- **📝 Smart Notes**: Generates structured study notes
- **🗄️ Database Integration**: Automatically saves to MySQL database
- **🌐 REST API**: Easy integration with Java application

## 🚀 Quick Setup

### 1. Install Ollama and Qwen3-coder Model

```bash
# Install Ollama from https://ollama.ai/
# Then pull the model:
ollama pull qwen3-coder:480b-cloud
```

### 2. Setup Python Environment

```bash
# Install Python dependencies
pip install -r requirements.txt

# Or run the setup script
python setup.py
```

### 3. Configure Database

Make sure your MySQL database is running with the `import_to_xampp.sql` imported.

Update `config.json` if needed:
```json
{
    "database": {
        "host": "localhost",
        "port": 3306,
        "user": "root",
        "password": "",
        "database": "studyspace_db"
    }
}
```

### 4. Start the Services

```bash
# Terminal 1: Start the Python API server
python api_server.py

# Terminal 2: Start the Java application
.\mvnw.cmd javafx:run
```

## 📁 File Structure

```
StudyAPPMain/
├── document_processor.py      # Main processing logic
├── api_server.py              # Flask REST API server
├── config.json               # Configuration file
├── requirements.txt           # Python dependencies
├── setup.py                  # Setup script
├── setup_document_processor.bat  # Windows setup script
└── README_DOCUMENT_PROCESSOR.md  # This file
```

## 🔧 API Endpoints

### Process Document
```http
POST /process
Content-Type: multipart/form-data

file: [document file]
```

**Response:**
```json
{
    "success": true,
    "message": "Document processed successfully",
    "data": {
        "note_title": "AI-Generated Note Title",
        "deck_title": "AI-Generated Deck Title",
        "flashcards_created": 15,
        "summary": "Document summary...",
        "subject": "Programming",
        "difficulty": "Medium"
    }
}
```

### Health Check
```http
GET /health
```

### Service Status
```http
GET /status
```

## 🎯 How It Works

1. **📄 Document Upload**: User selects PDF/PPT/DOC file in Java application
2. **🔄 API Call**: Java app sends file to Python API server
3. **📖 Content Extraction**: Python extracts text using PyPDF2, python-pptx, python-docx
4. **🧠 AI Processing**: Content sent to Qwen3-coder LLM for analysis
5. **📝 Structured Output**: LLM returns structured JSON with:
   - Title and summary
   - Key topics and definitions
   - Study notes
   - Difficulty and subject classification
6. **💾 Database Storage**: Python saves to MySQL database:
   - Creates note entry
   - Creates flashcard deck
   - Generates flashcards from definitions and topics
7. **🔄 UI Update**: Java app refreshes to show new content

## 🛠️ Configuration

### config.json
```json
{
    "database": {
        "host": "localhost",
        "port": 3306,
        "user": "root",
        "password": "",
        "database": "studyspace_db"
    },
    "llm_endpoint": "http://localhost:11434",
    "llm_model": "qwen3-coder:480b-cloud",
    "supported_formats": [".pdf", ".ppt", ".pptx", ".doc", ".docx"]
}
```

## 🐛 Troubleshooting

### Ollama Not Running
```bash
# Check if Ollama is running
curl http://localhost:11434/api/tags

# Start Ollama service
ollama serve
```

### Database Connection Issues
- Ensure XAMPP is running
- Check MySQL credentials in config.json
- Verify `import_to_xampp.sql` is imported

### Python Dependencies
```bash
# Reinstall requirements
pip install -r requirements.txt --force-reinstall
```

### API Server Issues
```bash
# Check if API server is running
curl http://localhost:8000/health

# Check logs for errors
python api_server.py
```

## 📊 Performance

- **Processing Time**: 10-30 seconds per document (depending on size and LLM response time)
- **File Size Limit**: 50MB maximum
- **Supported Formats**: PDF, PPT, PPTX, DOC, DOCX
- **Flashcard Generation**: 5-15 flashcards per document
- **Database Storage**: Automatic with MySQL integration

## 🔒 Security

- Local processing (no data sent to external services)
- File size limits to prevent abuse
- Input validation and error handling
- CORS enabled for Java application integration

## 📈 Future Enhancements

- [ ] Support for more document formats
- [ ] Batch processing capabilities
- [ ] Custom LLM prompts
- [ ] Processing queue management
- [ ] Advanced content filtering
- [ ] Multi-language support

## 🤝 Integration

The document processor seamlessly integrates with your Study Space Java application:

1. **Automatic Detection**: Java app detects when Python service is available
2. **Fallback Handling**: Graceful degradation if service is unavailable
3. **Real-time Updates**: UI refreshes automatically after processing
4. **Error Handling**: User-friendly error messages and retry options

## 📞 Support

If you encounter issues:

1. Check the logs in the terminal
2. Verify all services are running (Ollama, MySQL, Python API)
3. Ensure all dependencies are installed
4. Check configuration files for correct settings

---

**🎉 Enjoy your AI-powered document processing!** Your Study Space application now has intelligent document analysis capabilities powered by Qwen3-coder! 🚀
