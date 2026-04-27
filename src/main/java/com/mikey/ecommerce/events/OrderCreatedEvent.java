package com.mikey.ecommerce.events;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderCreatedEvent(
        Long orderId,
        String customerEmail,
        BigDecimal totalAmount,
        Instant createdAt
) {}