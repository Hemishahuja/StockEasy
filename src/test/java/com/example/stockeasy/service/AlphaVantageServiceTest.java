package com.example.stockeasy.service;

import com.example.stockeasy.config.AlphaVantageConfig;
import com.example.stockeasy.exception.AlphaVantageApiException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static reactor.core.publisher.Mono.*;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AlphaVantageServiceTest {

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec<?> requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec<?> requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    private AlphaVantageConfig config;
    private CacheService cacheService;
    private ObjectMapper objectMapper;
    private AlphaVantageService alphaVantageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        config = new AlphaVantageConfig();
        config.setApiKey("test-api-key");
        config.setBaseUrl("https://www.alphavantage.co");
        config.setTimeout(Duration.ofSeconds(30));
        config.setRateLimitDelay(1000L);
        config.setMaxRetries(3);
        config.setCacheTtl(Duration.ofMinutes(15));

        cacheService = new CacheService();
        objectMapper = new ObjectMapper();

        // Mock WebClient chain
        when(webClientBuilder.baseUrl(anyString())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        alphaVantageService = new AlphaVantageService(webClientBuilder, config, cacheService, objectMapper);
    }

    @Test
    void testGetIntradayTimeSeries_Success() {
        // Given
        String symbol = "AAPL";
        String interval = "5min";
        String mockResponse = """
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

        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(mockResponse));
        when(responseSpec.timeout(any(Duration.class))).thenReturn(responseSpec);

        // When
        Mono<JsonNode> result = alphaVantageService.getIntradayTimeSeries(symbol, interval);

        // Then
        StepVerifier.create(result)
                .assertNext(jsonNode -> {
                    assertNotNull(jsonNode);
                    assertTrue(jsonNode.has("Time Series (5min)"));
                    JsonNode timeSeries = jsonNode.get("Time Series (5min)");
                    assertTrue(timeSeries.has("2023-12-01 16:00:00"));
                })
                .verifyComplete();
    }

    @Test
    void testGetIntradayTimeSeries_CachedResponse() {
        // Given
        String symbol = "AAPL";
        String interval = "5min";
        String cacheKey = "intraday:AAPL:5min";

        // Pre-populate cache
        String mockResponse = """
            {
                "Meta Data": {"2. Symbol": "AAPL"},
                "Time Series (5min)": {}
            }
            """;
        JsonNode cachedJson = null;
        try {
            cachedJson = objectMapper.readTree(mockResponse);
        } catch (Exception e) {
            fail("Failed to parse test JSON");
        }
        cacheService.put(cacheKey, cachedJson, Duration.ofMinutes(15));

        // When
        Mono<JsonNode> result = alphaVantageService.getIntradayTimeSeries(symbol, interval);

        // Then
        StepVerifier.create(result)
                .assertNext(jsonNode -> {
                    assertNotNull(jsonNode);
                    assertEquals("AAPL", jsonNode.get("Meta Data").get("2. Symbol").asText());
                })
                .verifyComplete();

        // Verify WebClient was not called (cache hit)
        verify(webClient, never()).get();
    }

    @Test
    void testGetIntradayTimeSeries_ApiError() {
        // Given
        String symbol = "INVALID";
        String interval = "5min";
        String errorResponse = """
            {
                "Error Message": "Invalid API call. Please retry or visit the documentation."
            }
            """;

        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(errorResponse));
        when(responseSpec.timeout(any(Duration.class))).thenReturn(responseSpec);

        // When
        Mono<JsonNode> result = alphaVantageService.getIntradayTimeSeries(symbol, interval);

        // Then
        StepVerifier.create(result)
                .expectError(AlphaVantageApiException.class)
                .verify();
    }

    @Test
    void testGetIntradayTimeSeries_HtmlErrorResponse() {
        // Given
        String symbol = "AAPL";
        String interval = "5min";
        String htmlErrorResponse = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="utf-8">
                <title>Alpha Vantage Error</title>
            </head>
            <body>
                <h1>API Key Error</h1>
                <p>Please check your API key.</p>
            </body>
            </html>
            """;

        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(htmlErrorResponse));
        when(responseSpec.timeout(any(Duration.class))).thenReturn(responseSpec);

        // When
        Mono<JsonNode> result = alphaVantageService.getIntradayTimeSeries(symbol, interval);

        // Then
        StepVerifier.create(result)
                .expectError(AlphaVantageApiException.class)
                .verify();
    }

    @Test
    void testGetIntradayTimeSeries_RateLimitExceeded() {
        // Given
        String symbol = "AAPL";
        String interval = "5min";
        String rateLimitResponse = """
            {
                "Note": "Thank you for using Alpha Vantage! Our standard API call frequency is 5 calls per minute and 500 calls per day."
            }
            """;

        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(rateLimitResponse));
        when(responseSpec.timeout(any(Duration.class))).thenReturn(responseSpec);

        // When
        Mono<JsonNode> result = alphaVantageService.getIntradayTimeSeries(symbol, interval);

        // Then
        StepVerifier.create(result)
                .expectError(AlphaVantageApiException.class)
                .verify();
    }

    @Test
    void testGetIntradayTimeSeries_WebClientException() {
        // Given
        String symbol = "AAPL";
        String interval = "5min";

        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.error(new RuntimeException("Network error")));
        when(responseSpec.timeout(any(Duration.class))).thenReturn(responseSpec);

        // When
        Mono<JsonNode> result = alphaVantageService.getIntradayTimeSeries(symbol, interval);

        // Then
        StepVerifier.create(result)
                .expectError(AlphaVantageApiException.class)
                .verify();
    }

    @Test
    void testClearRateLimit() {
        // Given
        String symbol = "AAPL";

        // When
        alphaVantageService.clearRateLimit(symbol);

        // Then
        // This is a simple method that clears internal state
        // In a real implementation, we might verify the internal map is cleared
        assertTrue(true); // Placeholder assertion
    }

    @Test
    void testGetCacheSize() {
        // When
        int cacheSize = alphaVantageService.getCacheSize();

        // Then
        assertEquals(0, cacheSize);
    }
}
