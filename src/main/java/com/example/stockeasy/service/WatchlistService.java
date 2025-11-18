package com.example.stockeasy.service;

import com.example.stockeasy.domain.Watchlist;
import com.example.stockeasy.repo.WatchlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

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
        // In a real implementation, you would get user and stock from repositories
        // For now, we'll set them directly
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
