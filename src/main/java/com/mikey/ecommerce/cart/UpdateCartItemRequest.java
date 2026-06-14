package com.mikey.ecommerce.cart;

import jakarta.validation.constraints.Positive;

public record UpdateCartItemRequest(@Positive int quantity) {}
