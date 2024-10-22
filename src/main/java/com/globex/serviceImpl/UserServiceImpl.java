package com.globex.serviceImpl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.globex.dto.UserRegistrationDto;
import com.globex.model.Product;
import com.globex.model.Role;
import com.globex.model.Users;
import com.globex.repository.UserRepository;
import com.globex.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public Users findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public List<Users> findAll() {
        return userRepository.findAll();
    }

    public Users save(UserRegistrationDto registrationDto) {
        Users user = new Users(registrationDto.getFirstName(),
                             registrationDto.getLastName(),
                             registrationDto.getEmail(),
                             registrationDto.getMobileNumber(),
                             registrationDto.getGender(),
                             registrationDto.getAddress(),
                             registrationDto.getPostcode(),
                             passwordEncoder.encode(registrationDto.getPassword()),
                             Collections.singletonList(new Role("ROLE_USER")));

        return userRepository.save(user);
    }

    @Override
    public void delete(Users user) {
        userRepository.delete(user);
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Users user = userRepository.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("Invalid username or password");
        }
        
        System.out.println("User: " + username + ", Sultanaaaaaaaa Authorities: " + user.getRoles());

        return new User(user.getEmail(), user.getPassword(), mapRolesToAuthorities(user.getRoles()));

    }
    

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {

        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());

    }
    
    @Override
    public void saveProductInteraction(Users user, Product product) {
    	
    	System.out.print("****Sultana Attar Inside saveProductInteraction ****");
        // Logic to save the interaction in the database
        // This could involve creating a new entity to track interactions
    }
}