package com.example.stockeasy.repo;

import com.example.stockeasy.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * StockRepository interface for Stock entity persistence operations.
 * Provides CRUD operations and custom queries for Stock management.
 */
@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    
    /**
     * Find stock by symbol (for trading operations)
     */
    Optional<Stock> findBySymbol(String symbol);
    
    /**
     * Find stocks by sector for portfolio diversification analysis
     */
    List<Stock> findBySector(String sector);
    
    /**
     * Find stocks by industry for portfolio diversification analysis
     */
    List<Stock> findByIndustry(String industry);
    
    /**
     * Find active stocks for trading
     */
    List<Stock> findByIsActiveTrue();
    
    /**
     * Find stocks with price above threshold for investment analysis
     */
    List<Stock> findByCurrentPriceGreaterThan(BigDecimal priceThreshold);
    
    /**
     * Find stocks with price below threshold for value investing analysis
     */
    List<Stock> findByCurrentPriceLessThan(BigDecimal priceThreshold);
    
    /**
     * Get stocks with market data for dashboard display
     */
    @Query("SELECT s FROM Stock s LEFT JOIN FETCH s.marketData md WHERE s.id = md.stock.id ORDER BY md.date DESC")
    List<Object[]> findStocksWithLatestMarketData();
    
    /**
     * Find stocks in user's portfolio by user ID
     */
    @Query("SELECT DISTINCT s FROM Stock s JOIN Portfolio p ON s.id = p.stock.id WHERE p.user.id = :userId")
    List<Stock> findStocksInUserPortfolio(Long userId);
    
    /**
     * Find stocks in user's watchlist by user ID
     */
    @Query("SELECT DISTINCT s FROM Stock s JOIN Watchlist w ON s.id = w.stock.id WHERE w.user.id = :userId")
    List<Stock> findStocksInUserWatchlist(Long userId);
}
