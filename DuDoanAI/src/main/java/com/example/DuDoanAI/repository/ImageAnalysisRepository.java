package com.example.DuDoanAI.repository;

import com.example.DuDoanAI.model.ImageAnalysis;
import com.example.DuDoanAI.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageAnalysisRepository extends JpaRepository<ImageAnalysis, Long> {

    // Sửa thành analysisDate thay vì createdAt
    List<ImageAnalysis> findByOrderByAnalysisDateDesc();

    // Sửa thành analysisDate
    List<ImageAnalysis> findBySentimentLabelOrderByAnalysisDateDesc(String sentimentLabel);

    // Sửa thành analysisDate
    @Query("SELECT ia FROM ImageAnalysis ia WHERE ia.user.id = :userId ORDER BY ia.analysisDate DESC")
    List<ImageAnalysis> findByUserIdOrderByAnalysisDateDesc(Long userId);

    Long countBySentimentLabel(String sentimentLabel);

    @Query("SELECT COUNT(ia) FROM ImageAnalysis ia WHERE ia.user.id = :userId")
    Long countByUserId(Long userId);

    // Thêm các method mới để phù hợp với analysisDate
    List<ImageAnalysis> findByUserOrderByAnalysisDateDesc(User user);

    List<ImageAnalysis> findByUserAndSentimentLabelOrderByAnalysisDateDesc(User user, String sentimentLabel);

    Long countByUser(User user);
}