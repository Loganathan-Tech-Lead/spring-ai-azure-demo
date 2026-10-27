package com.bank.loan;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class LoanService {
    private final LoanRepository loans;
    private final LoanInstallmentRepository installments;

    public LoanService(LoanRepository loans, LoanInstallmentRepository installments) {
        this.loans = loans;
        this.installments = installments;
    }

    public List<Loan> loans(String customerId) {
        return loans.findByCustomerId(customerId);
    }

    // Same 404 whether the loan does not exist or belongs to someone else.
    public Loan loan(String customerId, String loanId) {
        return loans.findByIdAndCustomerId(loanId, customerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Loan not found"));
    }

    public List<LoanInstallment> installments(String customerId, String loanId, int limit) {
        loan(customerId, loanId); // ownership check
        int n = Math.max(1, Math.min(limit, 20));
        return installments.findByLoanIdAndStatusNotOrderByDueDateAsc(
                loanId, LoanInstallment.Status.PAID, PageRequest.of(0, n));
    }
}
