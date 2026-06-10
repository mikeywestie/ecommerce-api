package com.mikey.ecommerce.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
    private final boolean kafkaEnabled;

    public OrderEventProducer(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${app.events.kafka-enabled:false}") boolean kafkaEnabled
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaEnabled = kafkaEnabled;
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
        if (!kafkaEnabled) {
            log.info(
                    "Kafka publishing disabled. Skipping event topic={} key={}",
                    topic,
                    key
            );
            return;
        }

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