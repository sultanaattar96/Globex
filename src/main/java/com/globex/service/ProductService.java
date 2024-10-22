// ProductService.java
package com.globex.service;

import com.globex.model.Category;
import com.globex.model.Product;
import com.globex.repository.CategoryRepository;
import com.globex.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }
    
    public String getCategoryNameByProductId(Long productId) throws Exception {
        // Retrieve the product by its ID
        Product product = getProductById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        // Retrieve the category by its ID from the product
        Category category = categoryRepository.findById(product.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        // Return the category name
        return category.getName();
    }

    
    public void saveProduct(Product product) {
        productRepository.save(product);
    }
    
    public List<Product> getTop10BestSellers() {
        return productRepository.findTop10ByOrderBySalesCountDesc();
    }

    public List<Product> getTop10TrendingProducts() {
        return productRepository.findTop10ByOrderByRecentViewsDesc();
    }

}
