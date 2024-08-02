package com.globex.repository;

import com.globex.model.Recommendation;
import com.globex.model.RecommendationId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecommendationRepository extends JpaRepository<Recommendation, RecommendationId> {
    List<Recommendation> findByUserId(Long userId);
}
