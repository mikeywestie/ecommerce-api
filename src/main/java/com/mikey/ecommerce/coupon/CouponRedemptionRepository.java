package com.mikey.ecommerce.coupon;

import com.mikey.ecommerce.security.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRedemptionRepository extends JpaRepository<CouponRedemption, Long> {

  long countByCoupon(Coupon coupon);

  long countByCouponAndUser(Coupon coupon, AppUser user);
}
