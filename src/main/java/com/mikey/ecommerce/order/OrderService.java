package com.mikey.ecommerce.order;

import com.mikey.ecommerce.common.ApiException;
import com.mikey.ecommerce.dto.order.OrderItemResponse;
import com.mikey.ecommerce.dto.order.OrderResponse;
import com.mikey.ecommerce.inventory.InventoryRepository;
import com.mikey.ecommerce.product.Product;
import com.mikey.ecommerce.product.ProductRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

  private final OrderRepository orderRepository;
  private final ProductRepository productRepository;
  private final InventoryRepository inventoryRepository;

  public OrderService(
      OrderRepository orderRepository,
      ProductRepository productRepository,
      InventoryRepository inventoryRepository) {
    this.orderRepository = orderRepository;
    this.productRepository = productRepository;
    this.inventoryRepository = inventoryRepository;
  }

  @Transactional(readOnly = true)
  public List<OrderResponse> getOrders() {
    return orderRepository.findAllWithItems().stream().map(this::toOrderResponse).toList();
  }

  @Transactional
  public CustomerOrder createOrder(CreateOrderRequest request) {
    CustomerOrder order = new CustomerOrder(request.customerName(), request.customerEmail());

    for (OrderItemRequest itemRequest : request.items()) {
      Product product =
          productRepository
              .findById(itemRequest.productId())
              .orElseThrow(() -> new ApiException("Product not found: " + itemRequest.productId()));

      inventoryRepository
          .findByProductId(product.getId())
          .orElseThrow(
              () -> new ApiException("Inventory not found for product: " + product.getName()));

      OrderItem item = new OrderItem(order, product, itemRequest.quantity(), product.getPrice());

      order.addItem(item);
    }

    return orderRepository.save(order);
  }

  private OrderResponse toOrderResponse(CustomerOrder order) {
    return new OrderResponse(
        order.getId(),
        order.getCustomerName(),
        order.getCustomerEmail(),
        order.getStatus() != null ? order.getStatus().name() : "UNKNOWN",
        order.getTotalAmount(),
        order.getCouponCode(),
        order.getDiscountAmount(),
        order.getCreatedAt(),
        order.getItems().stream().map(this::toOrderItemResponse).toList());
  }

  private OrderItemResponse toOrderItemResponse(OrderItem item) {
    Product product = item.getProduct();

    return new OrderItemResponse(
        product.getId(),
        product.getName(),
        item.getQuantity(),
        item.getUnitPrice(),
        item.getLineTotal());
  }
}
