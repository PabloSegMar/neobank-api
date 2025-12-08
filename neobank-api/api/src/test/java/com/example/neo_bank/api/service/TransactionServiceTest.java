package com.example.neo_bank.api.service;

import com.example.neo_bank.api.dto.TransferRequest;
import com.example.neo_bank.api.model.Account;
import com.example.neo_bank.api.model.User;
import com.example.neo_bank.api.repository.AccountRepository;
import com.example.neo_bank.api.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void TransferMoney_Success() {
        Account origen = new Account();
        origen.setId(1L);
        origen.setIban("ES001");
        origen.setBalance(new BigDecimal("1000"));
        origen.setUser(new User());

        Account destino = new Account();
        destino.setId(2L);
        destino.setIban("ES002");
        destino.setBalance(new BigDecimal("200"));

        TransferRequest request = new TransferRequest();
        request.setFromIban("ES001");
        request.setToIban("ES002");
        request.setAmount(new BigDecimal("100"));


        when(accountRepository.findByIban("ES001")).thenReturn(Optional.of(origen));
        when(accountRepository.findByIban("ES002")).thenReturn(Optional.of(destino));
        when(transactionRepository.getDailyOutgoingSum(any(), any())).thenReturn(BigDecimal.ZERO);

        transactionService.transferMoney(request);

        assertEquals(new BigDecimal("900"), origen.getBalance());
        assertEquals(new BigDecimal("300"), destino.getBalance());

        verify(accountRepository, times(2)).save(any(Account.class));
    }

    @Test
    void transferMoney_SelfTransfer_ShouldFail() {
        TransferRequest request = new TransferRequest();
        request.setFromIban("ES001");
        request.setToIban("ES001");
        request.setAmount(new BigDecimal("50"));
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.transferMoney(request);
        });
        assertTrue(exception.getMessage().contains("mismo"));
    }

    @Test
    void transferMoney_NoBalance_ShouldFail() {
        Account origen = new Account();
        origen.setIban("ES001");
        origen.setBalance(new BigDecimal("10"));
        origen.setUser(new User());

        Account destino = new Account();
        destino.setIban("ES002");

        TransferRequest request = new TransferRequest();
        request.setFromIban("ES001");
        request.setToIban("ES002");
        request.setAmount(new BigDecimal("500"));

        when(accountRepository.findByIban("ES001")).thenReturn(Optional.of(origen));
        when(accountRepository.findByIban("ES002")).thenReturn(Optional.of(destino));
        when(transactionRepository.getDailyOutgoingSum(any(), any())).thenReturn(BigDecimal.ZERO);

        assertThrows(RuntimeException.class, () -> {
            transactionService.transferMoney(request);
        });

    }
}
