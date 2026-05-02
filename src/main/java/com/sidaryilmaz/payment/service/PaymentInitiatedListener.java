package com.sidaryilmaz.payment.service;

import com.sidaryilmaz.payment.config.KafkaTopics;
import com.sidaryilmaz.payment.event.PaymentEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentInitiatedListener {

    private static final Logger log = LoggerFactory.getLogger(PaymentInitiatedListener.class);

    private final PaymentService paymentService;

    public PaymentInitiatedListener(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @KafkaListener(topics = KafkaTopics.PAYMENTS_INITIATED, groupId = "payment-settlement")
    public void onPaymentInitiated(PaymentEvent event) {
        log.info("Settling payment {} for {} {}", event.paymentId(), event.amount(), event.currency());
        paymentService.settle(event.paymentId());
    }
}
