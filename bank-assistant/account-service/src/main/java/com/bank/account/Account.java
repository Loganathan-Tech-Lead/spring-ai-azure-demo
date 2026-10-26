package com.bank.account;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.math.BigDecimal;

@Entity
public class Account {
    @Id
    private String id;
    private String customerId;
    private String type;
    private BigDecimal balance;
    private String currency;

    protected Account() {}

    public Account(String id, String customerId, String type, BigDecimal balance, String currency) {
        this.id = id; this.customerId = customerId; this.type = type;
        this.balance = balance; this.currency = currency;
    }

    public String getId() { return id; }
    public String getCustomerId() { return customerId; }
    public String getType() { return type; }
    public BigDecimal getBalance() { return balance; }
    public String getCurrency() { return currency; }
}