package com.mikey.ecommerce.cart;

import com.mikey.ecommerce.cart.dto.AddCartItemRequest;
import com.mikey.ecommerce.cart.dto.CartItemResponse;
import com.mikey.ecommerce.cart.dto.CartResponse;
import com.mikey.ecommerce.common.ApiException;
import com.mikey.ecommerce.product.Product;
import com.mikey.ecommerce.product.ProductRepository;
import com.mikey.ecommerce.security.AppUser;
import com.mikey.ecommerce.security.AppUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.mikey.ecommerce.order.CreateOrderRequest;
import com.mikey.ecommerce.order.CustomerOrder;
import com.mikey.ecommerce.order.OrderItemRequest;
import com.mikey.ecommerce.dto.order.OrderResponse;
import com.mikey.ecommerce.mapper.OrderMapper;
import com.mikey.ecommerce.order.OrderService;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final AppUserRepository appUserRepository;
    private final OrderService orderService;

    public CartService(
            CartRepository cartRepository,
            ProductRepository productRepository,
            AppUserRepository appUserRepository,
            OrderService orderService
    ) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.appUserRepository = appUserRepository;
        this.orderService = orderService;
    }

    public CartResponse getCart(String userEmail) {

        AppUser user = findUser(userEmail);

        Cart cart = cartRepository
                .findByUser(user)
                .orElseGet(() ->
                        cartRepository.save(
                                new Cart(user)
                        )
                );

        return toResponse(cart);
    }

    public CartResponse addItem(
            String userEmail,
            AddCartItemRequest request
    ) {

        AppUser user = findUser(userEmail);

        Product product = productRepository
                .findById(request.productId())
                .orElseThrow(() ->
                        new ApiException("Product not found")
                );

        Cart cart = cartRepository
                .findByUser(user)
                .orElseGet(() ->
                        cartRepository.save(
                                new Cart(user)
                        )
                );

        CartItem existingItem = cart.getItems()
                .stream()
                .filter(i ->
                        i.getProduct()
                                .getId()
                                .equals(request.productId())
                )
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            existingItem.updateQuantity(
                    existingItem.getQuantity()
                            + request.quantity()
            );
        } else {
            cart.addItem(
                    new CartItem(
                            cart,
                            product,
                            request.quantity()
                    )
            );
        }

        Cart saved = cartRepository.save(cart);

        return toResponse(saved);
    }

    private AppUser findUser(String email) {
        return appUserRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ApiException("User not found")
                );
    }

    private CartResponse toResponse(Cart cart) {

        List<CartItemResponse> items =
                cart.getItems()
                        .stream()
                        .map(item ->
                                new CartItemResponse(
                                        item.getId(),
                                        item.getProduct().getId(),
                                        item.getProduct().getName(),
                                        item.getQuantity(),
                                        item.getProduct().getPrice(),
                                        item.getLineTotal()
                                )
                        )
                        .toList();

        BigDecimal total =
                items.stream()
                        .map(CartItemResponse::lineTotal)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        return new CartResponse(
                cart.getId(),
                items,
                total
        );
    }

    public OrderResponse checkout(String userEmail) {
        AppUser user = findUser(userEmail);

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ApiException("Cart not found"));

        if (cart.getItems().isEmpty()) {
            throw new ApiException("Cart is empty");
        }

        List<OrderItemRequest> orderItems = cart.getItems()
                .stream()
                .map(item -> new OrderItemRequest(
                        item.getProduct().getId(),
                        item.getQuantity()
                ))
                .toList();

        CreateOrderRequest orderRequest = new CreateOrderRequest(
                user.getName(),
                user.getEmail(),
                orderItems
        );

        CustomerOrder order = orderService.createOrder(orderRequest);

        cart.clear();
        cartRepository.save(cart);

        return OrderMapper.toResponse(order);
    }
}