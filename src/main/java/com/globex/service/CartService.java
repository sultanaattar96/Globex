package com.globex.service;

import com.globex.model.Cart;
import com.globex.model.Product;
import com.globex.model.Category;
import com.globex.model.Users;
import com.globex.repository.CartRepository;
import com.globex.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {

	@Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserPreferenceService userPreferenceService;

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Cart> getCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId);
    }

    public void addToCart(Cart cart) {
        // Save the item to the cart
        cartRepository.save(cart);

        // Update user preferences
        Users user = cart.getUser();
        Product product = cart.getProduct();
        // Fetch the category using the categoryId, handling the Optional
        Optional<Category> categoryOptional = categoryRepository.findById(product.getCategoryId());
        String categoryName = categoryOptional.map(Category::getName).orElse("Unknown Category");

        // Update user preferences with the category name
        userPreferenceService.updateUserPreferences(user.getId(), categoryName);
    }

    public void clearCart(Long userId) {
        List<Cart> cartItems = cartRepository.findByUserId(userId);
        cartRepository.deleteAll(cartItems);
    }
}
