package com.globex.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.globex.dto.ProductDTO;
import com.globex.model.GeneratedRecommendation;
import com.globex.model.Product;
import com.globex.model.Rating;
import com.globex.model.Recommendation;
import com.globex.model.UserPreferences;
import com.globex.model.Users;
import com.globex.repository.GeneratedRecommendationRepository;
import com.globex.repository.ProductRepository;
import com.globex.repository.RatingRepository;
import com.globex.repository.RecommendationRepository;
import com.globex.repository.UserPreferencesRepository;
import com.globex.repository.UserRepository;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class RecommendationService {
    private static final Logger logger = LoggerFactory.getLogger(RecommendationService.class);

    private final ProductRepository productRepository;
    private final RatingRepository ratingRepository;
    private final RecommendationRepository recommendationRepository;
    private final UserRepository userRepository;
    @Autowired
    private UserPreferencesRepository userPreferencesRepository;
    @Autowired
    private GeneratedRecommendationRepository generatedRecommendationRepository;
    private final CohereService cohereService;

    @Value("${recommendation.limit:5}")
    private int recommendationLimit;

    public RecommendationService(ProductRepository productRepository, RatingRepository ratingRepository, RecommendationRepository recommendationRepository, UserRepository userRepository, CohereService cohereService) {
        this.productRepository = productRepository;
        this.ratingRepository = ratingRepository;
        this.recommendationRepository = recommendationRepository;
        this.userRepository = userRepository;
        this.cohereService = cohereService;
    }
    
    // Modify the method to accept a Users object instead of a Long user ID
    public List<ProductDTO> recommendProductsHybrid(Users user) {
        Long userId = user.getId(); // Extract the user ID from the Users object

        // Get collaborative filtering recommendations
        List<ProductDTO> collaborativeRecommendations = recommendProducts(userId);

        // Get content-based filtering recommendations
        List<ProductDTO> contentBasedRecommendations = recommendProductsContentBased(userId);

        // Combine both lists, removing duplicates
        List<ProductDTO> hybridRecommendations = Stream.concat(collaborativeRecommendations.stream(), contentBasedRecommendations.stream())
            .distinct()
            .collect(Collectors.toList());

        // Limit the number of recommendations
        return hybridRecommendations.stream()
            .limit(recommendationLimit)
            .collect(Collectors.toList());
    }



    
    // Method to implement content-based filtering
    public List<ProductDTO> recommendProductsContentBased(Long userId) {
        // Fetch user preferences
        String userPreferences = getUserPreferences(userId);

        // Check if preferences are found and valid
        if (userPreferences == null) {
            // Handle the case where no preferences are found, maybe return an empty list or default recommendations
            logger.warn("No preferences found for user ID: {}", userId);
            return Collections.emptyList(); // Return an empty list if no preferences found
        }

        try {
            // Assuming the preferences contain a valid category ID
            Long categoryId = Long.parseLong(userPreferences);

            // Use the category ID to fetch related products
            List<Product> relatedProducts = productRepository.findByCategoryId(categoryId);

            // Convert these products to DTOs
            return relatedProducts.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        } catch (NumberFormatException e) {
            logger.error("Invalid preference format for user ID: {}", userId, e);
            // Handle the error, maybe return an empty list or default recommendations
            return Collections.emptyList();
        }
    }

    

    public List<ProductDTO> getRecommendations(String userPreferences) throws Exception {
        List<ProductDTO> recommendations = new ArrayList<>();
        
        try {
            String coherePrompt = "Recommend products based on the category: " + userPreferences;
            String cohereResponse = cohereService.generateText(coherePrompt, 100, 0.7);
            recommendations = parseCohereResponse(cohereResponse);
            System.out.println("Raw Cohere Response: " + cohereResponse);
            System.out.println("Parsed recommendations: " + recommendations);

            System.out.println("Final recommendations: " + recommendations);
        } catch (IOException e) {
            System.err.println("Error generating recommendations: " + e.getMessage());
        }

        return recommendations;
    }

    
    public List<ProductDTO> parseCohereResponse(String cohereResponse) {
        List<ProductDTO> recommendations = new ArrayList<>();

        // Extract the "text" content from the Cohere response
        String responseText = new JSONObject(cohereResponse)
                .getJSONArray("generations")
                .getJSONObject(0)
                .getString("text");

        // Print the response text for debugging
        System.out.println("Raw Text from Cohere: " + responseText);

        // Adjusted pattern to handle hyphen separators in the response
        Pattern pattern = Pattern.compile("\\d+\\.\\s*(.+?)\\s*-\\s*(.+?)(?:\\.|\\n|$)");
        Matcher matcher = pattern.matcher(responseText);

        while (matcher.find()) {
            String productName = matcher.group(1).trim();
            String productDescription = matcher.group(2).trim();

            System.out.println("Parsed Product Name: " + productName);
            System.out.println("Parsed Product Description: " + productDescription);

            // Create a ProductDTO object for each match
            ProductDTO product = new ProductDTO();
            product.setName(productName);
            product.setDescription(productDescription);
            product.setPrice(BigDecimal.ZERO); // Set a default price or update as needed

            recommendations.add(product);
        }

        System.out.println("Final recommendations: " + recommendations);
        return recommendations;
    }


    // Collaborative filtering method
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
        
     // Log the DTO for debugging
        System.out.println("Converted ProductDTO: " + productDTO);
        
        return productDTO;
    }
    
    /*public String getUserPreferences(Long userId) {
        Optional<UserPreferences> userPreferences = userPreferencesRepository.findByUserId(userId);
        return userPreferences.map(UserPreferences::getPreferences).orElse("No preferences found");
    }*/
    
    public String getUserPreferences(Long userId) {
        Optional<UserPreferences> userPreferences = userPreferencesRepository.findByUserId(userId);
        return userPreferences.map(UserPreferences::getPreferences).orElse(null);
    }


    public void saveGeneratedRecommendations(Long userId, String recommendations) {
        // First, clear any existing recommendations for this user
        generatedRecommendationRepository.deleteByUserId(userId);

        // Save new recommendations
        GeneratedRecommendation generatedRecommendation = new GeneratedRecommendation();
        generatedRecommendation.setUserId(userId);
        generatedRecommendation.setRecommendations(recommendations);
        generatedRecommendationRepository.save(generatedRecommendation);
    }
    
    /*public void resetRecommendations(Long userId) {
        // Delete recommendations for the user
        generatedRecommendationRepository.deleteByUserId(userId);
    }*/

    
}
