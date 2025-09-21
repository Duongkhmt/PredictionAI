package com.example.DuDoanAI.service;

import com.example.DuDoanAI.model.ImageAnalysis;
import com.example.DuDoanAI.model.User;
import com.example.DuDoanAI.repository.ImageAnalysisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ImageAnalysisService {

    @Autowired
    private ImageAnalysisRepository imageAnalysisRepository;

    @Autowired
    private UserService userService;

    public ImageAnalysis saveImageAnalysis(ImageAnalysis imageAnalysis) {
        return imageAnalysisRepository.save(imageAnalysis);
    }

    public List<ImageAnalysis> getUserImageAnalyses() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            Optional<User> userOptional = userService.findByUsername(username);
            if (userOptional.isPresent()) {
                // Sử dụng method mới với analysisDate
                return imageAnalysisRepository.findByUserOrderByAnalysisDateDesc(userOptional.get());
            }
        }
        return List.of();
    }

    public Optional<ImageAnalysis> getImageAnalysisById(Long id) {
        return imageAnalysisRepository.findById(id);
    }

    public void deleteImageAnalysis(Long id) {
        imageAnalysisRepository.deleteById(id);
    }

    public Long getUserAnalysisCount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            Optional<User> userOptional = userService.findByUsername(username);
            if (userOptional.isPresent()) {
                return imageAnalysisRepository.countByUser(userOptional.get());
            }
        }
        return 0L;
    }

    // Các phương thức khác
    public List<ImageAnalysis> getAllAnalysesOrderByDate() {
        return imageAnalysisRepository.findByOrderByAnalysisDateDesc();
    }

    public List<ImageAnalysis> getAnalysesBySentimentLabel(String sentimentLabel) {
        return imageAnalysisRepository.findBySentimentLabelOrderByAnalysisDateDesc(sentimentLabel);
    }

    public Long countBySentimentLabel(String sentimentLabel) {
        return imageAnalysisRepository.countBySentimentLabel(sentimentLabel);
    }

    // Thêm phương thức mới
    public List<ImageAnalysis> getAnalysesByUserAndSentiment(User user, String sentimentLabel) {
        return imageAnalysisRepository.findByUserAndSentimentLabelOrderByAnalysisDateDesc(user, sentimentLabel);
    }
}