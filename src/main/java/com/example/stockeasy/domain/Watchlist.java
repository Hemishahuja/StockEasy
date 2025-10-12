package com.example.stockeasy.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Watchlist entity representing stocks that users want to monitor.
 * Allows users to track stocks of interest without owning them.
 */
@Entity
@Table(name = "watchlists", 
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"user_id", "stock_id"})
       })
public class Watchlist {
    
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
    
    @Column(name = "added_at", nullable = false, updatable = false)
    private LocalDateTime addedAt = LocalDateTime.now();
    
    @Column(name = "notes")
    private String notes;
    
    @Column(name = "alert_price", precision = 15, scale = 2)
    private BigDecimal alertPrice;
    
    @Column(name = "alert_enabled", nullable = false)
    private Boolean alertEnabled = false;
    
    @Column(name = "rank")
    private Integer rank;
    
    // Constructors
    public Watchlist() {}
    
    public Watchlist(User user, Stock stock) {
        this.user = user;
        this.stock = stock;
    }
    
    public Watchlist(User user, Stock stock, BigDecimal alertPrice) {
        this.user = user;
        this.stock = stock;
        this.alertPrice = alertPrice;
        this.alertEnabled = true;
    }
    
    // Business methods
    @PrePersist
    public void prePersist() {
        if (this.addedAt == null) {
            this.addedAt = LocalDateTime.now();
        }
    }
    
    public void enableAlerts() {
        this.alertEnabled = true;
    }
    
    public void disableAlerts() {
        this.alertEnabled = false;
    }
    
    public void removeAlertPrice() {
        this.alertPrice = null;
        this.alertEnabled = false;
    }
    
    public boolean hasAlert() {
        return alertPrice != null && alertEnabled;
    }
    
    public boolean shouldTriggerAlert() {
        if (!hasAlert() || stock == null || stock.getCurrentPrice() == null) {
            return false;
        }
        
        BigDecimal currentPrice = stock.getCurrentPrice();
        BigDecimal alertPrice = this.alertPrice;
        
        // Trigger alert if current price crosses alert price (either above or below)
        return (currentPrice.compareTo(alertPrice) >= 0 && 
                (stock.getPreviousClose() == null || stock.getPreviousClose().compareTo(alertPrice) < 0)) ||
               (currentPrice.compareTo(alertPrice) <= 0 && 
                (stock.getPreviousClose() == null || stock.getPreviousClose().compareTo(alertPrice) > 0));
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public Stock getStock() { return stock; }
    public void setStock(Stock stock) { this.stock = stock; }
    
    public LocalDateTime getAddedAt() { return addedAt; }
    public void setAddedAt(LocalDateTime addedAt) { this.addedAt = addedAt; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public BigDecimal getAlertPrice() { return alertPrice; }
    public void setAlertPrice(BigDecimal alertPrice) { 
        this.alertPrice = alertPrice; 
        this.alertEnabled = true;
    }
    
    public Boolean getAlertEnabled() { return alertEnabled; }
    public void setAlertEnabled(Boolean alertEnabled) { this.alertEnabled = alertEnabled; }
    
    public Integer getRank() { return rank; }
    public void setRank(Integer rank) { this.rank = rank; }
    
    // Utility methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Watchlist watchlist)) return false;
        return Objects.equals(getId(), watchlist.getId());
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
    
    @Override
    public String toString() {
        return "Watchlist{" +
                "id=" + id +
                ", userId=" + (user != null ? user.getId() : null) +
                ", stockId=" + (stock != null ? stock.getId() : null) +
                ", symbol='" + (stock != null ? stock.getSymbol() : null) + '\'' +
                ", addedAt=" + addedAt +
                ", alertPrice=" + alertPrice +
                ", alertEnabled=" + alertEnabled +
                ", rank=" + rank +
                '}';
    }
}
