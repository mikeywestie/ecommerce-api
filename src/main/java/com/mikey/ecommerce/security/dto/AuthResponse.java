package com.mikey.ecommerce.security.dto;

public record AuthResponse(
        String token,
        String tokenType,
        String email,
        String role
) {}
