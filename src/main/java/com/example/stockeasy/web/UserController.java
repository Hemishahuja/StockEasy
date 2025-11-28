package com.example.stockeasy.web;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.stockeasy.domain.User;
import com.example.stockeasy.service.UserService;

/**
 * UserController for user profile management.
 * Handles user profile display, updates, and balance management.
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

    /**
     * REST API endpoint to set custom cash balance
     * Allows users to change their starting virtual balance
     */
    @PostMapping("/{userId}/balance/set")
    public ResponseEntity<Object> setCustomBalance(
            @PathVariable Long userId,
            @RequestBody java.util.Map<String, Object> request) {
        try {
            Object amountObj = request.get("amount");
            BigDecimal newBalance;
            
            if (amountObj instanceof Number) {
                newBalance = new BigDecimal(((Number) amountObj).toString());
            } else if (amountObj instanceof String) {
                newBalance = new BigDecimal((String) amountObj);
            } else {
                return ResponseEntity.badRequest().body(
                    createResponse(false, "Invalid amount format"));
            }
            
            BigDecimal updatedBalance = userService.setCustomCashBalance(userId, newBalance);
            
            return ResponseEntity.ok(createResponse(true, 
                "Balance updated successfully", 
                java.util.Map.of("newBalance", updatedBalance)));
                
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                createResponse(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                createResponse(false, "Failed to update balance: " + e.getMessage()));
        }
    }

    /**
     * REST API endpoint to add funds to balance
     * Useful for topping up virtual balance
     */
    @PostMapping("/{userId}/balance/add")
    public ResponseEntity<Object> addFunds(
            @PathVariable Long userId,
            @RequestBody java.util.Map<String, Object> request) {
        try {
            Object amountObj = request.get("amount");
            BigDecimal amount;
            
            if (amountObj instanceof Number) {
                amount = new BigDecimal(((Number) amountObj).toString());
            } else if (amountObj instanceof String) {
                amount = new BigDecimal((String) amountObj);
            } else {
                return ResponseEntity.badRequest().body(
                    createResponse(false, "Invalid amount format"));
            }
            
            BigDecimal newBalance = userService.addFundsToBalance(userId, amount);
            
            return ResponseEntity.ok(createResponse(true, 
                "Funds added successfully", 
                java.util.Map.of("newBalance", newBalance)));
                
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                createResponse(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                createResponse(false, "Failed to add funds: " + e.getMessage()));
        }
    }

    /**
     * REST API endpoint to reset balance to default
     */
    @PostMapping("/{userId}/balance/reset")
    public ResponseEntity<Object> resetBalance(@PathVariable Long userId) {
        try {
            BigDecimal resetBalance = userService.resetUserCashBalance(userId);
            
            return ResponseEntity.ok(createResponse(true, 
                "Balance reset successfully", 
                java.util.Map.of("newBalance", resetBalance)));
                
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                createResponse(false, "Failed to reset balance: " + e.getMessage()));
        }
    }

    /**
     * REST API endpoint to get current balance
     */
    @GetMapping("/{userId}/balance")
    public ResponseEntity<Object> getCurrentBalance(@PathVariable Long userId) {
        try {
            User user = userService.getUserProfile(userId);
            BigDecimal balance = user.getCashBalance();
            
            return ResponseEntity.ok(createResponse(true, "Success",
                java.util.Map.of("balance", balance)));
                
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                createResponse(false, "Failed to get balance: " + e.getMessage()));
        }
    }

    /**
     * Helper method to create standardized response
     */
    private java.util.Map<String, Object> createResponse(boolean success, String message) {
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("success", success);
        response.put("message", message);
        return response;
    }

    /**
     * Helper method to create standardized response with data
     */
    private java.util.Map<String, Object> createResponse(boolean success, String message, java.util.Map<String, Object> data) {
        java.util.Map<String, Object> response = createResponse(success, message);
        response.putAll(data);
        return response;
    }
}
