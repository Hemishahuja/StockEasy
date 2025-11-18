package com.example.stockeasy.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.stockeasy.domain.Portfolio;
import com.example.stockeasy.domain.User;
import com.example.stockeasy.dto.ResetPortfolioRequest;
import com.example.stockeasy.dto.ResetPortfolioResponse;
import com.example.stockeasy.dto.StockHolding;
import com.example.stockeasy.event.PortfolioResetEvent;
import com.example.stockeasy.exception.ResourceNotFoundException;
import com.example.stockeasy.repo.UserRepository;

/**
 * Service for handling portfolio reset operations.
 * Provides complete portfolio reset functionality with audit logging and transaction management.
 */
@Service
public class PortfolioResetService {
    
    private static final Logger logger = LoggerFactory.getLogger(PortfolioResetService.class);
    
    @Autowired
    private PortfolioService portfolioService;
    
    @Autowired
    private TransactionService transactionService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository;
    
    private static final BigDecimal INITIAL_CASH_BALANCE = new BigDecimal("100000.00");
    private static final int RESET_COOLDOWN_HOURS = 24; // Prevent frequent resets
    
    /**
     * Reset a user's portfolio to default state
     * @param userId the ID of the user whose portfolio should be reset
     * @param request the reset request containing confirmation
     * @return response indicating success or failure
     */
    @Transactional
    public ResetPortfolioResponse resetUserPortfolio(Long userId, ResetPortfolioRequest request) {
        try {
            // Validate request
            if (request == null) {
                return createErrorResponse("Reset request cannot be null");
            }
            
            if (!request.getConfirmReset()) {
                return createErrorResponse("Reset confirmation is required");
            }
            
            // Validate user exists
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
            
            // Check reset eligibility
            if (!validateResetEligibility(userId)) {
                return createErrorResponse("Reset operation not allowed at this time");
            }
            
            // Create audit snapshot before reset
            List<StockHolding> previousHoldings = createPortfolioSnapshot(userId);
            
            // Perform the reset operations
            performPortfolioReset(userId);
            
            // Create audit event
            PortfolioResetEvent resetEvent = new PortfolioResetEvent(
                    userId, 
                    previousHoldings, 
                    "UNKNOWN" // IP address would be injected in controller
            );
            
            // Log the reset operation
            logger.info("Portfolio reset completed for user {}: {}", userId, resetEvent);
            
            // Return success response
            return ResetPortfolioResponse.builder()
                    .success(true)
                    .message("Portfolio successfully reset to default state")
                    .initialCashBalance(INITIAL_CASH_BALANCE)
                    .build();
                    
        } catch (Exception e) {
            logger.error("Error during portfolio reset for user {}: {}", userId, e.getMessage(), e);
            return createErrorResponse("Failed to reset portfolio: " + e.getMessage());
        }
    }
    
    /**
     * Create a snapshot of current portfolio holdings for audit purposes
     * @param userId the ID of the user
     * @return list of current stock holdings
     */
    public List<StockHolding> createPortfolioSnapshot(Long userId) {
        List<Portfolio> currentPortfolios = portfolioService.getUserPortfolio(userId);
        
        return currentPortfolios.stream()
                .map(portfolio -> new StockHolding(
                        portfolio.getStock().getSymbol(),
                        portfolio.getStock().getCompanyName(),
                        portfolio.getQuantity(),
                        portfolio.getAveragePurchasePrice(),
                        portfolio.getCurrentValue()
                ))
                .collect(Collectors.toList());
    }
    
    /**
     * Validate if user is eligible for portfolio reset
     * @param userId the ID of the user
     * @return true if eligible, false otherwise
     */
    public boolean validateResetEligibility(Long userId) {
        // Check if user exists
        if (!userRepository.existsById(userId)) {
            return false;
        }
        
        // TODO: Add cooldown logic if needed
        // Could check last reset time from audit logs
        
        return true;
    }
    
    /**
     * Perform the actual portfolio reset operations
     * @param userId the ID of the user to reset
     */
    private void performPortfolioReset(Long userId) {
        // Clear all transactions first (due to foreign key constraints)
        transactionService.clearUserTransactions(userId);
        
        // Clear all portfolio holdings
        portfolioService.deleteUserPortfolio(userId);
        
        // Reset cash balance
        userService.resetUserCashBalance(userId);
        
        logger.info("Portfolio reset completed for user {}", userId);
    }
    
    /**
     * Create error response
     * @param message error message
     * @return error response
     */
    private ResetPortfolioResponse createErrorResponse(String message) {
        return ResetPortfolioResponse.builder()
                .success(false)
                .message(message)
                .build();
    }
}
