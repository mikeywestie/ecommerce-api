package com.mikey.ecommerce.payment;

import com.mikey.ecommerce.dto.payment.PaymentResponse;
import com.mikey.ecommerce.mapper.InventoryMapper;
import com.mikey.ecommerce.mapper.PaymentMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@Tag(name = "Payments")
public class PaymentController {

    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;

    public PaymentController(PaymentRepository paymentRepository, PaymentService paymentService) {
        this.paymentRepository = paymentRepository;
        this.paymentService = paymentService;
    }

    @GetMapping
    public List<PaymentResponse> findAll() {
        return paymentRepository.findAll()
                .stream()
                .map(PaymentMapper::toResponse)
                .toList();
    }

    @PostMapping
    public PaymentResponse pay(@Valid @RequestBody PaymentRequest request) {
        return PaymentMapper.toResponse(paymentService.processPayment(request));
    }
}
