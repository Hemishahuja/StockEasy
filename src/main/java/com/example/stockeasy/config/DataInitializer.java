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
        Stock apple = new Stock("AAPL", "Apple Inc.", "Technology company", "Technology", "Consumer Electronics",
                               java.math.BigDecimal.ZERO, null);
        apple.setPreviousClose(java.math.BigDecimal.valueOf(265.50));
        stockRepository.save(apple);

        Stock google = new Stock("GOOGL", "Alphabet Inc.", "Search engine company", "Technology", "Internet Services",
                                java.math.BigDecimal.ZERO, null);
        google.setPreviousClose(java.math.BigDecimal.valueOf(278.75));
        stockRepository.save(google);

        Stock microsoft = new Stock("MSFT", "Microsoft Corporation", "Software company", "Technology", "Software",
                                   java.math.BigDecimal.ZERO, null);
        microsoft.setPreviousClose(java.math.BigDecimal.valueOf(512.45));
        stockRepository.save(microsoft);

        Stock amazon = new Stock("AMZN", "Amazon.com Inc.", "E-commerce company", "Consumer Discretionary", "Internet Retail",
                                java.math.BigDecimal.ZERO, null);
        amazon.setPreviousClose(java.math.BigDecimal.valueOf(241.80));
        stockRepository.save(amazon);

        Stock tesla = new Stock("TSLA", "Tesla Inc.", "Electric vehicle company", "Consumer Discretionary", "Auto Manufacturers",
                               java.math.BigDecimal.ZERO, null);
        tesla.setPreviousClose(java.math.BigDecimal.valueOf(450.25));
        stockRepository.save(tesla);
    }
}
