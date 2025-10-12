package com.example.stockeasy.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * BuyTransaction subclass for buying stocks
 */
@Entity
@DiscriminatorValue("BUY")
public class BuyTransaction extends Transaction {
    
    public BuyTransaction() {
        super();
    }
    
    public BuyTransaction(User user, Stock stock, Integer quantity, BigDecimal price) {
        super(user, stock, quantity, price);
    }
    
    public BigDecimal getCashDeducted() {
        return getTotalAmount();
    }
}
