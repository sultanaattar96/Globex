package com.globex.model;

import java.io.Serializable;
import java.util.Objects;

public class RecommendationId implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long user;
    private Long product;

    // Default constructor
    public RecommendationId() {}

    // Parameterized constructor
    public RecommendationId(Long user, Long product) {
        this.user = user;
        this.product = product;
    }

    // Getters, Setters, hashCode, and equals
    @Override
    public int hashCode() {
        return Objects.hash(user, product);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        RecommendationId that = (RecommendationId) obj;
        return user.equals(that.user) && product.equals(that.product);
    }

    // Getters and Setters
    public Long getUser() {
        return user;
    }

    public void setUser(Long user) {
        this.user = user;
    }

    public Long getProduct() {
        return product;
    }

    public void setProduct(Long product) {
        this.product = product;
    }
}
