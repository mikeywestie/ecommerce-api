package com.mikey.ecommerce.mapper;

import com.mikey.ecommerce.dto.inventory.InventoryResponse;
import com.mikey.ecommerce.inventory.Inventory;

public class InventoryMapper {

    private InventoryMapper() {}

    public static InventoryResponse toResponse(Inventory inventory){
        return new InventoryResponse(
                inventory.getId(),
                ProductMapper.toSummary(inventory.getProduct()),
                inventory.getQuantityAvailable(),
                inventory.getQuantityAvailable() > 0
        );
    }
}