//package com.example.DuDoanAI.service;
//
//import com.example.DuDoanAI.config.GeminiConfig;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.io.ByteArrayResource;
//import org.springframework.http.MediaType;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//import org.springframework.web.reactive.function.client.WebClient;
//
//import java.util.Base64;
//import java.util.HashMap;
//import java.util.Map;
//
//@Service
//public class GeminiImageService {
//
//    @Autowired
//    private GeminiConfig geminiConfig;
//
//    @Autowired
//    private WebClient geminiWebClient;
//
//    @Autowired
//    private SentimentService sentimentService;
//
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    public String analyzeImageSentiment(MultipartFile imageFile) {
//        try {
//            // Convert image to base64
//            String base64Image = convertToBase64(imageFile);
//
//            // Prepare request for Gemini API
//            Map<String, Object> requestBody = createGeminiRequest(base64Image, imageFile.getContentType());
//
//            // Call Gemini API
//            String response = geminiWebClient.post()
//                    .uri("/models/gemini-pro-vision:generateContent?key=" + geminiConfig.getApiKey())
//                    .bodyValue(requestBody)
//                    .retrieve()
//                    .bodyToMono(String.class)
//                    .block();
//
//            // Parse response
//            String analysisResult = parseGeminiResponse(response);
//
//            // Phân tích sentiment từ kết quả
//            Double sentimentScore = sentimentService.calculateSentiment(analysisResult);
//            String sentimentLabel = sentimentService.getSentimentLabel(sentimentScore);
//
//            return String.format("""
//                📊 Phân tích hình ảnh:
//
//                🔍 Mô tả: %s
//
//                💡 Sentiment Score: %.2f
//                🏷️ Sentiment Label: %s
//
//                📝 Chi tiết: %s
//                """, analysisResult, sentimentScore, sentimentLabel, getSentimentDescription(sentimentScore));
//
//        } catch (Exception e) {
//            throw new RuntimeException("Lỗi phân tích hình ảnh: " + e.getMessage(), e);
//        }
//    }
//
//    private String convertToBase64(MultipartFile file) throws Exception {
//        byte[] bytes = file.getBytes();
//        return Base64.getEncoder().encodeToString(bytes);
//    }
//
//    private Map<String, Object> createGeminiRequest(String base64Image, String mimeType) {
//        Map<String, Object> request = new HashMap<>();
//
//        Map<String, Object> part = new HashMap<>();
//        part.put("inline_data", Map.of(
//                "mime_type", mimeType,
//                "data", base64Image
//        ));
//
//        Map<String, Object> content = new HashMap<>();
//        content.put("parts", new Object[]{part});
//
//        Map<String, Object> contents = new HashMap<>();
//        contents.put("contents", new Object[]{Map.of(
//                "parts", new Object[]{Map.of(
//                        "text", "Phân tích cảm xúc và nội dung của hình ảnh này. " +
//                                "Mô tả chi tiết những gì bạn thấy và đánh giá cảm xúc tổng thể. " +
//                                "Trả lời bằng Tiếng Việt."
//                )}
//        )});
//
//        request.put("contents", new Object[]{
//                Map.of("parts", new Object[]{
//                        Map.of("text", "Phân tích hình ảnh này và mô tả cảm xúc tổng thể. Trả lời bằng Tiếng Việt."),
//                        part
//                })
//        });
//
//        return request;
//    }
//
//    private String parseGeminiResponse(String response) throws Exception {
//        JsonNode rootNode = objectMapper.readTree(response);
//        JsonNode candidates = rootNode.path("candidates");
//        if (candidates.isArray() && candidates.size() > 0) {
//            JsonNode content = candidates.get(0).path("content");
//            JsonNode parts = content.path("parts");
//            if (parts.isArray() && parts.size() > 0) {
//                return parts.get(0).path("text").asText();
//            }
//        }
//        throw new RuntimeException("Không thể phân tích phản hồi từ Gemini API");
//    }
//
//    private String getSentimentDescription(Double score) {
//        if (score >= 0.7) return "Hình ảnh rất tích cực và vui vẻ";
//        if (score >= 0.3) return "Hình ảnh mang tính tích cực";
//        if (score >= -0.3) return "Hình ảnh trung lập";
//        if (score >= -0.7) return "Hình ảnh mang tính tiêu cực";
//        return "Hình ảnh rất tiêu cực";
//    }
//}

package com.example.DuDoanAI.service;

import com.example.DuDoanAI.config.GeminiConfig;
import com.example.DuDoanAI.model.ImageAnalysis;
import com.example.DuDoanAI.model.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class GeminiImageService {

    @Autowired
    private GeminiConfig geminiConfig;

    @Autowired
    private WebClient geminiWebClient;

    @Autowired
    private SentimentService sentimentService;

    @Autowired
    private ImageAnalysisService imageAnalysisService;

    @Autowired
    private UserService userService;

    @Value("${gemini.api.generate.url}")
    private String generateUrl;

    @Value("${gemini.api.model}")
    private String modelName;

    @Value("${gemini.api.key}")
    private String apiKey;


    private final ObjectMapper objectMapper = new ObjectMapper();

//    public String analyzeImageSentiment(MultipartFile imageFile) {
//        try {
//            String base64Image = convertToBase64(imageFile);
//            Map<String, Object> requestBody = createGeminiRequest(base64Image, imageFile.getContentType());
//
//            // Sử dụng URL đúng
//            String response = WebClient.create()
//                    .post()
//                    .uri(generateUrl.replace("${gemini.api.model}", modelName) + "?key=" + apiKey)
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .bodyValue(requestBody)
//                    .retrieve()
//                    .bodyToMono(String.class)
//                    .block();
//            // Parse response
//            String analysisResult = parseGeminiResponse(response);
//
//            // Phân tích sentiment từ kết quả
//            Double sentimentScore = sentimentService.calculateSentiment(analysisResult);
//            String sentimentLabel = sentimentService.getSentimentLabel(sentimentScore);
//
//            return String.format("""
//                📊 Phân tích hình ảnh:
//
//                🔍 Mô tả: %s
//
//                💡 Sentiment Score: %.2f
//                🏷️ Sentiment Label: %s
//
//                📝 Chi tiết: %s
//                """, analysisResult, sentimentScore, sentimentLabel,
//                    getSentimentDescription(sentimentScore));
//
//        } catch (WebClientResponseException e) {
//            throw new RuntimeException("Lỗi Gemini API: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(), e);
//        } catch (Exception e) {
//            throw new RuntimeException("Lỗi phân tích hình ảnh: " + e.getMessage(), e);
//        }
//    }

    public String analyzeImageSentiment(MultipartFile imageFile) {
        try {
            String base64Image = convertToBase64(imageFile);
            Map<String, Object> requestBody = createGeminiRequest(base64Image, imageFile.getContentType());

            String response = WebClient.create()
                    .post()
                    .uri(generateUrl.replace("${gemini.api.model}", modelName) + "?key=" + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            String analysisResult = parseGeminiResponse(response);
            Double sentimentScore = sentimentService.calculateSentiment(analysisResult);
            String sentimentLabel = sentimentService.getSentimentLabel(sentimentScore);
            String details = getSentimentDescription(sentimentScore);

            // Lưu vào database
            saveAnalysisToDatabase(imageFile, analysisResult, sentimentScore, sentimentLabel, details);

            return String.format("""
            📊 Phân tích hình ảnh:
            
            🔍 Mô tả: %s
            
            💡 Sentiment Score: %.2f
            🏷️ Sentiment Label: %s
            
            📝 Chi tiết: %s
            """, analysisResult, sentimentScore, sentimentLabel, details);

        } catch (WebClientResponseException e) {
            throw new RuntimeException("Lỗi Gemini API: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi phân tích hình ảnh: " + e.getMessage(), e);
        }
    }

    private void saveAnalysisToDatabase(MultipartFile imageFile, String analysisResult,
                                        Double sentimentScore, String sentimentLabel, String details) {
        try {
            ImageAnalysis imageAnalysis = new ImageAnalysis();
            imageAnalysis.setFileName(generateFileName());
            imageAnalysis.setOriginalFileName(imageFile.getOriginalFilename());
            imageAnalysis.setFileType(imageFile.getContentType());
            imageAnalysis.setFileSize(imageFile.getSize());
            imageAnalysis.setDescription(analysisResult);
            imageAnalysis.setSentimentScore(sentimentScore);
            imageAnalysis.setSentimentLabel(sentimentLabel);
            imageAnalysis.setDetails(details);
            imageAnalysis.setAnalysisDate(LocalDateTime.now()); // Set analysisDate

            // Lấy user hiện tại
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                String username = authentication.getName();
                Optional<User> userOptional = userService.findByUsername(username);
                if (userOptional.isPresent()) {
                    imageAnalysis.setUser(userOptional.get());
                }
            }

            imageAnalysisService.saveImageAnalysis(imageAnalysis);
        } catch (Exception e) {
            System.err.println("Lỗi khi lưu phân tích hình ảnh: " + e.getMessage());
        }
    }

    private String generateFileName() {
        return "image_analysis_" + System.currentTimeMillis();
    }

    private String convertToBase64(MultipartFile file) throws Exception {
        byte[] bytes = file.getBytes();
        return Base64.getEncoder().encodeToString(bytes);
    }

private Map<String, Object> createGeminiRequest(String base64Image, String mimeType) {
    Map<String, Object> request = new HashMap<>();

    Map<String, Object>[] contents = new Map[]{
            Map.of("parts", new Object[]{
                    Map.of("text", "Phân tích cảm xúc và nội dung của hình ảnh này. Mô tả chi tiết và đánh giá cảm xúc tổng thể. Trả lời bằng Tiếng Việt."),
                    Map.of("inline_data", Map.of(
                            "mime_type", mimeType,
                            "data", base64Image
                    ))
            })
    };

    request.put("contents", contents);

    // Thêm generation config
    Map<String, Object> generationConfig = Map.of(
            "temperature", 0.4,
            "maxOutputTokens", 2048,
            "topP", 0.8,
            "topK", 40
    );

    request.put("generationConfig", generationConfig);
    request.put("safetySettings", new Object[]{
            Map.of(
                    "category", "HARM_CATEGORY_HARASSMENT",
                    "threshold", "BLOCK_MEDIUM_AND_ABOVE"
            )
    });

    return request;
}

    private String parseGeminiResponse(String response) throws Exception {
        try {
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode candidates = rootNode.path("candidates");

            if (candidates.isArray() && candidates.size() > 0) {
                JsonNode content = candidates.get(0).path("content");
                JsonNode parts = content.path("parts");

                if (parts.isArray() && parts.size() > 0) {
                    return parts.get(0).path("text").asText();
                }
            }

            throw new RuntimeException("Không thể phân tích phản hồi từ Gemini API: " + response);

        } catch (Exception e) {
            throw new RuntimeException("Lỗi parse JSON response: " + response, e);
        }
    }

    private String getSentimentDescription(Double score) {
        if (score >= 0.7) return "Hình ảnh rất tích cực và vui vẻ";
        if (score >= 0.3) return "Hình ảnh mang tính tích cực";
        if (score >= -0.3) return "Hình ảnh trung lập";
        if (score >= -0.7) return "Hình ảnh mang tính tiêu cực";
        return "Hình ảnh rất tiêu cực";
    }

    // Method test API key
    public String testApiKey() {
        try {
            String response = geminiWebClient.get()
                    .uri("/models?key=" + apiKey)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return "API Key hợp lệ. Models: " + response;
        } catch (Exception e) {
            return "Lỗi API Key: " + e.getMessage();
        }
    }
}