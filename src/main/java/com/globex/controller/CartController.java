package com.globex.controller;

import com.globex.model.Cart;
import com.globex.model.Users;
import com.globex.repository.UserRepository;
import com.globex.service.CartService;
import com.globex.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String viewCart(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = ((UserDetails) authentication.getPrincipal()).getUsername();
        Users user = userRepository.findByEmail(userEmail);
        model.addAttribute("cartItems", cartService.getCartByUserId(user.getId()));
        return "cart";
    }

    @PostMapping("/add/{productId}")
    public String addToCart(@PathVariable Long productId, @RequestParam int quantity) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = ((UserDetails) authentication.getPrincipal()).getUsername();
        Users user = userRepository.findByEmail(userEmail);

        Cart cart = new Cart();
        cart.setUser(user);
        cart.setProduct(productService.getProductById(productId).orElseThrow());
        cart.setQuantity(quantity);
        cartService.addToCart(cart);

        return "redirect:/cart";
    }
}
