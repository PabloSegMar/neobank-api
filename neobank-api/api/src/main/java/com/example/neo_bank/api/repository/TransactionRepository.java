package com.example.neo_bank.api.repository;

import com.example.neo_bank.api.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
}
