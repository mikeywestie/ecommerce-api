package com.mikey.ecommerce.product;

import com.mikey.ecommerce.common.ApiException;
import com.mikey.ecommerce.dto.product.ProductSummaryResponse;
import com.mikey.ecommerce.inventory.Inventory;
import com.mikey.ecommerce.inventory.InventoryRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import com.mikey.ecommerce.dto.product.ProductResponse;
import com.mikey.ecommerce.mapper.ProductMapper;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Products")
public class ProductController {

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    public ProductController(ProductRepository productRepository, InventoryRepository inventoryRepository) {
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @GetMapping
    public List<ProductSummaryResponse> findAll() {

        return productRepository.findAll()
                .stream()
                .map(ProductMapper::toSummary)
                .toList();
    }

    @Operation(
            summary = "Get product by ID",
            description = "Fetches a single product's details using its unique numeric ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Product found successfully",
                    content = @Content(schema = @Schema(implementation = Product.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found",
                    content = @Content(schema = @Schema(implementation = ApiException.class))
            )
    })
    @GetMapping("/{id}")
    public ProductResponse findById(
            @Parameter(description = "The ID of the product to retrieve", example = "101")
            @PathVariable("id") Long id
    ) {
        return ProductMapper.toResponse(
                productRepository.findById(id)
                        .orElseThrow(() -> new ApiException("Product not found")));

    }


    @PostMapping
    public Product create(@Valid @RequestBody ProductRequest request) {
        Product product = productRepository.save(new Product(request.name(), request.description(), request.price()));
        int initialStock = request.initialStock() == null ? 0 : request.initialStock();
        inventoryRepository.save(new Inventory(product, initialStock));
        return product;
    }

    @PutMapping("/{id}")
    public Product update(@PathVariable("id") Long id, @Valid @RequestBody ProductRequest request) {
        Product product = retrieveProduct(id);
        product.update(request.name(), request.description(), request.price());
        return productRepository.save(product);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        productRepository.deleteById(id);
    }


    private Product retrieveProduct(Long productId ) {
        return productRepository.findById(productId)
                        .orElseThrow(() -> new ApiException("Product not found"));

    }
}
