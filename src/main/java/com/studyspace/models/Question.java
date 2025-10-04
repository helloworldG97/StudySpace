package com.studyspace.models;

import java.util.List;

//============ question model =============
//this is where quiz questions and answers are stored

public class Question {
    private String id;
    private String questionText;
    private List<String> options;
    private int correctOptionIndex;
    private String explanation;
    private Flashcard.Difficulty difficulty;
    
    public Question() {}
    
    public Question(String questionText, List<String> options, int correctOptionIndex, String explanation, Flashcard.Difficulty difficulty) {
        this.id = generateId();
        this.questionText = questionText;
        this.options = options;
        this.correctOptionIndex = correctOptionIndex;
        this.explanation = explanation;
        this.difficulty = difficulty;
    }
    
    private String generateId() {
        return "question_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }
    
    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }
    
    public int getCorrectOptionIndex() { return correctOptionIndex; }
    public void setCorrectOptionIndex(int correctOptionIndex) { this.correctOptionIndex = correctOptionIndex; }
    
    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
    
    public Flashcard.Difficulty getDifficulty() { return difficulty; }
    public void setDifficulty(Flashcard.Difficulty difficulty) { this.difficulty = difficulty; }
    
    // Utility methods
    public String getCorrectAnswer() {
        if (correctOptionIndex >= 0 && correctOptionIndex < options.size()) {
            return options.get(correctOptionIndex);
        }
        return "";
    }
    
    public boolean isCorrectAnswer(int selectedIndex) {
        return selectedIndex == correctOptionIndex;
    }
    
    public boolean isCorrectAnswer(String selectedAnswer) {
        return selectedAnswer.equals(getCorrectAnswer());
    }
    
    @Override
    public String toString() {
        return "Question{" +
                "id='" + id + '\'' +
                ", questionText='" + questionText + '\'' +
                ", difficulty=" + difficulty +
                ", correctOptionIndex=" + correctOptionIndex +
                '}';
    }
}
