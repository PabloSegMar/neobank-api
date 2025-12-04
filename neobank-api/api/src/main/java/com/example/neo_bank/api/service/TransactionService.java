package com.example.neo_bank.api.service;

import com.example.neo_bank.api.dto.TransferRequest;
import com.example.neo_bank.api.model.Account;
import com.example.neo_bank.api.repository.AccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransactionService {
    private final AccountRepository accountRepository;

    public TransactionService(AccountRepository accountRepository){
        this.accountRepository = accountRepository;
    }

    // Esta anotación obliga a que se sume la cantidad del que recibe
    // y se reste la cantidad del que paga
    // si hay algún fallo en una de estas operaciones se deshace la acción que ya sé haya realizado
    @Transactional
    public void transferMoney(TransferRequest request){

        // 1. Busco cuenta origen (la que paga)
        Account fromAccount = accountRepository.findByIban(request.getFromIban()).orElseThrow(() -> new RuntimeException("La cuenta origen no ha sido encontrada"));

        // 2. Buscar cuenta destino (la que recibe)
        Account toAccount = accountRepository.findByIban(request.getFromIban()).orElseThrow(() -> new RuntimeException("La cuenta destino no ha sido encontrada"));

        // 3. Validar si hay saldo suficiente
        // compareTo devuelve: -1 (menor), 0 (igual), 1 (mayor)

        if(fromAccount.getBalance().compareTo(request.getAmount()) < 0){
            throw new RuntimeException("No hay saldo suficiente");
        }

        // 4. Restar a uno, Sumar a otro
        // BigDecimal es inmutable, por tanto, tengo que guardar el resultado del .subtract()
        BigDecimal newBalanceTo = toAccount.getBalance().subtract(request.getAmount());
        fromAccount.setBalance(newBalanceTo);

        // 5. Guardar los cambios en BBDD
            accountRepository.save(fromAccount);
            accountRepository.save(toAccount);
    }
}
