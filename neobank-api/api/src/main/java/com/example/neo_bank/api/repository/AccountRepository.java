package com.example.neo_bank.api.repository;

import com.example.neo_bank.api.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    //Busca una cuenta por su IBAN
    Optional<Account> findByIban(String iban);

    boolean existsByIban(String id);
}
