//package com.example.DuDoanAI.repository;
//
//import com.example.DuDoanAI.model.SentimentAnalysis;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Repository
//public interface SentimentAnalysisRepository extends JpaRepository<SentimentAnalysis, Long> {
//
//    @Query("SELECT s FROM SentimentAnalysis s ORDER BY s.createdAt DESC")
//    List<SentimentAnalysis> findAllOrderByCreatedAtDesc();
//
//    List<SentimentAnalysis> findBySentiment(String sentiment);
//
//    List<SentimentAnalysis> findByTextInputContainingIgnoreCase(String keyword);
//
//    List<SentimentAnalysis> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime start, LocalDateTime end);
//
//    List<SentimentAnalysis> findByCreatedAtAfterOrderByCreatedAtDesc(LocalDateTime date);
//
//
//
//    @Query("SELECT COUNT(s) FROM SentimentAnalysis s WHERE s.sentiment = :sentiment")
//    long countBySentiment(@Param("sentiment") String sentiment);
//
//    @Query("SELECT s FROM SentimentAnalysis s WHERE s.confidence > :minConfidence ORDER BY s.confidence DESC")
//    List<SentimentAnalysis> findByHighConfidence(@Param("minConfidence") double minConfidence);
//}

package com.example.DuDoanAI.repository;

import com.example.DuDoanAI.model.SentimentAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SentimentAnalysisRepository extends JpaRepository<SentimentAnalysis, Long> {

    // Sửa lại phương thức findAllOrderByCreatedAtDesc
    @Query("SELECT sa FROM SentimentAnalysis sa ORDER BY sa.createdAt DESC")
    List<SentimentAnalysis> findAllOrderByCreatedAtDesc();

    List<SentimentAnalysis> findBySentiment(String sentiment);

    List<SentimentAnalysis> findByTextInputContainingIgnoreCase(String keyword);

    List<SentimentAnalysis> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime start, LocalDateTime end);

    List<SentimentAnalysis> findByCreatedAtAfterOrderByCreatedAtDesc(LocalDateTime date);

    // Sửa phương thức findTopNByOrderByCreatedAtDesc
    @Query("SELECT sa FROM SentimentAnalysis sa ORDER BY sa.createdAt DESC LIMIT :limit")
    List<SentimentAnalysis> findTopNByOrderByCreatedAtDesc(@Param("limit") int limit);

    // Thêm phương thức countBySentiment
    @Query("SELECT COUNT(sa) FROM SentimentAnalysis sa WHERE sa.sentiment = :sentiment")
    long countBySentiment(@Param("sentiment") String sentiment);

    // Thêm phương thức findBySentiment với sắp xếp
    @Query("SELECT sa FROM SentimentAnalysis sa WHERE sa.sentiment = :sentiment ORDER BY sa.createdAt DESC")
    List<SentimentAnalysis> findBySentimentOrderByCreatedAtDesc(@Param("sentiment") String sentiment);
}