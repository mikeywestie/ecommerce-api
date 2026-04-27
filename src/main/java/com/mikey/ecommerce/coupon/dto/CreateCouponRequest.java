package com.mikey.ecommerce.coupon.dto;

import com.mikey.ecommerce.coupon.CouponType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;

public record CreateCouponRequest(
        @NotBlank String code,
        @NotNull CouponType type,
        @NotNull @DecimalMin("0.01") BigDecimal value,
        @NotNull @Future Instant expiresAt
) {}