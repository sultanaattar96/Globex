package com.globex.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.globex.model.GeneratedRecommendation;

public interface GeneratedRecommendationRepository extends JpaRepository<GeneratedRecommendation, Long> {
    void deleteByUserId(Long userId);
}
