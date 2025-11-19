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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.example.stockeasy.domain.MarketData;
import com.example.stockeasy.domain.Stock;
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
    private FinnhubService finnhubService;

    @Mock
    private CacheService cacheService;

    @Mock
    private ObjectMapper objectMapper;

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
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> marketDataService.updateMarketData(1L, BigDecimal.valueOf(160.00)));
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

        // Mock Finnhub API response
        String jsonResponse = """
                {
                    "s": "ok",
                    "t": [1701446400],
                    "o": ["190.00"],
                    "h": ["191.00"],
                    "l": ["189.50"],
                    "c": ["190.50"],
                    "v": [1000000]
                }
                """;

        ObjectMapper realObjectMapper = new ObjectMapper();
        JsonNode jsonNode = realObjectMapper.readTree(jsonResponse);

        when(stockService.getStockBySymbol(symbol)).thenReturn(testStock);
        when(finnhubService.getStockCandles(anyString(), anyString(), anyLong(), anyLong()))
                .thenReturn(Mono.just(jsonNode));
        when(marketDataRepository.saveAll(any())).thenReturn(Arrays.asList(testMarketData));

        // When
        List<MarketData> result = marketDataService.fetchIntradayDataFromApiSync(symbol, interval);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(finnhubService).getStockCandles(anyString(), anyString(), anyLong(), anyLong());
        verify(marketDataRepository).saveAll(any());
    }

    @Test
    void testFetchIntradayDataFromApiSync_ApiError_FallbackToLocal() {
        // Given
        String symbol = "AAPL";
        String interval = "5min";
        List<MarketData> localData = Arrays.asList(testMarketData);

        when(stockService.getStockBySymbol(symbol)).thenReturn(testStock);
        when(finnhubService.getStockCandles(anyString(), anyString(), anyLong(), anyLong()))
                .thenReturn(Mono.error(new RuntimeException("API Error")));
        when(marketDataRepository.findByStockId(1L)).thenReturn(localData);

        // When
        List<MarketData> result = marketDataService.fetchIntradayDataFromApiSync(symbol, interval);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(localData, result);
        verify(finnhubService).getStockCandles(anyString(), anyString(), anyLong(), anyLong());
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
        verify(finnhubService, never()).getQuote(anyString());
    }

    @Test
    void testGetLatestMarketDataForSymbolSync_FromApi() throws Exception {
        // Given
        String symbol = "AAPL";
        String interval = "5min";

        // Mock Finnhub Quote API response
        String jsonResponse = """
                {
                    "c": 190.50,
                    "h": 191.00,
                    "l": 189.50,
                    "o": 190.00,
                    "pc": 189.00,
                    "t": 1701446400
                }
                """;

        ObjectMapper realObjectMapper = new ObjectMapper();
        JsonNode jsonNode = realObjectMapper.readTree(jsonResponse);

        when(stockService.getStockBySymbol(symbol)).thenReturn(testStock);
        when(marketDataRepository.findLatestMarketDataByStockId(1L)).thenReturn(null); // No local data
        when(finnhubService.getQuote(symbol)).thenReturn(Mono.just(jsonNode));
        when(marketDataRepository.save(any(MarketData.class))).thenReturn(testMarketData);

        // When
        MarketData result = marketDataService.getLatestMarketDataForSymbolSync(symbol, interval);

        // Then
        assertNotNull(result);
        verify(finnhubService).getQuote(symbol);
        verify(marketDataRepository).save(any(MarketData.class));
    }

    @Test
    void testGetLatestMarketDataForSymbolSync_SyntheticDataSaved() {
        // Given
        String symbol = "AAPL";
        String interval = "5min";

        when(stockService.getStockBySymbol(symbol)).thenReturn(testStock);
        when(marketDataRepository.findLatestMarketDataByStockId(1L)).thenReturn(null); // No local data
        when(finnhubService.getQuote(symbol))
                .thenReturn(Mono.error(new RuntimeException("API Error")));
        when(marketDataRepository.save(any(MarketData.class))).thenReturn(testMarketData);

        // When
        MarketData result = marketDataService.getLatestMarketDataForSymbolSync(symbol, interval);

        // Then
        assertNotNull(result);
        assertEquals(testMarketData, result);
        verify(finnhubService).getQuote(symbol);
        verify(marketDataRepository).save(any(MarketData.class)); // Verify synthetic data was saved
    }

    @Test
    void testGetLatestMarketDataForSymbolSync_StockNotFound() {
        // Given
        String symbol = "INVALID";
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
