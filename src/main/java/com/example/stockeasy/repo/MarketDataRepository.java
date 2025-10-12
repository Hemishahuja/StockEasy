package com.example.stockeasy.repo;

import com.example.stockeasy.domain.MarketData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * MarketDataRepository interface for MarketData entity persistence operations.
 * Provides CRUD operations and custom queries for MarketData management.
 */
@Repository
public interface MarketDataRepository extends JpaRepository<MarketData, Long> {
    
    /**
     * Find market data by stock ID
     */
    List<MarketData> findByStockId(Long stockId);
    
    /**
     * Get latest market data for each stock
     */
    @Query("SELECT md1 FROM MarketData md1 WHERE md1.date = (SELECT MAX(md2.date) FROM MarketData md2 WHERE md2.stock.id = md1.stock.id)")
    List<MarketData> findLatestMarketDataForAllStocks();
    
    /**
     * Get market data for a stock within date range
     */
    List<MarketData> findByStockIdAndDateBetween(Long stockId, LocalDateTime start, LocalDateTime end);
    
    /**
     * Get market data ordered by date for charting
     */
    @Query("SELECT md FROM MarketData md WHERE md.stock.id = :stockId ORDER BY md.date ASC")
    List<MarketData> findMarketDataByStockIdOrderByDateAsc(Long stockId);
    
    /**
     * Get market data for dashboard display
     */
    @Query("SELECT md FROM MarketData md WHERE md.stock.id = :stockId ORDER BY md.date DESC LIMIT 1")
    MarketData findLatestMarketDataByStockId(Long stockId);
}
