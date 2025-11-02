package com.example.stockeasy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.stockeasy.domain.User;

@Service
public class AuthService {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User authenticate(String username, String password) {
        return userService.authenticateUser(username, password);
    }

    public User register(String username, String email, String password, String firstName, String lastName) {
        // Encode password before registration
        String encodedPassword = passwordEncoder.encode(password);
        return userService.registerUser(username, email, encodedPassword, firstName, lastName);
    }
}
