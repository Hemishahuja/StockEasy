package com.example.stockeasy.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Transaction entity representing buy/sell transactions in the stock trading simulator.
 * Tracks all trading activities with timestamps, prices, and transaction types.
 */
@Entity
@Table(name = "transactions")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "transaction_type", discriminatorType = DiscriminatorType.STRING)
public class Transaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;
    
    @NotNull
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
    
    @NotNull
    @Column(name = "price", precision = 15, scale = 2, nullable = false)
    private BigDecimal price;
    
    @Column(name = "total_amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal totalAmount;
    
    @Column(name = "transaction_fee", precision = 15, scale = 2)
    private BigDecimal transactionFee;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "executed_at")
    private LocalDateTime executedAt;
    
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TransactionStatus status = TransactionStatus.PENDING;
    
    @Column(name = "notes")
    private String notes;
    
    // Constructors
    public Transaction() {}
    
    public Transaction(User user, Stock stock, Integer quantity, BigDecimal price) {
        this.user = user;
        this.stock = stock;
        this.quantity = quantity;
        this.price = price;
        this.totalAmount = price.multiply(new BigDecimal(quantity));
        this.executedAt = LocalDateTime.now();
    }
    
    // Business methods
    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.executedAt == null) {
            this.executedAt = LocalDateTime.now();
        }
        if (this.totalAmount == null && this.price != null && this.quantity != null) {
            this.totalAmount = this.price.multiply(new BigDecimal(this.quantity));
        }
    }
    
    public void execute() {
        this.executedAt = LocalDateTime.now();
        this.status = TransactionStatus.COMPLETED;
    }
    
    public void cancel() {
        this.status = TransactionStatus.CANCELLED;
    }
    
    public void fail() {
        this.status = TransactionStatus.FAILED;
    }
    
    public boolean isBuy() {
        return this instanceof BuyTransaction;
    }
    
    public boolean isSell() {
        return this instanceof SellTransaction;
    }
    
    public boolean isPending() {
        return status == TransactionStatus.PENDING;
    }
    
    public boolean isCompleted() {
        return status == TransactionStatus.COMPLETED;
    }
    
    public boolean isCancelled() {
        return status == TransactionStatus.CANCELLED;
    }
    
    public boolean isFailed() {
        return status == TransactionStatus.FAILED;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public Stock getStock() { return stock; }
    public void setStock(Stock stock) { this.stock = stock; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { 
        this.quantity = quantity; 
        if (this.price != null) {
            this.totalAmount = this.price.multiply(new BigDecimal(quantity));
        }
    }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { 
        this.price = price; 
        if (this.quantity != null) {
            this.totalAmount = price.multiply(new BigDecimal(this.quantity));
        }
    }
    
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    
    public BigDecimal getTransactionFee() { return transactionFee; }
    public void setTransactionFee(BigDecimal transactionFee) { this.transactionFee = transactionFee; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getExecutedAt() { return executedAt; }
    public void setExecutedAt(LocalDateTime executedAt) { this.executedAt = executedAt; }
    
    public TransactionStatus getStatus() { return status; }
    public void setStatus(TransactionStatus status) { this.status = status; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    // Utility methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transaction transaction)) return false;
        return Objects.equals(getId(), transaction.getId());
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
    
    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", userId=" + (user != null ? user.getId() : null) +
                ", stockId=" + (stock != null ? stock.getId() : null) +
                ", symbol='" + (stock != null ? stock.getSymbol() : null) + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", totalAmount=" + totalAmount +
                ", transactionType=" + getClass().getSimpleName() +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", executedAt=" + executedAt +
                '}';
    }
    
    // Enum for transaction status
    public enum TransactionStatus {
        PENDING, COMPLETED, CANCELLED, FAILED
    }
}
