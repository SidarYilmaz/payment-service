package com.sidaryilmaz.payment.dto;

import com.sidaryilmaz.payment.model.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentResponse(
        String paymentId,
        String debtorIban,
        String creditorIban,
        BigDecimal amount,
        String currency,
        PaymentStatus status,
        Instant createdAt
) {
}
