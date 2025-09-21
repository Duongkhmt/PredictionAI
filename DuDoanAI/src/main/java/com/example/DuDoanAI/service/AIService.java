//package com.example.DuDoanAI.service;
//
//import org.springframework.stereotype.Service;
//import org.springframework.web.reactive.function.client.WebClient;
//import org.springframework.beans.factory.annotation.Value;
//import reactor.core.publisher.Mono;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import java.util.*;
//import java.util.regex.Pattern;
//
//@Service
//public class AIService {
//
//    @Value("${gemini.api.key:}")
//
//    private String geminiApiKey;
//
////    @Value("${gemini.api.url:https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent}")
////    private String geminiApiUrl;
//    @Value("${gemini.api.url:https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent}")
//    private String geminiApiUrl;
//
//    private final WebClient webClient;
//    private final ObjectMapper objectMapper;
//    private final Pattern wordPattern = Pattern.compile("[^a-zàáâãèéêìíòóôõùúýăđĩũơưạảấầẩẫậắằẳẵặẹẻẽếềểễệỉịọỏốồổỗộớờởỡợụủứừửữựỳỵỷỹ]");
//
//    // Từ điển dự phòng
//    private final Map<String, Double> positiveWords = new HashMap<>();
//    private final Map<String, Double> negativeWords = new HashMap<>();
//    private final Map<String, Double> negativePhrases = new HashMap<>();
//
//    public AIService(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
//        this.webClient = webClientBuilder.build();
//        this.objectMapper = objectMapper;
//        initializeWordMaps();
//        initializePhraseMaps();
//    }
//
//    private void initializeWordMaps() {
//        // Từ tích cực
//        positiveWords.put("vui", 1.0);
//        positiveWords.put("hạnh phúc", 1.2);
//        positiveWords.put("tốt", 0.9);
//        positiveWords.put("tuyệt vời", 1.5);
//        positiveWords.put("xuất sắc", 1.3);
//        positiveWords.put("thích", 0.8);
//        positiveWords.put("yêu", 1.4);
//        positiveWords.put("đẹp", 0.9);
//        positiveWords.put("thú vị", 1.1);
//        positiveWords.put("hào hứng", 1.2);
//        positiveWords.put("phấn khích", 1.3);
//        positiveWords.put("thành công", 1.1);
//        positiveWords.put("chiến thắng", 1.2);
//        positiveWords.put("may mắn", 1.0);
//        positiveWords.put("tuyệt hảo", 1.4);
//        positiveWords.put("hoàn hảo", 1.3);
//        positiveWords.put("hài lòng", 1.0);
//        positiveWords.put("biết ơn", 1.1);
//        positiveWords.put("tận hưởng", 1.0);
//        positiveWords.put("tốt đẹp", 1.2);
//        positiveWords.put("ưng ý", 1.0);
//        positiveWords.put("nhiệt tình", 1.1);
//        positiveWords.put("lạc quan", 1.1);
//        positiveWords.put("tích cực", 1.2);
//        positiveWords.put("hy vọng", 1.3);
//        positiveWords.put("ấm áp", 1.0);
//        positiveWords.put("bình yên", 1.1);
//        positiveWords.put("thành đạt", 1.2);
//        positiveWords.put("viên mãn", 1.3);
//
//        // Từ tiêu cực
//        negativeWords.put("buồn", 1.0);
//        negativeWords.put("chán", 0.9);
//        negativeWords.put("tệ", 1.1);
//        negativeWords.put("xấu", 1.0);
//        negativeWords.put("ghét", 1.3);
//        negativeWords.put("khó chịu", 1.1);
//        negativeWords.put("thất vọng", 1.2);
//        negativeWords.put("tức giận", 1.3);
//        negativeWords.put("giận dữ", 1.3);
//        negativeWords.put("khổ sở", 1.2);
//        negativeWords.put("đau khổ", 1.4);
//        negativeWords.put("thất bại", 1.2);
//        negativeWords.put("thảm hại", 1.4);
//        negativeWords.put("kinh khủng", 1.3);
//        negativeWords.put("tồi tệ", 1.3);
//        negativeWords.put("chán nản", 1.2);
//        negativeWords.put("mệt mỏi", 1.0);
//        negativeWords.put("ức chế", 1.2);
//        negativeWords.put("bực bội", 1.1);
//        negativeWords.put("phiền muộn", 1.2);
//        negativeWords.put("đau lòng", 1.3);
//        negativeWords.put("thảm thương", 1.4);
//        negativeWords.put("muộn phiền", 1.5);
//        negativeWords.put("lo âu", 1.3);
//        negativeWords.put("căng thẳng", 1.3);
//        negativeWords.put("áp lực", 1.2);
//        negativeWords.put("bế tắc", 1.4);
//        negativeWords.put("tuyệt vọng", 1.5);
//        negativeWords.put("khủng hoảng", 1.6);
//        negativeWords.put("hoang mang", 1.2);
//        negativeWords.put("sợ hãi", 1.4);
//        negativeWords.put("hoảng loạn", 1.5);
//        negativeWords.put("bất an", 1.3);
//        negativeWords.put("đơn độc", 1.4);
//        negativeWords.put("lạc lõng", 1.3);
//        negativeWords.put("mất mát", 1.5);
//        negativeWords.put("tan vỡ", 1.4);
//        negativeWords.put("chia ly", 1.4);
//        negativeWords.put("thất tình", 1.4);
//        negativeWords.put("phản bội", 1.6);
//        negativeWords.put("chán chường", 1.4);
//        negativeWords.put("ngột ngạt", 1.3);
//        negativeWords.put("bi quan", 1.3);
//        negativeWords.put("tiêu cực", 1.4);
//        negativeWords.put("nguy hiểm", 1.5);
//        negativeWords.put("đe dọa", 1.6);
//        negativeWords.put("hiểm nguy", 1.5);
//        negativeWords.put("rủi ro", 1.3);
//        negativeWords.put("bất trắc", 1.4);
//        negativeWords.put("khốn khó", 1.5);
//        negativeWords.put("khó khăn", 1.3);
//        negativeWords.put("trắc trở", 1.4);
//        negativeWords.put("sóng gió", 1.4);
//        negativeWords.put("bão tố", 1.5);
//        negativeWords.put("kiệt sức", 1.4);
//        negativeWords.put("hụt hẫng", 1.3);
//        negativeWords.put("bất lực", 1.4);
//        negativeWords.put("thua cuộc", 1.4);
//        negativeWords.put("tủi thân", 1.3);
//        negativeWords.put("xót xa", 1.4);
//        negativeWords.put("nuối tiếc", 1.3);
//        negativeWords.put("hối hận", 1.4);
//    }
//
//    private void initializePhraseMaps() {
//        // Cụm từ tiêu cực phức tạp
//        negativePhrases.put("muộn phiền", 1.5);
//        negativePhrases.put("đang tiến gần", 1.4);
//        negativePhrases.put("áp lực cuộc sống", 1.4);
//        negativePhrases.put("khủng hoảng tinh thần", 1.7);
//        negativePhrases.put("cảm thấy cô đơn", 1.4);
//        negativePhrases.put("không còn hy vọng", 1.6);
//        negativePhrases.put("mất phương hướng", 1.4);
//        negativePhrases.put("bế tắc trong lòng", 1.5);
//        negativePhrases.put("nỗi buồn ám ảnh", 1.5);
//        negativePhrases.put("cảm giác bất an", 1.4);
//        negativePhrases.put("sợ hãi về tương lai", 1.5);
//        negativePhrases.put("áp lực công việc", 1.4);
//        negativePhrases.put("căng thẳng trong relationships", 1.4);
//        negativePhrases.put("mệt mỏi triền miên", 1.4);
//        negativePhrases.put("kiệt sức tinh thần", 1.5);
//        negativePhrases.put("cảm giác trống rỗng", 1.4);
//        negativePhrases.put("buồn chán kéo dài", 1.4);
//        negativePhrases.put("thất vọng về bản thân", 1.5);
//        negativePhrases.put("cô độc giữa đám đông", 1.5);
//        negativePhrases.put("nỗi đau khó nguôi", 1.5);
//        negativePhrases.put("tương lai mờ mịt", 1.5);
//        negativePhrases.put("cảm giác bị bỏ rơi", 1.5);
//        negativePhrases.put("khó khăn chồng chất", 1.5);
//        negativePhrases.put("sóng gió cuộc đời", 1.4);
//        negativePhrases.put("bão tố lòng người", 1.5);
//        negativePhrases.put("đau khổ tột cùng", 1.6);
//        negativePhrases.put("tuyệt vọng không lối thoát", 1.7);
//        negativePhrases.put("bế tắc không lối ra", 1.6);
//        negativePhrases.put("mất niềm tin", 1.5);
//        negativePhrases.put("tan nát trái tim", 1.6);
//    }
//
//    public String analyzeSentiment(String text) {
//        try {
//            // Ưu tiên sử dụng Gemini API nếu có key
//            if (isGeminiAvailable()) {
//                try {
//                    String geminiResult = analyzeWithGemini(text).block();
//                    if (geminiResult != null && !geminiResult.contains("Lỗi") &&
//                            !geminiResult.contains("Error")) {
//                        return geminiResult;
//                    }
//                } catch (Exception e) {
//                    System.out.println("Gemini API error, using keyword analysis: " + e.getMessage());
//                }
//            }
//
//            // Fallback 1: Sử dụng phương pháp từ khóa mới
//            String keywordResult = analyzeWithKeyword(text);
//            if (!"Trung tính".equals(keywordResult)) {
//                return keywordResult;
//            }
//
//            // Fallback 2: Về phương pháp cũ nếu phương pháp từ khóa trả về Trung tính
//            return analyzeWithEnhancedNaiveBayes(text);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "Trung tính";
//        }
//    }
//
//    private Mono<String> analyzeWithGemini(String text) {
//        try {
//            // Prompt cải tiến cho tiếng Việt
//            String prompt = "Bạn là chuyên gia phân tích cảm xúc tiếng Việt. " +
//                    "Hãy phân tích cảm xúc của văn bản sau và trả về chính xác một trong ba kết quả: Tích cực, Tiêu cực, Trung tính. " +
//                    "Phân tích kỹ ngữ cảnh, ẩn dụ, thành ngữ và ý nghĩa sâu xa trong văn bản tiếng Việt. " +
//                    "Chỉ trả về duy nhất một từ và không thêm bất kỳ giải thích nào. " +
//                    "Văn bản: \"" + text + "\"";
//
//            String requestBody = String.format(
//                    "{\"contents\":[{\"parts\":[{\"text\":\"%s\"}]}]}",
//                    prompt.replace("\"", "\\\"")
//            );
//
//            return webClient.post()
//                    .uri(geminiApiUrl + "?key=" + geminiApiKey)
//                    .header("Content-Type", "application/json")
//                    .bodyValue(requestBody)
//                    .retrieve()
//                    .bodyToMono(String.class)
//                    .map(response -> {
//                        try {
//                            JsonNode rootNode = objectMapper.readTree(response);
//
//                            // Kiểm tra lỗi từ API
//                            if (rootNode.has("error")) {
//                                return "Lỗi: " + rootNode.path("error").path("message").asText();
//                            }
//
//                            JsonNode candidates = rootNode.path("candidates");
//                            if (candidates.isArray() && candidates.size() > 0) {
//                                JsonNode content = candidates.get(0).path("content");
//                                JsonNode parts = content.path("parts");
//                                if (parts.isArray() && parts.size() > 0) {
//                                    String result = parts.get(0).path("text").asText().trim();
//
//                                    // Chuẩn hóa kết quả
//                                    result = normalizeSentimentResult(result);
//                                    return result;
//                                }
//                            }
//                            return "Lỗi: Không thể phân tích phản hồi từ AI";
//                        } catch (Exception e) {
//                            return "Lỗi: " + e.getMessage();
//                        }
//                    })
//                    .onErrorResume(e -> {
//                        System.out.println("Lỗi khi gọi Gemini API: " + e.getMessage());
//                        return Mono.just("Lỗi: " + e.getMessage());
//                    });
//
//        } catch (Exception e) {
//            return Mono.just("Lỗi: " + e.getMessage());
//        }
//    }
//
//    private String normalizeSentimentResult(String result) {
//        // Chuẩn hóa kết quả từ Gemini
//        String lowercaseResult = result.toLowerCase();
//
//        if (lowercaseResult.contains("tích cực") || lowercaseResult.contains("positive") ||
//                lowercaseResult.contains("pos") || lowercaseResult.contains("tốt") ||
//                lowercaseResult.contains("vui") || lowercaseResult.contains("hạnh phúc")) {
//            return "Tích cực";
//        }
//
//        if (lowercaseResult.contains("tiêu cực") || lowercaseResult.contains("negative") ||
//                lowercaseResult.contains("neg") || lowercaseResult.contains("xấu") ||
//                lowercaseResult.contains("buồn") || lowercaseResult.contains("tệ") ||
//                lowercaseResult.contains("chán") || lowercaseResult.contains("ghét")) {
//            return "Tiêu cực";
//        }
//
//        if (lowercaseResult.contains("trung tính") || lowercaseResult.contains("neutral") ||
//                lowercaseResult.contains("neut") || lowercaseResult.contains("bình thường")) {
//            return "Trung tính";
//        }
//
//        // Mặc định nếu không nhận diện được
//        return "Trung tính";
//    }
//
//    private String analyzeWithEnhancedNaiveBayes(String text) {
//        String lowercaseText = text.toLowerCase();
//        double positiveScore = 0;
//        double negativeScore = 0;
//
//        String[] words = lowercaseText.split("\\s+");
//        int wordCount = words.length;
//
//        if (wordCount == 0) {
//            return "Trung tính";
//        }
//
//        // Phân tích từng từ
//        for (String word : words) {
//            String cleanedWord = wordPattern.matcher(word).replaceAll("");
//            if (cleanedWord.length() < 2) continue;
//
//            if (positiveWords.containsKey(cleanedWord)) {
//                positiveScore += positiveWords.get(cleanedWord);
//            }
//
//            if (negativeWords.containsKey(cleanedWord)) {
//                negativeScore += negativeWords.get(cleanedWord);
//            }
//        }
//
//        // Phân tích cụm từ
//        negativeScore += analyzeNegativePhrases(lowercaseText);
//        positiveScore += analyzePositivePhrases(lowercaseText);
//
//        // Phân tích ngữ cảnh
//        negativeScore += analyzeContext(lowercaseText);
//
//        double avgPositive = positiveScore / wordCount;
//        double avgNegative = negativeScore / wordCount;
//
//        // Ngưỡng nhạy cảm hơn
//        if (avgPositive > avgNegative && avgPositive > 0.02) {
//            return "Tích cực";
//        } else if (avgNegative > avgPositive && avgNegative > 0.02) {
//            return "Tiêu cực";
//        } else {
//            return "Trung tính";
//        }
//    }
//
//    private double analyzeNegativePhrases(String text) {
//        double score = 0;
//        for (Map.Entry<String, Double> entry : negativePhrases.entrySet()) {
//            if (text.contains(entry.getKey())) {
//                score += entry.getValue();
//                System.out.println("Found negative phrase: " + entry.getKey() + " +" + entry.getValue());
//            }
//        }
//        return score;
//    }
//
//    private double analyzePositivePhrases(String text) {
//        double score = 0;
//        // Các cụm từ tích cực
//        Map<String, Double> positivePhrases = Map.of(
//                "rất vui", 0.4,
//                "cực kỳ hạnh phúc", 0.6,
//                "vô cùng tuyệt vời", 0.5,
//                "rất thích", 0.3,
//                "yêu thương", 0.4,
//                "thành công lớn", 0.5,
//                "may mắn", 0.3,
//                "hoàn hảo", 0.4
//        );
//
//        for (Map.Entry<String, Double> entry : positivePhrases.entrySet()) {
//            if (text.contains(entry.getKey())) {
//                score += entry.getValue();
//            }
//        }
//        return score;
//    }
//
//    private double analyzeContext(String text) {
//        double score = 0;
//
//        // Phát hiện các mẫu ngữ cảnh tiêu cực
//        if (text.contains("tiến gần") || text.contains("đang đến") || text.contains("ập đến")) {
//            score += 1.3;
//        }
//
//        if (text.contains("mọi") && (text.contains("phiền") || text.contains("muộn"))) {
//            score += 1.2;
//        }
//
//        if ((text.contains("đang") || text.contains("đã") || text.contains("sẽ")) &&
//                (text.contains("gần") || text.contains("tới") || text.contains("đến"))) {
//            score += 1.1;
//        }
//
//        // Các từ chỉ mức độ
//        if (text.contains("rất") || text.contains("quá") || text.contains("cực kỳ") ||
//                text.contains("vô cùng") || text.contains("hơi") || text.contains("khá")) {
//            score += 0.3;
//        }
//
//        return score;
//    }
//
//
//    public double calculateConfidence(String text, String sentiment) {
//        try {
//            // Nếu sử dụng Gemini, trả về độ tin cậy cao
//            if (isGeminiAvailable() && !sentiment.contains("Lỗi") && !sentiment.contains("Error")) {
//                return 0.92;
//            }
//
//            // Nếu sử dụng phương pháp từ khóa, trả về độ tin cậy trung bình
//            String keywordCheck = analyzeWithKeyword(text);
//            if (sentiment.equals(keywordCheck) && !"Trung tính".equals(sentiment)) {
//                return 0.85;
//            }
//
//            // Fallback về phương pháp cũ
//            return calculateNaiveBayesConfidence(text, sentiment);
//
//        } catch (Exception e) {
//            return 0.75;
//        }
//    }
//
//    private double calculateNaiveBayesConfidence(String text, String sentiment) {
//        String lowercaseText = text.toLowerCase();
//        String[] words = lowercaseText.split("\\s+");
//        int relevantWordCount = 0;
//        int totalWords = Math.max(words.length, 1);
//
//        for (String word : words) {
//            String cleanedWord = wordPattern.matcher(word).replaceAll("");
//            if (cleanedWord.length() < 2) continue;
//
//            if (sentiment.equals("Tích cực") && positiveWords.containsKey(cleanedWord)) {
//                relevantWordCount++;
//            } else if (sentiment.equals("Tiêu cực") && negativeWords.containsKey(cleanedWord)) {
//                relevantWordCount++;
//            }
//        }
//
//        // Kiểm tra cụm từ
//        if (sentiment.equals("Tiêu cực")) {
//            for (String phrase : negativePhrases.keySet()) {
//                if (lowercaseText.contains(phrase)) {
//                    relevantWordCount += 2; // Cụm từ có trọng số cao hơn
//                }
//            }
//        }
//
//        double baseConfidence = 0.3 + (0.7 * relevantWordCount / totalWords);
//
//        // Tăng độ tin cậy nếu văn bản dài
//        if (totalWords > 8) {
//            baseConfidence = Math.min(baseConfidence + 0.15, 0.95);
//        }
//
//        return Math.round(baseConfidence * 100.0) / 100.0; // Làm tròn 2 chữ số
//    }
//
//    public boolean isGeminiAvailable() {
//        return geminiApiKey != null && !geminiApiKey.trim().isEmpty();
//    }
//
//    public String generateAdvice(String sentiment, String text) {
//        try {
//            // Ưu tiên sử dụng Gemini API nếu có
//            if (isGeminiAvailable()) {
//                String geminiAdvice = generateAdviceWithGemini(sentiment, text).block();
//                if (geminiAdvice != null && !geminiAdvice.contains("Lỗi")) {
//                    return geminiAdvice;
//                }
//            }
//
//            // Fallback về lời khuyên mẫu
//            return generateSampleAdvice(sentiment, text);
//
//        } catch (Exception e) {
//            return generateSampleAdvice(sentiment, text);
//        }
//    }
//
//    private Mono<String> generateAdviceWithGemini(String sentiment, String text) {
//        try {
//            String prompt = String.format(
//                    "Bạn là chuyên gia tâm lý và huấn luyện viên cuộc sống. " +
//                            "Dựa trên cảm xúc %s từ văn bản: \"%s\" " +
//                            "Hãy đưa ra 3 lời khuyên ngắn gọn, thiết thực và mang tính tích cực. " +
//                            "Mỗi lời khuyên nên bắt đầu bằng biểu tượng cảm xúc. " +
//                            "Trả về dạng HTML với thẻ <ul> và <li>. " +
//                            "Ví dụ: <ul><li>😊 Lời khuyên 1</li><li>🌞 Lời khuyên 2</li></ul>",
//                    sentiment, text
//            );
//
//            String requestBody = String.format(
//                    "{\"contents\":[{\"parts\":[{\"text\":\"%s\"}]}]}",
//                    prompt.replace("\"", "\\\"")
//            );
//
//            return webClient.post()
//                    .uri(geminiApiUrl + "?key=" + geminiApiKey)
//                    .header("Content-Type", "application/json")
//                    .bodyValue(requestBody)
//                    .retrieve()
//                    .bodyToMono(String.class)
//                    .map(response -> {
//                        try {
//                            JsonNode rootNode = objectMapper.readTree(response);
//                            JsonNode candidates = rootNode.path("candidates");
//                            if (candidates.isArray() && candidates.size() > 0) {
//                                JsonNode content = candidates.get(0).path("content");
//                                JsonNode parts = content.path("parts");
//                                if (parts.isArray() && parts.size() > 0) {
//                                    return parts.get(0).path("text").asText().trim();
//                                }
//                            }
//                            return generateSampleAdvice(sentiment, text);
//                        } catch (Exception e) {
//                            return generateSampleAdvice(sentiment, text);
//                        }
//                    })
//                    .onErrorReturn(generateSampleAdvice(sentiment, text));
//
//        } catch (Exception e) {
//            return Mono.just(generateSampleAdvice(sentiment, text));
//        }
//    }
//
//    private String generateSampleAdvice(String sentiment, String text) {
//        Random random = new Random();
//        List<String> adviceList = new ArrayList<>();
//
//        switch (sentiment) {
//            case "Tiêu cực":
//                adviceList.addAll(Arrays.asList(
//                        "😊 Hãy thử hít thở sâu 5 lần và mỉm cười, bạn sẽ thấy nhẹ nhõm hơn",
//                        "🌞 Ra ngoài đi dạo 15 phút, không khí trong lành sẽ giúp tâm trạng tốt hơn",
//                        "🎵 Nghe một bản nhạc yêu thích, âm nhạc có thể chữa lành tâm hồn",
//                        "📝 Viết ra những điều bạn biết ơn trong cuộc sống",
//                        "☎️ Gọi cho một người bạn thân và trò chuyện",
//                        "🧘‍♂️ Tập vài động tác yoga đơn giản hoặc thiền 10 phút",
//                        "🍵 Uống một tách trà ấm và cho bản thân thời gian thư giãn",
//                        "📖 Đọc một cuốn sách truyền cảm hứng",
//                        "🎨 Thử một hoạt động sáng tạo như vẽ, viết hoặc nấu ăn"
//                ));
//                break;
//
//            case "Tích cực":
//                adviceList.addAll(Arrays.asList(
//                        "🌟 Tiếp tục duy trì năng lượng tích cực này!",
//                        "🤝 Chia sẻ cảm xúc tích cực với những người xung quanh",
//                        "🎯 Đặt thêm mục tiêu mới để phát huy tinh thần lạc quan",
//                        "💝 Làm điều tốt cho người khác để lan tỏa yêu thương",
//                        "📷 Ghi lại khoảnh khắc này để nhớ về sau",
//                        "🌻 Thử thách bản thân với một điều mới mẻ",
//                        "🎉 Tự thưởng cho bản thân vì đã giữ được tinh thần tích cực",
//                        "👥 Kết nối với những người có năng lượng tích cực",
//                        "🌈 Lập kế hoạch cho những dự án sắp tới"
//                ));
//                break;
//
//            default: // Trung tính
//                adviceList.addAll(Arrays.asList(
//                        "🔍 Hãy thử khám phá thêm về cảm xúc của bản thân",
//                        "📓 Viết nhật ký để hiểu rõ hơn về tâm trạng hiện tại",
//                        "🌳 Dành thời gian trong thiên nhiên để cân bằng cảm xúc",
//                        "🎼 Thử nghe các thể loại nhạc khác nhau",
//                        "🍃 Tập trung vào hiện tại và tận hưởng khoảnh khắc",
//                        "🤔 Đặt câu hỏi: 'Mình thực sự cần gì lúc này?'",
//                        "👣 Đi bộ và quan sát xung quanh một cách có ý thức",
//                        "💧 Uống đủ nước và chú ý đến sức khỏe thể chất",
//                        "🌅 Thức dậy sớm và ngắm bình minh"
//                ));
//        }
//
//        // Chọn ngẫu nhiên 3 lời khuyên
//        Collections.shuffle(adviceList);
//        StringBuilder adviceHtml = new StringBuilder("<ul class='advice-list'>");
//        for (int i = 0; i < Math.min(3, adviceList.size()); i++) {
//            adviceHtml.append("<li>").append(adviceList.get(i)).append("</li>");
//        }
//        adviceHtml.append("</ul>");
//
//        return adviceHtml.toString();
//    }
//
//    /**
//     * Phương thức phân tích cảm xúc dựa trên từ khóa theo quy tắc mới
//     * Sử dụng các Map positiveWords và negativeWords đã có sẵn
//     * Quy tắc:
//     * - Tích cực: Có từ tích cực và KHÔNG có từ tiêu cực
//     * - Tiêu cực: Có từ tiêu cực và KHÔNG có từ tích cực
//     * - Trung tính: Các trường hợp còn lại (không có từ nào, hoặc có cả hai)
//     */
//    public String analyzeWithKeyword(String text) {
//        if (text == null || text.trim().isEmpty()) {
//            return "Trung tính";
//        }
//
//        String lowerCaseText = text.toLowerCase().trim();
//        boolean foundPositive = false;
//        boolean foundNegative = false;
//
//        // Kiểm tra từ tích cực - sử dụng keySet() của positiveWords đã có
//        for (String word : positiveWords.keySet()) {
//            // Sử dụng regex để tìm từ đứng độc lập
//            if (Pattern.compile("\\b" + word + "\\b").matcher(lowerCaseText).find()) {
//                foundPositive = true;
//                break;
//            }
//        }
//
//        // Kiểm tra từ tiêu cực - sử dụng keySet() của negativeWords đã có
//        for (String word : negativeWords.keySet()) {
//            if (Pattern.compile("\\b" + word + "\\b").matcher(lowerCaseText).find()) {
//                foundNegative = true;
//                break;
//            }
//        }
//
//        // Áp dụng quy tắc phân loại
//        if (foundPositive && !foundNegative) {
//            return "Tích cực";
//        } else if (foundNegative && !foundPositive) {
//            return "Tiêu cực";
//        } else {
//            return "Trung tính";
//        }
//    }
//
//
//
//}

package com.example.DuDoanAI.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Value;
import reactor.core.publisher.Mono;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AIService {

    @Value("${gemini.api.key:}")
    private String geminiApiKey;

    @Value("${gemini.api.model:gemini-1.5-flash}")
    private String geminiApiModel;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public AIService(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    // Phương thức để lấy URL đầy đủ
    private String getGeminiApiUrl() {
        return "https://generativelanguage.googleapis.com/v1beta/models/" +
                geminiApiModel + ":generateContent";
    }

    public String analyzeSentiment(String text) {
        try {
            if (!isGeminiAvailable()) {
                return "Lỗi: Không có API key cho Gemini";
            }

            String geminiResult = analyzeWithGemini(text).block();
            if (geminiResult != null && !geminiResult.contains("Lỗi") &&
                    !geminiResult.contains("Error")) {
                return geminiResult;
            }

            return "Trung tính";

        } catch (Exception e) {
            e.printStackTrace();
            return "Trung tính";
        }
    }

    private Mono<String> analyzeWithGemini(String text) {
        try {
            String prompt = "Bạn là chuyên gia phân tích cảm xúc tiếng Việt. " +
                    "Hãy phân tích cảm xúc của văn bản sau và trả về chính xác một trong ba kết quả: Tích cực, Tiêu cực, Trung tính. " +
                    "Phân tích kỹ ngữ cảnh, ẩn dụ, thành ngữ và ý nghĩa sâu xa trong văn bản tiếng Việt. " +
                    "Chỉ trả về duy nhất một từ và không thêm bất kỳ giải thích nào. " +
                    "Văn bản: \"" + text + "\"";

            String requestBody = String.format(
                    "{\"contents\":[{\"parts\":[{\"text\":\"%s\"}]}]}",
                    prompt.replace("\"", "\\\"")
            );

            return webClient.post()
                    .uri(getGeminiApiUrl() + "?key=" + geminiApiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .map(response -> {
                        try {
                            JsonNode rootNode = objectMapper.readTree(response);

                            if (rootNode.has("error")) {
                                return "Lỗi: " + rootNode.path("error").path("message").asText();
                            }

                            JsonNode candidates = rootNode.path("candidates");
                            if (candidates.isArray() && candidates.size() > 0) {
                                JsonNode content = candidates.get(0).path("content");
                                JsonNode parts = content.path("parts");
                                if (parts.isArray() && parts.size() > 0) {
                                    String result = parts.get(0).path("text").asText().trim();
                                    return normalizeSentimentResult(result);
                                }
                            }
                            return "Lỗi: Không thể phân tích phản hồi từ AI";
                        } catch (Exception e) {
                            return "Lỗi: " + e.getMessage();
                        }
                    })
                    .onErrorResume(e -> {
                        System.out.println("Lỗi khi gọi Gemini API: " + e.getMessage());
                        return Mono.just("Lỗi: " + e.getMessage());
                    });

        } catch (Exception e) {
            return Mono.just("Lỗi: " + e.getMessage());
        }
    }

    private String normalizeSentimentResult(String result) {
        // Chuẩn hóa kết quả từ Gemini
        String lowercaseResult = result.toLowerCase();

        if (lowercaseResult.contains("tích cực") || lowercaseResult.contains("positive") ||
                lowercaseResult.contains("pos") || lowercaseResult.contains("tốt") ||
                lowercaseResult.contains("vui") || lowercaseResult.contains("hạnh phúc")) {
            return "Tích cực";
        }

        if (lowercaseResult.contains("tiêu cực") || lowercaseResult.contains("negative") ||
                lowercaseResult.contains("neg") || lowercaseResult.contains("xấu") ||
                lowercaseResult.contains("buồn") || lowercaseResult.contains("tệ") ||
                lowercaseResult.contains("chán") || lowercaseResult.contains("ghét")) {
            return "Tiêu cực";
        }

        if (lowercaseResult.contains("trung tính") || lowercaseResult.contains("neutral") ||
                lowercaseResult.contains("neut") || lowercaseResult.contains("bình thường")) {
            return "Trung tính";
        }

        // Mặc định nếu không nhận diện được
        return "Trung tính";
    }

    public double calculateConfidence(String text, String sentiment) {
        try {
            // Luôn trả về độ tin cậy cao khi sử dụng Gemini
            if (isGeminiAvailable() && !sentiment.contains("Lỗi") && !sentiment.contains("Error")) {
                return 0.92;
            }

            return 0.75; // Độ tin cậy thấp hơn nếu không có Gemini

        } catch (Exception e) {
            return 0.75;
        }
    }

    public boolean isGeminiAvailable() {
        return geminiApiKey != null && !geminiApiKey.trim().isEmpty();
    }

    public String generateAdvice(String sentiment, String text) {
        try {
            if (isGeminiAvailable()) {
                String geminiAdvice = generateAdviceWithGemini(sentiment, text).block();
                if (geminiAdvice != null && !geminiAdvice.contains("Lỗi")) {
                    return geminiAdvice;
                }
            }

            // Fallback về lời khuyên mẫu đơn giản
            return generateSimpleAdvice(sentiment);

        } catch (Exception e) {
            return generateSimpleAdvice(sentiment);
        }
    }

    private Mono<String> generateAdviceWithGemini(String sentiment, String text) {
        try {
            String prompt = String.format(
                    "Bạn là chuyên gia tâm lý và huấn luyện viên cuộc sống. " +
                            "Dựa trên cảm xúc %s từ văn bản: \"%s\" " +
                            "Hãy đưa ra 3 lời khuyên ngắn gọn, thiết thực và mang tính tích cực. " +
                            "Mỗi lời khuyên nên bắt đầu bằng biểu tượng cảm xúc. " +
                            "Trả về dạng HTML với thẻ <ul> và <li>. " +
                            "Ví dụ: <ul><li>😊 Lời khuyên 1</li><li>🌞 Lời khuyên 2</li></ul>",
                    sentiment, text
            );

            String requestBody = String.format(
                    "{\"contents\":[{\"parts\":[{\"text\":\"%s\"}]}]}",
                    prompt.replace("\"", "\\\"")
            );

            return webClient.post()
                    .uri(getGeminiApiUrl() + "?key=" + geminiApiKey) // Sửa ở đây
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .map(response -> {
                        try {
                            JsonNode rootNode = objectMapper.readTree(response);
                            JsonNode candidates = rootNode.path("candidates");
                            if (candidates.isArray() && candidates.size() > 0) {
                                JsonNode content = candidates.get(0).path("content");
                                JsonNode parts = content.path("parts");
                                if (parts.isArray() && parts.size() > 0) {
                                    return parts.get(0).path("text").asText().trim();
                                }
                            }
                            return generateSimpleAdvice(sentiment);
                        } catch (Exception e) {
                            return generateSimpleAdvice(sentiment);
                        }
                    })
                    .onErrorReturn(generateSimpleAdvice(sentiment));

        } catch (Exception e) {
            return Mono.just(generateSimpleAdvice(sentiment));
        }
    }

    private String generateSimpleAdvice(String sentiment) {
        switch (sentiment) {
            case "Tiêu cực":
                return "<ul class='advice-list'>" +
                        "<li>😊 Hãy thử hít thở sâu và thư giãn</li>" +
                        "<li>🌞 Ra ngoài đi dạo để cải thiện tâm trạng</li>" +
                        "<li>🎵 Nghe nhạc yêu thích để giải tỏa cảm xúc</li>" +
                        "</ul>";
            case "Tích cực":
                return "<ul class='advice-list'>" +
                        "<li>🌟 Tiếp tục duy trì năng lượng tích cực</li>" +
                        "<li>🤝 Chia sẻ cảm xúc với người thân</li>" +
                        "<li>🎯 Đặt mục tiêu mới để phát huy tinh thần</li>" +
                        "</ul>";
            default:
                return "<ul class='advice-list'>" +
                        "<li>🔍 Dành thời gian khám phá cảm xúc bản thân</li>" +
                        "<li>🌳 Hòa mình với thiên nhiên để cân bằng</li>" +
                        "<li>📓 Viết nhật ký để hiểu rõ tâm trạng</li>" +
                        "</ul>";
        }
    }
}