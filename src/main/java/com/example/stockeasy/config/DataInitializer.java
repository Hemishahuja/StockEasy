package com.example.stockeasy.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.example.stockeasy.domain.Stock;
import com.example.stockeasy.domain.User;
import com.example.stockeasy.repo.StockRepository;
import com.example.stockeasy.repo.UserRepository;
import com.example.stockeasy.service.MarketDataService;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private MarketDataService marketDataService;

    @Override
    public void run(String... args) throws Exception {
        // Create a default test user if none exists
        if (userRepository.count() == 0) {
            User defaultUser = new User("testuser", "test@example.com", "$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi"); // password: password
            defaultUser.setFirstName("Test");
            defaultUser.setLastName("User");
            defaultUser.setCashBalance(java.math.BigDecimal.valueOf(100000.00));
            userRepository.save(defaultUser);
            System.out.println("Default user created: username=testuser, password=password");
        }

        // Create sample stocks if none exist
        if (stockRepository.count() == 0) {
            createSampleStocks();
            System.out.println("Sample stocks created for demo");
        }
    }

    /**
     * After the application is fully started, this method is triggered to
     * fetch initial market data for all existing stocks in the background.
     */
    @EventListener(ApplicationReadyEvent.class)
    @Async // Runs this method in a separate background thread
    public void fetchInitialMarketData() {
        System.out.println("Application is ready. Starting initial market data fetch in the background...");
        List<Stock> stocks = stockRepository.findAll();
        if (stocks.isEmpty()) {
            System.out.println("No stocks found to fetch data for.");
            return;
        }

        stocks.forEach(stock -> {
            try {
                marketDataService.getLatestMarketDataForSymbolSync(stock.getSymbol(), "5min");
            } catch (Exception e) {
                System.err.println("Failed to fetch initial market data for " + stock.getSymbol() + ": " + e.getMessage());
            }
        });
        System.out.println("Initial market data fetch process completed.");
    }

    private void createSampleStocks() {
        // Create stocks with realistic current prices
        Stock apple = new Stock("AAPL", "Apple Inc.", "Technology company", "Technology", "Consumer Electronics",
                               java.math.BigDecimal.valueOf(185.75), null);
        apple.setPreviousClose(java.math.BigDecimal.valueOf(183.50));
        stockRepository.save(apple);

        Stock google = new Stock("GOOGL", "Alphabet Inc.", "Search engine company", "Technology", "Internet Services",
                                java.math.BigDecimal.valueOf(148.30), null);
        google.setPreviousClose(java.math.BigDecimal.valueOf(147.20));
        stockRepository.save(google);

        Stock microsoft = new Stock("MSFT", "Microsoft Corporation", "Software company", "Technology", "Software",
                                   java.math.BigDecimal.valueOf(334.80), null);
        microsoft.setPreviousClose(java.math.BigDecimal.valueOf(332.90));
        stockRepository.save(microsoft);

        Stock amazon = new Stock("AMZN", "Amazon.com Inc.", "E-commerce company", "Consumer Discretionary", "Internet Retail",
                                java.math.BigDecimal.valueOf(145.60), null);
        amazon.setPreviousClose(java.math.BigDecimal.valueOf(144.30));
        stockRepository.save(amazon);

        Stock tesla = new Stock("TSLA", "Tesla Inc.", "Electric vehicle company", "Consumer Discretionary", "Auto Manufacturers",
                               java.math.BigDecimal.valueOf(248.90), null);
        tesla.setPreviousClose(java.math.BigDecimal.valueOf(245.20));
        stockRepository.save(tesla);

        // Add a few more realistic stocks
        Stock nvidia = new Stock("NVDA", "NVIDIA Corporation", "Semiconductor company", "Technology", "Semiconductors",
                                java.math.BigDecimal.valueOf(465.80), null);
        nvidia.setPreviousClose(java.math.BigDecimal.valueOf(462.10));
        stockRepository.save(nvidia);

        Stock meta = new Stock("META", "Meta Platforms Inc.", "Social media company", "Technology", "Internet Services",
                              java.math.BigDecimal.valueOf(485.20), null);
        meta.setPreviousClose(java.math.BigDecimal.valueOf(482.80));
        stockRepository.save(meta);

        Stock netflix = new Stock("NFLX", "Netflix Inc.", "Streaming company", "Communication Services", "Entertainment",
                                java.math.BigDecimal.valueOf(495.40), null);
        netflix.setPreviousClose(java.math.BigDecimal.valueOf(492.10));
        stockRepository.save(netflix);

        System.out.println("Sample stocks created with realistic prices: AAPL, GOOGL, MSFT, AMZN, TSLA, NVDA, META, NFLX");
    }
}
