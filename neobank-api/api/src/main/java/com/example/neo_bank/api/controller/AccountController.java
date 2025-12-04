package com.example.neo_bank.api.controller;

import com.example.neo_bank.api.model.Account;
import com.example.neo_bank.api.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    // Petición POST para crear cuenta a un usuario concreto
    @PostMapping("/{userId}")
    public ResponseEntity<Account> createAccount(@PathVariable Long userId) {
        Account newAccount = accountService.createAccount(userId);
        return ResponseEntity.ok(newAccount);
    }
}
