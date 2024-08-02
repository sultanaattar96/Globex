package com.globex.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.globex.model.Users;

public class SecurityUtils {

    public static boolean isCurrentUserAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        Users user = (Users) authentication.getPrincipal();
        return "ADMIN".equals(user.getRole());
    }
}
