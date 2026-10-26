package com.bank.account;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AccountService {
    private final AccountRepository accounts;
    private final TxnRepository txns;

    public AccountService(AccountRepository accounts, TxnRepository txns) {
        this.accounts = accounts;
        this.txns = txns;
    }

    public List<Account> accountsOf(String customerId) {
        return accounts.findByCustomerId(customerId);
    }

    // A wrong owner gets the same 404 as a missing account,
    // so the caller cannot learn which account ids exist.
    public Account owned(String accountId, String customerId) {
        return accounts.findByIdAndCustomerId(accountId, customerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "account not found"));
    }

    public List<Txn> lastTransactions(String accountId, String customerId, int n) {
        owned(accountId, customerId);
        int size = Math.max(1, Math.min(n, 20));
        return txns.findByAccountIdOrderByCreatedAtDesc(accountId, PageRequest.of(0, size));
    }
}