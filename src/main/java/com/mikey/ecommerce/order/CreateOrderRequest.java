package com.mikey.ecommerce.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateOrderRequest(
        @NotBlank String customerName,
        @Email @NotBlank String customerEmail,
        @NotEmpty List<@Valid OrderItemRequest> items
) {}
