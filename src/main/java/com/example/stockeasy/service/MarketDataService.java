package com.example.stockeasy.service;

import com.example.stockeasy.domain.MarketData;
import com.example.stockeasy.repo.MarketDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * MarketDataService for market data management operations.
 * Handles market data updates, historical data management, and market analysis.
 */
@Service
public class MarketDataService {
    
    @Autowired
    private MarketDataRepository marketDataRepository;
    
    public MarketData createMarketData(Long stockId, LocalDateTime date, BigDecimal openPrice, BigDecimal highPrice, BigDecimal lowPrice, BigDecimal closePrice, Long volume) {
        MarketData marketData = new MarketData();
        // In a real implementation, you would get stock from repository
        // For now, we'll set stock directly
        marketData.setDate(date);
        marketData.setOpenPrice(openPrice);
        marketData.setHighPrice(highPrice);
        marketData.setLowPrice(lowPrice);
        marketData.setClosePrice(closePrice);
        marketData.setVolume(volume);
        
        return marketDataRepository.save(marketData);
    }
    
    public MarketData updateMarketData(Long marketDataId, BigDecimal newPrice) {
        MarketData marketData = marketDataRepository.findById(marketDataId)
                .orElseThrow(() -> new RuntimeException("Market data not found"));
        
        marketData.setClosePrice(newPrice);
        return marketDataRepository.save(marketData);
    }
    
    public List<MarketData> getMarketDataForStock(Long stockId) {
        return marketDataRepository.findByStockId(stockId);
    }
    
    public MarketData getLatestMarketDataForStock(Long stockId) {
        return marketDataRepository.findLatestMarketDataByStockId(stockId);
    }
    
    public List<MarketData> getMarketDataForStockInDateRange(Long stockId, LocalDateTime start, LocalDateTime end) {
        return marketDataRepository.findByStockIdAndDateBetween(stockId, start, end);
    }
    
    public List<MarketData> getLatestMarketDataForAllStocks() {
        return marketDataRepository.findLatestMarketDataForAllStocks();
    }
    
    public List<MarketData> getMarketDataForStockOrderedByDateAsc(Long stockId) {
        return marketDataRepository.findMarketDataByStockIdOrderByDateAsc(stockId);
    }
}
