package com.example.stockeasy.dto;

import java.time.LocalDateTime;

/**
 * Request object for portfolio reset operations.
 * Contains user confirmation and metadata for portfolio reset requests.
 */
public class ResetPortfolioRequest {
    
    private Long userId;
    private Boolean confirmReset;
    private LocalDateTime timestamp;
    
    public ResetPortfolioRequest() {
        this.timestamp = LocalDateTime.now();
    }
    
    public ResetPortfolioRequest(Long userId, Boolean confirmReset) {
        this.userId = userId;
        this.confirmReset = confirmReset;
        this.timestamp = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public Boolean getConfirmReset() {
        return confirmReset;
    }
    
    public void setConfirmReset(Boolean confirmReset) {
        this.confirmReset = confirmReset;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    @Override
    public String toString() {
        return "ResetPortfolioRequest{" +
                "userId=" + userId +
                ", confirmReset=" + confirmReset +
                ", timestamp=" + timestamp +
                '}';
    }
}
