package com.mikey.ecommerce.inventory;

import jakarta.validation.constraints.PositiveOrZero;

public record StockRequest(@PositiveOrZero int quantity) {}
