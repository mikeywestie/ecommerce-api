package com.mikey.ecommerce.payment;

import com.mikey.ecommerce.dto.payment.PaymentResponse;
import com.mikey.ecommerce.mapper.PaymentMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller responsible for payment processing and payment history retrieval.
 *
 * <p>This controller provides endpoints for:</p>
 * <ul>
 *     <li>Retrieving all processed payments</li>
 *     <li>Submitting a payment request for an order</li>
 * </ul>
 *
 * <p>Payment processing is delegated to {@link PaymentService}, which handles
 * business logic such as authorization, persistence, and event publication.</p>
 */
@RestController
@RequestMapping("/api/payments")
@Tag(
        name = "Payments",
        description = "Payment processing endpoints for retrieving payment history and processing customer payments."
)
public class PaymentController {

    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;

    public PaymentController(PaymentRepository paymentRepository,
                             PaymentService paymentService) {
        this.paymentRepository = paymentRepository;
        this.paymentService = paymentService;
    }

    /**
     * Retrieves all recorded payments.
     *
     * @return list of payment responses
     */
    @Operation(
            summary = "Get all payments",
            description = "Returns a list of all processed payments in the system."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Payments retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PaymentResponse.class)
                    )
            )
    })
    @GetMapping
    public List<PaymentResponse> findAll() {
        return paymentRepository.findAll()
                .stream()
                .map(PaymentMapper::toResponse)
                .toList();
    }

    /**
     * Processes a payment request for an order.
     *
     * @param request payment request containing order and payment details
     * @return processed payment response
     */
    @Operation(
            summary = "Process payment",
            description = "Processes a payment for an order and returns the resulting payment record."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Payment processed successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PaymentResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failed or payment could not be processed"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Referenced order not found"
            )
    })
    @PostMapping
    public PaymentResponse pay(
            @Parameter(
                    description = "Payment request containing order ID and payment details"
            )
            @Valid
            @RequestBody PaymentRequest request
    ) {
        return PaymentMapper.toResponse(
                paymentService.processPayment(request)
        );
    }
}