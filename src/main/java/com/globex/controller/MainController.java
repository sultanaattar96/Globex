package com.globex.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.globex.model.Product;
import com.globex.service.ProductService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/api/v1/")
public class MainController {

    private final ProductService productService;
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    public MainController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/login")
    public String getLoginPage(Model model) {
        return "login";
    }

    @GetMapping
    public String showIndexPage(Model model) {
        List<Product> products = productService.getAllProducts();
        logger.info("Loaded products: {}", products);
        model.addAttribute("products", products);
        return "index";
    }
}
