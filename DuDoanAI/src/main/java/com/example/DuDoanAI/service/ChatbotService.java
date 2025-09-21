package com.example.DuDoanAI.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public class ChatbotService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.model}")
    private String modelName;

    @Value("${gemini.api.generate.url}")
    private String generateUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final WebClient webClient = WebClient.create();

    public String chatWithGemini(String message) {
        try {
            Map<String, Object> requestBody = createChatRequest(message);

            String response = webClient.post()
                    .uri(generateUrl.replace("${gemini.api.model}", modelName) + "?key=" + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return parseChatResponse(response);

        } catch (Exception e) {
            return "Xin lỗi, tôi đang gặp chút trục trặc. Bạn có thể thử lại sau được không? ❤️";
        }
    }

    private Map<String, Object> createChatRequest(String message) {
        Map<String, Object> request = new HashMap<>();

        // System prompt để chatbot trở thành người tâm sự
        String systemPrompt = "Bạn là một người bạn thân thiết, luôn lắng nghe và thấu hiểu. " +
                "Hãy trò chuyện thân mật, đồng cảm và đưa ra những lời khuyên chân thành. " +
                "Luôn trả lời bằng tiếng Việt với giọng điệu ấm áp, gần gũi. " +
                "Hãy là một người bạn biết lắng nghe và chia sẻ.";

        Map<String, Object>[] contents = new Map[]{
                Map.of("parts", new Object[]{
                        Map.of("text", systemPrompt + "\n\nNgười dùng: " + message + "\n\nBạn:")
                })
        };

        request.put("contents", contents);

        // Cấu hình generation
        Map<String, Object> generationConfig = Map.of(
                "temperature", 0.8,  // Độ sáng tạo cao hơn cho chat
                "maxOutputTokens", 1024,
                "topP", 0.9,
                "topK", 40
        );

        request.put("generationConfig", generationConfig);

        // Safety settings
        request.put("safetySettings", new Object[]{
                Map.of("category", "HARM_CATEGORY_HARASSMENT", "threshold", "BLOCK_MEDIUM_AND_ABOVE"),
                Map.of("category", "HARM_CATEGORY_HATE_SPEECH", "threshold", "BLOCK_MEDIUM_AND_ABOVE"),
                Map.of("category", "HARM_CATEGORY_SEXUALLY_EXPLICIT", "threshold", "BLOCK_MEDIUM_AND_ABOVE"),
                Map.of("category", "HARM_CATEGORY_DANGEROUS_CONTENT", "threshold", "BLOCK_MEDIUM_AND_ABOVE")
        });

        return request;
    }

    private String parseChatResponse(String response) {
        try {
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode candidates = rootNode.path("candidates");

            if (candidates.isArray() && candidates.size() > 0) {
                JsonNode content = candidates.get(0).path("content");
                JsonNode parts = content.path("parts");

                if (parts.isArray() && parts.size() > 0) {
                    String responseText = parts.get(0).path("text").asText();
                    // Lọc bỏ phần "Bạn:" nếu có
                    return responseText.replaceFirst("^Bạn:\\s*", "").trim();
                }
            }

            return "Tôi không hiểu câu trả lời từ AI. Bạn có thể thử lại không?";

        } catch (Exception e) {
            return "Có lỗi xảy ra khi xử lý phản hồi: " + e.getMessage();
        }
    }

    // Method để chat với context (lịch sử hội thoại)
    public String chatWithContext(String message, String conversationHistory) {
        try {
            Map<String, Object> requestBody = createChatRequestWithContext(message, conversationHistory);

            String response = webClient.post()
                    .uri(generateUrl.replace("${gemini.api.model}", modelName) + "?key=" + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return parseChatResponse(response);

        } catch (Exception e) {
            return "Xin lỗi, tôi đang gặp chút trục trặc. Bạn có thể thử lại sau được không? ❤️";
        }
    }

    private Map<String, Object> createChatRequestWithContext(String message, String conversationHistory) {
        Map<String, Object> request = new HashMap<>();

        String systemPrompt = "Bạn là một người bạn thân thiết, luôn lắng nghe và thấu hiểu. " +
                "Dưới đây là lịch sử trò chuyện giữa bạn và người dùng:\n\n" +
                conversationHistory + "\n\n" +
                "Hãy tiếp tục cuộc trò chuyện một cách tự nhiên, đồng cảm. " +
                "Luôn trả lời bằng tiếng Việt với giọng điệu ấm áp, gần gũi.";

        Map<String, Object>[] contents = new Map[]{
                Map.of("parts", new Object[]{
                        Map.of("text", systemPrompt + "\n\nNgười dùng: " + message + "\n\nBạn:")
                })
        };

        request.put("contents", contents);

        Map<String, Object> generationConfig = Map.of(
                "temperature", 0.7,
                "maxOutputTokens", 1024,
                "topP", 0.9,
                "topK", 40
        );

        request.put("generationConfig", generationConfig);
        request.put("safetySettings", new Object[]{
                Map.of("category", "HARM_CATEGORY_HARASSMENT", "threshold", "BLOCK_MEDIUM_AND_ABOVE")
        });

        return request;
    }
}