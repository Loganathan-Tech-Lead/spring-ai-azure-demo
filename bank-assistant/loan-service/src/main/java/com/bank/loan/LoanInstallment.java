package com.bank.loan;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class LoanInstallment {
    public enum Status { PAID, DUE, UPCOMING }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String loanId;
    private LocalDate dueDate;
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    private Status status;

    protected LoanInstallment() {}

    public LoanInstallment(String loanId, LocalDate dueDate, BigDecimal amount, Status status) {
        this.loanId = loanId;
        this.dueDate = dueDate;
        this.amount = amount;
        this.status = status;
    }

    public Long getId() { return id; }
    public String getLoanId() { return loanId; }
    public LocalDate getDueDate() { return dueDate; }
    public BigDecimal getAmount() { return amount; }
    public Status getStatus() { return status; }
}
