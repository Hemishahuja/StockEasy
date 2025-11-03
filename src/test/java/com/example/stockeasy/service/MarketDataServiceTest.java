package com.example.stockeasy.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.example.stockeasy.domain.MarketData;
import com.example.stockeasy.domain.Stock;
import com.example.stockeasy.exception.AlphaVantageApiException;
import com.example.stockeasy.repo.MarketDataRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;

class MarketDataServiceTest {

    @Mock
    private MarketDataRepository marketDataRepository;

    @Mock
    private StockService stockService;

    @Mock
    private AlphaVantageService alphaVantageService;

    @Mock
    private CacheService cacheService;

    @InjectMocks
    private MarketDataService marketDataService;

    private Stock testStock;
    private MarketData testMarketData;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testStock = new Stock("AAPL", "Apple Inc.", BigDecimal.valueOf(270.37));
        testStock.setId(1L);

        testMarketData = new MarketData();
        testMarketData.setId(1L);
        testMarketData.setStock(testStock);
        testMarketData.setDate(LocalDateTime.now());
        testMarketData.setOpenPrice(BigDecimal.valueOf(150.00));
        testMarketData.setHighPrice(BigDecimal.valueOf(155.00));
        testMarketData.setLowPrice(BigDecimal.valueOf(148.00));
        testMarketData.setClosePrice(BigDecimal.valueOf(152.00));
        testMarketData.setVolume(1000000L);
    }

    @Test
    void testCreateMarketData() {
        // Given
        when(marketDataRepository.save(any(MarketData.class))).thenReturn(testMarketData);

        // When
        MarketData result = marketDataService.createMarketData(1L, LocalDateTime.now(),
                BigDecimal.valueOf(150.00), BigDecimal.valueOf(155.00),
                BigDecimal.valueOf(148.00), BigDecimal.valueOf(152.00), 1000000L);

        // Then
        assertNotNull(result);
        verify(marketDataRepository).save(any(MarketData.class));
    }

    @Test
    void testUpdateMarketData() {
        // Given
        BigDecimal newPrice = BigDecimal.valueOf(160.00);
        when(marketDataRepository.findById(1L)).thenReturn(Optional.of(testMarketData));
        when(marketDataRepository.save(any(MarketData.class))).thenReturn(testMarketData);

        // When
        MarketData result = marketDataService.updateMarketData(1L, newPrice);

        // Then
        assertNotNull(result);
        assertEquals(newPrice, result.getClosePrice());
        verify(marketDataRepository).findById(1L);
        verify(marketDataRepository).save(testMarketData);
    }

    @Test
    void testUpdateMarketData_NotFound() {
        // Given
        when(marketDataRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            marketDataService.updateMarketData(1L, BigDecimal.valueOf(160.00)));
        assertEquals("Market data not found", exception.getMessage());
    }

    @Test
    void testGetMarketDataForStock() {
        // Given
        List<MarketData> marketDataList = Arrays.asList(testMarketData);
        when(marketDataRepository.findByStockId(1L)).thenReturn(marketDataList);

        // When
        List<MarketData> result = marketDataService.getMarketDataForStock(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testMarketData, result.get(0));
        verify(marketDataRepository).findByStockId(1L);
    }

    @Test
    void testGetLatestMarketDataForStock() {
        // Given
        when(marketDataRepository.findLatestMarketDataByStockId(1L)).thenReturn(testMarketData);

        // When
        MarketData result = marketDataService.getLatestMarketDataForStock(1L);

        // Then
        assertNotNull(result);
        assertEquals(testMarketData, result);
        verify(marketDataRepository).findLatestMarketDataByStockId(1L);
    }

    @Test
    void testGetMarketDataForStockInDateRange() {
        // Given
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();
        List<MarketData> marketDataList = Arrays.asList(testMarketData);

        when(marketDataRepository.findByStockIdAndDateBetween(1L, start, end)).thenReturn(marketDataList);

        // When
        List<MarketData> result = marketDataService.getMarketDataForStockInDateRange(1L, start, end);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(marketDataRepository).findByStockIdAndDateBetween(1L, start, end);
    }

    @Test
    void testGetLatestMarketDataForAllStocks() {
        // Given
        List<MarketData> marketDataList = Arrays.asList(testMarketData);
        when(marketDataRepository.findLatestMarketDataForAllStocks()).thenReturn(marketDataList);

        // When
        List<MarketData> result = marketDataService.getLatestMarketDataForAllStocks();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(marketDataRepository).findLatestMarketDataForAllStocks();
    }

    @Test
    void testGetMarketDataForStockOrderedByDateAsc() {
        // Given
        List<MarketData> marketDataList = Arrays.asList(testMarketData);
        when(marketDataRepository.findMarketDataByStockIdOrderByDateAsc(1L)).thenReturn(marketDataList);

        // When
        List<MarketData> result = marketDataService.getMarketDataForStockOrderedByDateAsc(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(marketDataRepository).findMarketDataByStockIdOrderByDateAsc(1L);
    }

    @Test
    void testClearCacheForSymbol() {
        // Given
        String symbol = "AAPL";

        // When
        marketDataService.clearCacheForSymbol(symbol);

        // Then
        // This method currently just logs, so we verify it doesn't throw an exception
        assertTrue(true);
    }

    @Test
    void testFetchIntradayDataFromApiSync_Success() throws Exception {
        // Given
        String symbol = "AAPL";
        String interval = "5min";

        // Mock Alpha Vantage API response
        String jsonResponse = """
            {
                "Meta Data": {
                    "1. Information": "Intraday (5min) open, high, low, close prices and volume",
                    "2. Symbol": "AAPL",
                    "3. Last Refreshed": "2023-12-01 16:00:00",
                    "4. Interval": "5min",
                    "5. Output Size": "Compact",
                    "6. Time Zone": "US/Eastern"
                },
                "Time Series (5min)": {
                    "2023-12-01 16:00:00": {
                        "1. open": "190.00",
                        "2. high": "191.00",
                        "3. low": "189.50",
                        "4. close": "190.50",
                        "5. volume": "1000000"
                    }
                }
            }
            """;

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(jsonResponse);

        when(stockService.getStockBySymbol(symbol)).thenReturn(testStock);
        when(alphaVantageService.getIntradayTimeSeries(symbol, interval)).thenReturn(Mono.just(jsonNode));
        when(marketDataRepository.saveAll(any())).thenReturn(Arrays.asList(testMarketData));

        // When
        List<MarketData> result = marketDataService.fetchIntradayDataFromApiSync(symbol, interval);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(alphaVantageService).getIntradayTimeSeries(symbol, interval);
        verify(marketDataRepository).saveAll(any());
    }

    @Test
    void testFetchIntradayDataFromApiSync_ApiError_FallbackToLocal() {
        // Given
        String symbol = "AAPL";
        String interval = "5min";
        List<MarketData> localData = Arrays.asList(testMarketData);

        when(stockService.getStockBySymbol(symbol)).thenReturn(testStock);
        when(alphaVantageService.getIntradayTimeSeries(symbol, interval))
                .thenReturn(Mono.error(new AlphaVantageApiException("API Error", symbol)));
        when(marketDataRepository.findByStockId(1L)).thenReturn(localData);

        // When
        List<MarketData> result = marketDataService.fetchIntradayDataFromApiSync(symbol, interval);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(localData, result);
        verify(alphaVantageService).getIntradayTimeSeries(symbol, interval);
        verify(marketDataRepository).findByStockId(1L);
    }

    @Test
    void testGetLatestMarketDataForSymbolSync_FromDatabase() {
        // Given
        String symbol = "AAPL";
        String interval = "5min";

        when(stockService.getStockBySymbol(symbol)).thenReturn(testStock);
        when(marketDataRepository.findLatestMarketDataByStockId(1L)).thenReturn(testMarketData);

        // When
        MarketData result = marketDataService.getLatestMarketDataForSymbolSync(symbol, interval);

        // Then
        assertNotNull(result);
        assertEquals(testMarketData, result);
        verify(marketDataRepository).findLatestMarketDataByStockId(1L);
        verify(alphaVantageService, never()).getIntradayTimeSeries(anyString(), anyString());
    }

    @Test
    void testGetLatestMarketDataForSymbolSync_FromApi() throws Exception {
        // Given
        String symbol = "AAPL";
        String interval = "5min";

        // Mock Alpha Vantage API response
        String jsonResponse = """
            {
                "Meta Data": {"2. Symbol": "AAPL"},
                "Time Series (5min)": {
                    "2023-12-01 16:00:00": {
                        "1. open": "190.00",
                        "2. high": "191.00",
                        "3. low": "189.50",
                        "4. close": "190.50",
                        "5. volume": "1000000"
                    }
                }
            }
            """;

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(jsonResponse);

        when(stockService.getStockBySymbol(symbol)).thenReturn(testStock);
        when(marketDataRepository.findLatestMarketDataByStockId(1L)).thenReturn(null); // No local data
        when(alphaVantageService.getIntradayTimeSeries(symbol, interval)).thenReturn(Mono.just(jsonNode));
        when(marketDataRepository.saveAll(any())).thenReturn(Arrays.asList(testMarketData));

        // When
        MarketData result = marketDataService.getLatestMarketDataForSymbolSync(symbol, interval);

        // Then
        assertNotNull(result);
        verify(alphaVantageService).getIntradayTimeSeries(symbol, interval);
        verify(marketDataRepository).saveAll(any());
    }

    @Test
    void testGetLatestMarketDataForSymbolSync_SyntheticDataSaved() {
        // Given
        String symbol = "AAPL";
        String interval = "5min";

        when(stockService.getStockBySymbol(symbol)).thenReturn(testStock);
        when(marketDataRepository.findLatestMarketDataByStockId(1L)).thenReturn(null); // No local data
        when(alphaVantageService.getIntradayTimeSeries(symbol, interval))
                .thenReturn(Mono.error(new AlphaVantageApiException("API Error", symbol)));
        when(marketDataRepository.save(any(MarketData.class))).thenReturn(testMarketData);

        // When
        MarketData result = marketDataService.getLatestMarketDataForSymbolSync(symbol, interval);

        // Then
        assertNotNull(result);
        assertEquals(testMarketData, result);
        verify(alphaVantageService).getIntradayTimeSeries(symbol, interval);
        verify(marketDataRepository).save(any(MarketData.class)); // Verify synthetic data was saved
    }

    @Test
    void testGetLatestMarketDataForSymbolSync_StockNotFound() {
        // Given
        String symbol = "INVALID";
        String interval = "5min";

        when(stockService.getStockBySymbol(symbol)).thenReturn(null);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            marketDataService.getLatestMarketDataForSymbolSync(symbol, interval));
        assertEquals("Stock not found: INVALID", exception.getMessage());
    }

    @Test
    void testRefreshMarketDataSync() {
        // Given
        String symbol = "AAPL";
        String interval = "5min";
        List<MarketData> expectedData = Arrays.asList(testMarketData);

        // Mock the fetchIntradayDataFromApiSync method behavior
        when(stockService.getStockBySymbol(symbol)).thenReturn(testStock);
        when(marketDataRepository.findByStockId(1L)).thenReturn(expectedData);

        // When
        List<MarketData> result = marketDataService.refreshMarketDataSync(symbol, interval);

        // Then
        assertNotNull(result);
        assertEquals(expectedData, result);
    }
}
