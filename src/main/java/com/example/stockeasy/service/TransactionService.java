package com.example.stockeasy.service;

import com.example.stockeasy.domain.*;
import com.example.stockeasy.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * TransactionService for transaction management operations.
 * Handles buy/sell transactions, transaction processing, and transaction analysis.
 */
@Service
public class TransactionService {
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private StockRepository stockRepository;
    
    @Autowired
    private PortfolioRepository portfolioRepository;
    
    @Autowired
    private PortfolioService portfolioService;
    
    public Transaction buyStock(Long userId, Long stockId, Integer quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new RuntimeException("Stock not found"));
        
        // Check if user has sufficient funds
        BigDecimal totalCost = stock.getCurrentPrice().multiply(new BigDecimal(quantity));
        if (user.getCashBalance().compareTo(totalCost) < 0) {
            throw new RuntimeException("Insufficient funds");
        }
        
        // Create buy transaction
        BuyTransaction buyTransaction = new BuyTransaction(user, stock, quantity, stock.getCurrentPrice());
        
        // Update user's cash balance
        user.withdrawCash(totalCost);
        
        // Add stock to user's portfolio
        Portfolio portfolio = portfolioService.addToPortfolio(userId, stockId, quantity);
        
        // Save transaction and update user
        Transaction transaction = transactionRepository.save(buyTransaction);
        userRepository.save(user);
        portfolioRepository.save(portfolio);
        
        return transaction;
    }
    
    public Transaction sellStock(Long userId, Long stockId, Integer quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new RuntimeException("Stock not found"));
        
        // Check if user has sufficient shares
        Portfolio portfolio = portfolioRepository.findByUserIdAndStockId(userId, stockId).stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Insufficient shares"));
        
        if (portfolio.getQuantity() < quantity) {
            throw new RuntimeException("Insufficient shares");
        }
        
        // Create sell transaction
        SellTransaction sellTransaction = new SellTransaction(user, stock, quantity, stock.getCurrentPrice());
        
        // Update user's cash balance
        user.depositCash(sellTransaction.getCashAdded());
        
        // Update portfolio quantity
        portfolio.setQuantity(portfolio.getQuantity() - quantity);
        if (portfolio.getQuantity() > 0) {
            portfolioRepository.save(portfolio);
        } else {
            portfolioRepository.deleteById(portfolio.getId());
        }
        
        // Save transaction and update user
        Transaction transaction = transactionRepository.save(sellTransaction);
        userRepository.save(user);
        
        return transaction;
    }
    
    public List<Transaction> getUserTransactionHistory(Long userId) {
        return transactionRepository.findUserTransactionHistory(userId);
    }
    
    public List<Transaction> getUserBuyTransactions(Long userId) {
        return transactionRepository.findUserBuyTransactions(userId);
    }
    
    public List<Transaction> getUserSellTransactions(Long userId) {
        return transactionRepository.findUserSellTransactions(userId);
    }
    
    public List<Transaction> getRecentTransactions(Long userId) {
        return transactionRepository.findRecentTransactions(userId);
    }
    
    public BigDecimal calculateProfitLoss(Long userId, Long stockId) {
        List<Transaction> buyTransactions = transactionRepository.findByUserIdAndStockId(userId, stockId);
        if (buyTransactions.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal totalCost = buyTransactions.stream()
                .filter(t -> t instanceof BuyTransaction)
                .map(t -> t.getTotalAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal currentValue = BigDecimal.valueOf(portfolioService.calculatePortfolioValue(userId));
        
        return currentValue.subtract(totalCost);
    }
}
