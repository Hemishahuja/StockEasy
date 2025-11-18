package com.example.stockeasy.dto;

import java.math.BigDecimal;

/**
 * DTO representing a stock holding for audit snapshot purposes.
 * Captures the state of a portfolio holding at a specific point in time.
 */
public class StockHolding {
    
    private String stockSymbol;
    private String stockName;
    private Integer quantity;
    private BigDecimal averagePrice;
    private BigDecimal currentValue;
    
    public StockHolding() {
    }
    
    public StockHolding(String stockSymbol, String stockName, Integer quantity, 
                       BigDecimal averagePrice, BigDecimal currentValue) {
        this.stockSymbol = stockSymbol;
        this.stockName = stockName;
        this.quantity = quantity;
        this.averagePrice = averagePrice;
        this.currentValue = currentValue;
    }
    
    // Getters and Setters
    public String getStockSymbol() {
        return stockSymbol;
    }
    
    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }
    
    public String getStockName() {
        return stockName;
    }
    
    public void setStockName(String stockName) {
        this.stockName = stockName;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public BigDecimal getAveragePrice() {
        return averagePrice;
    }
    
    public void setAveragePrice(BigDecimal averagePrice) {
        this.averagePrice = averagePrice;
    }
    
    public BigDecimal getCurrentValue() {
        return currentValue;
    }
    
    public void setCurrentValue(BigDecimal currentValue) {
        this.currentValue = currentValue;
    }
    
    @Override
    public String toString() {
        return "StockHolding{" +
                "stockSymbol='" + stockSymbol + '\'' +
                ", stockName='" + stockName + '\'' +
                ", quantity=" + quantity +
                ", averagePrice=" + averagePrice +
                ", currentValue=" + currentValue +
                '}';
    }
}
