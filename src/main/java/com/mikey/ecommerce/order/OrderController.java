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
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@Tag(
    name = "Orders",
    description = "Order management endpoints for creating orders and retrieving order details.")
public class OrderController {

  private final OrderRepository orderRepository;
  private final OrderService orderService;

  public OrderController(OrderRepository orderRepository, OrderService orderService) {
    this.orderRepository = orderRepository;
    this.orderService = orderService;
  }

  @Operation(
      summary = "Get all orders",
      description = "Admin-only endpoint. Returns all customer orders.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Orders retrieved successfully",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = OrderResponse.class)))
      })
  @GetMapping
  public List<OrderResponse> findAll() {
    return orderRepository.findAllWithItems().stream().map(OrderMapper::toResponse).toList();
  }

  @Operation(
      summary = "Get my orders",
      description = "Returns only orders belonging to the authenticated user's email address.")
  @GetMapping("/my-orders")
  public List<OrderResponse> findMyOrders(Authentication authentication) {
    if (authentication == null || authentication.getName() == null) {
      throw new ApiException("Authenticated user not found", HttpStatus.UNAUTHORIZED);
    }

    return orderRepository.findByCustomerEmailWithItems(authentication.getName()).stream()
        .map(OrderMapper::toResponse)
        .toList();
  }

  @Operation(
      summary = "Get order by ID",
      description = "Admins can access any order. Customers can only access their own orders.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Order found successfully",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = OrderResponse.class))),
        @ApiResponse(responseCode = "404", description = "Order not found")
      })
  @GetMapping("/{id}")
  public OrderResponse findById(
      @Parameter(description = "Unique order ID", example = "1001") @PathVariable("id") Long id,
      Authentication authentication) {
    CustomerOrder order =
        orderRepository
            .findByIdWithItems(id)
            .orElseThrow(() -> new ApiException("Order not found", HttpStatus.NOT_FOUND));

    if (!isAdmin(authentication) && !belongsToAuthenticatedUser(order, authentication)) {
      throw new ApiException("Order not found", HttpStatus.NOT_FOUND);
    }

    return OrderMapper.toResponse(order);
  }

  @Operation(summary = "Create order", description = "Creates a new customer order.")
  @PostMapping
  public OrderResponse create(@Valid @RequestBody CreateOrderRequest request) {
    return OrderMapper.toResponse(orderService.createOrder(request));
  }

  private boolean isAdmin(Authentication authentication) {
    return authentication != null
        && authentication.getAuthorities().stream()
            .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
  }

  private boolean belongsToAuthenticatedUser(CustomerOrder order, Authentication authentication) {
    return authentication != null
        && authentication.getName() != null
        && order.getCustomerEmail() != null
        && order.getCustomerEmail().equalsIgnoreCase(authentication.getName());
  }
}
