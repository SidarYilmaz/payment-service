package com.sidaryilmaz.payment.event;

import com.sidaryilmaz.payment.model.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentEvent(
        String paymentId,
        String debtorIban,
        String creditorIban,
        BigDecimal amount,
        String currency,
        PaymentStatus status,
        Instant occurredAt
) {
}
