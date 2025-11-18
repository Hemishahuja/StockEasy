package com.example.stockeasy.event;

import java.time.LocalDateTime;
import java.util.List;

import com.example.stockeasy.dto.StockHolding;

/**
 * Domain event for portfolio reset operations.
 * Captures audit information when a user resets their portfolio.
 */
public class PortfolioResetEvent {
    
    private Long userId;
    private List<StockHolding> previousHoldings;
    private LocalDateTime timestamp;
    private String ipAddress;
    
    public PortfolioResetEvent() {
    }
    
    public PortfolioResetEvent(Long userId, List<StockHolding> previousHoldings, String ipAddress) {
        this.userId = userId;
        this.previousHoldings = previousHoldings;
        this.timestamp = LocalDateTime.now();
        this.ipAddress = ipAddress;
    }
    
    // Getters and Setters
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public List<StockHolding> getPreviousHoldings() {
        return previousHoldings;
    }
    
    public void setPreviousHoldings(List<StockHolding> previousHoldings) {
        this.previousHoldings = previousHoldings;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    @Override
    public String toString() {
        return "PortfolioResetEvent{" +
                "userId=" + userId +
                ", previousHoldingsCount=" + (previousHoldings != null ? previousHoldings.size() : 0) +
                ", timestamp=" + timestamp +
                ", ipAddress='" + ipAddress + '\'' +
                '}';
    }
}
