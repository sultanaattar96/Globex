package com.globex.controller;

import com.globex.model.Rating;
import com.globex.model.Product;
import com.globex.model.Users;
import com.globex.repository.ProductRepository;
import com.globex.repository.RatingRepository;
import com.globex.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RatingController {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/rate")
    public @ResponseBody String rateProduct(@RequestBody RatingRequest ratingRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Users user = userRepository.findByEmail(email);

        Product product = productRepository.findById(ratingRequest.getProductId()).orElse(null);

        if (product != null && user != null) {
            Rating newRating = new Rating();
            newRating.setProduct(product);
            newRating.setUser(user);
            newRating.setRating(ratingRequest.getRating());
            ratingRepository.save(newRating);
        }

        return "success";
    }

    public static class RatingRequest {
        private Long productId;
        private int rating;

        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public int getRating() {
            return rating;
        }

        public void setRating(int rating) {
            this.rating = rating;
        }
    }
}
