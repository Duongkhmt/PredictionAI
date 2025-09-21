
package com.example.DuDoanAI.controller;

import com.example.DuDoanAI.model.SentimentAnalysis;
import com.example.DuDoanAI.service.SentimentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/")
public class SentimentController {

    @Autowired
    private SentimentService sentimentService;

    // Helper method để thêm thông tin authentication vào model
    private void addAuthenticationInfo(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                !authentication.getName().equals("anonymousUser")) {
            model.addAttribute("username", authentication.getName());
            model.addAttribute("isAuthenticated", true);
        } else {
            model.addAttribute("isAuthenticated", false);
        }
    }

    // Trang chủ (public) - tự động chuyển hướng nếu đã đăng nhập
    @GetMapping("/")
    public String home(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null &&
                authentication.isAuthenticated() &&
                !authentication.getName().equals("anonymousUser");

        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("username", isAuthenticated ? authentication.getName() : "");

        // Tự động chuyển hướng đến dashboard nếu đã đăng nhập
        if (isAuthenticated) {
            return "redirect:/dashboard";
        }

        return "home";
    }

    @GetMapping("/home")
    public String homePage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null &&
                authentication.isAuthenticated() &&
                !authentication.getName().equals("anonymousUser");

        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("username", isAuthenticated ? authentication.getName() : "");

        // Tự động chuyển hướng đến dashboard nếu đã đăng nhập
        if (isAuthenticated) {
            return "redirect:/dashboard";
        }

        return "home";
    }

    // Trang dashboard chính (sau đăng nhập)
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        addAuthenticationInfo(model);

        // Kiểm tra nếu chưa đăng nhập thì chuyển về trang login
        if (!model.containsAttribute("isAuthenticated") ||
                Boolean.FALSE.equals(model.getAttribute("isAuthenticated"))) {
            return "redirect:/login";
        }

        // Thêm thống kê cho dashboard
        SentimentService.AnalysisStatistics stats = sentimentService.getStatistics();
        model.addAttribute("totalAnalyses", stats.getTotal());
        model.addAttribute("positiveCount", stats.getPositiveCount());
        model.addAttribute("negativeCount", stats.getNegativeCount());
        model.addAttribute("neutralCount", stats.getNeutralCount());
        model.addAttribute("positivePercentage", String.format("%.1f", stats.getPositivePercentage()));
        model.addAttribute("negativePercentage", String.format("%.1f", stats.getNegativePercentage()));
        model.addAttribute("neutralPercentage", String.format("%.1f", stats.getNeutralPercentage()));

        // Thêm phân tích gần đây
        List<SentimentAnalysis> recentAnalyses = sentimentService.getRecentAnalyses(5);
        model.addAttribute("recentAnalyses", recentAnalyses);

        return "dashboard";
    }

    // Trang phân tích - thêm nút quay về dashboard
    @GetMapping("/analyze")
    public String analyzePage(Model model) {
        addAuthenticationInfo(model);

        // Kiểm tra nếu chưa đăng nhập thì chuyển về trang login
        if (!model.containsAttribute("isAuthenticated") ||
                Boolean.FALSE.equals(model.getAttribute("isAuthenticated"))) {
            return "redirect:/login";
        }

        model.addAttribute("totalAnalyses", sentimentService.getTotalAnalyses());

        // Hiển thị phân tích mới nhất nếu có
        SentimentAnalysis latest = sentimentService.getLatestAnalysis();
        if (latest != null) {
            model.addAttribute("latestAnalysis", latest);
        }

        return "index";
    }

    // Xử lý phân tích
    @PostMapping("/analyze-sentiment")
    public String analyzeSentiment(@RequestParam String text,
                                   @RequestParam(required = false) Boolean detailed,
                                   Model model) {
        addAuthenticationInfo(model);

        // Kiểm tra nếu chưa đăng nhập thì chuyển về trang login
        if (!model.containsAttribute("isAuthenticated") ||
                Boolean.FALSE.equals(model.getAttribute("isAuthenticated"))) {
            return "redirect:/login";
        }

        if (text == null || text.trim().isEmpty()) {
            model.addAttribute("error", "Vui lòng nhập văn bản để phân tích");
            return analyzePage(model);
        }

        SentimentAnalysis analysis;
        if (Boolean.TRUE.equals(detailed) && text.length() > 100) {
            analysis = sentimentService.analyzeLongText(text);
        } else {
            analysis = sentimentService.analyzeText(text);
        }

        model.addAttribute("analysis", analysis);
        model.addAttribute("showResult", true);
        model.addAttribute("totalAnalyses", sentimentService.getTotalAnalyses());

        return "index";
    }

    // Lịch sử phân tích - thêm nút quay về dashboard
    @GetMapping("/predictions")
    public String showPredictions(Model model) {
        addAuthenticationInfo(model);

        // Kiểm tra nếu chưa đăng nhập thì chuyển về trang login
        if (!model.containsAttribute("isAuthenticated") ||
                Boolean.FALSE.equals(model.getAttribute("isAuthenticated"))) {
            return "redirect:/login";
        }

        List<SentimentAnalysis> analyses = sentimentService.getAllAnalyses();
        model.addAttribute("analyses", analyses);
        return "predictions";
    }

    // Thống kê - thêm nút quay về dashboard
    @GetMapping("/statistics")
    public String showStatistics(Model model) {
        addAuthenticationInfo(model);

        // Kiểm tra nếu chưa đăng nhập thì chuyển về trang login
        if (!model.containsAttribute("isAuthenticated") ||
                Boolean.FALSE.equals(model.getAttribute("isAuthenticated"))) {
            return "redirect:/login";
        }

        SentimentService.AnalysisStatistics stats = sentimentService.getStatistics();

        model.addAttribute("totalAnalyses", stats.getTotal());
        model.addAttribute("positiveCount", stats.getPositiveCount());
        model.addAttribute("negativeCount", stats.getNegativeCount());
        model.addAttribute("neutralCount", stats.getNeutralCount());
        model.addAttribute("positivePercentage", String.format("%.1f", stats.getPositivePercentage()));
        model.addAttribute("negativePercentage", String.format("%.1f", stats.getNegativePercentage()));
        model.addAttribute("neutralPercentage", String.format("%.1f", stats.getNeutralPercentage()));

        return "statistics";
    }

    // Tìm kiếm
    @GetMapping("/search")
    public String searchAnalyses(@RequestParam String keyword, Model model) {
        addAuthenticationInfo(model);

        // Kiểm tra nếu chưa đăng nhập thì chuyển về trang login
        if (!model.containsAttribute("isAuthenticated") ||
                Boolean.FALSE.equals(model.getAttribute("isAuthenticated"))) {
            return "redirect:/login";
        }

        List<SentimentAnalysis> results = sentimentService.searchAnalyses(keyword);
        model.addAttribute("analyses", results);
        model.addAttribute("searchKeyword", keyword);
        return "predictions";
    }

    // Cập nhật lời khuyên
    @PostMapping("/update-advice/{id}")
    public String updateAdvice(@PathVariable Long id, Model model) {
        addAuthenticationInfo(model);

        // Kiểm tra nếu chưa đăng nhập thì chuyển về trang login
        if (!model.containsAttribute("isAuthenticated") ||
                Boolean.FALSE.equals(model.getAttribute("isAuthenticated"))) {
            return "redirect:/login";
        }

        SentimentAnalysis updated = sentimentService.updateAdvice(id);
        if (updated != null) {
            model.addAttribute("message", "Lời khuyên đã được cập nhật!");
        } else {
            model.addAttribute("error", "Không tìm thấy phân tích");
        }
        return showPredictions(model);
    }

    // Xóa phân tích
    @PostMapping("/delete-analysis/{id}")
    public String deleteAnalysis(@PathVariable Long id, Model model) {
        addAuthenticationInfo(model);

        // Kiểm tra nếu chưa đăng nhập thì chuyển về trang login
        if (!model.containsAttribute("isAuthenticated") ||
                Boolean.FALSE.equals(model.getAttribute("isAuthenticated"))) {
            return "redirect:/login";
        }

        sentimentService.deleteAnalysis(id);
        model.addAttribute("message", "Đã xóa phân tích thành công!");
        return showPredictions(model);
    }

    // Xóa tất cả
    @PostMapping("/delete-all")
    public String deleteAllAnalyses(Model model) {
        addAuthenticationInfo(model);

        // Kiểm tra nếu chưa đăng nhập thì chuyển về trang login
        if (!model.containsAttribute("isAuthenticated") ||
                Boolean.FALSE.equals(model.getAttribute("isAuthenticated"))) {
            return "redirect:/login";
        }

        sentimentService.deleteAllAnalyses();
        model.addAttribute("message", "Đã xóa tất cả phân tích!");
        return showPredictions(model);
    }


}
