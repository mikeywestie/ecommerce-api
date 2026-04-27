package com.mikey.ecommerce.cart;

import com.mikey.ecommerce.cart.dto.AddCartItemRequest;
import com.mikey.ecommerce.cart.dto.CartResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.mikey.ecommerce.dto.order.OrderResponse;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(
            CartService cartService
    ) {
        this.cartService = cartService;
    }

    @GetMapping
    public CartResponse getCart(
            Authentication authentication
    ) {
        return cartService.getCart(
                authentication.getName()
        );
    }

    @PostMapping("/items")
    public CartResponse addItem(
            Authentication authentication,
            @Valid @RequestBody AddCartItemRequest request
    ) {
        return cartService.addItem(
                authentication.getName(),
                request
        );
    }

    @PostMapping("/checkout")
    public OrderResponse checkout(Authentication authentication) {
        return cartService.checkout(authentication.getName());
    }
}