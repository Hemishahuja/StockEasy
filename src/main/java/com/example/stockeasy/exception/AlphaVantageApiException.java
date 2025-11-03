package com.example.stockeasy.exception;

/**
 * Exception thrown when Alpha Vantage API calls fail.
 */
public class AlphaVantageApiException extends RuntimeException {

    private final String symbol;
    private final int statusCode;

    public AlphaVantageApiException(String message) {
        super(message);
        this.symbol = null;
        this.statusCode = 0;
    }

    public AlphaVantageApiException(String message, Throwable cause) {
        super(message, cause);
        this.symbol = null;
        this.statusCode = 0;
    }

    public AlphaVantageApiException(String message, String symbol) {
        super(message);
        this.symbol = symbol;
        this.statusCode = 0;
    }

    public AlphaVantageApiException(String message, String symbol, Throwable cause) {
        super(message, cause);
        this.symbol = symbol;
        this.statusCode = 0;
    }

    public AlphaVantageApiException(String message, String symbol, int statusCode) {
        super(message);
        this.symbol = symbol;
        this.statusCode = statusCode;
    }

    public AlphaVantageApiException(String message, String symbol, int statusCode, Throwable cause) {
        super(message, cause);
        this.symbol = symbol;
        this.statusCode = statusCode;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public boolean hasSymbol() {
        return symbol != null && !symbol.trim().isEmpty();
    }

    public boolean hasStatusCode() {
        return statusCode > 0;
    }
}
