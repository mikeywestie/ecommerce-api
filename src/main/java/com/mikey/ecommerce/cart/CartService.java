package com.mikey.ecommerce.cart;

import com.mikey.ecommerce.cart.dto.AddCartItemRequest;
import com.mikey.ecommerce.cart.dto.CartItemResponse;
import com.mikey.ecommerce.cart.dto.CartResponse;
import com.mikey.ecommerce.common.ApiException;
import com.mikey.ecommerce.coupon.Coupon;
import com.mikey.ecommerce.coupon.CouponRepository;
import com.mikey.ecommerce.dto.order.OrderResponse;
import com.mikey.ecommerce.events.CouponAppliedEvent;
import com.mikey.ecommerce.events.OrderCreatedEvent;
import com.mikey.ecommerce.events.OrderEventProducer;
import com.mikey.ecommerce.mapper.OrderMapper;
import com.mikey.ecommerce.order.CreateOrderRequest;
import com.mikey.ecommerce.order.CustomerOrder;
import com.mikey.ecommerce.order.OrderItemRequest;
import com.mikey.ecommerce.order.OrderService;
import com.mikey.ecommerce.product.Product;
import com.mikey.ecommerce.product.ProductRepository;
import com.mikey.ecommerce.security.AppUser;
import com.mikey.ecommerce.security.AppUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final AppUserRepository appUserRepository;
    private final OrderService orderService;
    private final CouponRepository couponRepository;
    private final OrderEventProducer orderEventProducer;

    public CartService(
            CartRepository cartRepository,
            ProductRepository productRepository,
            AppUserRepository appUserRepository,
            OrderService orderService,
            CouponRepository couponRepository,
            OrderEventProducer orderEventProducer
    ) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.appUserRepository = appUserRepository;
        this.orderService = orderService;
        this.couponRepository = couponRepository;
        this.orderEventProducer = orderEventProducer;
    }

    public CartResponse getCart(String userEmail) {
        AppUser user = findUser(userEmail);
        Cart cart = getOrCreateCart(user);
        return toResponse(cart);
    }

    public CartResponse addItem(String userEmail, AddCartItemRequest request) {
        AppUser user = findUser(userEmail);

        Product product = productRepository
                .findById(request.productId())
                .orElseThrow(() -> new ApiException("Product not found"));

        if (!product.isActive()) {
            throw new ApiException("Product is inactive");
        }

        Cart cart = getOrCreateCart(user);

        CartItem existingItem = cart.getItems()
                .stream()
                .filter(i -> i.getProduct().getId().equals(request.productId()))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            existingItem.updateQuantity(existingItem.getQuantity() + request.quantity());
        } else {
            cart.addItem(new CartItem(cart, product, request.quantity()));
        }

        return toResponse(cartRepository.save(cart));
    }

    public CartResponse updateItemQuantity(
            String userEmail,
            Long cartItemId,
            UpdateCartItemRequest request
    ) {
        AppUser user = findUser(userEmail);
        Cart cart = getExistingCart(user);

        CartItem item = findCartItem(cart, cartItemId);
        item.updateQuantity(request.quantity());

        return toResponse(cartRepository.save(cart));
    }

    public CartResponse removeItem(String userEmail, Long cartItemId) {
        AppUser user = findUser(userEmail);
        Cart cart = getExistingCart(user);

        CartItem item = findCartItem(cart, cartItemId);
        cart.removeItem(item);

        return toResponse(cartRepository.save(cart));
    }

    public OrderResponse checkout(String userEmail) {
        AppUser user = findUser(userEmail);

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ApiException("Cart not found"));

        if (cart.getItems().isEmpty()) {
            throw new ApiException("Cart is empty");
        }

        List<OrderItemRequest> orderItems =
                cart.getItems()
                        .stream()
                        .map(item ->
                                new OrderItemRequest(
                                        item.getProduct().getId(),
                                        item.getQuantity()
                                )
                        )
                        .toList();

        CreateOrderRequest orderRequest =
                new CreateOrderRequest(
                        user.getName(),
                        user.getEmail(),
                        orderItems
                );

        CustomerOrder order = orderService.createOrder(orderRequest);

        BigDecimal subtotal =
                cart.getItems()
                        .stream()
                        .map(CartItem::getLineTotal)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discount = calculateDiscount(cart, subtotal);

        if (cart.getCoupon() != null) {
            order.applyDiscount(cart.getCoupon().getCode(), discount);

            orderEventProducer.publish(
                    new CouponAppliedEvent(
                            order.getId(),
                            cart.getCoupon().getCode(),
                            discount,
                            java.time.Instant.now()
                    )
            );
        }

        orderEventProducer.publish(
                new OrderCreatedEvent(
                        order.getId(),
                        order.getCustomerEmail(),
                        order.getTotalAmount(),
                        order.getCreatedAt()
                )
        );

        cart.clear();
        cart.applyCoupon(null);
        cartRepository.save(cart);

        return OrderMapper.toResponse(order);
    }

    public CartResponse applyCoupon(String userEmail, String code) {
        AppUser user = findUser(userEmail);
        Cart cart = getExistingCart(user);

        Coupon coupon = couponRepository
                .findByCodeIgnoreCase(code)
                .orElseThrow(() -> new ApiException("Coupon not found"));

        if (!coupon.isActive()) {
            throw new ApiException("Coupon inactive");
        }

        if (coupon.isExpired()) {
            throw new ApiException("Coupon expired");
        }

        cart.applyCoupon(coupon);

        return toResponse(cartRepository.save(cart));
    }

    private AppUser findUser(String email) {
        return appUserRepository
                .findByEmail(email)
                .orElseThrow(() -> new ApiException("User not found"));
    }

    private Cart getOrCreateCart(AppUser user) {
        return cartRepository
                .findByUser(user)
                .orElseGet(() -> cartRepository.save(new Cart(user)));
    }

    private Cart getExistingCart(AppUser user) {
        return cartRepository
                .findByUser(user)
                .orElseThrow(() -> new ApiException("Cart not found"));
    }

    private CartItem findCartItem(Cart cart, Long cartItemId) {
        return cart.getItems()
                .stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new ApiException("Cart item not found"));
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

        BigDecimal subtotal =
                items.stream()
                        .map(CartItemResponse::lineTotal)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal total = subtotal;

        if (cart.getCoupon() != null) {
            total = subtotal.subtract(calculateDiscount(cart, subtotal));

            if (total.signum() < 0) {
                total = BigDecimal.ZERO;
            }
        }

        return new CartResponse(cart.getId(), items, total);
    }

    private BigDecimal calculateDiscount(Cart cart, BigDecimal subtotal) {
        if (cart.getCoupon() == null) {
            return BigDecimal.ZERO;
        }

        if (cart.getCoupon().getType().name().equals("PERCENTAGE")) {
            return subtotal.multiply(
                    cart.getCoupon().getValue().divide(BigDecimal.valueOf(100))
            );
        }

        BigDecimal discount = cart.getCoupon().getValue();

        if (discount.compareTo(subtotal) > 0) {
            return subtotal;
        }

        return discount;
    }
}