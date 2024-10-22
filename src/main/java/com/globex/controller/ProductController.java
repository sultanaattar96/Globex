package com.globex.controller;

import com.globex.dto.ProductDTO;
import com.globex.model.Product;
import com.globex.model.Rating;
import com.globex.model.Users;
import com.globex.repository.ProductRepository;
import com.globex.repository.RatingRepository;
import com.globex.repository.UserRepository;
import com.globex.service.LikeService;
import com.globex.service.ProductService;
import com.globex.service.RecommendationService;
import com.globex.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/products")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;
    
    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private LikeService likeService;
    
    @Autowired
    private RecommendationService recommendationService;

    @GetMapping("/index")
    public String showIndexPage(Model model) {
        List<Product> products = productService.getAllProducts();
        System.out.print("Sultana Text showIndexPage  ::::::::::::    " + products);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = null;
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            userEmail = ((UserDetails) authentication.getPrincipal()).getUsername();
        }
        
        // Fetch Best Sellers
        List<Product> bestSellers = productRepository.findTop10ByOrderBySalesCountDesc();
        model.addAttribute("bestSellers", bestSellers);

        // Fetch Trending Products
        List<Product> trendingProducts = productRepository.findTop10ByOrderByRecentViewsDesc();
        model.addAttribute("trendingProducts", trendingProducts);
        
        model.addAttribute("userEmail", userEmail);

        model.addAttribute("products", products);
        
        Users currentUser = getCurrentUser(); // Placeholder for actual user retrieval
        Long userId = currentUser.getId();
        // Fetch hybrid recommendations
        List<ProductDTO> recommendedProducts = recommendationService.recommendProductsHybrid(currentUser);

        // Add the recommended products to the model
        model.addAttribute("recommendedProducts", recommendedProducts);
        
        System.out.print("Sultana Text recommendedHybridProducts  ::::::::::::    " + recommendedProducts);
        
        return "index"; // Return the Thymeleaf template name
    }
    
    private Users getCurrentUser() {
        // Implement logic to retrieve the current user
        return new Users(); // Placeholder implementation
    }

    @GetMapping("/image/{id}")
    public ResponseEntity<byte[]> getProductImage(@PathVariable Long id) {
        // logger.info("Product not found with ID: {}", id);
        System.out.print("**** Inside getProductImage ****");
        Optional<Product> product = productService.getProductById(id);
        if (product.isPresent() && product.get().getImage() != null) {
            byte[] image = product.get().getImage();
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_TYPE, "image/png");
            return new ResponseEntity<>(image, headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}")
    public String showProduct(@PathVariable Long id, Model model) {
        Optional<Product> product = productService.getProductById(id);
        if (product.isEmpty()) {
            return "error/404"; // Ensure you have a 404.html page in templates/error
        }
        
        Product proCount = productService.getProductById(id).orElseThrow(() -> 
        new IllegalArgumentException("Product not found"));

        
        // Increment the recent views count
        proCount.setRecentViews(proCount.getRecentViews() + 1);
        productService.saveProduct(proCount);
        
        model.addAttribute("product", product.get());
        return "products/details"; // Ensure you have a details.html page in templates/product
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public Optional<Product> getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Void> likeProduct(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            String username = ((UserDetails) authentication.getPrincipal()).getUsername();
            logger.info("User authenticated: " + username);
            Users user = userRepository.findByEmail(username);
            if (user != null) {
                likeService.likeProduct(user.getId(), id);
                return ResponseEntity.ok().build();
            }
        }
        logger.warn("User not authenticated or authorized");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/{id}/unlike")
    public ResponseEntity<Void> unlikeProduct(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            String username = ((UserDetails) authentication.getPrincipal()).getUsername();
            Users user = userRepository.findByEmail(username);
            if (user != null) {
                likeService.unlikeProduct(user.getId(), id);
                return ResponseEntity.ok().build();
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    
    @PostMapping("/{id}/rate")
    public String rateProduct(@PathVariable Long id, @RequestParam int rating) {
    	logger.info("********** rateProduct ***********");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            String username = ((UserDetails) authentication.getPrincipal()).getUsername();
            logger.info("User authenticated: " + username);
            Users user = userRepository.findByEmail(username);
            if (user != null) {
                Rating newRating = new Rating();
                newRating.setUser(user);
                newRating.setProduct(productRepository.findById(id).orElse(null));
                newRating.setRating(rating);
                ratingRepository.save(newRating);
                return "redirect:/products/" + id; // Redirect back to the product details page
            }
        }
        logger.warn("User not authenticated or authorized");
        return "redirect:/login"; // Redirect to login if the user is not authenticated
    }
}
