package com.bank.loan;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class SeedData implements CommandLineRunner {
    private final LoanRepository loans;
    private final LoanInstallmentRepository installments;

    public SeedData(LoanRepository loans, LoanInstallmentRepository installments) {
        this.loans = loans;
        this.installments = installments;
    }

    @Override
    public void run(String... args) {
        LocalDate today = LocalDate.now();
        seed(new Loan("LN-2001", "C001", Loan.Type.HOME, new BigDecimal("250000.00"),
                new BigDecimal("231500.00"), new BigDecimal("7.50"), 240,
                new BigDecimal("2014.00"), today.plusDays(12), Loan.Status.ACTIVE));
        seed(new Loan("LN-2002", "C001", Loan.Type.CAR, new BigDecimal("20000.00"),
                new BigDecimal("14200.00"), new BigDecimal("8.00"), 60,
                new BigDecimal("405.53"), today.plusDays(5), Loan.Status.ACTIVE));
        seed(new Loan("LN-3001", "C002", Loan.Type.PERSONAL, new BigDecimal("10000.00"),
                new BigDecimal("6800.00"), new BigDecimal("11.00"), 36,
                new BigDecimal("327.45"), today.plusDays(20), Loan.Status.ACTIVE));
    }

    private void seed(Loan loan) {
        loans.save(loan);
        for (int i = -1; i <= 4; i++) {
            LoanInstallment.Status status = i < 0 ? LoanInstallment.Status.PAID
                    : i == 0 ? LoanInstallment.Status.DUE : LoanInstallment.Status.UPCOMING;
            installments.save(new LoanInstallment(loan.getId(),
                    loan.getNextDueDate().plusMonths(i), loan.getMonthlyEmi(), status));
        }
    }
}
