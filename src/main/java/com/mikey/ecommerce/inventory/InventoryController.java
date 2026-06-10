package com.mikey.ecommerce.inventory;

import com.mikey.ecommerce.common.ApiException;
import com.mikey.ecommerce.dto.inventory.InventoryResponse;
import com.mikey.ecommerce.mapper.InventoryMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@Tag(
        name = "Inventory",
        description = "Inventory management endpoints for viewing stock levels and adjusting product quantities."
)
public class InventoryController {

    private final InventoryRepository inventoryRepository;

    public InventoryController(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @GetMapping
    public List<InventoryResponse> findAll() {
        return inventoryRepository.findAll()
                .stream()
                .map(InventoryMapper::toResponse)
                .toList();
    }

    @GetMapping("/{productId}")
    public InventoryResponse findByProductId(@PathVariable("productId") Long productId) {
        return InventoryMapper.toResponse(
                inventoryRepository.findByProductId(productId)
                        .orElseThrow(() -> new ApiException("Inventory not found"))
        );
    }

    @PatchMapping("/{productId}/stock")
    public InventoryResponse addStock(
            @PathVariable("productId") Long productId,
            @Valid @RequestBody StockRequest request
    ) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ApiException("Inventory not found for product"));

        inventory.addStock(request.quantity());

        return InventoryMapper.toResponse(inventoryRepository.save(inventory));
    }

    @PutMapping("/{productId}/quantity")
    public InventoryResponse setQuantity(
            @PathVariable("productId") Long productId,
            @Valid @RequestBody StockRequest request
    ) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ApiException("Inventory not found for product"));

        inventory.setQuantity(request.quantity());

        return InventoryMapper.toResponse(inventoryRepository.save(inventory));
    }
}