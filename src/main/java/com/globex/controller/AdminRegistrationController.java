package com.globex.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.globex.dto.UserRegistrationDto;
import com.globex.model.Users;
import com.globex.serviceImpl.UserServiceImpl;

@Controller
@RequestMapping("api/v1/admin")
public class AdminRegistrationController {

    @Autowired
    private UserServiceImpl userService;

    @GetMapping("/registration")
    public String getAdminRegistrationPage(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "adminRegistration";
    }

    @PostMapping("/registration")
    public String registerAdmin(UserRegistrationDto registrationDto, Model model) {
        // Check if the current user is an admin
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Users currentUser = (Users) authentication.getPrincipal();
        String role = currentUser.getRole();

        if (!"ADMIN".equals(role)) {
            model.addAttribute("error", "You do not have permission to create an admin.");
            return "adminRegistration";
        }

        // Register new admin
        registrationDto.setRole("ADMIN"); // Set role as ADMIN
        userService.save(registrationDto);

        return "redirect:/api/v1/admin/registration?success";
    }
    
    @GetMapping("/login")
    public String getAdminLoginPage(Model model) {
        return "admin_login"; // Admin login page template
    }

    @PostMapping("/login")
    public String adminLogin(String username, String password) {
        // Delegate authentication to Spring Security
        // You need to configure Spring Security for this
        return "redirect:/admin/dashboard"; // Redirect to admin dashboard or relevant page
    }
}
