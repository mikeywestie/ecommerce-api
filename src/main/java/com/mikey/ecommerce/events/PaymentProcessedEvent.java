package com.mikey.ecommerce.events;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentProcessedEvent(
    Long paymentId,
    Long orderId,
    String paymentMethod,
    String status,
    BigDecimal amount,
    Instant paidAt
) {}
