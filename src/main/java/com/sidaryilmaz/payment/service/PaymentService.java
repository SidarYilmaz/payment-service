package com.sidaryilmaz.payment.service;

import com.sidaryilmaz.payment.config.KafkaTopics;
import com.sidaryilmaz.payment.dto.PaymentRequest;
import com.sidaryilmaz.payment.dto.PaymentResponse;
import com.sidaryilmaz.payment.event.PaymentEvent;
import com.sidaryilmaz.payment.exception.PaymentNotFoundException;
import com.sidaryilmaz.payment.model.Payment;
import com.sidaryilmaz.payment.repository.PaymentStore;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentStore paymentStore;
    private final PaymentEventPublisher eventPublisher;

    public PaymentService(PaymentStore paymentStore, PaymentEventPublisher eventPublisher) {
        this.paymentStore = paymentStore;
        this.eventPublisher = eventPublisher;
    }

    public PaymentResponse initiate(PaymentRequest request) {
        Payment payment = new Payment(
                UUID.randomUUID().toString(),
                request.debtorIban(),
                request.creditorIban(),
                request.amount(),
                request.currency()
        );
        paymentStore.save(payment);
        eventPublisher.publish(KafkaTopics.PAYMENTS_INITIATED, toEvent(payment));
        return toResponse(payment);
    }

    public void settle(String paymentId) {
        Payment payment = paymentStore.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));
        payment.settle();
        paymentStore.save(payment);
        eventPublisher.publish(KafkaTopics.PAYMENTS_SETTLED, toEvent(payment));
    }

    public PaymentResponse getPayment(String paymentId) {
        return paymentStore.findById(paymentId)
                .map(this::toResponse)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));
    }

    private PaymentEvent toEvent(Payment payment) {
        return new PaymentEvent(
                payment.getPaymentId(),
                payment.getDebtorIban(),
                payment.getCreditorIban(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getStatus(),
                Instant.now()
        );
    }

    private PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getPaymentId(),
                payment.getDebtorIban(),
                payment.getCreditorIban(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getStatus(),
                payment.getCreatedAt()
        );
    }
}
