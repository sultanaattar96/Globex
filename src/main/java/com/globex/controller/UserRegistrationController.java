package com.globex.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.globex.dto.UserRegistrationDto;
import com.globex.serviceImpl.UserServiceImpl;


@Controller
@RequestMapping("api/v1/registration")
public class UserRegistrationController {

    @Autowired
    private UserServiceImpl userService;

    @GetMapping
    public String getRegistrationPage(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "registration";
    }

    @PostMapping
    public String registerUser(UserRegistrationDto registrationDto) {
        userService.save(registrationDto);
        return "redirect:/api/v1/registration?success";
    }
}