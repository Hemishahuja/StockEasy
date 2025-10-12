package com.example.stockeasy.repo;

import com.example.stockeasy.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * UserRepository interface for User entity persistence operations.
 * Provides CRUD operations and custom queries for User management.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find user by username (for authentication)
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Find user by email (for authentication)
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Check if username exists (for registration validation)
     */
    boolean existsByUsername(String username);
    
    /**
     * Check if email exists (for registration validation)
     */
    boolean existsByEmail(String email);
    
    /**
     * Get user with related entities for dashboard display
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.portfolios LEFT JOIN FETCH u.watchlists WHERE u.id = :userId")
    Optional<User> findByIdWithRelationships(Long userId);
}
