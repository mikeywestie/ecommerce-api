package com.mikey.ecommerce.cart;

import com.mikey.ecommerce.cart.dto.AddCartItemRequest;
import com.mikey.ecommerce.cart.dto.CartItemResponse;
import com.mikey.ecommerce.cart.dto.CartResponse;
import com.mikey.ecommerce.common.ApiException;
import com.mikey.ecommerce.coupon.Coupon;
import com.mikey.ecommerce.coupon.CouponRedemption;
import com.mikey.ecommerce.coupon.CouponRedemptionRepository;
import com.mikey.ecommerce.coupon.CouponRepository;
import com.mikey.ecommerce.dto.order.OrderResponse;
import com.mikey.ecommerce.events.CouponAppliedEvent;
import com.mikey.ecommerce.events.OrderCreatedEvent;
import com.mikey.ecommerce.events.OrderEventProducer;
import com.mikey.ecommerce.inventory.Inventory;
import com.mikey.ecommerce.inventory.InventoryRepository;
import com.mikey.ecommerce.mapper.OrderMapper;
import com.mikey.ecommerce.order.CreateOrderRequest;
import com.mikey.ecommerce.order.CustomerOrder;
import com.mikey.ecommerce.order.OrderItemRequest;
import com.mikey.ecommerce.order.OrderService;
import com.mikey.ecommerce.payment.PaymentService;
import com.mikey.ecommerce.product.Product;
import com.mikey.ecommerce.product.ProductRepository;
import com.mikey.ecommerce.security.AppUser;
import com.mikey.ecommerce.security.AppUserRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CartService {

  private static final int LOW_STOCK_THRESHOLD = 5;
  private static final int ALMOST_SOLD_OUT_THRESHOLD = 10;

  private final CartRepository cartRepository;
  private final ProductRepository productRepository;
  private final InventoryRepository inventoryRepository;
  private final AppUserRepository appUserRepository;
  private final OrderService orderService;
  private final PaymentService paymentService;
  private final CouponRepository couponRepository;
  private final CouponRedemptionRepository couponRedemptionRepository;
  private final OrderEventProducer orderEventProducer;

  public CartService(
      CartRepository cartRepository,
      ProductRepository productRepository,
      InventoryRepository inventoryRepository,
      AppUserRepository appUserRepository,
      OrderService orderService,
      PaymentService paymentService,
      CouponRepository couponRepository,
      CouponRedemptionRepository couponRedemptionRepository,
      OrderEventProducer orderEventProducer) {
    this.cartRepository = cartRepository;
    this.productRepository = productRepository;
    this.inventoryRepository = inventoryRepository;
    this.appUserRepository = appUserRepository;
    this.orderService = orderService;
    this.paymentService = paymentService;
    this.couponRepository = couponRepository;
    this.couponRedemptionRepository = couponRedemptionRepository;
    this.orderEventProducer = orderEventProducer;
  }

  public CartResponse getCart(String userEmail) {
    AppUser user = findUser(userEmail);
    Cart cart = getOrCreateCart(user);
    return toResponse(cart);
  }

  public CartResponse addItem(String userEmail, AddCartItemRequest request) {
    AppUser user = findUser(userEmail);

    Product product =
        productRepository
            .findById(request.productId())
            .orElseThrow(() -> new ApiException("Product not found", HttpStatus.NOT_FOUND));

    if (!product.isActive()) {
      throw new ApiException("Product is inactive", HttpStatus.UNPROCESSABLE_ENTITY);
    }

    Cart cart = getOrCreateCart(user);

    CartItem existingItem =
        cart.getItems().stream()
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
      String userEmail, Long cartItemId, UpdateCartItemRequest request) {
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

  public OrderResponse checkout(String userEmail, String paymentOutcome) {
    AppUser user = findUser(userEmail);

    Cart cart =
        cartRepository
            .findByUser(user)
            .orElseThrow(() -> new ApiException("Cart not found", HttpStatus.NOT_FOUND));

    if (cart.getItems().isEmpty()) {
      throw new ApiException("Cart is empty", HttpStatus.UNPROCESSABLE_ENTITY);
    }

    validateCartStock(cart);

    if (cart.getCoupon() != null) {
      validateCouponUsage(cart.getCoupon(), user);
    }

    List<OrderItemRequest> orderItems =
        cart.getItems().stream()
            .map(item -> new OrderItemRequest(item.getProduct().getId(), item.getQuantity()))
            .toList();

    CreateOrderRequest orderRequest =
        new CreateOrderRequest(user.getName(), user.getEmail(), orderItems);

    CustomerOrder order = orderService.createOrder(orderRequest);

    BigDecimal subtotal =
        cart.getItems().stream()
            .map(CartItem::getLineTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal discount = calculateDiscount(cart, subtotal);

    if (cart.getCoupon() != null) {
      order.applyDiscount(cart.getCoupon().getCode(), discount);

      couponRedemptionRepository.save(new CouponRedemption(cart.getCoupon(), user, order));

      orderEventProducer.publish(
          new CouponAppliedEvent(
              order.getId(), cart.getCoupon().getCode(), discount, java.time.Instant.now()));
    }

    paymentService.processPaymentForOrder(order, resolvePaymentMethod(paymentOutcome));

    orderEventProducer.publish(
        new OrderCreatedEvent(
            order.getId(), order.getCustomerEmail(), order.getTotalAmount(), order.getCreatedAt()));

    cart.clear();
    cart.applyCoupon(null);
    cartRepository.save(cart);

    return OrderMapper.toResponse(order);
  }

  public CartResponse applyCoupon(String userEmail, String code) {
    AppUser user = findUser(userEmail);
    Cart cart = getExistingCart(user);

    Coupon coupon =
        couponRepository
            .findByCodeIgnoreCase(code)
            .orElseThrow(() -> new ApiException("Coupon not found", HttpStatus.NOT_FOUND));

    validateCouponAvailability(coupon);
    validateCouponUsage(coupon, user);

    cart.applyCoupon(coupon);

    return toResponse(cartRepository.save(cart));
  }

  public CartResponse removeCoupon(String userEmail) {
    AppUser user = findUser(userEmail);
    Cart cart = getExistingCart(user);

    cart.applyCoupon(null);

    return toResponse(cartRepository.save(cart));
  }

  private String resolvePaymentMethod(String paymentOutcome) {
    if ("FAIL".equalsIgnoreCase(paymentOutcome)
        || "FAILED".equalsIgnoreCase(paymentOutcome)
        || "PAYMENT_FAILED".equalsIgnoreCase(paymentOutcome)) {
      return "PAYMENT_FAILED";
    }

    return "DEMO_CARD";
  }

  private void validateCouponAvailability(Coupon coupon) {
    if (!coupon.isActive()) {
      throw new ApiException("Coupon inactive", HttpStatus.UNPROCESSABLE_ENTITY);
    }

    if (coupon.isExpired()) {
      throw new ApiException("Coupon expired", HttpStatus.UNPROCESSABLE_ENTITY);
    }
  }

  private void validateCouponUsage(Coupon coupon, AppUser user) {
    if (coupon.isReusable()
        && coupon.getMaxUsesPerCustomer() == null
        && coupon.getMaxTotalUses() == null) {
      return;
    }

    long totalUses = couponRedemptionRepository.countByCoupon(coupon);

    if (coupon.getMaxTotalUses() != null && totalUses >= coupon.getMaxTotalUses()) {
      throw new ApiException("Coupon total usage limit reached", HttpStatus.CONFLICT);
    }

    long customerUses = couponRedemptionRepository.countByCouponAndUser(coupon, user);

    if (!coupon.isReusable() && customerUses > 0) {
      throw new ApiException("Coupon already used by this customer", HttpStatus.CONFLICT);
    }

    if (coupon.getMaxUsesPerCustomer() != null && customerUses >= coupon.getMaxUsesPerCustomer()) {
      throw new ApiException("Coupon usage limit reached for this customer", HttpStatus.CONFLICT);
    }
  }

  private void validateCartStock(Cart cart) {
    for (CartItem item : cart.getItems()) {
      Inventory inventory =
          inventoryRepository
              .findByProductId(item.getProduct().getId())
              .orElseThrow(
                  () ->
                      new ApiException(
                          "Inventory not found for product: " + item.getProduct().getName(),
                          HttpStatus.NOT_FOUND));

      if (!item.getProduct().isActive()) {
        throw new ApiException(
            item.getProduct().getName() + " is no longer available",
            HttpStatus.UNPROCESSABLE_ENTITY);
      }

      if (inventory.getQuantityAvailable() <= 0) {
        throw new ApiException(
            item.getProduct().getName() + " is out of stock", HttpStatus.UNPROCESSABLE_ENTITY);
      }

      if (inventory.getQuantityAvailable() < item.getQuantity()) {
        throw new ApiException(
            "Only " + inventory.getQuantityAvailable() + " left for " + item.getProduct().getName(),
            HttpStatus.UNPROCESSABLE_ENTITY);
      }
    }
  }

  private AppUser findUser(String email) {
    return appUserRepository
        .findByEmail(email)
        .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));
  }

  private Cart getOrCreateCart(AppUser user) {
    return cartRepository.findByUser(user).orElseGet(() -> cartRepository.save(new Cart(user)));
  }

  private Cart getExistingCart(AppUser user) {
    return cartRepository
        .findByUser(user)
        .orElseThrow(() -> new ApiException("Cart not found", HttpStatus.NOT_FOUND));
  }

  private CartItem findCartItem(Cart cart, Long cartItemId) {
    return cart.getItems().stream()
        .filter(item -> item.getId().equals(cartItemId))
        .findFirst()
        .orElseThrow(() -> new ApiException("Cart item not found", HttpStatus.NOT_FOUND));
  }

  private CartResponse toResponse(Cart cart) {
    List<CartItemResponse> items = cart.getItems().stream().map(this::toCartItemResponse).toList();

    BigDecimal subtotal =
        items.stream()
            .map(CartItemResponse::lineTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .setScale(2, RoundingMode.HALF_UP);

    BigDecimal discount = calculateDiscount(cart, subtotal).setScale(2, RoundingMode.HALF_UP);

    BigDecimal total =
        subtotal.subtract(discount).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);

    return new CartResponse(
        cart.getId(),
        items,
        subtotal,
        discount,
        total,
        cart.getCoupon() == null ? null : cart.getCoupon().getCode());
  }

  private CartItemResponse toCartItemResponse(CartItem item) {
    Inventory inventory =
        inventoryRepository.findByProductId(item.getProduct().getId()).orElse(null);

    int availableQuantity = inventory == null ? 0 : inventory.getQuantityAvailable();

    String stockStatus = resolveStockStatus(item, availableQuantity);

    return new CartItemResponse(
        item.getId(),
        item.getProduct().getId(),
        item.getProduct().getName(),
        item.getQuantity(),
        item.getProduct().getPrice(),
        item.getLineTotal(),
        availableQuantity,
        stockStatus,
        resolveStockMessage(item, availableQuantity, stockStatus));
  }

  private String resolveStockStatus(CartItem item, int availableQuantity) {
    if (!item.getProduct().isActive()) {
      return "INACTIVE";
    }

    if (availableQuantity <= 0) {
      return "OUT_OF_STOCK";
    }

    if (availableQuantity < item.getQuantity()) {
      return "INSUFFICIENT_STOCK";
    }

    if (availableQuantity <= LOW_STOCK_THRESHOLD) {
      return "LOW_STOCK";
    }

    if (availableQuantity <= ALMOST_SOLD_OUT_THRESHOLD) {
      return "ALMOST_SOLD_OUT";
    }

    return "IN_STOCK";
  }

  private String resolveStockMessage(CartItem item, int availableQuantity, String stockStatus) {
    return switch (stockStatus) {
      case "INACTIVE" -> "Product is no longer available.";
      case "OUT_OF_STOCK" -> "Out of stock.";
      case "INSUFFICIENT_STOCK" -> "Only " + availableQuantity + " left in stock.";
      case "LOW_STOCK" -> "Low stock. " + availableQuantity + " left.";
      case "ALMOST_SOLD_OUT" -> "Almost sold out. " + availableQuantity + " left.";
      default -> "In stock.";
    };
  }

  private BigDecimal calculateDiscount(Cart cart, BigDecimal subtotal) {
    if (cart.getCoupon() == null) {
      return BigDecimal.ZERO;
    }

    if (cart.getCoupon().getType().name().equals("PERCENTAGE")) {
      return subtotal
          .multiply(cart.getCoupon().getValue())
          .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    BigDecimal discount = cart.getCoupon().getValue();

    if (discount.compareTo(subtotal) > 0) {
      return subtotal;
    }

    return discount;
  }
}
