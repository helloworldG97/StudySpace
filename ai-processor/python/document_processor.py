#!/usr/bin/env python3
"""
Document Processor for Study Space Application
Extracts content from PDF and PPT files, processes with Qwen3-coder LLM, and stores in database
"""

import os
import sys
import json
import logging
import argparse
from pathlib import Path
from typing import Dict, List, Optional, Tuple
import requests
import mysql.connector
from mysql.connector import Error
import PyPDF2
from pptx import Presentation
from docx import Document
import uuid
from datetime import datetime

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

class DocumentProcessor:
    """Main class for document processing and LLM integration"""
    
    def __init__(self, config_file: str = "config.json"):
        """Initialize the document processor with configuration"""
        self.config = self.load_config(config_file)
        self.db_connection = None
        self.llm_endpoint = self.config.get('llm_endpoint', 'http://localhost:11434')
        self.llm_model = self.config.get('llm_model', 'qwen3-coder:480b-cloud')
        
    def load_config(self, config_file: str) -> Dict:
        """Load configuration from JSON file"""
        try:
            with open(config_file, 'r') as f:
                return json.load(f)
        except FileNotFoundError:
            logger.warning(f"Config file {config_file} not found, using defaults")
            return self.get_default_config()
    
    def get_default_config(self) -> Dict:
        """Return default configuration"""
        return {
            "database": {
                "host": "localhost",
                "port": 3306,
                "user": "root",
                "password": "",
                "database": "studyspace_db"
            },
            "llm_endpoint": "http://localhost:11434",
            "llm_model": "qwen3-coder:480b-cloud",
            "output_dir": "./processed_documents",
            "supported_formats": [".pdf", ".ppt", ".pptx", ".doc", ".docx"]
        }
    
    def connect_database(self) -> bool:
        """Connect to MySQL database"""
        try:
            db_config = self.config['database']
            self.db_connection = mysql.connector.connect(
                host=db_config['host'],
                port=db_config['port'],
                user=db_config['user'],
                password=db_config['password'],
                database=db_config['database']
            )
            logger.info("Successfully connected to database")
            return True
        except Error as e:
            logger.error(f"Database connection failed: {e}")
            return False
    
    def extract_pdf_content(self, file_path: str) -> str:
        """Extract text content from PDF file"""
        try:
            content = ""
            with open(file_path, 'rb') as file:
                pdf_reader = PyPDF2.PdfReader(file)
                for page_num in range(len(pdf_reader.pages)):
                    page = pdf_reader.pages[page_num]
                    content += page.extract_text() + "\n"
            logger.info(f"Extracted {len(content)} characters from PDF: {file_path}")
            return content.strip()
        except Exception as e:
            logger.error(f"Error extracting PDF content: {e}")
            return ""
    
    def extract_ppt_content(self, file_path: str) -> str:
        """Extract text content from PowerPoint file"""
        try:
            content = ""
            prs = Presentation(file_path)
            
            for slide_num, slide in enumerate(prs.slides):
                content += f"\n--- Slide {slide_num + 1} ---\n"
                for shape in slide.shapes:
                    if hasattr(shape, "text"):
                        content += shape.text + "\n"
            
            logger.info(f"Extracted {len(content)} characters from PPT: {file_path}")
            return content.strip()
        except Exception as e:
            logger.error(f"Error extracting PPT content: {e}")
            return ""
    
    def extract_docx_content(self, file_path: str) -> str:
        """Extract text content from Word document"""
        try:
            doc = Document(file_path)
            content = ""
            for paragraph in doc.paragraphs:
                content += paragraph.text + "\n"
            
            logger.info(f"Extracted {len(content)} characters from DOCX: {file_path}")
            return content.strip()
        except Exception as e:
            logger.error(f"Error extracting DOCX content: {e}")
            return ""
    
    def extract_content(self, file_path: str) -> Tuple[str, str]:
        """Extract content from supported file types"""
        file_path = Path(file_path)
        file_extension = file_path.suffix.lower()
        
        if file_extension == '.pdf':
            content = self.extract_pdf_content(str(file_path))
            file_type = "PDF"
        elif file_extension in ['.ppt', '.pptx']:
            content = self.extract_ppt_content(str(file_path))
            file_type = "PowerPoint"
        elif file_extension in ['.doc', '.docx']:
            content = self.extract_docx_content(str(file_path))
            file_type = "Word Document"
        else:
            logger.error(f"Unsupported file type: {file_extension}")
            return "", ""
        
        return content, file_type
    
    def process_with_llm(self, content: str, file_type: str) -> Dict:
        """Process extracted content with Qwen3-coder LLM"""
        try:
            # Prepare the prompt for the LLM
            prompt = f"""
            You are an intelligent educational content processor. Analyze the imported text carefully and create both comprehensive study notes and educational flashcards.

            Content to analyze:
            {content[:50000]}  # Maximum content limit for complete document analysis
            
            For STUDY NOTES, act as an educational content analyzer:
            - Read the imported file carefully and create a detailed, well-organized outline summarizing its key concepts and sections
            - Identify the main topics and present them as clear, bolded section headers
            - Under each main topic, include concise bullet points or short paragraphs explaining:
              * Core ideas or definitions
              * Historical or conceptual background
              * Examples or case studies (if any)
              * Importance or impact on society or environment (if applicable)
            - Preserve the logical flow of the document (from overview → development → impact → solutions)
            - If the file contains laws, events, or key terms, highlight them with their significance
            - Use an academic yet easy-to-understand tone, suitable for students
            - Format as: Title, I. Overview, II. Historical Development, III. Core Concepts or Principles, IV. Impacts or Applications, V. Case Studies/Examples, VI. Conclusion/Future Directions
            
            For FLASHCARDS, act as an intelligent flashcard generator:
            - Identify key points – Extract important definitions, dates, laws, processes, causes and effects, examples, and important people or events
            - Simplify and clarify – Rephrase complex ideas into short, clear, and direct questions and answers
            - Promote understanding – Focus not only on memorization but also on conceptual comprehension (e.g., "Why" and "How" questions)
            - Avoid redundancy – Each flashcard should cover one key concept only
            
            Format your response as JSON with the following structure:
            {{
                "title": "string",
                "summary": "string",
                "key_topics": ["topic1", "topic2", ...],
                "definitions": ["term1: definition1", "term2: definition2", ...],
                "study_notes": "Title: [Automatically extract or summarize document title]\\n\\nI. Overview\\n- Short summary of the topic\\n\\nII. Historical Development\\n- Key events, movements, or milestones\\n\\nIII. Core Concepts or Principles\\n- Definitions and explanations\\n\\nIV. Impacts or Applications\\n- Effects, importance, and relevance\\n\\nV. Case Studies / Examples\\n- Real-world examples or laws mentioned\\n\\nVI. Conclusion / Future Directions\\n- Summary and outlook",
                "difficulty": "Easy|Medium|Hard",
                "subject": "string",
                "flashcards": [
                    {{
                        "question": "What is [concept]?",
                        "answer": "Clear, concise explanation (1-3 sentences)"
                    }},
                    {{
                        "question": "When did [event] occur?",
                        "answer": "Specific date and context"
                    }},
                    {{
                        "question": "What does [law/principle] state?",
                        "answer": "Brief statement of the law with key points"
                    }},
                    {{
                        "question": "What causes [phenomenon]?",
                        "answer": "List of main causes with brief explanations"
                    }},
                    {{
                        "question": "Give an example of [concept]",
                        "answer": "Specific real-world example with brief explanation"
                    }}
                ]
            }}
            
            Generate 20-50 flashcards covering different aspects of the content. Focus on creating meaningful, educational questions that promote understanding rather than simple memorization. Ensure comprehensive coverage of all topics in the document.
            """
            
            # Call the LLM API
            response = requests.post(
                f"{self.llm_endpoint}/api/generate",
                json={
                    "model": self.llm_model,
                    "prompt": prompt,
                    "stream": False,
                    "options": {
                        "temperature": 0.3,
                        "top_p": 0.9,
                        "max_tokens": 32000
                    }
                },
                timeout=300
            )
            
            if response.status_code == 200:
                result = response.json()
                llm_response = result.get('response', '')
                
                # Try to parse JSON response
                try:
                    # Extract JSON from the response (LLM might add extra text)
                    json_start = llm_response.find('{')
                    json_end = llm_response.rfind('}') + 1
                    if json_start != -1 and json_end != -1:
                        json_str = llm_response[json_start:json_end]
                        parsed_response = json.loads(json_str)
                        return parsed_response
                except json.JSONDecodeError:
                    logger.warning("LLM response is not valid JSON, using fallback")
                
                # Fallback if JSON parsing fails
                return {
                    "title": f"Document from {file_type}",
                    "summary": llm_response[:200] + "..." if len(llm_response) > 200 else llm_response,
                    "key_topics": ["General Topics"],
                    "definitions": [],
                    "study_notes": llm_response,
                    "difficulty": "Medium",
                    "subject": "General"
                }
            else:
                logger.error(f"LLM API error: {response.status_code}")
                return None
                
        except Exception as e:
            logger.error(f"Error processing with LLM: {e}")
            return None
    
    def save_to_database(self, file_path: str, content: str, file_type: str, llm_result: Dict, content_type: str = "both", user_id: str = None) -> bool:
        """Save processed content to database based on content type"""
        try:
            if not user_id:
                logger.error("User ID is required for saving content")
                return False
                
            if not self.db_connection or not self.db_connection.is_connected():
                if not self.connect_database():
                    return False
            
            cursor = self.db_connection.cursor()
            
            # Generate unique IDs
            note_id = str(uuid.uuid4())
            deck_id = str(uuid.uuid4())
            
            # Extract file name without extension
            file_name = Path(file_path).stem
            
            # Create note entry only if content_type includes notes
            if content_type in ["notes", "both"]:
                note_query = """
                INSERT INTO notes (id, user_id, title, content, category, created_at, modified_at, is_pinned)
                VALUES (%s, %s, %s, %s, %s, %s, %s, %s)
                """
                note_values = (
                    note_id,
                    user_id,
                    llm_result.get('title', file_name),
                    llm_result.get('study_notes', content[:20000]),
                    llm_result.get('subject', 'General'),
                    datetime.now(),
                    datetime.now(),
                    False
                )
                cursor.execute(note_query, note_values)
                logger.info(f"Created note: {note_id}")
            
            # Create flashcard deck only if content_type includes flashcards
            if content_type in ["flashcards", "both"]:
                deck_query = """
                INSERT INTO flashcard_decks (id, user_id, title, description, subject, difficulty, created_at, last_studied, total_study_sessions)
                VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)
                """
                deck_values = (
                    deck_id,
                    user_id,
                    f"{llm_result.get('title', file_name)} - Imported from {file_type}",
                    llm_result.get('summary', f"Content imported from {file_type} file"),
                    llm_result.get('subject', 'General'),
                    llm_result.get('difficulty', 'MEDIUM'),
                    datetime.now(),
                    None,
                    0
                )
                cursor.execute(deck_query, deck_values)
                logger.info(f"Created deck: {deck_id}")
            
            # Create flashcards from structured LLM response only if content_type includes flashcards
            if content_type in ["flashcards", "both"]:
                # First, try to use the new structured flashcards from LLM
                structured_flashcards = llm_result.get('flashcards', [])
                if structured_flashcards:
                    for flashcard_data in structured_flashcards:
                        flashcard_id = str(uuid.uuid4())
                        
                        flashcard_query = """
                        INSERT INTO flashcards (id, deck_id, question, answer, difficulty, created_at, last_studied, times_studied, is_correct)
                        VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)
                        """
                        flashcard_values = (
                            flashcard_id,
                            deck_id,
                            flashcard_data.get('question', ''),
                            flashcard_data.get('answer', ''),
                            llm_result.get('difficulty', 'MEDIUM'),
                            datetime.now(),
                            None,
                            0,
                            False
                        )
                        cursor.execute(flashcard_query, flashcard_values)
                else:
                    # Fallback to old method if structured flashcards not available
                    definitions = llm_result.get('definitions', [])
                    for i, definition in enumerate(definitions[:25]):  # Increased limit for more flashcards
                        if ':' in definition:
                            term, definition_text = definition.split(':', 1)
                            flashcard_id = str(uuid.uuid4())
                            
                            flashcard_query = """
                            INSERT INTO flashcards (id, deck_id, question, answer, difficulty, created_at, last_studied, times_studied, is_correct)
                            VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)
                            """
                            flashcard_values = (
                                flashcard_id,
                                deck_id,
                                f"What is {term.strip()}?",
                                definition_text.strip(),
                                llm_result.get('difficulty', 'MEDIUM'),
                                datetime.now(),
                                None,
                                0,
                                False
                            )
                            cursor.execute(flashcard_query, flashcard_values)
                
                    # Create flashcards from key topics as fallback
                    key_topics = llm_result.get('key_topics', [])
                    for i, topic in enumerate(key_topics[:15]):  # Increased limit for more topic flashcards
                        flashcard_id = str(uuid.uuid4())
                    
                        flashcard_query = """
                        INSERT INTO flashcards (id, deck_id, question, answer, difficulty, created_at, last_studied, times_studied, is_correct)
                        VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)
                        """
                        flashcard_values = (
                            flashcard_id,
                            deck_id,
                            f"Explain: {topic}",
                            f"This is a key topic from the {file_type} document: {topic}",
                            llm_result.get('difficulty', 'MEDIUM'),
                            datetime.now(),
                            None,
                            0,
                            False
                        )
                        cursor.execute(flashcard_query, flashcard_values)
            
            self.db_connection.commit()
            cursor.close()
            
            logger.info(f"Successfully saved processed content to database")
            if content_type in ["notes", "both"]:
                logger.info(f"Created note: {note_id}")
            if content_type in ["flashcards", "both"]:
                logger.info(f"Created deck: {deck_id}")
                structured_flashcards = llm_result.get('flashcards', [])
                if structured_flashcards:
                    logger.info(f"Created {len(structured_flashcards)} structured flashcards")
                else:
                    definitions = llm_result.get('definitions', [])
                    key_topics = llm_result.get('key_topics', [])
                    logger.info(f"Created {len(definitions[:25]) + len(key_topics[:15])} fallback flashcards")
            
            return True
            
        except Error as e:
            logger.error(f"Database error: {e}")
            return False
    
    def process_document(self, file_path: str, content_type: str = "both", user_id: str = None) -> Dict:
        """Main method to process a document
        
        Args:
            file_path: Path to the document file
            content_type: Type of content to create ("notes", "flashcards", or "both")
            user_id: ID of the user creating the content
        """
        if not user_id:
            return {"success": False, "error": "User ID is required"}
            
        logger.info(f"Processing document: {file_path} for content type: {content_type} for user: {user_id}")
        
        # Extract content
        content, file_type = self.extract_content(file_path)
        if not content:
            return {"success": False, "error": "Failed to extract content"}
        
        # Process with LLM
        llm_result = self.process_with_llm(content, file_type)
        if not llm_result:
            return {"success": False, "error": "LLM processing failed"}
        
        # Save to database based on content type
        if not self.save_to_database(file_path, content, file_type, llm_result, content_type, user_id):
            return {"success": False, "error": "Database save failed"}
        
        # Prepare response based on content type
        response = {
            "success": True,
            "summary": llm_result.get('summary', ''),
            "subject": llm_result.get('subject', 'General'),
            "difficulty": llm_result.get('difficulty', 'Medium')
        }
        
        if content_type in ["notes", "both"]:
            response["note_title"] = llm_result.get('title', 'Unknown')
            response["note_id"] = llm_result.get('title', 'Unknown')
        
        if content_type in ["flashcards", "both"]:
            response["deck_title"] = f"{llm_result.get('title', 'Unknown')} - Imported from {file_type}"
            structured_flashcards = llm_result.get('flashcards', [])
            if structured_flashcards:
                response["flashcards_created"] = len(structured_flashcards)
            else:
                response["flashcards_created"] = len(llm_result.get('definitions', [])) + len(llm_result.get('key_topics', []))
        
        return response

def main():
    """Main function for command line usage"""
    parser = argparse.ArgumentParser(description='Process documents for Study Space')
    parser.add_argument('file_path', help='Path to the document file')
    parser.add_argument('--config', default='config.json', help='Configuration file path')
    parser.add_argument('--output', help='Output directory for processed files')
    
    args = parser.parse_args()
    
    # Initialize processor
    processor = DocumentProcessor(args.config)
    
    # Process the document
    result = processor.process_document(args.file_path)
    
    if result['success']:
        print(f"✅ Successfully processed document!")
        print(f"📝 Note created: {result['note_id']}")
        print(f"🃏 Deck created: {result['deck_id']}")
        print(f"📚 Flashcards created: {result['flashcards_created']}")
        print(f"📖 Subject: {result['subject']}")
        print(f"📊 Difficulty: {result['difficulty']}")
        print(f"📄 Summary: {result['summary']}")
    else:
        print(f"❌ Error processing document: {result['error']}")
        sys.exit(1)

if __name__ == "__main__":
    main()
