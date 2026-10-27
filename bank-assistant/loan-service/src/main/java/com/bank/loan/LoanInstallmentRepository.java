package com.bank.loan;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanInstallmentRepository extends JpaRepository<LoanInstallment, Long> {
    List<LoanInstallment> findByLoanIdAndStatusNotOrderByDueDateAsc(
            String loanId, LoanInstallment.Status status, Pageable pageable);
}
