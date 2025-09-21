package com.example.DuDoanAI.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@RequestMapping("/api/gemini")
public class GeminiTestController {

    @Value("${gemini.api.models.url}")
    private String modelsUrl;

    @Value("${gemini.api.key}")
    private String apiKey;

    @GetMapping("/test-connection")
    public ResponseEntity<String> testConnection() {
        try {
            String response = WebClient.create()
                    .get()
                    .uri(modelsUrl + "?key=" + apiKey)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return ResponseEntity.ok("✅ Kết nối thành công!\n" + response);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("❌ Lỗi kết nối: " + e.getMessage());
        }
    }

    @GetMapping("/available-models")
    public ResponseEntity<String> getAvailableModels() {
        try {
            String response = WebClient.create()
                    .get()
                    .uri(modelsUrl + "?key=" + apiKey)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // Parse và lọc các models hỗ trợ vision
            JsonNode rootNode = new ObjectMapper().readTree(response);
            JsonNode models = rootNode.path("models");

            StringBuilder result = new StringBuilder("🤖 Models hỗ trợ vision:\n\n");
            for (JsonNode model : models) {
                String name = model.path("name").asText();
                String description = model.path("description").asText();
                if (name.contains("vision") || description.toLowerCase().contains("vision")) {
                    result.append("• ").append(name).append("\n");
                    result.append("  ").append(description).append("\n\n");
                }
            }

            return ResponseEntity.ok(result.toString());
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("❌ Lỗi: " + e.getMessage());
        }
    }
}
