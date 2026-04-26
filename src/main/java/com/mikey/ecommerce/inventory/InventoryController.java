package com.mikey.ecommerce.inventory;

import com.mikey.ecommerce.common.ApiException;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@Tag(name = "Inventory")
public class InventoryController {

    private final InventoryRepository inventoryRepository;

    public InventoryController(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @GetMapping
    public List<Inventory> findAll() {
        return inventoryRepository.findAll();
    }

    @GetMapping("/{productId}")
    public Inventory findByProductId(@PathVariable("productId")  Long productId) {
        return inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ApiException("Inventory not found for product"));
    }

    @PatchMapping("/{productId}/stock")
    public Inventory addStock(@PathVariable("productId") Long productId, @Valid @RequestBody StockRequest request) {
        Inventory inventory = findByProductId(productId);
        inventory.addStock(request.quantity());
        return inventoryRepository.save(inventory);
    }
}
