package com.example.stockeasy.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * MarketData entity representing historical price data for stocks.
 * Stores historical price information including OHLC (Open, High, Low, Close) data.
 */
@Entity
@Table(name = "market_data")
public class MarketData {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;
    
    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDateTime date;
    
    @Column(name = "open_price", precision = 15, scale = 2)
    private BigDecimal openPrice;
    
    @Column(name = "high_price", precision = 15, scale = 2)
    private BigDecimal highPrice;
    
    @Column(name = "low_price", precision = 15, scale = 2)
    private BigDecimal lowPrice;
    
    @Column(name = "close_price", precision = 15, scale = 2)
    private BigDecimal closePrice;
    
    @Column(name = "volume")
    private Long volume;
    
    @Column(name = "adjusted_close", precision = 15, scale = 2)
    private BigDecimal adjustedClose;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    // Constructors
    public MarketData() {}
    
    public MarketData(Stock stock, LocalDateTime date, BigDecimal openPrice, BigDecimal highPrice, BigDecimal lowPrice, BigDecimal closePrice) {
        this.stock = stock;
        this.date = date;
        this.openPrice = openPrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.closePrice = closePrice;
    }
    
    // Business methods
    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.date == null) {
            this.date = LocalDateTime.now();
        }
        if (this.closePrice != null && (this.openPrice == null || this.openPrice.compareTo(BigDecimal.ZERO) == 0)) {
            this.openPrice = this.closePrice;
        }
        if (this.closePrice != null && (this.highPrice == null || this.highPrice.compareTo(BigDecimal.ZERO) == 0)) {
            this.highPrice = this.closePrice;
        }
        if (this.closePrice != null && (this.lowPrice == null || this.lowPrice.compareTo(BigDecimal.ZERO) == 0)) {
            this.lowPrice = this.closePrice;
        }
    }
    
    public BigDecimal getPriceChange() {
        BigDecimal previousClose = getPreviousClose();
        if (previousClose == null || previousClose.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return closePrice.subtract(previousClose);
    }
    
    public BigDecimal getPriceChangePercent() {
        BigDecimal previousClose = getPreviousClose();
        if (previousClose == null || previousClose.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return closePrice.subtract(previousClose)
                .divide(previousClose, 4, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal("100"));
    }
    
    public BigDecimal getPreviousClose() {
        // In a real implementation, this would query the previous day's market data
        // For the simulator, we'll return the current close price as "previous" close
        return closePrice;
    }
    
    public boolean isPriceUp() {
        BigDecimal priceChange = getPriceChange();
        return priceChange.compareTo(BigDecimal.ZERO) > 0;
    }
    
    public boolean isPriceDown() {
        BigDecimal priceChange = getPriceChange();
        return priceChange.compareTo(BigDecimal.ZERO) < 0;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Stock getStock() { return stock; }
    public void setStock(Stock stock) { this.stock = stock; }
    
    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }
    
    public BigDecimal getOpenPrice() { return openPrice; }
    public void setOpenPrice(BigDecimal openPrice) { this.openPrice = openPrice; }
    
    public BigDecimal getHighPrice() { return highPrice; }
    public void setHighPrice(BigDecimal highPrice) { this.highPrice = highPrice; }
    
    public BigDecimal getLowPrice() { return lowPrice; }
    public void setLowPrice(BigDecimal lowPrice) { this.lowPrice = lowPrice; }
    
    public BigDecimal getClosePrice() { return closePrice; }
    public void setClosePrice(BigDecimal closePrice) { 
        this.closePrice = closePrice; 
        // When close price is updated, update the stock's current price
        if (stock != null) {
            stock.updatePrice(closePrice);
        }
    }
    
    public Long getVolume() { return volume; }
    public void setVolume(Long volume) { this.volume = volume; }
    
    public BigDecimal getAdjustedClose() { return adjustedClose; }
    public void setAdjustedClose(BigDecimal adjustedClose) { this.adjustedClose = adjustedClose; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    // Utility methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MarketData marketData)) return false;
        return Objects.equals(getId(), marketData.getId());
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
    
    @Override
    public String toString() {
        return "MarketData{" +
                "id=" + id +
                ", stockId=" + (stock != null ? stock.getId() : null) +
                ", date=" + date +
                ", open=" + openPrice +
                ", high=" + highPrice +
                ", low=" + lowPrice +
                ", close=" + closePrice +
                ", volume=" + volume +
                ", adjustedClose=" + adjustedClose +
                ", createdAt=" + createdAt +
                '}';
    }
}
