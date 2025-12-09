package com.example.neo_bank.api.scheduler;

import com.example.neo_bank.api.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class InterestScheduler {

    private final TransactionService transactionService;
    private static final Logger logger = LoggerFactory.getLogger(InterestScheduler.class);

    public InterestScheduler(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Scheduled(cron = "0 0 0 1 * ?")
    public void runInterestTask() {
        logger.info("CRON JOB: Iniciando cálculo mensual de intereses.");
        transactionService.applyInterestToAllAccounts();
        logger.info("CRON JOB: Cálculo de intereses finalizado.");
    }
}
