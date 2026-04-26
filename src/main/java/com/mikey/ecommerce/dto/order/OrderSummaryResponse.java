package com.mikey.ecommerce.dto.order;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderSummaryResponse(
        Long id,
        String customerName,
        String status,
        BigDecimal totalAmount,
        Instant createdAt
) {}
