package com.globex.repository;

import com.globex.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {
	List<Rating> findByUserId(Long userId);
    List<Rating> findByProductId(Long productId);
}
