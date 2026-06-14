package com.mikey.ecommerce.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mikey.ecommerce.common.ApiException;
import com.mikey.ecommerce.events.OrderEventProducer;
import com.mikey.ecommerce.events.PaymentProcessedEvent;
import com.mikey.ecommerce.inventory.InventoryRepository;
import com.mikey.ecommerce.order.CustomerOrder;
import com.mikey.ecommerce.order.OrderRepository;
import com.mikey.ecommerce.order.OrderStatus;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

  @Mock private OrderRepository orderRepository;

  @Mock private PaymentRepository paymentRepository;

  @Mock private InventoryRepository inventoryRepository;

  @Mock private OrderEventProducer orderEventProducer;

  private PaymentService paymentService;

  @BeforeEach
  void setUp() {
    paymentService =
        new PaymentService(
            orderRepository, paymentRepository, inventoryRepository, orderEventProducer);
  }

  @Test
  void processPayment_shouldMarkOrderAsPaidSavePaymentAndPublishEvent() {
    CustomerOrder order = orderWithIdAndTotal(1L, new BigDecimal("2599.98"));
    PaymentRequest request = new PaymentRequest(1L, "CARD");

    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
    when(paymentRepository.findByOrderId(1L)).thenReturn(Optional.empty());
    when(paymentRepository.save(any(Payment.class)))
        .thenAnswer(
            invocation -> {
              Payment payment = invocation.getArgument(0);
              setField(payment, "id", 10L);
              return payment;
            });

    Payment payment = paymentService.processPayment(request);

    assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
    assertThat(payment.getPaymentMethod()).isEqualTo("CARD");
    assertThat(payment.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
    assertThat(payment.getAmount()).isEqualByComparingTo("2599.98");

    ArgumentCaptor<PaymentProcessedEvent> eventCaptor =
        ArgumentCaptor.forClass(PaymentProcessedEvent.class);

    verify(orderEventProducer).publish(eventCaptor.capture());

    PaymentProcessedEvent event = eventCaptor.getValue();

    assertThat(event.paymentId()).isEqualTo(10L);
    assertThat(event.orderId()).isEqualTo(1L);
    assertThat(event.paymentMethod()).isEqualTo("CARD");
    assertThat(event.status()).isEqualTo("SUCCESS");
    assertThat(event.amount()).isEqualByComparingTo("2599.98");
  }

  @Test
  void processPayment_shouldMarkOrderAsPaymentFailed_whenPaymentMethodIsFail() {
    CustomerOrder order = orderWithIdAndTotal(1L, new BigDecimal("500.00"));
    PaymentRequest request = new PaymentRequest(1L, "FAIL");

    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
    when(paymentRepository.findByOrderId(1L)).thenReturn(Optional.empty());
    when(paymentRepository.save(any(Payment.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Payment payment = paymentService.processPayment(request);

    assertThat(order.getStatus()).isEqualTo(OrderStatus.PAYMENT_FAILED);
    assertThat(payment.getStatus()).isEqualTo(PaymentStatus.FAILED);
    assertThat(payment.getAmount()).isEqualByComparingTo("500.00");

    verify(orderEventProducer).publish(any(PaymentProcessedEvent.class));
  }

  @Test
  void processPayment_shouldThrowApiException_whenOrderDoesNotExist() {
    PaymentRequest request = new PaymentRequest(99L, "CARD");

    when(orderRepository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> paymentService.processPayment(request))
        .isInstanceOf(ApiException.class)
        .hasMessage("Order not found");

    verify(paymentRepository, never()).findByOrderId(any());
    verify(paymentRepository, never()).save(any());
    verify(orderEventProducer, never()).publish(any(PaymentProcessedEvent.class));
  }

  @Test
  void processPayment_shouldThrowApiException_whenPaymentAlreadyExistsForOrder() {
    CustomerOrder order = orderWithIdAndTotal(1L, new BigDecimal("500.00"));
    Payment existingPayment =
        new Payment(order, "CARD", PaymentStatus.SUCCESS, new BigDecimal("500.00"));

    PaymentRequest request = new PaymentRequest(1L, "CARD");

    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
    when(paymentRepository.findByOrderId(1L)).thenReturn(Optional.of(existingPayment));

    assertThatThrownBy(() -> paymentService.processPayment(request))
        .isInstanceOf(ApiException.class)
        .hasMessage("Payment already exists for this order");

    verify(paymentRepository, never()).save(any());
    verify(orderEventProducer, never()).publish(any(PaymentProcessedEvent.class));
  }

  @Test
  void processPayment_shouldThrowApiException_whenOrderIsNotCreated() {
    CustomerOrder order = orderWithIdAndTotal(1L, new BigDecimal("500.00"));
    order.markPaid();

    PaymentRequest request = new PaymentRequest(1L, "CARD");

    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
    when(paymentRepository.findByOrderId(1L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> paymentService.processPayment(request))
        .isInstanceOf(ApiException.class)
        .hasMessage("Only CREATED orders can be paid");

    verify(paymentRepository, never()).save(any());
    verify(orderEventProducer, never()).publish(any(PaymentProcessedEvent.class));
  }

  private CustomerOrder orderWithIdAndTotal(Long id, BigDecimal totalAmount) {
    CustomerOrder order = new CustomerOrder("Michael Westman", "michael@example.com");

    setField(order, "id", id);
    setField(order, "totalAmount", totalAmount);

    return order;
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
