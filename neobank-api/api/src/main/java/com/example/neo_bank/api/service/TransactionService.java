package com.example.neo_bank.api.service;

import com.example.neo_bank.api.audit.Audit;
import com.example.neo_bank.api.dto.TransferRequest;
import com.example.neo_bank.api.model.Account;
import com.example.neo_bank.api.model.Transaction;
import com.example.neo_bank.api.model.TransactionType;
import com.example.neo_bank.api.repository.AccountRepository;
import com.example.neo_bank.api.repository.TransactionRepository;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;

@Service
public class TransactionService {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    public TransactionService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public void transferMoney(TransferRequest request) {

        logger.info("INTENTO DE TRANSFERENCIA: {}€ desde IBAN {} hacia {}", request.getAmount(), request.getFromIban(), request.getToIban());

        // 1. Busco cuenta origen (la que paga)
        Account fromAccount = accountRepository.findByIban(request.getFromIban())
                .orElseThrow(() -> {
                    logger.error("Error: cuenta origen no encontrada");
                    return new RuntimeException("La cuenta origen no ha sido encontrada");

                });

        // 2. Buscar cuenta destino (la que recibe)
        Account toAccount = accountRepository.findByIban(request.getToIban())
                .orElseThrow(() -> {
                    logger.error("Error: cuenta destino no encontrada");
                    return new RuntimeException("La cuenta destino no ha sido encontrada");
                });
        // 3. Validar saldo
        if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
            logger.warn("Saldo insuficiente en la cuenta de {}", fromAccount.getUser().getEmail());
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

        logger.info("TRANSFERENCIA COMPLETADA con exito. Nuevo saldo de origen: {}€", fromAccount.getBalance());
    }

    @Transactional
    public void deposit(Long accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setAmount(amount);
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setTimestamp(LocalDateTime.now());

        transactionRepository.save(transaction);
    }

    @Transactional
    public void withdraw(Long accountId, BigDecimal amount){
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        if(account.getBalance().compareTo(amount) < 0){
            throw new RuntimeException("Saldo insuficiente para retirar");
        }

        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setAmount(amount.negate());
        transaction.setType(TransactionType.WITHDRAWAL);
        transaction.setTimestamp(LocalDateTime.now());

        transactionRepository.save(transaction);
    }

    @Transactional
    @Audit(action = "PAGO AUTOMÁTICO DE INTERESES")
    public void applyInterestToAllAccounts(){
        List<Account> accounts =   accountRepository.findAll();

        for(Account account : accounts){
            BigDecimal interest = account.getBalance().multiply(new BigDecimal("0.01"));
            if(interest.compareTo(BigDecimal.ZERO) > 0){
                account.setBalance(account.getBalance().add(interest));
                accountRepository.save(account);

                Transaction transaction = new Transaction();
                transaction.setAccount(account);
                transaction.setAmount(interest);
                transaction.setType(TransactionType.DEPOSIT);
                transaction.setTimestamp(LocalDateTime.now());

                transactionRepository.save(transaction);

                logger.info("Interés de {}€ aplicado a la cuenta {}", interest, account.getIban());
            }
        }
    }
}