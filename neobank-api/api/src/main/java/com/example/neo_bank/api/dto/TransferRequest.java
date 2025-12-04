package com.example.neo_bank.api.dto;

import java.math.BigDecimal;

public class TransferRequest {
    private String fromIban; // El que paga
    private String toIban; // El que recibe
    private BigDecimal amount; // Cantidad de dinero

    public TransferRequest(){}

    public String getFromIban() {
        return fromIban;
    }

    public void setFromIban(String fromIban) {
        this.fromIban = fromIban;
    }

    public String getToIban() {
        return toIban;
    }

    public void setToIban(String toIban) {
        this.toIban = toIban;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
