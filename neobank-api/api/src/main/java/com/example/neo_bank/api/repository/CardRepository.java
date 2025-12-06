package com.example.neo_bank.api.repository;

import com.example.neo_bank.api.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> findByAccountId(Long id);

    boolean existsByCardNumber(String cardNumber);
}
