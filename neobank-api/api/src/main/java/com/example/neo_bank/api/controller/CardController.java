package com.example.neo_bank.api.controller;

import com.example.neo_bank.api.dto.CardRequest;
import com.example.neo_bank.api.dto.CardResponse;
import com.example.neo_bank.api.model.Account;
import com.example.neo_bank.api.model.Card;
import com.example.neo_bank.api.service.AccountService;
import com.example.neo_bank.api.service.CardService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cards")
public class CardController {
    private final CardService cardService;
    private final AccountService accountService;

    public CardController(CardService cardService, AccountService accountService) {
        this.cardService = cardService;
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<?> createCard(@RequestBody @Valid CardRequest request) {
        if (!isOwnerAdmin(request.getAccountId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No puedes pedir tarjetas para cuentas ajenas");
        }

        Card newCard = cardService.createCard(request.getAccountId(), request.getCardType());
        return ResponseEntity.ok(new CardResponse(newCard));
    }


    @GetMapping
    public ResponseEntity<?> getAllCards() {
        List<Card> cards = cardService.getAllCards();

        List<CardResponse> response = cards.stream().map(CardResponse::new).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<?> getCardsByAccountId(@PathVariable Long accountId) {
        if (!isOwnerAdmin(accountId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para ver estas tarjetas");
        }
        List<Card> cards = cardService.getCardsByAccountId(accountId);

        List<CardResponse> response = cards.stream().map(CardResponse::new).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    private boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

    }

    private boolean isOwnerAdmin(Long accountId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String emailLogueado = auth.getName();
        if (isAdmin()) return true;

        Account account = accountService.getAccountById(accountId);
        return account.getUser().getEmail().equals(emailLogueado);
    }

}
