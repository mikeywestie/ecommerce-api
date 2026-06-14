package com.mikey.ecommerce.cart;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mikey.ecommerce.cart.dto.AddCartItemRequest;
import com.mikey.ecommerce.cart.dto.CartResponse;
import com.mikey.ecommerce.common.ApiException;
import com.mikey.ecommerce.coupon.Coupon;
import com.mikey.ecommerce.coupon.CouponRedemptionRepository;
import com.mikey.ecommerce.coupon.CouponRepository;
import com.mikey.ecommerce.coupon.CouponType;
import com.mikey.ecommerce.events.OrderEventProducer;
import com.mikey.ecommerce.inventory.Inventory;
import com.mikey.ecommerce.inventory.InventoryRepository;
import com.mikey.ecommerce.order.OrderService;
import com.mikey.ecommerce.payment.PaymentService;
import com.mikey.ecommerce.product.Product;
import com.mikey.ecommerce.product.ProductRepository;
import com.mikey.ecommerce.security.AppUser;
import com.mikey.ecommerce.security.AppUserRepository;
import com.mikey.ecommerce.security.Role;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

  @Mock private CartRepository cartRepository;

  @Mock private ProductRepository productRepository;

  @Mock private InventoryRepository inventoryRepository;

  @Mock private AppUserRepository appUserRepository;

  @Mock private OrderService orderService;

  @Mock private PaymentService paymentService;

  @Mock private CouponRepository couponRepository;

  @Mock private CouponRedemptionRepository couponRedemptionRepository;

  @Mock private OrderEventProducer orderEventProducer;

  private CartService cartService;

  @BeforeEach
  void setUp() {
    cartService =
        new CartService(
            cartRepository,
            productRepository,
            inventoryRepository,
            appUserRepository,
            orderService,
            paymentService,
            couponRepository,
            couponRedemptionRepository,
            orderEventProducer);
  }

  @Test
  void addItem_shouldCreateCartWhenUserHasNoCartAndAddProduct() {
    AppUser user = user();
    Product product =
        productWithId(1L, "Mechanical Keyboard", "RGB keyboard", new BigDecimal("1299.99"));

    when(appUserRepository.findByEmail("michael@example.com")).thenReturn(Optional.of(user));
    when(productRepository.findById(1L)).thenReturn(Optional.of(product));
    when(inventoryRepository.findByProductId(1L))
        .thenReturn(Optional.of(new Inventory(product, 20)));
    when(cartRepository.findByUser(user)).thenReturn(Optional.empty());
    when(cartRepository.save(any(Cart.class)))
        .thenAnswer(
            invocation -> {
              Cart cart = invocation.getArgument(0);
              setField(cart, "id", 1L);
              return cart;
            });

    CartResponse response =
        cartService.addItem("michael@example.com", new AddCartItemRequest(1L, 2));

    assertThat(response.cartId()).isEqualTo(1L);
    assertThat(response.items()).hasSize(1);
    assertThat(response.items().get(0).productId()).isEqualTo(1L);
    assertThat(response.items().get(0).productName()).isEqualTo("Mechanical Keyboard");
    assertThat(response.items().get(0).quantity()).isEqualTo(2);
    assertThat(response.items().get(0).unitPrice()).isEqualByComparingTo("1299.99");
    assertThat(response.items().get(0).lineTotal()).isEqualByComparingTo("2599.98");
    assertThat(response.items().get(0).availableQuantity()).isEqualTo(20);
    assertThat(response.items().get(0).stockStatus()).isEqualTo("IN_STOCK");
    assertThat(response.total()).isEqualByComparingTo("2599.98");
  }

  @Test
  void addItem_shouldIncreaseQuantityWhenProductAlreadyExistsInCart() {
    AppUser user = user();
    Product product =
        productWithId(1L, "Mechanical Keyboard", "RGB keyboard", new BigDecimal("1299.99"));
    Cart cart = cartWithId(user, 1L);
    cart.addItem(new CartItem(cart, product, 2));

    when(appUserRepository.findByEmail("michael@example.com")).thenReturn(Optional.of(user));
    when(productRepository.findById(1L)).thenReturn(Optional.of(product));
    when(inventoryRepository.findByProductId(1L))
        .thenReturn(Optional.of(new Inventory(product, 20)));
    when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
    when(cartRepository.save(cart)).thenReturn(cart);

    CartResponse response =
        cartService.addItem("michael@example.com", new AddCartItemRequest(1L, 3));

    assertThat(response.items()).hasSize(1);
    assertThat(response.items().get(0).quantity()).isEqualTo(5);
    assertThat(response.items().get(0).lineTotal()).isEqualByComparingTo("6499.95");
    assertThat(response.items().get(0).availableQuantity()).isEqualTo(20);
    assertThat(response.items().get(0).stockStatus()).isEqualTo("IN_STOCK");
    assertThat(response.total()).isEqualByComparingTo("6499.95");
  }

  @Test
  void addItem_shouldThrowApiExceptionWhenUserDoesNotExist() {
    when(appUserRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

    assertThatThrownBy(
            () -> cartService.addItem("missing@example.com", new AddCartItemRequest(1L, 1)))
        .isInstanceOf(ApiException.class)
        .hasMessage("User not found");

    verify(productRepository, never()).findById(any());
    verify(cartRepository, never()).save(any());
  }

  @Test
  void addItem_shouldThrowApiExceptionWhenProductDoesNotExist() {
    AppUser user = user();

    when(appUserRepository.findByEmail("michael@example.com")).thenReturn(Optional.of(user));
    when(productRepository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(
            () -> cartService.addItem("michael@example.com", new AddCartItemRequest(99L, 1)))
        .isInstanceOf(ApiException.class)
        .hasMessage("Product not found");

    verify(cartRepository, never()).save(any());
  }

  @Test
  void applyCoupon_shouldApplyPercentageCouponAndReturnDiscountedTotal() {
    AppUser user = user();
    Product product =
        productWithId(1L, "Mechanical Keyboard", "RGB keyboard", new BigDecimal("1000.00"));
    Cart cart = cartWithId(user, 1L);
    cart.addItem(new CartItem(cart, product, 2));

    Coupon coupon =
        new Coupon(
            "SAVE10",
            CouponType.PERCENTAGE,
            new BigDecimal("10.00"),
            Instant.now().plusSeconds(86_400));

    when(appUserRepository.findByEmail("michael@example.com")).thenReturn(Optional.of(user));
    when(inventoryRepository.findByProductId(1L))
        .thenReturn(Optional.of(new Inventory(product, 20)));
    when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
    when(couponRepository.findByCodeIgnoreCase("SAVE10")).thenReturn(Optional.of(coupon));
    when(cartRepository.save(cart)).thenReturn(cart);

    CartResponse response = cartService.applyCoupon("michael@example.com", "SAVE10");

    assertThat(response.total()).isEqualByComparingTo("1800.0000");
  }

  @Test
  void applyCoupon_shouldThrowApiExceptionWhenCouponIsInactive() {
    AppUser user = user();
    Cart cart = cartWithId(user, 1L);
    Coupon coupon =
        new Coupon(
            "SAVE10",
            CouponType.PERCENTAGE,
            new BigDecimal("10.00"),
            Instant.now().plusSeconds(86_400));
    coupon.deactivate();

    when(appUserRepository.findByEmail("michael@example.com")).thenReturn(Optional.of(user));
    when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
    when(couponRepository.findByCodeIgnoreCase("SAVE10")).thenReturn(Optional.of(coupon));

    assertThatThrownBy(() -> cartService.applyCoupon("michael@example.com", "SAVE10"))
        .isInstanceOf(ApiException.class)
        .hasMessage("Coupon inactive");

    verify(cartRepository, never()).save(any());
  }

  private AppUser user() {
    AppUser user =
        new AppUser("Michael Westman", "michael@example.com", "encoded-password", Role.CUSTOMER);
    setField(user, "id", 1L);
    return user;
  }

  private Cart cartWithId(AppUser user, Long id) {
    Cart cart = new Cart(user);
    setField(cart, "id", id);
    return cart;
  }

  private Product productWithId(Long id, String name, String description, BigDecimal price) {
    Product product =
        new Product(
            name, description, "Peripherals", "https://example.com/product.jpg", true, price);

    setField(product, "id", id);
    return product;
  }

  private void setField(Object target, String fieldName, Object value) {
    try {
      Field field = target.getClass().getDeclaredField(fieldName);
      field.setAccessible(true);
      field.set(target, value);
    } catch (NoSuchFieldException | IllegalAccessException ex) {
      throw new IllegalStateException("Could not set " + fieldName + " for test", ex);
    }
  }
}
