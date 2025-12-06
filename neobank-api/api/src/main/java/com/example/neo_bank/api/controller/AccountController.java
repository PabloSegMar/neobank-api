package com.example.neo_bank.api.controller;

import com.example.neo_bank.api.model.Account;
import com.example.neo_bank.api.repository.AccountRepository;
import com.example.neo_bank.api.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;
    private final AccountRepository accountRepository;

    public AccountController(AccountService accountService, AccountRepository accountRepository) {
        this.accountService = accountService;
        this.accountRepository = accountRepository;
    }

    // Petición POST para crear cuenta a un usuario concreto
    @PostMapping("/{userId}")
    public ResponseEntity<Account> createAccount(@PathVariable Long userId) {
        Account newAccount = accountService.createAccount(userId);
        return ResponseEntity.ok(newAccount);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccount(@PathVariable Long id) {
        Account account = accountService.getAccountById(id);


        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String emailUsuarioLogeado = auth.getName();

        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !account.getUser().getEmail().equals(emailUsuarioLogeado)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(account);


    }

    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts() {
        return ResponseEntity.ok(accountRepository.findAll());
    }
}
