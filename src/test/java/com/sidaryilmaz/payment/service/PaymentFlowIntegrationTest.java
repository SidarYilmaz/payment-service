package com.sidaryilmaz.payment.service;

import com.sidaryilmaz.payment.dto.PaymentRequest;
import com.sidaryilmaz.payment.dto.PaymentResponse;
import com.sidaryilmaz.payment.model.PaymentStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.Duration;

import static org.awaitility.Awaitility.await;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"payments.initiated", "payments.settled"})
@TestPropertySource(properties = {
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.consumer.auto-offset-reset=earliest"
})
class PaymentFlowIntegrationTest {

    @Autowired
    private PaymentService paymentService;

    @Test
    void initiatedPaymentIsSettledByListener() {
        PaymentResponse initiated = paymentService.initiate(
                new PaymentRequest("TR001", "TR002", new BigDecimal("750.00"), "TRY"));

        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            PaymentResponse current = paymentService.getPayment(initiated.paymentId());
            assertThat(current.status()).isEqualTo(PaymentStatus.SETTLED);
        });
    }
}
