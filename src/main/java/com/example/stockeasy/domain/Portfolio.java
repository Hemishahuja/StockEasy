package com.example.stockeasy.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Portfolio entity representing a user's stock holdings.
 * Tracks the quantity and value of stocks owned by a user.
 */
@Entity
@Table(name = "portfolios", 
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"user_id", "stock_id"})
       })
public class Portfolio {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;
    
    @NotNull
    @Column(name = "quantity", nullable = false)
    private Integer quantity = 0;
    
    @Column(name = "average_purchase_price", precision = 15, scale = 2, nullable = false)
    private BigDecimal averagePurchasePrice = BigDecimal.ZERO;
    
    @Column(name = "total_investment", precision = 15, scale = 2, nullable = false)
    private BigDecimal totalInvestment = BigDecimal.ZERO;
    
    @Column(name = "current_value", precision = 15, scale = 2, nullable = false)
    private BigDecimal currentValue = BigDecimal.ZERO;
    
    @Column(name = "profit_loss", precision = 15, scale = 2, nullable = false)
    private BigDecimal profitLoss = BigDecimal.ZERO;
    
    @Column(name = "profit_loss_percentage", precision = 10, scale = 2, nullable = false)
    private BigDecimal profitLossPercentage = BigDecimal.ZERO;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @Column(name = "last_transaction_at")
    private LocalDateTime lastTransactionAt;
    
    // Constructors
    public Portfolio() {}
    
    public Portfolio(User user, Stock stock) {
        this.user = user;
        this.stock = stock;
        this.averagePurchasePrice = stock.getCurrentPrice();
    }
    
    public Portfolio(User user, Stock stock, Integer quantity) {
        this.user = user;
        this.stock = stock;
        this.quantity = quantity;
        this.averagePurchasePrice = stock.getCurrentPrice();
        this.totalInvestment = stock.getCurrentPrice().multiply(new BigDecimal(quantity));
    }
    
    // Business methods
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    public void updateCurrentValue() {
        if (stock != null && stock.getCurrentPrice() != null) {
            this.currentValue = stock.getCurrentPrice().multiply(new BigDecimal(quantity));
            calculateProfitLoss();
        }
    }
    
    public void calculateProfitLoss() {
        if (totalInvestment.compareTo(BigDecimal.ZERO) > 0) {
            this.profitLoss = this.currentValue.subtract(this.totalInvestment);
            this.profitLossPercentage = this.profitLoss
                    .divide(this.totalInvestment, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(new BigDecimal("100"));
        } else {
            this.profitLoss = BigDecimal.ZERO;
            this.profitLossPercentage = BigDecimal.ZERO;
        }
    }
    
    public void addShares(Integer additionalQuantity) {
        if (additionalQuantity > 0) {
            BigDecimal additionalInvestment = stock.getCurrentPrice().multiply(new BigDecimal(additionalQuantity));
            
            // Update average purchase price using weighted average
            BigDecimal newTotalInvestment = this.totalInvestment.add(additionalInvestment);
            this.averagePurchasePrice = newTotalInvestment.divide(
                new BigDecimal(this.quantity + additionalQuantity), 2, BigDecimal.ROUND_HALF_UP);
            
            this.quantity += additionalQuantity;
            this.totalInvestment = newTotalInvestment;
            this.lastTransactionAt = LocalDateTime.now();
            updateCurrentValue();
        }
    }
    
    public boolean removeShares(Integer quantityToRemove) {
        if (quantityToRemove > 0 && this.quantity >= quantityToRemove) {
            this.quantity -= quantityToRemove;
            this.lastTransactionAt = LocalDateTime.now();
            updateCurrentValue();
            return true;
        }
        return false;
    }
    
    public boolean isEmpty() {
        return this.quantity == 0;
    }
    
    public boolean hasProfit() {
        return profitLoss.compareTo(BigDecimal.ZERO) > 0;
    }
    
    public boolean hasLoss() {
        return profitLoss.compareTo(BigDecimal.ZERO) < 0;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public Stock getStock() { return stock; }
    public void setStock(Stock stock) { this.stock = stock; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    
    public BigDecimal getAveragePurchasePrice() { return averagePurchasePrice; }
    public void setAveragePurchasePrice(BigDecimal averagePurchasePrice) { 
        this.averagePurchasePrice = averagePurchasePrice; 
    }
    
    public BigDecimal getTotalInvestment() { return totalInvestment; }
    public void setTotalInvestment(BigDecimal totalInvestment) { 
        this.totalInvestment = totalInvestment; 
    }
    
    public BigDecimal getCurrentValue() { return currentValue; }
    public void setCurrentValue(BigDecimal currentValue) { 
        this.currentValue = currentValue; 
    }
    
    public BigDecimal getProfitLoss() { return profitLoss; }
    public void setProfitLoss(BigDecimal profitLoss) { 
        this.profitLoss = profitLoss; 
    }
    
    public BigDecimal getProfitLossPercentage() { return profitLossPercentage; }
    public void setProfitLossPercentage(BigDecimal profitLossPercentage) { 
        this.profitLossPercentage = profitLossPercentage; 
    }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public LocalDateTime getLastTransactionAt() { return lastTransactionAt; }
    public void setLastTransactionAt(LocalDateTime lastTransactionAt) { 
        this.lastTransactionAt = lastTransactionAt; 
    }
    
    // Utility methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Portfolio portfolio)) return false;
        return Objects.equals(getId(), portfolio.getId());
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
    
    @Override
    public String toString() {
        return "Portfolio{" +
                "id=" + id +
                ", userId=" + (user != null ? user.getId() : null) +
                ", stockId=" + (stock != null ? stock.getId() : null) +
                ", symbol='" + (stock != null ? stock.getSymbol() : null) + '\'' +
                ", quantity=" + quantity +
                ", averagePurchasePrice=" + averagePurchasePrice +
                ", currentValue=" + currentValue +
                ", profitLoss=" + profitLoss +
                ", createdAt=" + createdAt +
                '}';
    }
}
