package com.mikey.ecommerce.coupon;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DemoCouponSeeder implements CommandLineRunner {

  private final CouponRepository couponRepository;

  public DemoCouponSeeder(CouponRepository couponRepository) {
    this.couponRepository = couponRepository;
  }

  @Override
  @Transactional
  public void run(String... args) {
    seedCoupon("SAVE10", CouponType.PERCENTAGE, new BigDecimal("10.00"), "2030-12-31T23:59:59Z");

    seedCoupon(
        "WELCOME250", CouponType.FIXED_AMOUNT, new BigDecimal("250.00"), "2030-12-31T23:59:59Z");
  }

  private void seedCoupon(String code, CouponType type, BigDecimal value, String expiresAt) {
    if (couponRepository.findByCodeIgnoreCase(code).isPresent()) {
      return;
    }

    try {
      Coupon coupon = new Coupon(code, type, value, Instant.parse(expiresAt));

      couponRepository.save(coupon);
    } catch (DateTimeParseException ex) {
      throw new IllegalStateException("Invalid demo coupon expiry date", ex);
    }
  }
}
