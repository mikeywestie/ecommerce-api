package com.mikey.ecommerce.events;

import java.math.BigDecimal;
import java.time.Instant;

public record CouponAppliedEvent(
        Long orderId,
        String couponCode,
        BigDecimal discountAmount,
        Instant appliedAt
) {}