package com.example.neo_bank.api.repository;

import com.example.neo_bank.api.model.Account;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    //Busca una cuenta por su IBAN
    Optional<Account> findByIban(String iban);

    boolean existsByIban(String id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.iban = :iban")
    Optional<Account> findByIbanWithLock(@Param("iban") String iban);
}
