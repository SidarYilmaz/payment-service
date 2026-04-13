package com.sidaryilmaz.payment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

public record PaymentRequest(

        @NotBlank
        String debtorIban,

        @NotBlank
        String creditorIban,

        @NotNull
        @DecimalMin(value = "0.01", message = "payment amount must be greater than zero")
        BigDecimal amount,

        @NotBlank
        @Pattern(regexp = "[A-Z]{3}", message = "currency must be a 3-letter ISO code")
        String currency
) {
}
