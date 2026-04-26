package com.mikey.ecommerce.dto.payment;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentResponse(
        Long id,
        Long orderId,
        String paymentMethod,
        String paymentStatus,
        BigDecimal amount,
        Instant paidAt
) {}
