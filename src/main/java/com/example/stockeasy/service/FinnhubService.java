package com.example.stockeasy.service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.example.stockeasy.config.FinnhubConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;

/**
 * Service for communicating with Finnhub API.
 */
@Service
public class FinnhubService {

    private static final Logger logger = LoggerFactory.getLogger(FinnhubService.class);

    private final WebClient webClient;
    private final FinnhubConfig config;
    private final CacheService cacheService;
    private final ObjectMapper objectMapper;

    // Global rate limiting: track the timestamp of the last API call.
    private final AtomicLong lastApiCallTime = new AtomicLong(0);

    public FinnhubService(WebClient.Builder webClientBuilder, FinnhubConfig config,
            CacheService cacheService, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder
                .baseUrl(config.getBaseUrl())
                .build();
        this.config = config;
        this.cacheService = cacheService;
        this.objectMapper = objectMapper;
    }

    /**
     * Fetches real-time quote data for a given symbol.
     *
     * @param symbol the stock symbol (e.g., "AAPL")
     * @return Mono containing the JSON response
     */
    public Mono<JsonNode> getQuote(String symbol) {
        String cacheKey = "quote:" + symbol;

        // Check cache first
        Object cachedData = cacheService.get(cacheKey);
        if (cachedData != null) {
            logger.debug("Returning cached quote data for symbol: {}", symbol);
            return Mono.just((JsonNode) cachedData);
        }

        return makeApiCallWithRetry("quote", symbol, null, cacheKey, 0);
    }

    /**
     * Fetches stock candles (historical/intraday data).
     *
     * @param symbol     the stock symbol
     * @param resolution the resolution (1, 5, 15, 30, 60, D, W, M)
     * @param from       start timestamp (UNIX)
     * @param to         end timestamp (UNIX)
     * @return Mono containing the JSON response
     */
    public Mono<JsonNode> getStockCandles(String symbol, String resolution, long from, long to) {
        String cacheKey = "candles:" + symbol + ":" + resolution + ":" + from + ":" + to;

        // Check cache first
        Object cachedData = cacheService.get(cacheKey);
        if (cachedData != null) {
            logger.debug("Returning cached candles data for symbol: {}", symbol);
            return Mono.just((JsonNode) cachedData);
        }

        Map<String, String> additionalParams = Map.of(
                "resolution", resolution,
                "from", String.valueOf(from),
                "to", String.valueOf(to));

        return makeApiCallWithRetry("stock/candle", symbol, additionalParams, cacheKey, 0);
    }

    /**
     * Fetches company profile data (including market cap).
     *
     * @param symbol the stock symbol
     * @return Mono containing the JSON response
     */
    public Mono<JsonNode> getCompanyProfile2(String symbol) {
        String cacheKey = "profile2:" + symbol;

        // Check cache first
        Object cachedData = cacheService.get(cacheKey);
        if (cachedData != null) {
            logger.debug("Returning cached profile data for symbol: {}", symbol);
            return Mono.just((JsonNode) cachedData);
        }

        return makeApiCallWithRetry("stock/profile2", symbol, null, cacheKey, 0);
    }

    /**
     * Makes API call with retry logic and rate limiting.
     */
    private Mono<JsonNode> makeApiCallWithRetry(String endpoint, String symbol, Map<String, String> additionalParams,
            String cacheKey, int attempt) {
        return enforceRateLimit()
                .then(makeApiCall(endpoint, symbol, additionalParams))
                .doOnNext(response -> {
                    // Cache successful response
                    cacheService.put(cacheKey, response, config.getCacheTtl());
                    logger.debug("Cached response for symbol: {}", symbol);
                })
                .onErrorResume(throwable -> {
                    logger.warn("API call failed for symbol {} (attempt {}): {}", symbol, attempt + 1,
                            throwable.getMessage());

                    if (attempt < config.getMaxRetries() && isRetryableError(throwable)) {
                        logger.info("Retrying API call for symbol {} in {} ms", symbol, config.getRateLimitDelay());
                        return Mono.delay(Duration.ofMillis(config.getRateLimitDelay()))
                                .then(makeApiCallWithRetry(endpoint, symbol, additionalParams, cacheKey, attempt + 1));
                    } else {
                        return Mono.error(new RuntimeException(
                                "Failed to fetch data for symbol: " + symbol + " after " + (attempt + 1) + " attempts",
                                throwable));
                    }
                });
    }

    /**
     * Makes the actual API call to Finnhub.
     */
    private Mono<JsonNode> makeApiCall(String endpoint, String symbol, Map<String, String> additionalParams) {
        return webClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/" + endpoint);
                    uriBuilder.queryParam("symbol", symbol);
                    uriBuilder.queryParam("token", config.getApiKey());

                    if (additionalParams != null) {
                        additionalParams.forEach(uriBuilder::queryParam);
                    }

                    return uriBuilder.build();
                })
                .retrieve()
                .bodyToMono(String.class)
                .timeout(config.getTimeout())
                .flatMap(this::parseJsonResponse)
                .doOnSuccess(response -> logger.debug("Successfully fetched data for symbol: {}", symbol));
    }

    /**
     * Enforces rate limiting by waiting if necessary.
     */
    private Mono<Void> enforceRateLimit() {
        long now = System.currentTimeMillis();
        long lastCall = lastApiCallTime.get();
        long timeSinceLastCall = now - lastCall;
        long delayMillis = config.getRateLimitDelay();

        if (timeSinceLastCall < delayMillis) {
            long waitTime = delayMillis - timeSinceLastCall;
            logger.debug("Global rate limiting: waiting {} ms before next API call.", waitTime);
            return Mono.delay(Duration.ofMillis(waitTime))
                    .then(Mono.fromRunnable(() -> lastApiCallTime.set(System.currentTimeMillis())));
        }

        lastApiCallTime.set(now);
        return Mono.empty();
    }

    /**
     * Parses JSON response string to JsonNode.
     */
    private Mono<JsonNode> parseJsonResponse(String responseBody) {
        // Log the raw response body for debugging purposes.
        logger.debug("Raw Finnhub API Response: {}", responseBody);

        try {
            JsonNode jsonNode = objectMapper.readTree(responseBody);

            // Check for error responses (Finnhub usually returns 403/429 status codes, but
            // sometimes JSON errors)
            if (jsonNode.has("error")) {
                throw new RuntimeException("Finnhub API error: " + jsonNode.get("error").asText());
            }

            return Mono.just(jsonNode);
        } catch (Exception e) {
            logger.error("Failed to parse JSON response: {}", e.getMessage());
            return Mono.error(new RuntimeException("Failed to parse API response", e));
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
}
