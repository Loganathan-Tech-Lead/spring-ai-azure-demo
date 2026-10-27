package com.bank.loan;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanController {
    private final LoanService service;

    public LoanController(LoanService service) {
        this.service = service;
    }

    @GetMapping
    public List<Loan> list(@RequestHeader("X-Customer-Id") String customerId) {
        return service.loans(customerId);
    }

    @GetMapping("/{id}")
    public Loan get(@RequestHeader("X-Customer-Id") String customerId, @PathVariable String id) {
        return service.loan(customerId, id);
    }

    @GetMapping("/{id}/installments")
    public List<LoanInstallment> installments(@RequestHeader("X-Customer-Id") String customerId,
                                              @PathVariable String id,
                                              @RequestParam(defaultValue = "5") int limit) {
        return service.installments(customerId, id, limit);
    }
}
