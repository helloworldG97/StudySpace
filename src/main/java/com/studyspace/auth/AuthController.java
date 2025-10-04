package com.studyspace.auth;

import com.studyspace.models.User;
import com.studyspace.utils.DataStore;

import java.util.regex.Pattern;

//============ authentication controller =============
//this is where user login and registration is handled

public class AuthController {
    
    private final DataStore dataStore;
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    
    public static final String ERROR_INVALID_EMAIL = "Please enter a valid email address";
    public static final String ERROR_EMPTY_FIELD = "All fields are required";
    public static final String ERROR_USER_EXISTS = "An account with this email already exists";
    public static final String ERROR_INVALID_CREDENTIALS = "Invalid email or password";
    public static final String ERROR_USER_NOT_FOUND = "No account found with this email";
    public static final String SUCCESS_REGISTRATION = "Account created successfully!";
    public static final String SUCCESS_LOGIN = "Welcome back!";
    
    public AuthController() {
        this.dataStore = DataStore.getInstance();
    }
    
    public AuthResult registerUser(String fullName, String email, String password) {
        AuthResult validation = validateRegistrationInput(fullName, email, password);
        if (!validation.isSuccess()) {
            return validation;
        }
        
        User newUser = dataStore.registerUser(fullName.trim(), email.toLowerCase().trim(), password);
        if (newUser == null) {
            return new AuthResult(false, ERROR_USER_EXISTS);
        }
        
        return new AuthResult(true, SUCCESS_REGISTRATION, newUser);
    }
    
    public AuthResult loginUser(String email, String password) {
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return new AuthResult(false, ERROR_EMPTY_FIELD);
        }
        
        boolean authenticated = dataStore.authenticateUser(email.toLowerCase().trim(), password);
        if (!authenticated) {
            return new AuthResult(false, ERROR_INVALID_CREDENTIALS);
        }
        
        User user = dataStore.getCurrentUser();
        return new AuthResult(true, SUCCESS_LOGIN, user);
    }
    
    private AuthResult validateRegistrationInput(String fullName, String email, String password) {
        if (fullName == null || fullName.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            return new AuthResult(false, ERROR_EMPTY_FIELD);
        }
        
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            return new AuthResult(false, ERROR_INVALID_EMAIL);
        }
        
        if (fullName.trim().length() < 2) {
            return new AuthResult(false, "Full name must be at least 2 characters long");
        }
        
        if (password.length() < 6) {
            return new AuthResult(false, "Password must be at least 6 characters long");
        }
        
        return new AuthResult(true, "Validation successful");
    }
    
    public boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }
    
    public boolean emailExists(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return false;
    }
    
    public void logout() {
        dataStore.logout();
    }
    
    public static class AuthResult {
        private final boolean success;
        private final String message;
        private final User user;
        
        public AuthResult(boolean success, String message) {
            this(success, message, null);
        }
        
        public AuthResult(boolean success, String message, User user) {
            this.success = success;
            this.message = message;
            this.user = user;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public User getUser() {
            return user;
        }
        
        @Override
        public String toString() {
            return "AuthResult{" +
                    "success=" + success +
                    ", message='" + message + '\'' +
                    ", user=" + (user != null ? user.getEmail() : "null") +
                    '}';
        }
    }
}
