package com.globex.controller;

import com.globex.dto.ProductDTO;
import com.globex.model.Cart;
import com.globex.model.Product;
import com.globex.model.Users;
import com.globex.repository.UserRepository;
import com.globex.service.CartService;
import com.globex.service.CohereService;
import com.globex.service.ProductService;
import com.globex.service.RecommendationService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Random;


@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RecommendationService recommendationService;
    
    @Autowired
    private CohereService cohereService;
    
    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    @GetMapping
    public String viewCart(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = ((UserDetails) authentication.getPrincipal()).getUsername();
        Users user = userRepository.findByEmail(userEmail);
        model.addAttribute("cartItems", cartService.getCartByUserId(user.getId()));
        
     // Check if recommendations are present in the model
        if (model.containsAttribute("recommendations")) {
            @SuppressWarnings("unchecked")
            List<ProductDTO> recommendations = (List<ProductDTO>) model.getAttribute("recommendations");

            // Generate a random number for each recommendation
            for (ProductDTO recommendation : recommendations) {
                recommendation.setId(getRandomNumber(1, 16));
            }
            model.addAttribute("recommendations", recommendations);
        }
       
        
        //model.addAttribute("randomImageId", getRandomNumber(1, 16));
        
        return "cart/cart";
    }
    
    // Utility method to get a random number between min and max (inclusive)
    public Long getRandomNumber(int min, int max) {
        Random random = new Random();
        return (long) (random.nextInt((max - min) + 1) + min);
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

    @PostMapping("/add/{productId}")
    public String addToCart(@PathVariable Long productId, @RequestParam int quantity, Model model, RedirectAttributes redirectAttributes) {
        // Get the current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = ((UserDetails) authentication.getPrincipal()).getUsername();
        Users user = userRepository.findByEmail(userEmail);

        // Create and save the cart item
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setProduct(productService.getProductById(productId).orElseThrow(() -> 
            new IllegalArgumentException("Product not found")));
        cart.setQuantity(quantity);
        cartService.addToCart(cart);
        
        // Fetch the category name of the product added to the cart
        String categoryName = null;
        try {
            categoryName = productService.getCategoryNameByProductId(productId);
            System.out.println("Sultanaaah This is categoryName: " + categoryName);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error fetching category name: " + e.getMessage());
        }

        // Generate recommendations based on the product category
        List<ProductDTO> recommendations;
        try {
            //String coherePrompt = "Recommend products based on the category: " + categoryName;
            String coherePrompt = "Recommend products based on the category: Shirts";
            int maxTokens = 100; // Adjust as needed
            double temperature = 0.7; // Adjust as needed
            String cohereResponse = cohereService.generateText(coherePrompt, maxTokens, temperature);
            recommendations = recommendationService.parseCohereResponse(cohereResponse); // Implement this method to parse the response into ProductDTOs
        } catch (Exception e) {
            System.err.println("Error generating recommendations: " + e.getMessage());
            e.printStackTrace();
            recommendations = Collections.emptyList();
        }
        
     // Increment the sales count for the product
        Product product = productService.getProductById(productId).orElseThrow(() -> 
            new IllegalArgumentException("Product not found"));
        product.setSalesCount(product.getSalesCount() + 1);
        productService.saveProduct(product);
        
        System.out.print("Sultanaaah This is recommendations: " + recommendations);
        System.out.print("Sultanaaah This is categoryName jvjbhj: " + categoryName);

        // Add recommendations to the model to display on the cart page
        model.addAttribute("recommendations", recommendations);

        // Check if the category name is properly fetched
        if (categoryName == null) {
            System.err.println("Category name could not be retrieved. Cannot generate recommendations.");
        }
        
     // Add recommendations to redirect attributes
        redirectAttributes.addFlashAttribute("recommendations", recommendations);
        
        return "redirect:/cart";

    }

}
