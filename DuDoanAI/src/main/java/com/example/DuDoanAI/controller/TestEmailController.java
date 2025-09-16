package com.example.DuDoanAI.controller;


import com.example.DuDoanAI.model.User;
import com.example.DuDoanAI.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.DuDoanAI.service.EmailService;

import java.util.Optional;

@RestController
@RequestMapping("/api/test")
public class TestEmailController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @GetMapping("/check-user")
    public ResponseEntity<String> checkUser(@RequestParam String email) {
        Optional<User> user = userRepository.findByUsername(email);
        if (user.isPresent()) {
            return ResponseEntity.ok("User found: " + user.get().getUsername());
        } else {
            return ResponseEntity.ok("User not found with email: " + email);
        }
    }

    @GetMapping("/test-email")
    public ResponseEntity<String> testEmail(@RequestParam String email) {
        try {
            emailService.sendResetPasswordEmail(email, "test-token-123");
            return ResponseEntity.ok("Test email sent to: " + email);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("Email error: " + e.getMessage());
        }
    }
}