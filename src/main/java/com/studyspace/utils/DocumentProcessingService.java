package com.studyspace.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Service class for integrating with Python document processing API
 */
public class DocumentProcessingService {
    private static final String API_BASE_URL = "http://localhost:8000";
    private static final String PROCESS_ENDPOINT = "/process";
    private static final String STATUS_ENDPOINT = "/status";
    private static final String HEALTH_ENDPOINT = "/health";
    
    private static DocumentProcessingService instance;
    private final ObjectMapper objectMapper;
    
    private DocumentProcessingService() {
        this.objectMapper = new ObjectMapper();
    }
    
    public static DocumentProcessingService getInstance() {
        if (instance == null) {
            instance = new DocumentProcessingService();
        }
        return instance;
    }
    
    /**
     * Get the current user ID from the authentication system
     */
    private String getCurrentUserId() {
        try {
            com.studyspace.utils.DataStore dataStore = com.studyspace.utils.DataStore.getInstance();
            com.studyspace.models.User currentUser = dataStore.getCurrentUser();
            if (currentUser != null) {
                return currentUser.getId();
            }
        } catch (Exception e) {
            System.err.println("Error getting current user ID: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Check if the Python API service is running
     */
    public boolean isServiceRunning() {
        try {
            URL url = URI.create(API_BASE_URL + HEALTH_ENDPOINT).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            
            int responseCode = connection.getResponseCode();
            return responseCode == 200;
        } catch (Exception e) {
            System.err.println("Error checking service status: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Process a document file using the Python API
     */
    public DocumentProcessingResult processDocument(File file) {
        return processDocument(file, "both");
    }
    
    /**
     * Process a document file using the Python API with specific content type
     */
    public DocumentProcessingResult processDocument(File file, String contentType) {
        // Get current user ID from authentication system
        String userId = getCurrentUserId();
        if (userId == null) {
            return new DocumentProcessingResult(false, "User not authenticated. Please log in to process documents.");
        }
        return processDocument(file, contentType, userId);
    }
    
    /**
     * Process a document file using the Python API with specific content type and user ID
     */
    public DocumentProcessingResult processDocument(File file, String contentType, String userId) {
        try {
            if (!isServiceRunning()) {
                return new DocumentProcessingResult(false, "Python document processing service is not running. Please start the service with: python api_server.py");
            }
            
            URL url = URI.create(API_BASE_URL + PROCESS_ENDPOINT).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            
            // Create multipart form data
            String boundary = "----WebKitFormBoundary" + System.currentTimeMillis();
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            
            try (OutputStream outputStream = connection.getOutputStream();
                 PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"))) {
                
                // Write user_id part
                writer.append("--" + boundary).append("\r\n");
                writer.append("Content-Disposition: form-data; name=\"user_id\"").append("\r\n");
                writer.append("\r\n");
                writer.append(userId).append("\r\n");
                
                // Write content type part
                writer.append("--" + boundary).append("\r\n");
                writer.append("Content-Disposition: form-data; name=\"content_type\"").append("\r\n");
                writer.append("\r\n");
                writer.append(contentType).append("\r\n");
                
                // Write file part
                writer.append("--" + boundary).append("\r\n");
                writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"").append("\r\n");
                writer.append("Content-Type: application/octet-stream").append("\r\n");
                writer.append("\r\n");
                writer.flush();
                
                // Write file content
                try (FileInputStream fileInputStream = new FileInputStream(file)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }
                
                writer.append("\r\n");
                writer.append("--" + boundary + "--").append("\r\n");
                writer.flush();
            }
            
            int responseCode = connection.getResponseCode();
            
            if (responseCode == 200) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    
                    JsonNode jsonResponse = objectMapper.readTree(response.toString());
                    boolean success = jsonResponse.get("success").asBoolean();
                    
                    if (success) {
                        JsonNode data = jsonResponse.get("data");
                        return new DocumentProcessingResult(
                            true,
                            "Document processed successfully",
                            data.has("note_title") ? data.get("note_title").asText() : null,
                            data.has("deck_title") ? data.get("deck_title").asText() : null,
                            data.has("flashcards_created") ? data.get("flashcards_created").asInt() : 0,
                            data.get("summary").asText(),
                            data.get("subject").asText(),
                            data.get("difficulty").asText()
                        );
                    } else {
                        return new DocumentProcessingResult(false, jsonResponse.get("error").asText());
                    }
                }
            } else {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
                    StringBuilder errorResponse = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        errorResponse.append(line);
                    }
                    
                    JsonNode errorJson = objectMapper.readTree(errorResponse.toString());
                    return new DocumentProcessingResult(false, errorJson.get("error").asText());
                }
            }
            
        } catch (Exception e) {
            return new DocumentProcessingResult(false, "Error processing document: " + e.getMessage());
        }
    }
    
    /**
     * Get service status information
     */
    public Map<String, Object> getServiceStatus() {
        Map<String, Object> status = new HashMap<>();
        
        try {
            URL url = URI.create(API_BASE_URL + STATUS_ENDPOINT).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            
            int responseCode = connection.getResponseCode();
            
            if (responseCode == 200) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    
                    JsonNode jsonResponse = objectMapper.readTree(response.toString());
                    status.put("running", jsonResponse.get("success").asBoolean());
                    status.put("llm_endpoint", jsonResponse.get("llm_endpoint").asText());
                    status.put("llm_model", jsonResponse.get("llm_model").asText());
                    status.put("database_connected", jsonResponse.get("database_connected").asBoolean());
                    status.put("supported_formats", jsonResponse.get("supported_formats"));
                }
            } else {
                status.put("running", false);
                status.put("error", "Service not responding");
            }
        } catch (Exception e) {
            status.put("running", false);
            status.put("error", e.getMessage());
        }
        
        return status;
    }
    
    /**
     * Result class for document processing
     */
    public static class DocumentProcessingResult {
        private final boolean success;
        private final String message;
        private final String noteTitle;
        private final String deckTitle;
        private final int flashcardsCreated;
        private final String summary;
        private final String subject;
        private final String difficulty;
        
        public DocumentProcessingResult(boolean success, String message) {
            this(success, message, null, null, 0, null, null, null);
        }
        
        public DocumentProcessingResult(boolean success, String message, String noteTitle, 
                                      String deckTitle, int flashcardsCreated, String summary, 
                                      String subject, String difficulty) {
            this.success = success;
            this.message = message;
            this.noteTitle = noteTitle;
            this.deckTitle = deckTitle;
            this.flashcardsCreated = flashcardsCreated;
            this.summary = summary;
            this.subject = subject;
            this.difficulty = difficulty;
        }
        
        // Getters
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public String getNoteTitle() { return noteTitle; }
        public String getDeckTitle() { return deckTitle; }
        public int getFlashcardsCreated() { return flashcardsCreated; }
        public String getSummary() { return summary; }
        public String getSubject() { return subject; }
        public String getDifficulty() { return difficulty; }
    }
}
