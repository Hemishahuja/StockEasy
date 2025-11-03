package com.example.stockeasy.service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.example.stockeasy.config.AlphaVantageConfig;
import com.example.stockeasy.exception.AlphaVantageApiException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;

/**
 * Service for communicating with Alpha Vantage API.
 */
@Service
public class AlphaVantageService {

    private static final Logger logger = LoggerFactory.getLogger(AlphaVantageService.class);

    private final WebClient webClient;
    private final AlphaVantageConfig config;
    private final CacheService cacheService;
    private final ObjectMapper objectMapper;

    // Rate limiting: track last API call time per symbol
    private final ConcurrentHashMap<String, Long> lastApiCallTimes = new ConcurrentHashMap<>();

    public AlphaVantageService(WebClient.Builder webClientBuilder, AlphaVantageConfig config,
                              CacheService cacheService, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder
                .baseUrl(config.getBaseUrl())
                .build();
        this.config = config;
        this.cacheService = cacheService;
        this.objectMapper = objectMapper;
    }

    /**
     * Fetches intraday time series data for a given symbol.
     *
     * @param symbol the stock symbol (e.g., "IBM")
     * @param interval the time interval (1min, 5min, 15min, 30min, 60min)
     * @return Mono containing the JSON response
     */
    public Mono<JsonNode> getIntradayTimeSeries(String symbol, String interval) {
        String cacheKey = "intraday:" + symbol + ":" + interval;

        // Check cache first
        Object cachedData = cacheService.get(cacheKey);
        if (cachedData != null) {
            logger.debug("Returning cached data for symbol: {}", symbol);
            return Mono.just((JsonNode) cachedData);
        }

        return makeApiCallWithRetry(symbol, interval, cacheKey, 0);
    }

    /**
     * Makes API call with retry logic and rate limiting.
     */
    private Mono<JsonNode> makeApiCallWithRetry(String symbol, String interval, String cacheKey, int attempt) {
        return enforceRateLimit(symbol)
                .then(makeApiCall(symbol, interval))
                .doOnNext(response -> {
                    // Cache successful response
                    cacheService.put(cacheKey, response, config.getCacheTtl());
                    logger.debug("Cached response for symbol: {}", symbol);
                })
                .onErrorResume(throwable -> {
                    logger.warn("API call failed for symbol {} (attempt {}): {}", symbol, attempt + 1, throwable.getMessage());

                    if (attempt < config.getMaxRetries() && isRetryableError(throwable)) {
                        logger.info("Retrying API call for symbol {} in {} ms", symbol, config.getRateLimitDelay());
                        return Mono.delay(Duration.ofMillis(config.getRateLimitDelay()))
                                .then(makeApiCallWithRetry(symbol, interval, cacheKey, attempt + 1));
                    } else {
                        return Mono.error(new AlphaVantageApiException(
                                "Failed to fetch data for symbol: " + symbol + " after " + (attempt + 1) + " attempts",
                                symbol, getStatusCode(throwable), throwable));
                    }
                });
    }

    /**
     * Makes the actual API call to Alpha Vantage.
     */
    private Mono<JsonNode> makeApiCall(String symbol, String interval) {
        Map<String, String> params = Map.of(
                "function", "TIME_SERIES_INTRADAY",
                "symbol", symbol,
                "interval", interval,
                "apikey", config.getApiKey(),
                "outputsize", "compact"
        );

        return webClient.get()
                .uri(uriBuilder -> {
                    params.forEach(uriBuilder::queryParam);
                    return uriBuilder.build();
                })
                .retrieve()
                .bodyToMono(String.class)
                .timeout(config.getTimeout())
                .flatMap(this::parseJsonResponse)
                .doOnNext(response -> {
                    // Update last API call time
                    lastApiCallTimes.put(symbol, System.currentTimeMillis());
                    logger.debug("Successfully fetched data for symbol: {}", symbol);
                });
    }

    /**
     * Enforces rate limiting by waiting if necessary.
     */
    private Mono<Void> enforceRateLimit(String symbol) {
        Long lastCallTime = lastApiCallTimes.get(symbol);
        if (lastCallTime != null) {
            long timeSinceLastCall = System.currentTimeMillis() - lastCallTime;
            long remainingDelay = config.getRateLimitDelay() - timeSinceLastCall;

            if (remainingDelay > 0) {
                logger.debug("Rate limiting: waiting {} ms for symbol {}", remainingDelay, symbol);
                return Mono.delay(Duration.ofMillis(remainingDelay)).then();
            }
        }
        return Mono.empty();
    }

    /**
     * Parses JSON response string to JsonNode.
     */
    private Mono<JsonNode> parseJsonResponse(String responseBody) {
        try {
            // Check if response is HTML (indicates an error page)
            if (responseBody.trim().startsWith("<!DOCTYPE html>") ||
                responseBody.trim().startsWith("<html")) {
                logger.warn("Received HTML error page from Alpha Vantage API instead of JSON");
                throw new AlphaVantageApiException("Alpha Vantage API returned HTML error page - likely invalid API key or demo limit exceeded");
            }

            JsonNode jsonNode = objectMapper.readTree(responseBody);

            // Check for Alpha Vantage error responses
            if (jsonNode.has("Error Message")) {
                String errorMessage = jsonNode.get("Error Message").asText();
                throw new AlphaVantageApiException("Alpha Vantage API error: " + errorMessage);
            }

            if (jsonNode.has("Note")) {
                String note = jsonNode.get("Note").asText();
                if (note.contains("call frequency")) {
                    throw new AlphaVantageApiException("Rate limit exceeded: " + note);
                }
            }

            return Mono.just(jsonNode);
        } catch (AlphaVantageApiException e) {
            // Re-throw our custom exceptions
            return Mono.error(e);
        } catch (Exception e) {
            logger.error("Failed to parse JSON response: {}", e.getMessage());
            return Mono.error(new AlphaVantageApiException("Failed to parse API response", e));
        }
    }

    /**
     * Determines if an error is retryable.
     */
    private boolean isRetryableError(Throwable throwable) {
        if (throwable instanceof WebClientResponseException) {
            WebClientResponseException responseException = (WebClientResponseException) throwable;
            int statusCode = responseException.getStatusCode().value();
            // Retry on server errors (5xx) and rate limiting (429)
            return statusCode >= 500 || statusCode == 429;
        }
        // Retry on network errors and timeouts
        return throwable instanceof WebClientException;
    }

    /**
     * Extracts status code from WebClient exceptions.
     */
    private int getStatusCode(Throwable throwable) {
        if (throwable instanceof WebClientResponseException) {
            return ((WebClientResponseException) throwable).getStatusCode().value();
        }
        return 0;
    }

    /**
     * Clears the rate limiting state for a symbol.
     */
    public void clearRateLimit(String symbol) {
        lastApiCallTimes.remove(symbol);
    }

    /**
     * Gets the current cache size.
     */
    public int getCacheSize() {
        return cacheService.size();
    }
}
