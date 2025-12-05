package com.example.neo_bank.api.controller;

import com.example.neo_bank.api.dto.TransferRequest;
import com.example.neo_bank.api.model.Transaction;
import com.example.neo_bank.api.repository.TransactionRepository;
import com.example.neo_bank.api.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;
    private final TransactionRepository transactionRepository;

    public TransactionController(TransactionService transactionService, TransactionRepository transactionRepository) {
        this.transactionService = transactionService;
        this.transactionRepository = transactionRepository;
    }

    @PostMapping
    public ResponseEntity<String> makeTransfer(@RequestBody TransferRequest request) {
        transactionService.transferMoney(request);
        return ResponseEntity.ok("Transferencia realizada con éxito");
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<List<Transaction>> getHistory(@PathVariable Long accountId) {
        return ResponseEntity.ok(transactionRepository.findByAccountId(accountId));
    }
}
