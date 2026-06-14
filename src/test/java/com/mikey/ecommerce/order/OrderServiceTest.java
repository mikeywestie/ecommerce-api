package com.mikey.ecommerce.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mikey.ecommerce.common.ApiException;
import com.mikey.ecommerce.inventory.Inventory;
import com.mikey.ecommerce.inventory.InventoryRepository;
import com.mikey.ecommerce.product.Product;
import com.mikey.ecommerce.product.ProductRepository;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

  @Mock private OrderRepository orderRepository;

  @Mock private ProductRepository productRepository;

  @Mock private InventoryRepository inventoryRepository;

  private OrderService orderService;

  @BeforeEach
  void setUp() {
    orderService = new OrderService(orderRepository, productRepository, inventoryRepository);
  }

  @Test
  void createOrder_shouldValidateInventoryAndSaveOrderWithoutReservingStock() {
    Product product =
        productWithId(
            1L, "Mechanical Keyboard", "RGB mechanical keyboard", new BigDecimal("1299.99"));
    Inventory inventory = new Inventory(product, 10);
    CreateOrderRequest request =
        new CreateOrderRequest(
            "Michael Westman", "michael@example.com", List.of(new OrderItemRequest(1L, 2)));

    when(productRepository.findById(1L)).thenReturn(Optional.of(product));
    when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.of(inventory));
    when(orderRepository.save(any(CustomerOrder.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    CustomerOrder savedOrder = orderService.createOrder(request);

    assertThat(savedOrder.getCustomerName()).isEqualTo("Michael Westman");
    assertThat(savedOrder.getCustomerEmail()).isEqualTo("michael@example.com");
    assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.CREATED);
    assertThat(savedOrder.getItems()).hasSize(1);
    assertThat(savedOrder.getTotalAmount()).isEqualByComparingTo("2599.98");

    assertThat(inventory.getQuantityAvailable()).isEqualTo(10);

    ArgumentCaptor<CustomerOrder> orderCaptor = ArgumentCaptor.forClass(CustomerOrder.class);

    verify(orderRepository).save(orderCaptor.capture());
    assertThat(orderCaptor.getValue().getItems()).hasSize(1);
  }

  @Test
  void createOrder_shouldThrowApiException_whenProductDoesNotExist() {
    CreateOrderRequest request =
        new CreateOrderRequest(
            "Michael Westman", "michael@example.com", List.of(new OrderItemRequest(99L, 1)));

    when(productRepository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> orderService.createOrder(request))
        .isInstanceOf(ApiException.class)
        .hasMessage("Product not found: 99");

    verify(inventoryRepository, never()).findByProductId(any());
    verify(orderRepository, never()).save(any());
  }

  @Test
  void createOrder_shouldSaveOrder_whenInventoryExistsEvenIfPaymentWillReserveLater() {
    Product product =
        productWithId(
            1L, "Mechanical Keyboard", "RGB mechanical keyboard", new BigDecimal("1299.99"));
    Inventory inventory = new Inventory(product, 1);
    CreateOrderRequest request =
        new CreateOrderRequest(
            "Michael Westman", "michael@example.com", List.of(new OrderItemRequest(1L, 2)));

    when(productRepository.findById(1L)).thenReturn(Optional.of(product));
    when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.of(inventory));
    when(orderRepository.save(any(CustomerOrder.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    CustomerOrder savedOrder = orderService.createOrder(request);

    assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.CREATED);
    assertThat(savedOrder.getItems()).hasSize(1);
    assertThat(inventory.getQuantityAvailable()).isEqualTo(1);

    verify(orderRepository).save(any(CustomerOrder.class));
  }

  @Test
  void createOrder_shouldThrowApiException_whenInventoryDoesNotExist() {
    Product product =
        productWithId(
            1L, "Mechanical Keyboard", "RGB mechanical keyboard", new BigDecimal("1299.99"));

    CreateOrderRequest request =
        new CreateOrderRequest(
            "Michael Westman", "michael@example.com", List.of(new OrderItemRequest(1L, 2)));

    when(productRepository.findById(1L)).thenReturn(Optional.of(product));
    when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> orderService.createOrder(request))
        .isInstanceOf(ApiException.class)
        .hasMessage("Inventory not found for product: Mechanical Keyboard");

    verify(orderRepository, never()).save(any());
  }

  private Product productWithId(Long id, String name, String description, BigDecimal price) {
    Product product =
        new Product(
            name, description, "Peripherals", "https://example.com/product.jpg", true, price);

    setId(product, id);
    return product;
  }

  private void setId(Product product, Long id) {
    try {
      Field idField = Product.class.getDeclaredField("id");
      idField.setAccessible(true);
      idField.set(product, id);
    } catch (NoSuchFieldException | IllegalAccessException ex) {
      throw new IllegalStateException("Could not set product id for test", ex);
    }
  }
}
