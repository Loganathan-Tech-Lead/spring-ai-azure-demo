package com.bank.account;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class AccountController {
    private final AccountService service;

    public AccountController(AccountService service) {
        this.service = service;
    }

    @GetMapping("/accounts")
    public List<Account> accounts(@RequestHeader("X-Customer-Id") String customerId) {
        return service.accountsOf(customerId);
    }

    @GetMapping("/accounts/{accountId}")
    public Account account(@PathVariable String accountId, @RequestHeader("X-Customer-Id") String customerId) {
        return service.owned(accountId, customerId);
    }

    @GetMapping("/accounts/{accountId}/transactions")
    public List<Txn> transactions(@PathVariable String accountId,
                                  @RequestHeader("X-Customer-Id") String customerId,
                                  @RequestParam(defaultValue = "5") int limit) {
        return service.lastTransactions(accountId, customerId, limit);
    }
}