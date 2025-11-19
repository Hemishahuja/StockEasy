package com.example.stockeasy.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.stockeasy.domain.MarketData;
import com.example.stockeasy.domain.Stock;
import com.example.stockeasy.repo.MarketDataRepository;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * MarketDataService for market data management operations.
 * Handles market data updates, historical data management, and market analysis.
 * Integrates with Finnhub API for real-time market data.
 */
@Service
public class MarketDataService {

    private static final Logger logger = LoggerFactory.getLogger(MarketDataService.class);

    @Autowired
    private MarketDataRepository marketDataRepository;

    @Autowired
    private StockService stockService;

    @Autowired
    private FinnhubService finnhubService;

    @Value("${app.stock.default-fallback-price}")
    private BigDecimal defaultFallbackPrice;

    public MarketData createMarketData(Long stockId, LocalDateTime date, BigDecimal openPrice, BigDecimal highPrice,
            BigDecimal lowPrice, BigDecimal closePrice, Long volume) {
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
     * Synchronous version: Fetches intraday market data from Finnhub API.
     * Falls back to local data if API is unavailable.
     *
     * @param symbol   the stock symbol
     * @param interval the time interval (1min, 5min, 15min, 30min, 60min)
     * @return List of MarketData entities
     */
    public List<MarketData> fetchIntradayDataFromApiSync(String symbol, String interval) {
        try {
            // First try to fetch fresh data from Finnhub API
            List<MarketData> apiData = fetchFromFinnhubApiSync(symbol, interval);
            if (!apiData.isEmpty()) {
                logger.info("Fetched {} fresh data points from Finnhub API for symbol: {}", apiData.size(), symbol);
                return apiData;
            }

            // Fall back to local data if API call was successful but returned no data
            logger.warn("API call failed or returned no data, falling back to local data for symbol: {}", symbol);
            return getLocalDataForSymbol(symbol);
        } catch (RuntimeException e) {
            logger.warn("Finnhub API error for symbol {}, falling back to local data: {}", symbol, e.getMessage());
            return getLocalDataForSymbol(symbol);
        } catch (Exception e) {
            logger.error("Unexpected error fetching data for symbol {}: {}", symbol, e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Manually refreshes market data for a specific stock symbol.
     *
     * @param symbol   the stock symbol
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
     * If no local data exists, attempts to fetch from Finnhub API.
     * Falls back to stock's current price if no market data exists.
     *
     * @param symbol   the stock symbol
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

            // No local data exists, try to fetch from Finnhub API
            logger.debug("No local market data found for {}, attempting to fetch from Finnhub API", symbol);
            try {
                // For latest data, we can use the Quote endpoint which is lighter
                MarketData latestFromApi = fetchLatestQuoteFromFinnhubSync(symbol);
                if (latestFromApi != null) {
                    logger.info("Fetched latest market data from API for {}: ${}", symbol,
                            latestFromApi.getClosePrice());
                    return latestFromApi;
                }
            } catch (RuntimeException e) {
                logger.warn("Failed to fetch market data from Finnhub API for {}: {}", symbol, e.getMessage());
                // Continue to fallback logic
            }

            // If no market data exists anywhere, use the stock's current price instead of
            // fallback
            logger.warn("No live or historical data found for {}. Using stock's current price instead of fallback.",
                    symbol);
            BigDecimal currentStockPrice = stock.getCurrentPrice();

            // Only use fallback if stock's current price is zero or null (indicates no
            // proper initialization)
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
                    symbol, effectivePrice,
                    currentStockPrice != null && currentStockPrice.compareTo(BigDecimal.ZERO) > 0 ? "stock's current"
                            : "fallback");
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
        // This would need to be implemented in CacheService if we want symbol-specific
        // cache clearing
        logger.info("Cache clearing requested for symbol: {}", symbol);
    }

    /**
     * Parses Finnhub Candle JSON response and converts to MarketData entities.
     *
     * @param jsonResponse the JSON response from Finnhub API
     * @param stock        the stock entity
     * @return List of MarketData entities
     */
    private List<MarketData> parseFinnhubCandlesResponse(JsonNode jsonResponse, Stock stock) {
        List<MarketData> marketDataList = new ArrayList<>();

        try {
            if (!jsonResponse.has("s") || !"ok".equals(jsonResponse.get("s").asText())) {
                logger.warn("Finnhub returned no data status: {}",
                        jsonResponse.has("s") ? jsonResponse.get("s").asText() : "unknown");
                return marketDataList;
            }

            JsonNode timestamps = jsonResponse.get("t");
            JsonNode opens = jsonResponse.get("o");
            JsonNode highs = jsonResponse.get("h");
            JsonNode lows = jsonResponse.get("l");
            JsonNode closes = jsonResponse.get("c");
            JsonNode volumes = jsonResponse.get("v");

            if (timestamps == null || !timestamps.isArray()) {
                return marketDataList;
            }

            for (int i = 0; i < timestamps.size(); i++) {
                try {
                    long unixSeconds = timestamps.get(i).asLong();
                    LocalDateTime dateTime = LocalDateTime.ofEpochSecond(unixSeconds, 0, java.time.ZoneOffset.UTC);

                    BigDecimal openPrice = new BigDecimal(opens.get(i).asText());
                    BigDecimal highPrice = new BigDecimal(highs.get(i).asText());
                    BigDecimal lowPrice = new BigDecimal(lows.get(i).asText());
                    BigDecimal closePrice = new BigDecimal(closes.get(i).asText());
                    Long volume = volumes.get(i).asLong();

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
                    logger.warn("Failed to parse data point at index {}: {}", i, e.getMessage());
                }
            }

            // Sort descending by date (newest first)
            marketDataList.sort((a, b) -> b.getDate().compareTo(a.getDate()));

            logger.info("Parsed {} data points for symbol: {}", marketDataList.size(), stock.getSymbol());

        } catch (Exception e) {
            logger.error("Failed to parse Finnhub response: {}", e.getMessage());
        }

        return marketDataList;
    }

    /**
     * Fetches intraday data from Finnhub API synchronously.
     *
     * @param symbol   the stock symbol
     * @param interval the time interval
     * @return List of MarketData entities
     */
    private List<MarketData> fetchFromFinnhubApiSync(String symbol, String interval) {
        try {
            logger.debug("Fetching intraday data from Finnhub for symbol: {} with interval: {}", symbol, interval);

            // Map interval to Finnhub resolution
            String resolution = "60"; // Default to 60 minutes
            if (interval.contains("1min"))
                resolution = "1";
            else if (interval.contains("5min"))
                resolution = "5";
            else if (interval.contains("15min"))
                resolution = "15";
            else if (interval.contains("30min"))
                resolution = "30";
            else if (interval.contains("60min"))
                resolution = "60";
            else if (interval.contains("D"))
                resolution = "D";

            // Fetch last 24 hours of data
            long to = System.currentTimeMillis() / 1000;
            long from = to - (24 * 60 * 60); // 24 hours ago

            // Get the reactive response and block to make it synchronous
            JsonNode jsonResponse = finnhubService.getStockCandles(symbol, resolution, from, to).block();

            if (jsonResponse == null) {
                logger.warn("Received null response from Finnhub API for symbol: {}", symbol);
                return new ArrayList<>();
            }

            // Get the stock entity
            Stock stock = stockService.getStockBySymbol(symbol);
            if (stock == null) {
                logger.warn("Stock not found for symbol: {}", symbol);
                return new ArrayList<>();
            }

            // Parse the response and convert to MarketData entities
            List<MarketData> marketDataList = parseFinnhubCandlesResponse(jsonResponse, stock);

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

        } catch (RuntimeException e) {
            logger.error("Finnhub API error for symbol {}: {}", symbol, e.getMessage());
            throw e; // Re-throw API exceptions
        } catch (Exception e) {
            logger.error("Unexpected error fetching data from Finnhub for symbol {}: {}", symbol, e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Fetches latest quote from Finnhub API synchronously.
     */
    private MarketData fetchLatestQuoteFromFinnhubSync(String symbol) {
        try {
            JsonNode jsonResponse = finnhubService.getQuote(symbol).block();

            if (jsonResponse == null)
                return null;

            Stock stock = stockService.getStockBySymbol(symbol);
            if (stock == null)
                return null;

            BigDecimal currentPrice = new BigDecimal(jsonResponse.get("c").asText());
            BigDecimal highPrice = new BigDecimal(jsonResponse.get("h").asText());
            BigDecimal lowPrice = new BigDecimal(jsonResponse.get("l").asText());
            BigDecimal openPrice = new BigDecimal(jsonResponse.get("o").asText());
            long timestamp = jsonResponse.get("t").asLong();

            MarketData marketData = new MarketData();
            marketData.setStock(stock);
            marketData.setDate(LocalDateTime.ofEpochSecond(timestamp, 0, java.time.ZoneOffset.UTC));
            marketData.setClosePrice(currentPrice);
            marketData.setOpenPrice(openPrice);
            marketData.setHighPrice(highPrice);
            marketData.setLowPrice(lowPrice);
            marketData.setVolume(0L); // Quote endpoint doesn't always return volume

            // Save and update stock price
            MarketData savedData = marketDataRepository.save(marketData);
            stockService.updateStockPrice(stock.getId(), currentPrice);

            return savedData;

        } catch (Exception e) {
            logger.error("Failed to fetch quote for {}: {}", symbol, e.getMessage());
            return null;
        }
    }

    /**
     * Fetches and updates company profile data (market cap, sector, etc.) for a
     * stock.
     */
    public void fetchCompanyProfileSync(String symbol) {
        try {
            JsonNode jsonResponse = finnhubService.getCompanyProfile2(symbol).block();
            if (jsonResponse == null)
                return;

            Stock stock = stockService.getStockBySymbol(symbol);
            if (stock == null)
                return;

            if (jsonResponse.has("marketCapitalization")) {
                // Finnhub returns market cap in millions
                BigDecimal marketCapMillions = new BigDecimal(jsonResponse.get("marketCapitalization").asText());
                stock.setMarketCap(marketCapMillions.multiply(BigDecimal.valueOf(1_000_000)));
            }
            if (jsonResponse.has("finnhubIndustry")) {
                stock.setIndustry(jsonResponse.get("finnhubIndustry").asText());
            }

            stockService.saveStock(stock);
            logger.info("Updated company profile for {}", symbol);

        } catch (Exception e) {
            logger.error("Failed to fetch company profile for {}: {}", symbol, e.getMessage());
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
