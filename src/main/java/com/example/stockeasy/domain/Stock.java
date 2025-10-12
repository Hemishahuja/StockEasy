package com.example.stockeasy.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Stock entity representing a tradable stock in the market.
 * Contains core stock information and relationships to portfolios, watchlists, and market data.
 */
@Entity
@Table(name = "stocks", 
       uniqueConstraints = {
           @UniqueConstraint(columnNames = "symbol"),
           @UniqueConstraint(columnNames = "company_name")
       })
public class Stock {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(max = 10)
    @Pattern(regexp = "^[A-Z]{1,10}$", message = "Symbol must contain only uppercase letters")
    @Column(name = "symbol", nullable = false, unique = true, length = 10)
    private String symbol;
    
    @NotBlank
    @Size(max = 100)
    @Column(name = "company_name", nullable = false, unique = true)
    private String companyName;
    
    @Size(max = 500)
    @Column(name = "description")
    private String description;
    
    @Size(max = 50)
    @Column(name = "sector")
    private String sector;
    
    @Size(max = 50)
    @Column(name = "industry")
    private String industry;
    
    @Size(max = 20)
    @Column(name = "currency")
    private String currency = "USD";
    
    @Column(name = "current_price", precision = 15, scale = 2, nullable = false)
    private BigDecimal currentPrice = BigDecimal.ZERO;
    
    @Column(name = "previous_close", precision = 15, scale = 2)
    private BigDecimal previousClose;
    
    @Column(name = "market_cap", precision = 20, scale = 2)
    private BigDecimal marketCap;
    
    @Column(name = "volume")
    private Long volume;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @Column(name = "last_traded_at")
    private LocalDateTime lastTradedAt;
    
    // Relationships
    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Portfolio> portfolios = new HashSet<>();
    
    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Watchlist> watchlists = new HashSet<>();
    
    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Transaction> transactions = new HashSet<>();
    
    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<MarketData> marketData = new HashSet<>();
    
    // Constructors
    public Stock() {}
    
    public Stock(String symbol, String companyName, BigDecimal currentPrice) {
        this.symbol = symbol;
        this.companyName = companyName;
        this.currentPrice = currentPrice;
    }
    
    public Stock(String symbol, String companyName, String description, String sector, 
                 String industry, BigDecimal currentPrice, BigDecimal marketCap) {
        this.symbol = symbol;
        this.companyName = companyName;
        this.description = description;
        this.sector = sector;
        this.industry = industry;
        this.currentPrice = currentPrice;
        this.marketCap = marketCap;
    }
    
    // Business methods
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    public void updatePrice(BigDecimal newPrice) {
        if (newPrice != null && newPrice.compareTo(BigDecimal.ZERO) > 0) {
            this.previousClose = this.currentPrice;
            this.currentPrice = newPrice;
            this.lastTradedAt = LocalDateTime.now();
            this.updatedAt = LocalDateTime.now();
        }
    }
    
    public BigDecimal getPriceChange() {
        if (previousClose == null || previousClose.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return currentPrice.subtract(previousClose);
    }
    
    public BigDecimal getPriceChangePercent() {
        if (previousClose == null || previousClose.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return currentPrice.subtract(previousClose)
                .divide(previousClose, 4, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal("100"));
    }
    
    public boolean isPriceUp() {
        return getPriceChange().compareTo(BigDecimal.ZERO) > 0;
    }
    
    public boolean isPriceDown() {
        return getPriceChange().compareTo(BigDecimal.ZERO) < 0;
    }
    
    public boolean isPriceUnchanged() {
        return getPriceChange().compareTo(BigDecimal.ZERO) == 0;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getSector() { return sector; }
    public void setSector(String sector) { this.sector = sector; }
    
    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }
    
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    
    public BigDecimal getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(BigDecimal currentPrice) { this.currentPrice = currentPrice; }
    
    public BigDecimal getPreviousClose() { return previousClose; }
    public void setPreviousClose(BigDecimal previousClose) { this.previousClose = previousClose; }
    
    public BigDecimal getMarketCap() { return marketCap; }
    public void setMarketCap(BigDecimal marketCap) { this.marketCap = marketCap; }
    
    public Long getVolume() { return volume; }
    public void setVolume(Long volume) { this.volume = volume; }
    
    public Boolean getActive() { return isActive; }
    public void setActive(Boolean active) { isActive = active; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public LocalDateTime getLastTradedAt() { return lastTradedAt; }
    public void setLastTradedAt(LocalDateTime lastTradedAt) { this.lastTradedAt = lastTradedAt; }
    
    public Set<Portfolio> getPortfolios() { return portfolios; }
    public void setPortfolios(Set<Portfolio> portfolios) { this.portfolios = portfolios; }
    
    public Set<Watchlist> getWatchlists() { return watchlists; }
    public void setWatchlists(Set<Watchlist> watchlists) { this.watchlists = watchlists; }
    
    public Set<Transaction> getTransactions() { return transactions; }
    public void setTransactions(Set<Transaction> transactions) { this.transactions = transactions; }
    
    public Set<MarketData> getMarketData() { return marketData; }
    public void setMarketData(Set<MarketData> marketData) { this.marketData = marketData; }
    
    // Utility methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Stock stock)) return false;
        return Objects.equals(getId(), stock.getId());
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
    
    @Override
    public String toString() {
        return "Stock{" +
                "id=" + id +
                ", symbol='" + symbol + '\'' +
                ", companyName='" + companyName + '\'' +
                ", currentPrice=" + currentPrice +
                ", previousClose=" + previousClose +
                ", sector='" + sector + '\'' +
                ", industry='" + industry + '\'' +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                '}';
    }
}
