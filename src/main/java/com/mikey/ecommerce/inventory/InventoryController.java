package com.mikey.ecommerce.inventory;

import com.mikey.ecommerce.common.ApiException;
import com.mikey.ecommerce.dto.inventory.InventoryResponse;
import com.mikey.ecommerce.mapper.InventoryMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller responsible for inventory management.
 *
 * <p>This controller provides endpoints for:</p>
 * <ul>
 *     <li>Retrieving all inventory records</li>
 *     <li>Retrieving inventory by product ID</li>
 *     <li>Adding stock to an existing inventory record</li>
 * </ul>
 *
 * <p>Each inventory record is associated with a product and tracks the
 * available stock quantity.</p>
 */
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

    /**
     * Retrieves all inventory records.
     *
     * @return list of inventory responses
     */
    @Operation(
            summary = "Get all inventory",
            description = "Returns a list of all inventory records including product details and current stock quantities."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Inventory retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = InventoryResponse.class)
                    )
            )
    })
    @GetMapping
    public List<InventoryResponse> findAll() {
        return inventoryRepository.findAll()
                .stream()
                .map(InventoryMapper::toResponse)
                .toList();
    }

    /**
     * Retrieves inventory for a specific product.
     *
     * @param productId product ID
     * @return inventory response
     */
    @Operation(
            summary = "Get inventory by product ID",
            description = "Returns the inventory record associated with the specified product ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Inventory record found successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = InventoryResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Inventory not found"
            )
    })
    @GetMapping("/{productId}")
    public InventoryResponse findByProductId(
            @Parameter(description = "Unique product ID", example = "101")
            @PathVariable("productId") Long productId
    ) {
        return InventoryMapper.toResponse(
                inventoryRepository.findByProductId(productId)
                        .orElseThrow(() ->
                                new ApiException("Inventory not found"))
        );
    }

    /**
     * Adds stock to an existing inventory record.
     *
     * @param productId product ID
     * @param request stock adjustment request
     * @return updated inventory response
     */
    @Operation(
            summary = "Add stock",
            description = "Increases the stock quantity for the specified product by the provided amount."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Stock updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = InventoryResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failed"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Inventory record not found"
            )
    })
    @PatchMapping("/{productId}/stock")
    public InventoryResponse addStock(
            @Parameter(description = "Unique product ID", example = "101")
            @PathVariable("productId") Long productId,

            @Parameter(description = "Stock quantity to add")
            @Valid
            @RequestBody StockRequest request
    ) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() ->
                        new ApiException("Inventory not found for product"));

        inventory.addStock(request.quantity());

        Inventory savedInventory = inventoryRepository.save(inventory);

        return InventoryMapper.toResponse(savedInventory);
    }
}