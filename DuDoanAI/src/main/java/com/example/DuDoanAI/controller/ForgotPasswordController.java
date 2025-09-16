package com.example.DuDoanAI.controller;

import com.example.DuDoanAI.model.PasswordResetToken;
import com.example.DuDoanAI.model.User;
import com.example.DuDoanAI.repository.PasswordResetTokenRepository;
import com.example.DuDoanAI.repository.UserRepository;
import com.example.DuDoanAI.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Controller
public class ForgotPasswordController {
    private static final Logger logger = LoggerFactory.getLogger(ForgotPasswordController.class);

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordResetTokenRepository tokenRepository;
    @Autowired private EmailService emailService;
    @Autowired private PasswordEncoder passwordEncoder;

    // Trang nhập Gmail
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        logger.info("Showing forgot password form");
        return "forgot_password";
    }

    // Xử lý gửi link reset
    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("username") String username, Model model) {
        logger.info("Processing forgot password for username: {}", username);

        try {
            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) {
                logger.warn("User not found with username: {}", username);
                model.addAttribute("errorMessage", "Không tìm thấy tài khoản với Gmail này!");
                return "forgot_password";
            }

            User user = userOpt.get();
            logger.info("Found user: {}", user.getUsername());

            String token = UUID.randomUUID().toString();
            logger.info("Generated token: {}", token);

            // Xóa token cũ nếu có
            tokenRepository.deleteByUser(user);

            PasswordResetToken resetToken = new PasswordResetToken();
            resetToken.setToken(token);
            resetToken.setUser(user);
            resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(15));

            tokenRepository.save(resetToken);
            logger.info("Token saved to database");

            // Gửi email
            emailService.sendResetPasswordEmail(user.getUsername(), token);
            logger.info("Email sent successfully");

            model.addAttribute("successMessage", "Link đặt lại mật khẩu đã được gửi đến email của bạn!");

        } catch (Exception e) {
            logger.error("Error in processForgotPassword: ", e);
            model.addAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
        }

        return "forgot_password";
    }

    // Trang nhập mật khẩu mới
    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);
        if (tokenOpt.isEmpty() || tokenOpt.get().isExpired()) {
            model.addAttribute("errorMessage", "Token không hợp lệ hoặc đã hết hạn!");
            return "reset_password";
        }
        model.addAttribute("token", token);
        return "reset_password";
    }

    // Xử lý cập nhật mật khẩu mới
    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token,
                                       @RequestParam("password") String password,
                                       Model model) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);
        if (tokenOpt.isEmpty() || tokenOpt.get().isExpired()) {
            model.addAttribute("errorMessage", "Token không hợp lệ hoặc đã hết hạn!");
            return "reset_password";
        }

        PasswordResetToken resetToken = tokenOpt.get();
        User user = resetToken.getUser();

        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        tokenRepository.delete(resetToken); // Xoá token đã dùng

        model.addAttribute("successMessage", "Mật khẩu đã được đặt lại thành công!");
        return "login"; // chuyển về trang login
    }
}
