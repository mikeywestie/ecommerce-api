package com.mikey.ecommerce.order;

import com.mikey.ecommerce.common.ApiException;
import com.mikey.ecommerce.inventory.Inventory;
import com.mikey.ecommerce.inventory.InventoryRepository;
import com.mikey.ecommerce.product.Product;
import com.mikey.ecommerce.product.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    public OrderService(OrderRepository orderRepository,
                        ProductRepository productRepository,
                        InventoryRepository inventoryRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional
    public CustomerOrder createOrder(CreateOrderRequest request) {
        CustomerOrder order = new CustomerOrder(request.customerName(), request.customerEmail());

        for (OrderItemRequest itemRequest : request.items()) {
            Product product = productRepository.findById(itemRequest.productId())
                    .orElseThrow(() -> new ApiException("Product not found: " + itemRequest.productId()));

            Inventory inventory = inventoryRepository.findByProductId(product.getId())
                    .orElseThrow(() -> new ApiException("Inventory not found for product: " + product.getName()));

            inventory.reserve(itemRequest.quantity());

            OrderItem item = new OrderItem(order, product, itemRequest.quantity(), product.getPrice());
            order.addItem(item);
        }

        return orderRepository.save(order);
    }
}
