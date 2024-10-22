// ProductRepository.java
package com.globex.repository;

import com.globex.model.Product;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Long> {
	
	// Native query to get top 10 best sellers
    @Query(value = "SELECT * FROM products ORDER BY sales_count DESC LIMIT 10", nativeQuery = true)
    List<Product> findTop10ByOrderBySalesCountDesc();

    // Native query to get top 10 trending products based on recent views
    @Query(value = "SELECT * FROM products ORDER BY recent_views DESC LIMIT 10", nativeQuery = true)
    List<Product> findTop10ByOrderByRecentViewsDesc();

    List<Product> findByCategoryId(Long categoryId);
}


