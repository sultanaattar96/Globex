package com.globex.controller;

import com.globex.dto.ProductDTO;
import com.globex.service.RecommendationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recommendations")
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;
    
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @GetMapping("/{userId}")
    public ResponseEntity<List<ProductDTO>> getRecommendations(@PathVariable Long userId) {
    	logger.info("**********  getRecommendations *************");
        List<ProductDTO> recommendations = recommendationService.recommendProducts(userId);
        return ResponseEntity.ok(recommendations);
    }

    @PostMapping("/generate/{userId}")
    public ResponseEntity<String> generateRecommendations(@PathVariable Long userId) {
    	logger.info("**********  generateRecommendations *************");
        recommendationService.recommendProducts(userId);
        return ResponseEntity.ok("Recommendations generated successfully.");
    }

    @PostMapping("/reset/{userId}")
    public ResponseEntity<String> resetRecommendations(@PathVariable Long userId) {
    	logger.info("**********  resetRecommendations *************");
        recommendationService.resetRecommendations(userId);
        return ResponseEntity.ok("Recommendations reset successfully.");
    }
}
