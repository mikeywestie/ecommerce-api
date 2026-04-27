package com.mikey.ecommerce.events;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderEventProducer {

    private static final String TOPIC =
            "order-created";

    private final KafkaTemplate<String,Object> kafkaTemplate;

    public OrderEventProducer(
            KafkaTemplate<String,Object> kafkaTemplate
    ) {
        this.kafkaTemplate = kafkaTemplate;
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
}