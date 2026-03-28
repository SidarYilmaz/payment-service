package com.sidaryilmaz.payment.config;

public final class KafkaTopics {

    public static final String PAYMENTS_INITIATED = "payments.initiated";
    public static final String PAYMENTS_SETTLED = "payments.settled";

    private KafkaTopics() {
    }
}
