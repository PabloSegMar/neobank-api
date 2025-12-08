package com.example.neo_bank.api.controller;

import com.example.neo_bank.api.model.Account;
import com.example.neo_bank.api.pdf.PdfService;
import com.example.neo_bank.api.repository.AccountRepository;
import com.example.neo_bank.api.service.AccountService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    private final PdfService pdfService;

    public AccountController(AccountService accountService, AccountRepository accountRepository, PdfService pdfService) {
        this.accountService = accountService;
        this.accountRepository = accountRepository;
        this.pdfService = pdfService;
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

    @GetMapping("/{id}/statement")
    public ResponseEntity<byte[]> getStatement(@PathVariable Long id) {
        Account account = accountService.getAccountById(id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String emailUsuarioLogeado = auth.getName();

        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !account.getUser().getEmail().equals(emailUsuarioLogeado)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        byte[] pdfBytes = pdfService.generateStatement(id);

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=extracto_" + id + " .pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);

    }
    @PostMapping("/migrate-security")
    public ResponseEntity<String> migrateData() {
        // 1. Cargar todas (se leen tal cual gracias al try-catch)
        List<Account> accounts = accountRepository.findAll();

        // 2. Guardar todas (al guardar, el convertToDatabaseColumn las encripta)
        accountRepository.saveAll(accounts);

        return ResponseEntity.ok("¡Migración completada! " + accounts.size() + " cuentas encriptadas.");
    }
}
