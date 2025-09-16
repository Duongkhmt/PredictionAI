package com.example.DuDoanAI.dto;

public class SentimentAnalysisDTO {
    private String textInput;
    private String sentiment;
    private Double confidence;

    // Constructors
    public SentimentAnalysisDTO() {}

    public SentimentAnalysisDTO(String textInput, String sentiment, Double confidence) {
        this.textInput = textInput;
        this.sentiment = sentiment;
        this.confidence = confidence;
    }

    // Getters and Setters
    public String getTextInput() { return textInput; }
    public void setTextInput(String textInput) { this.textInput = textInput; }

    public String getSentiment() { return sentiment; }
    public void setSentiment(String sentiment) { this.sentiment = sentiment; }

    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }
}