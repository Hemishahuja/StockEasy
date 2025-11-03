package com.example.stockeasy.util;

import java.math.BigDecimal;

import com.example.stockeasy.domain.Portfolio;
import com.example.stockeasy.domain.Stock;
import com.example.stockeasy.domain.Transaction;
import com.example.stockeasy.domain.User;
import com.example.stockeasy.exception.InsufficientFundsException;
import com.example.stockeasy.exception.InsufficientSharesException;
import com.example.stockeasy.exception.ResourceNotFoundException;

public class ValidationUtils {

    public static void validateTransaction(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }
        if (transaction.getQuantity() <= 0) {
            throw new IllegalArgumentException("Transaction quantity must be positive");
        }
        if (transaction.getPrice() == null || transaction.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transaction price must be positive");
        }
    }

    public static void validateUser(User user) {
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
    }

    public static void validateStock(Stock stock) {
        if (stock == null) {
            throw new ResourceNotFoundException("Stock not found");
        }
        if (stock.getSymbol() == null || stock.getSymbol().trim().isEmpty()) {
            throw new IllegalArgumentException("Stock symbol cannot be empty");
        }
        if (stock.getCurrentPrice() == null || stock.getCurrentPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Stock price must be positive");
        }
    }

    public static void validatePortfolio(Portfolio portfolio) {
        if (portfolio == null) {
            throw new ResourceNotFoundException("Portfolio entry not found");
        }
        if (portfolio.getQuantity() < 0) {
            throw new IllegalArgumentException("Portfolio quantity cannot be negative");
        }
    }

    public static void validateBuyTransaction(User user, Stock stock, Integer quantity) {
        validateUser(user);
        validateStock(stock);

        if (quantity <= 0) {
            throw new IllegalArgumentException("Buy quantity must be positive");
        }

        BigDecimal totalCost = stock.getCurrentPrice().multiply(new BigDecimal(quantity));
        if (user.getCashBalance().compareTo(totalCost) < 0) {
            throw new InsufficientFundsException("Insufficient funds for this transaction");
        }
    }

    public static void validateSellTransaction(User user, Portfolio portfolio, Integer quantity) {
        validateUser(user);
        validatePortfolio(portfolio);

        if (quantity <= 0) {
            throw new IllegalArgumentException("Sell quantity must be positive");
        }

        if (portfolio.getQuantity() < quantity) {
            throw new InsufficientSharesException("Insufficient shares for this transaction");
        }
    }

    public static void validateWatchlistOperation(User user, Stock stock) {
        validateUser(user);
        validateStock(stock);
    }
}
