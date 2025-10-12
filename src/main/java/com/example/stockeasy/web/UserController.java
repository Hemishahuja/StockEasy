package com.example.stockeasy.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.stockeasy.domain.User;
import com.example.stockeasy.service.UserService;

/**
 * UserController for user profile management.
 * Handles user profile display and updates.
 */
@Controller
@RequestMapping("/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * Display user profile
     */
    @GetMapping("/{userId}")
    public String getUserProfile(@PathVariable Long userId, Model model) {
        User user = userService.getUserProfile(userId);
        model.addAttribute("user", user);
        return "user/profile";
    }
}
