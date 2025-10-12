package com.example.stockeasy.service;

import com.example.stockeasy.domain.User;
import com.example.stockeasy.repo.UserRepository;
import com.example.stockeasy.repo.PortfolioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * UserService for user management operations.
 * Handles user registration, authentication, and user profile management.
 */
@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PortfolioRepository portfolioRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public User registerUser(String username, String email, String password, String firstName, String lastName) {
        // Check if user already exists
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }
        
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }
        
        // Create new user
        User user = new User(username, email, password);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setCashBalance(new BigDecimal("100000.00")); // Starting cash balance
        
        return userRepository.save(user);
    }
    
    public User authenticateUser(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));
        
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }
        
        user.updateLastLogin();
        return userRepository.save(user);
    }
    
    public User getUserProfile(Long userId) {
        return userRepository.findByIdWithRelationships(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    public User updateUserProfile(Long userId, String firstName, String lastName, String email) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        
        return userRepository.save(user);
    }
    
    public BigDecimal getPortfolioValue(Long userId) {
        return BigDecimal.valueOf(portfolioRepository.calculatePortfolioValue(userId));
    }
}
