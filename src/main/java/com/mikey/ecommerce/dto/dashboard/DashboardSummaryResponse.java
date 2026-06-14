package com.mikey.ecommerce.dto.dashboard;

import java.math.BigDecimal;

public record DashboardSummaryResponse(
    long totalOrders,
    long paidOrders,
    long pendingOrders,
    long cancelledOrders,
    BigDecimal totalRevenue,
    long inventoryAlerts) {}
