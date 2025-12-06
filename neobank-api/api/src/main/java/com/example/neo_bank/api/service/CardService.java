package com.example.neo_bank.api.service;

import com.example.neo_bank.api.model.Account;
import com.example.neo_bank.api.model.Card;
import com.example.neo_bank.api.model.CardType;
import com.example.neo_bank.api.repository.AccountRepository;
import com.example.neo_bank.api.repository.CardRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Service
public class CardService {
    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;
    private final Random random = new Random();

    public CardService(CardRepository cardRepository, AccountRepository accountRepository) {
        this.cardRepository = cardRepository;
        this.accountRepository = accountRepository;
    }

    public Card createCard(Long accountId, CardType type) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        Card card = new Card();
        card.setType(type);
        card.setAccount(account);
        card.setExpirationDate(LocalDate.now().plusYears(5));
        card.setCvv(generateRandomDigits(3));

        String cardNumber;
        do {
            cardNumber = generateRandomDigits(16);
        } while (cardRepository.existsByCardNumber(cardNumber));

        card.setCardNumber(cardNumber);
        return cardRepository.save(card);
    }

    public List<Card> getCardsByAccountId(Long accountId) {
        return cardRepository.findByAccountId(accountId);
    }

    private String generateRandomDigits(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();

    }
}
