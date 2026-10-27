package com.bank.loan;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class Loan {
    public enum Type { HOME, CAR, PERSONAL }
    public enum Status { ACTIVE, CLOSED }

    @Id
    private String id;
    private String customerId;
    @Enumerated(EnumType.STRING)
    private Type type;
    private BigDecimal principal;
    private BigDecimal outstandingBalance;
    private BigDecimal annualInterestRate;
    private int termMonths;
    private BigDecimal monthlyEmi;
    private LocalDate nextDueDate;
    @Enumerated(EnumType.STRING)
    private Status status;

    protected Loan() {}

    public Loan(String id, String customerId, Type type, BigDecimal principal,
                BigDecimal outstandingBalance, BigDecimal annualInterestRate,
                int termMonths, BigDecimal monthlyEmi, LocalDate nextDueDate, Status status) {
        this.id = id;
        this.customerId = customerId;
        this.type = type;
        this.principal = principal;
        this.outstandingBalance = outstandingBalance;
        this.annualInterestRate = annualInterestRate;
        this.termMonths = termMonths;
        this.monthlyEmi = monthlyEmi;
        this.nextDueDate = nextDueDate;
        this.status = status;
    }

    public String getId() { return id; }
    public String getCustomerId() { return customerId; }
    public Type getType() { return type; }
    public BigDecimal getPrincipal() { return principal; }
    public BigDecimal getOutstandingBalance() { return outstandingBalance; }
    public BigDecimal getAnnualInterestRate() { return annualInterestRate; }
    public int getTermMonths() { return termMonths; }
    public BigDecimal getMonthlyEmi() { return monthlyEmi; }
    public LocalDate getNextDueDate() { return nextDueDate; }
    public Status getStatus() { return status; }
}
