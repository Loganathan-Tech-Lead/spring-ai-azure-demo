package com.bank.loan;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LoanRepository extends JpaRepository<Loan, String> {
    List<Loan> findByCustomerId(String customerId);
    Optional<Loan> findByIdAndCustomerId(String id, String customerId);
}
