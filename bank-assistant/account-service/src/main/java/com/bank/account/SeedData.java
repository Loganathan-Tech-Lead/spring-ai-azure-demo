package com.bank.account;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Configuration
public class SeedData {
    @Bean
    CommandLineRunner seed(AccountRepository accounts, TxnRepository txns) {
        return args -> {
            if (accounts.count() > 0) return;
            accounts.save(new Account("ACC-1001", "C001", "SAVINGS", new BigDecimal("2500.00"), "INR"));
            accounts.save(new Account("ACC-1002", "C001", "CURRENT", new BigDecimal("800.50"), "INR"));
            accounts.save(new Account("ACC-2001", "C002", "SAVINGS", new BigDecimal("10000.00"), "INR"));
            LocalDateTime now = LocalDateTime.now();
            txns.save(new Txn("ACC-1001", new BigDecimal("1000.00"), "CREDIT", "Salary", now.minusDays(5)));
            txns.save(new Txn("ACC-1001", new BigDecimal("250.00"), "DEBIT", "Grocery store", now.minusDays(3)));
            txns.save(new Txn("ACC-1001", new BigDecimal("99.00"), "DEBIT", "Streaming subscription", now.minusDays(1)));
            txns.save(new Txn("ACC-2001", new BigDecimal("500.00"), "DEBIT", "Electricity bill", now.minusDays(2)));
        };
    }
}