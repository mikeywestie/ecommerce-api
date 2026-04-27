package com.mikey.ecommerce.order;

import com.mikey.ecommerce.common.ApiException;
import com.mikey.ecommerce.dto.order.OrderResponse;
import com.mikey.ecommerce.mapper.OrderMapper;
import com.mikey.ecommerce.mapper.PaymentMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders")
public class OrderController {

    private final OrderRepository orderRepository;
    private final OrderService orderService;

    public OrderController(OrderRepository orderRepository, OrderService orderService) {
        this.orderRepository = orderRepository;
        this.orderService = orderService;
    }

    @GetMapping
    public List<OrderResponse> findAll() {
        return orderRepository.findAllWithItems()
                .stream()
                .map(OrderMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public OrderResponse findById(@PathVariable("id") Long id) {
        return orderRepository.findByIdWithItems(id)
                .map(OrderMapper::toResponse)
                .orElseThrow(() -> new ApiException("Order not found"));
    }

    @PostMapping
    public OrderResponse create(@Valid @RequestBody CreateOrderRequest request) {
        return OrderMapper.toResponse(orderService.createOrder(request));
    }
}
