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
}