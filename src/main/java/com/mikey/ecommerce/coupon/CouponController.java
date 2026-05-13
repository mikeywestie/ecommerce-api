package com.mikey.ecommerce.coupon;

import com.mikey.ecommerce.coupon.dto.CouponResponse;
import com.mikey.ecommerce.coupon.dto.CreateCouponRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller responsible for coupon management.
 *
 * <p>This controller provides endpoints for:</p>
 * <ul>
 *     <li>Creating discount coupons</li>
 *     <li>Retrieving all available coupons</li>
 * </ul>
 *
 * <p>Coupons can be used during checkout to apply discounts to customer orders.</p>
 */
@RestController
@RequestMapping("/api/coupons")
@Tag(
        name = "Coupons",
        description = "Coupon management endpoints for creating and retrieving discount coupons."
)
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    /**
     * Creates a new coupon.
     *
     * @param request coupon creation request
     * @return created coupon response
     */
    @Operation(
            summary = "Create coupon",
            description = "Creates a new discount coupon that can be applied during checkout."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Coupon created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CouponResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failed"
            )
    })
    @PostMapping
    public CouponResponse create(
            @Parameter(description = "Coupon creation request containing code, discount, and expiry details")
            @Valid
            @RequestBody CreateCouponRequest request
    ) {
        return couponService.create(request);
    }

    /**
     * Retrieves all coupons.
     *
     * @return list of coupon responses
     */
    @Operation(
            summary = "Get all coupons",
            description = "Returns a list of all coupons available in the system."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Coupons retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CouponResponse.class)
                    )
            )
    })
    @GetMapping
    public List<CouponResponse> findAll() {
        return couponService.findAll();
    }
}