package com.mikey.ecommerce.coupon;

import com.mikey.ecommerce.coupon.dto.CouponResponse;
import com.mikey.ecommerce.coupon.dto.CreateCouponRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coupons")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @PostMapping
    public CouponResponse create(@Valid @RequestBody CreateCouponRequest request) {
        return couponService.create(request);
    }

    @GetMapping
    public List<CouponResponse> findAll() {
        return couponService.findAll();
    }
}