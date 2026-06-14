package com.mikey.ecommerce.payment;

import com.mikey.ecommerce.common.ApiException;
import com.mikey.ecommerce.events.OrderEventProducer;
import com.mikey.ecommerce.events.PaymentProcessedEvent;
import com.mikey.ecommerce.inventory.Inventory;
import com.mikey.ecommerce.inventory.InventoryRepository;
import com.mikey.ecommerce.order.CustomerOrder;
import com.mikey.ecommerce.order.OrderItem;
import com.mikey.ecommerce.order.OrderRepository;
import com.mikey.ecommerce.order.OrderStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

  private final OrderRepository orderRepository;
  private final PaymentRepository paymentRepository;
  private final InventoryRepository inventoryRepository;
  private final OrderEventProducer orderEventProducer;

  public PaymentService(
      OrderRepository orderRepository,
      PaymentRepository paymentRepository,
      InventoryRepository inventoryRepository,
      OrderEventProducer orderEventProducer) {
    this.orderRepository = orderRepository;
    this.paymentRepository = paymentRepository;
    this.inventoryRepository = inventoryRepository;
    this.orderEventProducer = orderEventProducer;
  }

  @Transactional
  public Payment processPayment(PaymentRequest request) {
    CustomerOrder order =
        orderRepository
            .findById(request.orderId())
            .orElseThrow(() -> new ApiException("Order not found"));

    return processPaymentForOrder(order, request.paymentMethod());
  }

  @Transactional
  public Payment processPaymentForOrder(CustomerOrder order, String paymentMethod) {
    if (paymentRepository.findByOrderId(order.getId()).isPresent()) {
      throw new ApiException("Payment already exists for this order");
    }

    if (order.getStatus() != OrderStatus.CREATED) {
      throw new ApiException("Only CREATED orders can be paid");
    }

    boolean failure =
        "FAIL".equalsIgnoreCase(paymentMethod)
            || "FAILED".equalsIgnoreCase(paymentMethod)
            || "PAYMENT_FAILED".equalsIgnoreCase(paymentMethod);

    boolean success = !failure;

    if (success) {
      reserveInventoryForPaidOrder(order);
      order.markPaid();
    } else {
      order.markPaymentFailed();
    }

    Payment payment =
        new Payment(
            order,
            paymentMethod,
            success ? PaymentStatus.SUCCESS : PaymentStatus.FAILED,
            order.getTotalAmount());

    Payment savedPayment = paymentRepository.save(payment);

    orderEventProducer.publish(
        new PaymentProcessedEvent(
            savedPayment.getId(),
            savedPayment.getOrder().getId(),
            savedPayment.getPaymentMethod(),
            savedPayment.getStatus().name(),
            savedPayment.getAmount(),
            savedPayment.getPaidAt()));

    return savedPayment;
  }

  private void reserveInventoryForPaidOrder(CustomerOrder order) {
    for (OrderItem item : order.getItems()) {
      Inventory inventory =
          inventoryRepository
              .findByProductId(item.getProduct().getId())
              .orElseThrow(
                  () ->
                      new ApiException(
                          "Inventory not found for product: " + item.getProduct().getName()));

      inventory.reserve(item.getQuantity());
    }
  }
}
