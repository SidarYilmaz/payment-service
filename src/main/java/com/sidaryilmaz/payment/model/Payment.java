package com.sidaryilmaz.payment.model;

import java.math.BigDecimal;
import java.time.Instant;

public class Payment {

    private final String paymentId;
    private final String debtorIban;
    private final String creditorIban;
    private final BigDecimal amount;
    private final String currency;
    private final Instant createdAt;
    private PaymentStatus status;

    public Payment(String paymentId, String debtorIban, String creditorIban,
                   BigDecimal amount, String currency) {
        this.paymentId = paymentId;
        this.debtorIban = debtorIban;
        this.creditorIban = creditorIban;
        this.amount = amount;
        this.currency = currency;
        this.createdAt = Instant.now();
        this.status = PaymentStatus.PENDING;
    }

    public void settle() {
        this.status = PaymentStatus.SETTLED;
    }

    public void reject() {
        this.status = PaymentStatus.REJECTED;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getDebtorIban() {
        return debtorIban;
    }

    public String getCreditorIban() {
        return creditorIban;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public PaymentStatus getStatus() {
        return status;
    }
}
