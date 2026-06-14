package com.mikey.ecommerce.order;

public enum OrderStatus {
  CREATED, // -> Order record created
  PENDING, // -> Waiting for payment
  PAID, // -> Payment successful
  PAYMENT_FAILED, // -> Payment unsuccessful
  CANCELLED // -> Order cancelled
}
