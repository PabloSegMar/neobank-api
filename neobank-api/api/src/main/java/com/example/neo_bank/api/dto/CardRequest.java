package com.example.neo_bank.api.dto;

import com.example.neo_bank.api.model.CardType;

public class CardRequest {
    private Long accountId;
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
