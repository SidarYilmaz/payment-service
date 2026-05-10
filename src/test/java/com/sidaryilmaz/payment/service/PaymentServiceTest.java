package com.sidaryilmaz.payment.service;

import com.sidaryilmaz.payment.config.KafkaTopics;
import com.sidaryilmaz.payment.dto.PaymentRequest;
import com.sidaryilmaz.payment.dto.PaymentResponse;
import com.sidaryilmaz.payment.event.PaymentEvent;
import com.sidaryilmaz.payment.exception.PaymentNotFoundException;
import com.sidaryilmaz.payment.model.PaymentStatus;
import com.sidaryilmaz.payment.repository.PaymentStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentEventPublisher eventPublisher;

    private PaymentStore paymentStore;
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        paymentStore = new PaymentStore();
        paymentService = new PaymentService(paymentStore, eventPublisher);
    }

    @Test
    void initiateCreatesPendingPaymentAndPublishesEvent() {
        PaymentRequest request = new PaymentRequest("TR001", "TR002", new BigDecimal("500.00"), "TRY");

        PaymentResponse response = paymentService.initiate(request);

        assertThat(response.status()).isEqualTo(PaymentStatus.PENDING);
        assertThat(response.paymentId()).isNotBlank();

        ArgumentCaptor<PaymentEvent> captor = ArgumentCaptor.forClass(PaymentEvent.class);
        verify(eventPublisher).publish(org.mockito.ArgumentMatchers.eq(KafkaTopics.PAYMENTS_INITIATED), captor.capture());
        assertThat(captor.getValue().status()).isEqualTo(PaymentStatus.PENDING);
        assertThat(captor.getValue().amount()).isEqualByComparingTo("500.00");
    }

    @Test
    void settleMovesPaymentToSettledAndPublishesEvent() {
        PaymentResponse initiated = paymentService.initiate(
                new PaymentRequest("TR001", "TR002", new BigDecimal("100.00"), "TRY"));

        paymentService.settle(initiated.paymentId());

        PaymentResponse settled = paymentService.getPayment(initiated.paymentId());
        assertThat(settled.status()).isEqualTo(PaymentStatus.SETTLED);
        verify(eventPublisher).publish(org.mockito.ArgumentMatchers.eq(KafkaTopics.PAYMENTS_SETTLED),
                org.mockito.ArgumentMatchers.any(PaymentEvent.class));
    }

    @Test
    void settleThrowsForUnknownPayment() {
        assertThatThrownBy(() -> paymentService.settle("missing-id"))
                .isInstanceOf(PaymentNotFoundException.class);
    }

    @Test
    void getPaymentThrowsForUnknownPayment() {
        assertThatThrownBy(() -> paymentService.getPayment("missing-id"))
                .isInstanceOf(PaymentNotFoundException.class);
    }
}
