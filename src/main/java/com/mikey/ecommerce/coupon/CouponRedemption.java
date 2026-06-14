package com.mikey.ecommerce.coupon;

import com.mikey.ecommerce.order.CustomerOrder;
import com.mikey.ecommerce.security.AppUser;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "coupon_redemptions")
public class CouponRedemption {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "coupon_id", nullable = false)
  private Coupon coupon;

  @ManyToOne(optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private AppUser user;

  @ManyToOne(optional = false)
  @JoinColumn(name = "order_id", nullable = false)
  private CustomerOrder order;

  @Column(nullable = false)
  private Instant redeemedAt = Instant.now();

  protected CouponRedemption() {}

  public CouponRedemption(Coupon coupon, AppUser user, CustomerOrder order) {
    this.coupon = coupon;
    this.user = user;
    this.order = order;
  }

  public Long getId() {
    return id;
  }

  public Coupon getCoupon() {
    return coupon;
  }

  public AppUser getUser() {
    return user;
  }

  public CustomerOrder getOrder() {
    return order;
  }

  public Instant getRedeemedAt() {
    return redeemedAt;
  }
}
