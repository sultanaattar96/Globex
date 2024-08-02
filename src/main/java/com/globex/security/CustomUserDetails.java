package com.globex.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;

public class CustomUserDetails implements UserDetails, Serializable {

    private static final long serialVersionUID = 1L;  // Unique version identifier

    private Long userId;  // Added userId field
    private String firstName;
    private String email;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    // Constructor
    public CustomUserDetails(Long userId, String firstName, String email, String password,
                             Collection<? extends GrantedAuthority> authorities) {
        this.userId = userId;  // Initialize userId
        this.firstName = firstName;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    // Getters and setters
    public Long getUserId() {  // Added getter for userId
        return userId;
    }

    public void setUserId(Long userId) {  // Added setter for userId (optional)
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;  // Return email as username
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
