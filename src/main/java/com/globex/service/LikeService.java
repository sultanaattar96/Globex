package com.globex.service;

import com.globex.model.Like;
import com.globex.model.Product;
import com.globex.model.Users;
import com.globex.repository.LikeRepository;
import com.globex.repository.ProductRepository;
import com.globex.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LikeService {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    public void likeProduct(Long userId, Long productId) {
        Users user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));
        Product product = productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("Invalid product ID"));

        if (!likeRepository.existsByUserAndProduct(user, product)) {
            Like like = new Like();
            like.setUser(user);
            like.setProduct(product);
            likeRepository.save(like);
        }
    }

    public void unlikeProduct(Long userId, Long productId) {
        Users user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));
        Product product = productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("Invalid product ID"));

        if (likeRepository.existsByUserAndProduct(user, product)) {
            likeRepository.deleteByUserAndProduct(user, product);
        }
    }
}
