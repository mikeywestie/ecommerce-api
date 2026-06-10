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

    @Column(nullable = false)
    private boolean reusable = true;

    private Integer maxUsesPerCustomer;

    private Integer maxTotalUses;

    protected Coupon() {}

    public Coupon(String code, CouponType type, BigDecimal value, Instant expiresAt) {
        this(code, type, value, expiresAt, true, null, null);
    }

    public Coupon(
            String code,
            CouponType type,
            BigDecimal value,
            Instant expiresAt,
            boolean reusable,
            Integer maxUsesPerCustomer,
            Integer maxTotalUses
    ) {
        this.code = code.toUpperCase();
        this.type = type;
        this.value = value;
        this.expiresAt = expiresAt;
        this.reusable = reusable;
        this.maxUsesPerCustomer = maxUsesPerCustomer;
        this.maxTotalUses = maxTotalUses;
    }

    public Long getId() { return id; }
    public String getCode() { return code; }
    public CouponType getType() { return type; }
    public BigDecimal getValue() { return value; }
    public boolean isActive() { return active; }
    public Instant getExpiresAt() { return expiresAt; }
    public boolean isReusable() { return reusable; }
    public Integer getMaxUsesPerCustomer() { return maxUsesPerCustomer; }
    public Integer getMaxTotalUses() { return maxTotalUses; }

    public void deactivate() {
        this.active = false;
    }

    public boolean isExpired() {
        return expiresAt.isBefore(Instant.now());
    }
}