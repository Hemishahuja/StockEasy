package com.example.stockeasy.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.stockeasy.domain.Portfolio;

/**
 * PortfolioRepository interface for Portfolio entity persistence operations.
 * Provides CRUD operations and custom queries for Portfolio management.
 */
@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    
    /**
     * Find portfolio entries by user ID
     */
    List<Portfolio> findByUserId(Long userId);
    
    /**
     * Find portfolio entries by stock ID
     */
    List<Portfolio> findByStockId(Long stockId);
    
    /**
     * Get user's portfolio with stock information
     */
    @Query("SELECT p FROM Portfolio p JOIN FETCH p.stock WHERE p.user.id = :userId ORDER BY p.quantity DESC")
    List<Portfolio> findUserPortfolioWithStockInfo(Long userId);
    
    /**
     * Calculate user's portfolio value by user ID
     */
    @Query("SELECT COALESCE(SUM(p.currentValue), 0) FROM Portfolio p WHERE p.user.id = :userId")
    Double calculatePortfolioValue(Long userId);
    
    /**
     * Find portfolio entries with profit/loss analysis
     */
    @Query("SELECT p FROM Portfolio p WHERE p.user.id = :userId ORDER BY p.profitLoss DESC")
    List<Portfolio> findUserPortfolioWithProfitLossAnalysis(Long userId);
    
    /**
     * Find portfolio entry by user ID and stock ID
     */
    List<Portfolio> findByUserIdAndStockId(Long userId, Long stockId);
    
    /**
     * Delete all portfolio entries for a user (for reset operations)
     */
    void deleteByUserId(Long userId);
}
