package com.mikey.ecommerce.mapper;

import com.mikey.ecommerce.dto.payment.PaymentResponse;
import com.mikey.ecommerce.payment.Payment;

public class PaymentMapper {

    private PaymentMapper(){}

    public static PaymentResponse toResponse(Payment payment){
        return new PaymentResponse(
                payment.getId(),
                payment.getOrder().getId(),
                payment.getPaymentMethod(),
                payment.getStatus().name(),
                payment.getAmount(),
                payment.getPaidAt()
        );
    }
}