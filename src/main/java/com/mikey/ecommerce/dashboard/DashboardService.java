package com.mikey.ecommerce.dashboard;

import com.mikey.ecommerce.dto.dashboard.DashboardSummaryResponse;
import com.mikey.ecommerce.inventory.InventoryRepository;
import com.mikey.ecommerce.order.OrderRepository;
import com.mikey.ecommerce.order.OrderStatus;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

  private final OrderRepository orderRepository;
  private final InventoryRepository inventoryRepository;

  public DashboardService(
      OrderRepository orderRepository, InventoryRepository inventoryRepository) {
    this.orderRepository = orderRepository;
    this.inventoryRepository = inventoryRepository;
  }

  public DashboardSummaryResponse getSummary() {
    long totalOrders = orderRepository.count();
    long paidOrders = orderRepository.countByStatus(OrderStatus.PAID);
    long pendingOrders = orderRepository.countByStatus(OrderStatus.PENDING);
    long cancelledOrders = orderRepository.countByStatus(OrderStatus.CANCELLED);
    BigDecimal totalRevenue = orderRepository.sumTotalAmountByStatus(OrderStatus.PAID);
    long inventoryAlerts = inventoryRepository.countLowStock(10);

    return new DashboardSummaryResponse(
        totalOrders, paidOrders, pendingOrders, cancelledOrders, totalRevenue, inventoryAlerts);
  }
}
