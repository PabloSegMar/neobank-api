package com.example.neo_bank.api.controller;

import com.example.neo_bank.api.dto.TransferRequest;
import com.example.neo_bank.api.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<String> makeTransfer(@RequestBody TransferRequest request) {
        transactionService.transferMoney(request);
        return ResponseEntity.ok("Transferencia realizada con éxito");
    }
}
