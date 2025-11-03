package com.example.stockeasy.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Configuration properties for Alpha Vantage API integration.
 */
@Configuration
@ConfigurationProperties(prefix = "alpha-vantage")
@Validated
public class AlphaVantageConfig {

    /**
     * Alpha Vantage API key.
     */
    @NotBlank
    private String apiKey;

    /**
     * Base URL for Alpha Vantage API.
     */
    @NotBlank
    private String baseUrl = "https://www.alphavantage.co";

    /**
     * Timeout for API calls.
     */
    @NotNull
    private Duration timeout = Duration.ofSeconds(30);

    /**
     * Rate limit delay between API calls (in milliseconds).
     */
    @Positive
    private long rateLimitDelay = 12000; // 12 seconds to stay under 5 calls/minute limit

    /**
     * Maximum number of retry attempts for failed API calls.
     */
    @Positive
    private int maxRetries = 3;

    /**
     * Cache TTL for API responses.
     */
    @NotNull
    private Duration cacheTtl = Duration.ofMinutes(15);

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Duration getTimeout() {
        return timeout;
    }

    public void setTimeout(Duration timeout) {
        this.timeout = timeout;
    }

    public long getRateLimitDelay() {
        return rateLimitDelay;
    }

    public void setRateLimitDelay(long rateLimitDelay) {
        this.rateLimitDelay = rateLimitDelay;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public Duration getCacheTtl() {
        return cacheTtl;
    }

    public void setCacheTtl(Duration cacheTtl) {
        this.cacheTtl = cacheTtl;
    }
}
