package com.example.stockeasy.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.stockeasy.domain.User;
import com.example.stockeasy.service.AuthService;

@Controller
public class AuthController {

    @Autowired
    private AuthService authService;

    // GET /login
    // Show the login page. If there's an error in the URL, display an error message.
    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid username or password");
        }
        return "auth/login";
    }// this returns the error which is invalid username or passwroed 


    // GET /register
    // Just return the registration page.
    @GetMapping("/register")
    public String register() {
        return "auth/register";
    }
    
    // POST /register
    // Handle the registration form submit. On success we show the login page with a success message.
    // On failure, we show the register page again with an error
    @PostMapping("/register")
    public String processRegistration(@RequestParam String username,
                                    @RequestParam String email,
                                    @RequestParam String password,
                                    @RequestParam String firstName,
                                    @RequestParam String lastName,
                                    Model model) {
        try {
            // Delegates to the service to create the new user
            User user = authService.register(username, email, password, firstName, lastName);
            
            // Simple success feedback; user still needs to log in
            model.addAttribute("message", "Registration successful! Please login.");
            return "auth/login";
        } catch (Exception e) {
            // Send a readable error back to the user
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            return "auth/register";
        }
    }
}
