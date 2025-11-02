package com.example.stockeasy.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.stockeasy.domain.Portfolio;
import com.example.stockeasy.domain.Stock;
import com.example.stockeasy.domain.User;
import com.example.stockeasy.exception.ResourceNotFoundException;
import com.example.stockeasy.repo.PortfolioRepository;
import com.example.stockeasy.repo.StockRepository;
import com.example.stockeasy.repo.UserRepository;
import com.example.stockeasy.util.ValidationUtils;

/**
 * PortfolioService for portfolio management operations.
 * Handles portfolio management, portfolio analysis, and portfolio operations.
 */
@Service
public class PortfolioService {
    
    @Autowired
    private PortfolioRepository portfolioRepository;
    
    @Autowired
    private StockRepository stockRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public Portfolio addToPortfolio(Long userId, Long stockId, Integer quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock not found"));

        // Validate inputs
        ValidationUtils.validateUser(user);
        ValidationUtils.validateStock(stock);
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        Portfolio portfolio = new Portfolio(user, stock, quantity);
        return portfolioRepository.save(portfolio);
    }
    
    public void removeFromPortfolio(Long portfolioId) {
        portfolioRepository.deleteById(portfolioId);
    }
    
    public List<Portfolio> getUserPortfolio(Long userId) {
        return portfolioRepository.findUserPortfolioWithStockInfo(userId);
    }
    
    public List<Portfolio> getUserPortfolioWithProfitLossAnalysis(Long userId) {
        return portfolioRepository.findUserPortfolioWithProfitLossAnalysis(userId);
    }
    
    public Double calculatePortfolioValue(Long userId) {
        return portfolioRepository.calculatePortfolioValue(userId);
    }
    
    public Portfolio updatePortfolioQuantity(Long portfolioId, Integer newQuantity) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new RuntimeException("Portfolio entry not found"));
        
        portfolio.setQuantity(newQuantity);
        return portfolioRepository.save(portfolio);
    }
    
    public BigDecimal getAveragePurchasePrice(Long userId, Long stockId) {
        List<Portfolio> portfolioItems = portfolioRepository.findByUserIdAndStockId(userId, stockId);
        
        if (portfolioItems.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal totalInvestment = portfolioItems.stream()
                .map(p -> p.getAveragePurchasePrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalQuantity = portfolioItems.stream()
                .map(p -> BigDecimal.valueOf(p.getQuantity()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return totalInvestment.divide(totalQuantity, 2, java.math.RoundingMode.HALF_UP);
    }
    
    public List<Portfolio> getPortfolioDiversification(Long userId) {
        return portfolioRepository.findByUserId(userId);
    }
}
