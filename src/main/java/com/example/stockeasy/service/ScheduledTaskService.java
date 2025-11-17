package com.example.stockeasy.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.stockeasy.domain.Stock;
import com.example.stockeasy.repo.StockRepository;

/**
 * Service for running scheduled background tasks, such as refreshing stock prices.
 */
@Service
public class ScheduledTaskService {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTaskService.class);

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private MarketDataService marketDataService;

    /**
     * Periodically refreshes the market data for all stocks in the database.
     * This job runs for the first time 10 seconds after application startup,
     * and then continues to run at the interval defined by `app.stock.market-data-refresh-interval`.
     */
    @Scheduled(initialDelay = 10000, fixedRateString = "${app.stock.market-data-refresh-interval}")
    public void refreshAllStockPrices() {
        logger.info("Starting scheduled job to refresh all stock prices...");
        List<Stock> stocks = stockRepository.findAll();

        for (Stock stock : stocks) {
            try {
                logger.debug("Scheduled refresh for symbol: {}", stock.getSymbol());
                marketDataService.getLatestMarketDataForSymbolSync(stock.getSymbol(), "5min");
            } catch (Exception e) {
                logger.error("Error during scheduled refresh for symbol {}: {}", stock.getSymbol(), e.getMessage());
            }
        }
        logger.info("Finished scheduled job to refresh all stock prices.");
    }
}