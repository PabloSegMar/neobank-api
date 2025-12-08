package com.example.neo_bank.api.scheduler;

import com.example.neo_bank.api.service.TransactionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class InterestScheduler {

    private final TransactionService transactionService;

    public InterestScheduler(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Scheduled(cron = "0 0 0 1 * ?")
    public void runInterestTask() {
        System.out.println("Calculando intereses...");
        transactionService.applyInterestToAllAccounts();
    }
}
