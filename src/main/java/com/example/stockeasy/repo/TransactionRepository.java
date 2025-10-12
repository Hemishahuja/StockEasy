package com.example.stockeasy.repo;

import com.example.stockeasy.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * TransactionRepository interface for Transaction entity persistence operations.
 * Provides CRUD operations and custom queries for Transaction management.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    /**
     * Find transactions by user ID
     */
    List<Transaction> findByUserId(Long userId);
    
    /**
     * Find transactions by stock ID
     */
    List<Transaction> findByStockId(Long stockId);
    
    /**
     * Get user's transaction history with stock information
     */
    @Query("SELECT t FROM Transaction t JOIN FETCH t.stock WHERE t.user.id = :userId ORDER BY t.executedAt DESC")
    List<Transaction> findUserTransactionHistory(Long userId);
    
    /**
     * Get user's buy transactions
     */
    @Query("SELECT t FROM Transaction t WHERE t INSTANCEOF BuyTransaction AND t.user.id = :userId ORDER BY t.executedAt DESC")
    List<Transaction> findUserBuyTransactions(Long userId);
    
    /**
     * Get user's sell transactions
     */
    @Query("SELECT t FROM Transaction t WHERE t INSTANCEOF SellTransaction AND t.user.id = :userId ORDER BY t.executedAt DESC")
    List<Transaction> findUserSellTransactions(Long userId);
    
    /**
     * Get transactions within date range for reporting
     */
    List<Transaction> findByExecutedAtBetween(LocalDateTime start, LocalDateTime end);
    
    /**
     * Get recent transactions for dashboard display
     */
    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId ORDER BY t.executedAt DESC LIMIT 10")
    List<Transaction> findRecentTransactions(Long userId);
    
    /**
     * Find transactions by user ID and stock ID
     */
    List<Transaction> findByUserIdAndStockId(Long userId, Long stockId);
}
