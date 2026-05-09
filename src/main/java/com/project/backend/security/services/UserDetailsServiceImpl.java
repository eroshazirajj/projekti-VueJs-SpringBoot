package com.project.backend.security.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.project.backend.models.User;
import com.project.backend.repositories.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Sigurohu që UserRepository e ka metodën findByUsername
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        // KJO ËSHTË E RËNDËSISHME: Roli duhet të ketë "ROLE_" para nëse përdor hasRole në Backend
        String role = user.getRole().startsWith("ROLE_") ? user.getRole() : "ROLE_" + user.getRole();

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(role)
                .build();
    }
}
