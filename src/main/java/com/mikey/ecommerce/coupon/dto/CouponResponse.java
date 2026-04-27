package com.mikey.ecommerce.coupon.dto;

import com.mikey.ecommerce.coupon.CouponType;

import java.math.BigDecimal;
import java.time.Instant;

public record CouponResponse(
        Long id,
        String code,
        CouponType type,
        BigDecimal value,
        boolean active,
        Instant expiresAt
) {}