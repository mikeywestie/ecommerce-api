package com.mikey.ecommerce.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderEventProducer {

    private static final Logger log =
            LoggerFactory.getLogger(OrderEventProducer.class);

    private static final String TOPIC = "order-created";
    private static final String PAYMENT_PROCESSED_TOPIC = "payment-processed";
    private static final String COUPON_APPLIED_TOPIC = "coupon-applied";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OrderEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(PaymentProcessedEvent event) {
        publishSafely(
                PAYMENT_PROCESSED_TOPIC,
                event.orderId().toString(),
                event
        );
    }

    public void publish(OrderCreatedEvent event) {
        publishSafely(
                TOPIC,
                event.orderId().toString(),
                event
        );
    }

    public void publish(CouponAppliedEvent event) {
        publishSafely(
                COUPON_APPLIED_TOPIC,
                event.orderId().toString(),
                event
        );
    }

    private void publishSafely(String topic, String key, Object event) {
        try {
            kafkaTemplate.send(topic, key, event)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.warn(
                                    "Kafka publish failed for topic={} key={}: {}",
                                    topic,
                                    key,
                                    ex.getMessage()
                            );
                            return;
                        }

                        log.info(
                                "Kafka event published topic={} key={}",
                                topic,
                                key
                        );
                    });
        } catch (Exception ex) {
            log.warn(
                    "Kafka publish skipped for topic={} key={}: {}",
                    topic,
                    key,
                    ex.getMessage()
            );
        }
    }
}