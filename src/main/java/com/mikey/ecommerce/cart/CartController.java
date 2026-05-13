package com.mikey.ecommerce.cart;

import com.mikey.ecommerce.cart.dto.AddCartItemRequest;
import com.mikey.ecommerce.cart.dto.CartResponse;
import com.mikey.ecommerce.dto.order.OrderResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller responsible for shopping cart management.
 *
 * <p>This controller provides endpoints for:</p>
 * <ul>
 *     <li>Retrieving the authenticated user's cart</li>
 *     <li>Adding items to the cart</li>
 *     <li>Applying coupon codes</li>
 *     <li>Checking out the cart to create an order</li>
 * </ul>
 *
 * <p>All operations are performed in the context of the currently
 * authenticated user.</p>
 */
@RestController
@RequestMapping("/api/cart")
@Tag(
        name = "Shopping Cart",
        description = "Shopping cart endpoints for managing cart items, applying coupons, and completing checkout."
)
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    /**
     * Retrieves the authenticated user's shopping cart.
     *
     * @param authentication current authenticated user
     * @return cart response
     */
    @Operation(
            summary = "Get cart",
            description = "Returns the current authenticated user's shopping cart and all associated items."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Cart retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CartResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated"
            )
    })
    @GetMapping
    public CartResponse getCart(Authentication authentication) {
        return cartService.getCart(authentication.getName());
    }

    /**
     * Adds an item to the authenticated user's cart.
     *
     * @param authentication current authenticated user
     * @param request item addition request
     * @return updated cart response
     */
    @Operation(
            summary = "Add item to cart",
            description = "Adds a product and quantity to the authenticated user's shopping cart."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Item added to cart successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CartResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failed or business rules were violated"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated"
            )
    })
    @PostMapping("/items")
    public CartResponse addItem(
            Authentication authentication,

            @Parameter(description = "Request containing product ID and quantity")
            @Valid
            @RequestBody AddCartItemRequest request
    ) {
        return cartService.addItem(
                authentication.getName(),
                request
        );
    }

    /**
     * Converts the cart into a customer order.
     *
     * @param authentication current authenticated user
     * @return created order response
     */
    @Operation(
            summary = "Checkout cart",
            description = "Creates a customer order from the authenticated user's cart and clears the cart after successful checkout."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Checkout completed successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OrderResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Cart is empty or checkout failed"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated"
            )
    })
    @PostMapping("/checkout")
    public OrderResponse checkout(Authentication authentication) {
        return cartService.checkout(authentication.getName());
    }

    /**
     * Applies a coupon code to the cart.
     *
     * @param authentication current authenticated user
     * @param code coupon code
     * @return updated cart response with discount applied
     */
    @Operation(
            summary = "Apply coupon",
            description = "Applies a coupon code to the authenticated user's shopping cart."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Coupon applied successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CartResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Coupon is invalid or expired"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated"
            )
    })
    @PostMapping("/apply-coupon/{code}")
    public CartResponse applyCoupon(
            Authentication authentication,

            @Parameter(description = "Coupon code to apply", example = "SAVE10")
            @PathVariable String code
    ) {
        return cartService.applyCoupon(
                authentication.getName(),
                code
        );
    }
}