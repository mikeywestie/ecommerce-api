package com.mikey.ecommerce.security.dto;

import com.mikey.ecommerce.security.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
    @NotBlank String name, @Email @NotBlank String email, @NotBlank String password, Role role) {}
