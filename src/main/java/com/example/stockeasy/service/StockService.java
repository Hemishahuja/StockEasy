package com.example.stockeasy.service;

import com.example.stockeasy.domain.Stock;
import com.example.stockeasy.repo.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * StockService for stock management operations.
 * Handles stock data management, price updates, and stock analysis.
 */
@Service
public class StockService {
    
    @Autowired
    private StockRepository stockRepository;
    
    public Stock createStock(String symbol, String companyName, String description, String sector, String industry) {
        Stock stock = new Stock();
        stock.setSymbol(symbol);
        stock.setCompanyName(companyName);
        stock.setDescription(description);
        stock.setSector(sector);
        stock.setIndustry(industry);
        stock.setCurrentPrice(BigDecimal.ZERO);
        stock.setActive(true);
        
        return stockRepository.save(stock);
    }
    
    public Stock updateStockPrice(Long stockId, BigDecimal newPrice) {
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new RuntimeException("Stock not found"));
        
        stock.updatePrice(newPrice);
        return stockRepository.save(stock);
    }
    
    public Stock getStockBySymbol(String symbol) {
        return stockRepository.findBySymbol(symbol)
                .orElseThrow(() -> new RuntimeException("Stock not found"));
    }
    
    public List<Stock> getStocksBySector(String sector) {
        return stockRepository.findBySector(sector);
    }
    
    public List<Stock> getStocksByIndustry(String industry) {
        return stockRepository.findByIndustry(industry);
    }
    
    public List<Stock> getActiveStocks() {
        return stockRepository.findByIsActiveTrue();
    }
    
    public List<Stock> getStocksInUserPortfolio(Long userId) {
        return stockRepository.findStocksInUserPortfolio(userId);
    }
    
    public List<Stock> getStocksInUserWatchlist(Long userId) {
        return stockRepository.findStocksInUserWatchlist(userId);
    }
    
    public List<Object[]> getStocksWithLatestMarketData() {
        return stockRepository.findStocksWithLatestMarketData();
    }
    
    public List<Stock> findStocksAbovePrice(BigDecimal priceThreshold) {
        return stockRepository.findByCurrentPriceGreaterThan(priceThreshold);
    }
    
    public List<Stock> findStocksBelowPrice(BigDecimal priceThreshold) {
        return stockRepository.findByCurrentPriceLessThan(priceThreshold);
    }
}
