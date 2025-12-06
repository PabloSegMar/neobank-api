package com.example.neo_bank.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class TransferRequest {
    @NotBlank(message = "El IBAN de origen es obligatorio")
    private String fromIban; // El que paga

    @NotBlank(message = "El IBAN de destino es obligatorio")
    private String toIban; // El que recibe

    @NotNull(message = "Debes ingresar una cantidad obligatoriamente")
    @DecimalMin(value = "0.01", message = "La cantidad mínima es 0.01")
    private BigDecimal amount; // Cantidad de dinero

    public TransferRequest() {
    }

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
