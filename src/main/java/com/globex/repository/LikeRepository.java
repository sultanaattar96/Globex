package com.globex.repository;

import com.globex.model.Like;
import com.globex.model.Product;
import com.globex.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByUserAndProduct(Users user, Product product);
    void deleteByUserAndProduct(Users user, Product product);
}
