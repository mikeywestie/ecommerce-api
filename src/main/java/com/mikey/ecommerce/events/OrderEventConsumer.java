package com.mikey.ecommerce.events;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventConsumer {

    @KafkaListener(
            topics = "order-created",
            groupId = "ecommerce-group"
    )
    public void consume(
            OrderCreatedEvent event
    ) {

        System.out.println(
                "EVENT RECEIVED -> " + event
        );
    }

    @KafkaListener(
            topics = "payment-processed",
            groupId = "ecommerce-group"
    )
    public void consumePaymentProcessed(
            PaymentProcessedEvent event
    ) {
        System.out.println(
                "PAYMENT EVENT RECEIVED -> " + event
        );
    }

    @KafkaListener(
            topics = "coupon-applied",
            groupId = "ecommerce-group"
    )
    public void consumeCouponApplied(
            CouponAppliedEvent event
    ) {
        System.out.println(
                "COUPON EVENT RECEIVED -> " + event
        );
    }
}