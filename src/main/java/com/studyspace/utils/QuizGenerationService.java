package com.studyspace.utils;

import com.studyspace.models.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Service for generating quizzes using AI from existing flashcards and notes
 */
public class QuizGenerationService {
    
    private static final String AI_ENDPOINT = "http://localhost:11434/api/generate";
    private static final String AI_MODEL = "qwen3-coder:480b-cloud";
    private final ObjectMapper objectMapper;
    
    public QuizGenerationService() {
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Generate a quiz from selected flashcard decks
     */
    public QuizGenerationResult generateQuizFromFlashcards(List<FlashcardDeck> selectedDecks, 
                                                          String quizTitle, 
                                                          String subject, 
                                                          Flashcard.Difficulty difficulty, 
                                                          int timeLimit,
                                                          int questionCount) {
        try {
            // Collect all flashcards from selected decks
            List<Flashcard> allFlashcards = new ArrayList<>();
            for (FlashcardDeck deck : selectedDecks) {
                allFlashcards.addAll(deck.getFlashcards());
            }
            
            if (allFlashcards.isEmpty()) {
                return new QuizGenerationResult(false, "No flashcards found in selected decks", null);
            }
            
            // Prepare content for AI processing
            String flashcardContent = prepareFlashcardContent(allFlashcards);
            
            // Generate quiz using AI
            String aiResponse = callAIForQuizGeneration(flashcardContent, "flashcards", questionCount, difficulty);
            
            if (aiResponse == null || aiResponse.trim().isEmpty()) {
                return new QuizGenerationResult(false, "AI service unavailable", null);
            }
            
            // Parse AI response and create quiz
            Quiz quiz = parseAIResponseToQuiz(aiResponse, quizTitle, subject, difficulty, timeLimit);
            
            if (quiz == null || quiz.getQuestions().isEmpty()) {
                return new QuizGenerationResult(false, "Failed to generate valid quiz questions", null);
            }
            
            return new QuizGenerationResult(true, "Quiz generated successfully", quiz);
            
        } catch (Exception e) {
            System.err.println("Error generating quiz from flashcards: " + e.getMessage());
            return new QuizGenerationResult(false, "Error generating quiz: " + e.getMessage(), null);
        }
    }
    
    /**
     * Generate a quiz from selected notes
     */
    public QuizGenerationResult generateQuizFromNotes(List<Note> selectedNotes, 
                                                     String quizTitle, 
                                                     String subject, 
                                                     Flashcard.Difficulty difficulty, 
                                                     int timeLimit,
                                                     int questionCount) {
        try {
            if (selectedNotes.isEmpty()) {
                return new QuizGenerationResult(false, "No notes selected", null);
            }
            
            // Prepare content for AI processing
            String noteContent = prepareNoteContent(selectedNotes);
            
            // Generate quiz using AI
            String aiResponse = callAIForQuizGeneration(noteContent, "notes", questionCount, difficulty);
            
            if (aiResponse == null || aiResponse.trim().isEmpty()) {
                return new QuizGenerationResult(false, "AI service unavailable", null);
            }
            
            // Parse AI response and create quiz
            Quiz quiz = parseAIResponseToQuiz(aiResponse, quizTitle, subject, difficulty, timeLimit);
            
            if (quiz == null || quiz.getQuestions().isEmpty()) {
                return new QuizGenerationResult(false, "Failed to generate valid quiz questions", null);
            }
            
            return new QuizGenerationResult(true, "Quiz generated successfully", quiz);
            
        } catch (Exception e) {
            System.err.println("Error generating quiz from notes: " + e.getMessage());
            return new QuizGenerationResult(false, "Error generating quiz: " + e.getMessage(), null);
        }
    }
    
    /**
     * Prepare flashcard content for AI processing
     */
    private String prepareFlashcardContent(List<Flashcard> flashcards) {
        StringBuilder content = new StringBuilder();
        content.append("FLASHCARD CONTENT FOR QUIZ GENERATION:\n\n");
        
        for (int i = 0; i < flashcards.size(); i++) {
            Flashcard card = flashcards.get(i);
            content.append("Flashcard ").append(i + 1).append(":\n");
            content.append("Question: ").append(card.getQuestion()).append("\n");
            content.append("Answer: ").append(card.getAnswer()).append("\n");
            content.append("Difficulty: ").append(card.getDifficulty().getDisplayName()).append("\n\n");
        }
        
        return content.toString();
    }
    
    /**
     * Prepare note content for AI processing
     */
    private String prepareNoteContent(List<Note> notes) {
        StringBuilder content = new StringBuilder();
        content.append("STUDY NOTES CONTENT FOR QUIZ GENERATION:\n\n");
        
        for (int i = 0; i < notes.size(); i++) {
            Note note = notes.get(i);
            content.append("Note ").append(i + 1).append(" - ").append(note.getTitle()).append(":\n");
            content.append("Subject: ").append(note.getCategory()).append("\n");
            content.append("Content: ").append(note.getContent()).append("\n\n");
        }
        
        return content.toString();
    }
    
    /**
     * Call AI service for quiz generation
     */
    private String callAIForQuizGeneration(String content, String sourceType, int questionCount, Flashcard.Difficulty difficulty) {
        try {
            // Prepare the prompt for quiz generation
            String prompt = String.format("""
                You are an intelligent quiz generator. Create a comprehensive quiz based on the provided %s content.
                
                Content to analyze:
                %s
                
                Generate exactly %d multiple-choice questions with the following requirements:
                
                1. Question Types:
                   - Definition questions (What is...?)
                   - Application questions (How does... work?)
                   - Analysis questions (Why does... happen?)
                   - Comparison questions (What's the difference between...?)
                   - Example questions (Which of the following is an example of...?)
                
                2. Difficulty Level: %s
                   - Easy: Basic recall and simple understanding
                   - Medium: Application and analysis
                   - Hard: Complex analysis, synthesis, and evaluation
                
                3. Question Structure:
                   - Clear, unambiguous questions
                   - 4 multiple choice options (A, B, C, D)
                   - One correct answer and three plausible distractors
                   - Detailed explanations for each answer
                
                4. Content Coverage:
                   - Cover all major topics from the source material
                   - Include both factual and conceptual questions
                   - Ensure questions test understanding, not just memorization
                
                Format your response as JSON with this exact structure:
                {
                    "questions": [
                        {
                            "question": "What is the main purpose of...?",
                            "options": [
                                "Option A text",
                                "Option B text", 
                                "Option C text",
                                "Option D text"
                            ],
                            "correct_answer": 0,
                            "explanation": "Detailed explanation of why this answer is correct and why others are wrong"
                        }
                    ]
                }
                
                Generate %d high-quality questions that comprehensively test knowledge of the provided content.
                """, sourceType, content.substring(0, Math.min(content.length(), 80000)), questionCount, difficulty.getDisplayName(), questionCount);
            
            // Make HTTP request to AI service
            URI uri = new URI(AI_ENDPOINT);
            URL url = uri.toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            
            // Prepare request body
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", AI_MODEL);
            requestBody.put("prompt", prompt);
            requestBody.put("stream", false);
            
            Map<String, Object> options = new HashMap<>();
            options.put("temperature", 0.3);
            options.put("top_p", 0.9);
            options.put("max_tokens", 40000);
            requestBody.put("options", options);
            
            // Send request
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = objectMapper.writeValueAsBytes(requestBody);
                os.write(input, 0, input.length);
            }
            
            // Read response
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }
                    
                    // Parse JSON response
                    JsonNode jsonResponse = objectMapper.readTree(response.toString());
                    return jsonResponse.get("response").asText();
                }
            } else {
                System.err.println("AI service returned error code: " + responseCode);
                return null;
            }
            
        } catch (Exception e) {
            System.err.println("Error calling AI service: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Parse AI response and create Quiz object
     */
    private Quiz parseAIResponseToQuiz(String aiResponse, String quizTitle, String subject, 
                                     Flashcard.Difficulty difficulty, int timeLimit) {
        try {
            // Extract JSON from AI response
            int jsonStart = aiResponse.indexOf('{');
            int jsonEnd = aiResponse.lastIndexOf('}') + 1;
            
            if (jsonStart == -1 || jsonEnd == -1) {
                System.err.println("No valid JSON found in AI response");
                return null;
            }
            
            String jsonStr = aiResponse.substring(jsonStart, jsonEnd);
            JsonNode jsonNode = objectMapper.readTree(jsonStr);
            
            // Create quiz
            Quiz quiz = new Quiz(quizTitle, "AI-generated quiz from study materials", subject, difficulty, timeLimit);
            
            // Parse questions
            JsonNode questionsNode = jsonNode.get("questions");
            if (questionsNode != null && questionsNode.isArray()) {
                for (JsonNode questionNode : questionsNode) {
                    Question question = parseQuestionFromJSON(questionNode);
                    if (question != null) {
                        quiz.addQuestion(question);
                    }
                }
            }
            
            return quiz;
            
        } catch (Exception e) {
            System.err.println("Error parsing AI response: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Parse individual question from JSON
     */
    private Question parseQuestionFromJSON(JsonNode questionNode) {
        try {
            String questionText = questionNode.get("question").asText();
            JsonNode optionsNode = questionNode.get("options");
            int correctAnswer = questionNode.get("correct_answer").asInt();
            String explanation = questionNode.get("explanation").asText();
            
            // Convert options to list
            List<String> options = new ArrayList<>();
            if (optionsNode != null && optionsNode.isArray()) {
                for (JsonNode option : optionsNode) {
                    options.add(option.asText());
                }
            }
            
            // Validate question data
            if (questionText == null || questionText.trim().isEmpty() ||
                options.size() != 4 || correctAnswer < 0 || correctAnswer >= 4) {
                System.err.println("Invalid question data, skipping");
                return null;
            }
            
            return new Question(questionText, options, correctAnswer, explanation, Flashcard.Difficulty.MEDIUM);
            
        } catch (Exception e) {
            System.err.println("Error parsing question: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Result class for quiz generation
     */
    public static class QuizGenerationResult {
        private final boolean success;
        private final String message;
        private final Quiz quiz;
        
        public QuizGenerationResult(boolean success, String message, Quiz quiz) {
            this.success = success;
            this.message = message;
            this.quiz = quiz;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public Quiz getQuiz() { return quiz; }
    }
}
