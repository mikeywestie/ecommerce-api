package com.mikey.ecommerce.payment;

import com.mikey.ecommerce.common.ApiException;
import com.mikey.ecommerce.order.CustomerOrder;
import com.mikey.ecommerce.order.OrderRepository;
import com.mikey.ecommerce.order.OrderStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    public PaymentService(OrderRepository orderRepository, PaymentRepository paymentRepository) {
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public Payment processPayment(PaymentRequest request) {
        CustomerOrder order = orderRepository.findById(request.orderId())
                .orElseThrow(() -> new ApiException("Order not found"));

        if (paymentRepository.findByOrderId(order.getId()).isPresent()) {
            throw new ApiException("Payment already exists for this order");
        }

        if (order.getStatus() != OrderStatus.CREATED) {
            throw new ApiException("Only CREATED orders can be paid");
        }

        boolean success = !"FAIL".equalsIgnoreCase(request.paymentMethod());

        if (success) {
            order.markPaid();
        } else {
            order.markPaymentFailed();
        }

        Payment payment = new Payment(
                order,
                request.paymentMethod(),
                success ? PaymentStatus.SUCCESS : PaymentStatus.FAILED,
                order.getTotalAmount()
        );

        return paymentRepository.save(payment);
    }
}
