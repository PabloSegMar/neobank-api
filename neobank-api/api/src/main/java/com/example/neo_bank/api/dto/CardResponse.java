package com.example.neo_bank.api.dto;

import com.example.neo_bank.api.model.Card;

public class CardResponse {
    private String maskedNumber;
    private String type;
    private String expirationDate;
    private String cvv;

    public CardResponse(Card card) {
        this.maskedNumber = "*** **** **** " + card.getCardNumber().substring(12);
        this.type = card.getType().toString();
        this.expirationDate = card.getExpirationDate().toString();
        this.cvv = card.getCvv();
    }

    public String getMaskedNumber() {
        return maskedNumber;
    }

    public void setMaskedNumber(String maskedNumber) {
        this.maskedNumber = maskedNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }
}
