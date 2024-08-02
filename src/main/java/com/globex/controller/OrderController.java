package com.globex.controller;

import com.globex.model.Cart;
import com.globex.model.Order;
import com.globex.model.Users;
import com.globex.repository.UserRepository;
import com.globex.service.CartService;
import com.globex.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartService cartService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/place")
    public String placeOrder() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = ((UserDetails) authentication.getPrincipal()).getUsername();
        Users user = userRepository.findByEmail(userEmail);

        List<Cart> cartItems = cartService.getCartByUserId(user.getId());
        for (Cart cartItem : cartItems) {
            Order order = new Order();
            order.setUser(user);
            order.setProduct(cartItem.getProduct());
            order.setDelivered(true); // Assume order is delivered immediately for simplicity
            orderService.placeOrder(order);
        }

        cartService.clearCart(user.getId());

        return "redirect:/orders";
    }

    @GetMapping
    public String viewOrders(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = ((UserDetails) authentication.getPrincipal()).getUsername();
        Users user = userRepository.findByEmail(userEmail);
        model.addAttribute("orders", orderService.getOrdersByUserId(user.getId()));
        return "orders";
    }
}
