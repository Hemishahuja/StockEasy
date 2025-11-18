package com.example.stockeasy.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.stockeasy.domain.MarketData;
import com.example.stockeasy.domain.Stock;
import com.example.stockeasy.exception.AlphaVantageApiException;
import com.example.stockeasy.repo.MarketDataRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * MarketDataService for market data management operations.
 * Handles market data updates, historical data management, and market analysis.
 * Integrates with Alpha Vantage API for real-time market data.
 */
@Service
public class MarketDataService {

    private static final Logger logger = LoggerFactory.getLogger(MarketDataService.class);
    private static final DateTimeFormatter ALPHA_VANTAGE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private MarketDataRepository marketDataRepository;

    @Autowired
    private StockService stockService;

    @Autowired
    private AlphaVantageService alphaVantageService;

    @Autowired
    private ObjectMapper objectMapper;
    
    @Value("${app.stock.default-fallback-price}")
    private BigDecimal defaultFallbackPrice;

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

    /**
     * Synchronous version: Fetches intraday market data from Alpha Vantage API.
     * Falls back to local data if API is unavailable.
     *
     * @param symbol the stock symbol
     * @param interval the time interval (1min, 5min, 15min, 30min, 60min)
     * @return List of MarketData entities
     */
    public List<MarketData> fetchIntradayDataFromApiSync(String symbol, String interval) {
        try {
            // First try to fetch fresh data from Alpha Vantage API
            List<MarketData> apiData = fetchFromAlphaVantageApiSync(symbol, interval);
            if (!apiData.isEmpty()) {
                logger.info("Fetched {} fresh data points from Alpha Vantage API for symbol: {}", apiData.size(), symbol);
                return apiData;
            }

            // Fall back to local data if API call was successful but returned no data
            logger.warn("API call failed or returned no data, falling back to local data for symbol: {}", symbol);
            return getLocalDataForSymbol(symbol);
        } catch (AlphaVantageApiException e) {
            logger.warn("Alpha Vantage API error for symbol {}, falling back to local data: {}", symbol, e.getMessage());
            return getLocalDataForSymbol(symbol);
        } catch (Exception e) {
            logger.error("Unexpected error fetching data for symbol {}: {}", symbol, e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Manually refreshes market data for a specific stock symbol.
     *
     * @param symbol the stock symbol
     * @param interval the time interval
     * @return List of MarketData entities
     */
    public List<MarketData> refreshMarketDataSync(String symbol, String interval) {
        logger.info("Manually refreshing market data for symbol: {}", symbol);
        List<MarketData> data = fetchIntradayDataFromApiSync(symbol, interval);
        if (!data.isEmpty()) {
            logger.info("Successfully refreshed {} data points for symbol: {}", data.size(), symbol);
        }
        return data;
    }

    /**
     * Gets the latest market data for a symbol from local database.
     * If no local data exists, attempts to fetch from Alpha Vantage API.
     * Falls back to stock's current price if no market data exists.
     *
     * @param symbol the stock symbol
     * @param interval the time interval
     * @return Latest MarketData entity with price data
     */
    public MarketData getLatestMarketDataForSymbolSync(String symbol, String interval) {
        try {
            logger.debug("Getting latest market data for symbol: {}", symbol);

            // Get the stock - this might throw an exception if stock doesn't exist
            Stock stock = stockService.getStockBySymbol(symbol);
            if (stock == null) {
                logger.warn("Stock not found for symbol: {}", symbol);
                throw new RuntimeException("Stock not found: " + symbol);
            }

            logger.debug("Found stock: {} with ID: {}", stock.getSymbol(), stock.getId());

            // Try to get latest market data from database first
            MarketData marketData = marketDataRepository.findLatestMarketDataByStockId(stock.getId());
            if (marketData != null) {
                logger.debug("Found existing market data for {}: ${}", symbol, marketData.getClosePrice());
                return marketData;
            }

            // No local data exists, try to fetch from Alpha Vantage API
            logger.debug("No local market data found for {}, attempting to fetch from Alpha Vantage API", symbol);
            try {
                List<MarketData> apiData = fetchFromAlphaVantageApiSync(symbol, interval);
                if (!apiData.isEmpty()) {
                    // Return the most recent data point from API
                    MarketData latestFromApi = apiData.get(0); // Assuming data is ordered by date descending
                    logger.info("Fetched latest market data from API for {}: ${}", symbol, latestFromApi.getClosePrice());
                    return latestFromApi;
                }
            } catch (AlphaVantageApiException e) {
                logger.warn("Failed to fetch market data from Alpha Vantage API for {}: {}", symbol, e.getMessage());
                // Continue to fallback logic
            }

            // If no market data exists anywhere, use the stock's current price instead of fallback
            logger.warn("No live or historical data found for {}. Using stock's current price instead of fallback.", symbol);
            BigDecimal currentStockPrice = stock.getCurrentPrice();
            
            // Only use fallback if stock's current price is zero or null (indicates no proper initialization)
            BigDecimal effectivePrice = (currentStockPrice != null && currentStockPrice.compareTo(BigDecimal.ZERO) > 0) 
                ? currentStockPrice 
                : defaultFallbackPrice;

            MarketData syntheticData = new MarketData();
            syntheticData.setStock(stock);
            syntheticData.setDate(LocalDateTime.now());
            syntheticData.setClosePrice(effectivePrice);
            syntheticData.setOpenPrice(effectivePrice);
            syntheticData.setHighPrice(effectivePrice);
            syntheticData.setLowPrice(effectivePrice);
            syntheticData.setVolume(0L);
            syntheticData.setStale(true); // Mark this data as stale/synthetic

            // Save the synthetic data to update the stock's current price
            MarketData savedData = marketDataRepository.save(syntheticData);
            logger.info("Created and saved synthetic market data for {}: ${} (using {} price)", 
                symbol, effectivePrice, currentStockPrice != null && currentStockPrice.compareTo(BigDecimal.ZERO) > 0 ? "stock's current" : "fallback");
            return savedData;

        } catch (Exception e) {
            logger.error("Failed to get latest data for symbol {}: {}", symbol, e.getMessage(), e);
            // Propagate a runtime exception to let the caller handle the failure.
            throw new RuntimeException("Failed to get latest market data for symbol " + symbol, e);
        }
    }

    /**
     * Clears cache for a specific symbol.
     *
     * @param symbol the stock symbol
     */
    public void clearCacheForSymbol(String symbol) {
        // This would need to be implemented in CacheService if we want symbol-specific cache clearing
        logger.info("Cache clearing requested for symbol: {}", symbol);
    }

    /**
     * Parses Alpha Vantage JSON response and converts to MarketData entities.
     *
     * @param jsonResponse the JSON response from Alpha Vantage API
     * @param stock the stock entity
     * @param interval the time interval
     * @return List of MarketData entities
     */
    private List<MarketData> parseAlphaVantageResponse(JsonNode jsonResponse, Stock stock, String interval) {
        List<MarketData> marketDataList = new ArrayList<>();

        try {
            // Get the time series data - the key format is "Time Series (Xmin)"
            String timeSeriesKey = "Time Series (" + interval + ")";
            JsonNode timeSeries = jsonResponse.get(timeSeriesKey);

            if (timeSeries == null) {
                logger.warn("No time series data found for interval: {}", interval);
                return marketDataList;
            }

            // Iterate through each timestamp
            Iterator<Map.Entry<String, JsonNode>> timestamps = timeSeries.fields();
            while (timestamps.hasNext()) {
                Map.Entry<String, JsonNode> timestampEntry = timestamps.next();
                String timestampStr = timestampEntry.getKey();
                JsonNode priceData = timestampEntry.getValue();

                try {
                    // Parse the timestamp
                    LocalDateTime dateTime = LocalDateTime.parse(timestampStr, ALPHA_VANTAGE_DATE_FORMAT);

                    // Extract OHLCV data
                    BigDecimal openPrice = new BigDecimal(priceData.get("1. open").asText());
                    BigDecimal highPrice = new BigDecimal(priceData.get("2. high").asText());
                    BigDecimal lowPrice = new BigDecimal(priceData.get("3. low").asText());
                    BigDecimal closePrice = new BigDecimal(priceData.get("4. close").asText());
                    Long volume = Long.parseLong(priceData.get("5. volume").asText());

                    // Create MarketData entity
                    MarketData marketData = new MarketData();
                    marketData.setStock(stock);
                    marketData.setDate(dateTime);
                    marketData.setOpenPrice(openPrice);
                    marketData.setHighPrice(highPrice);
                    marketData.setLowPrice(lowPrice);
                    marketData.setClosePrice(closePrice);
                    marketData.setVolume(volume);

                    marketDataList.add(marketData);

                } catch (Exception e) {
                    logger.warn("Failed to parse data point for timestamp {}: {}", timestampStr, e.getMessage());
                    // Continue with next data point
                }
            }

            logger.info("Parsed {} data points for symbol: {}", marketDataList.size(), stock.getSymbol());

        } catch (Exception e) {
            logger.error("Failed to parse Alpha Vantage response: {}", e.getMessage());
        }

        return marketDataList;
    }

    /**
     * Fetches intraday data from Alpha Vantage API synchronously.
     *
     * @param symbol the stock symbol
     * @param interval the time interval
     * @return List of MarketData entities
     */
    private List<MarketData> fetchFromAlphaVantageApiSync(String symbol, String interval) {
        try {
            logger.debug("Fetching intraday data from Alpha Vantage for symbol: {} with interval: {}", symbol, interval);

            // Get the reactive response and block to make it synchronous
            JsonNode jsonResponse = alphaVantageService.getIntradayTimeSeries(symbol, interval).block();

            if (jsonResponse == null) {
                logger.warn("Received null response from Alpha Vantage API for symbol: {}", symbol);
                return new ArrayList<>();
            }

            // Get the stock entity
            Stock stock = stockService.getStockBySymbol(symbol);
            if (stock == null) {
                logger.warn("Stock not found for symbol: {}", symbol);
                return new ArrayList<>();
            }

            // Parse the response and convert to MarketData entities
            List<MarketData> marketDataList = parseAlphaVantageResponse(jsonResponse, stock, interval);

            // Save to database
            if (!marketDataList.isEmpty()) {
                List<MarketData> savedData = marketDataRepository.saveAll(marketDataList);
                logger.info("Saved {} market data points to database for symbol: {}", savedData.size(), symbol);

                // Ensure the stock's current price is updated with the latest fetched price
                MarketData latestData = savedData.get(0); // Assuming list is sorted descending by date
                stockService.updateStockPrice(stock.getId(), latestData.getClosePrice());

                return savedData;
            }

            return marketDataList;

        } catch (AlphaVantageApiException e) {
            logger.error("Alpha Vantage API error for symbol {}: {}", symbol, e.getMessage());
            throw e; // Re-throw API exceptions
        } catch (Exception e) {
            logger.error("Unexpected error fetching data from Alpha Vantage for symbol {}: {}", symbol, e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Private helper to fetch local data for a symbol.
     *
     * @param symbol the stock symbol
     * @return List of MarketData, or empty list if not found.
     */
    private List<MarketData> getLocalDataForSymbol(String symbol) {
        try {
            Stock stock = stockService.getStockBySymbol(symbol);
            if (stock != null) {
                List<MarketData> localData = marketDataRepository.findByStockId(stock.getId());
                logger.info("Returning {} local data points for symbol: {}", localData.size(), symbol);
                return localData;
            }
        } catch (Exception e) {
            logger.error("Failed to get local data for symbol {}: {}", symbol, e.getMessage());
        }
        return new ArrayList<>();
    }
}
