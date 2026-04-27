package com.mikey.ecommerce.coupon;

import com.mikey.ecommerce.common.ApiException;
import com.mikey.ecommerce.coupon.dto.CouponResponse;
import com.mikey.ecommerce.coupon.dto.CreateCouponRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CouponService {

    private final CouponRepository couponRepository;

    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    public CouponResponse create(CreateCouponRequest request) {
        if (couponRepository.existsByCodeIgnoreCase(request.code())) {
            throw new ApiException("Coupon code already exists");
        }

        Coupon coupon = new Coupon(
                request.code(),
                request.type(),
                request.value(),
                request.expiresAt()
        );

        return toResponse(couponRepository.save(coupon));
    }

    public List<CouponResponse> findAll() {
        return couponRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private CouponResponse toResponse(Coupon coupon) {
        return new CouponResponse(
                coupon.getId(),
                coupon.getCode(),
                coupon.getType(),
                coupon.getValue(),
                coupon.isActive(),
                coupon.getExpiresAt()
        );
    }
}