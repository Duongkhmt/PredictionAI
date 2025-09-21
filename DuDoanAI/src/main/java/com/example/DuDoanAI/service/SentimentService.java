
package com.example.DuDoanAI.service;

import com.example.DuDoanAI.model.SentimentAnalysis;
import com.example.DuDoanAI.repository.SentimentAnalysisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SentimentService {

    @Autowired
    private SentimentAnalysisRepository repository;

    @Autowired
    private AIService aiService;

    /**
     * Phân tích cảm xúc và tạo lời khuyên cho văn bản ngắn
     */
    public SentimentAnalysis analyzeText(String text) {
        // Phân tích cảm xúc
        String sentiment = aiService.analyzeSentiment(text);

        // Tính độ tin cậy
        double confidence = aiService.calculateConfidence(text, sentiment);

        // Tạo lời khuyên cá nhân hóa
        String advice = aiService.generateAdvice(sentiment, text);

        // Lưu vào database
        SentimentAnalysis analysis = new SentimentAnalysis(text, sentiment, confidence, advice);
        return repository.save(analysis);
    }

    /**
     * Tính điểm sentiment dạng liên tục [-1..1] dựa trên nhãn và độ tin cậy từ
     * AIService.
     * Dương: Tích cực, Âm: Tiêu cực, 0: Trung tính
     */
    public Double calculateSentiment(String text) {
        String label = aiService.analyzeSentiment(text);
        double confidence = aiService.calculateConfidence(text, label);

        if ("Tích cực".equals(label)) {
            return confidence; // 0..1
        }
        if ("Tiêu cực".equals(label)) {
            return -confidence; // -1..0
        }
        return 0.0;
    }

    /**
     * Suy ra nhãn cảm xúc từ điểm liên tục với ngưỡng hợp lý.
     */
    public String getSentimentLabel(Double score) {
        if (score == null)
            return "Trung tính";
        // Ngưỡng có thể tinh chỉnh theo dữ liệu thực tế
        if (score > 0.3)
            return "Tích cực";
        if (score < -0.3)
            return "Tiêu cực";
        return "Trung tính";
    }

    /**
     * Phân tích cảm xúc cho văn bản dài bằng cách tách câu
     */
    public SentimentAnalysis analyzeLongText(String text) {
        // Tách văn bản thành các câu và phân tích từng câu
        String[] sentences = text.split("[.!?]+");

        int positiveCount = 0;
        int negativeCount = 0;
        int neutralCount = 0;
        double totalConfidence = 0;

        for (String sentence : sentences) {
            if (sentence.trim().isEmpty())
                continue;

            String sentiment = aiService.analyzeSentiment(sentence);
            double confidence = aiService.calculateConfidence(sentence, sentiment);

            totalConfidence += confidence;

            switch (sentiment) {
                case "Tích cực":
                    positiveCount++;
                    break;
                case "Tiêu cực":
                    negativeCount++;
                    break;
                default:
                    neutralCount++;
                    break;
            }
        }

        // Xác định cảm xúc tổng thể
        String overallSentiment;
        if (positiveCount > negativeCount && positiveCount > neutralCount) {
            overallSentiment = "Tích cực";
        } else if (negativeCount > positiveCount && negativeCount > neutralCount) {
            overallSentiment = "Tiêu cực";
        } else {
            overallSentiment = "Trung tính";
        }

        // Tính độ tin cậy trung bình
        double avgConfidence = sentences.length > 0 ? totalConfidence / sentences.length : 0.7;

        // Tạo lời khuyên dựa trên cảm xúc tổng thể
        String advice = aiService.generateAdvice(overallSentiment, text);

        // Lưu vào database
        SentimentAnalysis analysis = new SentimentAnalysis(text, overallSentiment, avgConfidence, advice);
        return repository.save(analysis);
    }

    /**
     * Lấy tất cả phân tích theo thứ tự mới nhất
     */
    public List<SentimentAnalysis> getAllAnalyses() {
        return repository.findAllOrderByCreatedAtDesc();
    }

    /**
     * Lấy phân tích theo loại cảm xúc
     */
    public List<SentimentAnalysis> getAnalysesBySentiment(String sentiment) {
        return repository.findBySentiment(sentiment);
    }

    /**
     * Đếm tổng số phân tích
     */
    public long getTotalAnalyses() {
        return repository.count();
    }

    /**
     * Lấy phân tích theo ID
     */
    public SentimentAnalysis getAnalysisById(Long id) {
        return repository.findById(id).orElse(null);
    }

    /**
     * Xóa phân tích theo ID
     */
    public void deleteAnalysis(Long id) {
        repository.deleteById(id);
    }

    /**
     * Lấy số liệu thống kê
     */
    public AnalysisStatistics getStatistics() {
        long total = getTotalAnalyses();
        long positiveCount = repository.countBySentiment("Tích cực");
        long negativeCount = repository.countBySentiment("Tiêu cực");
        long neutralCount = repository.countBySentiment("Trung tính");

        return new AnalysisStatistics(total, positiveCount, negativeCount, neutralCount);
    }

    /**
     * Lấy phân tích gần đây
     */
    public List<SentimentAnalysis> getRecentAnalyses(int limit) {
        return repository.findTopNByOrderByCreatedAtDesc(limit);
    }

    /**
     * Lớp inner cho thống kê
     */
    public static class AnalysisStatistics {
        private final long total;
        private final long positiveCount;
        private final long negativeCount;
        private final long neutralCount;

        public AnalysisStatistics(long total, long positiveCount, long negativeCount, long neutralCount) {
            this.total = total;
            this.positiveCount = positiveCount;
            this.negativeCount = negativeCount;
            this.neutralCount = neutralCount;
        }

        public long getTotal() {
            return total;
        }

        public long getPositiveCount() {
            return positiveCount;
        }

        public long getNegativeCount() {
            return negativeCount;
        }

        public long getNeutralCount() {
            return neutralCount;
        }

        public double getPositivePercentage() {
            return total > 0 ? (double) positiveCount / total * 100 : 0;
        }

        public double getNegativePercentage() {
            return total > 0 ? (double) negativeCount / total * 100 : 0;
        }

        public double getNeutralPercentage() {
            return total > 0 ? (double) neutralCount / total * 100 : 0;
        }
    }

    /**
     * Phân tích hàng loạt nhiều văn bản
     */
    public List<SentimentAnalysis> analyzeMultipleTexts(List<String> texts) {
        return texts.stream()
                .map(this::analyzeText)
                .toList();
    }

    /**
     * Cập nhật lời khuyên cho phân tích hiện có
     */
    public SentimentAnalysis updateAdvice(Long analysisId) {
        SentimentAnalysis analysis = repository.findById(analysisId).orElse(null);
        if (analysis != null) {
            String newAdvice = aiService.generateAdvice(analysis.getSentiment(), analysis.getTextInput());
            analysis.setAdvice(newAdvice);
            return repository.save(analysis);
        }
        return null;
    }

    /**
     * Tìm kiếm phân tích theo từ khóa
     */
    public List<SentimentAnalysis> searchAnalyses(String keyword) {
        return repository.findByTextInputContainingIgnoreCase(keyword);
    }

    /**
     * Lấy phân tích trong khoảng thời gian
     */
    public List<SentimentAnalysis> getAnalysesByTimeRange(LocalDateTime start, LocalDateTime end) {
        return repository.findByCreatedAtBetweenOrderByCreatedAtDesc(start, end);
    }

    /**
     * Lấy phân tích gần đây (24 giờ)
     */
    public List<SentimentAnalysis> getRecentAnalyses() {
        LocalDateTime yesterday = LocalDateTime.now().minusHours(24);
        return repository.findByCreatedAtAfterOrderByCreatedAtDesc(yesterday);
    }

    /**
     * Xóa tất cả phân tích
     */
    public void deleteAllAnalyses() {
        repository.deleteAll();
    }

    /**
     * Kiểm tra xem có phân tích nào không
     */
    public boolean hasAnalyses() {
        return repository.count() > 0;
    }

    /**
     * Lấy phân tích mới nhất
     */
    public SentimentAnalysis getLatestAnalysis() {
        List<SentimentAnalysis> analyses = getAllAnalyses();
        return analyses.isEmpty() ? null : analyses.get(0);
    }
}