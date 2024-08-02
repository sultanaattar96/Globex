package com.globex.service;

import com.globex.dto.ProductDTO;
import com.globex.model.Product;
import com.globex.model.Rating;
import com.globex.model.Recommendation;
import com.globex.repository.ProductRepository;
import com.globex.repository.RatingRepository;
import com.globex.repository.RecommendationRepository;
import com.globex.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Base64;

@Service
public class RecommendationService {
    private static final Logger logger = LoggerFactory.getLogger(RecommendationService.class);

    private final ProductRepository productRepository;
    private final RatingRepository ratingRepository;
    private final RecommendationRepository recommendationRepository;
    private final UserRepository userRepository;

    @Value("${recommendation.limit:5}")
    private int recommendationLimit;

    public RecommendationService(ProductRepository productRepository, RatingRepository ratingRepository, RecommendationRepository recommendationRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.ratingRepository = ratingRepository;
        this.recommendationRepository = recommendationRepository;
        this.userRepository = userRepository;
    }

    public List<ProductDTO> recommendProducts(Long userId) {
        logger.info("Starting recommendation process for user ID: {}", userId);

        List<Rating> userRatings = ratingRepository.findByUserId(userId);
        Map<Long, List<Rating>> productRatings = ratingRepository.findAll().stream()
                .collect(Collectors.groupingBy(rating -> rating.getProduct().getId()));

        // Calculate similarities between products
        Map<Long, Double> productSimilarity = new HashMap<>();
        for (Rating rating : userRatings) {
            Long productId = rating.getProduct().getId();
            for (Map.Entry<Long, List<Rating>> entry : productRatings.entrySet()) {
                Long otherProductId = entry.getKey();
                if (!productId.equals(otherProductId)) {
                    double similarity = calculateSimilarity(productRatings.get(productId), entry.getValue());
                    productSimilarity.put(otherProductId, productSimilarity.getOrDefault(otherProductId, 0.0) + similarity);
                }
            }
        }

        // Sort products by similarity score
        List<Product> recommendedProducts = productSimilarity.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(recommendationLimit)
                .map(entry -> productRepository.findById(entry.getKey()).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // Save recommendations to the database
        saveRecommendations(userId, recommendedProducts);

        logger.info("Recommendation process completed for user ID: {}", userId);
        return recommendedProducts.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public void resetRecommendations(Long userId) {
        logger.info("Resetting recommendations for user ID: {}", userId);
        List<Recommendation> existingRecommendations = recommendationRepository.findByUserId(userId);
        recommendationRepository.deleteAll(existingRecommendations);
        logger.info("Recommendations reset successfully for user ID: {}", userId);
    }

    private void saveRecommendations(Long userId, List<Product> recommendedProducts) {
        List<Recommendation> recommendations = recommendedProducts.stream()
                .map(product -> {
                    Recommendation recommendation = new Recommendation();
                    recommendation.setUser(userRepository.findById(userId).orElse(null));
                    recommendation.setProduct(product);
                    recommendation.setScore(calculateScore(userId, product.getId()));
                    return recommendation;
                })
                .collect(Collectors.toList());
        recommendationRepository.saveAll(recommendations);
    }

    private double calculateSimilarity(List<Rating> ratings1, List<Rating> ratings2) {
        Map<Long, Integer> ratingsMap1 = ratings1.stream()
                .collect(Collectors.toMap(rating -> rating.getUser().getId(), Rating::getRating));
        Map<Long, Integer> ratingsMap2 = ratings2.stream()
                .collect(Collectors.toMap(rating -> rating.getUser().getId(), Rating::getRating));

        List<Integer> commonRatings1 = new ArrayList<>();
        List<Integer> commonRatings2 = new ArrayList<>();

        for (Long userId : ratingsMap1.keySet()) {
            if (ratingsMap2.containsKey(userId)) {
                commonRatings1.add(ratingsMap1.get(userId));
                commonRatings2.add(ratingsMap2.get(userId));
            }
        }

        int n = commonRatings1.size();
        if (n == 0) return 0.0;

        double mean1 = commonRatings1.stream().mapToInt(Integer::intValue).average().orElse(0.0);
        double mean2 = commonRatings2.stream().mapToInt(Integer::intValue).average().orElse(0.0);

        double numerator = 0.0;
        double denominator1 = 0.0;
        double denominator2 = 0.0;

        for (int i = 0; i < n; i++) {
            double diff1 = commonRatings1.get(i) - mean1;
            double diff2 = commonRatings2.get(i) - mean2;
            numerator += diff1 * diff2;
            denominator1 += diff1 * diff1;
            denominator2 += diff2 * diff2;
        }

        double denominator = Math.sqrt(denominator1) * Math.sqrt(denominator2);
        return denominator == 0 ? 0.0 : numerator / denominator;
    }

    private float calculateScore(Long userId, Long productId) {
        // Placeholder logic for calculating recommendation score
        // You can implement a more sophisticated algorithm here
        return 1.0f;
    }

    private ProductDTO convertToDTO(Product product) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId());
        productDTO.setName(product.getName());
        productDTO.setDescription(product.getDescription());
        productDTO.setPrice(product.getPrice());
        productDTO.setStock(product.getStock());
        productDTO.setCategoryId(product.getCategoryId());
        productDTO.setCreatedAt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(product.getCreatedAt()));
        productDTO.setImage(Base64.getEncoder().encodeToString(product.getImage()));
        return productDTO;
    }
}
