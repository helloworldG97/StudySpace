#!/usr/bin/env python3
"""
Setup script for Document Processor
Installs dependencies and sets up the environment
"""

import subprocess
import sys
import os
from pathlib import Path

def install_requirements():
    """Install Python requirements"""
    print("📦 Installing Python requirements...")
    try:
        subprocess.check_call([sys.executable, "-m", "pip", "install", "-r", "requirements.txt"])
        print("✅ Requirements installed successfully!")
        return True
    except subprocess.CalledProcessError as e:
        print(f"❌ Error installing requirements: {e}")
        return False

def check_ollama():
    """Check if Ollama is running and has the required model"""
    print("🔍 Checking Ollama installation...")
    try:
        import requests
        response = requests.get("http://localhost:11434/api/tags", timeout=5)
        if response.status_code == 200:
            models = response.json().get('models', [])
            model_names = [model['name'] for model in models]
            if 'qwen3-coder:480b-cloud' in model_names:
                print("✅ Ollama is running and has qwen3-coder:480b-cloud model")
                return True
            else:
                print("⚠️  Ollama is running but qwen3-coder:480b-cloud model not found")
                print("   Available models:", model_names)
                return False
        else:
            print("❌ Ollama is not responding")
            return False
    except Exception as e:
        print(f"❌ Error checking Ollama: {e}")
        return False

def check_database():
    """Check database connection"""
    print("🔍 Checking database connection...")
    try:
        from document_processor import DocumentProcessor
        processor = DocumentProcessor()
        if processor.connect_database():
            print("✅ Database connection successful")
            return True
        else:
            print("❌ Database connection failed")
            return False
    except Exception as e:
        print(f"❌ Error checking database: {e}")
        return False

def create_directories():
    """Create necessary directories"""
    print("📁 Creating directories...")
    directories = [
        "processed_documents",
        "logs",
        "temp"
    ]
    
    for directory in directories:
        Path(directory).mkdir(exist_ok=True)
        print(f"   ✅ Created {directory}/")

def main():
    """Main setup function"""
    print("🚀 Setting up Document Processor for Study Space")
    print("=" * 50)
    
    # Create directories
    create_directories()
    
    # Install requirements
    if not install_requirements():
        print("❌ Setup failed at requirements installation")
        sys.exit(1)
    
    # Check Ollama
    ollama_ok = check_ollama()
    if not ollama_ok:
        print("\n⚠️  Ollama setup required:")
        print("   1. Install Ollama from https://ollama.ai/")
        print("   2. Run: ollama pull qwen3-coder:480b-cloud")
        print("   3. Start Ollama service")
    
    # Check database
    db_ok = check_database()
    if not db_ok:
        print("\n⚠️  Database setup required:")
        print("   1. Make sure XAMPP is running")
        print("   2. Import the import_to_xampp.sql file")
        print("   3. Update config.json with correct database credentials")
    
    print("\n" + "=" * 50)
    if ollama_ok and db_ok:
        print("✅ Setup completed successfully!")
        print("🚀 You can now run: python api_server.py")
    else:
        print("⚠️  Setup completed with warnings")
        print("   Please address the issues above before running the service")

if __name__ == "__main__":
    main()
