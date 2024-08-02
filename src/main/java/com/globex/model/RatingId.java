package com.globex.model;

import java.io.Serializable;
import java.util.Objects;

public class RatingId implements Serializable {

    private int user;
    private int product;

    // Default constructor
    public RatingId() {}

    // Parameterized constructor
    public RatingId(int user, int product) {
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
        RatingId that = (RatingId) obj;
        return user == that.user && product == that.product;
    }

    // Getters and Setters
    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    public int getProduct() {
        return product;
    }

    public void setProduct(int product) {
        this.product = product;
    }
}
