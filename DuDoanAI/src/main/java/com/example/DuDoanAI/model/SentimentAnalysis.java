package com.example.DuDoanAI.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sentiment_analysis")
public class SentimentAnalysis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "text_input", columnDefinition = "TEXT")
    private String textInput;

    private String sentiment;
    private Double confidence;

    @Column(columnDefinition = "TEXT")
    private String advice; // Thêm trường lời khuyên

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Constructors
    public SentimentAnalysis() {
        this.createdAt = LocalDateTime.now();
    }

    public SentimentAnalysis(String textInput, String sentiment, Double confidence, String advice) {
        this();
        this.textInput = textInput;
        this.sentiment = sentiment;
        this.confidence = confidence;
        this.advice = advice;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTextInput() { return textInput; }
    public void setTextInput(String textInput) { this.textInput = textInput; }

    public String getSentiment() { return sentiment; }
    public void setSentiment(String sentiment) { this.sentiment = sentiment; }

    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }

    public String getAdvice() { return advice; }
    public void setAdvice(String advice) { this.advice = advice; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
