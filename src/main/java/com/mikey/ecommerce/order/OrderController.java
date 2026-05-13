package com.mikey.ecommerce.order;

import com.mikey.ecommerce.common.ApiException;
import com.mikey.ecommerce.dto.order.OrderResponse;
import com.mikey.ecommerce.mapper.OrderMapper;
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
 * REST controller responsible for customer order management.
 *
 * <p>This controller provides endpoints for:</p>
 * <ul>
 *     <li>Retrieving all orders</li>
 *     <li>Retrieving a single order by ID</li>
 *     <li>Creating a new order</li>
 * </ul>
 *
 * <p>Order creation is delegated to {@link OrderService}, which handles
 * validation, persistence, inventory reservation, and event publication.</p>
 */
@RestController
@RequestMapping("/api/orders")
@Tag(
        name = "Orders",
        description = "Order management endpoints for creating orders and retrieving order details."
)
public class OrderController {

    private final OrderRepository orderRepository;
    private final OrderService orderService;

    public OrderController(OrderRepository orderRepository,
                           OrderService orderService) {
        this.orderRepository = orderRepository;
        this.orderService = orderService;
    }

    /**
     * Retrieves all orders with their associated order items.
     *
     * @return list of order responses
     */
    @Operation(
            summary = "Get all orders",
            description = "Returns a list of all customer orders including their associated order items."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Orders retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OrderResponse.class)
                    )
            )
    })
    @GetMapping
    public List<OrderResponse> findAll() {
        return orderRepository.findAllWithItems()
                .stream()
                .map(OrderMapper::toResponse)
                .toList();
    }

    /**
     * Retrieves a single order by its unique identifier.
     *
     * @param id order ID
     * @return detailed order response
     */
    @Operation(
            summary = "Get order by ID",
            description = "Returns a single order and all associated order items using the order ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Order found successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OrderResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Order not found"
            )
    })
    @GetMapping("/{id}")
    public OrderResponse findById(
            @Parameter(description = "Unique order ID", example = "1001")
            @PathVariable("id") Long id
    ) {
        return orderRepository.findByIdWithItems(id)
                .map(OrderMapper::toResponse)
                .orElseThrow(() -> new ApiException("Order not found"));
    }

    /**
     * Creates a new customer order.
     *
     * @param request order creation request containing customer and order item details
     * @return created order response
     */
    @Operation(
            summary = "Create order",
            description = "Creates a new customer order, persists order items, and triggers downstream event-driven processing."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Order created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OrderResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failed or business rules were violated"
            )
    })
    @PostMapping
    public OrderResponse create(
            @Parameter(description = "Order creation request containing customer and item details")
            @Valid
            @RequestBody CreateOrderRequest request
    ) {
        return OrderMapper.toResponse(
                orderService.createOrder(request)
        );
    }
}