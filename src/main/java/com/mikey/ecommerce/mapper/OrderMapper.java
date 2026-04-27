package com.mikey.ecommerce.mapper;

import com.mikey.ecommerce.dto.order.OrderItemResponse;
import com.mikey.ecommerce.dto.order.OrderResponse;
import com.mikey.ecommerce.order.CustomerOrder;
import com.mikey.ecommerce.order.OrderItem;

import java.util.List;

public class OrderMapper {

    private OrderMapper(){}

    public static OrderResponse toResponse(CustomerOrder order){
        List<OrderItemResponse> items =
                order.getItems()
                        .stream()
                        .map(OrderMapper::mapItem)
                        .toList();

        return new OrderResponse(
                order.getId(),
                order.getCustomerName(),
                order.getCustomerEmail(),
                order.getStatus().name(),
                order.getTotalAmount(),
                order.getCouponCode(),
                order.getDiscountAmount(),
                order.getCreatedAt(),
                items
        );
    }

    private static OrderItemResponse mapItem(OrderItem item){
        return new OrderItemResponse(
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getLineTotal()
        );
    }
}