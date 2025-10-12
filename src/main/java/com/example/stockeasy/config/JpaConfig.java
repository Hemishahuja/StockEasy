package com.example.stockeasy.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * JPA Configuration class for database configuration.
 * Configures JPA repositories and transaction management.
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.example.stockeasy.repo")
@EnableTransactionManagement
public class JpaConfig {
    
}
