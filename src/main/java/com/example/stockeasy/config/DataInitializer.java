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
            User defaultUser = new User("testuser", "test@example.com",
                    "$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi"); // password: password
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
                // Fetch latest price data
                marketDataService.getLatestMarketDataForSymbolSync(stock.getSymbol(), "D");

                // Fetch company profile (market cap, etc.)
                marketDataService.fetchCompanyProfileSync(stock.getSymbol());

            } catch (Exception e) {
                System.err.println(
                        "Failed to fetch initial market data for " + stock.getSymbol() + ": " + e.getMessage());
            }
        });
        System.out.println("Initial market data fetch process completed.");
    }

    private void createSampleStocks() {
        // Create sample stocks
        List<Stock> stocks = List.of(
                new Stock("AAPL", "Apple Inc.",
                        "Technology company that designs, manufactures, and markets mobile communication and media devices.",
                        "Technology", "Consumer Electronics", java.math.BigDecimal.valueOf(150.00),
                        java.math.BigDecimal.valueOf(2500000000000L)),
                new Stock("MSFT", "Microsoft Corporation",
                        "Technology company that develops, licenses, and supports software, services, devices, and solutions.",
                        "Technology", "Software - Infrastructure", java.math.BigDecimal.valueOf(310.00),
                        java.math.BigDecimal.valueOf(2300000000000L)),
                new Stock("GOOGL", "Alphabet Inc.",
                        "Holding company that provides web-based search, advertisements, maps, software applications, mobile operating systems, consumer content, enterprise solutions, commerce, and hardware products.",
                        "Technology", "Internet Content & Information", java.math.BigDecimal.valueOf(135.00),
                        java.math.BigDecimal.valueOf(1700000000000L)),
                new Stock("AMZN", "Amazon.com, Inc.",
                        "E-commerce giant that also offers cloud computing services, digital streaming, and artificial intelligence.",
                        "Consumer Cyclical", "Internet Retail", java.math.BigDecimal.valueOf(145.00),
                        java.math.BigDecimal.valueOf(1500000000000L)),
                new Stock("TSLA", "Tesla, Inc.", "Electric vehicle and clean energy company.", "Consumer Cyclical",
                        "Auto Manufacturers", java.math.BigDecimal.valueOf(240.00),
                        java.math.BigDecimal.valueOf(750000000000L)),
                new Stock("NVDA", "NVIDIA Corporation",
                        "Technology company known for designing graphics processing units (GPUs).", "Technology",
                        "Semiconductors", java.math.BigDecimal.valueOf(460.00),
                        java.math.BigDecimal.valueOf(1100000000000L)),
                new Stock("JPM", "JPMorgan Chase & Co.",
                        "Multinational investment bank and financial services holding company.", "Financial Services",
                        "Banks - Diversified", java.math.BigDecimal.valueOf(150.00),
                        java.math.BigDecimal.valueOf(430000000000L)),
                new Stock("JNJ", "Johnson & Johnson",
                        "Multinational corporation that develops medical devices, pharmaceuticals, and consumer packaged goods.",
                        "Healthcare", "Drug Manufacturers - General", java.math.BigDecimal.valueOf(160.00),
                        java.math.BigDecimal.valueOf(400000000000L)),
                new Stock("V", "Visa Inc.",
                        "Multinational financial services corporation that facilitates electronic funds transfers.",
                        "Financial Services", "Credit Services", java.math.BigDecimal.valueOf(245.00),
                        java.math.BigDecimal.valueOf(500000000000L)),
                new Stock("PG", "Procter & Gamble Co.", "Multinational consumer goods corporation.",
                        "Consumer Defensive", "Household & Personal Products", java.math.BigDecimal.valueOf(155.00),
                        java.math.BigDecimal.valueOf(360000000000L)),
                new Stock("UNH", "UnitedHealth Group Incorporated",
                        "Multinational managed healthcare and insurance company.", "Healthcare", "Healthcare Plans",
                        java.math.BigDecimal.valueOf(530.00), java.math.BigDecimal.valueOf(490000000000L)));

        stockRepository.saveAll(stocks);
        System.out.println("Sample stocks created: " + stocks.stream().map(Stock::getSymbol).toList());
    }
}
