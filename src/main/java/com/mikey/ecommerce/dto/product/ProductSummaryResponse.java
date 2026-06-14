package com.mikey.ecommerce.dto.product;

import java.math.BigDecimal;

public record ProductSummaryResponse(
    Long id,
    String name,
    String category,
    String subcategory,
    String brand,
    String imageUrl,
    boolean active,
    BigDecimal price) {}
