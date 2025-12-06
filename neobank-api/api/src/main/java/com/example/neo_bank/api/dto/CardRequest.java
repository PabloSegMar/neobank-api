package com.example.neo_bank.api.dto;

import com.example.neo_bank.api.model.CardType;
import jakarta.validation.constraints.NotNull;

public class CardRequest {
    @NotNull(message = "El ID de la cuenta es necesario")
    private Long accountId;

    @NotNull(message = "El tipo de tarjeta (DEBIT/CREDIT) es obligatorio")
    private CardType cardType;

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public CardType getCardType() {
        return cardType;
    }

    public void setCardType(CardType cardType) {
        this.cardType = cardType;
    }
}
