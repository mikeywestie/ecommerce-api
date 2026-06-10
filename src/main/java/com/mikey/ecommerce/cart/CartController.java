package com.mikey.ecommerce.cart;

import com.mikey.ecommerce.cart.dto.AddCartItemRequest;
import com.mikey.ecommerce.cart.dto.CartResponse;
import com.mikey.ecommerce.dto.order.OrderResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    public CartResponse getCart(Authentication authentication) {
        return cartService.getCart(authentication.getName());
    }

    @PostMapping("/items")
    public CartResponse addItem(
            Authentication authentication,
            @Valid @RequestBody AddCartItemRequest request
    ) {
        return cartService.addItem(authentication.getName(), request);
    }

    @PutMapping("/items/{cartItemId}")
    public CartResponse updateItemQuantity(
            Authentication authentication,
            @PathVariable Long cartItemId,
            @Valid @RequestBody UpdateCartItemRequest request
    ) {
        return cartService.updateItemQuantity(
                authentication.getName(),
                cartItemId,
                request
        );
    }

    @DeleteMapping("/items/{cartItemId}")
    public CartResponse removeItem(
            Authentication authentication,
            @PathVariable Long cartItemId
    ) {
        return cartService.removeItem(
                authentication.getName(),
                cartItemId
        );
    }

    @PostMapping("/checkout")
    public OrderResponse checkout(Authentication authentication) {
        return cartService.checkout(authentication.getName());
    }

    @PostMapping("/apply-coupon/{code}")
    public CartResponse applyCoupon(
            Authentication authentication,
            @PathVariable String code
    ) {
        return cartService.applyCoupon(authentication.getName(), code);
    }

    @DeleteMapping("/coupon")
    public CartResponse removeCoupon(Authentication authentication) {
        return cartService.removeCoupon(authentication.getName());
    }
}