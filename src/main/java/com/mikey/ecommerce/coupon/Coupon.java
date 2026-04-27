package com.mikey.ecommerce.coupon;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "coupons")
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private CouponType type;

    @Column(nullable=false, precision = 12, scale = 2)
    private BigDecimal value;

    @Column(nullable=false)
    private boolean active = true;

    @Column(nullable=false)
    private Instant expiresAt;

    protected Coupon() {}

    public Coupon(String code, CouponType type, BigDecimal value, Instant expiresAt) {
        this.code = code.toUpperCase();
        this.type = type;
        this.value = value;
        this.expiresAt = expiresAt;
    }

    public Long getId() { return id; }
    public String getCode() { return code; }
    public CouponType getType() { return type; }
    public BigDecimal getValue() { return value; }
    public boolean isActive() { return active; }
    public Instant getExpiresAt() { return expiresAt; }

    public void deactivate() {
        this.active = false;
    }

    public boolean isExpired() {
        return expiresAt.isBefore(Instant.now());
    }
}