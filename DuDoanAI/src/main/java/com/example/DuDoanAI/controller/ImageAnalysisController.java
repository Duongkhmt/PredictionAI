package com.example.DuDoanAI.controller;

import com.example.DuDoanAI.model.ImageAnalysis;
import com.example.DuDoanAI.service.GeminiImageService;
import com.example.DuDoanAI.service.ImageAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/image-analysis")
public class ImageAnalysisController {

    @Autowired
    private GeminiImageService geminiImageService;

    @Autowired
    private ImageAnalysisService imageAnalysisService;

    @GetMapping
    public String showImageAnalysisForm(Model model) {
        model.addAttribute("analysisResult", null);
        return "image_analysis";
    }

    @PostMapping("/analyze")
    public String analyzeImage(@RequestParam("imageFile") MultipartFile imageFile,
                               Model model) {
        try {
            if (imageFile.isEmpty()) {
                model.addAttribute("error", "Vui lòng chọn hình ảnh để phân tích");
                return "image_analysis";
            }

            // Kiểm tra file type
            String contentType = imageFile.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                model.addAttribute("error", "File phải là hình ảnh");
                return "image_analysis";
            }

            // Phân tích hình ảnh
            String analysisResult = geminiImageService.analyzeImageSentiment(imageFile);
            model.addAttribute("analysisResult", analysisResult);
            model.addAttribute("fileName", imageFile.getOriginalFilename());

        } catch (Exception e) {
            model.addAttribute("error", "Lỗi phân tích hình ảnh: " + e.getMessage());
        }

        return "image_analysis";
    }

    @PostMapping("/api/analyze")
    @ResponseBody
    public ResponseEntity<?> analyzeImageApi(@RequestParam("image") MultipartFile imageFile) {
        try {
            if (imageFile.isEmpty()) {
                return ResponseEntity.badRequest().body("Vui lòng chọn hình ảnh");
            }

            String analysisResult = geminiImageService.analyzeImageSentiment(imageFile);
            return ResponseEntity.ok(analysisResult);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Lỗi phân tích hình ảnh: " + e.getMessage());
        }
    }

}