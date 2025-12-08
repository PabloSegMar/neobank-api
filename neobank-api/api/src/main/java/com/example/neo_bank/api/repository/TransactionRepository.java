package com.example.neo_bank.api.repository;

import com.example.neo_bank.api.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccountId(Long accountId);

    @Query("SELECT SUM(t.amount) FROM Transaction t " + "WHERE t.account.id = :accountId " + "AND t.type = 'TRANSFER' " + "AND t.amount < 0" + "AND t.timestamp >= :startOfDay")
    BigDecimal getDailyOutgoingSum(@Param("accountId") Long accountId, @Param("startOfDay") LocalDateTime startOfDay);

    Page<Transaction> findByAccountId(Long accountId, Pageable pageable);
}
