package com.example.stockeasy.web;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.stockeasy.domain.User;
import com.example.stockeasy.dto.ResetPortfolioRequest;
import com.example.stockeasy.dto.ResetPortfolioResponse;
import com.example.stockeasy.service.PortfolioResetService;
import com.example.stockeasy.service.UserService;

/**
 * ResetController for portfolio reset operations.
 * Handles REST API endpoints for resetting user portfolios to default state.
 */
@Controller
@RequestMapping("/reset")
public class ResetController {
    
    private static final Logger logger = LoggerFactory.getLogger(ResetController.class);
    
    @Autowired
    private PortfolioResetService portfolioResetService;
    
    @Autowired
    private UserService userService;
    
    /**
     * REST API endpoint to reset user portfolio
     * @param request the reset request containing user confirmation
     * @return ResponseEntity with reset operation result
     */
    @PostMapping("/portfolio")
    public ResponseEntity<Map<String, Object>> resetPortfolio(
            @RequestBody ResetPortfolioRequest request) {
        
        try {
            // Get current authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = (User) userService.loadUserByUsername(username);
            Long userId = user.getId();
            
            // Log reset attempt with IP address (would be injected via filter/interceptor)
            logger.info("Portfolio reset requested for user {} (ID: {})", username, userId);
            
            // Validate request
            if (request == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Reset request cannot be null");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            if (!request.getConfirmReset()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Reset confirmation is required");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            // Perform portfolio reset
            ResetPortfolioResponse response = portfolioResetService.resetUserPortfolio(userId, request);
            
            // Prepare response
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("success", response.getSuccess());
            responseBody.put("message", response.getMessage());
            
            if (response.getSuccess()) {
                responseBody.put("initialCashBalance", response.getInitialCashBalance());
                responseBody.put("resetAt", response.getResetAt());
                
                logger.info("Portfolio reset successful for user {} (ID: {})", username, userId);
            }
            
            return ResponseEntity.ok(responseBody);
            
        } catch (Exception e) {
            logger.error("Error during portfolio reset: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to reset portfolio: " + e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * REST API endpoint to validate if user can reset portfolio
     * @return ResponseEntity with validation result
     */
    @PostMapping("/portfolio/validate")
    public ResponseEntity<Map<String, Object>> validateResetEligibility() {
        try {
            // Get current authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = (User) userService.loadUserByUsername(username);
            Long userId = user.getId();
            
            // Check if user is eligible for reset
            boolean isEligible = portfolioResetService.validateResetEligibility(userId);
            
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("eligible", isEligible);
            responseBody.put("message", isEligible ? 
                "User is eligible for portfolio reset" : 
                "User is not eligible for portfolio reset at this time");
            
            return ResponseEntity.ok(responseBody);
            
        } catch (Exception e) {
            logger.error("Error during reset eligibility validation: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("eligible", false);
            errorResponse.put("message", "Failed to validate eligibility: " + e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}
