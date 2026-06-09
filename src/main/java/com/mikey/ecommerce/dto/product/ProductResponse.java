package com.mikey.ecommerce.dto.product;

import java.math.BigDecimal;
import java.time.Instant;

public record ProductResponse(
        Long id,
        String name,
        String description,
        String category,
        String imageUrl,
        boolean active,
        BigDecimal price,
        Instant createdAt
) {}