package com.example.stockeasy.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.stockeasy.domain.BuyTransaction;
import com.example.stockeasy.domain.Portfolio;
import com.example.stockeasy.domain.SellTransaction;
import com.example.stockeasy.domain.Stock;
import com.example.stockeasy.domain.Transaction;
import com.example.stockeasy.domain.User;
import com.example.stockeasy.exception.ResourceNotFoundException;
import com.example.stockeasy.repo.PortfolioRepository;
import com.example.stockeasy.repo.StockRepository;
import com.example.stockeasy.repo.TransactionRepository;
import com.example.stockeasy.repo.UserRepository;
import com.example.stockeasy.util.ValidationUtils;

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
    
    @Transactional
    public Transaction buyStock(Long userId, Long stockId, Integer quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock not found"));

        // Validate the buy transaction
        ValidationUtils.validateBuyTransaction(user, stock, quantity);

        // Create buy transaction
        BuyTransaction buyTransaction = new BuyTransaction(user, stock, quantity, stock.getCurrentPrice());

        // Update user's cash balance
        BigDecimal totalCost = stock.getCurrentPrice().multiply(new BigDecimal(quantity));
        user.withdrawCash(totalCost);

        // Add stock to user's portfolio
        Portfolio portfolio = portfolioService.addToPortfolio(userId, stockId, quantity);

        // Save transaction and update user
        Transaction transaction = transactionRepository.save(buyTransaction);
        userRepository.save(user);
        portfolioRepository.save(portfolio);

        return transaction;
    }
    
    @Transactional
    public Transaction sellStock(Long userId, Long stockId, Integer quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock not found"));

        // Get user's portfolio for this stock
        Portfolio portfolio = portfolioRepository.findByUserIdAndStockId(userId, stockId).stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No portfolio entry found for this stock"));

        // Validate the sell transaction
        ValidationUtils.validateSellTransaction(user, portfolio, quantity);

        // Create sell transaction
        SellTransaction sellTransaction = new SellTransaction(user, stock, quantity, stock.getCurrentPrice());

        // Update user's cash balance
        user.depositCash(sellTransaction.getCashAdded());

        // Update portfolio quantity
        portfolio.setQuantity(portfolio.getQuantity() - quantity);
        if (portfolio.getQuantity() > 0) {
            portfolio.updateCurrentValue(); // Update current value after quantity change
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

        // Calculate current value for this specific stock
        List<Portfolio> portfolios = portfolioRepository.findByUserIdAndStockId(userId, stockId);
        if (portfolios.isEmpty()) {
            // No current holdings, so loss equals total cost invested
            return totalCost.negate();
        }

        BigDecimal currentValue = portfolios.stream()
                .map(Portfolio::getCurrentValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return currentValue.subtract(totalCost);
    }
    
    /**
     * Clear all transactions for a user (for reset operations)
     */
    @Transactional
    public void clearUserTransactions(Long userId) {
        transactionRepository.deleteByUserId(userId);
    }
}
