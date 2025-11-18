package com.example.stockeasy.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.stockeasy.domain.Watchlist;
import com.example.stockeasy.repo.WatchlistRepository;

/**
 * WatchlistService for watchlist management operations.
 * Handles watchlist mangemnt, price alerts, and notification processing.
 */
@Service
public class WatchlistService {
    
    @Autowired
    private WatchlistRepository watchlistRepository;
    
    public Watchlist addToWatchlist(Long userId, Long stockId) {
        Watchlist watchlist = new Watchlist();
        // Mock user and stock objects since we don't have full repository services yet
        // In a real implementation, you would fetch these from UserRepository and StockRepository
        
        // Create mock user object
        com.example.stockeasy.domain.User user = new com.example.stockeasy.domain.User();
        user.setId(userId);
        user.setFirstName("Demo");
        user.setLastName("User");
        user.setEmail("demo@example.com");
        
        // Create mock stock object
        com.example.stockeasy.domain.Stock stock = new com.example.stockeasy.domain.Stock();
        stock.setId(stockId);
        // The stock symbol/name would be fetched from the database in a real implementation
        
        watchlist.setUser(user);
        watchlist.setStock(stock);
        watchlist.setAlertEnabled(false); // Default to alerts disabled
        
        return watchlistRepository.save(watchlist);
    }
    
    public void removeFromWatchlist(Long watchlistId) {
        watchlistRepository.deleteById(watchlistId);
    }
    
    public List<Watchlist> getUserWatchlist(Long userId) {
        return watchlistRepository.findUserWatchlistWithStockInfo(userId);
    }
    
    public void enableAlerts(Long watchlistId) {
        Watchlist watchlist = watchlistRepository.findById(watchlistId)
                .orElseThrow(() -> new RuntimeException("Watchlist entry not found"));
        watchlist.enableAlerts();
        watchlistRepository.save(watchlist);
    }
    
    public void disableAlerts(Long watchlistId) {
        Watchlist watchlist = watchlistRepository.findById(watchlistId)
                .orElseThrow(() -> new RuntimeException("Watchlist entry found"));
        watchlist.disableAlerts();
        watchlistRepository.save(watchlist);
    }
    
    public void setAlertPrice(Long watchlistId, BigDecimal alertPrice) {
        Watchlist watchlist = watchlistRepository.findById(watchlistId)
                .orElseThrow(() -> new RuntimeException("Watchlist entry not found"));
        watchlist.setAlertPrice(alertPrice);
        watchlistRepository.save(watchlist);
    }
    
    public List<Watchlist> getWatchlistItemsWithAlertsEnabled() {
        return watchlistRepository.findWatchlistItemsWithAlertsEnabled();
    }
    
    public boolean checkPriceAlerts() {
        List<Watchlist> alertEnabledItems = getWatchlistItemsWithAlertsEnabled();
        boolean alertsTriggered = false;
        
        for (Watchlist watchlist : alertEnabledItems) {
            if (watchlist.shouldTriggerAlert()) {
                // In a real implementation, you would send notifications
                // For now, we'll just return whether alerts were triggered
                alertsTriggered = true;
            }
        }
        
        return alertsTriggered;
    }
}
