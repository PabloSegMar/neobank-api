package com.example.neo_bank.api.service;

import com.example.neo_bank.api.dto.TransferRequest;
import com.example.neo_bank.api.model.Account;
import com.example.neo_bank.api.model.Transaction;
import com.example.neo_bank.api.model.User;
import com.example.neo_bank.api.notification.NotificationService;
import com.example.neo_bank.api.repository.AccountRepository;
import com.example.neo_bank.api.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void TransferMoney_Success() {
        ReflectionTestUtils.setField(transactionService, "dailyLimit", new BigDecimal("2000"));

        Account origen = new Account();
        origen.setId(1L);
        origen.setIban("ES001");
        origen.setBalance(new BigDecimal("1000"));
        User userOrigen = new User();
        userOrigen.setEmail("origen@test.com");
        userOrigen.setName("Origen");
        origen.setUser(userOrigen);

        Account destino = new Account();
        destino.setId(2L);
        destino.setIban("ES002");
        destino.setBalance(new BigDecimal("200"));
        User userDestino = new User();
        userDestino.setEmail("destino@test.com");
        userDestino.setName("Destino");
        destino.setUser(userDestino);

        TransferRequest request = new TransferRequest();
        request.setFromIban("ES001");
        request.setToIban("ES002");
        request.setAmount(new BigDecimal("100"));

        when(accountRepository.findByIbanWithLock("ES001")).thenReturn(Optional.of(origen));
        when(accountRepository.findByIbanWithLock("ES002")).thenReturn(Optional.of(destino));
        when(transactionRepository.getDailyOutgoingSum(any(), any())).thenReturn(BigDecimal.ZERO);

        transactionService.transferMoney(request);

        assertEquals(new BigDecimal("900"), origen.getBalance());
        assertEquals(new BigDecimal("300"), destino.getBalance());

        verify(accountRepository, times(2)).save(any(Account.class));
        verify(transactionRepository, times(2)).save(any(Transaction.class));

        verify(notificationService, times(2)).sendNotification(anyString(), anyString());
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
        verify(accountRepository, never()).findByIbanWithLock(any());
    }

    @Test
    void transferMoney_NoBalance_ShouldFail() {
        Account origen = new Account();
        origen.setIban("ES001");
        origen.setBalance(new BigDecimal("10"));
        User user = new User(); user.setEmail("test@test.com");
        origen.setUser(user);

        Account destino = new Account();
        destino.setIban("ES002");

        TransferRequest request = new TransferRequest();
        request.setFromIban("ES001");
        request.setToIban("ES002");
        request.setAmount(new BigDecimal("500"));

        when(accountRepository.findByIbanWithLock("ES001")).thenReturn(Optional.of(origen));
        when(accountRepository.findByIbanWithLock("ES002")).thenReturn(Optional.of(destino));
        when(transactionRepository.getDailyOutgoingSum(any(), any())).thenReturn(BigDecimal.ZERO);

        ReflectionTestUtils.setField(transactionService, "dailyLimit", new BigDecimal("2000"));

        assertThrows(RuntimeException.class, () -> {
            transactionService.transferMoney(request);
        });

        verify(accountRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }
}