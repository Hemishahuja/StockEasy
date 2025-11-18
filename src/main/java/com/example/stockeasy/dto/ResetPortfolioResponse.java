package com.example.stockeasy.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response object for portfolio reset operations.
 * Contains operation result and restored portfolio state information.
 */
public class ResetPortfolioResponse {
    
    private Boolean success;
    private String message;
    private LocalDateTime resetAt;
    private BigDecimal initialCashBalance;
    
    public ResetPortfolioResponse() {
    }
    
    public ResetPortfolioResponse(Boolean success, String message) {
        this.success = success;
        this.message = message;
        this.resetAt = LocalDateTime.now();
    }
    
    public ResetPortfolioResponse(Boolean success, String message, BigDecimal initialCashBalance) {
        this.success = success;
        this.message = message;
        this.resetAt = LocalDateTime.now();
        this.initialCashBalance = initialCashBalance;
    }
    
    // Getters and Setters
    public Boolean getSuccess() {
        return success;
    }
    
    public void setSuccess(Boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public LocalDateTime getResetAt() {
        return resetAt;
    }
    
    public void setResetAt(LocalDateTime resetAt) {
        this.resetAt = resetAt;
    }
    
    public BigDecimal getInitialCashBalance() {
        return initialCashBalance;
    }
    
    public void setInitialCashBalance(BigDecimal initialCashBalance) {
        this.initialCashBalance = initialCashBalance;
    }
    
    @Override
    public String toString() {
        return "ResetPortfolioResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", resetAt=" + resetAt +
                ", initialCashBalance=" + initialCashBalance +
                '}';
    }
    
    /**
     * Builder pattern support for creating ResetPortfolioResponse instances
     */
    public static ResetPortfolioResponseBuilder builder() {
        return new ResetPortfolioResponseBuilder();
    }
    
    public static class ResetPortfolioResponseBuilder {
        private Boolean success;
        private String message;
        private LocalDateTime resetAt;
        private BigDecimal initialCashBalance;
        
        public ResetPortfolioResponseBuilder success(Boolean success) {
            this.success = success;
            return this;
        }
        
        public ResetPortfolioResponseBuilder message(String message) {
            this.message = message;
            return this;
        }
        
        public ResetPortfolioResponseBuilder resetAt(LocalDateTime resetAt) {
            this.resetAt = resetAt;
            return this;
        }
        
        public ResetPortfolioResponseBuilder initialCashBalance(BigDecimal initialCashBalance) {
            this.initialCashBalance = initialCashBalance;
            return this;
        }
        
        public ResetPortfolioResponse build() {
            ResetPortfolioResponse response = new ResetPortfolioResponse();
            response.setSuccess(this.success);
            response.setMessage(this.message);
            response.setResetAt(this.resetAt != null ? this.resetAt : LocalDateTime.now());
            response.setInitialCashBalance(this.initialCashBalance);
            return response;
        }
    }
}
