// ProductRepository.java
package com.globex.repository;

import com.globex.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
