package com.example.neo_bank.api.controller;

import com.example.neo_bank.api.audit.Audit;
import com.example.neo_bank.api.dto.MoneyRequest;
import com.example.neo_bank.api.dto.TransferRequest;
import com.example.neo_bank.api.model.Account;
import com.example.neo_bank.api.model.Transaction;
import com.example.neo_bank.api.repository.AccountRepository;
import com.example.neo_bank.api.repository.TransactionRepository;
import com.example.neo_bank.api.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TransactionController(TransactionService transactionService, TransactionRepository transactionRepository, AccountRepository accountRepository) {
        this.transactionService = transactionService;
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;

    }

    @PostMapping
    @Audit(action = "REALIZAR TRANSFERENCIA")
    public ResponseEntity<String> makeTransfer(@RequestBody @Valid TransferRequest request) {
        transactionService.transferMoney(request);
        return ResponseEntity.ok("Transferencia realizada con éxito");
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<List<Transaction>> getHistory(@PathVariable Long accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String emailUsuarioLogeado = auth.getName();

        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !account.getUser().getEmail().equals(emailUsuarioLogeado)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(transactionRepository.findByAccountId(accountId));
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        return ResponseEntity.ok(transactionRepository.findAll());
    }


    @PostMapping("/deposit")
    @Audit(action = "INGRESO EN CAJERO")
    public ResponseEntity<?> deposit(@RequestBody @Valid MoneyRequest request) {
        if (!isOwnerAdmin(request.getAccountId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No puedes ingresar dinero en cuentas ajenas");
        }

        transactionService.deposit(request.getAccountId(), request.getAmount());
        return ResponseEntity.ok("Dinero ingresado correctamente");
    }

    @PostMapping("/withdraw")
    @Audit(action = "RETIRO EN CAJERO")
    public ResponseEntity<?> withdraw(@RequestBody @Valid MoneyRequest request) {
        if (!isOwnerAdmin(request.getAccountId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No puedes retirar dinero de cuentas ajenas");
        }

        transactionService.withdraw(request.getAccountId(), request.getAmount());
        return ResponseEntity.ok("Dinero retirado correctamente");
    }


    private boolean isOwnerAdmin(Long accountId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String emailLogueado = auth.getName();

        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) return true;

        Account account = accountRepository.findById(accountId).orElse(null);
        return account != null && account.getUser().getEmail().equals(emailLogueado);

    }
}
