package com.mikey.ecommerce.events;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderEventProducer {

    private static final String TOPIC = "order-created";
    private static final String PAYMENT_PROCESSED_TOPIC = "payment-processed";
    private static final String COUPON_APPLIED_TOPIC = "coupon-applied";

    private final KafkaTemplate<String,Object> kafkaTemplate;

    public OrderEventProducer(
            KafkaTemplate<String,Object> kafkaTemplate
    ) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(PaymentProcessedEvent event) {
        kafkaTemplate.send(
                PAYMENT_PROCESSED_TOPIC,
                event.orderId().toString(),
                event
        );
    }

    public void publish(
            OrderCreatedEvent event
    ) {
        kafkaTemplate.send(
                TOPIC,
                event.orderId().toString(),
                event
        );
    }

    public void publish(CouponAppliedEvent event) {
        kafkaTemplate.send(
                COUPON_APPLIED_TOPIC,
                event.orderId().toString(),
                event
        );
    }
}