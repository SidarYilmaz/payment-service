package com.sidaryilmaz.payment.repository;

import com.sidaryilmaz.payment.model.Payment;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class PaymentStore {

    private final ConcurrentHashMap<String, Payment> payments = new ConcurrentHashMap<>();

    public Payment save(Payment payment) {
        payments.put(payment.getPaymentId(), payment);
        return payment;
    }

    public Optional<Payment> findById(String paymentId) {
        return Optional.ofNullable(payments.get(paymentId));
    }
}
