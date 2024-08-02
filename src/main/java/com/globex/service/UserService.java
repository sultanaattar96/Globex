package com.globex.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.globex.dto.UserRegistrationDto;
import com.globex.model.Product;
import com.globex.model.Users;

public interface UserService extends UserDetailsService {

    Users findById(Long id);

    List<Users> findAll();

    Users save(UserRegistrationDto registrationDto);

    void delete(Users user);

    void deleteById(Long id);
    
    void saveProductInteraction(Users user, Product product);

}
