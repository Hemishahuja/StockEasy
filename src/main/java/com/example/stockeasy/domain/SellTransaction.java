package com.example.stockeasy.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * SellTransaction subclass for selling stocks
 */
@Entity
@DiscriminatorValue("SELL")
public class SellTransaction extends Transaction {
    
    public SellTransaction() {
        super();
    }
    
    public SellTransaction(User user, Stock stock, Integer quantity, BigDecimal price) {
        super(user, stock, quantity, price);
    }
    
    public BigDecimal getCashAdded() {
        return getTotalAmount();
    }// this returns the amount left after selling.
}
