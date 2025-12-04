package com.example.neo_bank.api.service;

import com.example.neo_bank.api.dto.TransferRequest;
import com.example.neo_bank.api.model.Account;
import com.example.neo_bank.api.model.Transaction;
import com.example.neo_bank.api.model.TransactionType;
import com.example.neo_bank.api.repository.AccountRepository;
import com.example.neo_bank.api.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class TransactionService {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public TransactionService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public void transferMoney(TransferRequest request) {

        // 1. Busco cuenta origen (la que paga)
        Account fromAccount = accountRepository.findByIban(request.getFromIban())
                .orElseThrow(() -> new RuntimeException("La cuenta origen no ha sido encontrada"));

        // 2. Buscar cuenta destino (la que recibe)
        Account toAccount = accountRepository.findByIban(request.getToIban())
                .orElseThrow(() -> new RuntimeException("La cuenta destino no ha sido encontrada"));

        // 3. Validar saldo
        if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("No hay saldo suficiente");
        }


        // A. Restar al que paga
        BigDecimal newBalanceFrom = fromAccount.getBalance().subtract(request.getAmount());
        fromAccount.setBalance(newBalanceFrom);

        // B. Sumar al que recibe
        BigDecimal newBalanceTo = toAccount.getBalance().add(request.getAmount());
        toAccount.setBalance(newBalanceTo);

        // 5. Guardar los cambios en BBDD
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);


        // El que paga (Negativo)
        Transaction debit = new Transaction();
        debit.setAccount(fromAccount);
        debit.setAmount(request.getAmount().negate()); // Restamos
        debit.setType(TransactionType.TRANSFER);
        debit.setTimestamp(LocalDateTime.now());
        transactionRepository.save(debit);

        // El que recibe (Positivo)
        Transaction credit = new Transaction();
        credit.setAccount(toAccount);
        credit.setAmount(request.getAmount()); // Sumamos
        credit.setType(TransactionType.TRANSFER);
        credit.setTimestamp(LocalDateTime.now());
        transactionRepository.save(credit);
    }
}