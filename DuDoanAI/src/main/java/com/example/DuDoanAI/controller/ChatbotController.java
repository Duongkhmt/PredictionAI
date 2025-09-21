package com.example.DuDoanAI.controller;

import com.example.DuDoanAI.service.ChatbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/chatbot")
public class ChatbotController {

    @Autowired
    private ChatbotService chatbotService;

    @GetMapping
    public String showChatbotPage() {
        return "chatbot";
    }

    @PostMapping("/chat")
    @ResponseBody
    public ResponseEntity<String> chat(@RequestParam String message) {
        try {
            String response = chatbotService.chatWithGemini(message);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Xin lỗi, có lỗi xảy ra: " + e.getMessage());
        }
    }

    @PostMapping("/chat-with-context")
    @ResponseBody
    public ResponseEntity<String> chatWithContext(
            @RequestParam String message,
            @RequestParam String history) {
        try {
            String response = chatbotService.chatWithContext(message, history);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Xin lỗi, có lỗi xảy ra: " + e.getMessage());
        }
    }
}