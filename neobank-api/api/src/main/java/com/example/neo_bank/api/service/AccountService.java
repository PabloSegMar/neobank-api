package com.example.neo_bank.api.service;

import com.example.neo_bank.api.model.Account;
import com.example.neo_bank.api.model.User;
import com.example.neo_bank.api.repository.UserRepository;
import org.springframework.stereotype.Service;
import com.example.neo_bank.api.repository.AccountRepository;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepository accountRespository;
    private final UserRepository userRepository;

    public AccountService(AccountRepository accountRespository, UserRepository userRepository){
        this.accountRespository = accountRespository;
        this.userRepository = userRepository;
    }

    public Account createAccount(Long userId){
        //Si no existe el usuario se lanza un error
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Usuario con id " + userId + " no encontrado"));

        // Creo una nueva cuenta
        Account account = new Account();
        account.setBalance(BigDecimal.ZERO); // Se empieza con 0 euros
        account.setUser(user); // Vinculamos la cuenta al usuario

        // Genero un IBAN aleatorio
        String randomIBAN = "ES " + UUID.randomUUID().toString().substring(0,20).toUpperCase();
        account.setIban(randomIBAN);

        return accountRespository.save(account);


    }
}
