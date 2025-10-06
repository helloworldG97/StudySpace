#!/usr/bin/env python3
"""
API Server for Document Processing
Provides REST API endpoints for the Java application to process documents
"""

import os
import sys
import json
import logging
from flask import Flask, request, jsonify
from flask_cors import CORS
from document_processor import DocumentProcessor
import tempfile
import shutil
from pathlib import Path

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

app = Flask(__name__)
CORS(app)  # Enable CORS for Java application

# Initialize document processor
processor = DocumentProcessor()

@app.route('/health', methods=['GET'])
def health_check():
    """Health check endpoint"""
    return jsonify({"status": "healthy", "service": "document-processor"})

@app.route('/process', methods=['POST'])
def process_document():
    """Process a document file"""
    try:
        # Check if user_id is provided
        user_id = request.form.get('user_id')
        if not user_id:
            return jsonify({"success": False, "error": "User ID is required"}), 400
        
        # Check if file is present in request
        if 'file' not in request.files:
            return jsonify({"success": False, "error": "No file provided"}), 400
        
        file = request.files['file']
        if file.filename == '':
            return jsonify({"success": False, "error": "No file selected"}), 400
        
        # Check file extension
        file_extension = Path(file.filename).suffix.lower()
        if file_extension not in processor.config.get('supported_formats', ['.pdf', '.ppt', '.pptx', '.doc', '.docx']):
            return jsonify({
                "success": False, 
                "error": f"Unsupported file type: {file_extension}. Supported types: {processor.config.get('supported_formats')}"
            }), 400
        
        # Save uploaded file to temporary location
        with tempfile.NamedTemporaryFile(delete=False, suffix=file_extension) as temp_file:
            file.save(temp_file.name)
            temp_file_path = temp_file.name
        
        try:
            # Get content type from request (default to "both" for backward compatibility)
            content_type = request.form.get('content_type', 'both')
            if content_type not in ['notes', 'flashcards', 'both']:
                content_type = 'both'
            
            # Process the document
            result = processor.process_document(temp_file_path, content_type, user_id)
            
            if result['success']:
                logger.info(f"Successfully processed document: {file.filename} for content type: {content_type}")
                
                # Prepare response data based on content type
                response_data = {
                    "summary": result['summary'],
                    "subject": result['subject'],
                    "difficulty": result['difficulty']
                }
                
                if content_type in ['notes', 'both']:
                    response_data["note_title"] = result.get('note_title', 'Unknown')
                
                if content_type in ['flashcards', 'both']:
                    response_data["deck_title"] = result.get('deck_title', 'Unknown')
                    response_data["flashcards_created"] = result.get('flashcards_created', 0)
                
                return jsonify({
                    "success": True,
                    "message": "Document processed successfully",
                    "data": response_data
                })
            else:
                logger.error(f"Failed to process document: {result['error']}")
                return jsonify({
                    "success": False,
                    "error": result['error']
                }), 500
                
        finally:
            # Clean up temporary file
            if os.path.exists(temp_file_path):
                os.unlink(temp_file_path)
    
    except Exception as e:
        logger.error(f"Error processing document: {e}")
        return jsonify({
            "success": False,
            "error": f"Internal server error: {str(e)}"
        }), 500

@app.route('/supported-formats', methods=['GET'])
def get_supported_formats():
    """Get list of supported file formats"""
    return jsonify({
        "success": True,
        "formats": processor.config.get('supported_formats', ['.pdf', '.ppt', '.pptx', '.doc', '.docx'])
    })

@app.route('/status', methods=['GET'])
def get_status():
    """Get processing status and configuration"""
    return jsonify({
        "success": True,
        "status": "running",
        "llm_endpoint": processor.llm_endpoint,
        "llm_model": processor.llm_model,
        "database_connected": processor.connect_database() if not processor.db_connection else True,
        "supported_formats": processor.config.get('supported_formats', [])
    })

@app.errorhandler(413)
def too_large(e):
    """Handle file too large error"""
    return jsonify({
        "success": False,
        "error": "File too large. Maximum file size is 50MB."
    }), 413

@app.errorhandler(404)
def not_found(e):
    """Handle 404 errors"""
    return jsonify({
        "success": False,
        "error": "Endpoint not found"
    }), 404

@app.errorhandler(500)
def internal_error(e):
    """Handle 500 errors"""
    return jsonify({
        "success": False,
        "error": "Internal server error"
    }), 500

if __name__ == '__main__':
    # Load configuration
    config = processor.config
    api_config = config.get('api', {})
    
    host = api_config.get('host', 'localhost')
    port = api_config.get('port', 8000)
    debug = api_config.get('debug', True)
    
    logger.info(f"Starting Document Processor API Server on {host}:{port}")
    logger.info(f"LLM Endpoint: {processor.llm_endpoint}")
    logger.info(f"LLM Model: {processor.llm_model}")
    
    # Test database connection
    if processor.connect_database():
        logger.info("Database connection successful")
    else:
        logger.warning("Database connection failed - some features may not work")
    
    app.run(host=host, port=port, debug=debug)
