package com.example.neo_bank.api.service;

import com.example.neo_bank.api.audit.Audit;
import com.example.neo_bank.api.dto.TransferRequest;
import com.example.neo_bank.api.model.Account;
import com.example.neo_bank.api.model.Transaction;
import com.example.neo_bank.api.model.TransactionType;
import com.example.neo_bank.api.notification.NotificationService;
import com.example.neo_bank.api.repository.AccountRepository;
import com.example.neo_bank.api.repository.TransactionRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.slf4j.Logger;

@Service
public class TransactionService {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final NotificationService notificationService;
    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    @Value("${neobank.business.daily-limit}")
    private BigDecimal dailyLimit;

    @Value("${neobank.business.interest-rate}")
    private BigDecimal interestRate;

    public TransactionService(AccountRepository accountRepository, TransactionRepository transactionRepository, NotificationService notificationService) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public void transferMoney(TransferRequest request) {
        String cleanFromIban = request.getFromIban().trim();
        String cleanToIban = request.getToIban().trim();
        if (cleanFromIban.equals(cleanToIban)) {
            throw new IllegalArgumentException("No puedes transferirte dinero a ti mismo.");
        }
        logger.info("INTENTO DE TRANSFERENCIA: {}€ desde IBAN {} hacia {}", request.getAmount(), request.getFromIban(), request.getToIban());

        // 1. Busco cuenta origen (la que paga)
        Account fromAccount = accountRepository.findByIbanWithLock(cleanFromIban)
                .orElseThrow(() -> {
                    logger.error("Error: cuenta origen no encontrada");
                    return new RuntimeException("La cuenta origen no ha sido encontrada");
                });

        LocalDateTime startOfDay = LocalDateTime.now().with(LocalTime.MIN);
        BigDecimal spentToday = transactionRepository.getDailyOutgoingSum(fromAccount.getId(), startOfDay);
        if (spentToday == null) spentToday = BigDecimal.ZERO;
        BigDecimal totalSpent = spentToday.abs();

        if (totalSpent.add(request.getAmount()).compareTo(dailyLimit) > 0) {
            logger.warn("Limite diario excedido para {}", fromAccount.getIban());
            throw new RuntimeException("Has superado tu límite diario de transferencias (2.000 €).");
        }

        // 2. Buscar cuenta destino (la que recibe)
        Account toAccount = accountRepository.findByIbanWithLock(cleanToIban)
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

        String mensajeReceptor = String.format("Has recibido %s € de %s", request.getAmount(), fromAccount.getUser().getName());
        notificationService.sendNotification(toAccount.getUser().getEmail(), mensajeReceptor);

        String mensajeEmisor = String.format("Transferencia de %s € realizada con éxito s %s", request.getAmount(), toAccount.getUser().getName());
        notificationService.sendNotification(fromAccount.getUser().getEmail(), mensajeEmisor);
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
    public void withdraw(Long accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        if (account.getBalance().compareTo(amount) < 0) {
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
    public void applyInterestToAllAccounts() {
        int pageSize = 100;
        int pageNumber = 0;
        Page<Account> page;

        do {
            page = accountRepository.findAll(PageRequest.of(pageNumber, pageSize));

            for (Account account : page.getContent()) {
                BigDecimal interest = account.getBalance().multiply(interestRate).setScale(2, RoundingMode.HALF_EVEN);
                if (interest.compareTo(BigDecimal.ZERO) > 0) {
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
            pageNumber++;
        } while (page.hasNext());


    }
}