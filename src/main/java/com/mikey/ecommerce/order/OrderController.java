package com.mikey.ecommerce.order;

import com.mikey.ecommerce.common.ApiException;
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
    public List<CustomerOrder> findAll() {
        return orderRepository.findAll();
    }

    @GetMapping("/{id}")
    public CustomerOrder findById(@PathVariable("id") Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ApiException("Order not found"));
    }

    @PostMapping
    public CustomerOrder create(@Valid @RequestBody CreateOrderRequest request) {
        return orderService.createOrder(request);
    }
}
