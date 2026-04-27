package com.mikey.ecommerce.dto.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
        Long id,
        String customerName,
        String customerEmail,
        String status,
        BigDecimal totalAmount,
        String couponCode,
        BigDecimal discountAmount,
        Instant createdAt,
        List<OrderItemResponse> items
) {}
