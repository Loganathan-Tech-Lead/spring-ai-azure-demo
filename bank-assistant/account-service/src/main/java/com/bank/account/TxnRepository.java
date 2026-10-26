package com.bank.account;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TxnRepository extends JpaRepository<Txn, Long> {
    List<Txn> findByAccountIdOrderByCreatedAtDesc(String accountId, Pageable pageable);
}