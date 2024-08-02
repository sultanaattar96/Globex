package com.globex.service;

import com.globex.model.Users;
import com.globex.repository.UserRepository;
import com.globex.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Users user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new CustomUserDetails(
            user.getId(),  // Assuming User entity has getId() method
            user.getFirstName(),  // Assuming User entity has getFirstName() method
            user.getEmail(),
            user.getPassword(),
            user.getAuthorities() // Convert to Collection<GrantedAuthority>
        );
    }
}
