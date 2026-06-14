package com.mikey.ecommerce.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record ProductRequest(
    @NotBlank String name,
    String description,
    @NotBlank String category,
    String subcategory,
    String brand,
    String imageUrl,
    Boolean active,
    @NotNull @PositiveOrZero BigDecimal price,
    @PositiveOrZero Integer initialStock) {}
