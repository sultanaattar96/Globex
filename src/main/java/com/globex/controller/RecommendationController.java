package com.globex.controller;

import com.globex.dto.ProductDTO;
import com.globex.service.CohereService;
import com.globex.service.RecommendationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;


import java.util.List;

@RestController
@RequestMapping("/recommendations")
public class RecommendationController {
	
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);
    private final CohereService cohereService;

    @Autowired
    private RecommendationService recommendationService;
    

    @Autowired
    public RecommendationController(CohereService cohereService, RecommendationService recommendationService) {
        this.cohereService = cohereService;
        this.recommendationService = recommendationService;
    }
    

    @GetMapping("/{userId}")
    public ResponseEntity<List<ProductDTO>> getPublicRecommendations(@PathVariable Long userId) {
    	logger.info("**********  getRecommendations *************");
        List<ProductDTO> recommendations = recommendationService.recommendProducts(userId);
        return ResponseEntity.ok(recommendations);
    }
    
   /* @GetMapping("/preference/view")
    public String getUserPreferencesRecommendationsView(@RequestParam String userPreferences, Model model) {
        try {
            List<ProductDTO> recommendations = recommendationService.getRecommendations(userPreferences);
            model.addAttribute("userPreferences", recommendations);
            return "userPreferences"; // Refers to the recommendations.html Thymeleaf template
        } catch (Exception e) {
            model.addAttribute("error", "Unable to fetch recommendations. Please try again later.");
            return "error"; // Or any error page you have
        }
    }*/
    
    @GetMapping("/preference/view")
    public String getUserPreferencesRecommendationsView(@RequestParam String userPreferences, Model model) {
        List<ProductDTO> recommendations = null;
		try {
			recommendations = recommendationService.getRecommendations(userPreferences);
			System.out.println("Final recommendations: " + recommendations);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        model.addAttribute("recommendations", recommendations);
        return "recommendations"; // This refers to recommendations.html in the templates folder
    }
    

    /*@PostMapping("/generate/{userId}")
    public ResponseEntity<String> generateRecommendations(@PathVariable Long userId) {
    	logger.info("**********  generateRecommendations *************");
        recommendationService.recommendProducts(userId);
        return ResponseEntity.ok("Recommendations generated successfully.");
    }*/
    
    @PostMapping("/generate/{userId}")
    public ResponseEntity<String> generateRecommendations(@PathVariable Long userId) {
        logger.info("**********  generateRecommendations *************");

        //this common recommendation which is based on rating of the products.
        recommendationService.recommendProducts(userId);
        
        // Fetch the user's preferences or past purchase history from your database
        String userPreferences = recommendationService.getUserPreferences(userId);

        // Generate recommendations using Cohere based on the user's preferences
        try {
        	int maxTokens = 100; // or any value you deem appropriate
            double temperature = 0.7; // adjust this as needed for variability
            String generatedRecommendations = cohereService.generateText("Based on the following user preferences, recommend some products: " + userPreferences, maxTokens, temperature);
            recommendationService.saveGeneratedRecommendations(userId, generatedRecommendations);
            return ResponseEntity.ok("Recommendations generated successfully.");
        } catch (Exception e) {
            logger.error("Error generating recommendations", e);
            return ResponseEntity.status(500).body("Failed to generate recommendations.");
        }
    }


    @PostMapping("/reset/{userId}")
    public ResponseEntity<String> resetRecommendations(@PathVariable Long userId) {
    	logger.info("**********  resetRecommendations *************");
        recommendationService.resetRecommendations(userId);
        return ResponseEntity.ok("Recommendations reset successfully.");
    }
    
    /*@GetMapping("/best-sellers")
    public String getBestSellers(Model model) {
    	List<Product> bestSellers = productRepository.findTop10ByOrderBySalesCountDesc();
        model.addAttribute("bestSellers", bestSellers);
        return "cart/cart"; // Ensure this corresponds to your cart.html Thymeleaf template
    }

    @GetMapping("/trending")
    public String getTrendingProducts(Model model) {
        List<ProductDTO> trendingProducts = recommendationService.getTrendingProducts();
        model.addAttribute("trendingProducts", trendingProducts);
        return "cart/cart"; // Ensure this corresponds to your cart.html Thymeleaf template
    }*/
}
