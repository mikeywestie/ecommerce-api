package com.mikey.ecommerce.dto.inventory;

import com.mikey.ecommerce.dto.product.ProductSummaryResponse;

public record InventoryResponse(
        Long inventoryId,
        ProductSummaryResponse product,
        Integer quantityAvailable,
        boolean inStock
) {}
