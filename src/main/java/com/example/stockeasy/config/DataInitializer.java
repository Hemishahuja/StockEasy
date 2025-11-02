package com.example.stockeasy.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.stockeasy.domain.Stock;
import com.example.stockeasy.domain.User;
import com.example.stockeasy.repo.StockRepository;
import com.example.stockeasy.repo.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StockRepository stockRepository;

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

    private void createSampleStocks() {
        Stock apple = new Stock("AAPL", "Apple Inc.", "Technology company", "Technology", "Consumer Electronics",
                               java.math.BigDecimal.valueOf(150.00), null);
        apple.setPreviousClose(java.math.BigDecimal.valueOf(145.00));
        stockRepository.save(apple);

        Stock google = new Stock("GOOGL", "Alphabet Inc.", "Search engine company", "Technology", "Internet Services",
                                java.math.BigDecimal.valueOf(2800.00), null);
        google.setPreviousClose(java.math.BigDecimal.valueOf(2750.00));
        stockRepository.save(google);

        Stock microsoft = new Stock("MSFT", "Microsoft Corporation", "Software company", "Technology", "Software",
                                   java.math.BigDecimal.valueOf(330.00), null);
        microsoft.setPreviousClose(java.math.BigDecimal.valueOf(335.00));
        stockRepository.save(microsoft);

        Stock amazon = new Stock("AMZN", "Amazon.com Inc.", "E-commerce company", "Consumer Discretionary", "Internet Retail",
                                java.math.BigDecimal.valueOf(3200.00), null);
        amazon.setPreviousClose(java.math.BigDecimal.valueOf(3150.00));
        stockRepository.save(amazon);

        Stock tesla = new Stock("TSLA", "Tesla Inc.", "Electric vehicle company", "Consumer Discretionary", "Auto Manufacturers",
                               java.math.BigDecimal.valueOf(250.00), null);
        tesla.setPreviousClose(java.math.BigDecimal.valueOf(260.00));
        stockRepository.save(tesla);
    }
}
