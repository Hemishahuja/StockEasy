package com.example.stockeasy.repo;

import com.example.stockeasy.domain.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * WatchlistRepository interface for Watchlist entity persistence operations.
 * Provides CRUD operations and custom queries for Watchlist management.
 */
@Repository
public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {
    
    /**
     * Find watchlist entries by user ID
     */
    List<Watchlist> findByUserId(Long userId);
    
    /**
     * Find watchlist entries by stock ID
     */
    List<Watchlist> findByStockId(Long stockId);
    
    /**
     * Get user's watchlist with stock information
     */
    @Query("SELECT w FROM Watchlist w JOIN FETCH w.stock WHERE w.user.id = :userId ORDER BY w.rank ASC")
    List<Watchlist> findUserWatchlistWithStockInfo(Long userId);
    
    /**
     * Check if stock is in user's watchlist
     */
    boolean existsByUserIdAndStockId(Long userId, Long stockId);
    
    /**
     * Get stocks with price alerts for notification system
     */
    @Query("SELECT w FROM Watchlist w WHERE w.alertEnabled = true")
    List<Watchlist> findWatchlistItemsWithAlertsEnabled();
}
