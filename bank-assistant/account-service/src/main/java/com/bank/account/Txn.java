package com.bank.account;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Txn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String accountId;
    private BigDecimal amount;
    private String type; // CREDIT or DEBIT
    private String description;
    private LocalDateTime createdAt;

    protected Txn() {}

    public Txn(String accountId, BigDecimal amount, String type, String description, LocalDateTime createdAt) {
        this.accountId = accountId; this.amount = amount; this.type = type;
        this.description = description; this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getAccountId() { return accountId; }
    public BigDecimal getAmount() { return amount; }
    public String getType() { return type; }
    public String getDescription() { return description; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}